@echo off
setlocal EnableExtensions

title PIN Block Decrypter Installer Builder

REM ==================================================
REM APPLICATION SETTINGS
REM ==================================================

set "APP_NAME=PIN_BlockDecrypter"
set "APP_VERSION=1.0.0"
set "MAIN_CLASS=PINBlockDecrypterApp"
set "MAIN_JAR=PIN_BlockDecrypter.jar"
set "VENDOR=Dhanithya Beligolla"
set "DESCRIPTION=GUI utility for authorized TEST and UAT ZPK decryption and PIN Block encryption"

REM Keep this UUID unchanged for future versions.
set "UPGRADE_UUID=4B196CB1-3D2D-4D7A-9D73-61D9B7DA13A8"

REM ==================================================
REM JDK LOCATION
REM ==================================================

set "JAVA_HOME=C:\Program Files\Java\jdk-25"
set "JAVAC=%JAVA_HOME%\bin\javac.exe"
set "JAR=%JAVA_HOME%\bin\jar.exe"
set "JPACKAGE=%JAVA_HOME%\bin\jpackage.exe"

REM ==================================================
REM WIX LOCATION
REM ==================================================

REM This avoids the CMD parsing problem caused by (x86)
REM inside nested IF statements.
set "WIX_BIN=C:\Program Files (x86)\WiX Toolset v3.14\bin"
set "PATH=%WIX_BIN%;%PATH%"

REM ==================================================
REM PROJECT LOCATIONS
REM ==================================================

set "PROJECT_DIR=%~dp0"
set "BUILD_DIR=%PROJECT_DIR%build-installer"
set "CLASSES_DIR=%BUILD_DIR%\classes"
set "INPUT_DIR=%BUILD_DIR%\input"
set "INSTALLER_DIR=%PROJECT_DIR%installer"

set "JAVA_FILE=%PROJECT_DIR%PINBlockDecrypterApp.java"
set "PNG_ICON=%PROJECT_DIR%PBD.png"
set "ICO_ICON=%PROJECT_DIR%PBD.ico"

echo ==============================================
echo    PIN BLOCK DECRYPTER INSTALLER BUILDER
echo ==============================================
echo.
echo Project folder:
echo %PROJECT_DIR%
echo.

REM ==================================================
REM CHECK JDK TOOLS
REM ==================================================

echo Checking Java compiler...

if not exist "%JAVAC%" goto :JavacMissing

echo Java compiler found.
echo.

echo Checking JAR tool...

if not exist "%JAR%" goto :JarMissing

echo JAR tool found.
echo.

echo Checking jpackage...

if not exist "%JPACKAGE%" goto :JpackageMissing

echo jpackage found.
echo.

REM ==================================================
REM CHECK WIX TOOLSET
REM ==================================================

echo Checking WiX Toolset...

where candle.exe >nul 2>&1
if errorlevel 1 goto :WixMissing

where light.exe >nul 2>&1
if errorlevel 1 goto :WixMissing

echo WiX Toolset found.
echo.

REM ==================================================
REM CHECK PROJECT FILES
REM ==================================================

echo Checking project files...

if not exist "%JAVA_FILE%" goto :JavaFileMissing
if not exist "%PNG_ICON%" goto :PngMissing
if not exist "%ICO_ICON%" goto :IcoMissing

echo All project files were found.
echo.

REM ==================================================
REM CLEAN OLD BUILD
REM ==================================================

echo Removing previous build files...

if exist "%BUILD_DIR%" rmdir /s /q "%BUILD_DIR%"
if exist "%INSTALLER_DIR%" rmdir /s /q "%INSTALLER_DIR%"

mkdir "%BUILD_DIR%"
if errorlevel 1 goto :FolderError

mkdir "%CLASSES_DIR%"
if errorlevel 1 goto :FolderError

mkdir "%INPUT_DIR%"
if errorlevel 1 goto :FolderError

mkdir "%INSTALLER_DIR%"
if errorlevel 1 goto :FolderError

echo Build folders created.
echo.

REM ==================================================
REM COMPILE APPLICATION
REM ==================================================

echo ==============================================
echo Compiling the GUI application...
echo ==============================================
echo.

"%JAVAC%" -encoding UTF-8 -d "%CLASSES_DIR%" "%JAVA_FILE%"

if errorlevel 1 goto :CompileError

if not exist "%CLASSES_DIR%\PINBlockDecrypterApp.class" goto :MainClassMissing

copy /y "%PNG_ICON%" "%CLASSES_DIR%\PBD.png" >nul

if errorlevel 1 goto :IconCopyError

echo Java compilation completed.
echo.

REM ==================================================
REM CREATE JAR
REM ==================================================

