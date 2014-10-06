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

public class SettingsActivity extends ActionBarActivity {

	private int theme;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
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

	void goBack() {
		this.finish();
	}

	void saveSettings() {
		// TODO : Save the settings
		this.goBack();
	}
}
