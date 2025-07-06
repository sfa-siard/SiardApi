/*== MetaView.java =====================================================
MetaView interface provides access to view meta data.
Application : SIARD 2.0
Description : MetaView interface provides access to view meta data.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 29.06.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api;

import java.io.*;

/*====================================================================*/
/** MetaView interface provides access to view meta data.
 @author Hartwig Thomas
 */
public interface MetaView
  extends MetaSearch
{
  /*------------------------------------------------------------------*/
  /** return the associated schema meta data to which these meta data belong.
   * @return associated schema meta data.
   */
  public MetaSchema getParentMetaSchema();
  
  /*------------------------------------------------------------------*/
  /** return true, if the view meta data is valid.
   * @return true, if the view meta data is valid.
   */
  public boolean isValid();
  
  /*====================================================================
  view properties
  ====================================================================*/
  /* name */
  /*------------------------------------------------------------------*/
  /** get view name.
   * @return view name.
   */
  public String getName();
  
  /* query */
  /*------------------------------------------------------------------*/
  /** set the SQL:2008 (ISO 9075) query defining the view.
   * @param sQuery SQL query.
   */
  public void setQuery(String sQuery);
  /** get the SQL:2008 (ISO 9075) query defining the view.
   * @return the SQL:2008 (ISO 9075) query defining the view.
   */
  public String getQuery();
  
  /* queryOriginal */
  /*------------------------------------------------------------------*/
  /** set the original query string defining the view.
   * The original query can only be set if the SIARD archive 
   * is open for modification of primary data.
   * @param sQueryOriginal original query string defining the view.
   * @throws IOException if the original query cannot be set.
   */
  public void setQueryOriginal(String sQueryOriginal)
    throws IOException;
  /** get the original query string defining the view.
   * @return the original query string defining the view.
   */
  public String getQueryOriginal();
  
  /* description */
  /*------------------------------------------------------------------*/
  /** set description of the table.
   * @param sDescription description of the table.
   */
  public void setDescription(String sDescription);
  /** get description of the table.
   * @return description of the table.
   */
  public String getDescription();

  /* rows */
  /*------------------------------------------------------------------*/
  /** set the number of rows of the view.
   * The number of rows of the view can only be set if the SIARD archive 
   * is open for modification of primary data.
   * @param lRows number of rows of the view.
   * @throws IOException if the number of rows cannot be set.
   */
  public void setRows(long lRows)
    throws IOException;
  /** get the number of rows of the table.
   * @return number of rows of the table.
   */
  public long getRows();
  
  /*====================================================================
  list properties
  ====================================================================*/
  /*------------------------------------------------------------------*/
  /** get number of column meta data entries.
   * @return number of column meta data entries.
   */
  public int getMetaColumns();
  /** get the column meta data with the given index.
   * @param iColumn index of column meta data.
   * @return column meta data.
   */
  public MetaColumn getMetaColumn(int iColumn);
  /** get the column meta data with the given name.
   * @param sName name of column.
   * @return column meta data.
   */
  public MetaColumn getMetaColumn(String sName);
  /** add new column to table meta data.
   * A new table can only be created if the SIARD archive is open for 
   * modification of primary data.
   * N.B.: The resulting column meta data is invalid, until the type
   * or the type name have been set!
   * @param sName name of new column.
   * @return column meta data.
   * @throws IOException if column meta data could not be created.
   */
  public MetaColumn createMetaColumn(String sName)
    throws IOException;
  
} /* interface MetaView */
