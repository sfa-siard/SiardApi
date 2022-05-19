package ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard22;

import ch.admin.bar.siard2.api.generated.ColumnType;
import ch.admin.bar.siard2.api.generated.ColumnsType;
import ch.admin.bar.siard2.api.generated.ViewType;

import java.math.BigInteger;
import java.util.List;

public class ConvertableSiard22ViewType extends ViewType {

    public ConvertableSiard22ViewType(String name, String description, BigInteger rows, String query,
                                      String queryOriginal, List<ColumnType> columns) {
        this.name = name;
        this.description = description;
        this.rows = rows;
        this.query = query;
        this.queryOriginal = queryOriginal;
        this.columns = new ColumnsType();
        this.columns.getColumn().addAll(columns);
    }
}
