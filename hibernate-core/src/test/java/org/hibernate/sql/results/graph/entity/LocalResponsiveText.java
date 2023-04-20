package org.hibernate.sql.results.graph.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
import org.hibernate.annotations.Fetch;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.SEQUENCE;
import static org.hibernate.annotations.FetchMode.SUBSELECT;

/**
 * Local representation adSoul responsive text ad.
 */
// The allocation size has to be the same as defined in the Postgres sequence!
@SequenceGenerator(name = "seq_local_responsive_text", sequenceName = "seq_local_responsive_text", allocationSize = 100)
@Entity
@Table(name = "local_responsive_text")
public class LocalResponsiveText
        extends AbstractLocalResponsiveText<LocalResponsiveText, LocalResponsiveTextLine>
        implements GodzillaText<LocalResponsiveText> {
    /**
     * Id.
     */
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = SEQUENCE, generator = "seq_local_responsive_text")
    @SuppressWarnings("UnusedDeclaration")
    private Long id;

    /**
     * Template on which this text is based.
     * Do NOT cascade, because template may be a cached one.
     */
    @ManyToOne(fetch = LAZY, cascade = ALL, optional = false)
    @JoinColumn(name = "template_id", nullable = false)
    // Do not reattach template, if text is reattached, to avoid attaching the template to different sessions.
    private ResponsiveTextTemplate template;

    /**
     * Version of template used to generate this text.
     */
    @Column(name = "template_version", nullable = false)
    private int templateVersion;

    /**
     * Texts for headlines.
     * <p>
     * Always use via {@link #streamHeadlines()} to avoid {@code null} values due to manually deleted lines.
     */
    @ElementCollection(fetch = LAZY)
    @Fetch(SUBSELECT)
    @CollectionTable(name = "local_responsive_text_headline", joinColumns = @JoinColumn(name = "text_id", nullable = false))
    @OrderColumn(name = "p")
    private List<LocalResponsiveTextLine> headlines = new ArrayList<>();

    /**
     * Texts for descriptions.
     * <p>
     * Always use via {@link #streamDescriptions()} to avoid {@code null} values due to manually deleted lines.
     */
    @ElementCollection(fetch = LAZY)
    @Fetch(SUBSELECT)
    @CollectionTable(name = "local_responsive_text_description", joinColumns = @JoinColumn(name = "text_id", nullable = false))
    @OrderColumn(name = "p")
    private List<LocalResponsiveTextLine> descriptions = new ArrayList<>();

    /**
     * Texts for paths 1.
     * <p>
     * Always use via {@link #streamPaths1()} to avoid {@code null} values due to manually deleted lines.
     */
    @ElementCollection(fetch = LAZY)
    @Fetch(SUBSELECT)
    @CollectionTable(name = "local_responsive_text_path1", joinColumns = @JoinColumn(name = "text_id", nullable = false))
    @OrderColumn(name = "p")
    private List<LocalResponsiveTextLine> paths1 = new ArrayList<>();

    /**
     * Texts for paths 2.
     * <p>
     * Always use via {@link #streamPaths2()} to avoid {@code null} values due to manually deleted lines.
     */
    @ElementCollection(fetch = LAZY)
    @Fetch(SUBSELECT)
    @CollectionTable(name = "local_responsive_text_path2", joinColumns = @JoinColumn(name = "text_id", nullable = false))
    @OrderColumn(name = "p")
    private List<LocalResponsiveTextLine> paths2 = new ArrayList<>();

    /**
     * Hidden default constructor for Hibernate.
     */
    protected LocalResponsiveText() {}

    /**
     * Constructor.
     *
     * @param template
     *         Template on which this text is based.
     */
    public LocalResponsiveText(ResponsiveTextTemplate template) {
        this.template = template;
        this.templateVersion = template.getVersion();
    }

    /**
     * Copy constructor.
     */
    public LocalResponsiveText(LocalResponsiveText text) {
        this(text.getTemplate());

        updateFrom(text);
    }

    /**
     * Update text from the given text.
     *
     * @param generatedText
     *         Re-generated text.
     */
    @Override
    public final void updateFrom(LocalResponsiveText generatedText) {
        for (var type : getTemplate().allTextTypes()) {
            setTexts(type, generatedText.getTexts(type).stream()
                    .map(LocalResponsiveTextLine::new)
                    .toList());
        }

        setUrl(generatedText.getUrl());
        setGoogleAdsUrl(generatedText.getGoogleAdsUrl());
        setBingAdsUrl(generatedText.getBingAdsUrl());
        setPaused(generatedText.isPaused());

        // Do NOT consider pause flag, because this does NOT originate from the text template.

        this.templateVersion = generatedText.getTemplate().getVersion();
    }

    /**
     * Does this text needs an update from the given re-generated text?.
     *
     * @param generatedText
     *         Re-generated text.
     * @return Does at least one attribute needs to be updated?
     */
    @Override
    public boolean needsUpdateFrom(LocalResponsiveText generatedText) {
        boolean change = false;

        for (var type : getTemplate().allTextTypes()) {
            var texts = getTexts(type);
            var generatedTexts = generatedText.getTexts(type);
            change |= texts.size() != generatedTexts.size();
            for (int i = 0; !change && i < texts.size(); i++) {
                change |= !texts.get(i).isSameContent(generatedTexts.get(i));
            }
        }

        change |= !Objects.equals(getUrl(), generatedText.getUrl());
        change |= !Objects.equals(getGoogleAdsUrl(), generatedText.getGoogleAdsUrl());
        change |= !Objects.equals(getBingAdsUrl(), generatedText.getBingAdsUrl());
        change |= !Objects.equals(isPaused(), generatedText.isPaused());

        // Do NOT consider pause flag, because this does NOT originate from the text template.

        return change;
    }

    /**
     * Checks if text status matches attached template status.
     */
    public boolean statusMatches() {
        return isEnabled() == template.isActive();
    }

    /**
     * Database id.
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * Template used to generate this text.
     */
    @Override
    public ResponsiveTextTemplate getTemplate() {
        return template;
    }

    /**
     * Version of template used to generate this text.
     */
    public int getTemplateVersion() {
        return templateVersion;
    }

    @Override
    public Stream<LocalResponsiveTextLine> streamHeadlines() {
        // Remove wrong positions due to manually deleted lines.
        return headlines.stream().filter(Objects::nonNull);
    }

    @Override
    public void setHeadlines(List<LocalResponsiveTextLine> headlines) {
        this.headlines.clear();
        this.headlines.addAll(headlines);
    }

    @Override
    public Stream<LocalResponsiveTextLine> streamDescriptions() {
        // Remove wrong positions due to manually deleted lines.
        return descriptions.stream().filter(Objects::nonNull);
    }

    @Override
    public void setDescriptions(List<LocalResponsiveTextLine> descriptions) {
        this.descriptions.clear();
        this.descriptions.addAll(descriptions);
    }

    @Override
    public Stream<LocalResponsiveTextLine> streamPaths1() {
        // Remove wrong positions due to manually deleted lines.
        return paths1.stream().filter(Objects::nonNull);
    }

    @Override
    public void setPath1(LocalResponsiveTextLine path1) {
        this.paths1.clear();
        if (path1 != null) {
            this.paths1.add(path1);
        }
    }

    @Override
    protected void setPaths1(List<LocalResponsiveTextLine> paths1) {
        this.paths1.clear();
        this.paths1.addAll(paths1);
    }

    @Override
    public Stream<LocalResponsiveTextLine> streamPaths2() {
        // Remove wrong positions due to manually deleted lines.
        return paths2.stream().filter(Objects::nonNull);
    }

    @Override
    public void setPath2(LocalResponsiveTextLine path2) {
        this.paths2.clear();
        if (path2 != null) {
            this.paths2.add(path2);
        }
    }

    @Override
    protected void setPaths2(List<LocalResponsiveTextLine> paths2) {
        this.paths2.clear();
        this.paths2.addAll(paths2);
    }

    @Override
    public void remove() {
        getGroup().removeResponsiveText(this);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof LocalResponsiveText text &&
               Objects.equals(getGroup(), text.getGroup()) &&
               isFromSameTemplate(text);
    }

    /**
     * Has the text been generated from the same template?.
     */
    @Override
    public boolean isFromSameTemplate(LocalResponsiveText text) {
        return getTemplate().equals(text.getTemplate());
    }

    @Override
    public int hashCode() {
        return getTemplate().hashCode();
    }
}
