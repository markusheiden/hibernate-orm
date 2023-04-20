package org.hibernate.sql.results.graph.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import org.hibernate.annotations.Fetch;
import org.hibernate.sql.results.graph.entity.TextComponentLink.CustomTextComponentLink;
import org.hibernate.sql.results.graph.entity.TextComponentLink.SharedTextComponentLink;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REFRESH;
import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.SEQUENCE;
import static java.lang.String.format;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toSet;
import static org.hibernate.annotations.FetchMode.SELECT;
import static org.hibernate.annotations.FetchMode.SUBSELECT;
import static org.hibernate.sql.results.graph.entity.ResponsiveTextType.DESCRIPTIONS;
import static org.hibernate.sql.results.graph.entity.ResponsiveTextType.HEADLINES;
import static org.hibernate.sql.results.graph.entity.ResponsiveTextType.PATHS1;
import static org.hibernate.sql.results.graph.entity.ResponsiveTextType.PATHS2;

/**
 * Template for {@link LocalResponsiveText}s / responsive search ads (RSA).
 */
// The allocation size has to be the same as defined in the Postgres sequence!
@SequenceGenerator(name = "seq_text_template", sequenceName = "seq_text_template", allocationSize = 10)
@Entity
@Table(name = "responsive_text_template")
public class ResponsiveTextTemplate implements TextTemplate {
    /**
     * Supported text types.
     */
    private static final List<ResponsiveTextType> TEXT_TYPES = List.of(
            HEADLINES, DESCRIPTIONS, PATHS1, PATHS2);

    /**
     * Supported text kinds.
     */
    private static final Set<TextKind> TEXT_KINDS = TEXT_TYPES.stream()
            .map(ResponsiveTextType::getTextKind)
            .collect(toSet());

    /**
     * Id.
     * <p>
     * Yes, we use one sequence for all template types, so that the template id is unique in the tracking.
     */
    @Id
    @GeneratedValue(strategy = SEQUENCE, generator = "seq_text_template")
    private Long id;

