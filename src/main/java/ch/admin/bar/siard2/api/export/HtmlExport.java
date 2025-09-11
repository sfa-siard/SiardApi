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
    
    private final HtmlExportConfig config;

    public HtmlExport(MetaTable metaTable) throws IOException {
        this(metaTable, HtmlExportConfig.defaultConfig());
    }
    
    public HtmlExport(MetaTable metaTable, HtmlExportConfig config) throws IOException {
        this.metaTable = metaTable;
        this.metaTableFacade = new MetaTableFacade(metaTable);
        this.dispenser = new TableRecordDispenserImpl(metaTable.getTable());
        this.config = config;
    }

    public void write(OutputStream outputStream, File folderLobs) {
        try (
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, config.charset())
        ) {
            write(outputStreamWriter, folderLobs);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void write(OutputStreamWriter oswr, File folderLobs) throws IOException {
        StringBuilder sb = new StringBuilder();
        
        sb.append(HtmlTemplate.documentStart(metaTable.getName(), metaTable.getName(), metaTable.getDescription()));
        sb.append(HtmlTemplate.rowStart());
        sb.append(getColumnHeaders());
        sb.append(HtmlTemplate.rowEnd());
        sb.append(getRows(folderLobs));
        sb.append(HtmlTemplate.documentEnd());
        
        oswr.write(sb.toString());
        oswr.flush();
    }

    private String getColumnHeaders() {
        return metaTableFacade.getMetaColums()
                              .stream()
                              .map(MetaColumn::getName)
                              .map(HtmlTemplate::tableHeader)
                              .collect(Collectors.joining());
    }

    private String getRows(File folderLobs) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (long rows = 0; rows < metaTable.getRows(); rows++) {
            sb.append(HtmlTemplate.rowStart());
            sb.append(getColumns(folderLobs));
            sb.append(HtmlTemplate.rowEnd());
        }
        return sb.toString();
    }

    private String getColumns(File folderLobs) throws IOException {
        StringBuilder sb = new StringBuilder();

        new TableRecordFacade(this.dispenser.get()).getCells()
                                                   .forEach(cell -> {
                                                       try {
                                                           String cellContent = getValue(cell, folderLobs);
                                                           // Apply max content length if configured
                                                           if (config.maxCellContentLength() > 0 && cellContent.length() > config.maxCellContentLength()) {
                                                               cellContent = cellContent.substring(0, config.maxCellContentLength()) + "...";
                                                           }
                                                           sb.append(HtmlTemplate.tableCell(cellContent));
                                                       } catch (IOException e) {
                                                           throw new RuntimeException(e);
                                                       }
                                                   });

        return sb.toString();
    }

    private String getLinkToLob(Value value, File folderLobs, String fileName)
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
        sb.append(HtmlTemplate.link(fileName, fileName));

        return sb.toString();
    }

    private String getUdtValue(Value value, File folderLobs)
            throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(HtmlTemplate.definitionListStart());
        MetaValue mv = value.getMetaValue();
        for (int i = 0; i < value.getAttributes(); i++) {
            MetaField mf = mv.getMetaField(i);
            sb.append(HtmlTemplate.definitionTerm(mf.getName()));
            String attributeValue = getValue(value.getAttribute(i), folderLobs);
            sb.append(HtmlTemplate.definitionDescription(attributeValue));
        }
        sb.append(HtmlTemplate.definitionListEnd());
        return sb.toString();
    }

    /**
     * write the array value as an ordered list.
     *
     * @param value      array value to be written.
     * @param folderLobs root folder for internal LOBs in this table.
     * @throws IOException if an I/O error occurred.
     */
    private String getArrayValue(Value value, File folderLobs)
            throws IOException {

        StringBuilder sb = new StringBuilder();
        sb.append(HtmlTemplate.orderedListStart());
        for (int iElement = 0; iElement < value.getElements(); iElement++) {
            String elementValue = getValue(value.getElement(iElement), folderLobs);
            sb.append(HtmlTemplate.listItem(elementValue));
        }
        sb.append(HtmlTemplate.orderedListEnd());
        return sb.toString();
    }

    private String getValue(Value value, File folderLobs) throws IOException {
        StringBuilder sb = new StringBuilder();
        if (value.isNull()) return sb.toString();

        // if it is a Lob with a file name then create a link
        String fileName = value.getFilename();
        if (fileName != null) {
            sb.append(getLinkToLob(value, folderLobs, fileName));
            return sb.toString();
        }

        MetaValue metaValue = value.getMetaValue();
        MetaType metaType = metaValue.getMetaType();
        if ((metaType != null) && (metaType.getCategoryType() == CategoryType.UDT)) {
            sb.append(getUdtValue(value, folderLobs));
            return sb.toString();
        }

        if (metaValue.getCardinality() > 0) {
            sb.append(getArrayValue(value, folderLobs));
            return sb.toString();
        }
        sb.append(escapeHtml4(value.convert()));
        return sb.toString();
    }
}
