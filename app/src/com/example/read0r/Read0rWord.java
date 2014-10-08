package com.example.read0r;

public class Read0rWord {
	public Read0rWord(String word) {
		this.setWord(word);
		this.milliSeconds = calcMilliSeconds();
	}

	private String word;
	private int milliSeconds;
	
	protected int calcMilliSeconds() {
		int result = 100 + this.getWord().length() * 20;
		return result;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
		this.milliSeconds = calcMilliSeconds();
	}

	public int getMilliSeconds() {
		return milliSeconds;
	}
	
	public void setMilliSeconds(int ms) {
		this.milliSeconds = ms;
	}
}
