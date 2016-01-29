package com.freehui.wechat2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class WechatLocalSQLite extends SQLiteOpenHelper {
	
	static SQLiteDatabase db;
	static WechatLocalSQLite instance;
	static public String dbname = "wechat2.db";
	
	private WechatLocalSQLite(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	static public SQLiteDatabase getDB(){
		
		if (instance == null)
			instance = new WechatLocalSQLite(WechatApplication.getContext(),dbname,null,1);
		
		return instance.getReadableDatabase();
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
		this.db = db;
		
		Log.w("TAG","WechatLOcalSQLite.onCreate");
		
		db.execSQL(
		"create table wechat_user(name varchar(20),phonenumber varchar(20),password varchar(20))");
		db.execSQL(
		"create table wechat_temp_message(id integer primary key autoincrement,dstphone varchar(20),srcphone varchar(20),srcname varchar(20),dstname varchar(20),value varchar(500),recv verchar(8),read verchar(8),time timestamp not null default (datetime('now','localtime')))");
		db.execSQL(
		"create table wechat_addfriend_message(srcphone varchar(20),srcname varchar(20),dstphone varchar(20),value varchar(60))");
		db.execSQL(
		"create table wechat_friend(aphone varchar(20),aname varchar(20),bname varchar(20),bphone varchar(20))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
		Log.w("TAG","WechatLocalSQLite.onUpgrade.");	
	}
	
	static public boolean deleteDatabase(Context context,String name) {

		return context.deleteDatabase(dbname);
	}
}
