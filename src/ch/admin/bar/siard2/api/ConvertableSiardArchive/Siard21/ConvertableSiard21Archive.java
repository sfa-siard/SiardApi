package ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard21;


import ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard22.ConvertableSiard22Archive;
import ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard22.Siard21ToSiard22Transformer;

// understands a convertable SIARD 2.1 archive
public class ConvertableSiard21Archive extends ch.admin.bar.siard2.api.generated.old21.SiardArchive {

    public ConvertableSiard21Archive() {
        super();
    }

    public ConvertableSiard22Archive accept(Siard21ToSiard22Transformer visitor) {
        return visitor.visit(this);
    }
}
