package ch.enterag.utils.test;

import java.io.*;
import java.util.Random;

public class TestRandomInputStream
  extends InputStream
{
  private static Random _random = new Random(47);
  private static int _iBUFSIZ = 32768;
  private long _lSize = -1;
  private long _lPosition = 0;
  private byte[] _buf = TestUtils.getRandomFixedBytes(_iBUFSIZ);
  private int _iOffset = 0;
  
  public TestRandomInputStream(long lSize)
  {
    _lSize = (long)(lSize*_random.nextDouble());
  } /* constructor */
  
  @Override
  public int read() throws IOException
  {
    int iByte = -1;
    if (_lPosition < _lSize)
    {
      if (_iOffset >= _buf.length)
      {
        _buf = TestUtils.getRandomFixedBytes(_iBUFSIZ);
        _iOffset = 0;
      }
      iByte = _buf[_iOffset];
      if (iByte < 0)
        iByte = iByte + 256;
      _iOffset++;
      _lPosition++;
    }
    return iByte;
  } /* read */
  
  @Override
  public int read(byte[] bufRead, int iOffset, int iLength)
    throws IOException
  {
    int iRead = 0;
    if (iLength > 0)
    {
      long l = _lPosition;
      int i = _iOffset;
      for (int iByte = read(); (iByte != -1) && (iRead < iLength); iByte = read())
      {
        bufRead[iOffset+iRead] = (byte)iByte;
        iRead++;
        l = _lPosition;
        i = _iOffset;
      }
      if (iRead == 0)
        iRead = -1;
      _lPosition = l;
      _iOffset = i;
    }
    return iRead;
  } /* read */
  
  @Override
  public int read(byte[] bufRead)
    throws IOException
  {
    int iRead = read(bufRead,0,bufRead.length);
    return iRead;
  } /* read */
  
} /* TestInputStream */
