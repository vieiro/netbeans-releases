/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.nativeexecution;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.ConnectException;
import org.netbeans.modules.nativeexecution.api.ExecutionEnvironment;
import org.netbeans.modules.nativeexecution.api.util.HostInfoUtils;
import org.netbeans.modules.nativeexecution.support.EnvWriter;
import org.netbeans.modules.nativeexecution.support.Logger;
import org.netbeans.modules.nativeexecution.support.MacroMap;
import org.netbeans.modules.nativeexecution.support.UnbufferSupport;
import org.netbeans.modules.nativeexecution.support.WindowsSupport;
import org.openide.util.Utilities;

public final class LocalNativeProcess extends AbstractNativeProcess {

    private final static java.util.logging.Logger log = Logger.getInstance();
    private final static String shell;
    private final static boolean isWindows;
    private final InputStream processOutput;
    private final InputStream processError;
    private final OutputStream processInput;
    private final Process process;


    static {
        String sh = null;

        try {
            sh = HostInfoUtils.getShell(new ExecutionEnvironment());
        } catch (ConnectException ex) {
        }

        shell = sh;

        isWindows = Utilities.isWindows();
    }

    public LocalNativeProcess(NativeProcessInfo info) throws IOException {
        super(info);

        try {
            Process proc = null;
            InputStream is = null;
            InputStream es = null;
            OutputStream os = null;

            if (Utilities.isWindows() && shell == null) {
                throw new IOException("CYGWIN/MSYS are currently required on Windows."); // NOI18N
            }

            // Get working directory ....
            String workingDirectory = info.getWorkingDirectory(true);

            if (workingDirectory != null) {
                workingDirectory = new File(workingDirectory).getAbsolutePath();
                if (isWindows) {
                    workingDirectory = WindowsSupport.getInstance().normalizePath(workingDirectory);
                }
            }

            final MacroMap env = info.getEnvVariables();

            UnbufferSupport.initUnbuffer(info, env);

            // On windows add /bin to PATH in case cygwin is not in
            // the Path environment variable ...
            if (isWindows) {
                env.put("PATH", "/bin:$PATH"); // NOI18N
            }

            try {
                final ProcessBuilder pb = new ProcessBuilder(shell, "-s"); // NOI18N

                if (isInterrupted()) {
                    throw new InterruptedException();
                }

                try {
                    proc = pb.start();
                } catch (InterruptedIOException ex) {
                    throw new InterruptedException();
                } catch (IOException ex) {
                }

                is = proc.getInputStream();
                es = proc.getErrorStream();
                os = proc.getOutputStream();

                os.write("/bin/echo $$\n".getBytes()); // NOI18N
                os.flush();

                EnvWriter ew = new EnvWriter(os);
                ew.write(env);

                if (workingDirectory != null) {
                    os.write(("cd \"" + workingDirectory + "\"\n").getBytes()); // NOI18N
                }

                String cmd = "exec " + info.getCommandLine() + "\n"; // NOI18N

                if (isWindows) {
                    cmd = cmd.replaceAll("\\\\", "/"); // NOI18N
                }

                os.write(cmd.getBytes());
                os.flush();
            } catch (InterruptedException ex) {
                interrupt();
            } catch (InterruptedIOException ex) {
                interrupt();
            }

            if (proc == null || isInterrupted()) {
                if (proc != null) {
                    proc.destroy();
                }

                process = null;
                processError = new ByteArrayInputStream(new byte[0]);
                processOutput = new ByteArrayInputStream(new byte[0]);
                processInput = new ByteArrayOutputStream();
                return;
            }

            process = proc;
            processError = es;
            processInput = os;
            processOutput = is;

            try {
                readPID(processOutput);
            } catch (IOException ex) {
                interrupt();
            }
        } finally {
//            if (isInterrupted()) {
//                throw new InterruptedIOException("Process interrupted"); // NOI18N
//            }
        }
    }

    @Override
    public OutputStream getOutputStream() {
        return processInput;
    }

    @Override
    public InputStream getInputStream() {
        return processOutput;
    }

    @Override
    public InputStream getErrorStream() {
        return processError;
    }

    @Override
    public final int waitResult() throws InterruptedException {
        if (process == null) {
            throw new InterruptedException();
        }

        /*
         * Why not just process.waitResult()...
         * This is to avoid a problem with short-running tasks, when
         * this Thread (that waits for process' termination) doesn't see
         * that it has been interrupted....
         * TODO: describe situation in details... 
         */

        int result = -1;

//        // Get lock on process not to take it on every itteration
//        // (in process.exitValue())
//
//        synchronized (process) {
        // Why this synchronized is commented-out..
        // This is because ProcessReaper is also synchronized on this...
        // And it should be able to react on process' termination....

        while (true) {
            // This sleep is to avoid lost interrupted exception...
            try {
                Thread.sleep(200);
            // 200 - to make this check not so often...
            // actually, to avoid the problem, 1 is OK.
            } catch (InterruptedException ex) {
                throw ex;
            }

            try {
                result = process.exitValue();
            } catch (IllegalThreadStateException ex) {
                continue;
            }

            break;
        }
//        }

        return result;
    }

    @Override
    protected final synchronized void cancel() {
        if (process != null) {
            process.destroy();
        }
    }
}
