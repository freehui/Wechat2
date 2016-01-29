package com.freehui.wechat2.util;

public class UserActions {

	/* 注册 */
	public static final int REGISTER = 1;
	/* 登陆 */
	public static final int LOGIN = 2;
	/* 查看所有未接受的消息 */
	public static final int QUERY_UNREADMESSAGE = 3;
	/* 查询用户信息 */
	public static final int MESSAGE = 4;
	/* 添加好友 */
	public static final int ADDFRIEND = 5;
	/* 查询好友申请请求 */
	public static final int GET_ADDFRIEND_REQUEST = 6;
	/* 接受或拒绝好友申请 */
	public static final int OKORNO = 7;
	/* 获得好友列表 */
	public static final int GET_ALL_FRIEND = 8;
	/* 信息发送失败时的请求 */
	public static final int FAILED = 9;
	/* 更新头像 */
	public static final int USER_ACTIONS_UPDATE_IMAGE = 10;
	/* 得到头像 */
	public static final int USER_ACTIONS_GET_IMAGE = 11;
	/* 删除好友 */
	public static final int USER_ACTIONS_DEL_FRIEND = 12;
	/* 连接推送服务器 */
	public static final int USER_ACTIONS_CONNECT_PUSH_SERVER = 13;	
	
	/* 推送：对方接受好友请求 */
	public static final int PULL_ADDNOTIFI = 2;	
	/* 推送：对方发来好友请求 */
	public static final int PULL_ADDREQUEST = 3;	
	/* 推送：对方拒绝好友请求 */
	public static final int PULL_REFUSENOTIFI = 0;			
	/* 推送：对方发来消息 */
	public static final int PULL_CHATMESSAGE = 8;
	/* 推送：断线 */
	public static final int PUSH_DISCONNECT = 9;	
	
	/* 本地广播：阅读一个好友的所有消息 */
	public static final int LOCALBOARD_READMEESSAGE = 20;
	/* 本地广播：删除好友 */
	public static final int LOCALBOARD_DELETEFRIEND = 21;
		
	
}
