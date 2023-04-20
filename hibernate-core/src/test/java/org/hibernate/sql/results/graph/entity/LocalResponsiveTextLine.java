package org.hibernate.sql.results.graph.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import static jakarta.persistence.CascadeType.DETACH;
import static jakarta.persistence.FetchType.LAZY;

/**
 * Line of a {@link LocalResponsiveText}.
 */
@Embeddable
public class LocalResponsiveTextLine extends AbstractLocalResponsiveTextLine {
    /**
     * Component this line has been generated from.
     * Do NOT cascade, because component may be a cached one.
     */
    @ManyToOne(fetch = LAZY, cascade = DETACH, optional = false)
    @JoinColumn(name = "component_id", nullable = false)
    // Do not reattach component, if text is reattached, to avoid attaching the component to different sessions.
    private AbstractTextComponent component;

    /**
     * Version of component this line has been generated from.
     */
    @Column(name = "component_version", nullable = false)
    private long componentVersion;

    /**
     * Index of text in component.
     */
    @Column(name = "index", nullable = false)
    private int index;

    /**
     * Hidden default constructor for Hibernate.
     */
    protected LocalResponsiveTextLine() {}

    /**
     * Copy constructor.
     *
     * @param line
     *         Line to copy.
     */
    public LocalResponsiveTextLine(LocalResponsiveTextLine line) {
        this(line.getComponent(), line.getIndex(), line.getText(), line.getPin());
    }

    /**
     * Constructor.
     *
     * @param component
     *         Component this line has been generated from.
     * @param index
     *         Index of text in template.
     * @param text
     *         Text of line.
     * @param pin
     *         Position the text is pinned to.
     */
    public LocalResponsiveTextLine(AbstractTextComponent component, int index, String text, Integer pin) {
        super(text, pin);

        this.component = component;
        this.componentVersion = component.getVersion();
        this.index = index;
    }

    /**
     * Component this line has been generated from.
     */
    public AbstractTextComponent getComponent() {
        return component;
    }

    /**
     * Version of component this line has been generated from.
     */
    public long getComponentVersion() {
        return componentVersion;
    }

    /**
     * Index of text in template.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Check, if this text has the same content (index, text, pin) as the given text.
     */
    public boolean isSameContent(LocalResponsiveTextLine o) {
        return equals(o) &&
               getIndex() == o.getIndex() &&
               getText().equals(o.getText()) &&
               Objects.equals(getPin(), o.getPin());
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof LocalResponsiveTextLine line &&
               component.equals(line.getComponent());
    }

    @Override
    public int hashCode() {
        return component.hashCode();
    }

    @Override
    public String toString() {
        return "Line " + index + " of component " + component.getId() + ": " + getText();
    }
}
