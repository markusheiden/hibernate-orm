package org.hibernate.sql.results.graph.entity;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.hibernate.annotations.Type;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REFRESH;
import static jakarta.persistence.FetchType.LAZY;
import static java.util.Collections.disjoint;
import static java.util.Collections.unmodifiableSet;

/**
 * Shared responsive text component.
 */
@Entity
@DiscriminatorValue("shared")
public class SharedTextComponent extends AbstractTextComponent {
    @Column(name = "portal_id")
    private long portalId;

    @Column(name = "trademark", nullable = true)
    private Boolean trademark;

    @ManyToOne(fetch = LAZY, cascade = { PERSIST, MERGE, REFRESH }, optional = true)
    @JoinColumn(name = "promotion_id", nullable = true)
    private Promotion promotion;

    /**
     * Deleted flag.
     */
    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    /**
     * Hidden default constructor for Hibernate.
     */
    protected SharedTextComponent() {}

    /**
     * New component.
     *
     * @param portalId
     *         Portal id.
     * @param promotion
     *         Type use-case of the component.
     * @param textKind
     *         Text kind.
     * @param lines1
     *         Lines 1.
     * @param lines2
     *         Lines 2.
     */
    public SharedTextComponent(
            final long portalId,
            final Promotion promotion,
            final TextKind textKind,
            final List<String> lines1,
            final List<String> lines2) {
        super(textKind, lines1, lines2);

        this.portalId = portalId;
        this.trademark = false;
        this.promotion = promotion;
    }

    public long getPortalId() {
        return portalId;
    }

    public Boolean getTrademark() {
        return trademark;
    }

    /**
     * Promotion of component. May be {@code null}.
     */
    public Promotion getPromotion() {
        return promotion;
    }

    /**
     * Is this a promotion component?.
     */
    public boolean isPromotion() {
        return promotion != null;
    }

    /**
     * Mark component as deleted.
     */
    public void delete() {
        this.deleted = true;
    }

    /**
     * Is the component marked as deleted?.
     */
    public boolean isDeleted() {
        return deleted;
    }
}
