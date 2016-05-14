package com.example.fileexplorer.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class FileOpenHelper extends SQLiteOpenHelper{
	private Context context;
	
	public static final String CREATE_FILE = "create table File("
			+"id integer primary key autoincrement,"
			+"name text,"
			+"path text,"
			+"type text)";
	
	public FileOpenHelper(Context context,String name,CursorFactory factory,int version){
		super(context,name,factory,version);
		this.context = context;
	}
	
	public void onCreate(SQLiteDatabase db){
		db.execSQL(CREATE_FILE);
	}
	
	public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
		
	}
	
}
