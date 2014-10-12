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

	private ReadableBook document;

	private int portionSize;
	private int lastPosition;
	private boolean endReached;

	private Context context;

	private boolean skipSDCardCheck = false;

	public DocumentReader(ReadableBook document, Context context) {
		this.context = context;
		this.document = document;
		this.lastPosition = 0;
		this.portionSize = 2000;
		this.endReached = false;
	}

	public int getCurrentPosition() {
		return lastPosition;
	}

	public void setPortionSize(int portionSize) {
		this.portionSize = portionSize;
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
		if (isSDcardAvailable() || this.skipSDCardCheck) {
			try {
				fis = context.openFileInput(this.document.fileAddress);
				InputStreamReader isr = new InputStreamReader(fis);

				// Skipping to the index...
				isr.skip(letterIndex);

				// The actual reading
				char[] inputBuffer = new char[this.portionSize];
				int charsReaded = isr.read(inputBuffer);
				String portion = new String(inputBuffer);

				// if end reached
				if (charsReaded < this.portionSize) {
					this.endReached = true;
					portion += " ";
				}

				int charsThatCount = addWordsOfStringToCollection(portion,
						result);

				// Saving progress
				this.lastPosition = letterIndex + charsThatCount;

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
		return this.endReached;
	}

	@Override
	public long getDocLength() {
		if (this.document.length == 0) {
			File file = new File(this.document.fileAddress);
			if (file.exists()) {
				return file.length();
			}
			return 0;
		}
		return this.document.length;
	}
}
