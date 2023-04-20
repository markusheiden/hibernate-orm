package org.hibernate.sql.results.graph.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

/**
 * Line of a {@link AbstractLocalResponsiveText}.
 */
@MappedSuperclass
public abstract class AbstractLocalResponsiveTextLine {
    /**
     * Text of line.
     */
    @Column(name = "text", nullable = false)
    private String text;

    /**
     * Position the text is pinned to.
     */
    @Column(name = "pin", nullable = true)
    private Integer pin;

    /**
     * Hidden default constructor for Hibernate.
     */
    protected AbstractLocalResponsiveTextLine() {}

    /**
     * Constructor.
     *
     * @param text
     *         Text of line.
     * @param pin
     *         Position the text is pinned to.
     */
    protected AbstractLocalResponsiveTextLine(String text, Integer pin) {
        this.text = text;
        this.pin = pin;
    }

    /**
     * Position the text is pinned to.
     */
    public Integer getPin() {
        return pin;
    }

    /**
     * Text of line.
     */
    public String getText() {
        return text;
    }
}
