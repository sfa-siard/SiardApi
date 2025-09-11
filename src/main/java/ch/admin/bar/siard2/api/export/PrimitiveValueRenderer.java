package ch.admin.bar.siard2.api.export;

import ch.admin.bar.siard2.api.Value;

import java.io.IOException;

import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;

/**
 * Renderer for primitive values (strings, numbers, dates, etc.).
 * This is the fallback renderer that handles all basic value types.
 */
public class PrimitiveValueRenderer implements ValueRenderer {
    
    @Override
    public boolean canRender(Value value) {
        // This renderer can handle any value - it's the fallback
        return true;
    }
    
    @Override
    public String render(Value value, ValueRenderingContext context) throws IOException {
        // Special check for Boolean values - check if the content is actually empty
/*        try {
            if (value.getMetaValue().getPreType() == java.sql.Types.BOOLEAN) {
                // Check if the Boolean field is actually empty (not just parsed as false)
                String stringContent = value.getString();
                if (stringContent == null || stringContent.trim().isEmpty()) {
                    return "";
                }
            }
        } catch (Exception boolEx) {
            // Not a Boolean type or error accessing, continue with normal processing
        }*/
        
        if (value.isNull()) {
            return "";
        }
        
        String stringValue;
        try {
            stringValue = value.convert();
            if (stringValue == null) {
                return "";
            }
        } catch (Exception e) {
            // If conversion fails, try to get string representation directly
            try {
                stringValue = value.getString();
                if (stringValue == null) {
                    return "";
                }
            } catch (Exception ex) {
                // Last resort - return empty string for problematic values
                return "";
            }
        }
        
        // Apply max content length if configured
        if (context.config().maxCellContentLength() > 0 && 
            stringValue.length() > context.config().maxCellContentLength()) {
            stringValue = stringValue.substring(0, context.config().maxCellContentLength()) + "...";
        }
        
        return escapeHtml4(stringValue);
    }
    
    @Override
    public int getPriority() {
        return 0; // Lowest priority - this is the fallback renderer
    }
}
