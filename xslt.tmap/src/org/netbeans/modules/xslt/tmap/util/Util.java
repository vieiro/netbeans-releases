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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.xslt.tmap.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.xml.namespace.QName;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.xml.api.EncodingUtil;
import org.netbeans.modules.xml.catalogsupport.ProjectConstants;
import org.netbeans.modules.xml.retriever.catalog.Utilities;
import org.netbeans.modules.xml.wsdl.model.Definitions;
import org.netbeans.modules.xml.wsdl.model.Operation;
import org.netbeans.modules.xml.wsdl.model.OperationParameter;
import org.netbeans.modules.xml.wsdl.model.PortType;
import org.netbeans.modules.xml.wsdl.model.ReferenceableWSDLComponent;
import org.netbeans.modules.xml.wsdl.model.WSDLModel;
import org.netbeans.modules.xml.wsdl.model.WSDLModelFactory;
import org.netbeans.modules.xml.wsdl.model.extensions.bpel.PartnerLinkType;
import org.netbeans.modules.xml.wsdl.model.extensions.bpel.Role;
import org.netbeans.modules.xml.wsdl.model.Message;
import org.netbeans.modules.xml.xam.ModelSource;
import org.netbeans.modules.xml.xam.Reference;
import org.netbeans.modules.xml.xam.dom.NamedComponentReference;
import org.netbeans.modules.xslt.tmap.model.api.TMapComponent;
import org.netbeans.modules.xslt.tmap.model.api.TMapModel;
import org.netbeans.modules.xslt.tmap.model.api.WSDLReference;
import org.netbeans.modules.xslt.tmap.model.spi.TMapModelFactory;
import org.netbeans.modules.xslt.tmap.model.xsltmap.TransformationDesc;
import org.netbeans.modules.xslt.tmap.model.xsltmap.XmlUtil;
import org.netbeans.modules.xslt.tmap.model.xsltmap.XsltMapConst;
import org.netbeans.modules.xslt.tmap.nodes.TMapComponentNode;
import org.openide.ErrorManager;
import org.openide.cookies.EditCookie;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.util.NbBundle;

/**
 *
 * @author Vitaly Bychkov
 * @version 1.0
 */
public class Util {
    public static final String FORWARD_SLASH = "/"; // NOI18N
    public static final String UP_REL_FOLDER = "../"; // NOI18N
    public static final String CUR_REL_FOLDER = "./"; // NOI18N
    public static final String WSDL = "wsdl"; // NOI18N
    // TODO m
    public static final String SRC = "src"; // NOI18N

    private Util() {
    }
    
    /**
     * TODO m - looks like general utility method for all modules
     */
    public static FileObject getRelativeFO(FileObject startPoint
            , String relLocation) {
        if (startPoint == null || relLocation == null) {
            return null;
        }
        
        if (!startPoint.isFolder()) {
            startPoint = startPoint.getParent();
        }
        
        if (relLocation.startsWith(UP_REL_FOLDER)) {
            int upRelLength = UP_REL_FOLDER.length();
            while (relLocation.startsWith(UP_REL_FOLDER)) {
                startPoint = startPoint.getParent();
                relLocation = relLocation.substring(upRelLength);
            }
            
        } else if (relLocation.startsWith(CUR_REL_FOLDER)) {
            relLocation = relLocation.substring(CUR_REL_FOLDER.length());
        }
        return startPoint.getFileObject(relLocation);
    }
    
    /**
     * TODO m - looks like general utility method for all modules
     */
    public static String getRelativePath(FileObject fromFo, FileObject toFo) {
        String relativePath = FileUtil.getRelativePath(fromFo, toFo);
        if (relativePath != null) {
            return relativePath;
        }
        
        if (!fromFo.isFolder()) {
            fromFo = fromFo.getParent();
        }
        
        StringTokenizer fromPath = new StringTokenizer(fromFo.getPath()
                , FORWARD_SLASH);
        StringTokenizer toPath = new StringTokenizer(toFo.getPath()
                , FORWARD_SLASH);
        String tmpFromFolder = null;
        String tmpToFolder = null;
        while (fromPath.hasMoreTokens()) {
            tmpFromFolder = fromPath.nextToken();
            tmpToFolder = toPath.hasMoreTokens() ? toPath.nextToken() : null;
            if (!(tmpFromFolder.equals(tmpToFolder))) {
                break;
            }
        }
        if (tmpToFolder == null) {
            return null;
        }
        
        StringBuffer fromRelativePathPart = new StringBuffer(UP_REL_FOLDER);
        while (fromPath.hasMoreTokens()) {
            fromPath.nextToken();
            fromRelativePathPart.append(UP_REL_FOLDER);
        }
        
        StringBuffer toRelativePathPart = new StringBuffer(tmpToFolder);
        while (toPath.hasMoreTokens()) {
            toRelativePathPart.append(FORWARD_SLASH).append(toPath.nextToken());
        }
        
        return fromRelativePathPart.append(toRelativePathPart).toString();
    }
    
