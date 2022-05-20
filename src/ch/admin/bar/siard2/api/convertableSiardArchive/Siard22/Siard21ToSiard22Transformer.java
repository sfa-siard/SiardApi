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
                                             getMessageDigests(siard21Archive.getMessageDigest()),
                                             getSchemas(siard21Archive.getSchemas()),
                                             getUsers(siard21Archive.getUsers()));
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
                                                getTypes(siard21Schema.getTypes()),
                                                getRoutines(siard21Schema.getRoutines()),
                                                getTables(siard21Schema.getTables()),
                                                getViews(siard21Schema.getViews()));
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
                                                 getParameters(siard21Routine.getParameters()));
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
                                               getCandidateKeys(siard21Table.getCandidateKeys()),
                                               getCheckConstraints(siard21Table.getCheckConstraints()),
                                               getForeignKeys(siard21Table.getForeignKeys()));
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
                                                     safeConvert(ofNullable(siard21ForeignKey.getDeleteAction()).map(da -> da.value()),
                                                                 ReferentialActionType::fromValue),
                                                     safeConvert(ofNullable(siard21ForeignKey.getUpdateAction()).map(da -> da.value()),
                                                                 ReferentialActionType::fromValue),
                                                     siard21ForeignKey.getReferencedSchema(),
                                                     siard21ForeignKey.getReferencedTable(),
                                                     getReferences(siard21ForeignKey.getReference()));
    }

    @Override
    public ConvertableSiard22ReferenceType visit(ConvertableSiard21ReferenceType siard21Reference) {
        return new ConvertableSiard22ReferenceType(siard21Reference.getReferenced(), siard21Reference.getColumn());
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
        return new ConvertableSiard22UserType(siard21User.getName(), siard21User.getDescription());
    }

    private List<SchemaType> getSchemas(ch.admin.bar.siard2.api.generated.old21.SchemasType schemasType) {
        if (schemasType == null) return EMPTY_LIST;
        return schemasType.getSchema()
                          .stream()
                          .map(ConvertableSiard21SchemaType::new)
                          .map(schema -> schema.accept(this))
                          .collect(Collectors.toList());
    }

    private List<MessageDigestType> getMessageDigests(
            List<ch.admin.bar.siard2.api.generated.old21.MessageDigestType> messageDigest) {

        if (messageDigest == null) return EMPTY_LIST;
        return messageDigest.stream()
                            .map(ConvertableSiard21MessageDigestType::new)
                            .map(md -> md.accept(this))
                            .collect(Collectors.toList());
    }

    private List<ReferenceType> getReferences(List<ch.admin.bar.siard2.api.generated.old21.ReferenceType> reference) {
        if (reference == null) return EMPTY_LIST;
        return reference.stream()
                        .map(ConvertableSiard21ReferenceType::new)
                        .map(r -> r.accept(this))
                        .collect(Collectors.toList());
    }

    private List<ConvertableSiard22TypeType> getTypes(ch.admin.bar.siard2.api.generated.old21.TypesType types) {
        if (types == null) return EMPTY_LIST;
        return types.getType()
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

    private Collection<ConvertableSiard22RoutineType> getRoutines(
            ch.admin.bar.siard2.api.generated.old21.RoutinesType routines) {
        if (routines == null) return EMPTY_LIST;
        return routines.getRoutine()
                       .stream()
                       .map(ConvertableSiard21Routine::new)
                       .map(routine -> routine.accept(this))
                       .collect(Collectors.toList());
    }

    private List<UserType> getUsers(ch.admin.bar.siard2.api.generated.old21.UsersType users) {
        if (users == null) return EMPTY_LIST;
        return users.getUser()
                    .stream()
                    .map(ConvertableSiard21UserType::new)
                    .map(user -> user.accept(this))
                    .collect(Collectors.toList());
    }

    private List<ConvertablSiard22Parameter> getParameters(
            ch.admin.bar.siard2.api.generated.old21.ParametersType parameters) {
        if (parameters == null) return EMPTY_LIST;
        return parameters.getParameter()
                         .stream()
                         .map(ConvertableSiard21Parameter::new)
                         .map(parameter -> parameter.accept(this))
                         .collect(Collectors.toList());
    }


    private Collection<ConvertableSiard22TableType> getTables(
            ch.admin.bar.siard2.api.generated.old21.TablesType tables) {
        if (tables == null) return EMPTY_LIST;
        return tables.getTable()
                     .stream()
                     .map(ConvertableSiard21Table::new)
                     .map(table -> table.accept(this))
                     .collect(Collectors.toList());
    }


    private Collection<ConvertableSiard22UniqueKeyType> getCandidateKeys(
            ch.admin.bar.siard2.api.generated.old21.CandidateKeysType candidateKeys) {
        if (candidateKeys == null) return EMPTY_LIST;
        return candidateKeys
                .getCandidateKey()
                .stream()
                .map(ConvertableSiard21UniqueKeyType::new)
                .map(uniqueKey -> uniqueKey.accept(this))
                .collect(Collectors.toList());
    }

    private Collection<ConvertableSiard22CheckConstraintType> getCheckConstraints(
            ch.admin.bar.siard2.api.generated.old21.CheckConstraintsType checkConstraints) {
        if (checkConstraints == null) return EMPTY_LIST;
        return checkConstraints.getCheckConstraint()
                               .stream()
                               .map(ConvertableSiard21CheckConstraintType::new)
                               .map(c -> c.accept(this))
                               .collect(Collectors.toList());
    }

    private List<ConvertableSiard22ForeignKeyTypes> getForeignKeys(
            ch.admin.bar.siard2.api.generated.old21.ForeignKeysType foreignKeys) {
        if (foreignKeys == null) return EMPTY_LIST;
        return foreignKeys
                .getForeignKey()
                .stream()
                .map(ConvertableSiard21ForeignKeyTypes::new)
                .map(foreignKey -> foreignKey.accept(this))
                .collect(Collectors.toList());
    }

    private Collection<ConvertableSiard22ViewType> getViews(ch.admin.bar.siard2.api.generated.old21.ViewsType views) {
        if (views == null) return EMPTY_LIST;
        return views.getView()
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

    private <O> O safeConvert(Optional<String> value, Function<String, O> from) {
        return value.map(from).orElse(null);
    }
}