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


public class KrakenMarketMonitor implements Runnable{
	private int duration;
	private String currency;
	private Market krakenMarket;
	private Map priceMap;
	private Map exchangeRate;
	public static volatile boolean STOP = false;
	private static boolean STARTUP = true;
	private BigDecimalConvertor convertor = new BigDecimalConvertor();
	
	public KrakenMarketMonitor(String currency, int duration,Map priceMap,Map exchangeRate) {
		this.currency = currency;
		this.duration = duration;
		this.priceMap = priceMap;
		this.exchangeRate = exchangeRate;
		krakenMarket = new KrakenMarket();
	}
		

	public void run() {
		for(; !STOP ;) {
			//获取KRAKEN市场最新价		
			Map<String, Object> tickerMap = null;
			try {
				tickerMap = krakenMarket.ticker(currency + "usd");
				Map<String, Object> ticker = (Map<String, Object>)tickerMap.get("result");
				Map<String, Object> result = (Map<String, Object>)ticker.get("X"+ currency + "ZUSD");
				JSONArray array = (JSONArray) result.get("c");
				BigDecimal last = convertor.convert(array.get(0));
				last = last.multiply((BigDecimal) exchangeRate.get("zbusdtbuy")).setScale(2,BigDecimal.ROUND_HALF_UP);
				priceMap.put(currency + ":KRAKEN_USD_LAST" + "", last);
			}
			catch(Exception e) {
				System.out.println(e);
				continue;
			}			

			
			
			//获取KRAKEN最佳买卖价		
			Map<String, Object> bestAsk = null;
			Map<String, Object> bestBid = null;
			
			try{
				bestAsk = krakenMarket.getBestAsk(currency + "usd", new BigDecimal(3));
				bestBid = krakenMarket.getBestBid(currency + "usd", new BigDecimal(3));
				priceMap.put(currency + ":KRAKEN_USD_BESTASK", ((BigDecimal)bestAsk.get("bestAsk")).multiply((BigDecimal) exchangeRate.get("zbusdtbuy")).setScale(2,BigDecimal.ROUND_HALF_UP));
				priceMap.put(currency + ":KRAKEN_USD_BESTBID", ((BigDecimal)bestBid.get("bestBid")).multiply((BigDecimal) exchangeRate.get("zbusdtsell")).setScale(2,BigDecimal.ROUND_HALF_UP));
				priceMap.put(currency + ":KRAKEN_USD_BESTASKVOL", (BigDecimal)bestAsk.get("askVol"));
				priceMap.put(currency + ":KRAKEN_USD_BESTBIDVOL", (BigDecimal)bestBid.get("bidVol"));
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
