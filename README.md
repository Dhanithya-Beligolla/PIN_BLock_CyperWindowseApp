PIN BLOCK DECRYPTER - GUI PROJECT
=================================

FILES
-----
PINBlockDecrypterApp.java
    Main Java Swing GUI application build for windows Installer.

PBD.png
    Application window icon.

PBD.ico
    Windows installer and shortcut icon.

BuildInstaller.bat
    Compiles the application, creates the JAR, and builds the Windows installer.

REQUIREMENTS ON THE BUILD COMPUTER
----------------------------------
1. Oracle JDK 25 installed at:
   C:\Program Files\Java\jdk-25

2. WiX Toolset 3.14 installed.

BUILD
-----
1. Keep all four project files in the same folder.
2. Double-click BuildInstaller.bat.
3. Wait for INSTALLER BUILD COMPLETED.
4. The final installer will be:
   installer\PIN_BlockDecrypter.exe

RECIPIENT COMPUTER
------------------
Only send:
PIN_BlockDecrypter.exe

The recipient does not need Java installed because jpackage bundles the required runtime.

FUNCTIONS
---------
Tab 1 - Decrypt ZPK
- Clear ZMK input
- Encrypted ZPK input
- Decrypt button
- Copy result button
- Clear inputs button
- Show/hide sensitive values

Tab 2 - Encrypt PIN Block
- Clear ZPK input
- PIN Block input
- Encrypt button
- Copy result button
- Clear inputs button
- Show/hide clear ZPK

SECURITY NOTE
-------------
Use only with authorized TEST/UAT values. The application does not write entered
values to files, but copying a result places it on the Windows clipboard.
................................................................................
