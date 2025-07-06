package ch.enterag.utils;

import java.text.*;
import static org.junit.Assert.*;
import org.junit.*;

public class DUTester
{
  private static DU _du = DU.getInstance("en", "dd.MM.yyyy");
  
  @Test
  public void testDate()
  {
    try 
    { 
      String s1 = "2016-07-06Z";
      System.out.println(s1);
      java.sql.Date date1 = _du.fromXsDate(s1);
      System.out.println(String.valueOf(date1.getTime()));
      String s2 = _du.toXsDate(date1);
      System.out.println(s2);
      assertEquals("Date were not equal!",s1,s2);
      java.sql.Date date2 = _du.fromXsDate(s2);
      assertEquals("Dates were not equal!",date1,date2);
    }
    catch(ParseException pe) { fail(EU.getExceptionMessage(pe)); }
  }

  @Test
  public void testTime()
  {
    try 
    { 
      String s1 = "18:18:14.897123456Z";
      System.out.println(s1);
      java.sql.Time time1 = _du.fromXsTime(s1);
      System.out.println(String.valueOf(time1.getTime()));
      String s2 = _du.toXsTime(time1);
      System.out.println(s2);
      // assertEquals("Times were not equal!",s1,s2);
      java.sql.Time time2 = _du.fromXsTime(s2);
      assertEquals("Times were not equal!",time1,time2);
    }
    catch(ParseException pe) { fail(EU.getExceptionMessage(pe)); }
  }

  @Test
  public void testTimestamp()
  {
    try
    {
      String s1 = "2016-07-06T18:18:14.897123456Z";
      System.out.println(s1);
      java.sql.Timestamp ts1 = _du.fromXsDateTime(s1);
      System.out.println(String.valueOf(ts1.getTime())+"/"+String.valueOf(ts1.getNanos()));
      String s2 = _du.toXsDateTime(ts1);
      System.out.println(s2);
      assertEquals("Timestamps were not equal!",s1,s2);
      java.sql.Timestamp ts2 = _du.fromXsDateTime(s2);
      assertEquals("Timestamps were not equal!",ts1,ts2);
    }
    catch(ParseException pe) { fail(EU.getExceptionMessage(pe)); }
  }

  @Test
  public void testFormat()
  {
    String s = "1";
    s = String.format("%1$-3s", s).replace(" ", "0");
    System.out.println(s);
    s = "12";
    s = String.format("%1$-3s", s).replace(" ", "0");;
    System.out.println(s);
    s = "123";
    s = String.format("%1$-3s", s).replace(" ", "0");;
    System.out.println(s);
    s = "1234";
    s = String.format("%1$-3s", s).replace(" ", "0");;
    System.out.println(s);
    DecimalFormat df = new DecimalFormat("000");
    int i = 1;
    System.out.println(df.format(i));
    i = 12;
    System.out.println(df.format(i));
    i = 123;
    System.out.println(df.format(i));
    i = 1234;
    System.out.println(df.format(i));
    try
    {
      s = "001";
      i = df.parse(s).intValue();
      System.out.println(String.valueOf(i));
      s = "012";
      i = df.parse(s).intValue();
      System.out.println(String.valueOf(i));
      s = "123";
      i = df.parse(s).intValue();
      System.out.println(String.valueOf(i));
      s = "1234";
      i = df.parse(s).intValue();
      System.out.println(String.valueOf(i));
    }
    catch (ParseException pe) { fail(EU.getExceptionMessage(pe)); }
    
  }
}
