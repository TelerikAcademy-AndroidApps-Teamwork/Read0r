package com.example.read0r.Fakes;

import java.util.ArrayList;

import android.os.Environment;

import com.example.read0r.Activities.DownloadActivity;
import com.example.read0r.Interfaces.IDistantDataHandler;
import com.example.read0r.Models.DownloadableBook;
import com.example.read0r.Models.ReadableBook;
import com.telerik.everlive.sdk.core.EverliveApp;

public class FakeDistantDataHandler implements IDistantDataHandler {

	ArrayList<DownloadableBook> mBooks = new ArrayList<DownloadableBook>();
	private DownloadActivity context;
	
	public FakeDistantDataHandler(DownloadActivity context) {
		this.context = context;

		this.mBooks.add(new DownloadableBook("about_speed_reading.read0r",
				"Fast Reading Description", "???", 4, "science"));
		this.mBooks.add(new DownloadableBook("hypno.read0r", "Hello trainer",
				"???", 1, "fiction"));
		this.mBooks.add(new DownloadableBook("the_show_must_go_on.read0r",
				"The Show Must Go On", "???", 1, "fiction"));
		this.mBooks.add(new DownloadableBook("are_you_the_killer.read0r",
				"Are you the killer?", "???", 1, "thriller"));
		this.mBooks.add(new DownloadableBook("the_genious_inside_you.read0r",
				"The genious inside you", "???", 1, "self improvment"));
		this.mBooks.add(new DownloadableBook("about_speed_reading.read0r",
				"Fast Reading Description", "???", 4, "science"));
		this.mBooks.add(new DownloadableBook("hypno.read0r", "Hello trainer",
				"???", 1, "fiction"));
		this.mBooks.add(new DownloadableBook("the_show_must_go_on.read0r",
				"The Show Must Go On", "???", 1, "fiction"));
		this.mBooks.add(new DownloadableBook("are_you_the_killer.read0r",
				"Are you the killer?", "???", 1, "thriller"));
		this.mBooks.add(new DownloadableBook("the_genious_inside_you.read0r",
				"The genious inside you", "???", 1, "self improvment"));
		this.mBooks.add(new DownloadableBook("about_speed_reading.read0r",
				"Fast Reading Description", "???", 4, "science"));
		this.mBooks.add(new DownloadableBook("hypno.read0r", "Hello trainer",
				"???", 1, "fiction"));
		this.mBooks.add(new DownloadableBook("the_show_must_go_on.read0r",
				"The Show Must Go On", "???", 1, "fiction"));
		this.mBooks.add(new DownloadableBook("are_you_the_killer.read0r",
				"Are you the killer?", "???", 1, "thriller"));
		this.mBooks.add(new DownloadableBook("the_genious_inside_you.read0r",
				"The genious inside you", "???", 1, "self improvment"));
	}

	public ArrayList<String> getCategories() {
		ArrayList<String> cats = new ArrayList<String>();

		for (DownloadableBook book : this.mBooks) {
			if (!cats.contains(book.category)) {
				cats.add(book.category);
			}
		}

		return cats;
	}

	public void getBooks() {
		this.context.updateContent(this.mBooks);
	}

	public void getFilteredBooks(
			ArrayList<String> categories) {
		ArrayList<DownloadableBook> filtered = new ArrayList<DownloadableBook>();

		for (DownloadableBook downloadableBook : this.mBooks) {
			if (categories.contains(downloadableBook.category)) {
				filtered.add(downloadableBook);
			}
		}

		this.context.updateContent(filtered);
	}

}
