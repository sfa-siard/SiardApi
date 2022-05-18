package ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard22;

import ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard21.ConvertableSiard21Archive;
import ch.admin.bar.siard2.api.generated.SiardArchive;
import ch.admin.bar.siard2.api.generated.old21.*;
import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.*;

public class ConvertableSiard21ArchiveTest {


    public static final String CANDIDATE_KEY_NAME = "Candidate Key Name";
    public static final String CANDIDATE_KEY_DESCRIPTION = "Candidate Key Description";
    public static final String CANDIDATE_KEY_COLUMN_1 = "Candidate Key Column 1";
    public static final String CANDIDATE_KEY_COLUMN_2 = "Candidate Key Column 2";

    @Test
    public void shouldConvertSiardArchive21ToSiardArchive22() {
        // given
        Siard21ToSiard22Transformer visitor = new Siard21ToSiard22Transformer();
        ConvertableSiard21Archive convertableSiard21Archive = createExampleArchiveWithAllFieldsSet();

        // when
        SiardArchive result = convertableSiard21Archive.accept(visitor);

        // then
        assertSiardArchiveWithAllFieldsSet(result);
    }

    private void assertSiardArchiveWithAllFieldsSet(SiardArchive result) {
        assertNotNull(result);
        assertEquals(DB_NAME, result.getDbname());
        assertEquals(DESCRIPTION, result.getDescription());
        assertEquals(ARCHIVER, result.getArchiver());
        assertEquals(ARCHIVER_CONTACT, result.getArchiverContact());
        assertEquals(DATA_OWNER, result.getDataOwner());
        assertEquals(DATA_ORIGIN_TIMESPAN, result.getDataOriginTimespan());
        assertEquals(LOB_FOLDER, result.getLobFolder());
        assertEquals(PRODUCER_APPLICATION, result.getProducerApplication());
        assertEquals(ARCHIVAL_DATE, result.getArchivalDate());
        assertMessageDigests(result);
        assertEquals(CLIENT_MACHINE, result.getClientMachine());
        assertEquals(DATABASE_PRODUCT, result.getDatabaseProduct());
        assertEquals(CONNECTION, result.getConnection());
        assertEquals(DATABASE_USER, result.getDatabaseUser());
        assertSchemas(result.getSchemas());
    }

    private void assertMessageDigests(SiardArchive result) {
        ch.admin.bar.siard2.api.generated.MessageDigestType actualMessageDigest = result.getMessageDigest().get(0);
        assertEquals(MESSAGE_DIGEST, actualMessageDigest.getDigest());
        assertEquals(ch.admin.bar.siard2.api.generated.DigestTypeType.SHA_256,
                     actualMessageDigest.getDigestType());
    }

    private void assertSchemas(ch.admin.bar.siard2.api.generated.SchemasType schemas) {
        assertNotNull(schemas);
        assertEquals(schemas.getSchema().size(), 1);
        ch.admin.bar.siard2.api.generated.SchemaType schema = schemas.getSchema().get(0);
        assertNotNull(schema);
        assertSchema(schema);
    }

    private ConvertableSiard21Archive createExampleArchiveWithAllFieldsSet() {
        ConvertableSiard21Archive archive = new ConvertableSiard21Archive();
        archive.setDbname(DB_NAME);
        archive.setDescription(DESCRIPTION);
        archive.setArchiver(ARCHIVER);
        archive.setArchiverContact(ARCHIVER_CONTACT);
        archive.setDataOwner(DATA_OWNER);
        archive.setDataOriginTimespan(DATA_ORIGIN_TIMESPAN);
        archive.setLobFolder(LOB_FOLDER);
        archive.setProducerApplication(PRODUCER_APPLICATION);
        archive.setArchivalDate(ARCHIVAL_DATE);
        archive.getMessageDigest().add(createMessageDigests());
        archive.setClientMachine(CLIENT_MACHINE);
        archive.setDatabaseProduct(DATABASE_PRODUCT);
        archive.setConnection(CONNECTION);
        archive.setDatabaseUser(DATABASE_USER);
        archive.setSchemas(createSchemas());
        return archive;
    }

