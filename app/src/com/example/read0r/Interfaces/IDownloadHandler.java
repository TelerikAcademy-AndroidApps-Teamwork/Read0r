package com.example.read0r.Interfaces;

import android.content.Context;

import com.example.read0r.Activities.DownloadActivity;
import com.example.read0r.EverliveModels.DownloadableBook;
import com.example.read0r.SQLiteModels.ReadableBook;

public interface IDownloadHandler {
	ReadableBook downloadBook(DownloadActivity context, DownloadableBook bookToDownload);
}
