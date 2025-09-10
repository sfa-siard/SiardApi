package ch.admin.bar.siard2.api.export;

import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.api.facade.MetaTableFacade;
import ch.admin.bar.siard2.api.facade.TableRecordFacade;
import ch.admin.bar.siard2.api.generated.CategoryType;
import ch.admin.bar.siard2.api.primary.TableRecordDispenserImpl;
import org.apache.commons.text.StringEscapeUtils;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Types;
import java.util.stream.Collectors;

import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;

public class HtmlExport {

    private static final int BUFFER_SIZE = 8192;

    private final MetaTableFacade metaTableFacade;

    private final MetaTable metaTable;

    private final TableRecordDispenserImpl dispenser;

    public HtmlExport(MetaTable metaTable) throws IOException {
        this.metaTable = metaTable;
        this.metaTableFacade = new MetaTableFacade(metaTable);
        this.dispenser = new TableRecordDispenserImpl(metaTable.getTable());
    }

    public void write(OutputStream outputStream, File folderLobs) {
        try (
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)
        ) {
            write(folderLobs, outputStreamWriter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void write(File folderLobs, OutputStreamWriter oswr) throws IOException {
        StringBuilder content = new StringBuilder();
        content.append("<!DOCTYPE html>\n")
               .append("<html lang=\"en\">\n")
               .append("  <head>\n")
               .append("    <title>")
               .append(escapeHtml4(metaTable.getName()))
               .append("</title>\n")
               .append("    <meta charset=\"utf-8\" />\n")
               .append("  </head>\n")
               .append("  <body>\n")
               .append("    <p>")
               .append(metaTable.getName())
               .append("</p>\n")
               .append("    <p>")
               .append(metaTable.getDescription())
               .append("</p>\n")
               .append("    <table>\n")
               .append("      <tr>\n");

        content.append(getColumnHeaders());
        content.append("      </tr>\n");
        addRows(folderLobs, content);
        content.append("    </table>\n");
        content.append("  </body>\n");
        content.append("</html>\n");
        oswr.write(content.toString());
        oswr.flush();
    }

    private String getColumnHeaders() {
        return metaTableFacade.getMetaColums().stream()
                .map(MetaColumn::getName)
                .map(StringEscapeUtils::escapeHtml4)
                .map(name -> "        <th>" + name + "</th>\n")
                .collect(Collectors.joining());

    }

    private void addRows(File folderLobs, StringBuilder content) throws IOException {
        for (long rows = 0; rows < metaTable.getRows(); rows++) {
            content.append("      <tr>\n");
            addColumns(folderLobs, content);
            content.append("      </tr>\n");
        }
    }

    private void addColumns(File folderLobs, StringBuilder content) throws IOException {


        new TableRecordFacade(this.dispenser.get()).getCells()
                                      .forEach(cell -> {
                                          try {
                                              content.append("        <td>");
                                              writeValue(content, cell, folderLobs);
                                              content.append("</td>\n");
                                          } catch (IOException e) {
                                              throw new RuntimeException(e);
                                          }
                                      });
    }

    private void writeLinkToLob(StringBuilder content, Value value, File folderLobs, String fileName)
            throws IOException {
        URI absoluteLobFolder = value.getMetaValue()
                                     .getAbsoluteLobFolder();
        if (absoluteLobFolder != null) {
            fileName = absoluteLobFolder.resolve(fileName)
                                        .toURL()
                                        .toString();
            /* leave external LOB file where it is */
        } else if (folderLobs != null) {

            File fileLob = new File("/" + folderLobs.getAbsolutePath()
                                                    .replace('\\', '/') + "/" + fileName);
            fileLob.getParentFile()
                   .mkdirs();
            /* copy internal LOB file to lobFolder */
            int predefinedType = value.getMetaValue()
                                      .getPreType();
            if ((predefinedType == Types.BINARY) ||
                    (predefinedType == Types.VARBINARY) ||
                    (predefinedType == Types.BLOB) ||
                    (predefinedType == Types.DATALINK)) {
                InputStream inputStream = value.getInputStream();
                if (inputStream != null) {
                    FileOutputStream lobOutputStream = new FileOutputStream("/" + folderLobs.getAbsolutePath()
                                                                                            .replace('\\', '/') + "/" + fileName);
                    byte[] buf = new byte[BUFFER_SIZE];
                    for (int i = inputStream.read(buf); i != -1; i = inputStream.read(buf)) {
                        lobOutputStream.write(buf, 0, i);
                    }
                    lobOutputStream.close();
                    inputStream.close();
                }
            } else {
                Reader rdr = value.getReader();
                if (rdr != null) {
                    Writer fwLob = new FileWriter(fileLob);
                    char[] cbuf = new char[BUFFER_SIZE];
                    for (int iRead = rdr.read(cbuf); iRead != -1; iRead = rdr.read(cbuf))
                        fwLob.write(cbuf, 0, iRead);
                    fwLob.close();
                    rdr.close();
                }
            }
        }
        /* write a link to the LOB file to HTML */
        content.append("<a href=\"")
               .append(fileName)
               .append("\">")
               .append(fileName)
               .append("</a>");
    }

    private void writeUdtValue(StringBuilder content, Value value, File folderLobs)
            throws IOException {
        content.append("<dl>\n");
        MetaValue mv = value.getMetaValue();
        for (int i = 0; i < value.getAttributes(); i++) {
            MetaField mf = mv.getMetaField(i);
            content.append("  <dt>");
            content.append(escapeHtml4(mf.getName()));
            content.append("</dt>\n");
            content.append("  <dd>");
            writeValue(content, value.getAttribute(i), folderLobs);
            content.append("</dd>\n");
        }
        content.append("</dl>\n");
    }

    /**
     * write the array value as an ordered list.
     *
     * @param content         content.
     * @param value      array value to be written.
     * @param folderLobs root folder for internal LOBs in this table.
     * @throws IOException if an I/O error occurred.
     */
    private void writeArrayValue(StringBuilder content, Value value, File folderLobs)
            throws IOException {
        content.append("<ol>\n");
        for (int iElement = 0; iElement < value.getElements(); iElement++) {
            content.append("  <li>");
            writeValue(content, value.getElement(iElement), folderLobs);
            content.append("</li>\n");
        }
        content.append("</ol>\n");
    }

    private void writeValue(StringBuilder content, Value value, File folderLobs) throws IOException {

        if (value.isNull()) return;

        // if it is a Lob with a file name then create a link
        String fileName = value.getFilename();
        if (fileName != null) {
            writeLinkToLob(content, value, folderLobs, fileName);
            return;
        }

        MetaValue metaValue = value.getMetaValue();
        MetaType metaType = metaValue.getMetaType();
        if ((metaType != null) && (metaType.getCategoryType() == CategoryType.UDT)) {
            writeUdtValue(content, value, folderLobs);
            return;
        }

        if (metaValue.getCardinality() > 0) {
            writeArrayValue(content, value, folderLobs);
            return;
        }
        content.append(escapeHtml4(value.convert()));
    }
}
