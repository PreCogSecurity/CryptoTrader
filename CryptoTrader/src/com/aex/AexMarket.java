package com.aex;

import java.io.IOException;
import java.util.Map;

import com.zb.kits.ZBMarket;

import io.vcoins.Market;

public class AexMarket extends Market{
	public static String API_DOMAIN = "http://api.aex.com";
	public static String MK_TYPE = "bitcny";
	
	//aex的currency格式为btc、ltc
	public String ticker(String currency) throws IOException{
		if (currency != null && currency.contains("_")) {
			currency = currency.split("_")[0];
		}
		String callback = "{}";
			// 请求地址
		String url = API_DOMAIN + "/ticker.php?c=" + currency + "&mk_type=" + MK_TYPE;
//		log.info(currency + "-testTicker url: " + url);
		// 请求测试
		callback = get(url, "UTF-8");
//		log.info(currency + "-testTicker 结果: " + callback);
		return callback;
	}

	@Override
	public Map<String, Object> retriveMarketDepth(String market, int depth) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
}
