package ch.admin.bar.siard2.api.convertableSiardArchive.Siard22;

import ch.admin.bar.siard2.api.generated.ParametersType;
import ch.admin.bar.siard2.api.generated.RoutineType;

import java.util.List;

public class ConvertableSiard22RoutineType extends RoutineType {

    public ConvertableSiard22RoutineType(String name, String description, String body, String characteristic,
                                         String returnType, String specificName, String source,
                                         List<ConvertablSiard22Parameter> parameters) {
        super();
        this.name = name;
        this.description = description;
        this.body = body;
        this.characteristic = characteristic;
        this.returnType = returnType;
        this.specificName = specificName;
        this.source = source;
        this.parameters = new ParametersType();
        this.parameters.getParameter().addAll(parameters);
    }
}
