package com.example.read0r.Views;

import java.util.ArrayList;
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
	private ILocalDataHandler localDataHandler;
	private List<ReadableBook> books;
	private ReadableBook currentBook;
	private int currentBookIndex;

	private GestureDetector guestureDetector;
	private ScaleGestureDetector scaleDetector;

	private Paint currentBackgroundPaint;
	private Paint currentFontPaint;
	private Paint notCurrentPaint;
	private boolean initialized;

	private int width;
	private int height;
	private int cellWidth;
	private int cellHeight;

	private int currentElementLeft;
	private int currentElementTop;
	private int currentElementRight;
	private int currentElementBottom;

	private int nextElementLeft;
	private int nextElementTop;
	private int nextElementBottom;
	private int nextElementRight;

	private int prevElementLeft;
	private int prevElementTop;
	private int prevElementRight;
	private int prevElementBottom;

	private int offset;
	private int centerLineVertical;

	public ReadableBooksWidget(Context context) {
		this(context, null, 0);
	}

	public ReadableBooksWidget(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ReadableBooksWidget(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		boolean localDataIsFake = this.getResources().getBoolean(R.bool.useFakeLocalData);

		if (localDataIsFake) {
			this.localDataHandler = new Read0rLocalData();
		} else {
			this.localDataHandler = new FakeLocalDataHandler();
		}
		
		this.books = this.localDataHandler.getBooks();
		this.currentBook = this.books.size() > 0 ? this.books.get(0)
				: new ReadableBook("", "", "", 1, "", 0);
		this.currentBookIndex = 0;

		this.guestureDetector = new GestureDetector(this.getContext(), this);
		this.scaleDetector = new ScaleGestureDetector(this.getContext(), this);

		this.currentBackgroundPaint = new Paint();
		this.currentBackgroundPaint.setColor(Color.LTGRAY);

		this.currentFontPaint = new Paint();
		this.currentFontPaint.setColor(Color.BLACK);

		this.notCurrentPaint = new Paint();
		this.notCurrentPaint.setColor(Color.BLACK);
	}

	@Override
	public void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		if (changed || this.initialized) {
			this.initialized = true;

			this.width = Math.abs(left - right);
			this.height = Math.abs(top - bottom);
			this.cellWidth = this.width / 11;
			this.cellHeight = this.width / 10;

			this.currentElementLeft = this.cellWidth * 2;
			this.currentElementTop = this.cellHeight * 2;
			this.currentElementRight = this.cellWidth * 9;
			this.currentElementBottom = this.cellHeight * 8;

			this.nextElementLeft = this.cellWidth * 10;
			this.nextElementTop = this.cellHeight;
			this.nextElementRight = this.cellWidth * 14;
			this.nextElementBottom = this.cellHeight * 7;

			this.prevElementLeft = this.cellWidth * -3;
			this.prevElementTop = this.cellHeight;
			this.prevElementRight = this.cellWidth;
			this.prevElementBottom = this.cellHeight * 7;
		}
	}

	@Override
	public void onDraw(Canvas canvas) {

		if (this.offset != 0) {
			canvas.translate(this.offset, 0);

			int drawLen = this.width /3;
			if (this.offset > drawLen) {
				selectPrevElement();
			} else if (-this.offset > drawLen) {
				selectNextElement();
			}
		}

		if (initialized) {

		}
		if (this.currentBookIndex > 0) {
			canvas.drawRect(prevElementLeft, prevElementTop, prevElementRight,
					prevElementBottom, notCurrentPaint);
		}

		canvas.drawRect(currentElementLeft, currentElementTop,
				currentElementRight, currentElementBottom,
				currentBackgroundPaint);

		if (this.currentBookIndex < this.books.size() - 1) {
			canvas.drawRect(nextElementLeft, nextElementTop, nextElementRight,
					nextElementBottom, notCurrentPaint);
		}

		this.currentFontPaint.setTextSize(this.determineMaxTextSize(
				this.currentBook, this.cellWidth * 5));

		this.drawTextStill(canvas, (this.currentBookIndex + 1) + " / "
				+ this.books.size(), this.cellHeight);

		this.drawText(canvas, this.currentBook.title, this.cellHeight * 3);
		this.drawText(canvas, this.currentBook.author, this.cellHeight * 4);
		this.drawText(canvas, getCategory(this.currentBook),
				this.cellHeight * 5);
		this.drawText(canvas, getProgress(this.currentBook),
				this.cellHeight * 6);

		this.currentFontPaint.setTextSize(this.determineMaxTextSize(
				this.currentBook, this.cellWidth * 5) / 2);

		this.drawText(canvas, "(long press to read)", this.cellHeight * 7);

		super.onDraw(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean simpleGuestureHandled = this.guestureDetector
				.onTouchEvent(event);
		boolean scaleGuestureHandled = this.scaleDetector.onTouchEvent(event);
		return simpleGuestureHandled || scaleGuestureHandled;
	}

	private void drawText(Canvas canvas, String str, int y) {
		float x = (this.width / 2)
				- (this.currentFontPaint.measureText(str) / 2);
		canvas.drawText(str, x, y, this.currentFontPaint);
	}

	private void drawTextStill(Canvas canvas, String str, int y) {
		float x = (this.width / 2)
				- (this.currentFontPaint.measureText(str) / 2) - this.offset;
		canvas.drawText(str, x, y, this.currentFontPaint);
	}

	private void selectNextElement() {
		if (this.currentBookIndex < this.books.size() - 1) {
			this.currentBookIndex++;
			this.currentBook = this.books.get(this.currentBookIndex);
			this.offset = 0;
			this.invalidate();
		}
	}

	private void selectPrevElement() {
		if (this.currentBookIndex > 0) {
			this.currentBookIndex--;
			this.currentBook = this.books.get(this.currentBookIndex);
			this.offset = 0;
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
		// TODO Auto-generated method stub
		return false;
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		this.offset -= distanceX;

		if (this.currentBookIndex == this.books.size() - 1) {
			if (this.offset < 0) {
				this.offset = 0;
			}
		}
		if (this.currentBookIndex == 0) {
			if (this.offset > 0) {
				this.offset = 0;
			}
		}

		this.invalidate();
		return false;
	}

	public void onLongPress(MotionEvent e) {
		float x = e.getX();
		float y = e.getY();

		if (this.currentElementTop < y && y < this.currentElementBottom
				&& this.currentElementLeft < x && x < this.currentElementRight) {
			((ReadSelectActivity) this.getContext()).goToRead();
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
		return this.currentBook;
	}

}
