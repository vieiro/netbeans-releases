/*
 * IgnoreTest.java
 *
 * Created on June 8, 2006, 9:36 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.test.subversion.main.commit;

import java.io.File;
import java.io.PrintStream;
import javax.swing.table.TableModel;
import junit.textui.TestRunner;
import org.netbeans.jellytools.JellyTestCase;
import org.netbeans.jellytools.NbDialogOperator;
import org.netbeans.jellytools.OutputTabOperator;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jellytools.nodes.SourcePackagesNode;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.TimeoutExpiredException;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.junit.NbTestSuite;
import org.netbeans.junit.ide.ProjectSupport;
import org.netbeans.test.subversion.operators.CheckoutWizardOperator;
import org.netbeans.test.subversion.operators.RepositoryStepOperator;
import org.netbeans.test.subversion.operators.VersioningOperator;
import org.netbeans.test.subversion.operators.WorkDirStepOperator;
import org.netbeans.test.subversion.utils.RepositoryMaintenance;
import org.netbeans.test.subversion.utils.TestKit;

/**
 *
 * @author peter pis
 */
public class IgnoreTest extends JellyTestCase {
    
    public static final String TMP_PATH = "/tmp";
    public static final String REPO_PATH = "repo";
    public static final String WORK_PATH = "work";
    public static final String PROJECT_NAME = "SVNApplication";
    public File projectPath;
    public PrintStream stream;
    String os_name;
    
    /** Creates a new instance of IgnoreTest */
    public IgnoreTest(String name) {
        super(name);
    }
    
    protected void setUp() throws Exception {        
        os_name = System.getProperty("os.name");
        //System.out.println(os_name);
        System.out.println("### "+getName()+" ###");
        
    }
    
    protected boolean isUnix() {
        boolean unix = false;
        if (os_name.indexOf("Windows") == -1) {
            unix = true;
        }
        return unix;
    }
    
    public static void main(String[] args) {
        // TODO code application logic here
        TestRunner.run(suite());
    }
    
    public static NbTestSuite suite() {
        NbTestSuite suite = new NbTestSuite();
        suite.addTest(new IgnoreTest("testIgnoreUnignoreFile"));
        suite.addTest(new IgnoreTest("testIgnoreUnignorePackage"));
        suite.addTest(new IgnoreTest("testIgnoreUnignoreFilePackage"));
        return suite;
    }
    
