package ch.admin.bar.siard2.api.primary;

import ch.admin.bar.siard2.api.*;
import ch.enterag.utils.test.TestReader;
import ch.enterag.utils.test.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

// tests opening table records/ rows for sample archive with data link in format 2.2
public class RecordWithDataLinkTest {

    // created with SampleArchive generator
    private static final File ARCHIVE = new File("src/test/resources/testfiles/sample-datalink-2-2.siard");
    private static final String TSIMPLE = "TSIMPLE";
    private static final String TCOMPLEX = "TCOMPLEX";

    Archive archive;

    @Before
    public void setUp() throws Exception {
        archive = ArchiveImpl.newInstance();
        archive.open(ARCHIVE);
    }

    @After
    public void tearDown() throws Exception {
        archive.close();
    }

    @Test
    public void shouldHaveSchemas() {
        assertEquals(1, archive.getSchemas());
        assertEquals("SampleSchema", archive.getSchema(0)
                                            .getMetaSchema()
                                            .getName());
    }

    @Test
    public void shouldContainTables() {
        // when
        Schema schema = archive.getSchema(0);

        // then
        assertEquals(2, schema.getTables());
        assertEquals(TSIMPLE, schema.getTable(0)
                                    .getMetaTable()
                                    .getName());
        assertEquals(TCOMPLEX, schema.getTable(1)
                                     .getMetaTable()
                                     .getName());
    }

    @Test
    public void shouldContainRows() {
        // given
        Schema schema = archive.getSchema(0);

        // when
        Table simpleTable = schema.getTable(TSIMPLE);
        Table complexTable = schema.getTable(TCOMPLEX);

        // then
        assertEquals(4, simpleTable.getMetaTable()
                                   .getRows());
        assertEquals(2, complexTable.getMetaTable()
                                    .getRows());
    }

    @Test
    public void shouldOpenRecordsForSimpleTable() throws IOException {
        // given
        Schema schema = archive.getSchema(0);
        Table table = schema.getTable(TSIMPLE);

        // when
        RecordDispenser recordDispenser = table.openRecords();

        // then
        assertNotNull(recordDispenser);
        recordDispenser.close();
    }

    @Test
    public void shouldOpenRecordsForComplexTable() throws IOException {
        // given
        Schema schema = archive.getSchema(0);
        Table table = schema.getTable(TCOMPLEX);

        // when
        RecordDispenser recordDispenser = table.openRecords();

        // then
        assertNotNull(recordDispenser);
        recordDispenser.close();
    }

