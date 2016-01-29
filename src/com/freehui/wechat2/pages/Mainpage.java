package com.freehui.wechat2.pages;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html.ImageGetter;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.wechat2.R;
import com.freehui.wechat2.ActivityChat;
import com.freehui.wechat2.WechatApplication;
import com.freehui.wechat2.WechatLocalSQLite;
import com.freehui.wechat2.WechatSocket;
import com.freehui.wechat2.baseclass.BaseActivity;
import com.freehui.wechat2.baseclass.BaseFragment;
import com.freehui.wechat2.baseclass.FriendDescriptor;
import com.freehui.wechat2.util.ChatMessage;
import com.freehui.wechat2.util.DensityUtil;
import com.freehui.wechat2.util.UserActions;

@SuppressLint("ValidFragment")
public class Mainpage extends BaseFragment {

	View view;
	TextView notfoundmessage;
	ListView chatlist;
	ChatlistAdapter chatlistadapter;
	JSONArray unreadmessage;
	ChatListBroadcastReceiver chatlistbroadcast;
	LocalBroadcastManager localbroadcast;
	
	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			
			if (msg.what == UserActions.QUERY_UNREADMESSAGE) {

				if (msg.obj != null){


					try {

						unreadmessage = new JSONArray((String) msg.obj);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					LocalBroadcastManager localbroad = LocalBroadcastManager.getInstance(Mainpage.this.getActivity());
					SQLiteDatabase db = WechatLocalSQLite.getDB();
					WechatSocket sock = WechatSocket.getInstance();
					
					//先把从网络得到的消息保存到数据库
					for (int i = 0;i < unreadmessage.length();i++){
						
						JSONObject obj = null;
						try {
							obj = unreadmessage.getJSONObject(i);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						 
						try {
							db.execSQL(
									"insert into wechat_temp_message(srcname,srcphone,dstphone,dstname,value,recv,read) values(?,?,?,?,?,?,?)",
														new String[] { 
																obj.getString("name"), 
																obj.getString("phone"),
																sock.phone,
																sock.username,
																obj.getString("value"),"true","false"});
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}											
					}
					
					loadChatmsgFromLocal();
				}
			}
		};
	};
	
