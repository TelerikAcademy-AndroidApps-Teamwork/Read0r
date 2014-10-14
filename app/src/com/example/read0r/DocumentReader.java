package com.example.read0r;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.example.read0r.Interfaces.IDocumentReader;
import com.example.read0r.Models.ReadableBook;
import com.telerik.everlive.sdk.core.facades.special.CreateFileFacade;

public class DocumentReader implements IDocumentReader {

	private ReadableBook mDocument;

	private int mPortionSize;
	private int mLastPosition;
	private boolean mEndReached;

	private Context mContext;

	private boolean mSkipSDCardCheck = false;

	public DocumentReader(ReadableBook document, Context context) {
		this.mContext = context;
		this.mDocument = document;
		this.mLastPosition = 0;
		this.mPortionSize = 2000;
		this.mEndReached = false;
	}

	public int getCurrentPosition() {
		return mLastPosition;
	}

	public void setPortionSize(int portionSize) {
		this.mPortionSize = portionSize;
	}

	private int addWordsOfStringToCollection(String textPartition,
			List<String> result) {
		int st = 0;
		for (int end = 0; end < textPartition.length(); end++) {
			if (textPartition.charAt(end) == ' ') {
				result.add(textPartition.substring(st, end));
				st = end + 1;
			}
		}
		return st;
	}

	public List<String> getNextWordPortion(int letterIndex) {
		ArrayList<String> result = new ArrayList<String>();

		if (!this.isSDcardAvailable()) {
			Log.e("DocumentReader No SD card",
					"Error - no SD card is available");
		}

		FileInputStream fis = null;
		String readString = null;
		if (isSDcardAvailable() || this.mSkipSDCardCheck) {
			try {
				fis = mContext.openFileInput(this.mDocument.fileAddress);
				InputStreamReader isr = new InputStreamReader(fis);

				// Skipping to the index...
				isr.skip(letterIndex);

				// The actual reading
				char[] inputBuffer = new char[this.mPortionSize];
				int charsReaded = isr.read(inputBuffer);
				String portion = new String(inputBuffer);

				// if end reached
				if (charsReaded < this.mPortionSize) {
					this.mEndReached = true;
					portion += " ";
				}

				int charsThatCount = addWordsOfStringToCollection(portion,
						result);

				// Saving progress
				this.mLastPosition = letterIndex + charsThatCount;

				fis.close();
				isr.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				fis = null;
			}
		}
		return result;
	}

	private boolean isSDcardAvailable() {
		String state = Environment.getExternalStorageState();
		String expected = Environment.MEDIA_MOUNTED;
		if (state.equals(expected)) {
			return true;
		}
		return false;
	}

	public boolean endReached() {
		return this.mEndReached;
	}

	@Override
	public long getDocLength() {
		if (this.mDocument.length == 0) {
			File file = new File(this.mDocument.fileAddress);
			if (file.exists()) {
				return file.length();
			}
			return 0;
		}
		return this.mDocument.length;
	}
}
