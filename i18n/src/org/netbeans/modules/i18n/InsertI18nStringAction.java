/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License isdi available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */


package org.netbeans.modules.i18n;


import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import java.lang.ref.WeakReference;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.openide.cookies.EditorCookie;
import org.openide.nodes.Node;
import org.openide.NotifyDescriptor;
import org.openide.TopManager;
import org.openide.loaders.DataObject;
import org.openide.util.HelpCtx;
import org.openide.util.actions.CookieAction;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.Workspace;


/**
 * Insert internationalized string at caret position (if it is not in guarded block).
 *
 * @author   Petr Jiricka, Peter Zavadsky
 */
public class InsertI18nStringAction extends CookieAction {

    /** Generated serial version UID. */
    static final long serialVersionUID =-7002111874047983222L;       
    
    /** Weak reference to top component to which the i18n string will be added. */
    private WeakReference topComponentWRef = new WeakReference(null);

    /** Position to insert the new i18n-string. */
    private int position;
    
    
    /** 
     * Actually performs InsertI18nStringAction. Implements superclass abstract method.
     * @param activatedNodes currently activated nodes */
    protected void performAction (final Node[] activatedNodes) {
        final EditorCookie editorCookie = (EditorCookie)(activatedNodes[0]).getCookie(EditorCookie.class);
        if(editorCookie == null)
            return;
        
        editorCookie.open();

        // Set data object.
        DataObject dataObject = (DataObject)activatedNodes[0].getCookie(DataObject.class);
        if(dataObject == null)
            return; 

        JEditorPane[] panes = editorCookie.getOpenedPanes();
        
        if(panes == null || panes.length == 0)
            return;

        // Set insert position. 
        position = panes[0].getCaret().getDot();
        
        // If there is a i18n action in run on the same editor, cancel it.
        I18nManager.getDefault().cancel();

        try {
            addPanel(dataObject);
        } catch(IOException ioe) {
            if(Boolean.getBoolean("netbeans.debug.exceptions")) // NOI18N
                System.err.println("I18N: Document could not be loaded for "+dataObject.getName()); // NOI18N
            
            return;
        }

        // Ensure caret is visible.
        panes[0].getCaret().setVisible(true);;
    }

    /** Create panel used for specifying i18n string. */
    private JPanel createPanel(final DataObject dataObject) throws IOException {
        I18nSupport.Factory factory = FactoryRegistry.getFactory(dataObject.getClass());
        
        if(factory == null)
            throw new IllegalStateException("I18N: No factory registered for data object type="+dataObject.getClass().getName()); // NOI18N

        final I18nSupport support = factory.create(dataObject);
        
        final I18nPanel i18nPanel = new I18nPanel(support.getPropertyPanel(), false);
        
        i18nPanel.setI18nString(support.getDefaultI18nString());
        
        JButton OKButton = new JButton(I18nUtil.getBundle().getString("CTL_OKButton"));
        OKButton.setMnemonic((I18nUtil.getBundle().getString("CTL_OKButton_Mnem")).charAt(0));                 
        OKButton.getAccessibleContext().setAccessibleDescription(I18nUtil.getBundle().getString("ACS_CTL_OKButton"));  
        
        JButton cancelButton = new JButton(I18nUtil.getBundle().getString("CTL_CancelButton"));
        cancelButton.setMnemonic((I18nUtil.getBundle().getString("CTL_CancelButton_Mnem")).charAt(0));        
        cancelButton.getAccessibleContext().setAccessibleDescription(I18nUtil.getBundle().getString("ACS_CTL_CancelButton"));  
        
        JButton helpButton = new JButton(I18nUtil.getBundle().getString("CTL_HelpButton"));
        helpButton.setMnemonic((I18nUtil.getBundle().getString("CTL_HelpButton_Mnem")).charAt(0));        
        helpButton.getAccessibleContext().setAccessibleDescription(I18nUtil.getBundle().getString("ACS_CTL_HelpButton"));
        
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        JPanel rightPanel = new JPanel(new java.awt.GridLayout(1, 2, 5, 0));
        
        rightPanel.add(OKButton); 
        rightPanel.add(cancelButton);
        rightPanel.add(helpButton);
        
        helpButton.addActionListener(new ActionListener() {
           public void actionPerformed(java.awt.event.ActionEvent evt) {
              HelpCtx help = new HelpCtx(InsertI18nStringAction.class);

              String sysprop = System.getProperty("org.openide.actions.HelpAction.DEBUG"); // NOI18N

              if("true".equals(sysprop) || "full".equals(sysprop)) // NOI18N
                  System.err.println ("I18n module: Help button showing: " + help); // NOI18N, please do not comment out

              TopManager.getDefault().showHelp(help);

              return;               
           }
        });
                
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.0;        
        buttonPanel.add(new JPanel(), gridBagConstraints); 

        
        gridBagConstraints.gridx = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);        
        gridBagConstraints.weightx = 0.0;
        buttonPanel.add(rightPanel, gridBagConstraints);        

        // Panel.
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.NONE;
        gridBagConstraints.insets = new java.awt.Insets(17, 12, 11, 11);
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.weighty = 0.0;
        
        i18nPanel.add(buttonPanel, gridBagConstraints);

