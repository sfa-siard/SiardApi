package ch.admin.bar.siard2.api.export;

import ch.admin.bar.siard2.api.Value;
import lombok.SneakyThrows;

/**
 * Renderer for primitive values (strings, numbers, dates, etc.).
 * This is the fallback renderer that handles all basic value types.
 */
class PrimitiveValueRenderer implements ValueRenderer {
    
    @Override
    public boolean canRender(Value value) {
        // This renderer can handle any value - it's the fallback
        return true;
    }
    
    @SneakyThrows
    @Override
    public String render(Value value, ValueRenderingContext context) {
        String stringValue = value.convert();
        // Apply max content length if configured
        if (context.config().maxCellContentLength() > 0 && 
            stringValue.length() > context.config().maxCellContentLength()) {
            stringValue = stringValue.substring(0, context.config().maxCellContentLength()) + "...";
        }
        
        return stringValue;
    }
    
    @Override
    public int getPriority() {
        return 0; // Lowest priority - this is the fallback renderer
    }
}
