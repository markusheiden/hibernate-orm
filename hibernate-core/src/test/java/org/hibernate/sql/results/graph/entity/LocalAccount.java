package org.hibernate.sql.results.graph.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import static jakarta.persistence.CascadeType.DETACH;
import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.GenerationType.SEQUENCE;

/**
 * Local representation of Google Ads account.
 */
// The allocation size has to be the same as defined in the Postgres sequence!
@SequenceGenerator(name = "seq_local_account", sequenceName = "seq_local_account", allocationSize = 10)
@Entity
@Table(name = "local_account")
public class LocalAccount extends AbstractLocal<Long, Long> implements NamedLocal<Long, Long> {
    /**
     * Id.
     */
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = SEQUENCE, generator = "seq_local_account")
    private Long id;

    /**
     * Portal.
     * Do NOT cascade, because portal may be a cached one.
     */
    @ManyToOne(fetch = EAGER, cascade = DETACH, optional = false)
    @JoinColumn(name = "portal_id", nullable = false)
    // Do not reattach portal, if account is reattached, to avoid attaching the portal to different sessions.
    private Portal portal;

    /**
     * Is the account for trademarks keywords?.
     */
    @Column(name = "trademark", nullable = true)
    private Boolean trademark;

    /**
     * Description of the account.
     */
    @Column(name = "description", nullable = false)
    private String description;

    /**
     * Account name.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Google Ads account id.
     */
    @Column(name = "google_ads_id", nullable = true)
    private Long googleAdsId;

    /**
     * Bing Ads account id.
     */
    @Column(name = "bingads_id", nullable = true)
    private Long bingAdsId;

    /**
     * Hidden default constructor for Hibernate.
     */
    protected LocalAccount() {}

    /**
     * Constructor.
     *
     * @param portal
     *         Portal.
     * @param name
     *         Account name.
     */
    public LocalAccount(final Portal portal, final String name) {
        this(portal,
                true,
                "description",
                name);
    }

    /**
     * Constructor.
     *
     * @param portal
     *         Portal.
     * @param trademark
     *         Is the account for trademarks keywords?.
     * @param description
     *         Description of the account.
     * @param name
     *         Account name.
     */
    public LocalAccount(final Portal portal, final Boolean trademark, final String description, final String name) {
        this.trademark = trademark;
        this.description = description;
        this.name = name;
        this.portal = portal;
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
     * Is the account for trademarks keywords?.
     */
    public Boolean getTrademark() {
        return trademark;
    }

    /**
     * Description of the account.
     */
    public String getDescription() {
        return description;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Account name.
     */
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public Long getGoogleAdsId() {
        return googleAdsId;
    }

    @Override
    public void setGoogleAdsId(final Long googleAdsId) {
        this.googleAdsId = googleAdsId;
    }

    @Override
    public Long getBingAdsId() {
        return bingAdsId;
    }

    /**
     * Set Bing Ads account id.
     */
    @Override
    public void setBingAdsId(final Long bingAdsId) {
        this.bingAdsId = bingAdsId;
    }

    @Override
    public boolean equals(final Object o) {
        return o instanceof LocalAccount account &&
               Objects.equals(getPortal(), account.getPortal()) &&
               getName().equals(account.getName());
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
