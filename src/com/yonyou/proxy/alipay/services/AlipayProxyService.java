package com.yonyou.proxy.alipay.services;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(targetNamespace = "http://service.alipayproxy.yonyou.com")
public interface AlipayProxyService {

	//֧������
	@WebMethod 
	public String trade_pay(@WebParam(name="data") String param);
	
	//��ѯ֧����Ϣ
	@WebMethod
	public String trade_query(@WebParam(name="data") String param);
	
	//�˷�
	@WebMethod
	public String trade_refund(@WebParam(name="data") String param);
}
