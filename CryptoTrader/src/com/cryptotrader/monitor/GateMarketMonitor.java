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


public class GateMarketMonitor implements Runnable{
	private int duration;
	private String currency;
	private Market gateMarket;
	private Map priceMap;
	private Map exchangeRate;
	public static volatile boolean STOP = false;
	private static boolean STARTUP = true;
	private BigDecimalConvertor convertor = new BigDecimalConvertor();
	
	public GateMarketMonitor(String currency, int duration,Map priceMap,Map exchangeRate) {
		this.currency = currency;
		this.duration = duration;
		this.priceMap = priceMap;
		this.exchangeRate = exchangeRate;
		gateMarket = new GateMarket();
	}
		

	public void run() {
		for(; !STOP ;) {
			//获取GATE市场最新价		
			Map<String, Object> tickerMap = null;

			try {
				tickerMap = gateMarket.ticker(currency + "_usdt");
				//Map<String, Object> ticker = (Map<String, Object>)tickerMap.get("ticker");
				Object lastObj = tickerMap.get("last");
				BigDecimal last = null;
				last = convertor.convert(lastObj);
				last = last.multiply((BigDecimal) exchangeRate.get("usdtbuy")).setScale(2,BigDecimal.ROUND_HALF_UP);
				priceMap.put(currency + ":GATE_USDT_LAST" + "", last);
			}
			catch(Exception e) {
				System.out.println(e);
				continue;
			}
			
			
			//获取GATE最佳买卖价		
			Map<String, Object> bestAsk = null;
			Map<String, Object> bestBid = null;

			try{
				bestAsk = gateMarket.getBestAsk(currency + "_usdt", new BigDecimal(3));
				bestBid = gateMarket.getBestBid(currency + "_usdt", new BigDecimal(3));
				
				priceMap.put(currency + ":GATE_USDT_BESTASK", ((BigDecimal)bestAsk.get("bestAsk")).multiply((BigDecimal) exchangeRate.get("usdtbuy")).setScale(2,BigDecimal.ROUND_HALF_UP));
				priceMap.put(currency + ":GATE_USDT_BESTBID", ((BigDecimal)bestBid.get("bestBid")).multiply((BigDecimal) exchangeRate.get("usdtsell")).setScale(2,BigDecimal.ROUND_HALF_UP));
				priceMap.put(currency + ":GATE_USDT_BESTASKVOL", (BigDecimal)bestAsk.get("askVol"));
				priceMap.put(currency + ":GATE_USDT_BESTBIDVOL", (BigDecimal)bestBid.get("bidVol"));
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
				System.out.println("GATE市场监控器启动成功！");
				STARTUP = false;
			}
			

			//System.out.println("USDT_LAST:" + priceMap.get(currency + ":GATE_USDT_LAST"));
			//System.out.println("USDT_BESTASK:" + priceMap.get(currency + ":GATE_USDT_BESTASK"));
			//System.out.println("USDT_BESTBID:" + priceMap.get(currency + ":GATE_USDT_BESTBID"));
			
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
