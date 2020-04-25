package twitter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Extract consists of methods that extract information from a list of tweets.
 * <p>
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class Extract {

	/**
	 * Get the time period spanned by tweets.
	 *
	 * @param tweets list of tweets with distinct ids, not modified by this method.
	 * @return a minimum-length time interval that contains the timestamp of
	 * every tweet in the list.
	 */
	public static Timespan getTimespan(List<Tweet> tweets) {
		List<TimeInterval> timeIntervals = new ArrayList<>(1000);

		Collections.sort(tweets, new SortByTimestamp());
		int length = tweets.size();
		for (int i = 0; i < length - 1; i++) {
			timeIntervals.add(new TimeInterval(tweets.get(i), tweets.get(i + 1)));
		}

		Collections.sort(timeIntervals);
		TimeInterval timeInterval = timeIntervals.get(0);
		Timespan timespan = new Timespan(timeInterval.getTweet1().getTimestamp(), timeInterval.getTweet2().getTimestamp());

		return timespan;
	}

	/**
	 * Get usernames mentioned in a list of tweets.
	 *
	 * @param tweets list of tweets with distinct ids, not modified by this method.
	 * @return the set of usernames who are mentioned in the text of the tweets.
	 * A username-mention is "@" followed by a Twitter username (as
	 * defined by Tweet.getAuthor()'s spec).
	 * The username-mention cannot be immediately preceded or followed by any
	 * character valid in a Twitter username.
	 * For this reason, an email address like bitdiddle@mit.edu does NOT
	 * contain a mention of the username mit.
	 * Twitter usernames are case-insensitive, and the returned set may
	 * include a username at most once.
	 */
	public static Set<String> getMentionedUsers(List<Tweet> tweets) {
		throw new RuntimeException("not implemented");
	}

	/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
	 * Redistribution of original or derived work requires explicit permission.
	 * Don't post any of this code on the web or to a public Github repository.
	 */
}