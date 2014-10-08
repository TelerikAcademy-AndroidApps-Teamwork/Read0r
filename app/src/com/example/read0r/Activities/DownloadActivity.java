package com.example.read0r.Activities;

import java.util.ArrayList;
import java.util.Iterator;

import com.example.read0r.DownloadHandler;
import com.example.read0r.R;
import com.example.read0r.R.id;
import com.example.read0r.R.layout;
import com.example.read0r.R.menu;
import com.example.read0r.EverliveModels.DownloadableBook;
import com.example.read0r.Fakes.FakeDistantDataHandler;
import com.example.read0r.Fakes.FakeLocalDataHandler;
import com.example.read0r.Interfaces.IDistantDataHandler;
import com.example.read0r.SQLiteModels.ReadableBook;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class DownloadActivity extends ActionBarActivity implements OnClickListener {

	private Intent downloadFilterIntent;
	private int theme;
	private IDistantDataHandler distantDataHandler;
	private ArrayList<String> filters;
	private ArrayList<DownloadableBook> content;
	private FakeLocalDataHandler localDataHandler;
	private DownloadHandler downloadHandler;
	private Button backBtn;
	private Button filterBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download);

		this.downloadFilterIntent = new Intent(DownloadActivity.this,
				DownloadFilterActivity.class);

		this.theme = this.getResources().getInteger(R.integer.theme);

		this.distantDataHandler = new FakeDistantDataHandler();
		this.localDataHandler = new FakeLocalDataHandler();
		this.downloadHandler = new DownloadHandler();

		this.backBtn = (Button) this.findViewById(R.id.download_backButton);
		this.filterBtn = (Button) this.findViewById(R.id.download_filterButton);

		this.backBtn.setOnClickListener(this);
		this.filterBtn.setOnClickListener(this);
		
		this.applyTheme();
		this.updateFilters(this.getIntent());
		this.updateContent();
	}

	private void applyTheme() {
		// TODO : Apply the theme
	}

	private void updateFilters(Intent data) {
		if (data.hasExtra("filters")) {
			String[] filts = data.getStringArrayExtra("filters");
			this.filters = new ArrayList<String>();
			for (String string : filts) {
				this.filters.add(string);
			}
		} else {
			this.filters = new ArrayList<String>();
		}
	}

	private void updateContent() {
		this.content = this.distantDataHandler.getFilteredBooks(this.filters);
		ArrayList<ReadableBook> ownedBooks = this.localDataHandler.getBooks();

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

	public void onClick(View v) {
		if (v.getId() == R.id.download_backButton) {
			goBack();
		} else if (v.getId() == R.id.download_filterButton) {
			goToDownloadFilter();
		}
	}
	
	public void goBack() {
		this.finish();
	}

	public void goToDownloadFilter() {
		this.downloadFilterIntent.putExtra("filters", this.filters);
		this.startActivityForResult(this.downloadFilterIntent, 1);
	}

	void downloadBook() {
		DownloadableBook dummyBook_PlsReplaceMe = new DownloadableBook();
		// TODO : Get the selected book, and replace the dummy one

		ReadableBook downloadedBook = this.downloadHandler
				.downloadBook(this, dummyBook_PlsReplaceMe);
		this.localDataHandler.addBook(downloadedBook);
		this.updateContent();
	}
	
	 public void onBookDownloaded() {
		Toast toast = new Toast(this);
		toast.setText("Book Downloaded");
		toast.show();
	}

}
