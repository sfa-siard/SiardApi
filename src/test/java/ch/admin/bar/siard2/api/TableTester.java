package ch.admin.bar.siard2.api;

import static org.junit.Assert.*;
import java.io.*;
import org.junit.*;
import ch.admin.bar.siard2.api.primary.*;
import ch.enterag.utils.*;

public class TableTester
{
  private static final File _fileSIARD_10_SOURCE = new File("testfiles/sql1999.siard");
  private static final File _fileSIARD_10 = new File("tmp/sql1999.siard");
  private static final File _fileSIARD_21_NEW = new File("tmp/sql2008new.siard");
  private static ConfigurationProperties _cp = new ConfigurationProperties();
  private static final File _fileLOBS_FOLDER = new File(_cp.getLobsFolder());
  private static final File _fileOE = new File("testfiles/sfdboe.siard");
  private static final String _sDBNAME = "SIARD 2.1 Test Database";
  private static final String _sDATA_OWNER = "Enter AG, Rüti ZH, Switzerland";
  private static final String _sDATA_ORIGIN_TIMESPAN = "Second half of 2016";
  private static final String _sTEST_SCHEMA_NAME = "TESTSCHEMA";
  private static final String _sTEST_TABLE_NAME = "TESTTABLE";
  private static final String _sTEST_COLUMN1_NAME = "ID";
  private static final String _sTEST_COLUMN2_NAME = "TESTCOLUMN2";
  private static final String _sTEST_DISTINCT_TYPE = "TDISTINCT";
  private static final String _sTEST_DISTINCT_COLUMN = "CDISTINCT";
  private static final String _sTEST_UDTS_TYPE = "TUDTS";
  private static final String _sTEST_UDTS_COLUMN = "CUDTS";
  private static final String _sTEST_UDTS_ATTRIBUTE1_NAME = "TABLEID";
  private static final String _sTEST_UDTS_ATTRIBUTE2_NAME = "TRANSCRIPTION";
  private static final String _sTEST_UDTS_ATTRIBUTE3_NAME = "SOUND";
  private static final String _sTEST_ARRAY_COLUMN = "CARRAY";
  private static final String _sTEST_UDTC_TYPE = "TUDTC";
  private static final String _sTEST_UDTC_COLUMN = "CUDTC";
  private static final String _sTEST_UDTC_ATTRIBUTE1_NAME = "ID";
  private static final String _sTEST_UDTC_ATTRIBUTE2_NAME = "NESTEDROW";
  
  Table _tabNew = null;
  Table _tabOld = null;
  
