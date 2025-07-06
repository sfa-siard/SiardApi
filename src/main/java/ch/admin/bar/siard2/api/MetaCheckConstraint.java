/*== MetaCheckConstraint.java ==========================================
MetaCheckConstraint interface provides access to check constraint meta data.
Application : SIARD 2.0
Description : MetaCheckConstraint interface provides access to check 
              constraint meta data.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 27.06.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api;

import java.io.*;

/*====================================================================*/
/** MetaCheckConstraint interface provides access to check constraint meta data.
 @author Hartwig Thomas
 */
public interface MetaCheckConstraint
  extends MetaSearch
{
  /*------------------------------------------------------------------*/
  /** return the table meta data instance to which these check constraint
   * meta data belong.
   * @return table meta data instance.
   */
  public MetaTable getParentMetaTable();
  
  /*------------------------------------------------------------------*/
  /** return true, if the constraint's condition is not null.
   * @return true, if the constraint's condition is not null.
   */
  public boolean isValid();
  
  /*====================================================================
  check constraint properties
  ====================================================================*/
  /*------------------------------------------------------------------*/
  /** get name of check constraint.
   * @return name of check constraint.
   */
  public String getName();
  
  /*------------------------------------------------------------------*/
  /** set condition to be checked by constraint.
   * Can only be set if the SIARD archive is open for modification
   * of primary data.
   * @param sCondition condition to be checked by constraint.
   * @throws IOException if the value could not be set.
   */
  public void setCondition(String sCondition)
    throws IOException;
  /** get condition to be checked by constraint.
   * @return condition to be checked by constraint.
   */
  public String getCondition();
  
  /*------------------------------------------------------------------*/
  /** set description of the check constraint's meaning and content.
   * @param sDescription description of the check constraint's meaning and content.
   */
  public void setDescription(String sDescription);
  /** get description of the check constraint's meaning and content.
   * @return description of the check constraint's meaning and content.
   */
  public String getDescription();
  
} /* interface MetaCheckConstraint */
