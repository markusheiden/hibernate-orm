/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.orm.test.mapping.onetoone;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

import org.hibernate.annotations.NaturalId;

import org.hibernate.testing.orm.junit.EntityManagerFactoryScope;
import org.hibernate.testing.orm.junit.Jpa;
import org.junit.jupiter.api.Test;

/**
 * @author Vlad Mihalcea
 */
@Jpa(
		annotatedClasses = {
				OneToOneMapsIdTest.Person.class,
				OneToOneMapsIdTest.PersonDetails.class
		}
)
public class OneToOneMapsIdTest {

	@Test
	public void testLifecycle(EntityManagerFactoryScope scope) {
		Person _person = scope.fromTransaction( entityManager -> {
			Person person = new Person( "ABC-123" );
			entityManager.persist( person );

			return person;
		} );

		scope.inTransaction( entityManager -> {
			Person person = entityManager.find( Person.class, _person.getId() );

			PersonDetails personDetails = new PersonDetails();
			personDetails.setNickName( "John Doe" );
			personDetails.setPerson( person );

			entityManager.persist( personDetails );
		} );
	}

	@Entity(name = "Person")
	public static class Person {

		@Id
		@GeneratedValue
		private Long id;

		@NaturalId
		private String registrationNumber;

		public Person() {
		}

		public Person(String registrationNumber) {
			this.registrationNumber = registrationNumber;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getRegistrationNumber() {
			return registrationNumber;
		}
	}

	@Entity(name = "PersonDetails")
	public static class PersonDetails {

		@Id
		private Long id;

		private String nickName;

		@OneToOne
		@MapsId
		private Person person;

		public String getNickName() {
			return nickName;
		}

		public void setNickName(String nickName) {
			this.nickName = nickName;
		}

		public Person getPerson() {
			return person;
		}

		public void setPerson(Person person) {
			this.person = person;
		}
	}

}