echo ==============================================
echo Creating application JAR...
echo ==============================================
echo.

"%JAR%" --create --file "%INPUT_DIR%\%MAIN_JAR%" --main-class "%MAIN_CLASS%" -C "%CLASSES_DIR%" .

if errorlevel 1 goto :JarBuildError

if not exist "%INPUT_DIR%\%MAIN_JAR%" goto :JarOutputMissing

echo Application JAR created successfully.
echo.

REM ==================================================
REM BUILD WINDOWS INSTALLER
REM ==================================================

echo ==============================================
echo Building Windows installer EXE...
echo ==============================================
echo.

"%JPACKAGE%" --type exe --name "%APP_NAME%" --input "%INPUT_DIR%" --main-jar "%MAIN_JAR%" --main-class "%MAIN_CLASS%" --dest "%INSTALLER_DIR%" --app-version "%APP_VERSION%" --vendor "%VENDOR%" --description "%DESCRIPTION%" --icon "%ICO_ICON%" --add-modules java.desktop --win-shortcut --win-menu --win-menu-group "PIN Block Tools" --win-dir-chooser --win-per-user-install --win-upgrade-uuid "%UPGRADE_UUID%"

if errorlevel 1 goto :InstallerBuildError

REM ==================================================
REM RENAME INSTALLER TO EXACT REQUIRED NAME
REM ==================================================

if exist "%INSTALLER_DIR%\%APP_NAME%.exe" goto :InstallerReady

for %%F in ("%INSTALLER_DIR%\%APP_NAME%-*.exe") do call :RenameInstaller "%%~fF"

if not exist "%INSTALLER_DIR%\%APP_NAME%.exe" goto :InstallerNotFound

goto :InstallerReady

:RenameInstaller
if not exist "%~1" exit /b 0
move /y "%~1" "%INSTALLER_DIR%\%APP_NAME%.exe" >nul
exit /b 0

REM ==================================================
REM SUCCESS
REM ==================================================

:InstallerReady

echo.
echo ==============================================
echo       INSTALLER BUILD COMPLETED
echo ==============================================
echo.
echo Final installer:
echo %INSTALLER_DIR%\%APP_NAME%.exe
echo.
echo Give only this installer EXE to the recipient.
echo Java does not need to be installed on the recipient computer.
echo.
echo Opening installer folder...

explorer "%INSTALLER_DIR%"

echo.
pause
exit /b 0

REM ==================================================
REM ERROR HANDLERS
REM ==================================================

:JavacMissing
echo.
echo ERROR: javac.exe was not found.
echo Expected location:
echo %JAVAC%
goto :Fail

:JarMissing
echo.
echo ERROR: jar.exe was not found.
echo Expected location:
echo %JAR%
goto :Fail

:JpackageMissing
echo.
echo ERROR: jpackage.exe was not found.
echo Expected location:
echo %JPACKAGE%
goto :Fail

:WixMissing
echo.
echo ==============================================
echo ERROR: WiX Toolset 3.14 was not found.
echo ==============================================
echo.
echo Expected WiX folder:
echo C:\Program Files ^(x86^)\WiX Toolset v3.14\bin
echo.
echo Confirm these files exist:
echo candle.exe
echo light.exe
echo.
goto :Fail

:JavaFileMissing
echo.
echo ERROR: PINBlockDecrypterApp.java was not found.
echo Expected:
echo %JAVA_FILE%
goto :Fail

:PngMissing
echo.
echo ERROR: PBD.png was not found.
echo Expected:
echo %PNG_ICON%
goto :Fail

:IcoMissing
echo.
echo ERROR: PBD.ico was not found.
echo Expected:
echo %ICO_ICON%
goto :Fail

:FolderError
echo.
echo ERROR: A build folder could not be created.
goto :Fail

:CompileError
echo.
echo ==============================================
echo ERROR: Java compilation failed.
echo ==============================================
goto :Fail

:MainClassMissing
echo.
echo ERROR: PINBlockDecrypterApp.class was not generated.
goto :Fail

:IconCopyError
echo.
echo ERROR: PBD.png could not be copied into the application resources.
goto :Fail

:JarBuildError
echo.
echo ==============================================
echo ERROR: JAR creation failed.
echo ==============================================
goto :Fail

:JarOutputMissing
echo.
echo ERROR: The application JAR was not generated.
goto :Fail

:InstallerBuildError
echo.
echo ==============================================
echo ERROR: Installer creation failed.
echo ==============================================
goto :Fail

:InstallerNotFound
echo.
echo ERROR: The generated installer EXE could not be located.
goto :Fail

:Fail
echo.
pause
exit /b 1
