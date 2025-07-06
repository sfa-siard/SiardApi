/*== Table.java ========================================================
Table interface provides access to primary table data.
Application : SIARD 2.0
Description : Table interface provides access to primary table data.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 04.07.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api;

import java.io.*;
import ch.enterag.utils.background.*;

/*====================================================================*/
/** Table interface provides access to primary table data.
 @author Hartwig Thomas
 */
public interface Table
  extends Search
{
  /*------------------------------------------------------------------*/
  /** get schema with which this Table instance is associated
   * @return get schema associated with this table. 
   */
  public Schema getParentSchema();
  
  /*------------------------------------------------------------------*/
  /** return the associated table meta data describing this table instance.
   * @return associated table meta data.
   */
  public MetaTable getMetaTable();
  
  /*------------------------------------------------------------------*/
  /** export the table XSD.
   * @param osXsd table XSD is written here.
   * @throws IOException if an I/O error occurred.
   */
  public void exportTableSchema(OutputStream osXsd)
    throws IOException;
  
  /*------------------------------------------------------------------*/
  /** return true, if the table has at least one column.
   * @return true if the table has at least one column.
   */
  public boolean isValid();
  
  /*------------------------------------------------------------------*/
  /** check, if table is empty (contains no records).
   * @return true, if table is empty (contains no records).
   */
  public boolean isEmpty();

  /*------------------------------------------------------------------*/
  /** open a record inputs stream on the table.
   * N.B.: you can have multiple open input streams on a table.
   * @return record dispenser.
   * @throws IOException if archive is open for modification of primary
   * data. 
   */
  public RecordDispenser openRecords()
    throws IOException;
  
  /*------------------------------------------------------------------*/
  /** write the table XSD and create the XMLStreamWriter for writing the
   * records and create temporary LOB folders if necessary.
   * N.B.: Only one table can be open for record creation at a time!
   * @return record retainer.
   * @throws IOException if archive is not open for modification of primary
   * data. 
   */
  public RecordRetainer createRecords()
    throws IOException;
  
  /*------------------------------------------------------------------*/
  /** retrieve the root record set for this table.
   * @return the root record extract for this table.
   * @throws IOException if an I/O error occurred.
   */
  public RecordExtract getRecordExtract()
    throws IOException;
    
  /*------------------------------------------------------------------*/
  /** sorts the table data in the given direction by the data in the given 
   * column. 
   * The sorted table is stored in a temporary XML file which is
   * deleted when the JVM is finished.
   * Reading the table after the sort read from this temporary XML file 
   * rather than the original data in the SIARD file. 
   * @param bAscending true, if sort is by ascending values.
   * @param iSortColumn sort column (0-based)
   * @param progress progress interface for reporting progress, or null.
   * @throws IOException if an I/O error occurred.
   */
  public void sort(boolean bAscending, int iSortColumn, Progress progress)
    throws IOException;

  /*------------------------------------------------------------------*/
  /** get current sort direction of sorted table.
   * @return sort direction of sorted table.
   */
  public boolean getAscending();
  
  /*------------------------------------------------------------------*/
  /** get current sort column of sorted table.
   * @return sort column of sorted table.
   */
  public int getSortColumn();

  /*------------------------------------------------------------------*/
  /** export the database table as an HTML table (can be opened in Excel, ...)
   * N.B.: It is the caller's responsability to close the output stream!
   * @param os output stream to receive HTML.
   * @param folderLobs folder where internal LOB files are to be written.
   * @throws IOException if an I/O error occurs.
   */
  public void exportAsHtml(OutputStream os, File folderLobs)
    throws IOException;
  
} /* interface Table */
