package com.example.read0r;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;

public class Settings {

	private static SharedPreferences mPrefs;
	private static Editor mEditor;
	private static Context context;

	private static void init(Context cntxt) {
		if (context == null || context != cntxt) {
			context = cntxt;
			mPrefs = context.getSharedPreferences("Read0r Settings",
					context.MODE_PRIVATE);
			mEditor = mPrefs.edit();

//			if (mPrefs.contains("theme")) {
//				setTheme(cntxt, Color.BLACK);
//			}
//			if (mPrefs.contains("fontSize")) {
//				setFontSize(cntxt, 23);
//			}
//			if (mPrefs.contains("readingSpeed")) {
//				setReadingSpeed(cntxt, 99);
//			}
		}
	}

	public static int getTheme(Context cntxt) {
		init(cntxt);
		return mPrefs.getInt("theme", -1); // -1 is white theme
	}
	public static int getFontSize(Context cntxt) {
		init(cntxt);
		return mPrefs.getInt("fontSize", 24);
	}
	public static int getReadingSpeed(Context cntxt) {
		init(cntxt);
		return mPrefs.getInt("readingSpeed", 100);
	}
	
	public static boolean setTheme(Context cntxt, int value) {
		init(cntxt);
		mEditor.putInt("theme", value);
		return mEditor.commit();
	}
	public static boolean setFontSize(Context cntxt, int value) {
		init(cntxt);
		mEditor.putInt("fontSize", value);
		return mEditor.commit();
	}
	public static boolean setReadingSpeed(Context cntxt, int value) {
		init(cntxt);
		mEditor.putInt("readingSpeed", value);
		return mEditor.commit();
	}
}
