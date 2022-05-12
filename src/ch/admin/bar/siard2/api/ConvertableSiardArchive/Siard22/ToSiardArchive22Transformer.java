package ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard22;

import ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard21.Siard21Transformer;
import ch.admin.bar.siard2.api.generated.SiardArchive;

// understands transformation from SIARD 2.1 to the current Siard Archive
class ToSiardArchive22Transformer implements Siard21Transformer {

    @Override
    public SiardArchive transform() {
        return new SiardArchive();
    }
}
