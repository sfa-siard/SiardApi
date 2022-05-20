package ch.admin.bar.siard2.api.convertableSiardArchive.Siard22;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.convertableSiardArchive.Siard21.*;
import ch.admin.bar.siard2.api.generated.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

// understands transformation from SIARD 2.1 to the current SIARD Archive
public class Siard21ToSiard22Transformer implements Siard21Transformer {

    private static final List EMPTY_LIST = new ArrayList<>();

    @Override
    public SiardArchive visit(ConvertableSiard21Archive siard21Archive) {
        return new ConvertableSiard22Archive(Archive.sMETA_DATA_VERSION,
                                             siard21Archive.getDbname(),
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
                                             getSchemas(siard21Archive),
                                             getUsers(siard21Archive));
    }

    @Override
    public MessageDigestType visit(ConvertableSiard21MessageDigestType messageDigest) {
        return new ConvertableSiard22MessageDigestType(messageDigest.getDigest(),
                                                       safeConvert(ofNullable(messageDigest.getDigestType()).map(md -> md.value()),
                                                                   DigestTypeType::fromValue));
    }

    @Override
    public ConvertableSiard22SchemaType visit(ConvertableSiard21SchemaType siard21Schema) {
        return new ConvertableSiard22SchemaType(siard21Schema.getName(),
                                                siard21Schema.getDescription(),
                                                siard21Schema.getFolder(),
                                                getTypes(siard21Schema),
                                                getRoutines(siard21Schema),
                                                getTables(siard21Schema),
                                                getViews(siard21Schema));
    }

    @Override
    public ConvertableSiard22TypeType visit(ConvertableSiard21TypeType siard21Type) {

        return new ConvertableSiard22TypeType(siard21Type.getName(),
                                              siard21Type.getDescription(),
                                              siard21Type.getBase(),
                                              siard21Type.getUnderType(),
                                              siard21Type.isFinal(),
                                              safeConvert(ofNullable(siard21Type.getCategory()).map(c -> c.value()),
                                                          CategoryType::fromValue),
                                              getAttributes(siard21Type.getAttributes()));
    }

    @Override
    public ConvertableSiard22AttributeType visit(ConvertableSiard21AttributeType siard21Attribute) {
        return new ConvertableSiard22AttributeType(siard21Attribute.getName(),
                                                   siard21Attribute.getDescription(),
                                                   siard21Attribute.getType(),
                                                   siard21Attribute.getTypeSchema(),
                                                   siard21Attribute.getTypeName(),
                                                   siard21Attribute.getCardinality(),
                                                   siard21Attribute.getDefaultValue(),
                                                   siard21Attribute.isNullable(),
                                                   siard21Attribute.getTypeOriginal());
    }

    @Override
    public ConvertableSiard22RoutineType visit(ConvertableSiard21Routine siard21Routine) {
        return new ConvertableSiard22RoutineType(siard21Routine.getName(),
                                                 siard21Routine.getDescription(),
                                                 siard21Routine.getBody(),
                                                 siard21Routine.getCharacteristic(),
                                                 siard21Routine.getReturnType(),
                                                 siard21Routine.getSpecificName(),
                                                 siard21Routine.getSource(),
                                                 getParameters(siard21Routine));
    }


    @Override
    public ConvertablSiard22Parameter visit(ConvertableSiard21Parameter siard21Parameter) {
        return new ConvertablSiard22Parameter(siard21Parameter.getName(),
                                              siard21Parameter.getDescription(),
                                              siard21Parameter.getCardinality(),
                                              siard21Parameter.getMode(),
                                              siard21Parameter.getType(),
                                              siard21Parameter.getTypeName(),
                                              siard21Parameter.getTypeSchema(),
                                              siard21Parameter.getTypeOriginal());
    }

    @Override
    public ConvertableSiard22TableType visit(ConvertableSiard21Table siard21Table) {
        return new ConvertableSiard22TableType(siard21Table.getName(),
                                               siard21Table.getDescription(),
                                               siard21Table.getFolder(),
                                               siard21Table.getRows(),
                                               getColumns(siard21Table.getColumns()),
                                               getCandidateKeys(siard21Table),
                                               getCheckConstraints(siard21Table),
                                               getForeignKeys(siard21Table));
    }


    @Override
    public ConvertableSiard22UniqueKeyType visit(ConvertableSiard21UniqueKeyType siard21UniqueKey) {
        return new ConvertableSiard22UniqueKeyType(siard21UniqueKey.getName(),
                                                   siard21UniqueKey.getDescription(),
                                                   siard21UniqueKey.getColumn());
    }

