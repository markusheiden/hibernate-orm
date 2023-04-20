package org.hibernate.sql.results.graph.entity;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.CascadeType.DETACH;
import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REFRESH;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.SEQUENCE;

/**
 * Local representation of meta ad group.
 * <p>
 * A {@link LocalMetaGroup} groups together all {@link LocalGroup}s
 * with the same {@link #entities} and {@link #unknown} of a {@link #portal}.
 * E.g. "Adidas red" in exact, "Adidas red" in phrase and "Adidas red" in broad modifier.
 * <p>
 * Unknown {@link LocalMetaGroup}s do NOT contain normalized groups.
 * If the "no phrase" feature is active, {@link LocalMetaGroup}s do NOT contain a phrase group.
 */
// The allocation size has to be the same as defined in the Postgres sequence!
@SequenceGenerator(name = "seq_local_meta_group", sequenceName = "seq_local_meta_group", allocationSize = 100)
@Entity
@Table(name = "local_meta_group")
public class LocalMetaGroup {
    /**
     * Id.
     */
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = SEQUENCE, generator = "seq_local_meta_group")
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
     * Campaign.
     */
    @ManyToOne(fetch = LAZY, cascade = { PERSIST, MERGE, REFRESH }, optional = false)
    @JoinColumn(name = "campaign_id", nullable = false)
    private LocalCampaign campaign;

    /**
     * Contains keywords with unknown words.
     */
    @Column(name = "unknwn", nullable = false)
    private boolean unknown;

    /**
     * All groups.
     * <p>
     * Needs index idx_local_group_meta_group_id for eagerly fetching groups.
     */
    @OneToMany(mappedBy = "metaGroup", cascade = ALL, fetch = LAZY)
    @MapKeyColumn(name = "match_type", nullable = false, insertable = false, updatable = false)
    private Map<MatchType, LocalGroup> groups = new EnumMap<>(MatchType.class);

    /**
     * Hidden default constructor for Hibernate.
     */
    protected LocalMetaGroup() {}

    /**
     * Constructor.
     */
    public LocalMetaGroup(LocalCampaign campaign, boolean unknown) {
        this.unknown = unknown;
        this.campaign = campaign;
        this.account = campaign.getAccount();
        this.portal = account.getPortal();
    }

    /**
     * Database id.
     */
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
     * Campaign.
     */
    public LocalCampaign getCampaign() {
        return campaign;
    }

    /**
     * Check, if this group is empty and can be safely deleted.
     * <p>
     * This is the case, if there are no keywords or there are only normalized keywords left.
     */
    public boolean isEmpty() {
        return streamKeywords().allMatch(LocalKeyword::isNormalized);
    }

    /**
     * Check, if no group is manually paused.
     * <p>
     * Empty manually paused groups won't be deleted, even if not booked into Google Ads.
     * In this case they are a marker to never book this groups (entities) again.
     * <p>
     * TODO markus 2018-10-21: If just some groups are manually paused, pause the remaining ones too?
     */
    public boolean isNotManualPaused() {
        return streamGroups().allMatch(LocalGroup::isNotManualPaused);
    }

    /**
     * Has no group of this meta group been booked into Google Ads?
     * <p>
     * Meta groups are considered as booked into Google Ads, if at least one grouped has been booked in Google Ads.
     * This is because all groups should be kept or none at all and we don't delete booked groups.
     */
    public boolean isNotBookedInGoogleAds() {
        return streamGroups().allMatch(LocalGroup::isNotBookedInGoogleAds);
    }

    /**
     * Add new group.
     *
     * @param matchType
     *         Match type.
     * @param name
     *         Name.
     * @return Created group.
     * @deprecated This mutator is public just for fixing inconsistent meta groups in the database!
     */
    @Deprecated
    public LocalGroup addGroup(MatchType matchType, String name) {
        var group = new LocalGroup(this, matchType, name);
        groups.put(matchType, group);

        return group;
    }

    /**
     * Removes group from relations of this meta group.
     *
     * @param group
     *         Group to remove.
     */
    void removeGroup(LocalGroup group) {
        this.groups.remove(group.getMatchType());
    }

    /**
     * All groups.
     */
    public Collection<LocalGroup> getGroups() {
        return List.copyOf(groups.values());
    }

    /**
     * All groups.
     */
    public Stream<LocalGroup> streamGroups() {
        return groups.values().stream();
    }

    /**
     * Group for the given match type.
     */
    public LocalGroup getGroup(MatchType matchType) {
        return groups.get(matchType);
    }

    /**
     * All keyword texts. Use only keyword texts of not normalized groups, because that are the "original" keywords.
     */
    private Stream<String> streamKeywordTexts() {
        return streamGroups()
                .filter(LocalGroup::isNotNormalized)
                .flatMap(LocalGroup::streamKeywords)
                .map(LocalKeyword::getText)
                .distinct();
    }

    /**
     * All keywords of all groups.
     */
    public List<LocalKeyword> getKeywords() {
        return streamKeywords()
                .toList();
    }

    /**
     * All keywords of all groups.
     */
    public Stream<LocalKeyword> streamKeywords() {
        return groups.values().stream()
                .flatMap(LocalGroup::streamKeywords);
    }

    /**
     * Get the one and only normalized keyword of the given match type.
     * <p>
     * If there are multiple normalized keywords (due to data inconsistencies),
     * the most recent is returned, because it should be the correct one.
     */
    public LocalKeyword getNormalizedKeyword(MatchType matchType) {
        var group = getGroup(matchType);
        if (group == null) {
            return null;
        }
        return group.getNormalizedKeyword();
    }

    /**
     * Does this meta group contains only keywords with known words?.
     */
    public boolean isKnown() {
        return !unknown;
    }

    /**
     * Does this meta group contains keywords with unknown words?.
     */
    public boolean isUnknown() {
        return unknown;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof LocalMetaGroup metaGroup &&
               getCampaign().equals(metaGroup.getCampaign()) &&
               isUnknown() == metaGroup.isUnknown();
    }

    @Override
    public int hashCode() {
        return Boolean.hashCode(unknown);
    }
}