    public static Project getProject(FileObject projectFo) {
        FileObject projectRoot = null;
        return projectFo == null ? null : FileOwnerQuery.getOwner(projectFo);
    }

    
    public static FileObject getProjectRoot(FileObject projectFo) {
        FileObject projectRoot = null;
        Project project = FileOwnerQuery.getOwner(projectFo);
        if (project != null) {
            projectRoot = project.getProjectDirectory();
        }
        return projectRoot;
    }
    
    public static WSDLModel[] getAllProjectWsdls(FileObject projectRoot) {
        assert projectRoot != null;
        List<FileObject> fileObjects = new ArrayList<FileObject>();
        Enumeration projectFolders = projectRoot.getFolders(true);
        while(projectFolders.hasMoreElements()) {
            FileObject folder = (FileObject) projectFolders.nextElement();
            FileObject[] childrenFo = folder.getChildren();
            for (FileObject elem : childrenFo) {
                if (! elem.isFolder() && WSDL.equals(elem.getExt())) {
                    fileObjects.add(elem);
                }
            }
        }
        
        WSDLModel[] wsdlModels = null;
        if ( fileObjects != null && fileObjects.size() > 0) {
            wsdlModels = new WSDLModel[fileObjects.size()];
        }
        
        WSDLModelFactory factory = WSDLModelFactory.getDefault();
        for (int i = 0; i < wsdlModels.length; i++) {
            ModelSource wsdlModelSource = Utilities.getModelSource(fileObjects.get(i), true);
            wsdlModels[i] = factory.getModel(wsdlModelSource);
        }
        
        return wsdlModels;
    }
    
    public static FileObject[] getProjectSources(Project project) {
        List<FileObject> projectSources = new ArrayList<FileObject>();
        if (project == null) {
            return null; // sometimes project couldn't be founded for nb development project
//            throw new IllegalArgumentException("project shouldn't be null");
        }
        Sources sources = ProjectUtils.getSources(project);
        SourceGroup[] sourceGroups = sources.getSourceGroups(ProjectConstants.SOURCES_TYPE_XML);
        if (sourceGroups != null) {
            for (SourceGroup sourceGroup : sourceGroups) {
                projectSources.add(sourceGroup.getRootFolder());
            }
        }

        return projectSources.toArray(new FileObject[projectSources.size()]);
    }

    public static FileObject getProjectSource(Project project) {
        FileObject projectSource = null;
        if (project == null) {
            return null; // sometimes project couldn't be founded for nb development project
//            throw new IllegalArgumentException("project shouldn't be null");
        }
        Sources sources = ProjectUtils.getSources(project);
        SourceGroup[] sourceGroups = sources.getSourceGroups(ProjectConstants.SOURCES_TYPE_XML);
        if (sourceGroups != null && sourceGroups.length > 0) {
            projectSource = sourceGroups[0].getRootFolder();
        }

        return projectSource;
    }
    
    public static FileObject getTMapFo(Project project) {
        return getProjectSource(project).getFileObject("transformmap.xml");
    }
    
    public static File getTransformationDescriptor(Project project) {
        FileObject fo = getProjectSource(project).getFileObject("transformmap.xml");
        if (fo == null) {
            fo = getProjectSource(project).getFileObject("xsltmap.xml");
        }
        return fo == null ? null : FileUtil.toFile(fo);
    }
    
    public static FileObject getXsltMapFo(Project project) {
        if (project == null) {
            throw new IllegalArgumentException("project shouldn't be null");
        }
        FileObject xsltMapFo = null;
        FileObject projectSource = getProjectSource(project);
        assert projectSource != null;
        
        xsltMapFo = projectSource.getFileObject(XsltMapConst.XSLTMAP+"."+XsltMapConst.XML);
        return xsltMapFo;
    }

    public static FileObject getXsltMapFo(FileObject xsltFo) {
        FileObject xsltMapFo = null;
        if (xsltFo == null) {
            return null;
        }
//        FileObject projectRoot = getProjectRoot(xsltFo);
//        if (projectRoot != null) {
              xsltMapFo = xsltFo.getParent().getFileObject(XsltMapConst.XSLTMAP+"."+XsltMapConst.XML);

//            xsltMapFo = projectRoot.getFileObject(XsltMapObject.XSLTMAP+"."+XsltMapObject.XML);
//        }
        return xsltMapFo;
    }
    
