package com.example.read0r.Fakes;

import java.util.ArrayList;

import android.os.Environment;

import com.example.read0r.EverliveModels.DownloadableBook;
import com.example.read0r.Interfaces.IDistantDataHandler;
import com.example.read0r.SQLiteModels.ReadableBook;

public class FakeDistantDataHandler implements IDistantDataHandler {

	ArrayList<DownloadableBook> books = new ArrayList<DownloadableBook>();

	public FakeDistantDataHandler() {

		this.books.add(new DownloadableBook("http//fake.com",
				"About_speed_reading.read0r", "Fast Reading Description",
				"???", 4, "science"));

		this.books.add(new DownloadableBook("http//fake.com", "hypno.read0r",
				"Hallo, trainer", "???", 1, "fiction"));

		this.books.add(new DownloadableBook("http//fake.com",
				"The_Show_Must_Go_On.read0r", "The Show Must Go On", "???", 1,
				"fiction"));
	}

	public ArrayList<String> getCategories() {
		ArrayList<String> cats = new ArrayList<String>();

		for (DownloadableBook book : this.books) {
			if (!cats.contains(book.category)) {
				cats.add(book.category);
			}
		}

		return cats;
	}

	public ArrayList<DownloadableBook> getBooks() {
		return this.books;
	}

	public ArrayList<DownloadableBook> getFilteredBooks(ArrayList<String> categories) {
		ArrayList<DownloadableBook> filtered = new ArrayList<DownloadableBook>();
		
		for (DownloadableBook downloadableBook : this.books) {
			if (categories.contains(downloadableBook.category)) {
				filtered.add(downloadableBook);
			}
		}
		
		return filtered;
	}

}
