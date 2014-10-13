package com.example.read0r.Fakes;

import android.os.Environment;

import com.example.read0r.Activities.DownloadActivity;
import com.example.read0r.Interfaces.IDownloadHandler;
import com.example.read0r.Models.DownloadableBook;
import com.example.read0r.Models.ReadableBook;
import com.telerik.everlive.sdk.core.EverliveApp;

public class FakeDownloadHandler implements IDownloadHandler {

	@Override
	public ReadableBook downloadBook(DownloadActivity context,
			String url, DownloadableBook bookToDownload) {
		
		return new ReadableBook(Environment.getExternalStorageDirectory()
				.getPath() + bookToDownload.fileName, bookToDownload.title,
				bookToDownload.author, (int) bookToDownload.pages.intValue(),
				bookToDownload.category, 0);
	}

}
