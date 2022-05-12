package ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard22;

import ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard21.Siard21Transformer;
import ch.admin.bar.siard2.api.generated.SiardArchive;

import javax.xml.datatype.XMLGregorianCalendar;

// understands transformation from SIARD 2.1 to the current Siard Archive
class ToSiardArchive22Transformer implements Siard21Transformer {
    @Override
    public SiardArchive transform(String dbName, String description, String archiver, String archiverContact,
                                  String dataOwner, String dataOriginTimespan, String lobFolder,
                                  String producerApplication, XMLGregorianCalendar archivalDate,
                                  String connection) {
        return new ConvertableSiard22Archive(dbName,
                                             description,
                                             archiver,
                                             archiverContact,
                                             dataOwner,
                                             dataOriginTimespan,
                                             lobFolder,
                                             producerApplication, archivalDate, connection);
    }


    /*@Override
    public SiardArchive
    transform(String dbName, String description, String archiver, String archiverContact, String dataOwner,
                           String dataOriginTimespan, String lobFolder, String producerApplication,
                           XMLGregorianCalendar archivalDate, List<MessageDigestType> messageDigest) {
        return null;
       *//* return new ConvertableSiard22Archive(dbName,
                                             description,
                                             archiver,
                                             archiverContact,
                                             dataOwner,
                                             dataOriginTimespan,
                                             lobFolder,
                                             producerApplication, archivalDate);*//*
    }*/
}
