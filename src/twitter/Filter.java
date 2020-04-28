package twitter;

import twitter.exception.NullOrEmptyAuthorException;
import twitter.exception.NullTimestampException;
import twitter.exception.UnqualifiedUsernameException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Filter consists of methods that filter a list of tweets for those matching a
 * condition.
 * <p>
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class Filter {

	/**
	 * Find tweets written by a particular user.
	 *
	 * @param tweets   a list of tweets with distinct ids, not modified by this method.
	 * @param username Twitter username, required to be a valid Twitter username as
	 *                 defined by Tweet.getAuthor()'s spec.
	 * @return all and only the tweets in the list whose author is username,
	 * in the same order as in the input list.
	 */
	public static List<Tweet> writtenBy(List<Tweet> tweets, String username) throws UnqualifiedUsernameException, NullOrEmptyAuthorException {
		List<Tweet> result = new ArrayList<>();

		if (!isRequiredUsername(username)) {
			throw new UnqualifiedUsernameException("unqualified username");
		}

		for (Tweet tweet : tweets) {
			String author = tweet.getAuthor();
			if (author == null || author.isEmpty()) {
				throw new NullOrEmptyAuthorException("author name of tweet is null or empty string");
			}

			if (!isRequiredUsername(author)) {
				throw new UnqualifiedUsernameException("unqualified author name");
			}

			if (author.equalsIgnoreCase(username)) {
				result.add(tweet);
			}
		}

		return result;
	}

	private static boolean isRequiredUsername(String username) {
		for (int i = 0; i < username.length(); i++) {
			if (!isRequiredCharacter(username.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	private static boolean isRequiredCharacter(char c) {
		if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '-' || c == '_')
			return true;
		return false;
	}

	/**
	 * Find tweets that were sent during a particular timespan.
	 *
	 * @param tweets   a list of tweets with distinct ids, not modified by this method.
	 * @param timespan timespan
	 * @return all and only the tweets in the list that were sent during the timespan,
	 * in the same order as in the input list.
	 */
	public static List<Tweet> inTimespan(List<Tweet> tweets, Timespan timespan) throws NullTimestampException {
		List<Tweet> result = new ArrayList<>();
		if (timespan == null) {
			throw new NullTimestampException("timestamp of timespan is null");
		}

		for (Tweet tweet : tweets) {
			Instant timestamp = tweet.getTimestamp();
			Instant start = timespan.getStart();
			Instant end = timespan.getEnd();
			if (timestamp == null) {
				throw new NullTimestampException("timestamp of tweet is null");
			}

			if (timestamp.equals(start) || timestamp.equals(end)) {
				result.add(tweet);
			}

			if (timestamp.isAfter(start) && timestamp.isBefore(end)) {
				result.add(tweet);
			}
		}

		return result;
	}

	/**
	 * Find tweets that contain certain words.
	 *
	 * @param tweets a list of tweets with distinct ids, not modified by this method.
	 * @param words  a list of words to search for in the tweets.
	 *               A word is a nonempty sequence of nonspace characters.
	 * @return all and only the tweets in the list such that the tweet text (when
	 * represented as a sequence of nonempty words bounded by space characters
	 * and the ends of the string) includes *at least one* of the words
	 * found in the words list. Word comparison is not case-sensitive,
	 * so "Obama" is the same as "obama".  The returned tweets are in the
	 * same order as in the input list.
	 */
	public static List<Tweet> containing(List<Tweet> tweets, List<String> words) {
		throw new RuntimeException("not implemented");
	}

	/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
	 * Redistribution of original or derived work requires explicit permission.
	 * Don't post any of this code on the web or to a public Github repository.
	 */
}
