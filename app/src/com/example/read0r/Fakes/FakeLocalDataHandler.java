package com.example.read0r.Fakes;

import java.util.ArrayList;
import java.util.List;

import android.os.Environment;

import com.example.read0r.Interfaces.ILocalDataHandler;
import com.example.read0r.Models.ReadableBook;

public class FakeLocalDataHandler implements ILocalDataHandler {

	List<ReadableBook> mBooks = new ArrayList<ReadableBook>();

	public FakeLocalDataHandler() {
		this.mBooks.add(new ReadableBook(Environment
				.getExternalStorageDirectory().getPath()
				+ "/read0r/About_speed_reading.read0r",
				"Fast Reading Description", "???", 4, "science", 0));
		this.mBooks.add(new ReadableBook(Environment
				.getExternalStorageDirectory().getPath()
				+ "/read0r/hypno.read0r", "Hello trainer", "???", 1, "fiction",
				0));
		this.mBooks.add(new ReadableBook(Environment
				.getExternalStorageDirectory().getPath()
				+ "/read0r/About_speed_reading.read0r",
				"Fast Reading Description", "???", 4, "science", 0));
		this.mBooks.add(new ReadableBook(Environment
				.getExternalStorageDirectory().getPath()
				+ "/read0r/hypno.read0r", "Hello trainer", "???", 1, "fiction",
				0));
		this.mBooks.add(new ReadableBook(Environment
				.getExternalStorageDirectory().getPath()
				+ "/read0r/About_speed_reading.read0r",
				"Fast Reading Description", "???", 4, "science", 0));
		this.mBooks.add(new ReadableBook(Environment
				.getExternalStorageDirectory().getPath()
				+ "/read0r/hypno.read0r", "Hello trainer", "???", 1, "fiction",
				0));

		for (int i = 0; i < this.mBooks.size(); i++) {
			this.mBooks.get(i).id = i;
		}
	}

	public ReadableBook getBookById(int id) {
		if (id < this.mBooks.size() && id > 0) {
			return this.mBooks.get(id);
		} else {
			return null;
		}
	}

	public List<ReadableBook> getBooks() {
		return this.mBooks;
	}

	public void addBook(ReadableBook book) {
		this.mBooks.add(book);
	}

}
