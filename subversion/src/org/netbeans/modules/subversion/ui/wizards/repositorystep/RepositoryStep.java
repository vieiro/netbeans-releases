/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.subversion.ui.wizards.repositorystep;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.modules.subversion.OutputLogger;
import org.netbeans.modules.subversion.RepositoryFile;
import org.netbeans.modules.subversion.Subversion;
import org.netbeans.modules.subversion.client.ExceptionHandler;
import org.netbeans.modules.subversion.config.ProxyDescriptor;
import org.netbeans.modules.subversion.ui.repository.Repository;
import org.netbeans.modules.subversion.client.SvnClient;
import org.netbeans.modules.subversion.ui.wizards.AbstractStep;
import org.openide.ErrorManager;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.tigris.subversion.svnclientadapter.ISVNInfo;
import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNUrl;
        
/**
 *
 *
 *
 * @author Tomas Stupka
 */
public class RepositoryStep
        extends AbstractStep
        implements WizardDescriptor.AsynchronousValidatingPanel, PropertyChangeListener
{
    
    private Repository repository;        
    private RepositoryStepPanel panel;    

    private JLabel progressLabel;
    private ProgressHandle progress;
    private JComponent progressComponent;           
    private Thread backgroundValidationThread;

    private RepositoryFile repositoryFile;    

    private boolean acceptRevision;

    public RepositoryStep(boolean acceptRevision) {
        this.acceptRevision = acceptRevision;
    }

    public HelpCtx getHelp() {
        return new HelpCtx(RepositoryStep.class);
    }        

    protected JComponent createComponent() {
        if (repository == null) {
            repository = new Repository(true, acceptRevision, "Specify Subversion repository location:");
            repository.addPropertyChangeListener(this);
            panel = new RepositoryStepPanel();            
            panel.repositoryPanel.setLayout(new BorderLayout());
            panel.repositoryPanel.add(repository.getPanel());
            valid();
        }                        
        return panel;
    }

    protected void validateBeforeNext() {    
        final Repository.SelectedRepository selectedRepository = getSelectedRepository();
        if (selectedRepository==null) {
            validationDone();
            return;
        }
        
        backgroundValidationThread = Thread.currentThread();
        final SvnClient client;
        try {
            client = Subversion.getInstance().getClient(selectedRepository.getUrl(),
                                                        getProxyDescriptor(), 
                                                        repository.getUserName(),
                                                        repository.getPassword());
        } catch (SVNClientException ex) {
            invalid(ex.getLocalizedMessage());
            validationDone();
            return; 
        }
         
        repositoryFile = null; // reset
        
        final String invalidMsg[] = new String[1]; // ret value
        Runnable worker = new Runnable() {
            private void fail(String msg) {
                invalidMsg[0] = msg;
            }
            public void run() {
                invalid(null);                

                storeValues();              
                
                ISVNInfo info = null;                
                try {      
                    info = client.getInfo(selectedRepository.getUrl());                                                                                                        
                } catch (SVNClientException ex) {
                    String msg = ExceptionHandler.getCustomizedMessage(ex);
                    if(msg==null) {
                        msg = ex.getLocalizedMessage();
                    }
                    invalidMsg[0] = msg;
                }                                
                
                if(info != null) {                    
                    SVNUrl repositoryUrl = info.getRepository();
                    if(repositoryUrl==null) {
                        // XXX see issue #72810 and #72921. workaround! 
                        repositoryUrl = selectedRepository.getUrl();                        
                    }
                    SVNRevision revision = selectedRepository.getRevision();                                   
                    String[] repositorySegments = repositoryUrl.getPathSegments();
                    String[] selectedSegments = selectedRepository.getUrl().getPathSegments();
                    String[] repositoryFolder = new String[selectedSegments.length - repositorySegments.length];
                    System.arraycopy(selectedSegments, repositorySegments.length, 
                                     repositoryFolder, 0, 
                                     repositoryFolder.length);
                    try {                        
                        repositoryFile = new RepositoryFile(repositoryUrl, repositoryFolder, revision);                                                                                                        
                    } catch (MalformedURLException ex) {
                        ex.printStackTrace(); // should not happen
                    }
                } else {
                    if(invalidMsg[0] == null) {
                        invalidMsg[0] = "No information available for :" + selectedRepository.getUrl();
                    }
                }
            }
        };

        Thread workerThread = new Thread(worker, "SVN Repository Probe");  // NOI18N
        workerThread.start();
        try {
            workerThread.join();
            if (invalidMsg[0] == null) {
                valid();
            } else {
                valid(invalidMsg[0]);
            }
        } catch (InterruptedException e) {
            //invalid(org.openide.util.NbBundle.getMessage(RepositoryStep.class, "BK2023"));  // NOI18N
            ErrorManager err = ErrorManager.getDefault();
            err.annotate(e, "Passing interrupt to possibly uninterruptible nested thread: " + workerThread);
            try {
                client.cancelOperation(); // XXX do we need this
            } catch (SVNClientException ex) {
                ex.printStackTrace();  // cannot happen
            }
            workerThread.interrupt();
            valid("Action interrupted by user."); // should be a user action, so set again on valid ... 
            err.notify(ErrorManager.INFORMATIONAL, e);
        } finally {
            backgroundValidationThread = null;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    validationDone();
                }
            });
        }    
    }
    
    public void prepareValidation() {
        progress = ProgressHandleFactory.createHandle(NbBundle.getMessage(RepositoryStep.class, "BK2012")); // NOI18N
        JComponent bar = ProgressHandleFactory.createProgressComponent(progress);
        JButton stopButton = new JButton(org.openide.util.NbBundle.getMessage(RepositoryStep.class, "BK2022")); // NOI18N
        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stop();
            }
        });
        progressComponent = new JPanel();
        progressComponent.setLayout(new BorderLayout(6, 0));
        progressLabel = new JLabel();
        progressComponent.add(progressLabel, BorderLayout.NORTH);
        progressComponent.add(bar, BorderLayout.CENTER);
        progressComponent.add(stopButton, BorderLayout.LINE_END);
        progress.start(/*2, 5*/);
        panel.progressPanel.setVisible(true);
        panel.progressPanel.add(progressComponent, BorderLayout.SOUTH);
        panel.progressPanel.revalidate();

        setEditable(false);

        OutputLogger logger = new OutputLogger(); // XXX to use the logger this way is a hack
        logger.logCommandLine("==[IDE]== " + DateFormat.getDateTimeInstance().format(new Date()) + " " + this.getClass().getName() + ".prepareValidation()");
    }

    public void stop() {
        if (backgroundValidationThread != null) {
            backgroundValidationThread.interrupt();
        }
    }
    
    private void progress(String message) {
        if (progressLabel != null) {
            progressLabel.setText(message);
        }
    }

    private void validationDone() {
        progress.finish();
        panel.progressPanel.remove(progressComponent);
        panel.progressPanel.revalidate();
        panel.progressPanel.repaint();
        panel.progressPanel.setVisible(false);
        setEditable(true);

        OutputLogger logger = new OutputLogger(); // XXX to use the logger this way is a hack
        if(isValid()) {
            logger.logCommandLine("==[IDE]== " + DateFormat.getDateTimeInstance().format(new Date()) + " " + this.getClass().getName() + ".validationDone() - finnished");
        } else {
            logger.logCommandLine("==[IDE]== " + DateFormat.getDateTimeInstance().format(new Date()) + " " + this.getClass().getName() + ".validationDone() - finnished with error");
        }
    }
    
    private void setEditable(boolean editable) {
        repository.setEditable(editable);        
    }
        
    void storeValues() {
        repository.store();        
    }
    
    public RepositoryFile getRepositoryFile() {
        return repositoryFile;
    }
    
    private Repository.SelectedRepository getSelectedRepository() {      
        try {
            return repository.getSelectedRepository();
        } catch (Exception ex) {
            invalid(ex.getLocalizedMessage()); 
            return null;
        }
    }                   
    
    private ProxyDescriptor getProxyDescriptor() {
        return repository.getProxyDescriptor();
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals(Repository.PROP_VALID)) {
            if(repository.isValid()) {
                valid(repository.getMessage());
            } else {
                invalid(repository.getMessage());
            }
        }
    }

}

