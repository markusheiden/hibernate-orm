package org.hibernate.sql.results.graph.entity;

import java.util.Set;

/**
 * Text type for responsive text ads.
 */
public enum ResponsiveTextType {
    /**
     * Headlines.
     */
    HEADLINES(TextKind.HEADLINE, false, true),

    /**
     * Descriptions.
     */
    DESCRIPTIONS(TextKind.DESCRIPTION, false, true),

    /**
     * First paths for display URL.
     */
    PATHS1(TextKind.PATH, false, false),

    /**
     * Second paths for display URL.
     */
    PATHS2(TextKind.PATH, true, false, PATHS1);

    /**
     * Text kind.
     */
    private final TextKind textKind;

    /**
     * Use lines2?.
     */
    private final boolean lines2;

    /**
     * Are texts of the type are required?.
     */
    private final boolean required;

    /**
     * Text type which have to have texts for this type to be present.
     */
    private final Set<ResponsiveTextType> requiredOptionalTypes;

    /**
     * Hidden constructor.
     *
     * @param textKind
     *         Text kind.
     * @param lines2
     *         Use lines2?.
     * @param required
     *         Are texts of the type are required?.
     * @param requiredOptionalTypes
     *         Text type which have to have texts for this type to be present.
     */
    ResponsiveTextType(TextKind textKind, boolean lines2, boolean required, ResponsiveTextType... requiredOptionalTypes) {
        this.textKind = textKind;
        this.lines2 = lines2;
        this.required = required;
        this.requiredOptionalTypes = Set.of(requiredOptionalTypes);
    }

    /**
     * Text kind.
     */
    public TextKind getTextKind() {
        return textKind;
    }

    /**
     * Maximum text length.
     */
    public int getMaxTextLength() {
        return textKind.getMaxTextLength();
    }

    /**
     * Use lines2?.
     */
    public boolean isLines2() {
        return lines2;
    }

    /**
     * Are texts of the type are required?.
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * Are texts of the type are optional?.
     */
    public boolean isOptional() {
        return !required;
    }

    /**
     * Text type which have to have texts for this type to be present.
     */
    public Set<ResponsiveTextType> getRequiredOptionalTypes() {
        return requiredOptionalTypes;
    }

    /**
     * Is this text type (a part of) an url?
     */
    public boolean isPath() {
        return textKind == TextKind.PATH;
    }
}
