package com.yonyou.proxy.alipay.trade;

import com.alipay.demo.trade.config.Configs;

public class XlAlipayPay extends AlipayBase {

	static{
		/** һ��Ҫ�ڴ���AlipayTradeService֮ǰ����Configs.init()����Ĭ�ϲ���
         *  Configs���ȡclasspath�µ�alipayrisk10.properties�ļ�������Ϣ������Ҳ������ļ���ȷ�ϸ��ļ��Ƿ���classpathĿ¼
         */
        Configs.init("zfb-xl.properties");
	}
	
	@Override
	protected String getProviderId() {
		return Configs.getPid();
	}

}
