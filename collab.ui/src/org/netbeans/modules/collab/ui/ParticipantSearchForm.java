/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.collab.ui;

import java.awt.Dialog;
import java.util.*;
import javax.swing.event.*;

import org.openide.*;
import org.openide.util.NbBundle;

import com.sun.collablet.*;
import org.netbeans.modules.collab.core.Debug;

/**
 *
 * @author  sherylsu
 */
public class ParticipantSearchForm extends javax.swing.JPanel implements ListSelectionListener {
    private static boolean _isSearching = false;

    ////////////////////////////////////////////////////////////////////////////
    // Instance variables
    ////////////////////////////////////////////////////////////////////////////
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel TypeLb;
    private javax.swing.JList contactJList;
    private javax.swing.JRadioButton containsOptionRadioButton;
    private javax.swing.JRadioButton endsOptionRadioButton;
    private javax.swing.JButton findBtn;
    private javax.swing.JLabel findContactLb;
    private javax.swing.JTextField findContactTextField;
    private javax.swing.JLabel inviteMsgLbl;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField msgTextField;
    private javax.swing.JRadioButton nameTypeRadioButton;
    private javax.swing.ButtonGroup optionsButtonGroup;
    private javax.swing.JComboBox optionsComboBox;
    private javax.swing.JLabel optionsLb;
    private javax.swing.JPanel optionsPanel;
    private javax.swing.JButton removeBtn;
    private javax.swing.JPanel resultsPanel1;
    private javax.swing.JLabel searchMessageLabel;
    private javax.swing.JPanel searchPanel;
    private javax.swing.JRadioButton startsOptionRadioButton;
    private javax.swing.ButtonGroup typeButtonGroup;
    private javax.swing.JComboBox typeComboBox;
    private javax.swing.JPanel typePanel;
    private javax.swing.JRadioButton userIDTypeRadioButton;
    // End of variables declaration//GEN-END:variables
    private DialogDescriptor dialogDescriptor;
    private ArrayList contactList = new ArrayList();
    private ResourceBundle addContactBundle;
    private CollabSession session;
    private Conversation conversation;
    private boolean limitWasExceeded = false;
    private Vector result = new Vector(20);
    private boolean useComboBoxes = false; // TAF: Switch for UI style - temp
    private String inviteOption;

    /**
     *
     *
     */
    public ParticipantSearchForm(Conversation conversation) {
        this.conversation = conversation;
        this.session = conversation.getCollabSession();
        initialize();
    }

    /**
     *
     *
     */
    private void initialize() {
        // Create the dialog descriptor here, since we need it in order to
        // be able to change the valid state of the dialog
        dialogDescriptor = new DialogDescriptor(
                this, NbBundle.getMessage(ParticipantSearchForm.class, "TITLE_ParticipantSearchForm")
            ); // NOI18N
        inviteOption = NbBundle.getMessage(ParticipantSearchForm.class, "OPT_ParticipantSearchForm_Invite"); // NOI18N

        dialogDescriptor.setOptions(new Object[] { inviteOption, DialogDescriptor.CANCEL_OPTION });
        dialogDescriptor.setValid(false);

        initComponents();

        optionsComboBox.setVisible(useComboBoxes);
        typeComboBox.setVisible(useComboBoxes);

        //	optionsPanel.setVisible(!useComboBoxes);
        optionsPanel.setVisible(false);
        optionsLb.setVisible(false);
        typePanel.setVisible(!useComboBoxes);

        addContactBundle = NbBundle.getBundle(AddContactForm.class);
        optionsComboBox.addItem(addContactBundle.getString("LBL_AddContactForm_Contains")); // NOI18N
        optionsComboBox.addItem(addContactBundle.getString("LBL_AddContactForm_Ends")); // NOI18N
        optionsComboBox.addItem(addContactBundle.getString("LBL_AddContactForm_Starts")); // NOI18N

        typeComboBox.addItem(addContactBundle.getString("LBL_AddContactForm_Name")); // NOI18N
        typeComboBox.addItem(addContactBundle.getString("LBL_AddContactForm_UserID")); // NOI18N

        ListModel model = new ListModel(contactJList, result, false, true, false);

        ListRenderer renderer = new ListRenderer(model);
        contactJList.setCellRenderer(renderer);
        contactJList.setModel(model);
        contactJList.addListSelectionListener(this);

        // Listen to changes to the search field in order to enable or 
        // disable the find button
        findContactTextField.getDocument().addDocumentListener(
            new DocumentListener() {
                public void changedUpdate(DocumentEvent e) {
                }

                public void insertUpdate(DocumentEvent e) {
                    findBtn.setEnabled(
                        (findContactTextField.getText() != null) && (findContactTextField.getText().length() > 0)
                    );
                }

                public void removeUpdate(DocumentEvent e) {
                    findBtn.setEnabled(
                        (findContactTextField.getText() != null) && (findContactTextField.getText().length() > 0)
                    );
                }
            }
        );
    }