    private MessageDigestType createMessageDigests() {
        MessageDigestType messageDigests = new MessageDigestType();
        messageDigests.setDigest(MESSAGE_DIGEST);
        messageDigests.setDigestType(DigestTypeType.SHA_256);
        return messageDigests;
    }

    private void assertSchema(ch.admin.bar.siard2.api.generated.SchemaType schema) {
        assertEquals(schema.getName(), SCHEMA_NAME);
        assertEquals(schema.getDescription(), SCHEMA_DESCRIPTION);
        assertEquals(schema.getFolder(), SCHEMA_FOLDER);
        assertTypes(schema.getTypes());
        assertRoutines(schema.getRoutines());
        assertTables(schema.getTables());
    }

    private SchemasType createSchemas() {
        SchemasType schemas = new SchemasType();
        SchemaType schema = new SchemaType();
        schema.setName(SCHEMA_NAME);
        schema.setDescription(SCHEMA_DESCRIPTION);
        schema.setFolder(SCHEMA_FOLDER);
        schema.setTypes(createTypes());
        schema.setRoutines(createRoutines());
        schema.setViews(createViews());
        schema.setTables(createTables());
        schemas.getSchema().add(schema);
        return schemas;
    }

    private ViewsType createViews() {
        ViewsType views = new ViewsType();
        ViewType view = new ViewType();
        view.setName("View Name");
        view.setDescription("View Description");
        view.setRows(BigInteger.valueOf(512));
        view.setQuery("View Query");
        view.setQueryOriginal("View Query Original");
        ColumnsType columns = new ColumnsType();
        ColumnType column = new ColumnType();
        column.setName("Column Name");
        column.setDescription("Column Description");
        column.setDefaultValue("Column Default Value");
        column.setLobFolder("Column Log Folder");
        column.setMimeType("Column Mime Type");
        column.setType("Column Type");
        column.setTypeName("Column Type Name");
        column.setTypeSchema("Column Type Schema");
        column.setTypeOriginal("Column Type Original");
        column.setCardinality(BigInteger.valueOf(9));
        column.setNullable(TYPE_IS_FINAL);
        FieldsType fields = new FieldsType();
        FieldType field = new FieldType();
        field.setName("Field Name");
        field.setDescription("Field Description");
        field.setLobFolder("Field Lob Folder");
        field.setMimeType("Field Mime Type");
        FieldsType subFields = new FieldsType();
        FieldType subField = new FieldType();
        subField.setName("Sub Field Name");
        subField.setDescription("Sub Field Description");
        subField.setMimeType("Sub Field Mime Type");
        subField.setLobFolder("Sub Field Lob Folder");
        subField.setFields(null); // stop nesting of fields at this point - the schema allows further nesting, but two levels should be ok for this test
        subFields.getField().add(subField);
        field.setFields(subFields);
        fields.getField().add(field);
        column.setFields(fields);
        columns.getColumn().add(column);
        view.setColumns(columns);
        views.getView().add(view);
        return views;
    }

    private void assertTables(ch.admin.bar.siard2.api.generated.TablesType tables) {
        assertNotNull(tables);
        assertEquals(1, tables.getTable().size());
        ch.admin.bar.siard2.api.generated.TableType table = tables.getTable().get(0);
        assertEquals(TABLE_NAME, table.getName());
        assertEquals(TABLE_DESCRIPTION, table.getDescription());
        assertEquals(TABLE_FOLDER, table.getFolder());
        assertEquals(TABLE_ROWS, table.getRows());

        assertCandidateKeys(table.getCandidateKeys());
    }



