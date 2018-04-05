package ch.admin.bar.siard2.api;

import java.io.*;
import java.nio.file.*;
import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.utils.*;
import ch.enterag.utils.test.*;
import ch.admin.bar.siard2.api.primary.*;

public class RecordExtractTester
{
  private static final File _fileSAKILA = new File("testfiles/sfdbsakila.siard");
  private static final File _fileSIARD_21_NEW = new File("tmp/sql2008new.siard");
  private static final String _sDBNAME = "SIARD 2.1 Test Database";
  private static final String _sDATA_OWNER = "Enter AG, Rüti ZH, Switzerland";
  private static final String _sDATA_ORIGIN_TIMESPAN = "Second half of 2016";
  private static final String _sTEST_USER_NAME = "TESTUSER";
  private static final String _sTEST_SCHEMA_NAME = "TESTSCHEMA";
  private static final String _sTEST_TABLE_NAME = "TESTTABLE";
  private static final String _sTEST_COLUMN1_NAME = "CINTEGER";
  private static final String _sTEST_TYPE1_NAME = "BIGINT";
  private static final String _sTEST_COLUMN2_NAME = "CVARCHAR";
  private static final String _sTEST_TYPE2_NAME = "VARCHAR(256)";
  Table _tabNew = null;
  
  private void setMandatoryMetaData(Schema schema)
    throws IOException
  {
    MetaData md = schema.getParentArchive().getMetaData();
    if (md != null)
    {
      if (!SU.isNotEmpty(md.getDbName()))
        md.setDbName(_sDBNAME);
      if (!SU.isNotEmpty(md.getDataOwner()))
        md.setDataOwner(_sDATA_OWNER);
      if (!SU.isNotEmpty(md.getDataOriginTimespan()))
        md.setDataOriginTimespan(_sDATA_ORIGIN_TIMESPAN);
      if (md.getMetaUser(_sTEST_USER_NAME) == null)
        md.createMetaUser(_sTEST_USER_NAME);
    }
  }

  private Table createTable(Schema schema)
    throws IOException
  {
    Table tab = schema.createTable(_sTEST_TABLE_NAME);
    assertSame("Table create failed!",schema,tab.getParentSchema());
    
    MetaColumn mc1 = tab.getMetaTable().createMetaColumn(_sTEST_COLUMN1_NAME);
    mc1.setType(_sTEST_TYPE1_NAME);
    mc1.setNullable(false);
    
    MetaColumn mc2 = tab.getMetaTable().createMetaColumn(_sTEST_COLUMN2_NAME);
    mc2.setType(_sTEST_TYPE2_NAME);
    
    return tab;
  } /* createTable */
  
  private void populateCell(Cell cell, int iCell, long lRecord)
    throws IOException
  {
    if (iCell == 0)
      cell.setLong(lRecord);
    else
      cell.setString(TestUtils.getString((int)(256*Math.random())));
  }
  private void fillTable(long lRecords)
    throws IOException
  {
    RecordRetainer rr = _tabNew.createRecords();
    for (long lRecord = 0; lRecord < lRecords; lRecord++)
    {
      Record record = rr.create();
      for (int iCell = 0; iCell < record.getCells(); iCell++)
      {
        Cell cell = record.getCell(iCell);
        populateCell(cell,iCell,lRecord);
      }
      rr.put(record);
    }
    rr.close();
  } /* fillTable */
  
  @Before
  public void setUp()
  {
    try{ Files.deleteIfExists(_fileSIARD_21_NEW.toPath()); }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
  }
  
