package org.hibernate.sql.results.graph.entity;

/**
 * Locals that live in a {@link LocalGroup}.
 */
public interface GroupedLocal {
    /**
     * Meta group containing this local.
     */
    default LocalMetaGroup getMetaGroup() {
        return getGroup().getMetaGroup();
    }

    /**
     * Group containing this local.
     */
    LocalGroup getGroup();

    /**
     * Remove this local from its groups.
     */
    void remove();
}
