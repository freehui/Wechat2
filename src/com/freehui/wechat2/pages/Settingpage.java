package com.freehui.wechat2.pages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wechat2.R;
import com.freehui.wechat2.ActivityIMessage;
import com.freehui.wechat2.ActivitySetting;
import com.freehui.wechat2.WechatSocket;
import com.freehui.wechat2.baseclass.BaseActivity;
import com.freehui.wechat2.baseclass.BaseFragment;

public class Settingpage extends BaseFragment implements OnClickListener{
	
	View view;
	ViewGroup personal_layout;
	ViewGroup setting_layout;
	
	ImageView photo_icon;
	TextView tv_nickname;
	TextView tv_username;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		view = getActivity().getLayoutInflater().inflate(R.layout.tab_settings, null);
		personal_layout = (ViewGroup)view.findViewById(R.id.personal_layout);
		setting_layout = (ViewGroup)view.findViewById(R.id.setting_layout);
		
		personal_layout.setOnClickListener(this);
		setting_layout.setOnClickListener(this);
		
		photo_icon = (ImageView)view.findViewById(R.id.photo_icon);
		tv_nickname = (TextView)view.findViewById(R.id.tv_nickname);
		tv_username = (TextView)view.findViewById(R.id.tv_username);

	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		photo_icon.setImageBitmap(WechatSocket.getInstance().image);
		tv_nickname.setText(WechatSocket.getInstance().username);
		tv_username.setText("微信号:"+WechatSocket.getInstance().phone);		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		ViewGroup parent = (ViewGroup) view.getParent();
		if (parent != null) {
			parent.removeView(view);
		}
		return view;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
		if (arg0.equals(personal_layout)){
			
			((BaseActivity)getActivity()).bootActivity(ActivityIMessage.class);
		}else if (arg0.equals(setting_layout)){
			
			((BaseActivity)getActivity()).bootActivity(ActivitySetting.class);
		}
	}
}
