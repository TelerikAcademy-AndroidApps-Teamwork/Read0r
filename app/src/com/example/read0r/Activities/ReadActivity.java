package com.example.read0r.Activities;

import com.example.read0r.DocumentReader;
import com.example.read0r.R;
import com.example.read0r.Fakes.FakeLocalDataHandler;
import com.example.read0r.Models.ReadableBook;
import com.example.read0r.R.id;
import com.example.read0r.R.integer;
import com.example.read0r.R.layout;
import com.example.read0r.R.menu;
import com.example.read0r.Read0rWord;
import com.example.read0r.Read0rQueue;
import com.example.read0r.Read0rQueueHandler;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ReadActivity extends ActionBarActivity implements OnClickListener {

	private int theme;
	private int fontSize;
	private int speedPercent;
	private Read0rWord currentWord;
	private Read0rQueueHandler queueHandler;
	private boolean paused;
	private Button stopBtn;
	private Button pauseBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_read);

		
		int bookId = this.getIntent().getExtras().getInt("book_id");
		ReadableBook document = new FakeLocalDataHandler().getBookById(bookId);

		this.stopBtn = (Button) this.findViewById(R.id.read_stopButton);
		this.pauseBtn = (Button) this.findViewById(R.id.read_pauseButton);

		this.stopBtn.setOnClickListener(this);
		this.pauseBtn.setOnClickListener(this);
		
		Read0rQueue queue = new Read0rQueue();
		DocumentReader reader = new DocumentReader(document);
		
		this.queueHandler = new Read0rQueueHandler(queue, reader);

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
		Toast.makeText(this, "The document has reached it's end",
				Toast.LENGTH_LONG).show();
		pauseReading();
	}

	public void pauseReading() {
		if (this.paused) {
			pauseBtn.setText(this.getResources().getInteger(R.string.PauseBtn));
			this.paused = false;
			updateWord();
		} else {
			pauseBtn.setText(this.getResources().getInteger(R.string.ResumeBtn));
			this.paused = true;
		}
	}

	public void stopReading() {
		this.paused = true;
		this.goBack();
	}

	public void goBack() {
		this.finish();
	}

	public void onClick(View v) {
		if (v.getId() == R.id.read_stopButton) {
			stopReading();
		} else if (v.getId() == R.id.read_pauseButton) {
			pauseReading();
		}
	}
}
