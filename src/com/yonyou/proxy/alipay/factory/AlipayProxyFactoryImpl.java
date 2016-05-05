package com.yonyou.proxy.alipay.factory;

import java.util.HashMap;
import java.util.Map;

import com.yonyou.proxy.alipay.trade.FyAlipayPay;
import com.yonyou.proxy.alipay.trade.IYonyouPay;
import com.yonyou.proxy.alipay.trade.NsAlipayPay;
import com.yonyou.proxy.alipay.trade.SkAlipayPay;
import com.yonyou.proxy.alipay.trade.XlAlipayPay;
import com.yonyou.proxy.alipay.util.C;
import com.yonyou.proxy.alipay.util.FunctionRet;

public class AlipayProxyFactoryImpl implements AlipayProxyFactory {

	private Map<Integer, IYonyouPay> alipayMap = new HashMap<Integer, IYonyouPay>();

	private Map<String, Integer> skMap = new HashMap<String, Integer>();

	public Map<String, Integer> getSkMap() {
		return skMap;
	}

	public void setSkMap(Map<String, Integer> skMap) {
		this.skMap = skMap;
	}

	private IYonyouPay getPayObject(Integer indx) {
		IYonyouPay p = null;
		switch (indx) {
		case 1:
			p = new NsAlipayPay();
			break;
		case 2:
			p = new SkAlipayPay();
			break;
		case 3:
			p = new XlAlipayPay();
			break;
		case 4:
			p= new FyAlipayPay();
			break;
		}
		return p;
	}

	private IYonyouPay getPayImplement(String sqbm) {
		if (skMap.containsKey(sqbm)) {
			Integer k = skMap.get(sqbm);
			if (alipayMap.containsKey(k)) {
				IYonyouPay pay = alipayMap.get(k);
				if (pay == null) {
					IYonyouPay yp = getPayObject(k);
					alipayMap.put(k, yp);
				}
			}else{
				IYonyouPay yp = getPayObject(k);
				alipayMap.put(k, yp);
			}
			return alipayMap.get(k);
		}

		return null;
	}

	public String trade_pay(String param) {
		String sqbm = "";
		IYonyouPay pay = getPayImplement(sqbm);
		if (pay != null) {
			return pay.trade_pay(param);
		} else {
			// 返回未定义的值
			return FunctionRet.buildFailXml(sqbm+"初始化支付宝信息出错！", C.FAIL_CODE9);
		}
	}

	public String trade_query(String param) {
		String sqbm = "";
		IYonyouPay pay = getPayImplement(sqbm);
		if (pay != null) {
			return pay.trade_query(param);
		} else {
			// 返回未定义的值
			return FunctionRet.buildFailXml(sqbm+"初始化支付宝信息出错！", C.FAIL_CODE9);
		}
	}

	public String trade_refund(String param) {
		String sqbm = "";
		IYonyouPay pay = getPayImplement(sqbm);
		if (pay != null) {
			return pay.trade_refund(param);
		} else {
			// 返回未定义的值
			return FunctionRet.buildFailXml(sqbm+"初始化支付宝信息出错！", C.FAIL_CODE9);
		}
	}

}
