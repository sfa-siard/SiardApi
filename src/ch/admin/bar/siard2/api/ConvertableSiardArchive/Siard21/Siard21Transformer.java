package ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard21;

import ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard22.ConvertableSiard22Archive;
import ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard22.ConvertableSiard22MessageDigestType;

public interface Siard21Transformer {
    ConvertableSiard22Archive visit(ConvertableSiard21Archive siard21Archive);


    ConvertableSiard22MessageDigestType visit(ConvertableSiard21MessageDigestType messageDigest);
}
