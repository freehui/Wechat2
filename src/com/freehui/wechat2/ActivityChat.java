package com.freehui.wechat2;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.wechat2.R;
import com.freehui.wechat2.adapter.EmojiPagerAdapter;
import com.freehui.wechat2.adapter.EmojiPagerAdapter.onEmojiItemSelected;
import com.freehui.wechat2.baseclass.FriendDescriptor;
import com.freehui.wechat2.baseclass.NetworkActivity;
import com.freehui.wechat2.util.ChatMessage;
import com.freehui.wechat2.util.DensityUtil;
 

public class ActivityChat extends NetworkActivity implements OnClickListener {

	FriendDescriptor friend;
	ListView listview;
	Button send;
	EditText chatvalue;
	ArrayList<ChatMessage> chatmessage = new ArrayList<ChatMessage>();
	ChatBroadcastReceiver chatbroadcast;
	LocalBroadcastManager localbroadcast;
	ChatAdapter chatAdapter;
	GridView emoji;
	ImageView showemoji;
	View bottom_emoji;
	ViewPager emoji_pageswitcher;

	int [][] image_resource = {
			
			{
				
			R.drawable.emoji_1,
			R.drawable.emoji_2,
			R.drawable.emoji_3,
			R.drawable.emoji_4,
			R.drawable.emoji_5,
				
			R.drawable.emoji_6,
			R.drawable.emoji_7,
			R.drawable.emoji_8,
			R.drawable.emoji_9,
			R.drawable.emoji_10,
				
			R.drawable.emoji_11,
			R.drawable.emoji_12,
			R.drawable.emoji_13,
			R.drawable.emoji_14,
			R.drawable.emoji_15,
				
			R.drawable.emoji_16,
			R.drawable.emoji_17,
			R.drawable.emoji_18,
			R.drawable.emoji_19,
			R.drawable.emoji_20,
				
			R.drawable.a7d,
		},{
			
			R.drawable.emoji_21,
			R.drawable.emoji_22,
			R.drawable.emoji_23,
			R.drawable.emoji_24,
			R.drawable.emoji_25,
			
			R.drawable.emoji_26,
			R.drawable.emoji_27,
			R.drawable.emoji_28,
			R.drawable.emoji_29,
			R.drawable.emoji_30,
			
			R.drawable.emoji_31,
			R.drawable.emoji_32,
			R.drawable.emoji_33,
			R.drawable.emoji_34,
			R.drawable.emoji_35,
			
			R.drawable.emoji_36,
			R.drawable.emoji_37,
			R.drawable.emoji_38,
			R.drawable.emoji_39,
			R.drawable.emoji_40,
			
			R.drawable.a7d,
			
		},{
		
			R.drawable.emoji_41,
			R.drawable.emoji_42,
			R.drawable.emoji_43,
			R.drawable.emoji_44,
			R.drawable.emoji_45,
			
			R.drawable.emoji_46,
			R.drawable.emoji_47,
			R.drawable.emoji_48,
			R.drawable.emoji_49,
			R.drawable.emoji_50,
			
			R.drawable.emoji_51,
			R.drawable.emoji_52,
			R.drawable.emoji_53,
			R.drawable.emoji_54,
			R.drawable.emoji_55,
			
			R.drawable.emoji_56,
			R.drawable.emoji_57,
			R.drawable.emoji_58,
			R.drawable.emoji_59,
			R.drawable.emoji_60,
			
			R.drawable.a7d,
		},
		{
			
			R.drawable.emoji_61,
			R.drawable.emoji_62,
			R.drawable.emoji_63,
			R.drawable.emoji_64,
			R.drawable.emoji_65,
		
			R.drawable.emoji_66,
			R.drawable.emoji_67,
			R.drawable.emoji_68,
			R.drawable.emoji_69,
			R.drawable.emoji_70,
			
			R.drawable.emoji_71,
			R.drawable.a7d,
		}
	};	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_chat);
		setBack(findViewById(R.id.register_back));
		
		SQLiteDatabase db = WechatLocalSQLite.getDB();
		WechatSocket sock = WechatSocket.getInstance();
		localbroadcast = LocalBroadcastManager.getInstance(this);
		 
		Bundle b = getIntent().getExtras();
		
		if (b == null){
			finish();
			return;
		}
		
		//表情点击事件
		showemoji = (ImageView)findViewById(R.id.showemoji);
		showemoji.setOnClickListener(this);
		bottom_emoji = findViewById(R.id.bottom_emoji);
		
		friend = new FriendDescriptor();
		friend.setname(b.getString("name"));
		friend.setphone(b.getString("phone"));

		//发送一个本地广播，通知其他组件这个人的消息我已经读了
		Intent intent = new Intent();
		intent.setAction(getString(R.string.localbrard_readmessage));
		intent.putExtra("phone", friend.getphone());		
		localbroadcast.sendBroadcast(intent);	
		
		//改掉所有未读消息
		db.execSQL("update wechat_temp_message set read='true' where dstphone=? and read='false'", 
				   new String[] {sock.phone});
		
		friend.setimage(getImageFromLocal(friend.getphone()));

		listview = (ListView) findViewById(R.id.listview);
		send = (Button) findViewById(R.id.send_addfriend_request);
		chatvalue = (EditText) findViewById(R.id.chatvalue);
		chatvalue.setOnClickListener(this);
		chatvalue.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				bottom_emoji.setVisibility(View.GONE);
			}
		});
		
		send.setOnClickListener(this);

		chatAdapter = new ChatAdapter(this,
				new ArrayList<ChatMessage>());
		
		//开始读取聊天记录
		Cursor cursor = db.rawQuery(
				"select id,srcname,time,value,srcphone,recv from wechat_temp_message where " +
				"(srcphone = ? and dstphone = ?) or (srcphone = ? and dstphone = ?)"
				,new String[]{
						sock.phone,
						friend.getphone(),
						friend.getphone(),
						sock.phone
				});				
		
		if (cursor.moveToFirst()){
			
			do {
				
				ChatMessage msg = new ChatMessage();
				msg.settext(cursor.getString(3));
				msg.recv = cursor.getString(5).equals("true");
				chatAdapter.add(msg);
			}while(cursor.moveToNext());
		}				
		//读取完毕
		
		listview.setAdapter(chatAdapter);		

		((TextView) findViewById(R.id.friend_name)).setText(friend.getname());

		chatbroadcast = new ChatBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(getResources().getString(R.string.chatmessage_broadcast));
		localbroadcast.registerReceiver(chatbroadcast, filter);
		
		emoji_pageswitcher = (ViewPager) findViewById(R.id.emoji_pageswitcher);
		EmojiPagerAdapter adapter = new EmojiPagerAdapter(this,image_resource);
		adapter.setOnEmojiItemSelected(new onEmojiItemSelected() {
			
			public void onSelected(int pageindex, int emojiindex) {
				// TODO Auto-generated method stub
				
				int res = image_resource[pageindex][emojiindex];
				if (res == R.drawable.a7d){
					//删除按钮被点击
					
				    int action = KeyEvent.ACTION_DOWN;
				    //code:删除，其他code也可以，例如 code = 0
				    int code = KeyEvent.KEYCODE_DEL;
				    KeyEvent event = new KeyEvent(action, code);					
					chatvalue.onKeyDown(KeyEvent.KEYCODE_DEL, event);
				}else {

					//其他的图片
					chatvalue.append(Html.fromHtml("<img src='" + res + "'/>",imgGetter,null));
				}
			}
		});
		emoji_pageswitcher.setAdapter(adapter);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		LocalBroadcastManager.getInstance(ActivityChat.this).unregisterReceiver(chatbroadcast);
	}
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

		if (arg0.equals(send)) {

			if (chatvalue.getText().toString().isEmpty())
				return;
			
			//先用HTML解析他
			//String text = Html.toHtml(chatvalue.getText());
			String srctext = Html.toHtml(chatvalue.getText());
			//toHtml 后，文字外围会自动带上一个 <p> 标签，这里跳过这个标签
			//如果我们按过回车键，里面还会继续有 <p>，但这不影响，因为这是我们主动换行的
			srctext = srctext.substring(13, srctext.length()-5);
			//把双引号换成单引号，不换的话，无论是服务器还是客户端，都会解析出错
			srctext = srctext.replace("\"", "\'");
			
			//把消息发送到服务器
			WechatSocket ws = WechatSocket.getInstance();
			ws.send_chatmessage(friend.getphone(),friend.getname(), srctext);
			
			//在屏幕上显示这条消息
			ChatMessage msg = new ChatMessage(srctext,false);
			chatAdapter.add(msg);
			chatAdapter.commit();
			
			//构造广播，发送给其他组件
			Intent intent = new Intent();
			intent.setAction(getResources().getString(
					R.string.send_chatmessage_broadcast));
			intent.putExtra("data",
					"{\"name\":\"" + friend.getname() + "\",\"phone\":\""
							+ friend.getphone() + "\",\"value\":\""
							+ srctext + "\",\"ip\":\""
							+ friend.getip() + "\"}");

			SQLiteDatabase db = WechatLocalSQLite.getDB();
			WechatSocket sock = WechatSocket.getInstance();
			//把聊天记录存入数据库
			db.execSQL(
"insert into wechat_temp_message(srcname,srcphone,dstphone,dstname,value,recv) values(?,?,?,?,?,?)",
					new String[] { 
							friend.getname(), 
							friend.getphone(),
							sock.phone,
							sock.username,
							msg.getsrctext(),"false" });				
			
			localbroadcast.sendBroadcast(intent);

			chatvalue.setText("");
		}else if (arg0.equals(showemoji)){

			if (bottom_emoji.isShown()){
				bottom_emoji.setVisibility(View.GONE);
			}else{ 	 
				bottom_emoji.setVisibility(View.VISIBLE);
			}
			
			InputMethodManager imm = 
					(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(chatvalue.getWindowToken(), 0);			
			
		}else if (arg0.equals(chatvalue)){
			 
			bottom_emoji.setVisibility(View.GONE);
		} 
	}

	class ChatAdapter extends BaseAdapter {

		LayoutInflater inflater;
		ArrayList<ChatMessage> myfriendlist;

		public ChatAdapter(Context context, ArrayList<ChatMessage> list) {
			// TODO Auto-generated constructor stub

			inflater = LayoutInflater.from(context);
			this.myfriendlist = list;
		}

		/*
		 * 添加一个列表项
		 */
		public void add(ChatMessage msg) {

			myfriendlist.add(msg);
		}

		/*
		 * 提交所有添加和删除
		 */
		public void commit() {

			notifyDataSetChanged();
			listview.setSelection(listview.getCount() - 1);
		}

		public ChatMessage get(int index) {

			return myfriendlist.get(index);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub

			return myfriendlist.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return myfriendlist.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			ChatMessage msg = myfriendlist.get(position);
			Bitmap userimage = null;

			if (msg.recv == true) {
				// 这条消息是接收过来的，那么载入左侧的布局
				convertView = (ViewGroup) inflater.inflate(
						R.layout.chat_item_left, null);
				userimage = friend.getImage();
				convertView.setTag(1);
			} else {
				// 这条消息是我发送出去的，那么载入右侧的布局
				convertView = (ViewGroup) inflater.inflate(
						R.layout.chat_item_right, null);
				userimage = WechatSocket.getInstance().image;
				convertView.setTag(2);
			}

			ImageView image = (ImageView) convertView
					.findViewById(R.id.iv_userhead);
			TextView value = (TextView) convertView
					.findViewById(R.id.tv_chatcontent);

			image.setImageBitmap(userimage);
			
			String v1 = Html.toHtml((Spanned) msg.gettext());
			Spanned vvv = Html.fromHtml(v1.substring(13, v1.length()-5),imgGetter,null);
			value.setText(vvv);
			 
			return convertView;
		}
	}

	class ViewHolder {
		TextView value;
		ImageView image;
	}

	class ChatBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			
			String action = arg1.getAction();
			
			if (action.equals(getString(R.string.chatmessage_broadcast))){
				
				JSONObject obj;
				String v = "";

				try {
					obj = new JSONObject(arg1.getExtras().getString("data"));
					v = (String) obj.get("value");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				ChatMessage msg = new ChatMessage(v, true);
				chatAdapter.add(msg);
				chatAdapter.commit();		
				
				//改掉所有未读消息
				SQLiteDatabase db = WechatLocalSQLite.getDB();
				db.execSQL("update wechat_temp_message set read='true' where srcphone=? and dstphone=? and read='false'", 
						   new String[] {friend.getphone(),WechatSocket.getInstance().phone});
				
				//发送一个本地广播，通知其他组件这个人的消息我已经读了
				Intent intent = new Intent();
				intent.setAction(getString(R.string.localbrard_readmessage));
				intent.putExtra("phone", friend.getphone());		
				LocalBroadcastManager.getInstance(ActivityChat.this).sendBroadcast(intent);					
			}
		}
	}

	ImageGetter imgGetter = new ImageGetter() {
		
		public Drawable getDrawable(String source) {
			
			//这里的 ID 就是 R.drawable.imageID
			int id = Integer.parseInt(source);
			
			//得到图像
			Drawable d = getResources().getDrawable(id);
			
			//按原大小显示
			d.setBounds(0, 0, DensityUtil.dip2px(ActivityChat.this, 20), DensityUtil.dip2px(ActivityChat.this, 20));
			
			return d;
		}
	}; 
	
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == KeyEvent.KEYCODE_BACK){
			
			if (bottom_emoji.isShown()){
				bottom_emoji.setVisibility(View.GONE);
				return false;
			}else {
				
				close();
			}
		}
		
		return true;
	};

}
