/*== Record.java =======================================================
Record interface provides access to records of primary table data.
Application : SIARD 2.0
Description : Record interface provides access to records of primary table data.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 05.07.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api;

import java.io.*;
import java.util.List;

/*====================================================================*/
/** Record interface provides access to records of primary table data.
 @author Hartwig Thomas
 */
public interface Record
{
  /*------------------------------------------------------------------*/
  /** get table with which this Record instance is associated
   * @return table associated with this record. 
   */
  public Table getParentTable();
  
  /*------------------------------------------------------------------*/
  /** get row number (zero-based) of the current record.
   * @return row number of the current record. 
   */
  public long getRecord();
  
  /*====================================================================
  methods
  ====================================================================*/
  /*------------------------------------------------------------------*/
  /** get the number of cells of the record.
   * @return number of cells of the record.
   * @throws IOException if an I/O error occurred.
   */
  public int getCells()
    throws IOException;
  
  /*------------------------------------------------------------------*/
  /** get cell with given (0-based) index.
   * @param iCell index.
   * @return cell instance.
   * @throws IOException if an I/O error occurred.
   */
  public Cell getCell(int iCell)
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
  
} /* interface Record */
