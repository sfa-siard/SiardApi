package ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard22;

import ch.admin.bar.siard2.api.generated.SchemasType;
import ch.admin.bar.siard2.api.generated.old21.SchemaType;

import java.util.List;

public class ConvertableSiard22SchemasType extends SchemasType {
    public static SchemasType from(ch.admin.bar.siard2.api.generated.old21.SchemasType siard21Schemas) {
        SchemasType schemas = new SchemasType();
        List<SchemaType> schema = siard21Schemas.getSchema();
        return schemas;
    }
}
