package ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard21;

import ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard22.ConvertableSiard22Archive;
import ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard22.ConvertableSiard22MessageDigestType;
import ch.admin.bar.siard2.api.generated.SiardArchive;

public interface Siard21Transformer {
    void visit(ConvertableSiard21Archive siard21Archive);


    void visit(ConvertableSiard21MessageDigestType messageDigest);

    SiardArchive get();
}
