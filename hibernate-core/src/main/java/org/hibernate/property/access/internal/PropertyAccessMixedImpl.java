/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.property.access.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.hibernate.property.access.spi.Getter;
import org.hibernate.property.access.spi.GetterFieldImpl;
import org.hibernate.property.access.spi.GetterMethodImpl;
import org.hibernate.property.access.spi.PropertyAccess;
import org.hibernate.property.access.spi.PropertyAccessBuildingException;
import org.hibernate.property.access.spi.PropertyAccessStrategy;
import org.hibernate.property.access.spi.Setter;
import org.hibernate.property.access.spi.SetterFieldImpl;
import org.hibernate.property.access.spi.SetterMethodImpl;

import jakarta.persistence.AccessType;

import static org.hibernate.internal.util.ReflectHelper.findSetterMethod;
import static org.hibernate.internal.util.ReflectHelper.getterMethodOrNull;

/**
 * A {@link PropertyAccess} based on mix of getter/setter method and/or field.
 *
 * @author Steve Ebersole
 */
public class PropertyAccessMixedImpl implements PropertyAccess {
	private final PropertyAccessStrategy strategy;

	private final Getter getter;
	private final Setter setter;

	public PropertyAccessMixedImpl(PropertyAccessStrategy strategy, Class<?> containerJavaType, String propertyName) {
		this.strategy = strategy;

		final AccessType propertyAccessType = AccessStrategyHelper.getAccessType( containerJavaType, propertyName );
		switch ( propertyAccessType ) {
			case FIELD: {
				Field field = AccessStrategyHelper.fieldOrNull( containerJavaType, propertyName );
				if ( field == null ) {
					throw new PropertyAccessBuildingException(
							"Could not locate field for property named [" + containerJavaType.getName() + "#" + propertyName + "]"
					);
				}
				this.getter = fieldGetter( containerJavaType, propertyName, field );
				this.setter = fieldSetter( containerJavaType, propertyName, field );
				break;
			}
			case PROPERTY: {
				Method getterMethod = getterMethodOrNull( containerJavaType, propertyName );
				if ( getterMethod == null ) {
					throw new PropertyAccessBuildingException(
							"Could not locate getter for property named [" + containerJavaType.getName() + "#" + propertyName + "]"
					);
				}
				Method setterMethod = findSetterMethod( containerJavaType, propertyName, getterMethod.getReturnType() );

				this.getter = propertyGetter( containerJavaType, propertyName, getterMethod );
				this.setter = propertySetter( containerJavaType, propertyName, setterMethod );
				break;
			}
			default: {
				throw new PropertyAccessBuildingException(
						"Invalid access type " + propertyAccessType + " for property named [" + containerJavaType.getName() + "#" + propertyName + "]"
				);
			}
		}
	}

	// --- //

	protected Getter fieldGetter(Class<?> containerJavaType, String propertyName, Field field) {
		return new GetterFieldImpl( containerJavaType, propertyName, field );
	}

	protected Setter fieldSetter(Class<?> containerJavaType, String propertyName, Field field) {
		return new SetterFieldImpl( containerJavaType, propertyName, field );
	}

	protected Getter propertyGetter(Class<?> containerJavaType, String propertyName, Method method) {
		return new GetterMethodImpl( containerJavaType, propertyName, method );
	}

	protected Setter propertySetter(Class<?> containerJavaType, String propertyName, Method method) {
		return method == null ? null : new SetterMethodImpl( containerJavaType, propertyName, method );
	}

	@Override
	public PropertyAccessStrategy getPropertyAccessStrategy() {
		return strategy;
	}

	@Override
	public Getter getGetter() {
		return getter;
	}

	@Override
	public Setter getSetter() {
		return setter;
	}
}
