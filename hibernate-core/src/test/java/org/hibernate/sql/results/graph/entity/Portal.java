package org.hibernate.sql.results.graph.entity;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Currency;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.hibernate.annotations.Type;

import static jakarta.persistence.GenerationType.SEQUENCE;

/**
 * Portal.
 * <p>
 * It is important, that everything is loaded eager here,
 * because portals may be passed around between different threads.
 * That avoids the need for re-attaching entities to the session.
 */
// The allocation size has to be the same as defined in the Postgres sequence!
@SequenceGenerator(name = "seq_portal", sequenceName = "seq_portal", allocationSize = 1)
@Entity
@Table(name = "portal")
public class Portal {
    /**
     * Id.
     */
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = SEQUENCE, generator = "seq_portal")
    private Long id;

    /**
     * Deleted flag.
     */
    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    /**
     * Is the portal for testing purposes?.
     */
    @Column(name = "test", nullable = false)
    private boolean test = false;

    /**
     * Portal name used for the account structure, in the UI and more.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Portal host used for identifying clients for customer APIs.
     */
    @Column(name = "host", nullable = false)
    private String host;

    /**
     * Locale (language & country) for entities, texts and keywords.
     * May contain "unsupported" locales, e.g. "de_US" if a portal wants to advertise in german in the US.
     */
    @Column(name = "locale")
    private Locale locale;

    /**
     * Country derived from {@link #locale}.
     */
    @Transient
    private Locale country;

    /**
     * Language derived from {@link #locale}.
     */
    @Transient
    private Locale language;

    /**
     * Timezone.
     */
    @Column(name = "timezone")
    private ZoneId timezone;

    /**
     * Currency.
     */
    @Column(name = "currency")
    private Currency currency;

    /**
     * Maximum number of unknown words.
     */
    @Column(name = "max_unknown_words", nullable = false)
    private int maxUnknownWords;

    /**
     * Minimum amount of products the landing page for a keyword should have.
     * Used for booking and later for pausing.
     */
    @Column(name = "min_products", nullable = false)
    private int minProducts;

    /**
     * Id.
     */
    public Long getId() {
        return id;
    }

    /**
     * Unique ID based on the host.
     * Used by other services like the pcc.
     * <p>
     * Returns e.g. example-org_en-gb
     */
    public String getUniqueId() {
        return host
                       .replaceFirst("^www\\.", "")
                       .replaceAll("\\.", "-")
               + "_" + getLanguageTag();
    }

    /**
     * Portal language tag consisting of language and country.
     * <br>
     * Examples: de-de, en-gb, en-us
     */
    String getLanguageTag() {
        return locale.toLanguageTag().toLowerCase();
    }

    /**
     * Is the portal marked as deleted?.
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * Mark portal as deleted.
     */
    void delete() {
        this.deleted = true;
    }

    /**
     * Is the portal for testing purposes?.
     */
    public boolean isTest() {
        return test;
    }

    /**
     * Is the portal for testing purposes?.
     */
    public void setTest(boolean test) {
        this.test = test;
    }

    /**
     * Name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set name.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Host used for identifying clients for customer APIs.
     */
    public String getHost() {
        return host;
    }

    /**
     * Set the host used for the display URI.
     */
    public void setHost(final String host) {
        this.host = host;
    }

    /**
     * Locale (language & country) for entities, texts and keywords.
     * May contain "unsupported" locales, e.g. "de_US" if a portal wants to advertise in german in the US.
     * If you are only interest in the language (e.g. for the entity recognition) use {@link #getLanguage()} instead!
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Portal country. Used e.g. in naming strategies for prefixes.
     * This is only the country of {@link #getLocale()}.
     */
    public Locale getCountry() {
        if (country == null) {
            // No synchronization needed, because no conflicts may happen.
            country = new Locale("", locale.getCountry());
        }
        return country;
    }

    /**
     * Portal language. Used e.g. for entities.
     * This is only the language of {@link #getLocale()}.
     */
    public Locale getLanguage() {
        if (language == null) {
            // No synchronization needed, because no conflicts may happen.
            language = new Locale(locale.getLanguage());
        }
        return language;
    }

    /**
     * Locale (language & country) for entities, texts and keywords.
     * May contain "unsupported" locales, e.g. "de_US" if a portal wants to advertise in german in the US.
     */
    public void setLocale(final Locale locale) {
        this.country = null;
        this.language = null;
        this.locale = locale;
    }

    /**
     * Now in the portal's timezone.
     */
    public LocalDateTime now() {
        return LocalDateTime.now(timezone);
    }

    /**
     * Timezone.
     */
    public ZoneId getTimezone() {
        return timezone;
    }

    /**
     * Timezone.
     */
    public void setTimezone(ZoneId timezone) {
        this.timezone = timezone;
    }

    /**
     * Currency.
     */
    public Currency getCurrency() {
        return currency;
    }

    /**
     * Currency.
     */
    public void setCurrency(final Currency currency) {
        this.currency = currency;
    }

    /**
     * Maximum number of unknown words.
     */
    public int getMaxUnknownWords() {
        return maxUnknownWords;
    }

    /**
     * Set maximum number of unknown words.
     */
    public void setMaxUnknownWords(final int maxUnknownWords) {
        this.maxUnknownWords = maxUnknownWords;
    }

    /**
     * Returns the minimum amount of products the landing page for a keyword should have.
     * Used for booking and later for pausing.
     */
    public int getMinProducts() {
        return minProducts;
    }

    /**
     * Sets the minimum amount of products the landing page for a keyword should have.
     * Used for booking and later for pausing.
     */
    public void setMinProducts(int minProducts) {
        this.minProducts = minProducts;
    }

    @Override
    public boolean equals(final Object o) {
        return o instanceof Portal portal &&
               Objects.equals(id, portal.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
