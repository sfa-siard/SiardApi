package ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard22;

import ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard21.ConvertableSiard21Archive;
import ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard21.ConvertableSiard21MessageDigestType;
import ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard21.Siard21Transformer;
import ch.admin.bar.siard2.api.generated.SiardArchive;
import ch.admin.bar.siard2.api.generated.old21.MessageDigestType;
import ch.admin.bar.siard2.api.generated.old21.SchemasType;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

// understands transformation from SIARD 2.1 to the current Siard Archive
public class Siard21ToSiard22Transformer implements Siard21Transformer {


    @Override
    public ConvertableSiard22Archive visit(ConvertableSiard21Archive siard21Archive) {

        List<ch.admin.bar.siard2.api.generated.MessageDigestType> newMessageDigests = siard21Archive.getMessageDigest()
                                                                                                    .stream()
                                                                                                    .map(ConvertableSiard21MessageDigestType::new)
                                                                                                    .map(messageDigest -> messageDigest.accept(
                                                                                                            this))
                                                                                                    .collect(Collectors.toList());

        return new ConvertableSiard22Archive(siard21Archive.getDbname(),
                                             siard21Archive.getDescription(),
                                             siard21Archive.getArchiver(),
                                             siard21Archive.getArchiverContact(),
                                             siard21Archive.getDataOwner(),
                                             siard21Archive.getDataOriginTimespan(),
                                             siard21Archive.getLobFolder(),
                                             siard21Archive.getProducerApplication(),
                                             siard21Archive.getArchivalDate(),
                                             newMessageDigests,
                                             siard21Archive.getClientMachine(),
                                             siard21Archive.getDatabaseProduct(),
                                             siard21Archive.getConnection(),
                                             siard21Archive.getDatabaseUser(),
                                             siard21Archive.getSchemas());
    }


    @Override
    public ConvertableSiard22MessageDigestType visit(ConvertableSiard21MessageDigestType messageDigest) {
        return new ConvertableSiard22MessageDigestType(messageDigest.getDigest(),
                                                       messageDigest.getDigestType().value());
    }

}