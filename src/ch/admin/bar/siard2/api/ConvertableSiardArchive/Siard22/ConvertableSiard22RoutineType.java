package ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard22;

import ch.admin.bar.siard2.api.generated.RoutineType;

public class ConvertableSiard22RoutineType extends RoutineType {


    public ConvertableSiard22RoutineType(String name, String description, String body, String characteristic,
                                         String returnType, String specificName, String source) {
        this.name = name;
        this.description = description;
        this.body = body;
        this.characteristic = characteristic;
        this.returnType = returnType;
        this.specificName = specificName;
        this.source = source;
        //TODO: this.parameters = parameters
    }
}
