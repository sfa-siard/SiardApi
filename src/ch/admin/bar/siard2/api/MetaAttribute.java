/*== MetaAttribute.java ================================================
MetaAttribute interface provides access to attribute meta data.
Application : SIARD 2.0
Description : MetaAttribute interface provides access to attribute meta data. 
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 27.06.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api;

import java.io.*;

/*====================================================================*/
/** MetaAttribute interface provides access to attribute meta data.
 @author Hartwig Thomas
 */
public interface MetaAttribute
  extends MetaSearch
{
  /*------------------------------------------------------------------*/
  /** return the parent type meta data to which these meta data belong.
   * @return parent type meta data or null, if parent is an attribute.
   */
  public MetaType getParentMetaType();

  /*------------------------------------------------------------------*/
  /** return true, if attribute's type or type name is not null.
   * @return true, if attribute's type or type name is not null.
   */
  public boolean isValid();
  
  /*====================================================================
  attribute properties
  ====================================================================*/
  /*------------------------------------------------------------------*/
  /** get attribute name.
   * @return attribute name.
   */
  public String getName();

  /*------------------------------------------------------------------*/
  /** get position of field  in parent column or field (1-based!).
   * @return position of field in parent.
   */
  public int getPosition();
  
  /*------------------------------------------------------------------*/
  /** set SQL:2008 predefined data type of the attribute.
   * Can only be set if the SIARD archive is open for modification
   * of primary data.
   * If a UDT type schema or name for this attribute have been set they
   * are removed.
   * @param sType SQL:2008 predefined data type of the attribute.
   * @throws IOException if the value could not be set.
   */
  public void setType(String sType)
    throws IOException;
  /** set SQL:2008 predefined data type of the attribute.
   * Can only be set if the SIARD archive is open for modification
   * of primary data.
   * If a UDT type schema or name for this attribute have been set they
   * are removed.
   * The given data type (from java.sql.Types) is mapped to
   * an SQL:2008 predefined data type like this:
   * CHAR            "CHAR[(&lt;Precision&gt;)]"
   * VARCHAR         "VARCHAR[(&lt;Precision&gt;)]"
   * CLOB            "CLOB[(Precision)]"
   * NCHAR           "NCHAR[(&lt;Precision&gt;)]"
   * NVARCHAR        "NVARCHAR[(&lt;Precision&gt;)]"
   * NCLOB           "NCLOB[(&lt;Precision&gt;)]"
   * SQLXML          "XML"
   * BINARY          "BINARY[(&lt;Precision&gt;)]"
   * VARBINARY       "VARBINARY[(&lt;Precision&gt;)]"
   * BLOB            "BLOB[(&lt;Precision&gt;)]"
   * BOOLEAN         "BOOLEAN"
   * SMALLINT        "SMALLINT"
   * INTEGER         "INTEGER"
   * BIGINT          "BIGINT"
   * DECIMAL         "DECIMAL[(&lt;Precision&gt;[,&lt;Scale&gt;])]"
   * NUMERIC         "NUMERIC[(&lt;Precision&gt;[,&lt;Scale&gt;])]"
   * REAL            "REAL"
   * FLOAT           "FLOAT[(&lt;Precision&gt;)]"
   * DOUBLE          "DOUBLE PRECISION"
   * DATE            "DATE"
   * TIME            "TIME[(&lt;Scale&gt;)]"
   * TIMESTAMP       "TIMESTAMP[(&lt;Scale&gt;)]"
   * (not mapped)    "INTERVAL ..." (must be specified using the string argument)
   * When precision or scale are less than zero, they are treated
   * as not given. Optional parts (in brackets) are dropped unless all
   * their content is given. 
   * @param iDataType one of the java.sql.Types values listed above.
   * @param lPrecision length/precision of the data type.
   * @param iScale scale of the data type. 
   * @throws IOException if the value could not be set.
   */
  public void setPreType(int iDataType, long lPrecision, int iScale)
    throws IOException;
  /** get SQL:2008 predefined data type of the attribute.
   * @return SQL:2008 predefined data type of the attribute.
   */
  public String getType();
  /** get predefined data type of the attribute as a java.sql.Types integer
   * using this mapping:
   * null                               NULL
   * "CHAR[(&lt;Length&gt;)]"                 CHAR
   * "VARCHAR[(&lt;Length&gt;)]"              VARCHAR
   * "CLOB[(&lt;LOB Length&gt;)]"              CLOB
   * "NCHAR[(&lt;Length&gt;)]"                NCHAR
   * "NVARCHAR[(&lt;Length&gt;)]"             NVARCHAR        
   * "NCLOB[(&lt;LOB Length&gt;)]"             NCLOB           
   * "XML"                              SQLXML
   * "BINARY[(&lt;Length&gt;)]"               BINARY          
   * "VARBINARY[(&lt;Length&gt;)]"            VARBINARY       
   * "BLOB[(&lt;LOB Length&gt;)]"             BLOB            
   * "BOOLEAN"                          BOOLEAN         
   * "SMALLINT"                         SMALLINT        
   * "INTEGER"                          INTEGER         
   * "BIGINT"                           BIGINT          
   * "DECIMAL[(&lt;Precision&gt;[,&lt;Scale&gt;])]" DECIMAL         
   * "NUMERIC[(&lt;Precision&gt;[,&lt;Scale&gt;])]" NUMERIC         
   * "REAL"                             REAL            
   * "FLOAT[(&lt;Precision&gt;)]"             FLOAT           
   * "DOUBLE PRECISION"                 DOUBLE          
   * "DATE"                             DATE            
   * "TIME[(&lt;Scale&gt;)]"                  TIME            
   * "TIMESTAMP[(&lt;Scale&gt;)]"             TIMESTAMP       
   * "INTERVAL ..."                     OTHER
   * @return parsed predefined data type of the attribute,
   * or its base type (DISTINCT and ARRAY), or java.sql.Types.NULL otherwise.
   */
  public int getPreType();
  /** get (maximum) length/precision of type of -1 if it is not defined.
   * @return (maximum) length/precision of type.
   */
  public long getLength();
  /** get scale of type or -1 if it is not defined.
   * @return scale of type
   */
  public int getScale();
  
  /*------------------------------------------------------------------*/
  /** set original data type of the attribute.
   * Can only be set if the SIARD archive is open for modification
   * of primary data and no UDT type name for this attribute has been set.
   * @param sTypeOriginal original data type of the attribute.
   * @throws IOException if the value could not be set.
   */
  public void setTypeOriginal(String sTypeOriginal)
    throws IOException;
  /** get original data type of the attribute.
   * @return original data type of the attribute.
   */
  public String getTypeOriginal();
  
  /*------------------------------------------------------------------*/
  /** set schema of UDT type for this attribute.
   * Can only be set if the SIARD archive is open for modification
   * of primary data.
   * If a predefined type for this attribute has been set, it is removed.
   * @param sTypeSchema schema of UDT type for this attribute.
   * @throws IOException if the value could not be set.
   */
  public void setTypeSchema(String sTypeSchema)
    throws IOException;
  /** get schema of UDT type for this attribute.
   * @return schema of UDT type for this attribute.
   */
  public String getTypeSchema();
  
  /*------------------------------------------------------------------*/
  /** set name of UDT type for this attribute.
   * Can only be set if the SIARD archive is open for modification
   * of primary data.
   * If a predefined type for this attribute has been set, it is removed.
   * @param sTypeName name of UDT type for this attribute.
   * @throws IOException if the value could not be set.
   */
  public void setTypeName(String sTypeName)
    throws IOException;
  /** get name of UDT type for this attribute.
   * @return name of UDT type for this attribute.
   */
  public String getTypeName();
  /** get the type meta data for this attribute or null if its type name
   * is not set.
   * @return type meta data for this attribute.
   */
  public MetaType getMetaType();
  
  /*------------------------------------------------------------------*/
  /** set nullability of the column.
   * Can only be set if the SIARD archive is open for modification
   * of primary data, the table is still empty, and no UDT type name
   * for this column has been set.
   * @param bNullable true if column can be NULL, otherwise false.
   * @throws IOException if the value could not be set.
   */
  public void setNullable(boolean bNullable)
    throws IOException;
  /** get nullability of the column.
   * @return nullability of the column.
   */
  public boolean isNullable();

  /*------------------------------------------------------------------*/
  /** set default value of the attribute.
   * Can only be set if the SIARD archive is open for modification
   * of primary data, is empty, and no UDT type name
   * for this attribute has been set.
   * @param sDefaultValue default value for this column.
   * @throws IOException if the value could not be set.
   */
  public void setDefaultValue(String sDefaultValue)
    throws IOException;
  /** get default value of the column.
   * @return default value of the column.
   */
  public String getDefaultValue();
  
  /*------------------------------------------------------------------*/
  /** set cardinality (maximum array length) of the attribute if it is 
   * an ARRAY.
   * Can only be set if the SIARD archive is open for modification
   * of primary data, and is empty.
   * @param iCardinality cardinality of the array.
   * @throws IOException if the value could not be set.
   */
  public void setCardinality(int iCardinality)
    throws IOException;
  /** get cardinality or -1 if the attribute is not an ARRAY.
   * @return cardinality of ARRAY or -1.
   */
  public int getCardinality();
  
  /*------------------------------------------------------------------*/
  /** set description of the attribute.
   * @param sDescription description of the attribute.
   */
  public void setDescription(String sDescription);
  /** get description of the attribute.
   * @return description of the attribute.
   */
  public String getDescription();
  
} /* interface MetaAttribute */
