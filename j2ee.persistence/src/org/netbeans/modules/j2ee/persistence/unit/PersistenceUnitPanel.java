/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.modules.j2ee.persistence.unit;

import java.awt.CardLayout;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.DefaultListModel;
import javax.swing.text.JTextComponent;
import org.netbeans.api.db.explorer.ConnectionManager;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.j2ee.persistence.api.EntityClassScope;
import org.netbeans.modules.j2ee.persistence.dd.common.Persistence;
import org.netbeans.modules.j2ee.persistence.dd.common.PersistenceUnit;
import org.netbeans.modules.j2ee.persistence.dd.common.Properties;
import org.netbeans.modules.j2ee.persistence.dd.common.Property;
import org.netbeans.modules.j2ee.persistence.provider.DefaultProvider;
import org.netbeans.modules.j2ee.persistence.provider.Provider;
import org.netbeans.modules.j2ee.persistence.wizard.Util;
import org.netbeans.modules.j2ee.persistence.wizard.unit.JdbcListCellRenderer;
import org.netbeans.modules.j2ee.persistence.provider.ProviderUtil;
import org.netbeans.modules.j2ee.persistence.spi.datasource.JPADataSource;
import org.netbeans.modules.j2ee.persistence.spi.datasource.JPADataSourcePopulator;
import org.netbeans.modules.j2ee.persistence.spi.datasource.JPADataSourceProvider;
import org.netbeans.modules.j2ee.persistence.util.PersistenceProviderComboboxHelper;
import org.netbeans.modules.j2ee.persistence.wizard.library.PersistenceLibrarySupport;
import org.netbeans.modules.xml.multiview.ui.SectionInnerPanel;
import org.netbeans.modules.xml.multiview.ui.SectionView;
import org.netbeans.modules.xml.multiview.Error;
import org.openide.util.NbBundle;

/**
 *
 * @author  Martin Adamek
 */
public class PersistenceUnitPanel extends SectionInnerPanel {
    
    private final PersistenceUnit persistenceUnit;
    private PUDataObject dObj;
    private Project project;
    private boolean isContainerManaged;
    private boolean jpa20=false;

    //jpa2.0 specific
    private final java.lang.String[] validationModes = {"AUTO", "CALLBACK", "NONE"};//NOI18N
    private final java.lang.String[] cachingTypes = {"ALL", "NONE", "ENABLE_SELECTIVE", "DISABLE_SELECTIVE"};//NOI18N
    
    public PersistenceUnitPanel(SectionView view, final PUDataObject dObj,  final PersistenceUnit persistenceUnit) {
        super(view);
        this.dObj=dObj;
        this.jpa20=Persistence.VERSION_2_0.equals(dObj.getPersistence().getVersion());
        this.persistenceUnit=persistenceUnit;
        this.project = FileOwnerQuery.getOwner(this.dObj.getPrimaryFile());
        
        assert project != null : "Could not resolve project for " + dObj.getPrimaryFile(); //NOI18N
        
        if (PersistenceLibrarySupport.getLibrary(persistenceUnit) != null && ProviderUtil.getConnection(persistenceUnit) != null) {
            isContainerManaged = false;
        } else if (persistenceUnit.getJtaDataSource() != null || persistenceUnit.getNonJtaDataSource() != null) {
            isContainerManaged = true;
        } else {
            isContainerManaged = Util.isSupportedJavaEEVersion(project);
        }
        
        initComponents();
        
        PersistenceProviderComboboxHelper comboHelper = new PersistenceProviderComboboxHelper(project);
        if (isContainerManaged){
            comboHelper.connect(providerCombo);
            Provider provider = ProviderUtil.getProvider(persistenceUnit);
            providerCombo.setSelectedItem(provider);
        } else {
            comboHelper.connect(libraryComboBox);
            setSelectedLibrary();
        }
        
        setVisiblePanel();
        initIncludeAllEntities();
        initEntityList();
        
        initDataSource();
        if(jpa20)
        {
            initCache();
            initValidation();
        }
        
        nameTextField.setText(persistenceUnit.getName());
        setTableGeneration();
        handleCmAmSelection();
        
        registerModifiers();
        
    }
    