    private TablesType createTables() {
        TablesType tables = new TablesType();
        TableType table = new TableType();
        table.setName(TABLE_NAME);
        table.setDescription(TABLE_DESCRIPTION);
        table.setFolder(TABLE_FOLDER);
        table.setRows(TABLE_ROWS);

        table.setCandidateKeys(createCandidateKeys());
        CheckConstraintsType checkConstraintsType = new CheckConstraintsType();

        CheckConstraintType checkConstraint = new CheckConstraintType();
        checkConstraint.setCondition("Check Constraint Condition");
        checkConstraint.setDescription("Check Constraint Description");
        checkConstraint.setName("Check Constraint Name");
        checkConstraintsType.getCheckConstraint().add(checkConstraint);
        table.setCheckConstraints(checkConstraintsType);

        ForeignKeysType foreignKeys = new ForeignKeysType();
        ForeignKeyType foreignKey = new ForeignKeyType();
        foreignKey.setName("Foreign Key Name");
        foreignKey.setDescription("Foreign Key Description");
        foreignKey.setMatchType(MatchTypeType.FULL);
        foreignKey.setDeleteAction(ReferentialActionType.NO_ACTION);
        foreignKey.setUpdateAction(ReferentialActionType.CASCADE);
        foreignKey.setReferencedTable("Foreign Key Referenced Table");
        foreignKey.setReferencedSchema("Foreign Key Referenced Schema");
        foreignKeys.getForeignKey().add(foreignKey);
        table.setForeignKeys(foreignKeys);

        tables.getTable().add(table);
        return tables;
    }

    private void assertCandidateKeys(ch.admin.bar.siard2.api.generated.CandidateKeysType candidateKeys) {
        assertNotNull(candidateKeys);
        assertEquals(1, candidateKeys.getCandidateKey().size());
        ch.admin.bar.siard2.api.generated.UniqueKeyType candidateKey = candidateKeys.getCandidateKey().get(0);
        assertEquals(CANDIDATE_KEY_NAME, candidateKey.getName());
        assertEquals(CANDIDATE_KEY_DESCRIPTION, candidateKey.getDescription());
        assertThat(candidateKey.getColumn(), hasItems(CANDIDATE_KEY_COLUMN_1, CANDIDATE_KEY_COLUMN_2));
    }


    private CandidateKeysType createCandidateKeys() {
        CandidateKeysType candidateKeysType = new CandidateKeysType();
        UniqueKeyType candidateKey = new UniqueKeyType();
        candidateKey.setName(CANDIDATE_KEY_NAME);
        candidateKey.setDescription(CANDIDATE_KEY_DESCRIPTION);
        candidateKey.getColumn().addAll(Arrays.asList(CANDIDATE_KEY_COLUMN_1, CANDIDATE_KEY_COLUMN_2));
        candidateKeysType.getCandidateKey().add(candidateKey);
        return candidateKeysType;
    }


    private void assertRoutines(ch.admin.bar.siard2.api.generated.RoutinesType routines) {
        assertNotNull(routines);
        assertEquals(1, routines.getRoutine().size());
        ch.admin.bar.siard2.api.generated.RoutineType routine = routines.getRoutine().get(0);
        assertEquals(ROUTINE_NAME, routine.getName());
        assertEquals(ROUTINE_DESCRIPTION, routine.getDescription());
        assertEquals(ROUTINE_RETURN_TYPE, routine.getReturnType());
        assertEquals(ROUTINE_BODY, routine.getBody());
        assertEquals(ROUTINE_CHARACTERISTICS, routine.getCharacteristic());
        assertEquals(ROUTINE_SPECIFIC_NAME, routine.getSpecificName());
        assertEquals(ROUTINE_SOURCE, routine.getSource());
        assertParameters(routine.getParameters());
    }

    private RoutinesType createRoutines() {
        RoutinesType routines = new RoutinesType();
        RoutineType routine = new RoutineType();
        routine.setName(ROUTINE_NAME);
        routine.setDescription(ROUTINE_DESCRIPTION);
        routine.setReturnType(ROUTINE_RETURN_TYPE);
        routine.setBody(ROUTINE_BODY);
        routine.setCharacteristic(ROUTINE_CHARACTERISTICS);
        routine.setSpecificName(ROUTINE_SPECIFIC_NAME);
        routine.setSource(ROUTINE_SOURCE);
        routine.setParameters(createParameters());
        routines.getRoutine().add(routine);
        return routines;
    }

