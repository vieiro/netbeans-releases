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

package org.netbeans.modules.parsing.api;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.swing.event.ChangeListener;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.api.editor.mimelookup.test.MockMimeLookup;
import org.netbeans.junit.MockServices;
import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.RandomlyFails;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.Parser.Result;
import org.netbeans.modules.parsing.spi.ParserFactory;
import org.netbeans.modules.parsing.spi.ParserResultTask;
import org.netbeans.modules.parsing.spi.Scheduler;
import org.netbeans.modules.parsing.spi.SchedulerEvent;
import org.netbeans.modules.parsing.spi.SchedulerTask;
import org.netbeans.modules.parsing.spi.SourceModificationEvent;
import org.netbeans.modules.parsing.spi.TaskFactory;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;

/**
 *
 * @author tom
 */
public class ParserManagerTest extends NbTestCase {

    public ParserManagerTest (String name) {
        super (name);
    }

    @Override
    public void setUp () throws Exception {
        clearWorkDir ();
        // 1) register tasks and parsers
        MockServices.setServices (MockMimeLookup.class);
        MockMimeLookup.setInstances (
            MimePath.get ("text/foo"), new FooParserFactory());
    }

    public void testParseCache () throws Exception {
        final boolean[] called = new boolean[] {false};
        FooParser.getResultCount = 0;
        FooParser.parseCount = 0;
        FooParserFactory.createParserCount = 0;
        ParserManager.parse ("text/foo", new UserTask () {
            @Override
            public void run(ResultIterator resultIterator) throws Exception {
                called[0]=true;
            }
        });
        assertTrue(called[0]);
        assertEquals(1, FooParserFactory.createParserCount);
        assertEquals(1, FooParser.parseCount);
        assertEquals(1, FooParser.getResultCount);
        called[0] = false;
        ParserManager.parse("text/foo", new UserTask() {
            @Override
            public void run(ResultIterator resultIterator) throws Exception {
                called[0]=true;
            }
        });
        assertTrue(called[0]);
        assertEquals(1, FooParserFactory.createParserCount);
        assertEquals(2, FooParser.parseCount);
        assertEquals(2, FooParser.getResultCount);

        try {
            byte[] dummy = new byte[(int) Runtime.getRuntime().maxMemory()];
        } catch (Throwable e) {
            // Ignore OME
        }
        System.gc(); System.gc();


        called[0] = false;
        ParserManager.parse("text/foo", new UserTask() {
            @Override
            public void run(ResultIterator resultIterator) throws Exception {
                called[0]=true;
            }
        });
        assertTrue(called[0]);
        assertEquals(2, FooParserFactory.createParserCount);
        assertEquals(3, FooParser.parseCount);
        assertEquals(3, FooParser.getResultCount);


    }

    public void testParseDoesNotScheduleTasks () throws Exception {
        final CountDownLatch l = new CountDownLatch(1);
        MockServices.setServices (MockMimeLookup.class, MyScheduler.class);
        MockMimeLookup.setInstances (
            MimePath.get ("text/foo"), new FooParserFactory(),
                        new TaskFactory () {
                public Collection<SchedulerTask> create (Snapshot snapshot) {
                    return Arrays.asList (new SchedulerTask[] {
                        new ParserResultTask() {
                            @Override
                            public void run(Result result, SchedulerEvent event) {
                                l.countDown();
                            }
                            @Override
                            public int getPriority() {
                                return 100;
                            }
                            @Override
                            public Class<? extends Scheduler> getSchedulerClass() {
                                return Scheduler.EDITOR_SENSITIVE_TASK_SCHEDULER;
                            }
                            @Override
                            public void cancel() {}
                        }
                    });
                }
        });

        clearWorkDir ();
        //Collection c = MimeLookup.getLookup("text/boo").lookupAll (ParserFactory.class);
        FileObject workDir = FileUtil.toFileObject (getWorkDir ());
        FileObject testFile = FileUtil.createData (workDir, "bla.foo");
        FileUtil.setMIMEType ("foo", "text/foo");
        OutputStream outputStream = testFile.getOutputStream ();
        OutputStreamWriter writer = new OutputStreamWriter (outputStream);
        writer.append ("Toto je testovaci file, na kterem se budou delat hnusne pokusy!!!");
        writer.close ();
        Source source = Source.create (testFile);

        ParserManager.parse (Collections.singleton(source), new UserTask () {
            @Override
            public void run(ResultIterator resultIterator) throws Exception {
            }
        });

        DataObject.find(testFile).getLookup().lookup(EditorCookie.class).openDocument();

        assertFalse("Should not schedule the task", l.await(2, TimeUnit.SECONDS));
    }

    private static class FooParser extends Parser {

        static int getResultCount = 0;
        
        static int parseCount = 0;

        private Snapshot last;

        public void parse (Snapshot snapshot, Task task, SourceModificationEvent event) throws ParseException {
            parseCount++;
            last = snapshot;
        }

        public Result getResult (Task task) throws ParseException {
            getResultCount++;
            return new Result (last) {
                public void invalidate () {
                }
            };
        }

        public void cancel () {
        }

        public void addChangeListener (ChangeListener changeListener) {
        }

        public void removeChangeListener (ChangeListener changeListener) {
        }

        public @Override String toString () {
            return "FooParser";
        }
    }

    private static class FooParserFactory extends ParserFactory {

        static int createParserCount = 0;

        public Parser createParser (Collection<Snapshot> snapshots2) {
            createParserCount++;
            return new FooParser();
        }
    }

}
