package ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard22;

import ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard21.Siard21Transformer;
import ch.admin.bar.siard2.api.generated.old21.MessageDigestType;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.List;

// understands transformation from SIARD 2.1 to the current Siard Archive
class ToSiardArchive22Transformer implements Siard21Transformer<ConvertableSiard22Archive> {
    @Override
    public ConvertableSiard22Archive transform(String dbName, String description, String archiver,
                                               String archiverContact, String dataOwner, String dataOriginTimespan,
                                               String lobFolder, String producerApplication,
                                               XMLGregorianCalendar archivalDate, String connection,
                                               List<MessageDigestType> messageDigest) {

        return new ConvertableSiard22Archive(dbName,
                                             description,
                                             archiver,
                                             archiverContact,
                                             dataOwner,
                                             dataOriginTimespan,
                                             lobFolder,
                                             producerApplication,
                                             archivalDate,
                                             connection,
                                             ConvertableSiard22MessageDigestType.from(messageDigest));
    }

}
