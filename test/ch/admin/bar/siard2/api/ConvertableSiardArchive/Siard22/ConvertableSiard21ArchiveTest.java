package ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard22;


import ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard21.ConvertableSiard21Archive;
import ch.admin.bar.siard2.api.generated.SiardArchive;
import ch.admin.bar.siard2.api.generated.old21.DigestTypeType;
import ch.admin.bar.siard2.api.generated.old21.MessageDigestType;
import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ConvertableSiard21ArchiveTest {


    @Test
    public void shouldConvertSiardArchive21ToSiardArchive22() {

        ToSiardArchive22Transformer transformer = new ToSiardArchive22Transformer();
        ConvertableSiard21Archive convertableSiard21Archive = createExampleArchiveWithAllFieldsSet();

        SiardArchive result = convertableSiard21Archive.transform(transformer);

        assertSiardArchiveWithAllFieldSet(result);
    }

    private void assertSiardArchiveWithAllFieldSet(SiardArchive result) {
        assertNotNull(result);
        assertEquals(DB_NAME, result.getDbname());
        assertEquals(DESCRIPTION, result.getDescription());
        assertEquals(ARCHIVER, result.getArchiver());
        assertEquals(ARCHIVER_CONTACT, result.getArchiverContact());
        assertEquals(DATA_OWNER, result.getDataOwner());
        assertEquals(DATA_ORIGIN_TIMESPAN, result.getDataOriginTimespan());
        assertEquals(LOB_FOLDER, result.getLobFolder());
        assertEquals(PRODUCER_APPLICATION, result.getProducerApplication());
        assertEquals(ARCHIVAL_DATE, result.getArchivalDate());
        assertEquals(CONNECTION, result.getConnection());
        assertEquals(MESSAGE_DIGEST, result.getMessageDigest().get(0).getDigest());
        assertEquals(ch.admin.bar.siard2.api.generated.DigestTypeType.SHA_256,
                     result.getMessageDigest().get(0).getDigestType());
        assertEquals(CLIENT_MACHINE, result.getClientMachine());
        assertEquals(DATABASE_PRODUCT, result.getDatabaseProduct());

    }

    private ConvertableSiard21Archive createExampleArchiveWithAllFieldsSet() {
        ConvertableSiard21Archive archive = new ConvertableSiard21Archive();
        archive.setDbname(DB_NAME);
        archive.setDescription(DESCRIPTION);
        archive.setArchiver(ARCHIVER);
        archive.setArchiverContact(ARCHIVER_CONTACT);
        archive.setDataOwner(DATA_OWNER);
        archive.setDataOriginTimespan(DATA_ORIGIN_TIMESPAN);
        archive.setLobFolder(LOB_FOLDER);
        archive.setProducerApplication(PRODUCER_APPLICATION);
        archive.setArchivalDate(ARCHIVAL_DATE);
        MessageDigestType messageDigestType = new MessageDigestType();
        messageDigestType.setDigest(MESSAGE_DIGEST);
        messageDigestType.setDigestType(DigestTypeType.SHA_256);
        archive.getMessageDigest().add(messageDigestType);
        archive.setClientMachine(CLIENT_MACHINE);
        archive.setDatabaseProduct(DATABASE_PRODUCT);

        archive.setConnection(CONNECTION);

        return archive;
    }

    private static final String DB_NAME = "Convertable SIARD Archive in Format 2.1";
    private static final String DESCRIPTION = "Description";
    private static final String ARCHIVER = "Archiver";
    private static final String ARCHIVER_CONTACT = "Archiver Contact";
    private static final String DATA_OWNER = "Data Owner";
    private static final String DATA_ORIGIN_TIMESPAN = "First half of 2020";
    private static final String LOB_FOLDER = "/lob/folder";
    private static final String PRODUCER_APPLICATION = "Producer Application";
    private static final GregorianCalendar MARCH_16_2020 = new GregorianCalendar(2020, Calendar.MARCH, 16);
    private static final XMLGregorianCalendarImpl ARCHIVAL_DATE = new XMLGregorianCalendarImpl(MARCH_16_2020);
    private static final String CONNECTION = "Connection";
    private static final String MESSAGE_DIGEST = "Message Digest";
    private static final String CLIENT_MACHINE = "Client Machine";
    private static final String DATABASE_PRODUCT = "Database Product";
}