    public static FileObject getXsltMapFo(File projectFile) {
        if (projectFile == null) {
            return null;
        }
        
        FileObject projectFo = FileUtil.toFileObject(projectFile);
        Project project = getProject(projectFo);
        
        return project == null ? null : getXsltMapFo(project);
    }

    public static TMapModel getTMapModel(FileObject tmapFo) {
        TMapModel model = null;
        if (tmapFo != null) {
            ModelSource modelSource = Utilities.getModelSource(tmapFo, true);
            model = TMapModelFactory.TMapModelFactoryAccess.getFactory().getModel(modelSource);
        }
        
        return model;
    }
    
    public static FileObject createDefaultTransformmap(Project project) {
        assert project != null;
        FileObject tMapFo = null;
        FileObject projectSource = Util.getProjectSource(project);
        if (projectSource == null) {
            return null;
        }
        
        try {
            tMapFo = FileUtil.copyFile(Repository.getDefault().getDefaultFileSystem()
                    .findResource("org-netbeans-xsltpro/transformmap.xml"), //NOI18N
                    projectSource, "transformmap"); //NOI18N
            
            
//            tMapFo = XmlUtil.createNewXmlFo(
//                    projectSource.getPath(),
//                    "transformmap",
//                    TMapComponent.TRANSFORM_MAP_NS_URI);
            if (tMapFo != null) {
                fixEncoding(tMapFo, projectSource);
            }
            
        } catch (IOException ex) {
            ErrorManager.getDefault().notify(ex);
            return null;
        }
        return tMapFo;
    }
    
    public static File getXsltMapFile(File projectFile) {
        return FileUtil.toFile(getXsltMapFo(projectFile));
    }
    
    // TODO m

    public static PartnerLinkType findPartnerLinkType(FileObject projectRoot, String partnerLinkTypeName) {
        if (projectRoot == null || partnerLinkTypeName == null ) {

            return null;
        }
        
        PartnerLinkType wsdlPlt = null;
        WSDLModel[] models = getAllProjectWsdls(projectRoot);
        for (WSDLModel elem : models) {
            wsdlPlt = getPartnerLinkType(elem, partnerLinkTypeName);
            
            if (wsdlPlt != null) {
                break;
            }
        }
        
        return wsdlPlt;
    }

    public static Operation findWsdlOperation(FileObject projectRoot, String partnerLinkTypeName, String roleName, String portType, String operation) {
        if (partnerLinkTypeName == null 
                || roleName == null
                || projectRoot == null 
                || portType == null 
                || operation == null) 
        {
            return null;
        }
        
        // TODO m
        
        Operation wsdlOperation = null;
        PartnerLinkType plt = findPartnerLinkType(projectRoot, partnerLinkTypeName);
        
        Role role = null;
        if (plt != null) {
            role = plt.getRole1();
            role = role != null && !roleName.equals(role.getName()) ? null : role;
            if (role == null) {
                role = plt.getRole2();
                role = role != null && !roleName.equals(role.getName()) ? null : role;
            }
        }
        
        PortType portTypeElem = null;
        if (role != null) {
            NamedComponentReference<PortType> portTypeRef = role.getPortType();
            portTypeElem = portTypeRef == null ? null : portTypeRef.get();
            
            QName portTypeQname = portTypeRef == null ? null : portTypeRef.getQName();
            QName xsltMapPortTypeQName = QName.valueOf(portType);
            if (portTypeElem != null 
                    && xsltMapPortTypeQName != null 
                    && !xsltMapPortTypeQName.equals(portTypeQname)) {
                portTypeElem = null;
            }
        }
        

        if (portTypeElem != null) {
            Collection<Operation> operations = portTypeElem.getOperations();
            if (operations != null) {
                for (Operation elem : operations) {
                    if (operation.equals(elem.getName())) {
                        wsdlOperation = elem;
                        break;
                    }
                }
            }
        }
        
        return wsdlOperation;
    }

    // TODO m
    public static Operation findWsdlOperation(FileObject projectRoot, TransformationDesc transformDesc) {
        if (projectRoot == null || transformDesc == null) {
            return null;
        }
        
        return findWsdlOperation(
                projectRoot, 
                transformDesc.getPartnerLink(), 
                transformDesc.getRoleName(), 
                transformDesc.getPortType(), 
                transformDesc.getOperation());
////        
////        Operation wsdlOperation = null;
////        WSDLModel[] models = getAllProjectWsdls(projectRoot);
////        for (WSDLModel elem : models) {
////            wsdlOperation = getOperation(elem, transformDesc.getPortType(), transformDesc.getOperation());
////            
////            if (wsdlOperation != null) {
////                break;
////            }
////        }
////        
////        return wsdlOperation;
    }

