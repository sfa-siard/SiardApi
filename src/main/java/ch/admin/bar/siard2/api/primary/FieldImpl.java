/*== FieldImpl.java ====================================================
FieldImpl implements the interface Field.
Application : SIARD 2.0
Description : FieldImpl implements the interface Field. 
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 05.07.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api.primary;

import java.io.*;
import org.w3c.dom.*;
import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.api.meta.*;

/*====================================================================*/
/** FieldImpl implements the interface Field.
 @author Hartwig Thomas
 */
public class FieldImpl
  extends ValueImpl
  implements Field
{
  private Cell _cellAncestor = null;
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public Cell getAncestorCell() { return _cellAncestor; }
  
  private Value _valueParent = null;
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public Value getParent() { return _valueParent; }
  
  private MetaField _mf = null;
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public MetaField getMetaField() { return _mf; }
  
  /*------------------------------------------------------------------*/
  /** constructor
   * @param iIndex index (0-based) of field in parent.
   * @param valueParent parent cell or field.
   * @param cellAncestor ancestor cell.
   * @param mf field meta data.
   * @param elField DOM element representing the field's value.
   * @throws IOException if an I/O error occurred.
   */
  private FieldImpl(int iIndex, Value valueParent, Cell cellAncestor, MetaField mf, Element elField)
    throws IOException
  {
    _mf = mf;
    _valueParent = valueParent;
    _cellAncestor = cellAncestor;
    RecordImpl ri = (RecordImpl)cellAncestor.getParentRecord();
    initialize(ri.getRecord(),ri.getTemporaryLobFolder(),iIndex,elField,mf);
  } /* constructor */
  
  /*------------------------------------------------------------------*/
  /** factory
   * @param iIndex index (0-based) of field in parent.
   * @param valueParent parent cell or field.
   * @param cellAncestor ancestor cell.
   * @param mf field meta data.
   * @param elField DOM element representing the field's value.
   * @param fileLobFolder temporary folder for LOBs.
   * @throws IOException if an I/O error occurred.
   */
  public static Field newInstance(int iIndex, Value valueParent, Cell cellAncestor, MetaField mf, Element elField)
    throws IOException
  {
    return new FieldImpl(iIndex, valueParent, cellAncestor, mf, elField);
  } /* newInstance */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  protected Field createField(int iField, MetaField mf, Element el)
    throws IOException
  {
    if (el != null)
    {
      int iCardinality = mf.getCardinality();
      if (iCardinality > 0)
        super.extendArray(iField, iCardinality);
    }
    return FieldImpl.newInstance(iField,this,getAncestorCell(),mf,el);
  } /* createField */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  protected String getInternalLobFolder()
    throws IOException
  {
    return ((MetaFieldImpl)getMetaField()).getFolder();
  } /* getInternalLobFolder */
  
} /* FieldImpl */
