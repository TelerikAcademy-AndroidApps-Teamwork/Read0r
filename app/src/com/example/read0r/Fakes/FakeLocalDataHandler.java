package com.example.read0r.Fakes;

import java.util.ArrayList;

import android.os.Environment;

import com.example.read0r.Interfaces.ILocalDataHandler;
import com.example.read0r.SQLiteModels.ReadableBook;

public class FakeLocalDataHandler implements ILocalDataHandler {

	ArrayList<ReadableBook> books = new ArrayList<ReadableBook>();

	public FakeLocalDataHandler() {
		this.books.add(new ReadableBook(Environment
				.getExternalStorageDirectory().getPath()
				+ "/read0r/About_speed_reading.read0r",
				"Fast Reading Description", "???", 4, "science", 0));
		this.books.add(new ReadableBook(Environment
				.getExternalStorageDirectory().getPath()
				+ "/read0r/hypno.read0r", "Hallo trainer", "???", 1, "fiction",
				0));

	}

	public ReadableBook getBookById(int id) {
		if (id < this.books.size() && id > 0) {
			return this.books.get(id);
		} else {
			return null;
		}
	}

	public ArrayList<ReadableBook> getBooks() {
		return this.books;
	}

	public void addBook(ReadableBook book) {
		this.books.add(book);
	}

}
