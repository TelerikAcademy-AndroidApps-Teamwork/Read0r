package com.example.read0r.Interfaces;

import java.math.BigInteger;

public interface IDocumentReader {

	public long getCurrentPosition();
	public void setPortionSize(int portionSize);
	public String[] getNextWordPortion(long letterIndex);
}
