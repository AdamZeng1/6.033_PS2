package twitter;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import twitter.exception.UnqualifiedUsernameException;

import java.time.Instant;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SocialNetworkTest {

	/*
	 * partition:
	 * author doesn't cater rule of naming
	 * username doesn't cater rule of naming
	 * a tweet's author equals to @-mentioned username
	 * text of tweets contains no @-mention
	 *
	 */
	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

	@Test(expected = AssertionError.class)
	public void testAssertionsEnabled() {
		assert false; // make sure assertions are enabled with VM argument: -ea
	}

	@Test
	public void testGuessFollowsGraphEmpty() throws UnqualifiedUsernameException {
		Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<>());

		assertTrue("expected empty graph", followsGraph.isEmpty());
	}


	private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
	private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");

	private static final Tweet tweet1 = new Tweet(1, "233*+=", "is it reasonable to talk about rivest so much?", d1);
	private static final Tweet tweet2 = new Tweet(2, "//dswew#*", "rivest talk in 30 minutes #hype", d2);

	// covers author doesn't cater rule of naming
	@Test
	public void testGuessFollowsGraphAuthorNotCaterNamingRule() throws UnqualifiedUsernameException {
		exceptionRule.expect(UnqualifiedUsernameException.class);
		exceptionRule.expectMessage("author name is not valid");
		Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet1, tweet2));

		assertTrue("expected empty graph", followsGraph.isEmpty());
	}

	private static final Instant d5 = Instant.parse("2016-02-17T10:00:00Z");
	private static final Instant d6 = Instant.parse("2016-02-17T11:00:00Z");

	private static final Tweet tweet5 = new Tweet(1, "Adam", "is it reasonable to @aDam talk about rivest so much?", d5);
	private static final Tweet tweet6 = new Tweet(2, "ChRis", "rivest talk in @Chris 30 minutes #hype", d6);
	private static final Tweet tweet100 = new Tweet(2, "ChRis", "rivest talk in @Michael 30 minutes #hype", d6);

	// covers author equals to @-mentioned username author = 'Adam' | text of tweet = '@Adam'
	@Test
	public void testGuessFollowsGraphUsernameEqualsAuthor() throws UnqualifiedUsernameException {
		Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet5, tweet6, tweet100));

		assertEquals("expected size", 2, followsGraph.size());
		assertTrue("expected key", followsGraph.containsKey("CHRIS"));
		assertTrue("expected value", followsGraph.get("CHRIS").contains("MICHAEL"));

	}

	private static final Instant d7 = Instant.parse("2016-02-17T10:00:00Z");
	private static final Instant d8 = Instant.parse("2016-02-17T11:00:00Z");

	private static final Tweet tweet7 = new Tweet(1, "Adam", "is it reasonable to talk about rivest so much?", d7);
	private static final Tweet tweet8 = new Tweet(2, "ChRis", "rivest talk in 30 minutes #hype", d8);

	// covers text of tweets contains no @-mention
	@Test
	public void testGuessFollowsGraphTextContainsNoUsername() throws UnqualifiedUsernameException {
		Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet7, tweet8));

		assertEquals("expected size", 2, followsGraph.size());
		assertTrue("expected key", followsGraph.get("ADAM").isEmpty());
		assertTrue("expected value", followsGraph.get("CHRIS").isEmpty());
	}


	private static final Instant d9 = Instant.parse("2016-02-17T10:00:00Z");
	private static final Instant d10 = Instant.parse("2016-02-17T11:00:00Z");

	private static final Tweet tweet9 = new Tweet(1, "Adam", "is it reasonable @Michael to @Sheley talk about rivest so much?", d9);
	private static final Tweet tweet10 = new Tweet(2, "ChRis", "rivest talk in @Francis 30 @Jhonason minutes #hype", d10);

	// covers text contain same case-insensitive username e.g @Adam @adAm
	@Test
	public void testGuessFollowsGraphTextContainsCaseInsensitiveUsername() throws UnqualifiedUsernameException {
		Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet9, tweet10));

		assertEquals("expected size", 2, followsGraph.size());
		assertTrue("expected key", followsGraph.containsKey("ADAM"));
		assertTrue("expected key", followsGraph.containsKey("CHRIS"));
		assertTrue("expected value", followsGraph.get("CHRIS").contains("JHONASON"));
		assertTrue("expected value", followsGraph.get("CHRIS").contains("FRANCIS"));
		assertTrue("expected value", followsGraph.get("ADAM").contains("MICHAEL"));
		assertTrue("expected value", followsGraph.get("ADAM").contains("SHELEY"));
	}

	private static final Instant d21 = Instant.parse("2016-02-17T10:00:00Z");
	private static final Instant d22 = Instant.parse("2016-02-17T11:00:00Z");
	private static final Instant d23 = Instant.parse("2016-02-17T11:00:00Z");

	private static final Tweet tweet21 = new Tweet(1, "Adam", "is it reasonable @Michael to @Sheley talk about rivest so much?", d21);
	private static final Tweet tweet22 = new Tweet(2, "ChRis", "rivest talk in @Francis 30 @sHeLey minutes #hype", d22);
	private static final Tweet tweet23 = new Tweet(3, "Sheley", "rivest talk in @Adam 30 @ChRis minutes #hype", d23);


	// covers text contain same case-insensitive username e.g @Adam @adAm
	@Test
	public void testGuessFollowsGraphTextContainsTriadicClosurePair() throws UnqualifiedUsernameException {
		Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet21, tweet22, tweet23));

		assertEquals("expected size", 3, followsGraph.size());
		assertTrue("expected key", followsGraph.containsKey("ADAM"));
		assertTrue("expected key", followsGraph.containsKey("CHRIS"));
		assertTrue("expected key", followsGraph.containsKey("SHELEY"));
		assertTrue("expected value", followsGraph.get("CHRIS").contains("SHELEY"));
		assertTrue("expected value", followsGraph.get("CHRIS").contains("FRANCIS"));
		assertTrue("expected value", followsGraph.get("ADAM").contains("MICHAEL"));
		assertTrue("expected value", followsGraph.get("ADAM").contains("SHELEY"));
		assertTrue("expected value", followsGraph.get("ADAM").contains("CHRIS"));
		assertTrue("expected value", followsGraph.get("CHRIS").contains("ADAM"));

	}

	@Test
	public void testInfluencersEmpty() {
		Map<String, Set<String>> followsGraph = new HashMap<>();
		List<String> influencers = SocialNetwork.influencers(followsGraph);

		assertTrue("expected empty list", influencers.isEmpty());
	}


	private static final Set<String> firstUsersGroup = new HashSet<>(Arrays.asList("JHONASON", "FRANCIS", "CHRISTY", "JUDY", "MICHAEL"));
	private static final Set<String> secondUsersGroup = new HashSet<>(Arrays.asList("GACHER", "JHONASON", "TIGER", "FIAS", "LUMIA"));
	private static final Set<String> thirdUsersGroup = new HashSet<>(Arrays.asList("KIA", "FRANCIS", "BILL", "MICHAEL", "JHONASON"));

	// covers followsGraph contain two author and each contain five followed users
	@Test
	public void testInfluencers() {
		Map<String, Set<String>> followsGraph = new HashMap<>();
		followsGraph.put("ADAM", firstUsersGroup);
		followsGraph.put("CHRIS", secondUsersGroup);
		followsGraph.put("SIA", thirdUsersGroup);

		List<String> influencers = SocialNetwork.influencers(followsGraph);

		List<String> expectedInfluencers = Arrays.asList("JHONASON", "FRANCIS", "MICHAEL", "BILL", "CHRISTY", "FIAS", "GACHER", "JUDY", "KIA", "LUMIA", "TIGER");
		assertEquals(expectedInfluencers, influencers);
	}

	/*
	 * Warning: all the tests you write here must be runnable against any
	 * SocialNetwork class that follows the spec. It will be run against several
	 * staff implementations of SocialNetwork, which will be done by overwriting
	 * (temporarily) your version of SocialNetwork with the staff's version.
	 * DO NOT strengthen the spec of SocialNetwork or its methods.
	 *
	 * In particular, your test cases must not call helper methods of your own
	 * that you have put in SocialNetwork, because that means you're testing a
	 * stronger spec than SocialNetwork says. If you need such helper methods,
	 * define them in a different class. If you only need them in this test
	 * class, then keep them in this test class.
	 */


	/* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
	 * Redistribution of original or derived work requires explicit permission.
	 * Don't post any of this code on the web or to a public Github repository.
	 */
}
