package com.example.read0r.Activities;

import com.example.read0r.R;
import com.example.read0r.R.id;
import com.example.read0r.R.layout;
import com.example.read0r.R.menu;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class DownloadFilterActivity extends ActionBarActivity {

	private Intent backIntent;
	private int theme;
	private String[] filters;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download_filter);

		this.backIntent = new Intent(DownloadFilterActivity.this,
				DownloadActivity.class);

		this.theme = this.getResources().getInteger(R.integer.theme);

		this.applyTheme();
		this.initFilters();
		this.drawFilters();
	}

	private void applyTheme() {
		// TODO : Apply the theme
	}

	private void initFilters() {
		if (this.getIntent().hasExtra("filters")) {
			this.filters = this.getIntent().getStringArrayExtra("filters");
		} else {
			this.filters = new String[0];
		}
	}

	private void drawFilters() {
		// TODO : draw the menu
		
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

	void goBackSave() {
		this.backIntent.putExtra("filters", this.filters);
		
		this.startActivity(this.backIntent);
		
		if (getParent() == null) {
		    setResult(Activity.RESULT_OK, this.backIntent);
		} else {
		    getParent().setResult(Activity.RESULT_OK, this.backIntent);
		}
		
		finish();
	}

	void goBackCancel() {
		this.initFilters();
		this.goBackSave();
	}
}
