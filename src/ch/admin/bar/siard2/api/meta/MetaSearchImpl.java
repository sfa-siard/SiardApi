/*== MetaSearchImpl.java ===============================================
MetaSearchImpl implements the interface MetaSearch.
Application : SIARD 2.0
Description : MetaSearchImpl implements the interface MetaSearch.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 24.06.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api.meta;

import java.io.*;
import ch.enterag.utils.*;
import ch.enterag.utils.logging.*;
import ch.admin.bar.siard2.api.*;

/*====================================================================*/
/** MetaSearchImpl implements the interface MetaSearch.
 * @author Hartwig Thomas
 */
public abstract class MetaSearchImpl
  implements MetaSearch
{
  /*====================================================================
  (private) data members
  ====================================================================*/
  /** logger */  
  private static IndentLogger _il = IndentLogger.getIndentLogger(MetaSearch.class.getName());
  
  /** find string */
  protected String _sFindString = null;
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public String getFindString() { return _sFindString; }

  /** match case */
  protected boolean _bMatchCase = false;

  /** element which contains the found string (in interface order) */
  protected int _iFoundElement = 0;
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public int getFoundElement()
    throws IOException
  { 
    return _iFoundElement; 
  } /* getFoundElement */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public String getFoundString(DU du)
    throws IOException
  { 
    return getSearchElements(du)[_iFoundElement]; 
  } /* getFoundString */

  /** offset in element at position where string was found if _mdFind == this */
  protected int _iFoundOffset = -1;
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public int getFoundOffset() { return _iFoundOffset; }

  /*====================================================================
  abstract methods
  ====================================================================*/
  /*------------------------------------------------------------------*/
  /** get searchable elements
   * @return searchable elements of the searchable meta data.
   */
  abstract protected String[] getSearchElements(DU du) throws IOException;
  
  /*------------------------------------------------------------------*/
  /** get searchable sub meta data
   * N.B.: Must be overridden by meta data elements that have sub meta 
   * data elements!
   * @return searchable sub meta data.
   */
  protected MetaSearch[] getSubMetaSearches() 
    throws IOException
  {
    return new MetaSearch[] {};
  } /* getSubMetaSearches */
  
  /*====================================================================
  interface methods
  ====================================================================*/
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void find(String sFindString, boolean bMatchCase)
    throws IOException
  {
    _il.enter(sFindString,String.valueOf(bMatchCase),getClass().getName());
    _iFoundElement = 0;
    _iFoundOffset = -1;
    _bMatchCase = bMatchCase;
    if (bMatchCase)
      _sFindString = sFindString;
    else
      _sFindString = sFindString.toLowerCase();
    /* distribute find string to all elements */
    MetaSearch[] amsSubMeta = getSubMetaSearches();
    for (int iSubMeta = 0; iSubMeta < amsSubMeta.length; iSubMeta++)
    {
      if (amsSubMeta[iSubMeta] != null)
        amsSubMeta[iSubMeta].find(_sFindString,_bMatchCase);
    }
    _il.exit();
  } /* find */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} 
   * must be called at the end by all derived methods!
   */
  @Override
  public MetaSearch findNext(DU du)
    throws IOException
  {
    _il.enter(this.getClass().getName());
    MetaSearch msFind = null;
    if (_sFindString != null)
    {
      _il.event("Find string: "+_sFindString);
      /* skip previous find */
      _iFoundOffset++;
      /* check elements */
      String[] asElement = getSearchElements(du);
      while ((msFind == null) && (_iFoundElement < asElement.length))
      {
        int iPos = -1;
        String sElement = asElement[_iFoundElement];
        if (sElement != null)
        {
          if (_bMatchCase)
            iPos = sElement.indexOf(_sFindString,_iFoundOffset);
          else
            iPos = sElement.toLowerCase().indexOf(_sFindString,_iFoundOffset);
          if (iPos >= 0)
          {
            _il.event("Found in \""+sElement+"\" "+String.valueOf(_iFoundElement)+" at position "+String.valueOf(iPos));
            msFind = this;
            _iFoundOffset = iPos;
          }
          else
          {
            _iFoundOffset = 0;
            _iFoundElement++;
          }
        }
        else
          _iFoundElement++;
      }
      /* check sub meta data */
      if (msFind == null)
      {
        _iFoundOffset = -1;
        MetaSearch[] amsSubMeta = getSubMetaSearches();
        while ((msFind == null) && (_iFoundElement < asElement.length+amsSubMeta.length))
        {
          if (amsSubMeta[_iFoundElement - asElement.length] != null)
            msFind = amsSubMeta[_iFoundElement - asElement.length].findNext(du);
          if (msFind == null)
            _iFoundElement++;
        }
      }
      /* if nothing found, reset find string */
      if (msFind == null)
        _sFindString = null;
      else
        _il.event("Element: "+String.valueOf(_iFoundElement)+" / Offset: "+String.valueOf(_iFoundOffset));
    }
    _il.exit(String.valueOf(msFind));
    return msFind;
  } /* findNext */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public boolean canFindNext()
  {
    return (_sFindString != null);
  } /* canFindNext */

} /* MetaSearchImpl */
