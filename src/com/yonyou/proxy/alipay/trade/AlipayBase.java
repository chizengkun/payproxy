package com.yonyou.proxy.alipay.trade;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alipay.api.AlipayResponse;
import com.alipay.api.domain.TradeFundBill;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.MonitorHeartbeatSynResponse;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
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
import com.yonyou.proxy.alipay.util.C;
import com.yonyou.proxy.alipay.util.FunctionRet;
import com.yonyou.proxy.alipay.util.ParamsUtil;
import com.yonyou.proxy.service.DbAlipayIntf;

public abstract class AlipayBase implements IAliPayProxy {
	protected static Log log = LogFactory.getLog(AlipayBase.class);
	// ֧�������渶2.0����
	protected  AlipayTradeService tradeService;

	// ֧�������渶2.0���񣨼����˽��ױ��Ͻӿ��߼���
	protected  AlipayTradeService tradeWithHBService;

	// ֧�������ױ��Ͻӿڷ���
	protected  AlipayMonitorService monitorService;

	protected abstract String getProviderId();

	private DbAlipayIntf dbAlipayIntf;

	public AlipayBase(DbAlipayIntf dbAlipayIntf) {		
		this.dbAlipayIntf = dbAlipayIntf;
		
		loadConfigs();
		init();
	}

	
	protected abstract void  loadConfigs();		