    /**
     * Registers the components that modify the model. Should be called after
     * these components have been initialized (otherwise the underlying file will
     * be marked  as modified immediately upon opening it).
     */
    private void registerModifiers(){
        if (isContainerManaged){
            addImmediateModifier(dsCombo);
            if(dsCombo.isEditable()) {
                addImmediateModifier((JTextComponent)dsCombo.getEditor().getEditorComponent());
            }
            addImmediateModifier(providerCombo);
            addImmediateModifier(jtaCheckBox);
            
        } else {
            addImmediateModifier(jdbcComboBox);
            addImmediateModifier(libraryComboBox);
        }
        addImmediateModifier(nameTextField);
        addImmediateModifier(ddDropCreate);
        addImmediateModifier(ddCreate);
        addImmediateModifier(ddUnknown);
        addImmediateModifier(includeAllEntities);
        if(jpa20)
        {
            addImmediateModifier(ddAll);
            addImmediateModifier(ddNone);
            addImmediateModifier(ddEnableSelective);
            addImmediateModifier(ddDisableSelective);
            addImmediateModifier(ddDefault);
            addImmediateModifier(ddAuto);
            addImmediateModifier(ddNoValidation);
            addImmediateModifier(ddCallBack);
        }
    }
    
    
    /**
     * Sets which panel (container/application) is visible.
     */
    private void setVisiblePanel(){
        providerLabel.setVisible(isContainerManaged);
        providerCombo.setVisible(isContainerManaged);
        dsCombo.setVisible(isContainerManaged);
        datasourceLabel.setVisible(isContainerManaged);
        //
        libraryLabel.setVisible(!isContainerManaged);
        libraryComboBox.setVisible(!isContainerManaged);
        jdbcComboBox.setVisible(!isContainerManaged);
        jdbcLabel.setVisible(!isContainerManaged);
        //
        validationStrategyPanel.setVisible(jpa20);
        validationStrategyLabel.setVisible(jpa20);
        cachingStrategyPanel.setVisible(jpa20);
        cachingStrategyLabel.setVisible(jpa20);
    }
    private void initCache(){
        org.netbeans.modules.j2ee.persistence.dd.persistence.model_2_0.PersistenceUnit pu2=(org.netbeans.modules.j2ee.persistence.dd.persistence.model_2_0.PersistenceUnit) persistenceUnit;
        String caching=pu2.getCaching();
        if(cachingTypes[0].equals(caching))
        {
            ddAll.setSelected(true);
        }
        else if(cachingTypes[1].equals(caching))
        {
            ddNone.setSelected(true);
        }
        else if(cachingTypes[2].equals(caching))
        {
            ddEnableSelective.setSelected(true);
        }
        else if(cachingTypes[3].equals(caching))
        {
            ddDisableSelective.setSelected(true);
        }
        else
        {
            ddDefault.setSelected(true);
        }

    }
    
    private void initValidation(){
        org.netbeans.modules.j2ee.persistence.dd.persistence.model_2_0.PersistenceUnit pu2=(org.netbeans.modules.j2ee.persistence.dd.persistence.model_2_0.PersistenceUnit) persistenceUnit;
        String validation=pu2.getValidationMode();
        if(validationModes[1].equals(validation))
        {
            ddCallBack.setSelected(true);
        }
        else if(validationModes[2].equals(validation))
        {
            ddNoValidation.setSelected(true);
        }
        else//according to specification auto is default
        {
            ddAuto.setSelected(true);
        }
    }
    
