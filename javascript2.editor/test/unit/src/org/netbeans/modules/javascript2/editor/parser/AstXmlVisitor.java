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
package org.netbeans.modules.javascript2.editor.parser;

import com.oracle.truffle.js.parser.nashorn.internal.ir.AccessNode;
import com.oracle.truffle.js.parser.nashorn.internal.ir.BaseNode;
import com.oracle.truffle.js.parser.nashorn.internal.ir.BinaryNode;
import com.oracle.truffle.js.parser.nashorn.internal.ir.Block;
import com.oracle.truffle.js.parser.nashorn.internal.ir.CallNode;
import com.oracle.truffle.js.parser.nashorn.internal.ir.ClassNode;
import com.oracle.truffle.js.parser.nashorn.internal.ir.Expression;
import com.oracle.truffle.js.parser.nashorn.internal.ir.FunctionNode;
import com.oracle.truffle.js.parser.nashorn.internal.ir.IdentNode;
import com.oracle.truffle.js.parser.nashorn.internal.ir.IndexNode;
import com.oracle.truffle.js.parser.nashorn.internal.ir.LexicalContext;
import com.oracle.truffle.js.parser.nashorn.internal.ir.LiteralNode;
import com.oracle.truffle.js.parser.nashorn.internal.ir.Node;
import com.oracle.truffle.js.parser.nashorn.internal.ir.ObjectNode;
import com.oracle.truffle.js.parser.nashorn.internal.ir.PropertyNode;
import com.oracle.truffle.js.parser.nashorn.internal.ir.Symbol;
import com.oracle.truffle.js.parser.nashorn.internal.ir.UnaryNode;
import com.oracle.truffle.js.parser.nashorn.internal.ir.VarNode;
import com.oracle.truffle.js.parser.nashorn.internal.ir.visitor.NodeVisitor;
import com.oracle.truffle.js.parser.nashorn.internal.parser.Token;
import java.util.List;

/**
 *
 * @author Petr Pisl
 */
public class AstXmlVisitor extends NodeVisitor {

    private StringBuilder sb;
    private int indent;

    public AstXmlVisitor(LexicalContext lc) {
        super(lc);
        this.sb = new StringBuilder();
        this.indent = 0;
    }

    public String getXmTree() {
        return sb.toString();
    }

    private void createOpenTag(Node node) {
        String indentation = createSpaces();
        sb.append(indentation).append('<');
        sb.append(getNodeName(node));
        appendOffsetInfo(node);
        sb.append(">\n");
        increaseIndent();
    }

    private void createOpenTag(Node node, String... attributes) {
        String indentation = createSpaces();
        sb.append(indentation).append('<');
        sb.append(getNodeName(node));
        if (attributes != null && attributes.length > 0) {
            for (int i = 0; i < attributes.length; i++) {
                if (attributes[i] != null) {
                    if (attributes[i].charAt(0) != ' ') {
                        sb.append(' ');
                    }
                    sb.append(attributes[i]);
                }
            }
        }
        appendOffsetInfo(node);
        sb.append(">\n");
        increaseIndent();
    }

    private void createCloseTag(Node node) {
        decreaseIndent();
        String indentation = createSpaces();
        sb.append(indentation).append("</").append(getNodeName(node)).append(">\n");
    }

    private void createOpenCloseTag(Node node, String attributes) {
        String indentation = createSpaces();
        sb.append(indentation).append('<');
        sb.append(getNodeName(node));
        if (attributes != null && !attributes.isEmpty()) {
            if (attributes.charAt(0) != ' ') {
                sb.append(' ');
            }
            sb.append(attributes);
        }
        appendOffsetInfo(node);
        sb.append("/>\n");
    }

    private String createTagAttribute(String name, String value){
        if (value == null) {
            return null;
        }
        return name + "='" + value.trim() + "'";
    }
    
    private void createComment(String comment) {
        String indentation = createSpaces();
        sb.append(indentation).append("<!-- ").append(comment).append(" -->\n");
    }

    private String getNodeName(Node node) {
        String canonicalName = node.getClass().getCanonicalName();
        String name = canonicalName.substring(canonicalName.lastIndexOf('.') + 1);
        return name;
    }

    private void increaseIndent() {
        indent += 2;
    }

    private void decreaseIndent() {
        indent -= 2;
    }

    private String createSpaces() {
        StringBuilder spaces = new StringBuilder(indent);
        for (int i = 0; i < indent; i++) {
            sb.append(' ');
        }
        return spaces.toString();
    }

    private void appendOffsetInfo(Node node) {
        if(node instanceof FunctionNode) {
            appendOffsetInfo((FunctionNode)node);
            return;
        }
        sb.append(" start='").append(node.getStart()).append('\'');
        sb.append(" end='").append(node.getFinish()).append('\'');
    }
    
    private void appendOffsetInfo(FunctionNode node) {
        sb.append(" start='").append(Token.descPosition(node.getFirstToken())).append('\'');
        sb.append(" end='").append(Token.descPosition(node.getLastToken()) + Token.descLength(node.getLastToken())).append('\'');
    }

    
    private boolean processAttribute(final boolean add, final String name) {
        if (add) {
            String indentation = createSpaces();
            sb.append(indentation).append('<').append(name).append("/>\n");
        }
        return add;
    }

