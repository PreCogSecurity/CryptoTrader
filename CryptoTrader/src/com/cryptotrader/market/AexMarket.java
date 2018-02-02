package com.cryptotrader.market;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.cryptotrader.util.BigDecimalConvertor;


public class AexMarket extends Market{
	public static String API_DOMAIN = "http://api.aex.com";
	public static String MK_TYPE = null;
	public static String MK_CURRENCY = null;
	private BigDecimalConvertor convertor = new BigDecimalConvertor();
	
	
	/**
	 * ��ȡ�г�����
	 * @param
	 * currency ����
	 * @return
	 * jsonMap
	 */
	public Map<String, Object> ticker(String currency) {
		Map<String, Object> jsonMap = null;
		if (currency != null && currency.contains("_")) {
			MK_CURRENCY = currency.split("_")[0];
			MK_TYPE  = currency.split("_")[1];
		}		
		
		String url = API_DOMAIN + "/ticker.php?c=" + MK_CURRENCY + "&mk_type=" + MK_TYPE;
		//System.out.println(url);
		String callback = "{}";
		callback = get(url, "UTF-8");
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
	public Map<String, Object> retriveMarketDepth(String currency, int depth) throws IOException {
		Map<String, Object> jsonMap = null; 
		if (currency != null && currency.contains("_")) {
			MK_CURRENCY = currency.split("_")[0];
			MK_TYPE  = currency.split("_")[1];
		}
		
		String url = API_DOMAIN + "/depth.php?c=" + MK_CURRENCY + "&mk_type=" + MK_TYPE;
		String response = get(url, "UTF-8");
		jsonMap = JSON.parseObject(response, Map.class);
		//System.out.println(jsonMap);
		return jsonMap;
	}

	/**
	 * ��ȡ�������б�
	 * 
	 */
	public JSONArray getAsks(String currency, int size) throws IOException {
		JSONArray askArray = (JSONArray) this.retriveMarketDepth(currency, size).get("asks");
		//System.out.println("ASK:"+askArray);
		return askArray;
	}

	/**
	 * ��ȡ������б�
	 * 
	 */
	public JSONArray getBids(String currency, int size) throws IOException {
		JSONArray bidArray = (JSONArray) this.retriveMarketDepth(currency, size).get("bids");
		//System.out.println("BID:"+bidArray);
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
		BigDecimal totalVol = new BigDecimal(0.0000);
		for(Object item : askArray) {
			Object volObj = ((JSONArray)item).get(1);
			BigDecimal vol = null;
			vol = convertor.convert(volObj);
			totalVol = totalVol.add(vol).setScale(4,BigDecimal.ROUND_DOWN);
			//�����1������С��minAmount,��ȡ��һ������
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
	 * ��ȡ��������
	 * @param
	 * currency����
	 * minAmount��С�ҵ����������ʵ�ʹҵ�����С�ڸ�ֵ����ȡ��һ������
	 */
	public Map<String, Object> getBestBid(String currency, BigDecimal minAmount) throws IOException{
		Map<String, Object> bestBid = new HashMap(); 
		BigDecimal bidPrice = new BigDecimal(0.0);
		BigDecimal bidVol = new BigDecimal(0.0);
		BigDecimal totalVol = new BigDecimal(0.0000);
		JSONArray bidArray = (JSONArray) this.getBids(currency, 5);
		for(Object item : bidArray) {
			Object volObj = ((JSONArray)item).get(1);
			BigDecimal vol = null;
			vol = convertor.convert(volObj);
			totalVol = totalVol.add(vol).setScale(4,BigDecimal.ROUND_DOWN);
			//�����1������С��minAmount,��ȡ��һ�����
			if (totalVol.compareTo(minAmount) >= 0) {
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
