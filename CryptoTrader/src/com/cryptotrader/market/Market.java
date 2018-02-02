package com.cryptotrader.market;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;

public abstract class Market {	
	public final static int SUCCESS = 1000;
	
	public abstract Map<String, Object> ticker(String currency) throws IOException;
	public abstract Map<String, Object> retriveMarketDepth(String currency, int depth) throws IOException;
	public abstract JSONArray  getAsks(String currency, int size) throws IOException;
	public abstract JSONArray  getBids(String currency, int size) throws IOException;
	public abstract Map<String, Object> getBestAsk(String currency,BigDecimal minAmount) throws IOException;
	public abstract Map<String, Object> getBestBid(String currency,BigDecimal minAmount) throws IOException;
	
	protected String get(String urlAll, String charset) {
		BufferedReader reader = null;
		String result = null;
		StringBuffer sbf = new StringBuffer();
		String userAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";// 妯℃嫙娴忚鍣�
		try {
			URL url = new URL(urlAll);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setReadTimeout(30000);
			connection.setConnectTimeout(30000);
			connection.setRequestProperty("User-agent", userAgent);
			connection.connect();
			InputStream is = connection.getInputStream();
			reader = new BufferedReader(new InputStreamReader(is, charset));
			String strRead = null;
			while ((strRead = reader.readLine()) != null) {
				sbf.append(strRead);
				sbf.append("\r\n");
			}
			reader.close();
			result = sbf.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
