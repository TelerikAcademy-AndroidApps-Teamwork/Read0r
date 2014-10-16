package com.example.read0r.Activities;

import java.sql.SQLException;

import com.example.read0r.R;
import com.example.read0r.Models.ReadableBook;
import com.example.read0r.Views.ReadableBooksWidget;
import com.example.read0r.DatabaseHelper;
import com.example.read0r.R.id;
import com.example.read0r.R.layout;
import com.example.read0r.R.menu;
import com.example.read0r.Read0rLocalData;

import android.support.v7.app.ActionBarActivity;
import android.R.integer;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
public class ReadSelectActivity extends ActionBarActivity implements
		OnClickListener {

	private Intent mReadIntent;
	private int mTheme;
	private ReadableBook mCurrentBook;
	private Button mBackBtn;
	private Button mReadBtn;
	private PopupWindow mPopup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_read_select);

		this.mReadIntent = new Intent(ReadSelectActivity.this,
				ReadActivity.class);

		this.mTheme = com.example.read0r.Settings.getTheme(this);

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
		ReadableBooksWidget v = (ReadableBooksWidget) this
				.findViewById(R.id.readableBooksWidget1);
		this.mCurrentBook = v.getCurrentBook();
		this.mReadIntent.putExtra("book_id", this.mCurrentBook.id);
		this.startActivity(this.mReadIntent);
	}

	public void onClick(View v) {
		if (v.getId() == R.id.select_backButton) {
			goBack();
		} else if (v.getId() == R.id.select_readButton) {
			displayPrompt();
		}
	}

	public void displayPrompt() {
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;

		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View layout = (View) inflater.inflate(R.layout.prompt_read,
				(ViewGroup) findViewById(R.id.read_prompt_container));

		int sizeOfPopup = (int) Math.min(width / 1.2, height / 1.2);

		this.mPopup = new PopupWindow(layout, sizeOfPopup, sizeOfPopup, false);
		mPopup.showAtLocation(layout, Gravity.CENTER, 0, 0);

		ViewGroup popupContainer = (ViewGroup) layout;

		Button Ok = (Button) ((ViewGroup) popupContainer.getChildAt(2))
				.getChildAt(0);
		Button Cancel = (Button) ((ViewGroup) popupContainer.getChildAt(2))
				.getChildAt(1);

		Ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPopup.dismiss();
				onReadFromPointerChoice(true);
			}
		});
		Cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPopup.dismiss();
				onReadFromPointerChoice(false);
			}
		});
	}

	private void onReadFromPointerChoice(boolean choice) {
		if (!choice) {
			try {
				ReadableBooksWidget v = (ReadableBooksWidget) this
						.findViewById(R.id.readableBooksWidget1);
				this.mCurrentBook = v.getCurrentBook();
				Read0rLocalData db = new Read0rLocalData(this);
				this.mCurrentBook.positionPointer = 0;
				db.updateBook(this.mCurrentBook);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		goToRead();
	}
}