    private void assertParameters(ch.admin.bar.siard2.api.generated.ParametersType parameters) {
        assertNotNull(parameters);
        assertEquals(1, parameters.getParameter().size());
        ch.admin.bar.siard2.api.generated.ParameterType parameter = parameters.getParameter().get(0);
        assertEquals(PARAMETER_NAME, parameter.getName());
        assertEquals(PARAMETER_DESCRIPTION, parameter.getDescription());
        assertEquals(PARAMETER_CARDINALITY, parameter.getCardinality());
        assertEquals(PARAMETER_MODE, parameter.getMode());
        assertEquals(PARAMETER_TYPE, parameter.getType());
        assertEquals(PARAMETER_ORIGINAL, parameter.getTypeOriginal());
        assertEquals(PARAMETER_SCHEMA, parameter.getTypeSchema());
    }

    private ParametersType createParameters() {
        ParametersType parameters = new ParametersType();
        ParameterType parameter = new ParameterType();
        parameter.setName(PARAMETER_NAME);
        parameter.setDescription(PARAMETER_DESCRIPTION);
        parameter.setCardinality(PARAMETER_CARDINALITY);
        parameter.setMode(PARAMETER_MODE);
        parameter.setType(PARAMETER_TYPE);
        parameter.setTypeOriginal(PARAMETER_ORIGINAL);
        parameter.setTypeSchema(PARAMETER_SCHEMA);
        parameters.getParameter().add(parameter);
        return parameters;
    }

    private void assertTypes(ch.admin.bar.siard2.api.generated.TypesType types) {
        assertNotNull(types);
        assertNotNull(types.getType());
        assertEquals(1, types.getType().size());
        ch.admin.bar.siard2.api.generated.TypeType type = types.getType().get(0);
        assertEquals(TYPE_NAME, type.getName());
        assertEquals(TYPE_DESCRIPTION, type.getDescription());
        assertEquals(TYPE_BASE, type.getBase());
        assertEquals(TYPE_UNDER_TYPE, type.getUnderType());
        assertEquals(TYPE_IS_FINAL, type.isFinal());
        assertAttributes(type.getAttributes());
    }

    private TypesType createTypes() {
        TypesType types = new TypesType();
        TypeType type = new TypeType();
        type.setName(TYPE_NAME);
        type.setDescription(TYPE_DESCRIPTION);
        type.setBase(TYPE_BASE);
        type.setUnderType(TYPE_UNDER_TYPE);
        type.setFinal(TYPE_IS_FINAL);
        type.setAttributes(createAttributes());
        types.getType().add(type);
        return types;
    }

    private void assertAttributes(ch.admin.bar.siard2.api.generated.AttributesType attributes) {
        assertNotNull(attributes);
        assertEquals(1, attributes.getAttribute().size());
        ch.admin.bar.siard2.api.generated.AttributeType attribute = attributes.getAttribute().get(0);
        assertEquals(ATTRIBUTE_NAME, attribute.getName());
        assertEquals(ATTRIBUTE_DESCRIPTION, attribute.getDescription());
        assertEquals(ATTRIBUTE_TYPE, attribute.getType());
        assertEquals(ATTRIBUTE_TYPE_SCHEMA, attribute.getTypeSchema());
        assertEquals(ATTRIBUTE_TYPE_NAME, attribute.getTypeName());
        assertEquals(ATTRIBUTE_CARDINALITY, attribute.getCardinality());
        assertEquals(ATTRIBUTE_DEFAULT_VALUE, attribute.getDefaultValue());
        assertEquals(ATTRIBUTE_IS_NULLABLE, attribute.isNullable());
        assertEquals(ATTRIBUTE_TYPE_ORIGINAL, attribute.getTypeOriginal());
    }

    private AttributesType createAttributes() {
        AttributesType attributes = new AttributesType();
        AttributeType attribute = new AttributeType();
        attribute.setName(ATTRIBUTE_NAME);
        attribute.setDescription(ATTRIBUTE_DESCRIPTION);
        attribute.setType(ATTRIBUTE_TYPE);
        attribute.setTypeSchema(ATTRIBUTE_TYPE_SCHEMA);
        attribute.setTypeName(ATTRIBUTE_TYPE_NAME);
        attribute.setCardinality(ATTRIBUTE_CARDINALITY);
        attribute.setDefaultValue(ATTRIBUTE_DEFAULT_VALUE);
        attribute.setNullable(ATTRIBUTE_IS_NULLABLE);
        attribute.setTypeOriginal(ATTRIBUTE_TYPE_ORIGINAL);
        attributes.getAttribute().add(attribute);
        return attributes;
    }

