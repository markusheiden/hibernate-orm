package org.hibernate.sql.results.graph.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToOne;
import org.hibernate.annotations.Fetch;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REFRESH;
import static jakarta.persistence.FetchType.EAGER;
import static org.hibernate.annotations.FetchMode.SELECT;

/**
 * Component link of a {@link ResponsiveTextTemplate}.
 */
@MappedSuperclass
public abstract class TextComponentLink<C extends AbstractTextComponent> {
    @Embeddable
    public static class SharedTextComponentLink extends TextComponentLink<SharedTextComponent> {
        /**
         * Hidden default constructor for Hibernate.
         */
        protected SharedTextComponentLink() {}

        /**
         * Constructor.
         *
         * @param component
         *         Component.
         * @param pin
         *         Position the component is pinned to.
         */
        public SharedTextComponentLink(SharedTextComponent component, Integer pin) {
            super(component, pin);
        }
    }

    @Embeddable
    public static class CustomTextComponentLink extends TextComponentLink<CustomTextComponent> {
        /**
         * Hidden default constructor for Hibernate.
         */
        protected CustomTextComponentLink() {}

        /**
         * Constructor.
         *
         * @param component
         *         Component.
         * @param pin
         *         Position the component is pinned to.
         */
        public CustomTextComponentLink(CustomTextComponent component, Integer pin) {
            super(component, pin);
        }
    }

    /**
     * Component.
     */
    // Do NOT use @OneToOne(targetEntity = AbstractTextComponent.class),
    // because then casting to concrete subclasses is not possible!
    @OneToOne(cascade = { PERSIST, MERGE, REFRESH }, fetch = EAGER)
    @Fetch(SELECT)
    @JoinColumn(name = "component_id", nullable = false)
    private C component;

    /**
     * Position the component is pinned to.
     */
    @Column(name = "pin", nullable = true)
    private Integer pin;

    /**
     * Hidden default constructor for Hibernate.
     */
    protected TextComponentLink() {}

    /**
     * Constructor.
     *
     * @param component
     *         Component.
     * @param pin
     *         Position the component is pinned to.
     */
    TextComponentLink(C component, Integer pin) {
        this.component = component;
        this.pin = pin;
    }

    /**
     * Component.
     */
    public C getComponent() {
        return component;
    }

    /**
     * Position the component is pinned to.
     */
    public Integer getPin() {
        return pin;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public boolean equals(Object o) {
        return o instanceof TextComponentLink link &&
               component.equals(link.getComponent()) &&
               Objects.equals(pin, link.getPin());
    }

    @Override
    public int hashCode() {
        return component.hashCode();
    }

    @Override
    public String toString() {
        var result = "Component %d".formatted(component.getId());
        if (pin != null) {
            result += " pinned to position %d".formatted(pin);
        }
        return result;
    }
}
