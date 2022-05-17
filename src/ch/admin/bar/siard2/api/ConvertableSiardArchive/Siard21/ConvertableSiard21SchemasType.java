package ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard21;

import ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard22.Siard21ToSiard22Transformer;
import ch.admin.bar.siard2.api.generated.old21.SchemasType;

public class ConvertableSiard21SchemasType extends SchemasType {


    public void accept(Siard21ToSiard22Transformer visitor) {
        visitor.visit(this);
    }
}
