package com.freehui.wechat2.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import com.example.wechat2.R;
import com.freehui.wechat2.WechatApplication;


public class FileUtils {
	
	static public Bitmap getImageFromLocal(String phone){
		
		Context ctx = WechatApplication.getContext();
		Bitmap ret = null;
		FileInputStream localImage = null;
		
		try {						   
			localImage = new FileInputStream(ctx.getFilesDir() + "/" +phone+".jpg");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Log.w("TAG","Can't open local file");
			return ((BitmapDrawable)ctx.getResources().getDrawable(R.drawable.default_useravatar)).getBitmap();
		} 
		byte[] buf = null;
		try {
			buf = new byte[localImage.available()];
			localImage.read(buf);
		} catch (IOException e) {
			Log.w("TAG","new byte[localImage.available()]");
			return ((BitmapDrawable)ctx.getResources().getDrawable(R.drawable.default_useravatar)).getBitmap();
		}

		ret = BitmapFactory.decodeByteArray(buf, 0, buf.length);
		
		
		if (ret == null)
			return ((BitmapDrawable)ctx.getResources().getDrawable(R.drawable.default_useravatar)).getBitmap();
		else
			return ret;
	}
}
