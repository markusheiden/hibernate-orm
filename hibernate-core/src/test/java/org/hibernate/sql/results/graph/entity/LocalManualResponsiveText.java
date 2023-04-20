package org.hibernate.sql.results.graph.entity;

import java.time.LocalDate;
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
import jakarta.persistence.OrderColumn;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import org.hibernate.annotations.Fetch;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.SEQUENCE;
import static org.hibernate.annotations.FetchMode.SUBSELECT;

/**
 * Local representation of manual responsive text ad.
 */
// The allocation size has to be the same as defined in the Postgres sequence!
@SequenceGenerator(name = "seq_local_manual_responsive_text", sequenceName = "seq_local_manual_responsive_text", allocationSize = 100)
@Entity
@Table(name = "local_manual_responsive_text")
public class LocalManualResponsiveText
        extends AbstractLocalResponsiveText<LocalManualResponsiveText, LocalManualResponsiveTextLine>
        implements ManualText<LocalManualResponsiveText> {
    /**
     * Id.
     */
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = SEQUENCE, generator = "seq_local_manual_responsive_text")
    @SuppressWarnings("UnusedDeclaration")
    private Long id;

    /**
     * Texts for headlines.
     * <p>
     * Always use via {@link #streamHeadlines()} to avoid {@code null} values due to manually deleted lines.
     */
    @ElementCollection(fetch = LAZY)
    @Fetch(SUBSELECT)
    @CollectionTable(name = "local_manual_responsive_text_headline", joinColumns = @JoinColumn(name = "text_id", nullable = false))
    @OrderColumn(name = "p")
    private List<LocalManualResponsiveTextLine> headlines = new ArrayList<>();

    /**
     * Texts for descriptions.
     * <p>
     * Always use via {@link #streamDescriptions()} to avoid {@code null} values due to manually deleted lines.
     */
    @ElementCollection(fetch = LAZY)
    @Fetch(SUBSELECT)
    @CollectionTable(name = "local_manual_responsive_text_description", joinColumns = @JoinColumn(name = "text_id", nullable = false))
    @OrderColumn(name = "p")
    private List<LocalManualResponsiveTextLine> descriptions = new ArrayList<>();

    /**
     * Texts for paths 1.
     * <p>
     * Always use via {@link #streamPaths1()} to avoid {@code null} values due to manually deleted lines.
     */
    @ElementCollection(fetch = LAZY)
    @Fetch(SUBSELECT)
    @CollectionTable(name = "local_manual_responsive_text_path1", joinColumns = @JoinColumn(name = "text_id", nullable = false))
    @OrderColumn(name = "p")
    private List<LocalManualResponsiveTextLine> paths1 = new ArrayList<>();

    /**
     * Texts for paths 2.
     * <p>
     * Always use via {@link #streamPaths2()} to avoid {@code null} values due to manually deleted lines.
     */
    @ElementCollection(fetch = LAZY)
    @Fetch(SUBSELECT)
    @CollectionTable(name = "local_manual_responsive_text_path2", joinColumns = @JoinColumn(name = "text_id", nullable = false))
    @OrderColumn(name = "p")
    private List<LocalManualResponsiveTextLine> paths2 = new ArrayList<>();

    /**
     * Hidden default constructor for Hibernate.
     */
    protected LocalManualResponsiveText() {}

    /**
     * Constructor for creating a new manual text based on Google Ads data.
     */
    public LocalManualResponsiveText(
            LocalDate since, Long googleAdsId,
            List<LocalManualResponsiveTextLine> headlines, List<LocalManualResponsiveTextLine> descriptions,
            LocalManualResponsiveTextLine path1, LocalManualResponsiveTextLine path2, String url) {
        super.setGoogleAdsSince(since);
        super.setGoogleAdsId(googleAdsId);
        setHeadlines(headlines);
        setDescriptions(descriptions);
        setPath1(path1);
        setPath2(path2);
        setUrl(url);
    }

    @Override
    public Stream<LocalManualResponsiveTextLine> streamHeadlines() {
        // Remove wrong positions due to manually deleted lines.
        return headlines.stream().filter(Objects::nonNull);
    }

    /**
     * Database id.
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * Headlines.
     */
    @Override
    public void setHeadlines(List<LocalManualResponsiveTextLine> headlines) {
        this.headlines.clear();
        this.headlines.addAll(headlines);
    }

    @Override
    public Stream<LocalManualResponsiveTextLine> streamDescriptions() {
        // Remove wrong positions due to manually deleted lines.
        return descriptions.stream().filter(Objects::nonNull);
    }

    @Override
    public void setDescriptions(List<LocalManualResponsiveTextLine> descriptions) {
        this.descriptions.clear();
        this.descriptions.addAll(descriptions);
    }

    @Override
    public Stream<LocalManualResponsiveTextLine> streamPaths1() {
        // Remove wrong positions due to manually deleted lines.
        return paths1.stream().filter(Objects::nonNull);
    }

    @Override
    public void setPath1(LocalManualResponsiveTextLine path1) {
        this.paths1.clear();
        if (path1 != null) {
            this.paths1.add(path1);
        }
    }

    @Override
    protected void setPaths1(List<LocalManualResponsiveTextLine> paths1) {
        this.paths1.clear();
        this.paths1.addAll(paths1);
    }

    @Override
    public Stream<LocalManualResponsiveTextLine> streamPaths2() {
        // Remove wrong positions due to manually deleted lines.
        return paths2.stream().filter(Objects::nonNull);
    }

    @Override
    public void setPath2(LocalManualResponsiveTextLine path2) {
        this.paths2.clear();
        if (path2 != null) {
            this.paths2.add(path2);
        }
    }

    @Override
    protected void setPaths2(List<LocalManualResponsiveTextLine> paths2) {
        this.paths2.clear();
        this.paths2.addAll(paths2);
    }

    @Override
    public void remove() {
        getGroup().removeManualResponsiveText(this);
    }

    @Override
    public void setGoogleAdsSince(LocalDate googleAdsSince) {
        throw new UnsupportedOperationException("Google Ads since of manual texts cannot be changed.");
    }

    @Override
    public void setGoogleAdsId(Long googleAdsId) {
        throw new UnsupportedOperationException("Google Ads ids of manual texts cannot be changed.");
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof LocalManualResponsiveText text &&
               Objects.equals(getGroup(), text.getGroup()) &&
               getGoogleAdsId().equals(text.getGoogleAdsId());
    }

    @Override
    public int hashCode() {
        return getGoogleAdsId().hashCode();
    }
}
