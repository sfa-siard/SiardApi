package ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard21;

import ch.admin.bar.siard2.api.generated.SiardArchive;

public interface Siard21Transformer {
    void visit(ConvertableSiard21Archive siard21Archive);

    void visit(ConvertableSiard21MessageDigestType messageDigest);

    void visit(ConvertableSiard21SchemasType convertableSiard21SchemasType);

    void visit(ConvertableSiard21SchemaType convertableSiard21SchemaType);

    SiardArchive get();
}
