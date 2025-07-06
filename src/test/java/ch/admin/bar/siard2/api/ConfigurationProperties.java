package ch.admin.bar.siard2.api;

import java.io.*;
import java.util.*;
import static org.junit.Assert.*;

public class ConfigurationProperties
  extends Properties
{
  private static final long serialVersionUID = 5204423170460249028L;

  private void readProperties()
  {
    try
    {
      Reader rdr = new FileReader("build.properties");
      load(rdr);
      rdr.close();
    }
    catch (IOException ie) { fail(ie.getClass().getName()+": "+ie.getMessage()); }
  }
  
  public ConfigurationProperties()
  {
    readProperties();
  }
  
  public String getLobsFolder()
  {
    return getProperty("lobsfolder");
  }
  
}