    public void testIgnoreUnignoreFile() throws Exception {
        JemmyProperties.setCurrentTimeout("ComponentOperator.WaitComponentTimeout", 30000);
        JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", 30000);    
        stream = new PrintStream(new File(getWorkDir(), getName() + ".log"));
        CheckoutWizardOperator co = CheckoutWizardOperator.invoke();
        RepositoryStepOperator rso = new RepositoryStepOperator();       
        VersioningOperator vo = VersioningOperator.invoke();
        
        //create repository... 
        new File(TMP_PATH).mkdirs();
        new File(TMP_PATH + File.separator + WORK_PATH).mkdirs();
        RepositoryMaintenance.deleteFolder(new File(TMP_PATH + File.separator + REPO_PATH));
        RepositoryMaintenance.deleteFolder(new File(TMP_PATH + File.separator + WORK_PATH));
        RepositoryMaintenance.createRepository(TMP_PATH + File.separator + REPO_PATH);   
        RepositoryMaintenance.loadRepositoryFromFile(TMP_PATH + File.separator + REPO_PATH, getDataDir().getCanonicalPath() + File.separator + "repo_dump");      
        rso.setRepositoryURL(RepositoryStepOperator.ITEM_FILE + RepositoryMaintenance.changeFileSeparator(TMP_PATH + File.separator + REPO_PATH, false));
        
        rso.next();
        OutputTabOperator oto = new OutputTabOperator("file:///tmp");
        oto.clear();
        WorkDirStepOperator wdso = new WorkDirStepOperator();
        wdso.setRepositoryFolder("trunk/JavaApp");
        wdso.setLocalFolder(TMP_PATH + File.separator + WORK_PATH);
        wdso.checkCheckoutContentOnly(false);
        wdso.finish();
        //open project
        oto.waitText("Checking out... finished.");
        NbDialogOperator nbdialog = new NbDialogOperator("Checkout Completed");
        JButtonOperator open = new JButtonOperator(nbdialog, "Open Project");
        open.push();
        ProjectSupport.waitScanFinished();
        
        oto = new OutputTabOperator("file:///tmp");
        oto.clear();
        TestKit.createNewElement("JavaApp", "javaapp", "NewClass");
        Node node = new Node(new SourcePackagesNode("JavaApp"), "javaapp|NewClass");
        node.performPopupAction("Subversion|Ignore");
        oto.waitText("finished.");
        
        node = new Node(new SourcePackagesNode("JavaApp"), "javaapp|NewClass");
        org.openide.nodes.Node nodeIDE = (org.openide.nodes.Node) node.getOpenideNode();
        String color = TestKit.getColor(nodeIDE.getHtmlDisplayName());
        String status = TestKit.getStatus(nodeIDE.getHtmlDisplayName());
        assertEquals("Wrong color of node - file color should be ignored!!!", TestKit.IGNORED_COLOR, color);
        assertEquals("Wrong annotation of node - file status should be ignored!!!", TestKit.IGNORED_STATUS, status);
        
        node = new Node(new SourcePackagesNode("JavaApp"), "javaapp|NewClass");
        TimeoutExpiredException tee = null;
        try {
            node.performPopupAction("Subversion|Ignore");
        } catch (Exception e) {
            tee = (TimeoutExpiredException) e;
        }   
        assertNotNull("Ingnore action should be disabled!!!", tee);
        
        //unignore file
        oto = new OutputTabOperator("file:///tmp");
        oto.clear();
        node = new Node(new SourcePackagesNode("JavaApp"), "javaapp|NewClass");
        node.performPopupAction("Subversion|Unignore");
        oto.waitText("finished.");
        node = new Node(new SourcePackagesNode("JavaApp"), "javaapp|NewClass");
        nodeIDE = (org.openide.nodes.Node) node.getOpenideNode();        
        color = TestKit.getColor(nodeIDE.getHtmlDisplayName());
        status = TestKit.getStatus(nodeIDE.getHtmlDisplayName());
        assertEquals("Wrong color of node - file color should be new!!!", TestKit.NEW_COLOR, color);
        assertEquals("Wrong annotation of node - file status should be new!!!", TestKit.NEW_STATUS, status);
        
        //verify content of Versioning view
        oto = new OutputTabOperator("file:///tmp");
        oto.clear();
        node = new Node(new SourcePackagesNode("JavaApp"), "javaapp|NewClass");
        node.performPopupAction("Subversion|Show Changes");
        oto.waitText("Refreshing... finished.");
        Thread.sleep(1000);
        vo = VersioningOperator.invoke();
        TableModel model = vo.tabFiles().getModel();
        assertEquals("Versioning view should be empty", 1, model.getRowCount());
        assertEquals("File should be listed in Versioning view", "NewClass.java", model.getValueAt(0, 0).toString());
        
        TestKit.removeAllData("JavaApp");
        stream.flush();
        stream.close();
    }
    
