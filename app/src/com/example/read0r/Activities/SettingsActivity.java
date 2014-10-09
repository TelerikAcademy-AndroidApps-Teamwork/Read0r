package com.example.read0r.Activities;

import com.example.read0r.R;
import com.example.read0r.R.id;
import com.example.read0r.R.layout;
import com.example.read0r.R.menu;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;

public class SettingsActivity extends ActionBarActivity implements
		OnClickListener {

	private int theme;
	private RadioButton lightThemeRadio;
	private RadioButton darkThemeRadio;
	private Button backBtn;
	private Button saveBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		this.backBtn = (Button) this.findViewById(R.id.settings_backButton);
		this.saveBtn = (Button) this.findViewById(R.id.settings_saveButton);

		this.lightThemeRadio = (RadioButton) this
				.findViewById(R.id.settings_lightRadio);
		this.darkThemeRadio = (RadioButton) this
				.findViewById(R.id.settings_darkRadio);

		this.backBtn.setOnClickListener(this);
		this.saveBtn.setOnClickListener(this);
		this.lightThemeRadio.setOnClickListener(this);
		this.darkThemeRadio.setOnClickListener(this);
		
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

	public void goBack() {
		this.finish();
	}

	public void saveSettings() {
		// TODO : Save the settings
		this.goBack();
	}

	private void changeTheme(int color) {
		this.theme = color;
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
