package ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard22;

import ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard21.ConvertableSiard21Archive;
import ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard21.ConvertableSiard21MessageDigestType;
import ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard21.Siard21Transformer;
import ch.admin.bar.siard2.api.generated.SiardArchive;

// understands transformation from SIARD 2.1 to the current Siard Archive
public class Siard21ToSiard22Transformer implements Siard21Transformer {

    private ConvertableSiard22Archive siard22Archive;

    @Override
    public void visit(ConvertableSiard21Archive siard21Archive) {
        this.siard22Archive = new ConvertableSiard22Archive(siard21Archive.getDbname(),
                                                            siard21Archive.getDescription(),
                                                            siard21Archive.getArchiver(),
                                                            siard21Archive.getArchiverContact(),
                                                            siard21Archive.getDataOwner(),
                                                            siard21Archive.getDataOriginTimespan(),
                                                            siard21Archive.getLobFolder(),
                                                            siard21Archive.getProducerApplication(),
                                                            siard21Archive.getArchivalDate(),
                                                            siard21Archive.getClientMachine(),
                                                            siard21Archive.getDatabaseProduct(),
                                                            siard21Archive.getConnection(),
                                                            siard21Archive.getDatabaseUser(),
                                                            siard21Archive.getSchemas());

        siard21Archive.getMessageDigest()
                      .forEach(messageDigest -> new ConvertableSiard21MessageDigestType(messageDigest).accept(this));
    }


    @Override
    public void visit(ConvertableSiard21MessageDigestType messageDigest) {
        this.siard22Archive.getMessageDigest()
                           .add(new ConvertableSiard22MessageDigestType(messageDigest.getDigest(),
                                                                        messageDigest.getDigestType().value()));
    }

    @Override
    public SiardArchive get() {
        return this.siard22Archive;
    }

}