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

package org.netbeans.modules.editor.settings.storage.api;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import org.netbeans.api.editor.settings.EditorStyleConstants;
import org.netbeans.modules.editor.settings.storage.*;


/**
 * Getters and setters for font & color editor profiles. Instances of this 
 * class should be registerred in {@MimeLookup} for particular mime types.
 *
 * @author Jan Jancura
 */
public abstract class FontColorSettingsFactory {

    /**
     * Gets all token font and colors for given scheme or null, if 
     * scheme does not exists. 
     * 
     * @param profile the name of profile
     *
     * @return token font and colors
     */
    public abstract Collection /*<AttributeSet>*/ getAllFontColors (String profile);
    
    /**
     * Gets default values for all font & colors for given profile, or null
     * if profile does not exist or if it does not have any defaults. 
     * 
     * @param profile the name of profile
     *
     * @return default values for all font & colors
     */
    public abstract Collection /*<AttributeSet>*/ getAllFontColorDefaults 
        (String profile);
    
    /**
     * Sets all token font and colors for given scheme. 
     * 
     * @param profile the name of profile
     * @param fontColors new colorings
     */
    public abstract void setAllFontColors (
        String profile,
        Collection /*<AttributeSet>*/ fontColors
    );
    
    /**
     * Sets default values for all token font and colors for given scheme. 
     * 
     * @param profile the name of profile
     * @param fontColors new colorings
     */
    public abstract void setAllFontColorsDefaults (
        String profile,
        Collection /*<AttributeSet>*/ fontColors
    );
}
