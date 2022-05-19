package ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard22;

import ch.admin.bar.siard2.api.generated.DigestTypeType;
import ch.admin.bar.siard2.api.generated.MessageDigestType;

import java.util.List;
import java.util.stream.Collectors;

// understand
public class ConvertableSiard22MessageDigestType extends MessageDigestType {

    ConvertableSiard22MessageDigestType(String digest, String digestType) {
        super();
        this.digest = digest;
        this.digestType = DigestTypeType.fromValue(digestType);
    }
}