    public void testIgnoreUnignorePackage() throws Exception {
        JemmyProperties.setCurrentTimeout("ComponentOperator.WaitComponentTimeout", 30000);
        JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", 30000);    
        stream = new PrintStream(new File(getWorkDir(), getName() + ".log"));
        CheckoutWizardOperator co = CheckoutWizardOperator.invoke();
        RepositoryStepOperator rso = new RepositoryStepOperator();       
        VersioningOperator vo = VersioningOperator.invoke();
        
        //create repository... 
        new File(TMP_PATH).mkdirs();
        new File(TMP_PATH + File.separator + WORK_PATH).mkdirs();
        RepositoryMaintenance.deleteFolder(new File(TMP_PATH + File.separator + REPO_PATH));
        RepositoryMaintenance.deleteFolder(new File(TMP_PATH + File.separator + WORK_PATH));
        RepositoryMaintenance.createRepository(TMP_PATH + File.separator + REPO_PATH);   
        RepositoryMaintenance.loadRepositoryFromFile(TMP_PATH + File.separator + REPO_PATH, getDataDir().getCanonicalPath() + File.separator + "repo_dump");      
        rso.setRepositoryURL(RepositoryStepOperator.ITEM_FILE + RepositoryMaintenance.changeFileSeparator(TMP_PATH + File.separator + REPO_PATH, false));
        
        rso.next();
        OutputTabOperator oto = new OutputTabOperator("file:///tmp");
        oto.clear();  
        WorkDirStepOperator wdso = new WorkDirStepOperator();
        wdso.setRepositoryFolder("trunk/JavaApp");
        wdso.setLocalFolder(TMP_PATH + File.separator + WORK_PATH);
        wdso.checkCheckoutContentOnly(false);
        wdso.finish();
        //open project
        oto.waitText("Checking out... finished.");
        NbDialogOperator nbdialog = new NbDialogOperator("Checkout Completed");
        JButtonOperator open = new JButtonOperator(nbdialog, "Open Project");
        open.push();
        ProjectSupport.waitScanFinished();
        
        oto = new OutputTabOperator("file:///tmp");
        oto.clear();
        TestKit.createNewPackage("JavaApp", "xx");
        Node node = new Node(new SourcePackagesNode("JavaApp"), "xx");
        node.performPopupAction("Subversion|Ignore");
        oto.waitText("finished.");
        
        node = new Node(new SourcePackagesNode("JavaApp"), "xx");
        org.openide.nodes.Node nodeIDE = (org.openide.nodes.Node) node.getOpenideNode();
        //String color = TestKit.getColor(nodeIDE.getHtmlDisplayName());
        String status = TestKit.getStatus(nodeIDE.getHtmlDisplayName());
        //assertEquals("Wrong color of node - package color should be ignored!!!", TestKit.IGNORED_COLOR, color);
        assertEquals("Wrong annotation of node - package status should be ignored!!!", TestKit.IGNORED_STATUS, status);
        
        /*verify content of Versioning view
        oto = new OutputTabOperator("file:///tmp");
        oto.clear();
        node = new Node(new SourcePackagesNode("JavaApp"), "javaapp|NewClass");
        node.performPopupAction("Subversion|Show Changes");
        oto.waitText("Refreshing... finished.");
        Thread.sleep(1000);
        vo = VersioningOperator.invoke();
        TableModel model = vo.tabFiles().getModel();
        assertEquals("Versioning view should be empty", 0, model.getRowCount());*/
        
        node = new Node(new SourcePackagesNode("JavaApp"), "xx");
        TimeoutExpiredException tee = null;
        try {
            node.performPopupAction("Subversion|Ignore");
        } catch (Exception e) {
            tee = (TimeoutExpiredException) e;
        }   
        assertNotNull("Ingnore action should be disabled!!!", tee);
        
        //unignore file
        oto = new OutputTabOperator("file:///tmp");
        oto.clear();
        node = new Node(new SourcePackagesNode("JavaApp"), "xx");
        node.performPopupAction("Subversion|Unignore");
        oto.waitText("finished.");
        node = new Node(new SourcePackagesNode("JavaApp"), "xx");
        nodeIDE = (org.openide.nodes.Node) node.getOpenideNode();        
        //color = TestKit.getColor(nodeIDE.getHtmlDisplayName());
        status = TestKit.getStatus(nodeIDE.getHtmlDisplayName());
        //assertEquals("Wrong color of node - package color should be new!!!", TestKit.NEW_COLOR, color);
        assertEquals("Wrong annotation of node - package status should be new!!!", TestKit.NEW_STATUS, status);
        
        //verify content of Versioning view
        oto = new OutputTabOperator("file:///tmp");
        oto.clear();
        node = new Node(new SourcePackagesNode("JavaApp"), "xx");
        node.performPopupAction("Subversion|Show Changes");
        oto.waitText("Refreshing... finished.");
        Thread.sleep(1000);
        vo = VersioningOperator.invoke();
        TableModel model = vo.tabFiles().getModel();
        assertEquals("Versioning view should be empty", 1, model.getRowCount());
        assertEquals("Package should be listed in Versioning view", "xx", model.getValueAt(0, 0).toString());
        
        TestKit.removeAllData("JavaApp");
        stream.flush();
        stream.close();
    }
    
