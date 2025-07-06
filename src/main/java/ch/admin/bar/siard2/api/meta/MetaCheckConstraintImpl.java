/*== MetaCheckConstraintImpl.java ======================================
MetaCheckConstraintImpl implements the interface MetaCheckConstraint.
Application : SIARD 2.0
Description : MetaCheckConstraintImpl implements the interface MetaCheckConstraint.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 27.06.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api.meta;

import java.io.*;

import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.api.generated.*;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.enterag.utils.*;
import ch.enterag.utils.xml.XU;

/*====================================================================*/
/** MetaCheckConstraintImpl implements the interface MetaCheckConstraint.
 @author Hartwig Thomas
 */
public class MetaCheckConstraintImpl
  extends MetaSearchImpl
  implements MetaCheckConstraint
{
  private MetaTable _mtParent;
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public MetaTable getParentMetaTable() { return _mtParent; }
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public boolean isValid() { return getCondition() != null; }
  
  /*------------------------------------------------------------------*/
  /** get archive
   * @return archive.
   */
  private ArchiveImpl getArchiveImpl()
  {
    return (ArchiveImpl)getParentMetaTable().getTable().getParentSchema().getParentArchive();
  } /* getArchive */
  
  private CheckConstraintType _cct;

  /*------------------------------------------------------------------*/
  /** set template meta data.
   * @param cctTemplate template data.
   */
  public void setTemplate(CheckConstraintType cctTemplate)
  {
    if (!SU.isNotEmpty(getDescription()))
      setDescription(XU.fromXml(cctTemplate.getDescription()));
  } /* setTemplate */
  
  /*------------------------------------------------------------------*/
  /** constructor
   * @param mtParent table meta data object of SIARD archive.
   * @param cct CheckConstraintType instance (JAXB).
   */
  private MetaCheckConstraintImpl(MetaTable mtParent, CheckConstraintType cct)
  {
    _mtParent = mtParent;
    _cct = cct;
  } /* constructor MetaCheckConstraintImpl */
  
  /*------------------------------------------------------------------*/
  /** factory
   * @param mtParent table meta data object of SIARD archive.
   * @param cct CheckConstraintType instance (JAXB).
   * @return new MetaCheckConstraint instance.
   */
  public static MetaCheckConstraint newInstance(MetaTable mtParent, CheckConstraintType cct)
  {
    return new MetaCheckConstraintImpl(mtParent,cct);
  } /* newInstance */

  /* property Name */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public String getName() { return XU.fromXml(_cct.getName()); }
  
  /* property Condition */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setCondition(String sCondition)
    throws IOException
  {
    if (getArchiveImpl().canModifyPrimaryData())
    {
      if (getArchiveImpl().isMetaDataDifferent(getCondition(),sCondition))
        _cct.setCondition(XU.toXml(sCondition));
    }
    else
      throw new IOException("Condition cannot be set!");
  } /* setCondition */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public String getCondition() { return XU.fromXml(_cct.getCondition()); }
  
  /* property Description */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override 
  public void setDescription(String sDescription) 
  { 
    if (getArchiveImpl().isMetaDataDifferent(getDescription(), sDescription))
      _cct.setDescription(XU.toXml(sDescription)); 
  } /* setDescription */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public String getDescription() { return XU.fromXml(_cct.getDescription()); }
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public String[] getSearchElements(DU du)
    throws IOException
  { 
    return new String[] 
      {
        getName(),
        getCondition(),
        getDescription()
      };
  } /* getSearchElements */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc}
   * toString() returns the name of the check constraint which is to be 
   * displayed as the label of the check constraint node of the tree 
   * displaying the archive.   
   */
  @Override 
  public String toString()
  {
    return getName();
  }
} /* class MetaCheckConstraintImpl */
