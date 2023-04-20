package org.hibernate.sql.results.graph.entity;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.Fetch;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REFRESH;
import static jakarta.persistence.FetchType.EAGER;
import static org.hibernate.annotations.FetchMode.SELECT;

/**
 * Abstract local representation of responsive text ad.
 */
@MappedSuperclass
public abstract class AbstractLocalResponsiveText<T extends AbstractLocalResponsiveText<T, E>, E extends AbstractLocalResponsiveTextLine>
        extends AbstractLocal<Long, Long> implements Text<T> {

    /**
     * Has text been paused?.
     */
    @Column(name = "paused", nullable = false)
    private boolean paused;

    /**
     * URL.
     */
    @Column(name = "url")
    private String url;

    /**
     * URL with Google Ads tracking parameters.
     */
    @Column(name = "google_ads_url")
    private String googleAdsUrl;

    /**
     * URL with Bing Ads tracking parameters.
     */
    @Column(name = "bingads_url", nullable = true)
    private String bingAdsUrl;

    /**
     * Google Ads text ad ID.
     */
    @Column(name = "google_ads_id", nullable = true)
    private Long googleAdsId;

    /**
     * Bing Ads text id.
     */
    @Column(name = "bingads_id", nullable = true)
    private Long bingAdsId;

    /**
     * The last update to Google Ads failed.
     */
    @Column(name = "google_ads_update_failed", nullable = false)
    private boolean googleAdsUpdateFailed;

    /**
     * The last update to Bing Ads failed.
     */
    @Column(name = "bingads_update_failed", nullable = false)
    private boolean bingAdsUpdateFailed;

    /**
     * Group containing this text.
     */
    @ManyToOne(cascade = { PERSIST, MERGE, REFRESH }, fetch = EAGER, optional = false)
    @Fetch(SELECT)
    @JoinColumn(name = "group_id", nullable = false)
    private LocalGroup group;

    /**
     * Hidden default constructor for Hibernate.
     */
    protected AbstractLocalResponsiveText() {}

    /**
     * Has text been paused?.
     */
    @Override
    public boolean isPaused() {
        return paused;
    }

    /**
     * Has text been paused?.
     */
    @Override
    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    /**
     * Get texts of all types.
     */
    public Stream<String> streamAllTexts() {
        return Arrays.stream(ResponsiveTextType.values())
                .map(this::getTexts)
                .flatMap(Collection::stream)
                .map(AbstractLocalResponsiveTextLine::getText);
    }

    /**
     * Get all texts of the given type.
     */
    public List<E> getTexts(ResponsiveTextType type) {
        return switch (type) {
            case HEADLINES -> getHeadlines();
            case DESCRIPTIONS -> getDescriptions();
            case PATHS1 -> getPaths1();
            case PATHS2 -> getPaths2();
        };
    }

    /**
     * Set all texts of the given type.
     */
    public void setTexts(ResponsiveTextType type, List<E> texts) {
        switch (type) {
            case HEADLINES -> setHeadlines(texts);
            case DESCRIPTIONS -> setDescriptions(texts);
            case PATHS1 -> setPaths1(texts);
            case PATHS2 -> setPaths2(texts);
            default -> throw new IllegalArgumentException("Unsupported text type " + type);
        }
    }

    /**
     * Headlines.
     */
    public List<E> getHeadlines() {
        return streamHeadlines().toList();
    }

    /**
     * Headlines.
     */
    public abstract Stream<E> streamHeadlines();

    /**
     * Texts of headlines.
     */
    public List<String> getHeadlineTexts() {
        return streamHeadlines()
                .map(E::getText)
                .toList();
    }

    /**
     * Pins of headlines.
     */
    public List<Integer> getHeadlinePins() {
        return streamHeadlines()
                .map(E::getPin)
                .toList();
    }

    /**
     * Headlines.
     */
    public abstract void setHeadlines(List<E> headlines);

    /**
     * Descriptions.
     */
    public List<E> getDescriptions() {
        return streamDescriptions().toList();
    }

    /**
     * Descriptions.
     */
    public abstract Stream<E> streamDescriptions();

    /**
     * Texts of descriptions.
     */
    public List<String> getDescriptionTexts() {
        return streamDescriptions()
                .map(E::getText)
                .toList();
    }

    /**
     * Pins of descriptions.
     */
    public List<Integer> getDescriptionPins() {
        return streamDescriptions()
                .map(E::getPin)
                .toList();
    }

    /**
     * Descriptions.
     */
    public abstract void setDescriptions(List<E> descriptions);

    /**
     * Paths 1.
     */
    private List<E> getPaths1() {
        return streamPaths1().toList();
    }

    /**
     * Paths 1.
     */
    public abstract Stream<E> streamPaths1();

    /**
     * Path 1.
     */
    public E getPath1() {
        return streamPaths1()
                .findFirst()
                .orElse(null);
    }

    /**
     * Text for path 1.
     */
    public String getPath1Text() {
        return streamPaths1()
                .map(E::getText)
                .findFirst()
                .orElse(null);
    }

    /**
     * Path 1.
     */
    public abstract void setPath1(E path1);

    /**
     * Paths 1.
     */
    protected abstract void setPaths1(List<E> paths1);

    /**
     * Paths 2.
     */
    private List<E> getPaths2() {
        return streamPaths2().toList();
    }

    /**
     * Paths 2.
     */
    public abstract Stream<E> streamPaths2();

    /**
     * Path 2.
     */
    public E getPath2() {
        return streamPaths2()
                .findFirst()
                .orElse(null);
    }

    /**
     * Text for path 2.
     */
    public String getPath2Text() {
        return streamPaths2()
                .map(E::getText)
                .findFirst()
                .orElse(null);
    }

    /**
     * Path 2.
     */
    public abstract void setPath2(E path2);

    /**
     * Texts for paths 2.
     */
    protected abstract void setPaths2(List<E> paths2);

    /**
     * URL.
     */
    @Override
    public String getUrl() {
        return url;
    }

    /**
     * Set URL.
     */
    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * URL with Google Ads tracking parameters.
     */
    public String getGoogleAdsUrl() {
        return googleAdsUrl;
    }

    /**
     * URL with Google Ads tracking parameters.
     */
    public void setGoogleAdsUrl(String googleAdsUrl) {
        this.googleAdsUrl = googleAdsUrl;
    }

    /**
     * URL with Bing Ads tracking parameters.
     */
    @Override
    public String getBingAdsUrl() {
        return bingAdsUrl;
    }

    /**
     * URL with Bing Ads tracking parameters.
     */
    @Override
    public void setBingAdsUrl(String bingAdsUrl) {
        this.bingAdsUrl = bingAdsUrl;
    }

    /**
     * Google Ads text ID.
     */
    public Long getGoogleAdsId() {
        return googleAdsId;
    }

    /**
     * Set Google Ads text ID.
     */
    public void setGoogleAdsId(Long googleAdsId) {
        this.googleAdsId = googleAdsId;
    }

    /**
     * Bing Ads text id.
     */
    public Long getBingAdsId() {
        return bingAdsId;
    }

    /**
     * Set Bing Ads text id.
     */
    public void setBingAdsId(Long bingAdsId) {
        this.bingAdsId = bingAdsId;
    }

    /**
     * The last update to Google Ads failed.
     */
    public boolean isGoogleAdsUpdateFailed() {
        return googleAdsUpdateFailed;
    }

    /**
     * The last update to Google Ads failed.
     */
    public void googleAdsUpdateFailed() {
        this.googleAdsUpdateFailed = true;
    }

    /**
     * The last update to Bing Ads failed.
     */
    public boolean isBingAdsUpdateFailed() {
        return bingAdsUpdateFailed;
    }

    /**
     * The last update to Bing Ads failed.
     */
    public void bingAdsUpdateFailed() {
        this.bingAdsUpdateFailed = true;
    }

    /**
     * Reset update failure flags.
     */
    public void resetUpdateFailed() {
        this.googleAdsUpdateFailed = false;
        this.bingAdsUpdateFailed = false;
    }

    @Override
    public LocalGroup getGroup() {
        return group;
    }

    /**
     * Set group containing this text.
     */
    void setGroup(LocalGroup group) {
        this.group = group;
    }

    /**
     * Check, if this text has the same text as the given text.
     */
    public boolean isSameText(AbstractLocalResponsiveText<?, ?> text) {
        return getHeadlineTexts().equals(text.getHeadlineTexts()) &&
               getDescriptionTexts().equals(text.getDescriptionTexts()) &&
               Objects.equals(getPath1Text(), text.getPath1Text()) &&
               Objects.equals(getPath2Text(), text.getPath2Text());
    }
}
