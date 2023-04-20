package org.hibernate.sql.results.graph.entity;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import static jakarta.persistence.CascadeType.DETACH;
import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REFRESH;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.SEQUENCE;
import static java.util.Collections.unmodifiableSet;

/**
 * Local representation of Google Ads campaign.
 */
// The allocation size has to be the same as defined in the Postgres sequence!
@SequenceGenerator(name = "seq_local_campaign", sequenceName = "seq_local_campaign", allocationSize = 100)
@jakarta.persistence.Entity
@Table(name = "local_campaign")
public class LocalCampaign extends AbstractLocal<Long, Long> implements NamedLocal<Long, Long> {
    /**
     * Id.
     */
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = SEQUENCE, generator = "seq_local_campaign")
    private Long id;

    /**
     * Portal.
     * Do NOT cascade, because portal may be a cached one.
     */
    @ManyToOne(fetch = LAZY, cascade = DETACH, optional = false)
    @JoinColumn(name = "portal_id", nullable = false)
    // Do not reattach portal, if account is reattached, to avoid attaching the portal to different sessions.
    private Portal portal;

    /**
     * Account.
     */
    @ManyToOne(fetch = LAZY, cascade = { PERSIST, MERGE, REFRESH }, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private LocalAccount account;

    /**
     * Domain driven id of campaign. Thread-safe, because it is immutable.
     */
    @Transient
    private Long localId;

    /**
     * Is the campaign for trademarks keywords?.
     */
    @Column(name = "trademark", nullable = false)
    private boolean trademark;

    /**
     * Campaign name.
     */
    @Column(name = "name", nullable = true)
    private String name;

    /**
     * Google Ads campaign ID.
     */
    @Column(name = "google_ads_id", nullable = true)
    private Long googleAdsId;

    /**
     * Google Ads budget ID.
     */
    // TODO hendrik 2021-11-04: Remove budget id because we create the campaign in one step now
    @Column(name = "google_ads_budget_id", nullable = true)
    private Long googleAdsBudgetId;

    /**
     * Bing Ads campaign id.
     */
    @Column(name = "bingads_id", nullable = true)
    private Long bingAdsId;

    /**
     * Paused manually.
     */
    @Column(name = "manual_paused", nullable = false)
    private boolean manualPaused;

    /**
     * Bing Ads criterion IDs.
     */
    @ElementCollection(fetch = LAZY)
    @CollectionTable(name = "local_campaign_bingads_criterion", joinColumns = @JoinColumn(name = "campaign_id", nullable = false))
    @Column(name = "id", nullable = false)
    private Set<Long> bingAdsCriterionIds = new HashSet<>();

    /**
     * Budget.
     */
    @Column(name = "budget", nullable = false)
    private Double budget;

    /**
     * Percentage for mobile bids.
     */
    @Column(name = "mobile_modifier", nullable = false)
    private int mobileModifier;

    /**
     * Percentage for tablet bids.
     */
    @Column(name = "tablet_modifier", nullable = false)
    private int tabletModifier;

    /**
     * Percentage for desktop bids.
     */
    @Column(name = "desktop_modifier", nullable = false)
    private int desktopModifier;

    /**
     * Targeted languages.
     */
    @ElementCollection(fetch = LAZY)
    @CollectionTable(name = "local_campaign_language", joinColumns = @JoinColumn(name = "campaign_id", nullable = false))
    @Column(name = "language", nullable = false)
    private Set<Locale> languages;

    /**
     * Areas of interest.
     */
    @ElementCollection(fetch = LAZY)
    @CollectionTable(name = "local_campaign_area", joinColumns = @JoinColumn(name = "campaign_id", nullable = false))
    @Column(name = "location", nullable = false)
    private Set<Locale> areasOfInterest;

    /**
     * Activate enhanced CPC as campaign setting for auto optimized bidding by google.
     */
    @Column(name = "enhanced_cpc", nullable = false)
    private boolean enhancedCpc;

    /**
     * Spend your daily budget more quickly?
     */
    @Column(name = "accelerated_delivery", nullable = false)
    private boolean acceleratedDelivery;

    /**
     * Hidden default constructor for Hibernate.
     */
    protected LocalCampaign() {}

    /**
     * Constructor.
     *
     * @param account
     *         Account.
     * @param trademark
     *         Is the campaign for trademarks keywords?.
     * @param name
     *         Campaign name.
     */
    public LocalCampaign(LocalAccount account, boolean trademark, String name) {
        this.trademark = trademark;
        this.name = name;
        this.account = account;
        this.portal = account.getPortal();

        // Copy campaign config from portal.
        this.budget = 0.0;
        this.mobileModifier = 0;
        this.desktopModifier = 0;
        this.tabletModifier = 0;
        this.enhancedCpc = false;
        this.acceleratedDelivery = false;
        this.languages = new HashSet<>();
        this.areasOfInterest = new HashSet<>();
    }

    @Override
    public Long getId() {
        return id;
    }

    /**
     * Portal.
     */
    public Portal getPortal() {
        return portal;
    }

    /**
     * Account.
     */
    public LocalAccount getAccount() {
        return account;
    }

    /**
     * Is the campaign for trademarks keywords?.
     */
    public boolean isTrademark() {
        return trademark;
    }

    @Override
    public Long getGoogleAdsId() {
        return googleAdsId;
    }

    @Override
    public void setGoogleAdsId(Long googleAdsId) {
        this.googleAdsId = googleAdsId;
    }

    @Override
    public void resetGoogleAdsId() {
        resetGoogleAdsBudgetId();
        super.resetGoogleAdsId();
    }

    /**
     * Google Ads budget ID.
     */
    public Long getGoogleAdsBudgetId() {
        return googleAdsBudgetId;
    }

    /**
     * Set Google Ads budget ID.
     */
    public void setGoogleAdsBudgetId(Long googleAdsBudgetId) {
        this.googleAdsBudgetId = googleAdsBudgetId;
    }

    /**
     * Reset Google Ads budget ID.
     */
    public void resetGoogleAdsBudgetId() {
        this.googleAdsBudgetId = null;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Set Google Ads campaign name.
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Long getBingAdsId() {
        return bingAdsId;
    }

    @Override
    public void setBingAdsId(Long bingAdsId) {
        this.bingAdsId = bingAdsId;
    }

    /**
     * Has the group been manually paused?.
     */
    public boolean isManualPaused() {
        return manualPaused;
    }

    /**
     * Has the group not been manually paused?.
     */
    public boolean isNotManualPaused() {
        return !manualPaused;
    }

    /**
     * Set manually pausing of group?.
     */
    public void setManualPaused(boolean manualPaused) {
        this.manualPaused = manualPaused;
    }

    /**
     * Bing Ads criterion IDs.
     */
    public Set<Long> getBingAdsCriterionIds() {
        return bingAdsCriterionIds;
    }

    /**
     * Budget.
     */
    public Double getBudget() {
        return budget;
    }

    /**
     * Set budget.
     */
    public void setBudget(Double budget) {
        this.budget = budget;
    }

    /**
     * Percentage for mobile bids.
     */
    public int getMobileModifier() {
        return mobileModifier;
    }

    /**
     * Percentage for Tablet bids.
     */
    public int getTabletModifier() {
        return tabletModifier;
    }

    /**
     * Percentage for desktop bids.
     */
    public int getDesktopModifier() {
        return desktopModifier;
    }

    /**
     * Languages.
     */
    public Set<Locale> getLanguages() {
        return unmodifiableSet(languages);
    }

    /**
     * Areas of interest.
     */
    public Set<Locale> getAreasOfInterest() {
        return unmodifiableSet(areasOfInterest);
    }

    /**
     * Set enhanced CPC as campaign setting for auto optimized bidding by google.
     */
    public void setEnhancedCpc(boolean enhancedCpc) {
        this.enhancedCpc = enhancedCpc;
    }

    /**
     * Is enhanced CPC as campaign setting for auto optimized bidding by google active?
     */
    public boolean isEnhancedCpc() {
        return enhancedCpc;
    }

    /**
     * Spend your daily budget more quickly?
     */
    public boolean isAcceleratedDelivery() {
        return acceleratedDelivery;
    }

    /**
     * Spend your daily budget more quickly?
     */
    public void setAcceleratedDelivery(boolean acceleratedDelivery) {
        this.acceleratedDelivery = acceleratedDelivery;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof LocalCampaign campaign &&
               getAccount().equals(campaign.getAccount()) &&
               getName().equals(campaign.getName());
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }
}
