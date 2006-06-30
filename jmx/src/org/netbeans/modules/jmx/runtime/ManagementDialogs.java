/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 2004-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.jmx.runtime;

import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

import java.awt.*;

public class ManagementDialogs {
  private static DialogDisplayer standard = DialogDisplayer.getDefault();
  private static ManagementDialogs defaultInstance;

  private ManagementDialogs () { } // avoid direct instance creation

  public static ManagementDialogs getDefault () {
    if (defaultInstance == null)
      defaultInstance = new ManagementDialogs ();
    return defaultInstance;
  }

  public Object notify(NotifyDescriptor descriptor) {
    return standard.notify(descriptor);
  }

  public Dialog createDialog(DialogDescriptor descriptor) {
    return standard.createDialog(descriptor);
  }


  public static class DNSAMessage extends NotifyDescriptor.Message {
    /**
     * Create an informational report about the results of a command.
     *
     * @param message the message object
     * @see org.openide.NotifyDescriptor#NotifyDescriptor
     */
    public DNSAMessage(String key, Object message) {
      super(message);
    }

    /**
     * Create a report about the results of a command.
     *
     * @param message     the message object
     * @param messageType the type of message to be displayed
     * @see org.openide.NotifyDescriptor#NotifyDescriptor
     */
    public DNSAMessage(String key, Object message, int messageType) {
      super(message, messageType);
    }
  }

  public static class DNSAConfirmation extends NotifyDescriptor.Confirmation {

    /**
     * Create a yes/no/cancel question with default title.
     *
     * @param message the message object
     * @see org.openide.NotifyDescriptor#NotifyDescriptor
     */
    public DNSAConfirmation(String key, Object message) {
      super(message);
    }

    /**
     * Create a yes/no/cancel question.
     *
     * @param message the message object
     * @param title   the dialog title
     * @see org.openide.NotifyDescriptor#NotifyDescriptor
     */
    public DNSAConfirmation(String key, Object message, String title) {
      super(message, title);
    }

    /**
     * Create a question with default title.
     *
     * @param message    the message object
     * @param optionType the type of options to display to the user
     * @see org.openide.NotifyDescriptor#NotifyDescriptor
     */
    public DNSAConfirmation(String key, Object message, int optionType) {
      super(message, optionType);
    }

    /**
     * Create a question.
     *
     * @param message    the message object
     * @param title      the dialog title
     * @param optionType the type of options to display to the user
     * @see org.openide.NotifyDescriptor#NotifyDescriptor
     */
    public DNSAConfirmation(String key, Object message, String title, int optionType) {
      super(message, title, optionType);
    }

    /**
     * Create a confirmation with default title.
     *
     * @param message     the message object
     * @param optionType  the type of options to display to the user
     * @param messageType the type of message to use
     * @see org.openide.NotifyDescriptor#NotifyDescriptor
     */
    public DNSAConfirmation(String key, Object message, int optionType, int messageType) {
      super(message, optionType, messageType);
    }

    /**
     * Create a confirmation.
     *
     * @param message     the message object
     * @param title       the dialog title
     * @param optionType  the type of options to display to the user
     * @param messageType the type of message to use
     * @see org.openide.NotifyDescriptor#NotifyDescriptor
     */
    public DNSAConfirmation(String key, Object message, String title, int optionType, int messageType) {
      super(message, title, optionType, messageType);
    }
  }
}
