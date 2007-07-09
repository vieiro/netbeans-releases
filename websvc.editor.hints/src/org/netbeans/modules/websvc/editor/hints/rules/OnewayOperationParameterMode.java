/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.websvc.editor.hints.rules;

import com.sun.source.tree.Tree;
import javax.jws.WebParam.Mode;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.Fix;
import org.openide.util.NbBundle;

import org.netbeans.modules.websvc.editor.hints.common.ProblemContext;
import org.netbeans.modules.websvc.editor.hints.common.Rule;
import org.netbeans.modules.websvc.editor.hints.common.Utilities;
import org.netbeans.modules.websvc.editor.hints.fixes.RemoveAnnotation;

/**
 * @author Ajit.Bhate@Sun.COM
 */
public class OnewayOperationParameterMode extends Rule<ExecutableElement> implements WebServiceAnnotations {

    /** Creates a new instance of OnewayOperationParameterMode */
    public OnewayOperationParameterMode() {
    }

    @Override
    public ErrorDescription[] apply(ExecutableElement subject, ProblemContext ctx) {
        AnnotationMirror annEntity = Utilities.findAnnotation(subject, ANNOTATION_ONEWAY);
        Tree problemTree = ctx.getCompilationInfo().getTrees().getTree(subject, annEntity);
        for (VariableElement param : subject.getParameters()) {
            AnnotationMirror paramAnn = Utilities.findAnnotation(param, ANNOTATION_WEBPARAM);
            if (paramAnn != null) {
                AnnotationValue val = Utilities.getAnnotationAttrValue(paramAnn, ANNOTATION_ATTRIBUTE_MODE);
                Mode value = val == null ? null : Mode.valueOf(val.getValue().toString());
                if (Mode.INOUT == value || Mode.OUT == value) {
                    String label = NbBundle.getMessage(OnewayOperationParameterMode.class, "MSG_OnewayNotAllowed_HasOutParams");
                    Fix removeFix = new RemoveAnnotation(ctx.getFileObject(), subject, annEntity);
                    ctx.setElementToAnnotate(problemTree);
                    ErrorDescription problem = createProblem(subject, ctx, label, removeFix);
                    ctx.setElementToAnnotate(null);
                    return new ErrorDescription[]{problem};
                }
            }
        }
        return null;
    }

    protected final boolean isApplicable(ExecutableElement subject, ProblemContext ctx) {
        return Utilities.hasAnnotation(subject, ANNOTATION_ONEWAY);
    }
}
