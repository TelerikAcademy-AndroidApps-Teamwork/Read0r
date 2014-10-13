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

	private int mTheme;
	private int mFontSize;
	private int mSpeedPercent;
	private Read0rWord mCurrentWord;
	private Read0rQueueHandler mQueueHandler;
	private boolean mPaused;
	private Button mStopBtn;
	private Button mPauseBtn;
	private ILocalDataHandler mLocalDataHandler;
	private TextView mWordView;
	private TextView mProgressView;
	private View mBackground;

	private double mDelayMultiplicator = 1;
	private double mMinFontSize = 12;
	private int mMaxFontSize = 50;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_read);

		int bookId = this.getIntent().getExtras().getInt("book_id");

		boolean localDataIsFake = this.getResources().getBoolean(
				R.bool.useFakeLocalData);

		if (localDataIsFake) {
			this.mLocalDataHandler = new FakeLocalDataHandler();
		} else {
			this.mLocalDataHandler = new Read0rLocalData();
		}

		ReadableBook document = this.mLocalDataHandler.getBookById(bookId);

		this.guestureDetector = new GestureDetector(this, this);
		this.scaleDetector = new ScaleGestureDetector(this, this);

		this.mStopBtn = (Button) this.findViewById(R.id.read_stopButton);
		this.mPauseBtn = (Button) this.findViewById(R.id.read_pauseButton);
		this.mWordView = (TextView) this.findViewById(R.id.read_word);
		this.mProgressView = (TextView) this
				.findViewById(R.id.read_progressView);
		this.mBackground = this.findViewById(R.id.read_background);

		this.updateFontSize();

		this.mStopBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				stopReading();
			}
		});
		this.mPauseBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pauseReading();
			}
		});

		this.mBackground.setOnTouchListener(new OnTouchListener() {
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

		this.mQueueHandler = new Read0rQueueHandler(queue, reader);
		this.mQueueHandler.onCreate();
		this.mQueueHandler
				.onStart(new Intent(this, Read0rQueueHandler.class), 1);

		this.mTheme = this.getResources().getInteger(R.integer.theme);
		this.mFontSize = this.getResources().getInteger(R.integer.fontSize);
		this.mSpeedPercent = this.getResources().getInteger(
				R.integer.speedPercent);

		this.applyTheme();

		this.mPaused = true;
		mPauseBtn.setText("Start");
		// setUpdateTimeout(2000);
	}

	private void updateFontSize() {
		this.mWordView.setTextSize(this.mFontSize);
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
		if (!this.mPaused) {
			if (this.mQueueHandler.isDocumentOver()) {
				this.onDocumentOver();
				return;
			} else {
				this.mCurrentWord = this.mQueueHandler.getNextWord();
				this.mWordView.setText(this.mCurrentWord.getWord());
				this.mProgressView.setText(this.mQueueHandler.getProgress());
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
		long delay = this.mCurrentWord.getMilliSeconds();
		delay *= this.mDelayMultiplicator;
		delay = delay * this.mSpeedPercent / 100;
		return delay;
	}

	private void onDocumentOver() {
		Toast.makeText(this, "The document has reached it's end",
				Toast.LENGTH_LONG).show();
		pauseReading();
	}

	public void pauseReading() {
		if (this.mPaused) {
			mPauseBtn.setText(this.getResources().getInteger(R.string.PauseBtn));
			this.mPaused = false;
			updateWord();
		} else {
			mPauseBtn.setText(this.getResources().getInteger(R.string.ResumeBtn));
			this.mPaused = true;
		}
	}

	public void stopReading() {
		this.mPaused = true;
		this.goBack();
	}

	public void goBack() {
		this.finish();
	}

	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		this.mFontSize *= (detector.getScaleFactor()/2);

        // Don't let the object get too small or too large.
		this.mFontSize = (int) Math.max(this.mMinFontSize, Math.min(this.mFontSize, this.mMaxFontSize));

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
		this.mDelayMultiplicator = 1;
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		this.mDelayMultiplicator = 0.5;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (velocityX < 0) {
			this.mQueueHandler.goBack();
		}
		return true;
	}
}
