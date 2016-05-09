package com.yonyou.proxy.service;

import java.util.HashMap;
import java.util.Map;

import com.ufida.hap.util.DBAgent;

public class DbAlipayImpl implements DbAlipayIntf {

	public void saveTradePay(Long sfid, String sqbm, String inputs, String tradeNo, String amount, String storeId,
			String alipayJson, int flag, long t1, long t2) {
		// 保存对应的支付信息
		Map<String, Object> condi = new HashMap<String, Object>();
		condi.put("SFID", sfid);
		condi.put("SQBM", sqbm);
		condi.put("INPUTS", inputs);
		condi.put("TRADENO", tradeNo);
		condi.put("AMOUNT", amount);
		condi.put("STOREID", storeId);
		condi.put("ALIPAYJSON", alipayJson);
		condi.put("FLAG", flag);
		condi.put("T1", t1);
		condi.put("T2", t2);

		String sql = "INSERT INTO ALIPAY_TRADEPAY(SFID,SQBM,INPUTS,TRADENO,AMOUNT,STOREID,ALIPAYJSON, FLAG,T1,T2) VALUES(:SFID,:SQBM,:INPUTS,:TRADENO,:AMOUNT,:STOREID,:ALIPAYJSON,:FLAG,:T1,:T2)";
		DBAgent.getInstance().executeSQL(sql, condi);

	}

	public void saveTradeQuery(String tradeNo, int flag, String response, long t1, long t2) {
		Map<String, Object> condi = new HashMap<String, Object>();	
		condi.put("TRADENO", tradeNo);
		condi.put("RESPONSE", response);
		condi.put("FLAG", flag);
		condi.put("T1", t1);
		condi.put("T2", t2);

		String sql = "INSERT INTO ALIPAY_TRADEQUERY(TRADENO,RESPONSE, FLAG,T1,T2) VALUES(:TRADENO,:RESPONSE,:FLAG,:T1,:T2)";
		DBAgent.getInstance().executeSQL(sql, condi);

	}

	public void saveTradeRefund(String tradeNo, String refundAmount, String refundReason, String storeId,
			String result, int flag, long t1, long t2) {
		Map<String, Object> condi = new HashMap<String, Object>();		
		condi.put("TRADENO", tradeNo);
		condi.put("AMOUNT", refundAmount);
		condi.put("REASON", refundReason);
		condi.put("STOREID", storeId);
		condi.put("RESULT", result);
		condi.put("FLAG", flag);
		condi.put("T1", t1);
		condi.put("T2", t2);

		String sql = "INSERT INTO ALIPAY_TRADEREFUND(TRADENO,AMOUNT,STOREID,REASON,STOREID,RESULT, FLAG,T1,T2) VALUES(:TRADENO,:AMOUNT,:STOREID,:REASON,:STOREID,:RESULT,:FLAG,:T1,:T2)";
		DBAgent.getInstance().executeSQL(sql, condi);
	}

}
