/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.ddloaders.multiview;

import org.netbeans.modules.j2ee.dd.api.common.VersionNotSupportedException;
import org.netbeans.modules.j2ee.dd.api.ejb.ActivationConfig;
import org.netbeans.modules.j2ee.dd.api.ejb.ActivationConfigProperty;
import org.netbeans.modules.j2ee.dd.api.ejb.MessageDriven;
import org.netbeans.modules.j2ee.dd.api.ejb.MessageDrivenDestination;
import org.netbeans.modules.j2ee.ddloaders.multiview.ui.MessageDrivenOverviewForm;
import org.netbeans.modules.xml.multiview.ItemComboBoxHelper;
import org.netbeans.modules.xml.multiview.ItemEditorHelper;
import org.netbeans.modules.xml.multiview.ItemOptionHelper;
import org.netbeans.modules.xml.multiview.ui.SectionNodeView;

import javax.swing.*;

/**
 * @author pfiala
 */
public class MessageDrivenOverviewPanel extends MessageDrivenOverviewForm {

    private ActivationConfig config;
    private static final String PROPERTY_MESSAGE_SELECTOR = "messageSelector";  //NOI18N
    private static final String PROPERTY_ACKNOWLEDGE_NAME = "acknowledgeMode";  //NOI18N
    private static final String PROPERTY_SUBSCRIPTION_DURABILITY = "subscriptionDurability";    //NOI18N
    private static final String DESTINATION_TYPE_TOPIC = MessageDrivenDestination.DESTINATION_TYPE_TOPIC;
    private static final String DESTINATION_TYPE_QUEUE = MessageDrivenDestination.DESTINATION_TYPE_QUEUE;
    private static final String SUBSCRIPTION_DURABILITY_NONDURABLE = MessageDrivenDestination.SUBSCRIPTION_DURABILITY_NONDURABLE;
    private static final String SUBSCRIPTION_DURABILITY_DURABLE = MessageDrivenDestination.SUBSCRIPTION_DURABILITY_DURABLE;
    private static final String DESTINATION_TYPE = MessageDrivenDestination.DESTINATION_TYPE;

    /**
     * @param sectionNodeView enclosing SectionNodeView object
     */
    public MessageDrivenOverviewPanel(SectionNodeView sectionNodeView, final MessageDriven messageDriven) {
        super(sectionNodeView);

        final EjbJarMultiViewDataObject dataObject = (EjbJarMultiViewDataObject) sectionNodeView.getDataObject();

        addRefreshable(new ItemEditorHelper(getNameTextField(), new TextItemEditorModel(dataObject, false) {
            protected String getValue() {
                return messageDriven.getEjbName();
            }

            protected void setValue(String value) {
                messageDriven.setEjbName(value);
            }
        }));

        addRefreshable(new ItemOptionHelper(dataObject, getTransactionTypeButtonGroup()) {
            public String getItemValue() {
                return messageDriven.getTransactionType();
            }

            public void setItemValue(String value) {
                messageDriven.setTransactionType(value);
            }
        });

        config = getActivationConfig(messageDriven);

        final JTextField messageSelectorTextField = getMessageSelectorTextField();

        final JComboBox destinationTypeComboBox = getDestinationTypeComboBox();
        destinationTypeComboBox.addItem(DESTINATION_TYPE_TOPIC);
        destinationTypeComboBox.addItem(DESTINATION_TYPE_QUEUE);

        final JComboBox durabilityComboBox = getDurabilityComboBox();
        durabilityComboBox.addItem(SUBSCRIPTION_DURABILITY_NONDURABLE);
        durabilityComboBox.addItem(SUBSCRIPTION_DURABILITY_DURABLE);

        if (config == null) {
            durabilityComboBox.setEnabled(false);
            messageSelectorTextField.setEnabled(false);
        } else {
            addRefreshable(new ItemEditorHelper(messageSelectorTextField,
                            new TextItemEditorModel(dataObject, true, true) {
                protected String getValue() {
                    return getConfigProperty(PROPERTY_MESSAGE_SELECTOR);
                }

                protected void setValue(String value) {
                    setConfigProperty(PROPERTY_MESSAGE_SELECTOR, value);
                }
            }));

            addRefreshable(new ItemOptionHelper(dataObject, getAcknowledgeModeButtonGroup()) {
                public String getItemValue() {
                    return getConfigProperty(PROPERTY_ACKNOWLEDGE_NAME, "Auto-acknowledge");//NOI18N
                }

                public void setItemValue(String value) {
                    setConfigProperty(PROPERTY_ACKNOWLEDGE_NAME, value);
                }
            });

            final DurabilityComboBoxHelper durabilityComboBoxHelper = new DurabilityComboBoxHelper(dataObject, durabilityComboBox);

            new ItemComboBoxHelper(dataObject, destinationTypeComboBox) {
                {
                    setDurabilityEnabled();
                }

                public String getItemValue() {
                    return getConfigProperty(MessageDrivenDestination.DESTINATION_TYPE);
                }

                public void setItemValue(String value) {
                    setConfigProperty(DESTINATION_TYPE, value);
                    setDurabilityEnabled();
                }

                private void setDurabilityEnabled() {
                    durabilityComboBoxHelper.setComboBoxEnabled(DESTINATION_TYPE_TOPIC.equals(getItemValue()));
                }
            };

        }

        // the second ItemComboboxHelper for destinationTypeComboBox handles message-destination-type element
        new ItemComboBoxHelper(dataObject, destinationTypeComboBox) {

            public String getItemValue() {
                try {
                    return messageDriven.getMessageDestinationType();
                } catch (VersionNotSupportedException e) {
                    return null;
                }
            }

            public void setItemValue(String value) {
                try {
                    messageDriven.setMessageDestinationType(value);
                } catch (VersionNotSupportedException e) {
                    // ignore
                }
            }

        };

    }

