/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2008 Sun
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
package org.netbeans.api.extexecution;

import org.netbeans.modules.extexecution.InputOutputManager;
import org.netbeans.modules.extexecution.StopAction;
import org.netbeans.modules.extexecution.RerunAction;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.Reader;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.api.extexecution.ExecutionDescriptor.InputProcessorFactory;
import org.netbeans.api.extexecution.ExecutionDescriptor.InputProcessorFactory2;
import org.netbeans.api.extexecution.ExecutionDescriptor.LineConvertorFactory;
import org.netbeans.api.extexecution.base.BackgroundDescriptor;
import org.netbeans.api.extexecution.base.BackgroundService;
import org.netbeans.api.extexecution.base.ParametrizedRunnable;
import org.netbeans.api.extexecution.base.input.InputProcessor;
import org.netbeans.api.extexecution.base.input.InputProcessors;
import org.netbeans.api.extexecution.print.LineProcessors;
import org.netbeans.modules.extexecution.input.BaseInputProcessor;
import org.netbeans.modules.extexecution.input.DelegatingInputProcessor;
import org.openide.util.Cancellable;
import org.openide.util.Mutex;
import org.openide.util.NbBundle;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;

/**
 * Execution service provides the facility to execute the process while
 * displaying the output and handling the input.
 * <p>
 * It will execute the program with an associated I/O window, with stop and
 * restart buttons. It will also obey various descriptor properties such as
 * whether or not to show a progress bar.
 * <p>
 * All processes launched by this class are terminated on VM exit (if
 * these are not finished or terminated earlier).
 * <p>
 * Note that once service is run for the first time, subsequents runs can be
 * invoked by the user (rerun button) if it is allowed to do so
 * ({@link ExecutionDescriptor#isControllable()}).
 *
 * <div class="nonnormative">
 * <p>
 * Sample usage - executing ls command:
 * <pre>
 *     ExecutionDescriptor descriptor = new ExecutionDescriptor()
 *          .frontWindow(true).controllable(true);
 *
 *     ExternalProcessBuilder processBuilder = new ExternalProcessBuilder("ls");
 *
 *     ExecutionService service = ExecutionService.newService(processBuilder, descriptor, "ls command");
 *     Future&lt;Integer&gt task = service.run();
 * </pre>
 * </div>
 *
 * @author Petr Hejl
 * @see #newService(java.util.concurrent.Callable, org.netbeans.api.extexecution.ExecutionDescriptor, java.lang.String)
 * @see ExecutionDescriptor
 */
public final class ExecutionService {

    private static final Logger LOGGER = Logger.getLogger(ExecutionService.class.getName());

    static {
        // rerun accessor
        RerunAction.Accessor.setDefault(new RerunAction.Accessor() {

            @Override
            public Future<Integer> run(ExecutionService service, InputOutput required) {
                return service.run(required);
            }
        });
    }

    private final Callable<Process> processCreator;

    private final ExecutionDescriptor descriptor;

    private final String originalDisplayName;

    private ExecutionService(Callable<Process> processCreator, String displayName, ExecutionDescriptor descriptor) {
        this.processCreator = processCreator;
        this.originalDisplayName = displayName;
        this.descriptor = descriptor;
    }

    /**
     * Creates new execution service. Service will wrap up the processes
     * created by <code>processCreator</code> and will manage them.
     *
     * @param processCreator callable returning the process to wrap up
     * @param descriptor descriptor describing the configuration of service
     * @param displayName display name of this service
     * @return new execution service
     */
    @NonNull
    public static ExecutionService newService(@NonNull Callable<Process> processCreator,
            @NonNull ExecutionDescriptor descriptor, @NonNull String displayName) {
        return new ExecutionService(processCreator, displayName, descriptor);
    }

