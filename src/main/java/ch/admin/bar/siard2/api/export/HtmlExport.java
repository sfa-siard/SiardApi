package ch.admin.bar.siard2.api.export;

import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.api.facade.MetaTableFacade;
import ch.admin.bar.siard2.api.facade.TableRecordFacade;
import ch.admin.bar.siard2.api.generated.CategoryType;
import ch.admin.bar.siard2.api.primary.TableRecordDispenserImpl;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Types;

import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;

public class HtmlExport {

    private static final int BUFFER_SIZE = 8192;

    private final MetaTableFacade metaTableFacade;

    private final MetaTable metaTable;

    public HtmlExport(MetaTable metaTable) {
        this.metaTable = metaTable;
        this.metaTableFacade = new MetaTableFacade(metaTable);
    }

    public void write(OutputStream outputStream, File folderLobs) throws IOException {
        TableRecordDispenserImpl dispenser = new TableRecordDispenserImpl(metaTable.getTable());
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        write(folderLobs, outputStreamWriter, dispenser);
    }

    private void write(File folderLobs, OutputStreamWriter oswr, TableRecordDispenserImpl dispenser) throws IOException {
        StringBuilder content = new StringBuilder();
        content.append("<!DOCTYPE html>\r\n")
               .append("<html lang=\"en\">\r\n")
               .append("  <head>\r\n")
               .append("    <title>")
               .append(escapeHtml4(metaTable.getName()))
               .append("</title>\r\n")
               .append("    <meta charset=\"utf-8\" />\r\n")
               .append("  </head>\r\n")
               .append("  <body>\r\n")
               .append("    <p>")
               .append(metaTable.getName())
               .append("</p>\r\n")
               .append("    <p>")
               .append(metaTable.getDescription())
               .append("</p>\r\n")
               .append("    <table>\r\n")
               .append("      <tr>\r\n");

        addColumns(content);
        content.append("      </tr>\r\n");
        addRows(folderLobs, dispenser, content);
        content.append("    </table>\r\n");
        content.append("  </body>\r\n");
        content.append("</html>\r\n");
        oswr.write(content.toString());
        oswr.flush();
    }

    private void addColumns(StringBuilder content) {
        metaTableFacade.getMetaColums()
                       .forEach(col -> content.append("        <th>")
                                              .append(escapeHtml4(col.getName()))
                                              .append("</th>\r\n"));
    }

    private void addRows(File folderLobs, TableRecordDispenserImpl dispenser, StringBuilder content) throws IOException {
        for (long rows = 0; rows < metaTable.getRows(); rows++) {
            content.append("      <tr>\r\n");
            addColumns(folderLobs, dispenser, content);
            content.append("      </tr>\r\n");
        }
    }

    private void addColumns(File folderLobs, TableRecordDispenserImpl dispenser, StringBuilder content) throws IOException {
        new TableRecordFacade(dispenser.get()).getCells()
                                              .forEach(cell -> {
                                                  try {
                                                      content.append("        <td>");
                                                      writeValue(content, cell, folderLobs);
                                                      content.append("</td>\r\n");
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
        content.append("<dl>\r\n");
        MetaValue mv = value.getMetaValue();
        for (int i = 0; i < value.getAttributes(); i++) {
            MetaField mf = mv.getMetaField(i);
            content.append("  <dt>");
            content.append(escapeHtml4(mf.getName()));
            content.append("</dt>\r\n");
            content.append("  <dd>");
            writeValue(content, value.getAttribute(i), folderLobs);
            content.append("</dd>\r\n");
        }
        content.append("</dl>\r\n");
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
        content.append("<ol>\r\n");
        for (int iElement = 0; iElement < value.getElements(); iElement++) {
            content.append("  <li>");
            writeValue(content, value.getElement(iElement), folderLobs);
            content.append("</li>\r\n");
        }
        content.append("</ol>\r\n");
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
