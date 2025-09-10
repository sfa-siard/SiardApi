package ch.admin.bar.siard2.api.export;

import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.api.facade.MetaTableFacade;
import ch.admin.bar.siard2.api.facade.TableRecordFacade;
import ch.admin.bar.siard2.api.generated.CategoryType;
import ch.admin.bar.siard2.api.primary.TableRecordDispenserImpl;
import ch.enterag.sqlparser.Interval;
import ch.enterag.sqlparser.SqlLiterals;
import ch.enterag.utils.BU;
import ch.enterag.utils.DU;
import ch.enterag.utils.SU;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class HtmlExport {

    private static final int iBUFFER_SIZE = 8192;
    private final DU dateUtils = DU.getInstance(Locale.getDefault()
                                                      .getLanguage(), (new SimpleDateFormat()).toPattern());
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

    private void write(File folderLobs, OutputStreamWriter oswr, TableRecordDispenserImpl rd) throws IOException {
        StringBuilder content = new StringBuilder();
        content.append("<!DOCTYPE html>\r\n");
        content.append("<html lang=\"en\">\r\n");
        content.append("  <head>\r\n");
        content.append("    <title>" + SU.toHtml(metaTable.getName()) + "</title>\r\n");
        content.append("    <meta charset=\"utf-8\" />\r\n");
        content.append("  </head>\r\n");
        content.append("  <body>\r\n");
        content.append("    <p>" + metaTable.getName() + "</p>\r\n");
        content.append("    <p>" + metaTable.getDescription() + "</p>\r\n");
        content.append("    <table>\r\n");
        content.append("      <tr>\r\n");

        metaTableFacade.getMetaColums()
                       .stream()
                       .forEach(col -> {
                           content.append("        <th>");
                           content.append(SU.toHtml(col.getName()));
                           content.append("</th>\r\n");
                       });

        content.append("      </tr>\r\n");

        for (long lRow = 0; lRow < metaTable.getRows(); lRow++) {
            content.append("      <tr>\r\n");

            new TableRecordFacade(rd.get()).getCells()
                                           .forEach(cell -> {
                                               try {
                                                   content.append("        <td>");
                                                   writeValue(content, cell, folderLobs);
                                                   content.append("</td>\r\n");
                                               } catch (IOException e) {
                                                   throw new RuntimeException(e);
                                               }
                                           });

            content.append("      </tr>\r\n");
        }
        rd.close();
        content.append("    </table>\r\n");
        content.append("  </body>\r\n");
        content.append("</html>\r\n");
        oswr.write(content.toString());
        oswr.flush();
    }

    /**
     * write the value as HTML to the table cell.
     *
     * @param wr        writer.
     * @param value     value to be written.
     * @param sFilename file name for LOB.
     * @throws IOException if an I/O error occurred.
     */
    private void writeLinkToLob(StringBuilder wr, Value value, File folderLobs, String sFilename)
            throws IOException {
        URI uriAbsoluteFolder = value.getMetaValue()
                                     .getAbsoluteLobFolder();
        if (uriAbsoluteFolder != null) {
            URI uriExternal = uriAbsoluteFolder.resolve(sFilename);
            sFilename = uriExternal.toURL()
                                   .toString();
            /* leave external LOB file where it is */
        } else if (folderLobs != null) {

            sFilename = "/" + folderLobs.getAbsolutePath()
                                        .replace('\\', '/') + "/" + sFilename;
            File fileLob = new File(sFilename);
            fileLob.getParentFile()
                   .mkdirs();
            /* copy internal LOB file to lobFolder */
            int iType = value.getMetaValue()
                             .getPreType();
            if ((iType == Types.BINARY) ||
                    (iType == Types.VARBINARY) ||
                    (iType == Types.BLOB) ||
                    (iType == Types.DATALINK)) {
                InputStream is = value.getInputStream();
                if (is != null) {
                    FileOutputStream fosLob = new FileOutputStream(sFilename);
                    byte[] buf = new byte[iBUFFER_SIZE];
                    for (int iRead = is.read(buf); iRead != -1; iRead = is.read(buf))
                        fosLob.write(buf, 0, iRead);
                    fosLob.close();
                    is.close();
                }
            } else {
                Reader rdr = value.getReader();
                if (rdr != null) {
                    Writer fwLob = new FileWriter(fileLob);
                    char[] cbuf = new char[iBUFFER_SIZE];
                    for (int iRead = rdr.read(cbuf); iRead != -1; iRead = rdr.read(cbuf))
                        fwLob.write(cbuf, 0, iRead);
                    fwLob.close();
                    rdr.close();
                }
            }
        }
        /* write a link to the LOB file to HTML */
        wr.append("<a href=\"" + sFilename + "\">" + sFilename + "</a>");
    }

    /**
     * write the UDT value as a definition list.
     *
     * @param content         writer.
     * @param value      UDT value to be written.
     * @param folderLobs root folder for internal LOBs in this table.
     * @throws IOException if an I/O error occurred.
     */
    private void writeUdtValue(StringBuilder content, Value value, File folderLobs)
            throws IOException {
        content.append("<dl>\r\n");
        MetaValue mv = value.getMetaValue();
        for (int iAttribute = 0; iAttribute < value.getAttributes(); iAttribute++) {
            MetaField mf = mv.getMetaField(iAttribute);
            content.append("  <dt>");
            content.append(SU.toHtml(mf.getName()));
            content.append("</dt>\r\n");
            content.append("  <dd>");
            writeValue(content, value.getAttribute(iAttribute), folderLobs);
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

        content.append(SU.toHtml(convert(value, metaValue)));
    }


    private String convert(Value value, MetaValue metaValue) throws IOException {
        return switch (metaValue.getPreType()) {
            case Types.CHAR, Types.VARCHAR, Types.NCHAR, Types.NVARCHAR, Types.CLOB, Types.NCLOB, Types.SQLXML,
                 Types.DATALINK -> value.getString();
            case Types.BINARY, Types.VARBINARY, Types.BLOB -> "0x" + BU.toHex(value.getBytes());
            case Types.NUMERIC, Types.DECIMAL -> value.getBigDecimal()
                                                      .toPlainString();
            case Types.SMALLINT -> value.getInt()
                                        .toString();
            case Types.INTEGER -> value.getLong()
                                       .toString();
            case Types.BIGINT -> value.getBigInteger()
                                      .toString();
            case Types.FLOAT, Types.DOUBLE -> value.getDouble()
                                                   .toString();
            case Types.REAL -> value.getFloat()
                                    .toString();
            case Types.BOOLEAN -> value.getBoolean()
                                       .toString();
            case Types.DATE -> dateUtils.fromSqlDate(value.getDate());
            case Types.TIME -> dateUtils.fromSqlTime(value.getTime());
            case Types.TIMESTAMP -> dateUtils.fromSqlTimestamp(value.getTimestamp());
            case Types.OTHER -> SqlLiterals.formatIntervalLiteral(Interval.fromDuration(value.getDuration()));
            default -> "";
        };
    }
}