    private void initDataSource(){
        // Fixed enable/disable JTA checkbox based on isContainerManaged, 
        // instead of project environment (SE or not). See issue 147628
        jtaCheckBox.setEnabled(isContainerManaged);
        
        if (isContainerManaged && ProviderUtil.isValidServerInstanceOrNone(project)) {
            String jtaDataSource = persistenceUnit.getJtaDataSource();
            String nonJtaDataSource = persistenceUnit.getNonJtaDataSource();
            
            JPADataSourcePopulator dsPopulator = project.getLookup().lookup(JPADataSourcePopulator.class);
            if (dsPopulator != null){
                dsPopulator.connect(dsCombo);
                addModifier((JTextComponent)dsCombo.getEditor().getEditorComponent(), false);
            }
            
            String jndiName = (jtaDataSource != null ? jtaDataSource : nonJtaDataSource);
            selectDatasource(jndiName);
            
            jtaCheckBox.setSelected(jtaDataSource != null);
            
            String provider = persistenceUnit.getProvider();
            for (int i = 0; i < providerCombo.getItemCount(); i++) {
                Object item = providerCombo.getItemAt(i);
                if (item instanceof Provider){
                    if (((Provider) item).getProviderClass().equals(provider)) {
                        providerCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }
        } else if (!isContainerManaged){
            initJdbcComboBox();
            setSelectedLibrary();
            jtaCheckBox.setSelected(false);
        }
    }
    
    private void initJdbcComboBox(){
        DatabaseConnection[] connections = ConnectionManager.getDefault().getConnections();
        for (int i = 0; i < connections.length; i++) {
            jdbcComboBox.addItem(connections[i]);
        }
        setSelectedConnection();
    }
    
    private void initIncludeAllEntities(){
        boolean javaSE = Util.isJavaSE(project);
        includeAllEntities.setEnabled(!javaSE);
        includeAllEntities.setSelected(!javaSE && !persistenceUnit.isExcludeUnlistedClasses());
        includeAllEntities.setText(NbBundle.getMessage(PersistenceUnitPanel.class,
                "LBL_IncludeAllEntities",//NOI18N
                new Object[]{ProjectUtils.getInformation(project).getDisplayName()}));
    }
    
    private void initEntityList(){
        initEntityListControls();
        DefaultListModel listedClassesModel = new DefaultListModel();
        for (String elem : persistenceUnit.getClass2()) {
            listedClassesModel.addElement(elem);
        }
        entityList.setModel(listedClassesModel);
    }

    private void initEntityListControls(){
        boolean enable = !includeAllEntities.isSelected();
        entityList.setEnabled(enable);
        addClassButton.setEnabled(enable);
        removeClassButton.setEnabled(enable);    
    }
    /**
     * Sets selected item in library combo box.
     */
    private void setSelectedLibrary(){
        Provider selected = ProviderUtil.getProvider(persistenceUnit);
        if (selected == null){
            return;
        }
        for(int i = 0; i < libraryComboBox.getItemCount(); i++){
            Object item = libraryComboBox.getItemAt(i);
            Provider provider = (Provider) (item instanceof Provider ? item : null);
            if (provider!= null && provider.equals(selected)){
                libraryComboBox.setSelectedIndex(i);
                break;
            }
        }
    }
    
    private Provider getSelectedProvider(){
        if (isContainerManaged){
            return (Provider) providerCombo.getSelectedItem();
        }
        Object selectedItem = libraryComboBox.getSelectedItem();
        Provider provider = (Provider) (selectedItem instanceof Provider ? selectedItem : null);
        if (provider != null) {
            return  provider;
        }
        return ProviderUtil.getProvider(persistenceUnit.getProvider(), project);
    }
    
    /**
     * Selects appropriate table generation radio button.
     */
    private void setTableGeneration(){
        Provider provider = getSelectedProvider();
        // issue 123224. The user can have a persistence.xml in J2SE project without provider specified
        Property tableGeneration = (provider == null) ? null : ProviderUtil.getProperty(persistenceUnit, provider.getTableGenerationPropertyName());
        if (tableGeneration != null){
            if (provider.getTableGenerationCreateValue().equals(tableGeneration.getValue())){
                ddCreate.setSelected(true);
            } else if (provider.getTableGenerationDropCreateValue().equals(tableGeneration.getValue())){
                ddDropCreate.setSelected(true);
            }
        } else {
            ddUnknown.setSelected(true);
        }
        boolean toggle = (provider == null) ? false : provider.supportsTableGeneration();
        
        ddCreate.setEnabled(toggle);
        ddDropCreate.setEnabled(toggle);
        ddUnknown.setEnabled(toggle);
    }
    
    /**
     * Sets the selected item in connection combo box.
     */
    private void setSelectedConnection(){
        DatabaseConnection connection = ProviderUtil.getConnection(persistenceUnit);
        if (connection != null){
            jdbcComboBox.setSelectedItem(connection);
        } else {
            // custom connection (i.e. connection not registered in netbeans)
            Properties props = persistenceUnit.getProperties();
            if (props != null){
                Property[] properties = props.getProperty2();
                String url = null;
                Provider provider = ProviderUtil.getProvider(persistenceUnit);
                for (int i = 0; i < properties.length; i++) {
                    String key = properties[i].getName();
                    if (provider.getJdbcUrl().equals(key)) {
                        url = properties[i].getValue();
                        break;
                    }
                }
                if (url == null) {
                    url = NbBundle.getMessage(PersistenceUnitPanel.class, "LBL_CustomConnection");//NOI18N
                }
                jdbcComboBox.addItem(url);
                jdbcComboBox.setSelectedItem(url);
            }
        }
    }
    
    public void setValue(javax.swing.JComponent source, Object value) {
        if (source == nameTextField) {
            persistenceUnit.setName((String) value);
        } else if (source == dsCombo){
            setDataSource();
        } else if (source == dsCombo.getEditor().getEditorComponent()) {
            setDataSource((String)value);
        } else if (source == jdbcComboBox){
            if (value instanceof DatabaseConnection){
                ProviderUtil.setDatabaseConnection(persistenceUnit, (DatabaseConnection) value);
            }
        } else if (source == libraryComboBox){
            setProvider();
            setTableGeneration();
        } else if (providerCombo == source){
            String prevProvider = persistenceUnit.getProvider();
            setProvider();
            setDataSource();
            String curProvider = persistenceUnit.getProvider();
            if(prevProvider != null && curProvider != null) {
                ProviderUtil.migrateProperties(prevProvider, curProvider, persistenceUnit);
            }
        } else if (source == ddCreate || source == ddDropCreate || source == ddUnknown){
            ProviderUtil.setTableGeneration(persistenceUnit, getTableGeneration(), ProviderUtil.getProvider(persistenceUnit.getProvider(), project));
        } else if (source == includeAllEntities){
            persistenceUnit.setExcludeUnlistedClasses(!includeAllEntities.isSelected());
        } else if (source == jtaCheckBox){
            setDataSource();
        }
        else if(jpa20)
        {
            if(source==ddAll)
            {
                org.netbeans.modules.j2ee.persistence.dd.persistence.model_2_0.PersistenceUnit pu2=(org.netbeans.modules.j2ee.persistence.dd.persistence.model_2_0.PersistenceUnit) persistenceUnit;
                pu2.setCaching(cachingTypes[0]);
            }
            else if(source==ddNone)
            {
                org.netbeans.modules.j2ee.persistence.dd.persistence.model_2_0.PersistenceUnit pu2=(org.netbeans.modules.j2ee.persistence.dd.persistence.model_2_0.PersistenceUnit) persistenceUnit;
                pu2.setCaching(cachingTypes[1]);
            }
            else if(source==ddEnableSelective)
            {
                org.netbeans.modules.j2ee.persistence.dd.persistence.model_2_0.PersistenceUnit pu2=(org.netbeans.modules.j2ee.persistence.dd.persistence.model_2_0.PersistenceUnit) persistenceUnit;
                pu2.setCaching(cachingTypes[2]);
            }
            else if(source==ddDisableSelective)
            {
                org.netbeans.modules.j2ee.persistence.dd.persistence.model_2_0.PersistenceUnit pu2=(org.netbeans.modules.j2ee.persistence.dd.persistence.model_2_0.PersistenceUnit) persistenceUnit;
                pu2.setCaching(cachingTypes[3]);
            }
            else if(source==ddDefault)
            {
                org.netbeans.modules.j2ee.persistence.dd.persistence.model_2_0.PersistenceUnit pu2=(org.netbeans.modules.j2ee.persistence.dd.persistence.model_2_0.PersistenceUnit) persistenceUnit;
                pu2.setCaching(null);
            }
            else if(source==ddAuto)
            {
                org.netbeans.modules.j2ee.persistence.dd.persistence.model_2_0.PersistenceUnit pu2=(org.netbeans.modules.j2ee.persistence.dd.persistence.model_2_0.PersistenceUnit) persistenceUnit;
                pu2.setValidationMode(null);//can be either cleared or set to AUTO
            }
            else if(source==ddCallBack)
            {
                org.netbeans.modules.j2ee.persistence.dd.persistence.model_2_0.PersistenceUnit pu2=(org.netbeans.modules.j2ee.persistence.dd.persistence.model_2_0.PersistenceUnit) persistenceUnit;
                pu2.setValidationMode(validationModes[1]);
            }
            else if(source==ddNoValidation)
            {
                org.netbeans.modules.j2ee.persistence.dd.persistence.model_2_0.PersistenceUnit pu2=(org.netbeans.modules.j2ee.persistence.dd.persistence.model_2_0.PersistenceUnit) persistenceUnit;
                pu2.setValidationMode(validationModes[2]);
            }
        }
        performValidation();
    }
    
    private void performValidation(){
        PersistenceValidator validator = new PersistenceValidator(dObj);
        List<Error> result = validator.validate();
        if (!result.isEmpty()){
            getSectionView().getErrorPanel().setError(result.get(0));
        } else {
            getSectionView().getErrorPanel().clearError();
        }
        
    }
    
    private void setDataSource() {
        setDataSource(null);
    }
    
    private void setProvider(){
        
        if (isContainerManaged && providerCombo.getSelectedItem() instanceof Provider) {
            Provider provider = (Provider) providerCombo.getSelectedItem();
            ProviderUtil.removeProviderProperties(persistenceUnit);
            
            if (!(provider instanceof DefaultProvider)) {
                persistenceUnit.setProvider(provider.getProviderClass());
                setTableGeneration();
            }
            ProviderUtil.makePortableIfPossible(project, persistenceUnit);
            
        } else if (libraryComboBox.getSelectedItem() instanceof Provider){
            Provider provider = (Provider) libraryComboBox.getSelectedItem();
            ProviderUtil.removeProviderProperties(persistenceUnit);
            if (!(provider instanceof DefaultProvider)) {
                ProviderUtil.setProvider(persistenceUnit, provider, getSelectedConnection(), getTableGeneration());
            }
        }
    }
    
    private void setDataSource(String name) {
        
        String jndiName = name;
        
        if (jndiName == null) {
            int itemIndex = dsCombo.getSelectedIndex();
            Object item = dsCombo.getSelectedItem();
            JPADataSourceProvider dsProvider = project.getLookup().lookup(JPADataSourceProvider.class);
            JPADataSource jpaDS = dsProvider != null ? dsProvider.toJPADataSource(item) : null;
            if (jpaDS != null){
                jndiName = jpaDS.getJndiName();
            } else if (itemIndex == -1 && item != null){ // user input
                jndiName = item.toString();
            }
        }
        
        if (jndiName == null){
            return;
        }
        
        if (isJta()){
            persistenceUnit.setJtaDataSource(jndiName);
            persistenceUnit.setNonJtaDataSource(null);
            persistenceUnit.setTransactionType(PersistenceUnit.JTA_TRANSACTIONTYPE);
        } else {
            persistenceUnit.setJtaDataSource(null);
            persistenceUnit.setNonJtaDataSource(jndiName);
            persistenceUnit.setTransactionType(PersistenceUnit.RESOURCE_LOCAL_TRANSACTIONTYPE);
        }
    }
    
    private boolean isJta(){
        return jtaCheckBox.isEnabled() && jtaCheckBox.isSelected();
    }
    
    /**
     *@return selected table generation strategy.
     */
    private String getTableGeneration(){
        if (ddCreate.isSelected()){
            return Provider.TABLE_GENERATION_CREATE;
        } else if (ddDropCreate.isSelected()){
            return Provider.TABLE_GENERATION_DROPCREATE;
        } else {
            return Provider.TABLE_GENERATTION_UNKOWN;
        }
    }
    
    private DatabaseConnection getSelectedConnection(){
        DatabaseConnection connection = null;
        if (jdbcComboBox.getSelectedItem() instanceof DatabaseConnection){
            connection = (DatabaseConnection) jdbcComboBox.getSelectedItem();
        }
        return connection;
        
    }
    
    @Override
    public void rollbackValue(javax.swing.text.JTextComponent source) {
        if (nameTextField == source) {
            nameTextField.setText(persistenceUnit.getName());
        } else if (dsCombo.getEditor().getEditorComponent() == source){
            String jndiName = (isJta() ? persistenceUnit.getJtaDataSource() : persistenceUnit.getNonJtaDataSource());
            selectDatasource(jndiName);
        }
    }
    
    private void selectDatasource(String jndiName) {
        
        Object item = findDatasource(jndiName);
        dsCombo.setSelectedItem(item);
        if (dsCombo.getEditor() != null) { // item must be set in the editor
            dsCombo.configureEditor(dsCombo.getEditor(), item);
        }
    }
    
    private Object findDatasource(String jndiName) {
        
        if (jndiName != null) {
            int nItems = dsCombo.getItemCount();
            for (int i = 0; i < nItems; i++) {
                Object item = dsCombo.getItemAt(i);
                if (item instanceof JPADataSource && jndiName.equals(((JPADataSource)item).getJndiName())) {
                    return (JPADataSource)item;
                }
            }
        }
        
        return jndiName;
    }
    
    @Override
    protected void endUIChange() {
        dObj.modelUpdatedFromUI();
    }
    
    public void linkButtonPressed(Object ddBean, String ddProperty) {
    }
    
    public javax.swing.JComponent getErrorComponent(String errorId) {
        if ("name".equals(errorId)) {//NOI18N
            return nameTextField;
        }
        return null;
    }
    
    private void handleCmAmSelection() {
        boolean isCm = isContainerManaged;
        datasourceLabel.setEnabled(isCm);
        dsCombo.setEnabled(isCm);
        jtaCheckBox.setEnabled(isCm);
        libraryLabel.setEnabled(!isCm);
        libraryComboBox.setEnabled(!isCm);
        jdbcLabel.setEnabled(!isCm);
        jdbcComboBox.setEnabled(!isCm);
        setTableGeneration();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        nameLabel = new javax.swing.JLabel();
        libraryLabel = new javax.swing.JLabel();
        jdbcLabel = new javax.swing.JLabel();
        datasourceLabel = new javax.swing.JLabel();
        jtaCheckBox = new javax.swing.JCheckBox();
        dsCombo = new javax.swing.JComboBox();
        providerCombo = new javax.swing.JComboBox();
        providerLabel = new javax.swing.JLabel();
        libraryComboBox = new javax.swing.JComboBox();
        jdbcComboBox = new javax.swing.JComboBox();
        nameTextField = new javax.swing.JTextField();
        tableGenerationLabel = new javax.swing.JLabel();
        tableGenerationPanel = new javax.swing.JPanel();
        ddCreate = new javax.swing.JRadioButton();
        ddDropCreate = new javax.swing.JRadioButton();
        ddUnknown = new javax.swing.JRadioButton();
        includeAllEntities = new javax.swing.JCheckBox();
        entityClassesPanel = new javax.swing.JPanel();
        entityScrollPane = new javax.swing.JScrollPane();
        entityList = new javax.swing.JList();
        addClassButton = new javax.swing.JButton();
        removeClassButton = new javax.swing.JButton();
        includeEntitiesLabel = new javax.swing.JLabel();
        cachingStrategyLabel = new javax.swing.JLabel();
        cachingStrategyPanel = new javax.swing.JPanel();
        ddAll = new javax.swing.JRadioButton();
        ddNone = new javax.swing.JRadioButton();
        ddEnableSelective = new javax.swing.JRadioButton();
        ddDisableSelective = new javax.swing.JRadioButton();
        ddDefault = new javax.swing.JRadioButton();
        validationStrategyLabel = new javax.swing.JLabel();
        validationStrategyPanel = new javax.swing.JPanel();
        ddAuto = new javax.swing.JRadioButton();
        ddCallBack = new javax.swing.JRadioButton();
        ddNoValidation = new javax.swing.JRadioButton();

        setPreferredSize(new java.awt.Dimension(654, 471));
        setLayout(new java.awt.GridBagLayout());

        nameLabel.setText(org.openide.util.NbBundle.getMessage(PersistenceUnitPanel.class, "LBL_UnitName")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 11, 0, 11);
        add(nameLabel, gridBagConstraints);

        libraryLabel.setText(org.openide.util.NbBundle.getMessage(PersistenceUnitPanel.class, "LBL_PersistenceLibrary")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 11, 0, 11);
        add(libraryLabel, gridBagConstraints);

        jdbcLabel.setText(org.openide.util.NbBundle.getMessage(PersistenceUnitPanel.class, "LBL_JdbcConnection")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 11, 0, 11);
        add(jdbcLabel, gridBagConstraints);

        datasourceLabel.setText(org.openide.util.NbBundle.getMessage(PersistenceUnitPanel.class, "LBL_DatasourceName")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 11, 0, 11);
        add(datasourceLabel, gridBagConstraints);

        jtaCheckBox.setSelected(true);
        jtaCheckBox.setText(org.openide.util.NbBundle.getMessage(PersistenceUnitPanel.class, "LBL_JTA")); // NOI18N
        jtaCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 11, 0, 11);
        add(jtaCheckBox, gridBagConstraints);

        dsCombo.setEditable(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(11, 11, 0, 11);
        add(dsCombo, gridBagConstraints);

        providerCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(11, 11, 0, 11);
        add(providerCombo, gridBagConstraints);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/persistence/wizard/unit/Bundle"); // NOI18N
        providerLabel.setText(bundle.getString("LBL_Provider")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 11, 0, 11);
        add(providerLabel, gridBagConstraints);

        libraryComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                libraryComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(11, 11, 0, 11);
        add(libraryComboBox, gridBagConstraints);

        jdbcComboBox.setRenderer(new JdbcListCellRenderer());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(11, 11, 0, 11);
        add(jdbcComboBox, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 11, 0, 11);
        add(nameTextField, gridBagConstraints);

        java.util.ResourceBundle bundle1 = java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/persistence/unit/Bundle"); // NOI18N
        tableGenerationLabel.setText(bundle1.getString("LBL_TableGeneration")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 11, 0, 11);
        add(tableGenerationLabel, gridBagConstraints);

        tableGenerationPanel.setOpaque(false);
        tableGenerationPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        buttonGroup2.add(ddCreate);
        ddCreate.setText(bundle1.getString("LBL_Create")); // NOI18N
        ddCreate.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        tableGenerationPanel.add(ddCreate);

        buttonGroup2.add(ddDropCreate);
        ddDropCreate.setText(bundle1.getString("LBL_DropCreate")); // NOI18N
        ddDropCreate.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        tableGenerationPanel.add(ddDropCreate);

        buttonGroup2.add(ddUnknown);
        ddUnknown.setText(bundle1.getString("LBL_None")); // NOI18N
        ddUnknown.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        tableGenerationPanel.add(ddUnknown);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(11, 11, 0, 11);
        add(tableGenerationPanel, gridBagConstraints);

        includeAllEntities.setText(bundle1.getString("LBL_IncludeAllEntities")); // NOI18N
        includeAllEntities.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        includeAllEntities.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                includeAllEntitiesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 11, 0, 11);
        add(includeAllEntities, gridBagConstraints);

        entityClassesPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        entityClassesPanel.setOpaque(false);
        entityClassesPanel.setLayout(new java.awt.GridBagLayout());

        entityScrollPane.setMinimumSize(new java.awt.Dimension(50, 25));

        entityScrollPane.setViewportView(entityList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 11);
        entityClassesPanel.add(entityScrollPane, gridBagConstraints);

        addClassButton.setText(bundle1.getString("LBL_AddClasses")); // NOI18N
        addClassButton.setMargin(new java.awt.Insets(0, 10, 0, 10));
        addClassButton.setMaximumSize(new java.awt.Dimension(287, 29));
        addClassButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addClassButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        entityClassesPanel.add(addClassButton, gridBagConstraints);

        removeClassButton.setText(bundle1.getString("LBL_RemoveClass")); // NOI18N
        removeClassButton.setMargin(new java.awt.Insets(0, 10, 0, 10));
        removeClassButton.setMaximumSize(new java.awt.Dimension(284, 29));
        removeClassButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeClassButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        entityClassesPanel.add(removeClassButton, gridBagConstraints);

        includeEntitiesLabel.setText(bundle1.getString("LBL_IncludeEntityClasses")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        entityClassesPanel.add(includeEntitiesLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 33, 11, 11);
        add(entityClassesPanel, gridBagConstraints);

        cachingStrategyLabel.setText(bundle1.getString("LBL_CachingStrategy")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 11, 0, 11);
        add(cachingStrategyLabel, gridBagConstraints);

        cachingStrategyPanel.setOpaque(false);
        cachingStrategyPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        buttonGroup1.add(ddAll);
        ddAll.setText(bundle1.getString("LBL_All")); // NOI18N
        ddAll.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cachingStrategyPanel.add(ddAll);

        buttonGroup1.add(ddNone);
        ddNone.setText(bundle1.getString("LBL_None")); // NOI18N
        ddNone.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cachingStrategyPanel.add(ddNone);

        buttonGroup1.add(ddEnableSelective);
        ddEnableSelective.setText(bundle1.getString("LBL_EnableSelective")); // NOI18N
        ddEnableSelective.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cachingStrategyPanel.add(ddEnableSelective);

        buttonGroup1.add(ddDisableSelective);
        ddDisableSelective.setText(org.openide.util.NbBundle.getMessage(PersistenceUnitPanel.class, "LBL_DisableSelective")); // NOI18N
        ddDisableSelective.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cachingStrategyPanel.add(ddDisableSelective);

        buttonGroup1.add(ddDefault);
        ddDefault.setText(org.openide.util.NbBundle.getMessage(PersistenceUnitPanel.class, "LBL_Default")); // NOI18N
        ddDefault.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cachingStrategyPanel.add(ddDefault);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(11, 11, 0, 11);
        add(cachingStrategyPanel, gridBagConstraints);

        validationStrategyLabel.setText(bundle1.getString("LBL_ValidationStrategy")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 11, 0, 11);
        add(validationStrategyLabel, gridBagConstraints);

        validationStrategyPanel.setOpaque(false);
        validationStrategyPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        buttonGroup3.add(ddAuto);
        ddAuto.setSelected(true);
        ddAuto.setText(bundle1.getString("LBL_Auto")); // NOI18N
        ddAuto.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        validationStrategyPanel.add(ddAuto);

        buttonGroup3.add(ddCallBack);
        ddCallBack.setText(bundle1.getString("LBL_CallBack")); // NOI18N
        ddCallBack.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        validationStrategyPanel.add(ddCallBack);

        buttonGroup3.add(ddNoValidation);
        ddNoValidation.setText(bundle1.getString("LBL_None")); // NOI18N
        ddNoValidation.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        validationStrategyPanel.add(ddNoValidation);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(11, 11, 0, 11);
        add(validationStrategyPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    
    private void removeClassButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeClassButtonActionPerformed
        Object[] values = entityList.getSelectedValues();
        for (Object value : values) {
            dObj.removeClass(persistenceUnit, (String)value);
            ((DefaultListModel)entityList.getModel()).removeElement(value);
        }
    }//GEN-LAST:event_removeClassButtonActionPerformed
    
    private void addClassButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addClassButtonActionPerformed
        EntityClassScope entityClassScope = EntityClassScope.getEntityClassScope(dObj.getPrimaryFile());
        if (entityClassScope == null) {
            return;
        }
        String[] existingClassNames = persistenceUnit.getClass2();
        Set<String> ignoreClassNames = new HashSet<String>(Arrays.asList(existingClassNames));
        List<String> addedClassNames = AddEntityDialog.open(entityClassScope, ignoreClassNames);
        for (String entityClass : addedClassNames) {
            if (dObj.addClass(persistenceUnit, entityClass)){
                ((DefaultListModel)entityList.getModel()).addElement(entityClass);
            }
        }
    }//GEN-LAST:event_addClassButtonActionPerformed
    
    private void libraryComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_libraryComboBoxActionPerformed
        //        setProvider();
        ////        setSelectedLibrary();
        //        setTableGeneration();
    }//GEN-LAST:event_libraryComboBoxActionPerformed

    private void includeAllEntitiesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_includeAllEntitiesActionPerformed
        initEntityListControls();
    }//GEN-LAST:event_includeAllEntitiesActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addClassButton;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.JLabel cachingStrategyLabel;
    private javax.swing.JPanel cachingStrategyPanel;
    private javax.swing.JLabel datasourceLabel;
    private javax.swing.JRadioButton ddAll;
    private javax.swing.JRadioButton ddAuto;
    private javax.swing.JRadioButton ddCallBack;
    private javax.swing.JRadioButton ddCreate;
    private javax.swing.JRadioButton ddDefault;
    private javax.swing.JRadioButton ddDisableSelective;
    private javax.swing.JRadioButton ddDropCreate;
    private javax.swing.JRadioButton ddEnableSelective;
    private javax.swing.JRadioButton ddNoValidation;
    private javax.swing.JRadioButton ddNone;
    private javax.swing.JRadioButton ddUnknown;
    private javax.swing.JComboBox dsCombo;
    private javax.swing.JPanel entityClassesPanel;
    private javax.swing.JList entityList;
    private javax.swing.JScrollPane entityScrollPane;
    private javax.swing.JCheckBox includeAllEntities;
    private javax.swing.JLabel includeEntitiesLabel;
    private javax.swing.JComboBox jdbcComboBox;
    private javax.swing.JLabel jdbcLabel;
    private javax.swing.JCheckBox jtaCheckBox;
    private javax.swing.JComboBox libraryComboBox;
    private javax.swing.JLabel libraryLabel;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JComboBox providerCombo;
    private javax.swing.JLabel providerLabel;
    private javax.swing.JButton removeClassButton;
    private javax.swing.JLabel tableGenerationLabel;
    private javax.swing.JPanel tableGenerationPanel;
    private javax.swing.JLabel validationStrategyLabel;
    private javax.swing.JPanel validationStrategyPanel;
    // End of variables declaration//GEN-END:variables
    
}

