package com.example.read0r;

import com.example.read0r.Interfaces.IDocumentReader;
import com.example.read0r.SQLiteModels.ReadableBook;

public class DocumentReader implements IDocumentReader {

	private ReadableBook document;

	public DocumentReader(ReadableBook document) {
		this.document = document;
	}

	public long getCurrentPosition() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setPortionSize(int portionSize) {
		// TODO Auto-generated method stub
	}

	public String[] getNextWordPortion(long letterIndex) {
		// TODO Auto-generated method stub
		return null;
	}
}
