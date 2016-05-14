package com.example.fileexplorer.database;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class FileDB {

	
	public static final String DB_NAME = "file";
	public static final int VERSION = 1;
	private static FileDB fileDB;
	private SQLiteDatabase db;
	
	private FileDB(Context context){
		FileOpenHelper dbHelper = new FileOpenHelper(context,DB_NAME,null,VERSION);
		db = dbHelper.getWritableDatabase();
	}
	
	
	public synchronized static FileDB getInstance(Context context){
		if(fileDB == null){
			fileDB = new FileDB(context);
		}
		return fileDB;
	}
	
	
	public void saveFile(String path){
		File[] totalFile = new File(path).listFiles();
		if(totalFile == null)
			return ;
		for(File file : totalFile){
			ContentValues values = new ContentValues();
			if(file.isFile()){
				values.put("name", file.getName());  
				values.put("path", file.getAbsolutePath());  
				values.put("type",file.getName().substring(file.getName().lastIndexOf(".") + 1).toLowerCase());
				db.insert("File", null, values);
			}
			if(file.isDirectory()){     //如果是文件夹，递归查找
				saveFile(file.getAbsolutePath());
			}
			
		}
	}
	
}
