/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */
package org.netbeans.modules.cnd.modelimpl.csm.core;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.cnd.api.model.CsmFile;
import org.netbeans.modules.cnd.api.model.CsmUID;
import org.netbeans.modules.cnd.api.project.NativeFileItem;
import org.netbeans.modules.cnd.api.project.NativeProject;
import org.netbeans.modules.cnd.apt.support.APTDriver;
import org.netbeans.modules.cnd.apt.support.APTFileCacheManager;
import org.netbeans.modules.cnd.modelimpl.debug.Diagnostic;
import org.netbeans.modules.cnd.modelimpl.debug.DiagnosticExceptoins;
import org.netbeans.modules.cnd.modelimpl.debug.TraceFlags;
import org.netbeans.modules.cnd.modelutil.NamedEntity;
import org.netbeans.modules.cnd.modelutil.NamedEntityOptions;
import org.netbeans.modules.cnd.utils.cache.CndFileUtils;
import org.openide.util.RequestProcessor;
import org.openide.util.RequestProcessor.Task;

/**
 * Project implementation
 * @author Vladimir Kvashin
 */
public final class ProjectImpl extends ProjectBase {

    private ProjectImpl(ModelImpl model, Object platformProject, String name) {
        super(model, platformProject, name);
    // RepositoryUtils.put(this);
    }

    public static ProjectImpl createInstance(ModelImpl model, String platformProject, String name) {
        return createInstance(model, (Object) platformProject, name);
    }

    public static ProjectImpl createInstance(ModelImpl model, NativeProject platformProject, String name) {
        return createInstance(model, (Object) platformProject, name);
    }

    private static ProjectImpl createInstance(ModelImpl model, Object platformProject, String name) {
        ProjectBase instance = null;
        if (TraceFlags.PERSISTENT_REPOSITORY) {
            try {
                instance = readInstance(model, platformProject, name);
            } catch (Exception e) {
                // just report to console;
                // the code below will create project "from scratch"
                cleanRepository(platformProject, false);
                DiagnosticExceptoins.register(e);
            }
        }
        if (instance == null) {
            instance = new ProjectImpl(model, platformProject, name);
        }
        return (ProjectImpl) instance;
    }

    @Override
    protected final ParserQueue.Position getIncludedFileParserQueuePosition() {
        return ParserQueue.Position.HEAD;
    }

