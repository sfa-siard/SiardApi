/*== ValueImpl.java ===================================================
ValueImpl implements the interface Value.
Application : SIARD 2.0
Description : ValueImpl implements the interface Value. 
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 05.07.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api.primary;

import java.io.*;
import java.math.*;
import java.net.*;
import java.sql.*;
import java.sql.Date;
import java.text.*;
import java.util.*;
import javax.xml.datatype.*;
import org.w3c.dom.*;

import ch.enterag.utils.*;
import ch.enterag.utils.database.*;
import ch.enterag.utils.mime.*;
import ch.enterag.utils.xml.XU;
import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.api.generated.*;

/*====================================================================*/
/** ValueImpl implements the interface Value (common stuff for Cell and 
 * Field).
 @author Hartwig Thomas
 */
public abstract class ValueImpl
  implements Value
{
  private static final String _sSEQUENCE_PREFIX = "seq";  
  private static final String _sRECORD_PREFIX = "record";
  private static final String _sEXTENSION_TEXT = "txt";
  private static final String _sEXTENSION_XML = "xml";
  private static final String _sEXTENSION_BIN = "bin";
  private static final DU _du = DU.getInstance("en", "yyyy-MM-dd HH:mm:ss.S");

  private URI _uriTemporaryLobFolder = null;
  public URI getTemporaryLobFolder() { return _uriTemporaryLobFolder; }
  
  private long _lRecord = -1;
  /** return current row number (0-based).
   * @return current row number.
   */
  protected long getRecord() { return _lRecord; }
  
  private int _iIndex = -1;
  /** return index (0-based) of cell or field
   * @return index of cell or field.
   */
  protected int getIndex() { return _iIndex; }
  
  Element _elValue = null;
  /** get DOM element representing the value.
   * @return DOM element representing the value.
   * @throws IOException
   */
  private Element getValueElement()
    throws IOException
  {
    if (_elValue == null)
    {
      if (this instanceof Cell)
        _elValue = RecordImpl.getDocument().createElementNS(Archive.sSIARD2_TABLE_NAMESPACE,getColumnTag(getIndex()));
      else
      {
        ValueImpl viParent = (ValueImpl)((Field)this).getParent();
        int iCardinalityParent = viParent.getCardinality();
        if (iCardinalityParent >= 0)
          _elValue = RecordImpl.getDocument().createElementNS(Archive.sSIARD2_TABLE_NAMESPACE,getElementTag(getIndex()));
        else
        {
          MetaType mtParent = viParent.getMetaType();
          CategoryType catParent = mtParent.getCategoryType();
          if (catParent == CategoryType.UDT)
            _elValue = RecordImpl.getDocument().createElementNS(Archive.sSIARD2_TABLE_NAMESPACE,getAttributeTag(getIndex()));
        }
        viParent.getValueElement().appendChild(_elValue);
      }
    }
    return _elValue;
  } /* getValueElement */

  private MetaValue _mv = null;
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public MetaValue getMetaValue() { return _mv; }
  
  /*------------------------------------------------------------------*/
  /** extend an array to new size.
   * @param iSize new size.
   * @param iCardinality cardinality of array field.
   * @throws IOException if an I/O error occurred.
   */
  protected void extendArray(int iSize, int iCardinality)
    throws IOException
  {
    /* extend field map of array with NULL fields */
    for (int iField = 0; iField < iSize; iField++)
    {
      String sTag = getElementTag(iField);
      Field field = getFieldMap().get(sTag);
      if (field == null)
      {
        MetaField mfNull = getMetaField(iField);
        getFieldMap().put(sTag, createField(iField, mfNull, null));
      }
    }
  } /* extendArray */

  /*------------------------------------------------------------------*/
  /** create a child field of this cell or field.
   * @param iField index (0-based) of child field.
   * @param mf field meta data of child field.
   * @param el DOM element for value of child field.
   * @return freshly created field.
   * @throws IOException if an I/O error occurred.
   */
  protected abstract Field createField(int iField, MetaField mf, Element el)
    throws IOException;
  
  /*------------------------------------------------------------------*/
  /** return the internal LOB folder relative to this table's LOB folder,
   * or null, if it should be stored externally.
   * @return internal LOB folder relative to this table's LOB folder.
   * @throws IOException if an I/O error occurred. 
   */
  protected abstract String getInternalLobFolder()
    throws IOException;
  
  /*------------------------------------------------------------------*/
  /** return the current cell or the cell ancestor of this field.
   * @return
   */
  public abstract Cell getAncestorCell();
  
  /*------------------------------------------------------------------*/
  /** return the number of fields in this column's or field's meta data.
   * @return number of fields in this column's or field's meta data. 
   * @throws IOException of an I/O error occurred.
   */
  private int getMetaFields()
    throws IOException
  {
    return getMetaValue().getMetaFields();
  } /* getMetaFields */
  
  /*------------------------------------------------------------------*/
  /** return the field meta data with the given index in this column's or
   * field's meta data.
   * @param iField index (0-based) of the field.
   * @return field meta data.
   * @throws IOException
   */
  private MetaField getMetaField(int iField)
    throws IOException
  {
    return getMetaValue().getMetaField(iField);
  } /* getMetaField */

  /*------------------------------------------------------------------*/
  /** return the absolute LOB folder for externally storing the LOB of
   * this field. 
   * @return absolute LOB folder or null, if the LOB of this column
   *   is not to be stored externally.
   */
  private URI getAbsoluteLobFolder()
  {
    return getMetaValue().getAbsoluteLobFolder();
  } /* getAbsoluteFolder */
  
  /*------------------------------------------------------------------*/
  /** return the Table implementation to which this cell or field belongs.
   * @return Table implementation to which this cell or field belongs.
   */
  private TableImpl getTableImpl()
  {
    return (TableImpl)getAncestorCell().getParentRecord().getParentTable();
  } /* getTableImpl */
  
  /*------------------------------------------------------------------*/
  /** return the Archive implementation
   * @return Archive implementation
   */
  private ArchiveImpl getArchiveImpl()
  {
    return (ArchiveImpl)getTableImpl().getParentSchema().getParentArchive();
  } /* getArchiveImpl */

  /*------------------------------------------------------------------*/
  /** return the cardinality (maximum length of array) associated with 
   * this cell or field.
   * @return cardinality or -1 if it is not an array.
   * @throws IOException
   */
  private int getCardinality()  
    throws IOException
  {
    return getMetaValue().getCardinality();
  } /* getCardinality */
    
  /*------------------------------------------------------------------*/
  /** return the predefined type of this cell or field as a java.sql.Types
   * integer using this mapping:
   * null                               NULL
   * "CHAR[(<Length>)]"                 CHAR
   * "VARCHAR[(<Length>)]"              VARCHAR
   * "CLOB[(<LOB Length>)]"              CLOB
   * "NCHAR[(<Length>)]"                NCHAR
   * "NVARCHAR[(<Length>)]"             NVARCHAR        
   * "NCLOB[(<LOB Length>)]"             NCLOB           
   * "XML"                              SQLXML
   * "BINARY[(<Length>)]"               BINARY          
   * "VARBINARY[(<Length>)]"            VARBINARY       
   * "BLOB[(<LOB Length>)]"             BLOB            
   * "BOOLEAN"                          BOOLEAN         
   * "SMALLINT"                         SMALLINT        
   * "INTEGER"                          INTEGER         
   * "BIGINT"                           BIGINT          
   * "DECIMAL[(<Precision>[,<Scale>])]" DECIMAL         
   * "NUMERIC[(<Precision>[,<Scale>])]" NUMERIC         
   * "REAL"                             REAL            
   * "FLOAT[(<Precision>)]"             FLOAT           
   * "DOUBLE PRECISION"                 DOUBLE          
   * "DATE"                             DATE            
   * "TIME[(<Scale>)]"                  TIME            
   * "TIMESTAMP[(<Scale>)]"             TIMESTAMP       
   * "INTERVAL ..."                     OTHER
   * @return predefined type of this cell or field.
   * @throws IOException if an I/O error occurred.
   */
  private int getPreType()
    throws IOException
  {
    return getMetaValue().getPreType();
  } /* getPreType */
  
  /*------------------------------------------------------------------*/
  /** return the type meta data associated with this cell or field.
   * @return type meta data or null, if no type meta data is available.
   * @throws IOException if an I/O error occurred.
   */
  private MetaType getMetaType()
    throws IOException
  {
    return getMetaValue().getMetaType();
  } /* getMetaType */
  
  /*------------------------------------------------------------------*/
  /** return the MIME type of the LOBs in this column or field, or null, if
   * it was not set.
   * @return MIME type of the LOBs of this column or field.
   * @throws IOException
   */
  private String getMimeType()
  {
    return getMetaValue().getMimeType();
  } /* getMimeType */
  
  /*------------------------------------------------------------------*/
  /** column tag
   * @param iColumn index (0-based) of column.
   * @return column tag.
   */
  public static String getColumnTag(int iColumn)
  {
    return "c"+String.valueOf(iColumn+1);
  } /* getColumnTag */
  /*------------------------------------------------------------------*/
  /** ARRAY element tag
   * @param iIndex index (0-based) of ARRAY element.
   * @return ARRAY element tag.
   */
  public static String getElementTag(int iIndex)
  {
    return "a"+String.valueOf(iIndex+1);
  } /* getElementTag */
  /*------------------------------------------------------------------*/
  /** UDT attribute tag
   * @param iIndex index (0-based) of UDT attribute.
   * @return UDT attribute tag.
   */
  public static String getAttributeTag(int iIndex)
  {
    return "u"+String.valueOf(iIndex+1);
  } /* getAttributeTag */
  /*------------------------------------------------------------------*/
  /** ROW field tag
   * @param iIndex index (0-based) of ROW field.
   * @return ROW field tag.
   */
  public static String getFieldTag(int iIndex)
  {
    return "r"+String.valueOf(iIndex+1);
  } /* getFieldTag */

  /*------------------------------------------------------------------*/
  /** get the index of a cell of field from its tag.
   * @param sTag tag name.
   * @return index (0-based)
   */
  public static int getIndex(String sTag)
  {
    return Integer.parseInt(sTag.substring(1))-1;
  } /* getIndex */
  
  /*------------------------------------------------------------------*/
  /** get suitable tag for the type and index of this cell's or field's child field.
   * @param mtParent meta type of parent.
   * @param iCardinalityParent cardinality of parent.
   * @param iField index if field.
   * @return tag for child field.
   */
  private String getTag(MetaType mtParent, int iCardinalityParent, int iField)
  {
    String sTag = null;
    if (iCardinalityParent >= 0)
      sTag = getElementTag(iField);
    else
    {
      CategoryType catParent = mtParent.getCategoryType();
      if (catParent == CategoryType.UDT)
        sTag = getAttributeTag(iField);
      else // anonymous row
        sTag = getFieldTag(iField);
    }
    return sTag;
  } /* getTag */

  /*------------------------------------------------------------------*/
  /** return the file name for a non-inlined value of this cell or field.
   * @return file name for this cell or field.
   * @throws IOException
   */
  public String getLobFilename()
    throws IOException
  {
    int iPreType = getPreType();
    String sExtension = _sEXTENSION_TEXT;
    if (iPreType == Types.SQLXML)
      sExtension = _sEXTENSION_XML;
    else if ((iPreType == Types.BINARY) ||
             (iPreType == Types.VARBINARY) ||
             (iPreType == Types.BLOB))
    {
      sExtension = _sEXTENSION_BIN;
      String sMimeType = getMimeType();
      if (sMimeType != null)
        sExtension = MimeTypes.getExtension(sMimeType);
    }
    String sFilename = _sRECORD_PREFIX+String.valueOf(getRecord());
    return sFilename + "." + sExtension;
  } /* getLobFileName */

  /* the map of the fields contained in this cell or field */
  private Map<String,Field> _mapFields = null;
  private Map<String,Field> getFieldMap()
    throws IOException
  {
    if (_mapFields == null)
    {
      _mapFields = new HashMap<String,Field>();
      int iCardinality = getCardinality();
      if (iCardinality < 0)
      {
        MetaType mt = getMetaType();
        if (mt != null)
        {
          /* fill it with all NULL fields */
          for (int iField = 0; iField < getMetaFields(); iField++)
          {
            MetaField mf = getMetaField(iField);
            String sTag = getTag(mt,iCardinality, iField);
            _mapFields.put(sTag, createField(iField, mf, null));
          }
        }
      }
    }
    return _mapFields;
  } /* getFieldMap */

  /*------------------------------------------------------------------*/
  /** set the DOM element of the value of this cell or field.
   * @param elValue DOM value.
   * @throws IOException if an I/O error occurred.
   */
  private void setValue(Element elValue)
    throws IOException
  {
    if (elValue != null)
    {
      int iField = 0;
      /* pick up the fields in the value element */
      for (int iChild = 0; iChild < elValue.getChildNodes().getLength(); iChild++)
      {
        Node node = elValue.getChildNodes().item(iChild);
        if (node.getNodeType() == Node.ELEMENT_NODE)
        {
          Element elChild = (Element)node;
          MetaField mf = getMetaField(getIndex(elChild.getLocalName()));
          String sFieldTag = elChild.getLocalName();
          Field field = createField(iField,mf,elChild);
          getFieldMap().put(sFieldTag,field);
          iField++;
        }
      }
    }
    _elValue = elValue;
  } /* setValue */

  /*------------------------------------------------------------------*/
  /** get the DOM element for this cell's or field's value.
   * @return DOM value.
   * @throws IOException if an I/O error occurred.
   */
  public Element getValue()
    throws IOException
  {
    if (getFieldMap() != null)
    {
      if (getMetaFields() > 0)
        XU.clearElement(getValueElement());
      /* append fields */
      int iCardinality = getCardinality();
      MetaType mt = getMetaType();
      for (int iField = 0; iField < getMetaFields(); iField++)
      {
        String sTag = getTag(mt, iCardinality, iField);
        Field field = getFieldMap().get(sTag);
        if ((field != null) && (!field.isNull()))
        {
          Element elField = ((FieldImpl)field).getValue();
          if (elField != null)
            getValueElement().appendChild(elField);
        }
      }
    }
    return _elValue;
  } /* getValue */
  
  /*------------------------------------------------------------------*/
  /** to be called in constructor of CellImpl and FieldImpl.
   * @param lRecord row number of cell.
   * @param uriTemporaryLobFolder URI of temporary folder for LOBs.
   * @param iIndex index of value in parent cell or field 
   *   (is needed, because element can be null).
   * @param cell parent cell if this is the value of a cell.
   * @param elValue DOM element representing the value or null for NULL
   *   value.
   * @param mv meta data (column meta data for cell value, field meta data
   *   for field value). 
   */
  protected void initialize(long lRecord, URI uriTemporaryLobFolder, int iIndex, Element elValue, MetaValue mv)
    throws IOException
  {
    _lRecord = lRecord;
    _uriTemporaryLobFolder = uriTemporaryLobFolder;
    _iIndex = iIndex;
    _mv = mv;
    setValue(elValue);
    if (elValue != null)
    {
      int iCardinality = mv.getCardinality();
      if (iCardinality > 0)
      {
        /* get the last element child */
        Element elChild = null;
        for (Node nodeChild = elValue.getLastChild(); (elChild == null) && (nodeChild != null); nodeChild = nodeChild.getPreviousSibling())
        {
          if (nodeChild.getNodeType() == Node.ELEMENT_NODE)
            elChild = (Element)nodeChild;
        }
        if (elChild != null)
        {
          /* its index */
          int iArrayIndex = getIndex(elChild.getTagName());
          /* create NULL fields for array */
          extendArray(iArrayIndex+1,iCardinality);
        }
      }
    }
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public boolean isNull()
  {
    return _elValue == null;
  } /* isNull */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public String getString()
    throws IOException
  {
    String s = null;
    if (!getValueElement().hasAttribute(ArchiveImpl._sATTR_FILE))
      s = XU.fromXml(getValueElement());
    else
    {
      Reader rdrClob = getReader();
      if (rdrClob != null)
      {
        StringWriter swr = new StringWriter();
        char[] cbufTransfer = new char[ArchiveImpl._iBUFFER_SIZE];
        for (int iRead = rdrClob.read(cbufTransfer); iRead != -1; iRead = rdrClob.read(cbufTransfer))
          swr.write(cbufTransfer,0,iRead);
        rdrClob.close();
        s = swr.getBuffer().toString();
      }
    }
    return s;
  } /* getString */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setString(String s)
    throws IOException
  {
    boolean bShort = (_mv.getMaxLength() <= getArchiveImpl().getMaxInlineSize());
    if (bShort)
      XU.toXml(s, getValueElement());
    else
    {
      StringReader srdr = new StringReader(s);
      setReader(srdr);
    }
  } /* setString */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public byte[] getBytes()
    throws IOException
  {
    byte[] buf = null;
    if (!getValueElement().hasAttribute(ArchiveImpl._sATTR_FILE))
      buf = BU.fromHex(getValueElement().getTextContent());
    else
    {
      InputStream isBlob = getInputStream();
      if (isBlob != null)
      {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] bufTransfer = new byte[ArchiveImpl._iBUFFER_SIZE];
        for (int iRead = isBlob.read(bufTransfer); iRead != -1; iRead = isBlob.read(bufTransfer))
          baos.write(bufTransfer,0,iRead);
        isBlob.close();
        buf = baos.toByteArray();
      }
    }
    return buf;
  } /* getBytes */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setBytes(byte[] buf)
    throws IOException
  {
    boolean bShort = (_mv.getMaxLength() <= getArchiveImpl().getMaxInlineSize());
    if (bShort)
      getValueElement().appendChild(getValueElement().getOwnerDocument().createTextNode(BU.toHex(buf)));
    else
    {
      ByteArrayInputStream bais = new ByteArrayInputStream(buf);
      setInputStream(bais);
    }
  } /* setBytes */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Boolean getBoolean()
    throws IOException
  {
    Boolean b = null;
    if (!isNull())
    {
      int iPreType = getPreType();
      if (iPreType == Types.BOOLEAN)
        b = Boolean.valueOf(Boolean.parseBoolean(getValueElement().getTextContent()));
      else if (iPreType != Types.NULL)
        throw new IllegalArgumentException("Cell of type "+SqlTypes.getTypeName(iPreType)+" cannot be converted to boolean!");
      else      
        throw new IllegalArgumentException("Value of cell of complex type cannot be converted to boolean!");
    }
    return b;
  } /* getBoolean */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setBoolean(boolean b)
    throws IOException
  {
    int iPreType = getPreType();
    if (iPreType == Types.BOOLEAN)
      getValueElement().setTextContent(String.valueOf(b));
    else if (iPreType != Types.NULL)
      throw new IllegalArgumentException("Cell of type "+SqlTypes.getTypeName(iPreType)+" cannot be set to boolean value!");
    else
      throw new IllegalArgumentException("Value of cell of complex type cannot be set to boolean!");
  } /* setBoolean */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Short getShort()
    throws IOException
  {
    Short sh = null;
    if (!isNull())
    {
      int iPreType = getPreType();
      if ((iPreType == Types.SMALLINT) || 
          (iPreType == Types.INTEGER) || 
          (iPreType == Types.BIGINT))
        sh = Short.valueOf(Short.parseShort(getValueElement().getTextContent()));
      else if (iPreType != Types.NULL)
        throw new IllegalArgumentException("Cell of type "+SqlTypes.getTypeName(iPreType)+" cannot be converted to short!");
      else
        throw new IllegalArgumentException("Value of cell of complex type cannot be converted to short!");
    }
    return sh;
  } /* getShort */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setShort(short sh)
    throws IOException
  {
    int iPreType = getPreType();
    if ((iPreType == Types.SMALLINT) || 
        (iPreType == Types.INTEGER) || 
        (iPreType == Types.BIGINT))
      getValueElement().setTextContent(String.valueOf(sh));
    else if (iPreType != Types.NULL)
      throw new IllegalArgumentException("Cell of type "+SqlTypes.getTypeName(iPreType)+" cannot be set to short value!");
    else
      throw new IllegalArgumentException("Value of cell of complex type cannot be set to short!");
  } /* setShort */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Integer getInt()
    throws IOException
  {
    Integer i = null;
    if (!isNull())
    {
      int iPreType = getPreType();
      if ((iPreType == Types.SMALLINT) || 
          (iPreType == Types.INTEGER) || 
          (iPreType == Types.BIGINT))
        i = Integer.valueOf(Integer.parseInt(getValueElement().getTextContent()));
      else if (iPreType != Types.NULL)
        throw new IllegalArgumentException("Cell of type "+SqlTypes.getTypeName(iPreType)+" cannot be converted to int!");
      else
        throw new IllegalArgumentException("Value of cell of complex type cannot be converted to int!");
    }
    return i;
  } /* getInt */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setInt(int i)
    throws IOException
  {
    int iPreType = getPreType();
    if ((iPreType == Types.SMALLINT) || 
        (iPreType == Types.INTEGER) || 
        (iPreType == Types.BIGINT))
      getValueElement().setTextContent(String.valueOf(i));
    else if (iPreType != Types.NULL)
      throw new IllegalArgumentException("Cell of type "+SqlTypes.getTypeName(iPreType)+" cannot be set to int value!");
    else
      throw new IllegalArgumentException("Value of cell of complex type cannot be set to int!");
  } /* setInt */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Long getLong()
    throws IOException
  {
    Long l = null;
    if (!isNull())
    {
      int iPreType = getPreType();
      if ((iPreType == Types.SMALLINT) || 
          (iPreType == Types.INTEGER) || 
          (iPreType == Types.BIGINT))
        l = Long.valueOf(Long.parseLong(getValueElement().getTextContent()));
      else if (iPreType != Types.NULL)
        throw new IllegalArgumentException("Cell of type "+SqlTypes.getTypeName(iPreType)+" cannot be converted to int!");
      else
        throw new IllegalArgumentException("Value of cell of complex type cannot be converted to int!");
    }
    return l;
  } /* getLong */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setLong(long l)
    throws IOException
  {
    int iPreType = getPreType();
    if ((iPreType == Types.SMALLINT) || 
        (iPreType == Types.INTEGER) || 
        (iPreType == Types.BIGINT))
      getValueElement().setTextContent(String.valueOf(l));
    else if (iPreType != Types.NULL)
      throw new IllegalArgumentException("Cell of type "+SqlTypes.getTypeName(iPreType)+" cannot be set to int value!");
    else
      throw new IllegalArgumentException("Value of cell of complex type cannot be set to int!");
  } /* setLong */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public BigInteger getBigInteger()
    throws IOException
  {
    BigInteger bi = null;
    if (!isNull())
    {
      int iPreType = getPreType();
      if ((iPreType == Types.SMALLINT) || 
        (iPreType == Types.INTEGER) || 
        (iPreType == Types.BIGINT))
        bi = new BigInteger(getValueElement().getTextContent());
      else if (iPreType != Types.NULL)
        throw new IllegalArgumentException("Cell of type "+SqlTypes.getTypeName(iPreType)+" cannot be converted to BigInteger!");
      else
        throw new IllegalArgumentException("Value of cell of complex type cannot be converted to BigInteger!");
    }
    return bi;
  } /* getBigInteger */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setBigInteger(BigInteger bi)
    throws IOException
  {
    int iPreType = getPreType();
    if ((iPreType == Types.SMALLINT) || 
      (iPreType == Types.INTEGER) || 
      (iPreType == Types.BIGINT))
      getValueElement().setTextContent(bi.toString());
    else if (iPreType != Types.NULL)
      throw new IllegalArgumentException("Cell of type "+SqlTypes.getTypeName(iPreType)+" cannot be set to BigInteger value!");
    else
      throw new IllegalArgumentException("Value of cell of complex type cannot be set to BigInteger!");
  } /* setBigInteger */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public BigDecimal getBigDecimal()
    throws IOException
  {
    BigDecimal bd = null;
    if (!isNull())
    {
      int iPreType = getPreType();
      if ((iPreType == Types.DECIMAL) || 
          (iPreType == Types.NUMERIC) ||
          (iPreType == Types.SMALLINT) || 
          (iPreType == Types.INTEGER) || 
          (iPreType == Types.BIGINT) ||
          (iPreType == Types.FLOAT) || 
          (iPreType == Types.REAL) || 
          (iPreType == Types.DOUBLE))
        bd = new BigDecimal(getValueElement().getTextContent());
      else if (iPreType != Types.NULL)
        throw new IllegalArgumentException("Cell of type "+SqlTypes.getTypeName(iPreType)+" cannot be converted to BigDecimal!");
      else
        throw new IllegalArgumentException("Value of cell of complex type cannot be converted to BigDecimal!");
    }
    return bd;
  } /* getBigDecimal */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setBigDecimal(BigDecimal bd)
    throws IOException
  {
    int iPreType = getPreType();
    if ((iPreType == Types.DECIMAL) || 
        (iPreType == Types.NUMERIC) || 
        (iPreType == Types.SMALLINT) || 
        (iPreType == Types.INTEGER) || 
        (iPreType == Types.BIGINT) ||
        (iPreType == Types.FLOAT) || 
        (iPreType == Types.REAL) || 
        (iPreType == Types.DOUBLE))
    {
      /* avoid scientific notation and confusion with approximate float/double */ 
      String s = bd.toPlainString(); 
      try
      {
        long l = bd.longValueExact();
        s = String.valueOf(l);
      }
      catch(ArithmeticException ae) {} // is thrown, if exact conversion is impossible
      getValueElement().setTextContent(s);
    }
    else if (iPreType != Types.NULL)
      throw new IllegalArgumentException("Cell of type "+SqlTypes.getTypeName(iPreType)+" cannot be set to BigDecimal value!");
    else
      throw new IllegalArgumentException("Value of cell of complex type cannot be set to BigDecimal!");
  } /* setBigDecimal */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Float getFloat()
    throws IOException
  {
    Float f = null;
    if (!isNull())
    {
      int iPreType = getPreType();
      if ((iPreType == Types.REAL) || 
          (iPreType == Types.DOUBLE) || 
          (iPreType == Types.FLOAT))
        f = Float.valueOf(Float.parseFloat(getValueElement().getTextContent()));
      else if (iPreType != Types.NULL)
        throw new IllegalArgumentException("Cell of type "+SqlTypes.getTypeName(iPreType)+" cannot be converted to float!");
      else
        throw new IllegalArgumentException("Value of cell of complex type cannot be converted to float!");
    }
    return f;
  } /* getFloat */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setFloat(float f)
    throws IOException
  {
    int iPreType = getPreType();
    if ((iPreType == Types.REAL) || 
        (iPreType == Types.DOUBLE) || 
        (iPreType == Types.FLOAT))
      getValueElement().setTextContent(String.valueOf(f));
    else if (iPreType != Types.NULL)
      throw new IllegalArgumentException("Cell of type "+SqlTypes.getTypeName(iPreType)+" cannot be set to float value!");
    else
      throw new IllegalArgumentException("Value of cell of complex type cannot be set to float!");
  } /* setFloat */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Double getDouble()
    throws IOException
  {
    Double d = null;
    if (!isNull())
    {
      int iPreType = getPreType();
      if ((iPreType == Types.REAL) || 
          (iPreType == Types.FLOAT) || 
          (iPreType == Types.DOUBLE))
        d = Double.valueOf(Double.parseDouble(getValueElement().getTextContent()));
      else if (iPreType != Types.NULL)
        throw new IllegalArgumentException("Cell of type "+SqlTypes.getTypeName(iPreType)+" cannot be converted to double!");
      else
        throw new IllegalArgumentException("Value of cell of complex type cannot be converted to double!");
    }
    return d;
  } /* getDouble */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setDouble(double d)
    throws IOException
  {
    int iPreType = getPreType();
    if ((iPreType == Types.REAL) || 
        (iPreType == Types.DOUBLE) || 
        (iPreType == Types.FLOAT))
      getValueElement().setTextContent(String.valueOf(d));
    else if (iPreType != Types.NULL)
      throw new IllegalArgumentException("Cell of type "+SqlTypes.getTypeName(iPreType)+" cannot be set to double value!");
    else
      throw new IllegalArgumentException("Value of cell of complex type cannot be set to double!");
  } /* setDouble */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Date getDate()
    throws IOException
  {
    Date date = null;
    if (!isNull())
    {
      int iPreType = getPreType();
      if (iPreType == Types.DATE)
      {
        try { date = _du.fromXsDate(getValueElement().getTextContent()); }
        catch(ParseException pe) { throw new IllegalArgumentException("Cell value "+getValueElement().getTextContent()+" could not be parsed as xs:date!",pe); }
      }
      else if (iPreType != Types.NULL)
        throw new IllegalArgumentException("Cell of type "+SqlTypes.getTypeName(iPreType)+" cannot be converted to date!");
      else
        throw new IllegalArgumentException("Value of cell of complex type cannot be converted to date!");
    }
    return date;
  } /* getDate */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setDate(Date date)
    throws IOException
  {
    int iPreType = getPreType();
    if (iPreType == Types.DATE)
      getValueElement().setTextContent(_du.toXsDate(date));
    else if (iPreType != Types.NULL)
      throw new IllegalArgumentException("Cell of type "+SqlTypes.getTypeName(iPreType)+" cannot be set to date value!");
    else
      throw new IllegalArgumentException("Value of cell of complex type cannot be set to date!");
  } /* setDate */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Time getTime()
    throws IOException
  {
    Time time = null;
    if (!isNull())
    {
      int iPreType = getPreType();
      if (iPreType == Types.TIME)
      {
        try { time = _du.fromXsTime(getValueElement().getTextContent()); }
        catch(ParseException pe) { throw new IllegalArgumentException("Cell value "+getValueElement().getTextContent()+" could not be parsed as xs:time!",pe); }
      }
      else if (iPreType != Types.NULL)
        throw new IllegalArgumentException("Cell of type "+SqlTypes.getTypeName(iPreType)+" cannot be converted to time!");
      else
        throw new IllegalArgumentException("Value of cell of complex type cannot be converted to time!");
    }
    return time;
  } /* getTime */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setTime(Time time)
    throws IOException
  {
    int iPreType = getPreType();
    if (iPreType == Types.TIME)
      getValueElement().setTextContent(_du.toXsTime(time));
    else if (iPreType != Types.NULL)
      throw new IllegalArgumentException("Cell of type "+SqlTypes.getTypeName(iPreType)+" cannot be set to time value!");
    else
      throw new IllegalArgumentException("Value of cell of complex type cannot be set to time!");
  } /* setTime */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Timestamp getTimestamp()
    throws IOException
  {
    Timestamp ts = null;
    if (!isNull())
    {
      int iPreType = getPreType();
      if (iPreType == Types.TIMESTAMP)
      {
        try { ts = _du.fromXsDateTime(getValueElement().getTextContent()); }
        catch(ParseException pe) { throw new IllegalArgumentException("Cell value "+getValueElement().getTextContent()+" could not be parsed as xs:dateTime!",pe); }
      }
      else if (iPreType != Types.NULL)
        throw new IllegalArgumentException("Cell of type "+SqlTypes.getTypeName(iPreType)+" cannot be converted to timestamp!");
      else
        throw new IllegalArgumentException("Value of cell of complex type cannot be converted to timestamp!");
    }
    return ts;
  } /* getTimestamp */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setTimestamp(Timestamp ts)
    throws IOException
  {
    int iPreType = getPreType();
    if (iPreType == Types.TIMESTAMP)
      getValueElement().setTextContent(_du.toXsDateTime(ts));
    else if (iPreType != Types.NULL)
      throw new IllegalArgumentException("Cell of type "+SqlTypes.getTypeName(iPreType)+" cannot be set to timestamp value!");
    else
      throw new IllegalArgumentException("Value of cell of complex type cannot be set to timestamp!");
  } /* setTimestamp */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Duration getDuration()
    throws IOException
  {
    Duration duration = null;
    if (!isNull())
    {
      int iPreType = getPreType();
      if (iPreType == Types.OTHER)
      {
        try { duration = _du.fromXsDuration(getValueElement().getTextContent()); }
        catch(ParseException pe) { throw new IllegalArgumentException("Cell value "+getValueElement().getTextContent()+" could not be parsed as xs:duration!",pe); }
      }
      else if (iPreType != Types.NULL)
        throw new IllegalArgumentException("Cell of type "+SqlTypes.getTypeName(iPreType)+" cannot be converted to duration!");
      else
        throw new IllegalArgumentException("Value of cell of complex type cannot be converted to duration!");
    }
    return duration;
  } /* getDuration */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setDuration(Duration duration)
    throws IOException
  {
    int iPreType = getPreType();
    if (iPreType == Types.OTHER)
      getValueElement().setTextContent(_du.toXsDuration(duration));
    else if (iPreType != Types.NULL)
      throw new IllegalArgumentException("Cell of type "+SqlTypes.getTypeName(iPreType)+" cannot be set to duration value!");
    else
      throw new IllegalArgumentException("Value of cell of complex type cannot be set to duration!");
  } /* setDuration */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Reader getReader()
    throws IOException
  {
    Reader rdrClob = null;
    if (!isNull())
    {
      int iPreType = getPreType();
      if ((iPreType == Types.CHAR) ||
          (iPreType == Types.VARCHAR) ||
          (iPreType == Types.CLOB) ||
          (iPreType == Types.NCHAR) ||
          (iPreType == Types.NVARCHAR) ||
          (iPreType == Types.NCLOB) ||
          (iPreType == Types.SQLXML))
      {
        InputStream isLob = null;
        if (getValueElement().hasAttribute(ArchiveImpl._sATTR_FILE))
        {
          String sLobFile = getValueElement().getAttribute(ArchiveImpl._sATTR_FILE);
          /* file is either internal or external */
          URI uriExternalLobFolder = getAbsoluteLobFolder();
          if (uriExternalLobFolder == null)
            isLob = getArchiveImpl().openFileEntry(sLobFile);
          else
          {
            URI uriExternal = uriExternalLobFolder.resolve(sLobFile);
            isLob = new FileInputStream(FU.fromUri(uriExternal));
          }
        }
        rdrClob = new ValidatingReader(getValueElement(),isLob);
      }
      else if (iPreType != Types.NULL)
        throw new IllegalArgumentException("Cell of type "+SqlTypes.getTypeName(iPreType)+" cannot be read from input stream!");
      else
        throw new IllegalArgumentException("Value of cell of complex type cannot be read from input stream!");
    }
    return rdrClob;
  } /* getReader */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public long getCharLength()
    throws IOException
  {
    long lCharLength = -1;
    if (!isNull())
    {
      int iPreType = getPreType();
      if ((iPreType == Types.CHAR) ||
          (iPreType == Types.VARCHAR) ||
          (iPreType == Types.CLOB) ||
          (iPreType == Types.NCHAR) ||
          (iPreType == Types.NVARCHAR) ||
          (iPreType == Types.NCLOB) ||
          (iPreType == Types.SQLXML))
      {
        String sLength = getValueElement().getAttribute(ArchiveImpl._sATTR_LENGTH);
        if ((sLength != null) && (sLength.length() > 0))
          lCharLength = Long.parseLong(sLength);
        else
          lCharLength = getString().length();
      }        
      else
        lCharLength = Long.MIN_VALUE;
    }
    return lCharLength;
  } /* getCharLength */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setReader(Reader rdrClob)
    throws IOException
  {
    int iPreType = getPreType();
    if ((iPreType == Types.CHAR) ||
        (iPreType == Types.VARCHAR) ||
        (iPreType == Types.CLOB) ||
        (iPreType == Types.NCHAR) ||
        (iPreType == Types.NVARCHAR) ||
        (iPreType == Types.NCLOB) ||
        (iPreType == Types.SQLXML))
    {
      OutputStream osClob = null;
      int iMaxInlineSize = getArchiveImpl().getMaxInlineSize();
      /* try to read iMaxInLineSize+1 characters */
      char[] cbufPrefix = new char[iMaxInlineSize+1];
      int iOffset = 0;
      int iRead = -1;
      for (iRead = rdrClob.read(cbufPrefix); 
           (iOffset < cbufPrefix.length) && (iRead != -1); 
           iRead = rdrClob.read(cbufPrefix,iOffset,cbufPrefix.length-iOffset))
        iOffset = iOffset + iRead;
      String sLobFile = getLobFilename();
      URI uriExternalLobFolder = getAbsoluteLobFolder();
      File fileLob = null;
      if (uriExternalLobFolder == null)
      {
        sLobFile = getInternalLobFolder() + sLobFile;
        URI uriTemporaryLobFolder = getTemporaryLobFolder();
        fileLob = FU.fromUri(uriTemporaryLobFolder.resolve(sLobFile));
        sLobFile = getTableImpl().getTableFolder() + sLobFile;
      }
      else
      {
        int iMaxLobsPerFolder = getArchiveImpl().getMaxLobsPerFolder();
        if ((iMaxLobsPerFolder > 0) && (getTableImpl().getMetaTable().getRows() > iMaxLobsPerFolder))
        {
          long lSequence = getRecord() / getArchiveImpl().getMaxLobsPerFolder();
          sLobFile = _sSEQUENCE_PREFIX+String.valueOf(lSequence) + File.separator + sLobFile;
        }
        URI uriExternal = uriExternalLobFolder.resolve(sLobFile);
        fileLob = FU.fromUri(uriExternal);
      }
      if (!fileLob.getParentFile().exists())
        fileLob.getParentFile().mkdirs();
      osClob = new FileOutputStream(fileLob);
      getValueElement().setAttribute(ArchiveImpl._sATTR_FILE, sLobFile);
      Writer wrClob = new ValidatingWriter(getValueElement(), osClob);
      wrClob.write(cbufPrefix,0,iOffset);
      if (iRead != -1)
      {
        char[] cbufTransfer = new char[ArchiveImpl._iBUFFER_SIZE];
        for (iRead = rdrClob.read(cbufTransfer); iRead != -1; iRead = rdrClob.read(cbufTransfer))
          wrClob.write(cbufTransfer,0,iRead);
      }
      wrClob.close(); // sets LENGTH and DIGEST
      rdrClob.close();
    }
    else if (iPreType != Types.NULL)
      throw new IllegalArgumentException("Cell of type "+SqlTypes.getTypeName(iPreType)+" cannot be set using a reader!");
    else
      throw new IllegalArgumentException("Value of cell of complex type cannot be set using a reader!");
  } /* setReader */

  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public String getFilename()
    throws IOException
  {
    String sFilename = null;
    if (getValueElement().hasAttribute(ArchiveImpl._sATTR_FILE))
      sFilename = getValueElement().getAttribute(ArchiveImpl._sATTR_FILE);
    return sFilename;
  } /* getFilename */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public InputStream getInputStream()
    throws IOException
  {
    InputStream isLob = null;
    if (!isNull())
    {
      int iPreType = getPreType();
      if ((iPreType == Types.BINARY) || 
          (iPreType == Types.VARBINARY) ||
          (iPreType == Types.BLOB))
      {
        if (getValueElement().hasAttribute(ArchiveImpl._sATTR_FILE))
        {
          String sLobFile = getValueElement().getAttribute(ArchiveImpl._sATTR_FILE);
          /* file is either internal or external */
          URI uriExternalLobFolder = getAbsoluteLobFolder();
          if (uriExternalLobFolder == null)
            isLob = getArchiveImpl().openFileEntry(sLobFile);
          else
          {
            URI uriExternal = uriExternalLobFolder.resolve(sLobFile);
            isLob = new FileInputStream(FU.fromUri(uriExternal));
          }
        }
        isLob = new ValidatingInputStream(getValueElement(),isLob);
      }
      else if (iPreType != Types.NULL)
        throw new IllegalArgumentException("Cell of type "+SqlTypes.getTypeName(iPreType)+" cannot be read from input stream!");
      else
        throw new IllegalArgumentException("Value of cell of complex type cannot be read from input stream!");
    }
    return isLob;
  } /* getInputStream */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public long getByteLength()
    throws IOException
  {
    long lByteLength = -1;
    if (!isNull())
    {
      int iPreType = getPreType();
      if ((iPreType == Types.BINARY) || 
          (iPreType == Types.VARBINARY) ||
          (iPreType == Types.BLOB))
      {
        String sLength = getValueElement().getAttribute(ArchiveImpl._sATTR_LENGTH);
        if ((sLength != null) && (sLength.length() > 0))
          lByteLength = Long.parseLong(sLength);
        else
          lByteLength = getBytes().length;
      }        
      else
        lByteLength = Long.MIN_VALUE;
    }
    return lByteLength;
  } /* getByteLength */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setInputStream(InputStream isBlob)
    throws IOException
  {
    int iPreType = getPreType();
    if ((iPreType == Types.BINARY) || 
        (iPreType == Types.VARBINARY) ||
        (iPreType == Types.BLOB))
    {
      OutputStream osBlob = null;
      int iMaxInlineSize = getArchiveImpl().getMaxInlineSize();
      /* try to read 1 + iMaxInLineSize bytes */
      byte[] bufPrefix = new byte[1+iMaxInlineSize];
      int iOffset = 0;
      int iRead = -1;
      for(iRead = isBlob.read(bufPrefix); 
          (iOffset < bufPrefix.length) && (iRead != -1); 
          iRead = isBlob.read(bufPrefix,iOffset,bufPrefix.length-iOffset))
        iOffset = iOffset + iRead;
      String sLobFile = getLobFilename();
      URI uriExternalLobFolder = getAbsoluteLobFolder();
      File fileLob = null;
      if (uriExternalLobFolder == null)
      {
        sLobFile = getInternalLobFolder() + sLobFile;
        URI uriTemporaryLobFolder = getTemporaryLobFolder();
        fileLob = FU.fromUri(uriTemporaryLobFolder.resolve(sLobFile));
        sLobFile = getTableImpl().getTableFolder() + sLobFile;
      }
      else
      {
        int iMaxLobsPerFolder = getArchiveImpl().getMaxLobsPerFolder();
        if ((iMaxLobsPerFolder > 0) && (getTableImpl().getMetaTable().getRows() > iMaxLobsPerFolder))
        {
          long lSequence = getRecord() / getArchiveImpl().getMaxLobsPerFolder();
          sLobFile = _sSEQUENCE_PREFIX+String.valueOf(lSequence) + File.separator + sLobFile;
        }
        URI uriExternal = uriExternalLobFolder.resolve(sLobFile);
        fileLob = FU.fromUri(uriExternal);
      }
      if (!fileLob.getParentFile().exists())
        fileLob.getParentFile().mkdirs();
      osBlob = new FileOutputStream(fileLob);
      getValueElement().setAttribute(ArchiveImpl._sATTR_FILE, sLobFile);
      osBlob = new ValidatingOutputStream(getValueElement(), osBlob);
      osBlob.write(bufPrefix,0,iOffset);
      if (iRead != -1)
      {
        byte[] bufTransfer = new byte[ArchiveImpl._iBUFFER_SIZE];
        for (iRead = isBlob.read(bufTransfer); iRead != -1; iRead = isBlob.read(bufTransfer))
          osBlob.write(bufTransfer,0,iRead);
      }
      osBlob.close();
      isBlob.close();
    }
    else if (iPreType != Types.NULL)
      throw new IllegalArgumentException("Cell of type "+SqlTypes.getTypeName(iPreType)+" cannot be set using an input stream!");
    else
      throw new IllegalArgumentException("Value of cell of complex type cannot be set using an input stream!");
  } /* setInputStream */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public int getElements()
    throws IOException
  {
    int iElements = 0;
    int iCardinality = getCardinality();
    if (iCardinality >= 0)
      iElements = getMetaValue().getMetaFields();
    return iElements;
  } /* getElements */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Field getElement(int iElement)
    throws IOException
  {
    Field field = null;
    int iCardinality = getCardinality();
    if (iCardinality >= 0)
    {
      /* extend field map of array array with NULL fields */
      extendArray(iElement+1,iCardinality);
      field = getFieldMap().get(getElementTag(iElement));
    }
    else
      throw new IllegalArgumentException("Cell or field is not an ARRAY!");
    return field;
  } /* getElement */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public int getAttributes()
    throws IOException
  {
    int iAttributes = 0;
    MetaType mt = getMetaType();
    CategoryType cat = null;
    if (mt != null)
      cat = mt.getCategoryType();
    if (cat == CategoryType.UDT)
      iAttributes = getFieldMap().size();
    return iAttributes;
  } /* getAttributes */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Field getAttribute(int iAttribute)
    throws IOException
  {
    Field field = null;
    MetaType mt = getMetaType();
    CategoryType cat = null;
    if (mt != null)
      cat = mt.getCategoryType();
    if (cat == CategoryType.UDT)
      field = getFieldMap().get(getAttributeTag(iAttribute));
    else
      throw new IllegalArgumentException("Cell or field is not a UDT!");
    return field;
  } /* getAttribute */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Object getObject()
    throws IOException
  {
    Object o = null;
    if (!isNull())
    {
      int iPreType = getPreType();
      if (iPreType != Types.NULL)
      {
        switch(iPreType)
        {
          case Types.CHAR:
          case Types.VARCHAR:
          case Types.NCHAR: 
          case Types.NVARCHAR:
            o = getString();
            break;
          case Types.CLOB:
          case Types.NCLOB:
          case Types.SQLXML:
            o = getReader();
            break;
          case Types.BINARY:
          case Types.VARBINARY:
            o = getBytes();
            break;
          case Types.BLOB:
            o = getInputStream();
            break;
          case Types.DECIMAL:
          case Types.NUMERIC:
            o = getBigDecimal();
            break;
          case Types.SMALLINT:
            o = getInt();
            break;
          case Types.INTEGER:
            o = getLong();
            break;
          case Types.BIGINT:
            o = getBigInteger();
            break;
          case Types.FLOAT:
          case Types.DOUBLE:
            o = getDouble();
            break;
          case Types.REAL:
            o = getFloat();
            break;
          case Types.BOOLEAN:
            o = getBoolean();
            break;
          case Types.DATE:
            o = getDate();
            break;
          case Types.TIME:
            o = getTime();
            break;
          case Types.TIMESTAMP:
            o = getTimestamp();
            break;
          case Types.OTHER:
            o = getDuration();
            break;
        }
      }
      else
        throw new IllegalArgumentException("Cell is a structured type!");
    }
    return o;
  } /* getObject */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public List<Value> getValues(boolean bSupportsArrays, boolean bSupportsUdts)
    throws IOException
  {
    List<Value> listValues = new ArrayList<Value>();
    if (!bSupportsArrays)
    {
      for (int iElement = 0; iElement < getElements(); iElement++)
        listValues.addAll(getElement(iElement).getValues(bSupportsArrays,bSupportsUdts));
    }
    if (!bSupportsUdts)
    {
      for (int iAttribute = 0; iAttribute < getAttributes(); iAttribute++)
        listValues.addAll(getAttribute(iAttribute).getValues(bSupportsArrays,bSupportsUdts));
    }
    if (listValues.size() == 0)
      listValues.add(this);
    return listValues;
  } /* getValues */

  private void dumpElement(Element el, String sIndent)
  {
    System.out.print("\r\n"+sIndent+el.getTagName()+":");
    int iElements = 0;
    for (int i = 0; i < el.getChildNodes().getLength();i++)
    {
      Node node = el.getChildNodes().item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE)
      {
        Element elChild = (Element)node;
        dumpElement(elChild,sIndent+"  ");
        iElements++;
      }
    }
    if (iElements == 0)
      System.out.println(" "+el.getTextContent());
  }
  public void dumpDom()
  {
    if (_elValue != null)
      dumpElement(_elValue,"");
    else
      System.out.println("null");
  } /* dumpDom */
} /* class ValueImpl */
