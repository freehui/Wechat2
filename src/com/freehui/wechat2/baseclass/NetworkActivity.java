package com.freehui.wechat2.baseclass;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.freehui.wechat2.MainActivity;
import com.freehui.wechat2.WechatSocket;

public class NetworkActivity extends NoTieleActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		if (WechatSocket.islogin == false){
			//需要网络连接的 Activity 在打开时，如果网络未连接，则需要尝试连接
			
			SharedPreferences sp = getSharedPreferences("user",Activity.MODE_PRIVATE);
			String phone = sp.getString("phone", null);
			String password = sp.getString("password", null);
			if (phone != null && password != null){
				//用户登录过，那么尝试登录
				
				WechatSocket sock = WechatSocket.getInstance();
				sock.setPhone(phone);
				sock.setPassword(password);
				if (sock.login(this) == 2){
					//登录成功，直接返回即可。
					
					return;
				}
			}			
			
			//登录失败或之前未登录过，退出
			finishAll();
		}
	}
}
