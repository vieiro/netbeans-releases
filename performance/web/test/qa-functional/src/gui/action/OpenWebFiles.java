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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package gui.action;
import org.netbeans.jellytools.EditorOperator;
import org.netbeans.jellytools.ProjectsTabOperator;
import org.netbeans.jellytools.nodes.Node;

import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JPopupMenuOperator;
import org.netbeans.test.web.performance.WebPerformanceTestCase;

/**
 * Test of opening files.
 *
 * @author  mmirilovic@netbeans.org
 */
public class OpenWebFiles extends WebPerformanceTestCase {
    
    /** Node to be opened/edited */
    public static Node openNode ;
    
    /** Folder with data */
    public static String fileProject;
 
    /** Folder with data  */
    public static String fileFolder;
    
    /** Name of file to open */
    public static String fileName;
    
    /** Menu item name that opens the editor */
    public static String menuItem;
    
    protected static String OPEN = "Open"; //NOI18N
    
    protected static String EDIT = "Edit"; //NOI18N
    
    protected static String WEB_PAGES = "Web Pages"; //NOI18N
    
    private boolean wrappedEditorOperator = false;
    
    /**
     * Creates a new instance of OpenFiles
     * @param testName the name of the test
     */
    public OpenWebFiles(String testName) {
        super(testName);
        expectedTime = WINDOW_OPEN;
    }
    
    /**
     * Creates a new instance of OpenFiles
     * @param testName the name of the test
     * @param performanceDataName measured values will be saved under this name
     */
    public OpenWebFiles(String testName, String performanceDataName) {
        super(testName, performanceDataName);
        expectedTime = WINDOW_OPEN;
    }
    
    public void testOpeningWebXmlFile(){
        WAIT_AFTER_OPEN = 1000;
        //repaintManager().setOnlyEditor(false);
//        repaintManager().resetRegionFilters();
//        repaintManager().addRegionFilter(repaintManager().EDITOR_FILTER);
        setXMLEditorCaretFilteringOn();
        fileProject = "TestWebProject";
        fileFolder = "WEB-INF"; 
        fileName = "web.xml";
        menuItem = EDIT;
        setWrappedClonableEditor(true);
        doMeasurement();
        setWrappedClonableEditor(false);
    }

    public void testOpeningContextXmlFile(){
        WAIT_AFTER_OPEN = 1000;
        //repaintManager().setOnlyEditor(true);
//        repaintManager().addRegionFilter(repaintManager().EDITOR_FILTER);
        setXMLEditorCaretFilteringOn();
        fileProject = "TestWebProject";
        fileFolder = "META-INF"; 
        fileName = "context.xml";
        menuItem = EDIT;
        doMeasurement();
    }    

    public void testOpeningJSPFile(){
        WAIT_AFTER_OPEN = 1000;
        //repaintManager().setOnlyEditor(true);
//        repaintManager().addRegionFilter(repaintManager().EDITOR_FILTER);
        setXMLEditorCaretFilteringOn();
        fileProject = "TestWebProject";
        fileFolder = "";
        fileName = "Test.jsp";
        menuItem = OPEN;
        doMeasurement();
    }

    public void testOpeningBigJSPFile(){
        WAIT_AFTER_OPEN = 1000;
       //repaintManager().setOnlyEditor(true);
//        repaintManager().addRegionFilter(repaintManager().EDITOR_FILTER);
        setXMLEditorCaretFilteringOn();
        fileProject = "TestWebProject";
        fileFolder = "";
        fileName = "BigJSP.jsp";
        menuItem = OPEN;
        doMeasurement();
    }
    
    public void testOpeningHTMLFile(){
        WAIT_AFTER_OPEN = 1000;
        //repaintManager().setOnlyEditor(true);
//        repaintManager().addRegionFilter(repaintManager().EDITOR_FILTER);
        setXMLEditorCaretFilteringOn();
        fileProject = "TestWebProject";
        fileFolder = "";
        fileName = "HTML.html";
        menuItem = OPEN;
        doMeasurement();
    }

    public void testOpeningTagFile(){
        WAIT_AFTER_OPEN = 1000;
        //repaintManager().setOnlyEditor(true);
//        repaintManager().addRegionFilter(repaintManager().EDITOR_FILTER);
        setXMLEditorCaretFilteringOn();
        fileProject = "TestWebProject";
        fileFolder = "WEB-INF|tags"; 
        fileName = "mytag.tag";
        menuItem = OPEN;
        doMeasurement();
    }

    public void testOpeningTldFile(){
        WAIT_AFTER_OPEN = 1000;
       //repaintManager().setOnlyEditor(true);
//       repaintManager().addRegionFilter(repaintManager().EDITOR_FILTER);
        setXMLEditorCaretFilteringOn();
        fileProject = "TestWebProject";
        fileFolder = "WEB-INF"; 
        fileName = "MyTLD.tld";
        menuItem = OPEN;
        doMeasurement();
    }
    
    public void initialize(){
        EditorOperator.closeDiscardAll();
    }

    public void shutdown(){
        EditorOperator.closeDiscardAll();
        //repaintManager().setOnlyEditor(false);
        repaintManager().resetRegionFilters();
        
    }
    
    public void prepare(){
        System.out.println("PREPARE: "+WEB_PAGES + (fileFolder.equals("")?"":"|") + fileFolder + '|' + fileName);
        this.openNode = new Node(new ProjectsTabOperator().getProjectRootNode(fileProject),WEB_PAGES + (fileFolder.equals("")?"":"|") + fileFolder + '|' + fileName);
        
        if (this.openNode == null) {
            fail ("Cannot find node ["+WEB_PAGES  + (fileFolder.equals("")?"":"|") + fileFolder + '|' + fileName + "] in project [" + fileProject + "]");
        }
        log("========== Open file path ="+this.openNode.getPath());
    }
    
    public ComponentOperator open(){
        JPopupMenuOperator popup =  this.openNode.callPopup();
        if (popup == null) {
            fail ("Cannot get context menu for node ["+WEB_PAGES + (fileFolder.equals("")?"":"|") + fileFolder + '|' + fileName + "] in project [" + fileProject + "]");
        }
        log("------------------------- after popup invocation ------------");
        try {
            repaintManager().addRegionFilter(repaintManager().EDITOR_FILTER);
            popup.pushMenu(this.menuItem);
        }
        catch (org.netbeans.jemmy.TimeoutExpiredException tee) {
            fail ("Cannot push menu item "+this.menuItem+" of node ["+WEB_PAGES  + (fileFolder.equals("")?"":"|") + fileFolder + '|' + fileName + "] in project [" + fileProject + "]");
        }
        log("------------------------- after open ------------");
//        // XXX wrappedEditorOperator hack for web.xml editor
//        if (wrappedEditorOperator) {
//            return new TopComponentOperator(this.fileName);
//        } else {
        return null;
//        }
    }
    
    public void close(){
        EditorOperator edior = new EditorOperator(this.fileName);
        if (edior == null) {
            edior.closeDiscard();
        }
//        else {
//            fail ("no component to close");
//        }
        //repaintManager().setOnlyEditor(false);
        repaintManager().resetRegionFilters();
        
    }
    
    private void setWrappedClonableEditor(boolean value) {
        wrappedEditorOperator = value;
    }
    
}
