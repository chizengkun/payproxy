package com.yonyou.proxy.alipay.trade;

import com.alipay.demo.trade.config.Configs;
import com.yonyou.proxy.service.DbAlipayIntf;

public class NsAlipayPay extends AlipayBase{

	private String pid="";
	
	public NsAlipayPay(DbAlipayIntf dbintf) {
		super( dbintf);
	}

	@Override
	protected String getProviderId() {
		return pid;
	}

	@Override
	protected void loadConfigs() {
		/** һ��Ҫ�ڴ���AlipayTradeService֮ǰ����Configs.init()����Ĭ�ϲ���
         *  Configs���ȡclasspath�µ�alipayrisk10.properties�ļ�������Ϣ������Ҳ������ļ���ȷ�ϸ��ļ��Ƿ���classpathĿ¼
         */
        Configs.init("zfb-ns.properties");
        pid = Configs.getPid();
	}



}