        // Set listeners for buttons.
        OKButton.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    // OK button.
                    try {
                        I18nString i18nString = i18nPanel.getI18nString();

                        if(i18nString.key == null) {
                            return;
                        }
                        
                        // Try to add key to bundle.                            
                        support.getResourceHolder().addProperty(i18nString.getKey(), i18nString.getValue(), i18nString.getComment());

                        // Create field if necessary. 
                        // PENDING, should not be performed here -> capability moves to i18n wizard.
                        if(support.hasAdditionalCustomizer())
                            support.performAdditionalChanges();

                        // Replace string.
                        support.getDocument().insertString(position, i18nString.getReplaceString(), null);

                    } catch (IllegalStateException e) {
                        NotifyDescriptor.Message msg = new NotifyDescriptor.Message(
                            I18nUtil.getBundle().getString("EXC_BadKey"),
                            NotifyDescriptor.ERROR_MESSAGE);
                        TopManager.getDefault().notify(msg);
                    } catch (BadLocationException e) {
                        TopManager.getDefault().notify(
                            new NotifyDescriptor.Message(
                                I18nUtil.getBundle().getString("MSG_CantInsertInGuarded"),
                                NotifyDescriptor.INFORMATION_MESSAGE
                            )
                        );
                    } finally { 
                        cancel();
                    }
                }
            }
        );

        cancelButton.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    cancel();
                }
            }
        );

        return i18nPanel;
    }
    
    /** Adds panel to top component in split pane. */
    private void addPanel(DataObject sourceDataObject) throws IOException {
        TopComponent topComponent = (TopComponent)topComponentWRef.get();

        if(topComponent == null) {
            JPanel panel = createPanel(sourceDataObject);
            
            // actually create the dialog as top component
            topComponent = new TopComponent() {
                public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
                }
                
                public void writeExternal(ObjectOutput out) throws IOException {
                }
                
                protected Object writeReplace() throws ObjectStreamException {
                    return null;
                }
            };            
            topComponent.setCloseOperation(TopComponent.CLOSE_EACH);
            topComponent.setLayout(new BorderLayout());
            topComponent.add(panel, BorderLayout.CENTER);
            topComponent.setName(sourceDataObject.getName());           

            // dock into I18N mode if possible
            Workspace[] currentWs = TopManager.getDefault().getWindowManager().getWorkspaces();
            for (int i = currentWs.length; --i >= 0; ) {
                Mode i18nMode = currentWs[i].findMode(I18nManager.I18N_MODE);
                if (i18nMode == null) {
                    i18nMode = currentWs[i].createMode(
                        I18nManager.I18N_MODE,
                        I18nUtil.getBundle().getString("CTL_I18nDialogTitle"),
                        I18nManager.class.getResource("/org/netbeans/modules/i18n/i18nAction.gif") // NOI18N
                    );
                }
                i18nMode.dockInto(topComponent);
            }
            
            // Reset weak reference.
            topComponentWRef = new WeakReference(topComponent);
        }

        topComponent.open();
        topComponent.requestFocus();
    }
    
    /** Cancels the current insert i18n string action. */
    public void cancel() {
        TopComponent topComponent= (TopComponent)topComponentWRef.get();
        
        if(topComponent != null)
            topComponent.close();
    }
    
    /** Overrides superclass method. Adds additional test if i18n module has registered factory
     * for this data object to be able to perform i18n action. */
    protected boolean enable(Node[] activatedNodes) {
        if (!super.enable(activatedNodes))
            return false;
        
        // if has an open editor pane must not be in a guarded block
        // PENDING>>
        // It causes StackOverflowError
        // I18nSupport.isGuardedPosittion() checks teh way it causes change cookies (remove add SaveCookie), what
        // in turn calls back enable method, it calls isGuardedPosition again etc. etc.
        /*final SourceCookie.Editor sec = (SourceCookie.Editor)(activatedNodes[0]).getCookie(SourceCookie.Editor.class);        
        if (sec != null) {
            JEditorPane[] edits = sec.getOpenedPanes();
            if (edits != null && edits.length > 0) {
                int position = edits[0].getCaret().getDot();
                StyledDocument doc = sec.getDocument();
                DataObject obj = (DataObject)sec.getSource().getCookie(DataObject.class);
                if(I18nSupport.getI18nSupport(doc, obj).isGuardedPosition(position))
                    return false;
            }
        }*/
        // PENDING<<        
        
        DataObject dataObject = (DataObject)activatedNodes[0].getCookie(DataObject.class);
        
        if(dataObject == null)
            return false;
        
        return FactoryRegistry.hasFactory(dataObject.getClass());
    }

    /** Implements superclass abstract method.
     * @return MODE_EXACTLY_ONE.
     */
    protected int mode () {
        return MODE_EXACTLY_ONE;
    }

    /** Implemenst superclass abstract method.
     * @return <code>EditorCookie<code>.class 
     * #see org.openide.cookies.EditorCookie */
    protected Class[] cookieClasses () {
        return new Class [] {
            EditorCookie.class
        };
    }

    /** Gets localized name of action. Overrides superclass method. */
    public String getName() {
        return I18nUtil.getBundle().getString("CTL_InsertI18nString");
    }

    /** Gets the action's help context. Implemenst superclass abstract method. */
    public HelpCtx getHelpCtx() {
        return new HelpCtx(I18nUtil.HELP_ID_MANINSERT);
    }

    /** Gets the action's icon location.
     * @return the action's icon location
     */
    protected String iconResource () {
        return "/org/netbeans/modules/i18n/insertI18nStringAction.gif"; // NOI18N
    }
}
