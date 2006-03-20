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

package org.netbeans.installer.cluster;

import com.installshield.product.SoftwareObjectKey;
import com.installshield.product.SoftwareVersion;
import com.installshield.product.service.product.ProductService;
import com.installshield.util.Log;
import com.installshield.wizard.WizardAction;
import com.installshield.wizard.WizardBeanEvent;
import com.installshield.wizard.WizardBuilderSupport;
import com.installshield.wizard.service.ServiceException;

import java.util.Locale;

import org.netbeans.installer.Util;

/** This class is used to initialize some system properties at beginning
 * of installation.
 */
public class SetSystemPropertiesAction extends WizardAction {
    
    public void build(WizardBuilderSupport support) {
        super.build(support);
        Locale [] locales = new Locale[3];
        locales[0] = new Locale("zh","HK");
        locales[1] = new Locale("zh","TW");
        locales[2] = new Locale("zh","CN");
        try {
            support.putClass(Util.class.getName());
            support.putResourceBundles("org.netbeans.installer.cluster.Bundle",locales);
        } catch (Exception ex) {
            System.out.println(ex.getLocalizedMessage());
            ex.printStackTrace();
        }
    }
    
    public void execute(WizardBeanEvent evt) {
        Util.logSystemInfo(this);
        resolveProductBeanProperties();
    }
    
    private void resolveProductBeanProperties () {
        try {
            ProductService service = (ProductService) getService(ProductService.NAME);
            String prop;
            String resolvedProp;
            
            prop = (String) service.getProductBeanProperty
            (ProductService.DEFAULT_PRODUCT_SOURCE, "beanProduct", "name");
            resolvedProp = resolveString(prop);
            logEvent(this, Log.DBG,"prop: " + prop + " resolvedProp: " + resolvedProp);
            service.setRetainedProductBeanProperty
            (ProductService.DEFAULT_PRODUCT_SOURCE, "beanProduct", "name", resolvedProp);

            prop = (String) service.getProductBeanProperty
            (ProductService.DEFAULT_PRODUCT_SOURCE, "beanProduct", "description");
            resolvedProp = resolveString(prop);
            logEvent(this, Log.DBG,"prop: " + prop + " resolvedProp: " + resolvedProp);
            service.setRetainedProductBeanProperty
            (ProductService.DEFAULT_PRODUCT_SOURCE, "beanProduct", "description", resolvedProp);
            
            prop = (String) service.getProductBeanProperty
            (ProductService.DEFAULT_PRODUCT_SOURCE, "beanProduct", "productNumber");
            resolvedProp = resolveString(prop);
            logEvent(this, Log.DBG,"prop: " + prop + " resolvedProp: " + resolvedProp);
            service.setRetainedProductBeanProperty
            (ProductService.DEFAULT_PRODUCT_SOURCE, "beanProduct", "productNumber", resolvedProp);
            
            prop = (String) service.getProductBeanProperty
            (ProductService.DEFAULT_PRODUCT_SOURCE, "beanProduct", "vendor");
            resolvedProp = resolveString(prop);
            logEvent(this, Log.DBG,"prop: " + prop + " resolvedProp: " + resolvedProp);
            service.setRetainedProductBeanProperty
            (ProductService.DEFAULT_PRODUCT_SOURCE, "beanProduct", "vendor", resolvedProp);
            
            prop = (String) service.getProductBeanProperty
            (ProductService.DEFAULT_PRODUCT_SOURCE, "beanProduct", "vendorWebsite");
            resolvedProp = resolveString(prop);
            logEvent(this, Log.DBG,"prop: " + prop + " resolvedProp: " + resolvedProp);
            service.setRetainedProductBeanProperty
            (ProductService.DEFAULT_PRODUCT_SOURCE, "beanProduct", "vendorWebsite", resolvedProp);
            
            SoftwareObjectKey keyObject;
            SoftwareVersion version;
            String major, minor, maintenance, key;

            // ---------------------- Product ---------------------------
            keyObject = (SoftwareObjectKey) service.getProductBeanProperty
            (ProductService.DEFAULT_PRODUCT_SOURCE, "beanProduct", "key");
            key = resolveString("$L(org.netbeans.installer.cluster.Bundle,Product.UID)");
            logEvent(this, Log.DBG,"Product UID: " + key);
            keyObject.setUID(key);
            
            major = resolveString("$L(org.netbeans.installer.cluster.Bundle,Product.major)");
            minor = resolveString("$L(org.netbeans.installer.cluster.Bundle,Product.minor)");
            maintenance = resolveString("$L(org.netbeans.installer.cluster.Bundle,Product.maintenance)");
            
            version = new SoftwareVersion();
            version.setMajor(major);
            version.setMinor(minor);
            version.setMaintenance(maintenance);
            logEvent(this, Log.DBG,"Product version: " + getStringForm(version));
            keyObject.setVersion(version);
            
            service.setRetainedProductBeanProperty
            (ProductService.DEFAULT_PRODUCT_SOURCE, "beanProduct", "key", keyObject);
            
            // ------------------ Profiler Cluster -----------------------------
            keyObject = (SoftwareObjectKey) service.getProductBeanProperty
            (ProductService.DEFAULT_PRODUCT_SOURCE, "beanProfilerCluster", "key");
            key = resolveString("$L(org.netbeans.installer.cluster.Bundle,ProfilerCluster.UID)");
            logEvent(this, Log.DBG,"Profiler Cluster UID: " + key);
            keyObject.setUID(key);
            
            version = new SoftwareVersion();
            version.setMajor(major);
            version.setMinor(minor);
            version.setMaintenance(maintenance);
            logEvent(this, Log.DBG,"Profiler Cluster version: " + getStringForm(version));
            keyObject.setVersion(version);
            
            service.setRetainedProductBeanProperty
            (ProductService.DEFAULT_PRODUCT_SOURCE, "beanProfilerCluster", "key", keyObject);
        } catch (ServiceException ex) {
            ex.printStackTrace();
            Util.logStackTrace(this,ex);
        }
    }
    
    /** Simplified conversion of SoftwareVersion instance to String. Just for logging. */
    private String getStringForm (SoftwareVersion version) {
        String ret = "";
        if (version.getMajor().length() == 0) {
            return ret;
        } else {
            ret = version.getMajor();
        }
        if (version.getMinor().length() == 0) {
            return ret;
        } else {
            ret += "." + version.getMinor();
        }
        if (version.getMaintenance().length() == 0) {
            return ret;
        } else {
            ret += "." + version.getMaintenance();
        }
        return ret;
    }
    
}
