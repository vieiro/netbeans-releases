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

package org.netbeans.modules.parsing.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.spi.EmbeddingProvider;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.ParserResultTask;
import org.netbeans.modules.parsing.spi.Task;
import org.openide.util.Exceptions;
import org.openide.util.Parameters;

/**
 *
 * @author Tomas Zezula
 */
public class TaskProcessor {
    
    private static final Logger LOGGER = Logger.getLogger(Source.class.getName());
    
    /**Limit for task to be marked as a slow one, in ms*/
    private static final int SLOW_CANCEL_LIMIT = 50;
    
    //Scheduled requests waiting for execution
    private final static PriorityBlockingQueue<Request> requests = new PriorityBlockingQueue<Request> (10, new RequestPriorityComparator());
    //Finished requests waiting on reschedule by some scheduler or parser
    private final static Map<Source,Collection<Request>> finishedRequests = new WeakHashMap<Source,Collection<Request>>();    
    //Tasks which are scheduled (not yet executed) but blocked by expected event (waiting for event)
    private final static Map<Source,Collection<Request>> waitingRequests = new WeakHashMap<Source,Collection<Request>>();    
    //Tasked which should be cleared from requests or finieshedRequests
    private final static Collection<Task> toRemove = new LinkedList<Task> ();
    
    //Worker thread factory - single worker thread
    private final static WorkerThreadFactory factory = new WorkerThreadFactory ();
    //Currently running Task
    private final static CurrentRequestReference currentRequest = new CurrentRequestReference ();
//    private final static List<DeferredTask> todo = Collections.synchronizedList(new LinkedList<DeferredTask>());
                
    //Internal lock used to synchronize access to TaskProcessor iternal state
    private static class InternalLock {};    
    private static final Object INTERNAL_LOCK = new InternalLock ();
    
    
    //Parser lock used to prevent other tasks to run in case when there is an active task
    private final static ReentrantLock parserLock = new ReentrantLock (true);
    
    //Regexp of class names of tasks which shouldn't be scheduled - used for debugging & performance testing
    private static final Pattern excludedTasks;
    //Regexp of class names of tasks which should be scheduled - used for debugging & performance testing
    private static final Pattern includedTasks;
    
    static {
        Executors.newSingleThreadExecutor(factory).submit (new CompilationJob());
        //Initialize the excludedTasks
        Pattern _excludedTasks = null;
        try {
            String excludedValue= System.getProperty("org.netbeans.modules.parsing.impl.Source.excludedTasks");      //NOI18N
            if (excludedValue != null) {
                _excludedTasks = Pattern.compile(excludedValue);
            }
        } catch (PatternSyntaxException e) {
            Exceptions.printStackTrace(e);
        }
        excludedTasks = _excludedTasks;
        Pattern _includedTasks = null;
        try {
            String includedValue= System.getProperty("org.netbeans.modules.parsing.impl.Source.includedTasks");      //NOI18N
            if (includedValue != null) {
                _includedTasks = Pattern.compile(includedValue);
            }
        } catch (PatternSyntaxException e) {
            Exceptions.printStackTrace(e);
        }
        includedTasks = _includedTasks;
    }
    
    /** Adds a task to scheduled requests. The tasks will run sequentially.
     * @see Task for information about implementation requirements 
     * @task The task to run.
     * @source The source on which the task operates
     * @throws IOException
     */
    public static void addPhaseCompletionTask(final Task task, final Source source) throws IOException {
        Parameters.notNull("task", task);   //NOI18N
        Parameters.notNull("source", source);   //NOI18N
        final String taskClassName = task.getClass().getName();
        if (excludedTasks != null && excludedTasks.matcher(taskClassName).matches()) {
            if (includedTasks == null || !includedTasks.matcher(taskClassName).matches())
            return;
        }        
        handleAddRequest (new Request (task, source, true));
    }
    
    
    /**
     * Removes a aask from scheduled requests.
     * @param task The task to be removed.
     */
    public static void removePhaseCompletionTask(final Task task, final Source source) {
        Parameters.notNull("task", task);
        Parameters.notNull("source", source);
        final String taskClassName = task.getClass().getName();
        if (excludedTasks != null && excludedTasks.matcher(taskClassName).matches()) {
            if (includedTasks == null || !includedTasks.matcher(taskClassName).matches()) {
                return;
            }
        }
        synchronized (INTERNAL_LOCK) {
            toRemove.add (task);    //todo: Is this always needed?
            Collection<Request> rqs = finishedRequests.get(source);
            if (rqs != null) {
                for (Iterator<Request> it = rqs.iterator(); it.hasNext(); ) {
                    Request rq = it.next();
                    if (rq.task == task) {
                        it.remove();
                    }
                }
            }
        }
    }
    
