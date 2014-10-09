package com.example.read0r.Models;

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

	public int id;
	public String fileAddress;
	public String title;
	public String author;
	public int pages;
	public String category;

	public long positionPointer;
}
