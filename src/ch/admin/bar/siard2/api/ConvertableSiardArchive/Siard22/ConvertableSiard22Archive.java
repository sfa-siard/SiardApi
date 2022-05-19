package ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard22;

import ch.admin.bar.siard2.api.generated.MessageDigestType;
import ch.admin.bar.siard2.api.generated.SchemasType;
import ch.admin.bar.siard2.api.generated.SiardArchive;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.List;

// understands a Siard Archive v 2.2
public class ConvertableSiard22Archive extends SiardArchive {
    ConvertableSiard22Archive(String version, String dbName, String description, String archiver,
                              String archiverContact,
                              String dataOwner, String dataOriginTimespan, String lobFolder, String producerApplication,
                              XMLGregorianCalendar archivalDate,
                              String clientMachine,
                              String databaseProduct, String connection, String databaseUser,
                              List<MessageDigestType> messageDigests,
                              SchemasType schemas) {
        super();
        this.version = version;
        this.dbname = dbName;
        this.description = description;
        this.archiver = archiver;
        this.archiverContact = archiverContact;
        this.dataOwner = dataOwner;
        this.dataOriginTimespan = dataOriginTimespan;
        this.lobFolder = lobFolder;
        this.producerApplication = producerApplication;
        this.archivalDate = archivalDate;
        this.clientMachine = clientMachine;
        this.databaseProduct = databaseProduct;
        this.connection = connection;
        this.databaseUser = databaseUser;
        this.messageDigest = messageDigests;
        this.schemas = schemas;
    }
}

