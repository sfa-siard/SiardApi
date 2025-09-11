package ch.admin.bar.siard2.api.export;

import ch.admin.bar.siard2.api.MetaColumn;
import ch.admin.bar.siard2.api.MetaTable;
import ch.admin.bar.siard2.api.facade.MetaTableFacade;
import ch.admin.bar.siard2.api.facade.TableRecordFacade;
import ch.admin.bar.siard2.api.primary.TableRecordDispenserImpl;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.stream.Collectors;

public class HtmlExport {

    private static final int BUFFER_SIZE = 8192;

    private final MetaTableFacade metaTableFacade;

    private final MetaTable metaTable;

    private final TableRecordDispenserImpl dispenser;

    private final HtmlExportConfig config;

    private final ValueRendererRegistry rendererRegistry;

    public HtmlExport(MetaTable metaTable) throws IOException {
        this(metaTable, HtmlExportConfig.defaultConfig());
    }

    public HtmlExport(MetaTable metaTable, HtmlExportConfig config) throws IOException {
        this(metaTable, config, ValueRendererRegistry.createDefault());
    }

    public HtmlExport(MetaTable metaTable, HtmlExportConfig config, ValueRendererRegistry rendererRegistry) throws IOException {
        this.metaTable = metaTable;
        this.metaTableFacade = new MetaTableFacade(metaTable);
        this.dispenser = new TableRecordDispenserImpl(metaTable.getTable());
        this.config = config;
        this.rendererRegistry = rendererRegistry;
    }

    public void write(OutputStream outputStream, File folderLobs) {
        try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, config.charset())) {
            LobFileHandler lobHandler = new LobFileHandler(folderLobs);
            ValueRenderingContext context = new ValueRenderingContext(lobHandler, config, rendererRegistry);
            write(outputStreamWriter, context);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void write(OutputStreamWriter oswr, ValueRenderingContext context) throws IOException {
        StringBuilder sb = new StringBuilder();

        sb.append(HtmlTemplate.documentStart(metaTable.getName(), metaTable.getName(), metaTable.getDescription()));
        sb.append(HtmlTemplate.rowStart());
        sb.append(getColumnHeaders());
        sb.append(HtmlTemplate.rowEnd());
        sb.append(getRows(context));
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

    private String getRows(ValueRenderingContext context) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (long rows = 0; rows < metaTable.getRows(); rows++) {
            sb.append(HtmlTemplate.rowStart());
            sb.append(getColumns(context));
            sb.append(HtmlTemplate.rowEnd());
        }
        return sb.toString();
    }

    private String getColumns(ValueRenderingContext context) throws IOException {
        StringBuilder sb = new StringBuilder();

        new TableRecordFacade(this.dispenser.get()).getCells()
                                                   .forEach(cell -> {
                                                       try {
                                                           sb.append(HtmlTemplate.tableCell(context.rendererRegistry()
                                                                                                   .render(cell, context)));
                                                       } catch (IOException e) {
                                                           throw new RuntimeException(e);
                                                       }
                                                   });

        return sb.toString();
    }

}
