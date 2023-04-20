package org.hibernate.sql.results.graph.entity;

import jakarta.persistence.MappedSuperclass;

/**
 * Interface for local representation of Google Ads keyword.
 */
@MappedSuperclass
public abstract class AbstractLocalKeyword extends AbstractLocal<Long, Long> implements GroupedLocal {
    /**
     * Match type.
     */
    public abstract MatchType getMatchType();
}
