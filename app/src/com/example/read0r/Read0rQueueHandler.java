package com.example.read0r;

import java.math.BigInteger;

import com.example.read0r.Interfaces.IDocumentReader;
import com.example.read0r.Interfaces.IRead0rQueue;

public class Read0rQueueHandler {
	private IRead0rQueue queue;
	private IDocumentReader reader;
	private int partitionSize;
	private long currentIndex;

	public Read0rQueueHandler(IRead0rQueue queue, IDocumentReader reader) {
		this.queue = queue;
		this.reader = reader;
		this.currentIndex = 0;
		this.partitionSize = 2000;

		init();
	}

	public Read0rQueueHandler(IRead0rQueue queue, IDocumentReader reader,
			long startIndex) {
		this.queue = queue;
		this.reader = reader;
		this.currentIndex = startIndex;
		this.partitionSize = 2000;
	}

	public Read0rQueueHandler(IRead0rQueue queue, IDocumentReader reader,
			long startIndex, int partitionSize) {
		this.queue = queue;
		this.reader = reader;
		this.currentIndex = startIndex;
		this.partitionSize = partitionSize;
	}

	public boolean isDocumentOver() {
		return this.queue.count() == 0;
	}
	
	public Read0rWord getNextWord() {
		Read0rWord result = this.queue.getNext();
		if (this.queue.count() <= 50) {
			loadMoreWords();
		}
		return result;
	}

	private void init() {
		reader.setPortionSize(this.partitionSize);
		loadMoreWords();
	}

	private void loadMoreWords() {
		String[] words = reader.getNextWordPortion(this.currentIndex);
		for(String word : words) {
			Read0rWord queueItem = new Read0rWord(word);
			this.queue.add(queueItem);
		}
		this.currentIndex = reader.getCurrentPosition();
	}
}
