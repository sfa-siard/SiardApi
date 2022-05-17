package ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard22;

import ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard21.ConvertableSiard21Archive;
import ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard21.ConvertableSiard21MessageDigestType;
import ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard21.Siard21Transformer;
import ch.admin.bar.siard2.api.generated.old21.MessageDigestType;
import ch.admin.bar.siard2.api.generated.old21.SchemasType;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

// understands transformation from SIARD 2.1 to the current Siard Archive
public class ToSiardArchive22Transformer implements Siard21Transformer<ConvertableSiard22Archive> {


    private ToSiard22MessageDigestTransformer toSiard22MessageDigestTransformer = new ToSiard22MessageDigestTransformer();

    @Override
    public ConvertableSiard22Archive transform(String dbName, String description, String archiver,
                                               String archiverContact, String dataOwner, String dataOriginTimespan,
                                               String lobFolder, String producerApplication,
                                               XMLGregorianCalendar archivalDate,
                                               List<MessageDigestType> messageDigests, String clientMachine,
                                               String databaseProduct, String connection, String databaseUser,
                                               SchemasType schemas) {

        List<ch.admin.bar.siard2.api.generated.MessageDigestType> newMessageDigests = messageDigests.stream()
                                                                                                    .map(this::convert)
                                                                                                    .collect(Collectors.toList());
        return new ConvertableSiard22Archive(dbName,
                                             description,
                                             archiver,
                                             archiverContact,
                                             dataOwner,
                                             dataOriginTimespan,
                                             lobFolder,
                                             producerApplication,
                                             archivalDate,
                                             newMessageDigests,
                                             clientMachine,
                                             databaseProduct,
                                             connection,
                                             databaseUser,
                                             schemas);
    }

    private ch.admin.bar.siard2.api.generated.MessageDigestType convert(MessageDigestType messageDigestType) {
        // TODO: there is a lot of casting going on here - could I get rid of this?
        ConvertableSiard21MessageDigestType messageDigestType1 = new ConvertableSiard21MessageDigestType(messageDigestType);
        return (ch.admin.bar.siard2.api.generated.MessageDigestType) messageDigestType1.transform(toSiard22MessageDigestTransformer);
    }

}
