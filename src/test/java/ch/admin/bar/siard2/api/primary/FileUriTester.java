package ch.admin.bar.siard2.api.primary;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.utils.*;

public class FileUriTester
{

  @Test
  public void testRelative()
  {
    /* relative URI */
    try
    {
      URI uri = new URI("../../../Temp/lobs/");
      System.out.println(
        uri.getScheme()+"|"+
        uri.getAuthority()+"|"+
        uri.getHost()+"|"+
        uri.getPort()+"|"+
        uri.getPath()+"|"+
        uri.isAbsolute());
        File file = new File(".");
        URI uriResolve = file.toURI();
        URI uriResolved = uriResolve.resolve(uri);
        System.out.println(uriResolve.toString()+" + "+uri.toString()+" = "+uriResolved.toString());
      
      /* relative File to absolute URI */
      String sFolderRelative = "some folder";
      File fileRelative = new File(sFolderRelative); // assumes a file
      if (fileRelative.isDirectory())
        fail("File expected!");
      URI uriRelative = fileRelative.toURI(); // absolute URI
      if (uriRelative.isAbsolute())
      {
        System.out.println(
          uriRelative.getScheme()+"|"+
          uriRelative.getAuthority()+"|"+
          uriRelative.getHost()+"|"+
          uriRelative.getPort()+"|"+
          uriRelative.getPath()+"|"+
          uriRelative.isAbsolute());
        System.out.println("\""+fileRelative.toString()+"\": \""+uriRelative.toString()+"\"");
        uriRelative = new URI("file","",uriRelative.getPath(),null);
        fileRelative = new File(uriRelative);
        System.out.println("\""+uriRelative.toString()+"\": \""+fileRelative.toString()+"\"");
      }
      else
        fail("Absolute URI expteced!");

      /* absolute folder to absolute URI */
      File fileAbsolute = fileRelative.getAbsoluteFile().getParentFile(); // assumes a folder
      if (fileAbsolute.isFile())
        fail("Folder expected!");
      URI uriAbsolute = fileAbsolute.toURI(); // absolute URI terminated by /
      if (uriAbsolute.isAbsolute())
        System.out.println("\""+fileAbsolute.toString()+"\": \""+uriAbsolute.toString()+"\"");
      else
        fail("Absulute URI expected!");
      
      /* absolute URI to absolute folder */
      fileAbsolute = new File(uriAbsolute);
      if (fileAbsolute.isFile())
        fail("Folder expected!");
      System.out.println("\""+uriAbsolute.toString()+"\": \""+fileAbsolute.toString()+"\"");
  
      /* relative URI cannot be converted to File */
      uriRelative = uriAbsolute.relativize(uriRelative);
      if (!uriRelative.isAbsolute())
        System.out.println("\""+fileRelative.toString()+"\": \""+uriRelative.toString()+"\"");
      else 
        fail("Absolute URI expected!");
      // we cannot derive a relative file from a relative URI
      try { fileRelative = new File(uriRelative); }
      catch(IllegalArgumentException iae) { System.out.println(EU.getExceptionMessage(iae)); }
  
      /* resolved relative URI is just an absolute URI */
      uriAbsolute = uriAbsolute.resolve(uriRelative);
      if (uriAbsolute.isAbsolute())
        System.out.println("\""+uriRelative.toString()+"\": \""+uriAbsolute.toASCIIString()+"\"");
      else
        fail("Absolute URI expeced!");
      fileAbsolute = new File(uriAbsolute);
      if (fileAbsolute.isDirectory())
        fail("File expected!");
      System.out.println("\""+uriAbsolute.toString()+"\": \""+fileAbsolute.toString()+"\"");
    }
    catch(URISyntaxException use) { fail(EU.getExceptionMessage(use)); }
  }
  
  @Test
  public void testAbsolute()
  {
    String sFolderRelative = "some folder";
    File fileRelative = new File(sFolderRelative);
    URI uriAbsolute = fileRelative.toURI();
    File fileAbsolute = new File(uriAbsolute);
    System.out.println("\""+uriAbsolute.toString()+"\": \""+fileAbsolute.toString()+"\"");
    try
    {
      uriAbsolute = new URI("file","",uriAbsolute.getPath(),null);
      fileAbsolute = new File(uriAbsolute);
      System.out.println("\""+uriAbsolute.toString()+"\": \""+fileAbsolute.toString()+"\"");

      fileAbsolute = new File("\\\\econel200s2.enterag.ch\\Home\\Hartwig");
      // from URI to file we must use java.nio.Paths if UNC file names with host names are involved! 
      Path pathAbsolute = Paths.get(fileAbsolute.toString());
      uriAbsolute = pathAbsolute.toUri();
      System.out.println(
        uriAbsolute.getScheme()+"|"+
        uriAbsolute.getAuthority()+"|"+
        uriAbsolute.getHost()+"|"+
        uriAbsolute.getPort()+"|"+
        uriAbsolute.getPath());
      System.out.println("\""+fileAbsolute.toString()+"\": \""+uriAbsolute.toString()+"\"");

      /* try the same thing using Paths */
      pathAbsolute = Paths.get(uriAbsolute);
      fileAbsolute = pathAbsolute.toFile();
      System.out.println("\""+uriAbsolute.toString()+"\": \""+fileAbsolute.toString()+"\"");
      
    }
    catch(URISyntaxException use) { fail(EU.getExceptionMessage(use)); }
    
  }
}
