package com.yonyou.proxy.alipay.trade;

import com.alipay.demo.trade.config.Configs;
import com.yonyou.proxy.service.DbAlipayIntf;

public class SkAlipayPay extends AlipayBase {

	public SkAlipayPay(DbAlipayIntf dbAlipayIntf) {
		super(dbAlipayIntf);
	}

	private String pid="";
	@Override
	protected String getProviderId() {		
		return pid;
	}

	@Override
	protected void loadConfigs() {

		/** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的alipayrisk10.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfb-sk.properties");
        
        pid = Configs.getPid();		
	}

}
