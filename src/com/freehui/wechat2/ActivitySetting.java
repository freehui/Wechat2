package com.freehui.wechat2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

import com.example.wechat2.R;
import com.freehui.wechat2.baseclass.NoTieleActivity;
import com.freehui.wechat2.util.WechatActivityManager;
import com.freehui.wechat2.windows.WechatAlert;
import com.freehui.wechat2.windows.WechatAlert.WechatAlertonConfirm;

public class ActivitySetting extends NoTieleActivity implements OnClickListener {

	ViewGroup exituser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_setting);
		setBack(findViewById(R.id.register_back));

		exituser = (ViewGroup) findViewById(R.id.exituser);
		exituser.setOnClickListener(this);
	}
	
	PopupWindow pupopwait;
	ViewGroup view;
	View exit;
	View change;

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
		if (arg0.equals(exituser)) {

			if (pupopwait == null){
				
				view = (ViewGroup) getLayoutInflater().inflate(R.layout.exitdialog,
						null);
				view.setOnClickListener(this);
				(exit = view.findViewById(R.id.exit)).setOnClickListener(this);
				(change = view.findViewById(R.id.change)).setOnClickListener(this);
				view.setFocusable(true);
				view.setFocusableInTouchMode(true);
				pupopwait = new PopupWindow(view, LayoutParams.FILL_PARENT,
						LayoutParams.FILL_PARENT);
				pupopwait.setFocusable(true);
				pupopwait.setOutsideTouchable(true);
				pupopwait.setBackgroundDrawable(new BitmapDrawable());
			}
			
			pupopwait.showAtLocation(getWindow().getDecorView(),
					Gravity.CENTER, 0, 0);
			
		}else if (arg0.equals(view)){
				
			pupopwait.dismiss();
			return;
		}else if (arg0.equals(exit)){
			
			new WechatAlert(this)
				.setValue(getString(R.string.exitmessage))
				.setConfirmText("关闭微信")
				.setOnConfirm(new WechatAlertonConfirm() {
					
					@Override
					public void onConfirm() {
						// TODO Auto-generated method stub
						WechatActivityManager.finishAll();
					}
				}).show();
			
			pupopwait.dismiss(); 
			
		}else if (arg0.equals(change)){

			new WechatAlert(this)
			.setValue(getString(R.string.exituser))
			.setOnConfirm(new WechatAlertonConfirm() {
				
				@Override
				public void onConfirm() {
					// TODO Auto-generated method stub
					
					//最后清楚掉登录信息
					SharedPreferences sp = getSharedPreferences("user",Activity.MODE_PRIVATE);
					SharedPreferences.Editor editor = sp.edit();
					editor.putString("phone", null);
					editor.putString("password", null);
					
					if (editor.commit() == true){
						
						WechatSocket.getInstance().disconnected();
					 
						startActivity(new Intent(ActivitySetting.this,MainActivity.class));
						WechatActivityManager.finishAll();						
					}
				}
			})
			.setOnCancel(null).show();			
			
			pupopwait.dismiss();
		}
	}
}
