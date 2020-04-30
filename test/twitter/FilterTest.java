package twitter;

import twitter.exception.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

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
	 * inTimespan
	 * partition List<tweet> tweets:
	 * timestamp of tweet < 1970.1.1 00:00:00
	 * timestamp of tweet > 1970.1.1 00:00:00
	 * timestamp of tweet is null
	 * tweets are not sorted by timestamp of tweet
	 * timestamp of tweet > current date
	 * timestamp == timespan.start
	 * timestamp == timespan.end
	 * timestamp between start and end
	 *
	 *
	 * partition Instant Timespan:
	 * start or end is null
	 * start or end > current
	 * start or end < 1970.1.1 00:00:00
	 *
	 * ------------------------------------------------------
	 * containing
	 * partition of List<String> words
	 * word is empty string e.g. ""
	 * word is null
	 * word contains space characters e.g. "a cd c"
	 *
	 * partition of List<Tweet> tweets
	 * text of tweet is null
	 * text of tweet is empty string
	 * text of tweet doesn't contain the word
	 * one text of tweet contains several same words
	 * one text of tweet contain a word
	 * one text of tweet contain different words
	 *
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


	private static final Instant d15 = Instant.parse("1969-01-01T00:00:00Z");
	private static final Instant d16 = Instant.parse("1965-03-01T00:45:00Z");

	private static final Tweet tweet15 = new Tweet(15, "Adam", "is it reasonable to talk about rivest so much?", d15);
	private static final Tweet tweet16 = new Tweet(16, "Chris", "rivest talk in 30 minutes #hype", d16);

	// covers timestamp of tweet < 1970.1.1 00:00:00
	@Test
	public void testInTimespanMultipleTweetsMultipleResults() throws NullTimestampException {
		Instant testStart = Instant.parse("1962-02-17T09:00:00Z");
		Instant testEnd = Instant.parse("1990-02-17T12:00:00Z");

		List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet15, tweet16), new Timespan(testStart, testEnd));

		assertFalse("expected non-empty list", inTimespan.isEmpty());
		assertTrue("expected list to contain tweets", inTimespan.containsAll(Arrays.asList(tweet15, tweet16)));
		assertEquals("expected same order", 0, inTimespan.indexOf(tweet15));
	}

	private static final Instant d17 = Instant.parse("1971-01-01T00:00:00Z");
	private static final Instant d18 = Instant.parse("1972-03-01T00:45:00Z");

	private static final Tweet tweet17 = new Tweet(17, "Adam", "is it reasonable to talk about rivest so much?", d17);
	private static final Tweet tweet18 = new Tweet(18, "Chris", "rivest talk in 30 minutes #hype", d18);

	// covers timestamp of tweet > 1970.1.1 00:00:00
	@Test
	public void testInTimespanMultipleTweetsMultipleResultsTimestampLatterThanEpoch() throws NullTimestampException {
		Instant testStart = Instant.parse("1960-02-17T09:00:00Z");
		Instant testEnd = Instant.parse("1997-02-17T12:00:00Z");

		List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet17, tweet18), new Timespan(testStart, testEnd));

		assertFalse("expected non-empty list", inTimespan.isEmpty());
		assertTrue("expected list to contain tweets", inTimespan.containsAll(Arrays.asList(tweet17, tweet18)));
		assertEquals("expected same order", 0, inTimespan.indexOf(tweet17));
	}


	private static final Tweet tweet19 = new Tweet(19, "Adam", "is it reasonable to talk about rivest so much?", null);
	private static final Tweet tweet20 = new Tweet(20, "Chris", "rivest talk in 30 minutes #hype", null);

	// covers timestamp of tweet is null
	@Test
	public void testInTimespanMultipleTweetsMultipleResultsTimestampIsNull() throws NullTimestampException {
		exceptionRule.expect(NullTimestampException.class);
		exceptionRule.expectMessage("timestamp of tweet is null");
		Instant testStart = Instant.parse("1960-02-17T09:00:00Z");
		Instant testEnd = Instant.parse("1997-02-17T12:00:00Z");

		List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet19, tweet20), new Timespan(testStart, testEnd));

		assertFalse("expected non-empty list", inTimespan.isEmpty());
		assertTrue("expected list to contain tweets", inTimespan.containsAll(Arrays.asList(tweet19, tweet20)));
		assertEquals("expected same order", 0, inTimespan.indexOf(tweet19));
	}

	private static final Instant d21 = Instant.parse("1972-03-01T00:45:00Z");
	private static final Instant d22 = Instant.parse("1971-01-01T00:00:00Z");

	private static final Tweet tweet21 = new Tweet(21, "Adam", "is it reasonable to talk about rivest so much?", d21);
	private static final Tweet tweet22 = new Tweet(22, "Chris", "rivest talk in 30 minutes #hype", d22);

	// covers tweets are not sorted by timestamp
	@Test
	public void testInTimespanMultipleTweetsMultipleResultsTimestampNotInOrder() throws NullTimestampException {
		Instant testStart = Instant.parse("1960-02-17T09:00:00Z");
		Instant testEnd = Instant.parse("1997-02-17T12:00:00Z");

		List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet21, tweet22), new Timespan(testStart, testEnd));

		assertFalse("expected non-empty list", inTimespan.isEmpty());
		assertTrue("expected list to contain tweets", inTimespan.containsAll(Arrays.asList(tweet21, tweet22)));
		assertEquals("expected same order", 0, inTimespan.indexOf(tweet21));
	}

	private static final Instant d23 = Instant.parse("1972-03-01T00:45:00Z");
	private static final Instant d24 = Instant.parse("1971-01-01T00:00:00Z");

	private static final Tweet tweet23 = new Tweet(23, "Adam", "is it reasonable to talk about rivest so much?", d23);
	private static final Tweet tweet24 = new Tweet(24, "Chris", "rivest talk in 30 minutes #hype", d24);

	// covers timestamp of tweet equals start and end of Timespan
	@Test
	public void testInTimespanMultipleTweetsMultipleResultsTimestampEqualsBoundary() throws NullTimestampException {
		Instant testStart = Instant.parse("1971-01-01T00:00:00Z");
		Instant testEnd = Instant.parse("1972-03-01T00:45:00Z");

		List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet23, tweet24), new Timespan(testStart, testEnd));

		assertFalse("expected non-empty list", inTimespan.isEmpty());
		assertTrue("expected list to contain tweets", inTimespan.containsAll(Arrays.asList(tweet23, tweet24)));
		assertEquals("expected same order", 0, inTimespan.indexOf(tweet23));
	}

	private static final Instant d25 = Instant.parse("1972-03-01T00:45:00Z");
	private static final Instant d26 = Instant.parse("1971-01-01T00:00:00Z");

	private static final Tweet tweet25 = new Tweet(25, "Adam", "is it reasonable to talk about rivest so much?", d25);
	private static final Tweet tweet26 = new Tweet(26, "Chris", "rivest talk in 30 minutes #hype", d26);

	// covers timespan start and end latter than current date
	@Test
	public void testInTimespanMultipleTweetsMultipleResultsTimespanLatterThanCurrent() throws NullTimestampException {
		Instant testStart = Instant.parse("2022-03-01T00:45:00Z");
		Instant testEnd = Instant.parse("2023-01-01T00:00:00Z");

		List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet25, tweet26), new Timespan(testStart, testEnd));

		assertTrue("expected non-empty list", inTimespan.isEmpty());
	}

	private static final Instant d27 = Instant.parse("1972-03-01T00:45:00Z");
	private static final Instant d28 = Instant.parse("1971-01-01T00:00:00Z");

	private static final Tweet tweet27 = new Tweet(27, "Adam", "is it reasonable to talk about rivest so much?", d27);
	private static final Tweet tweet28 = new Tweet(28, "Chris", "rivest talk in 30 minutes #hype", d28);

	// covers timespan earlier than epoch
	@Test
	public void testInTimespanMultipleTweetsMultipleResultsTimespanEarlierThanEpoch() throws NullTimestampException {
		Instant testStart = Instant.parse("1962-03-01T00:45:00Z");
		Instant testEnd = Instant.parse("1963-01-01T00:00:00Z");

		List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet27, tweet28), new Timespan(testStart, testEnd));

		assertTrue("expected non-empty list", inTimespan.isEmpty());
	}

	private static final Instant d29 = Instant.parse("1972-03-01T00:45:00Z");
	private static final Instant d30 = Instant.parse("1971-01-01T00:00:00Z");

	private static final Tweet tweet29 = new Tweet(29, "Adam", "is it reasonable to talk about rivest so much?", d29);
	private static final Tweet tweet30 = new Tweet(30, "Chris", "rivest talk in 30 minutes #hype", d30);

	// covers timespan is null
	@Test
	public void testInTimespanMultipleTweetsMultipleResultsTimespanIsNull() throws NullTimestampException {
		exceptionRule.expect(NullPointerException.class);

		List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet29, tweet30), new Timespan(null, null));

	}

	private static final Instant d31 = Instant.parse("1972-03-01T00:45:00Z");
	private static final Instant d32 = Instant.parse("1971-01-01T00:00:00Z");

	private static final Tweet tweet31 = new Tweet(31, "Adam", "is it reasonable to talk about rivest so much?", d31);
	private static final Tweet tweet32 = new Tweet(32, "Chris", "rivest talk in 30 minutes #hype", d32);

	@Test
	public void testContaining() throws NullOrEmptyWordException, NullOrEmptyTextOfTweetException {
		List<Tweet> containing = Filter.containing(Arrays.asList(tweet31, tweet32), Arrays.asList("talk", "about", "minutes"));

		assertFalse("expected non-empty list", containing.isEmpty());
		assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet31, tweet32)));
		assertEquals("expected same order", 0, containing.indexOf(tweet31));
	}

	private static final Instant d33 = Instant.parse("1972-03-01T00:45:00Z");
	private static final Instant d34 = Instant.parse("1971-01-01T00:00:00Z");

	private static final Tweet tweet33 = new Tweet(33, "Adam", null, d33);
	private static final Tweet tweet34 = new Tweet(34, "Chris", "rivest talk in 30 minutes #hype", d34);

	// covers text of string is null
	@Test
	public void testContainingTextOfTweetIsNull() throws NullOrEmptyWordException, NullOrEmptyTextOfTweetException {
		exceptionRule.expect(NullPointerException.class);
		List<Tweet> containing = Filter.containing(Arrays.asList(tweet33, tweet34), Arrays.asList("talk"));

	}

	private static final Instant d35 = Instant.parse("1972-03-01T00:45:00Z");
	private static final Instant d36 = Instant.parse("1971-01-01T00:00:00Z");

	private static final Tweet tweet35 = new Tweet(35, "Adam", "", d35);
	private static final Tweet tweet36 = new Tweet(36, "Chris", "rivest talk in 30 minutes #hype", d36);

	// covers text of string is empty string
	@Test
	public void testContainingTextOfTweetIsEmptyString() throws NullOrEmptyWordException, NullOrEmptyTextOfTweetException {
		exceptionRule.expect(NullOrEmptyTextOfTweetException.class);
		exceptionRule.expectMessage("text of tweet is empty string");
		List<Tweet> containing = Filter.containing(Arrays.asList(tweet35, tweet36), Arrays.asList("talk"));

	}

	private static final Instant d37 = Instant.parse("1972-03-01T00:45:00Z");
	private static final Instant d38 = Instant.parse("1971-01-01T00:00:00Z");

	private static final Tweet tweet37 = new Tweet(37, "Adam", "is it reasonable to talk about rivest so much?", d37);
	private static final Tweet tweet38 = new Tweet(38, "Chris", "rivest talk in 30 minutes #hype", d38);

	// covers text of string doesn't contain word
	@Test
	public void testContainingTextOfTweetContainNoWord() throws NullOrEmptyWordException, NullOrEmptyTextOfTweetException {
		List<Tweet> containing = Filter.containing(Arrays.asList(tweet37, tweet38), Arrays.asList("greatest", "wonderful"));

		assertTrue("expected non-empty list", containing.isEmpty());
	}

	private static final Instant d39 = Instant.parse("1972-03-01T00:45:00Z");
	private static final Instant d40 = Instant.parse("1971-01-01T00:00:00Z");

	private static final Tweet tweet39 = new Tweet(39, "Adam", "is it reasonable to talk about rivest so much?", d39);
	private static final Tweet tweet40 = new Tweet(40, "Chris", "rivest talk in 30 minutes #hype", d40);

	// covers text of string contain one word
	@Test
	public void testContainingTextOfTweetContainOneWord() throws NullOrEmptyWordException, NullOrEmptyTextOfTweetException {
		List<Tweet> containing = Filter.containing(Arrays.asList(tweet39, tweet40), Arrays.asList("much"));

		assertFalse("expected non-empty list", containing.isEmpty());
		assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet39)));
		assertEquals("expected same order", 0, containing.indexOf(tweet39));
	}

	private static final Instant d41 = Instant.parse("1972-03-01T00:45:00Z");
	private static final Instant d42 = Instant.parse("1971-01-01T00:00:00Z");

	private static final Tweet tweet41 = new Tweet(39, "Adam", "is it reasonable to talk about rivest so much? yes, You should talk to him.", d41);
	private static final Tweet tweet42 = new Tweet(40, "Chris", "rivest talk in 30 minutes #hype", d42);

	// covers text of string contain several same words
	@Test
	public void testContainingTextOfTweetContainSeveralSameWords() throws NullOrEmptyWordException, NullOrEmptyTextOfTweetException {
		List<Tweet> containing = Filter.containing(Arrays.asList(tweet41, tweet42), Arrays.asList("talk"));

		assertFalse("expected non-empty list", containing.isEmpty());
		assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet41, tweet42)));
		assertEquals("expected same order", 0, containing.indexOf(tweet41));
	}

	private static final Instant d43 = Instant.parse("1972-03-01T00:45:00Z");
	private static final Instant d44 = Instant.parse("1971-01-01T00:00:00Z");

	private static final Tweet tweet43 = new Tweet(43, "Adam", "is it reasonable to talk about rivest so much? yes, You should talk to him.", d43);
	private static final Tweet tweet44 = new Tweet(44, "Chris", "rivest talk in 30 minutes #hype", d44);

	// covers text of string contain null
	@Test
	public void testContainingWordsContainNull() throws NullOrEmptyWordException, NullOrEmptyTextOfTweetException {
		exceptionRule.expect(NullOrEmptyWordException.class);
		exceptionRule.expectMessage("words contain null or empty string");
		List<Tweet> containing = Filter.containing(Arrays.asList(tweet43, tweet44), Arrays.asList(null, null));

	}

	// covers word contain empty string
	@Test
	public void testContainingWordsContainEmptyString() throws NullOrEmptyWordException, NullOrEmptyTextOfTweetException {
		exceptionRule.expect(NullOrEmptyWordException.class);
		exceptionRule.expectMessage("words contain null or empty string");
		List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList(""));
	}

	// covers word contain empty characters
	@Test
	public void testContainingWordsContainEmptyCharacters() throws NullOrEmptyWordException, NullOrEmptyTextOfTweetException {
		exceptionRule.expect(NullOrEmptyWordException.class);
		exceptionRule.expectMessage("words contain null or empty string");
		List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("a bc e"));

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
