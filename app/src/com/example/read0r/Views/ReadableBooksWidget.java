package com.example.read0r.Views;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.read0r.R;
import com.example.read0r.Read0rDistantData;
import com.example.read0r.Read0rLocalData;
import com.example.read0r.Activities.ReadSelectActivity;
import com.example.read0r.Fakes.FakeDistantDataHandler;
import com.example.read0r.Fakes.FakeLocalDataHandler;
import com.example.read0r.Interfaces.ILocalDataHandler;
import com.example.read0r.Models.DownloadableBook;
import com.example.read0r.Models.ReadableBook;

import android.R.integer;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.ScaleGestureDetector.OnScaleGestureListener;

public class ReadableBooksWidget extends View implements OnGestureListener,
		OnScaleGestureListener {
	private ILocalDataHandler mLocalDataHandler;
	private List<ReadableBook> mBooks;
	private ReadableBook mCurrentBook;
	private int mCurrentBookIndex;

	private GestureDetector mGuestureDetector;
	private ScaleGestureDetector mScaleDetector;

	private Paint mCurrentBackgroundPaint;
	private Paint mCurrentFontPaint;
	private Paint mNotCurrentPaint;
	private boolean mInitialized;

	private int mWidth;
	private int mHeight;
	private int mCellWidth;
	private int mCellHeight;

	private int mCurrentElementLeft;
	private int mCurrentElementTop;
	private int mCurrentElementRight;
	private int mCurrentElementBottom;

	private int mNextElementLeft;
	private int mNextElementTop;
	private int mNextElementBottom;
	private int mNextElementRight;

	private int mPrevElementLeft;
	private int mPrevElementTop;
	private int mPrevElementRight;
	private int mPrevElementBottom;

	private int mOffset;
	private int mCenterLineVertical;
	private Date mDateOfLastTap;
	private long mMillisecondsBetweenClicks = 500;

	public ReadableBooksWidget(Context context) {
		this(context, null, 0);
	}

	public ReadableBooksWidget(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ReadableBooksWidget(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		boolean localDataIsFake = this.getResources().getBoolean(
				R.bool.useFakeLocalData);

		if (localDataIsFake) {
			this.mLocalDataHandler = new FakeLocalDataHandler();
		} else {
			this.mLocalDataHandler = new Read0rLocalData(this.getContext());
		}

		try {
			this.mBooks = this.mLocalDataHandler.getBooks();
			this.mCurrentBook = this.mBooks.size() > 0 ? this.mBooks.get(0)
					: new ReadableBook("", "", "", 1, "", 0);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.mCurrentBookIndex = 0;

		this.mGuestureDetector = new GestureDetector(this.getContext(), this);
		this.mScaleDetector = new ScaleGestureDetector(this.getContext(), this);

		this.mCurrentBackgroundPaint = new Paint();
		this.mCurrentBackgroundPaint.setColor(Color.LTGRAY);

		this.mCurrentFontPaint = new Paint();
		this.mCurrentFontPaint.setColor(Color.BLACK);

		this.mNotCurrentPaint = new Paint();
		this.mNotCurrentPaint.setColor(Color.BLACK);
	}

	@Override
	public void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		if (changed || this.mInitialized) {
			this.mInitialized = true;

			this.mWidth = Math.abs(left - right);
			this.mHeight = Math.abs(top - bottom);
			this.mCellWidth = this.mWidth / 11;
			this.mCellHeight = this.mWidth / 10;

			this.mCurrentElementLeft = this.mCellWidth * 2;
			this.mCurrentElementTop = this.mCellHeight * 2;
			this.mCurrentElementRight = this.mCellWidth * 9;
			this.mCurrentElementBottom = this.mCellHeight * 8;

			this.mNextElementLeft = this.mCellWidth * 10;
			this.mNextElementTop = this.mCellHeight;
			this.mNextElementRight = this.mCellWidth * 14;
			this.mNextElementBottom = this.mCellHeight * 7;

			this.mPrevElementLeft = this.mCellWidth * -3;
			this.mPrevElementTop = this.mCellHeight;
			this.mPrevElementRight = this.mCellWidth;
			this.mPrevElementBottom = this.mCellHeight * 7;
		}
	}

	@Override
	public void onDraw(Canvas canvas) {

		canvas.translate(this.mOffset, 0);
		
		int drawLen = this.mWidth / 3;
		if (this.mOffset > drawLen) {
			selectPrevElement();
		} else if (-this.mOffset > drawLen) {
			selectNextElement();
		}

		if (this.mCurrentBookIndex > 0) {
			canvas.drawRect(mPrevElementLeft, mPrevElementTop,
					mPrevElementRight, mPrevElementBottom, mNotCurrentPaint);
		}

		canvas.drawRect(mCurrentElementLeft, mCurrentElementTop,
				mCurrentElementRight, mCurrentElementBottom,
				mCurrentBackgroundPaint);

		if (this.mCurrentBookIndex < this.mBooks.size() - 1) {
			canvas.drawRect(mNextElementLeft, mNextElementTop,
					mNextElementRight, mNextElementBottom, mNotCurrentPaint);
		}

		this.mCurrentFontPaint.setTextSize(this.determineMaxTextSize(
				this.mCurrentBook, this.mCellWidth * 5));

		this.drawTextStill(canvas, (this.mCurrentBookIndex + 1) + " / "
				+ this.mBooks.size(), this.mCellHeight);

		this.drawText(canvas, this.mCurrentBook.title, this.mCellHeight * 3);
		this.drawText(canvas, this.mCurrentBook.author, this.mCellHeight * 4);
		this.drawText(canvas, getCategory(this.mCurrentBook),
				this.mCellHeight * 5);
		this.drawText(canvas, getProgress(this.mCurrentBook),
				this.mCellHeight * 6);

		this.mCurrentFontPaint.setTextSize(this.determineMaxTextSize(
				this.mCurrentBook, this.mCellWidth * 5) / 2);

		this.drawText(canvas, "(long press to read)", this.mCellHeight * 7);

		super.onDraw(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean simpleGuestureHandled = this.mGuestureDetector
				.onTouchEvent(event);
		boolean scaleGuestureHandled = this.mScaleDetector.onTouchEvent(event);
		return simpleGuestureHandled || scaleGuestureHandled;
	}

	private void drawText(Canvas canvas, String str, int y) {
		float x = (this.mWidth / 2)
				- (this.mCurrentFontPaint.measureText(str) / 2);
		canvas.drawText(str, x, y, this.mCurrentFontPaint);
	}

	private void drawTextStill(Canvas canvas, String str, int y) {
		float x = (this.mWidth / 2)
				- (this.mCurrentFontPaint.measureText(str) / 2) - this.mOffset;
		canvas.drawText(str, x, y, this.mCurrentFontPaint);
	}

	private void selectNextElement() {
		if (this.mCurrentBookIndex < this.mBooks.size() - 1) {
			this.mCurrentBookIndex++;
			this.mCurrentBook = this.mBooks.get(this.mCurrentBookIndex);
			this.mOffset = 0;
			this.invalidate();
		}
	}

	private void selectPrevElement() {
		if (this.mCurrentBookIndex > 0) {
			this.mCurrentBookIndex--;
			this.mCurrentBook = this.mBooks.get(this.mCurrentBookIndex);
			this.mOffset = 0;
			this.invalidate();
		}
	}

	private int determineMaxTextSize(ReadableBook book, float maxWidth) {
		String str = book.title;
		if (str.length() < book.author.length()) {
			str = book.author;
		}
		if (str.length() < getProgress(book).length()) {
			str = getProgress(book);
		}
		if (str.length() < getCategory(book).length()) {
			str = getCategory(book);
		}

		int size = 12;
		Paint paint = new Paint();

		do {
			paint.setTextSize(++size);
		} while (paint.measureText(str) < maxWidth && size < 50);

		return Math.min(size, 50);
	}

	private String getCategory(ReadableBook book) {
		return "category: " + book.category;
	}

	private String getProgress(ReadableBook book) {
		int percent = 0;
		if (book.length > 0) {
			percent = (int) (book.positionPointer * 100 / book.length);
		}
		return "Progress: " + percent + " %";
	}

	public boolean onScale(ScaleGestureDetector detector) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean onScaleBegin(ScaleGestureDetector detector) {
		// TODO Auto-generated method stub
		return false;
	}

	public void onScaleEnd(ScaleGestureDetector detector) {
		// TODO Auto-generated method stub

	}

	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	public boolean onSingleTapUp(MotionEvent e) {
		if (this.mDateOfLastTap != null) {
			long timeSpan = new Date().getTime() - mDateOfLastTap.getTime();
			if (0 < timeSpan && timeSpan < this.mMillisecondsBetweenClicks) {
				this.onLongPress(e);
				this.mDateOfLastTap = null;
				return true;
			}
		}
		this.mDateOfLastTap = new Date();
		return false;
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		this.mOffset -= distanceX;

		if (this.mCurrentBookIndex == this.mBooks.size() - 1) {
			if (this.mOffset < 0) {
				this.mOffset = 0;
			}
		}
		if (this.mCurrentBookIndex == 0) {
			if (this.mOffset > 0) {
				this.mOffset = 0;
			}
		}

		this.invalidate();
		return false;
	}

	public void onLongPress(MotionEvent e) {
		float x = e.getX();
		float y = e.getY();

		if (this.mCurrentElementTop < y && y < this.mCurrentElementBottom
				&& this.mCurrentElementLeft < x
				&& x < this.mCurrentElementRight) {
			((ReadSelectActivity) this.getContext()).displayPrompt();
		}
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (velocityX > 0) {
			selectPrevElement();
		} else {
			selectNextElement();
		}
		return true;
	}

	public ReadableBook getCurrentBook() {
		return this.mCurrentBook;
	}

}
