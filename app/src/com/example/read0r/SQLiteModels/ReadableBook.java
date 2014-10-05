package com.example.read0r.SQLiteModels;

public class ReadableBook {
	public ReadableBook() {

	}

	public ReadableBook(String fileAddress, String title, String author, int pages,
			String category, long positionPointer) {
		this.fileAddress = fileAddress;
		this.title = title;
		this.author = author;
		this.pages = pages;
		this.category = category;
		
		this.positionPointer = positionPointer;
	}

	String fileAddress;
	String title;
	String author;
	int pages;
	String category;

	long positionPointer;
}
