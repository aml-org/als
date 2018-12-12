/**
 * Completion suggestion.
 *
 * Suggestion may have all of text, description and displayText filled,
 * but may have only some of them.
 * Priority of the field to use for display: displayText, text.
 * Priority of the field to use for text replacement: text, displayText.
 */
export interface Suggestion {
    /**
     * Full text to insert, including the index.
     */
    text?: string;
    /**
     * Description of the suggestion.
     */
    description?: string;
    /**
     * Text to display.
     */
    displayText?: string;
    /**
     * Detected suggestion prefix.
     */
    prefix?: string;
    /**
     * Suggestion category.
     */
    category?: string;
}