    @Test
  public void testSmall()
  {
    try
    {
      Archive archive = ArchiveImpl.newInstance();
      archive.create(_fileSIARD_21_NEW);
      Schema schema = archive.createSchema(_sTEST_SCHEMA_NAME);
      _tabNew = createTable(schema);
      fillTable(8);
      setMandatoryMetaData(schema);
      archive.close();
      
      archive = ArchiveImpl.newInstance();
      archive.open(_fileSIARD_21_NEW);
      schema = archive.getSchema(_sTEST_SCHEMA_NAME);
      _tabNew = schema.getTable(_sTEST_TABLE_NAME);
      RecordExtract rs = _tabNew.getRecordExtract();
      System.out.println(rs.getLabel());
      String sIndent = "  ";
      for (int iRecordSet = 0; iRecordSet < rs.getRecordExtracts(); iRecordSet++)
      {
        Record record = rs.getRecordExtract(iRecordSet).getRecord();
        System.out.println(sIndent + String.valueOf(record.getCell(0).getLong())+", "+record.getCell(1).getString());
      }
      archive.close();
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
  }
  
  private void printRecordSet(String sIndent, RecordExtract rs)
    throws IOException
  {
    String sLabel = rs.getLabel();
    Record record = rs.getRecord();
    System.out.print(sIndent);
    if (sLabel != null)
    {
      System.out.print(sLabel);
      if (record != null)
        System.out.print(": ");
    }
    if (record != null)
      System.out.println(String.valueOf(record.getCell(0).getLong())+", "+record.getCell(1).getString());
    else
      System.out.println();
    if (sLabel != null)
    {
      for (int iRecordSet = 0; iRecordSet < rs.getRecordExtracts(); iRecordSet++)
        printRecordSet(sIndent+"  ",rs.getRecordExtract(iRecordSet));
    }
  }

  private void testMetaDataOnly(int n)
      throws IOException
  {
    Archive archive = ArchiveImpl.newInstance();
    archive.create(_fileSIARD_21_NEW);
    Schema schema = archive.createSchema(_sTEST_SCHEMA_NAME);
    _tabNew = createTable(schema);
    _tabNew.getMetaTable().setRows((long)n);
    setMandatoryMetaData(schema);
    archive.close();
    System.out.println(_fileSIARD_21_NEW.getAbsolutePath());
    
    archive = ArchiveImpl.newInstance();
    archive.open(_fileSIARD_21_NEW);
    schema = archive.getSchema(_sTEST_SCHEMA_NAME);
    _tabNew = schema.getTable(_sTEST_TABLE_NAME);
    RecordExtract rs = _tabNew.getRecordExtract();
    try { printRecordSet("",rs); }
    catch(IOException ie) {}
    archive.close();
  }
  
  private void test(int n)
      throws IOException
  {
    Archive archive = ArchiveImpl.newInstance();
    archive.create(_fileSIARD_21_NEW);
    Schema schema = archive.createSchema(_sTEST_SCHEMA_NAME);
    _tabNew = createTable(schema);
    fillTable(n);
    setMandatoryMetaData(schema);
    archive.close();
    System.out.println(_fileSIARD_21_NEW.getAbsolutePath());
    
    archive = ArchiveImpl.newInstance();
    archive.open(_fileSIARD_21_NEW);
    schema = archive.getSchema(_sTEST_SCHEMA_NAME);
    _tabNew = schema.getTable(_sTEST_TABLE_NAME);
    RecordExtract rs = _tabNew.getRecordExtract();
    printRecordSet("",rs);
    archive.close();
  }
  
  private void testSort(int n)
      throws IOException
  {
    Archive archive = ArchiveImpl.newInstance();
    archive.create(_fileSIARD_21_NEW);
    Schema schema = archive.createSchema(_sTEST_SCHEMA_NAME);
    _tabNew = createTable(schema);
    fillTable(n);
    setMandatoryMetaData(schema);
    archive.close();
    System.out.println(_fileSIARD_21_NEW.getAbsolutePath());
    
    archive = ArchiveImpl.newInstance();
    archive.open(_fileSIARD_21_NEW);
    schema = archive.getSchema(_sTEST_SCHEMA_NAME);
    _tabNew = schema.getTable(_sTEST_TABLE_NAME);
    RecordExtract rs = _tabNew.getRecordExtract();
    printRecordSet("",rs);
    _tabNew.sort(true, 1, null);
    rs = _tabNew.getRecordExtract();
    System.out.println();
    System.out.println("Sorted");
    printRecordSet("",rs);
    archive.close();
  }

  @Test
  public void testSortSakila()
      throws IOException
  {
    Archive archive = ArchiveImpl.newInstance();
    archive.open(_fileSAKILA);
    Schema schema = archive.getSchema(0);
    Table table = schema.getTable("actor");
    RecordExtract rs = table.getRecordExtract();
    printRecordSet("",rs);
    table.sort(true, 2, null);
    rs = table.getRecordExtract();
    System.out.println();
    System.out.println("Sorted");
    printRecordSet("",rs);
    archive.close();
  }
  
  @Test
  public void testSort1()
  {
    try { testSort(1); }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
  }
  
  @Test
  public void testSort8()
  {
    try { testSort(8); }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
  }
  
  @Test
  public void testSort64()
  {
    try { testSort(64); }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
  }
  
  @Test
  public void test1()
  {
    try { test(1); }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
  }

  @Test
  public void test8()
  {
    try { test(8); }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
  }

  @Test
  public void test64()
  {
    try { test(64); }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
  }
  
  @Test
  public void test512()
  {
    try { test(512); }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
  }
  
  @Test
  public void test4096()
  {
    try { test(4096); }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
  }
  
  
  @Test
  public void testMetaDataOnly8()
  {
    try { testMetaDataOnly(8); }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
  }

  
}
