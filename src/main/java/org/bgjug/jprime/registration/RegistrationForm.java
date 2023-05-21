package org.bgjug.jprime.registration;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.ws.rs.core.Response;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.swing.JRViewer;
import org.apache.commons.lang3.StringUtils;
import org.bgjug.jprime.registration.client.RestClientFactory;
import org.bgjug.jprime.registration.model.VisitorData;

public class RegistrationForm {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private JTextField txtTicketInformation;

    private JButton findButton;

    private JTextPane messageLogPane;

    private JPanel mainPanel;

    private JPanel viewPanel;

    private JButton closeButton;

    private JButton clearLogButton;

    static JFrame frame = new JFrame("Visitor Registration");

    static {
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }

    public RegistrationForm() {
        txtTicketInformation.addActionListener(this::findAndPrint);

        frame.setContentPane(mainPanel);
        frame.pack();
        Point p = Utilities.centerComponentOnTheScreen(frame);
        frame.setLocation(p);
        frame.setVisible(true);
        closeButton.addActionListener(e -> RegistrationForm.frame.dispose());
        findButton.addActionListener(this::searchForVisitor);
        clearLogButton.addActionListener(e -> messageLogPane.setText(""));
    }

    private void searchForVisitor(ActionEvent actionEvent) {
        VisitorSearchDialog dialog = new VisitorSearchDialog();
        dialog.pack();
        dialog.setVisible(true);
        if (dialog.getTicketInfo() == null) {
            return;
        }

        processTicketInfo(dialog.getTicketInfo());
    }

    private void createUIComponents() {
    }

    static class TicketInfo {

        private final String organizer;

        private final String event;

        private final String type;

        private final String ticket;

        @JsonCreator
        public TicketInfo(@JsonProperty("organizer") String organizer, @JsonProperty("event") String event,
            @JsonProperty("type") String type, @JsonProperty("ticket") String ticket) {
            this.organizer = organizer;
            this.event = event;
            this.type = type;
            this.ticket = ticket;
        }

        public String getOrganizer() {
            return organizer;
        }

        public String getEvent() {
            return event;
        }

        public String getType() {
            return type;
        }

        public String getTicket() {
            return ticket;
        }
    }

    private void findAndPrint(ActionEvent e) {
        if (StringUtils.isEmpty(txtTicketInformation.getText())) {
            txtTicketInformation.requestFocus();
            return;
        }

        TicketInfo ticketInfo;
        try {
            ticketInfo = OBJECT_MAPPER.readValue(txtTicketInformation.getText(), TicketInfo.class);
        } catch (JsonProcessingException ex) {
            appendErrorMessageToLogPane("Invalid ticket information!!!");
            return;
        }

        processTicketInfo(ticketInfo);
    }

    private void processTicketInfo(TicketInfo ticketInfo) {
        VisitorData visitorData = findVisitorData(ticketInfo);
        if (visitorData == null) {
            return;
        }

        visitorData.setType("Visitor");
        if ("Volunteers".equalsIgnoreCase(visitorData.getRegistrantName())) {
            visitorData.setType("Volunteer");
        }

        LocalDate secondDay = Globals.SECOND_DAY;
        JasperPrint print = BadgePrinter.printBadge(ticketInfo.event, visitorData,
            LocalDate.now().isBefore(secondDay), true, !visitorData.isRegistered(),
            true);

        viewPanel.removeAll();
        JRViewer viewer = new JRViewer(print);
        viewPanel.add(viewer, BorderLayout.CENTER);
        frame.pack();

        try (Response response = RestClientFactory.ticketApi()
            .confirmVisitorRegistration(ticketInfo.ticket)) {
            if (response.getStatus() != 200) {
                appendErrorMessageToLogPane("Unable to confirm ticket registration!!!");
                return;
            }
        } catch (Exception ex) {
            appendErrorMessageToLogPane("Error while confirming visitor registration: " + ex.getMessage());
        }

        txtTicketInformation.setText("");
        txtTicketInformation.requestFocus();
    }

    private VisitorData findVisitorData(TicketInfo ticketInfo) {

        try (Response response = RestClientFactory.visitorApi()
            .visitorByTicket(Globals.YEAR, ticketInfo.ticket)) {

            if (response.getStatus() != 200) {
                appendErrorMessageToLogPane("Unable to find this ticket!!!");
                return null;
            }

            return response.readEntity(VisitorData.class);
        } catch (Exception ex) {
            appendErrorMessageToLogPane("Error while loading ticket information: " + ex.getMessage());
        }

        return null;
    }

    private void appendErrorMessageToLogPane(String errorMessage) {
        SimpleAttributeSet redForeground = new SimpleAttributeSet();
        StyleConstants.setForeground(redForeground, Color.RED);
        try {
            messageLogPane.getDocument().insertString(0, errorMessage + '\n', redForeground);
        } catch (BadLocationException exc) {
            throw new RuntimeException(exc);
        }
        txtTicketInformation.setText("");
        txtTicketInformation.requestFocus();
    }

}
