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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.ruby.hints.infrastructure;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.jruby.ast.Node;
import org.jruby.ast.NodeTypes;
import org.netbeans.api.gsf.CompilationInfo;
import org.netbeans.api.gsf.HintsProvider;
import org.netbeans.modules.ruby.AstPath;
import org.netbeans.modules.ruby.AstUtilities;
import org.netbeans.modules.ruby.hints.options.HintsSettings;
import org.netbeans.spi.editor.hints.ErrorDescription;

/**
 * Class which acts on the rules and suggestions by iterating the 
 * AST and invoking applicable rules
 * 
 * 
 * @author Tor Norbye
 */
public class RubyHintsProvider implements HintsProvider {
    private boolean cancelled;
    
    public RubyHintsProvider() {
    }
    
    public void computeHints(CompilationInfo info, List<ErrorDescription> result) {
        cancelled = false;
        
        Node root = AstUtilities.getRoot(info);

        if (root == null) {
            return;
        }
        Map<Integer,List<AstRule>> hints = RulesManager.getInstance().getHints(false);

        if (hints.isEmpty()) {
            return;
        }
        
        if (isCancelled()) {
            return;
        }
        
        AstPath path = new AstPath();
        path.descend(root);
        
        applyRules(NodeTypes.ROOTNODE, root, path, info, hints, result);
        
        scan(root, path, info, hints, result);
        path.ascend();
    }
    

    public void computeSuggestions(CompilationInfo info, List<ErrorDescription> result, int caretOffset) {
        cancelled = false;
        
        Node root = AstUtilities.getRoot(info);

        if (root == null) {
            return;
        }

        Map<Integer, List<AstRule>> suggestions = new HashMap<Integer, List<AstRule>>();
   
        suggestions.putAll(RulesManager.getInstance().getHints(true));
   
        for (Entry<Integer, List<AstRule>> e : RulesManager.getInstance().getSuggestions().entrySet()) {
            List<AstRule> rules = suggestions.get(e.getKey());

            if (rules != null) {
                List<AstRule> res = new LinkedList<AstRule>();

                res.addAll(rules);
                res.addAll(e.getValue());

                suggestions.put(e.getKey(), res);
            } else {
                suggestions.put(e.getKey(), e.getValue());
            }
        }

        if (suggestions.isEmpty()) {
            return;
        }
        

        if (isCancelled()) {
            return;
        }

        AstPath path = new AstPath(root, caretOffset);
        
        Iterator<Node> it = path.leafToRoot();
        while (it.hasNext()) {
            if (isCancelled()) {
                return;
            }

            Node node = it.next();
            applyRules(node.nodeId, node, path, info, suggestions, result);
        }
        
        //applyRules(NodeTypes.ROOTNODE, path, info, suggestions, result);
    }

    private void applyRules(int nodeType, Node node, AstPath path, CompilationInfo info, Map<Integer,List<AstRule>> hints,
            List<ErrorDescription> result) {
        List<AstRule> rules = hints.get(nodeType);

        if (rules != null) {
            for (AstRule rule : rules) {
                boolean enabled = true;

                if (rule instanceof AbstractHint) {
                    enabled = HintsSettings.isEnabled((AbstractHint)rule);
                }

                if (enabled) {
                    rule.run(info, node, path, result);
                }
            }
        }
    }
    
    private void scan(Node node, AstPath path, CompilationInfo info, Map<Integer,List<AstRule>> hints, List<ErrorDescription> result) {
        applyRules(node.nodeId, node, path, info, hints, result);
        
        @SuppressWarnings(value = "unchecked")
        List<Node> list = node.childNodes();

        for (Node child : list) {
            if (isCancelled()) {
                return;
            }

            path.descend(child);
            scan(child, path, info, hints, result);
            path.ascend();
        }        
    }

    public void cancel() {
        cancelled = true;
    }

    private boolean isCancelled() {
        return cancelled;
    }

}
