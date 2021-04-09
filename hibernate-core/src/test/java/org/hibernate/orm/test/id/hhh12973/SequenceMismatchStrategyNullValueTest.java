/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.orm.test.id.hhh12973;

import org.hibernate.id.SequenceMismatchStrategy;

import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.orm.junit.BaseUnitTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * @author Vlad Mihalcea
 */
@TestForIssue(jiraKey = "HHH-12973")
@BaseUnitTest
public class SequenceMismatchStrategyNullValueTest {

	@Test
	public void test() {
		assertEquals( SequenceMismatchStrategy.EXCEPTION, SequenceMismatchStrategy.interpret( null ) );
	}
}
