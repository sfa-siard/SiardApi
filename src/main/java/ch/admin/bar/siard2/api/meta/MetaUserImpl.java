/*== MetaUserImpl.java =================================================
MetaUserImpl implements the interface MetaUser.
Application : SIARD 2.0
Description : MetaUserImpl implements the interface MetaUser.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 24.06.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api.meta;

import java.io.IOException;

import ch.enterag.utils.*;
import ch.enterag.utils.xml.XU;
import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.api.generated.*;
import ch.admin.bar.siard2.api.primary.*;

/*====================================================================*/
/** MetaUserImpl implements the interface MetaUser.
 @author Hartwig Thomas
 */
public class MetaUserImpl
  extends MetaSearchImpl
  implements MetaUser
{
  private MetaData _mdParent = null;
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public MetaData getParentMetaData() { return _mdParent; }
  
  private UserType _ut = null;

  /*------------------------------------------------------------------*/
  /** get archive
   * @return archive.
   */
  private ArchiveImpl getArchive()
  {
    return (ArchiveImpl)getParentMetaData().getArchive();
  } /* getArchive */
  
  /*------------------------------------------------------------------*/
  /** set template meta data.
   * @param utTemplate
   */
  public void setTemplate(UserType utTemplate)
  {
    if (!SU.isNotEmpty(getDescription()))
      setDescription(XU.fromXml(utTemplate.getDescription()));
  } /* setTemplate */

  /*------------------------------------------------------------------*/
  /** constructor
   * @param mdParent global meta data object of SIARD archive.
   * @param ut UserType instance (JAXB).
   */
  private MetaUserImpl(MetaData mdParent, UserType ut)
  {
    _mdParent = mdParent;
    _ut = ut;
  } /* constructor MetaUserImpl */
  
  /*------------------------------------------------------------------*/
  /** factory
   * @param mdParent global meta data object of SIARD archive.
   * @param ut UserType instance (JAXB).
   * @return new MetaUser instance.
   */
  public static MetaUser newInstance(MetaData mdParent, UserType ut)
  {
    return new MetaUserImpl(mdParent,ut);
  } /* newInstance */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public String getName() { return XU.fromXml(_ut.getName()); }
  
  /* property Description */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override 
  public void setDescription(String sDescription) 
  { 
    if (getArchive().isMetaDataDifferent(getDescription(),sDescription))
      _ut.setDescription(XU.toXml(sDescription)); 
  } /* setDescription */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public String getDescription() { return XU.fromXml(_ut.getDescription()); }
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public String[] getSearchElements(DU du)
    throws IOException
  { 
    return new String[]
    {
      getName(),
      getDescription()
    };
  } /* getSearchElements */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc}
   * toString() returns the name of the user which is to be displayed 
   * as the label of the user node of the tree displaying the archive.   
   */
  @Override 
  public String toString()
  {
    return getName();
  }
} /* class MetaUserImpl */
