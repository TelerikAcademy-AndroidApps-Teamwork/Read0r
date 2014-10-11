package com.example.read0r;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;
import android.util.Log;

import com.example.read0r.Interfaces.IDocumentReader;
import com.example.read0r.Models.ReadableBook;

public class DocumentReader implements IDocumentReader {

	private ReadableBook document;

	private int portionSize;
	private int lastPosition;
	private boolean endReached;

	private RandomAccessFile raf;

	public DocumentReader(ReadableBook document) {
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

	public List<String> getNextWordPortion(int letterIndex) {
		ArrayList<String> result = new ArrayList<String>();
		String textPartition;
		
		if (!this.isSDcardAvailable()) {
			//return result;
		}
		
		try {
			if (this.raf == null) {
				File f = new File(document.fileAddress);
				this.raf = new RandomAccessFile(f, "rw");
				this.document.length = (int) (raf.length() /2);
			}
			
			byte[] buffer = new byte[this.portionSize * 2];
			raf.read(buffer, this.lastPosition, buffer.length);

			textPartition = new String(buffer);

			int st = 0;
			for (int end = 0; end < textPartition.length(); end++) {
				if (textPartition.charAt(end) == ' ') {
					result.add(textPartition.substring(st, end));
					st = end + 1;
				}
			}

			this.lastPosition = letterIndex + st;
			if (raf.length() <= this.lastPosition * 2) { // if end reached
				this.endReached = true;
			}

		} catch (IOException e) {
			Log.e("DocumentReader.getNextWordPortion()", "Error", e);
		}

		return result;
	}

	private boolean isSDcardAvailable() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}

	public boolean endReached() {
		return this.endReached;
	}
}
