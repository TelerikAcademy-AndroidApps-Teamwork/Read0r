package com.example.read0r.Activities;

import com.example.read0r.DistantDataHandler;
import com.example.read0r.DownloadHandler;
import com.example.read0r.LocalDataHandler;
import com.example.read0r.R;
import com.example.read0r.R.id;
import com.example.read0r.R.layout;
import com.example.read0r.R.menu;
import com.example.read0r.EverliveModels.DownloadableBook;
import com.example.read0r.Interfaces.IDistantDataHandler;
import com.example.read0r.SQLiteModels.ReadableBook;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class DownloadActivity extends ActionBarActivity {

	private Intent downloadFilterIntent;
	private int theme;
	private IDistantDataHandler distantDataHandler;
	private String[] filters;
	private DownloadableBook[] content;
	private LocalDataHandler localDataHandler;
	private DownloadHandler downloadHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download);

		this.downloadFilterIntent = new Intent(DownloadActivity.this,
				DownloadFilterActivity.class);

		this.theme = this.getResources().getInteger(R.integer.theme);

		this.distantDataHandler = new DistantDataHandler();
		this.localDataHandler = new LocalDataHandler();
		this.downloadHandler = new DownloadHandler();

		this.applyTheme();
		this.updateFilters(this.getIntent());
		this.updateContent();
	}

	private void applyTheme() {
		// TODO : Apply the theme
	}

	private void updateFilters(Intent data) {
		if (data.hasExtra("filters")) {
			this.filters = data.getStringArrayExtra("filters");
		} else {
			this.filters = new String[0];
		}
	}

	private void updateContent() {
		this.content = this.distantDataHandler.getFilteredBooks(this.filters);
		ReadableBook[] ownedBooks = this.localDataHandler.getBooks();

		for (DownloadableBook book : this.content) {
			for (ReadableBook ownedBook : ownedBooks) {
				if (book.title == ownedBook.title) {
					book.isOwned = true;
				}
			}
		}

		// TODO : Display books
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

	void goBack() {
		this.finish();
	}

	void goToDownloadFilter() {
		this.downloadFilterIntent.putExtra("filters", this.filters);
		this.startActivityForResult(this.downloadFilterIntent, 1);
	}

	void downloadBook() {
		DownloadableBook dummyBook_PlsReplaceMe = new DownloadableBook();
		// TODO : Get the selected book, and replace the dummy one

		ReadableBook downloadedBook = this.downloadHandler
				.downloadBook(dummyBook_PlsReplaceMe);
		this.localDataHandler.addBook(downloadedBook);
		this.updateContent();
	}
}
