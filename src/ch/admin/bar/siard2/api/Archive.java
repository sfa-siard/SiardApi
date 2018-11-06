/*== Archive.java ======================================================
Archive interface provides access to primary and meta data.
Application : SIARD 2.0
Description : Archive interface provides access to primary and meta data.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 21.06.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api;

import java.io.*;

/*====================================================================*/
/** Archive interface provides access to primary data and metadata.
 * To create an Archive instance use
 * ch.admin.bar.siard2.api.primary.ArchiveImpl.newInstance()
 * which returns an implementation of this interface.
 * @author Hartwig Thomas
 */
public interface Archive
{
  /** SIARD 2.x XSD location in class path */
  public static final String sSIARD2_META_DATA_XSD_RESOURCE = "/ch/admin/bar/siard2/api/res/metadata.xsd";
  public static final String sSIARD2_META_DATA_NAMESPACE = "http://www.bar.admin.ch/xmlns/siard/2/metadata.xsd";
  public static final String sSIARD2_GENERIC_TABLE_XSD_RESOURCE = "/ch/admin/bar/siard2/api/res/table.xsd";
  public static final String sSIARD2_TABLE_NAMESPACE = "http://www.bar.admin.ch/xmlns/siard/2/table.xsd";
  public static final String sSIARD_DEFAULT_EXTENSION = "siard";
  /** the oldest version of the meta data XSD still supported for reading */
  public static final String sMETA_DATA_VERSION_1_0 = "1.0";
  /** the abrogated version of the meta data XSD (unsupported) */
  public static final String sMETA_DATA_VERSION_2_0 = "2.0";
  /** the current version of the meta data XSD */
  public static final String sMETA_DATA_VERSION = "2.1";
  /** default maximum string size for inlining LOBs */
  public static final int iDEFAULT_MAX_INLINE_SIZE = 4000;
  
  /*====================================================================
  properties
  ====================================================================*/
  /*------------------------------------------------------------------*/
  /** gets file name.
   @return file name.
   */
  public File getFile();
  
  /*------------------------------------------------------------------*/
  /** returns true, if SIARD file was opened with an option to
   * modify the primary data.
   @return true, if primary data can be modified.
   */
  public boolean canModifyPrimaryData();
  
  /*------------------------------------------------------------------*/
  /** set maximum string size for inlining LOBs.
   * External LOBs are never inlined.
   * This value can only be set if the SIARD archive is open for
   * modification of primary data and still empty.
   * @param iMaxInlineSize maximum string size for inlining LOBs.
   * @throws IOException if the value cannot be set.
   */
  public void setMaxInlineSize(int iMaxInlineSize)
    throws IOException;
  
  /*------------------------------------------------------------------*/
  /** gets maximum string size for inlining LOBs.
   @return maximum string size for inlining LOBs.
   */
  public int getMaxInlineSize();

  /*------------------------------------------------------------------*/
  /** set maximum number of external LOB files per folder (-1 for unlimited).
   * If this number is positive, external LOBs are stored in a subfolder
   * "seq1" underneath the LOB folder of a table column. Whenver the 
   * maximum number of LOB files is reached, new subfolders "seq2", 
   * "seq3" etc. are created. 
   * This value can only be set if the SIARD archive is open for
   * modification of primary data and still empty.
   * @param iMaxLobsPerFolder maximum number of external LOB files per folder. 
   * @throws IOException if the value cannot be set.
   */
  public void setMaxLobsPerFolder(int iMaxLobsPerFolder)
    throws IOException;
  
  /*------------------------------------------------------------------*/
  /** get maximum number of external LOBs per folder.
   @return maximum number of external LOBs per folder or -1 for unlimited.
   */
  public int getMaxLobsPerFolder();
  
  /*------------------------------------------------------------------*/
  /** export the meta data schema.
   * N.B.: closes the stream!
   * @param osXsd stream to which meta data XSD is written.
   * @throws IOException if an I/O error occurred.
   */
  public void exportMetaDataSchema(OutputStream osXsd)
    throws IOException;
  
  /*------------------------------------------------------------------*/
  /** export the generic table XSD.
   * N.B.: closes the stream!
   * @param osXsd generic table XSD is written here.
   * @throws IOException if an I/O error occurred.
   */
  public void exportGenericTableSchema(OutputStream osXsd)
    throws IOException;

