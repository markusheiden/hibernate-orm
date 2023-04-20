package org.hibernate.sql.results.graph.entity;

import java.time.LocalDate;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;

/**
 * Base class for local representation of Google Ads / Bing Ads objects.
 */
@MappedSuperclass
public abstract class AbstractLocal<AWI, BAI> implements Local<AWI, BAI> {
    /**
     * Creation date in Google Ads.
     */
    @Column(name = "google_ads_since", nullable = true)
    private LocalDate googleAdsSince;

    /**
     * Error messages of last booking attempt.
     * These errors will NOT be persisted.
     */
    @Transient
    private Set<String> errors = null;

    @Override
    public LocalDate getGoogleAdsSince() {
        return googleAdsSince;
    }

    @Override
    public void setGoogleAdsSince(LocalDate googleAdsSince) {
        this.googleAdsSince = googleAdsSince;
    }

    @Override
    public final boolean isBookedInGoogleAds() {
        return getGoogleAdsId() != null;
    }

    @Override
    public final boolean isNotBookedInGoogleAds() {
        return getGoogleAdsId() == null;
    }

    @Override
    public void resetGoogleAdsId() {
        setGoogleAdsId(null);
    }

    @Override
    public final boolean isBookedInBingAds() {
        return getBingAdsId() != null;
    }

    @Override
    public final boolean isNotBookedInBingAds() {
        return getBingAdsId() == null;
    }

    @Override
    public void resetBingAdsId() {
        setBingAdsId(null);
    }
}
