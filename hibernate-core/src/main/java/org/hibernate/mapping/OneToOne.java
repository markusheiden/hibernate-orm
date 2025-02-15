/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.mapping;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.hibernate.MappingException;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.type.ForeignKeyDirection;
import org.hibernate.type.Type;

/**
 * A mapping model object representing a {@linkplain jakarta.persistence.OneToOne many-to-one association}.
 *
 * @author Gavin King
 */
public class OneToOne extends ToOne {

	private boolean constrained;
	private ForeignKeyDirection foreignKeyType;
	private KeyValue identifier;
	private String propertyName;
	private final String entityName;
	private String mappedByProperty;

	public OneToOne(MetadataBuildingContext buildingContext, Table table, PersistentClass owner) throws MappingException {
		super( buildingContext, table );
		this.identifier = owner.getKey();
		this.entityName = owner.getEntityName();
	}

	private OneToOne(OneToOne original) {
		super( original );
		this.constrained = original.constrained;
		this.foreignKeyType = original.foreignKeyType;
		this.identifier = original.identifier == null ? null : (KeyValue) original.identifier.copy();
		this.propertyName = original.propertyName;
		this.entityName = original.entityName;
		this.mappedByProperty = original.mappedByProperty;
	}

	@Override
	public OneToOne copy() {
		return new OneToOne( this );
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName==null ? null : propertyName.intern();
	}
	
	public String getEntityName() {
		return entityName;
	}
	
	public Type getType() throws MappingException {
		if ( getColumnSpan()>0 ) {
			return MappingHelper.specialOneToOne(
					getReferencedEntityName(),
					getForeignKeyType(),
					isReferenceToPrimaryKey(),
					getReferencedPropertyName(),
					isLazy(),
					isUnwrapProxy(),
					getEntityName(),
					getPropertyName(),
					isConstrained(),
					getBuildingContext()
			);
		}
		else {
			return MappingHelper.oneToOne(
					getReferencedEntityName(),
					getForeignKeyType(),
					isReferenceToPrimaryKey(),
					getReferencedPropertyName(),
					isLazy(),
					isUnwrapProxy(),
					entityName,
					propertyName,
					isConstrained(),
					getBuildingContext()
			);
		}
	}

	@Override
	public void createUniqueKey() {
		if ( !hasFormula() && getColumnSpan()>0  ) {
			getTable().createUniqueKey( getConstraintColumns() );
		}
	}

	@Override
	public List<Selectable> getVirtualSelectables() {
		List<Selectable> selectables = super.getVirtualSelectables();
		if ( selectables.isEmpty() ) {
			selectables = identifier.getSelectables();
		}
		return selectables;
	}

	public List<Column> getConstraintColumns() {
		List<Column> columns = super.getColumns();
		if ( columns.isEmpty() ) {
			columns = identifier.getColumns();
		}
		return columns;
	}

	@Override
	public Iterator<Selectable> getConstraintColumnIterator() {
		return identifier.getColumnIterator();
	}

	/**
	 * Returns the constrained.
	 * @return boolean
	 */
	public boolean isConstrained() {
		return constrained;
	}

	/**
	 * Returns the foreignKeyType.
	 * @return AssociationType.ForeignKeyType
	 */
	public ForeignKeyDirection getForeignKeyType() {
		return foreignKeyType;
	}

	/**
	 * Returns the identifier.
	 * @return Value
	 */
	public KeyValue getIdentifier() {
		return identifier;
	}

	/**
	 * Sets the constrained.
	 * @param constrained The constrained to set
	 */
	public void setConstrained(boolean constrained) {
		this.constrained = constrained;
	}

	/**
	 * Sets the foreignKeyType.
	 * @param foreignKeyType The foreignKeyType to set
	 */
	public void setForeignKeyType(ForeignKeyDirection foreignKeyType) {
		this.foreignKeyType = foreignKeyType;
	}

	/**
	 * Sets the identifier.
	 * @param identifier The identifier to set
	 */
	public void setIdentifier(KeyValue identifier) {
		this.identifier = identifier;
	}

	public boolean isNullable() {
		return !constrained;
	}

	public Object accept(ValueVisitor visitor) {
		return visitor.accept(this);
	}

	@Override
	public boolean isSame(ToOne other) {
		return other instanceof OneToOne && isSame( (OneToOne) other );
	}

	public boolean isSame(OneToOne other) {
		return super.isSame( other )
				&& Objects.equals( foreignKeyType, other.foreignKeyType )
				&& isSame( identifier, other.identifier )
				&& Objects.equals( propertyName, other.propertyName )
				&& Objects.equals( entityName, other.entityName )
				&& constrained == other.constrained;
	}

	public String getMappedByProperty() {
		return mappedByProperty;
	}

	public void setMappedByProperty(String mappedByProperty) {
		this.mappedByProperty = mappedByProperty;
	}
}
