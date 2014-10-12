package com.example.read0r.Models;

import java.util.UUID;

import com.telerik.everlive.sdk.core.model.base.DataItem;
import com.telerik.everlive.sdk.core.serialization.ServerIgnore;
import com.telerik.everlive.sdk.core.serialization.ServerProperty;
import com.telerik.everlive.sdk.core.serialization.ServerType;

@ServerType("DownloadableBooks")
public class DownloadableBook extends DataItem {
	public DownloadableBook() {
		this("", "", "", 0, "");
	}

	public DownloadableBook(String fileName,
		String title, String author, Number pages, String category) {

		this.fileName = fileName;

		this.title = title;
		this.author = author;
		this.pages = pages;
		this.category = category;

		this.isOwned = false;
	}
	
	@ServerProperty("Book")
    public UUID Book;
	
	@ServerProperty("fileName")
	public String fileName;
	
	@ServerProperty("title")
	public String title;
	
	@ServerProperty("author")
	public String author;
	
	@ServerProperty("pages")
	public Number pages;
	
	@ServerProperty("category")
	public String category;

	@ServerIgnore
	public boolean isOwned;
}
