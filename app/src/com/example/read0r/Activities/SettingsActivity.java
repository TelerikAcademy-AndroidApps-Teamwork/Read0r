package com.example.read0r.Activities;

import com.example.read0r.R;
import com.example.read0r.R.id;
import com.example.read0r.R.layout;
import com.example.read0r.R.menu;

import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SettingsActivity extends ActionBarActivity implements
		OnClickListener {

	private int mTheme;
	private RadioButton mLightThemeRadio;
	private RadioButton mDarkThemeRadio;
	private Button mBackBtn;
	private Button mSaveBtn;
	private int mFontSize;
	private int mSpeedPercent;
	private EditText mFontInput;
	private EditText mSpeedInput;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		this.mBackBtn = (Button) this.findViewById(R.id.settings_backButton);
		this.mSaveBtn = (Button) this.findViewById(R.id.settings_saveButton);

		this.mLightThemeRadio = (RadioButton) this
				.findViewById(R.id.settings_lightRadio);
		this.mDarkThemeRadio = (RadioButton) this
				.findViewById(R.id.settings_darkRadio);

		this.mFontInput = (EditText) this.findViewById(R.id.settings_fontInput);
		this.mSpeedInput = (EditText) this
				.findViewById(R.id.settings_speedInput);

		this.mBackBtn.setOnClickListener(this);
		this.mSaveBtn.setOnClickListener(this);
		this.mLightThemeRadio.setOnClickListener(this);
		this.mDarkThemeRadio.setOnClickListener(this);

		loadSettings();
		this.applyTheme();
		this.displaySettings();
	}

	private void loadSettings() {

		this.mTheme = com.example.read0r.Settings.getTheme(this);
		this.mFontSize = com.example.read0r.Settings.getFontSize(this);
		this.mSpeedPercent = com.example.read0r.Settings.getReadingSpeed(this);
	}

	private void applyTheme() {
		// TODO : Apply the theme
	}

	private void displaySettings() {
		this.mFontInput.setText(this.mFontSize + "");
		this.mSpeedInput.setText(this.mSpeedPercent + "");

		if (this.mTheme == Color.WHITE) {
			this.mDarkThemeRadio.setChecked(false);
			this.mLightThemeRadio.setChecked(true);
		} else if (this.mTheme == Color.BLACK) {
			this.mLightThemeRadio.setChecked(false);
			this.mDarkThemeRadio.setChecked(true);
		}
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

	public void goBack() {
		this.finish();
	}

	public void saveSettings() {
		this.mFontSize = Integer.parseInt(this.mFontInput.getText().toString());
		this.mSpeedPercent = Integer.parseInt(this.mSpeedInput.getText()
				.toString());

		boolean settingsCommited = true;
		settingsCommited = com.example.read0r.Settings.setFontSize(this,
				this.mFontSize);
		settingsCommited = settingsCommited
				&& com.example.read0r.Settings.setReadingSpeed(this,
						this.mSpeedPercent);
		settingsCommited = settingsCommited
				&& com.example.read0r.Settings.setTheme(this, this.mTheme);

		if (!settingsCommited) {
			showNotification("Read0r settings were not saved",
					"There was a problem saving your settings",
					R.id.notificationId_settingsError);
		}

		this.goBack();
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

	private void changeTheme(int color) {
		this.mTheme = color;
		this.applyTheme();
	}

	public void onClick(View v) {
		if (v.getId() == R.id.settings_backButton) {
			goBack();
		} else if (v.getId() == R.id.settings_saveButton) {
			saveSettings();
		} else if (v.getId() == R.id.settings_darkRadio) {
			changeTheme(Color.BLACK);
		} else if (v.getId() == R.id.settings_lightRadio) {
			changeTheme(Color.WHITE);
		}
	}

}
