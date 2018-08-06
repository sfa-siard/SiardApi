/*== MetaSchema.java ===================================================
MetaSchema interface provides access to schema meta data.
Application : SIARD 2.0
Description : MetaSchema interface provides access to schema meta data.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 27.06.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api;

import java.io.*;

/*====================================================================*/
/** MetaSchema interface provides access to schema meta data.
 @author Hartwig Thomas
 */
public interface MetaSchema
  extends MetaSearch
{
  /*------------------------------------------------------------------*/
  /** return the meta data on the archive level.
   * @return meta data on the archive level.
   */
  public MetaData getParentMetaData();
  
  /*------------------------------------------------------------------*/
  /** return the associated schema instance to which these meta data belong.
   * @return associated schema instance.
   */
  public Schema getSchema();
  
  /*------------------------------------------------------------------*/
  /** return true, if the schema meta data is valid.
   * @return true, if the schema meta data is valid.
   */
  public boolean isValid();
  
  /*====================================================================
  schema properties
  ====================================================================*/
  /* name */
  /*------------------------------------------------------------------*/
  /** get schema name.
   * @return schema name.
   */
  public String getName();
  
  /* folder */
  /*------------------------------------------------------------------*/
  /** get schema folder.
   * @return schema folder.
   */
  public String getFolder();
  
  /*------------------------------------------------------------------*/
  /** set description of the schema.
   * @param sDescription description of the schema.
   */
  public void setDescription(String sDescription);
  /** get description of the schema.
   * @return description of the schema.
   */
  public String getDescription();
  
  /*====================================================================
  list properties
  ====================================================================*/
  /*------------------------------------------------------------------*/
  /** get number of table meta data entries.
   * @return number of table meta data entries.
   */
  public int getMetaTables();
  /** get the table meta data with the given index.
   * @param iTable index of table meta data.
   * @return table meta data.
   */
  public MetaTable getMetaTable(int iTable);
  /** get the table meta data with the given name.
   * @param sName name of table.
   * @return table meta data.
   */
  public MetaTable getMetaTable(String sName);

  /*------------------------------------------------------------------*/
  /** get number of view meta data entries.
   * @return number of view meta data entries.
   */
  public int getMetaViews();
  /** get the view meta data with the given index.
   * @param iView index of view meta data.
   * @return view meta data.
   */
  public MetaView getMetaView(int iView);
  /** get the view meta data with the given name.
   * @param sName name of view.
   * @return view meta data.
   */
  public MetaView getMetaView(String sName);
  /** add new view to meta data.
   * A new view can only be created if the SIARD archive is open for 
   * modification of primary data.
   * @param sName name of the new view.
   * @return view meta data.
   * @throws IOException if new user could be created.
   */
  public MetaView createMetaView(String sName)
    throws IOException;
  /** remove a view from the meta data.
   * a view can only be removed if the SIARD archive is open for
   * modification of primary data.
   * @param mv view meta data to be removed.
   * @return true, if view meta data could be removed.
   * @throws IOException if schema did not fulfill the conditions.
   */
  public boolean removeMetaView(MetaView mv)
    throws IOException;

  /*------------------------------------------------------------------*/
  /** get number of routine meta data entries.
   * @return number of routine meta data entries.
   */
  public int getMetaRoutines();
  /** get the routine meta data with the given index.
   * @param iRoutine index of routine meta data.
   * @return routine meta data.
   */
  public MetaRoutine getMetaRoutine(int iRoutine);
  /** get the routine meta data with the given specific name.
   * @param sSpecificName name of routine.
   * @return routine meta data.
   */
  public MetaRoutine getMetaRoutine(String sSpecificName);
  /** add new routine to meta data.
   * A new routine can only be created if the SIARD archive is open for 
   * modification of primary data.
   * @param sName name of the new routine.
   * @return routine meta data.
   * @throws IOException if new user could be created.
   */
  public MetaRoutine createMetaRoutine(String sName)
    throws IOException;

  /*------------------------------------------------------------------*/
  /** get number of type meta data entries.
   * @return number of type meta data entries.
   */
  public int getMetaTypes();
  /** get the type meta data with the given index.
   * @param iType index of type meta data.
   * @return type meta data.
   */
  public MetaType getMetaType(int iType);
  /** get the type meta data with the given name.
   * @param sName name of type.
   * @return type meta data.
   */
  public MetaType getMetaType(String sName);
  /** add new type to meta data.
   * A new type can only be created if the SIARD archive is open for 
   * modification of primary data.
   * @param sName name of the new type.
   * @return type meta data.
   * @throws IOException if new user could be created.
   */
  public MetaType createMetaType(String sName)
    throws IOException;
  
} /* interface MetaSchema */
