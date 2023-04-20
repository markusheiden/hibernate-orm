package org.hibernate.sql.results.graph.entity;

/**
 * Text template interface.
 */
public interface TextTemplate {
    /**
     * Containing master template.
     */
    MasterTextTemplate getMasterTemplate();

    /**
     * Id.
     */
    Long getId();

    /**
     * Revision.
     */
    int getVersion();

    /**
     * Name as readable identifier.
     */
    String getName();

    /**
     * Is this template active and should be considered during text generation?
     */
    boolean isActive();
}
