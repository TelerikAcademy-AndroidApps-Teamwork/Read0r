package com.example.read0r.Interfaces;

import com.example.read0r.Read0rWord;

public interface IRead0rQueue {
	public Read0rWord getNext();
	public void add(Read0rWord word);
	public int count();
	public int getCharSum();
}
