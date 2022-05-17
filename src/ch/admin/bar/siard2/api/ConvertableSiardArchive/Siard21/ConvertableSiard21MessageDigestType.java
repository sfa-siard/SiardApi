package ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard21;


import ch.admin.bar.siard2.api.generated.old21.MessageDigestType;

public class ConvertableSiard21MessageDigestType extends ch.admin.bar.siard2.api.generated.old21.MessageDigestType {


    public ConvertableSiard21MessageDigestType(MessageDigestType messageDigest) {
        super();
        this.digest = messageDigest.getDigest();
        this.digestType = messageDigest.getDigestType();
    }

    public <T> T transform(Siard21MessageDigestTransformer<T> transformer) {
        return transformer.transform(this.digest, this.digestType.value());
    }
}
