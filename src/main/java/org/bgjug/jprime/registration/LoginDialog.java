package org.bgjug.jprime.registration;

import javax.swing.*;
import javax.ws.rs.core.Response;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.bgjug.jprime.registration.client.RestClientFactory;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import static org.bgjug.jprime.registration.client.RestClientFactory.loginApi;

public class LoginDialog extends JDialog {

    private JPanel contentPane;

    private JButton buttonOK;

    private JButton buttonCancel;

    private JTextField userNameField;

    private JPasswordField passwordField;

    private String cookie;

    private boolean success;

    public LoginDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        pack();
        Point p = Utilities.centerComponentOnTheScreen(this);
        setLocation(p);

        setTitle("JPrime 2023 Registration");

        Config config =  ConfigProvider.getConfig();
        userNameField.setText(config.getOptionalValue("org.bgjug.jprime.registration.username", String.class).orElse(null));
        passwordField.setText(config.getOptionalValue("org.bgjug.jprime.registration.password", String.class).orElse(null));

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {

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
            success = RestClientFactory.initializeCredentials(userNameField.getText(), new String(passwordField.getPassword()));
            if (!success) {
                JOptionPane.showMessageDialog(this, "Invalid credentials!!!", "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Unable to login: " + e.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
        }
        // add your code here
        dispose();
    }

    private void onCancel() {
        success = false;
        dispose();
    }

    public boolean isSuccess() {
        return success;
    }
}
