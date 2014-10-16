package com.example.read0r;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.util.Log;

import com.example.read0r.Activities.DownloadActivity;
import com.example.read0r.Interfaces.IDistantDataHandler;
import com.example.read0r.Models.DownloadableBook;
import com.telerik.everlive.sdk.core.EverliveApp;
import com.telerik.everlive.sdk.core.handlers.DataHandler;
import com.telerik.everlive.sdk.core.model.base.DataItem;
import com.telerik.everlive.sdk.core.result.RequestResult;

public class Read0rDistantData implements IDistantDataHandler {

	private EverliveApp mApp;
	private ArrayList<DownloadableBook> mLastKnownData;
	private DownloadActivity context;

	public Read0rDistantData(DownloadActivity context) {
		this.context = context;
		
		this.mApp = new EverliveApp("GSHZpwj9o3uHSR2d");
		this.mApp.workWith().authentication().login("Siko", "123456")
				.executeSync();
	}

	public ArrayList<String> getCategories() {
		ArrayList<String> cats = new ArrayList<String>();
		for (DownloadableBook book : this.mLastKnownData) {
			if (!cats.contains(book.category)) {
				cats.add(book.category);
			}
		}
		return cats;
	}

	public void getBooks() {
		new EverliveTask(this.mApp).execute(new ArrayList<String>());
		
//		RequestResult<ArrayList<DownloadableBook>> resultList;
//		DataHandler data = mApp.workWith().data(DownloadableBook.class);
//		resultList = data.getAll().executeSync();
//
//		if (resultList.getSuccess()) {
//			Object obj = resultList.getValue();
//			this.mLastKnownData = resultList.getValue();
//			Log.e("Everlive Success", this.mLastKnownData.size()
//					+ " items were loaded.");
//		} else {
//			Log.e("Everlive Error", resultList.getError().toString());
//			this.mLastKnownData = new ArrayList<DownloadableBook>();
//		}
//		return this.mLastKnownData;
	}

	public void getFilteredBooks(
			ArrayList<String> categoriesToExclude) {

		new EverliveTask(this.mApp).execute(categoriesToExclude);
//		ArrayList<DownloadableBook> filtered = new ArrayList<DownloadableBook>();
//		this.getBooks(); // refreshes the lastKnownData field
//
//		for (DownloadableBook downloadableBook : this.mLastKnownData) {
//			if (categories.contains(downloadableBook.category)) {
//				filtered.add(downloadableBook);
//			}
//		}
//
//		return filtered;
	}

	public EverliveApp getEverlive() {
		return this.mApp;
	}

	private void onResultReady() {
		this.context.updateContent(this.mLastKnownData);
	}
	
	private class EverliveTask extends AsyncTask<List<String>, Integer, List<DownloadableBook>> {

		private EverliveApp ev;

		public EverliveTask(EverliveApp app) {
			this.ev = app;
		}
		
		@Override
		protected List<DownloadableBook> doInBackground(List<String>... filters) {
			RequestResult<ArrayList<DownloadableBook>> resultList;
			DataHandler data = mApp.workWith().data(DownloadableBook.class);
			resultList = data.getAll().executeSync();

			if (resultList.getSuccess()) {
				mLastKnownData = resultList.getValue();
				Log.e("Everlive Success", mLastKnownData.size()
						+ " items were loaded.");
			} else {
				Log.e("Everlive Error", resultList.getError().toString());
				mLastKnownData = new ArrayList<DownloadableBook>();
			}

			List<String> categories = filters[0];
			ArrayList<DownloadableBook> filtered = new ArrayList<DownloadableBook>();
			for (DownloadableBook downloadableBook : mLastKnownData) {
				if (!categories.contains(downloadableBook.category)) {
					filtered.add(downloadableBook);
				}
			}
			return filtered;
		}
		
		@Override
		protected void onPostExecute(List<DownloadableBook> result) {
			onResultReady();
			super.onPostExecute(result);
		}
		
	}

}
