/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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
package org.netbeans.modules.clearcase.client;

import org.netbeans.modules.clearcase.ClearcaseException;
import org.netbeans.modules.clearcase.ClearcaseModuleConfig;

import java.util.logging.Logger;
import java.io.*;
import org.netbeans.modules.clearcase.client.mockup.CleartoolMockup;

/**
 * Encapsulates Clearcase shell process. 
 * 
 * @author Maros Sandor
 */
class Cleartool {
    
    private static final String MAGIC_PROMPT = "i-am-finished-with-previous-command-sir";

    /**
     * Default timeout between two output (or error) messages from a cleartool command.
     */
    private static final int DEFAULT_TIMEOUT_MS = 60000;
    
    private boolean fireAndForget;
   
    private final Process           ct;
    private final BufferedReader    ctOutput;
    private final BufferedReader    ctError;
    private final PrintWriter       ctInput;

    /**
     * Creates a new cleartool shell process.
     */
    public Cleartool() throws IOException {
        Logger.getLogger(Cleartool.class.getName()).fine("Cleartool: Creating cleartool process...");
        ct = createCleartoolProcess();        
        ctOutput = new BufferedReader(new InputStreamReader(ct.getInputStream()));
        ctError = new BufferedReader(new InputStreamReader(ct.getErrorStream()));
        ctInput = new PrintWriter(ct.getOutputStream());
        checkReady();
        Logger.getLogger(Cleartool.class.getName()).fine("Cleartool: cleartool process created");
    }    
    
    public void setFireAndForget(boolean fireAndForget) {
        this.fireAndForget = fireAndForget;
    }
    
    private synchronized void checkReady() throws IOException {
        ctInput.println(MAGIC_PROMPT);
        ctInput.flush();
        String ret = ctError.readLine();        
        if (!ret.contains(MAGIC_PROMPT)) {
            Logger.getLogger(Cleartool.class.getName()).fine("Cleartool: ERROR \"" + ret + "\"");
            throw new IOException("Invalid cleartool output: " + ret);
        } else {
            Logger.getLogger(Cleartool.class.getName()).finer("Cleartool: ERROR \"" + ret + "\"");
        }
    }

    /**
     * Forcibly closes the cleartool console, just like using Ctrl-C.
     */
    public void interrupt() {
        try {
            destroy();
        } catch (IOException e) {
            // swallow, we are not interested
        }
    }
    
    protected void finalize() throws Throwable {
        super.finalize();
        if (isValid()) {
            Logger.getLogger(Cleartool.class.getName()).warning("Cleartool process was not killed!");
            quit();
        }
    }

    /**
     * Tests whether this process and can be used to issue commands.
     * 
     * @return true if the cleartool shell is alive and ready
     */
    public synchronized boolean isValid() {
        try {
            readAll(ctOutput);
            readAll(ctError);
            ctInput.println(MAGIC_PROMPT);
            ctInput.flush();
            String line = readLine(ctError, 2000);                                    
            if (line == null || !line.contains(MAGIC_PROMPT)) {
                Logger.getLogger(Cleartool.class.getName()).fine("Cleartool: Process invalid: " + line);
                return false;
            }
        } catch (Exception e) {
            Logger.getLogger(Cleartool.class.getName()).fine("Cleartool: Process invalid");
            return false;
        }
        return true;
    }

    /**
     * Creates the runtime process with a started cleartool shell or the Cleartool mockup eventually
     * 
     * @return
     * @throws java.io.IOException
     */
    private static Process createCleartoolProcess() throws IOException {
        String vobRoot = System.getProperty("org.netbeans.modules.clearcase.client.mockup.vobRoot");
        Process ct;
        if (vobRoot == null || vobRoot.trim().equals("")) {
            ct = Runtime.getRuntime().exec(ClearcaseModuleConfig.getExecutablePath());
            Logger.getLogger(Cleartool.class.getName()).fine("Cleartool: shell process running");
        } else {
            ct = new CleartoolMockup(vobRoot);
            ((CleartoolMockup) ct).start();
            Logger.getLogger(Cleartool.class.getName()).fine("Cleartool: mockup process running");    
        }
        return ct;
    }
    
    /**
     * Reads from the stream with a timeout.
     * 
     * @param reader read to read from
     * @param timeoutMillis maximum time to wait for the stream to become readable, in milliseconds
     * @return a line read from the stream or null at EOF
     * @throws IOException if there is an I/O error or the timeout expired
     */
    private String readLine(BufferedReader reader, int timeoutMillis) throws IOException {
        long t0 = System.currentTimeMillis();
        for (;;) {
            if (isStreamReady(reader)) {
                String line = reader.readLine();
                Logger.getLogger(Cleartool.class.getName()).finer("Cleartool: LINE \"" + line + "\"");
                return line;
            }
            if (System.currentTimeMillis() - t0 > timeoutMillis) {
                Logger.getLogger(Cleartool.class.getName()).fine("Cleartool: Reader timed out");
                throw new IOException("Timeout expired: " + timeoutMillis);
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new IOException("Interrupted");
            }
        }
    }

