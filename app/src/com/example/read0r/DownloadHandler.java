package com.example.read0r;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;

import com.example.read0r.Activities.DownloadActivity;
import com.example.read0r.Interfaces.IDownloadHandler;
import com.example.read0r.Models.DownloadableBook;
import com.example.read0r.Models.ReadableBook;
import com.telerik.everlive.sdk.core.EverliveApp;

public class DownloadHandler implements IDownloadHandler {

	public ReadableBook downloadBook(DownloadActivity context,
			DownloadableBook bookToDownload) {

		DownloadTask task = new DownloadTask(context, bookToDownload);
		task.downloadTheBook();

		return new ReadableBook(Environment.getExternalStorageDirectory()
				+ "/read0r/" + bookToDownload.fileName,
				bookToDownload.title, bookToDownload.author,
				(int) bookToDownload.pages.intValue(), bookToDownload.category,
				0);
	}

	private class DownloadTask extends
			AsyncTask<String, Integer, DownloadableBook> {

		private DownloadActivity mContext;
		private PowerManager.WakeLock mWakeLock;
		private DownloadableBook mBookToDownload;
		private String mUrl;

		public DownloadTask(DownloadActivity context,
				DownloadableBook bookToDownload) {
			this.mContext = context;
			this.mBookToDownload = bookToDownload;
			this.mUrl = bookToDownload.url;
		}

		public void downloadTheBook() {
			AsyncTask<String, Integer, DownloadableBook> result = this
					.execute();
			return;
		}

		@Override
		protected DownloadableBook doInBackground(String... str) {
			String sUrl = this.mUrl;
			InputStream input = null;
			OutputStream output = null;
			HttpURLConnection connection = null;
			try {
				URL url = new URL(sUrl);
				connection = (HttpURLConnection) url.openConnection();
				connection.connect();

				// expect HTTP 200 OK, so we don't mistakenly save error report
				// instead of the file
				if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
					Log.e("Downloaf handler error",
							"Server returned HTTP "
									+ connection.getResponseCode() + " "
									+ connection.getResponseMessage());
					return null;
				}

				// this will be useful to display download percentage
				// might be -1: server did not report the length
				int fileLength = connection.getContentLength();

				// download the file
				input = connection.getInputStream();

				String filePath = Environment.getExternalStorageDirectory()
						+ "/read0r/" + this.mBookToDownload.fileName;

				File f = new File(filePath);
				if (!f.exists()) {
					f.getParentFile().mkdirs();
					f.createNewFile();
				}

				output = new FileOutputStream(filePath);

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
				Log.e("Download handler error", e.toString());
				return null;
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
			return null;
		}

		@Override
		protected void onPostExecute(DownloadableBook mBookToDownload) {
			super.onPostExecute(mBookToDownload);
			mContext.onBookDownloaded(mBookToDownload);
		}
	}
}
