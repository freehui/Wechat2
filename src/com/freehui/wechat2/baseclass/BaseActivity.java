package com.freehui.wechat2.baseclass;
 
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.wechat2.R;
import com.freehui.wechat2.WechatSocket;
import com.freehui.wechat2.util.ByteUtils;
import com.freehui.wechat2.util.WechatActivityManager;

public class BaseActivity extends FragmentActivity{
	
	boolean lock = false;
	
	/*
	 * 锁定 Activity。被锁定的 Activity 不会被管理器关闭。
	 */
	public void lock(){
		
		lock = true;
	}
	
	/*
	 * 解锁 Activity。
	 */	
	public void unlock(){
		
		lock = false;
	}
	
	/*
	 * 得到锁值
	 */
	public boolean readlock(){
		
		return lock;
	}
	
	/*
	 * 打开一个新的 Activity
	 * */
	public void bootActivity(Class<?> dstactivity){
		
		startActivity(new Intent(this,dstactivity));
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}
	
	/*
	 * 打开一个新的 Activity
	 * */
	public void bootActivity(Intent intent){
		
		startActivity(intent);
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}	
	
	/*
	 * 隐式启动 Activity。
	 * */
	public void bootActivity(String dstactivity){
		
		startActivity(new Intent(dstactivity));
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}	


	public void finishAll(){
		
		WechatActivityManager.finishAll();
	}
	
	public String getPhone(){
		
		return WechatSocket.getInstance().getPhone();
	}
	
	/*
	 * 压缩并缩放图片
	 */
	public ByteArrayOutputStream compressBitmap(Bitmap src){

		src = Bitmap.createScaledBitmap(src, 100, 100, false);
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		src.compress(Bitmap.CompressFormat.JPEG, 20, buf);
		return buf;
	}
	
	/*
	 * 从服务器得到一个头像
	 */
	public Bitmap getImageFromServer(String phone){
		
		return WechatSocket.getInstance().getImage(this, phone);
	}
		
	/*
	 * 从本地得到一个用户的头像，如果本地不存在，则返回默认的头像
	 */
	public Bitmap getImageFromLocal(String phone){
		
		Bitmap ret = null;
		FileInputStream localImage = null;
		
		try {						   
			localImage = new FileInputStream(getFilesDir() + "/" +phone+".jpg");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Log.w("TAG","Can't open local file");
			return ((BitmapDrawable)getResources().getDrawable(R.drawable.default_useravatar)).getBitmap();
		} 
		byte[] buf = null;
		try {
			buf = new byte[localImage.available()];
			localImage.read(buf);
		} catch (IOException e) {
			Log.w("TAG","new byte[localImage.available()]");
			return ((BitmapDrawable)getResources().getDrawable(R.drawable.default_useravatar)).getBitmap();
		}

		ret = BitmapFactory.decodeByteArray(buf, 0, buf.length);
		
		return ret;
	}
	
	PopupWindow pupopwait;
	ViewGroup view;
	/*
	 * 弹出一个等待框
	 */
	public void pupopwaiting(String text){

		if (pupopwait == null){
			
			view = (ViewGroup) getLayoutInflater().inflate(R.layout.waitingwindow, null);
			view.setFocusable(true);
			view.setFocusableInTouchMode(true);	
			ImageView icon_wait = (ImageView)view.findViewById(R.id.icon_wait);
			RotateAnimation anim = (RotateAnimation)AnimationUtils.loadAnimation(this,R.anim.waiting_rotate);
			icon_wait.startAnimation(anim);			
			pupopwait = new PopupWindow(view,LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			pupopwait.setFocusable(true);
			pupopwait.setOutsideTouchable(true);
			pupopwait.setBackgroundDrawable(new BitmapDrawable());			
		}else if (pupopwait.isShowing()){

			return;
		}

		((TextView)view.findViewById(R.id.waitingtext)).setText(text);
		pupopwait.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
	}

	public void closewaitwindow(){
		
		pupopwait.dismiss();
	}
    
	/*
	 * 如果一样在后台，则返回 true
	 */
	public boolean isBackground() {
		
	    ActivityManager activityManager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
	    List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
	    for (RunningAppProcessInfo appProcess : appProcesses) {
	         if (appProcess.processName.equals(getPackageName())) {
	                if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
	                	Log.i("后台", appProcess.processName);
	                	return true;
	                }else{
	                	Log.i("前台", appProcess.processName);
	                	return false;
	                }
	           }
	    }
	    return false;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		//强制竖屏
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		WechatActivityManager.addActivity(this);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		WechatActivityManager.delActivity(this);
	}
}
