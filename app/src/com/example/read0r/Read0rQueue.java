package com.example.read0r;

import java.util.LinkedList;
import java.util.Queue;

import com.example.read0r.Interfaces.IRead0rQueue;
import com.example.read0r.Models.ReadableBook;

public class Read0rQueue implements IRead0rQueue {

	Queue<Read0rWord> words = new LinkedList<Read0rWord>();
	
	public Read0rWord getNext() {
		return this.words.poll();
	}

	public void add(Read0rWord word) {
		this.words.add(word);
	}

	public int count() {
		return this.words.size();
	}

}
