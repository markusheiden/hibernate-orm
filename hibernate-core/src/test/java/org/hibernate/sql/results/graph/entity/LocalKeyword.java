package org.hibernate.sql.results.graph.entity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import org.hibernate.annotations.Fetch;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REFRESH;
import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.SEQUENCE;
import static org.hibernate.annotations.FetchMode.SELECT;

/**
 * Local representation of Google Ads keyword.
 */
// The allocation size has to be the same as defined in the Postgres sequence!
@SequenceGenerator(name = "seq_local_keyword", sequenceName = "seq_local_keyword", allocationSize = 1000)
@Entity
@Table(name = "local_keyword")
public class LocalKeyword extends AbstractLocalKeyword {
    /**
     * Maximum URL length.
     */
    private static int MAX_URL_LENGTH = 2047;

    /**
     * Maximum Google Ads URL length.
     *
     * @see <a href="https://developers.google.com/google-ads/api/docs/best-practices/system-limits#criterion">Limits</a>
     */
    private static int MAX_GOOGLEADS_URL_LENGTH = 2047;

    /**
     * Maximum Bing Ads URL length.
     *
     * @see <a href="https://docs.microsoft.com/en-us/advertising/guides/entity-hierarchy-limits?view=bingads-13#keyword">Entity Limits</a>
     */
    private static int MAX_BINGADS_URL_LENGTH = 2048;

