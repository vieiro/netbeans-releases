/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.vmd.midpnb.components.svg.parsers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.netbeans.modules.mobility.svgcore.util.SVGComponentsSupport;
import org.netbeans.modules.vmd.api.model.Debug;
import org.netbeans.modules.vmd.api.model.DesignComponent;
import org.netbeans.modules.vmd.api.model.PropertyValue;
import org.netbeans.modules.vmd.api.model.TypeID;
import org.netbeans.modules.vmd.midp.components.MidpArraySupport;
import org.netbeans.modules.vmd.midp.components.MidpTypes;
import org.netbeans.modules.vmd.midpnb.components.svg.form.SVGButtonCD;
import org.netbeans.modules.vmd.midpnb.components.svg.form.SVGCheckBoxCD;
import org.netbeans.modules.vmd.midpnb.components.svg.form.SVGComboBoxCD;
import org.netbeans.modules.vmd.midpnb.components.svg.form.SVGComponentCD;
import org.netbeans.modules.vmd.midpnb.components.svg.form.SVGComponentEventSourceCD;
import org.netbeans.modules.vmd.midpnb.components.svg.form.SVGFormCD;
import org.netbeans.modules.vmd.midpnb.components.svg.form.SVGLabelCD;
import org.netbeans.modules.vmd.midpnb.components.svg.form.SVGListCD;
import org.netbeans.modules.vmd.midpnb.components.svg.form.SVGRadioButtonCD;
import org.netbeans.modules.vmd.midpnb.components.svg.form.SVGSliderCD;
import org.netbeans.modules.vmd.midpnb.components.svg.form.SVGSpinnerCD;
import org.netbeans.modules.vmd.midpnb.components.svg.form.SVGTextFieldCD;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 *
 * @author avk
 */
public class SVGFormImageParser extends SVGComponentImageParser {

    private static final String DIGITS = "_\\d+"; //NOI18N
    private static final String DIGITS_END = DIGITS + "$"; //NOI18N
    private static final String PREFIX = "^"; //NOI18N
    private static final Pattern FORM_COMPONENT_ID_BUTTON = Pattern.compile(PREFIX + SVGComponentsSupport.ID_PREFIX_BUTTON + DIGITS_END);
    private static final Pattern FORM_COMPONENT_ID_LABEL = Pattern.compile(PREFIX + SVGComponentsSupport.ID_PREFIX_LABEL + DIGITS_END);
    private static final Pattern FORM_COMPONENT_ID_RADIO = Pattern.compile(PREFIX + SVGComponentsSupport.ID_PREFIX_RADIOBUTTON + DIGITS_END);
    private static final Pattern FORM_COMPONENT_ID_CHECKBOX = Pattern.compile(PREFIX + SVGComponentsSupport.ID_PREFIX_CHECKBOX + DIGITS_END);
    private static final Pattern FORM_COMPONENT_ID_COMBOBOX = Pattern.compile(PREFIX + SVGComponentsSupport.ID_PREFIX_COMBOBOX + DIGITS_END);
    private static final Pattern FORM_COMPONENT_ID_LIST = Pattern.compile(PREFIX + SVGComponentsSupport.ID_PREFIX_LIST + DIGITS_END);
    private static final Pattern FORM_COMPONENT_ID_SLIDER = Pattern.compile(PREFIX + SVGComponentsSupport.ID_PREFIX_SLIDER + DIGITS_END);
    private static final Pattern FORM_COMPONENT_ID_SPINNER = Pattern.compile(PREFIX + SVGComponentsSupport.ID_PREFIX_SPINNER + DIGITS_END);
    private static final Pattern FORM_COMPONENT_ID_TEXTFIELD = Pattern.compile(PREFIX + SVGComponentsSupport.ID_PREFIX_TEXTFIELD + DIGITS_END);
    private static final Pattern FORM_COMPONENT_ID_RADIOBUTTONFRAME = Pattern.compile(PREFIX + SVGComponentsSupport.ID_PREFIX_RADIOBUTTON_FRAME + DIGITS_END);
    
