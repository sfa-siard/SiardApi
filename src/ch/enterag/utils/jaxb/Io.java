/*== Io.java ===========================================================
XML I/O for JAXB objects. 
Version     : $Id: Io.java 614 2016-03-01 15:31:37Z hartwig $
Application : JAXB Utilities
Description : Generic static methods for validated reading and for writing
              of JAXB objects.
------------------------------------------------------------------------
Copyright  : 2012, Enter AG, Zurich, Switzerland
Created    : 29.05.2012, Hartwig Thomas
======================================================================*/
package ch.enterag.utils.jaxb;

import java.io.*;
import java.net.*;
import javax.xml.bind.*;
import javax.xml.namespace.*;
import javax.xml.transform.stream.*;

import ch.enterag.utils.*;
import ch.enterag.utils.reflect.*;

/*====================================================================*/
/** Abstract class cannot be instantiated but publishes static methods. 
 * @author Hartwig
 */
public abstract class Io
{
  static
  {
    /* For JAXB 2.3.0 this suppresses the problem with reflective access.
     * See: https://github.com/javaee/jaxb-v2/issues/1197 */
    System.setProperty("com.sun.xml.bind.v2.bytecode.ClassTailor.noOptimize","true");
  }
  
  /*--------------------------------------------------------------------*/
  /** read and unmarshal a JAXB object from an XML file.
   * @param classType JAXB class generated from XSD.
   * @param isXml input stream with XML conforming to JAXB class.
   * @return JAXB object.
   * @throws JAXBException if unmarshalling failed.
   */
  public static <T> T readJaxbObject(Class<T> classType, InputStream isXml)
    throws JAXBException
  {
    /* restore the error count to 10 again, because we do not want to miss any errors! */
    Glue.setPrivate(com.sun.xml.bind.v2.runtime.unmarshaller.UnmarshallingContext.class,"errorsCounter",Integer.valueOf(10));
    T jo = null;
    StreamSource ss = new StreamSource(isXml);
    JAXBContext ctx = JAXBContext.newInstance(classType);
    Unmarshaller u = ctx.createUnmarshaller();
    jo = (T)u.unmarshal(ss,classType).getValue();
    return jo;
  } /* readJaxbObject */
  
  /*--------------------------------------------------------------------*/
  /** read and unmarshal a JAXB object from an XML file, validating it
   * with an XSD.
   * @param classType JAXB class generated from XSD.
   * @param isXml input stream with XML conforming to XSD.
   * @param urlXsd URL where XSD can be found.
   * @return JAXB object.
   * @throws JAXBException if unmarshalling failed.
   */
  public static <T> T readJaxbObject(Class<T> classType, InputStream isXml, URL urlXsd)
    throws JAXBException
  {
    /* restore the error count to 10 again, because we do not want to miss any errors! */
    Glue.setPrivate(com.sun.xml.bind.v2.runtime.unmarshaller.UnmarshallingContext.class,"errorsCounter",Integer.valueOf(10));
    T jo = null;
    JAXBContext ctx = ValidatingJAXBContext.newInstance(urlXsd,classType); 
    Unmarshaller u = ctx.createUnmarshaller();
    StreamSource ss = new StreamSource(isXml);
    jo = (T)u.unmarshal(ss,classType).getValue();
    return jo;
  } /* readJaxbObject */
  
  /*--------------------------------------------------------------------*/
  /** read and unmarshal a JAXB object from an XML file
   * @param classType JAXB class generated from XSD.
   * @param fileXml file with XML conforming to XSD.
   * @return JAXB object.
   * @throws FileNotFoundException if fileXml could not be found.
   * @throws JAXBException if unmarshalling failed.
   * @throws IOException if an I/O error occurred.
   */
  public static <T> T readJaxbObject(Class<T> classType, File fileXml)
    throws FileNotFoundException, JAXBException, IOException
  {
    T jo = null;
    FileInputStream fis = new FileInputStream(fileXml);
    jo = readJaxbObject(classType, fis);
    fis.close();
    return jo;
  } /* readJaxbObject */
  
