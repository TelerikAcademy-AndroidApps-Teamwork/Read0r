package com.example.read0r.Activities;

import com.example.read0r.R;
import com.example.read0r.Models.ReadableBook;
import com.example.read0r.Views.ReadableBooksWidget;
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

	private Intent mReadIntent;
	private int mTheme;
	private ReadableBook mCurrentBook;
	private Button mBackBtn;
	private Button mReadBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_read_select);
		
		this.mReadIntent = new Intent(ReadSelectActivity.this,
				ReadActivity.class);
		
		this.mTheme = this.getResources().getInteger(R.integer.theme);

		this.mBackBtn = (Button) this.findViewById(R.id.select_backButton);
		this.mReadBtn = (Button) this.findViewById(R.id.select_readButton);

		this.mBackBtn.setOnClickListener(this);
		this.mReadBtn.setOnClickListener(this);
		
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
		ReadableBooksWidget v = (ReadableBooksWidget)this.findViewById(R.id.readableBooksWidget1);
		this.mCurrentBook = v.getCurrentBook();

		this.mReadIntent.putExtra("book_id", this.mCurrentBook.id);
		
		this.startActivity(this.mReadIntent);
	}

	public void onClick(View v) {
		if (v.getId() == R.id.select_backButton) {
			goBack();
		} else if (v.getId() == R.id.select_readButton) {
			goToRead();
		}
	}
}
