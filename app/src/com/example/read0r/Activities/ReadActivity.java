package com.example.read0r.Activities;

import com.example.read0r.DocumentReader;
import com.example.read0r.R;
import com.example.read0r.Fakes.FakeLocalDataHandler;
import com.example.read0r.R.id;
import com.example.read0r.R.integer;
import com.example.read0r.R.layout;
import com.example.read0r.R.menu;
import com.example.read0r.Read0rWord;
import com.example.read0r.SQLiteModels.ReadableBook;
import com.example.read0r.Read0rQueue;
import com.example.read0r.Read0rQueueHandler;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class ReadActivity extends ActionBarActivity {

	private int theme;
	private int fontSize;
	private int speedPercent;
	private Read0rWord currentWord;
	private Read0rQueueHandler queueHandler;
	private boolean paused;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download);

		int bookId = this.getIntent().getExtras().getInt("bookId");
		ReadableBook document = new FakeLocalDataHandler().getBookById(bookId);
		this.queueHandler = new Read0rQueueHandler(new Read0rQueue(),
				new DocumentReader(document));

		this.theme = this.getResources().getInteger(R.integer.theme);
		this.fontSize = this.getResources().getInteger(R.integer.fontSize);
		this.speedPercent = this.getResources().getInteger(
				R.integer.speedPercent);

		this.applyTheme();
	}

	private void applyTheme() {
		// TODO : Apply the theme
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

	void updateWord() {
		if (!this.paused) {
			if (this.queueHandler.isDocumentOver()) {
				this.onDocumentOver();
				return;
			} else {
				this.currentWord = this.queueHandler.getNextWord();
			}

			// set timeout
			new android.os.Handler().postDelayed(new Runnable() {
				public void run() {
					updateWord();
				}
			}, this.currentWord.getMilliSeconds());
		}
	}

	private void onDocumentOver() {
		Toast.makeText(this, "The document has reached it's end", Toast.LENGTH_LONG).show();
		pauseReading();
	}

	public void pauseReading() {
		if (this.paused) {
			this.paused = false;
			updateWord();
		} else {
			this.paused = true;
		}
	}

	public void stopReading(View v) {
		this.paused = true;
		
		this.goBack(v);
	}

	public void goBack(View v) {
		// not sure about which of those is right...
		this.finish();
	}
}