    @Test
    public void shouldReadAllColumnsForSimpleTable() throws IOException {
        // given
        Schema schema = archive.getSchema(0);
        Table table = schema.getTable(TSIMPLE);
        RecordDispenser recordDispenser = table.openRecords();

        // when
        recordDispenser.skip(1); // skip the 1st record - for simplicity
        Record record = recordDispenser.get();

        // then
        // only asserts values in the first row...
        assertEquals(1, record.getRecord());
        assertEquals(26, record.getCells());

        assertEquals("\"", record.getCell(0)
                                 .getString());
        assertEquals(
                "efghijklmnopqrstuvwxyz{|}~!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~!\"#$%&'(",
                record.getCell(1)
                      .getString());
        assertEquals(2345L, record.getCell(2)
                                  .getCharLength());
        assertTrue(TestUtils.equalReaders(new TestReader(2345L), record.getCell(2)
                                                                       .getReader()));
        assertEquals("!", record.getCell(3)
                                .getString());
        assertEquals("`abcdefghijklmnopqrstuvwxyz{|}~\u007F ¡¢£¤¥¦§¨©ª«¬\u00AD®¯°±²³´µ¶·¸¹º»¼½¾¿ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚÛÜÝÞßàáâãäåæçèéêëìíîïðñòóôõö÷øùúûüýþÿ", record.getCell(4)
                                                                                                                                                                         .getString());
        assertTrue("", record.getCell(5)
                             .isNull());
        assertEquals(36, record.getCell(6)
                               .getCharLength());
        assertEquals("content/schema0/table0/lob6/record1.xml", record.getCell(6)
                                                                      .getFilename());
        assertEquals("01", record.getCell(7)
                                 .getString());
        assertEquals("C8C9CACBCCCDCECFD0D1D2D3D4D5D6D7D8D9DADBDCDDDEDFE0E1E2E3E4E5E6E7E8E9EAEBECEDEEEFF0F1F2F3F4F5F6F7F8F9FAFBFCFDFEFF000102030405060708090A0B0C0D0E0F101112131415161718191A1B1C1D1E1F202122232425262728292A2B2C2D2E2F303132333435363738393A3B3C3D3E3F404142434445464748494A4B4C4D4E4F505152535455565758",
                     record.getCell(8)
                           .getString());
        assertEquals("content/schema0/table0/lob9/record1.bin", record.getCell(9)
                                                                      .getFilename());
        assertEquals("617280.45", record.getCell(10)
                                        .getString());
        assertEquals("0.617283945", record.getCell(11)
                                          .getString());
        assertEquals("24690", record.getCell(12)
                                    .getString());
        assertEquals("24691356", record.getCell(13)
                                       .getString());
        assertEquals("24691357802469134", record.getCell(14)
                                                .getString());
        assertEquals("0.6283184", record.getCell(15)
                                        .getString());
        assertEquals("0.3141592", record.getCell(16)
                                        .getString());
        assertEquals("3.14159265359", record.getCell(17)
                                            .getString());
        assertTrue("", record.getCell(18)
                             .isNull());
        assertEquals("2022-06-01", record.getCell(19)
                                         .getString());
        assertEquals("09:45:02.071Z", record.getCell(20)
                                            .getString());
        assertEquals("2022-06-01T09:45:02.123456789Z", record.getCell(21)
                                                             .getString());
        assertTrue("", record.getCell(22)
                             .isNull());
        assertEquals("-P34DT0H0M0S", record.getCell(23)
                                           .getString());
        assertEquals("P0DT0H0M23.456S", record.getCell(24)
                                              .getString());
        Cell dataLinkCell = record.getCell(25);
        assertEquals(79, dataLinkCell.getCharLength());
        assertEquals("content/schema0/table0/lob25/record1.bin", dataLinkCell.getFilename());
        assertEquals("DATALINK", dataLinkCell.getMetaColumn()
                                             .getType());

        recordDispenser.close();
    }

    @Test
    public void shouldReadAllColumnsForComplexTable() throws IOException {
        // given
        Schema schema = archive.getSchema(0);
        Table table = schema.getTable(TCOMPLEX);
        RecordDispenser recordDispenser = table.openRecords();

        // when
        Record record = recordDispenser.get();

        // then
        assertEquals(5, record.getCells());
        assertEquals("1234567890", record.getCell(0)
                                         .getString());
        assertEquals("987654321", record.getCell(1)
                                        .getString());

        Cell cell3 = record.getCell(2);
        assertEquals(4, cell3.getAttributes());
        assertEquals("12345", cell3.getAttribute(0)
                                   .getString());
        assertEquals("content/schema0/table1/lob2/field1/record0.txt", cell3.getAttribute(1)
                                                                            .getFilename());
        assertEquals("content/schema0/table1/lob2/field2/record0.bin", cell3.getAttribute(2)
                                                                            .getFilename());
        assertEquals("content/schema0/table1/lob2/field3/record0.bin", cell3.getAttribute(3)
                                                                            .getFilename());

        assertEquals("element 0,1element 0,3element 0,4", record.getCell(3)
                                                                .getString());

        Cell cell5 = record.getCell(4);
        assertEquals(2, cell5.getAttributes());
        assertEquals("-15", cell5.getAttribute(0)
                                 .getString());
        assertEquals(4, cell5.getAttribute(1)
                             .getAttributes());
        Field field = cell5.getAttribute(1);
        assertEquals("-12345", field.getAttribute(0)
                                    .getString());
        assertEquals("content/schema0/table1/lob4/field1/field1/record0.txt", field.getAttribute(1)
                                                                                   .getFilename());
        assertEquals("content/schema0/table1/lob4/field1/field2/record0.bin", field.getAttribute(2)
                                                                                   .getFilename());
        assertEquals("content/schema0/table1/lob4/field1/field3/record0.bin", field.getAttribute(3)
                                                                                   .getFilename());

        recordDispenser.close();
    }

    // the following string is generated using TestUtils.getString(256)
    private static final String NVARCHAR256 = "!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcd";


}