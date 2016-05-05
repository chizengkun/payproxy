package com.yonyou.proxy.service;

public interface DbAlipayIntf {

	void saveTradePay(Long sfid, String sqbm, String inputs,String tradeNo, String amount,
						String alipayJson, int flag, long t1, long t2);	
}
