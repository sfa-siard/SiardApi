package ch.enterag.utils.test;

import java.io.*;
import java.util.Arrays;

import ch.enterag.utils.SU;

public abstract class TestUtils
{
  public static int iBUFFER_SIZE = 8192;
  public static byte[] getBytes(int iLength)
  {
    byte[] buf = new byte[iLength];
    for (int i = 0; i < iLength; i++)
    {
      int j = i % 256;
      if (j > 127)
        j = j - 256;
      buf[i] = (byte)j;
    }
    return buf;
  } /* getBytes */
  
  public static byte[] getBytes(InputStream is)
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try
    {
      for (int iRead = is.read(); iRead != -1; iRead = is.read())
        baos.write(iRead);
      baos.close();
    }
    catch(IOException ie) { System.err.println(ie.getClass().getName()+": "+ie.getMessage()); }
    return baos.toByteArray();
  } /* getBytes */
  
  public static String getString(int iLength)
  {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < iLength; i++)
    {
      int j = i % 94;
      sb.appendCodePoint(33+j);
    }
    return sb.toString();
  } /* getString */
  
  public static String getString(Reader rdr)
  {
    StringWriter sw = new StringWriter();
    try
    {
      for (int iRead = rdr.read(); iRead != -1; iRead = rdr.read())
        sw.write((char)iRead);
      sw.close();
    }
    catch(IOException ie) { System.err.println(ie.getClass().getName()+": "+ie.getMessage()); }
    return sw.getBuffer().toString();
  } /* getString */

  public static String getNString(int iLength)
  {
    /* generate random bytes in the range [x20,xFF] */
    byte[] buf = new byte[iLength];
    for (int i = 0; i < iLength; i++)
    {
      int j = i % 192;
      if (j < 96)
        buf[i] = (byte)(32+j);
      else
        buf[i] = (byte)(64+j);
    }
    /* read them into a string as 1252 */
    return SU.getIsoLatin1String(buf);
  } /* getNString */
  
  public static String getNString(Reader rdr)
  {
    StringWriter sw = new StringWriter();
    try
    {
      for (int iRead = rdr.read(); iRead != -1; iRead = rdr.read())
        sw.write((char)iRead);
      sw.close();
    }
    catch(IOException ie) { System.err.println(ie.getClass().getName()+": "+ie.getMessage()); }
    return sw.getBuffer().toString();
  } /* getNString */

  /* reading deflated stuff does not always fill the buffer ... */
  private static int fillCharBuffer(Reader rdr, char[] cbuf)
    throws IOException
  {
    int iOffset = 0;
    for (int iRead = rdr.read(cbuf); (iRead != -1) && (iOffset < cbuf.length); iRead = rdr.read(cbuf,iOffset,cbuf.length-iOffset))
      iOffset = iOffset + iRead;
    if (iOffset == 0)
      iOffset = -1;
    return iOffset;
  }
  public static boolean equalReaders(Reader rdr1, Reader rdr2)
    throws IOException
  {
    boolean bEqual = true;
    char[] cbuf1 = new char[iBUFFER_SIZE];
    char[] cbuf2 = new char[iBUFFER_SIZE];
    long lRead1 = 0;
    long lRead2 = 0;
    int iRead1 = fillCharBuffer(rdr1, cbuf1);
    int iRead2 = fillCharBuffer(rdr2, cbuf2);
    while (bEqual && (iRead1 != -1) && (iRead2 != -1))
    {
      lRead1 = lRead1 + iRead1;
      lRead2 = lRead2 + iRead2;
      if (iRead1 != iRead2)
        bEqual = false;
      else
      {
        if (iRead1 < cbuf1.length)
          cbuf1 = Arrays.copyOf(cbuf1,iRead1);
        if (iRead2 < cbuf2.length)
          cbuf2 = Arrays.copyOf(cbuf2,iRead2);
        if (!Arrays.equals(cbuf1, cbuf2))
          bEqual = false;
        iRead1 = fillCharBuffer(rdr1, cbuf1);
        iRead2 = fillCharBuffer(rdr2, cbuf2);
      }
    }
    if (iRead1 != iRead2)
      bEqual = false;
    rdr1.close();
    rdr2.close();
    return bEqual;
  } /* equalReaders */

  /* reading deflated stuff does not always fill the buffer ... */
  private static int fillByteBuffer(InputStream is, byte[] buf)
    throws IOException
  {
    int iOffset = 0;
    for (int iRead = is.read(buf); (iRead != -1) && (iOffset < buf.length); iRead = is.read(buf,iOffset,buf.length-iOffset))
      iOffset = iOffset + iRead;
    if (iOffset == 0)
      iOffset = -1;
    return iOffset;
  }
  public static boolean equalInputStreams(InputStream is1, InputStream is2)
    throws IOException
  {
    boolean bEqual = true;
    byte[] buf1 = new byte[iBUFFER_SIZE];
    byte[] buf2 = new byte[iBUFFER_SIZE];
    long lRead1 = 0;
    long lRead2 = 0;
    int iRead1 = fillByteBuffer(is1, buf1);
    int iRead2 = fillByteBuffer(is2, buf2);
    while (bEqual && (iRead1 != -1) && (iRead2 != -1))
    {
      lRead1 = lRead1 + iRead1;
      lRead2 = lRead2 + iRead2;
      if (iRead1 != iRead2)
        bEqual = false;
      else
      {
        if (iRead1 < buf1.length)
          buf1 = Arrays.copyOf(buf1,iRead1);
        if (iRead2 < buf2.length)
          buf2 = Arrays.copyOf(buf2,iRead2);
        if (!Arrays.equals(buf1, buf2))
          bEqual = false;
        iRead1 = fillByteBuffer(is1, buf1);
        iRead2 = fillByteBuffer(is2, buf2);
      }
    }
    if (iRead1 != iRead2)
      bEqual = false;
    is1.close();
    is2.close();
    return bEqual;
  } /* equalInputStreams */
  
}
