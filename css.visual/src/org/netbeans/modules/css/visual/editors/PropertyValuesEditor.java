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
package org.netbeans.modules.css.visual.editors;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.Icon;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SpinnerModel;
import javax.swing.event.ChangeListener;
import org.netbeans.api.editor.EditorRegistry;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.css.indexing.api.CssIndex;
import org.netbeans.modules.css.lib.api.CssColor;
import org.netbeans.modules.css.lib.api.properties.FixedTextGrammarElement;
import org.netbeans.modules.css.lib.api.properties.PropertyDefinition;
import org.netbeans.modules.css.lib.api.properties.TokenAcceptor;
import org.netbeans.modules.css.lib.api.properties.UnitGrammarElement;
import org.netbeans.modules.css.model.api.Model;
import org.netbeans.modules.css.refactoring.api.RefactoringElementType;
import org.netbeans.modules.css.visual.RuleNode;
import org.netbeans.modules.web.common.api.WebUtils;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author marekfukala
 */
@NbBundle.Messages({
    "choose.color.item=Choose Color"
})
public class PropertyValuesEditor extends PropertyEditorSupport implements ExPropertyEditor {

    private Collection<UnitGrammarElement> unitElements;
    private Collection<FixedTextGrammarElement> fixedElements;
    private boolean addNoneProperty;
    private String[] tags;
    private Map<String, FixedTextGrammarElement> tags2fixedElement = new HashMap<String, FixedTextGrammarElement>();
    private boolean containsColor;
    private FileObject file;
    private PropertyDefinition pmodel;
    
    private static final String CHOOSE_COLOR_ITEM = new StringBuilder().append("<html><b>").append(Bundle.choose_color_item()).append("</b></html>").toString();  //NOI18N
    private static final JColorChooser COLOR_CHOOSER = new JColorChooser();

    public PropertyValuesEditor(PropertyDefinition pmodel, Model model, Collection<FixedTextGrammarElement> fixedElements, Collection<UnitGrammarElement> unitElements, boolean addNoneProperty) {
        this.fixedElements = fixedElements;
        this.unitElements = unitElements;
        this.addNoneProperty = addNoneProperty;
        this.pmodel = pmodel;
        this.file = model.getLookup().lookup(FileObject.class);
    }

    @Override
    public synchronized String[] getTags() {
        if (tags == null) {
            List<String> tagsList = new ArrayList<String>();
            
            //sort the items alphabetically first
            Collection<String> fixedElementNames = new TreeSet<String>();
            for (FixedTextGrammarElement element : fixedElements) {
                String value = element.getValue();
                if (value.length() > 0 && Character.isLetter(value.charAt(0))) { //filter operators & similar
                    fixedElementNames.add(value);
                    tags2fixedElement.put(value, element);

                    //TBD possibly refactor out so it is not so hardcoded
                    if ("color".equals(element.getVisibleOrigin())) { //NOI18N
                        containsColor = true;
                    }

                }
            }
            
            //the rest will handle the order by itself
            tagsList.addAll(fixedElementNames);

            if (containsColor) {
                if (file != null) {
                    Project project = FileOwnerQuery.getOwner(file);
                    if (project != null) {
                        try {
                            Collection<String> hashColorCodes = new TreeSet<String>();
                            CssIndex index = CssIndex.create(project);
                            Map<FileObject, Collection<String>> result = index.findAll(RefactoringElementType.COLOR);
                            for (FileObject f : result.keySet()) {
                                Collection<String> colors = result.get(f);
//                                boolean usedInCurrentFile = f.equals(file);
                                for (String color : colors) {
                                    hashColorCodes.add(color);
                                }
                            }
                            tagsList.addAll(0, hashColorCodes);
                        } catch (IOException ex) {
                            Exceptions.printStackTrace(ex);
                        }

                    }
                }
                
                tagsList.add(0, CHOOSE_COLOR_ITEM);
            }
            
            if (addNoneProperty) {
                //put as first item
                tagsList.add(0, RuleNode.NONE_PROPERTY_NAME);
            }
            
            tags = tagsList.toArray(new String[0]);
        }

        return tags;
    }

