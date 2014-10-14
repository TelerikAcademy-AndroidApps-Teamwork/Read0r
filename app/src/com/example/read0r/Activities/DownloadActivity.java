package com.example.read0r.Activities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.read0r.DownloadHandler;
import com.example.read0r.R;
import com.example.read0r.Read0rDistantData;
import com.example.read0r.Read0rLocalData;
import com.example.read0r.Fakes.FakeDistantDataHandler;
import com.example.read0r.Fakes.FakeDownloadHandler;
import com.example.read0r.Fakes.FakeLocalDataHandler;
import com.example.read0r.Interfaces.IDistantDataHandler;
import com.example.read0r.Interfaces.IDownloadHandler;
import com.example.read0r.Interfaces.ILocalDataHandler;
import com.example.read0r.Models.DownloadableBook;
import com.example.read0r.Models.ReadableBook;
import com.example.read0r.Views.DownloadableBooksWidget;
import com.telerik.everlive.sdk.core.EverliveApp;

import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.ActionBarActivity;
import android.R.integer;
import android.R.string;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.GestureDetector.OnGestureListener;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DownloadActivity extends ActionBarActivity {

	private Intent mDownloadFilterIntent;
	private int mTheme;
	private IDistantDataHandler mDistantDataHandler;
	private ArrayList<String> mFilters;
	private ArrayList<DownloadableBook> mContent;
	private ILocalDataHandler mLocalDataHandler;
	private IDownloadHandler mDownloadHandler;
	private Button mBackBtn;
	private Button mFilterBtn;
	private DownloadableBooksWidget mBooksWidget;
	private TextView mPageCounter;
	private DownloadableBook mBookToDownload;
	private PopupWindow mPopup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download);

		this.mDownloadFilterIntent = new Intent(DownloadActivity.this,
				DownloadFilterActivity.class);

		this.mTheme = com.example.read0r.Settings.getTheme(this);
		
		boolean distantDataIsFake = this.getResources().getBoolean(
				R.bool.useFakeDistantData);
		boolean localDataIsFake = this.getResources().getBoolean(
				R.bool.useFakeLocalData);
		boolean downloaderIsFake = this.getResources().getBoolean(
				R.bool.useFakeDocDownloader);

		if (distantDataIsFake) {
			this.mDistantDataHandler = new FakeDistantDataHandler();
		} else {
			this.mDistantDataHandler = new Read0rDistantData();
		}

		if (localDataIsFake) {
			this.mLocalDataHandler = new FakeLocalDataHandler();
		} else {
			this.mLocalDataHandler = new Read0rLocalData();
		}

		if (downloaderIsFake) {
			this.mDownloadHandler = new FakeDownloadHandler();
		} else {
			this.mDownloadHandler = new DownloadHandler();
		}

		this.mBackBtn = (Button) this.findViewById(R.id.download_backButton);
		this.mFilterBtn = (Button) this.findViewById(R.id.download_filterButton);
		this.mPageCounter = (TextView) this
				.findViewById(R.id.download_pageTrackerTextView);
		this.mBooksWidget = (DownloadableBooksWidget) this
				.findViewById(R.id.download_booksWidget);

		this.mBackBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				goBack();
			}
		});
		this.mFilterBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				goToDownloadFilter();
			}
		});

		this.applyTheme();
		this.updateFilters(this.getIntent());
		this.updateContent();
	}

	private void applyTheme() {
		// TODO : Apply the theme
	}

	private void updateFilters(Intent data) {
		if (data.hasExtra("filters")) {
			this.mFilters = (ArrayList<String>) data.getExtras().get("filters");
			if (this.mFilters != null) {
				return;
			}
		}
		this.mFilters = this.mDistantDataHandler.getCategories();
	}

	private void updateContent() {
		this.mContent = this.mDistantDataHandler.getFilteredBooks(this.mFilters);
		List<ReadableBook> ownedBooks = this.mLocalDataHandler.getBooks();

		for (DownloadableBook book : this.mContent) {
			for (ReadableBook ownedBook : ownedBooks) {
				if (book.title == ownedBook.title) {
					book.isOwned = true;
					break;
				}
			}
		}

		this.mBooksWidget.setBooks(this.mContent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		updateFilters(data);
	}

	public void updatePageCounter(String text) {
		this.mPageCounter.setText(text);
	}

	public void onBookSelection(DownloadableBook book) {
		this.mBookToDownload = book;
		if (!book.isOwned) {
			showDownloadPrompt();
		} else {
			LayoutInflater inflater = (LayoutInflater) this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View layout = (View) inflater.inflate(R.layout.popup_message,
					(ViewGroup) findViewById(R.id.popup_container));

			this.mPopup = new PopupWindow(layout, 200, 150, false);
			mPopup.showAtLocation(layout, Gravity.CENTER, 0, 0);

			ViewGroup popupContainer = (ViewGroup) layout;
			((TextView) popupContainer.getChildAt(0))
					.setText("You already own this book!");
			Button Ok = (Button) popupContainer.getChildAt(1);
			Ok.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mPopup.dismiss();
				}
			});
		}
	}

	public void onPromptResponseSelected(boolean downloadAccepted) {
		if (downloadAccepted) {
			boolean distantDataIsFake = this.getResources().getBoolean(
					R.bool.useFakeDistantData);
			String url;
			if (distantDataIsFake) {
				url = "";
			} else {
				EverliveApp everliveApp = ((Read0rDistantData) this.mDistantDataHandler)
						.getEverlive();
				UUID id = this.mBookToDownload.Book;
				// TODO: Fix this somehow
				// url = everliveApp.workWith().files().getFileDownloadUrl(id);
				url = "http://www.fake.com";
			}
			ReadableBook downloadedBook = this.mDownloadHandler.downloadBook(
					this, url, this.mBookToDownload);
			this.mLocalDataHandler.addBook(downloadedBook);
			this.updateContent();
			onBookDownloaded(this.mBookToDownload);
		}
	}

	private void showDownloadPrompt() {

		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View layout = (View) inflater.inflate(R.layout.prompt_download,
				(ViewGroup) findViewById(R.id.popup_element));

		this.mPopup = new PopupWindow(layout, 200, 200, false);
		mPopup.showAtLocation(layout, Gravity.CENTER, 0, 0);

		ViewGroup popupContainer = (ViewGroup) layout;

		((TextView) popupContainer.getChildAt(1))
				.setText("Do you want to download the book '"
						+ this.mBookToDownload.title + "'");

		Button Ok = (Button) ((ViewGroup) popupContainer.getChildAt(2))
				.getChildAt(0);
		Button Cancel = (Button) ((ViewGroup) popupContainer.getChildAt(2))
				.getChildAt(1);

		Cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPopup.dismiss();
				onPromptResponseSelected(false);
			}
		});
		Ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPopup.dismiss();
				onPromptResponseSelected(true);
			}
		});
	}

	public void goBack() {
		this.finish();
	}

	public void goToDownloadFilter() {
		this.mDownloadFilterIntent.putExtra("filters", this.mFilters);
		this.mDownloadFilterIntent.putExtra("categories",
				this.mDistantDataHandler.getCategories());
		this.startActivityForResult(this.mDownloadFilterIntent, 1);
	}

	@SuppressLint("NewApi")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void onBookDownloaded(DownloadableBook book) {

		Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
				R.drawable.attention);

		Notification.Builder builder = new Notification.Builder(this)
				.setSmallIcon(R.drawable.attention)
				.setLargeIcon(
						Bitmap.createScaledBitmap(largeIcon, 128, 128, false))
				.setContentTitle("Read0r book downloaded")
				.setContentText("'" + book.title + "' by " + book.author);

		Notification notification = builder.build();

		((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
				.notify(R.id.finishedDownloadNotifivation_id, notification);
	}

}
