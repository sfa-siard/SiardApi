package ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard21;


// understands a convertable SIARD 2.1 archive
public class ConvertableSiard21Archive extends ch.admin.bar.siard2.api.generated.old21.SiardArchive {

    public ConvertableSiard21Archive() {
        super();
    }

    public <T> T transform(Siard21Transformer transformer) {
        return transformer.transform();
    }
}
