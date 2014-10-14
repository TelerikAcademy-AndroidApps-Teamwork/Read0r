package com.example.read0r;

public class Read0rWord {
	public Read0rWord(String word) {
		this.setWord(word);
		this.mMilliSeconds = calcMilliSeconds();
	}

	private static char[] mSpecialChars = { '.', '!', '?', ',', ';', ':' };
	private String mWord;
	private int mMilliSeconds;

	protected int calcMilliSeconds() {
		int result = 150 + this.getWord().length() * 50;
		for (char ch : mSpecialChars) {
			char lastCh = this.getWord().charAt(this.getWord().length() - 1);
			if (ch == lastCh) {
				result += 150;
			}
		}
		return result;
	}

	public String getWord() {
		return mWord;
	}

	public void setWord(String word) {
		this.mWord = word;
		this.mMilliSeconds = calcMilliSeconds();
	}

	public int getMilliSeconds() {
		return mMilliSeconds;
	}

	public void setMilliSeconds(int ms) {
		this.mMilliSeconds = ms;
	}
}
