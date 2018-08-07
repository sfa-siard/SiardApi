/*======================================================================
RecordRetainer absorbs records of a table.
Application : SIARD 2.0
Description : RecordRetainer absorbs records of a table. 
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 31.08.2017, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api;

import java.io.*;

/*====================================================================*/
/** RecordRetainer absorbs records of a table.
 * @author Hartwig Thomas
 */
public interface RecordRetainer
{
  /*====================================================================
  methods
  ====================================================================*/
  /*------------------------------------------------------------------*/
  /** create an (empty) record with the current record number.
   * @return empty record
   * @throws IOException if an I/O error occurred.
   */
  public Record create() throws IOException;
  
  /*------------------------------------------------------------------*/
  /** write the next record.
   * @param record record to be retained.
   * @throws IOException if an I/O error occurred.
   */
  public void put(Record record) throws IOException;

  /*------------------------------------------------------------------*/
  /** close the Retainer.
   * @throws IOException if an I/O error occurred.
   */
  public void close() throws IOException;

  /*------------------------------------------------------------------*/
  /** get number of records already retained.
   * @return index of next record.
   */
  public long getPosition();
  
  /*------------------------------------------------------------------*/
  /** get byte count already written to XML.
   * @return byte count written to XML.
   */
  public long getByteCount();
  
} /* RecordRetainer */
