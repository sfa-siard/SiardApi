/*== MetaTable.java ====================================================
MetaTable interface provides access to table meta data.
Application : SIARD 2.0
Description : MetaTable interface provides access to table meta data.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 24.06.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api;

import java.io.*;
import java.util.List;

/*====================================================================*/
/** MetaTable interface provides access to table meta data.
 @author Hartwig Thomas
 */
public interface MetaTable
  extends MetaSearch
{
  /*------------------------------------------------------------------*/
  /** return the schema meta data.
   * @return schema meta data.
   */
  public MetaSchema getParentMetaSchema();
  
  /*------------------------------------------------------------------*/
  /** return the associated table instance to which these meta data belong.
   * @return associated table instance.
   */
  public Table getTable();
  
  /*------------------------------------------------------------------*/
  /** return true, if the table meta data is valid.
   * @return true, if the table meta data is valid.
   */
  public boolean isValid();
  
  /*====================================================================
  table properties
  ====================================================================*/
  /* name */
  /*------------------------------------------------------------------*/
  /** get table name.
   * @return table name.
   */
  public String getName();
  
  /* folder */
  /*------------------------------------------------------------------*/
  /** get table folder.
   * @return table folder.
   */
  public String getFolder();
  
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
  /** set number of rows of the table.
   * @param lRows number of rows of the table.
   * @throws IOException if an I/O error occurred.
   */
  public void setRows(long lRows)
    throws IOException;
  
  /*------------------------------------------------------------------*/
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
   * modification of primary data and the table is empty.
   * @param sName name of new column.
   * @return column meta data.
   * @throws IOException if column meta data could not be created.
   */
  public MetaColumn createMetaColumn(String sName)
    throws IOException;
  
  /*------------------------------------------------------------------*/
  /** get meta data entry of primary key of the table.
   * @return meta data of primary key of null, if no primary key has been created.
   */
  public MetaUniqueKey getMetaPrimaryKey();
  /** create primary key meta data of table.
   * A new primary key can only be created if the SIARD archive is open for 
   * modification of primary data.
   * @param sName name of new primary key.
   * @return primary key meta data.
   * @throws IOException if primary key meta data could not be created.
   */
  public MetaUniqueKey createMetaPrimaryKey(String sName)
    throws IOException;
  
  /*------------------------------------------------------------------*/
  /** get number of foreign key meta data entries.
   * @return number of foreign key meta data entries.
   */
  public int getMetaForeignKeys();
  /** get the foreign key meta data with the given index.
   * @param iForeignKey index of foreign key meta data.
   * @return foreign key meta data.
   */
  public MetaForeignKey getMetaForeignKey(int iForeignKey);
  /** get the foreign key meta data with the given name.
   * @param sName name of foreign key.
   * @return foreign key meta data.
   */
  public MetaForeignKey getMetaForeignKey(String sName);
  /** add new foreign key to meta data of table.
   * A new foreign key can only be created if the SIARD archive is open for 
   * modification of primary data.
   * @param sName name of foreign key.
   * @return foreign key meta data.
   * @throws IOException if foreign key meta data could not be created.
   */
  public MetaForeignKey createMetaForeignKey(String sName)
    throws IOException;
  
  /*------------------------------------------------------------------*/
  /** get number of candidate key meta data entries.
   * @return number of candidate key meta data entries.
   */
  public int getMetaCandidateKeys();
  /** get the candidate key meta data with the given index.
   * @param iCandidateKey index of candidate key meta data.
   * @return candidate key meta data.
   */
  public MetaUniqueKey getMetaCandidateKey(int iCandidateKey);
  /** get the candidate key meta data with the given name.
   * @param sName name of candidate key.
   * @return candidate key meta data.
   */
  public MetaUniqueKey getMetaCandidateKey(String sName);
  /** add new candidate key to meta data of table.
   * A new candidate key can only be created if the SIARD archive is open for 
   * modification of primary data.
   * @param sName name of new candidate key.
   * @return candidate key meta data.
   * @throws IOException if candidate key meta data could not be created.
   */
  public MetaUniqueKey createMetaCandidateKey(String sName)
    throws IOException;
  
  /*------------------------------------------------------------------*/
  /** get number of check constraint meta data entries.
   * @return number of check constraint meta data entries.
   */
  public int getMetaCheckConstraints();
  /** get the check constraint meta data with the given index.
   * @param iCheckConstraint index of check constraint meta data.
   * @return check constraint meta data.
   */
  public MetaCheckConstraint getMetaCheckConstraint(int iCheckConstraint);
  /** get the check constraint meta data with the given name.
   * @param sName name of check constraint.
   * @return check constraint meta data.
   */
  public MetaCheckConstraint getMetaCheckConstraint(String sName);
  /** add new check constraint to meta data of table.
   * A new check constraint can only be created if the SIARD archive is open for 
   * modification of primary data.
   * @param sName name of new check constraint.
   * @return check constraint meta data.
   * @throws IOException if check constraint meta data could not be created.
   */
  public MetaCheckConstraint createMetaCheckConstraint(String sName)
    throws IOException;
  
  /*------------------------------------------------------------------*/
  /** get number of trigger meta data entries.
   * @return number of trigger meta data entries.
   */
  public int getMetaTriggers();
  /** get the trigger meta data with the given index.
   * @param iTrigger index of trigger meta data.
   * @return trigger meta data.
   */
  public MetaTrigger getMetaTrigger(int iTrigger);
  /** get the trigger meta data with the given name.
   * @param sName name of trigger.
   * @return trigger meta data.
   */
  public MetaTrigger getMetaTrigger(String sName);
  /** add new trigger to meta data of table.
   * A new trigger can only be created if the SIARD archive is open for 
   * modification of primary data.
   * @param sName name of new trigger.
   * @return trigger meta data.
   * @throws IOException if trigger meta data could not be created.
   */
  public MetaTrigger createMetaTrigger(String sName)
    throws IOException;
  
  /*------------------------------------------------------------------*/
  /** return a list of "flattened" column names contained in this table,
   * each given as a list of column and field names. 
   * @param bSupportsArrays list is for database system which supports arrays.
   * @param bSupportsUdts list is for database system which supports UDT types.
   * @return list of "flattened" column names.
   * @throws IOException if an I/O error occurred.
   */
  public List<List<String>> getColumnNames(
    boolean bSupportsArrays, boolean bSupportsUdts)
    throws IOException;
  
  /*------------------------------------------------------------------*/
  /** retrieve the (predefined) type for this "flattened" column name.
   * @param listNames column/field name list indicating the column or sub 
   * field for which the type is to be retrieved.
   * @return type type for this "flattened" column name. 
   * @throws IOException if an I/O error occurred.
   */
  public String getType(List<String> listNames)
    throws IOException;
  
} /* interface MetaTable */