    private static final Pattern LABEL_TEXT_PROP = Pattern.compile(PREFIX + 
            SVGComponentsSupport.ID_PREFIX_LABEL + DIGITS +"_text$"); // NOI18N

    public synchronized static void parseSVGForm(final InputStream svgInputStream, final DesignComponent svgForm) {
        final SVGFormComponent[] srcComponents = getFormComponents(svgInputStream);
        if (srcComponents != null) {
            svgForm.getDocument().getTransactionManager().writeAccess(new Runnable() {

                public void run() {
                    for (SVGFormComponent srcComponent : srcComponents) {
                        DesignComponent svgComponent = srcComponent.createComponent(svgForm);
                        svgForm.addComponent(svgComponent);
                        MidpArraySupport.append(svgForm, SVGFormCD.PROP_COMPONENTS, svgComponent);
                    }
                }
            });
        }
    }

    public void parse(InputStream svgInputStream, DesignComponent svgComponent) {
        parseSVGForm(svgInputStream, svgComponent);
    }

    private static SVGFormComponent[] getFormComponents(final InputStream svgInputStream) {
        NamedElementsContentHandler ch = new NamedElementsContentHandler();
        try {
            XMLReader parser = XMLReaderFactory.createXMLReader();
            parser.setContentHandler(ch);
            parser.setEntityResolver(ch);
            parser.parse(new InputSource(svgInputStream));
        } catch (IOException ex) {
            Debug.warning(ex);
        } catch (SAXException ex) {
            Debug.warning(ex);
        }
        return ch.getFoundElements();
    }

    public abstract static class SVGFormComponent {

        public static SVGFormComponent create(final String id, final TypeID type, Float position) {
            return new SVGFormComponent(id, type, position) {

                @Override
                public DesignComponent createComponent(DesignComponent parentComponent) {
                    DesignComponent dc = parentComponent.getDocument().createComponent(type);
                    dc.writeProperty(SVGComponentCD.PROP_ID, MidpTypes.createStringValue(getId()));
                    return dc;
                }
            };
        }

        public static SVGFormComponent createComponent(final String id, 
                final TypeID type, final TypeID eventTypeId , Float position) 
        {
            return new SVGFormComponent(id, type, position) {

                @Override
                public DesignComponent createComponent(DesignComponent parentComponent) {
                    DesignComponent dc = parentComponent.getDocument().createComponent(type);
                    if (eventTypeId != null) {
                        DesignComponent svgES = parentComponent.getDocument()
                                .createComponent(eventTypeId);
                        svgES.writeProperty(
                                SVGComponentEventSourceCD.PROP_SVGCOMPONENT,
                                PropertyValue.createComponentReference(dc));
                        parentComponent.addComponent(svgES);
                    }
                    dc.writeProperty(SVGComponentCD.PROP_ID, MidpTypes.createStringValue(getId()));
                    Map<String,String> properties = getProperties();
                    if ( properties != null  ){
                        for ( Entry<String,String> entry : properties.entrySet()){
                            dc.writeProperty( entry.getKey(), 
                                    MidpTypes.createStringValue(entry.getValue()));
                        }
                    }
                    return dc;
                }
            };
        }
        
        private String id;
        private TypeID type;
        private Float position;
        private Map<String,String> myProperties;

        SVGFormComponent(String id, TypeID type, Float position) {
            if (type == null || id == null || position == null) {
                throw new IllegalArgumentException(" id or type argument is null"); //NOI18N
            }
            this.type = type;
            this.id = id;
            this.position = position;
        }

        public abstract DesignComponent createComponent(DesignComponent parentComponent);

        String getId() {
            return id;
        }

        TypeID getTypeID() {
            return type;
        }

        Float getPositon() {
            return position;
        }
        
        void setProperty( String name , String value ){
            if ( myProperties == null ){
                myProperties = new HashMap<String, String>();
            }
            myProperties.put( name, value );
        }
        
