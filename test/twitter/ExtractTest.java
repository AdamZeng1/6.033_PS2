package twitter;

import org.junit.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ExtractTest {

	/*
	 * Testing strategy
	 *
	 * cover the cartesian product of these partitions:
	 * partition on a: UNIX Epoch time 1970-01-01T00:00:00Z
	 * partition on b: UNIX Epoch time 1970-01-01T00:00:00Z
	 * partition on a: < UNIX Epoch time 1969-01-01T00:00:00Z
	 * partition on b: < UNIX Epoch time 1969-01-01T00:00:00Z
	 * partition on a: > UNIX Epoch time and < current time
	 * partition on b: > UNIX Epoch time and < current time
	 * partition on a: > current time
	 * partition on b: > current time
	 *
	 * cover the subdomains of these partitions:
	 * partition on signs of a and b:
	 * both < UNIX Epoch time
	 * both > UNIX Epoch time but < current time
	 * both > current time
	 * both = UNIX Epoch time
	 */

	private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
	private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");

	private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
	private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);

	@Test(expected = AssertionError.class)
	public void testAssertionsEnabled() {
		assert false; // make sure assertions are enabled with VM argument: -ea
	}

	@Test
	public void testGetTimespanTwoTweets() {
		Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet2));

		assertEquals("expected start", d1, timespan.getStart());
		assertEquals("expected end", d2, timespan.getEnd());
	}

	private static final Instant d3 = Instant.parse("1970-01-01T00:00:00Z");
	private static final Instant d4 = Instant.parse("1970-01-01T00:00:00Z");
	private static final Tweet tweet3 = new Tweet(1, "adam", "is it reasonable to talk about rivest so much?", d3);
	private static final Tweet tweet4 = new Tweet(2, "Eva", "rivest talk in 30 minutes #hype", d4);

	// covers a and b are both UNIX Epoch time
	@Test
	public void testGetTimespanTwoTweetsAreBothUnixEpochTime() {
		Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet3, tweet2, tweet4));
		assertEquals("expected start", d7, timespan.getStart());
		assertEquals("expected end", d8, timespan.getEnd());
	}

	private static final Instant d5 = Instant.parse("2022-01-01T11:45:12Z");
	private static final Instant d6 = Instant.parse("2022-01-01T11:21:22Z");
	private static final Tweet tweet5 = new Tweet(1, "Michael", "is it reasonable to talk about rivest so much?", d5);
	private static final Tweet tweet6 = new Tweet(2, "Diva", "rivest talk in 30 minutes #hype", d6);

	// covers a and b are both > current time
	@Test
	public void testGetTimespanTwoTweetsAreBothLaterThanCurrentTime() {
		Timespan timespan = Extract.getTimespan(Arrays.asList(tweet5, tweet7, tweet6, tweet8));
		assertEquals("expected start", d6, timespan.getStart());
		assertEquals("expected end", d5, timespan.getEnd());
	}

	private static final Instant d7 = Instant.parse("2019-02-01T11:45:12Z");
	private static final Instant d8 = Instant.parse("2019-01-01T11:21:22Z");
	private static final Tweet tweet7 = new Tweet(1, "Daniel", "is it reasonable to talk about rivest so much?", d7);
	private static final Tweet tweet8 = new Tweet(2, "Sam", "rivest talk in 30 minutes #hype", d8);

	private static final Instant d11 = Instant.parse("2016-02-01T11:45:12Z");
	private static final Instant d12 = Instant.parse("2014-01-01T11:21:22Z");
	private static final Tweet tweet11 = new Tweet(1, "Trump", "is it reasonable to talk about rivest so much?", d11);
	private static final Tweet tweet12 = new Tweet(2, "Donald", "rivest talk in 30 minutes #hype", d12);

	// covers a and b > UNIX Epoch time but < current time
	@Test
	public void testGetTimespanTwoTweetsLaterThanEpochTimeEarlierThanCurrentTime() {
		Timespan timespan = Extract.getTimespan(Arrays.asList(tweet7, tweet11, tweet8, tweet12));

		assertEquals("expected start", d8, timespan.getStart());
		assertEquals("expected end", d7, timespan.getEnd());
	}

	private static final Instant d9 = Instant.parse("1969-01-01T11:21:12Z");
	private static final Instant d10 = Instant.parse("1969-01-01T11:21:22Z");
	private static final Tweet tweet9 = new Tweet(1, "Lisa", "is it reasonable to talk about rivest so much?", d9);
	private static final Tweet tweet10 = new Tweet(2, "Luka", "rivest talk in 30 minutes #hype", d10);

	// covers a and b both < UNIX Epoch time
	@Test
	public void testGetTimespanTwoTweetsBothEarlierThanEpochTime() {
		Timespan timespan = Extract.getTimespan(Arrays.asList(tweet10, tweet7, tweet5, tweet9, tweet8, tweet6));

		assertEquals("expected start", d9, timespan.getStart());
		assertEquals("expected end", d10, timespan.getEnd());
	}

	@Test
	public void testGetMentionedUsersNoMention() {
		Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet1));

		assertTrue("expected empty set", mentionedUsers.isEmpty());
	}

	/*
	 * Warning: all the tests you write here must be runnable against any
	 * Extract class that follows the spec. It will be run against several staff
	 * implementations of Extract, which will be done by overwriting
	 * (temporarily) your version of Extract with the staff's version.
	 * DO NOT strengthen the spec of Extract or its methods.
	 *
	 * In particular, your test cases must not call helper methods of your own
	 * that you have put in Extract, because that means you're testing a
	 * stronger spec than Extract says. If you need such helper methods, define
	 * them in a different class. If you only need them in this test class, then
	 * keep them in this test class.
	 */


	/* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
	 * Redistribution of original or derived work requires explicit permission.
	 * Don't post any of this code on the web or to a public Github repository.
	 */

}
