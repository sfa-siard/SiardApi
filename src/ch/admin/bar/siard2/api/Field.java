package ch.admin.bar.siard2.api;

public interface Field
  extends Value
{
  /*------------------------------------------------------------------*/
  /** get parent field or cell with which this Field instance is associated.
   * @return get parent field or cell with which this Field instance is 
   * associated. 
   */
  public Value getParent();
  
  /*------------------------------------------------------------------*/
  /** get field meta data associated with this field. 
   * @return field meta data associated with this field. 
   */
  public MetaField getMetaField();
  
} /* interface Field */
