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

package org.netbeans.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static junit.framework.Assert.*;
import junit.framework.AssertionFailedError;

/**
 * Lets you register mock implementations of global services.
 * You might for example do this in {@link TestCase#setUp}.
 * @see org.openide.util.Lookup
 * @see java.util.ServiceLoader
 * @since org.netbeans.modules.nbjunit/1 1.30
 * @author Jesse Glick, Jaroslav Tulach
 */
public class MockServices {
    
    private MockServices() {}
    
    /**
     * Set (or reset) the set of mock services.
     * Clears any previous registration.
     * After this call, Lookup and ServiceLoader should both
     * "see" the newly registered classes.
     * (Other classes really registered in META-INF/services/ will
     * also be available, but after the ones you have registered.)
     * Each class must be public with a public no-arg constructor.
     * @param services a set of service classes to register
     * @throws IllegalArgumentException if some classes are not instantiable as beans
     */
    public static void setServices(Class... services) throws IllegalArgumentException {
        Thread.currentThread().setContextClassLoader(new ServiceClassLoader(services));
        // Need to also reset global lookup since it caches the singleton and we need to change it.
        try {
            Class lookup = Class.forName("org.openide.util.Lookup");
            Field defaultLookup = lookup.getDeclaredField("defaultLookup");
            defaultLookup.setAccessible(true);
            defaultLookup.set(null, null);
        } catch (ClassNotFoundException x) {
            // Fine, not using org-openide-lookup.jar.
        } catch (Exception x) {
            Logger.getLogger(MockServices.class.getName()).log(Level.WARNING, "Could not reset Lookup.getDefault()", x);
        }
    }
    
    private static final class ServiceClassLoader extends ClassLoader {
        
        private final Class[] services;
        
        public ServiceClassLoader(Class[] services) {
            super(MockServices.class.getClassLoader());
            for (Class c : services) {
                try {
                    assertEquals(c, getParent().loadClass(c.getName()));
                    if (!Modifier.isPublic(c.getModifiers())) {
                        throw new IllegalArgumentException("Class " + c.getName() + " must be public");
                    }
                    c.getConstructor();
                } catch (IllegalArgumentException x) {
                    throw x;
                } catch (NoSuchMethodException x) {
                    throw (IllegalArgumentException) new IllegalArgumentException("Class " + c.getName() + " has no public no-arg constructor").initCause(x);
                } catch (Exception x) {
                    throw (AssertionFailedError) new AssertionFailedError(x.toString()).initCause(x);
                }
            }
            this.services = services;
        }
        
        public URL getResource(String name) {
            Enumeration<URL> r;
            try {
                r = getResources(name);
            } catch (IOException x) {
                return null;
            }
            return r.hasMoreElements() ? r.nextElement() : null;
        }
        
        public Enumeration<URL> getResources(String name) throws IOException {
            final Enumeration<URL> supe = super.getResources(name);
            String prefix = "META-INF/services/";
            if (name.startsWith(prefix)) {
                try {
                    Class xface = loadClass(name.substring(prefix.length()));
                    List<String> impls = new ArrayList<String>();
                    for (Class c : services) {
                        @SuppressWarnings("unchecked") // OK to be raw type; cannot be generic anyway
                        boolean assignable = xface.isAssignableFrom(c);
                        if (assignable) {
                            impls.add(c.getName());
                        }
                    }
                    if (!impls.isEmpty()) {
                        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        PrintWriter pw = new PrintWriter(new OutputStreamWriter(baos, "UTF-8"));
                        for (String impl : impls) {
                            pw.println(impl);
                        }
                        pw.close();
                        final URL u = new URL("metainfservices", null, 0, xface.getName(), new URLStreamHandler() {
                            protected URLConnection openConnection(URL _u) throws IOException {
                                return new URLConnection(_u) {
                                    public void connect() throws IOException {}
                                    public InputStream getInputStream() throws IOException {
                                        return new ByteArrayInputStream(baos.toByteArray());
                                    }
                                };
                            }
                        });
                        return new Enumeration<URL>() {
                            private boolean parent = false;
                            public boolean hasMoreElements() {
                                return !parent || supe.hasMoreElements();
                            }
                            public URL nextElement() throws NoSuchElementException {
                                if (parent) {
                                    return supe.nextElement();
                                } else {
                                    parent = true;
                                    return u;
                                }
                            }
                        };
                    }
                } catch (ClassNotFoundException x) {}
            }
            return supe;
        }
        
        /*
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            // XXX make sure services can be loaded
            return super.loadClass(name);
        }
         */
        
    }
    
}
