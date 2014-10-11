package com.example.read0r;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;

import com.example.read0r.Activities.DownloadActivity;
import com.example.read0r.Interfaces.IDownloadHandler;
import com.example.read0r.Models.DownloadableBook;
import com.example.read0r.Models.ReadableBook;

public class DownloadHandler implements IDownloadHandler {

	public ReadableBook downloadBook(DownloadActivity context,
			DownloadableBook bookToDownload) {

		DownloadTask task = new DownloadTask(context, bookToDownload);
		task.downloadTheBook();

		return new ReadableBook(Environment.getExternalStorageDirectory().getPath() + bookToDownload.fileName,
				bookToDownload.title, bookToDownload.author,
				bookToDownload.pages, bookToDownload.category, 0);
	}

	private class DownloadTask extends AsyncTask<String, Integer, String> {

		private DownloadActivity context;
		private PowerManager.WakeLock mWakeLock;
		private DownloadableBook bookToDownload;

		public DownloadTask(DownloadActivity context,
				DownloadableBook bookToDownload) {
			this.context = context;
			this.bookToDownload = bookToDownload;
		}

		public String downloadTheBook() {
			return this.doInBackground(this.bookToDownload.downloadAddress);
		}

		@Override
		protected String doInBackground(String... sUrl) {
			InputStream input = null;
			OutputStream output = null;
			HttpURLConnection connection = null;
			try {
				URL url = new URL(sUrl[0]);
				connection = (HttpURLConnection) url.openConnection();
				connection.connect();

				// expect HTTP 200 OK, so we don't mistakenly save error report
				// instead of the file
				if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
					return "Server returned HTTP "
							+ connection.getResponseCode() + " "
							+ connection.getResponseMessage();
				}

				// this will be useful to display download percentage
				// might be -1: server did not report the length
				int fileLength = connection.getContentLength();

				// download the file
				input = connection.getInputStream();
				output = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/read0r/"
						+ this.bookToDownload.fileName);

				byte data[] = new byte[4096];
				long total = 0;
				int count;
				while ((count = input.read(data)) != -1) {
					// allow canceling with back button
					if (isCancelled()) {
						input.close();
						return null;
					}
					total += count;
					// publishing the progress....
					if (fileLength > 0) // only if total length is known
					{
						publishProgress((int) (total * 100 / fileLength));
					}
					output.write(data, 0, count);
				}
			} catch (Exception e) {
				return e.toString();
			} finally {
				try {
					if (output != null)
						output.close();
					if (input != null)
						input.close();
				} catch (IOException ignored) {
				}

				if (connection != null)
					connection.disconnect();
			}
			context.onBookDownloaded(bookToDownload);
			return null;
		}
	}
}
