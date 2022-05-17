package ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard21;

import ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard22.Siard21ToSiard22Transformer;
import ch.admin.bar.siard2.api.generated.old21.SchemaType;
import ch.admin.bar.siard2.api.generated.old21.SchemasType;

public class ConvertableSiard21SchemasType extends SchemasType {


    public ConvertableSiard21SchemasType(SchemaType schemas) {
        super();
        this.getSchema().add(schemas);
    }

    public void accept(Siard21ToSiard22Transformer visitor) {
        visitor.visit(this);
    }
}
