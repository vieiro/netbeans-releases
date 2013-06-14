/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.netbeans.modules.parsing.spi.indexing.support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.swing.event.ChangeListener;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.api.editor.mimelookup.test.MockMimeLookup;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.junit.MockServices;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.parsing.api.Embedding;
import org.netbeans.modules.parsing.api.ParserManager;
import org.netbeans.modules.parsing.api.ResultIterator;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.api.Task;
import org.netbeans.modules.parsing.api.UserTask;
import org.netbeans.modules.parsing.api.indexing.IndexingManager;
import org.netbeans.modules.parsing.impl.indexing.CacheFolder;
import org.netbeans.modules.parsing.impl.indexing.RepositoryUpdaterTest;
import org.netbeans.modules.parsing.impl.indexing.SPIAccessor;
import org.netbeans.modules.parsing.impl.indexing.lucene.LayeredDocumentIndex;
import org.netbeans.modules.parsing.impl.indexing.lucene.LuceneIndexFactory;
import org.netbeans.modules.parsing.spi.EmbeddingProvider;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.ParserFactory;
import org.netbeans.modules.parsing.spi.SchedulerTask;
import org.netbeans.modules.parsing.spi.SourceModificationEvent;
import org.netbeans.modules.parsing.spi.TaskFactory;
import org.netbeans.modules.parsing.spi.indexing.Context;
import org.netbeans.modules.parsing.spi.indexing.EmbeddingIndexer;
import org.netbeans.modules.parsing.spi.indexing.EmbeddingIndexerFactory;
import org.netbeans.modules.parsing.spi.indexing.Indexable;
import org.netbeans.modules.parsing.spi.indexing.PathRecognizer;
import org.netbeans.spi.java.classpath.ClassPathProvider;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.Pair;
import org.openide.util.test.TestFileUtils;

/**
 *
 * @author Tomas Zezula
 */
public class QuerySupportLifeLock230220Test extends NbTestCase {

    private FileObject sources;
    private FileObject srcFile;
    private ClassPath  scp;

    public QuerySupportLifeLock230220Test(@NonNull final String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        clearWorkDir();
        final FileObject wd = FileUtil.toFileObject(getWorkDir());
        final FileObject cache = FileUtil.createFolder(wd, "cache"); //NOI18N
        CacheFolder.setCacheFolder(cache);

        MockServices.setServices(
            FooPathRecognizer.class,
            EmbPathRecognizer.class,
            ClassPathProviderImpl.class);
        MockMimeLookup.setInstances(
            MimePath.get(FooPathRecognizer.FOO_MIME),
            new FooParser.Factory(),
            new EmbEmbeddingProvider.Factory(),
            new FooIndexer.Factory());
        MockMimeLookup.setInstances(
            MimePath.get(EmbPathRecognizer.EMB_MIME),
            new EmbParser.Factory(),
            new EmbIndexer.Factory());

        sources = FileUtil.createFolder(wd, "src");         //NOI18N
        srcFile = FileUtil.createData(sources, "file.foo"); //NOI18N
        scp = ClassPathSupport.createClassPath(sources);
        final ClassPathProviderImpl cppImpl = Lookup.getDefault().lookup(ClassPathProviderImpl.class);
        cppImpl.roots2cp = Pair.<FileObject[],ClassPath>of(
            new FileObject[]{sources},
            scp);
        TestFileUtils.writeFile(
            FileUtil.toFile(srcFile),
            "class {Lookup} class {ProjectManager} class {FileOwnerQuery}");    //NOI18N
        FileUtil.setMIMEType("foo", FooPathRecognizer.FOO_MIME);    //NOI18N
        RepositoryUpdaterTest.setMimeTypes(
            FooPathRecognizer.FOO_MIME,
            EmbPathRecognizer.EMB_MIME);
        RepositoryUpdaterTest.waitForRepositoryUpdaterInit();
    }


    public void testLifeLock230220() throws Exception {
        IndexingManager.getDefault().refreshIndexAndWait(sources.toURL(), null);
        final String indexerId = SPIAccessor.getInstance().getIndexerPath(
                EmbIndexer.EMB_INDEXER_NAME,
                EmbIndexer.EMB_INDEXER_VERSION);
        final FileObject cacheFolder = CacheFolder.getDataFolder(sources.toURL());
        assertNotNull(cacheFolder);
        final FileObject indexFolder = cacheFolder.getFileObject(indexerId);
        assertNotNull(indexFolder);
        final LayeredDocumentIndex index = LuceneIndexFactory.getDefault().getIndex(indexFolder);
        index.markKeyDirty(FileUtil.getRelativePath(sources, srcFile));
        final QuerySupport qs = QuerySupport.forRoots(EmbIndexer.EMB_INDEXER_NAME, EmbIndexer.EMB_INDEXER_VERSION, sources);
        final Collection<? extends IndexResult> result = qs.query(
                "key",  //NOI18N
                "",     //NOI18N
                QuerySupport.Kind.PREFIX,
                "key"); //NOI18N        
    }
    

