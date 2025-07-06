/*== MetaRoutineImpl.java ==============================================
MetaRoutineImpl implements the interface MetaRoutine.
Application : SIARD 2.0
Description : MetaRoutineImpl implements the interface MetaRoutine.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 29.06.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api.meta;

import java.io.*;
import java.util.*;
import ch.enterag.utils.*;
import ch.enterag.utils.xml.XU;
import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import ch.enterag.sqlparser.datatype.PredefinedType;
import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.api.generated.*;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;

/*====================================================================*/
/** MetaRoutineImpl implements the interface MetaRoutine.
 @author Hartwig Thomas
 */
public class MetaRoutineImpl
  extends MetaSearchImpl
  implements MetaRoutine
{
  private static ObjectFactory _of = new ObjectFactory();
  private Map<String,MetaParameter> _mapMetaParameters = new HashMap<String,MetaParameter>();

  private MetaSchema _msParent = null;
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public MetaSchema getParentMetaSchema() { return _msParent; }
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public boolean isValid() 
  {
    boolean bValid = true;
    if (bValid && (getMetaParameters() < 0))
      bValid = false;
    for (int iParameter = 0; bValid && (iParameter < getMetaParameters()); iParameter++)
    {
      if (!getMetaParameter(iParameter).isValid())
        bValid = false;
    }
    return bValid;
  } /* isValid */
  
  private RoutineType _rt = null;
  public RoutineType getRoutineType()
    throws IOException
  {
    for (int iParameter = 0; iParameter < getMetaParameters(); iParameter++)
    {
      MetaParameter mp = getMetaParameter(iParameter);
      ((MetaParameterImpl)mp).getParameterType();
    }
    return _rt;
  } /* getRoutineType */
  
  /*------------------------------------------------------------------*/
  /** get archive
   * @return archive.
   */
  private ArchiveImpl getArchive()
  {
    return (ArchiveImpl)getParentMetaSchema().getSchema().getParentArchive();
  } /* getArchive */
  
  private RoutineType _rtTemplate = null;
  /*------------------------------------------------------------------*/
  /** set template meta data.
   * @param rtTemplate template meta data.
   */
  public void setTemplate(RoutineType rtTemplate)
    throws IOException
  {
    _rtTemplate = rtTemplate;
    if (!SU.isNotEmpty(getBody()))
      setBody(XU.fromXml(_rtTemplate.getBody()));
    if (!SU.isNotEmpty(getDescription()))
      setDescription(XU.fromXml(_rtTemplate.getDescription()));
    ParametersType pts = _rtTemplate.getParameters();
    if (pts != null)
    {
      for (int iParameter = 0; iParameter < pts.getParameter().size(); iParameter++)
      {
        ParameterType ptTemplate = pts.getParameter().get(iParameter);
        String sName = XU.fromXml(ptTemplate.getName());
        MetaParameter mp = getMetaParameter(sName);
        if (mp != null)
        {
          MetaParameterImpl mpi = (MetaParameterImpl)mp;
          mpi.setTemplate(ptTemplate);
        }
      }
    }
  } /* setTemplate */
  
  /*------------------------------------------------------------------*/
  /** constructor
   * @param msParent schema meta data object of SIARD archive.
   * @param rt RoutineType instance (JAXB).
   * @throws IOException if an I/O error occurred.
   */
  private MetaRoutineImpl(MetaSchema msParent, RoutineType rt)
    throws IOException
  {
    _msParent = msParent;
    _rt = rt;
    /* open all parameter meta data */
    ParametersType pts = _rt.getParameters();
    if (pts != null)
    {
      for (int iParameter = 0; iParameter < pts.getParameter().size(); iParameter++)
      {
        ParameterType pt = pts.getParameter().get(iParameter);
        MetaParameter mp = MetaParameterImpl.newInstance(this, pt, iParameter+1);
        _mapMetaParameters.put(XU.fromXml(pt.getName()),mp);
      }
    }
  } /* constructor MetaRoutineImpl */
  
  /*------------------------------------------------------------------*/
  /** factory
   * @param msParent schema meta data object of SIARD archive.
   * @param rt RoutineType instance (JAXB).
   * @return new MetaRoutine instance.
   * @throws IOException if an I/O error occurred.
   */
  public static MetaRoutine newInstance(MetaSchema msParent, RoutineType rt)
    throws IOException
  {
    return new MetaRoutineImpl(msParent,rt);
  } /* newInstance */

  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public String getSpecificName() { return XU.fromXml(_rt.getSpecificName()); }
  
  /* property Name */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setName(String sName)
  { 
    if (getArchive().isMetaDataDifferent(getName(),sName))
      _rt.setName(XU.toXml(sName));
  } /* setBody */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public String getName() { return XU.fromXml(_rt.getName()); }
  
  /* property Body */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setBody(String sBody)
  { 
    if (getArchive().isMetaDataDifferent(getBody(),sBody))
      _rt.setBody(XU.toXml(sBody));
  } /* setBody */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public String getBody() { return XU.fromXml(_rt.getBody()); }

  /* property Source */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setSource(String sSource)
    throws IOException
  {
    if (getArchive().canModifyPrimaryData())
    {
      if (getArchive().isMetaDataDifferent(getSource(),sSource))
        _rt.setSource(XU.toXml(sSource));
    }
    else
      throw new IOException("Source cannot be set!");
  } /* setSource */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public String getSource() { return XU.fromXml(_rt.getSource()); }

  /* property Description */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override 
  public void setDescription(String sDescription) 
  { 
    if (getArchive().isMetaDataDifferent(getDescription(),sDescription))
      _rt.setDescription(XU.toXml(sDescription));
  } /* setDescription */
  /** {@inheritDoc} */
  @Override public String getDescription() { return XU.fromXml(_rt.getDescription()); }
  
  /* property Characteristic */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setCharacteristic(String sCharacteristic)
    throws IOException
  {
    if (getArchive().canModifyPrimaryData())
    {
      if (getArchive().isMetaDataDifferent(getCharacteristic(),sCharacteristic))
      _rt.setCharacteristic(XU.toXml(sCharacteristic));
    }
    else
      throw new IOException("Characteristic cannot be set!");
  } /* setSource */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public String getCharacteristic() { return XU.fromXml(_rt.getCharacteristic()); }

  /* property ReturnType */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setReturnType(String sReturnType)
    throws IOException
  {
    if (getArchive().canModifyPrimaryData())
    {
      if (getArchive().isMetaDataDifferent(getReturnType(),sReturnType))
        _rt.setReturnType(XU.toXml(sReturnType));
    }
    else
      throw new IOException("ReturnType cannot be set!");
  } /* setReturnType */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  public void setReturnPreType(int iReturnType, long lPrecision, int iScale)
    throws IOException
  {
    SqlFactory sf = new BaseSqlFactory();
    PredefinedType prt = sf.newPredefinedType();
    prt.initialize(iReturnType, lPrecision, iScale);
    String sReturnType = prt.format();
    setReturnType(sReturnType);
  } /* setReturnPreType */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public String getReturnType() { return XU.fromXml(_rt.getReturnType()); }

  /* parameters */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public int getMetaParameters() { return _mapMetaParameters.size(); }
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override 
  public MetaParameter getMetaParameter(int iParameter)
  {
    MetaParameter mp = null;
    ParametersType pts = _rt.getParameters();
    if (pts != null)
    {
      ParameterType pt = pts.getParameter().get(iParameter);
      String sName = XU.fromXml(pt.getName());
      mp = getMetaParameter(sName);
    }
    return mp;
  } /* getMetaParameter */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override 
  public MetaParameter getMetaParameter(String sName)
  {
    return _mapMetaParameters.get(sName);
  } /* getMetaParameter */
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override 
  public MetaParameter createMetaParameter(String sName)
    throws IOException
  {
    MetaParameter mp = null;
    if (getArchive().canModifyPrimaryData())
    {
      if (getMetaParameter(sName) == null)
      {
        ParametersType pts = _rt.getParameters();
        if (pts == null)
        {
          pts = _of.createParametersType();
          _rt.setParameters(pts);
        }
        ParameterType pt = _of.createParameterType();
        pt.setName(XU.toXml(sName));
        pt.setMode("IN"); // default mode
        pts.getParameter().add(pt);
        mp = MetaParameterImpl.newInstance(this, pt, _mapMetaParameters.size()+1);
        _mapMetaParameters.put(sName, mp);
        getArchive().isMetaDataDifferent(null,mp);
        if (_rtTemplate != null)
        {
          ParametersType ptsTemplate = _rtTemplate.getParameters();
          if (ptsTemplate != null)
          {
            ParameterType ptTemplate = null;
            for (int iParameter = 0; (ptTemplate == null) && (iParameter < ptsTemplate.getParameter().size()); iParameter++)
            {
              ParameterType ptTry = ptsTemplate.getParameter().get(iParameter);
              if (sName.equals(XU.fromXml(ptTry.getName())))
                ptTemplate = ptTry;
            }
            if ((ptTemplate != null) && (mp instanceof MetaParameterImpl))
            {
              MetaParameterImpl mpi = (MetaParameterImpl)mp;
              mpi.setTemplate(ptTemplate);
            }
          }
        }
      }
      else
        throw new IOException("Only one parameter with the same name allowed per routine!");
    }
    else
      throw new IOException("New parameters can only be created if archive is open for modification of primary data.");
    return mp;
  } /* createMetaParameter */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override
  protected MetaSearch[] getSubMetaSearches()
    throws IOException
  {
    MetaSearch[] ams = new MetaSearch[getMetaParameters()];
    for (int iParameter = 0; iParameter < getMetaParameters(); iParameter++)
      ams[iParameter] = getMetaParameter(iParameter);
    return ams;
  } /* getSubMetaSearches */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc} */
  @Override public String[] getSearchElements(DU du)
    throws IOException
  { 
    return new String[] 
      {
        getName(),
        getSpecificName(),
        getSource(),
        getBody(),
        getCharacteristic(),
        getReturnType(),
        getDescription()
      };
  } /* getSearchElements */
  
  /*------------------------------------------------------------------*/
  /** {@inheritDoc}
   * toString() returns the name of the routine which is to be displayed 
   * as the label of the routine node of the tree displaying the archive.   
   */
  @Override 
  public String toString()
  {
    return getName();
  }
} /* class MetaRoutineImpl */
