package ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard21;

public interface Siard21MessageDigestTransformer<T> {

    T transform(String digest, String digestType);
}
