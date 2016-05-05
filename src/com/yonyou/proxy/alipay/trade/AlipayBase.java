package com.yonyou.proxy.alipay.trade;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;

import com.alipay.api.AlipayResponse;
import com.alipay.api.domain.TradeFundBill;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.MonitorHeartbeatSynResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.TradeStatus;
import com.alipay.demo.trade.model.builder.AlipayHeartbeatSynContentBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradePayContentBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradeRefundContentBuilder;
import com.alipay.demo.trade.model.hb.EquipStatus;
import com.alipay.demo.trade.model.hb.ExceptionInfo;
import com.alipay.demo.trade.model.hb.HbStatus;
import com.alipay.demo.trade.model.hb.Product;
import com.alipay.demo.trade.model.hb.SysTradeInfo;
import com.alipay.demo.trade.model.hb.Type;
import com.alipay.demo.trade.model.result.AlipayF2FPayResult;
import com.alipay.demo.trade.model.result.AlipayF2FQueryResult;
import com.alipay.demo.trade.model.result.AlipayF2FRefundResult;
import com.alipay.demo.trade.service.AlipayMonitorService;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayMonitorServiceImpl;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.service.impl.AlipayTradeWithHBServiceImpl;
import com.alipay.demo.trade.utils.Utils;
import com.ufida.hap.util.ConvertUtils;
import com.ufida.hap.util.ParamUtil;
import com.yonyou.proxy.alipay.util.C;
import com.yonyou.proxy.alipay.util.FunctionRet;
import com.yonyou.proxy.alipay.util.ParamsUtil;
import com.yonyou.proxy.service.DbAlipayIntf;

public abstract class AlipayBase implements IYonyouPay {
	protected static Log log = LogFactory.getLog(AlipayBase.class);
	// 支付宝当面付2.0服务
	protected static AlipayTradeService tradeService;

	// 支付宝当面付2.0服务（集成了交易保障接口逻辑）
	protected static AlipayTradeService tradeWithHBService;

	// 支付宝交易保障接口服务
	protected static AlipayMonitorService monitorService;

	protected abstract String getProviderId();

	private DbAlipayIntf dbAlipayIntf;

	public DbAlipayIntf getDbAlipayIntf() {
		return dbAlipayIntf;
	}

	public void setDbAlipayIntf(DbAlipayIntf dbAlipayIntf) {
		this.dbAlipayIntf = dbAlipayIntf;
	}

	public AlipayBase() {

		/**
		 * 使用Configs提供的默认参数 AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
		 */
		tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

		// 支付宝当面付2.0服务（集成了交易保障接口逻辑）
		tradeWithHBService = new AlipayTradeWithHBServiceImpl.ClientBuilder().build();

		/**
		 * 如果需要在程序中覆盖Configs提供的默认参数, 可以使用ClientBuilder类的setXXX方法修改默认参数
		 * 否则使用代码中的默认设置
		 */
		monitorService = new AlipayMonitorServiceImpl.ClientBuilder()
		// .setGatewayUrl("http://localhost:7777/gateway.do")
				.setCharset("GBK").setFormat("json").build();
	}

	public void monitor_sys(String param) {
		// 系统商使用的交易信息格式，json字符串类型
		List<SysTradeInfo> sysTradeInfoList = new ArrayList<SysTradeInfo>();
		sysTradeInfoList.add(SysTradeInfo.newInstance("00000001", 5.2, HbStatus.S));
		sysTradeInfoList.add(SysTradeInfo.newInstance("00000002", 4.4, HbStatus.F));
		sysTradeInfoList.add(SysTradeInfo.newInstance("00000003", 11.3, HbStatus.P));
		sysTradeInfoList.add(SysTradeInfo.newInstance("00000004", 3.2, HbStatus.S));
		sysTradeInfoList.add(SysTradeInfo.newInstance("00000005", 4.1, HbStatus.S));

		// 填写异常信息，如果有的话
		List<ExceptionInfo> exceptionInfoList = new ArrayList<ExceptionInfo>();
		exceptionInfoList.add(ExceptionInfo.HE_SCANER);
		// exceptionInfoList.add(ExceptionInfo.HE_PRINTER);
		// exceptionInfoList.add(ExceptionInfo.HE_OTHER);

		// 填写扩展参数，如果有的话
		Map<String, Object> extendInfo = new HashMap<String, Object>();
		// extendInfo.put("SHOP_ID", "BJ_ZZ_001");
		// extendInfo.put("TERMINAL_ID", "1234");

		String appAuthToken = "";

		AlipayHeartbeatSynContentBuilder builder = new AlipayHeartbeatSynContentBuilder().setProduct(Product.FP)
				.setType(Type.CR).setEquipmentId("cr1000001").setEquipmentStatus(EquipStatus.NORMAL)
				.setTime(Utils.toDate(new Date())).setMac("0a:00:27:00:00:00").setNetworkType("LAN")
				.setProviderId("2088801928381893") // 设置系统商pid
				.setSysTradeInfoList(sysTradeInfoList) // 系统商同步trade_info信息
				// .setExceptionInfoList(exceptionInfoList) // 填写异常信息，如果有的话
				.setExtendInfo(extendInfo) // 填写扩展信息，如果有的话
		;

		MonitorHeartbeatSynResponse response = monitorService.heartbeatSyn(builder, appAuthToken);
		dumpResponse(response);
	}

