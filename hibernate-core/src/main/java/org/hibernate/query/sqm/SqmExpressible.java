/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.query.sqm;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.model.domain.DomainType;
import org.hibernate.query.BindableType;
import org.hibernate.query.sqm.tree.expression.SqmExpression;
import org.hibernate.type.descriptor.java.JavaType;

/**
 * Anything in the application domain model that can be used in an
 * SQM query as an expression
 *
 * @see SqmExpression#getNodeType
 *
 * @author Steve Ebersole
 */
public interface SqmExpressible<J> extends BindableType<J> {
	/**
	 * The Java type descriptor for this expressible
	 */
	JavaType<J> getExpressibleJavaType();

	default JavaType<?> getRelationalJavaType() {
		return getExpressibleJavaType();
	}

	@Override
	default boolean isInstance(J value) {
		return getExpressibleJavaType().isInstance( value );
	}

	@Override
	default SqmExpressible<J> resolveExpressible(SessionFactoryImplementor sessionFactory) {
		return this;
	}

	/**
	 * The name of the type.
	 *
	 * @apiNote This is the Hibernate notion of the type name.  For most types
	 * this will simply be the Java type (i.e. {@link Class}) name.  However
	 * using the String allows for Hibernate's dynamic model feature.
	 */
	default String getTypeName() {
		// default impl to handle the general case returning the Java type name
		JavaType<J> expressibleJavaType = getExpressibleJavaType();
		return expressibleJavaType == null ? "unknown" : expressibleJavaType.getJavaType().getTypeName();
	}

	DomainType<J> getSqmType();
}
