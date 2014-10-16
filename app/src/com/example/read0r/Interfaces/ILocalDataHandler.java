package com.example.read0r.Interfaces;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.read0r.Models.ReadableBook;

public interface ILocalDataHandler {

	ReadableBook getBookById(int id) throws SQLException;
	
	List<ReadableBook> getBooks() throws SQLException;

	void addBook(ReadableBook book) throws SQLException;
	
	void updateBook(ReadableBook book) throws SQLException;
}
