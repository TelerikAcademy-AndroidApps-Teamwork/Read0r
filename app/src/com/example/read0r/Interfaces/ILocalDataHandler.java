package com.example.read0r.Interfaces;

import java.util.ArrayList;
import java.util.List;

import com.example.read0r.Models.ReadableBook;

public interface ILocalDataHandler {

	ReadableBook getBookById(int id);
	
	List<ReadableBook> getBooks();

	void addBook(ReadableBook book);
	
	void updateBook(ReadableBook book);
}
