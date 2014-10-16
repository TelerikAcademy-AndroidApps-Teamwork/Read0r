package com.example.read0r.Interfaces;

import android.content.Context;

import com.example.read0r.Activities.DownloadActivity;
import com.example.read0r.Models.DownloadableBook;
import com.example.read0r.Models.ReadableBook;
import com.telerik.everlive.sdk.core.EverliveApp;

public interface IDownloadHandler {
	ReadableBook downloadBook(DownloadActivity context, DownloadableBook bookToDownload);
}
