package io.vcoins;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;

public abstract class Market {
	//以zb币种格式作为基准，其他market实现类进行调整
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
	
	public abstract String ticker(String currency) throws IOException;
	public abstract Map<String, Object> retriveMarketDepth(String market, int depth) throws IOException;
	
	protected String get(String urlAll, String charset) {
		BufferedReader reader = null;
		String result = null;
		StringBuffer sbf = new StringBuffer();
		String userAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";// 模拟浏览器
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
