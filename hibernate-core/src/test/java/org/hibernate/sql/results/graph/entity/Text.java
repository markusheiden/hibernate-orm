package org.hibernate.sql.results.graph.entity;

/**
 * Common interface for generated texts.
 */
public interface Text<T extends Text<T>> extends GroupedLocal, WithUrl {
    /**
     * @return true if a keyword is booked into Google Ads.
     */
    boolean isBookedInGoogleAds();

    /**
     * Google Ads text ad id.
     */
    Long getGoogleAdsId();

    /**
     * Is text enabled?.
     */
    default boolean isEnabled() {
        return !isPaused();
    }

    /**
     * Has text been paused?.
     */
    boolean isPaused();

    /**
     * Has text been paused?.
     */
    void setPaused(boolean paused);
}
