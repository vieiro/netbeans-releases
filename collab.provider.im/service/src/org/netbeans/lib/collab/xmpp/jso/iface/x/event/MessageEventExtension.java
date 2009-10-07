/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.lib.collab.xmpp.jso.iface.x.event;

import java.util.List;


import org.jabberstudio.jso.Extension;
import org.jabberstudio.jso.NSI;
import org.jabberstudio.jso.StreamElement;
import org.jabberstudio.jso.StreamDataFactory;
import org.jabberstudio.jso.util.Enumerator;

import net.outer_planes.jso.ElementNode;


/**
 * <p>
 * Interface for representing a "jabber:x:event" extension
 *
 * @author Rahul Shah
 *
 */
public interface MessageEventExtension extends Extension {

    //Constants
    /** The namespace URI for <tt></tt> and its know
        

     */

    public static final String NAMESPACE = "jabber:x:event";

    public static final NSI NAME = new NSI("x",NAMESPACE);

    public static final class EventType extends Enumerator {
        
        private EventType(String name) {
            super(name);
        }
                
        /*public boolean equals(Object o) {
          if(o instanceof NSI){
          return equals((NSI)o);
          } else if(o instanceof EventType) {
          return equals((EventType)o);
          } else if(o instanceof String) {
          return false;
          } else {
          return false;
          }

          }

          public boolean equals(NSI name) {
          NSI thisName = new NSI(toString(),NAMESPACE);
          return thisName.equals(name);
          }

          public boolean equals(EventType evt) {
          return toString().equalsIgnoreCase(evt.toString());

          }*/

    }

    public static final EventType OFFLINE   = new EventType("offline");

    public static final EventType DELIVERED = new EventType("delivered");

    public static final EventType DISPLAYED = new EventType("displayed");

    public static final EventType COMPOSING = new EventType("composing");

    public void addEvent(EventType evt);

    public void removeEvent(EventType evt);

    // public EventType getEvent(String eventName);

    public List getEvents();

    public void setMessageID(String id);

    public String getMessageID();

    public boolean hasMessageID();

    public boolean hasMessageEvent(EventType evt);

    public boolean containsMessageEvent();

    //        public void removeId(String id);
        
}
