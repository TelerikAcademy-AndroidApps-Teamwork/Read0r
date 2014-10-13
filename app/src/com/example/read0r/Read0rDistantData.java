package com.example.read0r;

import java.util.ArrayList;

import android.util.Log;

import com.example.read0r.Interfaces.IDistantDataHandler;
import com.example.read0r.Models.DownloadableBook;
import com.telerik.everlive.sdk.core.EverliveApp;
import com.telerik.everlive.sdk.core.handlers.DataHandler;
import com.telerik.everlive.sdk.core.model.base.DataItem;
import com.telerik.everlive.sdk.core.result.RequestResult;

public class Read0rDistantData implements IDistantDataHandler {

	private EverliveApp mApp;
	private ArrayList<DownloadableBook> mLastKnownData;

	public Read0rDistantData() {
		this.mApp = new EverliveApp("GSHZpwj9o3uHSR2d");
		this.mApp.workWith().authentication().login("Siko", "123456")
				.executeSync();
	}

	public ArrayList<String> getCategories() {
		if (this.mLastKnownData == null) {
			this.getBooks(); // refreshes the lastKnownData field
		}

		ArrayList<String> cats = new ArrayList<String>();
		for (DownloadableBook book : this.mLastKnownData) {
			if (!cats.contains(book.category)) {
				cats.add(book.category);
			}
		}
		return cats;
	}

	public ArrayList<DownloadableBook> getBooks() {

		RequestResult<ArrayList<DownloadableBook>> resultList;
		DataHandler data = mApp.workWith().data(DownloadableBook.class);
		resultList = data.getAll().executeSync();

		if (resultList.getSuccess()) {
			Object obj = resultList.getValue();
			this.mLastKnownData = resultList.getValue();
			Log.e("Everlive Success", this.mLastKnownData.size()
					+ " items were loaded.");
		} else {
			Log.e("Everlive Error", resultList.getError().toString());
			this.mLastKnownData = new ArrayList<DownloadableBook>();
		}
		return this.mLastKnownData;
	}

	public ArrayList<DownloadableBook> getFilteredBooks(
			ArrayList<String> categories) {
		ArrayList<DownloadableBook> filtered = new ArrayList<DownloadableBook>();
		this.getBooks(); // refreshes the lastKnownData field

		for (DownloadableBook downloadableBook : this.mLastKnownData) {
			if (categories.contains(downloadableBook.category)) {
				filtered.add(downloadableBook);
			}
		}

		return filtered;
	}

	public EverliveApp getEverlive() {
		return this.mApp;
	}
}
