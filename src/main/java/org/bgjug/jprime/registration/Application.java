package org.bgjug.jprime.registration;

import javax.swing.*;
import java.awt.*;

public class Application {
    public static void main(String[] args) {
        LoginDialog dialog = new LoginDialog();
        dialog.pack();
        dialog.setVisible(true);

        if (!dialog.isSuccess()) {
            System.exit(0);
        }

        new MainForm();
    }
}
