package org.hibernate.sql.results.graph.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.PostLoad;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import org.hibernate.annotations.Fetch;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REFRESH;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.SEQUENCE;
import static java.util.Collections.unmodifiableCollection;
import static java.util.Collections.unmodifiableList;
import static java.util.Comparator.nullsLast;
import static java.util.function.Predicate.not;
import static org.hibernate.annotations.FetchMode.SELECT;

/**
 * Local representation of Google Ads ad group.
 */
// The allocation size has to be the same as defined in the Postgres sequence!
@SequenceGenerator(name = "seq_local_group", sequenceName = "seq_local_group", allocationSize = 1000)
@Entity
@Table(name = "local_group")
public class LocalGroup extends AbstractLocal<Long, Long> implements NamedLocal<Long, Long> {

    /**
     * Comparator which sorts the most recently created {@link LocalKeyword} (that with no or the highest id) first.
     */
    private static final Comparator<LocalKeyword> KEYWORD_ID_COMPARATOR = Comparator
            .comparing(LocalKeyword::getId, nullsLast(Long::compareTo))
            .reversed();

    /**
     * Id.
     */
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = SEQUENCE, generator = "seq_local_group")
    private Long id;

    /**
     * Account.
     */
    @ManyToOne(fetch = LAZY, cascade = { PERSIST, MERGE, REFRESH }, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private LocalAccount account;

    /**
     * Meta group.
     */
    @ManyToOne(cascade = { PERSIST, MERGE, REFRESH }, fetch = LAZY, optional = false)
    @Fetch(SELECT)
    @JoinColumn(name = "meta_group_id", nullable = false)
    private LocalMetaGroup metaGroup;

    /**
     * Match type of all keywords in this group.
     */
    @Column(name = "match_type", nullable = false)
    private MatchType matchType;

    /**
     * Ad group name.
     */
    @Column(name = "name", nullable = true)
    private String name;

    /**
     * Google Ads ad group ID.
     */
    @Column(name = "google_ads_id", nullable = true)
    private Long googleAdsId;

    /**
     * Bing Ads group ID.
     */
    @Column(name = "bingads_id", nullable = true)
    private Long bingAdsId;

    /**
     * Paused manually.
     */
    @Column(name = "manual_paused", nullable = false)
    private boolean manualPaused;

    /**
     * Should ad rotation be optimized by network?
     */
    @Column(name = "ad_rotation_optimized", nullable = false)
    private boolean adRotationOptimized;

    /**
     * Responsive texts.
     */
    @OneToMany(mappedBy = "group", cascade = ALL, orphanRemoval = true, fetch = LAZY)
    @OrderColumn(name = "p")
    private List<LocalResponsiveText> responsiveTexts = new ArrayList<>();

    /**
     * Manual responsive texts.
     */
    @OneToMany(mappedBy = "group", cascade = ALL, orphanRemoval = true, fetch = LAZY)
    private List<LocalManualResponsiveText> manualResponsiveTexts = new ArrayList<>();

    /**
     * All keywords (lower case).
     */
    @OneToMany(mappedBy = "group", orphanRemoval = true, cascade = ALL, fetch = LAZY)
    @MapKeyColumn(name = "keyword", nullable = false, insertable = false, updatable = false)
    private Map<String, LocalKeyword> keywords = new HashMap<>();

    /**
     * All negatives (lower case).
     * <p>
     * Negatives "belong" to their keyword. So no orphanRemoval and no cascading delete here!
     */
    @OneToMany(mappedBy = "group", cascade = { PERSIST, MERGE, REFRESH }, fetch = LAZY)
    @MapKeyColumn(name = "keyword", nullable = false, insertable = false, updatable = false)
    private Map<String, LocalNegative> negatives = new HashMap<>();

    /**
     * Hidden default constructor for Hibernate.
     */
    protected LocalGroup() {}

    /**
     * Constructor.
     */
    public LocalGroup(LocalMetaGroup metaGroup, MatchType matchType, String name) {
        this.metaGroup = metaGroup;
        this.account = metaGroup.getAccount();
        this.matchType = matchType;
        this.name = name;

        this.adRotationOptimized = false;
    }

    /**
     * Fix for inconsistent positions in the database.
     * Remove resulting {@code null} values from the responsive texts list.
     */
    // TODO markus 2023-03-10: Remove ASAP, at least after a month.
    @PostLoad
    public void removeRemovedResponsiveTexts() {
        responsiveTexts.remove(null);
    }

    /**
     * Remove this group from its meta group.
     *
     * @deprecated This mutator is public just for fixing inconsistent meta groups in the database!
     */
    @Deprecated
    public void remove() {
        // Remove keywords and their negatives from the group. Otherwise, they would be re-saved.
        List.copyOf(keywords.values()).forEach(LocalKeyword::remove);
        // Remove negatives from the group, because they do NOT "belong" to the group.
        // Otherwise, the negatives point to a deleted group -> foreign key violation.
        List.copyOf(negatives.values()).forEach(LocalNegative::remove);

        if (metaGroup != null) {
            metaGroup.removeGroup(this);
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    /**
     * Portal.
     */
    public Portal getPortal() {
        return metaGroup.getPortal();
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
        return metaGroup.getCampaign();
    }

    /**
     * Meta group.
     */
    public LocalMetaGroup getMetaGroup() {
        return metaGroup;
    }

    /**
     * Does this group contains only keywords with known words?.
     */
    public boolean isKnown() {
        return metaGroup.isKnown();
    }

    /**
     * Does this group contain keywords with unknown words?.
     */
    public boolean isUnknown() {
        return metaGroup.isUnknown();
    }

    /**
     * Match type of all keywords in this group.
     */
    public MatchType getMatchType() {
        return matchType;
    }

    /**
     * Need keywords to be normalized for this match type?.
     */
    public boolean isNormalized() {
        return matchType.isNormalized();
    }

    /**
     * Need keywords to be not normalized for this match type?.
     */
    public boolean isNotNormalized() {
        return !matchType.isNormalized();
    }

    /**
     * Google Ads group ID.
     */
    @Override
    public Long getGoogleAdsId() {
        return googleAdsId;
    }

    /**
     * Set Google Ads group ID.
     */
    @Override
    public void setGoogleAdsId(Long googleAdsId) {
        this.googleAdsId = googleAdsId;
    }

    @Override
    public void resetGoogleAdsId() {
        // Group not found in Google Ads -> Remove all manual texts as they are no longer valid.
        // If they still exist, they will be re-downloaded by the manual text syncs.
        new ArrayList<>(manualResponsiveTexts).forEach(LocalManualResponsiveText::remove);
        responsiveTexts.forEach(LocalResponsiveText::resetGoogleAdsId);
        keywords.values().forEach(LocalKeyword::resetGoogleAdsId);
        negatives.values().forEach(LocalNegative::resetGoogleAdsId);
        super.resetGoogleAdsId();
    }

    /**
     * Group name.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Set Google Ads group name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Bing Ads group id.
     */
    @Override
    public Long getBingAdsId() {
        return bingAdsId;
    }

    /**
     * Set Bing Ads group id.
     */
    @Override
    public void setBingAdsId(Long bingAdsId) {
        this.bingAdsId = bingAdsId;
    }

    @Override
    public void resetBingAdsId() {
        manualResponsiveTexts.forEach(LocalManualResponsiveText::resetBingAdsId);
        responsiveTexts.forEach(LocalResponsiveText::resetBingAdsId);
        keywords.values().forEach(LocalKeyword::resetBingAdsId);
        negatives.values().forEach(LocalNegative::resetBingAdsId);
        super.resetBingAdsId();
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
     * Responsive texts.
     */
    public List<LocalResponsiveText> getResponsiveTexts() {
        return unmodifiableList(responsiveTexts);
    }

    /**
     * Responsive texts.
     */
    public Stream<LocalResponsiveText> streamResponsiveTexts() {
        return responsiveTexts.stream();
    }

    /**
     * Add responsive text.
     *
     * @param text
     *         Text to add.
     * @return responsiveText for method chaining.
     */
    public LocalResponsiveText addResponsiveText(LocalResponsiveText text) {
        text.setGroup(this);
        responsiveTexts.add(text);

        return text;
    }

    /**
     * Remove all responsive texts.
     */
    public void removeResponsiveTexts() {
        List.copyOf(getResponsiveTexts()).forEach(this::removeResponsiveText);
    }

    /**
     * Remove responsive text.
     */
    public void removeResponsiveText(LocalResponsiveText text) {
        responsiveTexts.remove(text);
        text.setGroup(null);
    }

    /**
     * Manual responsive texts.
     */
    public List<LocalManualResponsiveText> getManualResponsiveTexts() {
        return unmodifiableList(manualResponsiveTexts);
    }

    /**
     * Manual responsive texts.
     */
    public Stream<LocalManualResponsiveText> streamManualResponsiveTexts() {
        return manualResponsiveTexts.stream();
    }

    /**
     * Active manual responsive texts.
     */
    public Stream<LocalManualResponsiveText> streamActiveManualResponsiveTexts() {
        return manualResponsiveTexts.stream()
                .filter(not(LocalManualResponsiveText::isPaused));
    }

    /**
     * Add manual responsive text.
     *
     * @param manualText
     *         Text to add.
     * @return manualText for method chaining.
     */
    public LocalManualResponsiveText addManualResponsiveText(LocalManualResponsiveText manualText) {
        manualResponsiveTexts.add(manualText);
        manualText.setGroup(this);

        return manualText;
    }

    /**
     * Remove manual responsive text.
     */
    public void removeManualResponsiveText(LocalManualResponsiveText manualText) {
        manualResponsiveTexts.remove(manualText);
        manualText.setGroup(null);
    }


    /**
     * Check if there are any texts yet which have been successfully booked into Google Ads.
     */
    public boolean hasTextInGoogleAds() {
        return responsiveTexts.stream().anyMatch(LocalResponsiveText::isBookedInGoogleAds) ||
               manualResponsiveTexts.stream().anyMatch(LocalManualResponsiveText::isBookedInGoogleAds);
    }

    /**
     * Check if there are any texts yet which have been successfully booked into Bing Ads.
     */
    public boolean hasTextInBingAds() {
        return responsiveTexts.stream().anyMatch(LocalResponsiveText::isBookedInBingAds) ||
               manualResponsiveTexts.stream().anyMatch(LocalManualResponsiveText::isBookedInBingAds);
    }

    /**
     * Add the given keyword.
     */
    public LocalKeyword addKeyword(LocalKeyword keyword) {
        var result = keywords.get(keyword.getText());
        if (result != null) {
            return result;
        }

        keywords.put(keyword.getText(), keyword);
        keyword.setGroup(this);
        return keyword;
    }

    /**
     * Remove the given keyword.
     * Just keywords NOT booked into Google Ads may be removed to keep the database consistent with Google Ads.
     */
    void removeKeyword(LocalKeyword keyword) {
        keywords.remove(keyword.getText());
        keyword.setGroup(null);
    }

    /**
     * All keywords.
     */
    public Collection<LocalKeyword> getKeywords() {
        return unmodifiableCollection(keywords.values());
    }

    /**
     * All keywords.
     */
    public Stream<LocalKeyword> streamKeywords() {
        return keywords.values().stream();
    }

    /**
     * Keyword with the given text.
     * If not present, the normalized keyword if this is a normalized match type group.
     */
    public LocalKeyword getKeywordOrNormalizedKeyword(String text) {
        return matchType.isNormalized() ? getNormalizedKeyword() : getKeyword(text);
    }

    /**
     * Keyword with the given text.
     */
    public LocalKeyword getKeyword(String text) {
        return keywords.get(text.toLowerCase(getPortal().getLanguage()));
    }

    /**
     * Get the one and only normalized keyword.
     * <p>
     * If there are multiple normalized keywords (due to data inconsistencies),
     * the most recent is returned, because it should be the correct one.
     */
    public LocalKeyword getNormalizedKeyword() {
        // Use the most recently created keyword, because of entity renaming there may be more than one...
        return keywords.values().stream()
                .filter(LocalKeyword::isNormalized)
                .min(KEYWORD_ID_COMPARATOR)
                .orElse(null);
    }

    /**
     * Negative with the given text.
     */
    public LocalNegative getNegative(String text) {
        return negatives.get(text.toLowerCase(getPortal().getLanguage()));
    }

    /**
     * Add a new negative for the given keyword, if no matching negative exists.
     * Otherwise, the matching negative of the group will be used.
     *
     * @return Has a new negative been set at the keyword?
     */
    public boolean addNewNegativeFor(LocalKeyword keyword) {
        var negative = addNegative(new LocalNegative(keyword.getText(), keyword.getMatchType()));
        return keyword.setNegative(negative);
    }

    /**
     * Add the given negative keyword, if no matching negative exists.
     * Otherwise, the matching negative of the group will be returned.
     *
     * @return The negative added to the group.
     */
    public LocalNegative addNegative(LocalNegative negative) {
        var result = negatives.get(negative.getText());
        if (result != null) {
            return result;
        }

        negatives.put(negative.getText(), negative);
        negative.setGroup(this);
        return negative;
    }

    /**
     * Remove the given negative keyword.
     * Just negative keywords NOT booked into Google Ads may be removed to keep the database consistent with Google Ads.
     */
    void removeNegative(LocalNegative negative) {
        negatives.remove(negative.getText());
        negative.setGroup(null);
    }

    /**
     * Keywords (lower case).
     */
    public Collection<LocalNegative> getNegatives() {
        return unmodifiableCollection(negatives.values());
    }

    /**
     * Should ad rotation be optimized by the network?
     */
    public boolean isAdRotationOptimized() {
        return adRotationOptimized;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof LocalGroup group &&
               getMetaGroup().equals(group.getMetaGroup()) &&
               getMatchType().equals(group.getMatchType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(metaGroup, matchType);
    }
}
