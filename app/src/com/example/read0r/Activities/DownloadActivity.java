package com.example.read0r.Activities;

import java.util.ArrayList;
import java.util.List;

import com.example.read0r.DownloadHandler;
import com.example.read0r.R;
import com.example.read0r.Read0rDistantData;
import com.example.read0r.Read0rLocalData;
import com.example.read0r.Fakes.FakeDistantDataHandler;
import com.example.read0r.Fakes.FakeLocalDataHandler;
import com.example.read0r.Interfaces.IDistantDataHandler;
import com.example.read0r.Interfaces.ILocalDataHandler;
import com.example.read0r.Models.DownloadableBook;
import com.example.read0r.Models.ReadableBook;
import com.example.read0r.Views.DownloadableBooksWidget;

import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.ActionBarActivity;
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

	private Intent downloadFilterIntent;
	private int theme;
	private IDistantDataHandler distantDataHandler;
	private ArrayList<String> filters;
	private ArrayList<DownloadableBook> content;
	private ILocalDataHandler localDataHandler;
	private DownloadHandler downloadHandler;
	private Button backBtn;
	private Button filterBtn;
	private DownloadableBooksWidget booksWidget;
	private TextView pageCounter;
	private DownloadableBook bookToDownload;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download);

		this.downloadFilterIntent = new Intent(DownloadActivity.this,
				DownloadFilterActivity.class);

		this.theme = this.getResources().getInteger(R.integer.theme);
		boolean distantDataIsFake = this.getResources().getBoolean(R.bool.useFakeDistantData);
		boolean localDataIsFake = this.getResources().getBoolean(R.bool.useFakeLocalData);

		if (distantDataIsFake) {
			this.distantDataHandler = new FakeDistantDataHandler();
		} else {
			this.distantDataHandler = new Read0rDistantData();
		}
		
		if (localDataIsFake) {
			this.localDataHandler = new FakeLocalDataHandler();
		} else {
			this.localDataHandler = new Read0rLocalData();
		}
		
		
		this.downloadHandler = new DownloadHandler();

		this.backBtn = (Button) this.findViewById(R.id.download_backButton);
		this.filterBtn = (Button) this.findViewById(R.id.download_filterButton);
		this.pageCounter = (TextView) this
				.findViewById(R.id.download_pageTrackerTextView);
		this.booksWidget = (DownloadableBooksWidget) this
				.findViewById(R.id.download_booksWidget);

		this.backBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				goBack();
			}
		});
		this.filterBtn.setOnClickListener(new OnClickListener() {
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
			this.filters = (ArrayList<String>) data.getExtras().get("filters");
			if (this.filters != null) {
				return;
			}
		}
		this.filters = this.distantDataHandler.getCategories();
	}

	private void updateContent() {
		this.content = this.distantDataHandler.getFilteredBooks(this.filters);
		List<ReadableBook> ownedBooks = this.localDataHandler.getBooks();

		for (DownloadableBook book : this.content) {
			for (ReadableBook ownedBook : ownedBooks) {
				if (book.title == ownedBook.title) {
					book.isOwned = true;
				}
			}
		}

		this.booksWidget.setBooks(this.content);
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
		this.pageCounter.setText(text);
	}

	public void onBookSelection(DownloadableBook book) {
		this.bookToDownload = book;
		showDownloadPrompt();
	}

	public void onPromptResponseSelected(boolean downloadAccepted) {
		if (downloadAccepted) {
			ReadableBook downloadedBook = this.downloadHandler
					.downloadBook(this,
							((Read0rDistantData) this.distantDataHandler)
									.getEverlive(), this.bookToDownload);
			this.localDataHandler.addBook(downloadedBook);
			this.updateContent();
			onBookDownloaded(this.bookToDownload);
		}
	}

	private void showDownloadPrompt() {
		try {
			LayoutInflater inflater = (LayoutInflater) this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			ViewGroup layout = (ViewGroup) inflater.inflate(
					R.layout.fragment_download_prompt,
					(ViewGroup) findViewById(R.id.download_textView1));

			((TextView) layout.getChildAt(1))
					.setText("Do you want to download the book '" + this.bookToDownload.title
							+ "'");

			PopupWindow downloadPrompt = new PopupWindow(layout, 200, 200,
					false);
			downloadPrompt.showAtLocation(layout, Gravity.CENTER, 0, 0);

			Button Ok = (Button) ((ViewGroup) layout.getChildAt(2))
					.getChildAt(0);
			Button Cancel = (Button) ((ViewGroup) layout.getChildAt(2))
					.getChildAt(1);

			Cancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onPromptResponseSelected(false);
				}
			});
			Cancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onPromptResponseSelected(true);
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void goBack() {
		this.finish();
	}

	public void goToDownloadFilter() {
		this.downloadFilterIntent.putExtra("filters", this.filters);
		this.downloadFilterIntent.putExtra("categories",
				this.distantDataHandler.getCategories());
		this.startActivityForResult(this.downloadFilterIntent, 1);
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
