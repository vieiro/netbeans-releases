/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.maven.output;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.netbeans.modules.maven.api.output.OutputProcessor;
import org.netbeans.modules.maven.api.output.OutputVisitor;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.maven.api.Constants;
import org.netbeans.modules.maven.api.PluginPropertyUtils;
import org.netbeans.modules.maven.api.execute.RunConfig;
import org.netbeans.modules.maven.model.Utilities;
import org.netbeans.modules.maven.model.pom.Build;
import org.netbeans.modules.maven.model.pom.POMModel;
import org.netbeans.modules.maven.model.pom.POMModelFactory;
import org.netbeans.modules.maven.model.pom.Plugin;
import static org.netbeans.modules.maven.output.Bundle.TXT_ModulesNotSupported;
import org.netbeans.modules.xml.xam.ModelSource;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.OutputEvent;
import org.openide.windows.OutputListener;




/**
 * compilation output processing
 * @author  Milos Kleint
 */
public class JavaOutputListenerProvider implements OutputProcessor {
    
    private static final String[] JAVAGOALS = new String[] {
        "mojo-execute#compiler:compile", //NOI18N
        "mojo-execute#compiler:testCompile", //NOI18N
        // issue #252093
        "mojo-execute#takari-lifecycle-plugin:compile", //NOI18N
        "mojo-execute#takari-lifecycle-plugin:testCompile" //NOI18N
    };
    
    private final Pattern failPattern;
    private String windowsDrive; // #197381
    /** @see org.codehaus.plexus.compiler.javac.JavacCompiler#compile */
    private static final Pattern windowsDriveInfoPattern = Pattern.compile("Compiling \\d+ source files? to ([A-Za-z]:)\\\\.+");
    private final RunConfig config;
    private Boolean jdk9compilerVersionOK;
    private static final String GROUP_CLAZZ_NAME = "clazz";
    private static final String GROUP_LINE_NR = "linenr";
    private static final String GROUP_TEXT = "text";
    private static final String GROUP_DRIVE_NAME = "drive";
    private static final Pattern windowsDriveInfoPattern = Pattern.compile("(?:\\[INFO\\] )?Compiling \\d+ source files? to (?<" + GROUP_DRIVE_NAME + ">[A-Za-z]:)\\\\.+");
    
    /** Creates a new instance of JavaOutputListenerProvider */
    public JavaOutputListenerProvider(RunConfig config) {
        this.config = config;
        //[javac] required because of forked compilation
        //DOTALL seems to fix MEVENIDE-455 on windows. one of the characters seems to be a some kind of newline and that's why the line doesnt' get matched otherwise.
        failPattern = Pattern.compile("\\s*(?:\\[(WARNING|ERROR)\\])?(?:\\[javac\\])?(?:Compilation failure)?\\s*(?<" + GROUP_CLAZZ_NAME + ">.*)\\.java\\:\\[(?<" + GROUP_LINE_NR + ">[0-9]*),([0-9]*)\\] (?<" + GROUP_TEXT + ">.*)", Pattern.DOTALL); //NOI18N
    }
    
    private static final Pattern COMPILER_PROBLEM = Pattern.compile(".*module-info\\.java:.*module not found: .*");
    
