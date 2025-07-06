/*======================================================================
Basic interface for simple search in table.
Application : SIARD 2.0
Description : Basic interface for simple search in table. 
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 31.08.2017, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api;

import java.io.*;
import java.util.*;
import ch.enterag.utils.*;

/*====================================================================*/
/** Basic interface for simple search in table.
 * @author Hartwig Thomas
 *
 */
public interface Search
{
  /*------------------------------------------------------------------*/
  /** return string searched for.
   * @return find string.
   */
  public String getFindString();
  
  /*------------------------------------------------------------------*/
  /** get row (0-based) where find string was last found.
   * @return row where find string was last found.
   */
  public long getFoundRow();
  
  /*------------------------------------------------------------------*/
  /** get position (1-based) of cell where string was last found.
   * @return position (1-based) of cell where string was last found.
   */
  public int getFoundPosition();
  
  /*------------------------------------------------------------------*/
  /** get string representation of found cell.
   * @param du date formatter.
   * @return string representation of found cell.
   * @throws IOException if an I/O error occurred.
   */
  public String getFoundString(DU du)
    throws IOException;
  
  /*------------------------------------------------------------------*/
  /** return offset in string representation of the cell where find 
   * string was found.
   * @return offset in string representation of cell.
   */
  public int getFoundOffset();
  
  /*------------------------------------------------------------------*/
  /** open records in preparation for finding the desired string.
   * @param listColumns meta data of the columns to be searched. 
   * @param sFindString string to be searched for.
   * @param bMatchCase true, if exact match is required.
   * @throws IOException if an I/O error occurred.
   */
  public void find(List<MetaColumn> listColumns, String sFindString, boolean bMatchCase)
    throws IOException;
  
  
  /*------------------------------------------------------------------*/
  /** return cell which contains desired string.
   * @param du date to string transformer to be used.
   * @return cell which contains desired string or null.
   * @throws IOException if an I/O error occurred.
   */
  public Cell findNext(DU du)
      throws IOException;
  
  /*------------------------------------------------------------------*/
  /** returns true, if findNext can be applied.
   * @return true, if find has been called before and been found.
   */
  public boolean canFindNext();
  
} /* interface Search */
