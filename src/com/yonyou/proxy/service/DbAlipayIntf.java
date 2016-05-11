package com.yonyou.proxy.service;

public interface DbAlipayIntf {

	void saveTradePay(Long sfid, String sqbm, String inputs, String payCode,String tradeNo, String amount, String storeId,
			String alipayJson, int flag, long t1, long t2);

	void saveTradeQuery(String tradeNo, int flag, String response, long t1, long t2, String sqbm);

	void saveTradeRefund(String tradeNo, String refundAmount, String refundReason, String storeId, String result,
			int flag, long t1, long t2, String sqbm);
}