    @Messages("TXT_ModulesNotSupported=Modules are not supported with maven-compiler-plugin < 3.6. (Click to fix in pom.xml)")
    @Override
    public void processLine(String line, OutputVisitor visitor) {
            Matcher match = failPattern.matcher(line);
            if (match.matches()) {
                String clazz = match.group(GROUP_CLAZZ_NAME);
                String lineNum = match.group(GROUP_LINE_NR);
                String text = match.group(GROUP_TEXT);
                File clazzfile;
                if (clazz.startsWith("\\") && !clazz.startsWith("\\\\") && windowsDrive != null) {
                    clazzfile = FileUtil.normalizeFile(new File(windowsDrive + clazz + ".java"));
                } else {
                    clazzfile = FileUtil.normalizeFile(new File(clazz + ".java"));
                }
                FileUtil.refreshFor(clazzfile);
                FileObject file = FileUtil.toFileObject(clazzfile);
                String newclazz = clazz;
                if (file != null) {
                    Project prj = FileOwnerQuery.getOwner(file);
                    if (prj != null) {
                        Sources srcs = prj.getLookup().lookup(Sources.class);
                        if (srcs != null) {
                            for (SourceGroup grp : srcs.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA)) {
                                if (FileUtil.isParentOf(grp.getRootFolder(), file)) {
                                    newclazz = FileUtil.getRelativePath(grp.getRootFolder(), file);
                                    if (newclazz.endsWith(".java")) { //NOI18N
                                        newclazz = newclazz.substring(0, newclazz.length() - ".java".length()); //NOI18N
                                    }
                                }
                            }
                        }
                    }
                }
                line = line.replace(clazz, newclazz); //NOI18N
                boolean isImportant = text.indexOf("[deprecation]") < 0; // NOI18N
                if(COMPILER_PROBLEM.matcher(line).matches() && !isJDK9CompilerVersion()) {
                    visitor.setLine(line + "\n" + TXT_ModulesNotSupported());
                    visitor.setOutputListener(new OutputListener() {
                        @Override public void outputLineSelected(OutputEvent ev) {}
                        @Override public void outputLineAction(OutputEvent ev) {
                            FileObject pomFO = FileUtil.toFileObject(config.getMavenProject().getFile());                                                                
                            ModelSource modelSource = Utilities.createModelSource(pomFO);                                    
                            POMModel model = POMModelFactory.getDefault().getModel(modelSource);
                            org.netbeans.modules.maven.model.pom.Project p = model.getProject();
                            Build bld = p.getBuild();
                            if (bld != null) {
                                Plugin plg = bld.findPluginById(Constants.GROUP_APACHE_PLUGINS, Constants.PLUGIN_COMPILER);
                                if (plg != null) {
                                    int pos = plg.findPosition();                                            
                                    if(pos > -1) {
                                        Utilities.openAtPosition(model, pos);
                                    } else {
                                        try {
                                            DataObject pomDO = DataObject.find(pomFO);
                                            OpenCookie oc = pomDO.getLookup().lookup(OpenCookie.class);
                                            if(oc != null) {
                                                oc.open();
                                            }
                                        } catch (DataObjectNotFoundException x) {
                                            Logger.getLogger(JavaOutputListenerProvider.class.getName()).log(Level.INFO, null, x);
                                        }
                                    }
                                }    
                            }
                        }    
                        @Override public void outputLineCleared(OutputEvent ev) {}
                    }
                    , false );
                } else {
                    visitor.setLine(line);
                    visitor.setOutputListener(new CompileAnnotation(clazzfile, lineNum, text), isImportant); 
                }
            }
        match = windowsDriveInfoPattern.matcher(line);
        if (match.matches()) {
            windowsDrive = match.group(GROUP_DRIVE_NAME);
        }        
    }

    @Override
    public String[] getRegisteredOutputSequences() {
        return JAVAGOALS;
    }

    @Override
    public void sequenceStart(String sequenceId, OutputVisitor visitor) {
    }

    @Override
    public void sequenceEnd(String sequenceId, OutputVisitor visitor) {
    }
    
    @Override
    public void sequenceFail(String sequenceId, OutputVisitor visitor) {
    }
    
    private synchronized Boolean isJDK9CompilerVersion() {      
        if(jdk9compilerVersionOK == null) {
            String version = PluginPropertyUtils.getPluginVersion(config.getMavenProject(), Constants.GROUP_APACHE_PLUGINS, Constants.PLUGIN_COMPILER);
            jdk9compilerVersionOK = new ComparableVersion(version).compareTo(new ComparableVersion("3.6-SNAPSHOT")) >= 0 ? Boolean.TRUE : Boolean.FALSE;
        }
        return jdk9compilerVersionOK;
    }
    
}
