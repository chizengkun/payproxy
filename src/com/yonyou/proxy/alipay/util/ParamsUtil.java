package com.yonyou.proxy.alipay.util;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.parser.Parser;
import org.jsoup.parser.XmlTreeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 	WebService参数转换工具
 */
public class ParamsUtil {
	
	private static Logger LOGGER = LoggerFactory.getLogger(ParamsUtil.class);

	/**
	 * 简单参数类型转换
	 * @param xml
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, Object> toMap(String xml) {
		xml =StringEscapeUtils.unescapeHtml(xml); 
		Map<String, Object> paraMap = new HashMap<String, Object>();
		if(xml == null) 
			return paraMap;
		try {
			Document document = Jsoup.parse(new ByteArrayInputStream(xml.trim().getBytes()), "GBK", "", new Parser(new XmlTreeBuilder()));
			Element root = document.select("params").get(0);
			Elements els = root.children();
			for(Element element : els) {
				if(element.children().size() > 0) {
					String key = element.tagName();
					if(!paraMap.containsKey(key)) {
						paraMap.put(key, new ArrayList<Map<Object, Object>>());
					}
					Map<Object, Object> cMap = new HashMap<Object, Object>();
					for(Element ele : element.children()) {
						cMap.put(ele.tagName(), ele.text());
					}
					((List)paraMap.get(key)).add(cMap);
				} else {
					paraMap.put(element.tagName(), element.text());
				}
			}
		} catch (Exception e) {
			LOGGER.error("参数转换出错",e);
		}
		return paraMap;
	}

}
