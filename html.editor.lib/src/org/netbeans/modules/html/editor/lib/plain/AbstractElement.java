/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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


package org.netbeans.modules.html.editor.lib.plain;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.netbeans.modules.html.editor.lib.api.ProblemDescription;
import org.netbeans.modules.html.editor.lib.api.elements.Element;

/**
 * 
 * @author  mfukala@netbeans.org
 */
public abstract class AbstractElement implements Element {
    
    private CharSequence source;
    private List<ProblemDescription> problems;
    
    private int offset;
    private int length;
    
    AbstractElement( CharSequence doc, int offset, int length) {
        assert offset >=0 : "start offset must be >= 0 !";
        assert length >=0 : "element length must be positive!";

        this.offset = offset;
        this.length = length;
        this.source = doc;
    }

    @Override
    public int from() {
        return offset;
    }

    @Override
    public int to() {
        return offset + length;
    }
    
    @Override
    public CharSequence image() {
        return source.subSequence(from(), to());
    }

    @Override
    public Collection<ProblemDescription> problems() {
        return problems;
    }
    
    public void addProblem(ProblemDescription problem) {
        assert problem != null;
        if(problems == null) {
            problems = Collections.singletonList(problem); //save some memory for just one problem per element
        } else {
            if(problems.size() == 1) {
                ProblemDescription existing = problems.get(0);
                problems = new ArrayList<ProblemDescription>();
                problems.add(existing);
            }
            problems.add(problem);
        }
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("SyntaxElement[")
                .append(type().name())
                .append(' ')
                .append(from())
                .append('-')
                .append(to())
                .append(']')
                .toString(); //NOI18N
    }

}
