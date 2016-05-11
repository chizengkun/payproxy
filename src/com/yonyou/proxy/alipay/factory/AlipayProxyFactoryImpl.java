package com.yonyou.proxy.alipay.factory;

import java.util.HashMap;
import java.util.Map;

import com.ufida.hap.context.util.HapSpringContextUtils;
import com.yonyou.proxy.alipay.trade.FyAlipayPay;
import com.yonyou.proxy.alipay.trade.IYonyouPay;
import com.yonyou.proxy.alipay.trade.NsAlipayPay;
import com.yonyou.proxy.alipay.trade.SkAlipayPay;
import com.yonyou.proxy.alipay.trade.XlAlipayPay;
import com.yonyou.proxy.alipay.util.C;
import com.yonyou.proxy.alipay.util.FunctionRet;
import com.yonyou.proxy.alipay.util.ParamsUtil;
import com.yonyou.proxy.service.DbAlipayIntf;

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
		DbAlipayIntf dbintf = HapSpringContextUtils.getBean( DbAlipayIntf.class);
		switch (indx) {
		case 1:
			p = new NsAlipayPay(dbintf);
			break;
		case 2:
			p = new SkAlipayPay(dbintf);
			break;
		case 3:
			p = new XlAlipayPay(dbintf);
			break;
		case 4:
			p= new FyAlipayPay(dbintf);
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
		Map<String,Object> m = ParamsUtil.toMap(param);
		String sqbm= m.get("sqbm").toString();
		IYonyouPay pay = getPayImplement(sqbm);
		if (pay != null) {
			return pay.trade_pay(param);
		} else {
			// 返回未定义的值
			return FunctionRet.buildFailXml(sqbm+"初始化支付宝信息出错！", C.FAIL_CODE9);
		}
	}

	public String trade_query(String param) {
		Map<String,Object> m = ParamsUtil.toMap(param);
		String sqbm= m.get("sqbm").toString();
		IYonyouPay pay = getPayImplement(sqbm);
		if (pay != null) {
			return pay.trade_query(param);
		} else {
			// 返回未定义的值
			return FunctionRet.buildFailXml(sqbm+"初始化支付宝信息出错！", C.FAIL_CODE9);
		}
	}

	public String trade_refund(String param) {
		Map<String,Object> m = ParamsUtil.toMap(param);
		String sqbm= m.get("sqbm").toString();
		IYonyouPay pay = getPayImplement(sqbm);
		if (pay != null) {
			return pay.trade_refund(param);
		} else {
			// 返回未定义的值
			return FunctionRet.buildFailXml(sqbm+"初始化支付宝信息出错！", C.FAIL_CODE9);
		}
	}

}
