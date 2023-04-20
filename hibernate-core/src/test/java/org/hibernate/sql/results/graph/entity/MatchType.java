package org.hibernate.sql.results.graph.entity;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static java.util.Arrays.stream;

/**
 * Type of entity.
 */
public enum MatchType {
    /**
     * Exact match.
     */
    EXACT("e", false),

    /**
     * Phrase match.
     */
    PHRASE("p", false),

    /**
     * Phrase match normalized.
     */
    PHRASE_NORMALIZED("pn", true),

    /**
     * Broad match normalized.
     */
    BROAD_NORMALIZED("b", true);

    /**
     * Short name.
     */
    private final String shortName;

    /**
     * Need keywords to be normalized for this match type?. We do NOT support unknowns for normalized keywords.
     */
    private final boolean normalized;

    /**
     * Hidden constructor.
     *
     * @param shortName
     *         Short name.
     * @param normalized
     *         Need keywords to be normalized for this match type?.
     */
    MatchType(String shortName, boolean normalized) {
        this.shortName = shortName;
        this.normalized = normalized;
    }

    /**
     * Is the type short name valid?.
     *
     * @param shortName
     *         Short name.
     */
    public static boolean isValid(String shortName) {
        return byShortNameImpl(shortName).isPresent();
    }

    /**
     * Type by its short name.
     *
     * @param shortName
     *         Short name.
     * @throws NoSuchElementException
     *         If short name is not known.
     */
    public static MatchType byShortName(String shortName) throws NoSuchElementException {
        return byShortNameImpl(shortName)
                .orElseThrow(() -> new IllegalArgumentException("Invalid match type: " + shortName));
    }

    /**
     * Type by its short name.
     *
     * @param shortName
     *         Short name.
     */
    private static Optional<MatchType> byShortNameImpl(String shortName) {
        return stream(values())
                .filter(type -> type.getShortName().equals(shortName))
                .findFirst();
    }

    /**
     * Short name.
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * Is this match type supported?
     *
     * @param unknown
     *         Keyword / group / meta group contains unknown words.
     */
    public boolean isSupported(boolean unknown) {
        return !unknown || !this.isNormalized();
    }

    /**
     * Need keywords to be normalized for this match type?.
     */
    public boolean isNormalized() {
        return normalized;
    }

    /**
     * The "narrower" match type.
     */
    public MatchType narrower() {
        var values = values();
        int next = ordinal() - 1;

        return next >= 0 ? values[next] : null;
    }

    /**
     * The "broader" match type.
     */
    public MatchType broader() {
        var values = values();
        int next = ordinal() + 1;

        return next < values.length ? values[next] : null;
    }

    @Override
    public String toString() {
        return shortName;
    }

    /**
     * Is this match type contained in the given ones?.
     */
    public boolean in(MatchType... matchTypes) {
        return List.of(matchTypes).contains(this);
    }
}
