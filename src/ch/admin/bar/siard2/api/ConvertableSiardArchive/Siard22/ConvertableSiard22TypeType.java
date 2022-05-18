package ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard22;


import ch.admin.bar.siard2.api.generated.AttributeType;
import ch.admin.bar.siard2.api.generated.AttributesType;
import ch.admin.bar.siard2.api.generated.TypeType;

import java.util.Collection;

public class ConvertableSiard22TypeType extends TypeType {

    public ConvertableSiard22TypeType(String name, String description, String base, String underType, boolean isFinal,
                                      Collection<? extends AttributeType> attributes) {
        super();
        this.name = name;
        this.description = description;
        this.base = base;
        this.underType = underType;
        this._final = isFinal;
        this.attributes = new AttributesType();
        this.attributes.getAttribute().addAll(attributes);
    }
}
