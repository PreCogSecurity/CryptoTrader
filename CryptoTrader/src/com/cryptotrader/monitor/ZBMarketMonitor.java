package com.cryptotrader.monitor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.cryptotrader.market.*;
import com.cryptotrader.util.BigDecimalConvertor;


public class ZBMarketMonitor implements Runnable{
	private int duration;
	private String currency;
	private Market zbMarket;
	private Map priceMap;
	private Map exchangeRate;
	public static volatile boolean STOP = false;
	private static boolean STARTUP = true;
	private BigDecimalConvertor convertor = new BigDecimalConvertor();
	
	public ZBMarketMonitor(String currency, int duration,Map priceMap,Map exchangeRate) {
		this.currency = currency;
		this.duration = duration;
		this.priceMap = priceMap;
		this.exchangeRate = exchangeRate;
		zbMarket = new ZBMarket();
	}
		

	public void run() {
		for(; !STOP ;) {
			//获取ZB市场最新价		
			Map<String, Object> zbTickerMap = null;
			try {
				zbTickerMap = zbMarket.ticker(currency + "_qc");
				Map<String, Object> zbTicker = (Map<String, Object>)zbTickerMap.get("ticker");
				BigDecimal last = convertor.convert(zbTicker.get("last"));
				priceMap.put(currency + ":ZB_QC_LAST" + "", last);
			}
			catch(Exception e) {
				System.out.println(e);
				continue;
			}			

			try {
				zbTickerMap = zbMarket.ticker(currency + "_usdt");
				Map<String, Object> zbTicker = (Map<String, Object>)zbTickerMap.get("ticker");
				BigDecimal last = convertor.convert(zbTicker.get("last"));
				last = last.multiply((BigDecimal) exchangeRate.get("zbusdtbuy")).setScale(2,BigDecimal.ROUND_HALF_UP);
				priceMap.put(currency + ":ZB_USDT_LAST" + "", last);
			}
			catch(Exception e) {
				System.out.println(e);
				continue;
			}
			
			
			//获取ZB最佳买卖价		
			Map<String, Object> bestAsk = null;
			Map<String, Object> bestBid = null;
			try{
				bestAsk = zbMarket.getBestAsk(currency + "_qc", new BigDecimal(3));
				bestBid = zbMarket.getBestBid(currency + "_qc", new BigDecimal(3));
				priceMap.put(currency + ":ZB_QC_BESTASK", (BigDecimal)bestAsk.get("bestAsk"));
				priceMap.put(currency + ":ZB_QC_BESTBID", (BigDecimal)bestBid.get("bestBid"));
				priceMap.put(currency + ":ZB_QC_BESTASKVOL", (BigDecimal)bestAsk.get("askVol"));
				priceMap.put(currency + ":ZB_QC_BESTBIDVOL", (BigDecimal)bestBid.get("bidVol"));
			}catch(Exception e) {
				System.out.println(e);
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					System.out.println(e);
				}
				continue;
			}	
			
			try{
				bestAsk = zbMarket.getBestAsk(currency + "_usdt", new BigDecimal(3));
				bestBid = zbMarket.getBestBid(currency + "_usdt", new BigDecimal(3));
				priceMap.put(currency + ":ZB_USDT_BESTASK", ((BigDecimal)bestAsk.get("bestAsk")).multiply((BigDecimal) exchangeRate.get("zbusdtbuy")).setScale(2,BigDecimal.ROUND_HALF_UP));
				priceMap.put(currency + ":ZB_USDT_BESTBID", ((BigDecimal)bestBid.get("bestBid")).multiply((BigDecimal) exchangeRate.get("zbusdtsell")).setScale(2,BigDecimal.ROUND_HALF_UP));
				priceMap.put(currency + ":ZB_USDT_BESTASKVOL", (BigDecimal)bestAsk.get("askVol"));
				priceMap.put(currency + ":ZB_USDT_BESTBIDVOL", (BigDecimal)bestBid.get("bidVol"));
			}catch(Exception e) {
				System.out.println(e);
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					System.out.println(e);
				}
				continue;
			}	

			
			//如果是初次启动成功，则给出提示
			if(STARTUP){
				System.out.println("ZB市场监控器启动成功！");
				STARTUP = false;
			}
			
			//System.out.println("QC_LAST:" + priceMap.get(currency + ":ZB_QC_LAST"));
			//System.out.println("QC_BESTASK:" + priceMap.get(currency + ":ZB_QC_BESTASK"));
			//System.out.println("QC_BESTBID:" + priceMap.get(currency + ":ZB_QC_BESTBID"));
			//System.out.println("QC_BESTASKVOL:" + priceMap.get(currency + ":ZB_QC_BESTASKVOL"));
			//System.out.println("QC_BESTBIDVOL:" + priceMap.get(currency + ":ZB_QC_BESTBIDVOL"));
			//System.out.println("USDT_LAST:" + priceMap.get(currency + ":ZB_USDT_LAST"));
			//System.out.println("USDT_BESTASK:" + priceMap.get(currency + ":ZB_USDT_BESTASK"));
			//System.out.println("USDT_BESTBID:" + priceMap.get(currency + ":ZB_USDT_BESTBID"));
			
			try {
				//每次间隔时间随机，避免所有线程集中请求
				java.util.Random random = new java.util.Random();
				Thread.sleep(duration+ random.nextInt(duration));
			} catch (InterruptedException e) {

				System.out.println(e);
			}
			
		}
		
	}

}