    /**
     * Reschedules the task in case it was already executed.
     * Does nothing if the task was not yet executed.
     * @param task to reschedule
     * @param source to which the task it bound
     */
    public static void rescheduleTask(final Task task, final Source source) {
        Parameters.notNull("task", task);
        Parameters.notNull("source", source);
        synchronized (INTERNAL_LOCK) {
            Request request = currentRequest.getTaskToCancel (task);
            if ( request == null) {                
                Collection<Request> cr = finishedRequests.get(source);
                if (cr != null) {
                    for (Iterator<Request> it = cr.iterator(); it.hasNext();) {
                        Request fr = it.next();
                        if (task == fr.task) {
                            it.remove();
                            requests.add(fr);
                            if (cr.size()==0) {
                                it.remove();
                            }
                            break;
                        }
                    }
                }
            }
            else {
                currentRequest.cancelCompleted(request);
            }
        }
    }
    
    
    //Private methods
    private static void handleAddRequest (final Request nr) {
        assert nr != null;
        //Issue #102073 - removed running task which is readded is not performed
        synchronized (INTERNAL_LOCK) {            
            toRemove.remove(nr.task);
            requests.add (nr);
        }
        Request request = currentRequest.getTaskToCancel(nr.task.getPriority());
        try {
            if (request != null) {
                request.task.cancel();
            }
        } finally {
            currentRequest.cancelCompleted(request);
        }
    }
    
    
    //Private classes
    /**
     * Task scheduler loop
     * Dispatches scheduled tasks from {@link TaskProcessor#requests} and performs
     * them.
     */
     private static class CompilationJob implements Runnable {        
        