    @Override
    public ConvertableSiard22CheckConstraintType visit(ConvertableSiard21CheckConstraintType siard21CheckConstraint) {
        return new ConvertableSiard22CheckConstraintType(siard21CheckConstraint.getName(),
                                                         siard21CheckConstraint.getDescription(),
                                                         siard21CheckConstraint.getCondition());
    }

    @Override
    public ConvertableSiard22ForeignKeyTypes visit(ConvertableSiard21ForeignKeyTypes siard21ForeignKey) {
        return new ConvertableSiard22ForeignKeyTypes(siard21ForeignKey.getName(),
                                                     siard21ForeignKey.getDescription(),
                                                     safeConvert(ofNullable(siard21ForeignKey.getMatchType()).map(mt -> mt.value()),
                                                                 MatchTypeType::fromValue),
                                                     getReferentialActionType(siard21ForeignKey.getDeleteAction()),
                                                     getReferentialActionType(siard21ForeignKey.getUpdateAction()),
                                                     siard21ForeignKey.getReferencedSchema(),
                                                     siard21ForeignKey.getReferencedTable(),
                                                     getReferences(siard21ForeignKey));
    }

    @Override
    public ConvertableSiard22ReferenceType visit(ConvertableSiard21ReferenceType siard21Reference) {
        return new ConvertableSiard22ReferenceType(siard21Reference.getReferenced(),
                                                   siard21Reference.getColumn());
    }

    @Override
    public ConvertableSiard22ViewType visit(ConvertableSiard21ViewType siard21View) {
        return new ConvertableSiard22ViewType(siard21View.getName(),
                                              siard21View.getDescription(),
                                              siard21View.getRows(),
                                              siard21View.getQuery(),
                                              siard21View.getQueryOriginal(),
                                              getColumns(siard21View.getColumns()));
    }

    @Override
    public ConvertableSiard22ColumnType visit(ConvertableSiard21ColumnType siard21Column) {
        return new ConvertableSiard22ColumnType(siard21Column.getName(),
                                                siard21Column.getDescription(),
                                                siard21Column.getDefaultValue(),
                                                siard21Column.getLobFolder(),
                                                siard21Column.getMimeType(),
                                                siard21Column.getType(),
                                                siard21Column.getTypeName(),
                                                siard21Column.getTypeSchema(),
                                                siard21Column.getTypeOriginal(),
                                                siard21Column.getCardinality(),
                                                siard21Column.isNullable(),
                                                getFields(siard21Column.getFields()));
    }

    @Override
    public ConvertableSiard22FieldType visit(ConvertableSiard21FieldType siard21Field) {
        return new ConvertableSiard22FieldType(siard21Field.getName(),
                                               siard21Field.getDescription(),
                                               siard21Field.getMimeType(),
                                               siard21Field.getLobFolder(),
                                               getFields(siard21Field.getFields()));
    }

    @Override
    public ConvertableSiard22UserType visit(ConvertableSiard21UserType siard21User) {
        return new ConvertableSiard22UserType(siard21User.getName(),
                                              siard21User.getDescription());
    }

    private List<SchemaType> getSchemas(ConvertableSiard21Archive siard21Archive) {
        return siard21Archive.getSchemas()
                             .getSchema()
                             .stream()
                             .map(ConvertableSiard21SchemaType::new)
                             .map(schema -> schema.accept(this))
                             .collect(Collectors.toList());
    }

    private List<MessageDigestType> getMessageDigests(ConvertableSiard21Archive siard21Archive) {
        return siard21Archive.getMessageDigest()
                             .stream()
                             .map(ConvertableSiard21MessageDigestType::new)
                             .map(messageDigest -> messageDigest.accept(this))
                             .collect(Collectors.toList());
    }

    private List<ReferenceType> getReferences(ConvertableSiard21ForeignKeyTypes siard21ForeignKey) {
        return siard21ForeignKey.getReference()
                                .stream()
                                .map(ConvertableSiard21ReferenceType::new)
                                .map(reference -> reference.accept(this))
                                .collect(Collectors.toList());
    }

    private List<ConvertableSiard22TypeType> getTypes(ConvertableSiard21SchemaType siard21Schema) {
        if (siard21Schema.getTypes() == null) return EMPTY_LIST;
        return siard21Schema.getTypes()
                            .getType()
                            .stream()
                            .map(ConvertableSiard21TypeType::new)
                            .map(type -> type.accept(this))
                            .collect(Collectors.toList());
    }

