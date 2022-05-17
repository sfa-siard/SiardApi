package ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard22;

import ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard21.*;
import ch.admin.bar.siard2.api.generated.MessageDigestType;
import ch.admin.bar.siard2.api.generated.SchemaType;
import ch.admin.bar.siard2.api.generated.SchemasType;
import ch.admin.bar.siard2.api.generated.SiardArchive;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

// understands transformation from SIARD 2.1 to the current Siard Archive
public class Siard21ToSiard22Transformer implements Siard21Transformer {

    @Override
    public SiardArchive visit(ConvertableSiard21Archive siard21Archive) {
        List<MessageDigestType> messageDigests = siard21Archive.getMessageDigest()
                                                               .stream()
                                                               .map(messageDigest -> new ConvertableSiard21MessageDigestType(
                                                                       messageDigest).accept(this))
                                                               .collect(Collectors.toList());

        ConvertableSiard22Archive result = new ConvertableSiard22Archive(siard21Archive.getDbname(),
                                                                         siard21Archive.getDescription(),
                                                                         siard21Archive.getArchiver(),
                                                                         siard21Archive.getArchiverContact(),
                                                                         siard21Archive.getDataOwner(),
                                                                         siard21Archive.getDataOriginTimespan(),
                                                                         siard21Archive.getLobFolder(),
                                                                         siard21Archive.getProducerApplication(),
                                                                         siard21Archive.getArchivalDate(),
                                                                         siard21Archive.getClientMachine(),
                                                                         siard21Archive.getDatabaseProduct(),
                                                                         siard21Archive.getConnection(),
                                                                         siard21Archive.getDatabaseUser());
        result.getMessageDigest().addAll(messageDigests);

        SchemasType schemaContainer = new SchemasType();
        Collection<SchemaType> schemas = siard21Archive.getSchemas()
                                                                 .getSchema()
                                                                 .stream()
                                                                 .map(schema -> new ConvertableSiard21SchemaType(schema).accept(
                                                                         this)).collect(Collectors.toList());
        schemaContainer.getSchema().addAll(schemas);
        result.setSchemas(schemaContainer);

        return result;
    }


    @Override
    public MessageDigestType visit(ConvertableSiard21MessageDigestType messageDigest) {
        return new ConvertableSiard22MessageDigestType(messageDigest.getDigest(),
                                                       messageDigest.getDigestType().value());
    }

    @Override
    public void visit(ConvertableSiard21SchemasType convertableSiard21Schemas) {
        convertableSiard21Schemas.getSchema().forEach(schema -> {
            new ConvertableSiard21SchemaType(schema).accept(this);
        });
    }

    @Override
    public ConvertableSiard22SchemaType visit(ConvertableSiard21SchemaType convertableSiard21Schema) {
        return new ConvertableSiard22SchemaType(convertableSiard21Schema.getName(),
                                                convertableSiard21Schema.getDescription(),
                                                convertableSiard21Schema.getFolder());
    }
}