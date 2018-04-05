/*== MetaUniqueKeyImpl.java =========================================
MetaUniqueKeyImpl implements the interface MetaUniqueKey.
Application : SIARD 2.0
Description : MetaUniqueKeyImpl implements the interface MetaUniqueKey.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 27.06.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api.meta;

import java.io.*;

import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.api.generated.*;
import ch.admin.bar.siard2.api.primary.*;
import ch.enterag.utils.*;
import ch.enterag.utils.xml.XU;

/*====================================================================*/
/** MetaUniqueKeyImpl implements the interface MetaUniqueKey.
 @author Hartwig Thomas
 */
public class MetaUniqueKeyImpl
  extends MetaSearchImpl
  implements MetaUniqueKey
{
  private MetaTable _mtParent;
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public MetaTable getParentMetaTable() { return _mtParent; }
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public boolean isValid() { return getColumns() > 0; }
  
  /*------------------------------------------------------------------*/
  /** get archive
   * @return archive.
   */
  private ArchiveImpl getArchive()
  {
    return (ArchiveImpl)getParentMetaTable().getTable().getParentSchema().getParentArchive();
  } /* getArchive */

  private UniqueKeyType _ukt;

  /*------------------------------------------------------------------*/
  /** set template meta data.
   * @param uktTemplate template data.
   */
  public void setTemplate(UniqueKeyType uktTemplate)
  {
    if (!SU.isNotEmpty(getDescription()))
      setDescription(XU.fromXml(uktTemplate.getDescription()));
  } /* setTemplate */
  
  /*------------------------------------------------------------------*/
  /** constructor
   * @param mtParent table meta data object of SIARD archive.
   * @param ukt UniqueKeyType instance (JAXB).
   */
  private MetaUniqueKeyImpl(MetaTable mtParent, UniqueKeyType ukt)
  {
    _mtParent = mtParent;
    _ukt = ukt;
  } /* constructor MetaUniqueKeyImpl */
  
  /*------------------------------------------------------------------*/
  /** factory
   * @param mtParent table meta data object of SIARD archive.
   * @param ukt UniqueKeyType instance (JAXB).
   * @return new MetaUniqueKey instance.
   */
  public static MetaUniqueKey newInstance(MetaTable mtParent, UniqueKeyType ukt)
  {
    return new MetaUniqueKeyImpl(mtParent,ukt);
  } /* newInstance */

  /* property Name */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public String getName() { return XU.fromXml(_ukt.getName()); }
  
  /* list property column */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public int getColumns() { return _ukt.getColumn().size(); }
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public String getColumn(int iColumn) { return XU.fromXml(_ukt.getColumn().get(iColumn)); }
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void addColumn(String sColumn)
    throws IOException
  {
    if (getArchive().canModifyPrimaryData())
    {
      _ukt.getColumn().add(XU.toXml(sColumn));
      getArchive().isMetaDataDifferent(null,sColumn);
    }
    else
      throw new IOException("Column cannot be set!");
  } /* addColumn */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public String getColumnsString()
  {
    StringBuilder sbColumns = new StringBuilder();
    for (int iColumn = 0; iColumn < getColumns(); iColumn++)
    {
      if (iColumn > 0)
        sbColumns.append(", ");
      sbColumns.append(getColumn(iColumn));
    }
    return sbColumns.toString();
  } /* getColumnsString */
  
  /* property Description */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override 
  public void setDescription(String sDescription)
  { 
    if (getArchive().isMetaDataDifferent(getDescription(),sDescription))
      _ukt.setDescription(XU.toXml(sDescription)); 
  } /* setDescription */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public String getDescription() { return XU.fromXml(_ukt.getDescription()); }
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public String[] getSearchElements(DU du)
    throws IOException
  { 
    return new String[] {
      getName(),
      getColumnsString(),
      getDescription()
    };
  } /* getSearchElements */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc}
   * toString() returns the name of the unique key which is to be displayed 
   * as the label of the unique key (primary or candidate) node of the 
   * tree displaying the archive.   
   */
  @Override 
  public String toString()
  {
    String s = null;
    if (this == getParentMetaTable().getMetaPrimaryKey())
      s = "primary key";
    else
      s = getName();
    return s;
  }
} /* class MetaUniqueKeyImpl */