    private static final String DB_NAME = "Convertable SIARD Archive in Format 2.1";
    private static final String DESCRIPTION = "Description";
    private static final String ARCHIVER = "Archiver";
    private static final String ARCHIVER_CONTACT = "Archiver Contact";
    private static final String DATA_OWNER = "Data Owner";
    private static final String DATA_ORIGIN_TIMESPAN = "First half of 2020";
    private static final String LOB_FOLDER = "/lob/folder";
    private static final String PRODUCER_APPLICATION = "Producer Application";
    private static final GregorianCalendar MARCH_16_2020 = new GregorianCalendar(2020, Calendar.MARCH, 16);
    private static final XMLGregorianCalendarImpl ARCHIVAL_DATE = new XMLGregorianCalendarImpl(MARCH_16_2020);
    private static final String CONNECTION = "Connection";
    private static final String MESSAGE_DIGEST = "Message Digest";
    private static final String CLIENT_MACHINE = "Client Machine";
    private static final String DATABASE_PRODUCT = "Database Product";
    private static final String DATABASE_USER = "Database User";
    private static final String SCHEMA_NAME = "Schema Name";
    private static final String SCHEMA_DESCRIPTION = "Schema Description";
    private static final String SCHEMA_FOLDER = "Schema Folder";

    private static final String TYPE_DESCRIPTION = "Type Description";
    private static final String TYPE_NAME = "Type Name";
    private static final String TYPE_BASE = "Type Base";
    private static final String TYPE_UNDER_TYPE = "Type under Type";
    private static final boolean TYPE_IS_FINAL = true;

    private static final String ATTRIBUTE_NAME = "Attribute Name";
    private static final String ATTRIBUTE_DESCRIPTION = "Attribute Description";
    private static final String ATTRIBUTE_TYPE = "Attribute Type";
    private static final String ATTRIBUTE_TYPE_SCHEMA = "Attribute Type Schema";
    private static final String ATTRIBUTE_TYPE_NAME = "Attribute Type Name";
    private static final String ATTRIBUTE_DEFAULT_VALUE = "Attribute Default Value";
    private static final String ATTRIBUTE_TYPE_ORIGINAL = "Attribute Type Original";
    private static final boolean ATTRIBUTE_IS_NULLABLE = true;
    private static final BigInteger ATTRIBUTE_CARDINALITY = BigInteger.TEN;

    private static final String ROUTINE_DESCRIPTION = "Routine Description";
    private static final String ROUTINE_NAME = "Routine Name";
    private static final String ROUTINE_RETURN_TYPE = "Routine Return Type";
    private static final String ROUTINE_BODY = "Routine Body";
    private static final String ROUTINE_CHARACTERISTICS = "Routine Characteristics";
    private static final String ROUTINE_SPECIFIC_NAME = "Routine Specific Name";
    private static final String ROUTINE_SOURCE = "Routine Source";

    private static final String PARAMETER_NAME = "Parameter Name";
    private static final String PARAMETER_DESCRIPTION = "Parameter Description";
    private static final BigInteger PARAMETER_CARDINALITY = BigInteger.ONE;
    private static final String PARAMETER_MODE = "Parameter Mode";
    private static final String PARAMETER_TYPE = "Parameter Type";
    private static final String PARAMETER_ORIGINAL = "Parameter Original";
    private static final String PARAMETER_SCHEMA = "Parameter Schema";

    private static final String TABLE_NAME = "Table Name";
    private static final String TABLE_DESCRIPTION = "Table Description";
    private static final String TABLE_FOLDER = "Table Folder";
    private static final BigInteger TABLE_ROWS = BigInteger.valueOf(1024);


}
