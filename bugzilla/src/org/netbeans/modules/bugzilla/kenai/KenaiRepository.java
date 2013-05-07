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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.bugzilla.kenai;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.PasswordAuthentication;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.logging.Level;
import org.netbeans.modules.bugtracking.team.spi.TeamAccessor;
import org.netbeans.modules.bugtracking.team.spi.TeamProject;
import org.netbeans.modules.bugtracking.team.spi.OwnerInfo;
import org.netbeans.modules.bugtracking.team.spi.RepositoryUser;
import org.netbeans.modules.bugtracking.team.spi.TeamUtil;
import org.netbeans.modules.bugtracking.spi.RepositoryInfo;
import org.netbeans.modules.bugtracking.util.TextUtils;
import org.netbeans.modules.bugzilla.Bugzilla;
import org.netbeans.modules.bugzilla.BugzillaConnector;
import org.netbeans.modules.bugzilla.issue.BugzillaIssue;
import org.netbeans.modules.bugzilla.query.BugzillaQuery;
import org.netbeans.modules.bugzilla.query.QueryParameter;
import org.netbeans.modules.bugzilla.repository.BugzillaConfiguration;
import org.netbeans.modules.bugzilla.repository.BugzillaRepository;
import org.netbeans.modules.bugzilla.util.BugzillaConstants;
import org.netbeans.modules.bugzilla.util.BugzillaUtil;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

/**
 *
 * @author Tomas Stupka, Jan Stola
 */
public class KenaiRepository extends BugzillaRepository implements PropertyChangeListener {

    static final String ICON_PATH = "org/netbeans/modules/bugtracking/ui/resources/kenai-small.png"; // NOI18N
    private String urlParam;
    private Image icon;
    private final String product;
    private KenaiQuery myIssues;
    private KenaiQuery allIssues;
    private String host;
    private final TeamProject kenaiProject;

    KenaiRepository(TeamProject kenaiProject, String repoName, String url, String host, String userName, char[] password, String urlParam, String product) {
        super(createInfo(repoName, url)); // use name as id - can't be changed anyway
        this.urlParam = urlParam;
        icon = ImageUtilities.loadImage(ICON_PATH, true);
        this.product = product;
        this.host = host;
        assert kenaiProject != null;
        this.kenaiProject = kenaiProject;
        TeamAccessor kenaiAccessor = TeamUtil.getTeamAccessor(url);
        if (kenaiAccessor != null) {
            kenaiAccessor.addPropertyChangeListener(this, kenaiProject.getWebLocation().toString());
        }
    }

    public KenaiRepository(TeamProject kenaiProject, String repoName, String url, String host, String urlParam, String product) {
        this(kenaiProject, repoName, url, host, getKenaiUser(kenaiProject), getKenaiPassword(kenaiProject), urlParam, product);
    }

    @Override
    public Image getIcon() {
        return icon;
    }

    @Override
    public BugzillaQuery createQuery() {
        KenaiQuery q = new KenaiQuery(null, this, null, product, false, false);
        return q;
    }

    @Override
    public BugzillaIssue createIssue() {
        return super.createIssue();
    }

    @Override
    public synchronized Collection<BugzillaQuery> getQueries() {
        List<BugzillaQuery> ret = new LinkedList<BugzillaQuery>();
        ret.addAll(super.getQueries());
        ret.addAll(getDefinedQueries());
        return ret;
    }

    private Collection<BugzillaQuery> getDefinedQueries() {
        List<BugzillaQuery> queries = new ArrayList<BugzillaQuery>();
        
        BugzillaQuery mi = getMyIssuesQuery();
        if(mi != null) {
            queries.add(mi);
        }

        BugzillaQuery ai = getAllIssuesQuery();
        if(ai != null) {
            queries.add(ai);
        }

        return queries;
    }

    public TeamProject getKenaiProject() {
        return kenaiProject;
    }
    
    public synchronized BugzillaQuery getAllIssuesQuery() throws MissingResourceException {
        if(!providePredefinedQueries() || BugzillaUtil.isNbRepository(this)) return null;
        if (allIssues == null) {
            StringBuffer url = new StringBuffer();
            url = new StringBuffer();
            url.append(urlParam);
            url.append(MessageFormat.format(BugzillaConstants.ALL_ISSUES_PARAMETERS, product));
            allIssues = new KenaiQuery(NbBundle.getMessage(KenaiRepository.class, "LBL_AllIssues"), this, url.toString(), product, true, true); // NOI18N
        }
        return allIssues;
    }

    public synchronized BugzillaQuery getMyIssuesQuery() throws MissingResourceException {
        if(!providePredefinedQueries()) return null;
        if (myIssues == null) {
            String url = getMyIssuesQueryUrl();
            myIssues =
                new KenaiQuery(
                    NbBundle.getMessage(KenaiRepository.class, "LBL_MyIssues"), // NOI18N
                    this,
                    url.toString(),
                    product,
                    true,
                    true);
        }
        return myIssues;
    }

    private String getMyIssuesQueryUrl() {
        StringBuilder url = new StringBuilder();
        url.append(urlParam);
        String user = getKenaiUser(kenaiProject);
        if (user == null) {
            user = ""; // NOI18N
        }
        
        // XXX what if user already mail address?
        // XXX escape @?
        String userMail = user + "@" + host; // NOI18N
        String urlFormat = BugzillaUtil.isNbRepository(this) ? BugzillaConstants.NB_MY_ISSUES_PARAMETERS_FORMAT : BugzillaConstants.MY_ISSUES_PARAMETERS_FORMAT;
        url.append(MessageFormat.format(urlFormat, product, userMail));
        return url.toString();
    }

