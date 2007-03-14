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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.languages;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.netbeans.api.languages.ASTNode;
import org.netbeans.api.languages.ASTToken;
import org.netbeans.api.languages.Context;
import org.netbeans.api.languages.SyntaxContext;
import org.netbeans.api.lexer.Token;
import org.netbeans.modules.languages.parser.Pattern;
import org.netbeans.modules.languages.parser.Pattern;
import org.openide.ErrorManager;
import org.openide.util.Lookup;

/**
 *
 * @author Jan Jancura
 */
public class Feature {

    public enum Type {
        STRING,
        METHOD_CALL,
        PATTERN,
        NOT_SET
    }
    
    public static Feature create (
        String      featureName,
        Selector    selector
    ) {
        return new Feature (
            featureName, 
            selector, 
            null,
            Collections.<String,Evaluator>emptyMap (),
            Collections.<String,Pattern>emptyMap ()
        );
    }
    
    public static Feature createMethodCallFeature (
        String      featureName,
        Selector    selector,
        String      methodCall
    ) {
        return new Feature (
            featureName, 
            selector, 
            new Method (methodCall),
            Collections.<String,Evaluator>emptyMap (),
            Collections.<String,Pattern>emptyMap ()
        );
    }

    public static Feature createExpressionFeature (
        String      featureName,
        Selector    selector,
        String      expression
    ) {
        return new Feature (
            featureName, 
            selector, 
            new Expression (expression),
            Collections.<String,Evaluator>emptyMap (), 
            Collections.<String,Pattern>emptyMap ()
        );
    }

    public static Feature createExpressionFeature (
        String      featureName,
        Selector    selector,
        Pattern     pattern
    ) {
        return new Feature (
            featureName, 
            selector, 
            pattern,
            Collections.<String,Evaluator>emptyMap (), 
            Collections.<String,Pattern>emptyMap ()
        );
    }
    
    public static Feature create (
        String              featureName,
        Selector            selector,
        Map<String,String>  expressions,
        Map<String,String>  methods,
        Map<String,Pattern> patterns
    ) {
        Map<String,Evaluator> evaluators = new HashMap<String,Evaluator> ();
        Iterator<String> it = expressions.keySet ().iterator ();
        while (it.hasNext ()) {
            String key = it.next ();
            evaluators.put (key, new Expression (expressions.get (key)));
        }
        it = methods.keySet ().iterator ();
        while (it.hasNext ()) {
            String key = it.next ();
            evaluators.put (key, new Method (methods.get (key)));
        }
        return new Feature (
            featureName, 
            selector, 
            null, 
            evaluators, 
            patterns
        );
    }
    
    
    private String                  featureName;
    private Selector                selector;
    private Object                  value;
    private Map<String,Evaluator>   evaluators;
    private Map<String,Pattern>     patterns;
    
    private Feature (
        String                  featureName,
        Selector                selector,
        Object                  value,
        Map<String,Evaluator>   evaluators,
        Map<String,Pattern>     patterns
    ) {
        this.featureName = featureName;
        this.selector = selector;
        this.value = value;
        this.evaluators = evaluators;
        this.patterns = patterns;
    }
    
    public String getFeatureName () {
        return featureName;
    }
    
    public Selector getSelector () {
        return selector;
    }

    public boolean hasSingleValue () {
        return value != null;
    }
    
    public Type getType () {
        if (value == null) return Type.NOT_SET;
        if (value instanceof Pattern) return Type.PATTERN;
        if (value instanceof Method) return Type.METHOD_CALL;
        return Type.STRING;
    }
    
    public Object getValue () {
        if (value instanceof Evaluator)
            return ((Evaluator) value).evaluate ();
        return value;
    }
    
    public Pattern getPattern () {
        return (Pattern) value;
    }
    
    public Object getValue (Context context) {
        if (value == null) return null;
        return ((Evaluator) value).evaluate (context);
    }
    
    public Object getValue (Object[] parameters) {
        if (value == null) return null;
        return ((Method) value).evaluate (parameters);
    }
    
    public boolean getBoolean (String propertyName, boolean defaultValue) {
        Object o = getValue (propertyName);
        if (o == null) return defaultValue;
        if (o instanceof Boolean) return ((Boolean) o).booleanValue ();
        return Boolean.parseBoolean ((String) o);
    }
    
    public boolean getBoolean (String propertyName, Context context, boolean defaultValue) {
        Object o = getValue (propertyName, context);
        if (o == null) return defaultValue;
        if (o instanceof Boolean) return ((Boolean) o).booleanValue ();
        return Boolean.parseBoolean ((String) o);
    }
    
    public Object getValue (String propertyName) {
        Evaluator e = evaluators.get (propertyName);
        if (e != null)
            return e.evaluate ();
        return patterns.get (propertyName);
    }
    
    public Object getValue (String propertyName, Context context) {
        Evaluator e = evaluators.get (propertyName);
        if (e == null) return null;
        return e.evaluate (context);
    }
    
    public Object getValue (String propertyName, Object[] parameters) {
        Method e = (Method) evaluators.get (propertyName);
        if (e == null) return null;
        return e.evaluate (parameters);
    }
    
    public Pattern getPattern (String propertyName) {
        return patterns.get (propertyName);
    }

    public Type getType (String propertyName) {
        if (patterns.containsKey (propertyName)) return Type.PATTERN;
        Evaluator e = evaluators.get (propertyName);
        if (e == null) return Type.NOT_SET;
        if (e instanceof Method) return Type.METHOD_CALL;
        return Type.STRING;
    }
    
    public String getMethodName () {
        return ((Method) value).getMethodName ();
    }
    
    public String getMethodName (String propertyName) {
        Method m = (Method) evaluators.get (propertyName);
        if (m == null) return null;
        return m.getMethodName ();
    }


