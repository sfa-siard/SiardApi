package ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard22;

import ch.admin.bar.siard2.api.generated.CandidateKeysType;
import ch.admin.bar.siard2.api.generated.CheckConstraintsType;
import ch.admin.bar.siard2.api.generated.ForeignKeysType;
import ch.admin.bar.siard2.api.generated.TableType;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

public class ConvertableSiard22TableType extends TableType {
    public ConvertableSiard22TableType(String name, String description, String folder, BigInteger rows,
                                       Collection<ConvertableSiard22UniqueKeyType> candidateKeys,
                                       Collection<ConvertableSiard22CheckConstraintType> checkConstraints,
                                       List<ConvertableSiard22ForeignKeyTypes> foreignKeys) {
        this.name = name;
        this.description = description;
        this.folder = folder;
        this.rows = rows;
        this.candidateKeys = new CandidateKeysType();
        this.candidateKeys.getCandidateKey().addAll(candidateKeys);
        this.checkConstraints = new CheckConstraintsType();
        this.checkConstraints.getCheckConstraint().addAll(checkConstraints);
        this.foreignKeys = new ForeignKeysType();
        this.foreignKeys.getForeignKey().addAll(foreignKeys);
    }
}