    public 
    @Override
    void onFileEditStart(final FileBuffer buf, NativeFileItem nativeFile) {
        if (!acceptNativeItem(nativeFile)) {
            return;
        }
        if (TraceFlags.DEBUG) {
            Diagnostic.trace("------------------------- onFileEditSTART " + buf.getFile().getName()); //NOI18N
        }
        final FileImpl impl = createOrFindFileImpl(buf, nativeFile);
        if (impl != null) {
            APTDriver.getInstance().invalidateAPT(buf);
            APTFileCacheManager.invalidate(buf);
            synchronized (editedFiles) {
                if (!editedFiles.containsKey(impl)) {
                    // register edited file
                    editedFiles.put(impl, null);
                }
            }
//            scheduleParseOnEditing(buf, impl);
            buf.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {
                    scheduleParseOnEditing(buf, impl);
                }
            });
            impl.setBuffer(buf);
        }
    }

    public 
    @Override
    void onFileEditEnd(FileBuffer buf, NativeFileItem nativeFile) {
        if (!acceptNativeItem(nativeFile)) {
            return;
        }
        if (TraceFlags.DEBUG) {
            Diagnostic.trace("------------------------- onFileEditEND " + buf.getFile().getName()); //NOI18N
        }
        FileImpl file = getFile(buf.getFile(), false);
        if (file != null) {
            synchronized (editedFiles) {
                Task task = editedFiles.remove(file);
                if (task != null) {
                    task.cancel();
                } else {
                    // FixUp double file edit end on mounted files
                    return;
                }
            }
            file.setBuffer(buf);
//            file.clearStateCache();
            // no need for deep parsing util call here, because it will be called as external notification change anyway
//            DeepReparsingUtils.reparseOnEdit(file, this);
        }
    }

    private void addToQueueOnEditing(FileBuffer buf, FileImpl file) {
        if (isDisposing()) {
            return;
        }
        DeepReparsingUtils.reparseOnEditingFile(this, file, buf);
    }

    @Override
    public void onFilePropertyChanged(NativeFileItem nativeFile) {
        if (!acceptNativeItem(nativeFile)) {
            return;
        }
        if (TraceFlags.DEBUG) {
            Diagnostic.trace("------------------------- onFilePropertyChanged " + nativeFile.getFile().getName()); //NOI18N
        }
        DeepReparsingUtils.reparseOnPropertyChanged(nativeFile, this);
    }

    @Override
    public void onFilePropertyChanged(List<NativeFileItem> items) {
        if (items.size() > 0) {
            DeepReparsingUtils.reparseOnPropertyChanged(items, this);
        }
    }

    @Override
    public void onFileRemoved(FileImpl impl) {
        try {
            //Notificator.instance().startTransaction();
            onFileRemovedImpl(impl);
            if (impl != null) {
                DeepReparsingUtils.reparseOnRemoved(impl, this);
            }
        } finally {
            //Notificator.instance().endTransaction();
            Notificator.instance().flush();
        }
    }

    @Override
    public void onFileImplRemoved(List<FileImpl> files) {
        for (FileImpl impl : files) {
            onFileRemovedImpl(impl);
        }
        DeepReparsingUtils.reparseOnRemoved(files, this);
    }

    private FileImpl onFileRemovedImpl(FileImpl impl) {
        CndFileUtils.clearFileExistenceCache();
        if (impl != null) {
            synchronized (editedFiles) {
                Task task = editedFiles.remove(impl);
                if (task != null) {
                    task.cancel();
                }
            }
            removeNativeFileItem(impl.getUID());
            impl.dispose();
            removeFile(impl.getAbsolutePath());
            APTDriver.getInstance().invalidateAPT(impl.getBuffer());
            APTFileCacheManager.invalidate(impl.getBuffer());
            ParserQueue.instance().remove(impl);
        }
        return impl;
    }

    @Override
    public void onFileRemoved(List<NativeFileItem> items) {
        try {
            ParserQueue.instance().onStartAddingProjectFiles(this);
            List<FileImpl> toReparse = new ArrayList<FileImpl>();
            for (NativeFileItem item : items) {
                File file = item.getFile();
                try {
                    //Notificator.instance().startTransaction();
                    FileImpl impl = getFile(file, false);
                    if (impl != null) {
                        onFileRemovedImpl(impl);
                        toReparse.add(impl);
                    }
                } finally {
                    //Notificator.instance().endTransaction();
                    Notificator.instance().flush();
                }
            }
            DeepReparsingUtils.reparseOnRemoved(toReparse, this);
        } finally {
            ParserQueue.instance().onEndAddingProjectFiles(this);
        }
    }

    @Override
    public void onFileAdded(NativeFileItem nativeFile) {
        onFileAddedImpl(nativeFile, true);
    }

    private NativeFileItem onFileAddedImpl(NativeFileItem nativeFile, boolean deepReparse) {
        if (acceptNativeItem(nativeFile)) {
            CndFileUtils.clearFileExistenceCache();
            try {
                //Notificator.instance().startTransaction();
                createIfNeed(nativeFile, isSourceFile(nativeFile));
                return nativeFile;
            } finally {
                //Notificator.instance().endTransaction();
                Notificator.instance().flush();
                if (deepReparse) {
                    DeepReparsingUtils.reparseOnAdded(nativeFile, this);
                }
            }
        }
        return null;
    }

    @Override
    public void onFileAdded(List<NativeFileItem> items) {
        try {
            ParserQueue.instance().onStartAddingProjectFiles(this);
            List<NativeFileItem> toReparse = new ArrayList<NativeFileItem>();
            for (NativeFileItem item : items) {
                NativeFileItem done = onFileAddedImpl(item, false);
                if (done != null) {
                    toReparse.add(done);
                }
            }
            DeepReparsingUtils.reparseOnAdded(toReparse, this);
        } finally {
            ParserQueue.instance().onEndAddingProjectFiles(this);
        }
    }

    protected 
    @Override
    void ensureChangedFilesEnqueued() {
        synchronized (editedFiles) {
            super.ensureChangedFilesEnqueued();
            for (Iterator iter = editedFiles.keySet().iterator(); iter.hasNext();) {
                FileImpl file = (FileImpl) iter.next();
                if (!file.isParsingOrParsed()) {
                    ParserQueue.instance().add(file, getPreprocHandler(file.getBuffer().getFile()).getState(), ParserQueue.Position.TAIL);
                }
            }
        }
    //N.B. don't clear list of editedFiles here.
    }

    protected 
    @Override
    boolean hasChangedFiles(CsmFile skipFile) {
        if (skipFile == null) {
            return false;
        }
        synchronized (editedFiles) {
            for (Iterator iter = editedFiles.keySet().iterator(); iter.hasNext();) {
                FileImpl file = (FileImpl) iter.next();
                if ((skipFile != file) && !file.isParsingOrParsed()) {
                    return true;
                }
            }
        }
        return false;
    }
    private final Map<CsmFile, RequestProcessor.Task> editedFiles = new HashMap<CsmFile, RequestProcessor.Task>();

    public 
    @Override
    ProjectBase findFileProject(CharSequence absPath) {
        ProjectBase retValue = super.findFileProject(absPath);
        // trick for tracemodel. We should accept all not registered files as well, till it is not system one.
        if (retValue == null && ParserThreadManager.instance().isStandalone()) {
            retValue = absPath.toString().startsWith("/usr") ? retValue : this; // NOI18N
        }
        return retValue;
    }

    @Override
    public boolean isArtificial() {
        return false;
    }

    @Override
    public NativeFileItem getNativeFileItem(CsmUID<CsmFile> file) {
        return nativeFiles.getNativeFileItem(file);
    }

    @Override
    protected void putNativeFileItem(CsmUID<CsmFile> file, NativeFileItem nativeFileItem) {
        nativeFiles.putNativeFileItem(file, nativeFileItem);
    }

    @Override
    protected void removeNativeFileItem(CsmUID<CsmFile> file) {
        nativeFiles.removeNativeFileItem(file);
    }

    @Override
    protected void clearNativeFileContainer() {
        nativeFiles.clear();
    }
    private final NativeFileContainer nativeFiles = new NativeFileContainer();

    ////////////////////////////////////////////////////////////////////////////
    // impl of persistent
    public 
    @Override
    void write(DataOutput aStream) throws IOException {
        super.write(aStream);
        // we don't need this since ProjectBase persists fqn
        //UIDObjectFactory aFactory = UIDObjectFactory.getDefaultFactory();
        //aFactory.writeUID(getUID(), aStream);
        LibraryManager.getInstance().writeProjectLibraries(getUID(), aStream);
    }

    public ProjectImpl(DataInput input) throws IOException {
        super(input);
        // we don't need this since ProjectBase persists fqn
        //UIDObjectFactory aFactory = UIDObjectFactory.getDefaultFactory();
        //CsmUID uid = aFactory.readUID(input);
        //LibraryManager.getInsatnce().read(uid, input);
        LibraryManager.getInstance().readProjectLibraries(getUID(), input);
    //nativeFiles = new NativeFileContainer();
    }

    ////////////////////////////////////////////////////////////////////////////
    private final static RequestProcessor RP = new RequestProcessor("ProjectImpl RP", 50); // NOI18N
    private void scheduleParseOnEditing(final FileBuffer buf, final FileImpl file) {
        RequestProcessor.Task task;
        int delay;
        synchronized (editedFiles) {
            task = editedFiles.get(file);
            if (task != null) {
                if (TraceFlags.TRACE_182342_BUG) {
                    if (!task.isFinished()) {
                        new Exception("cancelling previous parse on edit task " + task.hashCode()).printStackTrace(System.err); // NOI18N
                    } else {
                        new Exception("previous parse on edit task was finished " + task.hashCode()).printStackTrace(System.err); // NOI18N
                    }
                }
                task.cancel();
            }
            if (TraceFlags.TRACE_182342_BUG) {
                for (CsmFile csmFile : editedFiles.keySet()) {
                    System.err.println("edited file " + csmFile);
                }
                System.err.println("current file " + file);
            }
             task = RP.create(new Runnable() {

                @Override
                public void run() {
                    try {
                        if (TraceFlags.TRACE_182342_BUG) {
                            System.err.printf("started scheduleParseOnEditing task for %s %s\n", file, buf);
                        }
                        addToQueueOnEditing(buf, file);
                    } catch (AssertionError ex) {
                        DiagnosticExceptoins.register(ex);
                    } catch (Exception ex) {
                        DiagnosticExceptoins.register(ex);
                    }
                }
            }, true);
            if (TraceFlags.TRACE_182342_BUG) {
                new Exception("created new parse on edit task " + task.hashCode()).printStackTrace(System.err);// NOI18N
            }
            task.setPriority(Thread.MIN_PRIORITY);
            delay = TraceFlags.REPARSE_DELAY;
            boolean doReparse = NamedEntityOptions.instance().isEnabled(new NamedEntity() {
                @Override
                public String getName() {
                    return "reparse-on-document-changed"; //NOI18N
                }
                @Override
                public boolean isEnabledByDefault() {
                    return true;
                }
            });
            if (doReparse) {
                if (file.getLastParseTime() / (delay+1) > 2) {
                    delay = Math.max(delay, file.getLastParseTime()+2000);
                }
            } else {
                delay = Integer.MAX_VALUE;
            }
            editedFiles.put(file, task);
        }
        task.schedule(delay);
    }

    @Override
    public void setDisposed() {
        super.setDisposed();
        synchronized (editedFiles) {
            for (Task task : editedFiles.values()) {
                if (task != null) {
                    task.cancel();
                }
            }
            editedFiles.clear();
        }
    }
}
