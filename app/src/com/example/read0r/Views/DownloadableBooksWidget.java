package com.example.read0r.Views;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.read0r.Activities.DownloadActivity;
import com.example.read0r.Models.DownloadableBook;

import android.R.integer;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View.OnTouchListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Toast;

public class DownloadableBooksWidget extends View implements OnGestureListener,
		OnScaleGestureListener {

	private List<DownloadableBook> mBooks;
	private List<DownloadableBook> mCurentPage;
	private int mPageNumber;
	private int mPageSize = 3;
	private boolean mPageChanged = true;
	private boolean mInitialized = false;

	private int mWidth;
	private int mHeight;

	private int mSelectedIndex;
	private int mItemHeight;
	private int mViewPortion;
	private int mCountOfLines;

	private Path mTitlePath;
	private GestureDetector mGuestureDetector;
	private ScaleGestureDetector mScaleDetector;

	private Paint mOwnedBookPaint;
	private Paint mNewBookPaint;
	private Paint mTextPaint;
	private Paint mSelectedBookPaint;
	private Date mDateOfLastTap;
	private long mMillisecondsBetweenClicks = 500;

	public DownloadableBooksWidget(Context context) {
		this(context, null);
	}

	public DownloadableBooksWidget(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DownloadableBooksWidget(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.setBooks(new ArrayList<DownloadableBook>());
		this.getBooks().add(new DownloadableBook("", "", "", 1, ""));

		this.mOwnedBookPaint = new Paint();
		this.mOwnedBookPaint.setColor(Color.LTGRAY);
		this.mNewBookPaint = new Paint();
		this.mNewBookPaint.setColor(Color.GREEN);
		this.mTextPaint = new Paint();
		this.mTextPaint.setColor(Color.BLACK);
		this.mSelectedBookPaint = new Paint();
		this.mSelectedBookPaint.setColor(Color.BLACK);

		this.mCountOfLines = 10;
		this.mSelectedIndex = 0;

		this.mGuestureDetector = new GestureDetector(this.getContext(), this);
		this.mScaleDetector = new ScaleGestureDetector(this.getContext(), this);
	}

	@Override
	public void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		this.mWidth = Math.abs(left - right);
		this.mHeight = Math.abs(top - bottom);

		super.onLayout(changed, left, top, right, bottom);

		this.mViewPortion = this.mWidth / 10;
		this.mItemHeight = this.mHeight / 3;

		if (changed || this.mInitialized) {
			this.mInitialized = true;
			this.mTitlePath = new Path();
			this.mTitlePath.moveTo(mViewPortion, mViewPortion);
			this.mTitlePath.lineTo(this.mWidth / 2,
					(float) (mViewPortion * 1.3));
			this.mTitlePath.lineTo(this.mWidth, mViewPortion);
		}
	}

	@Override
	public void onDraw(Canvas canvas) {

		if (this.mPageChanged) {
			setCurrentPage();
		}

		super.onDraw(canvas);
		for (int i = 0; i < this.mCurentPage.size(); i++) {

			DownloadableBook book = mCurentPage.get(i);
			int currentTop = i * this.mItemHeight;

			if (book.isOwned) {
				canvas.drawRect(0, currentTop + 1, this.mWidth, currentTop
						+ this.mItemHeight - 1, this.mOwnedBookPaint);
			} else {
				canvas.drawRect(0, currentTop + 1, this.mWidth, currentTop
						+ this.mItemHeight - 1, this.mNewBookPaint);
			}

			if (this.mSelectedIndex == i) {
				for (int ln = 1; ln <= mCountOfLines; ln++) {
					int currentLen = mViewPortion / mCountOfLines * ln;

					if (book.isOwned) {
						canvas.drawLine(0, currentTop + currentLen, currentLen,
								currentTop, this.mSelectedBookPaint);
					} else {
						canvas.drawLine(0, currentTop + currentLen, currentLen,
								currentTop, this.mSelectedBookPaint);
					}
				}
			}
			int fontSize = determineMaxTextSize(book, this.mWidth
					- this.mViewPortion * 2);
			this.mTextPaint.setTextSize(fontSize);

			canvas.drawTextOnPath(book.title, this.mTitlePath, 0, currentTop,
					this.mTextPaint);
			canvas.drawText(getAuthorPagesAndCategory(book), this.mViewPortion,
					currentTop + this.mItemHeight - (fontSize / 2),
					this.mTextPaint);
		}
	}

	private void setCurrentPage() {
		this.mPageChanged = false;
		this.mCurentPage = new ArrayList<DownloadableBook>();

		int i = this.mPageNumber * mPageSize;
		int len = Math.min(i + mPageSize, this.mBooks.size());
		while (i < len) {
			this.mCurentPage.add(this.mBooks.get(i));
			i++;
		}

		DownloadActivity da = (DownloadActivity) this.getContext();
		if (da != null) {
			int itemsOnPage = this.mPageNumber * mPageSize + 1;
			if (len == 0) {
				itemsOnPage = 0;
			}

			da.updatePageCounter("(" + itemsOnPage + "-" + len + ") of "
					+ this.mBooks.size());
		}
	}

	private String getAuthorPagesAndCategory(DownloadableBook book) {
		return book.author + "; " + book.pages + " pages; " + book.category;
	}

	private int determineMaxTextSize(DownloadableBook book, float maxWidth) {
		String str = book.title;
		if (str.length() < getAuthorPagesAndCategory(book).length()) {
			str = getAuthorPagesAndCategory(book);
		}

		int size = 12;
		Paint paint = new Paint();

		do {
			paint.setTextSize(++size);
		} while (paint.measureText(str) < maxWidth && size < 50);

		return Math.min(size, 50);
	}

	public List<DownloadableBook> getBooks() {
		return this.mBooks;
	}

	public void setBooks(List<DownloadableBook> books) {
		this.mBooks = books;
		this.mPageNumber = 0;
	}

	public void selectAt(float f) {
		this.mSelectedIndex = ((int) f) / this.mItemHeight;
		this.invalidate();
	}

	public boolean changePageByFling(float velocityY) {
		if (velocityY > 0) {
			goPageUp();
		} else {
			goPageDown();
		}
		return true;
	}

	private void goPageUp() {
		if (this.mPageNumber > 0) {
			this.mPageNumber--;
			this.mPageChanged = true;
			this.invalidate();
		}
	}

	private void goPageDown() {
		if ((this.mPageNumber + 1) * mPageSize < this.mBooks.size()) {
			this.mPageNumber++;
			this.mPageChanged = true;
			this.invalidate();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean simpleGuestureHandled = this.mGuestureDetector
				.onTouchEvent(event);
		boolean scaleGuestureHandled = this.mScaleDetector.onTouchEvent(event);
		return simpleGuestureHandled || scaleGuestureHandled;
	}

	public boolean onSingleTapConfirmed(MotionEvent e) {
		return false;
	}

	public boolean onDoubleTap(MotionEvent e) {
		return false;
	}

	public boolean onDoubleTapEvent(MotionEvent e) {
		return false;
	}

	public boolean onDown(MotionEvent e) {
		this.selectAt(e.getY());
		return false;
	}

	public void onShowPress(MotionEvent e) {
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
		return false;
	}

	public void onLongPress(MotionEvent e) {
		DownloadableBook selectedBook;
		if (e.getY() < this.mItemHeight) {
			selectedBook = this.mCurentPage.get(0);
		} else if (e.getY() < this.mItemHeight * 2) {
			selectedBook = this.mCurentPage.get(1);
		} else {
			selectedBook = this.mCurentPage.get(2);
		}

		if (selectedBook != null) {
			((DownloadActivity) this.getContext())
					.onBookSelection(selectedBook);
		}
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return this.changePageByFling(velocityY);
	}

	public boolean onScale(ScaleGestureDetector detector) {
		return false;
	}

	public boolean onScaleBegin(ScaleGestureDetector detector) {
		return false;
	}

	public void onScaleEnd(ScaleGestureDetector detector) {
	}
}
