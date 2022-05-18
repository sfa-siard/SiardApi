package ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard22;

import ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard21.*;
import ch.admin.bar.siard2.api.generated.MessageDigestType;
import ch.admin.bar.siard2.api.generated.SchemaType;
import ch.admin.bar.siard2.api.generated.SchemasType;
import ch.admin.bar.siard2.api.generated.SiardArchive;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

// understands transformation from SIARD 2.1 to the current Siard Archive
public class Siard21ToSiard22Transformer implements Siard21Transformer {

    private static final List EMPTY_LIST = new ArrayList<>();

    @Override
    public SiardArchive visit(ConvertableSiard21Archive siard21Archive) {
        return new ConvertableSiard22Archive(siard21Archive.getDbname(),
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
                                             siard21Archive.getDatabaseUser(),
                                             getMessageDigests(siard21Archive),
                                             getSchema(siard21Archive));
    }

    @Override
    public MessageDigestType visit(ConvertableSiard21MessageDigestType messageDigest) {
        return new ConvertableSiard22MessageDigestType(messageDigest.getDigest(),
                                                       messageDigest.getDigestType().value());
    }

    @Override
    public ConvertableSiard22SchemaType visit(ConvertableSiard21SchemaType convertableSiard21Schema) {

        return new ConvertableSiard22SchemaType(convertableSiard21Schema.getName(),
                                                convertableSiard21Schema.getDescription(),
                                                convertableSiard21Schema.getFolder(),
                                                getTypes(convertableSiard21Schema),
                                                getRoutines(convertableSiard21Schema));
    }


    @Override
    public ConvertableSiard22TypeType visit(ConvertableSiard21TypeType convertableSiard21TypeType) {
        Collection<ConvertableSiard22AttributeType> attributes = convertableSiard21TypeType.getAttributes()
                                                                                           .getAttribute()
                                                                                           .stream()
                                                                                           .map(attribute -> new ConvertableSiard21AttributeType(
                                                                                                   attribute).accept(
                                                                                                   this))
                                                                                           .collect(Collectors.toList());
        return new ConvertableSiard22TypeType(convertableSiard21TypeType.getName(),
                                              convertableSiard21TypeType.getDescription(),
                                              convertableSiard21TypeType.getBase(),
                                              convertableSiard21TypeType.getUnderType(),
                                              convertableSiard21TypeType.isFinal(),
                                              attributes);
    }

    @Override
    public ConvertableSiard22AttributeType visit(ConvertableSiard21AttributeType convertableSiard21AttributeType) {
        return new ConvertableSiard22AttributeType(convertableSiard21AttributeType.getName(),
                                                   convertableSiard21AttributeType.getDescription(),
                                                   convertableSiard21AttributeType.getType(),
                                                   convertableSiard21AttributeType.getTypeSchema(),
                                                   convertableSiard21AttributeType.getTypeName(),
                                                   convertableSiard21AttributeType.getCardinality(),
                                                   convertableSiard21AttributeType.getDefaultValue(),
                                                   convertableSiard21AttributeType.isNullable(),
                                                   convertableSiard21AttributeType.getTypeOriginal());
    }

    @Override
    public ConvertableSiard22RoutineType visit(ConvertableSiard21Routine convertableSiard21Routine) {
        return new ConvertableSiard22RoutineType(convertableSiard21Routine.getName(),
                                                 convertableSiard21Routine.getDescription(),
                                                 convertableSiard21Routine.getBody(),
                                                 convertableSiard21Routine.getCharacteristic(),
                                                 convertableSiard21Routine.getReturnType(),
                                                 convertableSiard21Routine.getSpecificName(),
                                                 convertableSiard21Routine.getSource(),
                                                 getParameters(convertableSiard21Routine));
    }


    @Override
    public ConvertablSiard22Parameter visit(ConvertableSiard21Parameter convertableSiard21Parameter) {
        return new ConvertablSiard22Parameter(convertableSiard21Parameter.getName(),
                                              convertableSiard21Parameter.getDescription(),
                                              convertableSiard21Parameter.getCardinality(),
                                              convertableSiard21Parameter.getMode(),
                                              convertableSiard21Parameter.getType(),
                                              convertableSiard21Parameter.getTypeName(),
                                              convertableSiard21Parameter.getTypeSchema(),
                                              convertableSiard21Parameter.getTypeOriginal());
    }

    private SchemasType getSchema(ConvertableSiard21Archive siard21Archive) {
        SchemasType schemaContainer = new SchemasType();
        Collection<SchemaType> schemas = siard21Archive.getSchemas()
                                                       .getSchema()
                                                       .stream()
                                                       .map(schema -> new ConvertableSiard21SchemaType(schema).accept(
                                                               this))
                                                       .collect(Collectors.toList());
        schemaContainer.getSchema().addAll(schemas);
        return schemaContainer;
    }

    private List<MessageDigestType> getMessageDigests(ConvertableSiard21Archive siard21Archive) {
        return siard21Archive.getMessageDigest()
                             .stream()
                             .map(messageDigest -> new ConvertableSiard21MessageDigestType(messageDigest).accept(this))
                             .collect(Collectors.toList());
    }

    private List<ConvertableSiard22TypeType> getTypes(ConvertableSiard21SchemaType convertableSiard21Schema) {
        if (convertableSiard21Schema.getTypes() == null) return EMPTY_LIST;
        return convertableSiard21Schema.getTypes()
                                       .getType()
                                       .stream()
                                       .map(type -> new ConvertableSiard21TypeType(type).accept(this))
                                       .collect(Collectors.toList());
    }

    private Collection<ConvertableSiard22RoutineType> getRoutines(
            ConvertableSiard21SchemaType convertableSiard21Schema) {
        if (convertableSiard21Schema.getRoutines() == null) return EMPTY_LIST;
        return convertableSiard21Schema.getRoutines()
                                       .getRoutine()
                                       .stream()
                                       .map(routine -> new ConvertableSiard21Routine(routine).accept(this))
                                       .collect(Collectors.toList());
    }

    private List<ConvertablSiard22Parameter> getParameters(ConvertableSiard21Routine convertableSiard21Routine) {
        if(convertableSiard21Routine.getParameters() == null) return EMPTY_LIST;
        return convertableSiard21Routine.getParameters()
                                        .getParameter()
                                        .stream()
                                        .map(ConvertableSiard21Parameter::new)
                                        .map(parameter -> parameter.accept(this))
                                        .collect(Collectors.toList());
    }


}