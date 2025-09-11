package ch.admin.bar.siard2.api.export;

import ch.admin.bar.siard2.api.Value;

import java.io.IOException;

/**
 * Renderer for array values.
 * Renders array values as HTML ordered lists with each element as a list item.
 */
class ArrayValueRenderer implements ValueRenderer {
    
    @Override
    public boolean canRender(Value value) throws IOException {
        return value.getMetaValue().getCardinality() > 0;
    }
    
    @Override
    public String render(Value value, ValueRenderingContext context) throws IOException {
        if (value.getElements() == 0) return "";

        StringBuilder sb = new StringBuilder();
        sb.append(HtmlTemplate.orderedListStart());
        for (int iElement = 0; iElement < value.getElements(); iElement++) {
            // Recursively render each array element
            String elementValue = context.rendererRegistry().render(value.getElement(iElement), context);
            sb.append(HtmlTemplate.listItem(elementValue));
        }
        
        sb.append(HtmlTemplate.orderedListEnd());
        return sb.toString();
    }
    
    @Override
    public int getPriority() {
        return 60; // Medium priority - arrays should be handled before primitives
    }
}
