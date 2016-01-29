package com.freehui.wechat2.windows;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.example.wechat2.R;


/*
 * 微信提示框
 */
public class WechatAlert implements OnClickListener{
		
	View view;
	PopupWindow window;
	WechatAlertonConfirm onconfirm;
	WechatAlertonCancel oncancel;
	Context context;
	
	/*
	 * 微信提示框的回调
	 */
	public interface WechatAlertonConfirm{
		
		void onConfirm();
	};
	public interface WechatAlertonCancel{
		
		void onCancel();
	};
		
	@SuppressWarnings("deprecation")
	public WechatAlert(Context context) {
		// TODO Auto-generated constructor stub
		
		this.context = context;
		
		view = LayoutInflater.from(context).inflate(R.layout.wechat_alert, null);
		
		view.setOnClickListener(this);
		view.findViewById(R.id.confirm_button).setOnClickListener(this);
		view.findViewById(R.id.cencel_button).setOnClickListener(this);	
		view.findViewById(R.id.window).setOnClickListener(this);	
		
		window = new PopupWindow(view, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		window.setFocusable(true);
		window.setOutsideTouchable(true);
		window.setBackgroundDrawable(new BitmapDrawable());			
	}

	public WechatAlert setTitle(String title){
		
		TextView tv = (TextView)view.findViewById(R.id.title);
		tv.setText(title);
		tv.setVisibility(View.VISIBLE);
		return this;
	}
	
	public WechatAlert setValue(String value){

		TextView tv = (TextView)view.findViewById(R.id.value);
		tv.setText(value);		
		return this;
	}
	
	public WechatAlert setOnDismiss(OnDismissListener lister){
		
		if (window != null)
			window.setOnDismissListener(lister);
		
		return this;
	}
	
	public WechatAlert setConfirmText(String value){

		TextView tv = (TextView)view.findViewById(R.id.confirm_button);
		tv.setText(value);		
		tv.setVisibility(View.VISIBLE);
		return this;
	}	
	
	public WechatAlert setCancelText(String value){

		TextView tv = (TextView)view.findViewById(R.id.cencel_button);
		tv.setText(value);		
		tv.setVisibility(View.VISIBLE);
		return this;
	}		
	
	public WechatAlert setOnCancel(WechatAlertonCancel onCancel){
		
		this.oncancel = onCancel;
		view.findViewById(R.id.cencel_button).setVisibility(View.VISIBLE);;	
		return this;
	}
	
	public WechatAlert setOnConfirm(WechatAlertonConfirm onConfirm){
		
		this.onconfirm = onConfirm;
		view.findViewById(R.id.confirm_button).setVisibility(View.VISIBLE);
		return this;
	}		
	
	public void show(){
		
		window.showAtLocation(((Activity) context).getWindow().getDecorView(),
				Gravity.CENTER, 0, 0);
	}
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
		switch (arg0.getId()){
			
		case R.id.confirm_button:
			
			if (onconfirm != null)
				onconfirm.onConfirm();
			
			break;
		case R.id.cencel_button:
			
			if (oncancel != null)
				oncancel.onCancel();
			
			break;
			
		case R.id.window:
			return;
		}
		
		window.dismiss();
	}
};
