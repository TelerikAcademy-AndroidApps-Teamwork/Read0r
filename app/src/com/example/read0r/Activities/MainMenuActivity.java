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

public class MainMenuActivity extends ActionBarActivity implements OnClickListener {

	private Intent readSelectIntent;
	private Intent downloadIntent;
	private Intent settingsIntent;
	private int theme;
	private Button readBtn;
	private Button downloadBtn;
	private Button settingsBtn;
	private Button quitBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);

		this.readSelectIntent = new Intent(MainMenuActivity.this,
				ReadSelectActivity.class);
		this.downloadIntent = new Intent(MainMenuActivity.this,
				DownloadActivity.class);
		this.settingsIntent = new Intent(MainMenuActivity.this,
				SettingsActivity.class);

		this.readBtn = (Button) this.findViewById(R.id.main_readButton);
		this.downloadBtn = (Button) this.findViewById(R.id.main_downloadButton);
		this.settingsBtn = (Button) this.findViewById(R.id.main_settingsButton);
		this.quitBtn = (Button) this.findViewById(R.id.main_quitButton);

		this.readBtn.setOnClickListener(this);
		this.downloadBtn.setOnClickListener(this);
		this.settingsBtn.setOnClickListener(this);
		this.quitBtn.setOnClickListener(this);
		
		this.theme = this.getResources().getInteger(R.integer.theme);
		
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
		this.startActivity(this.downloadIntent);
	}

	public void goToReadSelect() {
		this.startActivity(this.readSelectIntent);

	}

	public void goToSettings() {
		this.startActivity(this.settingsIntent);
	}

	public void quit() {
		throw new Error("Not Implemented");
	}

}
