/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.jellytools.modules.javacvs;

import java.awt.Component;
import javax.swing.JComponent;
import org.netbeans.jellytools.Bundle;
import org.netbeans.jellytools.TopComponentOperator;
import org.netbeans.jellytools.actions.Action;
import org.netbeans.jemmy.ComponentChooser;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JPopupMenuOperator;
import org.netbeans.jemmy.operators.JTableOperator;
import org.netbeans.jemmy.operators.JToggleButtonOperator;

/** Class implementing all necessary methods for handling "Versioning" view.
 * <br>
 * Usage:<br>
 * <pre>
 *      VersioningOperator vo = VersioningOperator.invoke();
 *      vo.checkLocal(true);
 *      vo.checkRemote(true);
 *      vo.checkAll(true);
 *      vo.refresh();
 *      vo.diff();
 *      vo.update();
 *      vo.performPopup("MyFile", "Exclude from Commit");
 *      CommitOperator co = vo.commit();
 *      co.setCommitMessage("Commit message.");
 *      co.commit();
 * </pre>
 *
 * @see CommitOperator
 * @see org.netbeans.jellytools.modules.javacvs.actions.ShowChangesAction
 *
 * @author Jiri.Skrivanek@sun.com
 */
public class VersioningOperator extends TopComponentOperator {
    
    /** "Versioning" */
    static final String VERSIONING_TITLE = Bundle.getStringTrimmed(
            "org.netbeans.modules.versioning.system.cvss.ui.syncview.Bundle",
            "CTL_Synchronize_TopComponent_Title");
    
    /** Waits for Versioning TopComponent within whole IDE. */
    public VersioningOperator() {
        super(waitTopComponent(null, VERSIONING_TITLE, 0, new VersioningSubchooser()));
    }
    
    /** Invokes Window|Versioning main menu item and returns new instance of
     * VersioningOperator.
     * @return new instance of VersioningOperator */
    public static VersioningOperator invoke() {
        String windowItem = Bundle.getStringTrimmed("org.netbeans.core.Bundle", "Menu/Window");
        String versioningItem = Bundle.getStringTrimmed(
                "org.netbeans.modules.versioning.system.cvss.ui.actions.status.Bundle",
                "BK0001");
        new Action(windowItem+"|"+versioningItem, null).perform();
        return new VersioningOperator();
    }
    
    private JToggleButtonOperator _tbAll;
    private JToggleButtonOperator _tbLocal;
    private JToggleButtonOperator _tbRemote;
    private JButtonOperator _btRefresh;
    private JButtonOperator _btDiff;
    private JButtonOperator _btUpdate;
    private JButtonOperator _btCommit;
    private JTableOperator _tabFiles;
    
    
    //******************************
    // Subcomponents definition part
    //******************************
    
    /** Tries to find "All" JToggleButton in this dialog.
     * @return JToggleButtonOperator
     */
    public JToggleButtonOperator tbAll() {
        if (_tbAll==null) {
            _tbAll = new JToggleButtonOperator(this, Bundle.getStringTrimmed(
                    "org.netbeans.modules.versioning.system.cvss.ui.syncview.Bundle",
                    "CTL_Synchronize_Action_All_Label"));
        }
        return _tbAll;
    }
    
    /** Tries to find "Local" JToggleButton in this dialog.
     * @return JToggleButtonOperator
     */
    public JToggleButtonOperator tbLocal() {
        if (_tbLocal==null) {
            _tbLocal = new JToggleButtonOperator(this, Bundle.getStringTrimmed(
                    "org.netbeans.modules.versioning.system.cvss.ui.syncview.Bundle",
                    "CTL_Synchronize_Action_Local_Label"));
        }
        return _tbLocal;
    }
    
    /** Tries to find "Remote" JToggleButton in this dialog.
     * @return JToggleButtonOperator
     */
    public JToggleButtonOperator tbRemote() {
        if (_tbRemote==null) {
            _tbRemote = new JToggleButtonOperator(this, Bundle.getStringTrimmed(
                    "org.netbeans.modules.versioning.system.cvss.ui.syncview.Bundle",
                    "CTL_Synchronize_Action_Remote_Label"));
        }
        return _tbRemote;
    }
    
