package com.example.read0r.Interfaces;

import com.example.read0r.EverliveModels.DownloadableBook;

public interface IDistantDataHandler {
	
	String[] getCategories();

	DownloadableBook[] getBooks();

	DownloadableBook[] getFilteredBooks(String[] categories);
}
