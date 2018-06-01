package com_client;

import java.io.File;
import java.util.ArrayList;

public class FileUtil {

	public ArrayList<String> getFiles(final File folder) {
		ArrayList<String> fileList = new ArrayList<String>();
		for (final File fileEntry : folder.listFiles()) {

			fileList.add(fileEntry.getPath());


		}
		return fileList;
	}

	public boolean deleteFile(String fileName) {
		File file = new File(fileName);
		if (file.delete()) {
			return true;
		} else {
			return false;
		}
	}

}
