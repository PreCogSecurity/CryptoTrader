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


/**
 * HUOBI�������ӿڳ���
 * @author sunliancheng687
 *
 */
public class HuobiMarket extends Market{	
	public final static int SUCCESS = 1000;
	//��Կ
	public final String ACCESS_KEY = "07a39fb9-1ebc-4e03-847e-142915db4e6d";
	//˽Կ
	public final String SECRET_KEY = "f75357d4-5e05-49c1-9b25-e26e04b1ece7";
	public final String URL_PREFIX = " https://api.huobi.pro/v1";
	public static String API_DOMAIN = "https://api.huobi.pro/market";
	
	
	/**
	 * ��ȡ�г�����
	 * @param
	 * currency ����
	 * @return
	 * jsonMap
	 */
	public Map<String, Object> ticker(String currency) {
		Map<String, Object> jsonMap = null;
		
		String callback = "{}";
		try {
			String url = API_DOMAIN + "/detail/merged?symbol=" + currency;
			callback = get(url, "UTF-8");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		jsonMap = JSON.parseObject(callback);
		//System.out.println("ticker:" + jsonMap);
		return jsonMap;
	}
	
	/**
	 * ��ȡ�г����
	 * @param
	 * currency ���� 
	 * depth �г��������
	 * @return
	 * jsonMap
	 */
	public Map<String, Object> retriveMarketDepth(String currency, int depth) throws IOException{
		Map<String, Object> jsonMap = null; 

		String url = API_DOMAIN + "/depth?symbol=" + currency + "&type=step2";
		String response = get(url, "UTF-8");
		jsonMap = JSON.parseObject(response, Map.class);
		//System.out.println(jsonMap);
		return jsonMap;
	}
	
	/**
	 * ��ȡ�������б�
	 * 
	 */
	public JSONArray getAsks(String currency, int size) throws IOException{
		Map<String, Object> jsonMap = null; 
		jsonMap = (Map)this.retriveMarketDepth(currency, size).get("tick");
		JSONArray askArray = (JSONArray)jsonMap.get("asks") ;
		System.out.println("ASK:" + askArray);
		return askArray;
	}
	
	/**
	 * ��ȡ������б�
	 * 
	 */
	public JSONArray getBids(String currency, int size) throws IOException{
		Map<String, Object> jsonMap = null; 
		jsonMap = (Map)this.retriveMarketDepth(currency, size).get("tick");
		JSONArray bidArray = (JSONArray)jsonMap.get("bids") ;
		System.out.println("BID:" + bidArray);
		return bidArray;
	}
	
	/**
	 * ��ȡ���������
	 * @param
	 * currency����
	 * minAmount��С�ҵ����������ʵ�ʹҵ�����С�ڸ�ֵ����ȡ��һ��������
	 */
	public Map<String, Object> getBestAsk(String currency, BigDecimal minAmount) throws IOException{		
		Map<String, Object> bestAsk = new HashMap(); 
		BigDecimal askPrice = new BigDecimal(0.0);
		BigDecimal askVol = new BigDecimal(0.0);
		
		JSONArray askArray = (JSONArray) this.getAsks(currency, 5);
		Collections.reverse(askArray);
		
		for(Object item : askArray) {
			Object volObj = ((JSONArray)item).get(1);
			BigDecimal vol = null;
			if(volObj instanceof Integer) {
				vol = new BigDecimal((Integer)volObj);
			} else if(volObj instanceof BigDecimal) {
				vol = (BigDecimal)volObj;
			}
			//�����1������С��minAmount,��ȡ��һ������
			if (vol.compareTo(minAmount) >= 0) {
				askPrice = (BigDecimal) ((JSONArray)item).get(0);
				askVol = vol;
				break;
			} 
		}
		
		bestAsk.put("bestAsk", askPrice.setScale(2,BigDecimal.ROUND_HALF_UP));
		bestAsk.put("askVol", askVol.setScale(4,BigDecimal.ROUND_HALF_UP));
		//System.out.println("BEST ASK:" + bestAsk);
		return bestAsk;
	}
	
	/**
	 * ��ȡ��������
	 * @param
	 * currency����
	 * minAmount��С�ҵ����������ʵ�ʹҵ�����С�ڸ�ֵ����ȡ��һ������
	 */
	public Map<String, Object> getBestBid(String currency, BigDecimal minAmount) throws IOException{
		Map<String, Object> bestBid = new HashMap(); 
		BigDecimal bidPrice = new BigDecimal(0.0);
		BigDecimal bidVol = new BigDecimal(0.0);
		
		JSONArray bidArray = (JSONArray) this.getBids(currency, 5);
		for(Object item : bidArray) {
			Object volObj = ((JSONArray)item).get(1);
			BigDecimal vol = null;
			if(volObj instanceof Integer) {
				vol = new BigDecimal((Integer)volObj);
			} else if(volObj instanceof BigDecimal) {
				vol = (BigDecimal)volObj;
			}
			//�����1������С��minAmount,��ȡ��һ�����
			if (vol.compareTo(minAmount) >= 0) {
				bidPrice = (BigDecimal) ((JSONArray)item).get(0);
				bidVol = vol;
				break;
			} 
		}
		bestBid.put("bestBid", bidPrice.setScale(2,BigDecimal.ROUND_HALF_UP));
		bestBid.put("bidVol", bidVol.setScale(4,BigDecimal.ROUND_HALF_UP));
		//System.out.println("BEST BID:" + bestBid);
		return bestBid;
	}
}
