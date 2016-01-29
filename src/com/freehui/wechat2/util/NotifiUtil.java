package com.freehui.wechat2.util;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;

import com.example.wechat2.R;
import com.freehui.wechat2.WechatApplication;
import com.freehui.wechat2.WechatSocket;

public class NotifiUtil {

	/*
	 * 如果一样在后台，则返回 true
	 */
	static public boolean isBackground() {

		Context ctx = WechatApplication.getContext();
		ActivityManager activityManager = (ActivityManager) ctx
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses();
		for (RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(ctx.getPackageName())) {
				if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
					Log.i("后台", appProcess.processName);
					return true;
				} else {
					Log.i("前台", appProcess.processName);
					return false;
				}
			}
		}
		return false;
	}

	/*
	 * 弹出一个通知
	 */
	static private int notify_id = 0;
    static public void notify(int id,String title, String value,Bitmap image,Intent intent) 
    {  
    	
		
/*		if (isBackground() == false)
			//应用程序在前台，那就不弹了
			return;*/
    	
    	Context ctx = WechatApplication.getContext();
        
    	//弹出提示时，要做的动作，比如响铃，震动
    	int defaultType = 0;  
        defaultType |= Notification.DEFAULT_LIGHTS;  
        defaultType |= Notification.DEFAULT_SOUND;  
        defaultType |= Notification.FLAG_AUTO_CANCEL;
        //defaultType |= Notification.DEFAULT_VIBRATE;  
        
        //创建一个提示构建对象
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx); 
        //设置动作
        builder.setDefaults(defaultType);  
        //设置显示在右下角的文字
        builder.setContentInfo("");
        //设置显示在右下角文字的右侧的小图标
        builder.setSmallIcon(R.drawable.ar9);          
        builder.setNumber(4);  
        //提示文本
        builder.setContentText(value);  
        //提示标题
        builder.setContentTitle(title); 
        //提示图标
        builder.setLargeIcon(image);  
       
        //设置刚弹出时，在屏幕上滚动的内容
        builder.setTicker(title+":"+value); 
        
        //如果需要在弹出的提示里显示确认和拒绝按钮，则设置他们
/*        builder.addAction(android.R.drawable.ic_delete, "Delete1", null);  
        builder.addAction(android.R.drawable.ic_delete, "Delete2", null);  
        */
        //设置风格
/*        builder.setStyle(new NotificationCompat.BigTextStyle()  
                .setBigContentTitle("BigTextTitle")  
                .setSummaryText("SummaryText")  
                .bigText("BigTextBigText"));  */
        
        //设置提示被点击后执行的intent
        PendingIntent pi= PendingIntent.getActivity(ctx, 0, intent, 0); 
        builder.setContentIntent(pi);
        
        //用构造器构造提示
        Notification notification = builder.build();  
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        
        //得到提示管理器
        NotificationManager manager = 
        		(NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        //弹出提示
        manager.notify(id, notification);  
    } 
}
