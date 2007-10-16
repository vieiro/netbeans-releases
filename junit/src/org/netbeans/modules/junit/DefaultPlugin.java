/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.junit;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.lang.model.element.TypeElement;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.project.classpath.ProjectClassPathModifier;
import org.netbeans.api.java.queries.UnitTestForSourceQuery;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.api.project.libraries.Library;
import org.netbeans.api.project.libraries.LibraryManager;
import org.netbeans.modules.junit.TestabilityResult.SkippedClass;
import org.netbeans.modules.junit.plugin.JUnitPlugin;
import org.netbeans.modules.junit.plugin.JUnitPlugin.CreateTestParam;
import org.netbeans.modules.junit.plugin.JUnitPlugin.Location;
import org.netbeans.modules.junit.wizards.Utils;
import org.netbeans.spi.java.classpath.ClassPathProvider;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.awt.Mnemonics;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.HelpCtx;
import org.openide.util.Mutex;
import org.openide.util.NbBundle;
import static java.util.logging.Level.FINER;
import static java.util.logging.Level.FINEST;
import static javax.lang.model.util.ElementFilter.methodsIn;
import static javax.lang.model.util.ElementFilter.typesIn;
import static org.netbeans.api.java.classpath.ClassPath.SOURCE;
import static org.netbeans.api.java.classpath.ClassPath.COMPILE;
import static org.netbeans.api.java.classpath.ClassPath.BOOT;
import static org.netbeans.api.java.project.JavaProjectConstants.SOURCES_TYPE_JAVA;
import static org.netbeans.modules.junit.JUnitSettings.JUNIT_GENERATOR_ASK_USER;
import static org.openide.ErrorManager.ERROR;
import static org.openide.ErrorManager.WARNING;
import static org.openide.NotifyDescriptor.CANCEL_OPTION;
import static org.openide.NotifyDescriptor.OK_CANCEL_OPTION;
import static org.openide.NotifyDescriptor.QUESTION_MESSAGE;
import static org.openide.NotifyDescriptor.WARNING_MESSAGE;

/**
 * Default JUnit plugin.
 *
 * @author  Marian Petras
 */
public final class DefaultPlugin extends JUnitPlugin {

    /** logger for logging management of JUnit libraries */
    private static final Logger LOG_JUNIT_VER
            = Logger.getLogger(DefaultPlugin.class.getName()
                               + "_JUnit_version_handling");            //NOI18N

    /** full name of a file specific for the JUnit 3.8.x library */
    private static final String JUNIT3_SPECIFIC
                                = "junit/awtui/TestRunner.class";       //NOI18N
    /** full name of a file specific for the JUnit 4.x library */
    private static final String JUNIT4_SPECIFIC
                                = "org/junit/Test.class";               //NOI18N
    
    /** */
    private JUnitVersion junitVer;

    /** name of FreeMarker template property - generate {@code &#64;BeforeClass} method? */
    private static final String templatePropBeforeClass = "classSetUp"; //NOI18N
    /** name of FreeMarker template property - generate {@code &#64;AfterClass} method? */
    private static final String templatePropAfterClass = "classTearDown";//NOI18N
    /** name of FreeMarker template property - generate {@code &#64;Before} method? */
    private static final String templatePropBefore = "methodSetUp";     //NOI18N
    /** name of FreeMarker template property - generate {@code &#64;After} method? */
    private static final String templatePropAfter = "methodTearDown";   //NOI18N
    /** name of FreeMarker template property - generate in-method source code hints? */
    private static final String templatePropCodeHints = "sourceCodeHint";   //NOI18N
    /** name of FreeMarker template property - generate hints - method placeholders? */
    private static final String templatePropMethodPH = "testMethodsPlaceholder"; //NOI18N
    /** name of FreeMarker template property - use Java annotations? */
    private static final String templatePropUseAnnotations = "useAnnotations"; //NOI18N
    /** name of FreeMarker template property - list of class names */
    private static final String templatePropClassNames = "classNames";  //NOI18N
    /**
     * name of FreeMarker template property - list of class names,
     * each with a suffix <code>&quot;.class&quot;</code>
     */
    private static final String templatePropClasses = "classes";        //NOI18N
    
    /** */
    private static java.util.ResourceBundle bundle = org.openide.util.NbBundle.getBundle(
            DefaultPlugin.class);