    private Collection<ConvertableSiard22AttributeType> getAttributes(
            ch.admin.bar.siard2.api.generated.old21.AttributesType attributes) {
        if (attributes == null) return EMPTY_LIST;
        return attributes.getAttribute()
                         .stream()
                         .map(ConvertableSiard21AttributeType::new)
                         .map(attribute -> attribute.accept(this))
                         .collect(Collectors.toList());
    }

    private Collection<ConvertableSiard22RoutineType> getRoutines(ConvertableSiard21SchemaType siard21Schema) {
        if (siard21Schema.getRoutines() == null) return EMPTY_LIST;
        return siard21Schema.getRoutines()
                            .getRoutine()
                            .stream()
                            .map(ConvertableSiard21Routine::new)
                            .map(routine -> routine.accept(this))
                            .collect(Collectors.toList());
    }

    private List<UserType> getUsers(ConvertableSiard21Archive siard21Archive) {
        if (siard21Archive.getUsers() == null) return EMPTY_LIST;
        return siard21Archive.getUsers()
                             .getUser()
                             .stream()
                             .map(ConvertableSiard21UserType::new)
                             .map(user -> user.accept(this))
                             .collect(Collectors.toList());
    }

    private List<ConvertablSiard22Parameter> getParameters(ConvertableSiard21Routine siard21Routine) {
        if (siard21Routine.getParameters() == null) return EMPTY_LIST;
        return siard21Routine.getParameters()
                             .getParameter()
                             .stream()
                             .map(ConvertableSiard21Parameter::new)
                             .map(parameter -> parameter.accept(this))
                             .collect(Collectors.toList());
    }


    private Collection<ConvertableSiard22TableType> getTables(ConvertableSiard21SchemaType siard21Schema) {
        if (siard21Schema.getTables() == null) return EMPTY_LIST;
        return siard21Schema.getTables()
                            .getTable()
                            .stream()
                            .map(ConvertableSiard21Table::new)
                            .map(table -> table.accept(this))
                            .collect(Collectors.toList());
    }


    private Collection<ConvertableSiard22UniqueKeyType> getCandidateKeys(ConvertableSiard21Table siard21Table) {
        if (siard21Table.getCandidateKeys() == null) return EMPTY_LIST;
        return siard21Table.getCandidateKeys()
                           .getCandidateKey()
                           .stream()
                           .map(ConvertableSiard21UniqueKeyType::new)
                           .map(uniqueKey -> uniqueKey.accept(this))
                           .collect(Collectors.toList());
    }

    private Collection<ConvertableSiard22CheckConstraintType> getCheckConstraints(
            ConvertableSiard21Table siard21Table) {
        if (siard21Table.getCheckConstraints() == null) return EMPTY_LIST;
        return siard21Table.getCheckConstraints()
                           .getCheckConstraint()
                           .stream()
                           .map(ConvertableSiard21CheckConstraintType::new)
                           .map(c -> c.accept(this))
                           .collect(Collectors.toList());
    }

    private List<ConvertableSiard22ForeignKeyTypes> getForeignKeys(ConvertableSiard21Table siard21Table) {
        if (siard21Table.getForeignKeys() == null) return EMPTY_LIST;
        return siard21Table.getForeignKeys()
                           .getForeignKey()
                           .stream()
                           .map(ConvertableSiard21ForeignKeyTypes::new)
                           .map(foreignKey -> foreignKey.accept(this))
                           .collect(Collectors.toList());
    }

    private Collection<ConvertableSiard22ViewType> getViews(ConvertableSiard21SchemaType siard21Schema) {
        if (siard21Schema.getViews() == null) return EMPTY_LIST;
        return siard21Schema.getViews()
                            .getView()
                            .stream()
                            .map(ConvertableSiard21ViewType::new)
                            .map(viewType -> viewType.accept(this))
                            .collect(Collectors.toList());
    }

    private List<ColumnType> getColumns(ch.admin.bar.siard2.api.generated.old21.ColumnsType columns) {
        if (columns == null) return EMPTY_LIST;
        return columns.getColumn()
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

    private ReferentialActionType getReferentialActionType(
            ch.admin.bar.siard2.api.generated.old21.ReferentialActionType referentialAction) {
        if (referentialAction == null) return null;
        return ReferentialActionType.fromValue(referentialAction.value());
    }

    private <O> O safeConvert(Optional<String> value, Function<String, O> from) {
        return value.map(from).orElse(null);
    }
}