    private void createSimpleTag(final String tagName, final String value) {
        String indentation = createSpaces();
        sb.append(indentation).append('<').append(tagName).append('>');
        sb.append(value);
        sb.append(indentation).append("</").append(tagName).append(">\n");
    }

    private void processWithComment(Node node, String comment) {
        if (node != null) {
            createComment(comment);
            node.accept(this);
        }
    }

    private void processWithComment(List<? extends Node> nodes, String comment) {
        if (nodes != null) {
            createComment(comment);
            for (Node node : nodes) {
                node.accept(this);
            }
        }
    }

    @Override
    protected boolean enterDefault(Node node) {
        createOpenTag(node);
        return super.enterDefault(node);
    }

    @Override
    protected Node leaveDefault(Node node) {
        createCloseTag(node);
        return super.leaveDefault(node);
    }

    private void processAttribute(BaseNode node) {
        processAttribute(node.isFunction(), "isFunction");
        processAttribute(node.isIndex(), "isIndex");
        processAttribute(node.isSuper(), "isSuper");
        processAttribute((Expression)node);
    }
    
    private void processAttribute(Block node) {
         processAttribute(node.isBreakableWithoutLabel(), "isBreakableWithoutLabel");
        processAttribute(node.isCatchBlock(), "isCatchBlock");
        processAttribute(node.isFunctionBody(), "isFunctionBody");
        processAttribute(node.isGlobalScope(), "isGlobalSpace");
        processAttribute(node.isParameterBlock(), "isParameterBlock");
        processAttribute(node.isSynthetic(), "isSynthetic");
        processAttribute(node.isTerminal(), "isTerminal");
        processAttribute((Node)node);
    }
    
    private void processAttribute(Expression node) {
        processAttribute(node.isAlwaysFalse(), "isAlwaysFalse");
        processAttribute(node.isAlwaysTrue(), "isAlwaysTrue");
        processAttribute(node.isOptimistic(), "isOptimistic");
        processAttribute(node.isSelfModifying(), "isSelfModifying");
        processAttribute((Node)node);
    }
    
    private void processAttribute(Node node) {
        processAttribute(node.isAssignment(), "isAssignment");
        processAttribute(node.isLoop(), "isLoop");
    }
    
    @Override
    public boolean enterAccessNode(AccessNode node) {
        createOpenTag(node,
                createTagAttribute("property", node.getProperty()));
        
        processAttribute(node);
        
        processWithComment(node.getBase(), "AccessNode Base");
        createCloseTag(node);
        return false;
    }

    @Override
    public boolean enterBinaryNode(BinaryNode node) {
        createOpenTag(node,
                createTagAttribute("type", node.tokenType().name()));
        processAttribute(node.isAssignment(), "isAssignment");
        processAttribute(node.isComparison(), "isComparison");
        processAttribute(node.isLogical(), "isLogical");
        processAttribute(node.isRelational(), "isRelational");
        processAttribute(node.isSelfModifying(), "isSelfModifying");
//        processWithComment(node.getAssignmentDest(), "BinaryNode AssignmentDest");
//        processWithComment(node.getAssignmentSource(), "BinaryNode AssignmentSource");
        processWithComment(node.lhs(), "BinaryNode lhs");
        processWithComment(node.rhs(), "BinaryNode rhs");
        createCloseTag(node);
        return false;
    }

    @Override
    public boolean enterBlock(Block node) {
        createOpenTag(node);
        
        processAttribute(node);
        processWithComment(node.getStatements(), "Block Statements");
       
        createCloseTag(node);
        return false;
    }
    
    @Override
    public boolean enterClassNode(ClassNode node) {
        createOpenTag(node, 
                node.getIdent() != null ? createTagAttribute("ident", node.getIdent().getName()) : null);
        
        processAttribute(node);
        processWithComment(node.getClassHeritage(), "ClassNode Heritage");
        processWithComment(node.getConstructor(), "ClassNode Constructor");
        processWithComment(node.getClassElements(), "ClassNode Elements");
        createCloseTag(node);
        return false;
    }

    @Override
    public boolean enterCallNode(CallNode node) {
        createOpenTag(node);
        
        processAttribute(node.isApplyToCall(), "isApplayToCall");
        processAttribute(node.isEval(), "isEval");
        processAttribute(node.isNew(), "isNew");
        processAttribute(node);
        
        processWithComment(node.getArgs(), "CallNode Arguments");
        processWithComment(node.getFunction(), "CallNode Function");
        createCloseTag(node);
        return false;
    }
    
    