        Map<String,String> getProperties(){
            return myProperties;
        }
    }
    
    /*private static class Pair {
        Pair( String key , SVGFormComponent component){
            myKey = key;
            myForm = component;
        }
        
        String getKey(){
            return myKey;
        }
        
        SVGFormComponent getComponent(){
            return myForm;
        }
        
        private String myKey;
        private SVGFormComponent myForm;
    }*/

    private static class NamedElementsContentHandler extends AbstractElementsContentHandler {

        private ArrayList<SVGFormComponent> foundElements;
        private Float radioButtonFramePosition;
        //private Stack<Pair> myStack;
        private SVGFormComponent myCurrentComponent;
        private String myPropName;
        private StringBuilder myText;

        public NamedElementsContentHandler() {
            this.foundElements = new ArrayList<SVGFormComponent>();
            //myStack = new Stack<Pair>();
        }

        public SVGFormComponent[] getFoundElements() {
          LinkedList<SVGFormComponent> reversedList = new LinkedList<SVGFormComponent>();
            for (SVGFormComponent c : foundElements) {
                reversedList.addFirst(c);
            }
            return reversedList.toArray(new SVGFormComponent[reversedList.size()]);
        }

        public final void resetFoundElements() {
            foundElements.clear();
        }

        @Override
        public final void startElement(String namespaceURI, String localName,
                String qName, Attributes atts)
                throws SAXException 
        {
            // get id attribute value
            final String id = atts.getValue("id"); // NOI18N
            final String transform = atts.getValue("transform");
            
            if (id != null && LABEL_TEXT_PROP.matcher( id ).find()) {
                myPropName = SVGLabelCD.PROP_TEXT;    // NOI18N
                return;
            }
            
            if (id == null || transform == null) {
                /*Pair entry = new Pair( localName , null );
                myStack.push( entry );*/
                return;
            }
            if (FORM_COMPONENT_ID_RADIOBUTTONFRAME.matcher(id).find()) {
                radioButtonFramePosition = getPosition(atts);
            }
            if (FORM_COMPONENT_ID_BUTTON.matcher(id).find()) {
                addSVGFormComponent(id, SVGButtonCD.TYPEID, getPosition(atts), 
                        localName );
            } else if (FORM_COMPONENT_ID_CHECKBOX.matcher(id).find()) {
                addSVGFormComponent(id, SVGCheckBoxCD.TYPEID, getPosition(atts), 
                        localName);
            } else if (FORM_COMPONENT_ID_COMBOBOX.matcher(id).find()) {
                addSVGFormComponent(id, SVGComboBoxCD.TYPEID, getPosition(atts), 
                        localName);
            } else if (FORM_COMPONENT_ID_LABEL.matcher(id).find()) {
                addSVGFormComponent(id, SVGLabelCD.TYPEID, getPosition(atts), 
                        localName);
            } else if (FORM_COMPONENT_ID_LIST.matcher(id).find()) {
                addSVGFormComponent(id, SVGListCD.TYPEID, getPosition(atts), 
                        localName);
            } else if (FORM_COMPONENT_ID_RADIO.matcher(id).find()) {
                addSVGFormComponent(id, SVGRadioButtonCD.TYPEID, 
                        getPositionForRadioButton(atts, radioButtonFramePosition), 
                        localName);
            } else if (FORM_COMPONENT_ID_SLIDER.matcher(id).find()) {
                addSVGFormComponent(id, SVGSliderCD.TYPEID, getPosition(atts), 
                        localName);
            } else if (FORM_COMPONENT_ID_SPINNER.matcher(id).find()) {
                addSVGFormComponent(id, SVGSpinnerCD.TYPEID, getPosition(atts), 
                        localName);
            } else if (FORM_COMPONENT_ID_TEXTFIELD.matcher(id).find()) {
                addSVGFormComponent(id, SVGTextFieldCD.TYPEID, getPosition(atts), 
                        localName);
            }
            else {
                /*Pair entry = new Pair( localName , null);
                myStack.push( entry );*/
            }
        }
        
