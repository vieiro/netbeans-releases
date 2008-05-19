/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.php.project.connections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.event.ChangeListener;
import org.openide.util.ChangeSupport;
import org.openide.util.NbBundle;

/**
 * @author Radek Matous, Tomas Mysik
 */
public class ConfigManager {
    private static final String PROP_DISPLAY_NAME = "$label"; // NOI18N

    private final Map<String/*|null*/, Map<String, String/*|null*/>/*|null*/> configs;
    private final ConfigProvider configProvider;
    private final String[] propertyNames;
    private final ChangeSupport changeSupport;

    public ConfigManager(ConfigProvider configProvider) {
        this.configProvider = configProvider;
        changeSupport = new ChangeSupport(this);
        configs = configProvider.getConfigs();

        List<String> tmp = new ArrayList<String>(Arrays.asList(configProvider.getConfigProperties()));
        tmp.add(PROP_DISPLAY_NAME);
        propertyNames = tmp.toArray(new String[tmp.size()]);
    }

    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }

    public synchronized boolean exists(String name) {
        return configs.keySet().contains(name) && configs.get(name) != null;
    }

    public synchronized Configuration createNew(String name, String displayName) {
        assert !exists(name);
        configs.put(name, new HashMap<String, String>());
        Configuration retval  = new Configuration(name);
        if (!name.equals(displayName)) {
            retval.putValue(PROP_DISPLAY_NAME, displayName);
        }
        markAsCurrentConfiguration(name);
        return retval;
    }

    public synchronized Collection<String> configurationNames() {
        return configs.keySet();
    }

    public synchronized Configuration currentConfiguration() {
        return new Configuration(configProvider.getActiveConfig());
    }

    public Configuration defaultConfiguration() {
        return new Configuration();
    }

    public synchronized Configuration configurationFor(String name) {
        return new Configuration(name);
    }

    public synchronized void markAsCurrentConfiguration(String currentConfig) {
        assert configs.keySet().contains(currentConfig);
        configProvider.setActiveConfig(currentConfig);
        changeSupport.fireChange();
    }

    private String[] getPropertyNames() {
        return propertyNames;
    }

    private Map<String, String/*|null*/> getProperties(String config) {
        return configs.get(config);
    }

    public final class Configuration {
        private final String name;

        private Configuration() {
            this(null);
        }

        private Configuration(String name) {
            if (name != null && name.trim().length() == 0) {
                name = null;
            }
            assert configs.keySet().contains(name) : "Unknown configuration: " + name;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public String getDisplayName() {
            String retval = getValue(PROP_DISPLAY_NAME);
            retval = retval != null ? retval : getName();
            return retval != null ? retval : NbBundle.getMessage(ConfigManager.class, "LBL_DefaultConfiguration");
        }

        public boolean isDefault() {
            return name == null;
        }

        public void delete() {
            synchronized (ConfigManager.this) {
                configs.put(getName(), null);
                //configs.remove(getName());
                markAsCurrentConfiguration(null);
            }
        }

        private boolean isDeleted() {
            return configs.get(getName()) == null;
        }

        public String getValue(String propertyName) {
            assert Arrays.asList(getPropertyNames()).contains(propertyName) : "Unknown property: " + propertyName;
            //assert !isDeleted();
            synchronized (ConfigManager.this) {
                return !isDeleted() ?  getProperties(getName()).get(propertyName) : null;
            }
        }

        public void putValue(String propertyName, String value) {
            assert Arrays.asList(getPropertyNames()).contains(propertyName) : "Unknown property: " + propertyName;
            assert !isDeleted();
            synchronized (ConfigManager.this) {
                getProperties(getName()).put(propertyName, value);
            }
        }

        public String[] getPropertyNames() {
            synchronized (ConfigManager.this) {
                return ConfigManager.this.getPropertyNames();
            }
        }
    }

    /**
     * Configuration provider for {@link ConfigManager configuration manager}.
     */
    public interface ConfigProvider {

        /**
         * Get all names of the properties which can be defined in each configuration.
         * @return an array of property names.
         */
        String[] getConfigProperties();

        /**
         * Get all the configurations the configuration manager should operate with.
         * @return all the configurations.
         */
        Map<String/*|null*/, Map<String, String/*|null*/>/*|null*/> getConfigs();

        /**
         * Get the currently active configuration name.
         * @return the currently active configuration name.
         */
        String getActiveConfig();

        /**
         * Set the currently active configuration name.
         * @param configName the currently active configuration name.
         */
        void setActiveConfig(String configName);
    }
}
