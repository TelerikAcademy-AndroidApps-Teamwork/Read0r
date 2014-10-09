package com.example.read0r.Activities;

import com.example.read0r.R;
import com.example.read0r.Models.ReadableBook;
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

public class ReadSelectActivity extends ActionBarActivity implements OnClickListener{

	private Intent readIntent;
	private int theme;
	private ReadableBook currentBook;
	private Button backBtn;
	private Button readBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_read_select);
		
		this.readIntent = new Intent(ReadSelectActivity.this,
				ReadActivity.class);
		
		this.theme = this.getResources().getInteger(R.integer.theme);

		this.backBtn = (Button) this.findViewById(R.id.select_backButton);
		this.readBtn = (Button) this.findViewById(R.id.select_readButton);

		this.backBtn.setOnClickListener(this);
		this.readBtn.setOnClickListener(this);
		
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

	public void goToRead() {
		this.readIntent.putExtra("bookId", this.currentBook.id);
		this.startActivity(this.readIntent);
	}

	public void onClick(View v) {
		if (v.getId() == R.id.select_backButton) {
			goBack();
		} else if (v.getId() == R.id.select_readButton) {
			goToRead();
		}
	}
}