    public static final class FooParser extends Parser {

        private Snapshot snapshot;

        @Override
        public void parse(Snapshot snapshot, Task task, SourceModificationEvent event) throws ParseException {
            this.snapshot = snapshot;
        }

        @Override
        public Result getResult(Task task) throws ParseException {
            return new R(snapshot);
        }

        @Override
        public void addChangeListener(ChangeListener changeListener) {
        }

        @Override
        public void removeChangeListener(ChangeListener changeListener) {
        }

        public static class R extends Result {

            private final List<CharSequence> embs;

            public R(@NonNull final Snapshot snapshot) {
                super(snapshot);
                embs = new ArrayList<CharSequence>();
                int embStart = -1;
                final CharSequence text = snapshot.getText();
                for (int i = 0; i < text.length(); i++) {
                    char c = text.charAt(i);
                    if (c == '{') {
                        embStart = i+1;
                    } else if (c == '}' && embStart >= 0) {
                        CharSequence emb = text.subSequence(embStart, i);
                        embs.add(emb);
                    }
                }
            }
            @Override
            protected void invalidate() {
            }

            public List<CharSequence> getEmbeddings() {
                return embs;
            }
        }

        public static class Factory extends ParserFactory {
            @Override
            public Parser createParser(Collection<Snapshot> snapshots) {
                return new FooParser();
            }
        }
    }

    public static final class EmbParser extends Parser {

        private Snapshot snapshot;

        @Override
        public void parse(Snapshot snapshot, Task task, SourceModificationEvent event) throws ParseException {
            this.snapshot = snapshot;
        }

        @Override
        public Result getResult(Task task) throws ParseException {
            return new Result(snapshot) {
                @Override
                protected void invalidate() {
                }
            };
        }

        @Override
        public void addChangeListener(ChangeListener changeListener) {
        }

        @Override
        public void removeChangeListener(ChangeListener changeListener) {
        }

        public static class Factory extends ParserFactory {
            @Override
            public Parser createParser(Collection<Snapshot> snapshots) {
                return new EmbParser();
            }
        }
    }

    public static final class EmbEmbeddingProvider extends EmbeddingProvider {

