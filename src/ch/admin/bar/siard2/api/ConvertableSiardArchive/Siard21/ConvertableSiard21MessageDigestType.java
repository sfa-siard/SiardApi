package ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard21;


import ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard22.ConvertableSiard22MessageDigestType;
import ch.admin.bar.siard2.api.generated.old21.MessageDigestType;

public class ConvertableSiard21MessageDigestType extends ch.admin.bar.siard2.api.generated.old21.MessageDigestType {


    public ConvertableSiard21MessageDigestType(MessageDigestType messageDigest) {
        super();
        this.digest = messageDigest.getDigest();
        this.digestType = messageDigest.getDigestType();
    }

    public ConvertableSiard22MessageDigestType accept(Siard21Transformer visitor) {
        return visitor.visit(this);
    }
}
