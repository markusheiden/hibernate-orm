/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.orm.test.bytecode.enhancement.lazy.proxy.inlinedirtychecking.dynamicupdate;

import java.util.HashSet;
import java.util.List;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Environment;
import org.hibernate.engine.spi.SessionImplementor;

import org.hibernate.testing.DialectChecks;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.bytecode.enhancement.BytecodeEnhancerRunner;
import org.hibernate.testing.bytecode.enhancement.CustomEnhancementContext;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.orm.test.bytecode.enhancement.lazy.proxy.inlinedirtychecking.DirtyCheckEnhancementContext;
import org.hibernate.orm.test.bytecode.enhancement.lazy.proxy.inlinedirtychecking.NoDirtyCheckEnhancementContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

@TestForIssue(jiraKey = "HHH14424")
@RunWith(BytecodeEnhancerRunner.class)
@CustomEnhancementContext({ NoDirtyCheckEnhancementContext.class, DirtyCheckEnhancementContext.class })
@RequiresDialectFeature(DialectChecks.SupportsIdentityColumns.class)
public class DynamicUpdateAndCollectionsTest extends BaseNonConfigCoreFunctionalTestCase {

	boolean skipTest;

	@Override
	protected void configureStandardServiceRegistryBuilder(StandardServiceRegistryBuilder ssrb) {
		super.configureStandardServiceRegistryBuilder( ssrb );
		ssrb.applySetting( AvailableSettings.DEFAULT_BATCH_FETCH_SIZE, "100" );
		ssrb.applySetting( AvailableSettings.GENERATE_STATISTICS, "true" );
	}

	@Override
	protected void applyMetadataSources(MetadataSources sources) {
		String byteCodeProvider = Environment.getProperties().getProperty( AvailableSettings.BYTECODE_PROVIDER );
		if ( byteCodeProvider != null && !Environment.BYTECODE_PROVIDER_NAME_BYTEBUDDY.equals( byteCodeProvider ) ) {
			// skip the test if the bytecode provider is Javassist
			skipTest = true;
		}
		else {
			sources.addAnnotatedClass( SamplingOrder.class );
			sources.addAnnotatedClass( Customer.class );
			sources.addAnnotatedClass( User.class );
			sources.addAnnotatedClass( Role.class );
		}
	}

	@Before
	public void setUp() {
		if ( skipTest ) {
			return;
		}
		inTransaction(
				session -> {
					User user = new User();
					user.setEmail( "foo@bar.com" );

					Role role = new Role();
					role.setName( "admin" );

					user.addRole( role );

					Customer customer = new Customer();
					customer.setUser( user );

					SamplingOrder order = new SamplingOrder();
					order.setNote( "it is a sample" );
					order.setCustomer( customer );

					session.save( user );
					session.save( role );
					session.save( customer );
					session.save( order );
				}
		);
	}

	@After
	public void tearDown() {
		if ( skipTest ) {
			return;
		}
		inTransaction(
				session -> {
					session.createQuery( "delete from SamplingOrder" ).executeUpdate();
					session.createQuery( "delete from Customer" ).executeUpdate();
					session.createQuery( "delete from User" ).executeUpdate();
					session.createQuery( "delete from Role" ).executeUpdate();
				}
		);
	}

	@Test
	public void testLoad() {
		if ( skipTest ) {
			return;
		}
		inTransaction(
				session -> {
					CriteriaBuilder cb = session.getCriteriaBuilder();
					CriteriaQuery<SamplingOrder> cq = cb.createQuery( SamplingOrder.class );
					Root<SamplingOrder> root = cq.from( SamplingOrder.class );
					root.fetch( SamplingOrder_.customer );

					TypedQuery<SamplingOrder> query = session.createQuery( cq );
					query.getResultList();
				}
		);

		inTransaction(
				session -> {
					List<User> users = session.createQuery( "from User u", User.class ).list();
					User user = users.get( 0 );
					assertThat( user.getEmail(), is( "foo@bar.com" ) );
				}
		);
	}

