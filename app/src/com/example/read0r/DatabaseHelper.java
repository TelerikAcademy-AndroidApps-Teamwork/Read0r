package com.example.read0r;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.example.read0r.Models.ReadableBook;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

// https://www.youtube.com/watch?v=beb-n2yq0kM
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
	// name of the database file for your application -- change to something appropriate for your app
	private static final String DATABASE_NAME = "readable_books.db";
	// any time you make changes to your database objects, you may have to increase the database version
	private static final int DATABASE_VERSION = 1;
	private static DatabaseHelper helper;
	
	// the DAO object we use to access the SimpleData table
	private Dao<ReadableBook, Integer> noteDao = null;
	private RuntimeExceptionDao<ReadableBook, Integer> noteRuntimeDao = null;
	private Dao<ReadableBook, Integer> simpleDao;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
	}
	

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			TableUtils.createTable(connectionSource, ReadableBook.class);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
	 * the various data to match the new version number.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			TableUtils.dropTable(connectionSource, ReadableBook.class, true);
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
			throw new RuntimeException(e);
		}
	}

	public Dao<ReadableBook, Integer> getNoteDao() throws SQLException {
		if (noteDao == null) {
			noteDao = getDao(ReadableBook.class);
		}
		return noteDao;
	}
	
	public RuntimeExceptionDao<ReadableBook, Integer> getNoteRuntimeExceprionDao() {
		if (noteRuntimeDao == null) {
			noteRuntimeDao = getRuntimeExceptionDao(ReadableBook.class);
		}
		return noteRuntimeDao;
	}

	public static synchronized DatabaseHelper getHelper(Context context) {
		if (helper == null) {
			helper = new DatabaseHelper(context);
		}
		return helper;
	}

	@Override
	public void close() {
		super.close();
		noteDao = null;
	}


	public Dao<ReadableBook, Integer> createSimpleDataDao() throws SQLException {
		if (simpleDao == null) {
			simpleDao = getDao(ReadableBook.class);
		}
		return simpleDao;
	}
}