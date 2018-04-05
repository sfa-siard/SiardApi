package ch.enterag.utils.jaxb;

import java.io.*;
import java.math.*;
import java.net.*;
import java.util.*;
import javax.xml.bind.*;
import javax.xml.datatype.*;
import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.utils.*;
import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.api.generated.*;
import ch.admin.bar.siard2.api.primary.*;

public class IoTester
{
  protected static void printErrorMessage(Error e)
  {
    System.err.println("  "+EU.getErrorMessage(e));
    System.err.flush();
  } /* printErrorMessage */

  protected static void printExceptionMessage(Exception e)
  {
    System.err.println("  "+EU.getExceptionMessage(e));
    System.err.flush();
  } /* printExceptionMessage */

  @Test
  public void testRead2008()
  {
    try
    {
      URL urlXsd = Archive.class.getResource(Archive.sSIARD2_META_DATA_XSD_RESOURCE);
      InputStream isXml = new FileInputStream("testfiles/metadata-21.xml");
      SiardArchive sa = Io.readJaxbObject(SiardArchive.class, isXml, urlXsd);
      isXml.close();
      System.out.println(String.valueOf(sa.getDatabaseUser()));
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(JAXBException je) { fail(EU.getExceptionMessage(je)); }
  }
  @Test
  public void testRead2003()
  {
    try
    {
      URL urlXsd = Archive.class.getResource(MetaDataXml.sSIARD10_XSD_RESOURCE);
      InputStream isXml = new FileInputStream("Documentation/SIARD Format/old10/metadata2003.xml");
      ch.admin.bar.siard2.api.generated.old10.SiardArchive sa = Io.readJaxbObject(ch.admin.bar.siard2.api.generated.old10.SiardArchive.class, isXml, urlXsd);
      isXml.close();
      System.out.println(String.valueOf(sa.getDatabaseUser()));
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(JAXBException je) { fail(EU.getExceptionMessage(je)); }
  }
  
  @Test
  public void testRead2011()
  {
    try
    {
      URL urlXsd = Archive.class.getResource(MetaDataXml.sSIARD10_XSD_RESOURCE);
      InputStream isXml = new FileInputStream("Documentation/SIARD Format/old10/metadata2011.xml");
      ch.admin.bar.siard2.api.generated.old10.SiardArchive sa = Io.readJaxbObject(ch.admin.bar.siard2.api.generated.old10.SiardArchive.class, isXml, urlXsd);
      isXml.close();
      System.out.println(String.valueOf(sa.getDatabaseUser()));
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(JAXBException je) { fail(EU.getExceptionMessage(je)); }
  }
  
  @Test
  public void testWriteMinimum()
  {
    ObjectFactory of = new ObjectFactory();
    SiardArchive sa = of.createSiardArchive();
    sa.setVersion("2.0");
    sa.setDbname("TestDb");
    sa.setDataOwner("TestOwner");
    sa.setDataOriginTimespan("2016");
    try
    {
      GregorianCalendar gc = new GregorianCalendar(); // now
      XMLGregorianCalendar xgc = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
      sa.setArchivalDate(xgc);
    }
    catch (DatatypeConfigurationException dce){ fail(EU.getExceptionMessage(dce)); }
    SchemasType sts = of.createSchemasType();
    SchemaType st = of.createSchemaType();
    st.setName("FirstSchema");
    st.setFolder("schema0");
    sts.getSchema().add(st);
    TablesType tts = of.createTablesType();
    TableType tt = of.createTableType();
    tt.setName("FirstTable");
    tt.setFolder("table0");
    ColumnsType cts = of.createColumnsType();
    ColumnType ct = of.createColumnType();
    ct.setName("FirstColumn");
    ct.setType("CHAR(5)");
    cts.getColumn().add(ct);
    tt.setColumns(cts);
    tt.setRows(BigInteger.valueOf(0l));
    tts.getTable().add(tt);
    st.setTables(tts);
    sa.setSchemas(sts);
    UsersType uts = of.createUsersType();
    UserType ut = of.createUserType();
    ut.setName("TestUser");
    uts.getUser().add(ut);
    sa.setUsers(uts);
    try
    {
      URL urlXsd = Archive.class.getResource("/ch/admin/bar/siard2/api/res/metadata.xsd");
      OutputStream osXml = new FileOutputStream("logs/minimum.xml");
      Io.writeJaxbObject(sa, osXml, urlXsd);
      osXml.close();
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(JAXBException je) { fail(EU.getExceptionMessage(je)); }
  }

  @Test
  public void testWriteEmpty()
  {
    ObjectFactory of = new ObjectFactory();
    SiardArchive sa = of.createSiardArchive();
    sa.setVersion("2.0");
    sa.setDbname("TestDb");
    sa.setDataOwner("TestOwner");
    sa.setDataOriginTimespan("2016");
    try
    {
      GregorianCalendar gc = new GregorianCalendar(); // now
      XMLGregorianCalendar xgc = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
      sa.setArchivalDate(xgc);
    }
    catch (DatatypeConfigurationException dce){ fail(EU.getExceptionMessage(dce)); }
    /**
    SchemasType sts = of.createSchemasType();
    SchemaType st = of.createSchemaType();
    st.setName("FirstSchema");
    st.setFolder("schema0");
    sts.getSchema().add(st);
    TablesType tts = of.createTablesType();
    TableType tt = of.createTableType();
    tt.setName("FirstTable");
    tt.setFolder("table0");
    ColumnsType cts = of.createColumnsType();
    ColumnType ct = of.createColumnType();
    ct.setName("FirstColumn");
    ct.setType("CHAR(5)");
    cts.getColumn().add(ct);
    tt.setColumns(cts);
    tt.setRows(BigInteger.valueOf(1l));
    tts.getTable().add(tt);
    st.setTables(tts);
    sa.setSchemas(sts);
    UsersType uts = of.createUsersType();
    UserType ut = of.createUserType();
    ut.setName("TestUser");
    uts.getUser().add(ut);
    sa.setUsers(uts);
    **/
    try
    {
      URL urlXsd = Archive.class.getResource("/ch/admin/bar/siard2/api/res/empty.xsd");
      OutputStream osXml = new FileOutputStream("logs/empty.xml");
      Io.writeJaxbObject(sa, osXml, urlXsd);
      osXml.close();
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(JAXBException je) { fail(EU.getExceptionMessage(je)); }
  }
}
