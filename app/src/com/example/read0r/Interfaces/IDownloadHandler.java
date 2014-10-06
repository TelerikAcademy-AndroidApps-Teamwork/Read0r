package com.example.read0r.Interfaces;

import com.example.read0r.EverliveModels.DownloadableBook;
import com.example.read0r.SQLiteModels.ReadableBook;

public interface IDownloadHandler {
	ReadableBook downloadBook(DownloadableBook bookToDownload);
}
