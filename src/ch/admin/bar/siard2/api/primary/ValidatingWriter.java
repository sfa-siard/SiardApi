package ch.admin.bar.siard2.api.primary;

import java.io.*;
import org.w3c.dom.*;
import ch.enterag.utils.xml.*;

public class ValidatingWriter
  extends Writer
{
  private Element _el = null;
  private Writer _wr = null;
  private long _lWritten = 0;
  
  public ValidatingWriter(Element el, OutputStream os)
  {
    XU.clearElement(el);
    _el = el;
    if (os == null)
      _wr = new StringWriter();
    else
    {
      try
      {
        _wr = new OutputStreamWriter(new ValidatingOutputStream(el, os),
          ArchiveImpl._sDEFAULT_ENCODING);
      }
      catch (UnsupportedEncodingException usee) { }
    }
  } /* constructor ValidatingWriter */

  @Override
  public void write(int iChar)
    throws IOException
  {
    _wr.write(iChar);
    _lWritten++;
  } /* write */
  
  @Override
  public void write(char[] cbuf)
    throws IOException
  {
    _wr.write(cbuf);
    _lWritten = _lWritten + cbuf.length;
  } /* write */
  
  @Override
  public void write(char[] cbuf, int iOffset, int iLength)
    throws IOException
  {
    _wr.write(cbuf, iOffset, iLength);
    _lWritten = _lWritten + iLength;
  } /* write */

  @Override
  public void write(String s)
    throws IOException
  {
    _wr.write(s);
    _lWritten = _lWritten + s.length();
  } /* write */
  
  @Override
  public void write(String s, int iOffset, int iLength)
    throws IOException
  {
    _wr.write(s, iOffset, iLength);
    _lWritten = _lWritten + iLength;
  } /* write */

  @Override
  public void flush() throws IOException
  {
    _wr.flush();
  } /* flush */

  @Override
  public void close() throws IOException
  {
    _wr.close();
    if (_wr instanceof StringWriter)
    {
      StringWriter swr = (StringWriter)_wr;
      XU.toXml(swr.toString(),_el);
    }
    _el.setAttribute(ArchiveImpl._sATTR_LENGTH, String.valueOf(_lWritten));
  } /* close */
  
} /* class VaidatingWriter */
