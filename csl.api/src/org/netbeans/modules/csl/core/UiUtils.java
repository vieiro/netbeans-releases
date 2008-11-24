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
package org.netbeans.modules.csl.core;

import org.netbeans.modules.csl.api.Phase;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.SwingUtilities;
import javax.swing.text.StyledDocument;
import org.netbeans.modules.csl.api.DeclarationFinder.DeclarationLocation;
import org.netbeans.modules.csl.api.ElementHandle;
import org.netbeans.modules.csl.api.ElementKind;
import org.netbeans.modules.csl.api.Modifier;
import org.netbeans.modules.csl.navigation.Icons;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.ParserManager;
import org.netbeans.modules.parsing.api.ResultIterator;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.api.UserTask;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.parsing.spi.Parser;
import org.openide.ErrorManager;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.LineCookie;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.text.Line;
import org.openide.text.NbDocument;


/** 
 * This file is originally from Retouche, the Java Support 
 * infrastructure in NetBeans. I have modified the file as little
 * as possible to make merging Retouche fixes back as simple as
 * possible. 
 *
 * This class contains various methods bound to visualization of Java model
 * elements. It was formerly included under SourceUtils
 *
 * XXX - needs cleanup
 *
 * @author Jan Lahoda
 * @author Tor Norbye
 */
public final class UiUtils {

    private static final Logger LOG = Logger.getLogger(UiUtils.class.getName());
    
    private UiUtils() {
    }

    /** Gets correct icon for given ElementKind.
     *@param modifiers Can be null for empty modifiers collection
     */
    public static Icon getElementIcon( ElementKind elementKind, Collection<Modifier> modifiers ) {
        return Icons.getElementIcon(elementKind, modifiers);
    }

    /**
     * Opens given {@link ComObject}.
     *
     * @param cpInfo fileobject whose {@link ClasspathInfo} will be used
     * @param el    declaration to open
     * @return true if and only if the declaration was correctly opened,
     *                false otherwise
     */
    public static boolean open(Source js, final ElementHandle handle) {
        DeclarationLocation location = getOpenInfo(js, handle);

        if (location != DeclarationLocation.NONE) {
            return doOpen(location.getFileObject(), location.getOffset());
        }

        return false;
    }

    private static DeclarationLocation getOpenInfo(final Source js, final ElementHandle handle) {
        assert js != null;
        assert handle != null; // Only one should be set

        try {
            FileObject fo = js.getFileObject();
            return getElementLocation(fo, handle);
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);

            return DeclarationLocation.NONE;
        }
    }

    public static boolean open(final FileObject fo, final int offset) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    doOpen(fo, offset);
                }
            });
            return true; // not exactly accurate, but....
        }
        
        return doOpen(fo, offset);
    }
    
    // Private methods ---------------------------------------------------------
    private static boolean doOpen(FileObject fo, int offset) {
        try {
            DataObject od = DataObject.find(fo);
            EditorCookie ec = od.getCookie(EditorCookie.class);
            LineCookie lc = od.getCookie(LineCookie.class);

            if ((ec != null) && (lc != null) && (offset != -1)) {
                StyledDocument doc = ec.openDocument();

                if (doc != null) {
                    int line = NbDocument.findLineNumber(doc, offset);
                    int lineOffset = NbDocument.findLineOffset(doc, line);
                    int column = offset - lineOffset;

                    if (line != -1) {
                        Line l = lc.getLineSet().getCurrent(line);

                        if (l != null) {
                            l.show(Line.ShowOpenType.OPEN, Line.ShowVisibilityType.FOCUS, column);

                            return true;
                        }
                    }
                }
            }

            OpenCookie oc = od.getCookie(OpenCookie.class);

            if (oc != null) {
                oc.open();

                return true;
            }
        } catch (IOException e) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
        }

        return false;
    }

    private static DeclarationLocation getElementLocation(final FileObject fo, final ElementHandle handle)
        throws IOException {
        assert handle != null;
        final DeclarationLocation[] result = new DeclarationLocation[] { DeclarationLocation.NONE };

        Source js = Source.create(fo);
        try {
            ParserManager.parse(Collections.singleton(js), new UserTask() {
                public void run(ResultIterator resultIterator) throws ParseException {
                    Parser.Result r = resultIterator.getParserResult();
                    if (!(r instanceof ParserResult)) {
                        return;
                    }

                    ParserResult info = (ParserResult) r;
                    info.toPhase(Phase.RESOLVED);

                    FileObject fileObject;
                    if (handle != null) {
                        fileObject = handle.getFileObject();
                    } else {
                        fileObject = fo;
                    }

                    if (fileObject == fo) {
// XXX: parsingapi
//                        Language language = LanguageRegistry.getInstance().getLanguageByMimeType(handle.getMimeType());
//                        Parser parser = language.getParser();
//                        //ParserResult pr = handle.getResult();
//                        //ElementHandle file = pr.getRoot();
//                        //if (file != null) {
//                            try {
//                                OffsetRange range = parser.getPositionManager().getOffsetRange(info, handle);
//
//                                if (range != OffsetRange.NONE && range != null) {
//                                    result[0] = new DeclarationLocation(fileObject, range.getStart());
//                                }
//                            } catch (IllegalArgumentException iae) {
//                                result[0] = new DeclarationLocation(fileObject, 0);
//                            }
//                        //}
                        result[0] = new DeclarationLocation(fileObject, -1);
                    } else {
                        // The element is not in the parse tree for this parse job; it is
                        // probably something like an indexed element
                        result[0] = new DeclarationLocation(fileObject, -1);
                    }
                }
            });
        } catch (ParseException e) {
            LOG.log(Level.WARNING, null, e);
        }

        return result[0];
    }
}