  /*--------------------------------------------------------------------*/
  /** read and unmarshal a JAXB object from an XML file, validating it
   * with an XSD.
   * @param classType JAXB class generated from XSD.
   * @param fileXml file with XML conforming to XSD.
   * @param urlXsd URL where XSD can be found.
   * @return JAXB object.
   * @throws FileNotFoundException if fileXml could not be found.
   * @throws JAXBException if unmarshalling failed.
   * @throws IOException if an I/O error occurred.
   */
  public static <T> T readJaxbObject(Class<T> classType, File fileXml , URL urlXsd)
    throws FileNotFoundException, JAXBException, IOException
  {
    T jo = null;
    FileInputStream fis = new FileInputStream(fileXml);
    jo = readJaxbObject(classType, fis, urlXsd);
    fis.close();
    return jo;
  } /* readJaxbObject */
  
  /*--------------------------------------------------------------------*/
  /** marshal and write a JAXB object to an output stream, validating it 
   * with an XSD.
   * @param jo JAXB object.
   * @param os output stream.
   * @param qname QName of the object to be streamed (needed if not root object).
   * @param sNoNamespaceSchemaLocation if not null, xsi:noNamespaceSchemaLocation
   * @param sSchemaLocation if not null, xsi:schemaLocation
   * @param bFormat true, if output should be formated.
   * @param urlXsd URL where XSD can be found.
   * @throws JAXBException if marshalling failed.
   */
  public static <T> void writeJaxbObject(T jo, OutputStream os, QName qname, 
    String sNoNamespaceSchemaLocation, String sSchemaLocation, boolean bFormat, URL urlXsd)
    throws JAXBException
  {
    JAXBContext ctx = JAXBContext.newInstance(jo.getClass());
    if (urlXsd == null)
      ctx = JAXBContext.newInstance(jo.getClass());
    else
      ctx = ValidatingJAXBContext.newInstance(urlXsd,jo.getClass());
    Marshaller m = ctx.createMarshaller();
    if (sNoNamespaceSchemaLocation != null)
      m.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION, sNoNamespaceSchemaLocation);
    if (sSchemaLocation != null)
      m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, sSchemaLocation);
    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.valueOf(bFormat));
    if (qname != null)
    {
      @SuppressWarnings("unchecked")
      JAXBElement<T> jbe = new JAXBElement<T>(qname, (Class<T>)jo.getClass(), jo);
      m.marshal(jbe, os);
    }
    else
      m.marshal(jo, os);
  } /* writeJaxbObject */

  /*--------------------------------------------------------------------*/
  /** marshal and write a JAXB object to an output stream.
   * @param jo JAXB object.
   * @param os output stream.
   * @param qname QName of the object to be streamed (needed if not root object).
   * @param sNoNamespaceSchemaLocation if not null, xsi:noNamespaceSchemaLocation
   * @param bFormat true, if output should be formated. 
   * @throws JAXBException if marshalling failed.
   */
  public static <T> void writeJaxbObject(T jo, OutputStream os, QName qname, String sNoNamespaceSchemaLocation, boolean bFormat)
    throws JAXBException
  {
    writeJaxbObject(jo, os, qname, sNoNamespaceSchemaLocation,null,bFormat,null);
  } /* writeJaxbObject */

  /*--------------------------------------------------------------------*/
  /** marshal and write a JAXB object to an output stream.
   * @param jo JAXB object.
   * @param os output stream.
   * @param qname QName of the object to be streamed (needed if not root object).
   * @param sNoNamespaceSchemaLocation if not null, xsi:noNamespaceSchemaLocation 
   * @throws JAXBException if marshalling failed.
   */
  public static <T> void writeJaxbObject(T jo, OutputStream os, QName qname, String sNoNamespaceSchemaLocation)
    throws JAXBException
  {
    writeJaxbObject(jo, os, qname, sNoNamespaceSchemaLocation, false);
  } /* writeJaxbObject */

  /*--------------------------------------------------------------------*/
  /** marshal and write a JAXB object to an output stream.
   * @param jo JAXB object.
   * @param os output stream.
   * @param sNoNamespaceSchemaLocation if not null, xsi:noNamespaceSchemaLocation 
   * @throws JAXBException if marshalling failed.
   */
  public static <T> void writeJaxbObject(T jo, OutputStream os, String sNoNamespaceSchemaLocation)
    throws JAXBException
  {
    writeJaxbObject(jo, os, null, sNoNamespaceSchemaLocation);
  } /* writeJaxbObject */

  /*--------------------------------------------------------------------*/
  /** marshal and write a JAXB object to an output stream.
   * @param jo JAXB object.
   * @param os output stream.
   * @throws JAXBException if marshalling failed.
   * @throws FileNotFoundException if fileXml could not be located.
   * @throws IOException if an I/O error occurred.
   */
  public static <T> void writeJaxbObject(T jo, OutputStream os)
    throws JAXBException
  {
    writeJaxbObject(jo, os, (String)null);
  } /* writeJaxbObject */

  /*--------------------------------------------------------------------*/
  /** marshal and write a JAXB object to an output stream, validating it 
   * with an XSD.
   * @param jo JAXB object.
   * @param os output stream.
   * @param qname QName of the object to be streamed (needed if not root object).
   * @param sSchemaLocation if not null, xsi:schemaLocation
   * @param urlXsd URL where XSD can be found.
   * @throws JAXBException if marshalling failed.
   */
  public static <T> void writeJaxbObject(T jo, OutputStream os, QName qname, String sSchemaLocation, URL urlXsd)
    throws JAXBException
  {
    writeJaxbObject(jo, os, qname, null, sSchemaLocation, false, urlXsd);
  } /* writeJaxbObject */

  /*--------------------------------------------------------------------*/
  /** marshal and write a JAXB object to an output stream, validating it 
   * with an XSD.
   * @param jo JAXB object.
   * @param os output stream.
   * @param sSchemaLocation if not null, xsi:schemaLocation
   * @param bFormat true, if output should be formated.
   * @param urlXsd URL where XSD can be found.
   * @throws JAXBException if marshalling failed.
   */
  public static <T> void writeJaxbObject(T jo, OutputStream os, String sSchemaLocation, boolean bFormat, URL urlXsd)
    throws JAXBException
  {
    writeJaxbObject(jo, os, null, null, sSchemaLocation, bFormat, urlXsd);
  } /* writeJaxbObject */

  /*--------------------------------------------------------------------*/
  /** marshal and write a JAXB object to an output stream, validating it 
   * with an XSD.
   * @param jo JAXB object.
   * @param os output stream.
   * @param sSchemaLocation if not null, xsi:schemaLocation
   * @param urlXsd URL where XSD can be found.
   * @throws JAXBException if marshalling failed.
   */
  public static <T> void writeJaxbObject(T jo, OutputStream os, String sSchemaLocation, URL urlXsd)
    throws JAXBException
  {
    writeJaxbObject(jo, os, sSchemaLocation, false, urlXsd);
  } /* writeJaxbObject */

  /*--------------------------------------------------------------------*/
  /** marshal and write a JAXB object to an output stream, validating it 
   * with an XSD.
   * @param jo JAXB object.
   * @param os output stream.
   * @param bFormat true, if output should be formated.
   * @param urlXsd URL where XSD can be found.
   * @throws JAXBException if marshalling failed.
   */
  public static <T> void writeJaxbObject(T jo, OutputStream os, boolean bFormat, URL urlXsd)
    throws JAXBException
  {
    writeJaxbObject(jo, os, null, bFormat, urlXsd);
  } /* writeJaxbObject */

  /*--------------------------------------------------------------------*/
  /** marshal and write a JAXB object to an output stream, validating it 
   * with an XSD.
   * @param jo JAXB object.
   * @param os output stream.
   * @param urlXsd URL where XSD can be found.
   * @throws JAXBException if marshalling failed.
   */
  public static <T> void writeJaxbObject(T jo, OutputStream os, URL urlXsd)
    throws JAXBException
  {
    writeJaxbObject(jo, os, false, urlXsd);
  } /* writeJaxbObject */

  /*--------------------------------------------------------------------*/
  /** marshal and write a JAXB object to an XML file.
   * @param jo JAXB object.
   * @param fileXml output file.
   * @param qname QName of the object to be streamed (needed if not root object).
   * @throws JAXBException if marshalling failed.
   * @throws FileNotFoundException if fileXml could not be located.
   * @throws IOException if an I/O error occurred.
   */
  public static <T> void writeJaxbObject(T jo, File fileXml, QName qname)
    throws FileNotFoundException, JAXBException, IOException
  {
    FileOutputStream fos = new FileOutputStream(fileXml);
    writeJaxbObject(jo, fos, qname, null);
    fos.close();
  } /* writeJaxbObject */

  /*--------------------------------------------------------------------*/
  /** marshal and write a JAXB object to an XML file.
   * @param jo JAXB object.
   * @param fileXml output file.
   * @throws JAXBException if marshalling failed.
   * @throws FileNotFoundException if fileXml could not be located.
   * @throws IOException if an I/O error occurred.
   */
  public static <T> void writeJaxbObject(T jo, File fileXml)
    throws FileNotFoundException, JAXBException, IOException
  {
    writeJaxbObject(jo,fileXml,null);
  } /* writeJaxbObject */

  /*--------------------------------------------------------------------*/
  /** marshal and write a JAXB object to a formatted string.
   * @param jo JAXB object.
   * @param qname QName of the object to be streamed (needed if not root object).
   * @param sSchemaLocation if not null, xsi:schemaLocation
   * @param urlXsd XML schema URL to be used for checking validity.
   * @throws JAXBException if marshalling failed.
   * @throws IOException if an I/O error occurred.
   */
  public static <T> String writeJaxbObject(T jo, QName qname, String sSchemaLocation, URL urlXsd)
    throws JAXBException, IOException
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    writeJaxbObject(jo, baos, qname, null, sSchemaLocation, true, urlXsd);
    baos.close();
    return baos.toString(SU.sUTF8_CHARSET_NAME);
  } /* writeJaxbObject */

  /*--------------------------------------------------------------------*/
  /** marshal and write a JAXB object to a formatted string.
   * @param jo JAXB object.
   * @param qname QName of the object to be streamed (needed if not root object).
   * @param sNoNamespaceSchemaLocation if not null, xsi:noNamespaceSchemaLocation
   * @throws JAXBException if marshalling failed.
   * @throws IOException if an I/O error occurred.
   */
  public static <T> String writeJaxbObject(T jo, QName qname, String sNoNamespaceSchemaLocation)
    throws JAXBException, IOException
  {
    return writeJaxbObject(jo, qname, sNoNamespaceSchemaLocation, null);
  } /* writeJaxbObject */

  /*--------------------------------------------------------------------*/
  /** marshal and write a JAXB object to a formatted string.
   * @param jo JAXB object.
   * @param qname QName of the object to be streamed (needed if not root object).
   * @param urlXsd XML schema URL to be used for checking validity.
   * @throws JAXBException if marshalling failed.
   * @throws IOException if an I/O error occurred.
   */
  public static <T> String writeJaxbObject(T jo, QName qname, URL urlXsd)
    throws JAXBException, IOException
  {
    return writeJaxbObject(jo, qname, null, urlXsd);
  } /* writeJaxbObject */

  /*--------------------------------------------------------------------*/
  /** marshal and write a JAXB object to a formatted string.
   * @param jo JAXB object.
   * @param qname QName of the object to be streamed (needed if not root object).
   * @throws JAXBException if marshalling failed.
   * @throws IOException if an I/O error occurred.
   */
  public static <T> String writeJaxbObject(T jo, QName qname)
    throws JAXBException, IOException
  {
    return writeJaxbObject(jo, qname, (URL)null);
  } /* writeJaxbObject */

  /*--------------------------------------------------------------------*/
  /** marshal and write a JAXB object to a string.
   * @param jo JAXB object.
   * @throws JAXBException if marshalling failed.
   * @throws IOException if an I/O error occurred.
   */
  public static <T> String writeJaxbObject(T jo, URL urlXsd)
    throws JAXBException, IOException
  {
    return writeJaxbObject(jo, (QName)null, urlXsd);
  } /* writeJaxbObject */

  /*--------------------------------------------------------------------*/
  /** marshal and write a JAXB object to a string.
   * @param jo JAXB object.
   * @throws JAXBException if marshalling failed.
   * @throws IOException if an I/O error occurred.
   */
  public static <T> String writeJaxbObject(T jo)
    throws JAXBException, IOException
  {
    return writeJaxbObject(jo, (QName)null);
  } /* writeJaxbObject */

} /* Io */
