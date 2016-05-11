package com.yonyou.proxy.alipay.util;

public interface C {
	
	final String  DOCUMENT_ROOT="params";
	
	final int SUCC_CODE = 0;  // 0 �ɹ�
	final int FAIL_CODE1= -1; // -1����ʧ��	  
	final int FAIL_CODE2= -2; // -2 �����쳣
	final int FAIL_CODE3= -3; // -3 δ֪�Ľ���
	final int FAIL_CODE4= -4; // -4 ִ���쳣
	final int FAIL_CODE8= -8; // -8 ����ʧ�� 
	final int FAIL_CODE9= -9; // -9 ��ʼ��֧����ʧ��
	
	
	static String CODE_NODE_XPATH = "//code";
	static String ROOT_NODE = "response";
	static String CODE_NODE = "code";
	static String TRADE_STATUS = "tradestatus";
	static String MSG_NODE = "message";
	
	static final String RESULT_NODE = "result";
	
	static final String LIST_NODE = "item";
	static final String LIST_COLLECTION_NODE = "collection";
	
}