    /**
     * Id.
     */
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = SEQUENCE, generator = "seq_local_keyword")
    @SuppressWarnings("UnusedDeclaration")
    private Long id;

    /**
     * Keyword text.
     */
    @Column(name = "keyword", nullable = false)
    private String text;

    /**
     * Match type.
     */
    @Column(name = "match_type", nullable = false)
    private MatchType matchType;

    /**
     * URL.
     */
    @Column(name = "url", nullable = false)
    private String url;

    /**
     * URL with Google Ads tracking parameters.
     */
    @Column(name = "google_ads_url", nullable = true)
    private String googleAdsUrl;

    /**
     * Google Ads tracking parameters.
     */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "tracking1Name", column = @Column(name = "google_ads_tracking_1_name")),
            @AttributeOverride(name = "tracking1Value", column = @Column(name = "google_ads_tracking_1_value")),
            @AttributeOverride(name = "tracking2Name", column = @Column(name = "google_ads_tracking_2_name")),
            @AttributeOverride(name = "tracking2Value", column = @Column(name = "google_ads_tracking_2_value")),
            @AttributeOverride(name = "tracking3Name", column = @Column(name = "google_ads_tracking_3_name")),
            @AttributeOverride(name = "tracking3Value", column = @Column(name = "google_ads_tracking_3_value")),
    })
    private TrackingParameters googleAdsTrackingParameters;

    /**
     * URL with Bing Ads tracking parameters.
     */
    @Column(name = "bingads_url", nullable = true)
    private String bingAdsUrl;

    /**
     * Bing Ads tracking parameters.
     */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "tracking1Name", column = @Column(name = "bingads_tracking_1_name")),
            @AttributeOverride(name = "tracking1Value", column = @Column(name = "bingads_tracking_1_value")),
            @AttributeOverride(name = "tracking2Name", column = @Column(name = "bingads_tracking_2_name")),
            @AttributeOverride(name = "tracking2Value", column = @Column(name = "bingads_tracking_2_value")),
            @AttributeOverride(name = "tracking3Name", column = @Column(name = "bingads_tracking_3_name")),
            @AttributeOverride(name = "tracking3Value", column = @Column(name = "bingads_tracking_3_value")),
    })
    private TrackingParameters bingAdsTrackingParameters;

    /**
     * Google Ads max. CPC.
     */
    @Column(name = "google_ads_max_cpc", nullable = false)
    private Double googleAdsMaxCPC;

    /**
     * Bing Ads max. CPC.
     */
    @Column(name = "bingads_max_cpc", nullable = false)
    private Double bingAdsMaxCPC;

    /**
     * The keyword has low search volume.
     */
    @Column(name = "low_search_volume", nullable = false)
    private boolean lowSearchVolume;

    /**
     * When has the keyword been detected as (no) low search volume?.
     */
    @Column(name = "low_search_volume_since", nullable = true)
    private LocalDate lowSearchVolumeSince;

    /**
     * Google Ads keyword ID.
     */
    @Column(name = "google_ads_id", nullable = true)
    private Long googleAdsId;

    /**
     * Bing Ads keyword ID.
     */
    @Column(name = "bingads_id", nullable = true)
    private Long bingAdsId;

    /**
     * The keyword been normalized.
     */
    @Column(name = "normalized", nullable = false)
    private boolean normalized;

    /**
     * The date when this keyword got the current static url.
     */
    @Column(name = "static_url_created")
    private Instant staticUrlCreated;

    /**
     * The keyword has manually been labeled as static URL.
     * This indicates that adSoul should not update the url at any time.
     */
    @Column(name = "static_url", nullable = false)
    private boolean staticUrl;

    /**
     * Negative for this keyword in the "broader" group, optional.
     * <p>
     * The negative "belongs" to this keyword. So cascade delete / remove orphans here.
     */
    @OneToOne(fetch = LAZY, orphanRemoval = true, cascade = ALL, optional = true)
    @Fetch(SELECT)
    @JoinColumn(name = "negative_id", nullable = true)
    private LocalNegative negative;

    /**
     * Group containing this keyword.
     */
    @ManyToOne(fetch = EAGER, cascade = { PERSIST, MERGE, REFRESH }, optional = false)
    @Fetch(SELECT)
    @JoinColumn(name = "group_id", nullable = false)
    private LocalGroup group;

    /**
     * Hidden default constructor for Hibernate.
     */
    protected LocalKeyword() {}

    /**
     * Constructor.
     *
     * @param text
     *         Lower cased keyword.
     * @param googleAdsMaxCPC
     *         Initial Google Ads maximum cpc.
     * @param bingAdsMaxCPC
     *         Initial Bing Ads maximum cpc.
     */
    public LocalKeyword(String text, MatchType matchType, String url,
            Map<String, String> googleAdsTracking, String googleAdsUrl, Map<String, String> bingAdsTracking, String bingAdsUrl,
            Double googleAdsMaxCPC, Double bingAdsMaxCPC) {
        this.text = text;
        this.matchType = matchType;
        this.url = url;
        this.googleAdsUrl = googleAdsUrl;
        this.googleAdsTrackingParameters = new TrackingParameters(googleAdsTracking);
        // May be null if the Bing Ads feature is not active.
        this.bingAdsUrl = bingAdsUrl;
        this.bingAdsTrackingParameters = new TrackingParameters(bingAdsTracking);
        this.googleAdsMaxCPC = googleAdsMaxCPC;
        this.bingAdsMaxCPC = bingAdsMaxCPC;
        this.normalized = matchType.isNormalized();
    }

    @Override
    public Long getId() {
        return id;
    }

    /**
     * Keyword text.
     */
    public String getText() {
        return text;
    }

    /**
     * Does this keyword contain only known words?.
     */
    public boolean isKnown() {
        return group.isKnown();
    }

    /**
     * Does this keyword contain any unknown words?.
     */
    public boolean isUnknown() {
        return group.isUnknown();
    }

    /**
     * Match type.
     */
    @Override
    public MatchType getMatchType() {
        return matchType;
    }

    /**
     * Has the keyword been manually labeled with 'static url'?
     * This indicates that adSoul should not update the url at any time.
     */
    public boolean isStaticUrl() {
        return staticUrl;
    }

    /**
     * Date the static URL was last set (created/updated).
     */
    public Instant getStaticUrlCreated() {
        return staticUrlCreated;
    }

    /**
     * Has the keyword been manually labeled with 'static url'?
     * This indicates that adSoul should not update the url at any time.
     */
    public void setStaticUrl(boolean staticUrl) {
        this.staticUrl = staticUrl;
    }

    /**
     * URL without tracking parameters.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Set URL without tracking parameters.
     * Updates the {@link #staticUrlCreated} field. Thus, it is important to set the {@link #staticUrl} flag first.
     */
    public void setUrl(String url) {
        if (!staticUrl) {
            staticUrlCreated = null;
        } else if (staticUrlCreated == null || !this.url.equals(url)) {
            staticUrlCreated = Instant.now();
        }
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
     *
     * @param googleAdsUrl
     *         New URL.
     */
    public void setGoogleAdsUrl(String googleAdsUrl) {
        this.googleAdsUrl = googleAdsUrl;
    }

    /**
     * Google Ads tracking parameters.
     */
    public TrackingParameters getGoogleAdsTrackingParameters() {
        if (googleAdsTrackingParameters == null) {
            googleAdsTrackingParameters = new TrackingParameters();
        }
        return googleAdsTrackingParameters;
    }

    /**
     * URL with Bing Ads tracking parameters.
     */
    public String getBingAdsUrl() {
        return bingAdsUrl;
    }

    /**
     * URL with Bing Ads tracking parameters.
     *
     * @param bingAdsUrl
     *         New URL.
     */
    public void setBingAdsUrl(String bingAdsUrl) {
        this.bingAdsUrl = bingAdsUrl;
    }

    /**
     * Bing ads tracking parameters.
     */
    public TrackingParameters getBingAdsTrackingParameters() {
        if (bingAdsTrackingParameters == null) {
            bingAdsTrackingParameters = new TrackingParameters();
        }
        return bingAdsTrackingParameters;
    }

    /**
     * Google Ads Max. CPC.
     */
    public Double getGoogleAdsMaxCPC() {
        return googleAdsMaxCPC;
    }

    /**
     * Set Google Ads max. CPC.
     */
    public void setGoogleAdsMaxCPC(Double googleAdsMaxCPC) {
        this.googleAdsMaxCPC = googleAdsMaxCPC;
    }

    /**
     * Bing Ads Max. CPC.
     */
    public Double getBingAdsMaxCPC() {
        return bingAdsMaxCPC;
    }

    /**
     * Set Bing Ads max. CPC.
     */
    public void setBingAdsMaxCPC(Double bingAdsMaxCPC) {
        this.bingAdsMaxCPC = bingAdsMaxCPC;
    }

    /**
     * Is the keyword low search volume?.
     */
    public boolean isLowSearchVolume() {
        return lowSearchVolume;
    }

    /**
     * When has the keyword been detected as (no) low search volume?.
     *
     * @return date or {@code null}, if not initially retrieved from Google Ads.
     */
    public LocalDate getLowSearchVolumeSince() {
        return lowSearchVolumeSince;
    }

    /**
     * Set the keyword as low search volume.
     */
    public void setLowSearchVolume(boolean lowSearchVolume, LocalDate since) {
        // Record just the date of change!
        if (this.lowSearchVolumeSince == null || this.lowSearchVolume != lowSearchVolume) {
            this.lowSearchVolumeSince = since;
        }
        this.lowSearchVolume = lowSearchVolume;
    }

    /**
     * Negative for this keyword in the "broader" group.
     */
    public LocalNegative getNegative() {
        return negative;
    }

    /**
     * Set negative for this keyword in the "broader" group.
     * Ignores attempts to overwrite an already set negative.
     *
     * @return Has a new negative been set?
     */
    public boolean setNegative(LocalNegative negative) {
        var oldKeyword = negative.getKeyword();
        boolean set = this.negative == null;
        this.negative = negative;
        negative.setKeyword(this);

        return set;
    }

    /**
     * Remove negative. Does NOT remove the negative from its group.
     *
     * @return Removed negative.
     */
    LocalNegative removeNegative() {
        var removed = negative;
        if (removed != null) {
            negative = null;
            removed.setKeyword(null);
        }

        return removed;
    }

    /**
     * Google Ads keyword ID.
     */
    @Override
    public Long getGoogleAdsId() {
        return googleAdsId;
    }

    /**
     * Set Google Ads keyword ID.
     */
    @Override
    public void setGoogleAdsId(Long googleAdsId) {
        this.googleAdsId = googleAdsId;
    }

    /**
     * Bing Ads keyword ID.
     */
    @Override
    public Long getBingAdsId() {
        return bingAdsId;
    }

    /**
     * Set Bing Ads keyword ID.
     */
    @Override
    public void setBingAdsId(Long bingAdsId) {
        this.bingAdsId = bingAdsId;
    }

    /**
     * Has the keyword been normalized?
     */
    public boolean isNormalized() {
        return normalized;
    }

    /**
     * Has the keyword not been normalized?
     */
    public boolean isNotNormalized() {
        return !normalized;
    }

    /**
     * Has the keyword been normalized?
     */
    @Deprecated // Use only for deprecated tests which require the normalized flag to be inconsistent with match type.
    public void setNormalized(boolean normalized) {
        this.normalized = normalized;
    }

    /**
     * Group containing this keyword.
     */
    @Override
    public LocalGroup getGroup() {
        return group;
    }

    /**
     * Set group containing this keyword.
     */
    void setGroup(LocalGroup group) {
        this.group = group;
    }

    /**
     * Remove this keyword from its group and negative. The negative will be removed too.
     */
    @Override
    public void remove() {
        if (group != null) {
            group.removeKeyword(this);
        }
        if (negative != null) {
            // This will reset the negative in this keyword too.
            negative.remove();
        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof LocalKeyword keyword &&
               Objects.equals(getGroup(), keyword.getGroup()) &&
               isSameContent(keyword);
    }

    /**
     * Check, if this keyword has the same content (match type and keyword text) as the given keyword.
     */
    public boolean isSameContent(LocalKeyword o) {
        return getText().equals(o.getText()) &&
               getMatchType().equals(o.getMatchType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, matchType);
    }
}
