package com.freehui.wechat2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.PopupWindow.OnDismissListener;

import com.example.wechat2.R;
import com.freehui.wechat2.push.ServicePush;
import com.freehui.wechat2.util.ByteUtils;
import com.freehui.wechat2.util.NetworkUtils;
import com.freehui.wechat2.util.UserActions;
import com.freehui.wechat2.util.WechatActivityManager;
import com.freehui.wechat2.windows.WechatAlert;

public class WechatSocket extends Socket{
	
	private String server_ip;
	public String username;
	public String phone;
	public Bitmap image;
	private String password;
	private InputStream in;
	private OutputStream out;
	public static boolean islogin = false;
	BroadcastReceiver networkchange;

	static final int ONLY_WRITE_INT = 99;
	static final int ONLY_WRITE_DATA = 98;
	static final int ONLY_READ = 97;
	
	static public class IORequest implements Serializable{
	
		private static final long serialVersionUID = 1L;
		int type;
		Object value;
		
		public IORequest(int type,Object value) {
			// TODO Auto-generated constructor stub
			this.type = type;
			this.value = value;
		}
	}
	
	private WechatSocket(){
		
		server_ip = WechatApplication.getContext().getString(R.string.server_ip);
		
		//创建 IO 请求队列（管道）
		bufstream = new PipedOutputStream();
		try {
			readstream = new PipedInputStream(bufstream);
			IORequestOutput = new ObjectOutputStream(bufstream);
			IORequestInput = new ObjectInputStream(readstream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//IO Handler 队列
		IORequestHandlerList = new ArrayList<Handler>();
	}
	
	static private WechatSocket instance;
	static public  WechatSocket getInstance(){
		
		if (instance == null)
			instance = new WechatSocket();
		
		return instance;
	}
	
	public void setPhone(String phone){
		
		this.phone = phone;
	}
	
	public void setPassword(String password){
		
		this.password = password;
	}
	
	public String getPhone(){
		
		return this.phone;
	}
	
	public String getPassword(){
		
		return this.password;
	}	
	
	/*
	 * 连接服务器
	 */
	public int connectServer(){

		int ret = 0;
		
		@SuppressWarnings({ "unchecked" })
		FutureTask task = new FutureTask(new Callable<Integer>() {
			
			@Override
			public Integer call() throws Exception {
				// TODO Auto-generated method stub
				
				try {
					
					connect(new InetSocketAddress(server_ip, 9981),1000);
					out = getOutputStream();
					in = getInputStream();		

					return 1;
				} catch (final IOException e) {
					// TODO Auto-generated catch block
					Log.w("..","无法连接到服务器"+server_ip);
					return 0;
				}
			}
		});
		
		Thread thread = new Thread(task);
		thread.start();
		
		try {
			ret = (Integer) task.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			Log.w("...","InterruptedException e");
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			Log.w("...","ExecutionException e");
			e.printStackTrace();
		}		
		
		return ret;
	}
	
	/*
	 * 断开连接
	 */
	public void disconnected(){
		
		try {
			if (instance != null)
				instance.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Context t = WechatApplication.getContext();
		t.stopService(new Intent(t,ServicePush.class));
		instance = null;
		islogin = false;
	}
	
	public void relogin(Context ctx){
		
		disconnected();
		instance = new WechatSocket();
		login(ctx);
	}
	
	/*
	 * 登陆
	 * 
	 * ret:
	 * 	2. 成功
	 *  1. 失败-密码错误或帐号不存在 
	 *  0. 无法连接到服务器
	 */
	@SuppressWarnings("unchecked")
	public int login(final Context context){
		
		//连接服务器
		if (connectServer() == 0)
			return 0;
		
		//发送登陆请求
		String wdata = "[{\"phonenumber\":\"" + phone
				+ "\",\"password\":\"" + password + "\"}]";		
		int ret = sync_control_request(UserActions.LOGIN, wdata);
		
		if (ret == 2){
			//成功登陆
			
			//得到当前用户的信息
			String Imsg = sync_request(UserActions.MESSAGE, phone);
			JSONObject obj;
			try {
				obj = new JSONObject(Imsg);
				username = obj.getString("name");
				phone = obj.getString("phone");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//得到当前用户的头像
			image = getImage(context,phone);
			
			//修改登陆状态
			islogin = true;
			
			//启动推送服务
			Intent serviceintent = new Intent(context, ServicePush.class);
			context.startService(serviceintent);		
				
			//启动处理 IO 请求的线程
			IORequestThread.start();
			
			//监听网络状态
			if (networkchange == null){
				IntentFilter filter = new IntentFilter();
				filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");				
		        networkchange = new BroadcastReceiver() {
					
					@Override
					public void onReceive(Context arg0, Intent arg1) {
						// TODO Auto-generated method stub
						
						Context ctx = WechatApplication.getContext();
						ConnectivityManager cm = 
							(ConnectivityManager)ctx.getSystemService(arg0.CONNECTIVITY_SERVICE);
						NetworkInfo networkinfo = cm.getActiveNetworkInfo();
						
						if (networkinfo != null && networkinfo.isAvailable()){
							//重新登录
							
							Log.w("TAG","RELOGIN");
			
						}else{ 
							//断开连接
							
							islogin = false;
							disconnected();
							WechatActivityManager.finishAll();		
						}
					}
				};			
				WechatApplication.getContext().registerReceiver(networkchange,filter);	
			}
		}else{
			//登陆失败的话，要断开和服务器的连接
			disconnected();
			islogin = false;
		}
		
		return ret;
	}
	
	/*
	 * 注册
	 */
	public int register(String name,String phone,String password,byte[] image){
		
		int ret = 0;
		
		//连接服务器
		if (connectServer() == 0)
			return 0;
		
		// 构造 json 数据 
		String data = "[{\"username\":\""
				+ name
				+ "\",\"phonenumber\":\""
				+ phone
				+ "\",\"password\":\""
				+ password + "\"}]"; 		

		//发送注册请求
		ret = sync_control_request(UserActions.REGISTER, data);
		
		
		if (ret == 2 && image != null){
			//如果需要上传头像，则进行上传
			
			updateImage(phone,image);
		}
		
		//断开连接。注册不需要长连接。
		disconnected();
		
		return ret;
	}
	
	/*
	 * 更新一个用户的头像
	 */
	public void updateImage(final String phone,final byte[] image){

		if (image.length == 0)
			return;

		Thread thread = new Thread(){
			public void run() {
				
				/*
				 * 数据包格式：
				 * 	类型:4
				 *  JSON文本长度:4
				 *  JSON文本:~
				 *  图片:~
				 */
				String json =  null;
				json = "[{\"username\":\"" + phone + "\",\"imglen\":\"" + image.length + "\"}]\0";
				int length = json.length();
				
				//类型和文本长度
				byte[] head2 = ByteUtils.byteMerger(
						ByteUtils.number_to_byte(UserActions.USER_ACTIONS_UPDATE_IMAGE),
						ByteUtils.number_to_byte(length));
			    
				//类型和文本长度，再加上JSON文本
				head2 = ByteUtils.byteMerger(head2, json.getBytes());		
				
				ByteArrayInputStream bis = new ByteArrayInputStream(image);
				
				//发出上传头像命令
				try {
					out.write(head2);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//开始上传头像
				byte [] t = new byte[500];
				int total = 0;
				int len = 0;
				while(total < image.length){
					
					Log.w("TAG","total:"+total+";size:"+image.length);

					try {
						len = bis.read(t);
					} catch (IOException e) {
						// TODO Auto-generated catch block
					e.printStackTrace();
					}
					
					total += len;
					if (total <= 0)
						break;
					
					try {
						out.write(t);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				

				try {
					bis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}					
			};
		};
		
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * 从服务器得到一个用户的头像
	 */
	public Bitmap getImage(final Context context,final String phone){
		
		Bitmap ret = null;
		
		//发送请求
		final int len = sync_control_request(UserActions.USER_ACTIONS_GET_IMAGE, phone);
		
		if (len == 0){
			//如果返回的值为 0，就说明没有这个人的头像，直接返回一个默认的头像。
			
			return BitmapFactory.decodeResource(
					context.getResources(), 
					R.drawable.default_useravatar);
		}
		
		//返回的不为0，那么这就是文件长度了，准备加载。
		
		FutureTask<Bitmap> task = new FutureTask<Bitmap>(new Callable<Bitmap>() {
				
			@Override
			public Bitmap call() throws Exception {
				// TODO Auto-generated method stub
				
				byte[] buf;
				ByteArrayOutputStream aos = new ByteArrayOutputStream();
				//这个 file 是本地头像，我们要把头像保存在本地。不追加写。
				
				FileOutputStream localImage = 
						new FileOutputStream(context.getFilesDir() + "/" + phone +".jpg",false);
				
				int total = 0;
				int readsize;
				Bitmap ret;
				
				while (total < len){
					
					//算出本次要读的长度
					readsize = ((len - total) > 500) ? 500 : (len - total);
					
					//读出当前内容
					buf = recv(readsize);
					
					try {
						//把读出的内容存放在 aso 内。
						aos.write(buf);
						//写入本地文件
						localImage.write(buf);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					//累计读取大小
					total += readsize;
				}		
				
				ret = ByteUtils.Bytes2Bimap(aos.toByteArray());
				
				return ret;
			}
		});
			
		Thread thread = new Thread(task);
		thread.start();
		
		try {
			ret = task.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ret;
	}
	
	PipedOutputStream bufstream;
	PipedInputStream readstream;
	ObjectOutputStream IORequestOutput;
	ObjectInputStream IORequestInput;
	//这个队列用来存放请求的 Handler，顺序和请求是一样的
	ArrayList<Handler> IORequestHandlerList;
	
	/*
	 * 这个线程顺序收发网络请求。
	 * Android 强制开线程访问网络，有时候如果发出多个请求（同时多个线程访问），会造成数据乱序，必须同步一下
	 */
	Thread IORequestThread = new Thread(){
			
		public void run() {
			 
			while(islogin){
				
				IORequest request = null;
				try {
					request = (IORequest) IORequestInput.readObject();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.w("TAG","IOException");
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					Log.w("TAG","ClassNotFoundException");
				}
				
				//构造 msg
				Message msg = new Message();
				msg.what = request.type;
				
				switch (request.type){
				
				case UserActions.QUERY_UNREADMESSAGE:
				case UserActions.GET_ALL_FRIEND:
				case UserActions.GET_ADDFRIEND_REQUEST:
				case UserActions.MESSAGE:
					//发送请求
					msg.obj = sync_request(request.type, (String)request.value);
					break;
					
				case ONLY_WRITE_INT:
					try {
						out.write(ByteUtils.number_to_byte((Integer)request.value));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						Log.w("TAG","out.write(ByteUtils.number_to_byte((Integer)request.value))");
					}
					continue;
				case UserActions.USER_ACTIONS_DEL_FRIEND:
					send(request.type,(String)request.value);
					continue;
				} 
				//取出Handler
				Handler handler = IORequestHandlerList.get(0);
				IORequestHandlerList.remove(0);
				handler.sendMessage(msg);
			}
		};
	};	
	
	/*
	 * 添加一个 IO 请求
	 */
	public void add_IORequest(int type,Object value,Handler handler){
		
		IORequest request = new IORequest(type,value);
		
		if (handler != null){ 
			IORequestHandlerList.add(handler);
		}
		
		try {
			IORequestOutput.writeObject(request);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.w("TAG","add_IORequest");
		}
	}
	
	/*
	 * 得到一个用户的用户信息
	 */
	public void getMesage(String phone,Handler handler){
		
		add_IORequest(UserActions.MESSAGE,phone,handler);
	}

	/*
	 * 查询未读消息.
	 */
	@SuppressWarnings("unchecked")
	public void query_unreadmessage(Handler handler){
			
		add_IORequest(UserActions.QUERY_UNREADMESSAGE, "...", handler);
	}
	
	/*
	 * 查询所有好友
	 */
	public void getAllfriend(Handler handler){
	
		add_IORequest(UserActions.GET_ALL_FRIEND, "...", handler);
	}
	
	/*
	 * 同步查询所有未读请求
	 * */
	public void query_addfriendrequest(Handler handler){

		add_IORequest(UserActions.GET_ADDFRIEND_REQUEST, "...", handler);
	}
	
	
	/*
	 * 发消息
	 */
	public void send_chatmessage(String phone,String name,String value){
		
		async_request(
				UserActions.FAILED,
				"[{\"dstphone\":\"" + phone + "\",\"dstname\":\"" + name + "\",\"value\":\"" + value + "\"}]",null);
	}
	
	
	/*
	 * 删除好友
	 */
	public void deleteFriend(String phone){
		
		add_IORequest(UserActions.USER_ACTIONS_DEL_FRIEND, phone, null);
	}
	
	/*
	 * 发送添加好友请求
	 * 
	 * phone: 对方的手机号
	 * value: 验证信息
	 */
	public void addfriend(String phone,String value){
		
		async_request(UserActions.ADDFRIEND, 
				"[{\"phone\":\"" + phone + "\"," + 
				  "\"value\":\"" + value + "\"," +
				  "\"name\":\"" + username + "\"}]",null);
	}
	
	/*
	 * 确认添加好友
	 */
	public void confirmaddfriend(String phone,String name){
		
		async_request(UserActions.OKORNO, 
				 "[{\"type\":\"" + 1 + 
				"\",\"phone\":\"" + phone + 
				"\",\"name\":\"" + name + "\"}]"
					, null);
	}
	
	/*
	 * 拒绝添加好友
	 */
	public void refuseaddfriend(String phone,String name){
		
		async_request(UserActions.OKORNO, 
				 "[{\"type\":\"" + 0 + 
				"\",\"phone\":\"" + phone + 
				"\",\"name\":\"" + name + "\"}]"
					, null);
	}	

	/*
	 * 发送一个只返回一个 int 值的同步请求
	 * 
	 * 	type: 请求类型
	 *  value: 附带数据
	 *  
	 * ret
	 * 	返回的值
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public int sync_control_request(final int type,final String value){
		
		int ret = 0;
		
		FutureTask task = new FutureTask(new Callable<Integer>() {
			
			public Integer call(){
				// TODO Auto-generated method stub
				
				return sendAndResult(type,value);
			}
		});
		
		Thread thread = new Thread(task);
		thread.start();
		
		try {
			ret = (Integer)task.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			Log.w("...","InterruptedException e");
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			Log.w("...","ExecutionException e");
			e.printStackTrace();
		}		
		return ret;
	}	
	
	/*
	 * 发送一个同步请求
	 * 
	 * 	type: 请求类型
	 *  value: 附带数据
	 *  
	 * ret
	 * 	返回的 JSON 字符串
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String sync_request(final int type,final String value){
		
		String ret = null;
		FutureTask task = new FutureTask(new Callable<String>() {
			
			public String call(){
				// TODO Auto-generated method stub
				
				int len = 0;
				String ret = null;
				
				len = sendAndResult(type,value);
				
				if (len < 4)//无新消息
					return null;
				
				//有消息，读出
				ret = new String(recv(len));
				
				return ret;
			}
		});
		
		Thread thread = new Thread(task);
		thread.start();
		
		try {
			ret = (String)task.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			Log.w("...","InterruptedException e");
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			Log.w("...","ExecutionException e");
			e.printStackTrace();
		}		
		return ret;
	}
	
	/*
	 * 发送一个异步请求
	 * 
	 * 	type: 请求类型
	 *  value: 附带数据
	 *  handler: 数据后，处理数据的 Handler
	 */
	@SuppressLint("NewApi") 
	public void async_request(final int type,final String value,final Handler handler){
		
		new Thread(){
			
			public void run() {
				
				Message msg = new Message();
				
				int len = 0;
				JSONArray ret = null;
				
				if (handler == null){
					//如果 handler 为空，就说明不需要处理，自然也就没有返回值，即使有也不知道怎么处理。
					send(type,value);
					return;
				}else {
					// handler 不为空，就说明有后续操作，即有返回值
					len = sendAndResult(type, value);
				}
				
				if (len < 4){//无新消息
					
					ret = null;
				}else {
					Log.w("TAG-len",len+"");
					try {
						//有消息，那么把他格式化为 JSON 数组
						ret = new JSONArray(recv(len));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						Log.w("服务器传来的数据格式有误","服务器传来的数据格式有误");
						e.printStackTrace();
					}
				}
				
				msg.what = type;
				msg.obj = ret;	
				handler.handleMessage(msg);
			};
		}.start();		
	}
	
	/*
	 * 发送一个接受 int 返回值的数据包
	 */
	public int sendAndResult(int t,String v){
		
		int ret = 0;
		
		send(t,v);
		ret = readint();
		
		return ret;
	}
	
	/*
	 * 发送一个数据包
	 * 
	 * t: 头部中的类型
	 * s: 数据包内容
	 */
	public void send(int t, String s) {

		byte[] data = s.getBytes();
		byte[] type = ByteUtils.number_to_byte(t);
		byte[] leng = ByteUtils.number_to_byte(data.length);
		byte[] head = ByteUtils.byteMerger(type, leng);
		byte[] out_data = ByteUtils.byteMerger(head, data);

		try {
			this.out.write(out_data);
			Log.w("...","out:" + s);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.w("this.out.write", "this.out.write");
		}
	}
	
	public int readint(){
		
		return ByteUtils.byte_to_number(recv(4), 0);
	}
	
	public byte[] recv(int len) {

		return NetworkUtils.readFromSocket(this, len);
	}	
}
