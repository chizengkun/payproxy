package com.yonyou.proxy.alipay.trade;

public interface IAliPayProxy {

		String trade_pay(   String param);
		String trade_query( String param);
		String trade_refund(String param);
		void monitor_sys( String param);
		
}
