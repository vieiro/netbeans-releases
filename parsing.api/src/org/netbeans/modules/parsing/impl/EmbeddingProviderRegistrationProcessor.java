/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.parsing.impl;

import java.util.Collections;
import java.util.Set;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import org.netbeans.modules.parsing.spi.EmbeddingProvider;
import org.netbeans.modules.parsing.spi.TaskFactory;
import org.openide.filesystems.annotations.LayerGeneratingProcessor;
import org.openide.filesystems.annotations.LayerGenerationException;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Zezula
 */
@ServiceProvider(service=Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class EmbeddingProviderRegistrationProcessor extends LayerGeneratingProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(EmbeddingProvider.Registration.class.getCanonicalName());
    }

    @Override
    protected boolean handleProcess(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) throws LayerGenerationException {
        for (Element e :  roundEnv.getElementsAnnotatedWith(EmbeddingProvider.Registration.class)) {
            if (!e.getKind().isClass()) {
                throw new LayerGenerationException("Annotated Element has to be a class.", e);  //NOI18N
            }
            final EmbeddingProvider.Registration reg = e.getAnnotation(EmbeddingProvider.Registration.class);
            String mimeType = reg.mimeType();
            if (mimeType == null) {
                throw new LayerGenerationException("Mime type has to be given.", e);  //NOI18N
            } else if (!mimeType.isEmpty()) {
                mimeType =  '/' + mimeType; //NOI18N
            }
            String targetMimeType = reg.targetMimeType();
            if (targetMimeType == null || targetMimeType.isEmpty()) {
                throw new LayerGenerationException("Target mime type has to be given.", e);  //NOI18N
            }
            layer(e).
                instanceFile("Editors" + mimeType, null, null).    //NOI18N
                stringvalue("instanceOf", TaskFactory.class.getName()).         ///NOI18N
                methodvalue("instanceCreate", EmbeddingProviderFactory.class.getName(), "create").         //NOI18N
                stringvalue(EmbeddingProviderFactory.ATTR_TARGET_MIME_TYPE, targetMimeType).
                instanceAttribute(EmbeddingProviderFactory.ATTR_PROVIDER, EmbeddingProvider.class).
                write();
        }
        return true;
    }

}
