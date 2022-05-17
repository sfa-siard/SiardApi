package ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard22;


import ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard21.ConvertableSiard21Archive;
import ch.admin.bar.siard2.api.generated.SiardArchive;
import ch.admin.bar.siard2.api.generated.old21.*;
import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ConvertableSiard21ArchiveTest {

    @Test
    public void shouldConvertSiardArchive21ToSiardArchive22() {
        // given
        ToSiardArchive22Transformer transformer = new ToSiardArchive22Transformer();
        ConvertableSiard21Archive convertableSiard21Archive = createExampleArchiveWithAllFieldsSet();

        // when
        SiardArchive result = convertableSiard21Archive.transform(transformer);

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

    private void assertSchemas(ch.admin.bar.siard2.api.generated.SchemasType schemas) {
        assertNotNull(schemas);
    }

    private void assertMessageDigests(SiardArchive result) {
        ch.admin.bar.siard2.api.generated.MessageDigestType actualMessageDigest = result.getMessageDigest().get(0);
        assertEquals(MESSAGE_DIGEST, actualMessageDigest.getDigest());
        assertEquals(ch.admin.bar.siard2.api.generated.DigestTypeType.SHA_256,
                     actualMessageDigest.getDigestType());
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

    private SchemasType createSchemas() {
        SchemasType schemas = new SchemasType();
        SchemaType schema = new SchemaType();
        schema.setName("Schame Type Name");
        schema.setDescription("Schema Type Description");
        schema.setFolder("Schema Type Folder");
        schema.setTypes(createTypes());
        schema.setRoutines(createRoutines());
        schema.setTables(createTables());
        schema.setViews(createViews());
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
        column.setNullable(true);
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

    private TablesType createTables() {
        TablesType tables = new TablesType();
        TableType table = new TableType();
        CandidateKeysType candidateKeysType = new CandidateKeysType();
        UniqueKeyType candidateKey = new UniqueKeyType();
        candidateKey.setName("Candidate Key Name");
        candidateKey.setDescription("Candidate Key Description");
        candidateKeysType.getCandidateKey().add(candidateKey);
        table.setCandidateKeys(candidateKeysType);
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

    private RoutinesType createRoutines() {
        RoutinesType routines = new RoutinesType();

        RoutineType routine = new RoutineType();
        routine.setDescription("Routine Type Description");
        routine.setName("Routine Type Name");
        routine.setReturnType("Routine Type Return Type");
        routine.setBody("Routine Type Body");
        routine.setCharacteristic("Routine Type Characteristig");
        ParametersType parameters = new ParametersType();
        ParameterType parameter = new ParameterType();
        parameter.setType("Parameter Type Type");
        parameter.setTypeName("Parameter Type Name");
        parameter.setDescription("Parameter Type Description");
        parameter.setTypeOriginal("Parameter Type Original");
        parameter.setTypeSchema("Parameter Type Schema");
        parameter.setCardinality(BigInteger.ONE);
        parameter.setMode("Parameter Type Mode");
        parameter.setName("Parameter Type Name");
        parameters.getParameter().add(parameter);
        routine.setParameters(parameters);
        routines.getRoutine().add(routine);
        return routines;
    }

    private TypesType createTypes() {
        TypesType types = new TypesType();
        TypeType type = new TypeType();
        type.setDescription("Type Type Description");
        type.setBase("Type Type Base");
        type.setName("Type Type Name");
        type.setUnderType("Type Type Under Type");
        type.setFinal(true);

        AttributesType attributes = new AttributesType();
        AttributeType attribute = new AttributeType();
        attribute.setDescription("Attribute Type Description");
        attribute.setType("Attribute Type Type");
        attribute.setName("Attribute Type Name");
        attribute.setTypeSchema("Attribute Type Schema");
        attribute.setTypeName("Attribute Type Type Name");
        attribute.setCardinality(BigInteger.TEN);
        attribute.setDefaultValue("Attribute Type Default Value");
        attribute.setNullable(true);
        attribute.setTypeOriginal("Attribute Type Original");
        attributes.getAttribute().add(attribute);
        type.setAttributes(attributes);
        types.getType().add(type);
        return types;
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
}
