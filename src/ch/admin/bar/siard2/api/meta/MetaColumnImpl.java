/*== MetaColumnImpl.java ===============================================
MetaColumnImpl implements the interface MetaColumn.
Application : SIARD 2.0
Description : MetaColumnImpl implements the interface MetaColumn.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 27.06.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api.meta;

import java.io.*;
import java.math.*;
import java.net.*;
import java.util.*;
import java.util.regex.Matcher;

import ch.enterag.utils.*;
import ch.enterag.utils.xml.XU;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.datatype.*;
import ch.enterag.sqlparser.datatype.enums.*;
import ch.enterag.sqlparser.ddl.enums.*;
import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.api.generated.*;
import ch.admin.bar.siard2.api.primary.*;

/*====================================================================*/
/** MetaColumnImpl implements the interface MetaColumn.
 @author Hartwig Thomas
 */
public class MetaColumnImpl
  extends MetaValueImpl
  implements MetaColumn
{
  private static final long lKILO = 1024;
  private static final long lMEGA = lKILO*lKILO;
  private static final long lGIGA = lKILO*lMEGA;
  public static final String _sLOB_FOLDER_PREFIX = "lob";
  private static ObjectFactory _of = new ObjectFactory();
  private Map<String,MetaField> _mapMetaFields = new HashMap<String,MetaField>();
  private Map<String,MetaField> getMetaFieldsMap()
    throws IOException
  {
    if (getArchiveImpl().canModifyPrimaryData())
    {
      if (getCardinality() < 0)
      {
        MetaType mt = getMetaType();
        if (mt != null)
        {
          CategoryType cat = mt.getCategoryType();
          if (cat != CategoryType.DISTINCT)
          {
            for (int i = _mapMetaFields.size(); i < mt.getMetaAttributes(); i++)
              createMetaField();
          }
        }
      }
    }
    return _mapMetaFields;
  } /* getMetaFieldsMap */
  
  private String _sFolder = null; // internal folder for LOB data

  private MetaTable _mtParent = null;
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public MetaTable getParentMetaTable() { return _mtParent; }
  
  private MetaView _mvParent = null;
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public MetaView getParentMetaView() { return _mvParent; }
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public MetaColumn getAncestorMetaColumn() { return this; }
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public boolean isValid() { return (getType() != null) || (getTypeName() != null); }
  
  private ColumnType _ct = null;
  public ColumnType getColumnType()
    throws IOException
  {
    for (int iField = 0; iField < getMetaFields(); iField++)
    {
      MetaField mf = getMetaField(iField);
      ((MetaFieldImpl)mf).getFieldType();
    }
    return _ct;
  } /* getColumnType */

  /*------------------------------------------------------------------*/
  /** get table.
   * @return table.
   */
  private Table getTable()
  {
    Table table = null;
    if (getParentMetaTable() != null)
      table = getParentMetaTable().getTable();
    return table;
  } /* getTable */
  
  /*------------------------------------------------------------------*/
  /** get archive
   * @return archive.
   */
  private ArchiveImpl getArchiveImpl()
  {
    Archive archive = null;
    if (getParentMetaTable() != null)
      archive = getParentMetaTable().getTable().getParentSchema().getParentArchive();
    else if (getParentMetaView() != null)
      archive = getParentMetaView().getParentMetaSchema().getSchema().getParentArchive();
    return (ArchiveImpl)archive;
  } /* getArchiveImpl */
  
  /*------------------------------------------------------------------*/
  /** get schema meta data.
   * @return schema meta data.
   */
  private MetaSchema getMetaSchema()
  {
    MetaSchema ms = null;
    if (getParentMetaTable() != null)
      ms = getParentMetaTable().getParentMetaSchema();
    else if (getParentMetaView() != null)
      ms = getParentMetaView().getParentMetaSchema();
    return ms;
  }
  
  private ColumnType _ctTemplate = null;
  /*------------------------------------------------------------------*/
  /** set template meta data.
   * @param ctTemplate template data.
   */
  public void setTemplate(ColumnType ctTemplate)
    throws IOException
  {
    _ctTemplate = ctTemplate;
    if (!SU.isNotEmpty(getDescription()))
      setDescription(XU.fromXml(_ctTemplate.getDescription()));
    if (getParentMetaTable() != null)
    {
      if (getArchiveImpl().canModifyPrimaryData())
      {
        if ((getLobFolder() == null) && SU.isNotEmpty(_ctTemplate.getLobFolder()))
          setLobFolder(URI.create(XU.fromXml(_ctTemplate.getLobFolder())));
        if ((getMimeType() == null) && SU.isNotEmpty(_ctTemplate.getMimeType()))
          setMimeType(XU.fromXml(_ctTemplate.getMimeType()));
      }
    }
    FieldsType fts = _ctTemplate.getFields();
    if (fts != null)
    {
      /** look up the type schema / type name and get its attribute list */
      for (int iField = 0; iField < fts.getField().size(); iField++)
      {
        FieldType ftTemplate = fts.getField().get(iField);
        String sName = XU.fromXml(ftTemplate.getName());
        MetaField mf = getMetaField(sName);
        if (mf != null)
        {
          MetaFieldImpl mfi = (MetaFieldImpl)mf;
          mfi.setTemplate(ftTemplate);
        }
      }
    }
  } /* setTemplate */
  
  /*------------------------------------------------------------------*/
  /** open all sub field meta data.
   * @throws IOException if an I/O error occurred.
   */
  private void openMetaFields()
    throws IOException
  {
    FieldsType fts = _ct.getFields();
    if (fts != null)
    {
      for (int iField = 0; iField < fts.getField().size(); iField++)
      {
        FieldType ft = fts.getField().get(iField);
        MetaField mf = MetaFieldImpl.newInstance(this, ft, _sFolder, iField+1);
        _mapMetaFields.put(mf.getName(),mf);
      }
    }
  } /* openMetaFields */
  
  /*------------------------------------------------------------------*/
  /** constructor
   * @param mtParent parent table meta data object of SIARD archive.
   * @param ct ColumnType instance (JAXB).
   * @param iPosition position (1-based) of column.
   * @throws IOException if the existing field meta data could not be 
   * matched to attributes. 
   */
  private MetaColumnImpl(MetaTable mtParent, ColumnType ct, int iPosition)
    throws IOException
  {
    super(iPosition);
    _mtParent = mtParent;
    _ct = ct;
    _sFolder = _sLOB_FOLDER_PREFIX+String.valueOf(iPosition-1)+"/";
    openMetaFields();
  } /* constructor MetaColumnImpl */
  
  /*------------------------------------------------------------------*/
  /** constructor
   * @param mvParent parent view meta data object of SIARD archive.
   * @param ct ColumnType instance (JAXB).
   * @param iPosition position (1-based) of column.
   * @throws IOException if the existing field meta data could not be 
   * matched to attributes. 
   */
  private MetaColumnImpl(MetaView mvParent, ColumnType ct, int iPosition)
    throws IOException
  {
    super(iPosition);
    _mvParent = mvParent;
    _ct = ct;
    openMetaFields();
  } /* constructor MetaColumnImpl */
  
  /*------------------------------------------------------------------*/
  /** factory
   * @param mtParent parent table meta data object of SIARD archive.
   * @param ct ColumnType instance (JAXB).
   * @param iPosition position (1-based) of column.
   * @return new MetaColumn instance.
   * @throws IOException if the existing field meta data could not be 
   * matched to attributes. 
   */
  public static MetaColumn newInstance(MetaTable mtParent, int iPosition, ColumnType ct)
    throws IOException
  {
    return new MetaColumnImpl(mtParent,ct,iPosition);
  } /* newInstance */

  /*------------------------------------------------------------------*/
  /** factory
   * @param mvParent parent view meta data object of SIARD archive.
   * @param ct ColumnType instance (JAXB).
   * @param iPosition position (1-based) of column.
   * @return new MetaColumn instance.
   * @throws IOException if the existing field meta data could not be 
   * matched to attributes. 
   */
  public static MetaColumn newInstance(MetaView mvParent, int iPosition, ColumnType ct)
    throws IOException
  {
    return new MetaColumnImpl(mvParent,ct,iPosition);
  } /* newInstance */

  /* property Name */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public String getName() { return XU.fromXml(_ct.getName()); }

  /*------------------------------------------------------------------*/
  /** get relative internal lob folder for this column or null, if it
   * is the column of a view or an external LOB folder is set.
   * @return relative internal lob folder.
   */
  public String getFolder() 
  {
    String sFolder = null;
    if (getLobFolder() == null)
      sFolder = _sFolder;
    return sFolder;
  } /* getFolder */
  
  /* property LobFolder */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override 
  public void setLobFolder(URI uriLobFolder) 
    throws IOException
  {
    boolean bMayBeSet = false;
    if (getLobFolder() == null)
    {
      if ((getTable() != null) && getTable().isEmpty())
        bMayBeSet = true;
    }
    else
      bMayBeSet = true;
    if (bMayBeSet)
    {
      if (getArchiveImpl().isMetaDataDifferent(getLobFolder(),uriLobFolder))
      {
        if (uriLobFolder != null)
        {
          MetaDataImpl mdi = (MetaDataImpl)(getParentMetaTable().getTable().getParentSchema().getParentArchive().getMetaData());
          if (uriLobFolder.getPath().endsWith("/"))
          {
            if (uriLobFolder.isAbsolute())
            {
              if (uriLobFolder.getScheme() == null)
              {
                try { uriLobFolder = new URI(MetaDataImpl._sURI_SCHEME_FILE,"",uriLobFolder.getPath(),null); }
                catch(URISyntaxException use) { }
              }
              if (!uriLobFolder.getScheme().equals(MetaDataImpl._sURI_SCHEME_FILE))
                throw new IllegalArgumentException("Only URIs with scheme \""+MetaDataImpl._sURI_SCHEME_FILE+"\" allowed for LOB folder!");
            }
            else if (mdi.getLobFolder() == null)
            {
              if (!uriLobFolder.getPath().startsWith("../"))
                throw new IllegalArgumentException("Relative LOB folder URIs must start with \"..\"!");
            }
            _ct.setLobFolder(XU.toXml(uriLobFolder.toString()));
          }
          else
            throw new IllegalArgumentException("Path of LOB folder URI must denote a folder (end with \"/\")!");
        }
        else
          throw new IllegalArgumentException("LOB folder URI must not be null!");
      }      
    }
    else
      throw new IOException("LOB folder value cannot be set!");
  } /* setLobFolder */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override 
  public URI getLobFolder()
  { 
    URI uriLobFolder = null;
    if (_ct.getLobFolder() != null)
    {
      try { uriLobFolder =  new URI(XU.fromXml(_ct.getLobFolder())); }
      catch(URISyntaxException use) { } 
    }
    return uriLobFolder;
  } /* getLobFolder */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override 
  public URI getAbsoluteLobFolder() 
  { 
    URI uriLocal = getLobFolder();
    MetaDataImpl mdi = (MetaDataImpl)(getArchiveImpl().getMetaData());
    if (uriLocal != null)
    {
      if (!uriLocal.isAbsolute())
      {
        URI uriGlobal = mdi.getLobFolder();
        if (uriGlobal != null)
        {
          uriGlobal = mdi.getAbsoluteUri(uriGlobal);
          uriLocal = uriGlobal.resolve(uriLocal);
        }
        else
          uriLocal = mdi.getAbsoluteUri(uriLocal);
      }
    }
    return uriLocal;
  } /* getAbsoluteLobFolder */
  
  /* property Type */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setType(String sType)
    throws IOException
  {
    ArchiveImpl ai = getArchiveImpl();
    if (ai.canModifyPrimaryData())
    {
      if (ai.isMetaDataDifferent(getTypeSchema(),null))
        _ct.setTypeSchema(null);
      if (ai.isMetaDataDifferent(getTypeName(),null))
        _ct.setTypeName(null);
      SqlFactory sf = new BaseSqlFactory();
      PredefinedType pt = new PredefinedType(sf);
      pt.parse(sType);
      sType = pt.format();
      if (ai.isMetaDataDifferent(getType(),sType))
        _ct.setType(XU.toXml(pt.format()));
    }
    else
      throw new IOException("Type cannot be set!");
  } /* setType */ 
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setPreType(int iDataType, long lPrecision, int iScale)
    throws IOException
  {
    SqlFactory sf = new BaseSqlFactory();
    PredefinedType prt = sf.newPredefinedType();
    prt.initialize(iDataType, lPrecision, iScale);
    String sType = prt.format();
    setType(sType);
  } /* setPreType */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public String getType() { return XU.fromXml(_ct.getType()); }
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public int getPreType()
    throws IOException
  {
    int iDataType = java.sql.Types.NULL;
    MetaType mt = getMetaType();
    CategoryType cat = null;
    if (mt != null)
      cat = mt.getCategoryType();
    String sType = getType();
    if (sType != null)
    {
      SqlFactory sf = new BaseSqlFactory();
      PredefinedType prt = sf.newPredefinedType();
      prt.parse(sType);
      PreType pt = prt.getType();
      iDataType = pt.getSqlType();
    }
    else if (cat == CategoryType.DISTINCT)
      iDataType = mt.getBasePreType();
    return iDataType;
  } /* getPreType */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public long getLength()
    throws IOException
  {
    long lLength = -1;
    MetaType mt = getMetaType();
    CategoryType cat = null;
    if (mt != null)
      cat = mt.getCategoryType();
    String sType = getType();
    if (sType != null)
    {
      SqlFactory sf = new BaseSqlFactory();
      PredefinedType prt = sf.newPredefinedType();
      prt.parse(sType);
      lLength = prt.getLength();
      if (lLength != PredefinedType.iUNDEFINED)
      {
        Multiplier mult = prt.getMultiplier();
        if (mult != null)
        {
          switch (mult)
          {
            case K: lLength = lLength * lKILO; break;
            case M: lLength = lLength * lMEGA; break;
            case G:  lLength = lLength * lGIGA; break;
          }
        }
      }
      else
        lLength = prt.getPrecision();
    }
    else if (cat == CategoryType.DISTINCT)
      lLength = mt.getBaseLength();
    return lLength;
  } /* getLength */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public int getScale()
    throws IOException
  {
    int iScale = -1;
    MetaType mt = getMetaType();
    CategoryType cat = null;
    if (mt != null)
      cat = mt.getCategoryType();
    String sType = getType();
    if (sType != null)
    {
      SqlFactory sf = new BaseSqlFactory();
      PredefinedType prt = sf.newPredefinedType();
      prt.parse(sType);
      iScale = prt.getScale();
    }
    else if (cat == CategoryType.DISTINCT)
      iScale = mt.getBaseScale();
    return iScale;
  } /* getScale */
  
  /* property TypeOriginal */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setTypeOriginal(String sTypeOriginal)
    throws IOException
  {
    if (getArchiveImpl().canModifyPrimaryData())
    {
      if (getArchiveImpl().isMetaDataDifferent(getTypeOriginal(),sTypeOriginal))
        _ct.setTypeOriginal(XU.toXml(sTypeOriginal));
    }
    else
      throw new IOException("Original type cannot be set!");
  } /* setTypeOriginal */ 
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public String getTypeOriginal() { return XU.fromXml(_ct.getTypeOriginal()); }

  /* property Nullable */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setNullable(boolean bNullable)
    throws IOException
  {
    if (getArchiveImpl().canModifyPrimaryData() && (getTable() != null) && getTable().isEmpty())
    {
      if (getArchiveImpl().isMetaDataDifferent(Boolean.valueOf(isNullable()),Boolean.valueOf(bNullable)))
        _ct.setNullable(bNullable);
    }
    else
      throw new IOException("Nullability cannot be set!");
  } /* setNullable */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public boolean isNullable() 
  {
    boolean bNullable = true; // default
    if (_ct.isNullable() != null)
      bNullable = _ct.isNullable().booleanValue();
    return bNullable;
  } /* isNullable */

  /* property DefaultValue */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setDefaultValue(String sDefaultValue)
    throws IOException
  {
    if (getArchiveImpl().canModifyPrimaryData() && (getTable() != null) && getTable().isEmpty())
    {
      if (getArchiveImpl().isMetaDataDifferent(getDefaultValue(),sDefaultValue))
        _ct.setDefaultValue(XU.toXml(sDefaultValue));
    }
    else
      throw new IOException("Default value cannot be set!");
  } /* setDefaultValue */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public String getDefaultValue() { return XU.fromXml(_ct.getDefaultValue()); }
  
  /* property MimeType */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setMimeType(String sMimeType)
    throws IOException
  {
    if (getArchiveImpl().canModifyPrimaryData())
    {
      if (getArchiveImpl().isMetaDataDifferent(getMimeType(),sMimeType))
        _ct.setMimeType(XU.toXml(sMimeType));
    }
    else
      throw new IOException("MIME type cannot be set!");
  } /* setMimeType */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public String getMimeType() { return XU.fromXml(_ct.getMimeType()); }
  
  /* property TypeSchema */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setTypeSchema(String sTypeSchema)
    throws IOException
  {
    if (getArchiveImpl().canModifyPrimaryData())
    {
      if (getArchiveImpl().isMetaDataDifferent(getType(),null))
        _ct.setType(null);
      if (getArchiveImpl().isMetaDataDifferent(getTypeSchema(),sTypeSchema))
        _ct.setTypeSchema(XU.toXml(sTypeSchema));
    }
    else
      throw new IOException("Type schema cannot be set!");
  } /* setTypeSchema */ 
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public String getTypeSchema() { return XU.fromXml(_ct.getTypeSchema()); }
  
  /* property TypeName */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setTypeName(String sTypeName)
    throws IOException
  {
    if (getArchiveImpl().canModifyPrimaryData())
    {
      if (getArchiveImpl().isMetaDataDifferent(getType(),null))
        _ct.setType(null);
      if (getArchiveImpl().isMetaDataDifferent(getTypeName(),sTypeName))
      {
        _ct.setTypeName(XU.toXml(sTypeName));
        if (getTypeSchema() == null)
          setTypeSchema(getMetaSchema().getName());
      }
    }
    else
      throw new IOException("Type name cannot be set!");
  } /* setTypeName */ 
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public String getTypeName() { return XU.fromXml(_ct.getTypeName()); }
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public MetaType getMetaType()
  {
    MetaType mt = null;
    if (getTypeName() != null)
    {
      Schema schema = getArchiveImpl().getSchema(getTypeSchema());
      if (schema != null)
        mt = schema.getMetaSchema().getMetaType(getTypeName());
    }
    return mt;
  } /* getMetaType */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public int getMetaFields()
    throws IOException
  {
    return getMetaFieldsMap().size();
  } /* getMetaFields */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public MetaField getMetaField(int iField)
    throws IOException
  {
    MetaField mf = null;
    /* dynamically expand the number of array elements */
    if (getCardinality() > 0)
    {
      for (int i = _mapMetaFields.size(); i < iField+1; i++)
        createMetaField();
    }
    for (Iterator<String> iterField = getMetaFieldsMap().keySet().iterator(); (mf == null) && iterField.hasNext(); )
    {
      String sName = iterField.next();
      MetaFieldImpl mfi = (MetaFieldImpl)getMetaField(sName);
      if (_ct.getFields().getField().get(iField) == mfi._ft)
        mf = mfi;
    }
    return mf;
  } /* getMetaField */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public MetaField getMetaField(String sName)
    throws IOException
  {
    /* dynamically expand the number of array elements */
    if (getCardinality() > 0)
    {
      Matcher match = _patARRAY_INDEX.matcher(sName);
      if (match.matches())
      {
        int iIndex = Integer.parseInt(match.group(1));
        for (int i = _mapMetaFields.size(); i < iIndex; i++)
          createMetaField();
      }
    }
    return getMetaFieldsMap().get(sName);
  } /* getMetaField */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public MetaField createMetaField()
    throws IOException
  {
    MetaField mf = null;
    if (getArchiveImpl().canModifyPrimaryData())
    {
      FieldsType fts = _ct.getFields();
      if (fts == null)
      {
        fts = _of.createFieldsType();
        _ct.setFields(fts);
      }
      FieldType ft = _of.createFieldType();
      fts.getField().add(ft);
      int iPosition = _mapMetaFields.size()+1;
      String sName = getName()+"["+String.valueOf(iPosition)+"]";
      if (getCardinality() < 0)
      {
        MetaType mt = getMetaType();
        MetaAttribute ma = mt.getMetaAttribute(iPosition-1);
        if (ma != null)
          sName = ma.getName();
      }
      ft.setName(XU.toXml(sName));
      mf = MetaFieldImpl.newInstance(this, ft,_sFolder,iPosition);
      _mapMetaFields.put(mf.getName(),mf);
      getArchiveImpl().isMetaDataDifferent(null,mf);
      if (_ctTemplate != null)
      {
        FieldsType ftsTemplate = _ctTemplate.getFields();
        if (ftsTemplate != null)
        {
          FieldType ftTemplate = null;
          for (int iField = 0; iField < ftsTemplate.getField().size(); iField++)
          {
            FieldType ftTry = ftsTemplate.getField().get(iField);
            if (sName.equals(ftTry.getName()))
              ftTemplate = ftTry;
          }
          if (ftTemplate != null)
          {
            MetaFieldImpl mfi = (MetaFieldImpl)mf;
            mfi.setTemplate(ftTemplate);
          }
        }
      }
    }
    else
      throw new IOException("New field cannot be added!");
    return mf;
  } /* createMetaField */

  /* property Cardinality */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setCardinality(int iCardinality)
    throws IOException
  {
    if (getArchiveImpl().canModifyPrimaryData() && ((getTable() == null) || getTable().isEmpty()))
    {
      if (getArchiveImpl().isMetaDataDifferent(getCardinality(),iCardinality))
        _ct.setCardinality(BigInteger.valueOf((long)iCardinality));
    }
    else
      throw new IOException("Cardinality cannot be set!");
  } /* setCardinality */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public int getCardinality()
    throws IOException
  {
    int iCardinality = -1;
    BigInteger bi = _ct.getCardinality();
    if (bi != null)
      iCardinality = bi.intValue();
    return iCardinality;
  } /* getCardinality */
  
  /* property Description */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override 
  public void setDescription(String sDescription) 
  { 
    if (getArchiveImpl().isMetaDataDifferent(getDescription(),sDescription))
      _ct.setDescription(XU.toXml(sDescription));
  } /* setDescription */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public String getDescription() { return XU.fromXml(_ct.getDescription()); }

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public String[] getSearchElements(DU du)
    throws IOException
  { 
    return new String[] 
      {
        getName(),
        String.valueOf(getPosition()),
        getLobFolder() == null? "": getLobFolder().toString(),
        getMimeType(),
        getType(),
        getTypeSchema(),
        getTypeName(),
        getTypeOriginal(),
        String.valueOf(isNullable()),
        getDefaultValue(),
        (getCardinality() <= 0)? "": String.valueOf(getCardinality()),
        getDescription()
      };
  } /* getSearchElements */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc}
   * toString() returns the name of the column which is to be displayed 
   * as the label of the column node of the tree displaying the archive.   
   */
  @Override 
  public String toString()
  {
    return getName();
  }
} /* class MetaColumnImpl */
