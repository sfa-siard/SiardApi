package ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard22;


import ch.admin.bar.siard2.api.ConvertableSiardArchive.Siard21.ConvertableSiard21Archive;
import ch.admin.bar.siard2.api.generated.SiardArchive;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class ConvertableSiard21ArchiveTest {

    @Test
    public void shouldConvertSiardArchive21ToSiardArchive22() {
        ToSiardArchive22Transformer transformer = new ToSiardArchive22Transformer();
        SiardArchive result = new ConvertableSiard21Archive().transform(transformer);
        assertNotNull(result);
    }
}