package com.example.read0r.Views;

import java.util.ArrayList;
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

	private List<DownloadableBook> books;
	private List<DownloadableBook> curentPage;
	private int pageNumber;
	private int pageSize = 3;
	private boolean pageChanged = true;

	private int width;
	private int height;
	private Paint ownedBookPaint;
	private Paint newBookPaint;
	private Paint textPaint;
	private int viewPortion;
	private Path titlePath;
	private int countOfLines;
	private boolean initialized = false;
	private int selectedIndex;
	private int itemHeight;
	private Paint selectedBookPaint;
	private int minItemHeight;
	private GestureDetector guestureDetector;
	private ScaleGestureDetector scaleDetector;

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
		this.getBooks().add(new DownloadableBook("", "", "", "", 1, ""));

		this.ownedBookPaint = new Paint();
		this.ownedBookPaint.setColor(Color.LTGRAY);
		this.newBookPaint = new Paint();
		this.newBookPaint.setColor(Color.GREEN);
		this.textPaint = new Paint();
		this.textPaint.setColor(Color.BLACK);
		this.selectedBookPaint = new Paint();
		this.selectedBookPaint.setColor(Color.BLACK);

		this.countOfLines = 10;
		this.selectedIndex = 0;

		this.guestureDetector = new GestureDetector(this.getContext(), this);
		this.scaleDetector = new ScaleGestureDetector(this.getContext(), this);

	}

	@Override
	public void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		this.width = Math.abs(left - right);
		this.height = Math.abs(top - bottom);

		super.onLayout(changed, left, top, right, bottom);

		this.viewPortion = this.width / 10;
		this.itemHeight = Math.max(this.height / 3, this.width / 10 * 2);

		if (changed || this.initialized) {
			this.initialized = true;
			this.titlePath = new Path();
			this.titlePath.moveTo(viewPortion, viewPortion);
			this.titlePath.lineTo(this.width / 2, (float) (viewPortion * 1.3));
			this.titlePath.lineTo(this.width, viewPortion);
		}
	}

	@Override
	public void onDraw(Canvas canvas) {
	
		if (this.pageChanged) {
			setCurrentPage();
		}
		
		super.onDraw(canvas);
		for (int i = 0; i < this.curentPage.size(); i++) {

			DownloadableBook book = curentPage.get(i);
			int currentTop = i * this.itemHeight;

			if (book.isOwned) {
				canvas.drawRect(0, currentTop + 1, this.width, currentTop
						+ this.itemHeight - 1, this.ownedBookPaint);
			} else {
				canvas.drawRect(0, currentTop + 1, this.width, currentTop
						+ this.itemHeight - 1, this.newBookPaint);
			}

			if (this.selectedIndex == i) {
				for (int ln = 1; ln <= countOfLines; ln++) {
					int currentLen = viewPortion / countOfLines * ln;

					if (book.isOwned) {
						canvas.drawLine(0, currentTop + currentLen, currentLen,
								currentTop, this.selectedBookPaint);
					} else {
						canvas.drawLine(0, currentTop + currentLen, currentLen,
								currentTop, this.selectedBookPaint);
					}
				}
			}
			int fontSize = determineMaxTextSize(book, this.width
					- this.viewPortion * 2);
			this.textPaint.setTextSize(fontSize);

			canvas.drawTextOnPath(book.title, this.titlePath, 0, currentTop,
					this.textPaint);
			canvas.drawText(getAuthorPagesAndCategory(book), this.viewPortion,
					currentTop + this.itemHeight - (fontSize / 2),
					this.textPaint);
		}
	}

	private void setCurrentPage() {
		this.pageChanged = false;
		this.curentPage = new ArrayList<DownloadableBook>();

		int i = this.pageNumber * pageSize;
		int len = Math.min(i + pageSize, this.books.size());
		while (i < len) {
			this.curentPage.add(this.books.get(i));
			i++;
		}
		
		DownloadActivity da = (DownloadActivity)this.getContext();
		if (da != null) {
			da.updatePageCounter("(" + (this.pageNumber * pageSize + 1) + "-" + len + ") of " + this.books.size());
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
		return this.books;
	}

	public void setBooks(List<DownloadableBook> books) {
		this.books = books;
		this.pageNumber = 0;
	}

	public void selectAt(float f) {
		this.selectedIndex = ((int) f) / this.itemHeight;
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
		if (this.pageNumber > 0) {
			this.pageNumber--;
			this.pageChanged = true;
			this.invalidate();
		}
	}

	private void goPageDown() {
		if ((this.pageNumber + 1) * pageSize < this.books.size()) {
			this.pageNumber++;
			this.pageChanged = true;
			this.invalidate();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean simpleGuestureHandled = this.guestureDetector
				.onTouchEvent(event);
		boolean scaleGuestureHandled = this.scaleDetector.onTouchEvent(event);
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
		return false;
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	public void onLongPress(MotionEvent e) {
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
