/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU General
 * Public License Version 2 only ("GPL") or the Common Development and Distribution
 * License("CDDL") (collectively, the "License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html or nbbuild/licenses/CDDL-GPL-2-CP. See the
 * License for the specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header Notice in
 * each file and include the License file at nbbuild/licenses/CDDL-GPL-2-CP.  Oracle
 * designates this particular file as subject to the "Classpath" exception as
 * provided by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the License Header,
 * with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * The Original Software is NetBeans. The Initial Developer of the Original Software
 * is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun Microsystems, Inc. All
 * Rights Reserved.
 * 
 * If you wish your version of this file to be governed by only the CDDL or only the
 * GPL Version 2, indicate your decision by adding "[Contributor] elects to include
 * this software in this distribution under the [CDDL or GPL Version 2] license." If
 * you do not indicate a single choice of license, a recipient has the option to
 * distribute your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above. However, if
 * you add GPL Version 2 code and therefore, elected the GPL Version 2 license, then
 * the option applies only if the new code is made subject to such option by the
 * copyright holder.
 */
package org.netbeans.installer.products.weblogic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.installer.product.Registry;
import org.netbeans.installer.product.components.Product;
import org.netbeans.installer.utils.applications.JavaUtils;
import org.netbeans.installer.product.components.ProductConfigurationLogic;
import org.netbeans.installer.utils.exceptions.InitializationException;
import org.netbeans.installer.utils.exceptions.InstallationException;
import org.netbeans.installer.utils.exceptions.UninstallationException;
import org.netbeans.installer.utils.helper.FilesList;
import org.netbeans.installer.utils.progress.Progress;
import org.netbeans.installer.wizard.Wizard;
import org.netbeans.installer.wizard.components.WizardComponent;
import org.netbeans.installer.wizard.components.panels.JdkLocationPanel;
import org.netbeans.installer.products.weblogic.wizard.panels.WebLogicPanel;
import org.netbeans.installer.utils.FileUtils;
import org.netbeans.installer.utils.LogManager;
import org.netbeans.installer.utils.StringUtils;
import org.netbeans.installer.utils.SystemUtils;
import org.netbeans.installer.utils.applications.JavaUtils.JavaInfo;
import org.netbeans.installer.utils.applications.NetBeansUtils;
import org.netbeans.installer.utils.applications.WebLogicUtils;
import org.netbeans.installer.utils.helper.Status;

/**
 *
 * @author Kirill Sorokin
 * @author Dmitry Lipin
 */
public class ConfigurationLogic extends ProductConfigurationLogic {
    /////////////////////////////////////////////////////////////////////////////////
    // Instance
    private List<WizardComponent> wizardComponents;

    // constructor //////////////////////////////////////////////////////////////////
    public ConfigurationLogic() throws InitializationException {
        wizardComponents = Wizard.loadWizardComponents(
                WIZARD_COMPONENTS_URI,
                getClass().getClassLoader());
    }

    // configuration logic implementation ///////////////////////////////////////////
    public void install(final Progress progress)
            throws InstallationException {
        final File directory = getProduct().getInstallationLocation();
       
        final File domainsubdir = new File(getProperty(WebLogicPanel.DOMAIN_INSTALLATION_SUBDIR_PROPERTY));
        final String domainname = getProperty(WebLogicPanel.DOMAINNAME_PROPERTY);
        final String username = getProperty(WebLogicPanel.USERNAME_PROPERTY);        
        final String password = getProperty(WebLogicPanel.PASSWORD_PROPERTY);       
        
        final File domaindir = new File(domainsubdir, domainname);

        final File javaHome =
                new File(getProperty(JdkLocationPanel.JDK_LOCATION_PROPERTY));
        JavaInfo info = JavaUtils.getInfo(javaHome);
        LogManager.log("Using the following JDK for WebLogic configuration : ");
        LogManager.log("... path    : "  + javaHome);
        LogManager.log("... version : "  + info.getVersion().toJdkStyle());
        LogManager.log("... vendor  : "  + info.getVendor());
        LogManager.log("... final   : "  + (!info.isNonFinal()));

        final FilesList list = getProduct().getInstalledFiles();
        
//        if(!domainsubdir.exists()) { //if it is a new dir
//               list.add(domainsubdir);
//        }
  
 /////////////////////////////////////////////////////////////////////////////       
        try {                                    
            progress.setDetail(getString("CL.install.create.domain")); // NOI18N
            WebLogicUtils.createDomain(directory, javaHome, domainsubdir, domainname, username, password);            
                
        } catch (IOException e) {
            throw new InstallationException(
                    getString("CL.install.error.create.domain"), // NOI18N
                    e);            
        }
        
        try {
            progress.setDetail(getString("CL.install.extra.files")); // NOI18N                        
            //TODO: do it more clever: check installed files after domain creation and add the difference only.
            //if(!domainsubdir.exists()) { //if it is a new dir
            //    list.add(domainsubdir);
            //} else {
            //    list.add(domaindir);                
            //}
            addFiles(list, domaindir);    
            list.add(new File(directory, REGISTRY_XML));
            list.add(new File(directory, LOGS_DIR));
        } catch (IOException e) {
            throw new InstallationException(
                    getString("CL.install.error.extra.files"), // NOI18N
                    e);
        }                  
      
        //get bundled registry to perform further runtime integration
        //http://wiki.netbeans.org/NetBeansInstallerIDEAndRuntimesIntegration
        Registry bundledRegistry = new Registry();
        try {
            final String bundledRegistryUri = System.getProperty(
                    Registry.BUNDLED_PRODUCT_REGISTRY_URI_PROPERTY);

            bundledRegistry.loadProductRegistry(
                    (bundledRegistryUri != null) ? bundledRegistryUri : Registry.DEFAULT_BUNDLED_PRODUCT_REGISTRY_URI);
        } catch (InitializationException e) {
            LogManager.log("Cannot load bundled registry", e);
        }
         
        /////////////////////////////////////////////////////////////////////////////
        try {
            progress.setDetail(getString("CL.install.ide.integration")); // NOI18N

            final List<Product> ides =
                    Registry.getInstance().getProducts("nb-base");
            List<Product> productsToIntegrate = new ArrayList<Product>();
            for (Product ide : ides) {
                if (ide.getStatus() == Status.INSTALLED) {
                    LogManager.log("... checking if " + getProduct().getDisplayName() + " can be integrated with " + ide.getDisplayName() + " at " + ide.getInstallationLocation());
                    final File location = ide.getInstallationLocation();
                    if (location != null && FileUtils.exists(location) && !FileUtils.isEmpty(location)) {
                        final Product bundledProduct = bundledRegistry.getProduct(ide.getUid(), ide.getVersion());
                        if (bundledProduct != null) {
                            //one of already installed IDEs is in the bundled registry as well - we need to integrate with it
                            productsToIntegrate.add(ide);
                            LogManager.log("... will be integrated since this produce is also bundled");
                        } else {
                            //check if this IDE is not integrated with any other WL instance - we need integrate with such IDE instance
                            try {
                                if(!isWebLogicRegistred(location)) {
                                    LogManager.log("... will be integrated since there it is not yet integrated with any instance or such an instance does not exist");
                                    productsToIntegrate.add(ide);
                                } else {
                                    LogManager.log("... will not be integrated since it is already integrated with another instance");
                                }
                            } catch (IOException e)  {
                                LogManager.log(e);
                            }
                        }
                    }
                }
            }

            for (Product productToIntegrate : productsToIntegrate) {
                final File location = productToIntegrate.getInstallationLocation();
                //registerJavaDB(location, new File(directory, "javadb"));
                LogManager.log("... integrate " + getProduct().getDisplayName() + " with " + productToIntegrate.getDisplayName() + " installed at " + location);
                if(!registerWebLogic(location, directory, domaindir, username, password)) {
                    continue;
                }
                
                // if the IDE was installed in the same session as the
                // appserver, we should add its "product id" to the IDE
                if (productToIntegrate.hasStatusChanged()) {
                    NetBeansUtils.addPackId(
                            location,
                            PRODUCT_ID);

                }
            }
        } catch  (IOException e) {
            throw new InstallationException(
                    getString("CL.install.error.ide.integration"), // NOI18N
                    e);
        }

        /////////////////////////////////////////////////////////////////////////////
        progress.setPercentage(Progress.COMPLETE);                     
    }
    
   private void addFiles(FilesList list, File location) throws IOException {
        LogManager.log("...addFiles");
        if(FileUtils.exists(location)) {
            if(location.isDirectory()) {
                list.add(location);
                File [] files = location.listFiles();
                if(files!=null && files.length>0) {
                    for(File f: files) {
                        addFiles(list, f);
                    }
                }
            } else {
                LogManager.log("...Adding " + location.getAbsolutePath() + " to the list");
                list.add(location);
            }
        }
    }    
    
    private boolean isWebLogicRegistred(File nbLocation) throws IOException {
        return new File (nbLocation, "nb/config/J2EE/InstalledServers/Instances/weblogic_autoregistered_instance").exists();
    }
  
    private boolean registerWebLogic(File nbLocation, File wlLocation, File domaindir, String username, String password) throws IOException {
        File javaExe = JavaUtils.getExecutable(new File(System.getProperty("java.home")));
        String [] cp = {
            "platform/core/core.jar",
            "platform/lib/boot.jar",
            "platform/lib/org-openide-modules.jar",
            "platform/core/org-openide-filesystems.jar",
            "platform/lib/org-openide-util.jar",
            "platform/lib/org-openide-util-lookup.jar",
            "enterprise/modules/org-netbeans-modules-j2eeapis.jar",
            "enterprise/modules/org-netbeans-modules-j2eeserver.jar",
            "enterprise/modules/org-netbeans-modules-j2ee-weblogic9.jar"
        };
        for(String c : cp) {
            File f = new File(nbLocation, c);
            if(!FileUtils.exists(f)) {
                LogManager.log("... cannot find jar required for WebLogic integration: " + f);
                return false;
            }
        }
        String mainClass = "org.netbeans.modules.j2ee.weblogic9.registration.AutomaticRegistration";
        List <String> commands = new ArrayList <String> ();
        File nbCluster = new File(nbLocation, "nb");
        commands.add(javaExe.getAbsolutePath());
        commands.add("-cp");
        commands.add(StringUtils.asString(cp, File.pathSeparator));
        commands.add(mainClass);
        commands.add("--add");
        commands.add(nbCluster.getAbsolutePath());     
        commands.add(new File(wlLocation, "wlserver").getAbsolutePath());
        commands.add(domaindir.getAbsolutePath());
        commands.add(username);
        commands.add(password);
        
        return SystemUtils.executeCommand(nbLocation, commands.toArray(new String[]{})).getErrorCode() == 0;
    }
    private  boolean removeWebLogicIntegration(File nbLocation,  File wlLocation, File domaindir) throws IOException {
        LogManager.log("... ide location is " + nbLocation);      

        //TODO Tomcat will be unregistered as well!!! Fix here and in Tomcat logic
        //FileUtils.deleteFile(new File (nbLocation, "nb/config/J2EE/InstalledServers/.nbattrs"));                 

        File javaExe = JavaUtils.getExecutable(new File(System.getProperty("java.home")));
        String [] cp = {
            "platform/core/core.jar",
            "platform/lib/boot.jar",
            "platform/lib/org-openide-modules.jar",
            "platform/core/org-openide-filesystems.jar",
            "platform/lib/org-openide-util.jar",
            "platform/lib/org-openide-util-lookup.jar",
            "enterprise/modules/org-netbeans-modules-j2eeapis.jar",
            "enterprise/modules/org-netbeans-modules-j2eeserver.jar",
            "enterprise/modules/org-netbeans-modules-j2ee-weblogic9.jar"
        };
        for(String c : cp) {
            File f = new File(nbLocation, c);
            if(!FileUtils.exists(f)) {
                LogManager.log("... cannot find jar required for WebLogic integration: " + f);
                return false;
            }
        }
        String mainClass = "org.netbeans.modules.j2ee.weblogic9.registration.AutomaticRegistration";
        List <String> commands = new ArrayList <String> ();
        File nbCluster = new File(nbLocation, "nb");
        commands.add(javaExe.getAbsolutePath());
        commands.add("-cp");
        commands.add(StringUtils.asString(cp, File.pathSeparator));
        commands.add(mainClass);
        commands.add("--remove");
        commands.add(nbCluster.getAbsolutePath());     
        commands.add(new File(wlLocation, "wlserver").getAbsolutePath());
        commands.add(domaindir.getAbsolutePath());        
        
        boolean result = SystemUtils.executeCommand(nbLocation, commands.toArray(new String[]{})).getErrorCode() == 0;
        
        FileUtils.deleteFile(new File (nbLocation, "nb/config/J2EE/InstalledServers/weblogic_autoregistered_instance"));
        return result;
    }
    
    

    public void uninstall(final Progress progress)
            throws UninstallationException {
        File directory = getProduct().getInstallationLocation();

        final File domainsubdir = new File(getProperty(WebLogicPanel.DOMAIN_INSTALLATION_SUBDIR_PROPERTY));
        final String domainname = getProperty(WebLogicPanel.DOMAINNAME_PROPERTY);           
        
        final File domaindir = new File(domainsubdir, domainname);
        

        /////////////////////////////////////////////////////////////////////////////
        try {
            progress.setDetail(getString("CL.uninstall.ide.integration")); // NOI18N

            final List<Product> ides =
                    Registry.getInstance().getProducts("nb-base");
            for (Product ide: ides) {
                if (ide.getStatus() == Status.INSTALLED) {
                    LogManager.log("... checking if " + ide.getDisplayName() + " is integrated with " + getProduct().getDisplayName() + " installed at " + directory);
                    final File nbLocation = ide.getInstallationLocation();
                   
                    if (nbLocation != null) {
                        removeWebLogicIntegration(nbLocation, directory, domaindir);
                    }
                }
            }
        } catch (IOException e) {
            throw new UninstallationException(
                    getString("CL.uninstall.error.ide.integration"), // NOI18N
                    e);
        }
        
/////////////////////////////////////////////////////////////////////////////
        try {
            progress.setDetail(getString("CL.uninstall.stop.domain")); // NOI18N

            WebLogicUtils.stopDomain(directory, domaindir);
        } catch (IOException e) {
            throw new UninstallationException(
                    getString("CL.uninstall.error.stop.domain"), // NOI18N
                    e);
        }
        
//        File directory = getProduct().getInstallationLocation();
//
//        /////////////////////////////////////////////////////////////////////////////
////        try {
////            progress.setDetail(getString("CL.uninstall.ide.integration")); // NOI18N
//
//            final List<Product> ides =
//                    Registry.getInstance().getProducts("nb-base");
//            for (Product ide: ides) {
//                if (ide.getStatus() == Status.INSTALLED) {
//                    LogManager.log("... checking if " + ide.getDisplayName() + " is integrated with " + getProduct().getDisplayName() + " installed at " + directory);
//                    final File nbLocation = ide.getInstallationLocation();
//
//                    if (nbLocation != null) {
//                        LogManager.log("... ide location is " + nbLocation);
//                        final String value = NetBeansUtils.getJvmOption(
//                                nbLocation,
//                                JVM_OPTION_NAME);
//                        LogManager.log("... ide integrated with: " + value);
//                        if ((value != null) &&
//                                (value.equals(directory.getAbsolutePath()))) {
//			    LogManager.log("... removing integration");
//                            NetBeansUtils.removeJvmOption(
//                                    nbLocation,
//                                    JVM_OPTION_NAME);
//                        }
//                    }
//                }
//            }
//        } catch (IOException e) {
//            throw new UninstallationException(
//                    getString("CL.uninstall.error.ide.integration"), // NOI18N
//                    e);
//        }
//
///////////////////////////////////////////////////////////////////////////////
//        try {
//            progress.setDetail(getString("CL.uninstall.delete.domain")); // NOI18N
//
//            GlassFishUtils.deleteDomain(directory, DOMAIN_NAME);
//        } catch (IOException e) {
//            throw new UninstallationException(
//                    getString("CL.uninstall.error.delete.domain"), // NOI18N
//                    e);
//        }
//
///////////////////////////////////////////////////////////////////////////////
//        try {
//            if(SystemUtils.isWindows()) {
//                progress.setDetail(getString("CL.uninstall.stop.derby")); // NOI18N
//
//                GlassFishUtils.stopDerby(directory);
//            }
//        } catch (IOException e) {
//            throw new UninstallationException(
//                    getString("CL.uninstall.error.stop.derby"), // NOI18N
//                    e);
//        } catch (NoSuchMethodError e) {
//            //TODO
//        }
//
//

/////////////////////////////////////////////////////////////////////////////
//        try {
//            progress.setDetail(getString("CL.uninstall.extra.files")); // NOI18N
//
//            if (SystemUtils.isWindows()) {
//                FileUtils.deleteFile(new File(directory, ASENV_BAT));
//                FileUtils.deleteFile(new File(directory, ASADMIN_BAT));
//                FileUtils.deleteFile(new File(directory, ASANT_BAT));
//                FileUtils.deleteFile(new File(directory, APPCLIENT_BAT));
//                FileUtils.deleteFile(new File(directory, JSPC_BAT));
//                FileUtils.deleteFile(new File(directory, PACKAGE_APPCLIENT_BAT));
//                FileUtils.deleteFile(new File(directory, VERIFIER_BAT));
//                FileUtils.deleteFile(new File(directory, ASUPGRADE_BAT));
//                FileUtils.deleteFile(new File(directory, CAPTURE_SCHEMA_BAT));
//                FileUtils.deleteFile(new File(directory, WSIMPORT_BAT));
//                FileUtils.deleteFile(new File(directory, WSGEN_BAT));
//                FileUtils.deleteFile(new File(directory, SCHEMAGEN_BAT));
//                FileUtils.deleteFile(new File(directory, XJC_BAT));
//                FileUtils.deleteFile(new File(directory, ASAPT_BAT));
//                FileUtils.deleteFile(new File(directory, WSCOMPILE_BAT));
//                FileUtils.deleteFile(new File(directory, WSDEPLOY_BAT));
//            } else {
//                FileUtils.deleteFile(new File(directory, ASENV_CONF));
//                FileUtils.deleteFile(new File(directory, UNINSTALL));
//                FileUtils.deleteFile(new File(directory, ASADMIN));
//                FileUtils.deleteFile(new File(directory, ASANT));
//                FileUtils.deleteFile(new File(directory, APPCLIENT));
//                FileUtils.deleteFile(new File(directory, JSPC));
//                FileUtils.deleteFile(new File(directory, PACKAGE_APPCLIENT));
//                FileUtils.deleteFile(new File(directory, VERIFIER));
//                FileUtils.deleteFile(new File(directory, ASUPGRADE));
//                FileUtils.deleteFile(new File(directory, CAPTURE_SCHEMA));
//                FileUtils.deleteFile(new File(directory, WSIMPORT));
//                FileUtils.deleteFile(new File(directory, WSGEN));
//                FileUtils.deleteFile(new File(directory, XJC));
//                FileUtils.deleteFile(new File(directory, SCHEMAGEN));
//                FileUtils.deleteFile(new File(directory, ASAPT));
//                FileUtils.deleteFile(new File(directory, WSCOMPILE));
//                FileUtils.deleteFile(new File(directory, WSDEPLOY));
//            }
//        } catch (IOException e) {
//            throw new UninstallationException(
//                    getString("CL.uninstall.error.extra.files"), // NOI18N
//                    e);
//        }

/////////////////////////////////////////////////////////////////////////////
        progress.setPercentage(Progress.COMPLETE);
    }

    public List<WizardComponent> getWizardComponents() {
        return wizardComponents;
    }

    @Override
    public boolean allowModifyMode() {
        return false;
    }


    public boolean requireLegalArtifactSaving() {     
       return false;                                   
    }

    
    
///////////////////////////////////////////////////////////////////////////
//      public static void createDomain(File weblogicLocation, File jdkLocation, File domainLocation, String domainName, String username, String adminPassword) throws IOException {
//        //SystemUtils.getEnvironment().put("MW_HOME", weblogicLocation.getAbsolutePath());
//       // SystemUtils.getEnvironment().put("JAVA_HOME", jdkLocation.getAbsolutePath());
//        
//        LogManager.log(/*ErrorLevel.DEBUG,*/ "environment =  " + SystemUtils.getEnvironment().toString());
//                              
//        final ExecutionResults results = executeCommand(weblogicLocation, jdkLocation, weblogicLocation,
//                "/bin/bash", getConfigure(weblogicLocation).getAbsolutePath());
//        System.out.println("results out"+ results.getStdOut());
//        LogManager.log("results err" + results.getStdErr());
//        LogManager.log("results out"+ results.getStdOut());
//        try{
//            LogManager.log("After process");
//            LogManager.log(/*ErrorLevel.DEBUG,*/ "CLASSPATH  env " + SystemUtils.getEnvironmentVariable("CLASSPATH"));
//            LogManager.log(/*ErrorLevel.DEBUG,*/ "PATH  env " + SystemUtils.getEnvironmentVariable("PATH"));
//            LogManager.log(/*ErrorLevel.DEBUG,*/ "CLASSPATH  getProperty " +  System.getProperty("CLASSPATH"));
//            LogManager.log(/*ErrorLevel.DEBUG,*/ "PATH  getProperty " +  System.getProperty("PATH"));
//           
//        }catch(NativeException ne) {
//            LogManager.log( ne);
//        }
//                              
////        if (results.getStdOut().indexOf(COULD_NOT_CREATE_DOMAIN_MARKER) != -1 || 
////		results.getStdErr().indexOf(COULD_NOT_CREATE_DOMAIN_MARKER) != -1) {
////            throw new DomainCreationException(CLI_130);
////        }
////        
////        if (results.getErrorCode() > 0) {
////            throw new DomainCreationException(results.getErrorCode());
////        }
//    }
//    public static File getConfigure(File location) {
//        return new File(location, SystemUtils.isWindows() ? "configure.cmd" : "configure.sh");
//    }
//    
//   public static ExecutionResults executeCommand(File weblogicLocation, File jdkLocation, File workingDirectory, String... command) throws IOException {
//        // construct the initial log message
//        String commandString = StringUtils.asString(command, StringUtils.SPACE);
//        
//       /* if (workingDirectory == null) {
//            workingDirectory = getCurrentDirectory();
//        }*/
//        
//        LogManager.log(ErrorLevel.MESSAGE,
//                "executing command: " + commandString +
//                ", in directory: " + workingDirectory);
//        LogManager.indent();
//        
//        StringBuilder processStdOut = new StringBuilder();
//        StringBuilder processStdErr = new StringBuilder();
//        int           errorLevel = ExecutionResults.TIMEOUT_ERRORCODE;
//        
//        ProcessBuilder builder= new ProcessBuilder(command).directory(workingDirectory);
//        
//        builder.environment().put("MW_HOME", weblogicLocation.getAbsolutePath());
//        builder.environment().put("JAVA_HOME", jdkLocation.getAbsolutePath());
//                
//        Process process = builder.start();
//        
//        long startTime = System.currentTimeMillis();
//        long endTime   = startTime + MAX_EXECUTION_TIME;
//        boolean doRun = true;
//        long delay = INITIAL_DELAY;
//        while (doRun && (System.currentTimeMillis() < endTime)) {
//            try {
//                Thread.sleep(delay);
//                if(delay < MAX_DELAY) {
//                    delay += DELTA_DELAY;
//                }
//            }  catch (InterruptedException e) {
//                ErrorManager.notifyDebug("Interrupted", e);
//            }
//            try {
//                errorLevel = process.exitValue();
//                doRun = false;
//            } catch (IllegalThreadStateException e) {
//                ; // do nothing - the process is still running
//            }
//            String string;
//            
//            string = StringUtils.readStream(process.getInputStream());
//            if (string.length() > 0) {
//                BufferedReader reader = new BufferedReader(new StringReader(string));
//                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
//                    LogManager.log(ErrorLevel.MESSAGE, "[stdout]: " + line);
//                }
//                
//                processStdOut.append(string);
//            }
//            
//            string = StringUtils.readStream(process.getErrorStream());
//            if (string.length() > 0) {
//                BufferedReader reader = new BufferedReader(new StringReader(string));
//                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
//                    LogManager.log(ErrorLevel.MESSAGE, "[stderr]: " + line);
//                }
//                
//                processStdErr.append(string);
//            }
//        }
//        
//        LogManager.log(ErrorLevel.MESSAGE, 
//                (doRun) ? 
//                    "[return]: killed by timeout" : 
//                    "[return]: " + errorLevel);
//        
//       try{
//           LogManager.log("In process");
//            LogManager.log(/*ErrorLevel.DEBUG,*/ "CLASSPATH  env local " + builder.environment().get("CLASSPATH"));
//            LogManager.log(/*ErrorLevel.DEBUG,*/ "PATH  env local " + builder.environment().get("PATH"));
//            LogManager.log(/*ErrorLevel.DEBUG,*/ "CLASSPATH  env " + SystemUtils.getEnvironmentVariable("CLASSPATH"));
//            LogManager.log(/*ErrorLevel.DEBUG,*/ "PATH  env " + SystemUtils.getEnvironmentVariable("PATH"));
//            LogManager.log(/*ErrorLevel.DEBUG,*/ "CLASSPATH  getProperty " +  System.getProperty("CLASSPATH"));
//            LogManager.log(/*ErrorLevel.DEBUG,*/ "PATH  getProperty " +  System.getProperty("PATH"));
//           
//        }catch(NativeException ne) {
//            LogManager.log( ne);
//        }       
//        process.destroy();        
//        LogManager.unindent();
//        LogManager.log(ErrorLevel.MESSAGE, "... command execution finished");
//        
//        return new ExecutionResults(errorLevel, processStdOut.toString(), processStdErr.toString());
//   }
//    
//    
///////////////////////////////////////////////////////////////////////////////////
//// Constants
    public static final String WIZARD_COMPONENTS_URI =
            "resource:" + // NOI18N
            "org/netbeans/installer/products/weblogic/wizard.xml"; // NOI18N

//    public static final String DOMAIN_NAME =
//            "domain1"; // NOI18N
//    
//    public static final String ASENV_BAT_TEMPLATE =
//            "lib/install/templates/asenv.bat.template"; // NOI18N
//    public static final String ASENV_BAT =
//            "config/asenv.bat"; // NOI18N
//    
//    public static final String ASADMIN_BAT_TEMPLATE =
//            "lib/install/templates/asadmin.bat.template"; // NOI18N
//    public static final String ASADMIN_BAT =
//            "bin/asadmin.bat"; // NOI18N
//    
//    public static final String ASANT_BAT_TEMPLATE =
//            "lib/install/templates/asant.bat.template"; // NOI18N
//    public static final String ASANT_BAT =
//            "bin/asant.bat"; // NOI18N
//    
//    public static final String APPCLIENT_BAT_TEMPLATE =
//            "lib/install/templates/appclient.bat.template"; // NOI18N
//    public static final String APPCLIENT_BAT =
//            "bin/appclient.bat"; // NOI18N
//    
//    public static final String JSPC_BAT_TEMPLATE =
//            "lib/install/templates/jspc.bat.template"; // NOI18N
//    public static final String JSPC_BAT =
//            "bin/jspc.bat"; // NOI18N
//    
//    public static final String PACKAGE_APPCLIENT_BAT_TEMPLATE =
//            "lib/install/templates/package-appclient.bat.template"; // NOI18N
//    public static final String PACKAGE_APPCLIENT_BAT =
//            "bin/package-appclient.bat"; // NOI18N
//    
//    public static final String VERIFIER_BAT_TEMPLATE =
//            "lib/install/templates/verifier.bat.template"; // NOI18N
//    public static final String VERIFIER_BAT =
//            "bin/verifier.bat"; // NOI18N
//    
//    public static final String ASUPGRADE_BAT_TEMPLATE =
//            "lib/install/templates/asupgrade.bat.template"; // NOI18N
//    public static final String ASUPGRADE_BAT =
//            "bin/asupgrade.bat"; // NOI18N
//    
//    public static final String CAPTURE_SCHEMA_BAT_TEMPLATE =
//            "lib/install/templates/capture-schema.bat.template"; // NOI18N
//    public static final String CAPTURE_SCHEMA_BAT =
//            "bin/capture-schema.bat"; // NOI18N
//    
//    public static final String WSIMPORT_BAT_TEMPLATE =
//            "lib/install/templates/wsimport.bat.template"; // NOI18N
//    public static final String WSIMPORT_BAT =
//            "bin/wsimport.bat"; // NOI18N
//    
//    public static final String WSGEN_BAT_TEMPLATE =
//            "lib/install/templates/wsgen.bat.template"; // NOI18N
//    public static final String WSGEN_BAT =
//            "bin/wsgen.bat"; // NOI18N
//    
//    public static final String SCHEMAGEN_BAT_TEMPLATE =
//            "lib/install/templates/schemagen.bat.template"; // NOI18N
//    public static final String SCHEMAGEN_BAT =
//            "bin/schemagen.bat"; // NOI18N
//    
//    public static final String XJC_BAT_TEMPLATE =
//            "lib/install/templates/xjc.bat.template"; // NOI18N
//    public static final String XJC_BAT =
//            "bin/xjc.bat"; // NOI18N
//    
//    public static final String ASAPT_BAT_TEMPLATE =
//            "lib/install/templates/asapt.bat.template"; // NOI18N
//    public static final String ASAPT_BAT =
//            "bin/asapt.bat"; // NOI18N
//    
//    public static final String WSCOMPILE_BAT_TEMPLATE =
//            "lib/install/templates/wscompile.bat.template"; // NOI18N
//    public static final String WSCOMPILE_BAT =
//            "bin/wscompile.bat"; // NOI18N
//    
//    public static final String WSDEPLOY_BAT_TEMPLATE =
//            "lib/install/templates/wsdeploy.bat.template"; // NOI18N
//    public static final String WSDEPLOY_BAT =
//            "bin/wsdeploy.bat"; // NOI18N
//    
//    public static final String UPDATETOOL_BAT_TEMPLATE =
//            "updatecenter/lib/install/templates/updatetool.bat.template";//NOI18N
//    public static final String UPDATETOOL_BAT =
//            "/updatecenter/bin/updatetool.bat";//NOI18N
//    
//    
//    public static final String ASENV_CONF_TEMPLATE =
//            "lib/install/templates/asenv.conf.template"; // NOI18N
//    public static final String ASENV_CONF =
//            "config/asenv.conf"; // NOI18N
//    
//    public static final String ASADMINENV_CONF_TEMPLATE =
//            "lib/install/templates/asadminenv.conf"; // NOI18N
//    public static final String ASADMINENV_CONF =
//            "config/asadminenv.conf"; // NOI18N
//    
//    public static final String UNINSTALL_TEMPLATE =
//            "lib/install/templates/uninstall.template"; // NOI18N
//    public static final String UNINSTALL =
//            "bin/uninstall"; // NOI18N
//    
//    public static final String ASADMIN_TEMPLATE =
//            "lib/install/templates/asadmin.template"; // NOI18N
//    public static final String ASADMIN =
//            "bin/asadmin"; // NOI18N
//    
//    public static final String ASANT_TEMPLATE =
//            "lib/install/templates/asant.template"; // NOI18N
//    public static final String ASANT =
//            "bin/asant"; // NOI18N
//    
//    public static final String APPCLIENT_TEMPLATE =
//            "lib/install/templates/appclient.template"; // NOI18N
//    public static final String APPCLIENT =
//            "bin/appclient"; // NOI18N
//    
//    public static final String JSPC_TEMPLATE =
//            "lib/install/templates/jspc.template"; // NOI18N
//    public static final String JSPC =
//            "bin/jspc"; // NOI18N
//    
//    public static final String PACKAGE_APPCLIENT_TEMPLATE =
//            "lib/install/templates/package-appclient.template"; // NOI18N
//    public static final String PACKAGE_APPCLIENT =
//            "bin/package-appclient"; // NOI18N
//    
//    public static final String VERIFIER_TEMPLATE =
//            "lib/install/templates/verifier.template"; // NOI18N
//    public static final String VERIFIER =
//            "bin/verifier"; // NOI18N
//    
//    public static final String ASUPGRADE_TEMPLATE =
//            "lib/install/templates/asupgrade.template"; // NOI18N
//    public static final String ASUPGRADE =
//            "bin/asupgrade"; // NOI18N
//    
//    public static final String CAPTURE_SCHEMA_TEMPLATE =
//            "lib/install/templates/capture-schema.template"; // NOI18N
//    public static final String CAPTURE_SCHEMA =
//            "bin/capture-schema"; // NOI18N
//    
//    public static final String WSIMPORT_TEMPLATE =
//            "lib/install/templates/wsimport.template"; // NOI18N
//    public static final String WSIMPORT =
//            "bin/wsimport"; // NOI18N
//    
//    public static final String WSGEN_TEMPLATE =
//            "lib/install/templates/wsgen.template"; // NOI18N
//    public static final String WSGEN =
//            "bin/wsgen"; // NOI18N
//    
//    public static final String XJC_TEMPLATE =
//            "lib/install/templates/xjc.template"; // NOI18N
//    public static final String XJC =
//            "bin/xjc"; // NOI18N
//    
//    public static final String SCHEMAGEN_TEMPLATE =
//            "lib/install/templates/schemagen.template"; // NOI18N
//    public static final String SCHEMAGEN =
//            "bin/schemagen"; // NOI18N
//    
//    public static final String ASAPT_TEMPLATE =
//            "lib/install/templates/asapt.template"; // NOI18N
//    public static final String ASAPT =
//            "bin/asapt"; // NOI18N
//    
//    public static final String WSCOMPILE_TEMPLATE =
//            "lib/install/templates/wscompile.template"; // NOI18N
//    public static final String WSCOMPILE =
//            "bin/wscompile"; // NOI18N
//    
//    public static final String WSDEPLOY_TEMPLATE =
//            "lib/install/templates/wsdeploy.template"; // NOI18N
//    public static final String WSDEPLOY =
//            "bin/wsdeploy"; // NOI18N
//    
//    public static final String UPDATETOOL_TEMPLATE =
//            "updatecenter/lib/install/templates/updatetool.template";//NOI18N
//    public static final String UPDATETOOL =
//            "/updatecenter/bin/updatetool";//NOI18N
//    
//    
//    public static final String CONFIG_HOME_TOKEN =
//            "%CONFIG_HOME%"; // NOI18N
//    public static final String INSTALL_HOME_TOKEN =
//            "%INSTALL_HOME%"; // NOI18N
//    public static final String WEBSERVICES_LIB_TOKEN =
//            "%WEBSERVICES_LIB%"; // NOI18N
//    public static final String JAVA_HOME_TOKEN =
//            "%JAVA_HOME%"; // NOI18N
//    public static final String JAVA_HOME_UNIX_ENV_TOKEN =
//            "$JAVA_HOME"; // NOI18N
//    public static final String ANT_HOME_TOKEN =
//            "%ANT_HOME%"; // NOI18N
//    public static final String ANT_LIB_TOKEN =
//            "%ANT_LIB%"; // NOI18N
//    public static final String NSS_HOME_TOKEN =
//            "%NSS_HOME%"; // NOI18N
//    public static final String NSS_BIN_HOME_TOKEN =
//            "%NSS_BIN_HOME%"; // NOI18N
//    public static final String IMQ_LIB_TOKEN =
//            "%IMQ_LIB%"; // NOI18N
//    public static final String IMQ_BIN_TOKEN =
//            "%IMQ_BIN%"; // NOI18N
//    public static final String JHELP_HOME_TOKEN =
//            "%JHELP_HOME%"; // NOI18N
//    public static final String ICU_LIB_TOKEN =
//            "%ICU_LIB%"; // NOI18N
//    public static final String JATO_LIB_TOKEN =
//            "%JATO_LIB%"; // NOI18N
//    public static final String WEBCONSOLE_LIB_TOKEN =
//            "%WEBCONSOLE_LIB%"; // NOI18N
//    public static final String USE_NATIVE_LAUNCHER_TOKEN =
//            "%USE_NATIVE_LAUNCHER%"; // NOI18N
//    public static final String LAUNCHER_LIB_TOKEN =
//            "%LAUNCHER_LIB%"; // NOI18N
//    public static final String JDMK_HOME_TOKEN =
//            "%JDMK_HOME%"; // NOI18N
//    public static final String LOCALE_TOKEN =
//            "%LOCALE%"; // NOI18N
//    public static final String DEF_DOMAINS_PATH_TOKEN =
//            "%DEF_DOMAINS_PATH%"; // NOI18N
//    public static final String ACC_CONFIG_TOKEN =
//            "%ACC_CONFIG%"; // NOI18N
//    public static final String HADB_HOME_TOKEN =
//            "%HADB_HOME%"; // NOI18N
//    public static final String MFWK_HOME_TOKEN =
//            "%MFWK_HOME%"; // NOI18N    
//    
//    public static final String DERBY_HOME_TOKEN =
//            "%DERBY_HOME%"; // NOI18N
//    public static final String HTTP_PORT_TOKEN =
//            "%HTTP_PORT%"; //NOI18N
//    public static final String ADMIN_PORT_TOKEN =
//            "%ADMIN_PORT%"; //NOI18N
//    public static final String AS_ADMIN_PORT_TOKEN =
//            "%AS_ADMIN_PORT%"; //NOI18N
//    public static final String AS_ADMIN_PROFILE_TOKEN =
//            "%AS_ADMIN_PROFILE%"; //NOI18N
//    public static final String AS_ADMIN_SECURE_TOKEN =
//            "%AS_ADMIN_SECURE%"; //NOI18N
//    
//    public static final String UC_INSTALL_HOME_TOKEN =
//            "@INSTALL_HOME@"; //NOI18N
//    public static final String UC_EXT_LIB_TOKEN =
//            "@EXT_LIB@";       //NOI18N
//    public static final String UC_AS_HOME_TOKEN =
//            "%appserver_home%"; //NOI18N
//    public static final String JDIC_LIB_TOKEN =
//            "@JDIC_LIB@"; //NOI18N
//    public static final String JDIC_STUB_LIB_TOKEN =
//            "@JDIC_STUB_LIB@"; //NOI18N
//    public static final String REGISTRATION_DIR_TOKEN =
//            "@REGISTRATION_DIR@";//NOI18N
//    
//    public static final String JDIC_LIB_WINDOWS =
//            "updatecenter/lib/jdic/windows/x86";//NOI18N
//    public static final String JDIC_LIB_LINUX =
//            "updatecenter/lib/jdic/linux/x86";//NOI18N
//    public static final String JDIC_LIB_SOLARIS_X86 =
//            "updatecenter/lib/jdic/sunos/x86";//NOI18N
//    public static final String JDIC_LIB_SOLARIS_SPARC =
//            "updatecenter/lib/jdic/sunos/sparc";//NOI18N
//    public static final String JDIC_LIB_MACOSX =
//            "updatecenter/lib/jdic/mac/ppc";//NOI18N
//    
//    public static final String JDIC_STUB_LIB_WINDOWS =
//            "updatecenter/lib/jdic/windows";//NOI18N
//    public static final String JDIC_STUB_LIB_LINUX =
//            "updatecenter/lib/jdic/linux";//NOI18N
//    public static final String JDIC_STUB_LIB_SOLARIS =
//            "updatecenter/lib/jdic/sunos";//NOI18N
//    public static final String JDIC_STUB_LIB_MACOSX =
//            "updatecenter/lib/jdic/mac";//NOI18N
//    public static final String REGISTRATION_DIR =
//            "lib/registration";
//    
//    
//    public static final String CONFIG_SUBDIR =
//            "config"; // NOI18N
//    public static final String LIB_SUBDIR =
//            "lib"; // NOI18N
//    public static final String LIB_ANT_SUBDIR =
//            "lib/ant"; // NOI18N
//    public static final String LIB_ANT_LIB_SUBDIR =
//            "lib/ant/lib"; // NOI18N
//    public static final String LIB_ADMINCGI_SUBDIR =
//            "lib/admincgi"; // NOI18N
//    public static final String IMQ_LIB_SUBDIR =
//            "imq/lib"; // NOI18N
//    public static final String IMQ_BIN_SUBDIR =
//            "imq/bin"; // NOI18N
//    public static final String DOMAINS_SUBDIR =
//            "domains"; // NOI18N
//    public static final String DERBY_SUBDIR =
//            "javadb"; // NOI18N
//    public static final String UC_INSTALL_HOME_SUBDIR =
//            "updatecenter"; //NOI18N
//    public static final String UC_BIN_SUBDIR =
//            "updatecenter/bin"; //NOI18N
//    public static final String UC_CONFIG_SUBDIR =
//            "updatecenter/config"; //NOI18N
//    
//    public static final String UC_EXT_LIB =
//            "updatecenter/lib/schema2beans.jar"; //NOI18N
//    
//    public static final String AS_ADMIN_PROFILE =
//            "developer"; //NOI18N
//    public static final String AS_ADMIN_SECURE =
//            "false"; //NOI18N
//    public static final String BIN_SUBDIR =
//            "bin"; // NOI18N
//    
//    public static final String DERBY_LOG =
//            "derby.log"; // NOI18N
//    
//    public static final String HADB_HOME =
//            " "; // NOI18N
//    public static final String MFWK_HOME_WINDOWS =
//            "lib\\SUNWmfwk"; //NOI18N
//    public static final String MFWK_HOME_SOLARIS =
//            "/opt/SUNWmfwk"; //NOI18N
//    public static final String MFWK_HOME_LINUX =
//            "/opt/sun/mfwk/share"; //NOI18N
//    public static final String MFWK_HOME_MACOSX =
//            "/opt/SUNWmfwk"; //NOI18N
//    
//    public static final String USE_NATIVE_LAUNCHER=
//            "false"; // NOI18N
//    public static final String LAUNCHER_LIB =
//            "\\jre\\bin\\client"; // NOI18N
//    public static final String JMDK_HOME =
//            "lib/SUNWjdmk/5.1"; // NOI18N
//    public static final String LOCALE =
//            "en_US"; // NOI18N
//    public static final String ACC_CONFIG =
//            "domains/" + DOMAIN_NAME + "/config/sun-acc.xml"; // NOI18N
//    
//    public static final String DOMAINS_DOMAIN1_IMQ_SUBDIR =
//            "domains/" + DOMAIN_NAME + "/imq"; // NOI18N
//    
//    public static final String IMQENV_CONF_ADDITION_WINDOWS =
//            "        set IMQ_DEFAULT_JAVAHOME={0}\n" + // NOI18N
//            "        set IMQ_DEFAULT_VARHOME={1}\n"; // NOI18N
//    public static final String IMQENV_CONF_ADDITION_UNIX =
//            "        IMQ_DEFAULT_JAVAHOME={0}\n" + // NOI18N
//            "        IMQ_DEFAULT_VARHOME={1}\n"; // NOI18N
//    
//    public static final String IMQENV_CONF =
//            "imq/etc/imqenv.conf"; // NOI18N
//    
//    public static final String JVM_OPTION_NAME =
//            "-Dcom.sun.aas.installRoot"; // NOI18N
//    

    public static final String LOGS_DIR = 
            "logs"; // NOI18N     
  
    public static final String REGISTRY_XML = 
            "registry.xml"; // NOI18N
    public static final String PRODUCT_ID =
            "WEBLOGIC"; // NOI18N
}
