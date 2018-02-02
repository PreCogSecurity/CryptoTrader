package com.cryptotrader.market;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.cryptotrader.util.BigDecimalConvertor;


/**
 * EXX交易所接口程序
 * @author sunliancheng687
 *
 */
public class ExxMarket extends Market{
	public static Map<String, String> MARKET_MAP = new HashMap<String, String>();
	static {

	}
	
	public final static int SUCCESS = 1000;
	//公钥
	public final String ACCESS_KEY = "";
	//私钥
	public final String SECRET_KEY = "";
	public final String URL_PREFIX = "https://trade.exx.com/api/";
	public static String API_DOMAIN = "https://api.exx.com";
	private BigDecimalConvertor convertor = new BigDecimalConvertor();
	
	
	/**
	 * 获取市场行情
	 * @param
	 * currency 币种
	 * @return
	 * jsonMap
	 */
	public Map<String, Object> ticker(String currency) {
		Map<String, Object> jsonMap = null;
		
		String callback = "{}";
		try {
			String url = API_DOMAIN + "/data/v1/ticker?currency=" + currency;
			System.out.println(url);
			callback = get(url, "UTF-8");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		jsonMap = JSON.parseObject(callback);
		//System.out.println("ticker:" + jsonMap);
		return jsonMap;
	}
	
	/**
	 * 获取市场深度
	 * @param
	 * currency 币种 
	 * depth 市场深度数量
	 * @return
	 * jsonMap
	 */
	public Map<String, Object> retriveMarketDepth(String currency, int depth) throws IOException{
		Map<String, Object> jsonMap = null; 

		String url = API_DOMAIN + "/data/v1/depth?currency=" + currency;
		System.out.println(url);
		String response = get(url, "UTF-8");
		jsonMap = JSON.parseObject(response, Map.class);
		//System.out.println(jsonMap);
		return jsonMap;
	}
	
	/**
	 * 获取卖出价列表
	 * 
	 */
	public JSONArray getAsks(String currency, int size) throws IOException{
		JSONArray askArray = (JSONArray) this.retriveMarketDepth(currency, size).get("asks");
		//System.out.println(askArray);
		return askArray;
	}
	
	/**
	 * 获取买入价列表
	 * 
	 */
	public JSONArray getBids(String currency, int size) throws IOException{
		JSONArray bidArray = (JSONArray) this.retriveMarketDepth(currency, size).get("bids");
		//System.out.println(bidArray);
		return bidArray;
	}

	/**
	 * 获取最佳卖出价
	 * @param
	 * currency币种
	 * minAmount最小挂单数量，如果实际挂单数量小于该值，则取下一个卖出价
	 */
	public Map<String, Object> getBestAsk(String currency, BigDecimal minAmount) throws IOException{		
		Map<String, Object> bestAsk = new HashMap(); 
		BigDecimal askPrice = new BigDecimal(0.0);
		BigDecimal askVol = new BigDecimal(0.0);
		
		JSONArray askArray = (JSONArray) this.getAsks(currency, 5);
		Collections.reverse(askArray);
		BigDecimal totalVol = new BigDecimal(0.0000);
		for(Object item : askArray) {
			Object volObj = ((JSONArray)item).get(1);
			BigDecimal vol = null;
			vol = convertor.convert(volObj);
			totalVol = totalVol.add(vol).setScale(4,BigDecimal.ROUND_DOWN);
			//如果卖1交易量小于minAmount,则取下一个卖价
			if (totalVol.compareTo(minAmount) >= 0) {
				askPrice = convertor.convert(((JSONArray)item).get(0));
				askVol = totalVol;
				break;
			} 
		}
		
		bestAsk.put("bestAsk", askPrice.setScale(2,BigDecimal.ROUND_HALF_UP));
		bestAsk.put("askVol", askVol.setScale(4,BigDecimal.ROUND_HALF_UP));
		//System.out.println("BEST ASK:" + bestAsk);
		return bestAsk;
	}
	
	/**
	 * 获取最佳买入价
	 * @param
	 * currency币种
	 * minAmount最小挂单数量，如果实际挂单数量小于该值，则取下一个买入
	 */
	public Map<String, Object> getBestBid(String currency, BigDecimal minAmount) throws IOException{
		Map<String, Object> bestBid = new HashMap(); 
		BigDecimal bidPrice = new BigDecimal(0.0);
		BigDecimal bidVol = new BigDecimal(0.0);
		
		JSONArray bidArray = (JSONArray) this.getBids(currency, 5);
		BigDecimal totalVol = new BigDecimal(0.0000);
		for(Object item : bidArray) {
			Object volObj = ((JSONArray)item).get(1);
			BigDecimal vol = null;
			vol = convertor.convert(volObj);
			totalVol = totalVol.add(vol).setScale(4,BigDecimal.ROUND_DOWN);
			//如果买1交易量小于minAmount,则取下一个买价
			if (vol.compareTo(minAmount) >= 0) {
				bidPrice = convertor.convert(((JSONArray)item).get(0));
				bidVol = totalVol;
				break;
			} 

		}
		bestBid.put("bestBid", bidPrice.setScale(2,BigDecimal.ROUND_HALF_UP));
		bestBid.put("bidVol", bidVol.setScale(4,BigDecimal.ROUND_HALF_UP));
		//System.out.println("BEST BID:" + bestBid);
		return bestBid;
	}
}
