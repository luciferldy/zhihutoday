package com.example.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MainDBHelper extends SQLiteOpenHelper{
	
	public static String DATABASE_NAME = "zhihupocket.db";
	public static String TABLE_STORIES = "stories_cache";
	public static String TABLE_TOPSTORIES = "topstories_cache";

	public MainDBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("create table if not exists topstories_cache("
				+ "date varchar,"
                + "image varchar,"  
                + "id varchar,"
                + "type varchar,"
                + "ga_prefix varchar,"
                + "title varchar)");
		db.execSQL("create table if not exists stories_cache("  
                  + "date varchar,"  
                  + "images varchar,"
                  + "id varchar,"
                  + "ga_prefix varchar,"
                  + "type varchar,"
                  + "title varchar)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Log.v("MainDBHelper", "MainDBHelper onUpgrade!");
	}

}