    private ActivationConfig getActivationConfig(final MessageDriven messageDriven) {
        ActivationConfig ac;

        try {
            ac = messageDriven.getActivationConfig();
        } catch (VersionNotSupportedException e1) {
            ac = null;
        }
        return ac;
    }

    private String getConfigProperty(String propertyName) {
        return getConfigProperty(propertyName, null);
    }

    private String getConfigProperty(String propertyName, String defaultValue) {
        ActivationConfigProperty[] properties = config.getActivationConfigProperty();
        String value = null;
        for (int i = 0; i < properties.length; i++) {
            ActivationConfigProperty property = properties[i];
            if (propertyName.equalsIgnoreCase(property.getActivationConfigPropertyName())) {
                value = property.getActivationConfigPropertyValue();
                break;
            }
        }
        return value == null ? defaultValue : value;
    }

    private void setConfigProperty(String propertyName, String propertyValue) {
        ActivationConfigProperty[] properties = config.getActivationConfigProperty();
        for (int i = 0; i < properties.length; i++) {
            ActivationConfigProperty property = properties[i];
            if (propertyName.equalsIgnoreCase(property.getActivationConfigPropertyName())) {
                if (propertyValue != null) {
                    property.setActivationConfigPropertyValue(propertyValue);
                } else {
                    config.removeActivationConfigProperty(property);
                }
                signalUIChange();
                return;
            }
        }
        if (propertyValue != null) {
            ActivationConfigProperty property = config.newActivationConfigProperty();
            property.setActivationConfigPropertyName(propertyName);
            property.setActivationConfigPropertyValue(propertyValue);
            config.addActivationConfigProperty(property);
        }
    }

    public void dataModelPropertyChange(Object source, String propertyName, Object oldValue, Object newValue) {
        scheduleRefreshView();
    }

    private class DurabilityComboBoxHelper extends ItemComboBoxHelper {

        public DurabilityComboBoxHelper(EjbJarMultiViewDataObject dataObject, JComboBox durabilityComboBox) {
            super(dataObject, durabilityComboBox);
        }

        public String getItemValue() {
            return getConfigProperty(PROPERTY_SUBSCRIPTION_DURABILITY, "NonDurable");//NOI18N
        }

        public void setItemValue(String value) {
            setConfigProperty(PROPERTY_SUBSCRIPTION_DURABILITY, value);
        }

        public void setComboBoxEnabled(boolean enabled) {
            getComboBox().setEnabled(enabled);
            setValue(enabled ? getItemValue() : null);
        }
    }
}