    /**
     * Quits (terminates) the cleartool process. The Cleartool object is unusable from this point on. 
     */
    public synchronized void quit() throws IOException, ClearcaseException {
        exec(QuitCommand);
        try {
            Logger.getLogger(Cleartool.class.getName()).fine("Cleartool: Waiting to terminate...");
            ct.waitFor();
            Logger.getLogger(Cleartool.class.getName()).fine("Cleartool: Terminated");
        } catch (InterruptedException e) {
            // the thread was interrupted, ignore it
        }
        destroy();
    }

    private void destroy() throws IOException {
        ctInput.close();
        ctOutput.close();
        ctError.close();
        ct.destroy();
        Logger.getLogger(Cleartool.class.getName()).fine("Cleartool: Process destroyed");
    }
    
    public synchronized void exec(ClearcaseCommand command) throws IOException, ClearcaseException {

        
        // read all pending output
        readAll(ctOutput);
        readAll(ctError);
        
        File cwd = command.getCommandWorkingDirectory();
        if (cwd != null) {
            ctInput.print("cd ");
            ctInput.println("'" + cwd.getAbsolutePath() + "'");
        }
        
        Logger.getLogger(Cleartool.class.getName()).fine("Cleartool: Executing " + command);
        
        ctInput.println(command.getStringCommand());
        if (!fireAndForget) ctInput.println(MAGIC_PROMPT);
        ctInput.flush();
        
        if (command == QuitCommand || fireAndForget) return; // do not expect any response, return here
        
        command.commandStarted();

        long timeout = DEFAULT_TIMEOUT_MS;
        long t0 = System.currentTimeMillis();
        
        for (;;) {
            if (isStreamReady(ctError)) {
                String line = ctError.readLine();
                if (line == null) throw new EOFException();                
                if (line.contains(MAGIC_PROMPT)) {
                    Logger.getLogger(Cleartool.class.getName()).finer("Cleartool: ERROR \"" + line + "\"");
                    break;
                }
                Logger.getLogger(Cleartool.class.getName()).fine("Cleartool: ERROR \"" + line + "\"");
                command.errorText(line);
                t0 = System.currentTimeMillis();
            } else {
                // if there was an error and the error stream is no longer readable, return this error
                if (command.hasFailed()) {
                    // Sometimes it happens that ct commands throw errors and they are not readily available in the error
                    // stream thus we need to wait a while for error messages to appear
                    // if we do not wait for them it may happen that other thread invokes isValid() and it will return 'false'
                    // because isValid() method will not read expected response from the error stream (it will instead obtain these, unread, error responses)
                    while (isStreamReady(ctError)) {
                        String line = ctError.readLine();                        
                        if (line == null) throw new EOFException();
                        if (line.contains(MAGIC_PROMPT)) {
                            Logger.getLogger(Cleartool.class.getName()).finer("Cleartool: ERROR \"" + line + "\"");
                            break;
                        }
                        Logger.getLogger(Cleartool.class.getName()).fine("Cleartool: ERROR \"" + line + "\"");
                    }
                    break;
                }
                if (notifyOutput(command)) {
                    t0 = System.currentTimeMillis();
                } else {
                    try {
                        // make sure the for(;;) cycle doesn't loop with no delay in between isStreamReady() calls
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
            if (System.currentTimeMillis() > t0 + timeout) throw new IOException("Cleartool: Command " + command + " timed out");
        }
        notifyOutput(command);
        command.commandFinished();
    }

    private boolean isStreamReady(BufferedReader reader) throws IOException {
        if (reader.ready()) return true;
        Thread.yield();
        return reader.ready();
    }

    /**
     * @param listener listener for outputText() events
     * @return true if there were bytes available and read from the stream, false otherwise 
     */
    private boolean notifyOutput(NotificationListener listener) throws IOException {
        boolean streamRead = false;
        while (isStreamReady(ctOutput)) {
            streamRead = true;
            String line = ctOutput.readLine();
            Logger.getLogger(Cleartool.class.getName()).fine("Cleartool: OUTPUT \"" + line + "\"");
            listener.outputText(line);
        }
        return streamRead;
    }

    private void readAll(BufferedReader in) throws IOException {
        while (in.ready()) in.read();
    }

    
    private static final ClearcaseCommand QuitCommand  = new ClearcaseCommand() {
        public void prepareCommand(Arguments arguments) throws ClearcaseException {
            arguments.add("quit");
        }
    };

}
