package com.example.read0r;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import com.j256.ormlite.dao.Dao;

import com.example.read0r.DatabaseHelper;
import com.example.read0r.Interfaces.ILocalDataHandler;
import com.example.read0r.Models.ReadableBook;

public class Read0rLocalData implements ILocalDataHandler {

	private Context context;
	private DatabaseHelper databaseHelper;
	private Dao<ReadableBook, Integer> noteDao;

	public Read0rLocalData(Context context) {
		this.context = context;
		DatabaseHelper helper = getHelper();
		try {
			noteDao = helper.createSimpleDataDao();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void deleteAll() throws SQLException {
		List<ReadableBook> books = this.getBooks();
		for (ReadableBook b : books) {
			noteDao.delete(b);
		}
	}

	private DatabaseHelper getHelper() {
		if (databaseHelper == null) {
			databaseHelper = DatabaseHelper.getHelper(this.context);
		}
		return databaseHelper;
	}

	public ReadableBook getBookById(int id) throws SQLException {
		List<ReadableBook> noteOne = noteDao.queryForEq("id", id);
		return noteOne.get(0);
	}

	public List<ReadableBook> getBooks() throws SQLException {
		List<ReadableBook> books = noteDao.queryForAll();
		return books;
	}

	public void addBook(ReadableBook book) throws SQLException {
		List<ReadableBook> books = noteDao.queryForEq("title", book.title);
		if (books == null || books.size() == 0) {
			noteDao.create(book);
		}
	}

	@Override
	public void updateBook(ReadableBook book) throws SQLException {
		noteDao.update(book);
	}
}