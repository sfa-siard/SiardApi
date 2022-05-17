package ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard21;

import ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard22.ConvertableSiard22SchemaType;
import ch.admin.bar.siard2.api.generated.MessageDigestType;
import ch.admin.bar.siard2.api.generated.SiardArchive;

public interface Siard21Transformer {
    SiardArchive visit(ConvertableSiard21Archive siard21Archive);

    MessageDigestType visit(ConvertableSiard21MessageDigestType messageDigest);

    void visit(ConvertableSiard21SchemasType convertableSiard21SchemasType);

    ConvertableSiard22SchemaType visit(ConvertableSiard21SchemaType convertableSiard21SchemaType);
}
