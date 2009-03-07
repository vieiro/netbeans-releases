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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.netbeans.modules.nativeexecution.api.util.HostInfoUtils;
import org.netbeans.modules.nativeexecution.support.Logger;

public final class LocalNativeProcess extends AbstractNativeProcess {

    private final InputStream processOutput;
    private final InputStream processError;
    private final OutputStream processInput;
    private final Process process;

    public LocalNativeProcess(NativeProcessInfo info) throws IOException {
        super(info);

        final String workingDirectory = info.getWorkingDirectory(true);
        final File wdir =
                workingDirectory == null ? null : new File(workingDirectory);

        final String shell =
                HostInfoUtils.getShell(info.getExecutionEnvironment());

        ProcessBuilder pb;
        if (shell != null) {
            // TODO: do it only when nessesary (replaceAll)
            String commandLine = info.getCommandLine().replaceAll("\\\\", "/"); //NOI18N
            pb = new ProcessBuilder(shell, "-c", // NOI18N
                    "/bin/echo $$ && exec " + commandLine);
        } else {
            String[] cmd = info.getCommand();
            cmd[0] = wdir.getAbsolutePath() + File.separator + cmd[0];
            pb = new ProcessBuilder(cmd);
        }

        pb.environment().putAll(info.getEnvVariables(pb.environment()));
        pb.directory(wdir);

        Process pr = null;

        try {
            pr = pb.start();
        } catch (IOException ex) {
            Logger.getInstance().warning(ex.getMessage());
            throw ex;
        }

        process = pr;

        processOutput = process.getInputStream();
        processError = process.getErrorStream();
        processInput = process.getOutputStream();

        if (shell != null) {
            readPID(processOutput);
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
        return process.waitFor();
    }

    @Override
    public void cancel() {
        process.destroy();
    }
}