    // TODO m
    public static PartnerLinkType getPartnerLinkType(WSDLModel wsdlModel, String partnerLinkTypeName) {
        if (wsdlModel == null || partnerLinkTypeName == null) {
            return null;
        }
        PartnerLinkType wsdlPlt = null;
        
        Definitions defs = wsdlModel.getDefinitions();
        if (defs == null) {
            return wsdlPlt;
        }
        
        List<PartnerLinkType> wsdlPlts = defs.getExtensibilityElements(PartnerLinkType.class);
        for (PartnerLinkType tmpPlt : wsdlPlts) {
            String pltNameLocalPart = tmpPlt.getName();
            String pltNamespace = wsdlModel.getDefinitions().getTargetNamespace();
//            String pltNamespace = pltQname.getNamespaceURI();
            if (partnerLinkTypeName.equals("{"+pltNamespace+"}"+pltNameLocalPart)) {
                wsdlPlt = tmpPlt;
                break;
            }
        }
        return wsdlPlt;
    }
  
    public static Operation findWsdlOperation(File projectRootFile, TransformationDesc transformDesc) {
        return findWsdlOperation(FileUtil.toFileObject(projectRootFile), transformDesc);
    }
    
// TODO m
    public static Operation getOperation(WSDLModel wsdlModel, String portType, String operation) {
        if (wsdlModel == null || portType == null || operation == null) {
            return null;
        }
        Operation resultOp = null;
        
        Definitions defs = wsdlModel.getDefinitions();
        if (defs == null) {
            return resultOp;
        }

        Collection<PortType> portTypes = defs.getPortTypes();
        PortType modelPortType = null;
        for (PortType tmpPortType : portTypes) {
            NamedComponentReference<PortType> tmpPortTypeRef = tmpPortType.
                            createReferenceTo(tmpPortType, PortType.class);

            QName tmpPortTypeQname = tmpPortTypeRef.getQName();
            String tmpPortTypeNs = tmpPortTypeQname.getNamespaceURI();
            String tmpPortTypeLocalPart = tmpPortTypeQname.getLocalPart();
            
            if (portType.equals("{"+tmpPortTypeNs+"}"+tmpPortTypeLocalPart)) {
                modelPortType = tmpPortType;
                break;
            }
        }
        
        if (modelPortType != null) {
            Collection<Operation> operations = modelPortType.getOperations();
            for (Operation tmpOperation : operations) {
                if (operation.equals(tmpOperation.getName())) {
                    resultOp = tmpOperation;
                    break;
                }
            }
        }

        return resultOp;
    }
    
    /**
     * @param if qnamedElement has structure like this: {namespaceURI}localName
     * @return if localName qnamedElement has structure like this: 
     * {namespaceURI}localName then return localName
     *
     */
    private String getLocalPart(String qnamedElement) {
        
        
        QName qnamedElementQName = QName.valueOf(qnamedElement);
        return qnamedElementQName.getLocalPart();
    }
    
    public static String getNamespace(ReferenceableWSDLComponent wsdlComp) {
        if (wsdlComp == null) {
            return null;
        }
        
        String namespace = null;
        
        WSDLModel model = wsdlComp.getModel();
        Definitions defs = null;

        if (model != null) {
            defs = model.getDefinitions();
        }
        
        if (defs != null) {
            namespace = defs.getTargetNamespace();
        }

        return namespace;
    }
    
    
    public static String getMessageType(Operation operation, boolean isInput) {
        if (operation == null) {
            return null;
        }
        
        String messageType = null;
        NamedComponentReference<Message> messageRef = null;

        OperationParameter opParam =  isInput 
                ? operation.getInput() : operation.getOutput();

        if (opParam != null) {
            messageRef = opParam.getMessage();
        }

        Message message = null;
        if (messageRef != null) {
            message = messageRef.get();
        }

        if (message != null) {
            String namespace = Util.getNamespace(message);
            namespace = namespace != null ? "{"+namespace+"}" : ""; // NOI18N
            messageType = namespace + message.getName();
        }
        
        return messageType;
    }

