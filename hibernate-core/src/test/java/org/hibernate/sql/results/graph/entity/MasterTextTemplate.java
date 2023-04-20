package org.hibernate.sql.results.graph.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import org.hibernate.annotations.Fetch;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REFRESH;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.SEQUENCE;
import static org.hibernate.annotations.FetchMode.SUBSELECT;

/**
 * A master template is a logical group of text templates. All text templates contained in a master template have access
 * to the set of entity variables that are configured for the master template.
 */
@SequenceGenerator(name = "seq_master_template", sequenceName = "seq_master_template", allocationSize = 1)
@Entity
@Table(name = "master_template")
public class MasterTextTemplate {
    /**
     * Id.
     */
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = SEQUENCE, generator = "seq_master_template")
    private Long id;

    /**
     * Trademark flag. Derived from the entity types.
     * <p>
     * May NOT be null, because the "all entity types" fallback is NOT supported for master text templates.
     */
    @Column(name = "trademark", nullable = false)
    private boolean trademark;

    /**
     * The portal which contains this master template.
     */
    @Column(name = "portal_id")
    private long portalId;

    /**
     * The responsive templates contained in this master template.
     * <p>
     * Load lazily for optimized counting of master templates.
     */
    @OneToMany(mappedBy = "masterTemplate", cascade = { PERSIST, MERGE, REFRESH }, fetch = LAZY)
    @Fetch(SUBSELECT)
    @OrderColumn(name = "p")
    private List<ResponsiveTextTemplate> responsiveTextTemplates = new ArrayList<>();

    /**
     * Hidden default constructor for Hibernate.
     */
    protected MasterTextTemplate() {}

    /**
     * Constructor for master templates with group selector.
     *
     * @param portalId
     *         Portal id.
     */
    public MasterTextTemplate(long portalId) {
        this.portalId = portalId;
        this.trademark = false;
    }

    public long getPortalId() {
        return portalId;
    }

    /**
     * Returns the ID of this master template.
     */
    public Long getId() {
        return id;
    }

    public Boolean getTrademark() {
        return trademark;
    }

    /**
     * Is there at least one active template?.
     */
    public boolean hasActiveTemplate() {
        return responsiveTextTemplates.stream().anyMatch(ResponsiveTextTemplate::isActive);
    }

    /**
     * All responsive text templates contained in this master template.
     */
    public Stream<ResponsiveTextTemplate> streamResponsiveTextTemplates() {
        return responsiveTextTemplates.stream();
    }

    /**
     * All responsive text templates contained in this master template.
     */
    public List<ResponsiveTextTemplate> getResponsiveTextTemplates() {
        return responsiveTextTemplates;
    }

    /**
     * Active responsive text templates contained in this master template.
     */
    public List<ResponsiveTextTemplate> getActiveResponsiveTextTemplates() {
        return streamResponsiveTextTemplates()
                .filter(ResponsiveTextTemplate::isActive)
                .toList();
    }

    /**
     * Set responsive text templates.
     */
    public void setResponsiveTextTemplates(List<ResponsiveTextTemplate> templates) {
        templates.forEach(template -> template.setMasterTemplate(this));
        this.responsiveTextTemplates.clear();
        this.responsiveTextTemplates.addAll(templates);
    }

    /**
     * Add the responsive text template to this master template.
     *
     * @param template
     *         Typically a newly generated template.
     * @return template for method chaining.
     */
    public ResponsiveTextTemplate addResponsiveTextTemplate(ResponsiveTextTemplate template) {
        template.setMasterTemplate(this);
        responsiveTextTemplates.add(template);
        return template;
    }

    /**
     * Remove responsive text template.
     */
    public void removeResponsiveTextTemplate(ResponsiveTextTemplate template) {
        template.setMasterTemplate(null);
        responsiveTextTemplates.remove(template);
    }

    /**
     * Remove all responsive text templates.
     */
    void removeAllResponsiveTextTemplates() {
        responsiveTextTemplates.forEach(template -> template.setMasterTemplate(null));
        responsiveTextTemplates.clear();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof MasterTextTemplate master &&
               Objects.equals(getTrademark(), master.getTrademark());
    }

    @Override
    public int hashCode() {
        return Objects.hash(trademark);
    }
}
