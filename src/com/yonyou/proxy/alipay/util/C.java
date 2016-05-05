package com.yonyou.proxy.alipay.util;

public interface C {
	

	final int LOOP_SLEEP_INTERVAL = 5;
	final int RETRY_LOOP_COUNT = 6;
	
	final int SUCC_CODE = 0;  // 0 �ɹ�
	final int FAIL_CODE1= -1; // -1����ʧ��	  
	final int FAIL_CODE2= -2; // -2 �����쳣
	final int FAIL_CODE3= -3; // -3 δ֪�Ľ���
	final int FAIL_CODE4= -4; //
	final int FAIL_CODE8= -8; // -8 ����ʧ�� 
	final int FAIL_CODE9= -9; // -9 ��ʼ��֧����ʧ��
	
	
	static String CODE_NODE_XPATH = "//resultcode";
	static String ROOT_NODE = "response";
	static String CODE_NODE = "resultcode";
	static String MSG_NODE = "resultmessage";
	
	static final String RESULT_NODE = "result";
	
	static final String LIST_NODE = "item";
	static final String LIST_COLLECTION_NODE = "collection";
	
}