        @Override
        public void characters( char[] ch, int start, int length )
                throws SAXException
        {
            if ( myPropName == null ){
                return;
            }
            if ( myText == null ){
                myText = new StringBuilder();
            }
            myText.append(ch, start , length);
        }

        private void addSVGFormComponent(String id, TypeID type, Float position,
                String localName ) {
            int index = getIndex(position);
            myCurrentComponent = SVGFormComponent.createComponent(id, 
                    type, SVGComponentCD.getEventType(type), 
                    position);
            if (index == -1) {
                ;
                foundElements.add( myCurrentComponent );
            } else {
                foundElements.add(index, myCurrentComponent );
            }
            /*Pair entry = new Pair( localName , component);
            myStack.push( entry );*/
        }

        @Override
        public void endElement(String namespaceURI, String localName, String qName)
                throws SAXException 
        {
            if ( myPropName == null ){
                return;
            }
            myCurrentComponent.setProperty( myPropName, myText.toString().trim() );
            myText = null;
            myPropName = null;
            /*Pair entry = myStack.pop();
            assert entry.getKey().equals( localName ) :"'endElement' mmethod " +
            		"is called for tag '"+localName+" that was not placed" +
            				" into the stack on 'startElement' handling";*/
        }

        private int getIndex(Float position) {
            int index = -1;
            if (position != null) {
                Float highestPosition = new Float(-1);
                for (SVGFormComponent c : foundElements) {
                    if (position > c.getPositon() && highestPosition < c.getPositon()) {
                        highestPosition = c.getPositon();
                        index = foundElements.indexOf(c);
                    }
                }
            }
            return index;
        }
    }

    private static Float getPosition(Attributes atts) {
        String transform = atts.getValue("transform"); //NOI18N
        Float position = null;
        if (transform != null) {
            if (transform.startsWith("translate")){
                position = getPositionFromTranslate(transform);
            } else if (transform.startsWith("matrix")){
                position = getPositionFromMatrix(transform);
            }
        }
        return position;
    }
    
    private static Float getPositionFromTranslate(String transform) {
        Float position = null;
        int begining = transform.indexOf(","); //NOI18N
        int end = transform.indexOf(")"); //NOI18N
        try{
            position = new Float(transform.substring(begining + 1, end));
        } catch (NumberFormatException nfe) {
            Logger.getLogger(SVGFormImageParser.class.getName()).info(nfe.getMessage());
        }
        return position;
    }
    
    private static Float getPositionFromMatrix(String transform) {
        Float position = null;
        int begining = transform.lastIndexOf(","); //NOI18N
        int end = transform.indexOf(")"); //NOI18N
        try{
            position = new Float(transform.substring(begining + 1, end));
        } catch (NumberFormatException nfe) {
            Logger.getLogger(SVGFormImageParser.class.getName()).info(nfe.getMessage());
        }
        return position;
    }

    private static Float getPositionForRadioButton(Attributes atts, Float framePosition) {
        String transform = atts.getValue("transform"); //NOI18N
        Float position = null;
        if (transform != null) {
            int begining = transform.indexOf(","); //NOI18N
            int end = transform.indexOf(")"); //NOI18N
            position = new Float(transform.substring(begining + 1, end));
            position = position + framePosition;
        }
        return position;
    }

    /**
     * Search for SVGComponents in the given SVG image (Tiny)
     * @param svgInputStream - SVG image
     * @return Array of svg id components with SVGCOmponent ID
     */
    public static final String[][] getComponentsInformation(InputStream svgInputStream) {
        SVGFormComponent[] components = getFormComponents(svgInputStream);
        String[][] values = new String[components.length][2];
        for (int i = 0; i < components.length; i++) {
            values[i][1] = components[i].getId();
            values[i][0] = MidpTypes.getSimpleClassName(components[i].getTypeID());
        }

        return values;
    }
}

