package com.yonyou.proxy.alipay.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.ufida.hap.util.BeanUtils;

/**
 * 方法返回工具
 */
public class FunctionRet {

	private static void AddChildElement(Element element, String key, Object value){
		if(value instanceof List) {
			List list = (List) value;						
			for(Object obj: list){
				Element itemNode = element.addElement( key);
				if (obj instanceof Map){
					Map m = (Map) obj;
					for(Object k: m.keySet()){
						if (m.get(k) instanceof List){
							AddChildElement(itemNode, k.toString(), m.get(k));
						}else{
							itemNode.addElement(String.valueOf(k)).setText(m.get(k) == null? "" : String.valueOf(m.get(k)));
						}
					}
				}else{
					Map m = BeanUtils.toMap(obj, new HashMap(), true);
					for(Object k: m.keySet()) {
						itemNode.addElement(String.valueOf(k)).setText(m.get(k) == null? "" : String.valueOf(m.get(k)));
					}
				}
			}
		}else if (value instanceof Map){
			Map map = (Map)value;
			for(Object k: map.keySet()) {
				Object val = map.get(k);
				AddChildElement(element, k.toString(), val);
			}			
		}else{
			element.addElement(String.valueOf(key)).setText(value == null? "" : String.valueOf(value));
		}
	}
	/**
	 * 构建成功返回xml
	 * @param obj
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String buildSuccessXml(Object obj){
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(C.ROOT_NODE);
		//String vLIST_NODE = "";
		root.addElement(C.CODE_NODE).setText(String.valueOf( C.SUCC_CODE));
		root.addElement(C.MSG_NODE).setText("");
		//vLIST_NODE= LIST_NODE;
		Element resultEle = root.addElement(C.RESULT_NODE);
	
		if(obj instanceof Map){
			Map map = (Map)obj;
			for(Object key: map.keySet()) {
				Object value = map.get(key);
				AddChildElement(resultEle, key.toString(), value);
			}
		} else if(obj instanceof List) {
			List list = (List)obj;
			for(Object object : list) {
				Element itemNode = resultEle.addElement(C.LIST_NODE);
				if(object instanceof Map) {
					Map map = (Map)object;
					for(Object key: map.keySet()) {
						Object value = map.get(key);
						AddChildElement(itemNode, key.toString(), value);
					}
				} else {
					Map map = BeanUtils.toMap(object, new HashMap(), true);
					for(Object key: map.keySet()) {
						itemNode.addElement(String.valueOf(key)).setText(map.get(key) == null? "" : String.valueOf(map.get(key)));
					}
				}
			}
		} else if (obj != null){
			Map map = BeanUtils.toMap(obj, new HashMap(), true);
			for(Object key: map.keySet()) {
				resultEle.addElement(String.valueOf(key)).setText(map.get(key) == null? "" : String.valueOf(map.get(key)));
			}
		}
		return doc.asXML();
	}
	
	/**
	 * 构建无返回值成功xml
	 * @param message
	 * @return
	 */
	public static String buildOpSuccessXml(String message){
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(C.ROOT_NODE);
		root.addElement(C.CODE_NODE).setText(String.valueOf( C.SUCC_CODE));
		root.addElement(C.MSG_NODE).setText(message);
		return doc.asXML();
	}
	
	
	/**
	 * @param message
	 * @param code   0 交易成功！  -1 交易失败     -2 初始化支付宝失败 -3 订单异常 -4 未知的交易
	 * @return
	 */
	public static String buildFailXml(String message, int code) {
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(C.ROOT_NODE);
		root.addElement(C.CODE_NODE).setText( String.valueOf( code));
		root.addElement(C.MSG_NODE).setText(message);
		return doc.asXML();
	}
}