    public void testIgnoreUnignoreFilePackage() throws Exception {
        JemmyProperties.setCurrentTimeout("ComponentOperator.WaitComponentTimeout", 30000);
        JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", 30000);    
        stream = new PrintStream(new File(getWorkDir(), getName() + ".log"));
        CheckoutWizardOperator co = CheckoutWizardOperator.invoke();
        RepositoryStepOperator rso = new RepositoryStepOperator();       
        VersioningOperator vo = VersioningOperator.invoke();
       
        //create repository... 
        new File(TMP_PATH).mkdirs();
        new File(TMP_PATH + File.separator + WORK_PATH).mkdirs();
        RepositoryMaintenance.deleteFolder(new File(TMP_PATH + File.separator + REPO_PATH));
        RepositoryMaintenance.deleteFolder(new File(TMP_PATH + File.separator + WORK_PATH));
        RepositoryMaintenance.createRepository(TMP_PATH + File.separator + REPO_PATH);   
        RepositoryMaintenance.loadRepositoryFromFile(TMP_PATH + File.separator + REPO_PATH, getDataDir().getCanonicalPath() + File.separator + "repo_dump");      
        rso.setRepositoryURL(RepositoryStepOperator.ITEM_FILE + RepositoryMaintenance.changeFileSeparator(TMP_PATH + File.separator + REPO_PATH, false));
        
        rso.next();
        OutputTabOperator oto = new OutputTabOperator("file:///tmp");
        oto.clear();
        WorkDirStepOperator wdso = new WorkDirStepOperator();
        wdso.setRepositoryFolder("trunk/JavaApp");
        wdso.setLocalFolder(TMP_PATH + File.separator + WORK_PATH);
        wdso.checkCheckoutContentOnly(false);
        wdso.finish();
        //open project
        oto.waitText("Checking out... finished.");
        NbDialogOperator nbdialog = new NbDialogOperator("Checkout Completed");
        JButtonOperator open = new JButtonOperator(nbdialog, "Open Project");
        open.push();
        ProjectSupport.waitScanFinished();
        
        oto = new OutputTabOperator("file:///tmp");
        oto.clear();
        TestKit.createNewElements("JavaApp", "xx", "NewClass");
        
        Node node = new Node(new SourcePackagesNode("JavaApp"), "xx");
        Node node2 = new Node(new SourcePackagesNode("JavaApp"), "xx|NewClass");
        node.performPopupAction("Subversion|Ignore");
        oto.waitText("finished.");
        
        node = new Node(new SourcePackagesNode("JavaApp"), "xx");
        node2 = new Node(new SourcePackagesNode("JavaApp"), "xx|NewClass");
        org.openide.nodes.Node nodeIDE = (org.openide.nodes.Node) node.getOpenideNode();
        org.openide.nodes.Node nodeIDE2 = (org.openide.nodes.Node) node2.getOpenideNode();
        //String color = TestKit.getColor(nodeIDE.getHtmlDisplayName());
        String status = TestKit.getStatus(nodeIDE.getHtmlDisplayName());
        String status2 = TestKit.getStatus(nodeIDE2.getHtmlDisplayName());
        //assertEquals("Wrong color of node - package color should be ignored!!!", TestKit.IGNORED_COLOR, color);
        assertEquals("Wrong annotation of node - package status should be ignored!!!", TestKit.IGNORED_STATUS, status);
        assertEquals("Wrong annotation of file - package status should be ignored!!!", TestKit.IGNORED_STATUS, status2);
        
        //unignore file
        oto = new OutputTabOperator("file:///tmp");
        oto.clear();
        node = new Node(new SourcePackagesNode("JavaApp"), "xx");
        node.performPopupAction("Subversion|Unignore");
        oto.waitText("finished.");
        node = new Node(new SourcePackagesNode("JavaApp"), "xx");
        node2 = new Node(new SourcePackagesNode("JavaApp"), "xx|NewClass");
        nodeIDE = (org.openide.nodes.Node) node.getOpenideNode();        
        nodeIDE2 = (org.openide.nodes.Node) node2.getOpenideNode();        
        String color = TestKit.getColor(nodeIDE2.getHtmlDisplayName());
        status = TestKit.getStatus(nodeIDE.getHtmlDisplayName());
        status2 = TestKit.getStatus(nodeIDE.getHtmlDisplayName());
        assertEquals("Wrong color of node - file color should be new!!!", TestKit.NEW_COLOR, color);
        assertEquals("Wrong annotation of node - package status should be new!!!", TestKit.NEW_STATUS, status);
        assertEquals("Wrong annotation of node - file status should be new!!!", TestKit.NEW_STATUS, status2);
        
        //verify content of Versioning view
        oto = new OutputTabOperator("file:///tmp");
        oto.clear();
        node = new Node(new SourcePackagesNode("JavaApp"), "xx");
        node.performPopupAction("Subversion|Show Changes");
        oto.waitText("Refreshing... finished.");
        Thread.sleep(1000);
        vo = VersioningOperator.invoke();
        TableModel model = vo.tabFiles().getModel();
        assertEquals("Versioning view should be empty", 2, model.getRowCount());
        String[] expected = {"xx", "NewClass.java"};
        String[] actual = new String[model.getRowCount()];
        for (int i = 0; i < actual.length; i++) {
            actual[i] = model.getValueAt(i, 0).toString();
        }
        int result = TestKit.compareThem(expected, actual, false);
        assertEquals("Wrong records in Versioning view", 2, result);
        TestKit.removeAllData("JavaApp");
        stream.flush();
        stream.close();
    }
}
