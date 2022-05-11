package ch.admin.bar.siard2.api.primary;

import ch.admin.bar.siard2.api.generated.SiardArchive;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MetaDataXmlTester {
    private static final String TESTFILES_METADATA_DIR = "testfiles/metadata/";
    private static final String METADATA_2_2_XML = "metadata-2.2.xml";
    private static final String METADATA_1_0_XML = "metadata-1.0.xml";
    private static final String METADATA_2_1_XML = "metadata-2.1.xml";

    @Test
    public void shoudReadSiard10XmlMetaData() throws FileNotFoundException {
        // given
        FileInputStream fis = new FileInputStream(TESTFILES_METADATA_DIR + METADATA_1_0_XML);

        // when
        SiardArchive sa = MetaDataXml.readSiard10Xml(fis);


        // then
        assertEquals(sa.getDbname(), "SIARD 1.0 MetaData");
    }

    @Test
    public void shouldReadSiard22XmlMetaData() throws FileNotFoundException {
        // given
        FileInputStream fis = new FileInputStream(TESTFILES_METADATA_DIR + METADATA_2_2_XML);

        // when
        SiardArchive sa = MetaDataXml.readSiard22Xml(fis);

        // then
        assertEquals(sa.getDbname(), "SIARD 2.2 MetaData");
    }


    @Test
    public void shouldReadSiard21XmlMetaData() throws FileNotFoundException {
        // given
        FileInputStream fis = new FileInputStream(TESTFILES_METADATA_DIR + METADATA_2_1_XML);

        // when
        SiardArchive sa = MetaDataXml.readSiard21Xml(fis);

        // then
        assertEquals(sa.getDbname(), "SIARD 2.1 MetaData");
    }

    @Test
    public void shouldNotReadSiard22XmlMetaData_ForSiard10_MetaData() throws FileNotFoundException {
        // given
        FileInputStream fis = new FileInputStream(TESTFILES_METADATA_DIR + METADATA_1_0_XML);

        // when
        SiardArchive sa = MetaDataXml.readSiard22Xml(fis);

        // then
        assertNull("should not have loaded a siard archive instance for metadata v 1.0", sa);
    }

    @Test
    public void shouldNotReadSiard21XmlMetaData_ForSiard10_MetaData() throws FileNotFoundException {
        // given
        FileInputStream fis = new FileInputStream(TESTFILES_METADATA_DIR + METADATA_1_0_XML);

        // when
        SiardArchive sa = MetaDataXml.readSiard21Xml(fis);

        // then
        assertNull("should not have loaded a siard archive instance for metadata v 1.0", sa);
    }

    @Test
    public void shouldNotReadSiard21XmlMetaData_ForSiard22_MetaData() throws FileNotFoundException {
        // given
        FileInputStream fis = new FileInputStream(TESTFILES_METADATA_DIR + METADATA_2_2_XML);

        // when
        SiardArchive sa = MetaDataXml.readSiard21Xml(fis);

        // then
        assertNull("should not have loaded a siard archive instance for metadata v 2.2", sa);
    }


    @Test
    public void shouldNotReadSiard22XmlMetatData_ForSiard21_MetaData() throws FileNotFoundException {
        // given
        FileInputStream fis = new FileInputStream(TESTFILES_METADATA_DIR + METADATA_2_1_XML);

        // when
        SiardArchive sa = MetaDataXml.readSiard22Xml(fis);

        // then
        assertNull("should not have loaded a siard archive instance for metadata v 2.1", sa);
    }

    @Test
    public void shouldNotReadSiard10XmlMetaData_ForSiard21_MetaData() throws FileNotFoundException {
        // given
        FileInputStream fis = new FileInputStream(TESTFILES_METADATA_DIR + METADATA_2_1_XML);

        // when
        SiardArchive sa = MetaDataXml.readSiard10Xml(fis);


        // then
        assertNull("should not have loaded a siard archive instance for metadata v 2.1", sa);
    }

    @Test
    public void shouldNotReadSiard10XmlMetaData_ForSiard22_MetaData() throws FileNotFoundException {
        // given
        FileInputStream fis = new FileInputStream(TESTFILES_METADATA_DIR + METADATA_2_2_XML);

        // when
        SiardArchive sa = MetaDataXml.readSiard10Xml(fis);


        // then
        assertNull("should not have loaded a siard archive instance for metadata v 2.2", sa);
    }

}