    /** Tries to find Refresh Status JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btRefresh() {
        if (_btRefresh==null) {
            _btRefresh = new JButtonOperator(this, new TooltipChooser(
                    Bundle.getString(
                    "org.netbeans.modules.versioning.system.cvss.ui.syncview.Bundle",
                    "CTL_Synchronize_Action_Refresh_Tooltip"),
                    this.getComparator()));
        }
        return _btRefresh;
    }
    
    /** Tries to find Diff All JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btDiff() {
        if (_btDiff==null) {
            _btDiff = new JButtonOperator(this, new TooltipChooser(
                    Bundle.getString(
                    "org.netbeans.modules.versioning.system.cvss.ui.syncview.Bundle",
                    "CTL_Synchronize_Action_Diff_Tooltip"),
                    this.getComparator()));
        }
        return _btDiff;
    }
    
    /** Tries to find Update All JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btUpdate() {
        if (_btUpdate==null) {
            _btUpdate = new JButtonOperator(this, new TooltipChooser(
                    Bundle.getString(
                    "org.netbeans.modules.versioning.system.cvss.ui.syncview.Bundle",
                    "CTL_Synchronize_Action_Update_Tooltip"),
                    this.getComparator()));
        }
        return _btUpdate;
    }
    
    /** Tries to find Commit All JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btCommit() {
        if (_btCommit==null) {
            _btCommit = new JButtonOperator(this, new TooltipChooser(
                    Bundle.getString(
                    "org.netbeans.modules.versioning.system.cvss.ui.syncview.Bundle",
                    "CTL_CommitForm_Action_Commit_Tooltip"),
                    this.getComparator()));
        }
        return _btCommit;
    }
    
    /** Tries to find files JTable in this dialog.
     * @return JTableOperator
     */
    public JTableOperator tabFiles() {
        if (_tabFiles==null) {
            _tabFiles = new JTableOperator(this);
        }
        return _tabFiles;
    }
    
    
    //****************************************
    // Low-level functionality definition part
    //****************************************
    
    /** checks or unchecks given JToggleButton
     * @param state boolean requested state
     */
    public void checkAll(boolean state) {
        if (tbAll().isSelected()!=state) {
            tbAll().push();
        }
    }
    
    /** checks or unchecks given JToggleButton
     * @param state boolean requested state
     */
    public void checkLocal(boolean state) {
        if (tbLocal().isSelected()!=state) {
            tbLocal().push();
        }
    }
    
    /** checks or unchecks given JToggleButton
     * @param state boolean requested state
     */
    public void checkRemote(boolean state) {
        if (tbRemote().isSelected()!=state) {
            tbRemote().push();
        }
    }
    
    /** clicks on Refresh Status JButton
     */
    public void refresh() {
        btRefresh().push();
    }
    
    /** clicks on Diff All JButton
     */
    public void diff() {
        btDiff().push();
    }
    
    /** clicks on Update All JButton
     */
    public void update() {
        btUpdate().push();
    }
    
    /** clicks on Commit JButton and returns CommitOperator.
     * @return CommitOperator instance
     */
    public CommitOperator commit() {
        btCommit().pushNoBlock();
        return new CommitOperator();
    }
    
    /** Performs popup menu on specified row.
     * @param row row number to be selected (starts from 0)
     * @param popupPath popup menu path
     */
    public void performPopup(int row, String popupPath) {
        tabFiles().selectCell(row, 0);
        JPopupMenuOperator popup = new JPopupMenuOperator(tabFiles().callPopupOnCell(row, 0));
        popup.pushMenu(popupPath);
    }
    
    /** Performs popup menu on specified file.
     * @param filename name of file to be selected
     * @param popupPath popup menu path
     */
    public void performPopup(String filename, String popupPath) {
        performPopup(tabFiles().findCellRow(filename), popupPath);
    }
    
    //*****************************************
    // High-level functionality definition part
    //*****************************************
    
    /** Performs verification of VersioningOperator by accessing all its components.
     */
    public void verify() {
        tbAll();
        tbLocal();
        tbRemote();
        btRefresh();
        btDiff();
        btUpdate();
        btCommit();
        tabFiles();
    }
    
    /** SubChooser to determine TopComponent is instance of
     * org.netbeans.modules.versioning.system.cvss.ui.syncview.CvsSynchronizeTopComponent
     * Used in constructor.
     */
    private static final class VersioningSubchooser implements ComponentChooser {
        public boolean checkComponent(Component comp) {
            return comp.getClass().getName().endsWith("CvsSynchronizeTopComponent");
        }
        
        public String getDescription() {
            return "org.netbeans.modules.versioning.system.cvss.ui.syncview.CvsSynchronizeTopComponent";
        }
    }
    
    /** Chooser which can be used to find a component with given tooltip,
     * for example a button.
     */
    private static class TooltipChooser implements ComponentChooser {
        private String buttonTooltip;
        private StringComparator comparator;
        
        public TooltipChooser(String buttonTooltip, StringComparator comparator) {
            this.buttonTooltip = buttonTooltip;
            this.comparator = comparator;
        }
        
        public boolean checkComponent(Component comp) {
            return comparator.equals(((JComponent)comp).getToolTipText(), buttonTooltip);
        }
        
        public String getDescription() {
            return "Button with tooltip \""+buttonTooltip+"\".";
        }
    }
}