	void loadChatmsgFromLocal(){
		
		WechatSocket sock = WechatSocket.getInstance();
		SQLiteDatabase db = WechatLocalSQLite.getDB();
		
		//从数据库中读取消息，并把最新一条和未读数显示到屏幕上（包括刚才从网络读取的）
		Cursor cursor = db.rawQuery(
				"select srcname,srcphone,value from (select * from (select * from wechat_temp_message where dstphone=?) t group by srcphone) order by id asc"
				,new String[]{sock.phone});				

		if (cursor.moveToFirst()){
			
			do {
				
				
				FriendDescriptor friend = new FriendDescriptor();
				friend.setname(cursor.getString(0));
				friend.setphone(cursor.getString(1));
				friend.setprivate(cursor.getString(2));
				//未读信息数
				friend.unreadmessage_count = db.rawQuery(
						"select id from wechat_temp_message where dstphone=? and srcphone=? and read='false'", 
						new String[] {sock.phone,friend.getphone()}).getCount();
				
				chatlistadapter.add(friend);
					  			
			}while(cursor.moveToNext());	
			chatlistadapter.commit();	
		}				
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		view = getActivity().getLayoutInflater().inflate(R.layout.tab_main,
				null);

		notfoundmessage = (TextView) view.findViewById(R.id.notfoundmessage);
		chatlist = (ListView) view.findViewById(R.id.chatlist);

		chatlistadapter = new ChatlistAdapter(getActivity());
		chatlist.setAdapter(chatlistadapter);

		chatlist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

				FriendDescriptor friend = chatlistadapter.get(arg2);
				Intent intent = new Intent(getActivity(), ActivityChat.class);
				intent.putExtra("phone", friend.getphone());
				intent.putExtra("name", friend.getname());
				((BaseActivity) getActivity()).bootActivity(intent);
			}
		});

		chatlist.setOnItemLongClickListener(new ItemLongClick());

		if (chatlistbroadcast == null) {

			//注册本地广播
			localbroadcast = LocalBroadcastManager.getInstance(getActivity());			
			chatlistbroadcast = new ChatListBroadcastReceiver();
			
			IntentFilter filter = new IntentFilter();
			
			filter.addAction(getResources().getString(R.string.chatmessage_broadcast));
			filter.addAction(getResources().getString(R.string.send_chatmessage_broadcast));
			filter.addAction(getResources().getString(R.string.localbrard_readmessage));
			filter.addAction(getResources().getString(R.string.localbrard_deletefriend));

			localbroadcast.registerReceiver(chatlistbroadcast, filter);
		}
		
		loadChatmsgFromLocal();
		WechatSocket.getInstance().query_unreadmessage(handler);
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		if (chatlistbroadcast == null)
			localbroadcast.unregisterReceiver(chatlistbroadcast);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	class ChatListBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			
			String action = arg1.getAction();
			
			if (!isAdded())		
				return ;
			
			if (action.equals(getResources().getString(R.string.localbrard_deletefriend))){
				
				String phone = arg1.getExtras().getString("phone");
				chatlistadapter.del(phone);
				chatlistadapter.commit();
				
			}else if (action.equals(getResources().getString(R.string.localbrard_readmessage))){
				
				String phone = arg1.getExtras().getString("phone");

				int i = chatlistadapter.find(phone);
				if (i == -1)
					return;
				
				FriendDescriptor friend = chatlistadapter.get(i);
				if (friend == null)
					return;		
				
				friend.unreadmessage_count = 0;
				
				chatlistadapter.notifyDataSetChanged();
				
				return;
			}else {
				JSONObject obj = null;
				try { 
					obj = new JSONObject(arg1.getExtras().getString("data"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				
				FriendDescriptor friend = null;
				try {
					
					friend = new FriendDescriptor(); 
										
					friend.setphone(obj.getString("phone"));
					friend.setname(obj.getString("name"));
					friend.setprivate(obj.getString("value"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				chatlistadapter.add(friend);
				chatlistadapter.commit();				
			}
		}
	}

	class ItemLongClick implements OnItemLongClickListener, OnClickListener {

		PopupWindow window = null;
		ViewGroup view;
		ViewGroup unread;
		ViewGroup top;
		ViewGroup deletechat;
		ChatlistAdapter parent;
		View chatview;
		int chatindex;

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			// TODO Auto-generated method stub

			if (arg0.equals(chatlist)) {

				parent = (ChatlistAdapter) arg0.getAdapter();
				chatview = arg1;
				chatindex = arg2;

				if (window == null) {

					view = (ViewGroup) getActivity().getLayoutInflater()
							.inflate(R.layout.dialog_chatslongclick, null);
					view.setFocusable(true);
					view.setFocusableInTouchMode(true);
					window = new PopupWindow(view, LayoutParams.FILL_PARENT,
							LayoutParams.FILL_PARENT);
					window.setFocusable(true);
					window.setOutsideTouchable(true);
					window.setBackgroundDrawable(new BitmapDrawable());

					unread = (ViewGroup) view.findViewById(R.id.unread);
					top = (ViewGroup) view.findViewById(R.id.top);
					deletechat = (ViewGroup) view.findViewById(R.id.deletechat);

					view.setOnClickListener(this);
					top.setOnClickListener(this);
					deletechat.setOnClickListener(this);
				}

				window.showAtLocation(getActivity().getWindow().getDecorView(),
						Gravity.CENTER, 0, 0);
			}

			return true;
		}

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub

			if (arg0.equals(unread)) {

			} else if (arg0.equals(top)) {

			} else if (arg0.equals(deletechat)) {

				SQLiteDatabase db = WechatLocalSQLite.getDB();
				WechatSocket sock = WechatSocket.getInstance();
				FriendDescriptor friend = chatlistadapter.get(chatindex);
				db.execSQL("delete from wechat_temp_message where dstphone = ? and srcphone = ?", 
						new String[]{sock.phone,friend.getphone()});
				parent.chatlist.remove(chatindex);
				chatlistadapter.commit();
			}

			if (window.isShowing())
				window.dismiss();
		}
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

	public class ChatlistAdapter extends BaseAdapter {

		LayoutInflater inflater;
		ArrayList<FriendDescriptor> chatlist = new ArrayList<FriendDescriptor>();
		ChatListBroadcastReceiver chatlistbcr;

		class ViewHolder {

			ImageView image;
			TextView tv_name;
			TextView tv_content;
			TextView tv_unread;
		}

		public ChatlistAdapter(Context context) {
			// TODO Auto-generated constructor stub

			inflater = LayoutInflater.from(context);
		}
		
		public int find(String phone){
		
			FriendDescriptor t = null;
			int i = -1;
			
			for (int j = 0; j < chatlist.size(); j++) {

				t = (FriendDescriptor) chatlist.get(j);
				if (TextUtils.equals(t.getphone(), phone)) {
					i = j;
					break;
				}
			}			
			
			return i;
		}
		
		/* 新增聊天信息（此时还不会生效，要再调用 commit()） */
		public void add(FriendDescriptor chat) {

			del(chat.getphone());
			// 添加回去。添加到第一个位置 
			chat.setimage(getParent().getImageFromLocal(chat.getphone()));
			chatlist.add(0, chat);					
		}

		/*
		 * 删除一个聊天消息框
		 */
		public void del(String phone){
			
			int i = find(phone);
			if (i != -1)
				chatlist.remove(i);		
		}

		/* 生效对聊天列表的修改。其实就是通知 adapter 进行修改。 */
		public void commit() {

			notifyDataSetChanged();

			if (getCount() == 0) {

				notfoundmessage.setVisibility(View.VISIBLE);
			} else {

				notfoundmessage.setVisibility(View.GONE);
			}
		}

		public FriendDescriptor get(int index) {

			return chatlist.get(index);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return chatlist.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return chatlist.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		ImageGetter imgGetter = new ImageGetter() {
			
			public Drawable getDrawable(String source) {
				
				//这里的 ID 就是 R.drawable.imageID
				int id = Integer.parseInt(source);
				
				//得到图像
				Drawable d = WechatApplication.getContext().getResources().getDrawable(id);
				 
				//按原大小显示
				d.setBounds(0, 0, DensityUtil.dip2px(getActivity(), 20), DensityUtil.dip2px(getActivity(), 20));
				
				return d;
			}
		}; 	
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			FriendDescriptor friend = chatlist.get(position);
			ViewHolder holder;

			if (convertView == null) {

				holder = new ViewHolder();
				
				convertView = (ViewGroup) inflater.inflate(R.layout.tab_main_select_item, null);
				
				holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
				holder.image = (ImageView) convertView.findViewById(R.id.iv_avatar1);
				holder.tv_unread = (TextView)convertView.findViewById(R.id.tv_unread);
				holder.tv_content = (TextView)convertView.findViewById(R.id.tv_content);
				
				convertView.setTag(holder);
			} else {

				holder = (ViewHolder) convertView.getTag();
			}

			Spanned vvv = Html.fromHtml(friend.getprivate(),imgGetter,null);
			holder.tv_content.setText(vvv);
			
			holder.tv_name.setText(friend.getname());
			holder.tv_unread.setText(friend.unreadmessage_count+"");
			holder.image.setImageBitmap(friend.getImage());
			
			if (friend.unreadmessage_count > 0){
				holder.tv_unread.setVisibility(View.VISIBLE);
				holder.tv_unread.setText(friend.unreadmessage_count+"");
			}else holder.tv_unread.setVisibility(View.GONE);

			return convertView;
		}

	}
}
