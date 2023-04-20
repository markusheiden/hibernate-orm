package org.hibernate.sql.results.graph.entity;

/**
 * Local with URL.
 */
public interface WithUrl {
    /**
     * URL.
     */
    String getUrl();

    /**
     * Set URL.
     */
    void setUrl(String url);

    /**
     * URL with Google Ads tracking parameters.
     */
    String getGoogleAdsUrl();

    /**
     * URL with Google Ads tracking parameters.
     */
    void setGoogleAdsUrl(String googleAdsUrl);

    /**
     * URL with Bing Ads tracking parameters.
     */
    String getBingAdsUrl();

    /**
     * URL with Google Ads tracking parameters.
     */
    void setBingAdsUrl(String bingAdsUrl);
}
