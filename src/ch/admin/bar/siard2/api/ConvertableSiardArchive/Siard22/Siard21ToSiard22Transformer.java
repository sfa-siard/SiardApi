package ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard22;

import ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard21.*;
import ch.admin.bar.siard2.api.generated.*;

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
                                                getRoutines(convertableSiard21Schema),
                                                getTables(convertableSiard21Schema),
                                                getViews(convertableSiard21Schema));
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

    @Override
    public ConvertableSiard22TableType visit(ConvertableSiard21Table convertableSiard21Table) {

        return new ConvertableSiard22TableType(convertableSiard21Table.getName(),
                                               convertableSiard21Table.getDescription(),
                                               convertableSiard21Table.getFolder(),
                                               convertableSiard21Table.getRows(),
                                               getCandidateKeys(convertableSiard21Table),
                                               getCheckConstraints(convertableSiard21Table),
                                               getForeignKeys(convertableSiard21Table));
    }


    @Override
    public ConvertableSiard22UniqueKeyType visit(ConvertableSiard21UniqueKeyType convertableSiard21UniqueKeyType) {
        return new ConvertableSiard22UniqueKeyType(convertableSiard21UniqueKeyType.getName(),
                                                   convertableSiard21UniqueKeyType.getDescription(),
                                                   convertableSiard21UniqueKeyType.getColumn());
    }

    @Override
    public ConvertableSiard22CheckConstraintType visit(
            ConvertableSiard21CheckConstraintType convertableSiard21CheckConstraintType) {
        return new ConvertableSiard22CheckConstraintType(convertableSiard21CheckConstraintType.getName(),
                                                         convertableSiard21CheckConstraintType.getDescription(),
                                                         convertableSiard21CheckConstraintType.getCondition());
    }

    @Override
    public ConvertableSiard22ForeignKeyTypes visit(
            ConvertableSiard21ForeignKeyTypes convertableSiard21ForeignKeyTypes) {

        return new ConvertableSiard22ForeignKeyTypes(convertableSiard21ForeignKeyTypes.getName(),
                                                     convertableSiard21ForeignKeyTypes.getDescription(),
                                                     convertableSiard21ForeignKeyTypes.getMatchType().value(),
                                                     convertableSiard21ForeignKeyTypes.getDeleteAction().value(),
                                                     convertableSiard21ForeignKeyTypes.getUpdateAction().value(),
                                                     convertableSiard21ForeignKeyTypes.getReferencedSchema(),
                                                     convertableSiard21ForeignKeyTypes.getReferencedTable(),
                                                     getReferences(convertableSiard21ForeignKeyTypes));
    }

    @Override
    public ConvertableSiard22ReferenceType visit(ConvertableSiard21ReferenceType convertableSiard21ReferenceType) {
        return new ConvertableSiard22ReferenceType(convertableSiard21ReferenceType.getReferenced(),
                                                   convertableSiard21ReferenceType.getColumn());
    }

    @Override
    public ConvertableSiard22ViewType visit(ConvertableSiard21ViewType convertableSiard21ViewType) {
        return new ConvertableSiard22ViewType(convertableSiard21ViewType.getName(),
                                              convertableSiard21ViewType.getDescription(),
                                              convertableSiard21ViewType.getRows(),
                                              convertableSiard21ViewType.getQuery(),
                                              convertableSiard21ViewType.getQueryOriginal(),
                                              getColumns(convertableSiard21ViewType));
    }

    @Override
    public ConvertableSiard22ColumnType visit(ConvertableSiard21ColumnType convertableSiard21ColumnType) {
        return new ConvertableSiard22ColumnType(convertableSiard21ColumnType.getName(),
                                                convertableSiard21ColumnType.getDescription(),
                                                convertableSiard21ColumnType.getDefaultValue(),
                                                convertableSiard21ColumnType.getLobFolder(),
                                                convertableSiard21ColumnType.getMimeType(),
                                                convertableSiard21ColumnType.getType(),
                                                convertableSiard21ColumnType.getTypeName(),
                                                convertableSiard21ColumnType.getTypeSchema(),
                                                convertableSiard21ColumnType.getTypeOriginal(),
                                                convertableSiard21ColumnType.getCardinality(),
                                                convertableSiard21ColumnType.isNullable(),
                                                getFields(convertableSiard21ColumnType.getFields()));
    }

    @Override
    public ConvertableSiard22FieldType visit(ConvertableSiard21FieldType convertableSiard21FieldType) {
        return new ConvertableSiard22FieldType(convertableSiard21FieldType.getName(),
                                               convertableSiard21FieldType.getDescription(),
                                               convertableSiard21FieldType.getMimeType(),
                                               convertableSiard21FieldType.getLobFolder(),
                                               getFields(convertableSiard21FieldType.getFields()));
    }


    private List<ReferenceType> getReferences(ConvertableSiard21ForeignKeyTypes convertableSiard21ForeignKeyTypes) {
        return convertableSiard21ForeignKeyTypes.getReference()
                                                .stream()
                                                .map(ConvertableSiard21ReferenceType::new)
                                                .map(reference -> reference.accept(this))
                                                .collect(Collectors.toList());
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
        if (convertableSiard21Routine.getParameters() == null) return EMPTY_LIST;
        return convertableSiard21Routine.getParameters()
                                        .getParameter()
                                        .stream()
                                        .map(ConvertableSiard21Parameter::new)
                                        .map(parameter -> parameter.accept(this))
                                        .collect(Collectors.toList());
    }


    private Collection<ConvertableSiard22TableType> getTables(ConvertableSiard21SchemaType convertableSiard21Schema) {
        if (convertableSiard21Schema.getTables() == null) return EMPTY_LIST;
        return convertableSiard21Schema.getTables()
                                       .getTable()
                                       .stream()
                                       .map(table -> new ConvertableSiard21Table(table))
                                       .map(table -> table.accept(this))
                                       .collect(Collectors.toList());
    }


    private Collection<ConvertableSiard22UniqueKeyType> getCandidateKeys(
            ConvertableSiard21Table convertableSiard21Table) {
        if (convertableSiard21Table.getCandidateKeys() == null) return EMPTY_LIST;
        return convertableSiard21Table.getCandidateKeys()
                                      .getCandidateKey()
                                      .stream()
                                      .map(ConvertableSiard21UniqueKeyType::new)
                                      .map(uniqueKey -> uniqueKey.accept(this))
                                      .collect(Collectors.toList());
    }

    private Collection<ConvertableSiard22CheckConstraintType> getCheckConstraints(
            ConvertableSiard21Table convertableSiard21Table) {
        if (convertableSiard21Table.getCheckConstraints() == null) return EMPTY_LIST;
        return convertableSiard21Table.getCheckConstraints()
                                      .getCheckConstraint()
                                      .stream()
                                      .map(ConvertableSiard21CheckConstraintType::new)
                                      .map(c -> c.accept(this))
                                      .collect(Collectors.toList());
    }

    private List<ConvertableSiard22ForeignKeyTypes> getForeignKeys(ConvertableSiard21Table convertableSiard21Table) {
        if (convertableSiard21Table.getForeignKeys() == null) return EMPTY_LIST;
        return convertableSiard21Table.getForeignKeys()
                                      .getForeignKey()
                                      .stream()
                                      .map(ConvertableSiard21ForeignKeyTypes::new)
                                      .map(foreignKey -> foreignKey.accept(this))
                                      .collect(Collectors.toList());
    }

    private Collection<ConvertableSiard22ViewType> getViews(ConvertableSiard21SchemaType convertableSiard21Schema) {
        if (convertableSiard21Schema.getViews() == null) return EMPTY_LIST;
        return convertableSiard21Schema.getViews()
                                       .getView()
                                       .stream()
                                       .map(ConvertableSiard21ViewType::new)
                                       .map(viewType -> viewType.accept(this))
                                       .collect(Collectors.toList());
    }

    private List<ColumnType> getColumns(ConvertableSiard21ViewType convertableSiard21ViewType) {
        if (convertableSiard21ViewType.getColumns() == null) return EMPTY_LIST;
        return convertableSiard21ViewType.getColumns()
                                         .getColumn()
                                         .stream()
                                         .map(ConvertableSiard21ColumnType::new)
                                         .map(column -> column.accept(this))
                                         .collect(Collectors.toList());

    }

    private List<FieldType> getFields(ch.admin.bar.siard2.api.generated.old21.FieldsType fields) {
        if (fields == null) return EMPTY_LIST;
        return fields.getField()
                     .stream()
                     .map(ConvertableSiard21FieldType::new)
                     .map(field -> field.accept(this))
                     .collect(Collectors.toList());
    }

}