    /**
     * 
     */
    @Override
    protected boolean canCreateTests(FileObject... fileObjects) {
        if (fileObjects.length == 0) {
            return false;
        }

        final FileObject firstFile = fileObjects[0];
        final SourceGroup sourceGroup = findSourceGroup(firstFile);
        if (sourceGroup == null) {
            return false;
        }
        final FileObject rootFolder = sourceGroup.getRootFolder();
        if (UnitTestForSourceQuery.findUnitTests(rootFolder).length == 0) {
            return false;
        }

        /*
         * Now we know that source folder of the first file has a corresponding
         * test folder (possible non-existent).
         */
        if (fileObjects.length == 1) {
            /* ... if there is just one file selected, it is all we need: */
            return true;
        }

        /*
         * ...for multiple files, we just check that all the selected files
         * have the same root folder:
         */
        for (int i = 1; i < fileObjects.length; i++) {
            FileObject fileObj = fileObjects[i];
            if (!FileUtil.isParentOf(rootFolder, fileObj)
                    || !sourceGroup.contains(fileObj)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Finds a Java source group the given file belongs to.
     * 
     * @param  file  {@code FileObject} to find a {@code SourceGroup} for
     * @return  the found {@code SourceGroup}, or {@code null} if the given
     *          file does not belong to any Java source group
     */
    private static SourceGroup findSourceGroup(FileObject file) {
        final Project project = FileOwnerQuery.getOwner(file);
        if (project == null) {
            return null;
        }

        Sources src = ProjectUtils.getSources(project);
        SourceGroup[] srcGrps = src.getSourceGroups(SOURCES_TYPE_JAVA);
        for (SourceGroup srcGrp : srcGrps) {
            FileObject rootFolder = srcGrp.getRootFolder();
            if (((file == rootFolder) || FileUtil.isParentOf(rootFolder, file)) 
                    && srcGrp.contains(file)) {
                return srcGrp;
            }
        }
        return null;
    }

    /**
     *
     */
    protected Location getTestLocation(Location sourceLocation) {
        FileObject fileObj = sourceLocation.getFileObject();
        ClassPath srcCp;
        
        if ((srcCp = ClassPath.getClassPath(fileObj, SOURCE)) == null) {
            return null;
        }
        
        String baseResName = srcCp.getResourceName(fileObj, '/', false);
        String testResName = !fileObj.isFolder()
                             ? getTestResName(baseResName, fileObj.getExt())
                             : getSuiteResName(baseResName);
        assert testResName != null;
        
        return getOppositeLocation(sourceLocation,
                                   srcCp,
                                   testResName,
                                   true);
    }
    
    /**
     *
     */
    protected Location getTestedLocation(Location testLocation) {
        FileObject fileObj = testLocation.getFileObject();
        ClassPath srcCp;
        
        if (fileObj.isFolder()
               || ((srcCp = ClassPath.getClassPath(fileObj, SOURCE)) == null)) {
            return null;
        }
        
        String baseResName = srcCp.getResourceName(fileObj, '/', false);
        String srcResName = getSrcResName(baseResName, fileObj.getExt());
        if (srcResName == null) {
            return null;     //if the selectedFO is not a test class (by name)
        }

        return getOppositeLocation(testLocation,
                                   srcCp,
                                   srcResName,
                                   false);
    }
    
    /**
     *
     */
    private static Location getOppositeLocation(
                                    final Location sourceLocation,
                                    final ClassPath fileObjCp,
                                    final String oppoResourceName,
                                    final boolean sourceToTest) {
        FileObject fileObj = sourceLocation.getFileObject();
        FileObject fileObjRoot;
        
        if ((fileObjRoot = fileObjCp.findOwnerRoot(fileObj)) == null) {
            return null;
        }
        
        URL[] oppoRootsURLs = sourceToTest
                              ? UnitTestForSourceQuery.findUnitTests(fileObjRoot)
                              : UnitTestForSourceQuery.findSources(fileObjRoot);
        //if (sourceToTest && (oppoRootsURLs.length == 0)) {
        //    PENDING - offer creation of new unit tests root
        //}
        if ((oppoRootsURLs == null) || (oppoRootsURLs.length == 0)) {
            return null;
        }
        
        ClassPath oppoRootsClassPath = ClassPathSupport
                                           .createClassPath(oppoRootsURLs);
        final List<FileObject> oppoFiles = oppoRootsClassPath
                                           .findAllResources(oppoResourceName);
        if (oppoFiles.isEmpty()) {
            //if (sourceToTest) {
            //    PENDING - offer creation of new test class
            //}
            return null;
        }
        
//        final ElementHandle elementHandle = sourceLocation.getElementHandle();
//        if (elementHandle == null) {
            return new Location(oppoFiles.get(0)/*, null*/);
//        }
        
//        /* Build SOURCE classpath: */
//        ClassPath[] srcCpDelegates = new ClassPath[2];
//        if (sourceToTest) {
//            srcCpDelegates[0] = fileObjCp;
//            srcCpDelegates[1] = oppoRootsClassPath;
//        } else {
//            srcCpDelegates[0] = oppoRootsClassPath;
//            srcCpDelegates[1] = fileObjCp;
//        }
//        ClassPath srcClassPath
//                = ClassPathSupport.createProxyClassPath(srcCpDelegates);
//        
//        /* Build COMPILE classpath: */
//        FileObject[] oppoRoots = oppoRootsClassPath.getRoots();
//        ClassPath[] compCpDelegates = new ClassPath[oppoRoots.length + 1];
//        int delegateIndex = 0;
//        if (sourceToTest) {
//            compCpDelegates[delegateIndex++]
//                    = ClassPath.getClassPath(fileObjRoot, COMPILE);
//        }
//        for (FileObject oppoRoot : oppoRoots) {
//            compCpDelegates[delegateIndex++]
//                    = ClassPath.getClassPath(oppoRoot, COMPILE);
//        }
//        if (!sourceToTest) {
//            compCpDelegates[delegateIndex++]
//                    = ClassPath.getClassPath(fileObjRoot, COMPILE);
//        }
//        ClassPath compClassPath
//                = ClassPathSupport.createProxyClassPath(compCpDelegates);
//        
//        /* Obtain the BOOT classpath: */
//        ClassPath bootClassPath = ClassPath.getClassPath(fileObj, BOOT);
//        
//        ClasspathInfo cpInfo = ClasspathInfo.create(bootClassPath,
//                                                    compClassPath,
//                                                    srcClassPath);
//        List<FileObject> files = new ArrayList<FileObject>(oppoFiles.size() + 1);
//        files.add(fileObj);
//        files.addAll(oppoFiles);
//        JavaSource javaSource = JavaSource.create(cpInfo, files);
//        
//        try {
//            MatchFinder matchFinder = new MatchFinder(sourceLocation,
//                                                      oppoFiles,
//                                                      sourceToTest);
//            javaSource.runUserActionTask(matchFinder, true);
//            return matchFinder.getResult();
//        } catch (IOException ex) {
//            Logger.getLogger("global").log(Level.SEVERE, null, ex);     //NOI18N
//            return null;
//        }
    }
    
//    /**
//     *
//     */
//    private static final class MatchFinder
//                            implements CancellableTask<CompilationController> {
//        private final FileObject currFile;
//        private final ElementHandle currElemHandle;
//        private final List<FileObject> oppoFiles;
//        private final boolean sourceToTest;
//        
//        private String currFilePkgPrefix;
//        private Element currElement;
//        
//        private volatile boolean cancelled;
//        
//        private String[] oppoClassNames;
//        private String oppoMethodName;
//        private int bestCandidateClassNamesCount;
//        private FileObject bestCandidateFile;
//        private Element bestCandidateElement;
//        
//        /** */
//        private FileObject oppoFile = null;
//        /** storage for the result */
//        private Element oppoElement = null;
//        
//        /**
//         *
//         */
//        private MatchFinder(Location currLocation,
//                            List<FileObject> oppoFiles,
//                            boolean sourceToTest) {
//            this.currFile = currLocation.getFileObject();
//            this.currElemHandle = currLocation.getElementHandle();
//            this.oppoFiles = oppoFiles;
//            this.sourceToTest = sourceToTest;
//        }
//        
//        /**
//         * This method is run once for the file referred by
//         * {@link #currLocation} and then once for each file contained
//         * in {@link #oppoFiles}.
//         *
//         * @param  controller  controller for the current run of this method
//         */
//        public void run(CompilationController controller) throws IOException {
//            if (oppoFile != null) {
//                /* We already have the result. */
//                
//                /*
//                 * This should be only possible if there are multiple oppoFiles.
//                 */
//                assert oppoFiles.size() > 1;
//                return;
//            }
//            
//            final FileObject runFile = controller.getFileObject();
//            if (runFile == currFile) {
//                resolveCurrentElement(controller);   //--> currElement
//                return;
//            }
//            
//            if (currElement == null) {
//                /*
//                 * The element for 'currLocation' was not resolved during
//                 * the first run of this method on this instance.
//                 */
//                return;
//            }
//            if ((oppoClassNames == null) || (oppoClassNames.length == 0)) {
//                return;
//            }
//
//            controller.toPhase(Phase.PARSED);
//            
//            final Elements elements = controller.getElements();
//            TypeElement topClass = elements.getTypeElement(getCanonicalClassName(oppoClassNames[0]));
//            if ((topClass != null)
//                    && !CLASS_LIKE_ELEM_TYPES.contains(topClass.getKind())) {
//                topClass = null;
//            }
//            if (cancelled || (topClass == null)) {
//                return;
//            }
//            
//            int classNamesCount = 0;
//            TypeElement bestClass = null;
//            TypeElement theSubClass = topClass;
//            while ((theSubClass != null) && (++classNamesCount < oppoClassNames.length)) {
//                bestClass = theSubClass;
//                
//                String oppoClassName = oppoClassNames[classNamesCount];
//                if (oppoClassName == null) {
//                    break;
//                }
//                
//                theSubClass = null;
//                for (TypeElement subClass : typesIn(bestClass.getEnclosedElements())) {
//                    if (cancelled) {
//                        return;
//                    }
//                    
//                    if (CLASS_LIKE_ELEM_TYPES.contains(subClass.getKind())
//                            && subClass.getSimpleName().toString().equals(oppoClassName)) {
//                        theSubClass = subClass;
//                        break;
//                    }
//                }
//            }
//            if (cancelled) {
//                return;
//            }
//            if (classNamesCount == oppoClassNames.length) {
//                bestClass = theSubClass;  //this does not get called in the above while (...) cycle
//                
//                if (oppoMethodName == null) {
//                    oppoFile = runFile;
//                    oppoElement = bestClass;
//                } else {
//                    ExecutableElement testMethod = findOppoMethod(bestClass);
//                    if (testMethod != null) {
//                        /* We found the test method! */
//                        oppoFile = runFile;
//                        oppoElement = testMethod;
//                    }
//                }
//                if (oppoFile != null) {
//                    return;
//                }
//            }
//            
//            if (classNamesCount > bestCandidateClassNamesCount) {
//                bestCandidateFile = runFile;
//                bestCandidateElement = bestClass;
//                bestCandidateClassNamesCount = classNamesCount;
//            }
//        }
//        
//        /**
//         */
//        private ExecutableElement findOppoMethod(TypeElement classElem) {
//            for (ExecutableElement elem : methodsIn(classElem.getEnclosedElements())) {
//                if (elem.getSimpleName().toString().equals(oppoMethodName)) {
//                    if (!sourceToTest) {
//                        return elem;
//                    }
//                    if (elem.getParameters().isEmpty()) {
//                        Set<Modifier> modifiers = elem.getModifiers();
//                        if (modifiers.contains(Modifier.PUBLIC)
//                                && !modifiers.contains(Modifier.STATIC)) {
//                            return elem;
//                        }
//                    }
//                    break;
//                }
//            }
//            return null;
//        }
//
//        public void cancel() {
//            cancelled = true;
//        }
//        
//        /**
//         */
//        private Location getResult() {
//            assert (oppoFile == null) == (oppoElement == null);
//            
//            return (oppoFile != null)
//                   ? new Location(oppoFile, oppoElement)
//                   : new Location(bestCandidateFile, bestCandidateElement);
//        }
//        
//        /**
//         * Resolves 'currElementHandle' and stores the result to 'currElement'.
//         */
//        private void resolveCurrentElement(CompilationController controller)
//                                                            throws IOException {
//            String canonicalFileName
//                   = controller.getClasspathInfo().getClassPath(PathKind.SOURCE)
//                     .getResourceName(currFile, '.', false);
//            int lastDotIndex = canonicalFileName.lastIndexOf('.');
//            currFilePkgPrefix = (lastDotIndex != -1)
//                                ? canonicalFileName.substring(0, lastDotIndex + 1)
//                                : null;
//            
//            controller.toPhase(Phase.PARSED);
//            if (cancelled) {
//                return;
//            }
//            currElement = currElemHandle.resolve(controller);
//            if (currElement == null) {
//                Logger.getLogger(getClass().getName()).log(
//                        Level.INFO,
//                        "Could not resolve element " + currElemHandle); //NOI18N
//                return;
//            }
//            
//            if (cancelled) {
//                return;
//            }
//            
//            Element clsElement;
//            ElementKind currElemKind = currElement.getKind();
//            if (CLASS_LIKE_ELEM_TYPES.contains(currElement.getKind())) {
//                clsElement = currElement;
//                oppoMethodName = null;
//            } else {
//                clsElement = currElement.getEnclosingElement();
//                oppoMethodName = (currElemKind == ElementKind.METHOD)
//                     ? getOppoMethodName(currElement.getSimpleName().toString())
//                     : null;    //no rule for finding tests for initializers
//            }
//            assert CLASS_LIKE_ELEM_TYPES.contains(clsElement.getKind());
//            
//            if (cancelled) {
//                return;
//            }
//            
//            oppoClassNames = buildOppoClassNames(clsElement);
//            if (oppoClassNames == null) {
//                oppoMethodName = null;
//            } else {
//                for (int i = 0; i < oppoClassNames.length; i++) {
//                    if (oppoClassNames[i] == null) {
//                        if (i == 0) {
//                            oppoClassNames = null;
//                        } else {
//                            String[] newArray = new String[i];
//                            System.arraycopy(oppoClassNames, 0, newArray, 0, i);
//                            oppoClassNames = newArray;
//                        }
//                        oppoMethodName = null;
//                        break;
//                    }
//                }
//
//            }
//        }
//        
//        /**
//         * 
//         * @return  may return {@code null} if this task has been cancelled
//         */
//        private String[] buildOppoClassNames(Element clsElement) {
//            String[] oppoClsNames;
//            String oppoClsName;
//            
//            Element clsParent = clsElement.getEnclosingElement();
//            if ((clsParent == null)
//                    || !CLASS_LIKE_ELEM_TYPES.contains(clsParent.getKind())) {
//                oppoClsName = getOppoClassName(clsElement.getSimpleName().toString());
//                oppoClsNames = (oppoClsName != null)
//                               ? new String[] {oppoClsName}
//                               : null;
//            } else {
//                List<String> clsNames = new ArrayList<String>();
//                clsNames.add(clsElement.getSimpleName().toString());
//                do {
//                    if (cancelled) {
//                        return null;
//                    }
//                    
//                    clsNames.add(clsParent.getSimpleName().toString());
//                    clsParent = clsParent.getEnclosingElement();
//                } while ((clsParent != null)
//                        && CLASS_LIKE_ELEM_TYPES.contains(clsParent.getKind()));
//                
//                if (cancelled) {
//                    return null;
//                }
//                
//                final int size = clsNames.size();
//                oppoClsNames = new String[size];
//                for (int i = 0; i < size; i++) {
//                    oppoClsName = getOppoClassName(clsNames.get(size - i - 1));
//                    if (oppoClsName == null) {
//                        break;
//                    }
//                    oppoClsNames[i] = oppoClsName;
//                }
//            }
//            return oppoClsNames;
//        }
//        
//        /**
//         */
//        private String getCanonicalClassName(String shortClassName) {
//            return (currFilePkgPrefix != null)
//                   ? currFilePkgPrefix + shortClassName
//                   : shortClassName;
//        }
//        
//        /**
//         */
//        private String getOppoClassName(String name) {
//            return sourceToTest ? getTestClassName(name)
//                                : getSourceClassName(name);
//        }
//        
//        /**
//         */
//        private String getOppoMethodName(String name) {
//            return sourceToTest ? getTestMethodName(name)
//                                : getSourceMethodName(name);
//        }
//        
//    }
    
    /**
     */
    private static String getTestResName(String baseResName, String ext) {
        StringBuilder buf
                = new StringBuilder(baseResName.length() + ext.length() + 10);
        buf.append(baseResName).append("Test");                         //NOI18N
        if (ext.length() != 0) {
            buf.append('.').append(ext);
        }
        return buf.toString();
    }
    
    /**
     */
    private static String getSuiteResName(String baseResName) {
        if (baseResName.length() == 0) {
            return JUnitSettings.getDefault().getRootSuiteClassName();
        }
        
        final String suiteSuffix = "Suite";                             //NOI18N

        String lastNamePart
                = baseResName.substring(baseResName.lastIndexOf('/') + 1);

        StringBuilder buf = new StringBuilder(baseResName.length()
                                              + lastNamePart.length()
                                              + suiteSuffix.length()
                                              + 6);
        buf.append(baseResName).append('/');
        buf.append(Character.toUpperCase(lastNamePart.charAt(0)))
           .append(lastNamePart.substring(1));
        buf.append(suiteSuffix);
        buf.append(".java");                                            //NOI18N

        return buf.toString();
    }
    
    /**
     */
    private static String getSrcResName(String testResName, String ext) {
        if (!testResName.endsWith("Test")) {                            //NOI18N
            return null;
        }
        
        StringBuilder buf
                = new StringBuilder(testResName.length() + ext.length());
        buf.append(testResName.substring(0, testResName.length() - 4));
        if (ext.length() != 0) {
            buf.append('.').append(ext);
        }
        return buf.toString();
    }
    
    /**
     */
    private static String getTestClassName(String baseClassName) {
        return baseClassName + "Test";                                  //NOI18N
    }
    
    /**
     */
    private static String getSourceClassName(String testClassName) {
        final String suffix = "Test";                                   //NOI18N
        final int suffixLen = suffix.length();
        
        return ((testClassName.length() > suffixLen)
                    && testClassName.endsWith(suffix))
               ? testClassName.substring(0, testClassName.length() - suffixLen)
               : null;
    }
    
    /**
     */
    private static String getTestMethodName(String baseMethodName) {
        final String prefix = "test";                                   //NOI18N
        final int prefixLen = prefix.length();
        
        StringBuffer buf = new StringBuffer(prefixLen
                                            + baseMethodName.length());
        buf.append(prefix).append(baseMethodName);
        buf.setCharAt(prefixLen,
                      Character.toUpperCase(baseMethodName.charAt(0)));
        return buf.toString();
    }
    
    /**
     */
    private static String getSourceMethodName(String testMethodName) {
        final String prefix = "test";                                   //NOI18N
        final int prefixLen = prefix.length();
        
        return ((testMethodName.length() > prefixLen)
                    && testMethodName.startsWith(prefix))
               ? new StringBuilder(testMethodName.length() - prefixLen)
                        .append(Character.toLowerCase(testMethodName.charAt(prefixLen)))
                        .append(testMethodName.substring(prefixLen + 1))
                        .toString()
               : null;
    }
    
    /**
     * Creates test classes for given source classes.
     * 
     * @param filesToTest  source files for which test classes should be
     *                      created
     * @param targetRoot   root folder of the target source root
     * @param params  parameters of creating test class
     * @return created test files
     */
    @Override
    protected FileObject[] createTests(
                                final FileObject[] filesToTest,
                                final FileObject targetRoot,
                                final Map<CreateTestParam, Object> params) {
        //XXX: not documented that in case that if filesToTest is <null>,
        //the target root param works as a target folder
        
        ProgressIndicator progress = new ProgressIndicator();
        progress.show();

        String msg = NbBundle.getMessage(
                    CreateTestAction.class,
                    "MSG_StatusBar_CreateTest_Begin");                  //NOI18N
        progress.displayStatusText(msg);

        final TestCreator testCreator = new TestCreator(params, junitVer);
        
        CreationResults results;
        try {
            final String templateId;
            final String suiteTemplateId;
            boolean forTestSuite
                    = (filesToTest != null)
                      && (filesToTest.length != 0)
                      && ((filesToTest.length > 1) || !filesToTest[0].isData());
            final boolean useAnnotations;
            switch (junitVer) {
                case JUNIT3:
                    templateId = "PROP_junit3_testClassTemplate";       //NOI18N
                    suiteTemplateId = forTestSuite
                                      ? "PROP_junit3_testSuiteTemplate" //NOI18N
                                      : null;
                    useAnnotations = TestUtil.areAnnotationsSupported(targetRoot);
                    break;
                case JUNIT4:
                    templateId = "PROP_junit4_testClassTemplate";       //NOI18N
                    suiteTemplateId = forTestSuite
                                      ? "PROP_junit4_testSuiteTemplate" //NOI18N
                                      : null;
                    useAnnotations = true;
                    break;
                default:
                    assert false;
                    templateId = null;
                    suiteTemplateId = null;
                    useAnnotations = false;
                    break;
            }
            DataObject doTestTempl = (templateId != null)
                                     ? loadTestTemplate(templateId)
                                     : null;
            if (doTestTempl == null) {
                return null;
            }
            DataObject doSuiteTempl = (suiteTemplateId != null)
                                      ? loadTestTemplate(suiteTemplateId)
                                      : null;
            if (forTestSuite && (doSuiteTempl == null)) {
                return null;
            }
            
            Map<String, Boolean> templateParams = createTemplateParams(params);
            if (useAnnotations) {
                templateParams.put(templatePropUseAnnotations, useAnnotations);
            }

            if ((filesToTest == null) || (filesToTest.length == 0)) {
                //XXX: Not documented that filesToTest may be <null>
                
                addTemplateParamEntry(params, CreateTestParam.INC_CODE_HINT,
                                      templateParams, templatePropMethodPH);

                String testClassName = (String) params.get(CreateTestParam.CLASS_NAME);
                assert testClassName != null;
                results = new CreationResults(1);
                DataObject testDataObj = createEmptyTest(targetRoot,
                                                         testClassName,
                                                         testCreator,
                                                         templateParams,
                                                         doTestTempl);
                if (testDataObj != null) {
                    results.addCreated(testDataObj);
                }
                
            } else {
                ClassPath testClassPath = ClassPathSupport.createClassPath(
                                                new FileObject[] {targetRoot});
                if (!forTestSuite) {
                    String testClassName = (String) params.get(CreateTestParam.CLASS_NAME);
                    if (testClassName == null) {
                        String srcClassName
                                = ClassPath.getClassPath(filesToTest[0], SOURCE)
                                  .getResourceName(filesToTest[0], '.', false);
                        testClassName = getTestClassName(srcClassName);
                    }
                    try {
                        results = createSingleTest(
                                filesToTest[0],
                                testClassName,
                                testCreator,
                                templateParams,
                                doTestTempl,
                                testClassPath,
                                false,             //do not skip any classes
                                null,              //parent suite
                                progress);
                    } catch (CreationError ex) {
                        ErrorManager.getDefault().notify(ex);
                        results = new CreationResults(1);
                    }
                } else {
                    results = new CreationResults();

                    // go through all nodes
                    for (FileObject fileToTest : filesToTest) {
                        try {
                            results.combine(createTests(fileToTest,
                                                        testCreator,
                                                        templateParams,
                                                        doTestTempl,
                                                        doSuiteTempl,
                                                        testClassPath,
                                                        null,
                                                        progress));
                        } catch (CreationError e) {
                            ErrorManager.getDefault().notify(e);
                        }
                    }
                }
            }
        } finally {
            progress.hide();
        }

        final Set<SkippedClass> skipped = results.getSkipped();
        final Set<DataObject> created = results.getCreated();
        if (!skipped.isEmpty()) {
            // something was skipped
            String message;
            if (skipped.size() == 1) {
                // one class? report it
                SkippedClass skippedClass = skipped.iterator().next();

                message = NbBundle.getMessage(
                        DefaultPlugin.class,
                        "MSG_skipped_class",                            //NOI18N
                        skippedClass.clsName,
                        strReason(skippedClass.reason, "COMMA", "AND"));//NOI18N
            } else {
                // more classes, report a general error
                // combine the results
                TestabilityResult reason = TestabilityResult.OK;
                for (SkippedClass sc : skipped) {
                    reason = TestabilityResult.combine(reason, sc.reason);
                }

                message = NbBundle.getMessage(
                        DefaultPlugin.class,
                        "MSG_skipped_classes",                          //NOI18N
                        strReason(reason, "COMMA", "OR"));              //NOI18N
            }
            TestUtil.notifyUser(message, NotifyDescriptor.INFORMATION_MESSAGE);

        }
        
        if (created.isEmpty()) {
            Mutex.EVENT.writeAccess(new Runnable() {
                public void run() {
                    TestUtil.notifyUser(
                            NbBundle.getMessage(
                                    DefaultPlugin.class,
                                    "MSG_No_test_created"),     //NOI18N
                            NotifyDescriptor.INFORMATION_MESSAGE);
                }
            });
        }
        
        FileObject[] createdFiles;
        if (created.isEmpty()) {
            createdFiles = null;
        } else {
            createdFiles = new FileObject[created.size()];
            int i = 0;
            for (DataObject dObj : created) {
                createdFiles[i++] = dObj.getPrimaryFile();
            }
        }
        return createdFiles;
    }

    /**
     * Create a map of FreeMaker template parameters from a map
     * of {@code CreateTestParam}s.
     */
    public static final Map<String, Boolean> createTemplateParams(
                                          Map<CreateTestParam, Object> params) {
        Map<String,Boolean> result = new HashMap<String,Boolean>(7);

        addTemplateParamEntry(params, CreateTestParam.INC_CLASS_SETUP,
                              result, templatePropBeforeClass);
        addTemplateParamEntry(params, CreateTestParam.INC_CLASS_TEAR_DOWN,
                              result, templatePropAfterClass);
        addTemplateParamEntry(params, CreateTestParam.INC_SETUP,
                              result, templatePropBefore);
        addTemplateParamEntry(params, CreateTestParam.INC_TEAR_DOWN,
                              result, templatePropAfter);
        addTemplateParamEntry(params, CreateTestParam.INC_CODE_HINT,
                              result, templatePropCodeHints);

        return result;
    }

    private static void addTemplateParamEntry(Map<CreateTestParam, Object> srcParams,
                                              CreateTestParam srcParamKey,
                                              Map<String, Boolean> templParams,
                                              String templParamKey) {
        Object value = srcParams.get(srcParamKey);
        if (value instanceof Boolean) {
            templParams.put(templParamKey, Boolean.class.cast(value));
        }
    }

    /**
     */
    public boolean setupJUnitVersionByProject(FileObject targetFolder) {
        return createTestActionCalled(new FileObject[] {targetFolder});
    }
    
    /**
     */
    @Override
    protected boolean createTestActionCalled(FileObject[] selectedFiles) {
        assert EventQueue.isDispatchThread();

        LOG_JUNIT_VER.finer("createTestActionCalled(...)");             //NOI18N

        Project project = FileOwnerQuery.getOwner(selectedFiles[0]);
        assert project != null;         //PENDING

        boolean storeSettings;
        try {
            storeSettings = readProjectSettingsJUnitVer(project);
            if (!storeSettings) {
                LOG_JUNIT_VER.finest(
                " - will not be able to store JUnit version settings"); //NOI18N
            }
        } catch (IllegalStateException ex) {
            String projectName = ProjectUtils.getInformation(project)
                                 .getDisplayName();
            DialogDisplayer.getDefault().notify(
                    new NotifyDescriptor.Message(
                            NbBundle.getMessage(
                                       getClass(),
                                       "MSG_NoTestFolderFoundInProject",//NOI18N
                                       projectName),
                            NotifyDescriptor.WARNING_MESSAGE));
            return false;
        }

        if (junitVer != null) {
            switch (junitVer) {
                case JUNIT3:
                    return true;
                case JUNIT4:
                    String sourceLevel = TestUtil.getSourceLevel(selectedFiles[0]);
                    if (sourceLevel == null) {    //could not get source level
                        return true;
                    }

                    if (sourceLevel.compareTo("1.5") >= 0) {            //NOI18N
                        return true;
                    } else if (askUserLastWasJUnit4NowSource14(sourceLevel)) {
                        junitVer = JUnitVersion.JUNIT3;
                        if (storeSettings) {
                            storeProjectSettingsJUnitVer(project);
                        }
                        return true;
                    }
                    return false;
                default:
                    assert false;
                    return false;
            }
        }

        readSystemSettingsJUnitVer();
        if (junitVer != null) {
            switch (junitVer) {
                case JUNIT3:
                    if (storeSettings) {
                        storeProjectSettingsJUnitVer(project);
                    }
                    return true;
                case JUNIT4:
                    String sourceLevel = TestUtil.getSourceLevel(selectedFiles[0]);
                    if ((sourceLevel != null)
                            && (sourceLevel.compareTo("1.5")) >= 0) {   //NOI18N
                        if (storeSettings) {
                            storeProjectSettingsJUnitVer(project);
                        }
                        return true;
                    } else if (sourceLevel == null) {
                        String msgKey
                            = "MSG_select_junit_version_srclvl_unknown";//NOI18N
                        junitVer = askUserWhichJUnitToUse(msgKey, true, true);
                        if ((junitVer != null) && storeSettings) {
                            storeProjectSettingsJUnitVer(project);
                        }
                        return (junitVer != null);
                    } else if (informUserOnlyJUnit3Applicable(sourceLevel)) {
                        junitVer = JUnitVersion.JUNIT3;
                        if (storeSettings) {
                            storeProjectSettingsJUnitVer(project);
                        }
                        return true;
                    }
                    return false;
                default:
                    assert false;
                    return false;
            }
        }

        String msgKey;
        boolean offerJUnit4;
        boolean showSourceLevelReqs;
        String sourceLevel = TestUtil.getSourceLevel(selectedFiles[0]);
        if (sourceLevel == null) {
            msgKey = "MSG_select_junit_version_srclvl_unknown";         //NOI18N
            offerJUnit4 = true;
            showSourceLevelReqs = true;
        } else {
            msgKey = "MSG_select_junit_version";                        //NOI18N
            offerJUnit4 = (sourceLevel.compareTo("1.5") >= 0);          //NOI18N
            showSourceLevelReqs = !offerJUnit4;
        }
        junitVer = askUserWhichJUnitToUse(msgKey,
                                          offerJUnit4,
                                          showSourceLevelReqs);
        if ((junitVer != null) && storeSettings) {
            storeProjectSettingsJUnitVer(project);
        }
        return (junitVer != null);
    }
    
    /**
     */
    private boolean askUserLastWasJUnit4NowSource14(String sourceLevel) {
        assert EventQueue.isDispatchThread();

        JComponent msg
               = createMessageComponent("MSG_last_was_junit4_what_now", //NOI18N
                                        sourceLevel);
        Object selectOption = NbBundle.getMessage(
                                    getClass(),
                                    "LBL_create_junit3_tests");         //NOI18N
        Object answer = DialogDisplayer.getDefault().notify(
                new DialogDescriptor(
                        wrapDialogContent(msg),
                        NbBundle.getMessage(
                                getClass(),
                                "LBL_title_cannot_use_junit4"),         //NOI18N
                        true,
                        new Object[] {selectOption, CANCEL_OPTION},
                        selectOption,
                        DialogDescriptor.DEFAULT_ALIGN,
                        (HelpCtx) null,
                        (ActionListener) null));

        return answer == selectOption;
    }

    /**
     */
    private boolean informUserOnlyJUnit3Applicable(String sourceLevel) {
        assert EventQueue.isDispatchThread();

        JComponent msg
              = createMessageComponent("MSG_cannot_use_default_junit4", //NOI18N
                                       sourceLevel);
//        Object selectOption = NbBundle.getMessage(
//                                    getClass(),
//                                    "LBL_create_junit3_tests");         //NOI18N
        JButton button = new JButton(); 
        Mnemonics.setLocalizedText(button, bundle.getString("LBL_Select"));
        button.getAccessibleContext().setAccessibleName("AN_create_junit3_tests");
        button.getAccessibleContext().setAccessibleDescription("AD_create_junit3_tests");
        
        Object answer = DialogDisplayer.getDefault().notify(
                new DialogDescriptor(
                        wrapDialogContent(msg),
                        NbBundle.getMessage(
                                getClass(),
                                "LBL_title_cannot_use_junit4"),         //NOI18N
                        true,       //modal
                        new Object[] {button, CANCEL_OPTION},
                        button,
                        DialogDescriptor.DEFAULT_ALIGN,
                        (HelpCtx) null,
                        (ActionListener) null));

        return answer == button;
    }

//    private String getText(String bundleKey) {
//        return NbBundle.getMessage(getClass(), bundleKey);
//    }
    
    /**
     */
    private JUnitVersion askUserWhichJUnitToUse(String msgKey,
                                                boolean offerJUnit4,
                                                boolean showSourceLevelCondition) {
        assert EventQueue.isDispatchThread();

        JRadioButton rbtnJUnit3 = new JRadioButton();
        Mnemonics.setLocalizedText(rbtnJUnit3, bundle.getString("LBL_JUnit3_generator"));
        rbtnJUnit3.getAccessibleContext().setAccessibleDescription(bundle.getString("AD_JUnit3_generator"));
        
        JRadioButton rbtnJUnit4 = new JRadioButton();
        Mnemonics.setLocalizedText(
                rbtnJUnit4,
                showSourceLevelCondition
                       ? bundle.getString("LBL_JUnit4_generator_reqs")  //NOI18N
                       : bundle.getString("LBL_JUnit4_generator"));     //NOI18N
        rbtnJUnit4.getAccessibleContext().setAccessibleDescription(bundle.getString("AD_JUnit4_generator"));

        ButtonGroup group = new ButtonGroup();
        group.add(rbtnJUnit3);
        group.add(rbtnJUnit4);

        if (offerJUnit4) {
            rbtnJUnit4.setSelected(true);
        } else {
            rbtnJUnit3.setSelected(true);
            rbtnJUnit4.setEnabled(false);
        }

        JComponent msg
                = createMessageComponent(msgKey);
        
        JPanel choicePanel = new JPanel(new GridLayout(0, 1, 0, 3));
        choicePanel.add(rbtnJUnit3);
        choicePanel.add(rbtnJUnit4);

        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.add(msg, BorderLayout.NORTH);
        panel.add(choicePanel, BorderLayout.CENTER);

        JButton button = new JButton(); 
        Mnemonics.setLocalizedText(button, bundle.getString("LBL_Select"));
        button.getAccessibleContext().setAccessibleName("AN_Select");
        button.getAccessibleContext().setAccessibleDescription("AD_Select");
        
//        Object selectOption = bundle.getString("LBL_Select");        //NOI18N
        Object answer = DialogDisplayer.getDefault().notify(
                new DialogDescriptor(
                        wrapDialogContent(panel),
                        bundle.getString("LBL_title_select_generator"),//NOI18N
                        true,
                        new Object[] {button, CANCEL_OPTION},
                        button,
                        DialogDescriptor.DEFAULT_ALIGN,
                        (HelpCtx) null,
                        (ActionListener) null));

        if (answer == button) {
            JUnitVersion ver;
            if (rbtnJUnit3.isSelected()) {
                ver = JUnitVersion.JUNIT3;
            } else if (rbtnJUnit4.isSelected()) {
                ver = JUnitVersion.JUNIT4;
            } else {
                assert false;
                ver = null;
            }
            return ver;
        } else {
            return null;
        }
    }

    /**
     */
    private JComponent createMessageComponent(String msgKey,
                                              String... args) {
        String message = NbBundle.getMessage(getClass(), msgKey, args);
        
        return GuiUtils.createMultilineLabel(message);
    }
    
    /**
     */
    private static JComponent wrapDialogContent(JComponent comp) {
        return wrapDialogContent(comp, true);
    }
    
    /**
     */
    private static JComponent wrapDialogContent(JComponent comp,
                                                boolean selfResizing) {
        JComponent result;
        
        if ((comp.getBorder() != null) || selfResizing) {
            result = selfResizing ? new SelfResizingPanel() : new JPanel();
            result.setLayout(new GridLayout());
            result.add(comp);
        } else {
            result = comp;
        }
        result.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        result.getAccessibleContext().setAccessibleDescription(bundle.getString("AD_title_select_generator"));
        return result;
    }

    /**
     * Gets JUnit version info from the project and stores it
     * into field {@link #junitVer}.
     * If the &quot;junit version&quot; info is not available,
     * {@code null} is stored.
     *
     * @param  project  project from which the information is to be obtained
     * @return  {@code true} of the set of project's libraries could be
     *          determined, {@code false} if it could not be determined
     * @exception  java.lang.IllegalStateException
     *             if the project does not contain any test folders
     * @see  #junitVer
     */
    private boolean readProjectSettingsJUnitVer(Project project)
                                                throws IllegalStateException {
        assert project != null;
        if (LOG_JUNIT_VER.isLoggable(FINER)) {
            LOG_JUNIT_VER.finer("readProjectSettingsJUnitVer("          //NOI18N
                                + ProjectUtils.getInformation(project).getDisplayName()
                                + ')');
        }

        junitVer = null;

        final boolean hasJUnit3;
        final boolean hasJUnit4;
        final ClassPath classPath = getTestClassPath(project); //may throw ISE
        if (classPath != null) {
            hasJUnit3 = (classPath.findResource(JUNIT3_SPECIFIC) != null);
            hasJUnit4 = (classPath.findResource(JUNIT4_SPECIFIC) != null);
        } else {
            hasJUnit3 = false;
            hasJUnit4 = false;
        }

        if (hasJUnit3 != hasJUnit4) {
            junitVer = hasJUnit3 ? JUnitVersion.JUNIT3
                                 : JUnitVersion.JUNIT4;
            if (LOG_JUNIT_VER.isLoggable(FINEST)) {
                LOG_JUNIT_VER.finest(" - detected version " + junitVer);//NOI18N
            }
        } else {
            LOG_JUNIT_VER.finest(" - no version detected");             //NOI18N
        }
        return (classPath != null);
    }

    /**
     * Finds classpath used for compilation of tests.
     * 
     * @param  project  project whose classpath should be found
     * @return  test classpath of the given project, or {@code null} if it could
     *          not be determined
     * @exception  java.lang.IllegalStateException
     *             if no test folders were found in the project
     */
    private static ClassPath getTestClassPath(final Project project)
                                                  throws IllegalStateException {
        assert project != null;
        if (LOG_JUNIT_VER.isLoggable(FINER)) {
            LOG_JUNIT_VER.finer("getTestClassPath("                     //NOI18N
                                + ProjectUtils.getInformation(project).getDisplayName()
                                + ')');
        }

        final Collection<FileObject> testFolders = Utils.getTestFolders(project);
        if (testFolders.isEmpty()) {
            LOG_JUNIT_VER.finest(" - no test folders found");           //NOI18N
            throw new IllegalStateException();
        }

        final ClassPathProvider cpProvider
                = project.getLookup().lookup(ClassPathProvider.class);
        if (cpProvider == null) {
            LOG_JUNIT_VER.finest(" - ClassPathProvider not found");     //NOI18N
            return null;
        }

        for (FileObject testRoot : testFolders) {
            ClassPath testClassPath = cpProvider.findClassPath(testRoot,
                                                               COMPILE);
            if (testClassPath != null) {
                if (LOG_JUNIT_VER.isLoggable(FINEST)) {
                    LOG_JUNIT_VER.finest(" - returning: "               //NOI18N
                                         + testClassPath);
                }
                return testClassPath;
            }
        }

        LOG_JUNIT_VER.finest(" - no compile classpath for tests found");//NOI18N
        return null;
    }

    /**
     * Stores JUnit version to the project's configuration file.
     *
     * @param  project  project whose configuration file is to be checked
     * @see  #junitVer
     */
    private void storeProjectSettingsJUnitVer(final Project project) {
        assert junitVer != null;

        if (LOG_JUNIT_VER.isLoggable(FINER)) {
            LOG_JUNIT_VER.finer("storeProjectSettignsJUnitVer("         //NOI18N
                                + ProjectUtils.getInformation(project).getDisplayName()
                                + ')');
        }

        final boolean hasJUnit3;
        final boolean hasJUnit4;
        final ClassPath classPath = getTestClassPath(project);
        if (classPath != null) {
            hasJUnit3 = (classPath.findResource(JUNIT3_SPECIFIC) != null);
            hasJUnit4 = (classPath.findResource(JUNIT4_SPECIFIC) != null);
        } else {
            hasJUnit3 = false;
            hasJUnit4 = false;
        }

        final Pattern pattern = Pattern.compile(
                                "^junit(?:_|\\W)+([34])(?:\\b|_).*");   //NOI18N

        JUnitLibraryComparator libraryComparator = null;

        Library libraryToAdd = null;
        Collection<Library> librariesToRemove = null;

        LOG_JUNIT_VER.finest(" - checking libraries:");                 //NOI18N
        Library[] libraries = LibraryManager.getDefault().getLibraries();
        for (Library library : libraries) {
            String name = library.getName().toLowerCase();
            if (LOG_JUNIT_VER.isLoggable(FINEST)) {
                LOG_JUNIT_VER.finest("    " + name);
            }
            if (!name.startsWith("junit")) {                            //NOI18N
                LOG_JUNIT_VER.finest("     - not a JUnit library");     //NOI18N
                continue;
            }

            boolean add    = false;
            boolean remove = false;
            Matcher matcher;
            final String verNumToAdd;
            if ((junitVer == JUnitVersion.JUNIT3) && !hasJUnit3) {
                verNumToAdd = "3";                                      //NOI18N
            } else if ((junitVer == JUnitVersion.JUNIT4) && !hasJUnit4) {
                verNumToAdd = "4";                                      //NOI18N
            } else {
                verNumToAdd = null;
            }
            String verNumToRemove = (junitVer == JUnitVersion.JUNIT3)
                                    ? "4" : "3";                        //NOI18N
            if (name.equals("junit")) {                                 //NOI18N
                add    = (verNumToAdd    == "3");                       //NOI18N
                remove = (verNumToRemove == "3");                       //NOI18N
            } else if ((matcher = pattern.matcher(name)).matches()) {
                String verNum = matcher.group(1);
                add    = verNum.equals(verNumToAdd   );
                remove = verNum.equals(verNumToRemove);
            }
            if (add) {
                LOG_JUNIT_VER.finest("     - to be added");             //NOI18N
                if (libraryToAdd == null) {
                    libraryToAdd = library;
                } else {
                    /*
                     * If there are multiple conforming libraries, we only want
                     * to add one - the most recent one (i.e. having the highest
                     * version number).
                     */
                    LOG_JUNIT_VER.finest("        - will be compared:");//NOI18N
                    if (libraryComparator == null) {
                        libraryComparator = new JUnitLibraryComparator();
                    }
                    if (libraryComparator.compare(libraryToAdd, library) > 0) {
                        LOG_JUNIT_VER.finest("        - it won");       //NOI18N
                        libraryToAdd = library;
                    } else {
                        LOG_JUNIT_VER.finest("        - it lost");      //NOI18N
                    }
                }
            }
            if (remove) {
                LOG_JUNIT_VER.finest("     - to be removed");           //NOI18N
                if (librariesToRemove == null) {
                    librariesToRemove = new ArrayList<Library>(2);
                }
                librariesToRemove.add(library);
            }
        }
        if ((libraryToAdd == null) && (librariesToRemove == null)) {
            return;
        }

        final List<FileObject> projectArtifacts = getProjectTestArtifacts(project);
        if (projectArtifacts.isEmpty()) {
            displayMessage("MSG_cannot_set_junit_ver",                  //NOI18N
                           WARNING_MESSAGE);
            return;
        }

        final Library[] libsToAdd, libsToRemove;
        if (libraryToAdd != null) {
            libsToAdd = new Library[] {libraryToAdd};
        } else{
            libsToAdd = null;
        }
        if (librariesToRemove != null) {
            libsToRemove = librariesToRemove.toArray(
                                        new Library[librariesToRemove.size()]);
        } else {
            libsToRemove = null;
        }
        assert (libsToAdd != null) || (libsToRemove != null);

        class LibrarySetModifier implements Runnable {
            public void run() {
                boolean modified = false;
                try {
                    if (libsToAdd != null) {
                        for (FileObject prjArtifact : projectArtifacts) {
                            modified |= ProjectClassPathModifier.addLibraries(
                                               libsToAdd, prjArtifact, COMPILE);
                        }
                    }
                    if (libsToRemove != null) {
                        for (FileObject prjArtifact : projectArtifacts) {
                            modified |= ProjectClassPathModifier.removeLibraries(
                                               libsToRemove, prjArtifact, COMPILE);
                        }
                    }
                } catch (UnsupportedOperationException ex) {
                    String prjName = ProjectUtils.getInformation(project)
                                     .getDisplayName();
                    ErrorManager.getDefault().log(
                            WARNING,
                            "Project " + prjName                        //NOI18N
                            + ": Could not modify set of JUnit libraries"   //NOI18N
                            + " - operation not supported by the project.");//NOI18N
                } catch (IOException ex) {
                    ErrorManager.getDefault().notify(ERROR, ex);
                }
                if (modified) {
                    try {
                        ProjectManager.getDefault().saveProject(project);
                    } catch (IOException ex) {
                        ErrorManager.getDefault().notify(ERROR, ex);
                    }
                }
            }
        }
        ProjectManager.mutex().writeAccess(
                new LibrarySetModifier());
    }

    /**
     * Schedules displaying of a message to the event-dispatching thread.
     * 
     * @param  bundleKey  resource bundle key of the message
     * @param  msgType  type of the message
     *                  (e.g. {@code NotifyDescriptor.INFORMATION_MESSAGE})
     */
    private static void displayMessage(String bundleKey, int msgType) {
        DialogDisplayer.getDefault().notifyLater(
                new NotifyDescriptor.Message(
                        NbBundle.getMessage(DefaultPlugin.class, bundleKey),
                        msgType));
    }

    /**
     * Finds a project artifact used as an argument to method
     * {@code ProjectClassPathModifier.removeLibraries(...)
     * when modifying the set of JUnit libraries (used for tests).
     * 
     * @param  project  project for which the project artifact should be found
     * @return  list of test project artifacts, or {@code null} an empty list
     *          if no one could be determined
     */
    private static List<FileObject> getProjectTestArtifacts(final Project project) {
        assert project != null;

        final ClassPathProvider cpProvider
                = project.getLookup().lookup(ClassPathProvider.class);
        if (cpProvider == null) {
            Collections.<FileObject>emptyList();
        }

        final Collection<FileObject> testFolders = Utils.getTestFolders(project);
        if (testFolders.isEmpty()) {
            Collections.<FileObject>emptyList();
        }

        List<FileObject> result = null;
        for (FileObject testRoot : testFolders) {
            ClassPath testClassPath = cpProvider.findClassPath(testRoot,
                                                               ClassPath.COMPILE);
            if (testClassPath != null) {
                if (result == null) {
                    if (testFolders.size() == 1) {
                        return Collections.<FileObject>singletonList(testRoot);
                    } else {
                        result = new ArrayList<FileObject>(3);
                    }
                }
                result.add(testRoot);
            }
        }
        return (result != null)
               ? result
               : Collections.<FileObject>emptyList();
    }

    /**
     * Reads information about preferred JUnit version from the IDE settings
     * and stores is into field {@link #junitVer}.
     *
     * @see  #junitVer
     */
    private void readSystemSettingsJUnitVer() {
        String value = JUnitSettings.getDefault().getGenerator();
        if ((value == null) || value.equals(JUNIT_GENERATOR_ASK_USER)) {
            junitVer = null;
        } else {
            try {
                junitVer = Enum.valueOf(JUnitVersion.class, value.toUpperCase());
            } catch (IllegalArgumentException ex) {
                junitVer = null;
            }
        }
    }
    
    /**
     * Creates a new test class.
     * 
     * @param  targetRoot     <!-- //PENDING -->
     * @param  testClassName  <!-- //PENDING -->
     * @param  testCreator  {@code TestCreator} to be used for filling
     *                      the test class template
     * @param  templateDataObj  {@code DataObject} representing
     *                          the test file template
     * @return  the created test, or {@code null} if no test was created
     */
    private DataObject createEmptyTest(FileObject targetRoot,
                                       String testClassName,
                                       TestCreator testCreator,
                                       final Map<String, ? extends Object> templateParams,
                                       DataObject templateDataObj) {
        if (testClassName == null) {
            throw new IllegalArgumentException("testClassName = null"); //NOI18N
        }
        
        DataObject testDataObj = null;
        try {
            DataFolder targetFolderDataObj = DataFolder.findFolder(targetRoot);
            testDataObj = templateDataObj.createFromTemplate(
                                        targetFolderDataObj,
                                        testClassName,
                                        templateParams);

            /* fill in setup etc. according to dialog settings */
            testCreator.createEmptyTest(testDataObj.getPrimaryFile());
        } catch (IOException ex) {
            ErrorManager.getDefault().notify(ex);
        }
        return testDataObj;
    }
    
    /**
     *
     */
    private static CreationResults createSingleTest(
                FileObject sourceFile,
                String testClassName,
                final TestCreator testCreator,
                final Map<String, ? extends Object> templateParams,
                DataObject templateDataObj,
                ClassPath testClassPath,
                boolean skipNonTestable,
                List<String> parentSuite,
                ProgressIndicator progress) throws CreationError {
        
        List<SkippedClass> nonTestable;
        List<ElementHandle<TypeElement>> testable;
        try {
            JavaSource javaSource = JavaSource.forFileObject(sourceFile);
            if (skipNonTestable) {
                nonTestable = new ArrayList<SkippedClass>();
                testable = TopClassFinder.findTestableTopClasses(javaSource,
                                                                 testCreator,
                                                                 nonTestable);
            } else {
                nonTestable = Collections.<SkippedClass>emptyList();
                testable = TopClassFinder.findTopClasses(javaSource);
            }
        } catch (IOException ex) {
            throw new CreationError(ex);
        }
        
        CreationResults result = new CreationResults(4);
        if (!nonTestable.isEmpty()) {
            result.addSkipped(nonTestable);
        }
        if (!testable.isEmpty()) {
            String packageName = TestUtil.getPackageName(ClassPath.getClassPath(sourceFile, ClassPath.SOURCE).getResourceName(sourceFile, '.', false));

            /* used only if (testClassName != null): */
            boolean defClassProcessed = false;

            try {
                for (ElementHandle<TypeElement> clsToTest : testable) {
                    String testResourceName;
                    String srcClassNameShort
                            = TestUtil.getSimpleName(clsToTest.getQualifiedName());
                    if (testClassName == null) {
                        testResourceName = TestUtil.getTestClassFullName(
                                                srcClassNameShort, packageName);
                        testClassName = testResourceName.replace('/', '.');
                    } else if (!defClassProcessed
                               && srcClassNameShort.equals(sourceFile.getName())) {
                        testResourceName = testClassName.replace('.', '/');
                        defClassProcessed = true;
                    } else {
                        if (packageName == null) {
                            packageName = TestUtil.getPackageName(testClassName);
                        }
                        testResourceName = TestUtil.getTestClassFullName(
                                                srcClassNameShort, packageName);
                    }

                    /* find or create the test class DataObject: */
                    DataObject testDataObj = null;
                    FileObject testFile = testClassPath.findResource(
                                            testResourceName + ".java");//NOI18N
                    boolean isNew = (testFile == null);
                    if (testFile == null) {
                        testDataObj = createTestClass(testClassPath,
                                                      testResourceName,
                                                      templateDataObj,
                                                      templateParams);
                        testFile = testDataObj.getPrimaryFile();
                    }
                    
                    testCreator.createSimpleTest(clsToTest, testFile, isNew);
                    if (testDataObj == null) {
                        testDataObj = DataObject.find(testFile);
                    }
                    save(testDataObj);
                    
                    result.addCreated(testDataObj);
                    // add the test class to the parent's suite
                    if (parentSuite != null) {
                        parentSuite.add(testClassName);
                    }
                }
            } catch (IOException ex) {       //incl. DataObjectNotFoundException
                throw new CreationError(ex);
            }
        }
        
        return result;
    }
    
    /**
     *
     */
    private static CreationResults createTests(
                final FileObject srcFileObj,
                final TestCreator testCreator,
                final Map<String, ? extends Object> templateParams,
                DataObject doTestT,
                DataObject doSuiteT,
                final ClassPath testClassPath,
                List<String> parentSuite,
                ProgressIndicator progress) throws CreationError {

        CreationResults results;
        if (srcFileObj.isFolder()) {
            results = new CreationResults();

            List<String> mySuite = new LinkedList<String>();
            
            progress.setMessage(getScanningMsg(srcFileObj.getName()));

            for (FileObject childFileObj : srcFileObj.getChildren()) {
                if (progress.isCanceled()) {
                    results.setAbborted();
                    break;
                }
                results.combine(createTests(childFileObj,
                                            testCreator,
                                            templateParams,
                                            doTestT,
                                            doSuiteT,
                                            testClassPath,
                                            mySuite,
                                            progress));
                if (results.isAbborted()) {
                    break;
                }
            }

            // if everything went ok, and the option is enabled,
            // create a suite for the folder .
            if (!results.isAbborted()
                    && !mySuite.isEmpty()
                    && JUnitSettings.getDefault().isGenerateSuiteClasses()) {
                createSuiteTest(srcFileObj,
                                (String) null,
                                testCreator,
                                templateParams,
                                doSuiteT,
                                testClassPath,
                                mySuite,
                                parentSuite,
                                progress);
            }
        } else if (srcFileObj.isData() && TestUtil.isJavaFile(srcFileObj)) {
            results = createSingleTest(srcFileObj,
                                       (String) null, //use the default clsName
                                       testCreator,
                                       templateParams,
                                       doTestT,
                                       testClassPath,
                                       true,
                                       parentSuite,
                                       progress);
        } else {
            results = CreationResults.EMPTY;
        }
        return results;
    }

    /**
     *
     */
    private static DataObject createSuiteTest(
            FileObject folder,
            String suiteName,
            final TestCreator testCreator,
            final Map<String, ? extends Object> templateParams,
            DataObject templateDataObj,
            ClassPath testClassPath,
            List<String> classesToInclude,
            List<String> parentSuite,
            ProgressIndicator progress) throws CreationError {

        // find correct package name
        ClassPath cp = ClassPath.getClassPath(folder, SOURCE);
        assert cp != null : "SOURCE classpath was not found for " + folder; //NOI18N
        if (cp == null) {
            return null;
        }
        
        String pkg = cp.getResourceName(folder, '/', false);
        String dotPkg = pkg.replace('/', '.');
        String fullSuiteName = (suiteName != null)
                               ? pkg + '/' + suiteName
                               : TestUtil.convertPackage2SuiteName(pkg);

        String classNames = makeListOfClasses(classesToInclude, null);
        String classes = makeListOfClasses(classesToInclude, ".class"); //NOI18N

        final Map<String, Object> suiteTemplParams
                = new HashMap<String, Object>(templateParams);
        suiteTemplParams.put(templatePropClassNames, classNames);
        suiteTemplParams.put(templatePropClasses,    classes);

        try {
            /* find or create the test class DataObject: */
            DataObject testDataObj = null;
            FileObject testFile = testClassPath.findResource(
                                        fullSuiteName + ".java");       //NOI18N
            boolean isNew = (testFile == null);
            if (testFile == null) {
                testDataObj = createTestClass(testClassPath,
                                              fullSuiteName,
                                              templateDataObj,
                                              suiteTemplParams);
                testFile = testDataObj.getPrimaryFile();
            }
            
//            List<String> processedClasses;
//            JavaSource testSrc = JavaSource.forFileObject(testFile);
            try {
//                processedClasses = testCreator.createTestSuite(classesToInclude,
//                                                               testSrc,
//                                                               isNew);
                if (testDataObj == null) {
                    testDataObj = DataObject.find(testFile);
                }
                save(testDataObj);
            } catch (Exception ex) {
                ErrorManager.getDefault().notify(ErrorManager.ERROR, ex);
                return null;
            }

            // add the suite class to the list of members of the parent
//            if ((parentSuite != null) && !processedClasses.isEmpty()) {
//                for (String simpleClassName : processedClasses) {
//                    parentSuite.add(dotPkg.length() != 0
//                                    ? dotPkg + '.' + simpleClassName
//                                    : simpleClassName);
//                }
//            }
            return testDataObj;
        } catch (IOException ioe) {
            throw new CreationError(ioe);
        }
    }

    /**
     * Makes a string contaning comma-separated list of the given names.
     * If the {@code suffix} parameter is non-{@code null}, each of the given
     * names is appended a given suffix.
     * <p>
     * Examples:
     * <pre>
     *     makeListOfClasses(<"Alpha", "Beta", "Gamma">, null)
     *         =&gt; "Alpha,Beta,Gamma"
     *     makeListOfClasses(<"Alpha", "Beta", "Gamma">, ".class")
     *         =&gt; "Alpha.class,Beta.class,Gamma.class"
     * </pre>
     */
    private static final String makeListOfClasses(final List<String> clsNames,
                                                  final String suffix) {
        if (clsNames.isEmpty()) {
            return "";                                                  //NOI18N
        }

        if (clsNames.size() == 1) {
            return (suffix == null) ? clsNames.get(0)
                                    : clsNames.get(0) + suffix;
        }

        StringBuilder buf = new StringBuilder(128);
        boolean first = true;
        for (String clsName : clsNames) {
            if (!first) {
                buf.append(',');
            }
            buf.append(clsName);
            if (suffix != null) {
                buf.append(suffix);
            }
            first = false;
        }
        return buf.toString();
    }

    /**
     *
     */
    public DataObject createSuiteTest(
                                final FileObject targetRootFolder,
                                final FileObject targetFolder,
                                final String suiteName,
                                final Map<CreateTestParam, Object> params) {
        return createSuiteTest(targetRootFolder,
                               targetFolder,
                               suiteName,
                               params,
                               createTemplateParams(params));
    }

    /**
     *
     */
    public DataObject createSuiteTest(
                                final FileObject targetRootFolder,
                                final FileObject targetFolder,
                                final String suiteName,
                                final Map<CreateTestParam, Object> params,
                                final Map<String, ? extends Object> templateParams) {
        TestCreator testCreator = new TestCreator(params, junitVer);
        ClassPath testClassPath = ClassPathSupport.createClassPath(
                new FileObject[] {targetRootFolder});
        List<String> testClassNames = TestUtil.getJavaFileNames(targetFolder,
                                                                testClassPath);
        
        final String templateId;
        switch (junitVer) {
            case JUNIT3:
                templateId = "PROP_junit3_testClassTemplate";           //NOI18N
                break;
            case JUNIT4:
                templateId = "PROP_junit4_testClassTemplate";           //NOI18N
                break;
            default:
                assert false;
                templateId = null;
                break;
        }
        final DataObject doSuiteTempl
                = loadTestTemplate(templateId);
        if (doSuiteTempl == null) {
            return null;
        }
        
        DataObject suiteDataObj;
        try {
            return createSuiteTest(targetFolder,
                                   suiteName,
                                   testCreator,
                                   templateParams,
                                   doSuiteTempl,
                                   testClassPath,
                                   new LinkedList<String>(testClassNames),
                                   null,            //parent suite
                                   null);           //progress indicator
        } catch (CreationError ex) {
            return null;
        }
    }
    
    /**
     */
    private static DataObject createTestClass(
            ClassPath cp,
            String testClassName,
            DataObject templateDataObj,
            final Map<String, ? extends Object> templateParams)
                                        throws DataObjectNotFoundException,
                                               IOException {
        
        assert cp.getRoots().length == 1;
        FileObject root = cp.getRoots()[0];
        int index = testClassName.lastIndexOf('/');
        String pkg = index > -1 ? testClassName.substring(0, index)
                                : "";                                   //NOI18N
        String clazz = index > -1 ? testClassName.substring(index+1)
                                  : testClassName;

        // create package if it does not exist
        if (pkg.length() > 0) {
            root = FileUtil.createFolder(root, pkg);        //IOException
        }
        // instantiate template into the package
        return templateDataObj.createFromTemplate(          //IOException
                    DataFolder.findFolder(root),
                    clazz,
                    templateParams);
    }

    /**
     *
     */
    private static void save(DataObject dataObj) throws IOException {
        SaveCookie sc = dataObj.getCookie(SaveCookie.class);
        if (null != sc) {
            sc.save();
        }
    }
        
    /**
     * Loads a test template.
     * If the template loading fails, displays an error message.
     *
     * @param  templateID  bundle key identifying the template type
     * @return  loaded template, or <code>null</code> if the template
     *          could not be loaded
     */
    private static DataObject loadTestTemplate(String templateID) {
        // get the Test class template
        String path = NbBundle.getMessage(DefaultPlugin.class,
                                          templateID);
        try {
            FileObject fo = Repository.getDefault().getDefaultFileSystem()
                            .findResource(path);
            if (fo == null) {
                noTemplateMessage(path);
                return null;
            }
            return DataObject.find(fo);
        }
        catch (DataObjectNotFoundException e) {
            noTemplateMessage(path);
            return null;
        }
    }
        
    /**
     *
     */
    private static void noTemplateMessage(String temp) {
        String msg = NbBundle.getMessage(
                CreateTestAction.class,
                "MSG_template_not_found",                           //NOI18N
                temp);
        NotifyDescriptor descr = new NotifyDescriptor.Message(
                msg,
                NotifyDescriptor.ERROR_MESSAGE);
        DialogDisplayer.getDefault().notify(descr);
    }

    /**
     * A helper method to create the reason string from a result
     * and two message bundle keys that indicate the separators to be used instead
     * of "," and " and " in a connected reason like: 
     * "abstract, package-private and without testable methods".
     * <p>
     * The values of the keys are expected to be framed by two extra characters
     * (e.g. as in " and "), which are stripped off. These characters serve to 
     * preserve the spaces in the properties file.
     *
     * @param reason the TestabilityResult to represent
     * @param commaKey bundle key for the connective to be used instead of ", "
     * @param andKey   bundle key for the connective to be used instead of "and"
     * @return String composed of the reasons contained in
     *         <code>reason</code> separated by the values of commaKey and
     *         andKey.
     */
    private static String strReason(TestabilityResult reason, String commaKey, String andKey) {
        String strComma = NbBundle.getMessage(CreateTestAction.class,commaKey);
        String strAnd = NbBundle.getMessage(CreateTestAction.class,andKey);
        String strReason = reason.getReason( // string representation of the reasons
                        strComma.substring(1, strComma.length()-1),
                        strAnd.substring(1, strAnd.length()-1));

        return strReason;

    }

    /**
     *
     */
    private static String getCreatingMsg(String className) {
        return NbBundle.getMessage(
                DefaultPlugin.class,
                "FMT_generator_status_creating",                        //NOI18N
                className);
    }

    /**
     *
     */
    private static String getScanningMsg(String sourceName) {
        return NbBundle.getMessage(
                DefaultPlugin.class,
                "FMT_generator_status_scanning",                        //NOI18N
                sourceName);
    }

    /**
     *
     */
    private static String getIgnoringMsg(String sourceName, String reason) {
        return NbBundle.getMessage(
                DefaultPlugin.class,
                "FMT_generator_status_ignoring",                        //NOI18N
                sourceName);
    }

    
    /**
     * Error thrown by failed test creation.
     */
    @SuppressWarnings("serial")
    private static final class CreationError extends Exception {
        CreationError() {};
        CreationError(Throwable cause) {
            super(cause);
        }
    }
    
    /**
     * Utility class representing the results of a test creation
     * process. It gatheres all tests (as DataObject) created and all
     * classes (as JavaClasses) for which no test was created.
     */
    static final class CreationResults {
        static final CreationResults EMPTY = new CreationResults();

        Set<DataObject> created; // Set< createdTest : DataObject >
        Set<SkippedClass> skipped;
        boolean abborted = false;

        CreationResults() { this(20);}

        CreationResults(int expectedSize) {
            created = new HashSet<DataObject>(expectedSize * 2, 0.5f);
            skipped = new HashSet<SkippedClass>(expectedSize * 2, 0.5f);
        }

        void setAbborted() {
            abborted = true;
        }

        /**
         * Returns true if the process of creation was abborted. The
         * result contains the results gathered so far.
         */
        boolean isAbborted() {
            return abborted;
        }

        /**
         * Adds a new entry to the set of created tests.
         * @return true if it was added, false if it was present before
         */
        boolean addCreated(DataObject test) {
            return created.add(test);
        }

        /**
         */
        boolean addSkipped(SkippedClass skippedClass) {
            return skipped.add(skippedClass);
        }
        
        /**
         */
        void addSkipped(Collection<SkippedClass> skippedClasses) {
            if (!skippedClasses.isEmpty()) {
                skipped.addAll(skippedClasses);
            }
        }

        /**
         * Returns a set of classes that were skipped in the process.
         * @return Set<SkippedClass>
         */
        Set<SkippedClass> getSkipped() {
            return skipped;
        }

        /**
         * Returns a set of test data objects created.
         * @return Set<DataObject>
         */
        Set<DataObject> getCreated() {
            return created;
        }

        /**
         * Combines two results into one. If any of the results is an
         * abborted result, the combination is also abborted. The
         * collections of created and skipped classes are unified.
         * @param rhs the other CreationResult to combine into this
         */
        void combine(CreationResults rhs) {
            if (rhs.abborted) {
                this.abborted = true;
            }

            this.created.addAll(rhs.created);
            this.skipped.addAll(rhs.skipped);
        }

    }
    
}
