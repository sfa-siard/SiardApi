package ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard22;

import ch.admin.bar.siard2.api.generated.TableType;

import java.math.BigInteger;

public class ConvertableSiard22TableType extends TableType {
    public ConvertableSiard22TableType(String name, String description, String folder, BigInteger rows) {
        this.name = name;
        this.description = description;
        this.folder = folder;
        this.rows = rows;
    }
}
