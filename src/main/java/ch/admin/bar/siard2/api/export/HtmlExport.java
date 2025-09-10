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
                                              content.append(writeValue(cell, folderLobs));
                                              content.append("</td>\n");
                                          } catch (IOException e) {
                                              throw new RuntimeException(e);
                                          }
                                      });
    }

    private String writeLinkToLob(Value value, File folderLobs, String fileName)
            throws IOException {
        StringBuilder sb = new StringBuilder();
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
        sb.append("<a href=\"")
               .append(fileName)
               .append("\">")
               .append(fileName)
               .append("</a>");

        return sb.toString();
    }

    private String writeUdtValue( Value value, File folderLobs)
            throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("<dl>\n");
        MetaValue mv = value.getMetaValue();
        for (int i = 0; i < value.getAttributes(); i++) {
            MetaField mf = mv.getMetaField(i);
            sb.append("  <dt>");
            sb.append(escapeHtml4(mf.getName()));
            sb.append("</dt>\n");
            sb.append("  <dd>");
            sb.append(writeValue( value.getAttribute(i), folderLobs));
            sb.append("</dd>\n");
        }
        sb.append("</dl>\n");
        return sb.toString();
    }

    /**
     * write the array value as an ordered list.
     *
     * @param value      array value to be written.
     * @param folderLobs root folder for internal LOBs in this table.
     * @throws IOException if an I/O error occurred.
     */
    private String writeArrayValue(Value value, File folderLobs)
            throws IOException {

        StringBuilder sb = new StringBuilder();
        sb.append("<ol>\n");
        for (int iElement = 0; iElement < value.getElements(); iElement++) {
            sb.append("  <li>");
            sb.append(writeValue(value.getElement(iElement), folderLobs));
            sb.append("</li>\n");
        }
        sb.append("</ol>\n");
        return sb.toString();
    }

    private String writeValue(Value value, File folderLobs) throws IOException {
        StringBuilder sb = new StringBuilder();
        if (value.isNull()) return sb.toString();

        // if it is a Lob with a file name then create a link
        String fileName = value.getFilename();
        if (fileName != null) {
            sb.append(writeLinkToLob(value, folderLobs, fileName));
            return sb.toString();
        }

        MetaValue metaValue = value.getMetaValue();
        MetaType metaType = metaValue.getMetaType();
        if ((metaType != null) && (metaType.getCategoryType() == CategoryType.UDT)) {
            sb.append(writeUdtValue(value, folderLobs));
            return sb.toString();
        }

        if (metaValue.getCardinality() > 0) {
            sb.append(writeArrayValue(value, folderLobs));
            return sb.toString();
        }
        sb.append(escapeHtml4(value.convert()));
        return sb.toString();
    }
}
