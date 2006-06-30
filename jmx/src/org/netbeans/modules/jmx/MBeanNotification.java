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

package org.netbeans.modules.jmx;

import java.util.ArrayList;
import java.util.List;

/**
 * Class which describes the structure of an MBean notification
 *
 */
public class MBeanNotification {

    private String notificationClass = ""; // NOI18N
    private String notificationDescription = ""; // NOI18N
    private List<MBeanNotificationType> notificationType = null;

    /**
     * Custom constructor
     * @param notificationClass the java class of the notification
     * @param notificationDescription the description of the notification
     * @param notificationType the list of the notification types for that 
     * notification
     */
    public MBeanNotification(String notificationClass, 
            String notificationDescription,
            List<MBeanNotificationType> notificationType) {
        this.notificationClass = notificationClass;
        this.notificationDescription = notificationDescription;
        this.notificationType = notificationType;
    }
    
    /**
     * Sets the notification class
     * @param notifClass the notification class to set
     */
    public void setNotificationClass(String notifClass) {
        this.notificationClass = notifClass;
    }
    
    /**
     * Method which returns the class of the notification
     * @return String the class of the notification
     */
    public String getNotificationClass() {
        return notificationClass;
    }
    
    /**
     * Sets the notification description
     * @param notifDescr the notification description to set
     */
    public void setNotificationDescription(String notifDescr) {
        this.notificationDescription = notifDescr;
    }
    
    /**
     * Method which returns the description of the notification
     * @return String the description of the notification
     *
     */
    public String getNotificationDescription() {
        return notificationDescription;
    }
    
    /**
     * Adds a notification type to the notification type list
     * @param notifType the notification type to add to the list
     */
    public void addNotificationType(MBeanNotificationType notifType) {
        notificationType.add(notifType);
    }
    
    /**
     * Removes a notification type from the list
     * @param notifType the notification type to remove from the list
     */
    public void removeNotificationType(MBeanNotificationType notifType) {
        notificationType.remove(notifType);
    }
    
    /**
     * Removes a notification type by it's index in the list
     * @param index the index of the notification type to remove
     */
    public void removeNotificationType(int index) {
        notificationType.remove(index);
    }
    
    /**
     * Method which returns the a notification type by it's index in the list
     * @return MBeanNotificationType the notification type
     * @param index the index of the notification type to return
     */
    public MBeanNotificationType getNotificationType(int index) {
        return notificationType.get(index);
    }
    
    /**
     * Sets the notification type list of this notification
     * @param array array of notification types
     */
    public void setNotificationTypeList(ArrayList<MBeanNotificationType> 
            array) {
        notificationType = array;
    }
    
    /**
     * Returns the whole notification type list for the current notification
     * @return ArrayList<MBeanNotificationType> the notification type list
     */
    public List<MBeanNotificationType> getNotificationTypeList() {
        return notificationType;
    }
    
    /**
     * Returns a string concat of all notification types for the current
     * notification; each one seperated by ","
     * @return String the string containing all notification types
     */
    public String getNotificationTypeClasses() {
        String notifTypeClass = ""; // NOI18N
        for (int i = 0; i < notificationType.size(); i++) {
            notifTypeClass += notificationType.get(i).getNotificationType();
            
            if (i < notificationType.size() -1)
                notifTypeClass += ","; // NOI18N
        }
        return notifTypeClass;
    }
    
    /**
     * Method which returns the number of types of the current notification
     * Returns -1 if null
     * @return int the number of types of the notification
     *
     */
    public int getNotificationTypeCount() {
        if (notificationType != null)
            return notificationType.size();
        else
            return -1;
    }
}
