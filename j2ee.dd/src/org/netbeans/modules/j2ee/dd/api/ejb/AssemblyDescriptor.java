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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.dd.api.ejb;

//
// This interface has all of the bean info accessor methods.
//
import org.netbeans.modules.j2ee.dd.api.common.SecurityRole;
import org.netbeans.modules.j2ee.dd.api.common.MessageDestination;
import org.netbeans.modules.j2ee.dd.api.common.VersionNotSupportedException;

public interface AssemblyDescriptor extends org.netbeans.modules.j2ee.dd.api.common.CommonDDBean {

        public static final String SECURITY_ROLE = "SecurityRole";	// NOI18N
	public static final String METHOD_PERMISSION = "MethodPermission";	// NOI18N
	public static final String CONTAINER_TRANSACTION = "ContainerTransaction";	// NOI18N
	public static final String MESSAGE_DESTINATION = "MessageDestination";	// NOI18N
	public static final String EXCLUDE_LIST = "ExcludeList";	// NOI18N
        
        public ContainerTransaction[] getContainerTransaction();
        
        public ContainerTransaction getContainerTransaction(int index);
        
        public void setContainerTransaction(ContainerTransaction[] value);
        
        public void setContainerTransaction(int index, ContainerTransaction value);
        
	public int sizeContainerTransaction();

	public int addContainerTransaction(org.netbeans.modules.j2ee.dd.api.ejb.ContainerTransaction value);

	public int removeContainerTransaction(org.netbeans.modules.j2ee.dd.api.ejb.ContainerTransaction value);
        
        public ContainerTransaction newContainerTransaction();
        
        public MethodPermission[] getMethodPermission();
        
        public MethodPermission getMethodPermission(int index);
        
        public void setMethodPermission(MethodPermission[] value);
        
        public void setMethodPermission(int index, MethodPermission value);
        
	public int addMethodPermission(org.netbeans.modules.j2ee.dd.api.ejb.MethodPermission value);

	public int sizeMethodPermission();

	public int removeMethodPermission(org.netbeans.modules.j2ee.dd.api.ejb.MethodPermission value);
        
        public MethodPermission newMethodPermission();
        
        public SecurityRole[] getSecurityRole();
        
        public SecurityRole getSecurityRole(int index);
        
        public void setSecurityRole(SecurityRole[] value);
        
        public void setSecurityRole(int index, SecurityRole value);
     
	public int sizeSecurityRole();

	public int removeSecurityRole(org.netbeans.modules.j2ee.dd.api.common.SecurityRole value);

	public int addSecurityRole(org.netbeans.modules.j2ee.dd.api.common.SecurityRole value);
        
        public SecurityRole newSecurityRole();

        public void setExcludeList(ExcludeList value);

        public ExcludeList getExcludeList();
        
        public ExcludeList newExcludeList();
        
        //2.1
        public MessageDestination[] getMessageDestination() throws VersionNotSupportedException;
        
        public MessageDestination getMessageDestination(int index) throws VersionNotSupportedException;
        
        public void setMessageDestination(MessageDestination[] value) throws VersionNotSupportedException;
        
        public void setMessageDestination(int index, MessageDestination value) throws VersionNotSupportedException;
        
        public int sizeMessageDestination() throws VersionNotSupportedException;

	public int removeMessageDestination(MessageDestination value) throws VersionNotSupportedException;

	public int addMessageDestination(MessageDestination value) throws VersionNotSupportedException;
        
        public MessageDestination newMessageDestination() throws VersionNotSupportedException;
        
}
 

