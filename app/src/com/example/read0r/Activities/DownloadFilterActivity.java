package com.example.read0r.Activities;

import java.util.ArrayList;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

public class DownloadFilterActivity extends ActionBarActivity implements
		OnClickListener {

	private Intent backIntent;
	private int theme;
	private ArrayList<String> filters;
	private Button backBtn;
	private Button filterBtn;
	private ArrayList<String> categories;
	private LinearLayout categoriesList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download_filter);

		this.backIntent = new Intent(DownloadFilterActivity.this,
				DownloadActivity.class);

		this.theme = this.getResources().getInteger(R.integer.theme);

		this.backBtn = (Button) this.findViewById(R.id.filter_backButton);
		this.filterBtn = (Button) this.findViewById(R.id.filter_filterButton);

		this.categoriesList = (LinearLayout) this.findViewById(R.id.filter_categoriesList);
		
		this.backBtn.setOnClickListener(this);
		this.filterBtn.setOnClickListener(this);

		this.applyTheme();
		this.initFilters();
		this.drawFilters();
	}

	private void applyTheme() {
		// TODO : Apply the theme
	}

	private void initFilters() {
		if (this.getIntent().hasExtra("filters")) {
			this.filters = (ArrayList<String>) this.getIntent().getExtras()
					.get("filters");
		} else {
			this.filters = new ArrayList<String>();
		}

		if (this.getIntent().hasExtra("categories")) {
			this.categories = (ArrayList<String>) this.getIntent().getExtras()
					.get("categories");
		} else {
			this.categories = new ArrayList<String>();
		}
	}

	private void drawFilters() {
		this.categoriesList.removeAllViews();
		
		for (int i = 0; i < this.categories.size(); i++) {
			String cat = this.categories.get(i);
			CheckBox cb = new CheckBox(this);
			cb.setText(cat);
			for (String filt : this.filters) {
				if (filt.equals(cat)) {
					cb.setChecked(true);
				}
			}
			
			cb.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					CheckBox chb = (CheckBox)v;
					if (chb.isChecked()) {
						filters.add(chb.getText().toString());
					} else {
						filters.remove(filters.indexOf(chb.getText().toString()));
					}
				}
			});
			
			this.categoriesList.addView(cb);
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

	public void goBackSave() {
		this.backIntent.putExtra("filters", this.filters);

		this.startActivity(this.backIntent);

		if (getParent() == null) {
			setResult(Activity.RESULT_OK, this.backIntent);
		} else {
			getParent().setResult(Activity.RESULT_OK, this.backIntent);
		}

		finish();
	}

	public void goBackCancel() {
		this.initFilters();
		this.goBackSave();
	}

	public void onClick(View v) {
		if (v.getId() == R.id.filter_backButton) {
			goBackCancel();
		} else if (v.getId() == R.id.filter_filterButton) {
			goBackSave();
		}

	}
}
