/*== Value.java ========================================================
Value interface provides access to value handling of cells or fields.
Application : SIARD 2.0
Description : Value interface provides access to value handling of cells 
              or fields. 
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 07.07.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api;

import java.io.*;
import java.math.*;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import javax.xml.datatype.*;

/*====================================================================*/
/** Value interface provides access to value handling of cells or fields.
 @author Hartwig Thomas
 */
public interface Value
{
  /*------------------------------------------------------------------*/
  /** get ancestor cell with which this Field instance is ultimately associated.
   * @return get ancestor cell with which this Field instance is ultimately 
   * associated. 
   */
  public Cell getAncestorCell();

  /*------------------------------------------------------------------*/
  /** return value (cell or field) meta data associated with this value.
   * @return value (cell or field) meta data associated with this value.
   */
  public MetaValue getMetaValue();
  
  /*------------------------------------------------------------------*/
  /** return true, if field value is null
   * @return true, if field value is null.
   */
  public boolean isNull();
  
  /* xs:string */
  /*------------------------------------------------------------------*/
  /** return string value of cell (or null for NULL).
   * @return string value.
   * @throws IOException if an I/O error occurred.
   */
  public String getString()
    throws IOException;
  /*------------------------------------------------------------------*/
  /** set string value of cell.
   * @param s string value of cell.
   * @throws IOException if an I/O error occurred.
   */
  public void setString(String s)
    throws IOException;
  
  /* xs:hexBinary */
  /*------------------------------------------------------------------*/
  /** return byte array value of cell (or null for NULL).
   * @return byte array value.
   * @throws IOException if an I/O error occurred.
   */
  public byte[] getBytes()
    throws IOException;
  /*------------------------------------------------------------------*/
  /** set byte array value of cell.
   * @param buf byte array value of cell.
   * @throws IOException if an I/O error occurred.
   */
  public void setBytes(byte[] buf)
    throws IOException;
  
  /* xs:boolean */
  /*------------------------------------------------------------------*/
  /** return boolean value of cell (or null for NULL).
   * @return boolean value.
   * @throws IOException if an I/O error occurred.
   */
  public Boolean getBoolean()
    throws IOException;
  /*------------------------------------------------------------------*/
  /** set boolean value of cell.
   * @param b boolean value of cell.
   * @throws IOException if an I/O error occurred.
   */
  public void setBoolean(boolean b)
    throws IOException;
  
  /* xs:int */
  /*------------------------------------------------------------------*/
  /** return short value of cell (or null for NULL) for cells of type
   * SMALLINT.
   * @return short value.
   * @throws IOException if an I/O error occurred.
   */
  public Short getShort()
    throws IOException;
  /*------------------------------------------------------------------*/
  /** set short value of cell for cells of type SMALLINT.
   * @param sh short value of cell.
   * @throws IOException if an I/O error occurred.
   */
  public void setShort(short sh)
    throws IOException;

  /*------------------------------------------------------------------*/
  /** return integer value of cell (or null for NULL) for cells of type
   * INTEGER.
   * @return integer value.
   * @throws IOException if an I/O error occurred.
   */
  public Integer getInt()
    throws IOException;
  /*------------------------------------------------------------------*/
  /** set integer value of cell for cells of type INTEGER.
   * @param i integer value of cell.
   * @throws IOException if an I/O error occurred.
   */
  public void setInt(int i)
    throws IOException;

  /* xs:integer */
  /*------------------------------------------------------------------*/
  /** return long value of cell (or null for NULL) for cells of type
   * BIGINT.
   * @return long value.
   * @throws IOException if an I/O error occurred.
   */
  public Long getLong()
    throws IOException;
  /*------------------------------------------------------------------*/
  /** set long value of cell for cells of type BIGINT.
   * @param l long value of cell.
   * @throws IOException if an I/O error occurred.
   */
  public void setLong(long l)
    throws IOException;
  
  /*------------------------------------------------------------------*/
  /** return big integer value of cell (or null for NULL).
   * @return big integer value.
   * @throws IOException if an I/O error occurred.
   */
  public BigInteger getBigInteger()
    throws IOException;
  /*------------------------------------------------------------------*/
  /** set big integer value of cell.
   * @param bi big integer value of cell.
   * @throws IOException if an I/O error occurred.
   */
  public void setBigInteger(BigInteger bi)
    throws IOException;
  
  /* xs:decimal */
  /*------------------------------------------------------------------*/
  /** return big decimal value of cell (or null for NULL).
   * @return big decimal value.
   * @throws IOException if an I/O error occurred.
   */
  public BigDecimal getBigDecimal()
    throws IOException;
  /*------------------------------------------------------------------*/
  /** set big decimal value of cell.
   * @param bd big decimal value of cell.
   * @throws IOException if an I/O error occurred.
   */
  public void setBigDecimal(BigDecimal bd)
    throws IOException;
  
  /* xs:float */
  /*------------------------------------------------------------------*/
  /** return float value of cell (or null for NULL) for cells of type REAL.
   * @return float value.
   * @throws IOException if an I/O error occurred.
   */
  public Float getFloat()
    throws IOException;
  /*------------------------------------------------------------------*/
  /** set float value of cell for cells of type REAL.
   * @param f float value of cell.
   * @throws IOException if an I/O error occurred.
   */
  public void setFloat(float f)
    throws IOException;
  
  /* xs:double */
  /*------------------------------------------------------------------*/
  /** return double value of cell (or null for NULL).
   * @return double value.
   * @throws IOException if an I/O error occurred.
   */
  public Double getDouble()
    throws IOException;
  /*------------------------------------------------------------------*/
  /** set double value of cell.
   * @param d double value of cell.
   * @throws IOException if an I/O error occurred.
   */
  public void setDouble(double d)
    throws IOException;
  
