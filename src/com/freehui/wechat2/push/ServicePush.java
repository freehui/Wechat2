package com.freehui.wechat2.push;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.wechat2.R;
import com.freehui.wechat2.ActivityAcceptRequest;
import com.freehui.wechat2.ActivityChat;
import com.freehui.wechat2.WechatApplication;
import com.freehui.wechat2.WechatLocalSQLite;
import com.freehui.wechat2.WechatSocket;
import com.freehui.wechat2.baseclass.FriendDescriptor;
import com.freehui.wechat2.util.ByteUtils;
import com.freehui.wechat2.util.ChatMessage;
import com.freehui.wechat2.util.FileUtils;
import com.freehui.wechat2.util.NotifiUtil;
import com.freehui.wechat2.util.UserActions;
import com.freehui.wechat2.util.WechatActivityManager;
import com.freehui.wechat2.windows.WechatAlert;
import com.freehui.wechat2.windows.WechatAlert.WechatAlertonConfirm;

public class ServicePush extends Service {
	
	boolean loop;
	Context ctx;
	
	public void onCreate() {
		super.onCreate();
		ctx = WechatApplication.getContext();
		Log.w("TAG","Service-onCreate");
	};
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		
		Log.w("TAG","onStartCommand");
		
		loop = true;	
		pushthread.start();
		return START_STICKY;
	}
	
	
	Thread pushthread = new Thread(){
			
		LocalBroadcastManager localbroad;
		
		void Addfriend_notify(FriendDescriptor friend){
							
			if (friend == null || friend.getphone() == null || friend.getname() == null)
				return;	
			NotifiUtil.notify(
					Integer.parseInt(friend.getphone()),
					friend.getname(), 
					"我通过了你的好友验证请求", 
					FileUtils.getImageFromLocal(friend.getphone()),
					new Intent());						
		}
		
		void Chat_notify(FriendDescriptor friend){
			
			if (friend == null || friend.getphone() == null || friend.getname() == null)
				return;
			
			Intent intent = new Intent(ctx,ActivityChat.class);
			intent.putExtra("phone", friend.getphone());
			intent.putExtra("name", friend.getname());

			NotifiUtil.notify(
					Integer.parseInt(friend.getphone()),
					friend.getname(), 
					new ChatMessage(friend.getprivate()).gettext().toString(), 
					FileUtils.getImageFromLocal(friend.getphone()),
					intent);
		}
		
		void Request_notify(FriendDescriptor friend){
			
			if (friend == null || friend.getphone() == null || friend.getname() == null)
				return;			
			Intent intent = new Intent();

			NotifiUtil.notify(
					Integer.parseInt(friend.getphone()),
					friend.getname(), 
					"请求添加你为好友",
					FileUtils.getImageFromLocal(friend.getphone()), 
					intent);
		}
		
		void store_msg(FriendDescriptor msg){
			
			SQLiteDatabase db = WechatLocalSQLite.getDB();
			WechatSocket sock = WechatSocket.getInstance();
			
			db.execSQL(
"insert into wechat_temp_message(srcname,srcphone,dstphone,dstname,value,recv,read) values(?,?,?,?,?,?,?)",
					new String[] { 
							msg.getname(), 
							msg.getphone(),
							sock.phone,
							sock.username,
							msg.getprivate(),"true","false"});			
		}
		
		public void run() {
			
			InputStream in = null;
			OutputStream out = null;			
			Socket sock = new Socket();
			
			try {
				sock.connect(new InetSocketAddress(WechatApplication.getContext().getString(R.string.server_ip), 9981),1000);
				out = sock.getOutputStream();
				in = sock.getInputStream();	
			} catch (IOException e) {
				// TODO Auto-generated catch block
				
				Log.w("TAG","Can't connect to push server, please try login again.");
				e.printStackTrace();
			}
			
			String v = "[{\"phone\":\""+ WechatSocket.getInstance().phone +"\"}]";
			int len = v.length();
			//发出请求，告知服务器我接受当前用户的推送
			byte[] odata = ByteUtils.byteMerger(
							ByteUtils.number_to_byte(UserActions.USER_ACTIONS_CONNECT_PUSH_SERVER), 
							ByteUtils.number_to_byte(len));
			odata = ByteUtils.byteMerger(odata,v.getBytes());
			
			try {
				out.write(odata);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			byte[] type = new byte[4];
			byte[] length = new byte[4];
			byte[] value;
			
			localbroad = LocalBroadcastManager.getInstance(ctx);
			
			while (loop){
				
				try {
					in.read(type);
					in.read(length);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				value = new byte[ByteUtils.byte_to_number(length,0)];
				try {
					in.read(value);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				int t = ByteUtils.byte_to_number(type,0);
				if (t == UserActions.PUSH_DISCONNECT){
					//断线了！
					WechatActivityManager.finishAll();
					return;
				}
				
				String pushvalue = new String(value);
				JSONObject obj;
				FriendDescriptor friend = new FriendDescriptor();
				try {
					//对于P2P来说，这里还需要一个对端的IP，这里暂时没弄
					obj = new JSONObject(pushvalue);
					friend.setname(obj.getString("name"));
					friend.setphone(obj.getString("phone"));
					friend.setprivate(obj.getString("value"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					continue; 
				}							
				
				Intent intent = new Intent();
				
				/* 根据消息类型决定点击后打开的 Activity */
				switch (t){
				
				case UserActions.PULL_ADDREQUEST://添加好友的请求通知
					Request_notify(friend);
					intent.setAction(getResources().getString(R.string.addfriend_broadcast));
					break;		
				case UserActions.PULL_ADDNOTIFI: //我方的好友请求被对方答应后的通知
					Addfriend_notify(friend);
					intent.setAction(getResources().getString(R.string.addnotify_broadcast));
					break;
				case UserActions.PULL_CHATMESSAGE://得到聊天信息后的通知
					Chat_notify(friend);
					intent.setAction(getResources().getString(R.string.chatmessage_broadcast));
					store_msg(friend);
					break;					
				}			
				
				intent.putExtra("type", t);
				intent.putExtra("data", pushvalue);			
				localbroad.sendBroadcast(intent);
			}
		};
	};
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		//停止线程
		loop = false;
		//关闭和服务器的连接
		WechatSocket.getInstance().disconnected();
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}
