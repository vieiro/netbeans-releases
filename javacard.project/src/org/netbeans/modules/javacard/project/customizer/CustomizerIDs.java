/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2009 Sun
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
package org.netbeans.modules.javacard.project.customizer;

import org.openide.util.NbBundle;

enum CustomizerIDs {
    CUSTOMIZER_ID_DEPENDENCIES("Dependencies"), //NOI18N
    CUSTOMIZER_ID_PACKAGING("Packaging"), //NOI18N
    CUSTOMIZER_ID_RUN("Run"), //NOI18N
    CUSTOMIZER_ID_SOURCES("Sources"), //NOI18N
    CUSTOMIZER_ID_WEB("Web"), //NOI18N
    CUSTOMIZER_ID_APPLET("Applets"), //NOI18N
    CUSTOMIZER_ID_COMPILING("Compiling"), //NOI18N
    CUSTOMIZER_ID_SECURITY("Security"), //NOI18N
    ;
    private final String fileName;
    private CustomizerIDs(String fileName) {
        this.fileName = fileName;
    }

    public String getDisplayName() {
        return NbBundle.getMessage (CustomizerIDs.class, fileName);
    }

    public static CustomizerIDs forFileName (String name) {
        for (CustomizerIDs id : values()) {
            if (name.equals(id.fileName)) {
                return id;
            }
        }
        throw new AssertionError ("Unknown customizer name '" + name + "'"); //NOI18N
    }
}
