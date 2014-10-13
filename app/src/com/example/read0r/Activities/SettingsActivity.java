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
import android.widget.EditText;
import android.widget.RadioButton;

public class SettingsActivity extends ActionBarActivity implements
		OnClickListener {

	private int theme;
	private RadioButton lightThemeRadio;
	private RadioButton darkThemeRadio;
	private Button backBtn;
	private Button saveBtn;
	private int fontSize;
	private int speedPercent;
	private EditText fontInput;
	private EditText speedInput;

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

		this.fontInput = (EditText) this.findViewById(R.id.settings_fontInput);
		this.speedInput = (EditText) this
				.findViewById(R.id.settings_speedInput);

		this.backBtn.setOnClickListener(this);
		this.saveBtn.setOnClickListener(this);
		this.lightThemeRadio.setOnClickListener(this);
		this.darkThemeRadio.setOnClickListener(this);

		loadSettings();
		this.applyTheme();
		this.displaySettings();
	}

	private void loadSettings() {
		this.theme = this.getResources().getInteger(R.integer.theme);
		this.fontSize = this.getResources().getInteger(R.integer.fontSize);
		this.speedPercent = this.getResources().getInteger(
				R.integer.speedPercent);
	}

	private void applyTheme() {
		// TODO : Apply the theme
	}

	private void displaySettings() {
		this.fontInput.setText(this.fontSize);
		this.speedInput.setText(this.speedPercent);

		if (this.theme == Color.WHITE) {
			this.darkThemeRadio.setChecked(false);
			this.lightThemeRadio.setChecked(true);
		} else if (this.theme == Color.BLACK) {
			this.lightThemeRadio.setChecked(false);
			this.darkThemeRadio.setChecked(true);
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
		this.getResources();
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
