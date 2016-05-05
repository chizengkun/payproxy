package com.yonyou.proxy.alipay.services;

import javax.jws.WebService;

import com.yonyou.proxy.alipay.factory.AlipayProxyFactory;


@WebService(endpointInterface = "com.yonyou.proxy.alipay.services.AlipayProxyService")
public class AlipayProxyServiceImpl implements AlipayProxyService {

	private AlipayProxyFactory proxyFactory;
	
	
	public AlipayProxyFactory getProxyFactory() {
		return proxyFactory;
	}

   
	public void setProxyFactory(AlipayProxyFactory proxyFactory) {
		this.proxyFactory = proxyFactory;
	}


	public String trade_pay(String param) {
		return null;
	}


	public String trade_query(String param) {
		return null;
	}


	public String trade_refund(String param) {
		return null;
	}



}
