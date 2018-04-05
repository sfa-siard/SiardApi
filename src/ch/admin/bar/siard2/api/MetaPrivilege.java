/*== MetaPrivilege.java ================================================
MetaPrivilege interface provides access to privilege meta data.
Application : SIARD 2.0
Description : MetaPrivilege interface provides access to privilege meta data.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 24.06.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api;

import java.io.*;

/*====================================================================*/
/** MetaPrivilege interface provides access to privilege meta data.
 @author Hartwig Thomas
 */
public interface MetaPrivilege
  extends MetaSearch
{
  /*------------------------------------------------------------------*/
  /** return the global meta data instance to which these privilege
   * meta data belong.
   * @return global meta data instance.
   */
  public MetaData getParentMetaData();

  /*====================================================================
  privilege properties
  ====================================================================*/
  /*------------------------------------------------------------------*/
  /** get privilege type (incl. ROLE privilege or "ALL PRIVILEGES".
   * @return privilege type (incl. ROLE privilege or "ALL PRIVILEGES".
   */
  public String getType();
  
  /*------------------------------------------------------------------*/
  /** get privilege object (may be null for ROLE privilege).
   * @return privilege object (may be null for ROLE privilege).
   */
  public String getObject();
  
  /*------------------------------------------------------------------*/
  /** get grantor (user or role name) of privilege.
   * @return grantor (user or role name) of privilege.
   */
  public String getGrantor();
  
  /*------------------------------------------------------------------*/
  /** get grantee (user or role name) of privilege.
   * @return grantee (user or role name) of privilege.
   */
  public String getGrantee();
  
  /*------------------------------------------------------------------*/
  /** set optional option "GRANT" or "ADMIN".
   * Can only be set if the SIARD archive is open for modification
   * of primary data.
   * @param sOption optional option "GRANT" or "ADMIN".
   * @throws IOException if the value could not be set.
   */
  public void setOption(String sOption)
    throws IOException;
  /** get optional option "GRANT" or "ADMIN".
   * @return optional option "GRANT" or "ADMIN".
   */
  public String getOption();
  
  /*------------------------------------------------------------------*/
  /** set description of the grant's meaning and content.
   * @param sDescription description of the grant's meaning and content.
   */
  public void setDescription(String sDescription);
  /** get description of the grant's meaning and content.
   * @return description of the grant's meaning and content.
   */
  public String getDescription();
  
} /* interface MetaPrivilege */
