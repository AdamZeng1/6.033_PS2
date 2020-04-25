package twitter;

public class TimeInterval implements Comparable<TimeInterval> {
	private Tweet tweet1;
	private Tweet tweet2;
	private long secondsOfTimeInterval;

	public TimeInterval(Tweet tweet1, Tweet tweet2) {
		if (tweet1.getTimestamp().getEpochSecond() > tweet2.getTimestamp().getEpochSecond()) {
			this.tweet1 = tweet2;
			this.tweet2 = tweet1;
		}

		this.tweet1 = tweet1;
		this.tweet2 = tweet2;

		this.secondsOfTimeInterval = this.tweet2.getTimestamp().getEpochSecond() - this.tweet1.getTimestamp().getEpochSecond();

	}

	public Tweet getTweet1() {
		return tweet1;
	}

	public Tweet getTweet2() {
		return tweet2;
	}

	public long getSecondsOfTimeInterval() {
		return secondsOfTimeInterval;
	}

	public void setTweet1(Tweet tweet1) {
		this.tweet1 = tweet1;
	}

	public void setTweet2(Tweet tweet2) {
		this.tweet2 = tweet2;
	}

	public void setSecondsOfTimeInterval(long secondsOfTimeInterval) {
		this.secondsOfTimeInterval = secondsOfTimeInterval;
	}

	@Override
	public int compareTo(TimeInterval o) {
		if (this.secondsOfTimeInterval - o.secondsOfTimeInterval > 0) return 1;
		if (this.secondsOfTimeInterval - o.secondsOfTimeInterval < 0) return -1;
		else return 0;
	}
}
