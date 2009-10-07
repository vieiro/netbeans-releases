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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.gsf.api;

import java.util.List;
import org.openide.filesystems.FileObject;

/**
 * Wrapper around org.netbeans.spi.editor.hints.ErrorDescription
 *
 * @author Tor Norbye
 */
public class Hint {
    private final String description;
    private final List<HintFix> fixes;
    private final FileObject file;
    private final OffsetRange range;
    private final Rule rule;
    private int priority;
    
    public Hint(Rule rule, String description, FileObject file, OffsetRange range, List<HintFix> fixes, int priority) {
        this.rule = rule;
        this.description = description;
        this.file = file;
        this.range = range;
        this.fixes = fixes;
        this.priority = priority;
    }
    
    public Rule getRule() {
        return this.rule;
    }

    public String getDescription() {
        return description;
    }

    public FileObject getFile() {
        return file;
    }

    public List<HintFix> getFixes() {
        return fixes;
    }

    public OffsetRange getRange() {
        return range;
    }
    
    public int getPriority() {
        return priority;
    }
    
    @Override
    public String toString() {
        return "Description(desc=" + description + ",fixes=" + fixes + ")";
    }
}
