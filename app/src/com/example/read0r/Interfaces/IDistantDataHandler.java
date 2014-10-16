package com.example.read0r.Interfaces;

import java.util.ArrayList;

import com.example.read0r.Models.DownloadableBook;

public interface IDistantDataHandler {
	
	ArrayList<String> getCategories();

	void getBooks();

	void getFilteredBooks(ArrayList<String> categories);
}
