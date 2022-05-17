package ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard22;

import ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard21.Siard21MessageDigestTransformer;

public class ToSiard22MessageDigestTransformer implements Siard21MessageDigestTransformer {
    @Override
    public ConvertableSiard22MessageDigestType transform(String digest, String digestType) {
        return new ConvertableSiard22MessageDigestType(digest, digestType);
    }
}
