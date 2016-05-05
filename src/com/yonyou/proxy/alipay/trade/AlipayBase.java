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
	// ֧�������渶2.0����
	protected static AlipayTradeService tradeService;

	// ֧�������渶2.0���񣨼����˽��ױ��Ͻӿ��߼���
	protected static AlipayTradeService tradeWithHBService;

	// ֧�������ױ��Ͻӿڷ���
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
		 * ʹ��Configs�ṩ��Ĭ�ϲ��� AlipayTradeService����ʹ�õ�������Ϊ��̬��Ա���󣬲���Ҫ����new
		 */
		tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

		// ֧�������渶2.0���񣨼����˽��ױ��Ͻӿ��߼���
		tradeWithHBService = new AlipayTradeWithHBServiceImpl.ClientBuilder().build();

		/**
		 * �����Ҫ�ڳ����и���Configs�ṩ��Ĭ�ϲ���, ����ʹ��ClientBuilder���setXXX�����޸�Ĭ�ϲ���
		 * ����ʹ�ô����е�Ĭ������
		 */
		monitorService = new AlipayMonitorServiceImpl.ClientBuilder()
		// .setGatewayUrl("http://localhost:7777/gateway.do")
				.setCharset("GBK").setFormat("json").build();
	}

	public void monitor_sys(String param) {
		// ϵͳ��ʹ�õĽ�����Ϣ��ʽ��json�ַ�������
		List<SysTradeInfo> sysTradeInfoList = new ArrayList<SysTradeInfo>();
		sysTradeInfoList.add(SysTradeInfo.newInstance("00000001", 5.2, HbStatus.S));
		sysTradeInfoList.add(SysTradeInfo.newInstance("00000002", 4.4, HbStatus.F));
		sysTradeInfoList.add(SysTradeInfo.newInstance("00000003", 11.3, HbStatus.P));
		sysTradeInfoList.add(SysTradeInfo.newInstance("00000004", 3.2, HbStatus.S));
		sysTradeInfoList.add(SysTradeInfo.newInstance("00000005", 4.1, HbStatus.S));

		// ��д�쳣��Ϣ������еĻ�
		List<ExceptionInfo> exceptionInfoList = new ArrayList<ExceptionInfo>();
		exceptionInfoList.add(ExceptionInfo.HE_SCANER);
		// exceptionInfoList.add(ExceptionInfo.HE_PRINTER);
		// exceptionInfoList.add(ExceptionInfo.HE_OTHER);

		// ��д��չ����������еĻ�
		Map<String, Object> extendInfo = new HashMap<String, Object>();
		// extendInfo.put("SHOP_ID", "BJ_ZZ_001");
		// extendInfo.put("TERMINAL_ID", "1234");

		String appAuthToken = "";

		AlipayHeartbeatSynContentBuilder builder = new AlipayHeartbeatSynContentBuilder().setProduct(Product.FP)
				.setType(Type.CR).setEquipmentId("cr1000001").setEquipmentStatus(EquipStatus.NORMAL)
				.setTime(Utils.toDate(new Date())).setMac("0a:00:27:00:00:00").setNetworkType("LAN")
				.setProviderId("2088801928381893") // ����ϵͳ��pid
				.setSysTradeInfoList(sysTradeInfoList) // ϵͳ��ͬ��trade_info��Ϣ
				// .setExceptionInfoList(exceptionInfoList) // ��д�쳣��Ϣ������еĻ�
				.setExtendInfo(extendInfo) // ��д��չ��Ϣ������еĻ�
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
		// (����) �������⣬���������û���֧��Ŀ�ġ��硰ϲʿ�ࣨ�ֶ��꣩���ѡ�
		String subject = "����֧��-����";

		// (����) �����ܽ���λΪԪ�����ܳ���1��Ԫ
		// ���ͬʱ�����ˡ����۽�,�����ɴ��۽�,�������ܽ�����,�����������������:�������ܽ�=�����۽�+�����ɴ��۽�
		String totalAmount = ConvertUtils.toString(inputs.get("AMOUNT"));

		// (����) �������룬�û�֧����Ǯ���ֻ�app�������������ĸ�������
		String authCode = ConvertUtils.toString(inputs.get("ALIPAYKEY"));

		// (���Ƽ�ʹ��) �����ɴ��۽���������̼�ƽ̨�����ۿۻ���������������Ʒ������ۣ����Խ�������Ʒ�ܼ���д�����ֶΣ�Ĭ��ȫ����Ʒ�ɴ���
		// �����ֵδ����,�������ˡ������ܽ�,�����ɴ��۽� ���ֵĬ��Ϊ�������ܽ�- �����ɴ��۽�
		// String discountableAmount = "1.00"; //

		// (��ѡ) �������ɴ��۽���������̼�ƽ̨�����ۿۻ�������ˮ��������ۣ��򽫶�Ӧ�����д�����ֶ�
		// �����ֵδ����,�������ˡ������ܽ�,�����۽�,���ֵĬ��Ϊ�������ܽ�-�����۽�
		String undiscountableAmount = ConvertUtils.toString(inputs.get("DISCOUNTABLE"), "0.0");
		;

		// ����֧�����˺�ID������֧��һ��ǩԼ�˺���֧�ִ���ͬ���տ��˺ţ�(��sellerId��Ӧ��֧�����˺�)
		// ������ֶ�Ϊ�գ���Ĭ��Ϊ��֧����ǩԼ���̻���PID��Ҳ����appid��Ӧ��PID
		String sellerId = "";

		// �������������ԶԽ��׻���Ʒ����һ����ϸ��������������д"������Ʒ2����15.00Ԫ"
		String body = String.format("���Ʒ���֧��������֧��%sԪ", totalAmount);

		// �̻�����Ա��ţ���Ӵ˲�������Ϊ�̻�����Ա������ͳ��
		String operatorId = ConvertUtils.toString(inputs.get("USERID"));

		// (����) �̻��ŵ��ţ�ͨ���ŵ�ź��̼Һ�̨�������þ�׼���ŵ���ۿ���Ϣ����ѯ֧��������֧��
		String storeId = ConvertUtils.toString(inputs.get("STOREID"));

		// ҵ����չ������Ŀǰ�������֧���������ϵͳ�̱��(ͨ��setSysServiceProviderId����)����������ѯ֧��������֧��
		String providerId = getProviderId();
		ExtendParams extendParams = new ExtendParams();
		extendParams.setSysServiceProviderId(providerId);

		// ֧����ʱ������ɨ�뽻�׶���Ϊ5����
		String timeExpress = "5m";

		// ��Ʒ��ϸ�б�����д������Ʒ��ϸ��Ϣ��
		List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
		// ����һ����Ʒ��Ϣ����������ֱ�Ϊ��Ʒid��ʹ�ù��꣩�����ơ����ۣ���λΪ�֣��������������Ҫ�����Ʒ������GoodsDetail
		GoodsDetail goods1 = GoodsDetail.newInstance("clinic_id001", "���Ʒ���", ConvertUtils.toLong(totalAmount) * 100, 1);
		// ������һ����Ʒ���������Ʒ��ϸ�б�
		goodsDetailList.add(goods1);

		// ��������builder�������������
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

		// ����tradePay������ȡ���渶Ӧ��
		AlipayF2FPayResult result = tradeWithHBService.tradePay(builder);
		Long t2 = System.currentTimeMillis();
		dbAlipayIntf.saveTradePay(sfid, sqbm, params, outTradeNo,totalAmount, 
					builder.toJsonString(), result.getTradeStatus().ordinal(), t1, t2);
		switch (result.getTradeStatus()) {
		case SUCCESS:
			log.info("֧����֧���ɹ�!");					
			return FunctionRet.buildOpSuccessXml(outTradeNo);
		case FAILED:
			log.error("֧����֧��ʧ��!!!");
			return FunctionRet.buildFailXml(outTradeNo, C.FAIL_CODE1);
		case UNKNOWN:
			log.error("ϵͳ�쳣������״̬δ֪!!!");
			return FunctionRet.buildFailXml(outTradeNo, C.FAIL_CODE2);
		default:
			log.error("��֧�ֵĽ���״̬�����׷����쳣!!!");
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
		payRunner.setDelay(5); // �����������ӳ�5�뿪ʼ���ȣ���������Ĭ��3��
		payRunner.setDuration(10); // ���ü��10����е��ȣ���������Ĭ��15 * 60��
		payRunner.schedule();

		Map<String, Object> inputs = ParamsUtil.toMap(param);
		Long t1 = System.currentTimeMillis();
		Long sfid = ConvertUtils.toLong(inputs.get("SFID"));
		String sqbm = ConvertUtils.toString(inputs.get("SQBM"));
		// (����) �̻���վ����ϵͳ��Ψһ�����ţ�64���ַ����ڣ�ֻ�ܰ�����ĸ�����֡��»��ߣ�
		// �豣֤�̻�ϵͳ�˲����ظ�������ͨ�����ݿ�sequence���ɣ�
		String outTradeNo = String.format("alins_%s_%d_%d_%d", sqbm, sfid, t1, (long) (Math.random() * 100000L));

		String ret = "";
		// �������渶���˴�ÿ��5�����һ��֧���ӿڣ����ҵ������Ϊ0ʱ���ױ����߳��˳�
		for (int i = 0; i < C.RETRY_LOOP_COUNT; i++) {
			ret = monitor_schedule(inputs, param, outTradeNo, t1);
			switch ( checkPaySucc(ret)) {
			case C.SUCC_CODE:
				break;
			default:
				//����ʧ�ܻ��쳣�� ��ѯ����6�Σ��������״̬���������˽���, ��6�β��ɹ�����
				ret = trade_query_logic( outTradeNo);
				switch ( checkPaySucc( ret)) {
				case C.SUCC_CODE:					
					break;
				default:
					trade_refund_logic(outTradeNo, ConvertUtils.toString(inputs.get("AMOUNT")), "���ײ�ѯ״̬���ɹ��˷�", ConvertUtils.toString(inputs.get("STOREID")));
					ret = FunctionRet.buildFailXml("����ʧ��!", C.FAIL_CODE4);
					break;
				}
				// ��ѯ��Ӧ�Ķ���״̬��6�ˣ�ȷ���˾ͷ���, ״̬���������ٲ�ѯ
				break;
			}
			Utils.sleep(C.LOOP_SLEEP_INTERVAL * 1000);
		}

		// �����˳���������Ե���shutdown���Ű�ȫ�˳�
		payRunner.shutdown();

		if (ret.equals(""))
			ret = FunctionRet.buildFailXml(String.format("֧������ʧ��,�շѵ���%d", sfid), C.FAIL_CODE1);
		return ret;
	}

	public String trade_pay(String param) {
		return trade_pay_logic( param);
	}

	private String query_trade(String outTrade) {
		AlipayF2FQueryResult result = tradeService.queryTradeResult(outTrade);
		switch (result.getTradeStatus()) {
		case SUCCESS:
			log.info("��ѯ���ظö���֧���ɹ�: )");
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
			log.error("��ѯ���ظö���֧��ʧ�ܻ򱻹ر�!!!");
			return FunctionRet.buildFailXml(outTrade, C.FAIL_CODE1);

		case UNKNOWN:
			log.error("ϵͳ�쳣������֧��״̬δ֪!!!");
			return FunctionRet.buildFailXml(outTrade, C.FAIL_CODE2);
		default:
			log.error("��֧�ֵĽ���״̬�����׷����쳣!!!");
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
			ret = FunctionRet.buildFailXml(String.format("��ѯ����״̬ʧ��,������%s", outTrade), C.FAIL_CODE1);
		return ret;
	}

	public String trade_query(String param) {
		Map<String, Object> paramMap = ParamsUtil.toMap( param);
		return trade_query_logic( ConvertUtils.toString( paramMap.get("TRADENO")));
	}

	private String refund(String outTradeNo, String refundAmount,String refundReason, String storeId ){
		// (����) �ⲿ�����ţ���Ҫ�˿�׵��̻��ⲿ������
        // (����) �˿���ý�����С�ڵ��ڶ�����֧������λΪԪ
        // (��ѡ����Ҫ֧���ظ��˻�ʱ����) �̻��˿�����ţ���֧ͬ�������׺��µĲ�ͬ�˿�����Ŷ�Ӧͬһ�ʽ��׵Ĳ�ͬ�˿����룬
        // ������֧ͬ�������׺��¶����ͬ�̻��˿�����ŵ��˿�ף�֧����ֻ�����һ���˿�
        String outRequestNo = "";
        // (����) �˿�ԭ�򣬿���˵���û��˿�ԭ�򣬷���Ϊ�̼Һ�̨�ṩͳ��
        // (����) �̻��ŵ��ţ��˿�����¿���Ϊ�̼Һ�̨�ṩ�˿�Ȩ���ж���ͳ�Ƶ����ã���ѯ֧��������֧��

        AlipayTradeRefundContentBuilder builder = new AlipayTradeRefundContentBuilder()
                .setOutTradeNo(outTradeNo)
                .setRefundAmount(refundAmount)
                .setRefundReason(refundReason)
                .setOutRequestNo(outRequestNo)
                .setStoreId(storeId);

        AlipayF2FRefundResult result = tradeService.tradeRefund(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("֧�����˿�ɹ�: )");
                return FunctionRet.buildOpSuccessXml( outTradeNo);
            case FAILED:
                log.error("֧�����˿�ʧ��!!!");
                return FunctionRet.buildFailXml(outTradeNo, C.FAIL_CODE1);
            case UNKNOWN:
                log.error("ϵͳ�쳣�������˿�״̬δ֪!!!");
                return FunctionRet.buildFailXml(outTradeNo, C.FAIL_CODE2);
            default:
                log.error("��֧�ֵĽ���״̬�����׷����쳣!!!");
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
			ret = FunctionRet.buildFailXml(String.format("�˷Ѷ���ʧ��,������%s", outTradeNo), C.FAIL_CODE1);
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
