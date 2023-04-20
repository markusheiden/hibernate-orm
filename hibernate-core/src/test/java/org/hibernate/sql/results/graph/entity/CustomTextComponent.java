package org.hibernate.sql.results.graph.entity;

import java.util.List;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Custom text component.
 */
@Entity
@DiscriminatorValue("custom")
public class CustomTextComponent extends AbstractTextComponent {
    /**
     * Hidden default constructor for Hibernate.
     */
    protected CustomTextComponent() {}

    /**
     * Copy constructor for creating a component as copy of another {@link AbstractTextComponent}.
     */
    public CustomTextComponent(final AbstractTextComponent source) {
        super(source.getTextKind(), source.getLines1(), source.getLines2());
    }

    /**
     * Constructor for creating an empty component from scratch.
     */
    public CustomTextComponent(TextKind textKind) {
        super(textKind, List.of(), List.of());
    }
}
