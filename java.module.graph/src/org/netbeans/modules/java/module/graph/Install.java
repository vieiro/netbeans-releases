/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.java.module.graph;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.atomic.AtomicReference;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.core.api.multiview.MultiViewHandler;
import org.netbeans.core.api.multiview.MultiViewPerspective;
import org.netbeans.core.api.multiview.MultiViews;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.modules.OnStart;
import org.openide.modules.OnStop;
import org.openide.util.lookup.Lookups;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author Tomas Zezula
 */
public final class Install {

    private static final AtomicReference<PropertyChangeListener> listenerRef = new AtomicReference<>();

    private Install() {
        throw new IllegalStateException("No instance allowed");     //NOI18N
    }

    @OnStart
    public static final class Start implements Runnable {
        @Override
        public void run() {
            WindowManager.getDefault().invokeWhenUIReady(() -> {
                final PropertyChangeListener listener = new RegListener();
                if (listenerRef.compareAndSet(null, listener)) {
                    WindowManager.getDefault().getRegistry().addPropertyChangeListener(listener);
                }
            });
        }
    }

    @OnStop
    public static final class Stop implements Runnable {
        @Override
        public void run() {
            final PropertyChangeListener listener = listenerRef.get();
            if (listener != null && listenerRef.compareAndSet(listener, null)) {
                WindowManager.getDefault().getRegistry().removePropertyChangeListener(listener);
            }
        }
    }

    private static final class RegListener implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (TopComponent.Registry.PROP_TC_OPENED.equals(evt.getPropertyName())) {
                final Object newValue = evt.getNewValue();
                if (newValue instanceof TopComponent) {
                    final TopComponent opened = (TopComponent) newValue;
                    final FileObject moduleInfo = findModuleInfo(opened);
                    if (moduleInfo != null) {
                        final MultiViewHandler multiViewHandler = MultiViews.findMultiViewHandler(opened);
                        if (multiViewHandler != null) {
                            final MultiViewPerspective current = multiViewHandler.getSelectedPerspective();
                            multiViewHandler.addMultiViewDescription(
                                    new GraphMultiViewDescription(Lookups.fixed(moduleInfo)),
                                    -1);
                            multiViewHandler.requestActive(current);
                        }
                    }
                }
            }
        }

        @CheckForNull
        private static FileObject findModuleInfo(@NonNull final TopComponent tc) {
            FileObject file = tc.getLookup().lookup(FileObject.class);
            if (file == null) {
                final DataObject dobj = tc.getLookup().lookup(DataObject.class);
                if (dobj != null) {
                    file = dobj.getPrimaryFile();
                }
            }
            return file != null && file.getNameExt().equals("module-info.java") ?   //NOI18N
                    file:
                    null;
        }
    }
}
