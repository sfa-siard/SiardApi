package ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard22;

import ch.admin.bar.siard2.api.generated.DigestTypeType;
import ch.admin.bar.siard2.api.generated.MessageDigestType;

import java.util.List;
import java.util.stream.Collectors;

// understand
public class ConvertableSiard22MessageDigestType extends MessageDigestType {

    ConvertableSiard22MessageDigestType(String digest, String digestType) {
        this.digest = digest;
        this.digestType = DigestTypeType.fromValue(digestType);
    }

    public static List<MessageDigestType> from(
            List<ch.admin.bar.siard2.api.generated.old21.MessageDigestType> messageDigests) {
        return messageDigests.stream().map(ConvertableSiard22MessageDigestType::convert).collect(Collectors.toList());
    }

    private static ch.admin.bar.siard2.api.generated.MessageDigestType convert(
            ch.admin.bar.siard2.api.generated.old21.MessageDigestType oldMessageDigest) {
        return new ConvertableSiard22MessageDigestType(oldMessageDigest.getDigest(),
                                                       oldMessageDigest.getDigestType().value());

    }

}
