/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 
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

package org.netbeans.modules.cnd.makeproject.api.compilers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Vector;
import org.netbeans.modules.cnd.makeproject.api.platforms.Platform;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

public class GNUCCompiler extends CCCCompiler {
    private static final String compilerStdoutCommand = "gcc -x c -dM -E"; // NOI18N
    private static final String compilerStderrCommand = "gcc -x c -v -E"; // NOI18N
    private PersistentList systemIncludeDirectoriesList = null;
    private PersistentList systemPreprocessorSymbolsList = null;
    private boolean saveOK = true;
    
    private static final String[] DEVELOPMENT_MODE_OPTIONS = {
        "",  // Fast Build // NOI18N
        "-g", // Debug" // NOI18N
        "-g -O", // Performance Debug" // NOI18N
        "-g", // Test Coverage // NOI18N
        "-g -O2", // Dianosable Release // NOI18N
        "-O2", // Release // NOI18N
        "-O3", // Performance Release // NOI18N
    };
    
    private static final String[] WARNING_LEVEL_OPTIONS = {
        "-w", // No Warnings // NOI18N
        "", // Default // NOI18N
        "-Wall", // More Warnings // NOI18N
        "-Werror", // Convert Warnings to Errors // NOI18N
    }; // FIXUP: from Bundle
    
    public String getDevelopmentModeOptions(int value) {
        return DEVELOPMENT_MODE_OPTIONS[value];
    }
    
    /** Creates a new instance of GNUCCompiler */
    public GNUCCompiler() {
        super(CCompiler, "gcc", "GNU C Compiler"); // NOI18N
    }
    
    public String getWarningLevelOptions(int value) {
        if (value < WARNING_LEVEL_OPTIONS.length)
            return WARNING_LEVEL_OPTIONS[value];
        else
            return ""; // NOI18N
    }
    
    public String getSixtyfourBitsOption(boolean value) {
        return value ? "-m64" : ""; // NOI18N
    }
    
    public String getStripOption(boolean value) {
        return value ? "-s" : ""; // NOI18N
    }
    
    public void setSystemIncludeDirectories(Platform platform, List values) {
        systemIncludeDirectoriesList = new PersistentList(values);
    }
    
    public void setSystemPreprocessorSymbols(Platform platform, List values) {
        systemPreprocessorSymbolsList = new PersistentList(values);
    }
    
    public List getSystemPreprocessorSymbols(Platform platform) {
        if (systemPreprocessorSymbolsList != null)
            return systemPreprocessorSymbolsList;
        
        if (parseCompilerOutput) {
            getSystemIncludesAndDefines(platform);
            return systemPreprocessorSymbolsList;
        }
        
        Vector list = new Vector();
        return list;
    }
    
    public List getSystemIncludeDirectories(Platform platform) {
        if (systemIncludeDirectoriesList != null)
            return systemIncludeDirectoriesList;
        
        if (parseCompilerOutput) {
            getSystemIncludesAndDefines(platform);
            return systemIncludeDirectoriesList;
        }
        
        Vector list = new Vector();
        if (platform.getId() == Platform.PLATFORM_SOLARIS_INTEL || platform.getId() == Platform.PLATFORM_SOLARIS_SPARC) {
            list.add("/usr/local/include");
            list.add("/usr/sfw/include");
            list.add("/usr/sfw/lib/gcc/i386-pc-solaris2.10/3.4.3/include");
            list.add("/usr/include");
        } else if (platform.getId() == Platform.PLATFORM_LINUX) {
            list.add("/usr/local/include");
            list.add("/usr/include");
        } else if (platform.getId() == Platform.PLATFORM_WINDOWS) {
            if( new File("/usr/").exists() ) {	// NOI18N
                list.add("/usr/lib/gcc/i686-pc-cygwin/3.4.4/include");
                list.add("/usr/include");
                list.add("/usr/lib/gcc/i686-pc-cygwin/3.4.4/../../../../include/w32api");
            } else if( new File("C:/cygwin").exists() ) {	// NOI18N
                list.add("C:/cygwin/lib/gcc/i686-pc-cygwin/3.4.4/include");
                list.add("C:/cygwin/usr/include");
                list.add("C:/cygwin/lib/gcc/i686-pc-cygwin/3.4.4/../../../../include/w32api");
            }
        }
        return list;
    }
    
    public void saveSystemIncludesAndDefines() {
        if (systemIncludeDirectoriesList != null && saveOK)
            systemIncludeDirectoriesList.saveList(getClass().getName() + "." + "systemIncludeDirectoriesList");
        if (systemPreprocessorSymbolsList != null && saveOK)
            systemPreprocessorSymbolsList.saveList(getClass().getName() + "." + "systemPreprocessorSymbolsList");
    }
    
