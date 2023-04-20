package org.hibernate.sql.results.graph.entity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Stream;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Persistent tracking parameters.
 * <p>
 * Google Ads limits:
 * <ul>
 *     <li>Custom parameters: Max. 8.</li>
 *     <li>Custom parameter names: Max. 16 alphanumeric chars.</li>
 *     <li>Custom parameter values: Max. 250 chars.</li>
 * </ul>
 */
@Embeddable
public class TrackingParameters implements Iterable<Entry<String, String>> {
    /**
     * Name of first custom tracking parameter.
     */
    @Column(name = "tracking_1_name")
    private String tracking1Name;

    /**
     * Value of first custom tracking parameter.
     */
    @Column(name = "tracking_1_value")
    private String tracking1Value;

    /**
     * Name of second custom tracking parameter.
     */
    @Column(name = "tracking_2_name")
    private String tracking2Name;

    /**
     * Value of second custom tracking parameter.
     */
    @Column(name = "tracking_2_value")
    private String tracking2Value;

    /**
     * Name of third custom tracking parameter.
     */
    @Column(name = "tracking_3_name")
    private String tracking3Name;

    /**
     * Value of third custom tracking parameter.
     */
    @Column(name = "tracking_3_value")
    private String tracking3Value;

    /**
     * Default constructor for no tracking parameters.
     */
    public TrackingParameters() {}

    /**
     * Constructor with tracking parameters.
     */
    public TrackingParameters(Map<String, String> trackingParameters) {
        setParameters(trackingParameters);
    }

    @Override
    public Iterator<Entry<String, String>> iterator() {
        return getParameters().entrySet().iterator();
    }

    /**
     * Stream of tracking parameters.
     */
    public Stream<Entry<String, String>> stream() {
        return getParameters().entrySet().stream();
    }

    /**
     * Custom parameters for tracking template.
     */
    public Map<String, String> getParameters() {
        var result = new HashMap<String, String>();
        if (tracking1Name != null) {
            result.put(tracking1Name, tracking1Value);
        }
        if (tracking2Name != null) {
            result.put(tracking2Name, tracking2Value);
        }
        if (tracking3Name != null) {
            result.put(tracking3Name, tracking3Value);
        }

        return result;
    }

    /**
     * Custom parameters for tracking template.
     */
    public final void setParameters(Map<String, String> trackingParameters) {
        var iter = new TreeMap<>(trackingParameters).entrySet().iterator();
        if (iter.hasNext()) {
            var trackingParameter = iter.next();
            tracking1Name = trackingParameter.getKey();
            tracking1Value = trackingParameter.getValue();
        } else {
            tracking1Name = null;
            tracking1Value = null;
        }
        if (iter.hasNext()) {
            var trackingParameter = iter.next();
            tracking2Name = trackingParameter.getKey();
            tracking2Value = trackingParameter.getValue();
        } else {
            tracking2Name = null;
            tracking2Value = null;
        }
        if (iter.hasNext()) {
            var trackingParameter = iter.next();
            tracking3Name = trackingParameter.getKey();
            tracking3Value = trackingParameter.getValue();
        } else {
            tracking3Name = null;
            tracking3Value = null;
        }
    }
}
