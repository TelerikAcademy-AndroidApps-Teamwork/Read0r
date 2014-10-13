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

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;


import com.example.read0r.DatabaseHelper;
import com.example.read0r.Interfaces.IDocumentReader;
import com.example.read0r.Interfaces.ILocalDataHandler;
import com.example.read0r.Models.ReadableBook;

public class Read0rLocalData extends OrmLiteBaseActivity<DatabaseHelper> implements ILocalDataHandler {

	public RuntimeExceptionDao<ReadableBook, Integer> noteDao;

	public Read0rLocalData() {
	
		noteDao = getHelper().getNoteRuntimeExceprionDao();
	}			
	
	public ReadableBook getBookById(int id) {
		
		List<ReadableBook> noteOne = noteDao.queryForEq("id", id);	
		return noteOne.get(0);
	}	
	
	public List<ReadableBook> getBooks() {
		
		List<ReadableBook> books = noteDao.queryForAll();
		return books;
	}
	
	public void addBook(ReadableBook book) {
		noteDao.create(book);
	}	
	
	@Override
	public void updateBook(ReadableBook book) {
		// TODO: Implement me!
	}
}




