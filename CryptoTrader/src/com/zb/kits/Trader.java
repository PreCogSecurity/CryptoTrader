package com.zb.kits;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpException;

import com.alibaba.fastjson.JSON;

public class Trader {
	public String ACCESS_KEY = "70cf93f7-7cc9-466b-94c4-40d477055764";
	public String SECRET_KEY = "e4bd8ca7-0aab-4998-a203-decc7392605a";
	public final String URL_PREFIX = "https://trade.zb.com/api/";
	public static String API_DOMAIN = "http://api.zb.com";
	public static final String ASK = "0";
	public static final String BID = "1";

	public String cancelOrder(String currency, String orderId) {		
		String json = "{}";
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("method", "cancelOrder");
			params.put("id", orderId);
			params.put("currency", currency);

			json = this.getJsonPost(params);
//			System.out.println("testGetOrder 结果: " + json);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return json;
	}
	
	public Map<String, Object> getAccountInfo() {
		Map<String, Object> jsonMap = null; 
		try {
			// 霄1�7加密的请求参敄1�7
			Map<String, String> params = new HashMap<String, String>();
			params.put("method", "getAccountInfo");
			jsonMap = JSON.parseObject(this.getJsonPost(params));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return jsonMap;
	}
	
	private String getJsonPost(Map<String, String> params) {
		params.put("accesskey", ACCESS_KEY);// 这个霄1�7要加入签各1�7,放前靄1�7
		String digest = EncryDigestUtil.digest(SECRET_KEY);

		String sign = EncryDigestUtil.hmacSign(MapSort.toStringMap(params), digest); // 参数执行加密
		String method = params.get("method");

		// 加入验证
		params.put("sign", sign);
		params.put("reqTime", System.currentTimeMillis() + "");
		String url = URL_PREFIX + method + " 参数:" + params;
		System.out.println(url);
		String json = "{}";
		try {
			json = HttpUtilManager.getInstance().requestHttpPost(URL_PREFIX, method, params);
		} catch (Exception e) {
			
		}
		return json;
	}
	
	public String getOrders(String currency,  String tradeType) {
		String json = "{}";
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("method", "getOrders");
			params.put("tradeType", tradeType);
			params.put("currency", currency);
			params.put("pageIndex", "1");

			json = this.getJsonPost(params);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
//		System.out.println(json);
		return json; 
	}
	
	public String order(String market, String tradeType, BigDecimal price, BigDecimal amount) {
		String json = "{}";
		try {
			// 霄1�7加密的请求参数， tradeType=0卖单
			Map<String, String> params = new HashMap<String, String>();
			params.put("method", "order");
			params.put("price", price.toPlainString());
			params.put("amount", amount.toPlainString());
			params.put("tradeType", tradeType);
			params.put("currency", market);

			// 请求测试
			json = this.getJsonPost(params);
//			System.out.println("order return: " + json);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return json;
	}
}
