package com.yonyou.proxy.alipay.trade;

import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.service.impl.AlipayMonitorServiceImpl;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.service.impl.AlipayTradeWithHBServiceImpl;
import com.yonyou.proxy.service.DbAlipayIntf;

public class NsAlipayPay extends AlipayBase{

	
	static{
		/** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的alipayrisk10.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfb-ns.properties");
	}
    	
	public NsAlipayPay(DbAlipayIntf dbAlipayIntf){
		super(dbAlipayIntf);
	}

	@Override
	protected String getProviderId() {
		return Configs.getPid();
	}



}