  /* xs:date (dateType) */
  /*------------------------------------------------------------------*/
  /** return date value of cell (or null for NULL).
   * @return date value.
   * @throws IOException if an I/O error occurred.
   */
  public Date getDate()
    throws IOException;
  /*------------------------------------------------------------------*/
  /** set date value of cell.
   * @param date date value of cell.
   * @throws IOException if an I/O error occurred.
   */
  public void setDate(Date date)
    throws IOException;
  
  /* xs:time (timeType) */
  /*------------------------------------------------------------------*/
  /** return time value of cell (or null for NULL).
   * @return time value.
   * @throws IOException if an I/O error occurred.
   */
  public Time getTime()
    throws IOException;
  /*------------------------------------------------------------------*/
  /** set time value of cell.
   * @param time time value of cell.
   * @throws IOException if an I/O error occurred.
   */
  public void setTime(Time time)
    throws IOException;
  
  /* xs:dateTime (dateTimeType) */
  /*------------------------------------------------------------------*/
  /** return timestamp value of cell (or null for NULL).
   * @return timestamp value.
   * @throws IOException if an I/O error occurred.
   */
  public Timestamp getTimestamp()
    throws IOException;
  /*------------------------------------------------------------------*/
  /** set timestamp value of cell.
   * @param ts timestamp value of cell.
   * @throws IOException if an I/O error occurred.
   */
  public void setTimestamp(Timestamp ts)
    throws IOException;
  
  /* xs:duration */
  /*------------------------------------------------------------------*/
  /** return duration value of cell (or null for NULL).
   * @return duration value.
   * @throws IOException if an I/O error occurred.
   */
  public Duration getDuration()
    throws IOException;
  /*------------------------------------------------------------------*/
  /** set duration value of cell.
   * @param duration duration value of cell.
   * @throws IOException if an I/O error occurred.
   */
  public void setDuration(Duration duration)
    throws IOException;
  
  /* clobType */
  /*------------------------------------------------------------------*/
  /** return cell reader (or null for NULL).
   * @return cell reader (must be closed by caller if it is not null!).
   * @throws IOException if an I/O error occurred.
   */
  public Reader getReader()
    throws IOException;
  
  /*------------------------------------------------------------------*/
  /** return length (in characters) of character value or -1 otherwise.
   * @return length (in characters) of character value or -1 otherwise.
   * @throws IOException if an I/O error occurred.
   */
  public long getCharLength()
    throws IOException;
  
  /*------------------------------------------------------------------*/
  /** set cell reader.
   * @param rdrClob cell reader (is closed at end).
   * @throws IOException if an I/O error occurred.
   */
  public void setReader(Reader rdrClob)
    throws IOException;

  /*------------------------------------------------------------------*/
  /** return file name stored in attribute of cell, or null.
   * @return file name stored in attribute of cell, or null.
   * @throws IOException if an I/O error occurred.
   */
  public String getFilename()
    throws IOException;
  
  /* blobType */
  /*------------------------------------------------------------------*/
  /** return cell input stream (or null for NULL).
   * @return cell input stream (must be closed if it is not null!).
   * @throws IOException if an I/O error occurred.
   */
  public InputStream getInputStream()
    throws IOException;
  
  /*------------------------------------------------------------------*/
  /** return byte length of binary value or -1 otherwise.
   * @return byte length of binary value or -1 otherwise.
   * @throws IOException if an I/O error occurred.
   */
  public long getByteLength()
    throws IOException;
  
  /*------------------------------------------------------------------*/
  /** set cell input stream.
   * @param isBlob cell input stream (is closed at end).
   * @throws IOException if an I/O error occurred.
   */
  public void setInputStream(InputStream isBlob)
    throws IOException;
  
  /*  predefined type */
  /*------------------------------------------------------------------*/
  /** return object of type appropriate for the data type, or null,
   * it it is NULL.
   * N.B.: if a Reader or InputStream are returned, the caller should
   * close them after use.
   * @return object of type appropriate for the data type, or null.
   * @throws IOException if an I/O error occurred.
   */
  public Object getObject()
    throws IOException;
  
  /* array */
  /*------------------------------------------------------------------*/
  /** return number of elements of ARRAY cell (or 0 for NULL).
   * @return number of elements.
   * @throws IOException if an I/O error occurred.
   */
  public int getElements()
    throws IOException;
  /*------------------------------------------------------------------*/
  /** get ARRAY field with the given index.
   * @param iElement index.
   * @return ARRAY field.
   * @throws IOException if an I/O error occurred.
   */
  public Field getElement(int iElement)
    throws IOException;
  
  /* udt */
  /*------------------------------------------------------------------*/
  /** return number of attributes of UDT cell (or 0 for NULL).
   * @return number of attributes.
   * @throws IOException if an I/O error occurred.
   */
  public int getAttributes()
    throws IOException;
  /*------------------------------------------------------------------*/
  /** get UDT attribute field with the given index.
   * @param iAttribute index.
   * @return UDT field.
   * @throws IOException if an I/O error occurred.
   */
  public Field getAttribute(int iAttribute)
    throws IOException;
  
  /*------------------------------------------------------------------*/
  /** get a linearized ("flattened") list of values represented by this 
   * value instance.
   * @param bSupportsArrays list is for database system which supports arrays.
   * @param bSupportsUdts list is for database system which supports UDT types.
   * @return "flattened" list of values.
   * @throws IOException if an I/O error occurred.
   */
  public List<Value> getValues(boolean bSupportsArrays, boolean bSupportsUdts)
    throws IOException;
  
} /* interface Value */