  /*------------------------------------------------------------------*/
  /** export the current meta data.
   * The current meta data are exported, even if they do not validate
   * against metadata.xsd. This can be used to help finding errors
   * in the creation of a SIARD file.
   * @param osXml stream to which meta data XML is written.
   * @throws IOException if an I/O error occurred.
   */
  public void exportMetaData(OutputStream osXml)
    throws IOException;
  
  /*------------------------------------------------------------------*/
  /** import a meta data template.
   * Meta data and comments as well as locations for external LOBs are
   * copied from this template for database object in the
   * SIARD archive being created, when their names match with a name in
   * the template and they are not yet set.
   * This value can only be set if the SIARD archive is open for
   * modification of primary data.
   * If the SIARD archive is empty and no meta data have been set,
   * its meta data are initialized from the template.
   * @param isMetaDataTemplate stream with meta data XML for the template.
   * @throws IOException if the value cannot be set or an I/O error occurred.
   */
  public void importMetaDataTemplate(InputStream isMetaDataTemplate)
    throws IOException;
  
  /*====================================================================
  methods
  ====================================================================*/
  /*------------------------------------------------------------------*/
  /** open an existing SIARD file for reading and modification of 
   * meta data only.
   * @param file name of the SIARD file on disk.
   * @throws IOException if an I/O error occurred.
   */
  public void open(File file) throws IOException;
  
  /*------------------------------------------------------------------*/
  /** create a new SIARD file with empty meta data and without primary
   * data.
   * If a file with the same name exists already, this method throws 
   * a FileAlreadyExistsException.  
   * The default value (4000) is used for the maximum inline size.
   * All LOBs are stored internally in the SIARD file.
   * @param file SIARD file to be created/opened.
   * @throws IOException if an I/O error occurred.
   */
  public void create(File file) throws IOException;

  /*------------------------------------------------------------------*/
  /** close the SIARD file and save the current modifications.
   * If the SIARD files was open for writing, this computes a new message
   * digest which is stored in the meta data.
   * Otherwise only the modified meta data are saved.
   * @throws IOException if an I/O error occurred.
   */
  public void close() throws IOException;
  
  /*------------------------------------------------------------------*/
  /** gets meta data for modification.
   @return MetaData instance, or null, if the Archive is not yet created.
   */
  public MetaData getMetaData();
  
  /*------------------------------------------------------------------*/
  /** reloads the meta data from the ZIP file.
   * N.B.: reverts all modifications of the meta data! 
   * @throws IOException if an I/O error occurred.
   */
  public void loadMetaData() throws IOException;
  
  /*------------------------------------------------------------------*/
  /** writes the meta data to the ZIP file.
   * @throws IOException if an I/O error occurred.
   */
  public void saveMetaData() throws IOException;

  /*------------------------------------------------------------------*/
  /** gets the number of schemas in this archive.
   * @return number of schemas in the archive.
   */
  public int getSchemas();
  
  /*------------------------------------------------------------------*/
  /** gets a schema for reading primary data.
   * @param iSchema index.
   * @return schema open for reading.
   */
  public Schema getSchema(int iSchema);
  
  /*------------------------------------------------------------------*/
  /** gets a schema for reading primary data.
   * @param sName schema name.
   * @return schema open for reading.
   */
  public Schema getSchema(String sName);
  
  /*------------------------------------------------------------------*/
  /** create a new schema for writing primary data.
   * A new schema can only be created if the SIARD archive is open for 
   * modification of primary data.
   * @param sName schema name.
   * @return schema open for writing.
   * @throws IOException if the schema could not be created.
   */
  public Schema createSchema(String sName)
    throws IOException;

  /*------------------------------------------------------------------*/
  /** checks if the Archive instance is "empty" (no primary, only meta data).
   @return true, if instance is empty.
   */
  public boolean isEmpty();

  /*------------------------------------------------------------------*/
  /** checks if the Archive instance is valid, i.e. it has valid meta data
   * which reference at least one table containing at least one record
   * of primary data. 
   @return true, if instance is valid.
   */
  public boolean isValid();

  /*------------------------------------------------------------------*/
  /** checks integrity of the primary data of the Archive instance.
   * @return true, if primary data have not been modified since the last
   *         time they were saved while the SIARD file was open for 
   *         modification.
   * @throws IOException if an error occurred.
   */
  public boolean isPrimaryDataUnchanged()
    throws IOException;
  
  public boolean isMetaDataUnchanged();
  
} /* interface Archive */
