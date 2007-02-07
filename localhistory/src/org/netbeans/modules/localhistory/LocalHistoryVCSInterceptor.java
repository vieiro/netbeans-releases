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
package org.netbeans.modules.localhistory;

import java.util.HashMap;
import java.io.File;  
import java.io.IOException;       
import java.util.HashSet;
import java.util.Map;        
import java.util.Set;
import org.netbeans.modules.localhistory.store.LocalHistoryStore;
import org.netbeans.modules.versioning.spi.VCSInterceptor;

/**       
 * 
 * Listens to file system operations from the IDE and eventually handles them synchronously
 * 
 * @author Tomas Stupka
 */
class LocalHistoryVCSInterceptor extends VCSInterceptor {
        
    private class StorageMoveHandler {
        private long ts = -1;
        
        private final File from;
        private final File to;
        
        StorageMoveHandler(File from, File to) {
            this.from = from;
            this.to = to;            
        }
        
        public void delete() {            
            getStore().fileDeleteFromMove(from, to, ts);
        }
        
        public void create() {
            ts = to.lastModified(); 
            getStore().fileCreateFromMove(from, to, ts);            
        }         
    }; 
    
    private LocalHistoryStore getStore() {
        return LocalHistory.getInstance().getLocalHistoryStore();
    }
    
    private Map<String, StorageMoveHandler> moveHandlerMap;
    
    /** Creates a new instance of LocalHistoryVCSInterceptor */
    public LocalHistoryVCSInterceptor() {
        
    }    
    
    // ==================================================================================================
    // DELETE
    // ==================================================================================================
    Set<File> toBeDeleted = new HashSet<File>(5); // XXX
    public boolean beforeDelete(File file) {
        if(!accept(file)) {
            return false;
        }
        toBeDeleted.add(file);
        storeFile(file); // will be stored in the history if there is no actuall entry yet        
        return false;
    }
    
    public void doDelete(File file) throws IOException {
        // do nothing
    }

    public void afterDelete(File file) {      
        if(!toBeDeleted.remove(file)) {
            return;
        }               
        
        String key = file.getAbsolutePath();
        if(getMoveHandlerMap().containsKey(key)) {
            StorageMoveHandler handler = getMoveHandlerMap().get(key);
            try {
                handler.delete();
            } finally {
                getMoveHandlerMap().remove(key);
            }                            
        } else {            
            getStore().fileDelete(file, System.currentTimeMillis());                        
        }        
    }
    
    // ==================================================================================================
    // MOVE
    // ==================================================================================================
    
    public boolean beforeMove(final File from, final File to) {
        if(!accept(from)) {
            return false;
        }
                
        // moving a package comes either like  
        // - create(to) and delete(from)
        // - or the files from the package come like move(from, to)
        StorageMoveHandler handler = new StorageMoveHandler(from, to);
        getMoveHandlerMap().put(to.getAbsolutePath(), handler);        
        getMoveHandlerMap().put(from.getAbsolutePath(), handler);                    
        return false;    
    }

    public void doMove(File from, File to) throws IOException {
        // do nothing
    }

    public void afterMove(File from, File to) {       
        if(!accept(from)) {
            return;
        }
        
        String key = to.getAbsolutePath();
        if(getMoveHandlerMap().containsKey(key)) {
            StorageMoveHandler handler = getMoveHandlerMap().get(key);
            try {
                handler.create();
                handler.delete();
            } finally {
                getMoveHandlerMap().remove(key);    
                getMoveHandlerMap().remove(from.getAbsolutePath());    
            }            
        }   
    }
    
    // ==================================================================================================
    // CREATE
    // ==================================================================================================

    public boolean beforeCreate(File file, boolean isDirectory) {
        // do nothing
        return false;
    }

    public void doCreate(File file, boolean isDirectory) throws IOException {
        // do nothing  
    }

    public void afterCreate(File file) {                       
        if(!accept(file)) {
            return;
        }
                
        if(file.length() == 0) {            
            return;
        }
        
        String key = file.getAbsolutePath();
        if(getMoveHandlerMap().containsKey(key)) {
            StorageMoveHandler handler = getMoveHandlerMap().get(key);
            try {
                handler.create();
            } finally {
                getMoveHandlerMap().remove(key);
            }            
        } else {
            getStore().fileCreate(file, file.lastModified());            
        }               
    }
    
    // ==================================================================================================
    // CHANGE
    // ==================================================================================================
    
    public void afterChange(File file) {    
        if(!accept(file)) {
            return;
        }        
        storeFile(file);
    }

    public void beforeChange(File file) {    
        // XXX file.exists() is a hack
        if(file.exists() && !accept(file)) {
            return;
        }        
        storeFile(file);
    }
    
    private void storeFile(File file) {        
        getStore().fileChange(file, file.lastModified());
    }
        
    private Map<String, StorageMoveHandler> getMoveHandlerMap() {
        if(moveHandlerMap == null) {
            moveHandlerMap = new HashMap<String, StorageMoveHandler>();
        }
        return moveHandlerMap;
    }
    
    /**
     * 
     * Decides if a file has to be stored in the Local History or not.
     * 
     * @param file the file to be stored
     * @param checkTimestamp checks if 
     * 
     * @return true if the file has to be stored in the Local History, otherwise false 
     */
    private boolean accept(File file) {       
        if(file.lastModified() > 0 &&                  
           file.lastModified() < System.currentTimeMillis() - LocalHistorySettings.getTTL()) 
        {
            return false;
        }
        
        if(!LocalHistory.getInstance().isManaged(file)) {
            return false;
        }               
                      
        return true;
    }    
    
}
