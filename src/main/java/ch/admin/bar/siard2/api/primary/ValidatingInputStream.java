package ch.admin.bar.siard2.api.primary;

import java.io.*;
import java.security.*;
import java.util.*;
import org.w3c.dom.*;
import ch.enterag.utils.*;

public class ValidatingInputStream
  extends InputStream
{
  private InputStream _is = null;
  private MessageDigest _md = null;
  private byte[] _bufDigest = null;
  private long _lLength = -1;
  private long _lRead = 0;
  private boolean _bValidated = false;

  private void initialize(Element el, InputStream is, boolean bValidateLength)
  {
    if (is == null)
      _is = new ByteArrayInputStream(BU.fromHex(el.getTextContent()));
    else
    {
      _is = is;
      if (bValidateLength)
      {
        String sLength = el.getAttribute(ArchiveImpl._sATTR_LENGTH);
        if ((sLength != null) && (sLength.length() > 0))
          _lLength = Long.parseLong(sLength);
      }
      String sAlgorithm = el.getAttribute(ArchiveImpl._sATTR_DIGEST_TYPE);
      if ((sAlgorithm != null) && (sAlgorithm.length() > 0))
      {
        String sMessageDigest = el.getAttribute(ArchiveImpl._sATTR_MESSAGE_DIGEST);
        if ((sMessageDigest != null) && (sMessageDigest.length() > 0))
        {
          _bufDigest = BU.fromHex(sMessageDigest);
          try 
          { 
            _md = MessageDigest.getInstance(sAlgorithm);
            _is = new DigestInputStream(is,_md);
          }
          catch (NoSuchAlgorithmException nsae){}
        }
      }
    }
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** constructor
   * @param el DOM element with attributes length, digestType, messageDigest.
   * @param is InputStream to be wrapped.
   */
  ValidatingInputStream(Element el, InputStream is, boolean bValidateLength)
  {
    initialize(el,is,bValidateLength);
  } /* constructor ValidatingInputStream */

  /*------------------------------------------------------------------*/
  /** constructor
   * @param el DOM element with attributes length, digestType, messageDigest.
   * @param is InputStream to be wrapped.
   */
  public ValidatingInputStream(Element el, InputStream is)
  {
    initialize(el,is,true);
  } /* constructor ValidatingInputStream */

  private void validateAtEof()
    throws IOException
  {
    if (!_bValidated)
    {
      _bValidated = true;
      if (_lLength >= 0)
      {
        if (_lLength != _lRead)
          throw new IOException("Unexpected length "+String.valueOf(_lRead)+" instead of "+String.valueOf(_lLength)+" detected!");
        if (_md != null)
        {
          if (!Arrays.equals(_bufDigest,_md.digest()))
            throw new IOException("Message digest did not match!");
        }
      }
    }
  } /* validateAtEof */
  
  @Override
  public int read() 
    throws IOException
  {
    int iRead = _is.read();
    if (iRead != -1)
      _lRead++;
    else
      validateAtEof();
    return iRead;
  } /* read */
  
  @Override
  public int read(byte[] buf) 
    throws IOException
  {
    int iRead = _is.read(buf);
    if (iRead != -1)
      _lRead = _lRead + iRead;
    else
      validateAtEof();
    return iRead;
  } /* read */
  
  @Override
  public int read(byte[] buf, int iOffset, int iLength) 
    throws IOException
  {
    int iRead = 0;
    if (iLength > 0)
    {
      iRead = _is.read(buf,iOffset,iLength);
      if (iRead != -1)
        _lRead = _lRead + iRead;
      else
        validateAtEof();
    }
    return iRead;
  } /* read */

  @Override
  public long skip(long lSkip)
    throws IOException
  {
    long lSkipped = _is.skip(lSkip);
    _lRead = _lRead + lSkipped;
    return lSkipped;
  } /* skip */
  
  @Override
  public void close() 
    throws IOException
  {
    if (!_bValidated)
    {
      if (_lLength > _lRead)
        skip(_lLength-_lRead);
      while(!_bValidated)
        read();
    }
    _is.close();
  } /* close */
  
} /* ValidatingInputStream */