    @Override
    public void setAsText(String str) {
        if (str == null) {
            return;
        }

        if (str.isEmpty()) {
            return;
        }

        //same value, ignore
        if (str.equals(getValue())) {
            return;
        }
        
        if(CHOOSE_COLOR_ITEM.equals(str)) {
            //color chooser
            final AtomicReference<Color> color_ref = new AtomicReference<Color>();
            JDialog dialog = JColorChooser.createDialog(EditorRegistry.lastFocusedComponent(), Bundle.choose_color_item(), true, COLOR_CHOOSER,
                    new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            //disalog confirmed
                            color_ref.set(COLOR_CHOOSER.getColor());
                        }
                    }, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    //dialog cancelled
                }
            });
            dialog.setVisible(true);
            dialog.dispose();

            Color color = color_ref.get();
            if(color != null) {
                str = WebUtils.toHexCode(color);
            } else {
                //dialog cancelled, no value - do not allow the CHOOSE_COLOR_ITEM marker to be set the to property
                return ;
            }

        }
        
        setValue(str);

    }

    @Override
    public String getAsText() {
        return getValue().toString();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "; property: " + pmodel.getName();
    }

    
    
    @Override
    public void attachEnv(PropertyEnv env) {
        //if there's at least one unit element, then the text field needs to be editable
        env.getFeatureDescriptor().setValue("canEditAsText", Boolean.TRUE); //NOI18N

        if (containsColor) {
            env.getFeatureDescriptor().setValue("customListCellRendererSupport", new ColorListCellRendererSupport()); //NOI18N
        }
        
        env.getFeatureDescriptor().setValue("valueIncrement", new SpinnerModel() { //NOI18N
            private String getNextValue(boolean forward) {
                String value = getAsText();
                for (TokenAcceptor genericAcceptor : TokenAcceptor.ACCEPTORS) {

                    if (genericAcceptor instanceof TokenAcceptor.NumberPostfixAcceptor) {
                        TokenAcceptor.NumberPostfixAcceptor acceptor = (TokenAcceptor.NumberPostfixAcceptor) genericAcceptor;
                        if (acceptor.accepts(value)) {
                            int i = acceptor.getNumberValue(value).intValue();
                            CharSequence postfix = acceptor.getPostfix(value);

                            StringBuilder sb = new StringBuilder();
                            sb.append(i + (forward ? 1 : -1));
                            if(postfix != null) {
                                sb.append(postfix);
                            }

                            return sb.toString();
                        }
                    } else if (genericAcceptor instanceof TokenAcceptor.Number) {
                        TokenAcceptor.Number acceptor = (TokenAcceptor.Number) genericAcceptor;
                        if (acceptor.accepts(value)) {
                            int i = acceptor.getNumberValue(value).intValue();

                            StringBuilder sb = new StringBuilder();
                            sb.append(i + (forward ? 1 : -1));

                            return sb.toString();
                        }
                    }

                }
               
                //not acceptable token
                return null;
            }

            @Override
            public Object getValue() {
                //no-op
                return null;
            }

            @Override
            public void setValue(Object value) {
                //no-op
            }

            @Override
            public Object getNextValue() {
                return getNextValue(true);
            }

            @Override
            public Object getPreviousValue() {
                return getNextValue(false);
            }

            @Override
            public void addChangeListener(ChangeListener l) {
                //no-op
            }

            @Override
            public void removeChangeListener(ChangeListener l) {
                //no-op
            }
        });
    }

    private class ColorListCellRendererSupport extends AtomicReference<ListCellRenderer> implements ListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            ListCellRenderer peer = get();
            
            assert peer != null; //the ComboInplaceEditor must set the original renreder!
            
            if(peer instanceof ColorListCellRendererSupport) {
                System.out.println("warning: nesting of ColorListCellRendererSupport!");
            }

            Component res = peer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (res instanceof JLabel) {
                JLabel label = (JLabel) res;
                assert value instanceof String; //the ComboBoxModel is created over getTags() array
                String strval = (String) value;

                Icon icon = null;
                
                if( strval.startsWith("#") ) { //NOI18N
                    String colorCode = strval.substring(1);
                    icon = WebUtils.createColorIcon(colorCode); //null CssColor will create default icon
                }

                if(strval.equals(CHOOSE_COLOR_ITEM)) {
                    Color chooserColor = COLOR_CHOOSER.getColor();
                    String hexCode = chooserColor != null ? WebUtils.toHexCode(chooserColor) : null;
                    icon = WebUtils.createColorIcon(hexCode);
                }
                
                FixedTextGrammarElement element = tags2fixedElement.get(strval);
                if(!"inherit".equals(strval)) { //filter out colors for inherit
                    if (element != null) {
                        if ("color".equals(element.getVisibleOrigin())) { //NOI18N
                            //try to find color code
                            CssColor color = CssColor.getColor(strval);
                            icon = WebUtils.createColorIcon(color == null ? null : color.colorCode()); //null CssColor will create default icon
                        }
                    }
                }
                label.setIcon(icon);
            }
            return res;

        }
    }
}
