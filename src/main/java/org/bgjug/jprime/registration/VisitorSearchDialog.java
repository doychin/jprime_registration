package org.bgjug.jprime.registration;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.ws.rs.core.Response;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.apache.commons.lang.StringUtils;
import org.bgjug.jprime.registration.api.VisitorSearch;

public class VisitorSearchDialog extends JDialog {

    private final String cookie;

    private JPanel contentPane;

    private JButton buttonOK;

    private JButton buttonCancel;

    private JTextField txtFirstName;

    private JTextField txtEmail;

    private JTextField txtCompany;

    private JTable visitorsTable;

    private JButton btnSearch;

    private JTextField txtLastName;

    private RegistrationForm.TicketInfo ticketInfo;

    public VisitorSearchDialog(String cookie) {
        this.cookie = cookie;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(btnSearch);
        setLocationRelativeTo(null);
        setTitle("Visitors search");

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
        btnSearch.addActionListener(this::search);

        DocumentListener documentChangeListener = new TextFieldsDocumentListener();
        txtFirstName.getDocument().addDocumentListener(documentChangeListener);
        txtLastName.getDocument().addDocumentListener(documentChangeListener);
        txtEmail.getDocument().addDocumentListener(documentChangeListener);
        txtCompany.getDocument().addDocumentListener(documentChangeListener);

        txtFirstName.requestFocus();
    }

    private void updateEditFieldsState() {
        txtFirstName.setEnabled(
            StringUtils.isEmpty(txtCompany.getText()) && StringUtils.isEmpty(txtEmail.getText()));
        txtLastName.setEnabled(txtFirstName.isEnabled());

        txtCompany.setEnabled(StringUtils.isEmpty(txtLastName.getText()) && StringUtils.isEmpty(
            txtFirstName.getText()) && StringUtils.isEmpty(txtEmail.getText()));
        txtEmail.setEnabled(StringUtils.isEmpty(txtLastName.getText()) && StringUtils.isEmpty(
            txtFirstName.getText()) && StringUtils.isEmpty(txtCompany.getText()));
    }

    private void search(ActionEvent e) {
        if (StringUtils.isNotEmpty(txtFirstName.getText()) || StringUtils.isNotEmpty(txtLastName.getText())) {
            VisitorSearch searchData = new VisitorSearch();
            searchData.setFirstName(txtFirstName.getText());
            searchData.setLastName(txtLastName.getText());
            try (Response response = RestClientFactory.visitorApi().visitorSearch("2023", searchData, cookie)) {

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error while calling search on the remote server!!!", ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        ticketInfo = null;
        dispose();
    }

    private void createUIComponents() {
        visitorsTable = new JTable(new Object[][] {}, new Object[] {"Name", "Company", "E-mail"});
    }

    public RegistrationForm.TicketInfo getTicketInfo() {
        return ticketInfo;
    }

    class TextFieldsDocumentListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            updateEditFieldsState();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            updateEditFieldsState();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            updateEditFieldsState();
        }
    }
}