    private void restoreSystemIncludesAndDefines(Platform platform) {
        systemIncludeDirectoriesList = PersistentList.restoreList(getClass().getName() + "." + "systemIncludeDirectoriesList");
        systemPreprocessorSymbolsList = PersistentList.restoreList(getClass().getName() + "." + "systemPreprocessorSymbolsList");
    }
    
    private void getSystemIncludesAndDefines(Platform platform) {
        restoreSystemIncludesAndDefines(platform);
        if (systemIncludeDirectoriesList == null || systemPreprocessorSymbolsList == null) {
            getFreshSystemIncludesAndDefines(platform);
        }
    }
    
    private void getFreshSystemIncludesAndDefines(Platform platform) {
        try {
            systemIncludeDirectoriesList = new PersistentList();
            systemPreprocessorSymbolsList = new PersistentList();
        getSystemIncludesAndDefines(platform, compilerStderrCommand, false);
        getSystemIncludesAndDefines(platform, compilerStdoutCommand, true);
            saveOK = true;
    }
        catch (IOException ioe) {
            String errormsg = NbBundle.getMessage(getClass(), "CANTFINDCOMPILER", getName());
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(errormsg, NotifyDescriptor.ERROR_MESSAGE));
            saveOK = false;
        }
    }
    
    public void resetSystemIncludesAndDefines(Platform platform) {
        getFreshSystemIncludesAndDefines(platform);
    }
    
    protected void parseCompilerOutput(Platform platform, InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        
        // Hack to map include paths to CYGWIN location. FIXUP
        String includePrefix = ""; // NOI18N
        if (platform.getId() == Platform.PLATFORM_WINDOWS) {
            if (new File("/usr/").exists())  // NOI18N
                includePrefix = ""; // NOI18N
            else if (new File("C:/cygwin").exists())  // NOI18N
                includePrefix = "C:/cygwin"; // NOI18N
            else if (new File("D:/cygwin").exists())  // NOI18N
                includePrefix = "D:/cygwin"; // NOI18N
            else if (new File("E:/cygwin").exists())  // NOI18N
                includePrefix = "E:/cygwin"; // NOI18N
            else if (new File("F:/cygwin").exists())  // NOI18N
                includePrefix = "F:/cygwin"; // NOI18N
        }
        
        try {
            String line;
            boolean startIncludes = false;
            while ((line = reader.readLine()) != null) {
                //System.out.println(line);
                if (line.startsWith("#include <...>")) { // NOI18N
                    startIncludes = true;
                    continue;
                }
                if (line.startsWith("End of search")) { // NOI18N
                    startIncludes = false;
                    continue;
                }
                if (startIncludes) {
                    line = line.trim();
                    systemIncludeDirectoriesList.add(includePrefix + line);
                    if (includePrefix.length() > 0 && line.startsWith("/usr/lib")) // NOI18N
                        systemIncludeDirectoriesList.add(includePrefix + line.substring(4));
                    continue;
                }
                
                int defineIndex = line.indexOf("-D"); // NOI18N
                while (defineIndex > 0) {
                    String token;
                    int spaceIndex = line.indexOf(" ", defineIndex + 1); // NOI18N
                    if (spaceIndex > 0) {
                        token = line.substring(defineIndex+2, spaceIndex);
                        systemPreprocessorSymbolsList.add(token);
                        defineIndex = line.indexOf("-D", spaceIndex); // NOI18N
                    }
                }
                
                if (line.startsWith("#define ")) { // NOI18N
                    int i = line.indexOf(' ', 8);
                    if (i > 0) {
                        String token = line.substring(8, i) + "=" + line.substring(i+1);
                        systemPreprocessorSymbolsList.add(token);
                    }
                }
            }
            is.close();
            reader.close();
        } catch (IOException ioe) {
            ErrorManager.getDefault().notify(ErrorManager.WARNING, ioe); // FIXUP
        }
    }
    
    private void dumpLists() {
        System.out.println("==================================" + getDisplayName());
        for (int i = 0; i < systemIncludeDirectoriesList.size(); i++) {
            System.out.println("-I" + systemIncludeDirectoriesList.get(i)); // NOI18N
        }
        for (int i = 0; i < systemPreprocessorSymbolsList.size(); i++) {
            System.out.println("-D" + systemPreprocessorSymbolsList.get(i)); // NOI18N
        }
    }
}
