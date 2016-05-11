package com.yonyou.proxy.alipay.util;

public interface C {
	
	final String  DOCUMENT_ROOT="params";
	
	final int SUCC_CODE = 0;  // 0 成功
	final int FAIL_CODE1= -1; // -1交易失败	  
	final int FAIL_CODE2= -2; // -2 订单异常
	final int FAIL_CODE3= -3; // -3 未知的交易
	final int FAIL_CODE4= -4; // -4 执行异常
	final int FAIL_CODE8= -8; // -8 交易失败 
	final int FAIL_CODE9= -9; // -9 初始化支付宝失败
	
	
	static String CODE_NODE_XPATH = "//code";
	static String ROOT_NODE = "response";
	static String CODE_NODE = "code";
	static String TRADE_STATUS = "tradestatus";
	static String MSG_NODE = "message";
	
	static final String RESULT_NODE = "result";
	
	static final String LIST_NODE = "item";
	static final String LIST_COLLECTION_NODE = "collection";
	
}