	protected void dumpResponse(AlipayResponse response) {
		if (response != null) {
			log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
			if (StringUtils.isNotEmpty(response.getSubCode())) {
				log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(), response.getSubMsg()));
			}
			log.info("body:" + response.getBody());
		}
	}

	private String monitor_schedule(Map<String, Object> inputs, String params, String outTradeNo, Long t1) {

		Long sfid = ConvertUtils.toLong(inputs.get("SFID"));
		String sqbm = ConvertUtils.toString(inputs.get("SQBM"));
		// (必填) 订单标题，粗略描述用户的支付目的。如“喜士多（浦东店）消费”
		String subject = "条码支付-诊疗";

		// (必填) 订单总金额，单位为元，不能超过1亿元
		// 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
		String totalAmount = ConvertUtils.toString(inputs.get("AMOUNT"));

		// (必填) 付款条码，用户支付宝钱包手机app点击“付款”产生的付款条码
		String authCode = ConvertUtils.toString(inputs.get("ALIPAYKEY"));

		// (不推荐使用) 订单可打折金额，可以配合商家平台配置折扣活动，如果订单部分商品参与打折，可以将部分商品总价填写至此字段，默认全部商品可打折
		// 如果该值未传入,但传入了【订单总金额】,【不可打折金额】 则该值默认为【订单总金额】- 【不可打折金额】
		// String discountableAmount = "1.00"; //

		// (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
		// 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
		String undiscountableAmount = ConvertUtils.toString(inputs.get("DISCOUNTABLE"), "0.0");
		;

		// 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
		// 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
		String sellerId = "";

		// 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
		String body = String.format("诊疗费用支付宝共需支付%s元", totalAmount);

		// 商户操作员编号，添加此参数可以为商户操作员做销售统计
		String operatorId = ConvertUtils.toString(inputs.get("USERID"));

		// (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
		String storeId = ConvertUtils.toString(inputs.get("STOREID"));

		// 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
		String providerId = getProviderId();
		ExtendParams extendParams = new ExtendParams();
		extendParams.setSysServiceProviderId(providerId);

		// 支付超时，线下扫码交易定义为5分钟
		String timeExpress = "5m";

		// 商品明细列表，需填写购买商品详细信息，
		List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
		// 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
		GoodsDetail goods1 = GoodsDetail.newInstance("clinic_id001", "诊疗费用", ConvertUtils.toLong(totalAmount) * 100, 1);
		// 创建好一个商品后添加至商品明细列表
		goodsDetailList.add(goods1);

		// 创建请求builder，设置请求参数
		AlipayTradePayContentBuilder builder = new AlipayTradePayContentBuilder()
				.setOutTradeNo(outTradeNo)
				.setSubject(subject)
				.setAuthCode(authCode)
				.setTotalAmount(totalAmount)
				.setStoreId(storeId)
				.setUndiscountableAmount(undiscountableAmount)
				.setBody(body)
				.setOperatorId(operatorId)
				.setExtendParams(extendParams)
				.setSellerId(sellerId)
				.setGoodsDetailList(goodsDetailList)
				.setTimeExpress(timeExpress);

		// 调用tradePay方法获取当面付应答
		AlipayF2FPayResult result = tradeWithHBService.tradePay(builder);
		Long t2 = System.currentTimeMillis();
		dbAlipayIntf.saveTradePay(sfid, sqbm, params, outTradeNo,totalAmount, 
					builder.toJsonString(), result.getTradeStatus().ordinal(), t1, t2);
		switch (result.getTradeStatus()) {
		case SUCCESS:
			log.info("支付宝支付成功!");					
			return FunctionRet.buildOpSuccessXml(outTradeNo);
		case FAILED:
			log.error("支付宝支付失败!!!");
			return FunctionRet.buildFailXml(outTradeNo, C.FAIL_CODE1);
		case UNKNOWN:
			log.error("系统异常，订单状态未知!!!");
			return FunctionRet.buildFailXml(outTradeNo, C.FAIL_CODE2);
		default:
			log.error("不支持的交易状态，交易返回异常!!!");
			return FunctionRet.buildFailXml(outTradeNo, C.FAIL_CODE3);
		}
	}

	private int checkPaySucc(String ret) {
		Document doc;
		try {
			doc = DocumentHelper.parseText(ret);

			Element ment = (Element) doc.selectSingleNode(C.CODE_NODE_XPATH);
			if (ment != null) {
				return ConvertUtils.toInteger(ment.getText(), C.FAIL_CODE2);
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return C.FAIL_CODE2;
	}
	
	private String trade_pay_logic(String param){
		YonyouPayHbRunner payRunner = new YonyouPayHbRunner(monitorService);
		payRunner.setDelay(5); // 设置启动后延迟5秒开始调度，不设置则默认3秒
		payRunner.setDuration(10); // 设置间隔10秒进行调度，不设置则默认15 * 60秒
		payRunner.schedule();

		Map<String, Object> inputs = ParamsUtil.toMap(param);
		Long t1 = System.currentTimeMillis();
		Long sfid = ConvertUtils.toLong(inputs.get("SFID"));
		String sqbm = ConvertUtils.toString(inputs.get("SQBM"));
		// (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
		// 需保证商户系统端不能重复，建议通过数据库sequence生成，
		String outTradeNo = String.format("alins_%s_%d_%d_%d", sqbm, sfid, t1, (long) (Math.random() * 100000L));

		String ret = "";
		// 启动当面付，此处每隔5秒调用一次支付接口，并且当随机数为0时交易保障线程退出
		for (int i = 0; i < C.RETRY_LOOP_COUNT; i++) {
			ret = monitor_schedule(inputs, param, outTradeNo, t1);
			switch ( checkPaySucc(ret)) {
			case C.SUCC_CODE:
				break;
			default:
				//交易失败或异常， 查询订单6次，如果返回状态不正常，退结算, 退6次不成功任务
				ret = trade_query_logic( outTradeNo);
				switch ( checkPaySucc( ret)) {
				case C.SUCC_CODE:					
					break;
				default:
					trade_refund_logic(outTradeNo, ConvertUtils.toString(inputs.get("AMOUNT")), "交易查询状态不成功退费", ConvertUtils.toString(inputs.get("STOREID")));
					ret = FunctionRet.buildFailXml("交易失败!", C.FAIL_CODE4);
					break;
				}
				// 查询对应的订单状态，6此，确认了就返回, 状态不清晰就再查询
				break;
			}
			Utils.sleep(C.LOOP_SLEEP_INTERVAL * 1000);
		}

		// 满足退出条件后可以调用shutdown优雅安全退出
		payRunner.shutdown();

		if (ret.equals(""))
			ret = FunctionRet.buildFailXml(String.format("支付交易失败,收费单号%d", sfid), C.FAIL_CODE1);
		return ret;
	}

	public String trade_pay(String param) {
		return trade_pay_logic( param);
	}

	private String query_trade(String outTrade) {
		AlipayF2FQueryResult result = tradeService.queryTradeResult(outTrade);
		switch (result.getTradeStatus()) {
		case SUCCESS:
			log.info("查询返回该订单支付成功: )");
			AlipayTradeQueryResponse response = result.getResponse();
			dumpResponse(response);
			log.info(response.getTradeStatus());
			if (Utils.isListNotEmpty(response.getFundBillList())) {
				for (TradeFundBill bill : response.getFundBillList()) {
					log.info(bill.getFundChannel() + ":" + bill.getAmount());
				}
			}
			return FunctionRet.buildOpSuccessXml(outTrade);
		case FAILED:
			log.error("查询返回该订单支付失败或被关闭!!!");
			return FunctionRet.buildFailXml(outTrade, C.FAIL_CODE1);

		case UNKNOWN:
			log.error("系统异常，订单支付状态未知!!!");
			return FunctionRet.buildFailXml(outTrade, C.FAIL_CODE2);
		default:
			log.error("不支持的交易状态，交易返回异常!!!");
			return FunctionRet.buildFailXml(outTrade, C.FAIL_CODE3);
		}
	}

	private String trade_query_logic(String outTrade) {
		String ret = "";
		for (int k = 0; k < C.RETRY_LOOP_COUNT; k++) {
			ret = query_trade(outTrade);
			if (!ret.equals("")) {
				switch (checkPaySucc(ret)) {
				case C.SUCC_CODE:
					break;
				}
			}
			Utils.sleep(C.LOOP_SLEEP_INTERVAL * 1000);
		}
		if (ret.equals(""))
			ret = FunctionRet.buildFailXml(String.format("查询订单状态失败,订单号%s", outTrade), C.FAIL_CODE1);
		return ret;
	}

	public String trade_query(String param) {
		Map<String, Object> paramMap = ParamsUtil.toMap( param);
		return trade_query_logic( ConvertUtils.toString( paramMap.get("TRADENO")));
	}

	private String refund(String outTradeNo, String refundAmount,String refundReason, String storeId ){
		// (必填) 外部订单号，需要退款交易的商户外部订单号
        // (必填) 退款金额，该金额必须小于等于订单的支付金额，单位为元
        // (可选，需要支持重复退货时必填) 商户退款请求号，相同支付宝交易号下的不同退款请求号对应同一笔交易的不同退款申请，
        // 对于相同支付宝交易号下多笔相同商户退款请求号的退款交易，支付宝只会进行一次退款
        String outRequestNo = "";
        // (必填) 退款原因，可以说明用户退款原因，方便为商家后台提供统计
        // (必填) 商户门店编号，退款情况下可以为商家后台提供退款权限判定和统计等作用，详询支付宝技术支持

        AlipayTradeRefundContentBuilder builder = new AlipayTradeRefundContentBuilder()
                .setOutTradeNo(outTradeNo)
                .setRefundAmount(refundAmount)
                .setRefundReason(refundReason)
                .setOutRequestNo(outRequestNo)
                .setStoreId(storeId);

        AlipayF2FRefundResult result = tradeService.tradeRefund(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝退款成功: )");
                return FunctionRet.buildOpSuccessXml( outTradeNo);
            case FAILED:
                log.error("支付宝退款失败!!!");
                return FunctionRet.buildFailXml(outTradeNo, C.FAIL_CODE1);
            case UNKNOWN:
                log.error("系统异常，订单退款状态未知!!!");
                return FunctionRet.buildFailXml(outTradeNo, C.FAIL_CODE2);
            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                return FunctionRet.buildFailXml(outTradeNo, C.FAIL_CODE3);
        }
	}
	
	private String trade_refund_logic(String outTradeNo, String refundAmount,String refundReason, String storeId ){
		String ret="";
		for(int k=0; k < C.RETRY_LOOP_COUNT; k++){
			ret = refund(outTradeNo, refundAmount, refundReason, storeId);
			switch (checkPaySucc( ret)) {
			case C.SUCC_CODE:				
				break;			
			}
		}
		if (ret.equals(""))
			ret = FunctionRet.buildFailXml(String.format("退费订单失败,订单号%s", outTradeNo), C.FAIL_CODE1);
		return ret;
	}
	public String trade_refund(String param) {
		Map<String, Object> paramMap = ParamsUtil.toMap( param);
		String outTradeNo = ConvertUtils.toString( paramMap.get("TRADENO"));
		String refundAmount = ConvertUtils.toString( paramMap.get("REFUNDAMOUNT"));
		String refundReason = ConvertUtils.toString( paramMap.get("REFUNDREASON"));
		String storeId = ConvertUtils.toString( paramMap.get("STOREID"));
		return trade_refund_logic(outTradeNo, refundAmount, refundReason, storeId);
	}

}