    /**
     *
     *
     */
    protected Conversation getConversation() {
        return this.conversation;
    }

    /**
     *
     *
     */
    protected CollabSession getCollabSession() {
        return this.session;
    }

    /**
     *
     *
     */
    public void inviteToConversation() {
        Dialog dialog = null;

        try {
            dialog = DialogDisplayer.getDefault().createDialog(dialogDescriptor);

            dialog.show();

            if (dialogDescriptor.getValue() == inviteOption) {
                if (sessionExists()) {
                    getConversation().invite(getUserSelectionList(), msgTextField.getText());
                }
            }
        } finally {
            dialog.dispose();
        }
    }

    /**
     *
     *
     */
    public CollabPrincipal[] getUserSelectionList() {
        CollabPrincipal[] ret = new CollabPrincipal[result.size()];
        result.copyInto(ret);

        return ret;
    }

    /**
     *
     *
     */
    public void search() {
        if (!sessionExists()) {
            return;
        }

        if (findContactTextField.getText().equals("")) {
            String msg = NbBundle.getMessage(AddContactForm.class, "Enter_a_String_to_search_for"); // NOI18N
            DialogDisplayer.getDefault().notify(
                new NotifyDescriptor.Message(msg, NotifyDescriptor.INFORMATION_MESSAGE)
            );

            return;
        }

        if (!isSearchByID()) {
            byte[] x;
            int minLength = 3;
            int length;

            String slen = addContactBundle.getString("Minimum_search_length"); // NOI18N

            if (slen != null) {
                try {
                    Integer ilen = new Integer(slen);
                    minLength = ilen.intValue();
                } catch (Exception e) {
                    Debug.debugNotify(e);
                }
            }

            try {
                x = findContactTextField.getText().getBytes("UTF-8"); // NOI18N
                length = x.length;
            } catch (Exception e) {
                length = findContactTextField.getText().length();
                Debug.debugNotify(e);
            }

            if (length < minLength) {
                String msg = NbBundle.getMessage(
                        AddContactForm.class, "Enter_at_least_num", // NOI18N
                        Integer.toString(minLength)
                    );

                // Indicate the problem to user
                searchMessageLabel.setText(msg); // NOI18N

                return;
            }
        }

        String txt = findContactTextField.getText();
        int searchType = CollabSession.SEARCHTYPE_EQUALS;

        if (!isSearchByID()) {
            if (useComboBoxes) {
                switch (optionsComboBox.getSelectedIndex()) {
                case 0:
                    searchType = CollabSession.SEARCHTYPE_CONTAINS;

                    break;

                case 1:
                    searchType = CollabSession.SEARCHTYPE_ENDSWITH;

                    break;

                case 2:
                    searchType = CollabSession.SEARCHTYPE_STARTSWITH;

                    break;
                }
            } else {
                /*
                if (containsOptionRadioButton.isSelected())
                        searchType = CollabSession.SEARCHTYPE_CONTAINS;
                else
                if (startsOptionRadioButton.isSelected())
                        searchType = CollabSession.SEARCHTYPE_STARTSWITH;
                else
                if (endsOptionRadioButton.isSelected())
                        searchType = CollabSession.SEARCHTYPE_ENDSWITH;
                 */
                searchType = CollabSession.SEARCHTYPE_CONTAINS;
            }
        } else {
            searchType = CollabSession.SEARCHTYPE_EQUALS;
        }

        Debug.out.println(" search type: " + searchType); // NOI18N
        doSearch("", txt, searchType); // NOI18N
    }

