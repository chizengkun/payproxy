package com.yonyou.proxy.alipay.services;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(targetNamespace = "http://service.alipayproxy.yonyou.com")
public interface AlipayProxyService {

	//支付代码
	@WebMethod 
	public String trade_pay(@WebParam(name="data") String param);
	
	//查询支付信息
	@WebMethod
	public String trade_query(@WebParam(name="data") String param);
	
	//退费
	@WebMethod
	public String trade_refund(@WebParam(name="data") String param);
}
