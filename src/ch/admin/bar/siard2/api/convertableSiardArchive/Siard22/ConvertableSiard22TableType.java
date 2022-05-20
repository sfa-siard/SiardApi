package ch.admin.bar.siard2.api.convertableSiardArchive.Siard22;

import ch.admin.bar.siard2.api.generated.*;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

public class ConvertableSiard22TableType extends TableType {
    public ConvertableSiard22TableType(String name, String description, String folder, BigInteger rows,
                                       List<ColumnType> columns,
                                       Collection<ConvertableSiard22UniqueKeyType> candidateKeys,
                                       Collection<ConvertableSiard22CheckConstraintType> checkConstraints,
                                       List<ConvertableSiard22ForeignKeyTypes> foreignKeys) {
        super();
        this.name = name;
        this.description = description;
        this.folder = folder;
        this.rows = rows;
        this.columns = new ColumnsType();
        this.columns.getColumn().addAll(columns);
        this.candidateKeys = new CandidateKeysType();
        this.candidateKeys.getCandidateKey().addAll(candidateKeys);
        this.checkConstraints = new CheckConstraintsType();
        this.checkConstraints.getCheckConstraint().addAll(checkConstraints);
        this.foreignKeys = new ForeignKeysType();
        this.foreignKeys.getForeignKey().addAll(foreignKeys);
    }
}
