package ch.admin.bar.siard2.api.export;


import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.Schema;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@DisplayName("Test Html Export of Tables")
public class HtmlExportTest {

    private static final File SFDBOE_SIARD = new File("src/test/resources/testfiles/sfdboe.siard");
    private static final File DATALINK_SIARD = new File("src/test/resources/testfiles/sample-datalink-2-2.siard");

    @Test
    @DisplayName("Export a table as HTML")
    public void exportAsHtml() throws IOException {
        // given
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
        String generatedHtml = Files.readString(fileTable.toPath(), StandardCharsets.UTF_8);
        String expectedHtml = Files.readString(Paths.get("src/test/resources/export/CUSTOMERS.html"), StandardCharsets.UTF_8);

        assertEquals(expectedHtml.trim().toLowerCase(), generatedHtml.trim().toLowerCase(), "Generated HTML should match expected content");
    }

    @Test
    @DisplayName("Export a table as HTML with Datalinks")
    public void exportAsHtmlWithDatalink() throws IOException {
        // given
        Archive archive = ArchiveImpl.newInstance();
        archive.open(DATALINK_SIARD);
        Schema schema = archive.getSchema("SampleSchema");
        File fileTable = new File("src/test/resources/tmp/TSIMPLE.html");
        FileOutputStream fosTable = new FileOutputStream(fileTable);

        // when
        schema.getTable("TSIMPLE")
              .exportAsHtml(fosTable, new File("src/test/resources/tmp/lobs"));
        fosTable.close();
        archive.close();

        // then
        List<String> generatedHtml = Files.readAllLines(fileTable.toPath(), StandardCharsets.UTF_8);
        List<String> expectedHtml = Files.readAllLines(Paths.get("src/test/resources/export/TSIMPLE.html"), StandardCharsets.UTF_8);

        assertIterableEquals(expectedHtml, generatedHtml, "Generated HTML should match expected content");
    }

}