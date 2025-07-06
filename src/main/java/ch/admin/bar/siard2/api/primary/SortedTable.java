/*======================================================================
SortedTable is an internal interface to access sorted tables.
Application : SIARD 2.0
Description : SortedTable is an internal interface to access tables sorted 
              by some column in a given direction.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 15.10.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/

package ch.admin.bar.siard2.api.primary;

import java.io.*;
import ch.enterag.utils.background.*;
import ch.admin.bar.siard2.api.*;

/*====================================================================*/
/** SortedTable is an internal interface to access tables sorted  by some 
 * column in a given direction.
 * @author Hartwig Thomas
 */
public interface SortedTable
{
  /*------------------------------------------------------------------*/
  /** opens the input stream of the sorted table
   * @throws IOException if an I/O error occurred.
   */
  public InputStream open() throws IOException;
  
  /*------------------------------------------------------------------*/
  /** sorts the table data in the given direction by the data in the given 
   * column.
   *  
   * The sort is stable, i.e. equal elements retain their order.
   * So sorting by first name and then by last name results in an order
   * where entries with the same last name are sorted by first name.
   *  
   * The sorted table is stored in a temporary XML file which is
   * deleted when the JVM is finished. The primary data in the SIARD
   * file remain unchanged.
   * 
   * Reading the table after the sort reads from this temporary XML file 
   * rather than the original data in the SIARD file.
   * 
   * @param table table to represented by this sorted table.
   * @param bAscending true, if sort is ascending.
   * @param iSortColumn sort column (0-based)
   * @throws IOException if an I/O error occurred.
   */
  public void sort(Table table, boolean bAscending, int iSortColumn, Progress bg)
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
  
} /* SortedTable */
