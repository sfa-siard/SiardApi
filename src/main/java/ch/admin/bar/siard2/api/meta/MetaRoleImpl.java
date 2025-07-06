/*== MetaRoleImpl.java =================================================
MetaRoleImpl implements the interface MetaRole.
Application : SIARD 2.0
Description : MetaRoleImpl implements the interface MetaRole.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 24.06.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api.meta;

import java.io.IOException;

import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.api.generated.*;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.enterag.utils.*;
import ch.enterag.utils.xml.XU;

/*====================================================================*/
/** MetaRoleImpl implements the interface MetaRole.
 @author Hartwig Thomas
 */
public class MetaRoleImpl
  extends MetaSearchImpl
  implements MetaRole
{
  private MetaData _mdParent = null;
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public MetaData getParentMetaData() { return _mdParent; }

  private RoleType _rt = null;

  /*------------------------------------------------------------------*/
  /** get archive
   * @return archive.
   */
  private ArchiveImpl getArchiveImpl()
  {
    return (ArchiveImpl)getParentMetaData().getArchive();
  } /* getArchive */
  
  /*------------------------------------------------------------------*/
  /** set template meta data.
   * @param rtTemplate
   */
  public void setTemplate(RoleType rtTemplate)
  {
    if (!SU.isNotEmpty(getDescription()))
      setDescription(XU.fromXml(rtTemplate.getDescription()));
  } /* setTemplate */

  /*------------------------------------------------------------------*/
  /** constructor
   * @param mdParent global meta data object of SIARD archive.
   * @param rt RoleType instance (JAXB).
   */
  private MetaRoleImpl(MetaData mdParent, RoleType rt)
  {
    _mdParent = mdParent;
    _rt = rt;
  } /* constructor MetaRoleImpl */
  
  /*------------------------------------------------------------------*/
  /** factory
   * @param mdParent global meta data object of SIARD archive.
   * @param rt RoleType instance (JAXB).
   * @return new MetaRole instance.
   */
  public static MetaRole newInstance(MetaData mdParent, RoleType rt)
  {
    return new MetaRoleImpl(mdParent,rt);
  } /* newInstance */

  /* property Name */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public String getName() { return XU.fromXml(_rt.getName()); }
  
  /* property Admin */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override 
  public void setAdmin(String sAdmin)
    throws IOException
  {
    if (getArchiveImpl().canModifyPrimaryData())
    {
      if (getArchiveImpl().isMetaDataDifferent(getAdmin(),sAdmin))
        _rt.setAdmin(XU.toXml(sAdmin));
    }
    else
      throw new IOException("Admin name cannot be set!");
  } /* setAdmin */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public String getAdmin() { return XU.fromXml(_rt.getAdmin()); }
  
  /* property Description */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override 
  public void setDescription(String sDescription)
  { 
    if (getArchiveImpl().isMetaDataDifferent(getDescription(),sDescription))
      _rt.setDescription(XU.toXml(sDescription));
  }
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public String getDescription() { return XU.fromXml(_rt.getDescription()); }
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public String[] getSearchElements(DU du)
    throws IOException
  { 
    return new String[]
    {
      getName(),
      getAdmin(),
      getDescription()
    };
  } /* getSearchElements */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc}
   * toString() returns the name of the role which is to be displayed 
   * as the label of the role node of the tree displaying the archive.   
   */
  @Override 
  public String toString()
  {
    return getName();
  }
} /* class MetaRoleImpl */
