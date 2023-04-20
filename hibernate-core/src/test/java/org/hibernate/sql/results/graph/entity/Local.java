package org.hibernate.sql.results.graph.entity;

import java.time.LocalDate;

public interface Local<AWI, BAI> {
    /**
     * Database id.
     */
    Long getId();

    /**
     * Creation date in Google Ads.
     */
    LocalDate getGoogleAdsSince();

    /**
     * Creation date in Google Ads.
     */
    void setGoogleAdsSince(LocalDate googleAdsSince);

    /**
     * Local is booked into Google Ads.
     */
    boolean isBookedInGoogleAds();

    /**
     * Local is NOT booked into Google Ads.
     */
    boolean isNotBookedInGoogleAds();

    /**
     * Google Ads ID.
     */
    AWI getGoogleAdsId();

    /**
     * Google Ads ID.
     */
    void setGoogleAdsId(AWI googleAdsId);

    /**
     * Set Google Ads ID to {@code null}.
     */
    void resetGoogleAdsId();

    /**
     * Local is booked into Bing Ads.
     */
    boolean isBookedInBingAds();

    /**
     * Local is NOT booked into Bing Ads.
     */
    boolean isNotBookedInBingAds();

    /**
     * Bing Ads id.
     */
    BAI getBingAdsId();

    /**
     * Bing Ads id.
     */
    void setBingAdsId(BAI bingAdsId);

    /**
     * Set Bing Ads id to {@code null}.
     */
    void resetBingAdsId();
}
