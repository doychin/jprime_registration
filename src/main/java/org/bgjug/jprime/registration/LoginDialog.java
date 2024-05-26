package org.bgjug.jprime.registration;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.bgjug.jprime.registration.client.RestClientFactory;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

public class LoginDialog extends JDialog {

    private static final byte[] KEY_BYTES = "JPr1meEncrypti0nKeyT0SecureP@ssw".getBytes();

    private static final String ERROR_TITLE = "Error";

    private JPanel contentPane;

    private JButton buttonOK;

    private JButton buttonCancel;

    private JTextField userNameField;

    private JPasswordField passwordField;

    private JCheckBox chkSaveLoginDetails;

    private JComboBox<String> comboUrlList;

    private boolean success;

    public LoginDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        pack();
        Point p = Utilities.centerComponentOnTheScreen(this);
        setLocation(p);

        setTitle("JPrime " + Globals.YEAR + " Registration");

        Config config = ConfigProvider.getConfig();
        String[] urlList = config.getOptionalValue("org.bgjug.jprime.registration.url.list", String[].class)
            .orElse(new String[] {});
        comboUrlList.setModel(new DefaultComboBoxModel<>(urlList));

        String url = config.getOptionalValue("org.bgjug.jprime.registration.url", String.class)
            .orElse("http://localhost:8080/");
        comboUrlList.setSelectedItem(url);
        userNameField.setText(
            config.getOptionalValue("org.bgjug.jprime.registration.username", String.class).orElse(null));
        String encryptedPassword =
            config.getOptionalValue("org.bgjug.jprime.registration.password", String.class).orElse(null);

        if (StringUtils.isNotBlank(encryptedPassword)) {
            try {
                passwordField.setText(decryptPassword(encryptedPassword));
            } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException |
                     UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException e) {
                passwordField.setText("");
            }
        }

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        try {
            if (comboUrlList.getSelectedItem() == null || StringUtils.isBlank(
                comboUrlList.getSelectedItem().toString())) {
                JOptionPane.showMessageDialog(this, "Invalid or missing base URL!", "Unable to save settings",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (StringUtils.isBlank(userNameField.getText())) {
                JOptionPane.showMessageDialog(this, "Username is required!", ERROR_TITLE,
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (passwordField.getPassword().length == 0) {
                JOptionPane.showMessageDialog(this, "Password is required!", ERROR_TITLE,
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            success = RestClientFactory.initializeCredentials(comboUrlList.getSelectedItem().toString(),
                userNameField.getText(), new String(passwordField.getPassword()));
            if (!success) {
                JOptionPane.showMessageDialog(this, "Invalid credentials!!!", ERROR_TITLE,
                    JOptionPane.ERROR_MESSAGE);
            } else {
                if (chkSaveLoginDetails.isSelected()) {
                    saveProperties();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Unable to login: " + e.getMessage(), ERROR_TITLE,
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        // add your code here
        dispose();
    }

    private void saveProperties() {
        List<String> urlList = IntStream.range(0, comboUrlList.getItemCount())
            .mapToObj(i -> comboUrlList.getItemAt(i))
            .collect(Collectors.toList());

        String selectedUrl = comboUrlList.getSelectedItem().toString();
        if (!urlList.contains(selectedUrl)) {
            urlList.add(selectedUrl);
        }

        Properties properties = new Properties();
        properties.put("org.bgjug.jprime.registration.username", userNameField.getText());
        properties.put("org.bgjug.jprime.registration.url", selectedUrl);
        properties.put("org.bgjug.jprime.registration.url.list", String.join(",", urlList));

        try (OutputStream output = Files.newOutputStream(
            Paths.get("microprofile-config-overrides.properties"))) {
            properties.put("org.bgjug.jprime.registration.password",
                encryptPassword(new String(passwordField.getPassword())));
            properties.store(output, "JPrime Registration program properties");
        } catch (IOException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException |
                 InvalidKeyException | NoSuchAlgorithmException e) {
            JOptionPane.showMessageDialog(this,
                "Unable to store current settings to a properties file!!!\n Error: " + e.getMessage(),
                "Unable to save settings", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCancel() {
        success = false;
        dispose();
    }

    public boolean isSuccess() {
        return success;
    }

    private String encryptPassword(String password)
        throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
        IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = initCipher(Cipher.ENCRYPT_MODE);
        byte[] cipherText = cipher.doFinal(password.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeBase64URLSafeString(cipherText);
    }

    private String decryptPassword(String encryptedPassword)
        throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
        UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = initCipher(Cipher.DECRYPT_MODE);
        String decodeStr = URLDecoder.decode(encryptedPassword, StandardCharsets.UTF_8.toString());
        byte[] base64decodedTokenArr = Base64.decodeBase64(decodeStr.getBytes(StandardCharsets.UTF_8));
        byte[] decryptedPassword = cipher.doFinal(base64decodedTokenArr);
        return new String(decryptedPassword, StandardCharsets.UTF_8);
    }

    private static Cipher initCipher(int decryptMode)
        throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec key = new SecretKeySpec(KEY_BYTES, "AES");
        cipher.init(decryptMode, key);
        return cipher;
    }
}
