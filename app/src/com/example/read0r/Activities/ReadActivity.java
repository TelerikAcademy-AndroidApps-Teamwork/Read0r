package com.example.read0r.Activities;

import java.util.Date;

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
import android.R.integer;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
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
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("NewApi")
public class ReadActivity extends ActionBarActivity implements
		OnGestureListener, OnScaleGestureListener, SensorEventListener {

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
	private Date mDateOfLastTap;
	private long mMillisecondsBetweenClicks = 500;
	private TextView mTitleView;
	private ReadableBook mBookToRead;
	private PopupWindow mPopup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_read);

		loadSettings();
		loadBookInformation();

		this.guestureDetector = new GestureDetector(this, this);
		this.scaleDetector = new ScaleGestureDetector(this, this);

		handleUI();
		initQueueHandler();
		this.applyTheme();

		this.mPaused = true;
		mPauseBtn.setText("Start");
	}

	private void loadBookInformation() {
		int bookId = this.getIntent().getExtras().getInt("book_id");
		boolean localDataIsFake = this.getResources().getBoolean(
				R.bool.useFakeLocalData);

		if (localDataIsFake) {
			this.mLocalDataHandler = new FakeLocalDataHandler();
		} else {
			this.mLocalDataHandler = new Read0rLocalData();
		}
		this.mBookToRead = this.mLocalDataHandler.getBookById(bookId);
	}

	private void loadSettings() {
		this.mTheme = com.example.read0r.Settings.getTheme(this);
		this.mFontSize = com.example.read0r.Settings.getFontSize(this);
		this.mSpeedPercent = com.example.read0r.Settings.getReadingSpeed(this);
	}

	private void initQueueHandler() {
		Read0rQueue queue = new Read0rQueue();
		IDocumentReader reader;
		boolean docReaderIsFake = this.getResources().getBoolean(
				R.bool.useFakeDocReader);

		if (docReaderIsFake) {
			reader = new FakeDocumentReader();
		} else {
			reader = new DocumentReader(mBookToRead, this);
		}

		this.mQueueHandler = new Read0rQueueHandler(queue, reader);
		this.mQueueHandler.onCreate();
		this.mQueueHandler.onStart(new Intent(this, Read0rQueueHandler.class),
				1);
	}

	private void handleUI() {
		this.mStopBtn = (Button) this.findViewById(R.id.read_stopButton);
		this.mPauseBtn = (Button) this.findViewById(R.id.read_pauseButton);
		this.mWordView = (TextView) this.findViewById(R.id.read_word);
		this.mTitleView = (TextView) this.findViewById(R.id.read_titleView);
		this.mProgressView = (TextView) this
				.findViewById(R.id.read_progressView);
		this.mBackground = this.findViewById(R.id.read_background);

		this.mTitleView.setText(mBookToRead.title);
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
				if (event.getAction() == MotionEvent.ACTION_UP) {
					mDelayMultiplicator = 1;
				}
				boolean simpleGuestureHandled = guestureDetector
						.onTouchEvent(event);
				boolean scaleGuestureHandled = scaleDetector
						.onTouchEvent(event);
				return simpleGuestureHandled || scaleGuestureHandled;
			}
		});
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
				this.mProgressView.setText(this.mQueueHandler.getProgress()
						+ "% proggress");
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
		pauseReading();

		Toast.makeText(this, "You have reached the end of the book.",
				Toast.LENGTH_LONG).show();

		showNotification("You have finished reading a book",
				"The book's name was '" + this.mBookToRead.title
						+ "' and it was writen by by "
						+ this.mBookToRead.author,
				R.id.notificationId_finishedBook);

		askToBoastOnFaceBook();
	}

	private void showNotification(String title, String content,
			int notificationidId) {
		Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
				R.drawable.attention);
		Notification.Builder builder = new Notification.Builder(this)
				.setSmallIcon(R.drawable.attention)
				.setLargeIcon(
						Bitmap.createScaledBitmap(largeIcon, 128, 128, false))
				.setContentTitle(title).setContentText(content);
		Notification notification = builder.build();
		((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
				.notify(notificationidId, notification);
	}

	private void askToBoastOnFaceBook() {
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View layout = (View) inflater.inflate(R.layout.prompt_download,
				(ViewGroup) findViewById(R.id.popup_element));

		this.mPopup = new PopupWindow(layout, 200, 200, false);
		mPopup.showAtLocation(layout, Gravity.CENTER, 0, 0);

		ViewGroup popupContainer = (ViewGroup) layout;

		((TextView) popupContainer.getChildAt(0)).setText("The book has ended");
		((TextView) popupContainer.getChildAt(1))
				.setText("Do you want to boast on facebook about it");

		Button Ok = (Button) ((ViewGroup) popupContainer.getChildAt(2))
				.getChildAt(0);
		Button Cancel = (Button) ((ViewGroup) popupContainer.getChildAt(2))
				.getChildAt(1);

		Cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPopup.dismiss();
				onBoastOnFaceBookChoice(false);
			}
		});
		Ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPopup.dismiss();
				onBoastOnFaceBookChoice(true);
			}
		});
	}

	private void onBoastOnFaceBookChoice(boolean boastOnFaceBook) {
		if (boastOnFaceBook) {
			// this.mFb = new Facebook();

			showNotification("Facebook boasting canceled",
					"Facebook boasting is not yet implemented.",
					R.id.notificationId_facebook);
		}
		this.finish();
	}

	public void pauseReading() {
		if (this.mPaused) {
			this.mPauseBtn.setText("Pause");
			this.mPaused = false;
			this.updateWord();
		} else {
			this.mPauseBtn.setText("Resume");
			this.mPaused = true;
		}
	}

	public void stopReading() {
		this.mPaused = true;
		this.goBack();
	}

	public void goBack() {
		askToSaveProgress();
	}

	private void askToSaveProgress() {
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View layout = (View) inflater.inflate(R.layout.prompt_download,
				(ViewGroup) findViewById(R.id.popup_element));

		this.mPopup = new PopupWindow(layout, 200, 200, false);
		mPopup.showAtLocation(layout, Gravity.CENTER, 0, 0);

		ViewGroup popupContainer = (ViewGroup) layout;

		((TextView) popupContainer.getChildAt(0))
				.setText("You are about to leave this book");
		((TextView) popupContainer.getChildAt(1))
				.setText("Do you want to save your progress?");

		Button Ok = (Button) ((ViewGroup) popupContainer.getChildAt(2))
				.getChildAt(0);
		Button Cancel = (Button) ((ViewGroup) popupContainer.getChildAt(2))
				.getChildAt(1);

		Cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPopup.dismiss();
				onSaveProgressChoice(false);
			}
		});
		Ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPopup.dismiss();
				onSaveProgressChoice(true);
			}
		});
	}

	private void onSaveProgressChoice(boolean saveProgress) {
		if (saveProgress) {
			this.mBookToRead.positionPointer = this.mQueueHandler
					.getCurrentPosition();
		}
		boolean docReaderIsFake = this.getResources().getBoolean(
				R.bool.useFakeDocReader);
		if (!docReaderIsFake) {
			this.mLocalDataHandler.updateBook(this.mBookToRead);
		} else {
			showNotification(
					"Read0r progress was not saved",
					"This program is using the FakeDocumentReader, and therefore saving any data might lead to problems later.",
					R.id.notificationId_error);
		}
		this.finish();
	}

	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		float scaleFactor = detector.getScaleFactor() / 2;
		this.mFontSize *= scaleFactor;
		// Don't let the font get too small or too large.
		this.mFontSize = (int) Math.max(this.mMinFontSize,
				Math.min(this.mFontSize, this.mMaxFontSize));
		this.updateFontSize();
		return false;
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
		if (this.mDateOfLastTap != null) {
			long timeSpan = new Date().getTime() - mDateOfLastTap.getTime();
			if (0 < timeSpan && timeSpan < this.mMillisecondsBetweenClicks) {
				this.pauseReading();
				this.mDateOfLastTap = null;
				return true;
			}
		}
		this.mDateOfLastTap = new Date();
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
		this.mDelayMultiplicator = 0.2;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (velocityX < 0) {
			this.mQueueHandler.goBack();
		}
		return true;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
			Toast.makeText(getApplicationContext(), "working",
					Toast.LENGTH_SHORT).show();
			float distanceSm = event.values[0];
			if (distanceSm < 20) {
				if (!this.mPaused) {
					this.pauseReading();
				}

				Toast.makeText(
						this,
						"Please do not read from so close. It can be bad for your eyes.",
						Toast.LENGTH_SHORT).show();
			}
		}
	}
}
