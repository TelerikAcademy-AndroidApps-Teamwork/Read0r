package com.example.read0r.SQLiteModels;

import android.graphics.Color;

public class Read0rSettings {
	public Read0rSettings() {
		this.speedPercent = 100;
		this.fontSize = 24;
		this.theme = Color.WHITE;
	}
	public Read0rSettings(int speedPercent, int fontSize, int theme) {
		this.speedPercent = speedPercent;
		this.fontSize = fontSize;
		this.theme = theme;
	}
	
	int speedPercent;
	int fontSize;
	int theme;
}
