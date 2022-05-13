package ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard21;

import ch.admin.bar.siard2.api.generated.old21.MessageDigestType;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.List;

public interface Siard21Transformer<T> {
    T transform(String dbName, String description, String archiver, String archiverContact, String dataOwner,
                String dataOriginTimespan, String lobFolder, String producerApplication,
                XMLGregorianCalendar archivalDate, String connection,
                List<MessageDigestType> messageDigest, String clientMachine, String databaseProduct);
}
