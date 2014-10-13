package com.example.read0r.Fakes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.read0r.Interfaces.IDocumentReader;

public class FakeDocumentReader implements IDocumentReader {

	private String mText = "That's just a test. It's just a test. Making a test. Testing this test. That's just a test. It's just a test. Making a test. Testing this test. That's just a test. It's just a test. Making a test. Testing this test. That's just a test. It's just a test. Making a test. Testing this test. That's just a test. It's just a test. Making a test. Testing this test. That's just a test. It's just a test. Making a test. Testing this test. That's just a test. It's just a test. Making a test. Testing this test.";

	private int mPortionSize;
	private int mPosition;

	@Override
	public int getCurrentPosition() {
		return this.mPosition;
	}

	@Override
	public boolean endReached() {
		return this.endReached();
	}

	@Override
	public void setPortionSize(int portionSize) {
		this.mPortionSize = portionSize;
	}

	@Override
	public List<String> getNextWordPortion(int letterIndex) {
		ArrayList<String> results = new ArrayList<String>();
		int len = this.mText.length() - letterIndex;
		if (len > this.mPortionSize) {
			len = this.mPortionSize;
		}
		int charsCount = addWordsOfStringToCollection(
				this.mText.substring(letterIndex, len), results);
		this.mPosition = letterIndex + charsCount;
		return results;
	}

	private int addWordsOfStringToCollection(String textPartition,
			List<String> result) {
		int st = 0;
		for (int end = 0; end < textPartition.length(); end++) {
			if (textPartition.charAt(end) == ' ') {
				result.add(textPartition.substring(st, end));
				st = end + 1;
			}
		}
		return st;
	}

	@Override
	public long getDocLength() {
		return this.mText.length();
	}

}
