/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.java.j2seproject;

import java.io.PrintWriter;
import java.net.URL;
import java.util.Properties;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.java.j2seproject.applet.AppletSupport;
import org.netbeans.modules.java.j2seproject.ui.customizer.MainClassChooser;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.netbeans.api.project.TestUtil;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.ProjectGenerator;
import org.openide.filesystems.URLMapper;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 * Tests for J2SEActionProvider
 *
 * @author David Konecny
 */
public class J2SEActionProviderTest extends NbTestCase {
    
    public J2SEActionProviderTest(java.lang.String testName) {
        super(testName);
    }

    private FileObject scratch;
    private FileObject projdir;
    private FileObject sources;
    private FileObject build;
    private FileObject tests;
    private ProjectManager pm;
    private Project pp;
    private J2SEActionProvider actionProvider;
    private DataObject someSource1;
    private DataObject someSource2;
    private DataObject someSource3;
    private DataObject someTest1;
    private DataObject someTest2;
    
    protected void setUp() throws Exception {
        super.setUp();
        TestUtil.setLookup(new Object[] {
            new org.netbeans.modules.java.j2seproject.J2SEProjectType(),
            new org.netbeans.modules.projectapi.SimpleFileOwnerQueryImplementation()
        }, J2SEActionProvider.class.getClassLoader());
        scratch = TestUtil.makeScratchDir(this);
        projdir = scratch.createFolder("proj");
        AntProjectHelper helper = ProjectGenerator.createProject(projdir, "org.netbeans.modules.java.j2seproject");
        pm = ProjectManager.getDefault();
        pp = pm.findProject(projdir);
        actionProvider = (J2SEActionProvider)pp.getLookup().lookup(J2SEActionProvider.class);
        EditableProperties props = helper.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
        props.setProperty("src.dir", "src");
        props.setProperty("test.src.dir", "test");
        props.setProperty("build.dir", "build");
        props.setProperty("build.classes.dir", "${build.dir}/classes");
        props.setProperty("main.class", "foo.Main");
        helper.putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, props);
        sources = projdir.createFolder("src");
        tests = projdir.createFolder("test");
        projdir.createData("build.xml");
        build = projdir.createFolder("build");
        build.createFolder("classes");
        FileObject fo = sources.createFolder("foo").createData("Bar.java");
        someSource1 = DataObject.find(fo);
        fo = sources.getFileObject("foo").createData("Main.java");
        createMain(fo);
        someSource2 = DataObject.find(fo);
        fo = sources.getFileObject("foo").createData("Third.java");
        someSource3 = DataObject.find(fo);
        fo = tests.createFolder("foo").createData("BarTest.java");
        someTest1 = DataObject.find(fo);
        fo = tests.getFileObject("foo").createData("MainTest.java");
        someTest2 = DataObject.find(fo);
        assertNotNull(someSource1);
        assertNotNull(someSource2);
        assertNotNull(someTest1);
        assertNotNull(someTest2);
    }

    protected void tearDown() throws Exception {
        scratch = null;
        projdir = null;
        pm = null;
        TestUtil.setLookup(Lookup.EMPTY);
        super.tearDown();
    }
    
    private void createMain(FileObject fo) throws Exception {
        FileLock lock = fo.lock();
        PrintWriter pw = new PrintWriter(fo.getOutputStream(lock));
        pw.println("package foo;");
        pw.println("public class Main { public static void main(String[] args){}; };");
        pw.flush();
        pw.close();
        lock.releaseLock();
    }
    
    public void testGetTargetNames() throws Exception {
        Properties p;
        Lookup context;
        String[] targets;
        
        // test COMMAND_COMPILE_SINGLE
        
        p = new Properties();
        context = Lookups.fixed(new DataObject[] {someSource1});
        targets = actionProvider.getTargetNames(ActionProvider.COMMAND_COMPILE_SINGLE, context, p);
        assertNotNull("Must found some targets for COMMAND_COMPILE_SINGLE", targets);
        assertEquals("There must be one target for COMMAND_COMPILE_SINGLE", 1, targets.length);
        assertEquals("Unexpected target name", "compile-single", targets[0]);
        assertEquals("There must be one target parameter", 1, p.keySet().size());
        assertEquals("There must be be target parameter", "foo/Bar.java", p.getProperty("javac.includes"));
        p = new Properties();
        context = Lookups.fixed(new DataObject[] {someTest1,someTest2});
        targets = actionProvider.getTargetNames(ActionProvider.COMMAND_COMPILE_SINGLE, context, p);
        assertNotNull("Must found some targets for COMMAND_COMPILE_SINGLE", targets);
        assertEquals("There must be one target for COMMAND_COMPILE_SINGLE", 1, targets.length);
        assertEquals("Unexpected target name", "compile-test-single", targets[0]);
        assertEquals("There must be one target parameter", 1, p.keySet().size());
        assertEquals("There must be be target parameter", "foo/BarTest.java,foo/MainTest.java", p.getProperty("javac.includes"));
        
        // test COMMAND_TEST_SINGLE
        
        p = new Properties();
        context = Lookups.fixed(new DataObject[] {someSource1});
        targets = actionProvider.getTargetNames(ActionProvider.COMMAND_TEST_SINGLE, context, p);
        assertNotNull("Must found some targets for COMMAND_TEST_SINGLE", targets);
        assertEquals("There must be one target for COMMAND_TEST_SINGLE", 1, targets.length);
        assertEquals("Unexpected target name", "test-single", targets[0]);
        assertEquals("There must be one target parameter", 2, p.keySet().size());
        assertEquals("There must be be target parameter", "foo/BarTest.java", p.getProperty("javac.includes"));
        assertEquals("There must be be target parameter", "foo/BarTest.java", p.getProperty("test.includes"));
        p = new Properties();
        context = Lookups.fixed(new DataObject[] {someSource1,someSource2});
        targets = actionProvider.getTargetNames(ActionProvider.COMMAND_TEST_SINGLE, context, p);
        assertNotNull("Must found some targets for COMMAND_TEST_SINGLE", targets);
        assertEquals("There must be one target for COMMAND_TEST_SINGLE", 1, targets.length);
        assertEquals("Unexpected target name", "test-single", targets[0]);
        assertEquals("There must be one target parameter", 2, p.keySet().size());
        assertEquals("There must be be target parameter", "foo/BarTest.java,foo/MainTest.java", p.getProperty("javac.includes"));
        assertEquals("There must be be target parameter", "foo/BarTest.java,foo/MainTest.java", p.getProperty("test.includes"));
        
        // test COMMAND_DEBUG_TEST_SINGLE
        
        p = new Properties();
        context = Lookups.fixed(new DataObject[] {someSource1});
        targets = actionProvider.getTargetNames(ActionProvider.COMMAND_DEBUG_TEST_SINGLE, context, p);
        assertNotNull("Must found some targets for COMMAND_DEBUG_TEST_SINGLE", targets);
        assertEquals("There must be one target for COMMAND_DEBUG_TEST_SINGLE", 1, targets.length);
        assertEquals("Unexpected target name", "debug-test", targets[0]);
        assertEquals("There must be one target parameter", 1, p.keySet().size());
        assertEquals("There must be be target parameter", "foo.BarTest", p.getProperty("test.class"));
        
        // test COMMAND_DEBUG_FIX
        
        p = new Properties();
        context = Lookups.fixed(new DataObject[] {someSource1});
        targets = actionProvider.getTargetNames(JavaProjectConstants.COMMAND_DEBUG_FIX, context, p);
        assertNotNull("Must found some targets for COMMAND_DEBUG_FIX", targets);
        assertEquals("There must be one target for COMMAND_DEBUG_FIX", 1, targets.length);
        assertEquals("Unexpected target name", "debug-fix", targets[0]);
        assertEquals("There must be one target parameter", 1, p.keySet().size());
        assertEquals("There must be be target parameter", "foo/Bar", p.getProperty("fix.includes"));
        p = new Properties();
        context = Lookups.fixed(new DataObject[] {someTest1});
        targets = actionProvider.getTargetNames(JavaProjectConstants.COMMAND_DEBUG_FIX, context, p);
        assertNotNull("Must found some targets for COMMAND_DEBUG_FIX", targets);
        assertEquals("There must be one target for COMMAND_DEBUG_FIX", 1, targets.length);
        assertEquals("Unexpected target name", "debug-fix-test", targets[0]);
        assertEquals("There must be one target parameter", 1, p.keySet().size());
        assertEquals("There must be be target parameter", "foo/BarTest", p.getProperty("fix.includes"));
        
        // test COMMAND_RUN_SINGLE
        
        p = new Properties();
        context = Lookups.fixed(new DataObject[] {someSource2});
        MainClassChooser.unitTestingSupport_hasMainMethodResult = Boolean.TRUE;
        try {
            targets = actionProvider.getTargetNames(ActionProvider.COMMAND_RUN_SINGLE, context, p);
        } finally {
            MainClassChooser.unitTestingSupport_hasMainMethodResult = null;
        }
        assertNotNull("Must found some targets for COMMAND_RUN_SINGLE", targets);
        assertEquals("There must be one target for COMMAND_RUN_SINGLE", 1, targets.length);
        assertEquals("Unexpected target name", "run-single", targets[0]);
        assertEquals("There must be one target parameter", 2, p.keySet().size());
        assertEquals("There must be be target parameter", "foo/Main.java", p.getProperty("javac.includes"));
        assertEquals("There must be be target parameter", "foo.Main", p.getProperty("run.class"));
        p = new Properties();
        context = Lookups.fixed(new DataObject[] {someSource2});
        MainClassChooser.unitTestingSupport_hasMainMethodResult = Boolean.FALSE;
        AppletSupport.unitTestingSupport_isApplet = Boolean.TRUE;
        try {
            targets = actionProvider.getTargetNames(ActionProvider.COMMAND_RUN_SINGLE, context, p);
        } finally {
            MainClassChooser.unitTestingSupport_hasMainMethodResult = null;
            AppletSupport.unitTestingSupport_isApplet = null;
        }
        assertNotNull("Must found some targets for COMMAND_RUN_SINGLE", targets);
        assertEquals("There must be one target for COMMAND_RUN_SINGLE", 1, targets.length);
        assertEquals("Unexpected target name", "run-applet", targets[0]);
        assertEquals("There must be one target parameter", 2, p.keySet().size());
        assertEquals("There must be be target parameter", "foo/Main.java", p.getProperty("javac.includes"));
        FileObject appletHtml = build.getFileObject("Main", "html");
        assertNotNull("Applet HTML page must be generated", appletHtml);
        URL appletUrl = URLMapper.findURL(appletHtml, URLMapper.EXTERNAL);
        assertEquals("There must be be target parameter", appletUrl.toExternalForm(), p.getProperty("applet.url"));
        p = new Properties();
        context = Lookups.fixed(new DataObject[] {someTest1});
        MainClassChooser.unitTestingSupport_hasMainMethodResult = Boolean.TRUE;
        AppletSupport.unitTestingSupport_isApplet = Boolean.TRUE;
        try {
            targets = actionProvider.getTargetNames(ActionProvider.COMMAND_RUN_SINGLE, context, p);
        } finally {
            MainClassChooser.unitTestingSupport_hasMainMethodResult = null;
            AppletSupport.unitTestingSupport_isApplet = null;
        }
        assertNotNull("Must found some targets for COMMAND_RUN_SINGLE", targets);
        assertEquals("There must be one target for COMMAND_RUN_SINGLE", 1, targets.length);
        assertEquals("Unexpected target name", "test-single", targets[0]);
        assertEquals("There must be one target parameter", 2, p.keySet().size());
        assertEquals("There must be be target parameter", "foo/BarTest.java", p.getProperty("javac.includes"));
        assertEquals("There must be be target parameter", "foo/BarTest.java", p.getProperty("test.includes"));

        // test COMMAND_DEBUG_SINGLE
        
        p = new Properties();
        context = Lookups.fixed(new DataObject[] {someSource2});
        MainClassChooser.unitTestingSupport_hasMainMethodResult = Boolean.TRUE;
        try {
            targets = actionProvider.getTargetNames(ActionProvider.COMMAND_DEBUG_SINGLE, context, p);
        } finally {
            MainClassChooser.unitTestingSupport_hasMainMethodResult = null;
        }
        assertNotNull("Must found some targets for COMMAND_DEBUG_SINGLE", targets);
        assertEquals("There must be one target for COMMAND_DEBUG_SINGLE", 1, targets.length);
        assertEquals("Unexpected target name", "debug-single", targets[0]);
        assertEquals("There must be one target parameter", 2, p.keySet().size());
        assertEquals("There must be be target parameter", "foo/Main.java", p.getProperty("javac.includes"));
        assertEquals("There must be be target parameter", "foo.Main", p.getProperty("debug.class"));
        p = new Properties();
        context = Lookups.fixed(new DataObject[] {someSource2});
        MainClassChooser.unitTestingSupport_hasMainMethodResult = Boolean.FALSE;
        AppletSupport.unitTestingSupport_isApplet = Boolean.TRUE;
        try {
            targets = actionProvider.getTargetNames(ActionProvider.COMMAND_DEBUG_SINGLE, context, p);
        } finally {
            MainClassChooser.unitTestingSupport_hasMainMethodResult = null;
            AppletSupport.unitTestingSupport_isApplet = null;
        }
        assertNotNull("Must found some targets for COMMAND_DEBUG_SINGLE", targets);
        assertEquals("There must be one target for COMMAND_DEBUG_SINGLE", 1, targets.length);
        assertEquals("Unexpected target name", "debug-applet", targets[0]);
        assertEquals("There must be one target parameter", 3, p.keySet().size());
        assertEquals("There must be be target parameter", "foo/Main.java", p.getProperty("javac.includes"));
        appletHtml = build.getFileObject("Main", "html");
        assertNotNull("Applet HTML page must be generated", appletHtml);
        appletUrl = URLMapper.findURL(appletHtml, URLMapper.EXTERNAL);
        assertEquals("There must be be target parameter", appletUrl.toExternalForm(), p.getProperty("applet.url"));
        p = new Properties();
        context = Lookups.fixed(new DataObject[] {someTest1});
        MainClassChooser.unitTestingSupport_hasMainMethodResult = Boolean.TRUE;
        AppletSupport.unitTestingSupport_isApplet = Boolean.TRUE;
        try {
            targets = actionProvider.getTargetNames(ActionProvider.COMMAND_DEBUG_SINGLE, context, p);
        } finally {
            MainClassChooser.unitTestingSupport_hasMainMethodResult = null;
            AppletSupport.unitTestingSupport_isApplet = null;
        }
        assertNotNull("Must found some targets for COMMAND_DEBUG_SINGLE", targets);
        assertEquals("There must be one target for COMMAND_DEBUG_SINGLE", 1, targets.length);
        assertEquals("Unexpected target name", "debug-test", targets[0]);
        assertEquals("There must be one target parameter", 1, p.keySet().size());
        assertEquals("There must be be target parameter", "foo.BarTest", p.getProperty("test.class"));
        
        // test COMMAND_RUN
        
        p = new Properties();
        context = Lookup.EMPTY;
        MainClassChooser.unitTestingSupport_hasMainMethodResult = Boolean.TRUE;
        try {
            targets = actionProvider.getTargetNames(ActionProvider.COMMAND_RUN, context, p);
        } finally {
            MainClassChooser.unitTestingSupport_hasMainMethodResult = null;
        }
        assertNotNull("Must found some targets for COMMAND_RUN", targets);
        assertEquals("There must be one target for COMMAND_RUN", 1, targets.length);
        assertEquals("Unexpected target name", "run", targets[0]);
        assertEquals("There must be one target parameter", 1, p.keySet().size());
        assertEquals("There must be be target parameter", "foo.Main", p.getProperty("main.class"));
        
        // test COMMAND_DEBUG
        
        p = new Properties();
        context = Lookup.EMPTY;
        MainClassChooser.unitTestingSupport_hasMainMethodResult = Boolean.TRUE;
        try {
            targets = actionProvider.getTargetNames(ActionProvider.COMMAND_DEBUG, context, p);
        } finally {
            MainClassChooser.unitTestingSupport_hasMainMethodResult = null;
        }
        assertNotNull("Must found some targets for COMMAND_DEBUG", targets);
        assertEquals("There must be one target for COMMAND_DEBUG", 1, targets.length);
        assertEquals("Unexpected target name", "debug", targets[0]);
        assertEquals("There must be one target parameter", 2, p.keySet().size());
        assertEquals("There must be be target parameter", "foo.Main", p.getProperty("main.class"));
        
        // test COMMAND_DEBUG_STEP_INTO
        
        p = new Properties();
        context = Lookup.EMPTY;
        MainClassChooser.unitTestingSupport_hasMainMethodResult = Boolean.TRUE;
        try {
            targets = actionProvider.getTargetNames(ActionProvider.COMMAND_DEBUG_STEP_INTO, context, p);
        } finally {
            MainClassChooser.unitTestingSupport_hasMainMethodResult = null;
        }
        assertNotNull("Must found some targets for COMMAND_DEBUG_STEP_INTO", targets);
        assertEquals("There must be one target for COMMAND_DEBUG_STEP_INTO", 1, targets.length);
        assertEquals("Unexpected target name", "debug-stepinto", targets[0]);
        assertEquals("There must be one target parameter", 2, p.keySet().size());
        assertEquals("There must be be target parameter", "foo.Main", p.getProperty("main.class"));
        
    }
    
    public void testIsActionEnabled() throws Exception {
        Lookup context;
        boolean enabled;
        
        // test COMMAND_COMPILE_SINGLE
        
        context = Lookups.fixed(new DataObject[] {someSource1});
        enabled = actionProvider.isActionEnabled(ActionProvider.COMMAND_COMPILE_SINGLE, context);
        assertTrue("COMMAND_COMPILE_SINGLE must be enabled on one source", enabled);

        context = Lookups.fixed(new DataObject[] {someSource1, someSource2});
        enabled = actionProvider.isActionEnabled(ActionProvider.COMMAND_COMPILE_SINGLE, context);
        assertTrue("COMMAND_COMPILE_SINGLE must be enabled on multiple sources", enabled);
        
        context = Lookups.fixed(new DataObject[] {someTest1, someTest2});
        enabled = actionProvider.isActionEnabled(ActionProvider.COMMAND_COMPILE_SINGLE, context);
        assertTrue("COMMAND_COMPILE_SINGLE must be enabled on multiple tests", enabled);
        
        context = Lookups.fixed(new DataObject[] {someSource1, someTest1});
        enabled = actionProvider.isActionEnabled(ActionProvider.COMMAND_COMPILE_SINGLE, context);
        assertFalse("COMMAND_COMPILE_SINGLE must be disabled on mixed files", enabled);
        
        // test COMMAND_TEST_SINGLE
        
        context = Lookups.fixed(new DataObject[] {someTest1});
        enabled = actionProvider.isActionEnabled(ActionProvider.COMMAND_TEST_SINGLE, context);
        assertFalse("COMMAND_TEST_SINGLE must be disabled on one test", enabled);
        
        context = Lookups.fixed(new DataObject[] {someTest1, someTest2});
        enabled = actionProvider.isActionEnabled(ActionProvider.COMMAND_TEST_SINGLE, context);
        assertFalse("COMMAND_TEST_SINGLE must be disabled on multiple tests", enabled);
        
        context = Lookups.fixed(new DataObject[] {someSource3});
        enabled = actionProvider.isActionEnabled(ActionProvider.COMMAND_TEST_SINGLE, context);
        assertFalse("COMMAND_TEST_SINGLE must be disabled on non-test file which does not have associated test", enabled);

        context = Lookups.fixed(new DataObject[] {someSource2});
        enabled = actionProvider.isActionEnabled(ActionProvider.COMMAND_TEST_SINGLE, context);
        assertTrue("COMMAND_TEST_SINGLE must be enabled on source file which has associated test", enabled);
        
        context = Lookups.fixed(new DataObject[] {someSource1, someSource2});
        enabled = actionProvider.isActionEnabled(ActionProvider.COMMAND_TEST_SINGLE, context);
        assertTrue("COMMAND_TEST_SINGLE must be enabled on source files which has associated tests", enabled);
        
        context = Lookups.fixed(new DataObject[] {someSource1, someSource3});
        enabled = actionProvider.isActionEnabled(ActionProvider.COMMAND_TEST_SINGLE, context);
        assertFalse("COMMAND_TEST_SINGLE must be disabled on mixture of source files when some files do not have tests", enabled);
        
        context = Lookups.fixed(new DataObject[] {someSource1, someTest1});
        enabled = actionProvider.isActionEnabled(ActionProvider.COMMAND_TEST_SINGLE, context);
        assertFalse("COMMAND_TEST_SINGLE must be disabled on mixture of source files and test files", enabled);
        
        // test COMMAND_DEBUG_TEST_SINGLE
        
        context = Lookups.fixed(new DataObject[] {someTest1});
        enabled = actionProvider.isActionEnabled(ActionProvider.COMMAND_DEBUG_TEST_SINGLE, context);
        assertFalse("COMMAND_DEBUG_TEST_SINGLE must be disabled on test files", enabled);
        
        context = Lookups.fixed(new DataObject[] {someTest1, someTest2});
        enabled = actionProvider.isActionEnabled(ActionProvider.COMMAND_DEBUG_TEST_SINGLE, context);
        assertFalse("COMMAND_DEBUG_TEST_SINGLE must be disabled on multiple tests", enabled);
        
        context = Lookups.fixed(new DataObject[] {someSource3});
        enabled = actionProvider.isActionEnabled(ActionProvider.COMMAND_DEBUG_TEST_SINGLE, context);
        assertFalse("COMMAND_DEBUG_TEST_SINGLE must be disabled on non-test file which does not have associated test", enabled);

        context = Lookups.fixed(new DataObject[] {someSource2});
        enabled = actionProvider.isActionEnabled(ActionProvider.COMMAND_DEBUG_TEST_SINGLE, context);
        assertTrue("COMMAND_DEBUG_TEST_SINGLE must be enabled on source file which has associated test", enabled);

        context = Lookups.fixed(new DataObject[] {someSource1, someSource2});
        enabled = actionProvider.isActionEnabled(ActionProvider.COMMAND_DEBUG_TEST_SINGLE, context);
        assertFalse("COMMAND_DEBUG_TEST_SINGLE must be disabled on multiple source files", enabled);

        // test COMMAND_DEBUG_FIX
        
        context = Lookups.fixed(new DataObject[] {someTest1});
        enabled = actionProvider.isActionEnabled(JavaProjectConstants.COMMAND_DEBUG_FIX, context);
        assertTrue("COMMAND_DEBUG_FIX must be enabled on one test", enabled);
        
        context = Lookups.fixed(new DataObject[] {someTest1, someTest2});
        enabled = actionProvider.isActionEnabled(JavaProjectConstants.COMMAND_DEBUG_FIX, context);
        assertFalse("COMMAND_DEBUG_FIX must be disabled on multiple tests", enabled);
        
        context = Lookups.fixed(new DataObject[] {someSource1});
        enabled = actionProvider.isActionEnabled(JavaProjectConstants.COMMAND_DEBUG_FIX, context);
        assertTrue("COMMAND_DEBUG_FIX must be enabled on one source", enabled);

        context = Lookups.fixed(new DataObject[] {someSource1, someSource2});
        enabled = actionProvider.isActionEnabled(JavaProjectConstants.COMMAND_DEBUG_FIX, context);
        assertFalse("COMMAND_DEBUG_FIX must be disabled on multiple source files", enabled);
        
        context = Lookups.fixed(new DataObject[] {someSource1, someTest1});
        enabled = actionProvider.isActionEnabled(JavaProjectConstants.COMMAND_DEBUG_FIX, context);
        assertFalse("COMMAND_DEBUG_FIX must be disabled on multiple mixed files", enabled);
        
        // test COMMAND_RUN_SINGLE
        
        context = Lookups.fixed(new DataObject[] {someSource1});
        enabled = actionProvider.isActionEnabled(ActionProvider.COMMAND_RUN_SINGLE, context);
        assertTrue("COMMAND_RUN_SINGLE must be enabled on one source", enabled);
        
        context = Lookups.fixed(new DataObject[] {someSource1, someSource2});
        enabled = actionProvider.isActionEnabled(ActionProvider.COMMAND_RUN_SINGLE, context);
        assertFalse("COMMAND_RUN_SINGLE must be disabled on multiple sources", enabled);
        
        context = Lookups.fixed(new DataObject[] {someTest1});
        enabled = actionProvider.isActionEnabled(ActionProvider.COMMAND_RUN_SINGLE, context);
        assertTrue("COMMAND_RUN_SINGLE must be enabled on test file", enabled);

        context = Lookups.fixed(new DataObject[] {someTest1, someTest2});
        enabled = actionProvider.isActionEnabled(ActionProvider.COMMAND_RUN_SINGLE, context);
        assertFalse("COMMAND_RUN_SINGLE must be disabled on multiple test files", enabled);
        
        context = Lookups.fixed(new DataObject[] {someSource1, someTest1});
        enabled = actionProvider.isActionEnabled(ActionProvider.COMMAND_RUN_SINGLE, context);
        assertFalse("COMMAND_RUN_SINGLE must be disabled on mixed multiple test files", enabled);
        
        // test COMMAND_DEBUG_SINGLE
        
        context = Lookups.fixed(new DataObject[] {someSource1});
        enabled = actionProvider.isActionEnabled(ActionProvider.COMMAND_DEBUG_SINGLE, context);
        assertTrue("COMMAND_DEBUG_SINGLE must be enabled on one source", enabled);
        
        context = Lookups.fixed(new DataObject[] {someSource1, someSource2});
        enabled = actionProvider.isActionEnabled(ActionProvider.COMMAND_DEBUG_SINGLE, context);
        assertFalse("COMMAND_DEBUG_SINGLE must be disabled on multiple sources", enabled);
        
        context = Lookups.fixed(new DataObject[] {someTest1});
        enabled = actionProvider.isActionEnabled(ActionProvider.COMMAND_DEBUG_SINGLE, context);
        assertTrue("COMMAND_DEBUG_SINGLE must be enabled on test file", enabled);

        context = Lookups.fixed(new DataObject[] {someTest1, someTest2});
        enabled = actionProvider.isActionEnabled(ActionProvider.COMMAND_DEBUG_SINGLE, context);
        assertFalse("COMMAND_DEBUG_SINGLE must be disabled on multiple test files", enabled);

        context = Lookups.fixed(new DataObject[] {someSource1, someTest1});
        enabled = actionProvider.isActionEnabled(ActionProvider.COMMAND_DEBUG_SINGLE, context);
        assertFalse("COMMAND_DEBUG_SINGLE must be disabled on mixed multiple test files", enabled);
    }
    
}
