package com.yonyou.proxy.alipay.factory;


public interface AlipayProxyFactory {
		String trade_pay(    String param);
		String trade_query(  String param);
		String trade_refund( String param);
}