    @Version
    private int version;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = LAZY, cascade = { PERSIST, MERGE, REFRESH }, optional = true)
    @JoinColumn(name = "promotion_id", nullable = true)
    private Promotion promotion;

    @Column(name = "active", nullable = false)
    private boolean active;

    /**
     * Shared headlines. May contain {@code null} elements, if referenced shared components have been deleted.
     */
    @ElementCollection(fetch = EAGER)
    @Fetch(SUBSELECT)
    @CollectionTable(name = "responsive_text_template_shared_headline", joinColumns = @JoinColumn(name = "template_id", nullable = false))
    @OrderColumn(name = "p")
    private List<SharedTextComponentLink> sharedHeadlines = new ArrayList<>();

    /**
     * Shared descriptions. May contain {@code null} elements, if referenced shared components have been deleted.
     */
    @ElementCollection(fetch = EAGER)
    @Fetch(SUBSELECT)
    @CollectionTable(name = "responsive_text_template_shared_description", joinColumns = @JoinColumn(name = "template_id", nullable = false))
    @OrderColumn(name = "p")
    private List<SharedTextComponentLink> sharedDescriptions = new ArrayList<>();

    /**
     * Shared path. May contain {@code null} elements, if referenced shared components have been deleted.
     */
    @ElementCollection(fetch = EAGER)
    @Fetch(SUBSELECT)
    @CollectionTable(name = "responsive_text_template_shared_path", joinColumns = @JoinColumn(name = "template_id", nullable = false))
    @OrderColumn(name = "p")
    private List<SharedTextComponentLink> sharedPaths = new ArrayList<>();

    @ElementCollection(fetch = EAGER)
    @Fetch(SUBSELECT)
    @CollectionTable(name = "responsive_text_template_custom_headline", joinColumns = @JoinColumn(name = "template_id", nullable = false))
    @OrderColumn(name = "p")
    private List<CustomTextComponentLink> customHeadlines = new ArrayList<>();

    @ElementCollection(fetch = EAGER)
    @Fetch(SUBSELECT)
    @CollectionTable(name = "responsive_text_template_custom_description", joinColumns = @JoinColumn(name = "template_id", nullable = false))
    @OrderColumn(name = "p")
    private List<CustomTextComponentLink> customDescriptions = new ArrayList<>();

    @ElementCollection(fetch = EAGER)
    @Fetch(SUBSELECT)
    @CollectionTable(name = "responsive_text_template_custom_path", joinColumns = @JoinColumn(name = "template_id", nullable = false))
    @OrderColumn(name = "p")
    private List<CustomTextComponentLink> customPaths = new ArrayList<>();

    @ManyToOne(fetch = EAGER, cascade = { PERSIST, MERGE, REFRESH }, optional = true)
    @Fetch(SELECT)
    @JoinColumn(name = "master_template_id", nullable = true)
    private MasterTextTemplate masterTemplate;

    /**
     * Hidden default constructor for Hibernate.
     */
    protected ResponsiveTextTemplate() {}

    public ResponsiveTextTemplate(String name) {
        this(name, null);
    }

    public ResponsiveTextTemplate(String name, Promotion promotion) {
        this.name = name;
        this.promotion = promotion;
    }

    /**
     * Checks if template was deleted, by checking if there is a master template attached.
     */
    public boolean isDeleted() {
        return masterTemplate == null;
    }

    @Override
    public MasterTextTemplate getMasterTemplate() {
        return masterTemplate;
    }

    void setMasterTemplate(MasterTextTemplate masterTemplate) {
        this.masterTemplate = masterTemplate;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Promotion getPromotion() {
        return promotion;
    }

    /**
     * Checks if template is a promotion template.
     */
    public boolean isPromotion() {
        return promotion != null;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Update the template's active flag based on the promotion.
     */
    public void updateActive(LocalDateTime now) {
        if (promotion == null) {
            return;
        }
        this.active = promotion.isActive(now);
    }

    /**
     * All supported {@link ResponsiveTextType}s.
     */
    public List<ResponsiveTextType> allTextTypes() {
        return TEXT_TYPES;
    }

    /**
     * All supported {@link TextKind}s.
     */
    public Set<TextKind> allTextKinds() {
        return TEXT_KINDS;
    }

    /**
     * All {@link AbstractTextComponent components} of a {@link TextKind} as stream.
     */
    public Stream<TextComponentLink<?>> getTextComponents(TextKind kind) {
        return Stream.concat(sharedTextComponents(kind).stream(), customTextComponents(kind).stream());
    }

    /**
     * Shared headlines. Unmodifiable.
     */
    public List<SharedTextComponentLink> getSharedHeadlines() {
        // Remove null elements due to referenced deleted shared components.
        return unmodifiableList(sharedHeadlines);
    }

    /**
     * Shared descriptions. Unmodifiable.
     */
    public List<SharedTextComponentLink> getSharedDescriptions() {
        // Remove null elements due to referenced deleted shared components.
        return unmodifiableList(sharedDescriptions);
    }

    /**
     * Shared path.
     */
    public List<SharedTextComponentLink> getSharedPaths() {
        // Remove null elements due to referenced deleted shared components.
        return unmodifiableList(sharedPaths);
    }

    /**
     * Shared text components of the given type. Modifiable. Just for internal use.
     */
    public List<SharedTextComponentLink> getSharedTextComponents(TextKind kind) {
        return unmodifiableList(sharedTextComponents(kind));
    }

    /**
     * Remove all shared components of a kind.
     */
    public void removeAllSharedComponents(TextKind kind) {
        sharedTextComponents(kind).clear();
    }

    /**
     * Shared text components of the given type. Modifiable. Just for internal use.
     */
    private List<SharedTextComponentLink> sharedTextComponents(TextKind kind) {
        // Remove null elements due to referenced deleted shared components.
        return switch (kind) {
            case HEADLINE -> sharedHeadlines;
            case DESCRIPTION -> sharedDescriptions;
            case PATH -> sharedPaths;
        };
    }

    /**
     * Custom headlines. Unmodifiable.
     */
    public List<CustomTextComponentLink> getCustomHeadlines() {
        return unmodifiableList(customHeadlines);
    }

    /**
     * Custom descriptions. Unmodifiable.
     */
    public List<CustomTextComponentLink> getCustomDescriptions() {
        return unmodifiableList(customDescriptions);
    }

    /**
     * Custom path.
     */
    public List<CustomTextComponentLink> getCustomPaths() {
        return unmodifiableList(customPaths);
    }

    /**
     * Custom text components of the given type. Modifiable. Just for internal use.
     */
    public List<CustomTextComponentLink> getCustomTextComponents(TextKind kind) {
        return unmodifiableList(customTextComponents(kind));
    }

    /**
     * Custom text components of the given type. Modifiable. Just for internal use.
     */
    private List<CustomTextComponentLink> customTextComponents(TextKind kind) {
        return switch (kind) {
            case HEADLINE -> customHeadlines;
            case DESCRIPTION -> customDescriptions;
            case PATH -> customPaths;
        };
    }

    /**
     * Can the component be added?
     *
     * <ul>
     *    <li>Checks that component not already has been added.</li>
     *    <li>Checks that the maximum number of components constraint is not violated.</li>
     * </ul>
     */
    public boolean canBeAdded(SharedTextComponent component) {
        var kind = component.getTextKind();
        var sharedTextComponents = getSharedTextComponents(kind);
        return !contains(component) && sharedTextComponents.size() < kind.getMaxTexts();
    }

    /**
     * Has the component already been added to this template?.
     */
    private boolean contains(SharedTextComponent component) {
        return sharedTextComponents(component.getTextKind()).stream()
                .map(TextComponentLink::getComponent)
                .anyMatch(component::equals);
    }

    /**
     * Add the {@link SharedTextComponent} to this template without a pin.
     *
     * @param component
     *         The {@link SharedTextComponent} to add.
     * @return component for method chaining.
     */
    public SharedTextComponent add(SharedTextComponent component) {
        return add(component, null);
    }

    /**
     * Add the {@link SharedTextComponent} to this template.
     *
     * @param component
     *         The {@link SharedTextComponent} to add.
     * @param pin
     *         Pin.
     * @return component for method chaining.
     */
    public SharedTextComponent add(SharedTextComponent component, Integer pin) {
        if (contains(component)) {
            return null;
        }

        sharedTextComponents(component.getTextKind())
                .add(new SharedTextComponentLink(component, pin));
        return component;
    }

    /**
     * Copy the specified {@link SharedTextComponent} to this template without a pin.
     *
     * @param source
     *         The {@link SharedTextComponent} that will be copied.
     * @return The {@link CustomTextComponent} copy that has been added.
     */
    public CustomTextComponent addCopyOf(SharedTextComponent source) {
        var copy = new CustomTextComponent(source);
        add(copy, null);
        return copy;
    }

    /**
     * Has the component already been added to this template?.
     */
    private boolean contains(CustomTextComponent component) {
        return customTextComponents(component.getTextKind()).stream()
                .map(TextComponentLink::getComponent)
                .anyMatch(component::equals);
    }

    /**
     * Add the {@link CustomTextComponent} to this template without a pin.
     *
     * @param component
     *         The {@link CustomTextComponent} to add.
     * @return customTextComponent for method chaining.
     */
    public CustomTextComponent add(final CustomTextComponent component) {
        return add(component, null);
    }

    /**
     * Add the {@link CustomTextComponent} to this template.
     *
     * @param component
     *         The {@link CustomTextComponent} to add.
     * @param pin
     *         Pin.
     * @return component for method chaining.
     */
    public CustomTextComponent add(final CustomTextComponent component, Integer pin) {
        if (contains(component)) {
            return null;
        }

        customTextComponents(component.getTextKind())
                .add(new CustomTextComponentLink(component, pin));
        return component;
    }

    @Override
    public boolean equals(final Object o) {
        return o instanceof ResponsiveTextTemplate template &&
               Objects.equals(id, template.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
