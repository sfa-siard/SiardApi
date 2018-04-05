/*======================================================================
RecordExtractImpl implements the RecordExtract interface. 
Application : SIARD 2.0
Description : RecordExtractImpl implements the RecordExtract interface. 
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 01.10.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api.primary;

import java.io.*;
import ch.admin.bar.siard2.api.*;

/*====================================================================*/
/** RecordExtractImpl implements the RecordExtract interface.
 * @author Hartwig Thomas
 */
public class RecordExtractImpl
  implements RecordExtract
{
  public static final String _sLABEL_ROWS = "rows";
  public static final String _sLABEL_ROW = "row";
  private static int _iMAX_RECORDS = 50;
  public static int getMaxRecords() { return _iMAX_RECORDS; }
  public static void setMaxRecords(int iMaxRecords) { _iMAX_RECORDS = iMaxRecords; }

  private Table _table = null;
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Table getTable() { return _table; }
  
  private long _lOffset = -1;
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public long getOffset() { return _lOffset; }
  
  private long _lDelta = -1;
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public long getDelta() { return _lDelta; }
  
  private RecordExtract _rsParent = null;
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public RecordExtract getParentRecordExtract() { return _rsParent; }
  
  private Record _recordOffset = null;
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Record getRecord() { return _recordOffset; }
  
  private RecordExtract[] _ars = null; 

  /*------------------------------------------------------------------*/
  /** construct a root record set of a table. 
   * @param table for root record set.
   */
  private RecordExtractImpl(Table table)
  {
    _table = table;
    _lDelta = 1;
    for (long lDelta = _lDelta; lDelta < table.getMetaTable().getRows(); lDelta = lDelta*_iMAX_RECORDS) 
      _lDelta = lDelta;
    _lOffset = 0;
  } /* constructor */
  
  /*------------------------------------------------------------------*/
  /** construct a record set in a record set. 
   * @param rsParent parent record set or null for root.
   * @param iRecordSet index of new record set in parent record set.
   * @param record record at offset.
   */
  private RecordExtractImpl(RecordExtract rsParent, int iRecordSet, Record recordOffset)
  {
    _rsParent = rsParent;
    _recordOffset = recordOffset;
    _table = rsParent.getTable();
    _lOffset = rsParent.getOffset() + iRecordSet*rsParent.getDelta();
    _lDelta = rsParent.getDelta() / _iMAX_RECORDS;
  } /* constructor */
  
  /*------------------------------------------------------------------*/
  /** factory for a root record set in a table.
   * @param table table instance containing all records.
   */
  public static RecordExtract newInstance(Table table)
  {
    return new RecordExtractImpl(table);
  } /* factory */

  /*------------------------------------------------------------------*/
  /** factory for a record set in a record set.
   * @param rsParent parent record set.
   * @param iRecordSet index of new record set in parent record set.
   */
  public static RecordExtract newInstance(RecordExtract rsParent, int iRecordSet, Record recordOffset)
  {
    return new RecordExtractImpl(rsParent,iRecordSet,recordOffset);
  } /* factory */

  /*------------------------------------------------------------------*/
  /** get the full number of records in and under this record set.
   * @return the full number of records in and under this record set.
   */
  private long getRecords()
  {
    long lRows = _table.getMetaTable().getRows();
    long lRecords = getRecordExtracts()*_lDelta;
    if (_lOffset + lRecords > lRows)
      lRecords = lRows - _lOffset;
    return lRecords;
  } /* getRecords */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public String getLabel()
  {
    String sLabel = null;
    if (_lDelta > 0)
    {
      sLabel = _sLABEL_ROWS;
      String sRows = String.valueOf(getTable().getMetaTable().getRows());
      if (getParentRecordExtract() != null)
      {
        String sOffset = String.valueOf(_lOffset);
        while(sOffset.length() < sRows.length())
          sOffset = "0"+sOffset;
        sLabel = _sLABEL_ROW+sOffset;
      }
      sLabel = sLabel + " ("+String.valueOf(getRecords()) + ")";
    }
    return sLabel;
  } /* getLabel */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public int getRecordExtracts()
  {
    int iRecordExtracts = 0;
    if (_ars == null)
    {
      if (_lDelta > 0)
      {
        long lMaxRows = _table.getMetaTable().getRows()-_lOffset;
        iRecordExtracts = _iMAX_RECORDS;
        if (iRecordExtracts*_lDelta > lMaxRows)
          iRecordExtracts = (int)((lMaxRows+_lDelta-1)/_lDelta);
      }
    }
    else
      iRecordExtracts = _ars.length;
    return iRecordExtracts;
  } /* getRecordExtracts */

  /*------------------------------------------------------------------*/
  /** load the records of this record set.
   * @throws IOException if an I/O error occurred.
   */
  private void loadRecordExtract()
    throws IOException
  {
    _ars = new RecordExtract[getRecordExtracts()];
    RecordDispenser rd = _table.openRecords();
    rd.skip(_lOffset);
    _ars[0] = RecordExtractImpl.newInstance(this, 0, rd.get());
    for (int iRecordSet = 1; iRecordSet < getRecordExtracts(); iRecordSet++)
    {
      rd.skip(_lDelta-1);
      _ars[iRecordSet] = RecordExtractImpl.newInstance(this, iRecordSet, rd.get());
    }
    rd.close();
  } /* loadRecordExtract */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public RecordExtract getRecordExtract(int iRecordExtract)
    throws IOException
  {
    if (_ars == null)
      loadRecordExtract();
    return _ars[iRecordExtract];
  } /* getRecordExtract */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc}
   * toString() returns the label of the record extract which is to be 
   * displayed as the label of the record extract node of the tree 
   * displaying the archive.   
   */
  @Override 
  public String toString()
  {
    return getLabel();
  }
} /* class RecordExtractImpl */
