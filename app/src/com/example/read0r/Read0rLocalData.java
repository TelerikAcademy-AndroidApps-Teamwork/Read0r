package com.example.read0r;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import com.example.read0r.Interfaces.IDocumentReader;
import com.example.read0r.Interfaces.ILocalDataHandler;
import com.example.read0r.Models.ReadableBook;

public class Read0rLocalData implements ILocalDataHandler {

	//private final String LOG_TAG = getClass().getSimpleName();
	private ConnectionSource connectionSource;
	private Dao<ReadableBook, Integer> readleBooksDoa;

	public Read0rLocalData() {
		
		if (this.connectionSource == null) {
			try {
				this.connectionSource =
						new JdbcConnectionSource(
								"jdbc:h2:/data/data/com.example.read0r/databases/read0r");
				this.readleBooksDoa = DaoManager.createDao(this.connectionSource, ReadableBook.class);
			} catch (SQLException e) {
				throw new RuntimeException("Problems initializing database objects", e);
			}
			try {
				TableUtils.createTable(this.connectionSource, ReadableBook.class);
			} catch (SQLException e) {
				// ignored
			}
		}
	}
	
	public ReadableBook getBookById(int id) {
		
		List<ReadableBook> results;
		try {
			results = readleBooksDoa.queryBuilder().where().eq("id", id).query();
		} catch (SQLException e) {
			e.printStackTrace();
			
			return null;
		}
		return results.get(0);
	}	
	
	public List<ReadableBook> getBooks() {
		
		List<ReadableBook> allBooksList;
		try {
			allBooksList = readleBooksDoa.queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
			
			return null;
		}
				
		return allBooksList;
	}
	
	public void addBook(ReadableBook book) {
		
		try {
			readleBooksDoa.create(book);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}	
}




