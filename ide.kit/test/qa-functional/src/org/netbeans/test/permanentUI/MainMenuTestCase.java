/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2013 Oracle and/or its affiliates. All rights reserved.
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
package org.netbeans.test.permanentUI;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import javax.swing.JMenuItem;
import javax.swing.MenuElement;
import org.netbeans.jellytools.MainWindowOperator;
import org.netbeans.jemmy.operators.JMenuBarOperator;
import org.netbeans.jemmy.operators.Operator;
import org.netbeans.jemmy.operators.Operator.DefaultStringComparator;
import org.netbeans.junit.Manager;
import org.netbeans.test.permanentUI.utils.MenuChecker;
import org.netbeans.test.permanentUI.utils.NbMenuItem;
import org.netbeans.test.permanentUI.utils.Utilities;

/**
 *
 * @author Lukas Hasik, Jan Peska, Marian.Mirilovic@oracle.com
 */
public abstract class MainMenuTestCase extends PermUITestCase {

    /**
     * Need to be defined because of JUnit
     *
     * @param name
     */
    public MainMenuTestCase(String name) {
        super(name);
    }


    //=============================oneMenuTests=================================
    /**
     * @param menuName to be tested
     * @return difference between menuName and golden file with the same name
     */
    void oneMenuTest(String menuName) {
        oneMenuTest(menuName, getGoldenFile("mainmenu", menuName + getContext().getPathSuffix()).getAbsolutePath());
    }

    /**
     * @param menuName to be tested
     * @param goldenFileName to be tested
     * @return difference between menuName and goldenFileName
     *
     * You shouldn't call directly this method.
     */
    private void oneMenuTest(String menuName, String goldenFileName) throws IllegalArgumentException {
        NbMenuItem testedMenu = Utilities.readMenuStructureFromFile(goldenFileName);
        assertNotNull("Nothing read from " + goldenFileName, testedMenu); //was the file read correctly?

        LogFiles logFiles = new LogFiles();
        PrintStream ideFileStream = null;
        PrintStream goldenFileStream = null;

        //filtering separators out from sub-menu
        testedMenu.setSubmenu(removeSeparators(testedMenu));

        try {
            ideFileStream = logFiles.getIdeFileStream();
            goldenFileStream = logFiles.getGoldenFileStream();

            Utilities.printMenuStructure(goldenFileStream, testedMenu, "   ", 1);
            captureScreen();

            NbMenuItem menuItem = getMainMenuItem(menuName);
            Utilities.printMenuStructure(ideFileStream, menuItem, "   ", 1);

            assertNotNull("Cannot find menu " + menuName, menuItem);//is there such menu?

            Manager.getSystemDiff().diff(logFiles.pathToIdeLogFile, logFiles.pathToGoldenLogFile, logFiles.pathToDiffLogFile);
            String message = Utilities.readFileToString(logFiles.pathToDiffLogFile);
            assertFile(message, logFiles.pathToGoldenLogFile, logFiles.pathToIdeLogFile, logFiles.pathToDiffLogFile);
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        } finally {
            ideFileStream.close();
            goldenFileStream.close();
        }
    }

    //===========================oneSubMenuTests================================
    /**
     * Tests submenu items including mnemonics.
     *
     * @param submenuPath Menu name e.g. Window|Projects.
     * @param context context e.g. Java, Php, none
     * @param preInitSubMenu when sub menu doesn't pop up in time, you can try
     * to pre-initialize the sub menu. TRUE = pre-init. FALSE by default. The
     * item HAS TO BE JAVAX.SWING item !!!
     */
    void oneSubMenuTest(String submenuPath, boolean preInitSubMenu) {
        String fileName = submenuPath.replace('|', '-').replace(' ', '_').replace('/','#');
        oneSubMenuTest(submenuPath, getGoldenFile("mainmenu", fileName + getContext().getPathSuffix()).getAbsolutePath(), preInitSubMenu);
    }