	@Test
	public void testRemoveCustomers() {
		if ( skipTest ) {
			return;
		}
		Long samplingOrderId = fromTransaction(
				session -> {
					SamplingOrder samplingOrder = getSamplingOrderFetchCustomer( session );
					samplingOrder.setCustomer( null );
					return samplingOrder.getId();
				}
		);

		inTransaction(
				session -> {
					SamplingOrder samplingOrder = session.get( SamplingOrder.class, samplingOrderId );
					assertThat( samplingOrder.getCustomer(), is( nullValue() ) );
				}
		);
	}

	@Test
	public void testAddUserRoles() {
		if ( skipTest ) {
			return;
		}
		inTransaction(
				session -> {
					SamplingOrder samplingOrder = getSamplingOrderFetchCustomer( session );
					User user = samplingOrder.getCustomer().getUser();
					Role role = new Role();
					role.setName( "superuser" );
					user.addRole( role );
					session.save( role );
				}
		);

		inTransaction(
				session -> {
					List<User> users = session.createQuery( "from User u", User.class ).list();
					User user = users.get( 0 );
					assertThat( user.getEmail(), is( "foo@bar.com" ) );
					assertThat( user.getRoles().size(), is( 2 ) );
				}
		);

		inTransaction(
				session -> {
					SamplingOrder samplingOrder = getSamplingOrderFetchCustomer( session );
					User user = samplingOrder.getCustomer().getUser();
					Role role = new Role();
					user.getRoles().add( role );
					session.save( role );
				}
		);

		inTransaction(
				session -> {
					List<User> users = session.createQuery( "from User u", User.class ).list();
					User user = users.get( 0 );
					assertThat( user.getEmail(), is( "foo@bar.com" ) );
					assertThat( user.getRoles().size(), is( 3 ) );
				}
		);

		inTransaction(
				session -> {
					User user = session.createQuery( "from User", User.class ).list().get( 0 );
					Role role = new Role();
					user.getRoles().add( role );
					session.save( role );
				}
		);

		inTransaction(
				session -> {
					List<User> users = session.createQuery( "from User u", User.class ).list();
					User user = users.get( 0 );
					assertThat( user.getEmail(), is( "foo@bar.com" ) );
					assertThat( user.getRoles().size(), is( 4 ) );
				}
		);

	}

	@Test
	public void testDeleteUserRoles() {
		if ( skipTest ) {
			return;
		}
		inTransaction(
				session -> {
					SamplingOrder samplingOrder = getSamplingOrderFetchCustomer( session );
					User user = samplingOrder.getCustomer().getUser();
					user.setRoles( new HashSet<>() );
				}
		);

		inTransaction(
				session -> {
					List<User> users = session.createQuery( "from User u", User.class ).list();
					User user = users.get( 0 );
					assertThat( user.getRoles().size(), is( 0 ) );
				}
		);
	}

	@Test
	public void testModifyUserMail() {
		if ( skipTest ) {
			return;
		}
		inTransaction(
				session -> {
					SamplingOrder samplingOrder = getSamplingOrderFetchCustomer( session );
					User user = samplingOrder.getCustomer().getUser();
					user.setEmail( "bar@foo.com" );
				}
		);

		inTransaction(
				session -> {
					List<User> users = session.createQuery( "from User u", User.class ).list();
					User user = users.get( 0 );
					assertThat( user.getEmail(), is( "bar@foo.com" ) );
					assertThat( user.getRoles().size(), is( 1 ) );
				}
		);
	}

	private SamplingOrder getSamplingOrderFetchCustomer(SessionImplementor session) {
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<SamplingOrder> cq = cb.createQuery( SamplingOrder.class );
		Root<SamplingOrder> root = cq.from( SamplingOrder.class );
		root.fetch( SamplingOrder_.customer );

		TypedQuery<SamplingOrder> query = session.createQuery( cq );
		List<SamplingOrder> resultList = query.getResultList();
		return resultList.get( 0 );
	}

}
