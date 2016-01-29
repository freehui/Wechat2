package com.freehui.wechat2.baseclass;



/*
 * 只有外部类和静态内部类才能被序列化。普通的内部类无法序列化！血的教训！
 */
public class IORequest {
	private static final long serialVersionUID = 1L;
	public String value;
	
	public IORequest(String value) {
		// TODO Auto-generated constructor stub
		
		this.value = value;
	}
}