  private void setMandatoryMetaData(Schema schema)
  {
    MetaData md = schema.getParentArchive().getMetaData();
    try
    {
      if (!SU.isNotEmpty(md.getDbName()))
        md.setDbName(_sDBNAME);
      if (!SU.isNotEmpty(md.getDataOwner()))
        md.setDataOwner(_sDATA_OWNER);
      if (!SU.isNotEmpty(md.getDataOriginTimespan()))
        md.setDataOriginTimespan(_sDATA_ORIGIN_TIMESPAN);
      if (md.getMetaSchemas() == 0)
        md.getArchive().createSchema("TEST_SCHEMA");
      Schema sch = md.getArchive().getSchema(0);
      if (sch.getTables() != 0)
      {
        Table table = sch.getTable(0);
        MetaTable mt = table.getMetaTable();
        if (mt.getMetaColumns() == 0)
        {
          MetaColumn mc = mt.createMetaColumn("TEST_COLUMN");
          mc.setType("INTEGER");
        }
      }
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
  }

  private void deleteFolder(File fileFolder)
    throws IOException
  {
    if (fileFolder.exists())
    {
      if (fileFolder.isDirectory())
      {
        File[] afile = fileFolder.listFiles();
        for (int iFile = 0; iFile < afile.length; iFile++)
        {
          File file = afile[iFile];
          if (file.isDirectory())
            deleteFolder(file);
          else
            file.delete();
        }
        fileFolder.delete();
      }
      else
        throw new IOException("deleteFolder only deletes directories!");
    }
  }

  private void createTypes(MetaSchema ms)
    throws IOException
  {
    MetaType mtDistinct = ms.createMetaType(_sTEST_DISTINCT_TYPE);
    mtDistinct.setCategory("distinct");
    mtDistinct.setBase("INTEGER");
    
    MetaType mtRow =  ms.createMetaType(_sTEST_UDTS_TYPE);
    mtRow.setCategory("udt");
    MetaAttribute mr1 = mtRow.createMetaAttribute(_sTEST_UDTS_ATTRIBUTE1_NAME);
    mr1.setType("INTEGER");
    MetaAttribute mr2 = mtRow.createMetaAttribute(_sTEST_UDTS_ATTRIBUTE2_NAME);
    mr2.setType("CLOB");
    MetaAttribute mr3 = mtRow.createMetaAttribute(_sTEST_UDTS_ATTRIBUTE3_NAME);
    mr3.setType("BLOB");
    
    MetaType mtUdt = ms.createMetaType(_sTEST_UDTC_TYPE);
    mtUdt.setCategory("udt");
    MetaAttribute mu1 = mtUdt.createMetaAttribute(_sTEST_UDTC_ATTRIBUTE1_NAME);
    mu1.setType("INTEGER");
    MetaAttribute mu2 = mtUdt.createMetaAttribute(_sTEST_UDTC_ATTRIBUTE2_NAME);
    mu2.setTypeName(_sTEST_UDTS_TYPE);
  }
  
  @Before
  public void setUp()
  {
    try 
    {
      FU.copy(_fileSIARD_10_SOURCE,_fileSIARD_10);
      if (_fileSIARD_21_NEW.exists())
        _fileSIARD_21_NEW.delete();
      deleteFolder(_fileLOBS_FOLDER);
      Archive archive = ArchiveImpl.newInstance();
      archive.create(_fileSIARD_21_NEW);
      Schema schema = archive.createSchema(_sTEST_SCHEMA_NAME);
      createTypes(schema.getMetaSchema());
      _tabNew = schema.createTable(_sTEST_TABLE_NAME);
      assertSame("Table create failed!",schema,_tabNew.getParentSchema());
      archive = ArchiveImpl.newInstance();
      archive.open(_fileSIARD_10);
      schema = archive.getSchema(0);
      _tabOld = schema.getTable(0);
      assertSame("Table open failed!",schema,_tabOld.getParentSchema());
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
  }
  
  @After
  public void tearDown()
  {
    try
    {
      setMandatoryMetaData(_tabNew.getParentSchema());
      _tabNew.getParentSchema().getParentArchive().close();
      _tabOld.getParentSchema().getParentArchive().close();
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
  }
  
  @Test
  public void testGetMetaTable()
  {
    MetaTable mt = _tabNew.getMetaTable();
    assertSame("MetaTable represents wrong table!",_tabNew,mt.getTable());
    mt = _tabOld.getMetaTable();
    assertSame("MetaTable represents wrong table!",_tabOld,mt.getTable());
  }

  @Test
  public void testIsValid()
  {
    assertEquals("New table is not valid (no columns/rows)!",false,_tabNew.isValid());
    assertEquals("Old table must be valid!",true,_tabOld.isValid());
  }
  
  @Test
  public void testIsEmpty()
  {
    assertEquals("New table must be empty (no columns/rows)!",true,_tabNew.isEmpty());
    assertEquals("Old table is not empty!",false,_tabOld.isEmpty());
  }

  @Test
  public void testExportTableSchema()
  {
    try
    {
      File file = new File("tmp/"+_tabOld.getMetaTable().getFolder()+"_old.xsd");
      FileOutputStream fos = new FileOutputStream(file);
      _tabOld.exportTableSchema(fos);
      fos.close();
      
      file = new File("tmp/"+_tabNew.getMetaTable().getFolder()+"_new.xsd");
      fos = new FileOutputStream(file);
      try
      { 
        _tabNew.exportTableSchema(fos);
        fail("Export of table xsd without columns should fail!");
      }
      catch(IOException ie) { System.out.println(EU.getExceptionMessage(ie)); }
      finally { fos.close(); }

      MetaColumn mc1 = _tabNew.getMetaTable().createMetaColumn(_sTEST_COLUMN1_NAME);
      mc1.setType("INTEGER");
      MetaColumn mc2 = _tabNew.getMetaTable().createMetaColumn(_sTEST_COLUMN2_NAME);
      mc2.setType("VARCHAR(256)");
      fos = new FileOutputStream(file);
      _tabNew.exportTableSchema(fos);
      fos.close();

    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
  }
  
  @Test
  public void testExportComplexTableSchema()
  {
    try
    {
      File file = new File("tmp/"+_tabNew.getMetaTable().getFolder()+"_new_complex.xsd");

      MetaColumn mc1 = _tabNew.getMetaTable().createMetaColumn(_sTEST_COLUMN1_NAME);
      mc1.setType("INTEGER");
      mc1.setNullable(false);
      
      MetaColumn mc2 = _tabNew.getMetaTable().createMetaColumn(_sTEST_DISTINCT_COLUMN);
      mc2.setTypeName(_sTEST_DISTINCT_TYPE);
      
      MetaColumn mc3 = _tabNew.getMetaTable().createMetaColumn(_sTEST_UDTS_COLUMN);
      mc3.setTypeName(_sTEST_UDTS_TYPE);
      
      MetaColumn mc4 = _tabNew.getMetaTable().createMetaColumn(_sTEST_ARRAY_COLUMN);
      mc4.setType("VARCHAR(256)");
      mc4.setCardinality(4);
      
      MetaColumn mc5 = _tabNew.getMetaTable().createMetaColumn(_sTEST_UDTC_COLUMN);
      mc5.setTypeName(_sTEST_UDTC_TYPE);
      
      FileOutputStream fos = new FileOutputStream(file);
      _tabNew.exportTableSchema(fos);
      fos.close();

    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
  }

  @Test
  public void testRecords()
  {
    try
    {
      RecordDispenser rd = _tabOld.openRecords();
      Record record = rd.get();
      System.out.println(record);
      rd.close();

      MetaColumn mc1 = _tabNew.getMetaTable().createMetaColumn(_sTEST_COLUMN1_NAME);
      mc1.setType("INTEGER");
      MetaColumn mc2 = _tabNew.getMetaTable().createMetaColumn(_sTEST_COLUMN2_NAME);
      mc2.setType("VARCHAR(256)");
      RecordRetainer rr = _tabNew.createRecords();
      record = rr.create();
      System.out.println(record);
      rr.close();
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
  }
  
  @Test
  public void testSort()
  {
    try
    {
      MetaColumn mc1 = _tabNew.getMetaTable().createMetaColumn(_sTEST_COLUMN1_NAME);
      mc1.setType("INTEGER");
      MetaColumn mc2 = _tabNew.getMetaTable().createMetaColumn(_sTEST_COLUMN2_NAME);
      mc2.setType("VARCHAR(256)");
      RecordRetainer rr = _tabNew.createRecords();
      Record record = rr.create();
      record.getCell(0).setInt(5);
      record.getCell(1).setString("aber");
      rr.put(record);
      record = rr.create();
      record.getCell(0).setInt(1);
      record.getCell(1).setString("ähh");
      rr.put(record);
      record = rr.create();
      record.getCell(0).setInt(3);
      record.getCell(1).setString("ober");
      rr.put(record);
      record = rr.create();
      record.getCell(0).setInt(1);
      record.getCell(1).setString("über");
      rr.put(record);
      record = rr.create();
      record.getCell(0).setInt(4);
      record.getCell(1).setString("uber");
      rr.put(record);
      rr.close();
      setMandatoryMetaData(_tabNew.getParentSchema());
      _tabNew.getParentSchema().getParentArchive().close();
      Archive archive = ArchiveImpl.newInstance();
      archive.open(_fileSIARD_21_NEW);
      Schema schema = archive.getSchema(_sTEST_SCHEMA_NAME);
      _tabNew = schema.getTable(_sTEST_TABLE_NAME);
      System.out.println("Unsorted:");
      RecordDispenser rd = _tabNew.openRecords();
      for (int iRecord = 0; iRecord < _tabNew.getMetaTable().getRows(); iRecord++)
      {
        record = rd.get();
        System.out.println(String.valueOf(record.getCell(0).getInt())+", "+record.getCell(1).getString());
      }
      rd.close();
      System.out.println("Sorted by string:");
      _tabNew.sort(true, 1,null);
      rd = _tabNew.openRecords();
      for (int iRecord = 0; iRecord < _tabNew.getMetaTable().getRows(); iRecord++)
      {
        record = rd.get();
        System.out.println(String.valueOf(record.getCell(0).getInt())+", "+record.getCell(1).getString());
      }
      rd.close();
      System.out.println("And then decending by integer:");
      _tabNew.sort(false, 0,null);
      rd = _tabNew.openRecords();
      for (int iRecord = 0; iRecord < _tabNew.getMetaTable().getRows(); iRecord++)
      {
        record = rd.get();
        System.out.println(String.valueOf(record.getCell(0).getInt())+", "+record.getCell(1).getString());
      }
      rd.close();
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
  }
  
  @Test
  public void testExportAsHtml()
  {
    try
    {
      Archive archive = ArchiveImpl.newInstance();
      // archive.open(new File("../SiardGui/testfiles/sfdboe.siard"));
      archive.open(_fileOE);
      Schema schema = archive.getSchema("OE"); 
      File fileTable = new File("tmp/CUSTOMERS.html");
      FileOutputStream fosTable = new FileOutputStream(fileTable);
      schema.getTable("CUSTOMERS").exportAsHtml(fosTable, new File("tmp/lobs"));
      fosTable.close();
      archive.close();
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
  } /* testExportHtml */
}
