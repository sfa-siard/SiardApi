package ch.admin.bar.siard2.api.export;

import ch.admin.bar.siard2.api.*;
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

    public void write(OutputStream os, File folderLobs, MetaTable metaTable) throws IOException {

        TableRecordDispenserImpl rd = new TableRecordDispenserImpl(metaTable.getTable());
        OutputStreamWriter oswr = new OutputStreamWriter(os, StandardCharsets.UTF_8);
        oswr.write("<!DOCTYPE html>\r\n");
        oswr.write("<html lang=\"en\">\r\n");
        oswr.write("  <head>\r\n");
        oswr.write("    <title>" + SU.toHtml(metaTable.getName()) + "</title>\r\n");
        oswr.write("    <meta charset=\"utf-8\" />\r\n");
        oswr.write("  </head>\r\n");
        oswr.write("  <body>\r\n");
        oswr.write("    <p>" + metaTable.getName() + "</p>\r\n");
        oswr.write("    <p>" + metaTable.getDescription() + "</p>\r\n");
        oswr.write("    <table>\r\n");
        oswr.write("      <tr>\r\n");
        for (int iColumn = 0; iColumn < metaTable.getMetaColumns(); iColumn++) {
            oswr.write("        <th>");
            oswr.write(SU.toHtml(metaTable.getMetaColumn(iColumn)
                    .getName()));
            oswr.write("</th>\r\n");
        }
        oswr.write("      </tr>\r\n");

        for (long lRow = 0; lRow < metaTable.getRows(); lRow++) {
            oswr.write("      <tr>\r\n");
            TableRecord tableRecord = rd.get();
            for (int iColumn = 0; iColumn < tableRecord.getCells(); iColumn++) {
                oswr.write("        <td>");
                Cell cell = tableRecord.getCell(iColumn);
                writeValue(oswr, cell, folderLobs);
                oswr.write("</td>\r\n");
            }
            oswr.write("      </tr>\r\n");
        }
        rd.close();
        oswr.write("    </table>\r\n");
        oswr.write("  </body>\r\n");
        oswr.write("</html>\r\n");
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
    private void writeLinkToLob(Writer wr, Value value, File folderLobs, String sFilename)
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
        wr.write("<a href=\"" + sFilename + "\">" + sFilename + "</a>");
    }

    /**
     * write the UDT value as a definition list.
     *
     * @param wr         writer.
     * @param value      UDT value to be written.
     * @param folderLobs root folder for internal LOBs in this table.
     * @throws IOException if an I/O error occurred.
     */
    private void writeUdtValue(Writer wr, Value value, File folderLobs)
            throws IOException {
        wr.write("<dl>\r\n");
        MetaValue mv = value.getMetaValue();
        for (int iAttribute = 0; iAttribute < value.getAttributes(); iAttribute++) {
            MetaField mf = mv.getMetaField(iAttribute);
            wr.write("  <dt>");
            wr.write(SU.toHtml(mf.getName()));
            wr.write("</dt>\r\n");
            wr.write("  <dd>");
            writeValue(wr, value.getAttribute(iAttribute), folderLobs);
            wr.write("</dd>\r\n");
        }
        wr.write("</dl>\r\n");
    }

    /**
     * write the array value as an ordered list.
     *
     * @param wr         writer.
     * @param value      array value to be written.
     * @param folderLobs root folder for internal LOBs in this table.
     * @throws IOException if an I/O error occurred.
     */
    private void writeArrayValue(Writer wr, Value value, File folderLobs)
            throws IOException {
        wr.write("<ol>\r\n");
        for (int iElement = 0; iElement < value.getElements(); iElement++) {
            wr.write("  <li>");
            writeValue(wr, value.getElement(iElement), folderLobs);
            wr.write("</li>\r\n");
        }
        wr.write("</ol>\r\n");
    }

    /**
     * write the value as HTML to the table cell.
     *
     * @param wr         writer.
     * @param value      value to be written.
     * @param folderLobs root folder for internal LOBs in this table.
     * @throws IOException if an I/O error occurred.
     */
    private void writeValue(Writer wr, Value value, File folderLobs)
            throws IOException {
        DU du = DU.getInstance(Locale.getDefault()
                                     .getLanguage(), (new SimpleDateFormat()).toPattern());
        if (!value.isNull()) {
            // if it is a Lob with a file name then create a link
            String sFilename = value.getFilename();
            if (sFilename != null)
                writeLinkToLob(wr, value, folderLobs, sFilename);
            else {
                MetaValue mv = value.getMetaValue();
                MetaType mt = mv.getMetaType();
                if ((mt != null) && (mt.getCategoryType() == CategoryType.UDT))
                    writeUdtValue(wr, value, folderLobs);
                else if (mv.getCardinality() > 0)
                    writeArrayValue(wr, value, folderLobs);
                else {
                    String sText = null;
                    int iType = mv.getPreType();
                    switch (iType) {
                        case Types.CHAR:
                        case Types.VARCHAR:
                        case Types.NCHAR:
                        case Types.NVARCHAR:
                        case Types.CLOB:
                        case Types.NCLOB:
                        case Types.SQLXML:
                        case Types.DATALINK:
                            sText = value.getString();
                            break;
                        case Types.BINARY:
                        case Types.VARBINARY:
                        case Types.BLOB:
                            sText = "0x" + BU.toHex(value.getBytes());
                            break;
                        case Types.NUMERIC:
                        case Types.DECIMAL:
                            sText = value.getBigDecimal()
                                         .toPlainString();
                            break;
                        case Types.SMALLINT:
                            sText = value.getInt()
                                         .toString();
                            break;
                        case Types.INTEGER:
                            sText = value.getLong()
                                         .toString();
                            break;
                        case Types.BIGINT:
                            sText = value.getBigInteger()
                                         .toString();
                            break;
                        case Types.FLOAT:
                        case Types.DOUBLE:
                            sText = value.getDouble()
                                         .toString();
                            break;
                        case Types.REAL:
                            sText = value.getFloat()
                                         .toString();
                            break;
                        case Types.BOOLEAN:
                            sText = value.getBoolean()
                                         .toString();
                            break;
                        case Types.DATE:
                            sText = du.fromSqlDate(value.getDate());
                            break;
                        case Types.TIME:
                            sText = du.fromSqlTime(value.getTime());
                            break;
                        case Types.TIMESTAMP:
                            sText = du.fromSqlTimestamp(value.getTimestamp());
                            break;
                        case Types.OTHER:
                            sText = SqlLiterals.formatIntervalLiteral(Interval.fromDuration(value.getDuration()));
                            break;
                    }
                    wr.write(SU.toHtml(sText));
                }
            }
        }
    }
}
