import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.Locale;

// test comment

public class PINBlockDecrypterApp extends JFrame {

    private static final String APP_VERSION = "1.0.1";

    private static final Color BRAND_RED = new Color(183, 28, 28);
    private static final Color BRAND_RED_DARK = new Color(136, 14, 14);
    private static final Color LIGHT_BACKGROUND = new Color(248, 249, 250);

    private final JPasswordField zmkField = new JPasswordField();
    private final JTextField encryptedZpkField = new JTextField();
    private final JPasswordField clearZpkResultField = new JPasswordField();

    private final JPasswordField clearZpkInputField = new JPasswordField();
    private final JTextField pinBlockField = new JTextField();
    private final JTextField encryptedPinBlockResultField = new JTextField();

    public PINBlockDecrypterApp() {
        setTitle("PIN Block Decrypter v" + APP_VERSION);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(820, 590));
        setSize(820, 590);
        setLocationRelativeTo(null);

        Image appIcon = loadAppIcon();
        if (appIcon != null) {
            setIconImage(appIcon);
        }

        setContentPane(createMainPanel());
    }

    private JPanel createMainPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(LIGHT_BACKGROUND);

        root.add(createHeader(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabs.addTab("1. Decrypt ZPK", createDecryptTab());
        tabs.addTab("2. Encrypt PIN Block", createEncryptTab());
        tabs.setBorder(new EmptyBorder(14, 18, 12, 18));

        root.add(tabs, BorderLayout.CENTER);
        root.add(createFooter(), BorderLayout.SOUTH);

        return root;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(14, 0));
        header.setBackground(BRAND_RED);
        header.setBorder(new EmptyBorder(18, 24, 18, 24));

        JLabel badge = new JLabel("PBD", SwingConstants.CENTER);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 24));
        badge.setForeground(Color.WHITE);
        badge.setPreferredSize(new Dimension(72, 56));
        badge.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("PIN Block Decrypter");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Authorized TEST/UAT key and PIN block utility");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(new Color(255, 235, 238));

        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(subtitle);

        header.add(badge, BorderLayout.WEST);
        header.add(textPanel, BorderLayout.CENTER);

        return header;
    }

    private JPanel createDecryptTab() {
        JPanel outer = createTabContainer();

        JPanel form = createFormPanel("Decrypt an encrypted ZPK using a clear ZMK");

        addFormRow(form, 0,
                "Clear ZMK (32 HEX characters)",
                zmkField,
                "Example format: 3012998B6363CFA3B271C6B474B49906");

        addFormRow(form, 1,
                "Encrypted ZPK (32 HEX characters)",
                encryptedZpkField,
                "Enter the encrypted double-length ZPK.");

        clearZpkResultField.setEditable(false);
        clearZpkResultField.setBackground(new Color(245, 245, 245));
        addFormRow(form, 2,
                "Clear ZPK Result",
                clearZpkResultField,
                "The decrypted ZPK is displayed here.");

        JCheckBox showValues = new JCheckBox("Show ZMK and clear ZPK values");
        showValues.setOpaque(false);
        showValues.addActionListener(e -> {
            char echo = showValues.isSelected() ? (char) 0 : '\u2022';
            zmkField.setEchoChar(echo);
            clearZpkResultField.setEchoChar(echo);
        });

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setOpaque(false);

        JButton decryptButton = createPrimaryButton("Decrypt ZPK");
        decryptButton.addActionListener(e -> decryptZpk());

        JButton copyButton = createSecondaryButton("Copy Result");
        copyButton.addActionListener(e -> copyField(clearZpkResultField, "Clear ZPK"));

        JButton clearButton = createSecondaryButton("Clear Inputs");
        clearButton.addActionListener(e -> clearDecryptTab());

        actions.add(decryptButton);
        actions.add(copyButton);
        actions.add(clearButton);

        JPanel controls = new JPanel();
        controls.setOpaque(false);
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
        controls.add(showValues);
        controls.add(Box.createVerticalStrut(14));
        controls.add(actions);

        outer.add(form, BorderLayout.CENTER);
        outer.add(controls, BorderLayout.SOUTH);

        return outer;
    }

    private JPanel createEncryptTab() {
        JPanel outer = createTabContainer();

        JPanel form = createFormPanel("Encrypt a PIN Block using a clear ZPK");

        addFormRow(form, 0,
                "Clear ZPK (32 or 48 HEX characters)",
                clearZpkInputField,
                "Double-length and triple-length 3DES ZPK values are supported.");

        addFormRow(form, 1,
                "PIN Block from Excel (16 HEX characters)",
                pinBlockField,
                "The PIN block must be exactly 8 bytes / 16 HEX characters.");

        encryptedPinBlockResultField.setEditable(false);
        encryptedPinBlockResultField.setBackground(new Color(245, 245, 245));
        addFormRow(form, 2,
                "Encrypted PIN Block Result",
                encryptedPinBlockResultField,
                "Use the result only in the approved test flow.");

        JCheckBox showZpk = new JCheckBox("Show clear ZPK value");
        showZpk.setOpaque(false);
        showZpk.addActionListener(e ->
                clearZpkInputField.setEchoChar(showZpk.isSelected() ? (char) 0 : '\u2022'));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setOpaque(false);

        JButton encryptButton = createPrimaryButton("Encrypt PIN Block");
        encryptButton.addActionListener(e -> encryptPinBlock());

        JButton copyButton = createSecondaryButton("Copy Result");
        copyButton.addActionListener(e ->
                copyField(encryptedPinBlockResultField, "Encrypted PIN Block"));

        JButton clearButton = createSecondaryButton("Clear Inputs");
        clearButton.addActionListener(e -> clearEncryptTab());

        actions.add(encryptButton);
        actions.add(copyButton);
        actions.add(clearButton);

        JPanel controls = new JPanel();
        controls.setOpaque(false);
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
        controls.add(showZpk);
        controls.add(Box.createVerticalStrut(14));
        controls.add(actions);

        outer.add(form, BorderLayout.CENTER);
        outer.add(controls, BorderLayout.SOUTH);

        return outer;
    }

    private JPanel createTabContainer() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(LIGHT_BACKGROUND);
        panel.setBorder(new EmptyBorder(14, 8, 12, 8));
        return panel;
    }

    private JPanel createFormPanel(String title) {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(
                        BorderFactory.createLineBorder(new Color(220, 220, 220)),
                        title,
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 14),
                        BRAND_RED_DARK
                ),
                new EmptyBorder(14, 16, 12, 16)
        ));
        return form;
    }

    private void addFormRow(
            JPanel panel,
            int row,
            String labelText,
            JTextField field,
            String helpText
    ) {
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = row * 2;
        labelConstraints.anchor = GridBagConstraints.WEST;
        labelConstraints.insets = new Insets(8, 4, 4, 18);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(label, labelConstraints);

        GridBagConstraints fieldConstraints = new GridBagConstraints();
        fieldConstraints.gridx = 1;
        fieldConstraints.gridy = row * 2;
        fieldConstraints.weightx = 1.0;
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        fieldConstraints.insets = new Insets(8, 0, 4, 4);

        field.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
        field.setPreferredSize(new Dimension(440, 34));
        panel.add(field, fieldConstraints);

        GridBagConstraints helpConstraints = new GridBagConstraints();
        helpConstraints.gridx = 1;
        helpConstraints.gridy = row * 2 + 1;
        helpConstraints.anchor = GridBagConstraints.WEST;
        helpConstraints.insets = new Insets(0, 2, 8, 4);

        JLabel help = new JLabel(helpText);
        help.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        help.setForeground(new Color(95, 99, 104));
        panel.add(help, helpConstraints);
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(245, 245, 245));
        footer.setBorder(new EmptyBorder(9, 20, 9, 20));

        JLabel warning = new JLabel(
                "Version " + APP_VERSION + " | Do not use production keys or production PIN data. No values are saved."
        );
        warning.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        warning.setForeground(new Color(90, 90, 90));

        footer.add(warning, BorderLayout.WEST);
        return footer;
    }

    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(BRAND_RED);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(9, 16, 9, 16));
        return button;
    }

    private JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(9, 14, 9, 14));
        return button;
    }

    private void decryptZpk() {
        try {
            String zmkHex = normalizeHex(new String(zmkField.getPassword()));
            String encryptedZpkHex = normalizeHex(encryptedZpkField.getText());

            validateHex(zmkHex, 32, "Clear ZMK");
            validateHex(encryptedZpkHex, 32, "Encrypted ZPK");

            /*
             * IMPORTANT:
             * This method now calls the same cryptographic flow used by
             * the DecryptZPK console application:
             *
             * 1. HEX to bytes
             * 2. Expand 16-byte ZMK as K1 + K2 + K1
             * 3. DESede/ECB/NoPadding
             * 4. DECRYPT_MODE
             * 5. Convert result to uppercase HEX
             */
            String clearZpkHex = decryptZpkHex(zmkHex, encryptedZpkHex);
            clearZpkResultField.setText(clearZpkHex);

            showSuccess("ZPK decrypted successfully.");
        } catch (Exception exception) {
            clearZpkResultField.setText("");
            showError(exception.getMessage());
        }
    }

    private void encryptPinBlock() {
        try {
            String clearZpkHex = normalizeHex(new String(clearZpkInputField.getPassword()));
            String pinBlockHex = normalizeHex(pinBlockField.getText());

            validateHexOneOf(clearZpkHex, new int[]{32, 48}, "Clear ZPK");
            validateHex(pinBlockHex, 16, "PIN Block");

            /*
             * IMPORTANT:
             * This method now calls the same cryptographic flow used by
             * the EncryptPinBlockSingle console application:
             *
             * 1. Expand a 16-byte ZPK as K1 + K2 + K1, or use a
             *    24-byte ZPK without modification
             * 2. DESede/ECB/NoPadding
             * 3. ENCRYPT_MODE
             * 4. Convert result to uppercase HEX
             */
            String encryptedPinBlockHex =
                    encryptPinBlockHex(clearZpkHex, pinBlockHex);

            encryptedPinBlockResultField.setText(encryptedPinBlockHex);

            showSuccess("PIN Block encrypted successfully.");
        } catch (Exception exception) {
            encryptedPinBlockResultField.setText("");
            showError(exception.getMessage());
        }
    }

    /*
     * Exact cryptographic logic used by DecryptZPK.java.
     */
    private static String decryptZpkHex(
            String zmkHex,
            String encryptedZpkHex
    ) throws Exception {

        byte[] zmk = hexToBytes(zmkHex);
        byte[] encryptedZpk = hexToBytes(encryptedZpkHex);

        byte[] zmk24 =
                expandDoubleLength3DESKey(zmk);

        Cipher cipher =
                Cipher.getInstance(
                        "DESede/ECB/NoPadding"
                );

        SecretKeySpec zmkKey =
                new SecretKeySpec(
                        zmk24,
                        "DESede"
                );

        cipher.init(
                Cipher.DECRYPT_MODE,
                zmkKey
        );

        byte[] clearZpk =
                cipher.doFinal(encryptedZpk);

        return bytesToHex(clearZpk);
    }

    /*
     * Exact cryptographic logic used by EncryptPinBlockSingle.java.
     */
    private static String encryptPinBlockHex(
            String clearZpkHex,
            String pinBlockHex
    ) throws Exception {

        byte[] zpkKey =
                prepareTripleDesKey(
                        clearZpkHex
                );

        byte[] pinBlock =
                hexToBytes(
                        pinBlockHex
                );

        Cipher cipher =
                Cipher.getInstance(
                        "DESede/ECB/NoPadding"
                );

        SecretKeySpec keySpec =
                new SecretKeySpec(
                        zpkKey,
                        "DESede"
                );

        cipher.init(
                Cipher.ENCRYPT_MODE,
                keySpec
        );

        byte[] encryptedPinBlock =
                cipher.doFinal(
                        pinBlock
                );

        return bytesToHex(
                encryptedPinBlock
        );
    }

    private void clearDecryptTab() {
        zmkField.setText("");
        encryptedZpkField.setText("");
        clearZpkResultField.setText("");
        zmkField.requestFocusInWindow();
    }

    private void clearEncryptTab() {
        clearZpkInputField.setText("");
        pinBlockField.setText("");
        encryptedPinBlockResultField.setText("");
        clearZpkInputField.requestFocusInWindow();
    }

    private void copyField(JTextField field, String valueName) {
        String value = field.getText().trim();

        if (value.isEmpty()) {
            showError(valueName + " is empty.");
            return;
        }

        Toolkit.getDefaultToolkit()
                .getSystemClipboard()
                .setContents(new StringSelection(value), null);

        showSuccess(valueName + " copied to the clipboard.");
    }

    private static String normalizeHex(String value) {
        return value
                .replaceAll("\\s+", "")
                .toUpperCase(Locale.ROOT);
    }

    private static void validateHex(String value, int requiredLength, String fieldName) {
        if (value.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }

        if (value.length() != requiredLength) {
            throw new IllegalArgumentException(
                    fieldName + " must contain exactly " + requiredLength + " HEX characters."
            );
        }

        if (!value.matches("[0-9A-F]+")) {
            throw new IllegalArgumentException(
                    fieldName + " contains invalid characters. Use only 0-9 and A-F."
            );
        }
    }

    private static void validateHexOneOf(
            String value,
            int[] allowedLengths,
            String fieldName
    ) {
        if (value.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }

        boolean validLength = false;

        for (int length : allowedLengths) {
            if (value.length() == length) {
                validLength = true;
                break;
            }
        }

        if (!validLength) {
            throw new IllegalArgumentException(
                    fieldName + " must contain 32 or 48 HEX characters."
            );
        }

        if (!value.matches("[0-9A-F]+")) {
            throw new IllegalArgumentException(
                    fieldName + " contains invalid characters. Use only 0-9 and A-F."
            );
        }
    }

    private static byte[] expandDoubleLength3DESKey(byte[] key16) {
        if (key16.length != 16) {
            throw new IllegalArgumentException(
                    "Clear ZMK must be 16 bytes / 32 HEX characters."
            );
        }

        byte[] key24 = new byte[24];
        System.arraycopy(key16, 0, key24, 0, 16);
        System.arraycopy(key16, 0, key24, 16, 8);
        return key24;
    }

    private static byte[] prepareTripleDesKey(String keyHex) {
        byte[] key = hexToBytes(keyHex);

        if (key.length == 16) {
            byte[] fullKey = new byte[24];
            System.arraycopy(key, 0, fullKey, 0, 16);
            System.arraycopy(key, 0, fullKey, 16, 8);
            return fullKey;
        }

        if (key.length == 24) {
            return key;
        }

        throw new IllegalArgumentException(
                "Clear ZPK must be 16 bytes or 24 bytes."
        );
    }

    private static byte[] hexToBytes(String hex) {
        byte[] bytes = new byte[hex.length() / 2];

        for (int i = 0; i < hex.length(); i += 2) {
            bytes[i / 2] = (byte) Integer.parseInt(
                    hex.substring(i, i + 2),
                    16
            );
        }

        return bytes;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder(bytes.length * 2);

        for (byte value : bytes) {
            result.append(String.format("%02X", value));
        }

        return result.toString();
    }

    private Image loadAppIcon() {
        try {
            java.net.URL resource = getClass().getResource("/PBD.png");
            return resource == null ? null : new ImageIcon(resource).getImage();
        } catch (Exception ignored) {
            return null;
        }
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "PIN Block Decrypter",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Input Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private static void applyLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo lookAndFeel : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(lookAndFeel.getName())) {
                    UIManager.setLookAndFeel(lookAndFeel.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
            // The application will use the default Java look and feel.
        }

        UIManager.put("TabbedPane.selected", new Color(255, 235, 238));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            applyLookAndFeel();
            PINBlockDecrypterApp app = new PINBlockDecrypterApp();
            app.setVisible(true);
        });
    }
}
