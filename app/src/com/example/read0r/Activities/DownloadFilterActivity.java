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

	private Intent mBackIntent;
	private int mTheme;
	private ArrayList<String> mFilters;
	private Button mBackBtn;
	private Button mFilterBtn;
	private ArrayList<String> mCategories;
	private LinearLayout mCategoriesList;
	private ArrayList<String> mOriginFilters;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download_filter);

		this.mBackIntent = new Intent(DownloadFilterActivity.this,
				DownloadActivity.class);

		this.mTheme = this.getResources().getInteger(R.integer.theme);

		this.mBackBtn = (Button) this.findViewById(R.id.filter_backButton);
		this.mFilterBtn = (Button) this.findViewById(R.id.filter_filterButton);

		this.mCategoriesList = (LinearLayout) this
				.findViewById(R.id.filter_categoriesList);

		this.mBackBtn.setOnClickListener(this);
		this.mFilterBtn.setOnClickListener(this);

		if (this.getIntent().hasExtra("filters")) {
			this.mOriginFilters = (ArrayList<String>) this.getIntent()
					.getExtras().get("filters");
		}

		this.applyTheme();
		this.initFilters();
		this.drawFilters();
	}

	private void applyTheme() {
		// TODO : Apply the theme
	}

	private void initFilters() {
		this.mFilters = new ArrayList<String>();
		for (String string : mOriginFilters) {
			this.mFilters.add(string);
		}

		if (this.getIntent().hasExtra("categories")) {
			this.mCategories = (ArrayList<String>) this.getIntent().getExtras()
					.get("categories");
		} else {
			this.mCategories = new ArrayList<String>();
		}
	}

	private void drawFilters() {
		this.mCategoriesList.removeAllViews();

		for (int i = 0; i < this.mCategories.size(); i++) {
			String cat = this.mCategories.get(i);
			CheckBox cb = new CheckBox(this);
			cb.setText(cat);
			for (String filt : this.mFilters) {
				if (filt.equals(cat)) {
					cb.setChecked(true);
				}
			}

			cb.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					CheckBox chb = (CheckBox) v;
					if (chb.isChecked()) {
						mFilters.add(chb.getText().toString());
					} else {
						mFilters.remove(mFilters
								.indexOf(chb.getText().toString()));
					}
				}
			});

			this.mCategoriesList.addView(cb);
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
		this.mBackIntent.putExtra("filters", this.mFilters);

		if (getParent() == null) {
			setResult(Activity.RESULT_OK, this.mBackIntent);
		} else {
			getParent().setResult(Activity.RESULT_OK, this.mBackIntent);
		}

		this.finish();
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