    /**
     * Tests submenu items including mnemonics.
     *
     * @param submenuName to be tested
     * @param mainmenuName to be tested
     * @param goldenFileName to be tested
     * @return difference between submenuName and goldenFileName
     */
    private void oneSubMenuTest(String submenuPath, String goldenFileName, boolean preInitSubMenu) throws IllegalArgumentException {
        NbMenuItem testedSubMenuItem = Utilities.readSubmenuStructureFromFile(goldenFileName);
        assertNotNull("Nothing read from " + goldenFileName, testedSubMenuItem); //was the file read correctly?

        // when sub-menu has time out exception problems. It can Helps.
        if (preInitSubMenu) {
            String firstSubMenuItem = testedSubMenuItem.getSubmenu().get(0).getName();
            MainWindowOperator.getDefault().menuBar().showMenuItem(submenuPath + "|" + firstSubMenuItem, new Operator.DefaultStringComparator(true, true));
        }

        LogFiles logFiles = new LogFiles();
        PrintStream ideFileStream = null;
        PrintStream goldenFileStream = null;

        //filtering separators out from sub-menu
        testedSubMenuItem.setSubmenu(removeSeparators(testedSubMenuItem));

        try {
            ideFileStream = logFiles.getIdeFileStream();
            goldenFileStream = logFiles.getGoldenFileStream();

            Utilities.printMenuStructure(goldenFileStream, testedSubMenuItem, "   ", 1);
            captureScreen();

            //TEST 1
            String submenuItems[] = submenuPath.split("\\|");
            assertTrue("submenuPath must be >= 2. - " + submenuPath, submenuItems.length >= 2); //check the size
            //TEST 2
            NbMenuItem mainM = getMainMenuItem(submenuItems[0]);
            NbMenuItem submenuItem = Utilities.getMenuByName(submenuItems[submenuItems.length - 1], mainM);
            assertNotNull("Cannot find submenu " + submenuPath, submenuItem);//is there such submenu?
            //remove the mnemonic of the submenu item because it is not in the perm ui spec too
            submenuItem.setMnemo((char) 0);

            Utilities.printMenuStructure(ideFileStream, submenuItem, "   ", 1);

            //TEST - menu structure
            Manager.getSystemDiff().diff(logFiles.pathToIdeLogFile, logFiles.pathToGoldenLogFile, logFiles.pathToDiffLogFile);
            String message = Utilities.readFileToString(logFiles.pathToDiffLogFile);
            assertFile(message, logFiles.pathToGoldenLogFile, logFiles.pathToIdeLogFile, logFiles.pathToDiffLogFile);
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        } finally {
            ideFileStream.close();
            goldenFileStream.close();
        }
    }

    /**
     * Press menu item.
     *
     * @param mainMenuItem the item.
     * @return Operator.
     */
    protected JMenuBarOperator pushMainMenuItem(String mainMenuItem) {
        ///open menu to let it create sucesfully
        JMenuBarOperator mainmenuOp = MainWindowOperator.getDefault().menuBar();
        ///use string comparator with exact matching
        mainmenuOp.pushMenu(mainMenuItem, new DefaultStringComparator(true, false));

        return mainmenuOp;
    }

    /**
     * Construct path to menu item.
     *
     * @param mainMenuItem item in menu.
     * @return path.
     */
    protected NbMenuItem getMainMenuItem(String mainMenuItem) {

        JMenuBarOperator mainmenuOp = pushMainMenuItem(mainMenuItem);
        //parse all the menu elements
        int position = MenuChecker.getElementPosition(mainMenuItem, mainmenuOp.getSubElements());
        MenuElement theMenuElement = mainmenuOp.getSubElements()[position];
        NbMenuItem theMenu = new NbMenuItem((JMenuItem) theMenuElement);
        theMenu.setSubmenu(MenuChecker.getMenuArrayList(mainmenuOp.getMenu(position)));

        return theMenu;
    }

    protected ArrayList<NbMenuItem> removeSeparators(NbMenuItem item) {
        return Utilities.filterOutSeparators(item.getSubmenu());
    }


}