    /**
     *
     *
     */
    protected void doSearch(String serv, String txt, int searchType) {
        if (_isSearching) {
            return;
        }

        limitWasExceeded = false;

        try {
            _isSearching = true;

            findBtn.setEnabled(false);

            // Indicate that searching has begin
            searchMessageLabel.setText(addContactBundle.getString("MSG_AddContactForm_Searching")); // NOI18N

            CollabPrincipal[] users = getCollabSession().findPrincipals(searchType, txt);
            Debug.out.println("Matches found for \"" + txt + "\": " + ((users != null) ? ("" + users.length) : "null"));

            if ((users == null) || (users.length == 0)) {
                // Let user know there were no matches
                searchMessageLabel.setText(addContactBundle.getString("No_Matches_Found")); // NOI18N
            } else if (users != null) {
                Vector selectedUsersCheck = new Vector(result.size());

                for (int i = 0; i < result.size(); i++) {
                    selectedUsersCheck.add(((CollabPrincipal) result.get(i)).getIdentifier());
                }

                if (users.length == 1) {
                    Debug.out.println("Search result: " + users[0].getIdentifier());

                    if (!selectedUsersCheck.contains(users[0].getIdentifier())) {
                        selectedUsersCheck.addElement(users[0].getIdentifier());
                        result.addElement(users[0]);
                        Debug.out.println("Added uid to result: " + users[0].getIdentifier());
                    }

                    // Indicate that a contact was added
                    searchMessageLabel.setText(addContactBundle.getString("MSG_AddContactForm_ContactAdded")); // NOI18N

                    contactJList.setListData(result);
                    updateValidStatus();
                } else if (users.length > 1) {
                    SelectUserForm f = new SelectUserForm(users);
                    CollabPrincipal[] ip = f.getSelectedUsers();

                    if (ip != null) {
                        for (int n = 0; n < ip.length; n++) {
                            Debug.out.println("selected uid = " + ip[n].getIdentifier());

                            if (!selectedUsersCheck.contains(ip[n].getIdentifier())) {
                                selectedUsersCheck.addElement(ip[n].getIdentifier());
                                result.addElement(ip[n]);
                                Debug.out.println("added uid to result = " + ip[n].getIdentifier());
                            }
                        }

                        // Indicate that a contact was added
                        searchMessageLabel.setText(addContactBundle.getString("MSG_AddContactForm_ContactAdded")); // NOI18N

                        contactJList.setListData(result);
                        updateValidStatus();
                    } else {
                        Debug.out.println("No search result selected");

                        // Indicate that no contacts were added
                        searchMessageLabel.setText(
                            addContactBundle.getString("MSG_AddContactForm_ContactSelectionCancelled")
                        ); // NOI18N
                    }
                }

                for (int i = 0; i < users.length; i++) {
                    Debug.out.println("search result: " + users[i].getDisplayName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Debug.logDebugException("Unknown error performing search", e, true);

            // TODO: Nice exception dialog here
            Debug.debugNotify(e);

            // Let user know there were no matches due to an error
            searchMessageLabel.setText(addContactBundle.getString("No_Matches_Found_Error")); // NOI18N
        } finally {
            _isSearching = false;
            findBtn.setEnabled(true);
        }
    }

    /**
     *
     *
     */
    public boolean isSearchByID() {
        if (useComboBoxes) {
            return (typeComboBox.getSelectedIndex() == 1);
        } else {
            return !nameTypeRadioButton.isSelected();
        }
    }

    /**
     *
     *
     */
    private Vector toUserName(Vector users) {
        Vector names = new Vector(users.size());

        for (int i = 0; i < users.size(); i++) {
            names.add(((CollabPrincipal) users.elementAt(i)).getDisplayName());
        }

        return names;
    }

    /**
     *
     *
     */
    protected void updateValidStatus() {
        dialogDescriptor.setValid(contactJList.getModel().getSize() != 0);
    }

    /**
     *
     *
     */
    protected void updateRemoveButtonState() {
        // #6176075
        removeBtn.setEnabled((contactJList.getModel().getSize() > 0) && !contactJList.isSelectionEmpty());
    }

    /**
     *
     *
     */
    private boolean sessionExists() {
        // check session status #5080138
        CollabSession[] sessions = CollabManager.getDefault().getSessions();

        for (int i = 0; i < sessions.length; i++) {
            if (sessions[i].equals(getCollabSession())) {
                return true;
            }
        }

        return false;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        optionsButtonGroup = new javax.swing.ButtonGroup();
        typeButtonGroup = new javax.swing.ButtonGroup();
        searchPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        findContactLb = new javax.swing.JLabel();
        findContactTextField = new javax.swing.JTextField();
        findBtn = new javax.swing.JButton();
        optionsLb = new javax.swing.JLabel();
        optionsPanel = new javax.swing.JPanel();
        containsOptionRadioButton = new javax.swing.JRadioButton();
        startsOptionRadioButton = new javax.swing.JRadioButton();
        endsOptionRadioButton = new javax.swing.JRadioButton();
        optionsComboBox = new javax.swing.JComboBox();
        TypeLb = new javax.swing.JLabel();
        typePanel = new javax.swing.JPanel();
        nameTypeRadioButton = new javax.swing.JRadioButton();
        userIDTypeRadioButton = new javax.swing.JRadioButton();
        typeComboBox = new javax.swing.JComboBox();
        searchMessageLabel = new javax.swing.JLabel();
        resultsPanel1 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        contactJList = new javax.swing.JList();
        removeBtn = new javax.swing.JButton();
        inviteMsgLbl = new javax.swing.JLabel();
        msgTextField = new javax.swing.JTextField();

        FormListener formListener = new FormListener();

        setLayout(new java.awt.GridBagLayout());

        setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5), null));
        setMinimumSize(new java.awt.Dimension(600, 400));
        searchPanel.setLayout(new java.awt.GridBagLayout());

        searchPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ParticipantSearchForm.class, "KEY_AddContactForm_SearchPanelTitle")), javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        jPanel2.setLayout(new java.awt.GridBagLayout());

        findContactLb.setText(org.openide.util.NbBundle.getMessage(ParticipantSearchForm.class, "LBL_AddContactForm_FindContact"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        jPanel2.add(findContactLb, gridBagConstraints);

        findContactTextField.setColumns(32);
        findContactTextField.addActionListener(formListener);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        jPanel2.add(findContactTextField, gridBagConstraints);

        findBtn.setText(org.openide.util.NbBundle.getMessage(ParticipantSearchForm.class, "BTN_AddContactForm_Find"));
        findBtn.setEnabled(false);
        findBtn.addActionListener(formListener);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel2.add(findBtn, gridBagConstraints);

        optionsLb.setText(org.openide.util.NbBundle.getMessage(ParticipantSearchForm.class, "LBL_AddContactForm_Options"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 5, 5);
        jPanel2.add(optionsLb, gridBagConstraints);

        optionsButtonGroup.add(containsOptionRadioButton);
        containsOptionRadioButton.setSelected(true);
        containsOptionRadioButton.setText(org.openide.util.NbBundle.getMessage(ParticipantSearchForm.class, "LBL_AddContactForm_Contains"));
        containsOptionRadioButton.setPreferredSize(null);
        optionsPanel.add(containsOptionRadioButton);

        optionsButtonGroup.add(startsOptionRadioButton);
        startsOptionRadioButton.setText(org.openide.util.NbBundle.getMessage(ParticipantSearchForm.class, "LBL_AddContactForm_Starts"));
        startsOptionRadioButton.setPreferredSize(null);
        optionsPanel.add(startsOptionRadioButton);

        optionsButtonGroup.add(endsOptionRadioButton);
        endsOptionRadioButton.setText(org.openide.util.NbBundle.getMessage(ParticipantSearchForm.class, "LBL_AddContactForm_Ends"));
        endsOptionRadioButton.setPreferredSize(null);
        optionsPanel.add(endsOptionRadioButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(optionsPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel2.add(optionsComboBox, gridBagConstraints);

        TypeLb.setText(org.openide.util.NbBundle.getMessage(ParticipantSearchForm.class, "LBL_AddContactForm_Type"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 5, 5);
        jPanel2.add(TypeLb, gridBagConstraints);

        typeButtonGroup.add(nameTypeRadioButton);
        nameTypeRadioButton.setSelected(true);
        nameTypeRadioButton.setText(org.openide.util.NbBundle.getMessage(ParticipantSearchForm.class, "LBL_AddContactForm_Name"));
        typePanel.add(nameTypeRadioButton);

        typeButtonGroup.add(userIDTypeRadioButton);
        userIDTypeRadioButton.setText(org.openide.util.NbBundle.getMessage(ParticipantSearchForm.class, "LBL_AddContactForm_UserID"));
        typePanel.add(userIDTypeRadioButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel2.add(typePanel, gridBagConstraints);

        typeComboBox.addItemListener(formListener);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel2.add(typeComboBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        searchPanel.add(jPanel2, gridBagConstraints);

        searchMessageLabel.setForeground(new java.awt.Color(0, 0, 255));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        searchPanel.add(searchMessageLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(searchPanel, gridBagConstraints);

        resultsPanel1.setLayout(new java.awt.GridBagLayout());

        resultsPanel1.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ParticipantSearchForm.class, "LBL_ParticipantSearchForm_searchResultLabel")), javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        contactJList.setMaximumSize(null);
        contactJList.setMinimumSize(null);
        contactJList.setPreferredSize(null);
        contactJList.setVisibleRowCount(10);
        jScrollPane1.setViewportView(contactJList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jScrollPane1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        resultsPanel1.add(jPanel1, gridBagConstraints);

        removeBtn.setText(org.openide.util.NbBundle.getMessage(ParticipantSearchForm.class, "BTN_AddContactForm_Remove"));
        removeBtn.setEnabled(false);
        removeBtn.addActionListener(formListener);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        resultsPanel1.add(removeBtn, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(resultsPanel1, gridBagConstraints);

        inviteMsgLbl.setText(org.openide.util.NbBundle.getMessage(ParticipantSearchForm.class, "LBL_ParticipantSearchForm_InviteMessage"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 3, 0, 5);
        add(inviteMsgLbl, gridBagConstraints);

        msgTextField.setText(org.openide.util.NbBundle.getMessage(ParticipantSearchForm.class, "MSG_ParticipantSearchForm_InvitationMessage"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 3);
        add(msgTextField, gridBagConstraints);

    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements java.awt.event.ActionListener, java.awt.event.ItemListener {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (evt.getSource() == findContactTextField) {
                ParticipantSearchForm.this.findContactTextFieldActionPerformed(evt);
            }
            else if (evt.getSource() == findBtn) {
                ParticipantSearchForm.this.findBtnActionPerformed(evt);
            }
            else if (evt.getSource() == removeBtn) {
                ParticipantSearchForm.this.removeBtnActionPerformed(evt);
            }
        }

        public void itemStateChanged(java.awt.event.ItemEvent evt) {
            if (evt.getSource() == typeComboBox) {
                ParticipantSearchForm.this.typeComboBoxItemStateChanged(evt);
            }
        }
    }
    // </editor-fold>//GEN-END:initComponents

    private void findContactTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findContactTextFieldActionPerformed
        search();
        findContactTextField.selectAll();
        findContactTextField.requestFocus();
    }//GEN-LAST:event_findContactTextFieldActionPerformed

    private void findBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findBtnActionPerformed
        search();
        findContactTextField.selectAll();
        findContactTextField.requestFocus();
    }//GEN-LAST:event_findBtnActionPerformed

    private void removeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeBtnActionPerformed

        // Remove the selected contacts from the list
        int[] selected = contactJList.getSelectedIndices();

        // Retrieve the set of selected elements
        List selectedElements = new ArrayList();

        for (int i = 0; i < selected.length; i++) {
            selectedElements.add(contactJList.getModel().getElementAt(selected[i]));
        }

        // Remove the elements from the model's vector by identity
        for (Iterator i = selectedElements.iterator(); i.hasNext();)
            result.removeElement(i.next());

        contactJList.clearSelection();
        contactJList.updateUI();
        updateValidStatus();
        updateRemoveButtonState();
    }//GEN-LAST:event_removeBtnActionPerformed

    private void typeComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_typeComboBoxItemStateChanged

        // TODO add your handling code here:
    }//GEN-LAST:event_typeComboBoxItemStateChanged

    public void valueChanged(ListSelectionEvent event) {
        updateRemoveButtonState();
    }
}
