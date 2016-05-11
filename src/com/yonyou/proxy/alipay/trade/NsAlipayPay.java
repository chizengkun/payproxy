package com.yonyou.proxy.alipay.trade;

import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.service.impl.AlipayMonitorServiceImpl;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.service.impl.AlipayTradeWithHBServiceImpl;
import com.yonyou.proxy.service.DbAlipayIntf;

public class NsAlipayPay extends AlipayBase{

	
	static{
		/** һ��Ҫ�ڴ���AlipayTradeService֮ǰ����Configs.init()����Ĭ�ϲ���
         *  Configs���ȡclasspath�µ�alipayrisk10.properties�ļ�������Ϣ������Ҳ������ļ���ȷ�ϸ��ļ��Ƿ���classpathĿ¼
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
