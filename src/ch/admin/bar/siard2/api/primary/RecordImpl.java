/*== RecordImpl.java ===================================================
RecordImpl implements the interface Record.
Application : SIARD 2.0
Description : RecordImpl implements the interface Record.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 05.07.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api.primary;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.api.Table;
import ch.admin.bar.siard2.api.meta.*;
import ch.admin.bar.siard2.api.generated.table.*;

/*====================================================================*/
/** RecordImpl implements the interface Record.
 @author Hartwig Thomas
 */
public class RecordImpl
  implements Record
{
  private static Document _doc = null;
  public static Document getDocument()
    throws IOException
  {
    if (_doc == null)
    {
      DocumentBuilder db = TableImpl.getDocumentBuilder();
      _doc = db.newDocument();
    }
    return _doc;
  } /* getDocument */

  private URI _uriTemporaryLobFolder = null;
  public URI getTemporaryLobFolder() { return _uriTemporaryLobFolder; }
  
  private ObjectFactory _of = new ObjectFactory();
  private Map<String,Cell> _mapCells = null;
  private Map<String,Cell> getCellMap()
    throws IOException
  {
    if (_mapCells == null)
    {
      _mapCells = new HashMap<String,Cell>();
      /* the cells that are null were not in the list */
      for (int iColumn = 0; iColumn < getParentTable().getMetaTable().getMetaColumns(); iColumn++)
      {
        MetaColumnImpl mci = (MetaColumnImpl)getParentTable().getMetaTable().getMetaColumn(iColumn);
        String sColumnTag = CellImpl.getColumnTag(iColumn);
        if (_mapCells.get(sColumnTag) == null)
          _mapCells.put(sColumnTag, CellImpl.newInstance(this,iColumn,mci, null));
      }
    }
    return _mapCells;
  }
  
  private Table _tableParent = null;
  private long _lRecord = -1;
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public long getRecord() { return _lRecord; }
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public Table getParentTable() { return _tableParent; }
  
  private RecordType _rt = null;
  private void setRecordType(RecordType rt)
    throws IOException
  {
    for (int iColumn = 0; iColumn < rt.getAny().size(); iColumn++)
    {
      Element elCell = (Element)rt.getAny().get(iColumn);
      String sTag = elCell.getLocalName();
      int iIndex = CellImpl.getIndex(elCell.getLocalName());
      MetaColumnImpl mc = (MetaColumnImpl)getParentTable().getMetaTable().getMetaColumn(iIndex);
      Cell cell = CellImpl.newInstance(this, iIndex, mc, elCell);
      getCellMap().put(sTag, cell);
    }
    _rt = rt;
  } /* setRecordType */
  
  RecordType getRecordType()
    throws IOException
  {
    _rt.getAny().clear();
    for (int iColumn = 0; iColumn < getParentTable().getMetaTable().getMetaColumns(); iColumn++)
    {
      Cell cell = getCellMap().get(CellImpl.getColumnTag(iColumn));
      if (!cell.isNull())
      {
        Element elCell = ((CellImpl)cell).getValue();
        if (elCell != null)
          _rt.getAny().add(elCell);
      }
    }
    return _rt;
  } /* getRecordType */
  
  /*------------------------------------------------------------------*/
  /** constructor for writing a record.
   * @param tableParent associated table instance.
   * @param lRecord row in table.
   * @param uriTemporaryLobFolder temporary folder for LOBs.
   */
  private RecordImpl(Table tableParent, long lRecord, URI uriTemporaryLobFolder)
    throws IOException
  {
    _tableParent = tableParent;
    _lRecord = lRecord;
    _uriTemporaryLobFolder = uriTemporaryLobFolder;
    setRecordType(_of.createRecordType());
  } /* constructor */
  
  /*------------------------------------------------------------------*/
  /** factory for writing a record.
   * @param tableParent associated table instance.
   * @param lRecord row in table.
   * @param uriTemporaryLobFolder temporary folder for LOBs.
   */
  public static Record newInstance(Table tableParent, long lRecord, URI uriTemporaryLobFolder)
    throws IOException
  {
    return new RecordImpl(tableParent, lRecord, uriTemporaryLobFolder);
  } /* factory */
  
  /*------------------------------------------------------------------*/
  /** constructor for reading a record.
   * @param tableParent associated table instance.
   * @param lRecord row in table.
   * @param rt record type filled with cell elements.
   */
  private RecordImpl(Table tableParent, long lRecord, RecordType rt)
    throws IOException
  {
    _tableParent = tableParent;
    _lRecord = lRecord;
    if (rt.getAny().size() > 0)
    {
      Element el = (Element)rt.getAny().get(0);
      if (el.getOwnerDocument() != getDocument())
        _doc = el.getOwnerDocument();
    }
    setRecordType(rt);
  } /* constructor */
  
  /*------------------------------------------------------------------*/
  /** factory for reading a record.
   * @param tableParent associated table instance.
   * @param lRecord row in table.
   * @param rt record type filled with cell elements.
   */
  public static Record newInstance(Table tableParent, long lRecord, RecordType rt)
    throws IOException
  {
    return new RecordImpl(tableParent,lRecord,rt);
  } /* factory */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public int getCells()
    throws IOException
  {
    return getCellMap().size();
  } /* getCells */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Cell getCell(int iCell)
    throws IOException
  {
    String sTag = CellImpl.getColumnTag(iCell);
    Cell cell = getCellMap().get(sTag);
    return cell;
  } /* getCell */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public List<Value> getValues(boolean bSupportsArrays, boolean bSupportsUdts)
    throws IOException
  {
    List<Value> listValues = new ArrayList<Value>();
    for (int iCell = 0; iCell < getCells(); iCell++)
      listValues.addAll(getCell(iCell).getValues(bSupportsArrays,bSupportsUdts));
    return listValues;
  } /* getValues */
  
} /* class RecordImpl */