    @Override
    public boolean enterFunctionNode(FunctionNode node) {
        createOpenTag(node,
                createTagAttribute("name", node.getName()),
                createTagAttribute("kind", node.getKind().name()));
        
        processAttribute(node.hasDeclaredFunctions(), "hasDeclaredFunctions");
        processAttribute(node.hasScopeBlock(), "hasScopeBlock");
        processAttribute(node.inDynamicContext(), "isDynamicContext");
        processAttribute(node.isAnonymous(), "isAnonymous");
        processAttribute(node.isClassConstructor(), "isClassConstructor");
        processAttribute(node.isDeclared(), "isDeclared");
        processAttribute(node.isMethod(), "isMethod");
        processAttribute(node.isProgram(), "isProgram");
        processAttribute(node.isNamedFunctionExpression(), "isNamedFunctionExpression");
        processAttribute(node.isSubclassConstructor(), "isSubclassConstructor");
        processAttribute(node.isVarArg(), "isVarArg");
        processAttribute(node);
        
        processWithComment(node.getParameters(), "FunctionNode Parameters");
        processWithComment(node.getBody(), "FunctionNode Body");
        createCloseTag(node);
        return false;
    }

    @Override
    public boolean enterIdentNode(IdentNode node) {
        createOpenTag(node);
        createSimpleTag("name", node.getName());
        if (node.getPropertyName() != null && !node.getName().equals(node.getPropertyName())) {
            createSimpleTag("propertyName", node.getPropertyName());
        }

        processAttribute(node.isDeclaredHere(), "isDeclaredHere");
        processAttribute(node.isDefaultParameter(), "isDefaultParameter");
        processAttribute(node.isDestructuredParameter(), "isDestructuredParameter");
        processAttribute(node.isDirectSuper(), "isDirectSuppert");
        processAttribute(node.isFunction(), "isFunction");
        processAttribute(node.isFutureStrictName(), "isFutureStrictName");
        processAttribute(node.isInitializedHere(), "isInitializedHere");
        processAttribute(node.isInternal(), "isInternal");
        processAttribute(node.isPropertyName(), "isPropertyName");
        processAttribute(node.isProtoPropertyName(), "isProtoPropertyName");
        processAttribute(node.isRestParameter(), "isRestParameter");
        processAttribute(node);
        createCloseTag(node);
        return false;
    }

    @Override
    public boolean enterIndexNode(IndexNode node) {
        createOpenTag(node);
        
        processAttribute(node);
        
        processWithComment(node.getBase(), "IndexNode Base");
        processWithComment(node.getIndex(), "IndexNode Index");
        createCloseTag(node);
        return false;
    }
    
    

    @Override
    public boolean enterLiteralNode(LiteralNode node) {
        if (!(node instanceof LiteralNode.ArrayLiteralNode)) {
            createOpenCloseTag(node, 
                    node.getValue() != null ? createTagAttribute("value", node.getValue().toString()) : null);
            return false;
        }

        return super.enterLiteralNode(node);
    }

    
            
    @Override
    public boolean enterObjectNode(ObjectNode node) {
        createOpenTag(node);
        processAttribute(node);
        processWithComment(node.getElements(), "ObjectNode Elements");
        createCloseTag(node);
        return false;
    }

    
    @Override
    public boolean enterPropertyNode(PropertyNode node) {
        createOpenTag(node, createTagAttribute("name", node.getKeyName()));
        processAttribute(node.isComputed(), "isComputed");
        processAttribute(node.isStatic(), "isStatic");
        processAttribute(node);
        
        processWithComment(node.getKey(), "PropertyNode Key");
        processWithComment(node.getValue(), "PropertyNode Value");
        processWithComment(node.getGetter(), "PropertyNode Getter");
        processWithComment(node.getSetter(), "PropertyNode Setter");
        createCloseTag(node);
        return false;
    }

    @Override
    public boolean enterUnaryNode(UnaryNode node) {
        createOpenTag(node, createTagAttribute("type", node.tokenType().name()));

        processAttribute(node.isAssignment(), "isAssignment");
        if (node.getExpression() != node.getAssignmentDest()) {
            processWithComment(node.getAssignmentDest(), "UnaryNode AssignmentDest");
        }
        if (node.getExpression() != node.getAssignmentSource()) {
            processWithComment(node.getAssignmentSource(), "UnaryNode AssignmentSource");
        }
        processWithComment(node.getExpression(), "UnaryNode Expression");
        
        createCloseTag(node);
        return false;
    }

    
    
    @Override
    public boolean enterVarNode(VarNode node) {
        createOpenTag(node, createTagAttribute("name", node.getName().getName()));
        processAttribute(node.hasInit(), "hasInit");
        processAttribute(node.isAssignment(), "isAssignment");
        processAttribute(node.isBlockScoped(), "isBlockScoped");
        processAttribute(node.isConst(), "isConst");
        processAttribute(node.isFunctionDeclaration(), "isFunctionDeclaration");
        processAttribute(node.isLet(), "isLet");
        
        processWithComment(node.getAssignmentDest(), "VarNode Assignment Dest");
        processWithComment(node.getInit(), "VarNode Init");
        if (node.getAssignmentSource() != node.getInit()) {
            processWithComment(node.getAssignmentSource(), "VarNode Assignment Source");
        }
        createCloseTag(node);
        return false;
    }

    
}