        @SuppressWarnings ("unchecked") //NOI18N
        public void run () {
            try {
                while (true) {                   
                    try {
                        synchronized (INTERNAL_LOCK) {
                            //Clean up toRemove tasks
                            if (!toRemove.isEmpty()) {
                                for (Iterator<Collection<Request>> it = finishedRequests.values().iterator(); it.hasNext();) {
                                    Collection<Request> cr = it.next ();
                                    for (Iterator<Request> it2 = cr.iterator(); it2.hasNext();) {
                                        Request fr = it2.next();
                                        if (toRemove.remove(fr.task)) {
                                            it2.remove();
                                        }
                                    }
                                    if (cr.size()==0) {
                                        it.remove();
                                    }
                                }
                            }
                        }
                        final Request r = requests.poll(2,TimeUnit.SECONDS);
                        if (r != null) {
                            currentRequest.setCurrentTask(r);
                            try {                            
                                final Source source = r.source;
                                if (source == null) {
                                    assert r.task instanceof ParserResultTask : "Illegal request: EmbeddingProvider has to be bound to Source";     //NOI18N
                                    parserLock.lock ();
                                    try {
                                        ((ParserResultTask)r.task).run (null, null);
                                    } catch (RuntimeException re) {
                                        Exceptions.printStackTrace(re);
                                    }
                                    finally {
                                        parserLock.unlock();
                                    }
                                }
                                else {
                                    Parser parser;
                                    synchronized (INTERNAL_LOCK) {
                                        //Not only the finishedRequests for the current request.javaSource should be cleaned,
                                        //it will cause a starvation
                                        if (toRemove.remove(r.task)) {
                                            continue;
                                        }
                                        synchronized (source) {
                                            final Set<SourceFlags> flags = SourceAccessor.getINSTANCE().getFlags(source);
                                            boolean changeExpected = flags.contains(SourceFlags.CHANGE_EXPECTED);
                                            if (changeExpected) {
                                                //Skeep the task, another invalidation is comming
                                                Collection<Request> rc = waitingRequests.get (r.source);
                                                if (rc == null) {
                                                    rc = new LinkedList<Request> ();
                                                    waitingRequests.put (r.source, rc);
                                                }
                                                rc.add(r);
                                                continue;
                                            }
                                            parser = SourceAccessor.getINSTANCE().getParser(source);
                                        }
                                    }
                                    assert parser != null;                                        
                                    parserLock.lock();
                                    try {
                                        boolean shouldCall;                                            
                                        final Parser.Result result = parser.parse(source);
                                        synchronized (source) {
                                            shouldCall = SourceAccessor.getINSTANCE().getFlags(source).contains(SourceFlags.INVALID);
                                        }
                                        if (shouldCall) {
                                            try {
                                                final long startTime = System.currentTimeMillis();
                                                if (r.task instanceof ParserResultTask) {
                                                    ((ParserResultTask)r.task).run (result,source);
                                                }
                                                else if (r.task instanceof EmbeddingProvider) {
                                                    //todo: What the embedding provider should do?
                                                }
                                                else {
                                                    assert false : "Unknown task type: " + r.task.getClass();   //NOI18N
                                                }
                                                final long endTime = System.currentTimeMillis();
                                                if (LOGGER.isLoggable(Level.FINEST)) {
                                                    LOGGER.finest(String.format("Executed task: %s in %d ms.",  //NOI18N
                                                        r.task.getClass().toString(), (endTime-startTime)));
                                                }
                                                if (LOGGER.isLoggable(Level.FINER)) {
                                                    final long cancelTime = currentRequest.getCancelTime();
                                                    if (cancelTime >= startTime && (endTime - cancelTime) > SLOW_CANCEL_LIMIT) {
                                                        LOGGER.finer(String.format("Task: %s ignored cancel for %d ms.",  //NOI18N
                                                            r.task.getClass().toString(), (endTime-cancelTime)));
                                                    }
                                                }
                                            } catch (Exception re) {
                                                Exceptions.printStackTrace (re);
                                            }
                                        }
                                    } finally {
                                        parserLock.unlock();
                                    }

                                    if (r.reschedule) {                                            
                                        synchronized (INTERNAL_LOCK) {
                                            boolean canceled = currentRequest.setCurrentTask(null);
                                            synchronized (source) {
                                                if (canceled || SourceAccessor.getINSTANCE().getFlags(source).contains(SourceFlags.INVALID)) {
                                                    //The JavaSource was changed or canceled rechedule it now
                                                    requests.add(r);
                                                }
                                                else {
                                                    //Up to date JavaSource add it to the finishedRequests
                                                    Collection<Request> rc = finishedRequests.get (r.source);
                                                    if (rc == null) {
                                                        rc = new LinkedList<Request> ();
                                                        finishedRequests.put (r.source, rc);
                                                    }
                                                    rc.add(r);
                                                }
                                            }
                                        }
                                   }
                                }
                            } finally {
                                currentRequest.setCurrentTask(null);                   
                            }
                        } 
                    } catch (Throwable e) {
                        if (e instanceof InterruptedException) {
                            throw (InterruptedException)e;
                        }
                        else if (e instanceof ThreadDeath) {
                            throw (ThreadDeath)e;
                        }
                        else {
                            Exceptions.printStackTrace(e);
                        }
                    }                    
                }
            } catch (InterruptedException ie) {
                ie.printStackTrace();
                // stop the service.
            }
        }                        
    }
    
    /**
     * Request for performing a task on given Source
     */
    //@ThreadSafe
    private static class Request {
        
        static final Request DUMMY = new Request ();
        
        private final Task task;
        private final Source source;
        private final boolean reschedule;
        
        /**
         * Creates new Request
         * @param task to be performed
         * @param source on which the task should be performed
         * @param reschedule when true the task is periodic request otherwise one time request
         */
        public Request (final Task task, final Source source, final boolean reschedule) {
            assert task != null;
            this.task = task;
            this.source = source;
            this.reschedule = reschedule;
        }
        
        private Request () {  
            task = null;
            source = null;
            reschedule = false;
        }
        
        public @Override String toString () {            
            if (reschedule) {
                return String.format("Periodic request to perform: %s on: %s",  //NOI18N
                        task == null ? null : task.toString(),
                        source == null ? null : source.toString());
            }
            else {
                return String.format("One time request to perform: %s on: %s",  //NOI18N
                        task == null ? null : task.toString(),
                        source == null ? null : source.toString());
            }
        }
        
