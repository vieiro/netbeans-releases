/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.parsing.impl;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Callable;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.annotations.common.NullAllowed;
import org.netbeans.modules.masterfs.providers.ProvidedExtensions;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.impl.event.EventSupport;
import org.netbeans.modules.parsing.impl.indexing.RepositoryUpdater;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.ParserResultTask;
import org.netbeans.modules.parsing.spi.Scheduler;
import org.netbeans.modules.parsing.spi.SchedulerTask;
import org.openide.util.Pair;
import org.openide.util.Parameters;

/**
 * Temporary helper functions needed by the java.source
 * @author Tomas Zezula
 */
public class Utilities {

    private static final ThreadLocal<Parser.CancelReason> cancelReason = new ThreadLocal<Parser.CancelReason>();
    
    private Utilities () {}

    //MasterFS bridge
    public static <T> T runPriorityIO (final Callable<T> r) throws Exception {
        assert r != null;
        return ProvidedExtensions.priorityIO(r);
    }

    //Helpers for java reformatter, may be removed when new reformat api will be done
    public static void acquireParserLock () {
        TaskProcessor.acquireParserLock();
    }

    public static void releaseParserLock () {
        TaskProcessor.releaseParserLock();
    }

    //Helpers for asserts in java.source    
    public static boolean holdsParserLock () {
        return TaskProcessor.holdsParserLock();
    }

    /**
     * Returns true if given thread is a TaskProcessor dispatch thread.
     * @param Thread thread
     * @return boolean
     */
    public static boolean isTaskProcessorThread (final Thread thread) {
        Parameters.notNull("thread", thread);
        return TaskProcessor.factory.isDispatchThread(thread);
    }

    //Helpers for indexing in java.source, will be removed when indexing will be part of parsing api
    /**
     * Temporary may be replaced by scheduler, hepefully.
     */
    public static void scheduleSpecialTask (final Runnable runnable, int priority) {
        TaskProcessor.scheduleSpecialTask(runnable, priority);
    }
    
    public static void runAsScanWork(@NonNull Runnable work) {
        Parameters.notNull("work", work);   //NOI18N
        RepositoryUpdater.getDefault().runAsWork(work);
    }
    
    /**
     * Sets the {@link IndexingStatus}
     * @param st an {@link IndexingStatus}
     */
    public static void setIndexingStatus (final IndexingStatus st) {
        assert st != null;
        assert status == null;
        status = st;
    }

    public static Set<? extends RepositoryUpdater.IndexingState> getIndexingState() {
        if (status == null) {
            return RepositoryUpdater.getDefault().getIndexingState();
        } else {
            return status.getIndexingState();
        }
    }

    /**
     * Asks the {@link IndexingStatus} about state of indexing
     * @return true when indexing is active
     */
    public static boolean isScanInProgress () {
        return !getIndexingState().isEmpty();        
    }
    //where
    private static volatile IndexingStatus status;

    /**
     * Provides state of indexing
     */
    public static interface IndexingStatus {
        Set<? extends RepositoryUpdater.IndexingState> getIndexingState ();
    }

    //Helpers to bridge java.source factories into parsing.api
    public static void revalidate (final Source source) {
        final EventSupport support = SourceAccessor.getINSTANCE().getEventSupport(source);
        assert support != null;
        support.resetState(true, false, -1, -1, false);
    }
    
    public static void addParserResultTask (final ParserResultTask<?> task, final Source source) {
        Parameters.notNull ("task", task);
        Parameters.notNull ("source", source);
        final SourceCache cache = SourceAccessor.getINSTANCE ().getCache (source);
        TaskProcessor.addPhaseCompletionTasks (
            Collections.<Pair<SchedulerTask,Class<? extends Scheduler>>>singleton(Pair.<SchedulerTask,Class<? extends Scheduler>>of(task,null)),
            cache,
            true);
    }
    
    public static void removeParserResultTask (final ParserResultTask<?> task, final Source source) {
        Parameters.notNull ("task", task);
        Parameters.notNull ("source", source);
        TaskProcessor.removePhaseCompletionTasks(Collections.singleton(task), source);
    }
    
    public static void rescheduleTask (final ParserResultTask<?> task, final Source source) {
        Parameters.notNull ("task", task);
        Parameters.notNull ("source", source);
        TaskProcessor.rescheduleTasks (Collections.<SchedulerTask>singleton (task), source, null);
    }
    

    //Internal API among TaskProcessor and RepositoryUpdater
    //If SchedulerTask will need the information about cancel
    //add the CancelReason parameter into cancel like it's in Parser
    public static Parser.CancelReason getTaskCancelReason() {
        return cancelReason.get();
    }

    static void setTaskCancelReason(final @NullAllowed Parser.CancelReason reason) {
        if (reason == null) {
            cancelReason.remove();
        } else {
            cancelReason.set(reason);
        }
    }
}
