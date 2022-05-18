package ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard21;

import ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard22.ConvertableSiard22TypeType;
import ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard22.Siard21ToSiard22Transformer;
import ch.admin.bar.siard2.api.generated.old21.TypeType;

public class ConvertableSiard21TypeType extends TypeType {


    public ConvertableSiard21TypeType(TypeType type) {
        this.name = type.getName();
        this.description = type.getDescription();
        this.base = type.getBase();
        this.underType = type.getUnderType();
        this._final = type.isFinal();
    }

    public ConvertableSiard22TypeType accept(Siard21ToSiard22Transformer visitor) {
        return visitor.visit(this);
    }
}
