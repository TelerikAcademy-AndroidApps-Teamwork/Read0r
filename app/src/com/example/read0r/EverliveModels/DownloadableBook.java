package com.example.read0r.EverliveModels;

public class DownloadableBook {
	public DownloadableBook() {

	}

	public DownloadableBook(String downloadAddress, String title, String author, int pages,
			String category) {
		this.downloadAddress = downloadAddress;
		this.title = title;
		this.author = author;
		this.pages = pages;
		this.category = category;
	}

	String downloadAddress;
	String title;
	String author;
	int pages;
	String category;
}