    // TODO m
    public static void fixEncoding(FileObject xslFo, FileObject dirFo) 
            throws IOException 
    {
        DataObject xslDo = DataObject.find(xslFo);
        if (xslDo != null) {
            DataFolder df = DataFolder.findFolder(dirFo);
            String encoding = EncodingUtil.getProjectEncoding(df.getPrimaryFile());

            if ( !EncodingUtil.isValidEncoding(encoding)) {
              encoding = "UTF-8"; // NOI18N
            }
            EditCookie edit = xslDo.getCookie(EditCookie.class);

            if (edit != null) {
              EditorCookie editorCookie = xslDo.getCookie(EditorCookie.class);
              Document doc = (Document)editorCookie.openDocument();
              fixEncoding(doc, encoding);
              SaveCookie save = xslDo.getCookie(SaveCookie.class);

              if (save != null) {
                save.save();
              }
            }
        }
    }
    
    public static void fixEncoding(javax.swing.text.Document document, String encoding) {
      if (encoding == null) {
        encoding = "UTF-8"; //NOI18N
      }
      try {
        document.insertString(19, " encoding=\""+encoding+"\"", null);
      }
      catch (BadLocationException e) {
      }
    }

// html names/tooltip/message presenter rel methods
    public static String getGrayString(String message) {
        return getGrayString("", message);
    }
    
    public static String getGrayString(String nonGrayPrefix, String message) {
        return message == null ? nonGrayPrefix : "<html>"+getCorrectedHtmlRenderedString(nonGrayPrefix) // NOI18N
        +"<font color='"+GRAY_COLOR+"'>"+getCorrectedHtmlRenderedString(message)+"</font></html>";// NOI18N
    }
    
    public static String getGrayString(String nonGrayPrefix, String message
            , String nonGraySuffix) {
        return getGrayString(nonGrayPrefix,message,nonGraySuffix, true);
    }
    
    public static String getGrayString(String nonGrayPrefix, String message
            , String nonGraySuffix, boolean isSetHtmlHeader) {
        String htmlHeader = isSetHtmlHeader ? "<html>" : ""; // NOI18N
        String htmlFooter = isSetHtmlHeader ? "</html>" : ""; // NOI18N
        return message == null ? nonGrayPrefix : htmlHeader
                +getCorrectedHtmlRenderedString(nonGrayPrefix)
                +"<font color='"+GRAY_COLOR+"'>" // NOI18N
                +getCorrectedHtmlRenderedString(message)
                +"</font>" // NOI18N
                +(nonGraySuffix == null ? ""
                : getCorrectedHtmlRenderedString(nonGraySuffix))
                +htmlFooter;// NOI18N
    }

    public static final String getCorrectedHtmlRenderedString(String htmlString) {
        if (htmlString == null) {
            return null;
        }
        htmlString = htmlString.replaceAll("&amp;","&"); // NOI18n
        htmlString = htmlString.replaceAll("&gt;",">;"); // NOI18n
        htmlString = htmlString.replaceAll("&lt;","<"); // NOI18n
        
        htmlString = htmlString.replaceAll("&","&amp;"); // NOI18n
        htmlString = htmlString.replaceAll(">","&gt;"); // NOI18n
        htmlString = htmlString.replaceAll("<","&lt;"); // NOI18n
        return htmlString;
    }
  
  public static FileObject getSrcFolder(Project project) {
    return project.getProjectDirectory().getFileObject("src");
  }

  public static String getReferenceLocalName(WSDLReference wsdlRef) {
      if (wsdlRef == null) {
          return null;
      }
      
      QName refQname = wsdlRef.getQName();
      return refQname == null ? null : refQname.getLocalPart();
  }
  
  public static String getReferenceLocalName(Reference ref) {
      if (ref == null) {
          return null;
      }
      
      return ref == null ? null : ref.getRefString();
  }

        public static String getLocalizedAttribute(Reference attributeRef, String attributeName) {
            if (attributeRef == null) {
                return TMapComponentNode.EMPTY_STRING;
            }
            
            attributeName = attributeName == null ? "" : attributeName;
            return NbBundle.getMessage(
                    TMapComponentNode.class,
                    "LBL_ATTRIBUTE_HTML_TEMPLATE", // NOI18N
                    attributeName,
                    attributeRef.getRefString()
                    );
        }
        
        public static String getLocalizedAttribute(String attributeValue, String attributeName) {
            if (attributeValue == null) {
                return TMapComponentNode.EMPTY_STRING;
            }
            
            attributeName = attributeName == null ? TMapComponentNode.EMPTY_STRING : attributeName;
            attributeValue = attributeValue == null ? TMapComponentNode.EMPTY_STRING : attributeValue;
            return NbBundle.getMessage(
                    TMapComponentNode.class,
                    "LBL_ATTRIBUTE_HTML_TEMPLATE", // NOI18N
                    attributeName,
                    attributeValue
                    );
        }
  
  
  private static String GRAY_COLOR = "#999999";
    

}
