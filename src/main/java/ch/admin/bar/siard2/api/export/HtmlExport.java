package ch.admin.bar.siard2.api.export;

import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.api.facade.MetaTableFacade;
import ch.admin.bar.siard2.api.facade.TableRecordFacade;
import ch.admin.bar.siard2.api.generated.CategoryType;
import ch.admin.bar.siard2.api.primary.TableRecordDispenserImpl;
import org.apache.commons.text.StringEscapeUtils;

import java.io.*;
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
            write(outputStreamWriter, new LobFileHandler(folderLobs));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void write(OutputStreamWriter oswr, LobFileHandler lobHandler) throws IOException {
        StringBuilder sb = new StringBuilder();
        
        sb.append(HtmlTemplate.documentStart(metaTable.getName(), metaTable.getName(), metaTable.getDescription()));
        sb.append(HtmlTemplate.rowStart());
        sb.append(getColumnHeaders());
        sb.append(HtmlTemplate.rowEnd());
        sb.append(getRows(lobHandler));
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

    private String getRows(LobFileHandler lobHandler) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (long rows = 0; rows < metaTable.getRows(); rows++) {
            sb.append(HtmlTemplate.rowStart());
            sb.append(getColumns(lobHandler));
            sb.append(HtmlTemplate.rowEnd());
        }
        return sb.toString();
    }

    private String getColumns(LobFileHandler lobHandler) throws IOException {
        StringBuilder sb = new StringBuilder();

        new TableRecordFacade(this.dispenser.get()).getCells()
                                                   .forEach(cell -> {
                                                       try {
                                                           String cellContent = getValue(cell, lobHandler);
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

    private String getLinkToLob(Value value, LobFileHandler lobHandler, String fileName)
            throws IOException {
        String processedFileName = lobHandler.processLobFile(value, fileName);
        return HtmlTemplate.link(processedFileName, fileName);
    }

    private String getUdtValue(Value value, LobFileHandler lobHandler)
            throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(HtmlTemplate.definitionListStart());
        MetaValue mv = value.getMetaValue();
        for (int i = 0; i < value.getAttributes(); i++) {
            MetaField mf = mv.getMetaField(i);
            sb.append(HtmlTemplate.definitionTerm(mf.getName()));
            String attributeValue = getValue(value.getAttribute(i), lobHandler);
            sb.append(HtmlTemplate.definitionDescription(attributeValue));
        }
        sb.append(HtmlTemplate.definitionListEnd());
        return sb.toString();
    }

    private String getArrayValue(Value value, LobFileHandler lobHandler)
            throws IOException {

        StringBuilder sb = new StringBuilder();
        sb.append(HtmlTemplate.orderedListStart());
        for (int iElement = 0; iElement < value.getElements(); iElement++) {
            String elementValue = getValue(value.getElement(iElement), lobHandler);
            sb.append(HtmlTemplate.listItem(elementValue));
        }
        sb.append(HtmlTemplate.orderedListEnd());
        return sb.toString();
    }

    private String getValue(Value value, LobFileHandler lobHandler) throws IOException {
        StringBuilder sb = new StringBuilder();
        if (value.isNull()) return sb.toString();

        // if it is a Lob with a file name then create a link
        String fileName = value.getFilename();
        if (fileName != null) {
            sb.append(getLinkToLob(value, lobHandler, fileName));
            return sb.toString();
        }

        MetaValue metaValue = value.getMetaValue();
        MetaType metaType = metaValue.getMetaType();
        if ((metaType != null) && (metaType.getCategoryType() == CategoryType.UDT)) {
            sb.append(getUdtValue(value, lobHandler));
            return sb.toString();
        }

        if (metaValue.getCardinality() > 0) {
            sb.append(getArrayValue(value, lobHandler));
            return sb.toString();
        }
        sb.append(escapeHtml4(value.convert()));
        return sb.toString();
    }
}
