package org.bgjug.jprime.registration;

import javax.swing.*;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

import org.bgjug.jprime.registration.api.Speaker;
import org.bgjug.jprime.registration.client.RestClientFactory;
import org.bgjug.jprime.registration.model.VisitorData;

public class MainForm {

    static JFrame frame;

    private JButton visitorRegButton;

    private JPanel mainPanel;

    private JButton speakerRegistrationButton;

    private JButton testBadgeButton;

    public MainForm() {
        visitorRegButton.addActionListener(this::visitorRegistration);
        testBadgeButton.addActionListener(this::testBadge);
        speakerRegistrationButton.addActionListener(this::speakerRegistration);
    }

    public static void main(String[] args) {
        LoginDialog dialog = new LoginDialog();
        dialog.pack();
        dialog.setVisible(true);

        if (!dialog.isSuccess()) {
            System.exit(0);
        }

        frame = new JFrame("JPrime 2023 Registration");
        frame.setContentPane(new MainForm().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        Point p = Utilities.centerComponentOnTheScreen(frame);
        frame.setLocation(p);
        frame.setVisible(true);
    }

    private void speakerRegistration(ActionEvent actionEvent) {
        int result =
            JOptionPane.showConfirmDialog(frame, "Do you want to print badges for all accepted speakers?",
                "Speaker badge printing", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.NO_OPTION) {
            return;
        }

        List<Speaker> speakerList;
        try (Response response = RestClientFactory.speakerApi().allSpeakers("2023")) {
            if (response.getStatus() != 200) {
                JOptionPane.showMessageDialog(frame, "Error while loading speakers information!!!",
                    response.readEntity(String.class), JOptionPane.ERROR_MESSAGE);
                return;
            }

            speakerList = response.readEntity(new SpeakerList());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error while loading speakers information!!!",
                ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            return;
        }

        while (true) {
            try {
                speakerList.stream().limit(2).filter(speaker -> !speaker.isPrinted()).forEach(speaker -> {
                    VisitorData visitorData = new VisitorData(speaker.getName(), "not@there.yet",
                        speaker.isFeatured() ? "Featured speaker" : "Speaker", "Speaker");
                    BadgePrinter.printBadge("JPrime 2023", visitorData, true, true, true, true);
                    speaker.setPrinted(true);
                });
                break;
            } catch (Exception e) {
                result = JOptionPane.showConfirmDialog(frame,
                    "Error while printing speaker badges!!!. Do you want to continue with printing?",
                    "Print Error", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                if (result == JOptionPane.NO_OPTION) {
                    break;
                }
            }
        }
    }

    private void testBadge(ActionEvent actionEvent) {
        int result = JOptionPane.showConfirmDialog(frame, "Are you sure you want to print test badge?",
            "Test badge printing", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.NO_OPTION) {
            return;
        }

        VisitorData visitorData =
            new VisitorData("Doychin Bondzhev", "not@this.year", "dSoft-Bulgaria Ltd.", "Organizer");
        BadgePrinter.printBadge("JPrime 2023", visitorData, true, true, true, true);
    }

    private void visitorRegistration(ActionEvent e) {
        new RegistrationForm();
    }

    private static class SpeakerList extends GenericType<List<Speaker>> {}
}
