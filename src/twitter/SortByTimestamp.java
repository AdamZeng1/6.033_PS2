package twitter;

import java.util.Comparator;

public class SortByTimestamp implements Comparator<Tweet> {
	@Override
	public int compare(Tweet tweet1, Tweet tweet2) {
		if (tweet1.getTimestamp().isBefore(tweet2.getTimestamp())) return -1;
		if (tweet1.getTimestamp().isAfter(tweet2.getTimestamp())) return 1;
		else return 0;
	}
}
