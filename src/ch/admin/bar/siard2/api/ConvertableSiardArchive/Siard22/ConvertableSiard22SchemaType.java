package ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard22;

import ch.admin.bar.siard2.api.generated.*;

import java.util.Collection;
import java.util.List;

public class ConvertableSiard22SchemaType extends SchemaType {
    public ConvertableSiard22SchemaType(String name, String description, String folder,
                                        Collection<ConvertableSiard22TypeType> types,
                                        Collection<ConvertableSiard22RoutineType> routines,
                                        Collection<ConvertableSiard22TableType> tables) {
        super();
        this.name = name;
        this.description = description;
        this.folder = folder;
        this.types = new TypesType();
        this.getTypes().getType().addAll(types);
        this.routines = new RoutinesType();
        this.routines.getRoutine().addAll(routines);
        this.tables = new TablesType();
        this.tables.getTable().addAll(tables);
    }
}
