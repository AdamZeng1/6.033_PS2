package twitter;

import twitter.exception.NullOrEmptyAuthorException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import twitter.exception.UnqualifiedUsernameException;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class FilterTest {

	/*
	 * Testing Strategy
	 *
	 * writtenBy
	 * partition username:
	 * username caters the requirement of {A..Z, a..z, 0..9, _, -}
	 * username doesn't caters the requirement e.g. @!)();;/,
	 *
	 * partition tweets:
	 * contain case-insensitive same author e.g. jbieber, JBieBer
	 * contain author doesn't cater the requirement e.g. @!)();;/,
	 * all authors are the same
	 * all authors are different
	 * author is empty string
	 * author is null
	 *
	 */

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

	private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
	private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");

	private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
	private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);

	@Test(expected = AssertionError.class)
	public void testAssertionsEnabled() {
		assert false; // make sure assertions are enabled with VM argument: -ea
	}

	@Test
	public void testWrittenByMultipleTweetsSingleResult() throws Exception {
		List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "alyssa");

		assertEquals("expected singleton list", 1, writtenBy.size());
		assertTrue("expected list to contain tweet", writtenBy.contains(tweet1));
	}

	private static final Instant d3 = Instant.parse("2016-02-17T10:00:00Z");
	private static final Instant d4 = Instant.parse("2016-02-17T11:00:00Z");

	private static final Tweet tweet3 = new Tweet(3, "alyssa", "is it reasonable to talk about rivest so much?", d3);
	private static final Tweet tweet4 = new Tweet(4, "bbitdiddle", "rivest talk in 30 minutes #hype", d4);

	// covers unqualified username e.g. @!)();;/,
	@Test
	public void testWrittenByMultipleTweetsUnqualifiedUsername() throws Exception {
		exceptionRule.expect(UnqualifiedUsernameException.class);
		exceptionRule.expectMessage("unqualified username");
		List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet3, tweet4), "@!)();;/,");

		assertEquals("expected singleton list", 0, writtenBy.size());
		assertFalse("expected list to contain tweet", writtenBy.contains(tweet1));
	}

	private static final Instant d5 = Instant.parse("2016-02-17T10:00:00Z");
	private static final Instant d6 = Instant.parse("2016-02-17T11:00:00Z");

	private static final Tweet tweet5 = new Tweet(5, "alyssa", "is it reasonable to talk about rivest so much?", d5);
	private static final Tweet tweet6 = new Tweet(6, "AlysSA", "rivest talk in 30 minutes #hype", d6);

	// covers username and author case-insensitive e.g. username AlySsa and tweet author contain alyssa AlysSA
	@Test
	public void testWrittenByMultipleTweetsCaseInsensitive() throws Exception {
		List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet5, tweet6), "ALySsa");

		assertEquals("expected singleton list", 2, writtenBy.size());
		assertTrue("expected list to contain tweet", writtenBy.contains(tweet5));
		assertTrue("expected list to contain tweet", writtenBy.contains(tweet6));
	}

	private static final Instant d7 = Instant.parse("2016-02-17T10:00:00Z");
	private static final Instant d8 = Instant.parse("2016-02-17T11:00:00Z");

	private static final Tweet tweet7 = new Tweet(7, "alyssa", "is it reasonable to talk about rivest so much?", d7);
	private static final Tweet tweet8 = new Tweet(8, "aLySSa", "rivest talk in 30 minutes #hype", d8);

	// covers author case-insensitive
	@Test
	public void testWrittenByMultipleTweetsAuthorCaseInsensitive() throws Exception {
		List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet7, tweet8), "ALYSSA");

		assertEquals("expected singleton list", 2, writtenBy.size());
		assertTrue("expected list to contain tweet", writtenBy.contains(tweet7));
		assertTrue("expected list to contain tweet", writtenBy.contains(tweet8));
	}


	private static final Instant d9 = Instant.parse("2016-02-17T10:00:00Z");
	private static final Instant d10 = Instant.parse("2016-02-17T11:00:00Z");

	private static final Tweet tweet9 = new Tweet(9, ";:sa@312&*^%", "is it reasonable to talk about rivest so much?", d9);
	private static final Tweet tweet10 = new Tweet(10, ";:sa@312++==", "rivest talk in 30 minutes #hype", d10);

	// covers author doesn't cater the requirement e.g. ;:sa@312&*^%
	@Test
	public void testWrittenByMultipleTweetsUnqualifiedAuthor() throws Exception {
		exceptionRule.expect(UnqualifiedUsernameException.class);
		exceptionRule.expectMessage("unqualified author name");
		List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet9, tweet10), "ALYSSA");

		assertEquals("expected singleton list", 0, writtenBy.size());
		assertFalse("expected list to contain tweet", writtenBy.contains(tweet9));
		assertFalse("expected list to contain tweet", writtenBy.contains(tweet10));


	}

	private static final Instant d11 = Instant.parse("2016-02-17T10:00:00Z");
	private static final Instant d12 = Instant.parse("2016-02-17T11:00:00Z");

	private static final Tweet tweet11 = new Tweet(11, "45Ad-am_", "is it reasonable to talk about rivest so much?", d11);
	private static final Tweet tweet12 = new Tweet(12, "01Michael_-", "rivest talk in 30 minutes #hype", d12);

	// covers authors are all different from each other e.g.
	@Test
	public void testWrittenByMultipleTweetsDifferentAuthors() throws Exception {
		List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet11, tweet12), "45Ad-am_");

		assertEquals("expected singleton list", 1, writtenBy.size());
		assertTrue("expected list to contain tweet", writtenBy.contains(tweet11));
		assertFalse("expected list to contain tweet", writtenBy.contains(tweet12));
	}

	private static final Instant d13 = Instant.parse("2016-02-17T10:00:00Z");
	private static final Instant d14 = Instant.parse("2016-02-17T11:00:00Z");

	private static final Tweet tweet13 = new Tweet(13, "", "is it reasonable to talk about rivest so much?", d13);
	private static final Tweet tweet14 = new Tweet(14, null, "rivest talk in 30 minutes #hype", d14);


	// covers authors are all different from each other e.g.
	@Test
	public void testWrittenByMultipleTweetsNullOrEmptyStringAuthors() throws Exception {
		exceptionRule.expect(NullOrEmptyAuthorException.class);
		exceptionRule.expectMessage("author name of tweet is null or empty string");

		List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet13, tweet14), "45Ad-am_");

		assertEquals("expected singleton list", 0, writtenBy.size());
		assertTrue("expected list to contain tweet", writtenBy.contains(tweet13));
		assertFalse("expected list to contain tweet", writtenBy.contains(tweet14));
	}

	@Test
	public void testInTimespanMultipleTweetsMultipleResults() {
		Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
		Instant testEnd = Instant.parse("2016-02-17T12:00:00Z");

		List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));

		assertFalse("expected non-empty list", inTimespan.isEmpty());
		assertTrue("expected list to contain tweets", inTimespan.containsAll(Arrays.asList(tweet1, tweet2)));
		assertEquals("expected same order", 0, inTimespan.indexOf(tweet1));
	}

	@Test
	public void testContaining() {
		List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("talk"));

		assertFalse("expected non-empty list", containing.isEmpty());
		assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet1, tweet2)));
		assertEquals("expected same order", 0, containing.indexOf(tweet1));
	}

	/*
	 * Warning: all the tests you write here must be runnable against any Filter
	 * class that follows the spec. It will be run against several staff
	 * implementations of Filter, which will be done by overwriting
	 * (temporarily) your version of Filter with the staff's version.
	 * DO NOT strengthen the spec of Filter or its methods.
	 *
	 * In particular, your test cases must not call helper methods of your own
	 * that you have put in Filter, because that means you're testing a stronger
	 * spec than Filter says. If you need such helper methods, define them in a
	 * different class. If you only need them in this test class, then keep them
	 * in this test class.
	 */


	/* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
	 * Redistribution of original or derived work requires explicit permission.
	 * Don't post any of this code on the web or to a public Github repository.
	 */
}
