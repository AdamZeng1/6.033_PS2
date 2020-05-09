package twitter;

import javafx.util.Pair;
import twitter.exception.UnqualifiedUsernameException;

import java.util.*;

/**
 * SocialNetwork provides methods that operate on a social network.
 * <p>
 * A social network is represented by a Map<String, Set<String>> where map[A] is
 * the set of people that person A follows on Twitter, and all people are
 * represented by their Twitter usernames. Users can't follow themselves. If A
 * doesn't follow anybody, then map[A] may be the empty set, or A may not even exist
 * as a key in the map; this is true even if A is followed by other people in the network.
 * Twitter usernames are not case sensitive, so "ernie" is the same as "ERNie".
 * A username should appear at most once as a key in the map or in any given
 * map[A] set.
 * <p>
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class SocialNetwork {

	/**
	 * Guess who might follow whom, from evidence found in tweets.
	 *
	 * @param tweets a list of tweets providing the evidence, not modified by this
	 *               method.
	 * @return a social network (as defined above) in which Ernie follows Bert
	 * if and only if there is evidence for it in the given list of
	 * tweets.
	 * One kind of evidence that Ernie follows Bert is if Ernie
	 * @-mentions Bert in a tweet. This must be implemented. Other kinds
	 * of evidence may be used at the implementor's discretion.
	 * All the Twitter usernames in the returned social network must be
	 * either authors or @-mentions in the list of tweets.
	 */
	public static Map<String, Set<String>> guessFollowsGraph(List<Tweet> tweets) throws UnqualifiedUsernameException {
		Map<String, Set<String>> result = new HashMap<>();
		if (!isTweetAuthorQualified(tweets)) {
			throw new UnqualifiedUsernameException("author name is not valid");
		}

		for (Tweet tweet : tweets) {
			String author = tweet.getAuthor().toUpperCase();
			Set<String> mentionedUsers = Extract.getMentionedUsersOfSingleTweet(tweet);
			mentionedUsers = Filter.convertToUppercase(mentionedUsers);
			if (mentionedUsers.contains(author)) {
				mentionedUsers.remove(author);
			}
			if (result.keySet().contains(author)) {
				Set<String> latterMentionedUsers = new HashSet<>();
				Set<String> previousMentionedUsers = result.get(author);
				latterMentionedUsers.addAll(previousMentionedUsers);
				latterMentionedUsers.addAll(mentionedUsers);
				result.put(author, latterMentionedUsers);
			} else {
				result.put(author, mentionedUsers);
			}
		}
		addMutualFollowers(result);
		return result;

	}


	private static Map<String, Set<String>> addMutualFollowers(Map<String, Set<String>> followGraphWithoutMutualFollowers) {
		Map<String, Set<String>> peopleFollowingPerson = getPeopleFollowingPerson(followGraphWithoutMutualFollowers);
		Map<String, Set<String>> mutualFollowers = getMutualFollowers(peopleFollowingPerson, followGraphWithoutMutualFollowers);
		List<Pair<String, String>> triadicClosurePeople = getTriadicClosurePeople(mutualFollowers);
		Set<String> peopleFollowedByPerson;
		for (Pair<String, String> triadicClosurePerson : triadicClosurePeople) {
			String personKey = triadicClosurePerson.getKey();
			String personValue = triadicClosurePerson.getValue();
			peopleFollowedByPerson = followGraphWithoutMutualFollowers.get(personKey);
			peopleFollowedByPerson.add(personValue);
		}
		return followGraphWithoutMutualFollowers;
	}

	/**
	 * @param peopleFollowedByPerson Key: A , Value: people who are followed by A.
	 * @return Key: A , Value: people who follow A.
	 */
	private static Map<String, Set<String>> getPeopleFollowingPerson(Map<String, Set<String>> peopleFollowedByPerson) {
		Map<String, Set<String>> result = new HashMap<>(1000);
		Set<String> peopleFollowedBySecondSpecificPerson;
		Set<String> peopleFollowingFirstSpecificPerson;
		// traversal followedOfPeople
		for (String firstSpecificPeople : peopleFollowedByPerson.keySet()) {
			peopleFollowingFirstSpecificPerson = new HashSet<>(1000);
			for (String secondSpecificPeople : peopleFollowedByPerson.keySet()) {
				if (secondSpecificPeople.equals(firstSpecificPeople)) {
					continue;
				}
				peopleFollowedBySecondSpecificPerson = peopleFollowedByPerson.get(secondSpecificPeople);
				if (peopleFollowedBySecondSpecificPerson.contains(firstSpecificPeople)) {
					peopleFollowingFirstSpecificPerson.add(secondSpecificPeople);
				}
			}
			result.put(firstSpecificPeople, peopleFollowingFirstSpecificPerson);
		}
		return result;
	}

	/**
	 * get the mutual follow relationship from peopleFollowingPerson, peopleFollowedByPerson
	 *
	 * @param peopleFollowingPerson  Key: A Value: people who are following A.
	 * @param peopleFollowedByPerson Key: A Value: people who are followed by A.
	 * @return Key: A Value: people who follow A and A follow people too.
	 */
	private static Map<String, Set<String>> getMutualFollowers(Map<String, Set<String>> peopleFollowingPerson, Map<String, Set<String>> peopleFollowedByPerson) {
		Map<String, Set<String>> result = new HashMap<>();
		Set<String> mutualFollowersOfSpecificPerson;
		Set<String> peopleFollowingSpecificPerson;
		Set<String> peopleFollowedBySpecificPerson;
		for (String specificPeople : peopleFollowedByPerson.keySet()) {
			peopleFollowingSpecificPerson = peopleFollowingPerson.get(specificPeople);
			peopleFollowedBySpecificPerson = peopleFollowedByPerson.get(specificPeople);
			mutualFollowersOfSpecificPerson = new HashSet<>(peopleFollowedBySpecificPerson);
			mutualFollowersOfSpecificPerson.retainAll(peopleFollowingSpecificPerson);
			result.put(specificPeople, mutualFollowersOfSpecificPerson);
		}
		return result;
	}

	/**
	 * reference:https://en.wikipedia.org/wiki/Triadic_closure if A->B and B->A and B->C and C->B, A and C become Triadic Closure
	 *
	 * @param mutualFollowersByPerson Key: A Value: people who follow A and A follow people too.
	 * @return Key: A Value: C | A and C become Triadic Closure
	 */
	public static List<Pair<String, String>> getTriadicClosurePeople(Map<String, Set<String>> mutualFollowersByPerson) {
		List<Pair<String, String>> result = new ArrayList<>(1000);
		Pair<String, String> specificMutualPair;
		Set<String> mutualFollowersByFirstSpecificPerson;
		Set<String> mutualFollowersBySecondSpecificPerson;
		Set<String> intersectionBetweenTwoMutualFollowers;
		for (String firstSpecificPerson : mutualFollowersByPerson.keySet()) {
			mutualFollowersByFirstSpecificPerson = mutualFollowersByPerson.get(firstSpecificPerson);

			for (String secondSpecificPerson : mutualFollowersByPerson.keySet()) {
				if (firstSpecificPerson.equals(secondSpecificPerson)) {
					continue;
				}
				mutualFollowersBySecondSpecificPerson = mutualFollowersByPerson.get(secondSpecificPerson);
				intersectionBetweenTwoMutualFollowers = new HashSet<>(mutualFollowersByFirstSpecificPerson);
				intersectionBetweenTwoMutualFollowers.retainAll(mutualFollowersBySecondSpecificPerson);
				if (!mutualFollowersByFirstSpecificPerson.isEmpty()) {
					specificMutualPair = new Pair<>(firstSpecificPerson, secondSpecificPerson);
					result.add(specificMutualPair);
				}
			}
		}
		return result;
	}


	private static boolean isTweetAuthorQualified(List<Tweet> tweets) throws UnqualifiedUsernameException {
		for (Tweet tweet : tweets) {
			if (!Filter.isRequiredUsername(tweet.getAuthor())) {
				return false;
			}
		}
		return true;

	}

	/**
	 * Find the people in a social network who have the greatest influence, in
	 * the sense that they have the most followers.
	 *
	 * @param followsGraph a social network (as defined above)
	 * @return a list of all distinct Twitter usernames in followsGraph, in
	 * descending order of follower count.
	 */
	public static List<String> influencers(Map<String, Set<String>> followsGraph) {
		Map<String, Integer> followersNumberOfUsers = new HashMap<String, Integer>();
		for (String author : followsGraph.keySet()) {
			Set<String> followedUsers = followsGraph.get(author);
			for (String username : followedUsers) {
				if (followersNumberOfUsers.containsKey(username)) {
					int number = followersNumberOfUsers.get(username);
					number++;
					followersNumberOfUsers.put(username, number);
				} else {
					followersNumberOfUsers.put(username, 1);
				}
			}
		}

		List<String> result = convertMapToOrderList(followersNumberOfUsers);
		return result;
	}


	private static List<String> convertMapToOrderList(Map<String, Integer> followersNumberOfUsers) {
		List<String> result = new ArrayList<>(1000);
		Set<Map.Entry<String, Integer>> entries = followersNumberOfUsers.entrySet();

		Comparator<Map.Entry<String, Integer>> valueComparator = new Comparator<Map.Entry<String, Integer>>() {
			@Override
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				if (o1.getValue() > o2.getValue()) {
					return -1;
				} else if (o1.getValue() < o2.getValue()) {
					return 1;
				} else {
					if (o1.getKey().compareTo(o2.getKey()) > 0) {
						return 1;
					} else {
						return -1;
					}
				}
			}
		};

		// Sort method needs a List, convert Set to List in Java
		List<Map.Entry<String, Integer>> listOfEntries = new ArrayList<Map.Entry<String, Integer>>(entries);

		// Sort HashMap by values using comparator
		Collections.sort(listOfEntries, valueComparator);
		for (Map.Entry<String, Integer> entry : listOfEntries) {
			result.add(entry.getKey());
		}
		return result;
	}

	/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
	 * Redistribution of original or derived work requires explicit permission.
	 * Don't post any of this code on the web or to a public Github repository.
	 */
}
