package com.yonyou.proxy.alipay.trade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alipay.demo.trade.model.builder.AlipayHeartbeatSynContentBuilder;
import com.alipay.demo.trade.model.hb.EquipStatus;
import com.alipay.demo.trade.model.hb.ExceptionInfo;
import com.alipay.demo.trade.model.hb.Product;
import com.alipay.demo.trade.model.hb.SysTradeInfo;
import com.alipay.demo.trade.model.hb.Type;
import com.alipay.demo.trade.service.AlipayMonitorService;
import com.alipay.demo.trade.service.impl.hb.AbsHbRunner;
import com.alipay.demo.trade.service.impl.hb.HbQueue;
import com.alipay.demo.trade.utils.Utils;

public class YonyouPayHbRunner extends AbsHbRunner {

	public YonyouPayHbRunner(AlipayMonitorService monitorService) {
		super(monitorService);
	}

	@Override
	public String getAppAuthToken() {
		// ����ϵͳ�̣������Ϊ���̻�������ر��Ͻӿڣ�����Ҫ����ֵ���������Ϊϵͳ���Լ������ױ��Ͻӿڿ�������ɲ�����
        return null;
	}

	@Override
	public AlipayHeartbeatSynContentBuilder getBuilder() {
		// ϵͳ��ʹ�õĽ�����Ϣ��ʽ��json�ַ������ͣ��ӽ��׶����л�ȡ
        List<SysTradeInfo> sysTradeInfoList = HbQueue.poll();

        // �쳣��Ϣ�Ĳɼ���ϵͳ���������
        List<ExceptionInfo> exceptionInfoList = new ArrayList<ExceptionInfo>();
//        exceptionInfoList.add(ExceptionInfo.HE_SCANER);
//        exceptionInfoList.add(ExceptionInfo.HE_PRINTER);
//        exceptionInfoList.add(ExceptionInfo.HE_OTHER);

        AlipayHeartbeatSynContentBuilder builder = new AlipayHeartbeatSynContentBuilder()
                .setProduct(Product.FP)
                .setType(Type.CR)
                .setEquipmentId("cr1000001")
                .setEquipmentStatus(EquipStatus.NORMAL)
                .setTime(Utils.toDate(new Date()))
                .setStoreId("store10001")
                .setMac("0a:00:27:00:00:00")
                .setNetworkType("LAN")
                .setProviderId("2088801928381893")  // ����ϵͳ��pid
                .setSysTradeInfoList(sysTradeInfoList)   // ϵͳ��ͬ��trade_info��Ϣ
                .setExceptionInfoList(exceptionInfoList)  // ��д�쳣��Ϣ������еĻ�
                ;
        return builder;
	}

}