	//������������õõ���Ӧ�ĳ�ʼ������
	protected void  init() {
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
				.setProviderId(getProviderId()) // ����ϵͳ��pid
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

	private String do_tray_pay(String params) {

		Map<String, Object> inputs = ParamsUtil.toMap(params);
		Long t1 = System.currentTimeMillis();
		Long sfid = ConvertUtils.toLong(inputs.get("sfid"));
		String sqbm = ConvertUtils.toString(inputs.get("sqbm"));
		// (����) �̻���վ����ϵͳ��Ψһ�����ţ�64���ַ����ڣ�ֻ�ܰ�����ĸ�����֡��»��ߣ�
		// �豣֤�̻�ϵͳ�˲����ظ�������ͨ�����ݿ�sequence���ɣ�
		String outTradeNo = String.format("alins_%s_%d_%d_%d", sqbm, sfid, t1, (long) (Math.random() * 100000L));
		log.debug(outTradeNo);
		// (����) �������⣬���������û���֧��Ŀ�ġ��硰ϲʿ�ࣨ�ֶ��꣩���ѡ�
		String subject = "����֧��-����";

		// (����) �����ܽ���λΪԪ�����ܳ���1��Ԫ
		// ���ͬʱ�����ˡ����۽�,�����ɴ��۽�,�������ܽ�����,�����������������:�������ܽ�=�����۽�+�����ɴ��۽�
		String totalAmount = ConvertUtils.toString(inputs.get("amount"));

		// (����) �������룬�û�֧����Ǯ���ֻ�app�������������ĸ�������
		String authCode = ConvertUtils.toString(inputs.get("alipaykey"));

		// (���Ƽ�ʹ��) �����ɴ��۽���������̼�ƽ̨�����ۿۻ���������������Ʒ������ۣ����Խ�������Ʒ�ܼ���д�����ֶΣ�Ĭ��ȫ����Ʒ�ɴ���
		// �����ֵδ����,�������ˡ������ܽ�,�����ɴ��۽� ���ֵĬ��Ϊ�������ܽ�- �����ɴ��۽�
		// String discountableAmount = "1.00"; //

		// (��ѡ) �������ɴ��۽���������̼�ƽ̨�����ۿۻ�������ˮ��������ۣ��򽫶�Ӧ�����д�����ֶ�
		// �����ֵδ����,�������ˡ������ܽ�,�����۽�,���ֵĬ��Ϊ�������ܽ�-�����۽�
		String undiscountableAmount = ConvertUtils.toString(inputs.get("discountable"), "0.0");

		// ����֧�����˺�ID������֧��һ��ǩԼ�˺���֧�ִ���ͬ���տ��˺ţ�(��sellerId��Ӧ��֧�����˺�)
		// ������ֶ�Ϊ�գ���Ĭ��Ϊ��֧����ǩԼ���̻���PID��Ҳ����appid��Ӧ��PID
		String sellerId = "";

		// �������������ԶԽ��׻���Ʒ����һ����ϸ��������������д"������Ʒ2����15.00Ԫ"
		String body = String.format("���Ʒ���֧��������֧��%sԪ", totalAmount);

		// �̻�����Ա��ţ���Ӵ˲�������Ϊ�̻�����Ա������ͳ��
		String operatorId = ConvertUtils.toString(inputs.get("userid"));

		// (����) �̻��ŵ��ţ�ͨ���ŵ�ź��̼Һ�̨�������þ�׼���ŵ���ۿ���Ϣ����ѯ֧��������֧��
		String storeId = ConvertUtils.toString(inputs.get("storeid"));

		// ҵ����չ������Ŀǰ�������֧���������ϵͳ�̱��(ͨ��setSysServiceProviderId����)����������ѯ֧��������֧��
		String providerId = getProviderId();
		ExtendParams extendParams = new ExtendParams();
		extendParams.setSysServiceProviderId(providerId);

		// ֧����ʱ������ɨ�뽻�׶���Ϊ5����
		String timeExpress = "5m";

		// ��Ʒ��ϸ�б�����д������Ʒ��ϸ��Ϣ��
		List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
		// ����һ����Ʒ��Ϣ����������ֱ�Ϊ��Ʒid��ʹ�ù��꣩�����ơ����ۣ���λΪ�֣��������������Ҫ�����Ʒ������GoodsDetail
		Double price = ConvertUtils.toDouble(totalAmount) * 100;
		GoodsDetail goods1 = GoodsDetail.newInstance("clinic_id001", "���Ʒ���", price.longValue(), 1);
		// ������һ����Ʒ���������Ʒ��ϸ�б�
		goodsDetailList.add(goods1);

		// ��������builder�������������
		AlipayTradePayContentBuilder builder = new AlipayTradePayContentBuilder().setOutTradeNo(outTradeNo)
				.setSubject(subject).setAuthCode(authCode).setTotalAmount(totalAmount).setStoreId(storeId)
				.setUndiscountableAmount(undiscountableAmount).setBody(body).setOperatorId(operatorId)
				.setExtendParams(extendParams).setSellerId(sellerId).setGoodsDetailList(goodsDetailList)
				.setTimeExpress(timeExpress);

		try {
			// ����tradePay������ȡ���渶Ӧ��
			AlipayF2FPayResult result = tradeWithHBService.tradePay(builder);
			Long t2 = System.currentTimeMillis();

			log.info("����ϵͳ��Ψһ������" + outTradeNo);
			log.info("�������" + totalAmount);
			log.info("�̻���" + storeId);
			dbAlipayIntf.saveTradePay(sfid, sqbm, params, authCode, outTradeNo, totalAmount, storeId,
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
		} catch (Exception ex) {
			ex.printStackTrace();
			return FunctionRet.buildFailXml(ex.getMessage(), C.FAIL_CODE4);
		}
	}

	public String trade_pay(String param) {
		return do_tray_pay(param);
	}

	private String do_trade_query(String outTrade, String sqbm) {
		Long t1 = System.currentTimeMillis();
		try {
			AlipayF2FQueryResult result = tradeService.queryTradeResult(outTrade);

			AlipayTradeQueryResponse response = result.getResponse();
			dumpResponse(response);

			dbAlipayIntf.saveTradeQuery(outTrade, result.getTradeStatus().ordinal(),
					response != null ? response.getBody() : "", t1, System.currentTimeMillis(), sqbm);
			switch (result.getTradeStatus()) {
			case SUCCESS:
				log.info("��ѯ���ظö���֧���ɹ�: )");
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
		} catch (Exception ex) {
			ex.printStackTrace();
			return FunctionRet.buildFailXml(ex.getMessage(), C.FAIL_CODE4);
		}
	}

	public String trade_query(String param) {
		Map<String, Object> paramMap = ParamsUtil.toMap(param);
		return do_trade_query(ConvertUtils.toString(paramMap.get("tradeno")), ConvertUtils.toString(paramMap.get("sqbm")));
	}

	private String do_trade_refund(String outTradeNo, String refundAmount, String refundReason, String storeId, String sqbm) {
		// (����) �ⲿ�����ţ���Ҫ�˿�׵��̻��ⲿ������
		// (����) �˿���ý�����С�ڵ��ڶ�����֧������λΪԪ
		// (��ѡ����Ҫ֧���ظ��˻�ʱ����) �̻��˿�����ţ���֧ͬ�������׺��µĲ�ͬ�˿�����Ŷ�Ӧͬһ�ʽ��׵Ĳ�ͬ�˿����룬
		// ������֧ͬ�������׺��¶����ͬ�̻��˿�����ŵ��˿�ף�֧����ֻ�����һ���˿�
		String outRequestNo = "";
		// (����) �˿�ԭ�򣬿���˵���û��˿�ԭ�򣬷���Ϊ�̼Һ�̨�ṩͳ��
		// (����) �̻��ŵ��ţ��˿�����¿���Ϊ�̼Һ�̨�ṩ�˿�Ȩ���ж���ͳ�Ƶ����ã���ѯ֧��������֧��
		Long t1 = System.currentTimeMillis();
		try {
			AlipayTradeRefundContentBuilder builder = new AlipayTradeRefundContentBuilder().setOutTradeNo(outTradeNo)
					.setRefundAmount(refundAmount).setRefundReason(refundReason).setOutRequestNo(outRequestNo)
					.setStoreId(storeId);

			AlipayF2FRefundResult result = tradeService.tradeRefund(builder);
			
			log.info("����ϵͳ��Ψһ������" + outTradeNo);
			log.info("�����˿���" + refundAmount);
			log.info("�̻���" + storeId);
			dbAlipayIntf.saveTradeRefund(outTradeNo, refundAmount, refundReason, storeId, result.getResponse().getBody(), result
					.getTradeStatus().ordinal(), t1, System.currentTimeMillis(), sqbm);
			switch (result.getTradeStatus()) {
			case SUCCESS:
				log.info("֧�����˿�ɹ�: )");
				return FunctionRet.buildOpSuccessXml(outTradeNo);
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
		} catch (Exception ex) {
			ex.printStackTrace();
			return FunctionRet.buildFailXml(ex.getMessage(), C.FAIL_CODE4);
		}
	}

	public String trade_refund(String param) {
		Map<String, Object> paramMap = ParamsUtil.toMap(param);
		String sqbm = ConvertUtils.toString(paramMap.get("sqbm"));
		String outTradeNo = ConvertUtils.toString(paramMap.get("tradeno"));
		String refundAmount = ConvertUtils.toString(paramMap.get("refundamount"));
		String refundReason = ConvertUtils.toString(paramMap.get("refundreason"));
		String storeId = ConvertUtils.toString(paramMap.get("storeid"));
		return do_trade_refund(outTradeNo, refundAmount, refundReason, storeId, sqbm);
	}

}