    @Override
    public synchronized void refreshConfiguration() {
        KenaiConfiguration conf = (KenaiConfiguration) getConfiguration();
        conf.reset();
        super.refreshConfiguration();
    }

    @Override
    protected BugzillaConfiguration createConfiguration(boolean forceRefresh) {
        KenaiConfiguration kc = new KenaiConfiguration(this, product);
        kc.initialize(this, forceRefresh);
        return kc;
    }

    @Override
    public void ensureCredentials() {
        authenticate(null);
    }

    @Override
    public void refreshAllQueries() {
        super.refreshAllQueries(false);
    }

    @Override
    public boolean authenticate(String errroMsg) {
        PasswordAuthentication pa = TeamUtil.getPasswordAuthentication(kenaiProject.getWebLocation().toString(), true);
        if(pa == null) {
            return false;
        }
        
        String user = pa.getUserName();
        char[] password = pa.getPassword();

        setTaskRepository(user, password);

        return true;
    }

    @Override
    protected Object[] getLookupObjects() {
        Object[] obj = super.getLookupObjects();
        Object[] obj2 = new Object[obj.length + 1];
        System.arraycopy(obj, 0, obj2, 0, obj.length);
        obj2[obj2.length - 1] = kenaiProject;
        return obj2;
    }

    /**
     * Returns the name of the bz product - should be the same as the name of the kenai project that owns this repository
     * @return
     */
    public String getProductName () {
        return product;
    }

    private static String getKenaiUser(TeamProject kenaiProject) {
        PasswordAuthentication pa = TeamUtil.getPasswordAuthentication(kenaiProject.getWebLocation().toString(), false);
        if(pa != null) {
            return pa.getUserName();
        }
        return "";                                                              // NOI18N
    }

    private static char[] getKenaiPassword(TeamProject kenaiProject) {
        PasswordAuthentication pa = TeamUtil.getPasswordAuthentication(kenaiProject.getWebLocation().toString(), false);
        if(pa != null) {
            return pa.getPassword();
        }
        return new char[0];                                                     // NOI18N
    }

    @Override
    protected QueryParameter[] getSimpleSearchParameters() {
        List<QueryParameter> ret = new ArrayList<QueryParameter>();
        ret.add(new QueryParameter.SimpleQueryParameter("product", new String[] { product }, getTaskRepository().getCharacterEncoding() ));    //NOI18N        

        // XXX this relies on the fact that the user can't change the selection
        //     while the quicksearch is oppened. Works for now, but might change in the future
        Node[] nodes = WindowManager.getDefault().getRegistry().getActivatedNodes();
        OwnerInfo ownerInfo = getOwnerInfo(nodes);
        if(ownerInfo != null && ownerInfo.getOwner().equals(product)) {
            List<String> data = ownerInfo.getExtraData();
            if(data != null && data.size() > 0) {
                ret.add(new QueryParameter.SimpleQueryParameter("component", new String[] { data.get(0) }, getTaskRepository().getCharacterEncoding()));    //NOI18N
            }
        }

        return ret.toArray(new QueryParameter[ret.size()]);
    }

    @Override
    public Collection<RepositoryUser> getUsers() {
        return TeamUtil.getProjectMembers(kenaiProject);
    }

    public String getHost() {
        return host;
    }

    private static String getRepositoryId(String name, String url) {
        return TextUtils.encodeURL(url) + ":" + name;                           // NOI18N
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals(TeamAccessor.PROP_LOGIN)) {

            // XXX move to spi?
            // get kenai credentials
            String user;
            char[] psswd;
            PasswordAuthentication pa = 
                    TeamUtil.getPasswordAuthentication(kenaiProject.getWebLocation().toString(), false); // do not force login
            if(pa != null) {
                user = pa.getUserName();
                psswd = pa.getPassword();
            } else {
                user = "";                                                      // NOI18N
                psswd = new char[0];                                            // NOI18N
            }

            setTaskRepository(user, psswd);

            synchronized(KenaiRepository.this) {
                if(evt.getNewValue() != null) {
                    if(myIssues != null) {
                        // XXX this is a mess - setting the controller and the query
                        KenaiQueryController c = (KenaiQueryController) myIssues.getController();
                        String url = getMyIssuesQueryUrl();
                        c.populate(url);
                        myIssues.setUrlParameters(url);
                    }
                } 
            }
        }
    }

    @Override
    public OwnerInfo getOwnerInfo(Node[] nodes) {
        OwnerInfo ownerInfo = super.getOwnerInfo(nodes);
        if(ownerInfo != null) {
            if(ownerInfo.getOwner().equals(product)) {
                return ownerInfo;
            } else {
                Bugzilla.LOG.log(
                        Level.WARNING,
                        " returned owner [{0}] for {1} is different then product [{2}]",
                        new Object[]{
                            ownerInfo.getOwner(),
                            nodes[0],
                            product});                               // NOI18N
                return null;
            }
        }
        return null;
    }

    private boolean providePredefinedQueries() {
        String provide = System.getProperty("org.netbeans.modules.bugzilla.noPredefinedQueries");   // NOI18N
        return !"true".equals(provide);                                                             // NOI18N
    }

    private static RepositoryInfo createInfo(String repoName, String url) {
        String id = getRepositoryId(repoName, url);
        String tooltip = NbBundle.getMessage(BugzillaRepository.class, "LBL_RepositoryTooltipNoUser", new Object[] {repoName, url}); // NOI18N
        return new RepositoryInfo(id, BugzillaConnector.ID, url, repoName, tooltip);
    }
}
