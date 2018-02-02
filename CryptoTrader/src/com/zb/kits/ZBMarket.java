package com.zb.kits;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import io.vcoins.Market;

public class ZBMarket extends Market{
	public static Map<String, String> MARKET_MAP = new HashMap<String, String>();
	static {
		MARKET_MAP.put("btc_qc","0.01");
		MARKET_MAP.put("bcc_qc","0.01");
		MARKET_MAP.put("ubtc_qc","0.01");
		MARKET_MAP.put("ltc_qc","0.01");
		MARKET_MAP.put("eth_qc","0.01");
		MARKET_MAP.put("etc_qc","0.01");
		MARKET_MAP.put("bts_qc","0.0001");
		MARKET_MAP.put("eos_qc","0.001");
		MARKET_MAP.put("qtum_qc","0.01");
		MARKET_MAP.put("hsr_qc","0.01");
		MARKET_MAP.put("xrp_qc","0.0001");
		MARKET_MAP.put("bcd_qc","0.01");
		MARKET_MAP.put("dash_qc","0.01");
		MARKET_MAP.put("sbtc_qc","0.01");
		MARKET_MAP.put("ink_qc","0.001");
		MARKET_MAP.put("tv_qc","0.001");
		MARKET_MAP.put("bcx_qc","0.0001");
		MARKET_MAP.put("bth_qc","0.01");
		MARKET_MAP.put("lbtc_qc","0.01");
		MARKET_MAP.put("chat_qc","0.001");
		MARKET_MAP.put("hlc_qc","0.001");
	}
	
	public final static int SUCCESS = 1000;
	
	public final String ACCESS_KEY = "29105743-f6b3-47ce-8d10-af815d26c246";
	public final String SECRET_KEY = "326787a5-6826-4722-9035-9ad9e2b58b75";
	public final String URL_PREFIX = "https://trade.zb.com/api/";// 测试环境,测试环境是ttapi测试不通
	public static String API_DOMAIN = "http://api.zb.com";
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> retriveMarketDepth(String market, int depth) throws IOException{
		Map<String, Object> jsonMap = null; 

//		String currency = "ltc_btc";
//		String merge = "0.1";
		String url = API_DOMAIN + "/data/v1/depth?market=" + market + "&size=" + depth;
		String response = get(url, "UTF-8");
		jsonMap = JSON.parseObject(response, Map.class);
//		System.out.println(jsonMap);
		return jsonMap;
	}
	
	public String ticker(String currency) {
		String callback = "{}";
		try {
			// 请求地址
			String url = API_DOMAIN + "/data/v1/ticker?market=" + currency;
//			log.info(currency + "-testTicker url: " + url);
			// 请求测试
			callback = get(url, "UTF-8");
//			log.info(currency + "-testTicker 结果: " + callback);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return callback;
	}
}
