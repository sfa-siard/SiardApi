package ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard22;

import ch.admin.bar.siard2.api.generated.RoutineType;
import ch.admin.bar.siard2.api.generated.RoutinesType;
import ch.admin.bar.siard2.api.generated.SchemaType;
import ch.admin.bar.siard2.api.generated.TypesType;

import java.util.Collection;
import java.util.List;

public class ConvertableSiard22SchemaType extends SchemaType {
        public ConvertableSiard22SchemaType(String name, String description, String folder,
                                            List<ConvertableSiard22TypeType> types, Collection<ConvertableSiard22RoutineType> newRoutines) {
        super();
        this.name = name;
        this.description = description;
        this.folder = folder;
        this.types = new TypesType();
        this.getTypes().getType().addAll(types);
        this.routines = new RoutinesType();
        this.routines.getRoutine().addAll(newRoutines);
    }
}