    /**
     * Runs the process described by this service. The call does not block
     * and the task is represented by the returned value. Integer returned
     * as a result of the {@link Future} is exit code of the process.
     * <p>
     * The output tabs are reused (if caller does not use the custom one,
     * see {@link ExecutionDescriptor#getInputOutput()}) - the tab to reuse
     * (if any) is selected by having the same name and same buttons
     * (control and option). If there is no output tab to reuse new one
     * is opened.
     * <p>
     * This method can be invoked multiple times returning the different and
     * unrelated {@link Future}s. On each call <code>Callable&lt;Process&gt;</code>
     * passed to {@link #newService(java.util.concurrent.Callable, org.netbeans.api.extexecution.ExecutionDescriptor, java.lang.String)}
     * is invoked in order to create the process. If the process creation fails
     * (throwing an exception) returned <code>Future</code> will throw
     * {@link java.util.concurrent.ExecutionException} on {@link Future#get()}
     * request.
     * <p>
     * For details on execution control see {@link ExecutionDescriptor}.
     *
     * @return task representing the actual run, value representing result
     *             of the {@link Future} is exit code of the process
     */
    @NonNull
    public Future<Integer> run() {
        return run(null);
    }

    private Future<Integer> run(InputOutput required) {
        final InputOutputManager.InputOutputData ioData = getInputOutput(required);

        final String displayName = ioData.getDisplayName();
        final ProgressCancellable cancellable = descriptor.isControllable() ? new ProgressCancellable() : null;
        final ProgressHandle handle = createProgressHandle(ioData.getInputOutput(), displayName, cancellable);
        final InputOutput io = ioData.getInputOutput();

        final OutputWriter out = io.getOut();
        final OutputWriter err = io.getErr();
        final Reader in = io.getIn();

        class ExecutedHolder {
            private boolean executed = false;
        }
        final ExecutedHolder executed = new ExecutedHolder();

        BackgroundDescriptor realDescriptor = new BackgroundDescriptor();
        realDescriptor = realDescriptor.charset(descriptor.getCharset());
        realDescriptor = realDescriptor.inReader(in);
        realDescriptor = realDescriptor.preExecution(new Runnable() {

            @Override
            public void run() {
                executed.executed = true;
                Runnable orig = descriptor.getPreExecution();
                if (orig != null) {
                    orig.run();
                }
            }
        });
        realDescriptor = realDescriptor.postExecution(new ParametrizedRunnable<Integer>() {

            @Override
            public void run(Integer parameter) {
                cleanup(handle, ioData, ioData.getInputOutput() != descriptor.getInputOutput(),
                        descriptor.isFrontWindowOnError() && parameter != null && parameter != 0);
                Runnable orig = descriptor.getPostExecution();
                if (orig != null) {
                    orig.run();
                }
            }
        });
        realDescriptor = realDescriptor.outProcessorFactory(new BackgroundDescriptor.InputProcessorFactory() {

            @Override
            public InputProcessor newInputProcessor() {
                return createOutProcessor(out);
            }
        });
        realDescriptor = realDescriptor.errProcessorFactory(new BackgroundDescriptor.InputProcessorFactory() {

            @Override
            public InputProcessor newInputProcessor() {
                return createErrProcessor(err);
            }
        });
        
        BackgroundService service = BackgroundService.newService(processCreator, realDescriptor);
        final Future<Integer> delegate = service.run();
        
        final Future<Integer> current = new Future<Integer>() {

            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                boolean ret = delegate.cancel(mayInterruptIfRunning);
                if (!executed.executed) {
                    // not executed at all - passing false to show
                    cleanup(handle, ioData, false);

                    synchronized (InputOutputManager.class) {
                        if (ioData.getInputOutput() != descriptor.getInputOutput()) {
                            InputOutputManager.addInputOutput(ioData);
                        }
                    }
                } else {
// uncomment this if state after cancel should be the same as when completed normally
//                    if (ret && mayInterruptIfRunning && executed.executed) {
//                        try {
//                            latch.await();
//                        } catch (InterruptedException ex) {
//                            Thread.currentThread().interrupt();
//                        }
//                    }
                }
                return ret;
            }

            @Override
            public boolean isCancelled() {
                return delegate.isCancelled();
            }

            @Override
            public boolean isDone() {
                return delegate.isDone();
            }

            @Override
            public Integer get() throws InterruptedException, ExecutionException {
                return delegate.get();
            }

            @Override
            public Integer get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                return delegate.get(timeout, unit);
            }
        };

        // TODO cleanup
        final StopAction workingStopAction = ioData.getStopAction();
        final RerunAction workingRerunAction = ioData.getRerunAction();

        Mutex.EVENT.readAccess(new Runnable() {
            @Override
            public void run() {
                if (workingStopAction != null) {
                    synchronized (workingStopAction) {
                        workingStopAction.setTask(current);
                        workingStopAction.setEnabled(true);
                    }
                }

                if (workingRerunAction != null) {
                    synchronized (workingRerunAction) {
                        workingRerunAction.setExecutionService(ExecutionService.this);
                        workingRerunAction.setRerunCondition(descriptor.getRerunCondition());
                        workingRerunAction.setEnabled(false);
                    }
                }
            }
        });

        if (cancellable != null) {
            cancellable.setTask(current);
        }

        return current;
    }

    /**
     * Retrieves or creates the output window usable for the current run.
     *
     * @param required output window required by rerun or <code>null</code>
     * @return the output window usable for the current run
     */
    private InputOutputManager.InputOutputData getInputOutput(InputOutput required) {
        InputOutputManager.InputOutputData result = null;

        synchronized (InputOutputManager.class) {
            InputOutput io = descriptor.getInputOutput();
            if (io != null) {
                result = new InputOutputManager.InputOutputData(io,
                        originalDisplayName, null, null, null);
            }

            // try to acquire required one (rerun action)
            // this will always succeed if this method is called from EDT
            if (result == null) {
                result = InputOutputManager.getInputOutput(required);
            }

            // try to find free output windows
            if (result == null) {
                result = InputOutputManager.getInputOutput(
                        originalDisplayName, descriptor.isControllable(), descriptor.getOptionsPath());
            }

            // free IO was not found, create new one
            if (result == null) {
                result = InputOutputManager.createInputOutput(
                        originalDisplayName, descriptor.isControllable(), descriptor.getOptionsPath());
            }

            configureInputOutput(result.getInputOutput());
        }

        return result;
    }

    /**
     * Configures the output window before usage.
     *
     * @param inputOutput output window to configure
     */
    private void configureInputOutput(InputOutput inputOutput) {
        if (inputOutput == InputOutput.NULL) {
            return;
        }

        if (descriptor.getInputOutput() == null || !descriptor.noReset()) {
            try {
                inputOutput.getOut().reset();
            } catch (IOException exc) {
                LOGGER.log(Level.INFO, null, exc);
            }

            // Note - do this AFTER the reset() call above; if not, weird bugs occur
            inputOutput.setErrSeparated(false);
        }

        // Open I/O window now. This should probably be configurable.
        if (descriptor.isFrontWindow()) {
            inputOutput.select();
        }

        inputOutput.setInputVisible(descriptor.isInputVisible());
    }

    private ProgressHandle createProgressHandle(InputOutput inputOutput,
            String displayName, Cancellable cancellable) {

        if (!descriptor.showProgress() && !descriptor.showSuspended()) {
            return null;
        }

        ProgressHandle handle = ProgressHandleFactory.createHandle(displayName,
                cancellable, new ProgressAction(inputOutput));

        handle.setInitialDelay(0);
        handle.start();
        handle.switchToIndeterminate();

        if (descriptor.showSuspended()) {
            handle.suspend(NbBundle.getMessage(ExecutionService.class, "Running"));
        }

        return handle;
    }

    private void cleanup(final ProgressHandle progressHandle,
            final InputOutputManager.InputOutputData inputOutputData,
            final boolean managed, final boolean show) {

        cleanup(progressHandle, inputOutputData, show);

        synchronized (InputOutputManager.class) {
            if (managed) {
                InputOutputManager.addInputOutput(inputOutputData);
            }
        }
    }

    private void cleanup(final ProgressHandle progressHandle,
            final InputOutputManager.InputOutputData inputOutputData, final boolean show) {

        Runnable ui = new Runnable() {
            @Override
            public void run() {
                if (show) {
                    inputOutputData.getInputOutput().select();
                }
                if (inputOutputData.getStopAction() != null) {
                    inputOutputData.getStopAction().setEnabled(false);
                }
                if (inputOutputData.getRerunAction() != null) {
                    inputOutputData.getRerunAction().setEnabled(true);
                }
                if (progressHandle != null) {
                    progressHandle.finish();
                }
            }
        };

        Mutex.EVENT.readAccess(ui);
    }

    private InputProcessor createOutProcessor(OutputWriter writer) {
        LineConvertorFactory convertorFactory = descriptor.getOutConvertorFactory();
        InputProcessor outProcessor = null;
        if (descriptor.isOutLineBased()) {
            outProcessor = InputProcessors.bridge(LineProcessors.printing(writer,
                    convertorFactory != null ? convertorFactory.newLineConvertor() : null, true));
        } else {
            outProcessor = org.netbeans.api.extexecution.print.InputProcessors.printing(writer,
                    convertorFactory != null ? convertorFactory.newLineConvertor() : null, true);
        }

        InputProcessorFactory descriptorOutFactory = descriptor.getOutProcessorFactory();
        if (descriptorOutFactory != null) {
            outProcessor = new BaseInputProcessor(descriptorOutFactory.newInputProcessor(
                    new DelegatingInputProcessor(outProcessor)));
        } else {
            InputProcessorFactory2 descriptorOutFactory2 = descriptor.getOutProcessorFactory2();
            if (descriptorOutFactory2 != null) {
                outProcessor = descriptorOutFactory2.newInputProcessor(outProcessor);
            }
        }

        return outProcessor;
    }

    private InputProcessor createErrProcessor(OutputWriter writer) {
        LineConvertorFactory convertorFactory = descriptor.getErrConvertorFactory();
        InputProcessor errProcessor = null;
        if (descriptor.isErrLineBased()) {
            errProcessor = InputProcessors.bridge(LineProcessors.printing(writer,
                    convertorFactory != null ? convertorFactory.newLineConvertor() : null, false));
        } else {
            errProcessor = org.netbeans.api.extexecution.print.InputProcessors.printing(writer,
                    convertorFactory != null ? convertorFactory.newLineConvertor() : null, false);
        }

        InputProcessorFactory descriptorErrFactory = descriptor.getErrProcessorFactory();
        if (descriptorErrFactory != null) {
            errProcessor = new BaseInputProcessor(descriptorErrFactory.newInputProcessor(
                    new DelegatingInputProcessor(errProcessor)));
        } else {
            InputProcessorFactory2 descriptorErrFactory2 = descriptor.getErrProcessorFactory2();
            if (descriptorErrFactory2 != null) {
                errProcessor = descriptorErrFactory2.newInputProcessor(errProcessor);
            }
        }

        return errProcessor;
    }

    private static class ProgressCancellable implements Cancellable {

        private Future<Integer> task;

        public ProgressCancellable() {
            super();
        }

        public synchronized void setTask(Future<Integer> task) {
            this.task = task;
        }

        @Override
        public synchronized boolean cancel() {
            if (task != null) {
                task.cancel(true);
            }
            return true;
        }
    }

    private static class ProgressAction extends AbstractAction {

        private final InputOutput io;

        public ProgressAction(InputOutput io) {
            this.io = io;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            io.select();
        }
    }
}
