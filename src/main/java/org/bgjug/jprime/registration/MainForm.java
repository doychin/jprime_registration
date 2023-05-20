package org.bgjug.jprime.registration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import org.apache.commons.lang3.StringUtils;
import org.bgjug.jprime.registration.model.VisitorData;

public class MainForm {

    private final String cookie;

    private JButton visitorRegButton;

    static JFrame frame;

    public static void main(String[] args) {
        LoginDialog dialog = new LoginDialog();
        dialog.pack();
        dialog.setVisible(true);
        if (StringUtils.isEmpty(dialog.getCookie())) {
            System.exit(0);
        }

        frame = new JFrame("JPrime 2023 Registration");
        frame.setContentPane(new MainForm(dialog.getCookie()).mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        Point p = Utilities.centerComponentOnTheScreen(frame);
        frame.setLocation(p);
        frame.setVisible(true);
    }

    private JPanel mainPanel;

    private JButton speakerRegistrationButton;

    private JButton organizersVolunteersButton;

    private JButton testBadgeButton;

    public MainForm(String cookie) {
        this.cookie = cookie;
        visitorRegButton.addActionListener(this::visitorRegistration);
        testBadgeButton.addActionListener(this::testBadge);
    }

    private void testBadge(ActionEvent actionEvent) {
        int result = JOptionPane.showConfirmDialog(frame, "Are you sure you want to print test badge?", "Test badge printing",
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.NO_OPTION){
            return;
        }

        VisitorData visitorData =
            new VisitorData("Doychin Bondzhev", "not@this.year", "dSoft-Bulgaria Ltd.", "Organizer");
        BadgePrinter.printBadge("JPrime 2023", visitorData, true, true, true, true);
    }

    private void visitorRegistration(ActionEvent e) {
        new RegistrationForm(cookie);
    }
}
