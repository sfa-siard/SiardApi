package ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard22;

import ch.admin.bar.siard2.api.generated.ForeignKeyType;
import ch.admin.bar.siard2.api.generated.MatchTypeType;
import ch.admin.bar.siard2.api.generated.ReferentialActionType;
import ch.admin.bar.siard2.api.generated.ReferenceType;

import java.util.List;

public class ConvertableSiard22ForeignKeyTypes extends ForeignKeyType {
    public ConvertableSiard22ForeignKeyTypes(String name, String description, String matchType,
                                             String deleteAction, String updateAction,
                                             String referencedSchema, String referencedTable,
                                             List<ReferenceType> references) {
        super();
        this.name = name;
        this.description = description;
        this.matchType = MatchTypeType.fromValue(matchType);
        this.deleteAction = ReferentialActionType.fromValue(deleteAction);
        this.updateAction = ReferentialActionType.fromValue(updateAction);
        this.referencedSchema = referencedSchema;
        this.referencedTable = referencedTable;
        this.reference = references;
    }
}
