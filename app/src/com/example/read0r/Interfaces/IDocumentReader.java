package com.example.read0r.Interfaces;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public interface IDocumentReader {

	public int getCurrentPosition();
	public boolean endReached();
	public void setPortionSize(int portionSize);
	public List<String> getNextWordPortion(int letterIndex);
}
