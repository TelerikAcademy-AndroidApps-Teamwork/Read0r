package com.example.read0r;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import android.R.integer;

import com.example.read0r.Interfaces.IDocumentReader;
import com.example.read0r.Interfaces.IRead0rQueue;

public class Read0rQueueHandler {
	private IRead0rQueue queue;
	private IDocumentReader reader;
	private int partitionSize;
	private int currentIndex;

	public Read0rQueueHandler(IRead0rQueue queue, IDocumentReader reader) {
		this.queue = queue;
		this.reader = reader;
		this.currentIndex = 0;
		this.partitionSize = 2000;

		init();
	}

	public Read0rQueueHandler(IRead0rQueue queue, IDocumentReader reader,
			int startIndex) {
		this.queue = queue;
		this.reader = reader;
		this.currentIndex = startIndex;
		this.partitionSize = 2000;
	}

	public Read0rQueueHandler(IRead0rQueue queue, IDocumentReader reader,
			int startIndex, int partitionSize) {
		this.queue = queue;
		this.reader = reader;
		this.currentIndex = startIndex;
		this.partitionSize = partitionSize;
	}

	public boolean isDocumentOver() {
		return this.queue.count() == 0;
	}

	public Read0rWord getNextWord() {
		if (this.queue.count() == 0) {
			Read0rWord word = new Read0rWord("end of document.");
			word.setMilliSeconds(-1);
			return word;
		}

		Read0rWord result = this.queue.getNext();

		if (this.queue.count() <= 50) {
			if (!this.reader.endReached()) {
				loadMoreWords();
			}
		}
		return result;
	}

	public int getCurrentPosition() {
		return this.reader.getCurrentPosition() - this.queue.getCharSum();
	}

	private void init() {
		reader.setPortionSize(this.partitionSize);
		loadMoreWords();
	}

	private void loadMoreWords() {
		List<String> words = reader.getNextWordPortion(this.currentIndex);
		for (String word : words) {
			Read0rWord queueItem = new Read0rWord(word);
			this.queue.add(queueItem);
		}
		this.currentIndex = reader.getCurrentPosition();
	}
}
