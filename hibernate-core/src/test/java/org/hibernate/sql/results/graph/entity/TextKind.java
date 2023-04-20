package org.hibernate.sql.results.graph.entity;

/**
 * Text kind for text components.
 */
public enum TextKind {
    HEADLINE(false, 3, 3, 15, 30),
    DESCRIPTION(false, 2, 2, 4, 90),
    PATH(true, 1, 0, 1, 15);

    private final boolean lines2;
    private final int maxPosition;
    private final int minTexts;
    private final int maxTexts;
    private final int maxTextLength;

    TextKind(final boolean lines2, final int maxPosition, int minTexts, int maxTexts, final int maxTextLength) {
        this.lines2 = lines2;
        this.maxPosition = maxPosition;
        this.minTexts = minTexts;
        this.maxTexts = maxTexts;
        this.maxTextLength = maxTextLength;
    }

    /**
     * Name of text kind. E.g. for error messages.
     */
    public String getName() {
        return name().toLowerCase();
    }

    /**
     * Check, if the given pin position is valid.
     */
    public boolean isValidPin(Integer position) {
        return position == null || position >= 1 && position <= maxPosition;
    }

    /**
     * Does the text kind support a second set of text lines?.
     */
    public boolean hasLines2() {
        return lines2;
    }

    /**
     * Supports pins?.
     */
    public boolean supportsPins() {
        return maxPosition > 1;
    }

    /**
     * Maximum position of a text kind. E.g. at max. 3 headlines are allowed.
     */
    public int getMaxPosition() {
        return maxPosition;
    }

    /**
     * Minimum number of alternative texts. E.g. for headlines at least 3 alternative texts are required.
     */
    public int getMinTexts() {
        return minTexts;
    }

    /**
     * Maximum number of alternative texts. E.g. for headlines at max. 15 alternative texts are allowed.
     */
    public int getMaxTexts() {
        return maxTexts;
    }

    /**
     * Maximum length of a text. E.g. each headline may contain at max. 30 chars.
     */
    public int getMaxTextLength() {
        return maxTextLength;
    }
}
