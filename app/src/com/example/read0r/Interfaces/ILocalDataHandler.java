package com.example.read0r.Interfaces;

import java.util.ArrayList;

import com.example.read0r.Models.ReadableBook;

public interface ILocalDataHandler {

	ReadableBook getBookById(int id);
	
	ArrayList<ReadableBook> getBooks();

	void addBook(ReadableBook book);
}
