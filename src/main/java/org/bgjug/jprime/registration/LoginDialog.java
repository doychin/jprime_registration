package org.bgjug.jprime.registration;

import javax.swing.*;
import javax.ws.rs.core.Response;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static org.bgjug.jprime.registration.RestClientFactory.loginApi;

public class LoginDialog extends JDialog {

    private JPanel contentPane;

    private JButton buttonOK;

    private JButton buttonCancel;

    private JTextField userNameField;

    private JPasswordField passwordField;

    private String cookie;

    public LoginDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        setLocationRelativeTo(null);

        setTitle("JPrime 2023 Registration");

        userNameField.setText("admin");
        passwordField.setText("password");

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
        try (Response r = loginApi().login(userNameField.getText(), new String(passwordField.getPassword()),
            "Submit")) {
            if (r.getStatus() != 302 || !r.getHeaderString("Location").contains("admin")) {
                return;
            }

            cookie = r.getCookies().get("JSESSIONID").getValue();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Unable to login: " + e.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
        }

        // add your code here
        dispose();
    }

    private void onCancel() {
        cookie = null;
        dispose();
    }

    public String getCookie() {
        return cookie;
    }
}
