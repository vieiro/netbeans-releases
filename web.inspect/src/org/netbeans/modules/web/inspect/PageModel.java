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
package org.netbeans.modules.web.inspect;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import org.openide.nodes.Node;

/**
 * Model of an inspected web-page.
 *
 * @author Jan Stola
 */
public abstract class PageModel {
    public static final String PROP_DOCUMENT = "document"; // NOI18N
    /** Name of the property that is fired when the set of selected elements is changed. */
    public static final String PROP_SELECTED_NODES = "selectedNodes"; // NOI18N
    /** Name of the property that is fired when the set of highlighted nodes is changed. */
    public static final String PROP_HIGHLIGHTED_NODES = "highlightedNodes"; // NOI18N
    /** Name of the property that is fired when the selection mode is switched on/off. */
    public static final String PROP_SELECTION_MODE = "selectionMode"; // NOI18N
    /** Property change support. */
    private PropertyChangeSupport propChangeSupport = new PropertyChangeSupport(this);

    /**
     * Returns the document node.
     * 
     * @return document node.
     */
    public abstract Node getDocumentNode();

    /**
     * Returns the document URL.
     * 
     * @return document URL.
     */
    public abstract String getDocumentURL();

    /**
     * Sets the selected nodes.
     * 
     * @param nodes nodes to select in the page.
     */
    public abstract void setSelectedNodes(List<? extends Node> nodes);

    /**
     * Returns selected nodes.
     * 
     * @return selected nodes.
     */
    public abstract List<? extends Node> getSelectedNodes();

    /**
     * Sets the highlighted nodes.
     * 
     * @param nodes highlighted nodes.
     */
    public abstract void setHighlightedNodes(List<? extends Node> nodes);

    /**
     * Switches the selection mode on or off.
     * 
     * @param selectionMode determines whether the selection mode should
     * be switched on or off.
     */
    public abstract void setSelectionMode(boolean selectionMode);

    /**
     * Determines whether the selection mode is switched on or off.
     * 
     * @return {@code true} when the selection mode is switched on,
     * returns {@code false} otherwise.
     */
    public abstract boolean isSelectionMode();

    /**
     * Returns highlighted nodes.
     * 
     * @return highlighted nodes.
     */
    public abstract List<? extends Node> getHighlightedNodes();

    /**
     * Returns CSS Styles view for this page. If the view needs to affect
     * the lookup of the enclosing {@code TopComponent} it may do so using
     * the value of its {@code "lookup"} client property.
     *
     * @return CSS Styles view for this page.
     */
    public abstract JComponent getCSSStylesView();

    /**
     * Disposes this page model.
     */
    protected abstract void dispose();

    /**
     * Adds a property change listener.
     * 
     * @param listener listener to add.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Removes a property change listener.
     * 
     * @param listener listener to remove.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propChangeSupport.removePropertyChangeListener(listener);
    }

    /**
     * Fires the specified property change.
     * 
     * @param propName name of the property.
     * @param oldValue old value of the property or {@code null}.
     * @param newValue new value of the property or {@code null}.
     */
    protected void firePropertyChange(String propName, Object oldValue, Object newValue) {
        propChangeSupport.firePropertyChange(propName, oldValue, newValue);
    }

    /**
     * Information about a resource (like script, image or style sheet) used by a page.
     */
    public static class ResourceInfo {
        /** Type of the resource. */
        private Type type;
        /** URL of the resource. */
        private String url;

        /**
         * Creates a new {@code ResourceInfo}.
         * 
         * @param type type of the resource.
         * @param url URL of the resource.
         */
        public ResourceInfo(Type type, String url) {
            this.type = type;
            this.url = url;
        }

        /**
         * Returns the type of the resource.
         * 
         * @return type of the resource.
         */
        public Type getType() {
            return type;
        }

        /**
         * Returns the URL of the resource.
         * 
         * @return URL of the resource.
         */
        public String getURL() {
            return url;
        }

        /**
         * Type of a resource.
         */
        public enum Type {
            /** HTML of the page itself. */
            HTML("html"), // NOI18N
            /** Style sheet. */
            STYLESHEET("styleSheet"), // NOI18N
            /** Script. */
            SCRIPT("script"), // NOI18N
            /** Image. */
            IMAGE("image"); // NOI18N

            /** Code of the resource type. */
            private String code;

            /**
             * Creates a new {@code Type}.
             * 
             * @param code code of the resource type.
             */
            private Type(String code) {
                this.code = code;
            }

            /**
             * Returns the code of this resource type.
             * 
             * @return code of this resource type.
             */
            public String getCode() {
                return code;
            }

            /**
             * Returns type of a resource from its code.
             * 
             * @param code code of the resource type.
             * @return type of a resource from its code.
             */
            public static Type fromCode(String code) {
                Type result = null;
                for (Type type : values()) {
                    if (type.code.equals(code)) {
                        result = type;
                        break;
                    }
                }
                return result;
            }
        };
    }

    /**
     * Information about a CSS/style rule.
     */
    public static class RuleInfo {
        /** URL of the style sheet this rule comes from. */
        private String sourceURL;
        /** Selector of this rule. */
        private String selector;
        /**
         * Style information of the rule - maps the name of style attribute
         * (specified by this rule) to its value.
         */
        private Map<String,String> style;

        /**
         * Creates a new {@code RuleInfo}.
         * 
         * @param sourceURL URL of the style sheet the rule comes from.
         * @param selector selector of the rule.
         * @param style style information of the rule.
         */
        public RuleInfo(String sourceURL, String selector, Map<String,String> style) {
            this.sourceURL = sourceURL;
            this.selector = selector;
            this.style = style;
        }

        /**
         * Returns URL of the style sheet this rule comes from.
         * 
         * @return URL of the style sheet this rule comes from.
         */
        public String getSourceURL() {
            return sourceURL;
        }

        /**
         * Returns the selector of this rule.
         * 
         * @return selector of this rule.
         */
        public String getSelector() {
            return selector;
        }

        /**
         * Returns style information of the rule.
         * 
         * @return style information of the rule.
         */
        public Map<String,String> getStyle() {
            return style;
        }
        
    }
    
}
