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

package org.netbeans.modules.editor;

import java.awt.Point;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import javax.swing.text.BadLocationException;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileObject;
import org.openide.text.Annotation;
import org.openide.text.Line;
import java.io.IOException;
import java.util.HashMap;
import org.netbeans.editor.ext.ToolTipSupport;
import org.openide.loaders.DataObjectNotFoundException;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Utilities;
import org.netbeans.editor.AnnotationDesc;
import org.netbeans.editor.ext.ExtEditorUI;
import org.netbeans.editor.ext.ExtUtilities;
import java.beans.PropertyChangeListener;
import org.openide.cookies.EditorCookie;
import org.openide.loaders.DataObject;
import org.openide.cookies.InstanceCookie;
import java.util.Enumeration;
import java.util.ArrayList;
import java.beans.PropertyChangeEvent;
import java.lang.ref.WeakReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.EditorKit;
import org.netbeans.api.editor.settings.EditorStyleConstants;
import org.netbeans.modules.editor.lib2.highlighting.HighlightingManager;
import org.netbeans.spi.editor.highlighting.HighlightAttributeValue;
import org.netbeans.spi.editor.highlighting.HighlightsSequence;
import org.openide.filesystems.Repository;
import org.openide.util.RequestProcessor;

/**
* ToolTip annotations reading and refreshing
*
* @author Miloslav Metelka
* @version 1.00
*/

public class NbToolTip extends FileChangeAdapter {
    
    private static final Logger LOG = Logger.getLogger(NbToolTip.class.getName());
    
    private static final HashMap<String,WeakReference<NbToolTip>> mime2tip = new HashMap<String,WeakReference<NbToolTip>>();
    
    private static int lastRequestId;
    
    private String mimeType;
    
    private Annotation[] tipAnnotations;
    
    private RequestProcessor toolTipRP = new RequestProcessor("ToolTip-Evaluator", 1); // NOI18N
    
    static synchronized void buildToolTip(JTextComponent target) {
        String mimeType = NbEditorUtilities.getMimeType(target.getDocument());
        NbToolTip tip = getTip(mimeType);
        tip.buildTip(target);
    }
    
    private static int newRequestId() {
        return ++lastRequestId;
    }
    
    private static int getLastRequestId() {
        return lastRequestId;
    }
    
    
    private NbToolTip(String mimeType) {
        this.mimeType = mimeType;
    }
    
    private static NbToolTip getTip(String mimeType) {
        WeakReference<NbToolTip> nttWr = mime2tip.get(mimeType);
        NbToolTip tip = nttWr == null ? null : nttWr.get();
        if (tip == null) {
            tip = new NbToolTip(mimeType);
            mime2tip.put(mimeType, new WeakReference<NbToolTip>(tip));
        }
        return tip;
    }

    private Annotation[] getTipAnnotations() {
        Annotation[] annos;
        synchronized (NbToolTip.class) {
            annos = tipAnnotations;
        }
        
        if (annos == null) {

            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Searching for tooltip annotations for mimeType = '" + mimeType + "'"); //NOI18N
            }

            // XXX: should use Class2LayerFolder and InstanceProvider
            FileObject annoFolder = Repository.getDefault().getDefaultFileSystem().
            findResource("Editors/" + mimeType + "/ToolTips"); //NOI18N
        
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("tooltip annotation folder = '" + annoFolder + "'"); //NOI18N
            }

