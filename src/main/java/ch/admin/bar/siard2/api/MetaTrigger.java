/*== MetaTrigger.java ==================================================
MetaTrigger interface provides access to trigger meta data.
Application : SIARD 2.0
Description : MetaTrigger interface provides access to trigger meta data.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 27.06.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api;

import java.io.*;

/*====================================================================*/
/** MetaTrigger interface provides access to trigger meta data.
 @author Hartwig Thomas
 */
public interface MetaTrigger
  extends MetaSearch
{
  /*------------------------------------------------------------------*/
  /** return the table meta data instance to which these trigger
   * meta data belong.
   * @return table meta data instance.
   */
  public MetaTable getParentMetaTable();
  
  /*------------------------------------------------------------------*/
  /** return true, if the trigger's action time, event and action is set.
   * @return true, if the trigger's action time, event and action is set.
   */
  public boolean isValid();
  
  /*====================================================================
  trigger properties
  ====================================================================*/
  /*------------------------------------------------------------------*/
  /** get trigger name.
   * @return trigger name.
   */
  public String getName();
  
  /*------------------------------------------------------------------*/
  /** set trigger action time (BEFORE, AFTER or INSTEAD OF).
   * Can only be set if the SIARD archive is open for modification
   * of primary data.
   * @param sActionTime trigger action time (BEFORE, AFTER or INSTEAD OF).
   * @throws IOException if the value could not be set.
   */
  public void setActionTime(String sActionTime)
    throws IOException;
  /** get trigger action time.
   * @return trigger action time.
   */
  public String getActionTime();

  /*------------------------------------------------------------------*/
  /** set trigger event INSERT, DELETE, UPDATE [OF &lt;trigger column list&gt;].
   * Can only be set if the SIARD archive is open for modification
   * of primary data.
   * @param sTriggerEvent trigger event INSERT, DELETE, UPDATE [OF &lt;trigger column list&gt;].
   * @throws IOException if the value could not be set.
   */
  public void setTriggerEvent(String sTriggerEvent)
    throws IOException;
  /** get trigger event INSERT, DELETE, UPDATE [OF &lt;trigger column list&gt;].
   * @return trigger event INSERT, DELETE, UPDATE [OF &lt;trigger column list&gt;].
   */
  public String getTriggerEvent();

  /*------------------------------------------------------------------*/
  /** set alias list &lt;old or new values alias&gt;.
   * Can only be set if the SIARD archive is open for modification
   * of primary data.
   * @param sAliasList alias list &lt;old or new values alias&gt;.
   * @throws IOException if the value could not be set.
   */
  public void setAliasList(String sAliasList)
    throws IOException;
  /** get alias list &lt;old or new values alias&gt;.
   * @return alias list &lt;old or new values alias&gt;.
   */
  public String getAliasList();
  
  /*------------------------------------------------------------------*/
  /** set triggered action.
   * Can only be set if the SIARD archive is open for modification
   * of primary data.
   * @param sTriggeredAction triggered action.
   * @throws IOException if the value could not be set.
   */
  public void setTriggeredAction(String sTriggeredAction)
    throws IOException;
  /** get triggered action.
   * @return triggered action.
   */
  public String getTriggeredAction();
  
  /*------------------------------------------------------------------*/
  /** set description of the trigger's meaning and content.
   * @param sDescription description of the trigger's meaning and content.
   */
  public void setDescription(String sDescription);
  /** get description of the trigger's meaning and content.
   * @return description of the trigger's meaning and content.
   */
  public String getDescription();
  
} /* interface MetaTrigger */
