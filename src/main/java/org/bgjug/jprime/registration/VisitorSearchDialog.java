package org.bgjug.jprime.registration;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import javax.ws.rs.core.Response;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.bgjug.jprime.registration.api.Visitor;
import org.bgjug.jprime.registration.api.VisitorSearch;

public class VisitorSearchDialog extends JDialog {

    private static final Vector<String> COLUMN_NAMES =
        new Vector<>(Arrays.asList("Name", "Company", "E-mail"));

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
        visitorsTable.getSelectionModel().addListSelectionListener(this::selectionChanged);

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
        try (Response response = RestClientFactory.visitorApi().visitorSearch("2023", searchData, cookie)) {
            if (response.getStatus() != 200) {
                JOptionPane.showMessageDialog(this, "Error while calling search on the remote server!!!",
                    response.readEntity(String.class), JOptionPane.ERROR_MESSAGE);
                return;
            }
            visitorList = new ObjectMapper().readValue(response.readEntity(String.class),
                new TypeReference<List<Visitor>>() {});
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

        visitorsTable.setModel(new DefaultTableModel(visitorList.stream()
            .map(v -> new Vector<>(Arrays.asList(v.getName(), v.getCompany(), v.getEmail())))
            .collect(Collectors.toCollection(Vector::new)), COLUMN_NAMES));
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
}