/*== CellImpl.java =====================================================
CellImpl implements the interface Cell.
Application : SIARD 2.0
Description : CellImpl implements the interface Cell. 
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
/** CellImpl implements the interface Cell.
 @author Hartwig Thomas
 */
public class CellImpl
  extends ValueImpl
  implements Cell
{
  private Record _record = null;
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public Record getParentRecord() { return _record; }
  
  private MetaColumn _mc = null;
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public MetaColumn getMetaColumn() { return _mc; }

  /*------------------------------------------------------------------*/
  /** constructor
   * @param record parent record to which this cell belongs.
   * @param iIndex index of value in parent cell or field 
   *   (is needed, because element can be null).
   * @param mc associated MetaColumn instance.
   * @param elCell DOM element holding the full cell.
   */
  private CellImpl(Record record, int iIndex, MetaColumn mc, Element elCell)
    throws IOException
  {
    _record = record;
    _mc = mc;
    RecordImpl ri = (RecordImpl)record;
    initialize(ri.getRecord(), ri.getTemporaryLobFolder(), iIndex, elCell, mc);
  } /* constructor */
  
  /*------------------------------------------------------------------*/
  /** factory
   * @param iIndex index of value in parent cell or field 
   *   (is needed, because element can be null).
   * @param record parent record to which this cell belongs.
   * @param mc associated MetaColumn instance.
   * @param elCell DOM element holding the full cell.
   */
  public static Cell newInstance(Record record, int iIndex, MetaColumn mc, Element elCell)
    throws IOException
  {
    return new CellImpl(record, iIndex, mc, elCell);
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
    return FieldImpl.newInstance(iField,this,this,mf,el);
  } /* createField */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public Cell getAncestorCell()
  {
    return this;
  } /* getCell */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  protected String getInternalLobFolder()
    throws IOException
  {
    return ((MetaColumnImpl)getMetaColumn()).getFolder();
  } /* getInternalLobFolder */

} /* class CellImpl */
