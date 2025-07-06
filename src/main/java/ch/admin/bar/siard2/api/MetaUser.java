/*== MetaUser.java =====================================================
MetaUser interface provides access to user meta data.
Application : SIARD 2.0
Description : MetaUser interface provides access to user meta data.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 24.06.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api;

/*====================================================================*/
/** MetaUser interface provides access to user meta data.
 @author Hartwig Thomas
 */
public interface MetaUser
  extends MetaSearch
{
  /*------------------------------------------------------------------*/
  /** return the global meta data instance to which these user
   * meta data belong.
   * @return global meta data instance.
   */
  public MetaData getParentMetaData();

  /*====================================================================
  user properties
  ====================================================================*/
  /*------------------------------------------------------------------*/
  /** get user name.
   * @return user name.
   */
  public String getName();
  
  /*------------------------------------------------------------------*/
  /** set description of the user.
   * @param sDescription description of the user.
   */
  public void setDescription(String sDescription);
  /** get description of the user.
   * @return description of the user.
   */
  public String getDescription();
  
} /* interface MetaUser */
