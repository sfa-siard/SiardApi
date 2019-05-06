/*== TableImpl.java ========================================================
TableImpl implements the interface Table.
Application : SIARD 2.0
Description : TableImpl implements the interface Table.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 04.07.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api.primary;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import ch.enterag.utils.*;
import ch.enterag.utils.xml.*;
import ch.enterag.utils.background.*;
import ch.enterag.sqlparser.*;
import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.api.Table;
import ch.admin.bar.siard2.api.generated.*;
import ch.admin.bar.siard2.api.meta.*;

/*====================================================================*/
/** TableImpl implements the interface Table.
 @author Hartwig Thomas
 */
public class TableImpl
  extends SearchImpl
  implements Table
{
  public static final String _sTABLE_FOLDER_PREFIX = "table";
  private static final int iBUFFER_SIZE = 8192;
  private static final long lROWS_MAX_VALIDATE = 1024; // Long.MAX_VALUE;
  public static final String _sXML_NS = "xmlns";
  public static final String _sXSI_PREFIX = "xsi";
  public static final String _sTAG_SCHEMA_LOCATION = "schemaLocation";
  public static final String _sTAG_TABLE = "table";
  public static final String _sTAG_RECORD = "row";
  public static final String _sATTR_TABLE_VERSION = "version";
  private static final ch.admin.bar.siard2.api.generated.ObjectFactory _OF = new ch.admin.bar.siard2.api.generated.ObjectFactory();
  
  static DocumentBuilder _db = null;
  static DocumentBuilder getDocumentBuilder()
    throws IOException
  {
    try
    {
      if (_db == null)
      {
        /* avoid Oracle XML parsing and make outcomes predictable */
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance(
          com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl.class.getName(),
          null);
        dbf.setNamespaceAware(true);
        _db = dbf.newDocumentBuilder();
      }
    }
    catch(ParserConfigurationException pce) { throw new IOException("DocumentBuilder could not be created!",pce); }
    return _db;
  } /* getDocumentBuilder */
  private static Transformer _trans = null;
  private static Transformer getTransformer()
    throws IOException
  {
    try
    {
      if (_trans == null)
      {
        /* avoid Oracle XML parsing and make outcomes predictable */
        TransformerFactory tf = TransformerFactory.newInstance(
          com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl.class.getName(),
          null);
        _trans = tf.newTransformer();
        _trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        _trans.setOutputProperty(OutputKeys.METHOD, "xml");
        _trans.setOutputProperty(OutputKeys.INDENT, "yes");
        _trans.setOutputProperty(OutputKeys.ENCODING, SU.sUTF8_CHARSET_NAME);
        // _trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
      }
    }
    catch(TransformerConfigurationException tce) { throw new IOException("Transformer could not be created!",tce); }
    return _trans;
  } /* getTransformer */

  /** sorted, temporary table */
  private SortedTable _stable = null;
  public SortedTable getSortedTable() { return _stable; }
  
  private Schema _schemaParent = null;
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public Schema getParentSchema() { return _schemaParent; }

  private MetaTable _mt = null;
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public MetaTable getMetaTable() { return _mt; }

  private ArchiveImpl getArchiveImpl()
  {
    return (ArchiveImpl)getParentSchema().getParentArchive();
  } /* getArchive */
  
  /*------------------------------------------------------------------*/
  String getTableFolder()
  {
    return ((SchemaImpl)getParentSchema()).getSchemaFolder()+getMetaTable().getFolder()+"/";
  } /* getTableFolder */
  
  String getTableXsd()
  {
    return getTableFolder()+getMetaTable().getFolder()+".xsd";
  }

  String getTableXml()
  {
    return getTableFolder()+getMetaTable().getFolder()+".xml";
  }

  /*------------------------------------------------------------------*/
  /** add the <xs:element> meta data definition for the given tag and type
   * to the parent element (<xs:sequence>).
   * @param elParent parent element (<xs:sequence>).
   * @param sTag name of element.
   * @param mv column or field meta data to be represented by <xs:element>.
   * @param bNullable true, if element may be missing.
   * @throws IOException if an I/O error occurred.
   */
  private void addElement(Element elParent, String sTag, MetaValue mv, boolean bNullable)
    throws IOException
  {
    Document doc = elParent.getOwnerDocument();
    // Element elElement = doc.createElementNS(elParent.getNamespaceURI(),"xs:element");
    Element elElement = doc.createElement("xs:element");
    elParent.appendChild(elElement);
    elElement.setAttribute("name", sTag);
    int iPreType = mv.getPreType();
    if ((iPreType != Types.NULL) && (mv.getMetaFields() == 0))
    {
      boolean bShort = (mv.getMaxLength() <= getArchiveImpl().getMaxInlineSize());
      String sXmlType = null;
      switch (iPreType)
      {
        case Types.CHAR: sXmlType = bShort?"xs:string":"clobType"; break;
        case Types.VARCHAR: sXmlType = bShort?"xs:string":"clobType"; break;
        case Types.CLOB: sXmlType = "clobType"; break;
        case Types.NCHAR: sXmlType = bShort?"xs:string":"clobType"; break;
        case Types.NVARCHAR: sXmlType = bShort?"xs:string":"clobType"; break;
        case Types.NCLOB: sXmlType = "clobType"; break;
        case Types.BINARY: sXmlType = bShort?"xs:hexBinary":"blobType"; break;
        case Types.VARBINARY: sXmlType = bShort?"xs:hexBinary":"blobType"; break;
        case Types.BLOB: sXmlType = "blobType"; break;
        case Types.NUMERIC: sXmlType = "xs:decimal"; break;
        case Types.DECIMAL: sXmlType = "xs:decimal"; break;
        case Types.SMALLINT: sXmlType = "xs:integer"; break;
        case Types.INTEGER: sXmlType = "xs:integer"; break;
        case Types.BIGINT: sXmlType = "xs:integer"; break;
        case Types.FLOAT: sXmlType = "xs:double"; break;
        case Types.REAL: sXmlType = "xs:float"; break;
        case Types.DOUBLE: sXmlType = "xs:double"; break;
        case Types.BOOLEAN: sXmlType = "xs:boolean"; break;
        case Types.DATE: sXmlType = "dateType"; break;
        case Types.TIME: sXmlType = "timeType"; break;
        case Types.TIMESTAMP: sXmlType = "dateTimeType"; break;
        case Types.SQLXML: sXmlType = "clobType"; break;
        case Types.OTHER: sXmlType = "xs:duration"; break;
      }
      elElement.setAttribute("type", sXmlType);
      if (bNullable)
        elElement.setAttribute("minOccurs", "0");
    }
    else
    {
      elElement.setAttribute("minOccurs", "0");
      
      //Element elComplexType = doc.createElementNS(elElement.getNamespaceURI(),"xs:complexType");
      Element elComplexType = doc.createElement("xs:complexType");
      elElement.appendChild(elComplexType);
      
      //Element elSequence = doc.createElementNS(elComplexType.getNamespaceURI(),"xs:sequence");
      Element elSequence = doc.createElement("xs:sequence");
      elComplexType.appendChild(elSequence);
    
      for (int iField = 0; iField < mv.getMetaFields(); iField++)
      {
        MetaValue mvField = mv.getMetaField(iField);
        String sTagField = null;
        if (mv.getCardinality() > 0)
          sTagField = CellImpl.getElementTag(iField);
        else
        {
          CategoryType cat = mv.getMetaType().getCategoryType();
          if (cat == CategoryType.UDT)
            sTagField = CellImpl.getAttributeTag(iField);
        }
        addElement(elSequence, sTagField, mvField, true);
      }
    }
  } /* addElement */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void exportTableSchema(OutputStream osXsd)
    throws IOException
  {
    String sEntryName = getTableXsd(); 
    if (getArchiveImpl().existsFileEntry(sEntryName))
    {
      /* export existing old table XSD in old format! */
      byte[] buf = new byte[iBUFFER_SIZE];
      InputStream is = getArchiveImpl().openFileEntry(sEntryName);
      for (int iRead = is.read(buf); iRead != -1; iRead = is.read(buf))
        osXsd.write(buf,0,iRead);
      is.close();
    }
    else if (getMetaTable().getMetaColumns() > 0)
    {
      try
      {
        /* read table template into DOM */
        InputStream isXsdTable = ArchiveImpl.class.getResourceAsStream(Archive.sSIARD2_GENERIC_TABLE_XSD_RESOURCE);
        Document doc = getDocumentBuilder().parse(isXsdTable);
        /* edit DOM */
        Element elAny = (Element)doc.getElementsByTagName("xs:any").item(0);
        Element elSequence = (Element)elAny.getParentNode();
        XU.clearElement(elSequence);
        for (int iColumn = 0; iColumn < getMetaTable().getMetaColumns(); iColumn++)
        {
          MetaColumn mc = getMetaTable().getMetaColumn(iColumn);
          String sTag = CellImpl.getColumnTag(iColumn);
          addElement(elSequence, sTag, (MetaValue)mc, mc.isNullable());
        }
        /* write it to stream */
        getTransformer().transform(new DOMSource(doc), new StreamResult(osXsd));
        osXsd.close();
      }
      catch(SAXException se) { throw new IOException(se); }
      catch (TransformerConfigurationException tcfe) { throw new IOException(tcfe); }
      catch (TransformerException tfe) { throw new IOException(tfe); }
    }
    else
      throw new IOException("Table contains no columns!");
  } /* exportTableSchema */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public boolean isEmpty()
  {
    boolean bEmpty = true;
    ArchiveImpl ai = getArchiveImpl();
    if (ai.getZipFile().getFileEntry(getTableXml()) != null)
    	bEmpty = false;
    return bEmpty;
  } /* isEmpty */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public boolean isValid()
  {
    boolean bValid = getMetaTable().isValid();
    if (bValid && (getMetaTable().getMetaColumns() == 0))
      bValid = false;
    RecordDispenser rd = null;
    try
    {
      rd = openRecords();
      long lRowsValidate = Math.min(lROWS_MAX_VALIDATE, getMetaTable().getRows());
      rd.skip(lRowsValidate);
      if (lRowsValidate == getMetaTable().getRows())
        if (rd.get() != null)
          bValid = false;
    }
    catch(IOException ie) { bValid = false; }
    finally
    {
      if (rd != null)
      {
        try { rd.close(); }
        catch(IOException ie) { }
      }
    }
    return bValid;
  } /* isValid */
  
  /*------------------------------------------------------------------*/
  /** constructor for existing table
   * @param schemaParent schema to which this Table instance belongs.
   * @param sName name of table.
   * @throws IOException if the table cannot be created.
   */
  private TableImpl(Schema schemaParent, String sName)
    throws IOException
  {
    _schemaParent = schemaParent;
    MetaSchemaImpl msi = (MetaSchemaImpl)getParentSchema().getMetaSchema();
    TablesType tts = msi.getSchemaType().getTables();
    if (tts == null)
    {
      tts = _OF.createTablesType();
      msi.getSchemaType().setTables(tts);
    }
    TableType tt = null;
    for (int iTable = 0; (tt == null) && (iTable < tts.getTable().size()); iTable++)
    {
      TableType ttTry = tts.getTable().get(iTable);
      if (sName.equals(ttTry.getName()))
        tt = ttTry;
    }
    SchemaImpl si = ((SchemaImpl)getParentSchema());
    if (tt == null)
    {
      String sFolder = _sTABLE_FOLDER_PREFIX+String.valueOf(tts.getTable().size()); 
      ArchiveImpl ai = (ArchiveImpl)getArchiveImpl();
      ai.createFolderEntry(si.getSchemaFolder()+sFolder+"/");
      tt = MetaTableImpl.createTableType(sName,sFolder);
      tts.getTable().add(tt);
    }
    _mt = MetaTableImpl.newInstance(this, tt);
    si.registerTable(sName,this);
  } /* constructor TableImpl */

  /*------------------------------------------------------------------*/
  /** factory
   * @param schemaParent schema to which the Table instance belongs.
   * @param sName table name.
   * @return new Table instance.
   * @throws IOException if the table cannot be created.
   */
  public static Table newInstance(Schema schemaParent, String sName)
    throws IOException
  {
    Table table = new TableImpl(schemaParent, sName);
    return table;
  } /* newInstance */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public RecordDispenserImpl openRecords()
    throws IOException
  {
    return new RecordDispenserImpl(this);
  } /* openRecords */
  
  private boolean _bCreating = false;
  public boolean isCreating() { return _bCreating; }
  public void setCreating(boolean bCreating) { _bCreating = bCreating; }
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public RecordRetainerImpl createRecords()
    throws IOException
  {
    return new RecordRetainerImpl(this);
  } /* createRecords */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public RecordExtract getRecordExtract()
    throws IOException
  {
    RecordExtract re = null;
    /* open table XML */
    if (!getArchiveImpl().canModifyPrimaryData())
      re = RecordExtractImpl.newInstance(this);
    else
      throw new IOException("Records cannot be read if archive is open for modification!");
    return re;
  } /* getRecordExtract */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void sort(boolean bAscending, int iSortColumn, Progress progress)
    throws IOException
  {
    SortedTable stable = _stable;
    if (stable == null)
      stable = new SortedTableImpl();
    stable.sort(this, bAscending, iSortColumn,progress);
    _stable = stable;
  } /* sort */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public boolean getAscending()
  {
    boolean bAscending = true;
    if (_stable != null)
      bAscending = _stable.getAscending();
    return bAscending;
  } /* getAscending */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public int getSortColumn()
  {
    int iSortColumn = -1;
    if (_stable != null)
      iSortColumn = _stable.getSortColumn();
    return iSortColumn;
  } /* getSortColumn */

  /*------------------------------------------------------------------*/
  /** write the value as HTML to the table cell.
   * @param wr writer.
   * @param value value to be written.
   * @param folderLob root folder for internal LOBs in this table.
   * @param sFilename file name for LOB.
   * @throws IOException if an I/O error occurred.
   */
  private void writeLinkToLob(Writer wr, Value value, File folderLobs, String sFilename)
    throws IOException
  {
    URI uriAbsoluteFolder = value.getMetaValue().getAbsoluteLobFolder(); 
    if (uriAbsoluteFolder != null)
    {
      URI uriExternal = uriAbsoluteFolder.resolve(sFilename);
      sFilename = uriExternal.toURL().toString();
      /* leave external LOB file where it is */
    }
    else if (folderLobs != null)
    {
      
      sFilename = "/"+folderLobs.getAbsolutePath().replace('\\', '/')+"/"+sFilename;
      File fileLob = new File(sFilename);
      fileLob.getParentFile().mkdirs();
      /* copy internal LOB file to lobFolder */
      int iType = value.getMetaValue().getPreType();
      if ((iType == Types.BINARY) ||
        (iType == Types.VARBINARY) ||
        (iType == Types.BLOB))
      {
        InputStream is = value.getInputStream();
        if (is != null)
        {
          FileOutputStream fosLob = new FileOutputStream(sFilename);
          byte[] buf = new byte[iBUFFER_SIZE];
          for (int iRead = is.read(buf); iRead != -1; iRead = is.read(buf))
            fosLob.write(buf,0,iRead);
          fosLob.close();
          is.close();
        }
      }
      else
      {
        Reader rdr = value.getReader();
        if (rdr != null)
        {
          Writer fwLob = new FileWriter(fileLob);
          char[] cbuf = new char[iBUFFER_SIZE];
          for (int iRead = rdr.read(cbuf); iRead != -1; iRead = rdr.read(cbuf))
            fwLob.write(cbuf,0,iRead);
          fwLob.close();
          rdr.close();
        }
      }
    }
    /* write a link to the LOB file to HTML */
    wr.write("<a href=\""+sFilename+"\">"+sFilename+"</a>");
  } /* writeLinkToLob */

  /*------------------------------------------------------------------*/
  /** write the UDT value as a definition list.
   * @param wr writer.
   * @param value UDT value to be written.
   * @param folderLobs root folder for internal LOBs in this table.
   * @throws IOException if an I/O error occurred.
   */
  private void writeUdtValue(Writer wr, Value value, File folderLobs)
    throws IOException
  {
    wr.write("<dl>\r\n");
    MetaValue mv = value.getMetaValue();
    for (int iAttribute = 0; iAttribute < value.getAttributes(); iAttribute++)
    {
      MetaField mf = mv.getMetaField(iAttribute);
      wr.write("  <dt>");
      wr.write(SU.toHtml(mf.getName()));
      wr.write("</dt>\r\n");
      wr.write("  <dd>");
      writeValue(wr,value.getAttribute(iAttribute),folderLobs);
      wr.write("</dd>\r\n");
    }
    wr.write("</dl>\r\n");
  } /* writeUdtValue */
  
  /*------------------------------------------------------------------*/
  /** write the array value as an ordered list.
   * @param wr writer.
   * @param value array value to be written.
   * @param folderLobs root folder for internal LOBs in this table.
   * @throws IOException if an I/O error occurred.
   */
  private void writeArrayValue(Writer wr, Value value, File folderLobs)
    throws IOException
  {
    wr.write("<ol>\r\n");
    for (int iElement = 0; iElement < value.getElements(); iElement++)
    {
      wr.write("  <li>");
      writeValue(wr,value.getElement(iElement),folderLobs);
      wr.write("</li>\r\n");
    }
    wr.write("</ol>\r\n");
  } /* writeUdtValue */
  
  /*------------------------------------------------------------------*/
  /** write the value as HTML to the table cell.
   * @param wr writer.
   * @param value value to be written.
   * @param folderLobs root folder for internal LOBs in this table.
   * @throws IOException if an I/O error occurred.
   */
  private void writeValue(Writer wr, Value value, File folderLobs)
    throws IOException
  {
    DU du = DU.getInstance(Locale.getDefault().getLanguage(), (new SimpleDateFormat()).toPattern());
    if (!value.isNull())
    {
      // if it is a Lob with a file name then create a link
      String sFilename = value.getFilename();
      if (sFilename != null)
        writeLinkToLob(wr,value,folderLobs,sFilename);
      else
      {
        MetaValue mv = value.getMetaValue();
        MetaType mt = mv.getMetaType();
        if ((mt != null) && (mt.getCategoryType() == CategoryType.UDT))
          writeUdtValue(wr, value, folderLobs);
        else if (mv.getCardinality() > 0)
          writeArrayValue(wr, value, folderLobs);
        else
        {
          String sText = null;
          int iType = mv.getPreType();
          switch(iType)
          {
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.NCHAR:
            case Types.NVARCHAR:
            case Types.CLOB:
            case Types.NCLOB:
            case Types.SQLXML:
              sText = value.getString();
              break;
            case Types.BINARY:
            case Types.VARBINARY:
            case Types.BLOB:
              sText = "0x"+BU.toHex(value.getBytes());
              break;
            case Types.NUMERIC:
            case Types.DECIMAL:
              sText = value.getBigDecimal().toPlainString();
              break;
            case Types.SMALLINT:
              sText = value.getInt().toString();
              break;
            case Types.INTEGER:
              sText = value.getLong().toString();
              break;
            case Types.BIGINT:
              sText = value.getBigInteger().toString();
              break;
            case Types.FLOAT:
            case Types.DOUBLE:
              sText = value.getDouble().toString();
              break;
            case Types.REAL:
              sText = value.getFloat().toString();
              break;
            case Types.BOOLEAN:
              sText = value.getBoolean().toString();
              break;
            case Types.DATE:
              sText = du.fromSqlDate(value.getDate());
              break;
            case Types.TIME:
              sText = du.fromSqlTime(value.getTime());
              break;
            case Types.TIMESTAMP:
              sText = du.fromSqlTimestamp(value.getTimestamp());
              break;
            case Types.OTHER:
              sText = SqlLiterals.formatIntervalLiteral(Interval.fromDuration(value.getDuration()));
              break;
          }
          wr.write(SU.toHtml(sText));
        }
      }
    }
  } /* writeValue */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void exportAsHtml(OutputStream os, File folderLobs)
    throws IOException
  {
    OutputStreamWriter oswr = new OutputStreamWriter(os,SU.sUTF8_CHARSET_NAME);
    MetaTable mt = getMetaTable();
    oswr.write("<!DOCTYPE html>\r\n");
    oswr.write("<html lang=\"en\">\r\n");
    oswr.write("  <head>\r\n");
    oswr.write("    <title>"+SU.toHtml(mt.getName())+"</title>\r\n");
    oswr.write("    <meta charset=\"utf-8\" />\r\n");
    oswr.write("  </head>\r\n");
    oswr.write("  <body>\r\n");
    oswr.write("    <table>\r\n");
    oswr.write("      <tr>\r\n");
    for (int iColumn = 0; iColumn < mt.getMetaColumns(); iColumn++)
    {
      oswr.write("        <th>");
      oswr.write(SU.toHtml(mt.getMetaColumn(iColumn).getName()));
      oswr.write("</th>\r\n");
    }
    oswr.write("      </tr>\r\n");
    RecordDispenser rd = openRecords();
    for (long lRow = 0; lRow < getMetaTable().getRows(); lRow++)
    {
      oswr.write("      <tr>\r\n");
      Record record = rd.get();
      for (int iColumn = 0; iColumn < record.getCells(); iColumn++)
      {
        oswr.write("        <td>");
        Cell cell = record.getCell(iColumn);
        writeValue(oswr, cell, folderLobs);
        oswr.write("</td>\r\n");
      }
      oswr.write("      </tr>\r\n");
    }
    rd.close();
    oswr.write("    </table>\r\n");
    oswr.write("  </body>\r\n");
    oswr.write("</html>\r\n");
    oswr.flush();
  } /* exportAsHtml */

} /* class TableImpl */
