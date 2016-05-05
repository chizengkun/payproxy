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
		// 对于系统商，如果是为了商户开发监控保障接口，则需要传此值，否则如果为系统商自己做交易保障接口开发，则可不传。
        return null;
	}

	@Override
	public AlipayHeartbeatSynContentBuilder getBuilder() {
		// 系统商使用的交易信息格式，json字符串类型，从交易队列中获取
        List<SysTradeInfo> sysTradeInfoList = HbQueue.poll();

        // 异常信息的采集，系统商自行完成
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
                .setProviderId("2088801928381893")  // 设置系统商pid
                .setSysTradeInfoList(sysTradeInfoList)   // 系统商同步trade_info信息
                .setExceptionInfoList(exceptionInfoList)  // 填写异常信息，如果有的话
                ;
        return builder;
	}

}
