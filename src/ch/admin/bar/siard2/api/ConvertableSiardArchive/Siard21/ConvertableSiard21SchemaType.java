package ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard21;

import ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard22.ConvertableSiard22SchemaType;
import ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard22.Siard21ToSiard22Transformer;
import ch.admin.bar.siard2.api.generated.old21.SchemaType;

public class ConvertableSiard21SchemaType extends SchemaType {


    public ConvertableSiard21SchemaType(SchemaType schema) {
        super();
        this.name = schema.getName();
        this.description = schema.getDescription();
        this.folder = schema.getFolder();
        this.types = schema.getTypes();
    }

    public ConvertableSiard22SchemaType accept(Siard21ToSiard22Transformer visitor) {
        return visitor.visit(this);
    }
}
