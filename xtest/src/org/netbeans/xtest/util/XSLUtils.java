/*
 * XSLUtils.java
 *
 * Created on June 4, 2002, 4:18 PM
 */

package org.netbeans.xtest.util;

// 
import java.io.*;

import org.w3c.dom.*;
import javax.xml.parsers.*;
// xsl 
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.dom.*;

/**
 *
 * @author  mb115822
 */
public class XSLUtils {
    
   // only static utils
   private XSLUtils() {
   }
   
   
   
   
   public static File getXSLFile(File xtestHome, String xslFilename) throws IOException {
       if (!xtestHome.isDirectory()) {
           throw new IOException("xtest home is not set");
       }
       File xslFile = new File(xtestHome,"lib"+File.separator+"xsl"+File.separator+xslFilename);
       if (!xslFile.isFile()) {
           throw new IOException("cannot find xsl file "+xslFile);
       }
       return xslFile;
   }
   
   
   public static Transformer getTransformer(File xsl) throws TransformerConfigurationException {
       StreamSource xslSource = new StreamSource(xsl);
       Transformer transformer = XMLFactoryUtil.newTransformer(xslSource);
       return transformer;
   }
   
   public static Document transform(Transformer transformer, Document xml) throws TransformerException, ParserConfigurationException {
       DOMSource domSource = new DOMSource(xml);
       DOMResult domResult = new DOMResult();
       transform(transformer,domSource,domResult);
       Node aNode = domResult.getNode();       
       Document resultDoc = XMLFactoryUtil.newDocumentBuilder().newDocument();
       resultDoc.appendChild(aNode);
       return resultDoc;
   }
   
   public static void transform(Transformer transformer, Document xml, File outputXML) throws TransformerException, IOException {
       DOMSource domSource = new DOMSource(xml);
       FileOutputStream outputFileStream = new FileOutputStream(outputXML);
       StreamResult streamResult = new StreamResult(outputFileStream);
       transform(transformer,domSource,streamResult);
       outputFileStream.close();
    }
    
    
    public static void transform(Transformer transformer, Source xmlSource, Result outputTarget) throws TransformerException {
        transformer.transform(xmlSource, outputTarget);
    }
    
    
    public static void transform(Transformer transformer, File inputXML, File outputXML) throws TransformerException, IOException {
        StreamSource xmlSource = new StreamSource(inputXML);
        FileOutputStream outputFileStream = new FileOutputStream(outputXML);
        StreamResult xmlResult = new StreamResult(outputFileStream);
        transform(transformer,xmlSource,xmlResult);
        outputFileStream.close();
    }
    
    public static void transform(File xsl, File inputXML, File outputXML) throws TransformerException {
        Transformer transformer = getTransformer(xsl);
        transform(inputXML,outputXML,xsl);
    }

}
