package ch.admin.bar.siard2.api.export;

/**
 * Context object that provides dependencies and configuration for value rendering.
 * This allows renderers to access shared services without tight coupling.
 */
public record ValueRenderingContext(
    LobFileHandler lobFileHandler,
    HtmlExportConfig config,
    ValueRendererRegistry rendererRegistry
) {
    
    /**
     * Create a new rendering context.
     *
     * @param lobFileHandler the LOB file handler for processing file-based values
     * @param config the export configuration
     * @param rendererRegistry the registry for recursive rendering
     */
    public ValueRenderingContext {
        // Validation could be added here if needed
    }
}