            if (annoFolder != null) {
                ArrayList<Annotation> al = new ArrayList<Annotation>();
                Enumeration en = annoFolder.getChildren(false);
                while (en.hasMoreElements()) {
                    FileObject fo = (FileObject)en.nextElement();
                    
                    if (LOG.isLoggable(Level.FINE)) {
                        LOG.fine("tooltip annotation fileobject=" + fo); //NOI18N
                    }

                    try {
                        DataObject dob = DataObject.find(fo);
                        InstanceCookie ic = dob.getCookie(InstanceCookie.class);

                        if (LOG.isLoggable(Level.FINE)) {
                            LOG.fine("tooltip annotation instanceCookie=" + ic); //NOI18N
                        }

                        if (ic != null) {
                            Object a = ic.instanceCreate();

                            if (LOG.isLoggable(Level.FINE)) {
                                LOG.fine("tooltip annotation instance=" + a); //NOI18N
                            }

                            if (a instanceof Annotation) {
                                if (LOG.isLoggable(Level.FINE)) {
                                    LOG.fine("Found tooltip annotation = " + a //NOI18N
                                        + ", class = " + a.getClass() // NOI18N
                                        + " for mimeType = '" + mimeType + "'"// NOI18N
                                    );
                                }
                                
                                al.add((Annotation)a);
                            }
                        }
                    } catch (DataObjectNotFoundException donfe) {
                        LOG.log(Level.FINE, null, donfe);
                    } catch (IOException ioe) {
                        LOG.log(Level.FINE, null, ioe);
                    } catch (ClassNotFoundException cnfe) {
                        LOG.log(Level.FINE, null, cnfe);
                    }
                }
                
                annos = al.toArray(new Annotation[al.size()]);
                synchronized (NbToolTip.class) {
                    tipAnnotations = annos;
                }
                
                annoFolder.addFileChangeListener(this);
            }
        }
        
        return annos;
    }
     
    private void buildTip(JTextComponent target) {
        ExtEditorUI eeui = ExtUtilities.getExtEditorUI(target);
        ToolTipSupport tts = eeui == null ? null : eeui.getToolTipSupport();

        if (tts == null) {
            return; // no tooltip support, no tooltips
        }
        
        // Calls View.getTooltipText, which usually does nothing. CollapsedView.getTooltipText
        // however calls ToolTipSupport to show its own tooltip component and then
        // returns an empty string.
        String toolTipText = target.getUI().getToolTipText(target, tts.getLastMouseEvent().getPoint());
        if (toolTipText != null){
            return;
        }
        
        Annotation[] annos = getTipAnnotations();
        if (annos != null) {
            BaseDocument doc = Utilities.getDocument(target);
            if (doc != null) {
                DataObject dob = NbEditorUtilities.getDataObject(doc);
                if (dob != null && dob.isValid()) {
                    EditorCookie ec = dob.getCookie(EditorCookie.class);
                    if (ec != null) {
                        StyledDocument openedDoc;
                        try {
                            openedDoc = ec.openDocument();
                        } catch (IOException e) {
                            LOG.log(Level.FINE, null, e);
                            openedDoc = null; // should return in next if stmt
                        }

                        if (openedDoc != doc) { // doc has changed in meantime
                            return;
                        }

                        // partial fix of #33165 - read-locking of the document added
                        doc.readLock();
                        try {
                            Point p = tts.getLastMouseEvent().getPoint();
                            int offset = p.x >= 0 ? target.viewToModel(p) : -1;
                            if (offset >= 0 && offset < Utilities.getRowEnd(doc, offset)) {
                                EditorKit kit = org.netbeans.editor.Utilities.getKit(target);
                                if (kit instanceof NbEditorKit) {
                                    Object tooltipAttributeValue = null;
                                    Line.Part lp = null;
                                    Annotation [] tooltipAnnotations = null;
                                    AnnotationDesc annoDesc = null;
                                    
                                    // Get the highlighting layers stuff
                                    HighlightsSequence seq = HighlightingManager.getInstance().getHighlights(target, null).getHighlights(offset, offset + 1);
                                    if (seq.moveNext()) {
                                        tooltipAttributeValue = seq.getAttributes().getAttribute(EditorStyleConstants.Tooltip);
                                    }
                                    
                                    // Get the annotations stuff
                                    int line = Utilities.getLineOffset(doc, offset);
                                    int col = offset - Utilities.getRowStart(target, offset);
                                    Line.Set ls = ec.getLineSet();
                                    if (ls != null) {
                                        Line l = ls.getCurrent(line);
                                        if (l != null) {
                                            lp = l.createPart(col, 0);
                                            if (lp != null) {
                                                annoDesc = doc.getAnnotations().getActiveAnnotation(line);
                                                if (annoDesc != null && ((offset < annoDesc.getOffset() || offset >= annoDesc.getOffset() + annoDesc.getLength()))) {
                                                    annoDesc = null;
                                                }
                                                tooltipAnnotations = annos;
                                            }
                                        }
                                    }
                                    
                                    if ((lp != null && tooltipAnnotations != null) || tooltipAttributeValue != null) {
                                        int requestId = newRequestId();
                                        toolTipRP.post(new Request(
                                            annoDesc, tooltipAnnotations, lp, // annotations stuff
                                            offset, tooltipAttributeValue, // highlighting layers stuff
                                            tts, target, doc, (NbEditorKit) kit, requestId)); // request & tooltip support
                                    }
                                }
                            }
                        } catch (BadLocationException ble) {
                            LOG.log(Level.FINE, null, ble);
                        } finally {
                            doc.readUnlock();
                        }
                    }
                }
            }
        }
    }
        
    private static class Request implements Runnable, PropertyChangeListener, DocumentListener {
        
        private ToolTipSupport tts;
        
        private final Annotation[] annos;
        
        private final AnnotationDesc annoDesc;
        
        private final Line.Part linePart;
        
        private final JTextComponent component;
        
        private final AbstractDocument doc;
        
        private final NbEditorKit kit;
        
        private final int offset;
        
        private final Object tooltipAttributeValue;
        
        private final int requestId;
        
        private boolean documentModified;
        
        Request(
            AnnotationDesc annoDesc, Annotation[] annos, Line.Part lp,
            int offset, Object tooltipAttributeValue,
            ToolTipSupport tts, JTextComponent component, AbstractDocument doc, NbEditorKit kit, int requestId
        ) {
            this.annoDesc = annoDesc;
            this.annos = annos;
            this.linePart = lp;
            this.tts = tts;
            this.component = component;
            this.doc = doc;
            this.kit = kit;
            this.offset = offset;
            this.tooltipAttributeValue = tooltipAttributeValue;
            this.requestId = requestId;
        }
        
        public void run() {
            if (tts == null) return;
            
            if (tts == null || tts.getStatus() == ToolTipSupport.STATUS_HIDDEN) {
                return; // do nothing
            }
            if (!isRequestValid()) {
                return;
            }

            if (tts!=null) tts.addPropertyChangeListener(this);
            
            kit.toolTipAnnotationsLock(doc);
            try {
                doc.readLock();
                try {

                    if (!isRequestValid()) {
                        return;
                    }

                    // Read tooltip from highlighting layers attribute
                    String tooltipFromHighlightingLayers = null;
                    {
                        if (tooltipAttributeValue != null) {
                            if (tooltipAttributeValue instanceof String) {
                                tooltipFromHighlightingLayers = (String) tooltipAttributeValue;
                            } else if (tooltipAttributeValue instanceof HighlightAttributeValue) {
                                @SuppressWarnings("unchecked") //NOI18N
                                String value = ((HighlightAttributeValue<String>) tooltipAttributeValue).getValue(
                                    component, doc, EditorStyleConstants.Tooltip, offset, offset + 1);
                                tooltipFromHighlightingLayers = value;
                            } else {
                                LOG.fine("Invalid '" + EditorStyleConstants.Tooltip + "' attribute value " + tooltipAttributeValue); //NOI18N
                            }
                        }
                    }
                    
                    // Read tooltip from annotations
                    String tooltipFromAnnotations = null;
                    {
                        if (annos != null) {
                            // Attach tooltip annotations
                            for (int i = 0; i < annos.length; i++) {
                                annos[i].attach(linePart);
                            }

                            if (annoDesc != null) {
                                tooltipFromAnnotations = annoDesc.getShortDescription();
                                annoDesc.addPropertyChangeListener(this);
                            } else {
                                for (int i = 0; i < annos.length; i++) {
                                    String desc = annos[i].getShortDescription();
                                    if (desc != null) {
                                        tooltipFromAnnotations = desc;
                                    }
                                    annos[i].addPropertyChangeListener(this);
                                }
                            }
                        }
                    }
                    
                    // Set tooltip text, if any
                    if (tooltipFromAnnotations != null) {
                        // Ignore empty strings, but do not use highlight layers tooltip,
                        // the annotation will compute its tooltip asynchronously
                        if (tooltipFromAnnotations.length() > 0 && tts != null) {
                            tts.setToolTipText(tooltipFromAnnotations);
                        }
                    } else if (tooltipFromHighlightingLayers != null && tts != null) {
                        tts.setToolTipText(tooltipFromHighlightingLayers);
                    }
                } finally {
                    doc.readUnlock();
                }
            } finally {
                kit.toolTipAnnotationsUnlock(doc);
            }
        }
          
        private boolean isRequestValid() {
            return (getLastRequestId() == this.requestId)
                && !documentModified
                && isDocumentValid();
        }

        private boolean isDocumentValid() {
            DataObject dob = NbEditorUtilities.getDataObject(doc);
            if (dob != null) {
                EditorCookie ec = dob.getCookie(EditorCookie.class);
                if (ec != null) {
                    StyledDocument openedDoc;
                    try {
                        openedDoc = ec.openDocument();
                    } catch (IOException e) {
                        openedDoc = null; // should return in next if stmt
                    }
                    
                    return (openedDoc == doc);
                }
            }
            return false;
        }

        private void dismiss() {
            if (tts !=null) tts.removePropertyChangeListener(this);
            tts = null; // signal that support no longer valid

            if (annoDesc != null) {
                annoDesc.removePropertyChangeListener(this);
            } else if (annos != null) {
                for (int i = 0; i < annos.length; i++) {
                    annos[i].removePropertyChangeListener(this);
                    annos[i].detach();
                }
            }
        }

        public void propertyChange(PropertyChangeEvent evt) {
            String propName = evt.getPropertyName();
            if (Annotation.PROP_SHORT_DESCRIPTION.equals(propName) || AnnotationDesc.PROP_SHORT_DESCRIPTION.equals(propName)) {
                if (evt.getNewValue() != null) {
                    final String tipText = (String)evt.getNewValue();
                    Utilities.runInEventDispatchThread( // ensure to run in AWT thread
                        new Runnable() {
                            public void run() {
                                if (tts != null) {
                                    tts.setToolTipText(tipText);
                                }
                            }
                        }
                    );
                }
                
            } else if (ToolTipSupport.PROP_STATUS.equals(propName)) {
                if (((Integer)evt.getNewValue()).intValue() == ToolTipSupport.STATUS_HIDDEN) {
                    dismiss();
                }
            }
        }
        
        public void insertUpdate(DocumentEvent evt) {
            documentModified = true;
        }
        
        public void removeUpdate(DocumentEvent evt) {
            documentModified = true;
        }
        
        public void changedUpdate(DocumentEvent evt) {
        }

    } // End of Request class

}
