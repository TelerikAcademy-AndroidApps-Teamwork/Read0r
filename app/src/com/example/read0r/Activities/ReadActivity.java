package com.example.read0r.Activities;

import java.sql.SQLException;
import java.util.Date;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
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
	private boolean twitterCanceled;

	private static SharedPreferences mSharedPreferences;

	// Replace the following value with the Consumer key
	static String TWITTER_CONSUMER_KEY = "2W8bYiAtb4RY66HWXAVYJ4SIo";
	// Replace the following value with the Consumer secret
	static String TWITTER_CONSUMER_SECRET = "LpgZyYXQ84NJ35TeeYkiFxSWwNRWpHbE7kBLaGuEuadZLF7oFM";

	static String PREFERENCE_NAME = "twitter _ oauth";
	static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
	static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
	static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLoggedIn";

	static final String TWITTER_CALLBACK_URL = "oauth://tcookbook";

	static final String URL_TWITTER_AUTH = "auth_url";
	static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
	static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";

	private static Twitter twitter;
	private static RequestToken requestToken;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_read);

		loadSettings();
		loadBookInformation();

		mSharedPreferences = getApplicationContext().getSharedPreferences(
				"read0rTwitter", 0);

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
			this.mLocalDataHandler = new Read0rLocalData(this);
		}
		try {
			this.mBookToRead = this.mLocalDataHandler.getBookById(bookId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
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

		askToBoastOnline();

		showNotification("You have finished reading a book",
				"The book's name was '" + this.mBookToRead.title
						+ "' and it was writen by by "
						+ this.mBookToRead.author,
				R.id.notificationId_finishedBook);

	}

	private void showNotification(String title, String content,
			int notificationidId) {

		try {
			Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
					R.drawable.attention);
			Notification.Builder builder = new Notification.Builder(this)
					.setSmallIcon(R.drawable.attention)
					.setLargeIcon(
							Bitmap.createScaledBitmap(largeIcon, 128, 128,
									false)).setContentTitle(title)
					.setContentText(content);
			Notification notification = builder.getNotification();
			((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
					.notify(notificationidId, notification);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void askToBoastOnline() {

		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;

		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View layout = (View) inflater.inflate(R.layout.prompt_basic,
				(ViewGroup) findViewById(R.id.popup_element));

		int sizeOfPopup = (int) Math.min(width / 1.2, height / 1.2);

		this.mPopup = new PopupWindow(layout, sizeOfPopup, sizeOfPopup, false);
		mPopup.showAtLocation(layout, Gravity.CENTER, 0, 0);

		ViewGroup popupContainer = (ViewGroup) layout;

		((TextView) popupContainer.getChildAt(0)).setText("The book has ended");
		((TextView) popupContainer.getChildAt(1))
				.setText("Do you want to tweet in twitter about it");

		Button Ok = (Button) ((ViewGroup) popupContainer.getChildAt(2))
				.getChildAt(0);
		Button Cancel = (Button) ((ViewGroup) popupContainer.getChildAt(2))
				.getChildAt(1);

		Cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPopup.dismiss();
				onBoastOnlineChoice(false);
			}
		});
		Ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPopup.dismiss();
				onBoastOnlineChoice(true);
			}
		});
	}

	private void onBoastOnlineChoice(boolean boastOnline) {
		if (boastOnline) {

			loginToTwitter();

			while (!isTwitterLoggedInAlready() && !twitterCanceled) {
			}

			String status = "Whatever I want";
			new updateTwitterStatus().execute(status);

			logoutFromTwitter();
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
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;

		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View layout = (View) inflater.inflate(R.layout.prompt_basic,
				(ViewGroup) findViewById(R.id.popup_element));

		int sizeOfPopup = (int) Math.min(width / 1.2, height / 1.2);

		this.mPopup = new PopupWindow(layout, sizeOfPopup, sizeOfPopup, false);
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
				finish();
			}
		});
		Ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPopup.dismiss();
				onSaveProgressChoice(true);
				finish();
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
			try {
				this.mLocalDataHandler.updateBook(this.mBookToRead);
			} catch (SQLException e) {
				e.printStackTrace();
				showNotification(
						"Read0r progress was not saved",
						"The local data base of the app was not working normally.",
						R.id.notificationId_error);
			}
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
		if (velocityX > 0) {
			this.mQueueHandler.goBack();
			Toast.makeText(getApplicationContext(), "going back...",
					Toast.LENGTH_SHORT).show();
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

	private void loginToTwitter() {
		if (!isTwitterLoggedInAlready()) {
			ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
			builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
			Configuration configuration = builder.build();

			TwitterFactory factory = new TwitterFactory(configuration);
			twitter = factory.getInstance();

			if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)) {
				try {
					requestToken = twitter
							.getOAuthRequestToken(TWITTER_CALLBACK_URL);
					this.startActivity(new Intent(Intent.ACTION_VIEW, Uri
							.parse(requestToken.getAuthenticationURL())));
				} catch (TwitterException e) {
					e.printStackTrace();
					twitterCanceled = true;
				}
			} else {
				new Thread(new Runnable() {
					public void run() {
						try {
							requestToken = twitter
									.getOAuthRequestToken(TWITTER_CALLBACK_URL);
							startActivity(new Intent(Intent.ACTION_VIEW, Uri
									.parse(requestToken.getAuthenticationURL())));
						} catch (TwitterException e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
		} else {
			Toast.makeText(getApplicationContext(),
					"Already logged into Twitter", Toast.LENGTH_LONG).show();
		}
	}

	class updateTwitterStatus extends AsyncTask<String, String, String> {

		protected String doInBackground(String... args) {
			String status = args[0];
			try {
				ConfigurationBuilder builder = new ConfigurationBuilder();
				builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
				builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);

				String access_token = mSharedPreferences.getString(
						PREF_KEY_OAUTH_TOKEN, "");
				String access_token_secret = mSharedPreferences.getString(
						PREF_KEY_OAUTH_SECRET, "");

				AccessToken accessToken = new AccessToken(access_token,
						access_token_secret);
				Twitter twitter = new TwitterFactory(builder.build())
						.getInstance(accessToken);

				twitter4j.Status response = twitter.updateStatus(status);

			} catch (TwitterException e) {
				Log.d("*** Twitter Update Error: ", e.getMessage());
			}
			return null;
		}

		protected void onPostExecute(String file_url) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(getApplicationContext(),
							"Status tweeted successfully", Toast.LENGTH_SHORT)
							.show();
				}
			});
		}

	}

	private void logoutFromTwitter() {
		Editor e = mSharedPreferences.edit();
		e.remove(PREF_KEY_OAUTH_TOKEN);
		e.remove(PREF_KEY_OAUTH_SECRET);
		e.remove(PREF_KEY_TWITTER_LOGIN);
		e.commit();
	}

	private boolean isTwitterLoggedInAlready() {
		return mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
	}

	protected void onResume() {
		super.onResume();
	}
}
