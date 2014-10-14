package com.example.read0r;

import java.util.LinkedList;
import java.util.Queue;

import android.R.integer;

import com.example.read0r.Interfaces.IRead0rQueue;
import com.example.read0r.Models.ReadableBook;

public class Read0rQueue implements IRead0rQueue {

	private Queue<Read0rWord> mWords = new LinkedList<Read0rWord>();
	
	public Read0rWord getNext() {
		return this.mWords.poll();
	}

	public void add(Read0rWord word) {
		this.mWords.add(word);
	}

	public int count() {
		return this.mWords.size();
	}

	public int getCharSum() {
		int num = 0;
		for (Read0rWord wrd : mWords) {
			num += wrd.getWord().length();
			num ++; // for the space character of that word
		}
		return num;
	}

}
