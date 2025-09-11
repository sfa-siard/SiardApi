package ch.admin.bar.siard2.api.export;

import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;

/**
 * HTML template generator for table exports.
 * Provides static methods to generate consistent HTML structure.
 */
class HtmlTemplate {
    
    private static final String DOCUMENT_START_TEMPLATE = """
        <!DOCTYPE html>
        <html lang="en">
          <head>
            <title>%s</title>
            <meta charset="utf-8" />
          </head>
          <body>
            <p>%s</p>
            <p>%s</p>
            <table>
        """;
    
    private static final String DOCUMENT_END_TEMPLATE = """
            </table>
          </body>
        </html>
        """;
    
    private HtmlTemplate() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Generate the HTML document start including head and table opening.
     *
     * @param title       the document title
     * @param tableName   the table name to display
     * @param description the table description
     * @return formatted HTML document start
     */
    static String documentStart(String title, String tableName, String description) {
        return String.format(DOCUMENT_START_TEMPLATE, 
            escapeHtml4(title), 
            escapeHtml4(tableName), 
            escapeHtml4(description != null ? description : ""));
    }
    
    /**
     * Generate the HTML document end.
     *
     * @return HTML document closing tags
     */
    static String documentEnd() {
        return DOCUMENT_END_TEMPLATE;
    }
    
    /**
     * Generate a table header cell.
     *
     * @param columnName the column name
     * @return formatted HTML th element
     */
    static String tableHeader(String columnName) {
        return "        <th>" + escapeHtml4(columnName) + "</th>\r\n";
    }
    
    /**
     * Generate a table data cell.
     *
     * @param content the cell content (already escaped if needed)
     * @return formatted HTML td element
     */
    static String tableCell(String content) {
        return "        <td>" + content + "</td>\r\n";
    }
    
    /**
     * Generate a table row start tag.
     *
     * @return HTML tr opening tag
     */
    static String rowStart() {
        return "      <tr>\r\n";
    }
    
    /**
     * Generate a table row end tag.
     *
     * @return HTML tr closing tag
     */
    static String rowEnd() {
        return "      </tr>\r\n";
    }
    
    /**
     * Generate an HTML link element.
     *
     * @param href the link URL
     * @param text the link text
     * @return formatted HTML a element
     */
    static String link(String href, String text) {
        return String.format("<a href=\"%s\">%s</a>", 
            escapeHtml4(href), 
            escapeHtml4(text));
    }
    
    /**
     * Generate an HTML definition list start.
     *
     * @return HTML dl opening tag
     */
    static String definitionListStart() {
        return "<dl>\r\n";
    }
    
    /**
     * Generate an HTML definition list end.
     *
     * @return HTML dl closing tag
     */
    static String definitionListEnd() {
        return "</dl>\r\n";
    }
    
    /**
     * Generate an HTML definition term.
     *
     * @param term the term text
     * @return formatted HTML dt element
     */
    static String definitionTerm(String term) {
        return "  <dt>" + escapeHtml4(term) + "</dt>\r\n";
    }
    
    /**
     * Generate an HTML definition description.
     *
     * @param description the description content (already escaped if needed)
     * @return formatted HTML dd element
     */
    static String definitionDescription(String description) {
        return "  <dd>" + description + "</dd>\r\n";
    }
    
    /**
     * Generate an HTML ordered list start.
     *
     * @return HTML ol opening tag
     */
    static String orderedListStart() {
        return "<ol>\r\n";
    }
    
    /**
     * Generate an HTML ordered list end.
     *
     * @return HTML ol closing tag
     */
    static String orderedListEnd() {
        return "</ol>\r\n";
    }
    
    /**
     * Generate an HTML list item.
     *
     * @param content the item content (already escaped if needed)
     * @return formatted HTML li element
     */
    static String listItem(String content) {
        return "  <li>" + content + "</li>\r\n";
    }
}
