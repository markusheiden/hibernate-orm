package org.hibernate.sql.results.graph.entity;

/**
 * Locals with a name.
 */
public interface NamedLocal<AWI, BAI> extends Local<AWI, BAI> {
    /**
     * Name.
     */
    String getName();
}
