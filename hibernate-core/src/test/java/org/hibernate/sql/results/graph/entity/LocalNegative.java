package org.hibernate.sql.results.graph.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import org.hibernate.annotations.Fetch;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REFRESH;
import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.SEQUENCE;
import static org.hibernate.annotations.FetchMode.SELECT;

/**
 * Local representation of Google Ads negative keyword.
 */
// The allocation size has to be the same as defined in the Postgres sequence!
@SequenceGenerator(name = "seq_local_negative", sequenceName = "seq_local_negative", allocationSize = 1000)
@Entity
@Table(name = "local_negative")
public class LocalNegative extends AbstractLocalKeyword {
    /**
     * Id.
     */
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = SEQUENCE, generator = "seq_local_negative")
    @SuppressWarnings("UnusedDeclaration")
    private Long id;

    /**
     * Negative text.
     */
    @Column(name = "keyword", nullable = false)
    private String text;

    /**
     * Match type.
     */
    @Column(name = "match_type", nullable = false)
    private MatchType matchType;

    /**
     * Google Ads negative ID.
     */
    @Column(name = "google_ads_id", nullable = true)
    private Long googleAdsId;

    /**
     * Bing Ads negative ID.
     */
    @Column(name = "bingads_id", nullable = true)
    private Long bingAdsId;

    /**
     * Keyword which belongs this negative.
     */
    @OneToOne(mappedBy = "negative", fetch = LAZY, cascade = { PERSIST, MERGE, REFRESH }, optional = true)
    @Fetch(SELECT)
    private LocalKeyword keyword;

    /**
     * Group containing this negative.
     */
    @ManyToOne(fetch = EAGER, cascade = { PERSIST, MERGE, REFRESH }, optional = true)
    @Fetch(SELECT)
    @JoinColumn(name = "group_id", nullable = true)
    private LocalGroup group;

    /**
     * Hidden default constructor for Hibernate.
     */
    protected LocalNegative() {}

    /**
     * Constructor.
     *
     * @param text
     *         Lower cased negative text.
     * @param matchType
     *         Match type.
     */
    public LocalNegative(String text, MatchType matchType) {
        this.text = text;
        this.matchType = matchType;
    }

    /**
     * Id.
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * Negative text.
     */
    public String getText() {
        return text;
    }

    /**
     * Match type.
     */
    @Override
    public MatchType getMatchType() {
        return matchType;
    }

    /**
     * Google Ads account id.
     */
    public Long getGoogleAdsAccountId() {
        return group.getAccount().getGoogleAdsId();
    }

    /**
     * Google Ads group id.
     */
    public Long getGoogleAdsGroupId() {
        return group.getGoogleAdsId();
    }

    /**
     * Google Ads negative ID.
     */
    @Override
    public Long getGoogleAdsId() {
        return googleAdsId;
    }

    /**
     * Set Google Ads negative ID.
     */
    @Override
    public void setGoogleAdsId(Long googleAdsId) {
        this.googleAdsId = googleAdsId;
    }

    /**
     * Bing Ads negative id.
     */
    @Override
    public Long getBingAdsId() {
        return bingAdsId;
    }

    /**
     * Set Bing Ads negative id.
     */
    @Override
    public void setBingAdsId(Long bingAdsId) {
        this.bingAdsId = bingAdsId;
    }

    /**
     * Keyword which belongs this negative.
     */
    public LocalKeyword getKeyword() {
        return keyword;
    }

    /**
     * Set keyword which belongs this negative.
     */
    void setKeyword(LocalKeyword keyword) {
        this.keyword = keyword;
    }

    /**
     * Group containing this negative.
     */
    @Override
    public LocalGroup getGroup() {
        return group;
    }

    /**
     * Set group containing this negative.
     */
    void setGroup(LocalGroup group) {
        this.group = group;
    }

    /**
     * Remove this negative from its group and keyword.
     */
    @Override
    public void remove() {
        if (group != null) {
            group.removeNegative(this);
        }
        if (keyword != null) {
            // This will reset the keyword in this negative too.
            keyword.removeNegative();
        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof LocalNegative negative &&
               getGroup().equals(negative.getGroup()) &&
               getText().equals(negative.getText()) &&
               getMatchType().equals(negative.getMatchType());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
