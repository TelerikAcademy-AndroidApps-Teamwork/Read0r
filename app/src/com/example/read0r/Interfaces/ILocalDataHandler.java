package com.example.read0r.Interfaces;

import com.example.read0r.SQLiteModels.ReadableBook;

public interface ILocalDataHandler {

	ReadableBook getBookById(int id);
	
	ReadableBook[] getBooks();

	void addBook(ReadableBook book);
}
