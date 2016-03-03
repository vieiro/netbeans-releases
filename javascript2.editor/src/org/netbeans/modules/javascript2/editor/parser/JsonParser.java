/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development and
 * Distribution License("CDDL") (collectively, the "License"). You may not use
 * this file except in compliance with the License. You can obtain a copy of
 * the License at http://www.netbeans.org/cddl-gplv2.html or
 * nbbuild/licenses/CDDL-GPL-2-CP. See the License for the specific language
 * governing permissions and limitations under the License. When distributing
 * the software, include this License Header Notice in each file and include
 * the License file at nbbuild/licenses/CDDL-GPL-2-CP. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided by
 * Oracle in the GPL Version 2 section of the License file that accompanied
 * this code. If applicable, add the following below the License Header, with
 * the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license." If you do not indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to its
 * licensees as provided above. However, if you add GPL Version 2 code and
 * therefore, elected the GPL Version 2 license, then the option applies only
 * if the new code is made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.javascript2.editor.parser;

import com.oracle.truffle.js.parser.nashorn.internal.ir.FunctionNode;
import org.netbeans.modules.javascript2.editor.api.lexer.JsTokenId;
import org.netbeans.modules.parsing.api.Snapshot;

/**
 *
 * @author Petr Hejl
 */
public class JsonParser extends SanitizingParser {

    public JsonParser() {
        super(JsTokenId.jsonLanguage());
    }

    @Override
    protected String getDefaultScriptName() {
        return "json.json"; // NOI18N
    }

    @Override
    protected FunctionNode parseSource(Snapshot snapshot, String name, String text, int caretOffset, JsErrorManager errorManager, boolean isModule) throws Exception {
        return null;
//        Source source = new Source(name, text);
//        Options options = new Options("nashorn");
//        options.process(new String[] {
//            "--parse-only=true", // NOI18N
//            "--empty-statements=true", // NOI18N
//            "--debug-lines=false"}); // NOI18N
//
//        errorManager.setLimit(0);
//        jdk.nashorn.internal.runtime.Context nashornContext = new jdk.nashorn.internal.runtime.Context(options, errorManager, JsonParser.class.getClassLoader());
//        // XXX
//        //jdk.nashorn.internal.runtime.Context.setContext(nashornContext);
//        jdk.nashorn.internal.codegen.Compiler compiler = jdk.nashorn.internal.codegen.Compiler.compiler(source, nashornContext);
//        jdk.nashorn.internal.parser.JSONParser parser = new jdk.nashorn.internal.parser.JSONParser(source, errorManager, nashornContext._strict);
//
//        Node objectNode = null;
//        try {
//            objectNode = parser.parse();
//        } catch (ParserException ex) {
//            // JSON parser has no recovery
//            errorManager.error(ex);
//        }
//
//        // we are doing this as our infrusture requires function node on top
//        // TODO we may get rid of such dep later
//        FunctionNode node = null;
//        if (objectNode != null) {
//            node = new FunctionNode(source, 0, text.length(), compiler, null, null, "runScript"); // NOI18N
//            node.setKind(FunctionNode.Kind.SCRIPT);
//            node.setStatements(Collections.<Node>singletonList(objectNode));
//            node.setIdent(new IdentNode(source, objectNode.getToken(), 0, node.getName()));
//        }
//        return node;
    }

    @Override
    protected String getMimeType() {
        return JsTokenId.JSON_MIME_TYPE;
    }

}
