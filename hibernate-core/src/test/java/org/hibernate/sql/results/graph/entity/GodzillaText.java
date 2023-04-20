package org.hibernate.sql.results.graph.entity;

/**
 * Common interface for generated texts.
 */
public interface GodzillaText<T extends GodzillaText<T>> extends Text<T> {
    /**
     * Template on which this text is based.
     * <p>
     * Be careful: The template may have changed since this text has been generated.
     */
    TextTemplate getTemplate();

    /**
     * Does this text needs an update from the given re-generated text?.
     *
     * @param generatedText
     *         Re-generated text.
     * @return Does at least one attribute needs to be updated?
     */
    boolean needsUpdateFrom(T generatedText);

    /**
     * Update text from the given text.
     *
     * @param generatedText
     *         Re-generated text.
     */
    void updateFrom(T generatedText);

    /**
     * Has the text been generated from the same template?.
     */
    boolean isFromSameTemplate(T text);
}