        @Override
        public List<Embedding> getEmbeddings(final Snapshot snapshot) {
            try {
                final FileObject file = snapshot.getSource().getFileObject();
                final ClassPath scp = ClassPath.getClassPath(file, FooPathRecognizer.SOURCES);
                final FileObject root = scp.findOwnerRoot(file);
                QuerySupport qs = QuerySupport.forRoots(EmbIndexer.EMB_INDEXER_NAME, EmbIndexer.EMB_INDEXER_VERSION, root);
                Collection<? extends IndexResult> res = qs.query(
                        "key",      //NOI18N
                        "",         //NOI18N
                        QuerySupport.Kind.PREFIX,
                        "key");     //NOI18N
                StringBuilder oldState = new StringBuilder("OLD STATE: ");  //NOI18N
                for (IndexResult r : res) {
                    oldState.append(r.getValue("key")); //NOI18N
                    oldState.append(' '); //NOI18N
                }
                System.out.println(oldState);
                final List<Embedding> result = new ArrayList<Embedding>();
                ParserManager.parse(
                    Collections.singleton(snapshot.getSource()),
                    new UserTask() {
                    @Override
                    public void run(ResultIterator resultIterator) throws Exception {
                        final Parser.Result res = resultIterator.getParserResult();
                        for (CharSequence cs : ((FooParser.R)res).getEmbeddings()) {
                            result.add(snapshot.create(cs, EmbPathRecognizer.EMB_MIME));
                        }
                    }
            });
                return result;
            } catch (ParseException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public int getPriority() {
            return 1;
        }

        @Override
        public void cancel() {
        }

        public static class Factory extends TaskFactory {
            @Override
            public Collection<? extends SchedulerTask> create(Snapshot snapshot) {
                return Collections.singleton(new EmbEmbeddingProvider());
            }
        }
    }

    public static final class FooIndexer extends EmbeddingIndexer {

        @Override
        protected void index(Indexable indexable, Parser.Result parserResult, Context context) {
            System.out.println("FOO: " + parserResult.getSnapshot().getText());
        }

        public static final class Factory extends EmbeddingIndexerFactory {

            public static final String FOO_INDEXER_NAME = "Foo-Indexer";    //NOI18N
            public static final int FOO_INDEXER_VERSION = 1;

            @Override
            public EmbeddingIndexer createIndexer(Indexable indexable, Snapshot snapshot) {
                return new FooIndexer();
            }

            @Override
            public void filesDeleted(Iterable<? extends Indexable> deleted, Context context) {
            }

            @Override
            public void filesDirty(Iterable<? extends Indexable> dirty, Context context) {
                try {
                    final IndexingSupport is = IndexingSupport.getInstance(context);
                    for (Indexable i : dirty) {
                        is.markDirtyDocuments(i);
                    }
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }

            @Override
            public String getIndexerName() {
                return FOO_INDEXER_NAME;
            }

            @Override
            public int getIndexVersion() {
                return FOO_INDEXER_VERSION;
            }

        }

    }

    public static final class EmbIndexer extends EmbeddingIndexer {

        public static final String EMB_INDEXER_NAME = "Emb-Indexer";    //NOI18N
        public static final int EMB_INDEXER_VERSION = 1;

        private final Factory f;

        private EmbIndexer (@NonNull final Factory f) {
            this.f = f;
        }

        @Override
        protected void index(Indexable indexable, Parser.Result parserResult, Context context) {
            try {
                System.out.println("EMB: " + parserResult.getSnapshot().getText());
                IndexingSupport is = IndexingSupport.getInstance(context);
                if (!indexable.equals(f.lastIndexable)) {
                    is.removeDocuments(indexable);
                    f.lastIndexable = indexable;
                }
                final IndexDocument doc = is.createDocument(indexable);
                doc.addPair(
                    "key",  //NOI18N
                     parserResult.getSnapshot().getText().toString(),
                     true,
                     true);
                is.addDocument(doc);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }

            
        }

        public static final class Factory extends EmbeddingIndexerFactory {

            private Indexable lastIndexable;

            @Override
            public EmbeddingIndexer createIndexer(Indexable indexable, Snapshot snapshot) {
                return new EmbIndexer(this);
            }

            @Override
            public void filesDeleted(Iterable<? extends Indexable> deleted, Context context) {
            }

            @Override
            public void filesDirty(Iterable<? extends Indexable> dirty, Context context) {
                try {
                    final IndexingSupport is = IndexingSupport.getInstance(context);
                    for (Indexable i : dirty) {
                        is.markDirtyDocuments(i);
                    }
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }

            @Override
            public String getIndexerName() {
                return EMB_INDEXER_NAME;
            }

            @Override
            public int getIndexVersion() {
                return EMB_INDEXER_VERSION;
            }

            @Override
            public boolean scanStarted(Context context) {
                lastIndexable = null;
                return super.scanStarted(context);
            }

        }

    }

    public static final class FooPathRecognizer extends PathRecognizer {

        public static final String SOURCES = "src"; //NOI18N
        public static final String FOO_MIME = "text/x-foo";  //NOI18N

        @Override
        public Set<String> getSourcePathIds() {
            return Collections.singleton(SOURCES);
        }

        @Override
        public Set<String> getLibraryPathIds() {
            return Collections.<String>emptySet();
        }

        @Override
        public Set<String> getBinaryLibraryPathIds() {
            return Collections.<String>emptySet();
        }

        @Override
        public Set<String> getMimeTypes() {
            return Collections.singleton(FOO_MIME);
        }
    }

    public static final class EmbPathRecognizer extends PathRecognizer {

        public static final String EMB_MIME = "text/x-emb";  //NOI18N

        @Override
        public Set<String> getSourcePathIds() {
            return Collections.singleton(FooPathRecognizer.SOURCES);
        }

        @Override
        public Set<String> getLibraryPathIds() {
            return Collections.<String>emptySet();
        }

        @Override
        public Set<String> getBinaryLibraryPathIds() {
            return Collections.<String>emptySet();
        }

        @Override
        public Set<String> getMimeTypes() {
            return Collections.singleton(EMB_MIME);
        }
    }

    public static final class ClassPathProviderImpl implements ClassPathProvider {

        private volatile Pair<FileObject[],ClassPath> roots2cp;

        @Override
        public ClassPath findClassPath(FileObject file, String type) {
            if (FooPathRecognizer.SOURCES.equals(type)) {
                final Pair<FileObject[],ClassPath> _roots2cp = roots2cp;
                if (_roots2cp != null) {
                    for (FileObject root : _roots2cp.first()) {
                        if (root.equals(file) || FileUtil.isParentOf(root, file)) {
                            return _roots2cp.second();
                        }
                    }
                }
            }
            return null;
        }

    }

}
