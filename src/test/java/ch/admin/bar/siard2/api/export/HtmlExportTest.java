package ch.admin.bar.siard2.api.export;


import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.Schema;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class HtmlExportTest {

    private static final File SFDBOE_SIARD = new File("src/test/resources/testfiles/sfdboe.siard");

    @Test
    public void exportAsHtml() throws IOException {
        // GIVEN
        Archive archive = ArchiveImpl.newInstance();
        archive.open(SFDBOE_SIARD);
        Schema schema = archive.getSchema("OE");
        File fileTable = new File("src/test/resources/tmp/CUSTOMERS.html");
        FileOutputStream fosTable = new FileOutputStream(fileTable);

        // when
        schema.getTable("CUSTOMERS")
              .exportAsHtml(fosTable, new File("src/test/resources/tmp/lobs"));
        fosTable.close();
        archive.close();

        // then
        String generatedHtml = new String(Files.readAllBytes(fileTable.toPath()), StandardCharsets.UTF_8);
        String expectedHtml = new String(Files.readAllBytes(Paths.get("src/test/resources/export/CUSTOMERS.html")), StandardCharsets.UTF_8);

        assertEquals("Generated HTML should match expected content",
                     expectedHtml.trim(),
                     generatedHtml.trim());
    }

}