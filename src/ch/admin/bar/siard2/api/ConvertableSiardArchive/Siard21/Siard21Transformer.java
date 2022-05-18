package ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard21;

import ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard22.*;
import ch.admin.bar.siard2.api.generated.MessageDigestType;
import ch.admin.bar.siard2.api.generated.SiardArchive;

public interface Siard21Transformer {
    SiardArchive visit(ConvertableSiard21Archive siard21Archive);

    MessageDigestType visit(ConvertableSiard21MessageDigestType messageDigest);

    ConvertableSiard22SchemaType visit(ConvertableSiard21SchemaType convertableSiard21SchemaType);

    ConvertableSiard22TypeType visit(ConvertableSiard21TypeType convertableSiard21TypeType);

    ConvertableSiard22AttributeType visit(ConvertableSiard21AttributeType convertableSiard21AttributeType);

    ConvertableSiard22RoutineType visit(ConvertableSiard21Routine convertableSiard21Routine);

    ConvertablSiard22Parameter visit(ConvertableSiard21Parameter convertableSiard21Parameter);
}