        public @Override int hashCode () {
            return this.task == null ? 0 : this.task.getPriority();
        }
        
        public @Override boolean equals (Object other) {
            if (other instanceof Request) {
                Request otherRequest = (Request) other;
                return reschedule == otherRequest.reschedule
                    && (source == null ? otherRequest.source == null : source.equals (otherRequest.source))
                    && (task == null ? otherRequest.task == null : task.equals(otherRequest.task));
            }
            else {
                return false;
            }
        }        
    }
    
    /**
     * Comparator of {@link Request}s which oreders them using {@link Task#getPriority()}
     */
    //@ThreadSafe
    private static class RequestPriorityComparator implements Comparator<Request> {
        public int compare (Request r1, Request r2) {
            assert r1 != null && r2 != null;
            return r1.task.getPriority() - r2.task.getPriority();
        }
    }
    
    /**
     * Single thread factory creating worker thread.
     */
    //@NotThreadSafe
    private static class WorkerThreadFactory implements ThreadFactory {
        
        private Thread t;
        
        public Thread newThread(Runnable r) {
            assert this.t == null;
            this.t = new Thread (r,"Java Source Worker Thread");     //NOI18N
            return this.t;
        }
        /**
         * Checks if the given thread is a worker thread
         * @param t the thread to be checked
         * @return true when the given thread is a worker thread
         */        
        public boolean isDispatchThread (Thread t) {
            assert t != null;
            return this.t == t;
        }
    }
    
    
    /**
     *  Encapsulates current request. May be trasformed into 
     *  JavaSource private static methods, but it may be less readable.
     */
    //@ThreadSafe
    private static final class CurrentRequestReference {                        
                
        
        private Request reference;
        private Request canceledReference;
        private long cancelTime;
        private final AtomicBoolean canceled = new AtomicBoolean();
        
        boolean setCurrentTask (Request reference) throws InterruptedException {
            boolean result = false;
            synchronized (INTERNAL_LOCK) {
                while (this.canceledReference!=null) {
                    INTERNAL_LOCK.wait();
                }
                result = this.canceled.getAndSet(false);
                this.cancelTime = 0;
                this.reference = reference;
            }
            return result;
        }                
        
        Request getTaskToCancel (final int priority) {
            Request request = null;
            if (!factory.isDispatchThread(Thread.currentThread())) {
                synchronized (INTERNAL_LOCK) {
                    if (this.reference != null && priority<this.reference.task.getPriority()) {
                        assert this.canceledReference == null;
                        request = this.reference;
                        this.canceledReference = request;
                        this.reference = null;
                        this.canceled.set(true);                    
                        this.cancelTime = System.currentTimeMillis();
                    }
                }
            }
            return request;
        }
        
                
        Request getTaskToCancel (final Task task) {
            Request request = null;
            if (!factory.isDispatchThread(Thread.currentThread())) {
                synchronized (INTERNAL_LOCK) {
                    if (this.reference != null && task == this.reference.task) {
                        assert this.canceledReference == null;
                        request = this.reference;
                        this.canceledReference = request;
                        this.reference = null;
                        this.canceled.set(true);
                    }
                }
            }
            return request;
        }
        
        Request getTaskToCancel () {
            Request request = null;
            if (!factory.isDispatchThread(Thread.currentThread())) {                
                synchronized (INTERNAL_LOCK) {
                     request = this.reference;
                    if (request != null) {
                        assert this.canceledReference == null;
                        this.canceledReference = request;
                        this.reference = null;
                        this.canceled.set(true);
                        this.cancelTime = System.currentTimeMillis();
                    }
                }
            }
            return request;
        }
                        
        boolean isCanceled () {
            synchronized (INTERNAL_LOCK) {
                return this.canceled.get();
            }
        }                
        
        long getCancelTime () {
            synchronized (INTERNAL_LOCK) {
                return this.cancelTime;
            }
        }
        
        void cancelCompleted (final Request request) {
            if (request != null) {
                synchronized (INTERNAL_LOCK) {
                    assert request == this.canceledReference;
                    this.canceledReference = null;
                    INTERNAL_LOCK.notify();
                }
            }
        }
    }

}
