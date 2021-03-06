/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2016 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2016 Sun Microsystems, Inc.
 */
package org.netbeans.modules.java.api.common.queries;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.event.ChangeListener;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.spi.java.queries.CompilerOptionsQueryImplementation;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.openide.filesystems.FileObject;
import org.openide.util.ChangeSupport;
import org.openide.util.Parameters;

/**
 *
 * @author Tomas Zezula
 */
final class CompilerOptionsQueryImpl implements CompilerOptionsQueryImplementation {

    private final PropertyEvaluator eval;
    private final String additionalCompilerOptionsProperty;
    private final AtomicReference<Result> result;

    CompilerOptionsQueryImpl(
            @NonNull final PropertyEvaluator eval,
            @NonNull final String additionalCompilerOptionsProperty) {
        Parameters.notNull("eval", eval);   //NOI18N
        Parameters.notNull("additionalCompilerOptionsProperty", additionalCompilerOptionsProperty); //NOI18N
        this.eval = eval;
        this.additionalCompilerOptionsProperty = additionalCompilerOptionsProperty;
        this.result = new AtomicReference<>();
    }

    @Override
    @CheckForNull
    public Result getOptions(FileObject file) {
        Result res = result.get();
        if (res == null) {
            res = new ResultImpl(eval, additionalCompilerOptionsProperty);
            if (!result.compareAndSet(null, res)) {
                res = result.get();
            }
            assert res != null;
        }
        return res;
    }

    private static final class ResultImpl extends Result implements PropertyChangeListener {

        private final PropertyEvaluator eval;
        private final String additionalCompilerOptionsProperty;
        private final ChangeSupport listeners;
        private volatile List<String> cache;

        ResultImpl(
                @NonNull final PropertyEvaluator eval,
                @NonNull final String additionalCompilerOptionsProperty) {
            this.eval = eval;
            this.additionalCompilerOptionsProperty = additionalCompilerOptionsProperty;
            this.listeners = new ChangeSupport(this);
            this.eval.addPropertyChangeListener(this);
        }

        @Override
        @NonNull
        public List<? extends String> getArguments() {
            List<String> res = cache;
            if (res == null) {
                final String additionalCompilerOptions = eval.getProperty(additionalCompilerOptionsProperty);
                res = additionalCompilerOptions == null || additionalCompilerOptions.isEmpty() ?
                        Collections.emptyList() :
                        parseLine(additionalCompilerOptions);
                cache = res;
            }
            return res;
        }

        @Override
        public void addChangeListener(@NonNull final ChangeListener listener) {
            Parameters.notNull("listener", listener);   //NOI18N
            listeners.addChangeListener(listener);
        }

        @Override
        public void removeChangeListener(@NonNull final ChangeListener listener) {
            Parameters.notNull("listener", listener);   //NOI18N
            listeners.removeChangeListener(listener);
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            final String propName = evt.getPropertyName();
            if (propName == null || additionalCompilerOptionsProperty.equals(propName)) {
                cache = null;
                listeners.fireChange();
            }
        }

    }

}
