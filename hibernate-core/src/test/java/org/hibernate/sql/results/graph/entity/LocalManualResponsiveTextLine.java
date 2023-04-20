package org.hibernate.sql.results.graph.entity;

import java.util.Objects;

import jakarta.persistence.Embeddable;

/**
 * Line of a {@link LocalManualResponsiveText}.
 */
@Embeddable
public class LocalManualResponsiveTextLine extends AbstractLocalResponsiveTextLine {
    /**
     * Hidden default constructor for Hibernate.
     */
    protected LocalManualResponsiveTextLine() {}

    public LocalManualResponsiveTextLine(String text) {
        this(text, null);
    }

    public LocalManualResponsiveTextLine(String text, Integer pin) {
        super(text, pin);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof AbstractLocalResponsiveTextLine line &&
               getText().equals(line.getText()) &&
               Objects.equals(getPin(), line.getPin());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getText(), getPin());
    }

    @Override
    public String toString() {
        return getText();
    }
}
