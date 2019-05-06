package ch.admin.bar.siard2.api.primary;

import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.io.*;
import javax.xml.bind.*;
import ch.enterag.utils.*;
import ch.enterag.utils.zip.*;
import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.api.generated.*;
import ch.admin.bar.siard2.api.meta.*;

public class ArchiveImpl
  implements Archive
{
  private static ObjectFactory _of = new ObjectFactory();
  public static final String sSIARD2_TABLE_TEMPLATE_XSD_RESOURCE = "/ch/admin/bar/siard2/api/res/table0.xsd";
  private static final String _sHEADER_FOLDER = "header/";
  public static String getHeaderFolder() { return _sHEADER_FOLDER; }
  private static final String _sCONTENT_FOLDER = "content/";
  public static String getContentFolder() { return _sCONTENT_FOLDER; }
  private static final String _sSIARDVERSION_FOLDER = "siardversion/";
  private static final String _sSIARDVERSION_FOLDER_2_0 = "version/";
  public static String getSiardVersionFolder() { return _sHEADER_FOLDER+_sSIARDVERSION_FOLDER+Archive.sMETA_DATA_VERSION+"/"; }
  public static String getSiardVersionFolder20() { return _sHEADER_FOLDER+_sSIARDVERSION_FOLDER_2_0+Archive.sMETA_DATA_VERSION_2_0+"/"; }
  private static final String _sMETADATA_XML = "metadata.xml";
  public static String getMetaDataXml() { return _sHEADER_FOLDER+_sMETADATA_XML; }
  private static final String _sMETADATA_XSD = "metadata.xsd";
  public static String getMetaDataXsd() { return _sHEADER_FOLDER+_sMETADATA_XSD; }
  private static final String _sGENERIC_TABLE_XSD = "table.xsd";
  public static String getGenericTableXsd() { return _sHEADER_FOLDER+_sGENERIC_TABLE_XSD; }
  private static final String _sMETADATA_XSL = "metadata.xsl";
  public static String getMetaDataXsl() { return _sHEADER_FOLDER+_sMETADATA_XSL; }
  private static String sPRONOM_ID = "siardversion";
  static final int _iBUFFER_SIZE = 8192;
  static final DigestTypeType _dttDEFAULT_DIGEST_ALGORITHM = DigestTypeType.MD_5;
  static final String _sDEFAULT_ENCODING = SU.sUTF8_CHARSET_NAME;
  static final String _sATTR_FILE = "file";
  static final String _sATTR_LENGTH = "length";
  static final String _sATTR_DIGEST_TYPE = "digestType";
  static final String _sATTR_MESSAGE_DIGEST = "digest";
  public StopWatch _swValid = StopWatch.getInstance();
  private boolean _bValid = false; // cached value

  private Zip64File _zipFile = null;
  public Zip64File getZipFile() { return _zipFile; }
  
  /*------------------------------------------------------------------*/
  /** check whether a file entry exists.
   * @param sEntryName name of file entry.
   * @return true, if it exists, false otherwise.
   * @throws IOException if the entry name does not denote a file entry.
   */
  public boolean existsFileEntry(String sEntryName)
    throws IOException
  {
    boolean bExists = false;
    if (!sEntryName.endsWith("/"))
    {
      if (getZipFile().getFileEntry(sEntryName) != null)
        bExists = true;
    }
    else
      throw new IOException("ZIP file entries must not end with \"/\"!");
    return bExists;
  } /* existsFileEntry */

  /*------------------------------------------------------------------*/
  /** check whether a folder entry exists.
   * @param sEntryName name of folder entry.
   * @return true, if it exists, false otherwise.
   * @throws IOException if the entry name does not denote a folder entry.
   */
  public boolean existsFolderEntry(String sEntryName)
    throws IOException
  {
    boolean bExists = false;
    if (sEntryName.endsWith("/"))
    {
      if (getZipFile().getFileEntry(sEntryName) != null)
        bExists = true;
    }
    else
      throw new IOException("ZIP folder entries must end with \"/\"!");
    return bExists;
  } /* existsFolderEntry */

  /*------------------------------------------------------------------*/
  /** create a new folder entry.
   * @param sEntryName name of folder entry.
   * @throws IOException if the folder entry cannot be created.
   */
  public void createFolderEntry(String sEntryName)
    throws IOException
  {
    if (sEntryName.endsWith("/"))
    {
      if (!existsFolderEntry(sEntryName))
      {
        int iLastSlash = sEntryName.substring(0,sEntryName.length()-1).lastIndexOf('/');
        if (iLastSlash > 0)
        {
          String sParentFolder = sEntryName.substring(0,iLastSlash+1);
          if (!existsFolderEntry(sParentFolder))
            createFolderEntry(sParentFolder);
        }
        EntryOutputStream eos = getZipFile().openEntryOutputStream(sEntryName, FileEntry.iMETHOD_STORED, new Date());
        eos.close();
      }
      else
        throw new IOException("Folder "+sEntryName+" exists already!");
    }
    else
      throw new IOException("Folder names must end with \"/\"!");
  } /* createFolderEntry */

  /*------------------------------------------------------------------*/
  /** open an existing file entry.
   * @param sEntryName name of file entry.
   * @return open input stream (must be closed by caller!).
   * @throws IOException if an I/O error occurred.
   */
  public InputStream openFileEntry(String sEntryName)
    throws IOException
  {
    return getZipFile().openEntryInputStream(sEntryName);
  } /* openFileEntry */
  
  /*------------------------------------------------------------------*/
  /** create a new file entry.
   * @param sEntryName name of file entry.
   * @return open input stream (must be closed by caller!).
   * @throws IOException if an I/O error occurred.
   */
  public OutputStream createFileEntry(String sEntryName)
    throws IOException
  {
    return getZipFile().openEntryOutputStream(sEntryName,FileEntry.iMETHOD_DEFLATED,new Date());
  } /* createFileEntry */
  
  /*------------------------------------------------------------------*/
  /** private constructor for inaccessibility from the outside.
   */
  private ArchiveImpl() {}
  
  /*------------------------------------------------------------------*/
  /** factory returns interface.
   * @return implementation of Archive interface.
   */
  public static Archive newInstance()
  {
    return new ArchiveImpl();
  } /* newInstance */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public File getFile() 
  {
    return new File(getZipFile().getDiskFile().getFileName()); 
  } /* getFile */
  
  private String _sPreviousMetaDataVersion = Archive.sMETA_DATA_VERSION;
  public String getPreviousMetaDataVersion() { return _sPreviousMetaDataVersion; } 

  private boolean _bModifyPrimaryData = false;
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  public boolean canModifyPrimaryData() { return _bModifyPrimaryData; }
  
  private boolean _bMetaDataModified = false;
  public boolean isMetaDataDifferent(Object oOld, Object oNew) 
  {
    boolean bDifferent = true;
    if (Objects.equals(oOld, oNew))
      bDifferent = false;
    if (bDifferent)
      _bMetaDataModified = true;
    return bDifferent;
  } /* isMetaDataDifferent */

  private int _iMaxInlineSize = Archive.iDEFAULT_MAX_INLINE_SIZE;
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override 
  public void setMaxInlineSize(int iMaxInlineSize)
    throws IOException
  {
    if (canModifyPrimaryData() && isEmpty())
      _iMaxInlineSize = iMaxInlineSize;
    else
      throw new IOException("Maximum inline size can only be set for SIARD archives that are empty!");
  } /* setMaxInlineSize */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public int getMaxInlineSize() { return _iMaxInlineSize; }

  private int _iMaxLobsPerFolder = -1; // negative = unlimited
  @Override
  public void setMaxLobsPerFolder(int iMaxLobsPerFolder)
    throws IOException
  {
    if (canModifyPrimaryData() && isEmpty())
      _iMaxLobsPerFolder = iMaxLobsPerFolder;
    else
      throw new IOException("Maximum number of external LOBs per folder can only be set for SIARD archives that are empty!");
  }
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public int getMaxLobsPerFolder() { return _iMaxLobsPerFolder; }

  private MetaData _md = null;
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public MetaData getMetaData() { return _md; }

  private void exportResource(String sResource, OutputStream os)
    throws IOException
  {
    URL urlXsd = Archive.class.getResource(sResource);
    if (urlXsd != null)
    {
      byte[] buffer = new byte[_iBUFFER_SIZE];
      InputStream isXsd = urlXsd.openStream();
      for (int iRead = isXsd.read(buffer); iRead != -1; iRead = isXsd.read(buffer))
      {
        if (iRead > 0)
          os.write(buffer,0,iRead);
      }
      isXsd.close();
      os.close();
    }
    else
      throw new IOException("Resource "+sResource+" not in JAR!");
  } /* exportResource */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override 
  public void exportMetaDataSchema(OutputStream osXsd)
    throws IOException
  {
    exportResource(Archive.sSIARD2_META_DATA_XSD_RESOURCE, osXsd);
  } /* exportMetaData */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  public void exportGenericTableSchema(OutputStream osXsd)
    throws IOException
  {
    exportResource(Archive.sSIARD2_GENERIC_TABLE_XSD_RESOURCE,osXsd);
  } /* exportGenericTableSchema */

  /*------------------------------------------------------------------*/
  /** export metadata.xml to an output stream.
   * @param osXml output stream.
   * @param bValidate true, if data must be validated.
   * @throws IOException
   */
  private void exportMetaData(OutputStream osXml, boolean bValidate)
    throws IOException
  {
    MetaDataImpl mdi = (MetaDataImpl)getMetaData();
    try 
    { 
      MetaDataXml.writeXml(mdi.getSiardArchive(), osXml, bValidate);
      osXml.close();
      if (isEmpty())
        _bMetaDataModified = false;
    }
    catch(JAXBException je) { throw new IOException("Error exporting metadata!",je); }
  } /* exportMetaData */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override 
  public void exportMetaData(OutputStream osXml)
    throws IOException
  {
    exportMetaData(osXml,false);
  } /* exportMetaData */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void importMetaDataTemplate(InputStream isXml)
    throws IOException
  {
    SiardArchive saTemplate = MetaDataXml.readXml(isXml);
    if (saTemplate != null)
    {
      if (getZipFile() == null) /* import before open or create goes to temporary SIARD file */
      {
        File fileArchive = File.createTempFile("mdo",".siard");
        fileArchive.delete();
        create(fileArchive);
        fileArchive.deleteOnExit();
        _md = MetaDataImpl.newInstance(this,saTemplate);
        SchemasType sts = saTemplate.getSchemas();
        if (sts != null)
        {
          for (int iSchema = 0; iSchema < sts.getSchema().size(); iSchema++)
          {
            SchemaType st = sts.getSchema().get(iSchema);
            createSchema(st.getName());
          }
        }
        _bMetaDataModified = false;
      }
      MetaDataImpl mdi = (MetaDataImpl)getMetaData();
      mdi.setTemplate(saTemplate);
    }
    else 
      throw new IOException("Error importing metadata!");
  } /* importMetaDataTemplate */
  
  private Map<String,Schema> _mapSchemas = new HashMap<String,Schema>();
  public void registerSchema(String sName, Schema schema)
  {
    _mapSchemas.put(sName, schema);
  } /* registerSchema */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void loadMetaData()
    throws IOException
  {
    FileEntry feMetaData = getZipFile().getFileEntry(getMetaDataXml());
    if (feMetaData != null)
    {
      SiardArchive sa = null;
      // format version 2.1
      if (existsFolderEntry(getSiardVersionFolder()))
      {
        InputStream isMetaData = openFileEntry(getMetaDataXml());
        sa = MetaDataXml.readXml(isMetaData);
        isMetaData.close();
      }
      // format version 2.0 (abrogated)
      else if (existsFolderEntry(getSiardVersionFolder20()))
      {
        throw new IOException("Unsupported SIARD format version 2.0!");
      }
      // format version 1.0
      else
      {
        InputStream isMetaData = openFileEntry(getMetaDataXml());
        sa = MetaDataXml.readXmlOld10(isMetaData);
        isMetaData.close();
        _sPreviousMetaDataVersion = Archive.sMETA_DATA_VERSION_1_0;
      }
      if (sa == null)
        throw new IOException("Invalid SIARD meta data!");
      _md = MetaDataImpl.newInstance(this,sa);
    }
    else
      throw new IOException("Invalid SIARD file (missing metadata.xml)!");
  } /* loadMetaData */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void open(File file)
    throws IOException
  {
    if (file.exists())
    {
      /* open the ZIP file */
      _zipFile = new Zip64File(file);
      loadMetaData();
      SiardArchive sa = ((MetaDataImpl)getMetaData()).getSiardArchive();
      SchemasType sts = sa.getSchemas();
      for (int iSchema = 0; iSchema < sts.getSchema().size(); iSchema++)
      {
        SchemaType st = sts.getSchema().get(iSchema);
        SchemaImpl.newInstance(this, st.getName());
      }
      _bModifyPrimaryData = false;
      _bMetaDataModified = false;
      /* compute initial cached validity */
      validate();
    }
    else
      throw new IOException("SIARD file "+file.getAbsolutePath()+" does not exist!");
  } /* open */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void create(File file)
    throws IOException
  {
    if (!file.exists())
    {
      /* open/create the ZIP file */
      _zipFile = new Zip64File(file);
      _bModifyPrimaryData = true;
      _bMetaDataModified = true;
      createFolderEntry(_sCONTENT_FOLDER);
      _md = MetaDataImpl.newInstance(this,MetaDataImpl.createSiardArchive());
    }
    else
      throw new FileAlreadyExistsException("File "+file.getAbsolutePath()+" exists already!");
  } /* create */

  /*------------------------------------------------------------------*/
  /** computes the message digest of the primary data of the Db instance.
   * @param dtt algorithm to be used.
   * @return message digest or null, if no primary data are available.
   * @throws IOException if an I/O error occurred.
   */
  private MessageDigestType getMessageDigest(DigestTypeType dtt)
    throws IOException
  {
    FileEntry feHeader = getZipFile().getFileEntry(_sHEADER_FOLDER);
    /* if meta data folder is stored, then compute MD5 from 0 to its offset, 
     * else from 0 to end */
    long lPrimaryEnd = getZipFile().getDiskFile().length();
    if (feHeader != null)
      lPrimaryEnd = feHeader.getOffset();
    /* now determine the digest */
    byte[] bufDigest = getZipFile().getDiskFile().digest(dtt.value(),0,lPrimaryEnd);
    MessageDigestType md = _of.createMessageDigestType();
    md.setDigestType(dtt);
    md.setDigest(BU.toHex(bufDigest));
    return md;
  } /* getMessageDigest */

  /*------------------------------------------------------------------*/
  /** resets internal state to initial state.
   * Makes it possible to reuse the archive calling open() or create()
   * although that is not really recommended.
   */
  private void reset()
  {
    
    _zipFile = null;
    _sPreviousMetaDataVersion = Archive.sMETA_DATA_VERSION;
    _bModifyPrimaryData = false;
    _bMetaDataModified = false;
    _iMaxInlineSize = Archive.iDEFAULT_MAX_INLINE_SIZE;
    _iMaxLobsPerFolder = -1; // negative = unlimited
    _md = null;
  } /* reset */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void saveMetaData()
    throws IOException
  {
    if (_bMetaDataModified)
    {
      /* version folder */
      MetaData md = getMetaData();
      if (!md.getVersion().equals(Archive.sMETA_DATA_VERSION))
      {
        getZipFile().delete(getMetaDataXsl());
        getZipFile().delete(getMetaDataXsd());
        /* create version stamp */
        String sVersionFolder = _sHEADER_FOLDER+sPRONOM_ID+"/"+Archive.sMETA_DATA_VERSION+"/";
        createFolderEntry(sVersionFolder);
        /* copy metadata.xsd to header */
        OutputStream eos = createFileEntry(getMetaDataXsd());
        exportMetaDataSchema(eos);
        /* copy generic table.xsd to header */
        eos = createFileEntry(getGenericTableXsd());
        exportGenericTableSchema(eos);
        _sPreviousMetaDataVersion = Archive.sMETA_DATA_VERSION;
      }
      /* default version */
      FileEntry feMetadata = getZipFile().getFileEntry(getMetaDataXml());
      if (feMetadata != null)
        getZipFile().delete(getMetaDataXml());
      OutputStream eos = createFileEntry(getMetaDataXml());
      exportMetaData(eos,true);
      _bMetaDataModified = false;
    }
  } /* saveMetaData */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void close()
    throws IOException
  {
    if (getZipFile() != null)
    {
      if (canModifyPrimaryData())
      {
        /* compute digest */
        MetaDataImpl mdi = (MetaDataImpl)getMetaData();
        mdi.setMessageDigest(getMessageDigest(_dttDEFAULT_DIGEST_ALGORITHM));
        /* create header folder */
        createFolderEntry(_sHEADER_FOLDER);
        /* create version stamp */
        String sVersionFolder = _sHEADER_FOLDER+sPRONOM_ID+"/"+Archive.sMETA_DATA_VERSION+"/";
        createFolderEntry(sVersionFolder);
        /* copy metadata.xsd to header */
        OutputStream eos = createFileEntry(getMetaDataXsd());
        exportMetaDataSchema(eos);
        /* copy generic table.xsd to header */
        eos = createFileEntry(getGenericTableXsd());
        exportGenericTableSchema(eos);
      }
      /* if meta data have been modified, then save them */
      saveMetaData();
      getZipFile().close();
      reset();
    }
    else
      throw new IOException("Archive was not open!");
  } /* close */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public int getSchemas() { return getMetaData().getMetaSchemas(); }
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public Schema getSchema(int iSchema) 
  { 
    Schema schema = null;
    MetaSchema ms = getMetaData().getMetaSchema(iSchema);
    if (ms != null) // can happen on Archive.open()
    {
      String sName = ms.getName();
      schema = getSchema(sName);
    }
    return schema;
  } /* getSchema */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public Schema getSchema(String sName) { return _mapSchemas.get(sName); }
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Schema createSchema(String sName)
    throws IOException
  {
    Schema schema = null;
    if (canModifyPrimaryData())
    {
      /* meta schema is created when schema is instantiated */
      if (_mapSchemas.get(sName) == null)
      {
        schema = SchemaImpl.newInstance(this, sName);
        // handle template
        MetaDataImpl mdi = (MetaDataImpl)getMetaData();
        SiardArchive saTemplate = mdi.getTemplate();
        if (saTemplate != null)
        {
          SchemasType sts = saTemplate.getSchemas();
          if (sts != null)
          {
            SchemaType stTemplate = null;
            for (int iSchema = 0; iSchema < sts.getSchema().size(); iSchema++)
            {
              SchemaType stTry = sts.getSchema().get(iSchema);
              if (sName.equals(stTry.getName()))
                stTemplate = stTry;
            }
            if (stTemplate != null)
            {
              MetaSchemaImpl msi = (MetaSchemaImpl)schema.getMetaSchema();
              msi.setTemplate(stTemplate);
            }
          }
        }
        
      }
      else
        throw new IOException("Schema name must be unique within database!");
    }
    else
      throw new IOException("Schema cannot be created!\r\n"+
        "SIARD archive is not open for modification of primary data!");
    return schema;
  } /* createSchema */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public boolean isEmpty()
  {
    boolean bEmpty = true;
    for (Iterator<String> iterSchema = _mapSchemas.keySet().iterator(); iterSchema.hasNext(); )
    {
      String sName = iterSchema.next();
      Schema schema = getSchema(sName);
      if (!schema.isEmpty())
        bEmpty = false;
    }
    return bEmpty;
  } /* isEmpty */

  /*------------------------------------------------------------------*/
  /** check validity of archive.
   */
  private void validate()
  {
    _swValid.start();
    _bValid = getMetaData().isValid();
    if (_bValid && (getSchemas() < 1))
      _bValid = false;
    for (int iSchema = 0; _bValid && (iSchema < getSchemas()); iSchema++)
    {
      Schema schema = getSchema(iSchema);
      if (schema == null)
        _bValid = false; 
      else if (!schema.isValid())
        _bValid = false; 
    }
    _swValid.stop();
  } /* validate */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public boolean isValid()
  {
    if (canModifyPrimaryData())
      validate();
    return _bValid;
  } /* isValid */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public boolean isPrimaryDataUnchanged()
    throws IOException
  {
    boolean bUnchanged = false;
    for (int i = 0; i < getMetaData().getMessageDigest().size(); i++)
    {
      MessageDigestType md = getMetaData().getMessageDigest().get(i);
      bUnchanged = md.getDigest().equals(getMessageDigest(md.getDigestType()).getDigest());
    }
    return bUnchanged;  
  } /* isPrimaryDataUnchanged */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  public boolean isMetaDataUnchanged()
  {
    return !_bMetaDataModified;
  } /* isMetaDataUnchanged */

} /* ArchiveImpl */
