package twitter;

import java.util.*;

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
		Set<String> mentionedUsernames = new HashSet<>();

		for (Tweet tweet : tweets) {
			mentionedUsernames.addAll(getMentionedUsersOfSingleTweet(tweet));
		}

		return mentionedUsernames;
	}

	private static Set<String> getMentionedUsersOfSingleTweet(Tweet tweet) {
		Set<String> mentionedUsernames = new HashSet<>();
		List<Integer> locationOfAtSymbol = getLocationOfAtSymbol(tweet);
		String text = tweet.getText();
		int sizeOfLocationOfAtSymbol = locationOfAtSymbol.size();
		int start, end;

		for (int i = 0; i < sizeOfLocationOfAtSymbol; i++) {
			start = locationOfAtSymbol.get(i);
			end = text.length();
			if (i != sizeOfLocationOfAtSymbol - 1) {
				end = locationOfAtSymbol.get(i + 1);
			}
			String mentionedUsername = generateMentionedUsernameBySingleIndex(text, start, end);
			if (!mentionedUsername.isEmpty()) {
				mentionedUsernames.add(mentionedUsername);
			}
		}
		return mentionedUsernames;
	}


	private static String generateMentionedUsernameBySingleIndex(String text, Integer start, Integer end) {
		StringBuilder mentionedUsername = new StringBuilder();
		if (start + 1 == text.length() - 1) {
			return mentionedUsername.toString();
		}

		for (int i = start + 1; i < end; i++) {
			char character = text.charAt(i);
			if (isRequiredCharacter(character)) {
				mentionedUsername.append(character);
			}
			if (!isRequiredCharacter(character) && character == ' ') {
				break;
			}
			if (!isRequiredCharacter(character) && character != ' ') {
				mentionedUsername = new StringBuilder();
				break;
			}
		}

		return mentionedUsername.toString();
	}

	private static List<Integer> getLocationOfAtSymbol(Tweet tweet) {
		List<Integer> locationOfAtSymbol = new ArrayList<>(1000);

		for (int i = 0; i < tweet.getText().length(); i++) {
			char character = tweet.getText().charAt(i);
			if (character == '@') {
				locationOfAtSymbol.add(i);
			}
		}
		return locationOfAtSymbol;
	}

	private static boolean isRequiredCharacter(char c) {
		if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '-' || c == '_')
			return true;
		return false;
	}

	/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
	 * Redistribution of original or derived work requires explicit permission.
	 * Don't post any of this code on the web or to a public Github repository.
	 */
}
