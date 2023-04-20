package org.hibernate.sql.results.graph.entity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import static jakarta.persistence.GenerationType.SEQUENCE;

@SequenceGenerator(name = "seq_promotion", sequenceName = "seq_promotion", allocationSize = 1)
@Entity
@Table(name = "promotion")
public class Promotion implements Comparable<Promotion> {
    private static final Comparator<Promotion> PROMOTION_NAME_COMPARATOR = Comparator.comparing(Promotion::getName);
    @Id
    @GeneratedValue(strategy = SEQUENCE, generator = "seq_promotion")
    private Long id;

    @Column(name = "portal_id")
    private long portalId;

    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Date-time of the beginning of the promotion. If set, all ad-templates for this promotion will go active. If not set, the promotion will have no definite beginning and only obey to the end date-time if set.
     */
    @Column(name = "start_time")
    private LocalDateTime start;

    /**
     * Date-time of the end of the promotion. If set, all ad-templates for this promotion will go inactive. If not set, the promotion will have no definite end and only obey to the start date-time if set.
     */
    @Column(name = "end_time")
    private LocalDateTime end;

    /**
     * User who modified this promotion last.
     */
    @Column(name = "modified_by", nullable = true)
    private String modifiedBy;

    /**
     * Last modified at.
     */
    @Column(name = "modified_at")
    private Instant modifiedAt;

    /**
     * Hidden default constructor for Hibernate.
     */
    protected Promotion() {}

    Promotion(Long id, long portalId, String name, LocalDateTime start, LocalDateTime end) {
        this(portalId, name);
        this.id = id;
        this.start = start;
        this.end = end;
    }

    public Promotion(long portalId, String name) {
        this.portalId = portalId;
        this.name = name;
    }

    /**
     * Is the promotion currently active.
     */
    public boolean isActive(LocalDateTime now) {
        if (start == null && end == null) {
            return false;
        }

        return (start == null || start.isBefore(now)) && (end == null || end.isAfter(now));
    }

    public Long getId() {
        return id;
    }

    public long getPortalId() {
        return portalId;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public Instant getModifiedAt() {
        return modifiedAt;
    }

    public void modified(String modifiedBy, Instant modifiedAt) {
        this.modifiedBy = modifiedBy;
        this.modifiedAt = modifiedAt;
    }

    @Override
    public int compareTo(Promotion other) {
        return PROMOTION_NAME_COMPARATOR.compare(this, other);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Promotion promotion &&
               getPortalId() == promotion.getPortalId() &&
               getName().equals(promotion.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(portalId, name);
    }
}
