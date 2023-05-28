package org.bgjug.jprime.registration;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.bgjug.jprime.registration.api.Visitor;
import org.bgjug.jprime.registration.api.VisitorSearch;
import org.bgjug.jprime.registration.client.RestClientFactory;

public class VisitorSearchDialog extends JDialog {

    private static final Vector<String> COLUMN_NAMES =
        new Vector<>(Arrays.asList("Name", "Company", "E-mail", "Ticket ID"));

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

    public VisitorSearchDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(btnSearch);
        pack();
        Point p = Utilities.centerComponentOnTheScreen(this);
        setLocation(p);
        setTitle("Visitors search");
        visitorsTable.getSelectionModel().addListSelectionListener(this::selectionChanged);
        visitorsTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

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

    private void selectionChanged(ListSelectionEvent listSelectionEvent) {
        buttonOK.setEnabled(visitorsTable.getSelectedRow() != -1);
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
        VisitorSearch searchData = new VisitorSearch();
        if (StringUtils.isNotEmpty(txtFirstName.getText()) || StringUtils.isNotEmpty(txtLastName.getText())) {
            searchData.setFirstName(txtFirstName.getText());
            searchData.setLastName(txtLastName.getText());
        }

        if (StringUtils.isNotEmpty(txtEmail.getText())) {
            searchData.setEmail(txtEmail.getText());
        }

        if (StringUtils.isNotEmpty(txtCompany.getText())) {
            searchData.setCompany(txtCompany.getText());
        }

        List<Visitor> visitorList;
        try (Response response = RestClientFactory.visitorApi().visitorSearch(Globals.YEAR, searchData)) {
            if (response.getStatus() != 200) {
                JOptionPane.showMessageDialog(this, "Error while calling search on the remote server!!!",
                    response.readEntity(String.class), JOptionPane.ERROR_MESSAGE);
                return;
            }
            visitorList = response.readEntity(new VisitorList());
            if (visitorList.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "There are no matching results for the specified search criteria", "Nothing found",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error while calling search on the remote server!!!",
                ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            return;
        }

        Vector<? extends Vector<String>> dataVectors = visitorList.stream().sorted(Comparator.comparing(
                                                                      Visitor::getName))
            .map(v -> new Vector<>(Arrays.asList(v.getName(), v.getCompany(), v.getEmail(), v.getTicket())))
            .collect(Collectors.toCollection(Vector::new));
        visitorsTable.setModel(new DefaultTableModel(dataVectors, COLUMN_NAMES));
    }

    private void onOK() {
        Object cellValue = visitorsTable.getModel().getValueAt(visitorsTable.getSelectedRow(), 3);
        String ticket = cellValue != null ? cellValue.toString() : null;
        if (ticket != null) {
            ticketInfo = new RegistrationForm.TicketInfo(null, "JPrime " + Globals.YEAR, "Visitor", ticket);
        }

        dispose();
    }

    private void onCancel() {
        ticketInfo = null;
        dispose();
    }

    private void createUIComponents() {
        visitorsTable = new JTable(new Vector<>(), COLUMN_NAMES);
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

    private static class VisitorList extends GenericType<List<Visitor>> {}
}
