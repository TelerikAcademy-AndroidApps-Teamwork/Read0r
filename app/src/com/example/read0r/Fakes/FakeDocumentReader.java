package com.example.read0r.Fakes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.read0r.Interfaces.IDocumentReader;

public class FakeDocumentReader implements IDocumentReader {

	private String text = "That's just a test. It's just a test. Making a test. Testing this test. That's just a test. It's just a test. Making a test. Testing this test. That's just a test. It's just a test. Making a test. Testing this test. That's just a test. It's just a test. Making a test. Testing this test. That's just a test. It's just a test. Making a test. Testing this test. That's just a test. It's just a test. Making a test. Testing this test. That's just a test. It's just a test. Making a test. Testing this test.";

	private int portionSize;
	private int position;

	@Override
	public int getCurrentPosition() {
		return this.position;
	}

	@Override
	public boolean endReached() {
		return this.endReached();
	}

	@Override
	public void setPortionSize(int portionSize) {
		this.portionSize = portionSize;
	}

	@Override
	public List<String> getNextWordPortion(int letterIndex) {
		ArrayList<String> results = new ArrayList<String>();
		int len = this.text.length() - letterIndex;
		if (len > this.portionSize) {
			len = this.portionSize;
		}
		int charsCount = addWordsOfStringToCollection(
				this.text.substring(letterIndex, len), results);
		this.position = letterIndex + charsCount;
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
		return this.text.length();
	}

}
