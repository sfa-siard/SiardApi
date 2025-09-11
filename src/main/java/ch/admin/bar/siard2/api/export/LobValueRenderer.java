package ch.admin.bar.siard2.api.export;

import ch.admin.bar.siard2.api.Value;

import java.io.IOException;

/**
 * Renderer for LOB (Large Object) values that have associated file names.
 * Handles both internal and external LOBs by delegating to the LobFileHandler.
 */
class LobValueRenderer implements ValueRenderer {
    
    @Override
    public boolean canRender(Value value) throws IOException {
        return value.getFilename() != null;
    }
    
    @Override
    public String render(Value value, ValueRenderingContext context) throws IOException {
        String fileName = value.getFilename();
        String processedFileName = context.lobFileHandler().processLobFile(value, fileName);
        return HtmlTemplate.link(processedFileName, fileName);
    }
    
    @Override
    public int getPriority() {
        return 100; // High priority - LOBs should be handled before other checks
    }
}
