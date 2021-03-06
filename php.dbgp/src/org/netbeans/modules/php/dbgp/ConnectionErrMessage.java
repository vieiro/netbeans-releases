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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
package org.netbeans.modules.php.dbgp;

import java.awt.Color;
import java.awt.Font;
import javax.swing.Icon;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import org.netbeans.modules.php.project.api.PhpOptions;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.HtmlBrowser;
import org.openide.util.NbBundle;

/**
 *
 * @author  Radek Matous
 */
public class ConnectionErrMessage extends javax.swing.JPanel {
    private final String message;
    public static void showMe(int seconds) {
        ConnectionErrMessage panel = new ConnectionErrMessage(seconds);

        NotifyDescriptor messageDescriptor = new NotifyDescriptor.Message("");//NOI18N
        DialogDescriptor descr2 = new DialogDescriptor(panel, messageDescriptor.getTitle(),
                true, new Object[]{DialogDescriptor.OK_OPTION}, null, DialogDescriptor.BOTTOM_ALIGN, null, null);
        DialogDisplayer.getDefault().createDialog(descr2).setVisible(true);
    }

    /** Creates new form ConnectionErrMessage */
    private ConnectionErrMessage(int seconds) {
        message = createMessage(seconds);
        initComponents();
    }

    private static String createMessage(int seconds) {
        int debuggerPort = PhpOptions.getInstance().getDebuggerPort();
        final String entry1 = "<li>"+NbBundle.getMessage(ConnectionErrMessage.class, "MSG_ErrDebugSessionEntry1")+"</li>";//NOI18N
        final String entry2 = "<li>"+NbBundle.getMessage(ConnectionErrMessage.class, "MSG_ErrDebugSessionEntry2")+"</li>";//NOI18N
        final String entry3 = "<li>"+NbBundle.getMessage(ConnectionErrMessage.class, "MSG_ErrDebugSessionEntry3")+"</li>";//NOI18N
        final String entry4 = "<li>"+NbBundle.getMessage(ConnectionErrMessage.class, "MSG_ErrDebugSessionEntry4",
                String.valueOf(debuggerPort))+"</li>";//NOI18N
        final String entries = "<ul>"+entry1+entry2+entry3+entry4+"</ul>";
        return "<html>"+NbBundle.getMessage(ConnectionErrMessage.class, "MSG_ErrDebugSession", seconds,entries)+"</html>";//NOI18N
    }

    private static JLabel createIconLabel() {
        final Icon icon = UIManager.getIcon("OptionPane.informationIcon");//NOI18N
        return (icon != null) ? new JLabel(icon) : new JLabel();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        messageIconLabel = createIconLabel();
        messageTextLabel = new javax.swing.JLabel();
        link = HyperlinkPane.create();

        messageTextLabel.setText(message);

        link.setEditable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(messageIconLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(link)
                    .addComponent(messageTextLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 482, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(messageTextLabel)
                    .addComponent(messageIconLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(link, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
                .addContainerGap())
        );

        messageTextLabel.getAccessibleContext().setAccessibleDescription("Error Message");
        link.getAccessibleContext().setAccessibleName("Error Message");

        getAccessibleContext().setAccessibleName("Error Message Form");
        getAccessibleContext().setAccessibleDescription("Error Message Form");
    }// </editor-fold>//GEN-END:initComponents

    private static class HyperlinkPane extends JEditorPane implements HyperlinkListener {
        static HyperlinkPane create() {
            String url = NbBundle.getMessage(ConnectionErrMessage.class, "MSG_ErrDebugSessionLinkURL");//NOI18N
            String descr = NbBundle.getMessage(ConnectionErrMessage.class, "MSG_ErrDebugSessionLinkDescription");//NOI18N
            String text = "<a href=\""+url+"\">"+descr+"</a>";//NOI18N
            Font font = UIManager.getFont("Label.font");//NOI18N
            Color color = UIManager.getColor("Label.background");
            if (font == null || color == null ) {
                JLabel lbl = new JLabel();
                font = lbl.getFont();
                color = lbl.getBackground();
            }
            HyperlinkPane retval = new HyperlinkPane("<html><body style=\"font-size: " +//NOI18N
                    font.getSize() + "pt; font-family: " + font.getName() + ";\">" + text + "</body></html>");//NOI18N
            retval.setBackground(color);//NOI18N
            retval.setEditable(false);
            retval.setFocusable(false);
            return retval;
        }

        private HyperlinkPane(String text) {
            super("text/html", text);//NOI18N
            addHyperlinkListener(this);
        }

        @Override
        public synchronized void addHyperlinkListener(HyperlinkListener listener) {
            super.addHyperlinkListener(listener);
        }

        @Override
        public void hyperlinkUpdate(HyperlinkEvent hlevt) {
            if (HyperlinkEvent.EventType.ACTIVATED == hlevt.getEventType()) {
                assert hlevt.getURL() != null;
                HtmlBrowser.URLDisplayer displayer = HtmlBrowser.URLDisplayer.getDefault();
                assert displayer != null : "HtmlBrowser.URLDisplayer found.";
                displayer.showURL(hlevt.getURL());
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JEditorPane link;
    private javax.swing.JLabel messageIconLabel;
    private javax.swing.JLabel messageTextLabel;
    // End of variables declaration//GEN-END:variables
}
