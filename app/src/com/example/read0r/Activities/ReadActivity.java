package com.example.read0r.Activities;

import com.example.read0r.DocumentReader;
import com.example.read0r.R;
import com.example.read0r.Fakes.FakeDocumentReader;
import com.example.read0r.Fakes.FakeLocalDataHandler;
import com.example.read0r.Interfaces.IDocumentReader;
import com.example.read0r.Interfaces.ILocalDataHandler;
import com.example.read0r.Models.ReadableBook;
import com.example.read0r.Services.Read0rQueueHandler;
import com.example.read0r.Read0rLocalData;
import com.example.read0r.Read0rWord;
import com.example.read0r.Read0rQueue;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ReadActivity extends ActionBarActivity implements
		OnGestureListener, OnScaleGestureListener {

	private GestureDetector guestureDetector;
	private ScaleGestureDetector scaleDetector;

	private int theme;
	private int fontSize;
	private int speedPercent;
	private Read0rWord currentWord;
	private Read0rQueueHandler queueHandler;
	private boolean paused;
	private Button stopBtn;
	private Button pauseBtn;
	private ILocalDataHandler localDataHandler;
	private TextView wordView;
	private TextView progressView;
	private View background;

	private double delayMultiplicator = 1;
	private double minFontSize = 12;
	private int maxFontSize = 50;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_read);

		int bookId = this.getIntent().getExtras().getInt("book_id");

		boolean localDataIsFake = this.getResources().getBoolean(
				R.bool.useFakeLocalData);

		if (localDataIsFake) {
			this.localDataHandler = new FakeLocalDataHandler();
		} else {
			this.localDataHandler = new Read0rLocalData();
		}

		ReadableBook document = this.localDataHandler.getBookById(bookId);

		this.guestureDetector = new GestureDetector(this, this);
		this.scaleDetector = new ScaleGestureDetector(this, this);

		this.stopBtn = (Button) this.findViewById(R.id.read_stopButton);
		this.pauseBtn = (Button) this.findViewById(R.id.read_pauseButton);
		this.wordView = (TextView) this.findViewById(R.id.read_word);
		this.progressView = (TextView) this
				.findViewById(R.id.read_progressView);
		this.background = this.findViewById(R.id.read_background);

		this.updateFontSize();

		this.stopBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				stopReading();
			}
		});
		this.pauseBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pauseReading();
			}
		});

		this.background.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				boolean simpleGuestureHandled = guestureDetector
						.onTouchEvent(event);
				boolean scaleGuestureHandled = scaleDetector
						.onTouchEvent(event);
				return simpleGuestureHandled || scaleGuestureHandled;
			}
		});

		Read0rQueue queue = new Read0rQueue();
		IDocumentReader reader;
		boolean docReaderIsFake = this.getResources().getBoolean(
				R.bool.useFakeDocReader);

		if (docReaderIsFake) {
			reader = new FakeDocumentReader();
		} else {
			reader = new DocumentReader(document, this);
		}

		this.queueHandler = new Read0rQueueHandler(queue, reader);
		this.queueHandler.onCreate();
		this.queueHandler
				.onStart(new Intent(this, Read0rQueueHandler.class), 1);

		this.theme = this.getResources().getInteger(R.integer.theme);
		this.fontSize = this.getResources().getInteger(R.integer.fontSize);
		this.speedPercent = this.getResources().getInteger(
				R.integer.speedPercent);

		this.applyTheme();

		this.paused = true;
		pauseBtn.setText("Start");
		// setUpdateTimeout(2000);
	}

	private void updateFontSize() {
		this.wordView.setTextSize(this.fontSize);
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
				this.wordView.setText(this.currentWord.getWord());
				this.progressView.setText(this.queueHandler.getProgress());
			}

			// set timeout
			setUpdateTimeout(this.calculateDelay());
		}
	}

	private void setUpdateTimeout(long delay) {
		new android.os.Handler().postDelayed(new Runnable() {
			public void run() {
				updateWord();
			}
		}, delay);
	}

	private long calculateDelay() {
		long delay = this.currentWord.getMilliSeconds();
		delay *= this.delayMultiplicator;
		delay = delay * this.speedPercent / 100;
		return delay;
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

	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		this.fontSize *= (detector.getScaleFactor()/2);

        // Don't let the object get too small or too large.
		this.fontSize = (int) Math.max(this.minFontSize, Math.min(this.fontSize, this.maxFontSize));

		return true;
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		this.pauseReading();
		this.delayMultiplicator = 1;
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		this.delayMultiplicator = 0.5;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (velocityX < 0) {
			this.queueHandler.goBack();
		}
		return true;
	}
}
