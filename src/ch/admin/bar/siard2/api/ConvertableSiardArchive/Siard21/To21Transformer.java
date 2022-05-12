package ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard21;

import ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard10.SiardArchive10Transformer;

public class To21Transformer implements SiardArchive10Transformer {

    @Override
    public ConvertableSiard21Archive transform() {
        return new ConvertableSiard21Archive();
    }
}
