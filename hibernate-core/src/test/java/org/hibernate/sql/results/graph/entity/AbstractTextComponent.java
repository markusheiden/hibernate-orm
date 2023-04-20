package org.hibernate.sql.results.graph.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import org.hibernate.annotations.Fetch;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.GenerationType.SEQUENCE;
import static jakarta.persistence.InheritanceType.SINGLE_TABLE;
import static java.lang.String.format;
import static java.util.Collections.unmodifiableList;
import static org.hibernate.annotations.FetchMode.SUBSELECT;

@Entity
@Inheritance(strategy = SINGLE_TABLE)
@Table(name = "text_component")
@DiscriminatorColumn(name = "type")
public abstract class AbstractTextComponent {
    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "seq_text_component", sequenceName = "seq_text_component", allocationSize = 1)
    @GeneratedValue(strategy = SEQUENCE, generator = "seq_text_component")
    private Long id;

    @Version
    @Column(name = "version")
    private int version;

    @Column(name = "text_kind")
    @Enumerated(STRING)
    private TextKind textKind;

    @ElementCollection(fetch = EAGER)
    @Fetch(SUBSELECT)
    @CollectionTable(name = "text_component_line1",
            joinColumns = @JoinColumn(name = "text_component_id", nullable = false))
    @OrderColumn(name = "p")
    @Column(name = "text", nullable = false)
    private List<String> lines1 = new ArrayList<>();

    @ElementCollection(fetch = EAGER)
    @Fetch(SUBSELECT)
    @CollectionTable(name = "text_component_line2",
            joinColumns = @JoinColumn(name = "text_component_id", nullable = false))
    @OrderColumn(name = "p")
    @Column(name = "text", nullable = false)
    private List<String> lines2 = new ArrayList<>();

    /**
     * User who modified this entity last.
     */
    @Column(name = "modified_by", nullable = true)
    private String modifiedBy;

    /**
     * Last modified at.
     */
    @Column(name = "modified_at")
    private Instant modifiedAt;

    /**
     * Hidden default constructor for Hibernate.
     */
    protected AbstractTextComponent() {}

    AbstractTextComponent(TextKind textKind, List<String> lines1, List<String> lines2) {
        this.textKind = textKind;
        this.lines1.addAll(lines1);
        if (lines2 != null) {
            this.lines2.addAll(lines2);
        }
    }

    public Long getId() {
        return id;
    }

    public int getVersion() {
        return version;
    }

    public TextKind getTextKind() {
        return textKind;
    }

    public List<String> getLines(ResponsiveTextType textType) {
        return textType.isLines2() ? getLines2() : getLines1();
    }

    /**
     * All lines: lines1 and lines2.
     */
    public Stream<String> getAllLines() {
        return Stream.concat(lines1.stream(), lines2.stream());
    }

    public List<String> getLines1() {
        return unmodifiableList(lines1);
    }

    public void setLines1(final List<String> lines1) {
        this.lines1.clear();
        this.lines1.addAll(lines1);
    }

    public List<String> getLines2() {
        return unmodifiableList(lines2);
    }

    public void setLines2(final List<String> lines2) {
        this.lines2.clear();
        if (lines2 != null) {
            this.lines2.addAll(lines2);
        }
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public Instant getModifiedAt() {
        return modifiedAt;
    }

    public void modified(String modifiedBy, Instant modifiedAt) {
        this.modifiedBy = modifiedBy;
        this.modifiedAt = modifiedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AbstractTextComponent component)) {
            return false;
        } else if (id == null) {
            // If not yet persisted, compare object identity.
            return this == o;
        } else {
            return Objects.equals(id, component.getId());
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return format("Text component %d for %s", id, textKind);
    }
}
