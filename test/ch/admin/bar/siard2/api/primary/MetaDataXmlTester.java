package ch.admin.bar.siard2.api.primary;

import java.io.*;
import javax.xml.bind.*;
import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.utils.*;
import ch.admin.bar.siard2.api.generated.*;

public class MetaDataXmlTester
{
  @Test
  public void testLoadXml()
  {
    try
    {
      FileInputStream fis = new FileInputStream("testfiles/metadata2011-20.xml");
      SiardArchive sa = MetaDataXml.readXml(fis);
      fis.close();
      System.out.println(sa.getDbname());
    }
    catch (FileNotFoundException fnfe) { fail(EU.getExceptionMessage(fnfe)); }
    catch (IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch (Exception e) { fail(EU.getExceptionMessage(e)); }

    try
    {
      FileInputStream fis = new FileInputStream("testfiles/metadata2011-10.xml");
      SiardArchive sa = MetaDataXml.readXml(fis);
      fis.close();
      System.out.println(sa.getDbname());
      fail("Old XML cannot be parsed using new XSD!");
    }
    catch (FileNotFoundException fnfe) { fail(EU.getExceptionMessage(fnfe)); }
    catch (IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch (Exception e) { System.err.println(EU.getExceptionMessage(e)); }
  }
  
  @Test
  public void testLoadXmlOld10()
  {
    try
    {
      FileInputStream fis = new FileInputStream("testfiles/metadata2011-10.xml");
      SiardArchive sa = MetaDataXml.readXmlOld10(fis);
      fis.close();
      if (sa == null)
        fail(" XML version 1.0 cannot be parsed using XSD version 1.0!");
    }
    catch (FileNotFoundException fnfe) { fail(EU.getExceptionMessage(fnfe)); }
    catch (IOException ie) { fail(EU.getExceptionMessage(ie)); }

    try
    {
      FileInputStream fis = new FileInputStream("testfiles/metadata2011-10.xml");
      SiardArchive sa = MetaDataXml.readXmlOld10(fis);
      fis.close();
      System.out.println(sa.getDbname());
      
      FileOutputStream fos = new FileOutputStream("testfiles/metadata2011-21.xml");
      MetaDataXml.writeXml(sa,fos,true);
      fos.close();
    }
    catch (FileNotFoundException fnfe) { fail(EU.getExceptionMessage(fnfe)); }
    catch (IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch (JAXBException je) { fail(EU.getExceptionMessage(je)); }
  }

}
