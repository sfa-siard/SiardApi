package ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard21;

import javax.xml.datatype.XMLGregorianCalendar;

public interface Siard21Transformer {
    <T> T transform(String dbName, String description, String archiver, String archiverContact, String dataOwner,
                    String dataOriginTimespan, String lobFolder, String producerApplication,
                    XMLGregorianCalendar archivalDate, String connection);
}
