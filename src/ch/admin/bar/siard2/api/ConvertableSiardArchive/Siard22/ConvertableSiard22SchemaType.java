package ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard22;

import ch.admin.bar.siard2.api.generated.SchemaType;

import java.util.List;

public class ConvertableSiard22SchemaType extends SchemaType {
        public ConvertableSiard22SchemaType(String name, String description, String folder,
                                            List<ConvertableSiard22TypeType> types) {
        super();
        this.name = name;
        this.description = description;
        this.folder = folder;
        this.types = new ch.admin.bar.siard2.api.generated.TypesType();
        this.getTypes().getType().addAll(types);


    }
}
