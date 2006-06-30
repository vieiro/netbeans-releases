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
 * Software is Sun Microsystems, Inc. Portions Copyright 2004-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.junit.wizards;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.project.Project;
import org.netbeans.modules.junit.CreateTestAction;
import org.netbeans.modules.junit.GuiUtils;
import org.netbeans.modules.junit.JUnitPluginTrampoline;
import org.netbeans.modules.junit.JUnitSettings;
import org.netbeans.modules.junit.TestCreator;
import org.netbeans.modules.junit.TestUtil;
import org.netbeans.modules.junit.plugin.JUnitPlugin;
import org.netbeans.modules.junit.plugin.JUnitPlugin.CreateTestParam;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.loaders.TemplateWizard;
import org.openide.util.NbBundle;

/**
 */
public class SimpleTestCaseWizardIterator
        implements TemplateWizard.Iterator {

    /** */
    private static SimpleTestCaseWizardIterator instance;

    /** */
    private TemplateWizard wizard;

    /** index of step &quot;Name &amp; Location&quot; */
    private static final int INDEX_CHOOSE_CLASS = 2;

    /** name of panel &quot;Name &amp; Location&quot; */
    private final String nameChooseClass = NbBundle.getMessage(
            SimpleTestCaseWizardIterator.class,
            "LBL_panel_ChooseClass");                                   //NOI18N
    /** index of the current panel */
    private int current;
    /** registered change listeners */
    private List changeListeners;
    /** */
    private Project lastSelectedProject = null;
    /** panel for choosing name and target location of the test class */
    private WizardDescriptor.Panel classChooserPanel;

    /**
     */
    public void addChangeListener(ChangeListener l) {
        if (changeListeners == null) {
            changeListeners = new ArrayList(2);
        }
        changeListeners.add(l);
    }

    /**
     */
    public void removeChangeListener(ChangeListener l) {
        if (changeListeners != null) {
            changeListeners.remove(l);
            if (changeListeners.isEmpty()) {
                changeListeners = null;
            }
        }
    }

    /**
     * Notifies all registered listeners about a change.
     *
     * @see  #addChangeListener
     * @see  #removeChangeListener
     */
    private void fireChange() {
        if (changeListeners != null) {
            ChangeEvent e = new ChangeEvent(this);
            Iterator i = changeListeners.iterator();
            while (i.hasNext()) {
                ((ChangeListener) i.next()).stateChanged(e);
            }
        }
    }

    /**
     */
    public boolean hasPrevious() {
        return current > INDEX_CHOOSE_CLASS;
    }

    /**
     */
    public boolean hasNext() {
        return current < INDEX_CHOOSE_CLASS;
    }

    /**
     */
    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        current--;
    }

    /**
     */
    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        current++;
    }

    /**
     */
    public WizardDescriptor.Panel current() {
        switch (current) {
            case INDEX_CHOOSE_CLASS:
                return getClassChooserPanel();
            default:
                throw new IllegalStateException();
        }
    }

    /**
     * Returns a panel for choosing name and target location of the test
     * class. If the panel already exists, returns the existing panel,
     * otherwise creates a new panel.
     *
     * @return  existing panel or a newly created panel if it did not exist
     */
    private WizardDescriptor.Panel getClassChooserPanel() {
        final Project project = Templates.getProject(wizard);
        if (classChooserPanel == null || project != lastSelectedProject) {
            final Utils utils = new Utils(project);
            if (utils.getSourcesToTestsMap(true).isEmpty()) {
                classChooserPanel = new StepProblemMessage(
                        project,
                        NbBundle.getMessage(EmptyTestCaseWizardIterator.class,
                                            "MSG_NoTestSourceGroup"));  //NOI18N
            } else {
                if (classChooserPanel == null) {
                    classChooserPanel = new SimpleTestStepLocation();
                }
                ((SimpleTestStepLocation) classChooserPanel).setUp(utils);
            }
        }
        lastSelectedProject = project;
        return classChooserPanel;
    }

    /**
     */
    public String name() {
        switch (current) {
            case INDEX_CHOOSE_CLASS:
                return nameChooseClass;
            default:
                throw new AssertionError(current);
        }
    }
    
    private void loadSettings(TemplateWizard wizard) {
        JUnitSettings settings = JUnitSettings.getDefault();
        
        wizard.putProperty(GuiUtils.CHK_PUBLIC,
                           Boolean.valueOf(settings.isMembersPublic()));
        wizard.putProperty(GuiUtils.CHK_PROTECTED,
                           Boolean.valueOf(settings.isMembersProtected()));
        wizard.putProperty(GuiUtils.CHK_PACKAGE,
                           Boolean.valueOf(settings.isMembersPackage()));
        wizard.putProperty(GuiUtils.CHK_SETUP,
                           Boolean.valueOf(settings.isGenerateSetUp()));
        wizard.putProperty(GuiUtils.CHK_TEARDOWN,
                           Boolean.valueOf(settings.isGenerateTearDown()));
        wizard.putProperty(GuiUtils.CHK_METHOD_BODIES,
                           Boolean.valueOf(settings.isBodyContent()));
        wizard.putProperty(GuiUtils.CHK_JAVADOC,
                           Boolean.valueOf(settings.isJavaDoc()));
        wizard.putProperty(GuiUtils.CHK_HINTS,
                           Boolean.valueOf(settings.isBodyComments()));

        wizard.putProperty("NewFileWizard_Title", NbBundle.getMessage(SimpleTestStepLocation.class, "LBL_simpleTestWizard_stepName"));
    }

    private void saveSettings(TemplateWizard wizard) {
        JUnitSettings settings = JUnitSettings.getDefault();
        
        settings.setMembersPublic(
                Boolean.TRUE.equals(wizard.getProperty(GuiUtils.CHK_PUBLIC)));
        settings.setMembersProtected(
                Boolean.TRUE.equals(wizard.getProperty(GuiUtils.CHK_PROTECTED)));
        settings.setMembersPackage(
                Boolean.TRUE.equals(wizard.getProperty(GuiUtils.CHK_PACKAGE)));
        settings.setGenerateSetUp(
                Boolean.TRUE.equals(wizard.getProperty(GuiUtils.CHK_SETUP)));
        settings.setGenerateTearDown(
                Boolean.TRUE.equals(wizard.getProperty(GuiUtils.CHK_TEARDOWN)));
        settings.setBodyContent(
                Boolean.TRUE.equals(wizard.getProperty(GuiUtils.CHK_METHOD_BODIES)));
        settings.setJavaDoc(
                Boolean.TRUE.equals(wizard.getProperty(GuiUtils.CHK_JAVADOC)));
        settings.setBodyComments(
                Boolean.TRUE.equals(wizard.getProperty(GuiUtils.CHK_HINTS)));
    }

    /**
     * <!-- PENDING -->
     */
    public void initialize(TemplateWizard wiz) {
        this.wizard = wiz;
        current = INDEX_CHOOSE_CLASS;
        loadSettings(wiz);

        String [] panelNames =  new String [] {
          NbBundle.getMessage(EmptyTestCaseWizardIterator.class,"LBL_panel_chooseFileType"),
          NbBundle.getMessage(EmptyTestCaseWizardIterator.class,"LBL_panel_ChooseClass")};

        ((javax.swing.JComponent)getClassChooserPanel().getComponent()).putClientProperty("WizardPanel_contentData", panelNames); 
        ((javax.swing.JComponent)getClassChooserPanel().getComponent()).putClientProperty("WizardPanel_contentSelectedIndex", new Integer(0)); 

    }

    /**
     * <!-- PENDING -->
     */
    public void uninitialize(TemplateWizard wiz) {
        if ((classChooserPanel != null)
                && !(classChooserPanel instanceof StepProblemMessage)) {
            
            assert classChooserPanel instanceof SimpleTestStepLocation;
            
            ((SimpleTestStepLocation) classChooserPanel).cleanUp();
        }
        classChooserPanel = null;
        changeListeners = null;
        this.wizard = null;
    }

    public Set instantiate(TemplateWizard wiz) throws IOException {
        saveSettings(wiz);
        
        /* collect and build necessary data: */
        FileObject classToTest = (FileObject)
                wizard.getProperty(SimpleTestCaseWizard.PROP_CLASS_TO_TEST);
        FileObject testRootFolder = (FileObject)
                wizard.getProperty(SimpleTestCaseWizard.PROP_TEST_ROOT_FOLDER);
        Map<CreateTestParam, Object> params
                = TestUtil.getSettingsMap(false);
                
        /* create test class(es) for the selected source class: */
        JUnitPlugin plugin = TestUtil.getPluginForProject(
                                                Templates.getProject(wizard));
        /*
         * The JUnitPlugin instance must be initialized _before_ field
         * JUnitPluginTrampoline.DEFAULT gets accessed.
         * See issue #74744.
         */
        final FileObject[] testFileObjects
                = JUnitPluginTrampoline.DEFAULT.createTests(
                     plugin,
                     new FileObject[] {classToTest},
                     testRootFolder,
                     params);
        
        //XXX: What if the selected class is not testable?
        //     It should not be skipped!
        
        if (testFileObjects == null) {
            throw new IOException();
        }
        
        final Set<DataObject> dataObjects
               = new HashSet<DataObject>((int) (testFileObjects.length * 1.5f));
        for (FileObject testFile : testFileObjects) {
            try {
                dataObjects.add(DataObject.find(testFile));
            } catch (DataObjectNotFoundException ex) {
                //XXX - does nothing special - just continues
            }
        }
        
        if (dataObjects.isEmpty()) {
            throw new IOException();
        }
        return dataObjects;
    }

    /**
     */
    public static SimpleTestCaseWizardIterator singleton() {
        if (instance == null) {
            // PENDING - it should not be kept forever
            instance = new SimpleTestCaseWizardIterator();
        }
        return instance;
    }

}
