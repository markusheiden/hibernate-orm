/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.orm.test.locking.jpa;

import java.util.List;
import java.util.Map;

import org.hibernate.dialect.HSQLDialect;
import org.hibernate.query.spi.QueryImplementor;

import org.hibernate.testing.jdbc.SQLStatementInspector;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.hibernate.testing.orm.junit.SkipForDialect;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.LockModeType;
import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.PessimisticLockException;
import jakarta.persistence.QueryTimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.hibernate.jpa.SpecHints.HINT_SPEC_QUERY_TIMEOUT;

/**
 * @author Steve Ebersole
 */
@DomainModel(annotatedClasses = { Employee.class, Department.class })
@SessionFactory(useCollectingStatementInspector = true)
@SkipForDialect(dialectClass = HSQLDialect.class, reason = "Seems HSQLDB doesn't cancel the query if it waits for a lock?!")
public class FollowOnLockingTest {

	@Test
	public void testQueryLockingWithoutFollowOn(SessionFactoryScope scope) {
		testQueryLocking( scope, false );
	}
	@Test
	public void testQueryLockingWithFollowOn(SessionFactoryScope scope) {
		testQueryLocking( scope, true );
	}

	public void testQueryLocking(SessionFactoryScope scope, boolean followOnLocking) {
		SQLStatementInspector statementInspector = scope.getCollectingStatementInspector();
		scope.inSession( (s) -> {
			// After a transaction commit, the lock mode is set to NONE, which the TCK also does
			scope.inTransaction(
					s,
					session -> {
						final Department engineering = new Department( 1, "Engineering" );
						session.persist( engineering );

						session.persist( new Employee( 1, "John", 9F, engineering ) );
						session.persist( new Employee( 2, "Mary", 10F, engineering ) );
						session.persist( new Employee( 3, "June", 11F, engineering ) );
					}
			);

			scope.inTransaction(
					s,
					session -> {
						statementInspector.clear();

						final QueryImplementor<Employee> query = session.createQuery(
								"select e from Employee e where e.salary > 10",
								Employee.class
						);
						if ( followOnLocking ) {
							query.setFollowOnLocking( true );
						}
						query.setLockMode( LockModeType.PESSIMISTIC_READ );
						final List<Employee> employees = query.list();

						assertThat( employees ).hasSize( 1 );
						final LockModeType appliedLockMode = session.getLockMode( employees.get( 0 ) );
						assertThat( appliedLockMode ).isIn(
								LockModeType.PESSIMISTIC_READ,
								LockModeType.PESSIMISTIC_WRITE
						);

						if ( followOnLocking ) {
							statementInspector.assertExecutedCount( 2 );
						}
						else {
							statementInspector.assertExecutedCount( 1 );
						}

						try {
							// with the initial txn still active (locks still held), try to update the row from another txn
							scope.inTransaction( (session2) -> {
								session2.createMutationQuery( "update Employee e set salary = 90000 where e.id = 3" )
										.setTimeout( 1 )
										.executeUpdate();
							} );
							fail( "Locked entity update was allowed" );
						}
						catch (PessimisticLockException | LockTimeoutException | QueryTimeoutException expected) {
						}
					}
			);
		} );
	}

	@AfterEach
	public void dropTestData(SessionFactoryScope scope) {
		scope.inTransaction( (session) -> {
			session.createMutationQuery( "delete Employee" ).executeUpdate();
			session.createMutationQuery( "delete Department" ).executeUpdate();
		} );
	}
}