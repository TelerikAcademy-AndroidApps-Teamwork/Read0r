package com.example.read0r;

public class Read0rWord {
	public Read0rWord(String word) {
		this.setWord(word);
		this.milliSeconds = calcMilliSeconds();
	}

	protected int calcMilliSeconds() {
		int result = 100 + this.getWord().length() * 20;
		return result;
	}

	String getWord() {
		return word;
	}

	void setWord(String word) {
		this.word = word;
		this.milliSeconds = calcMilliSeconds();
	}

	int getMilliSeconds() {
		return milliSeconds;
	}

	private String word;
	private int milliSeconds;
}
