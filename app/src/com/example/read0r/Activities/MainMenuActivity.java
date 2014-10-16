package com.example.read0r.Activities;

import com.example.read0r.R;
import com.example.read0r.R.id;
import com.example.read0r.R.layout;
import com.example.read0r.R.menu;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainMenuActivity extends ActionBarActivity implements
		OnClickListener {

	private Intent mReadSelectIntent;
	private Intent mDownloadIntent;
	private Intent mSettingsIntent;
	private int mTheme;
	private Button mReadBtn;
	private Button mDownloadBtn;
	private Button mSettingsBtn;
	private Button mQuitBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);

		this.mReadSelectIntent = new Intent(MainMenuActivity.this,
				ReadSelectActivity.class);
		this.mDownloadIntent = new Intent(MainMenuActivity.this,
				DownloadActivity.class);
		this.mSettingsIntent = new Intent(MainMenuActivity.this,
				SettingsActivity.class);

		this.mReadBtn = (Button) this.findViewById(R.id.main_readButton);
		this.mDownloadBtn = (Button) this
				.findViewById(R.id.main_downloadButton);
		this.mSettingsBtn = (Button) this
				.findViewById(R.id.main_settingsButton);
		this.mQuitBtn = (Button) this.findViewById(R.id.main_quitButton);

		this.mReadBtn.setOnClickListener(this);
		this.mDownloadBtn.setOnClickListener(this);
		this.mSettingsBtn.setOnClickListener(this);
		this.mQuitBtn.setOnClickListener(this);

		this.mTheme = com.example.read0r.Settings.getTheme(this);

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

	public void onClick(View v) {
		if (v.getId() == R.id.main_readButton) {
			goToReadSelect();
		} else if (v.getId() == R.id.main_downloadButton) {
			goToDownload();
		} else if (v.getId() == R.id.main_settingsButton) {
			goToSettings();
		} else if (v.getId() == R.id.main_quitButton) {
			quit();
		}
	}

	public void goToDownload() {
		this.startActivity(this.mDownloadIntent);
	}

	public void goToReadSelect() {
		this.startActivity(this.mReadSelectIntent);

	}

	public void goToSettings() {
		this.startActivity(this.mSettingsIntent);
	}

	public void quit() {
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(1);
	}

}