    // innerclasses ............................................................
    
    private abstract static class Evaluator {
        public abstract Object evaluate ();
        public abstract Object evaluate (Context context);
    }
    
    private static class Expression extends Evaluator {

        private String[] names;
        private String expression;

        private Expression (String expression) {
            this.expression = expression;
            if (expression == null) return;
            List<String> l = new ArrayList<String> ();
            int start = 0;
            do {
                int ss = expression.indexOf ('$', start);
                if (ss < 0) {
                    l.add (expression.substring (start, expression.length ()));
                    break;
                }
                l.add (expression.substring (start, ss));
                ss++;
                int se = expression.indexOf ('$', ss);
                if (se < 0) se = expression.length ();
                l.add (expression.substring (ss, se));
                start = se + 1;
            } while (start < expression.length ());
            names = l.toArray (new String [l.size ()]);
        }

        public Object evaluate (Context context) {
            if (context instanceof SyntaxContext) {
                Object l = ((SyntaxContext) context).getASTPath ().getLeaf ();
                if (l instanceof ASTNode)
                    return evaluate ((ASTNode) l);
                if (l instanceof ASTToken)
                    return evaluate ((ASTToken) l);
            } else {
                Token t = context.getTokenSequence ().token ();
                ASTToken stoken = ASTToken.create (
                    context.getTokenSequence ().language ().mimeType (),
                    t.id ().name (),
                    t.text ().toString (),
                    context.getTokenSequence ().offset ()
                );
                 return evaluate (stoken);
            }
            throw new IllegalArgumentException ();
        }

        public Object evaluate () {
            return expression;
        }

        private Object evaluate (ASTNode node) {
            if (names == null) return null;
            StringBuilder sb = new StringBuilder ();
            int i, k = names.length;
            for (i = 0; i < k; i += 2) {
                sb.append (names [i]);
                if (i + 1 >= names.length) break;
                if (names [i + 1].equals ("")) {
                    sb.append (node.getAsText ());
                    continue;
                }
                Object o = get (node, names [i + 1]);
                if (o == null)
                    return null; //sb.append ('?').append (names [i + 1]).append ('?');
                else
                if (o instanceof ASTToken)
                    sb.append (((ASTToken) o).getIdentifier ());
                else
                    sb.append (((ASTNode) o).getAsText ());
            }
            return sb.toString ();
        }

        private static Object get (ASTNode node, String s) {
            int i = s.indexOf ('.');
            if (i > 0) {
                String ss = s.substring (0, i);
                ASTNode n = node.getNode (ss);
                if (n != null)
                    return get (n, s.substring (i + 1));
                return null;
            }
            ASTNode n = node.getNode (s);
            if (n != null) return n;
            return node.getTokenType (s);
        }

        private Object evaluate (ASTToken token) {
            if (names == null) return null;
            StringBuilder sb = new StringBuilder ();
            int i, k = names.length;
            for (i = 0; i < k; i += 2) {
                sb.append (names [i]);
                if (i + 1 >= names.length) break;
                if (names [i + 1].equals ("identifier"))
                    sb.append (token.getIdentifier ());
                else
                if (names [i + 1].equals (""))
                    sb.append (token.getIdentifier ());
                else
                if (names [i + 1].equals ("type"))
                    sb.append (token.getType ());
                else
                    return null; //sb.append ('?').append (names [i + 1]).append ('?');
            }
            return sb.toString ();
        }
    }

    private static class Method extends Evaluator {

        private String methodName;
        private java.lang.reflect.Method method;
        private boolean resolved = false;

        private Method (String methodName) {
            this.methodName = methodName;
        }

        public Object evaluate () {
            return evaluate (new Object[] {});
        }

        public Object evaluate (Context context) {
            return evaluate (new Object[] {context});
        }

//        public Object evaluate (ASTToken token) {
//            return evaluate (new Object[] {token});
//        }

        public Object evaluate (
            Object[]    params
        ) {
            if (!resolved) {
                resolved = true;
                int i = methodName.lastIndexOf ('.');
                if (i < 1) 
                    throw new IllegalArgumentException (methodName);
                String className = methodName.substring (0, i);
                String methodN = methodName.substring (i + 1);
                ClassLoader cl = (ClassLoader) Lookup.getDefault ().
                    lookup (ClassLoader.class);
                try {
                    Class cls = cl.loadClass (className);
                    java.lang.reflect.Method[] ms = cls.getMethods ();
                    int j, jj = ms.length;
                    for (j = 0; j < jj; j++)
                        if (ms [j].getName ().equals (methodN) &&
                            ms [j].getParameterTypes ().length == params.length
                        ) {
                            Class[] pts = ms [j].getParameterTypes ();
                            int l, ll = params.length;
                            for (l = 0; l < ll; l++) {
                                if (params [l] != null && 
                                    !pts [l].isAssignableFrom (params [l].getClass ())
                                )
                                    break;
                            }
                            if (l < ll) continue;
                            method = ms [j];
                            break;
                        }
                    if (method == null)
                        throw new NoSuchMethodException (methodName);
                } catch (ClassNotFoundException ex) {
                    ErrorManager.getDefault ().notify (ex);
                } catch (NoSuchMethodException ex) {
                    ErrorManager.getDefault ().notify (ex);
                }
            }
            if (method != null)
                try {
                    return method.invoke (null, params);
                } catch (IllegalAccessException ex) {
                    ErrorManager.getDefault ().notify (ex);
                } catch (InvocationTargetException ex) {
                    ErrorManager.getDefault ().notify (ex);
                } catch (IllegalArgumentException ex) {
                    ErrorManager.getDefault ().notify (ex);
                }
            return null;
        }

        public String getMethodName() {
            return methodName;
        }
    }
}


