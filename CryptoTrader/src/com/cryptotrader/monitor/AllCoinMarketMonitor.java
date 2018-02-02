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


public class AllCoinMarketMonitor implements Runnable{
	private int duration;
	private String currency;
	private Market allcoinMarket;
	private Map priceMap;
	private Map exchangeRate;
	public static volatile boolean STOP = false;
	private static boolean STARTUP = true;
	private BigDecimalConvertor convertor = new BigDecimalConvertor();
	
	public AllCoinMarketMonitor(String currency, int duration,Map priceMap,Map exchangeRate) {
		this.currency = currency;
		this.duration = duration;
		this.priceMap = priceMap;
		this.exchangeRate = exchangeRate;
		allcoinMarket = new AllCoinMarket();
	}
		

	public void run() {
		for(; !STOP ;) {
			//获取ALLCOIN市场最新价		
			Map<String, Object> tickerMap = null;
			try {
				tickerMap = allcoinMarket.ticker(currency + "_usd");
				Map<String, Object> ticker = (Map<String, Object>)tickerMap.get("ticker");
				BigDecimal last = convertor.convert(ticker.get("last")).multiply((BigDecimal) exchangeRate.get("ckusdbuy")).setScale(2,BigDecimal.ROUND_HALF_UP);
				priceMap.put(currency + ":ALLCOIN_CKUSD_LAST" + "", last);
			}
			catch(Exception e) {
				System.out.println(e);
				continue;
			}			
			
			//获取ALLCOIN最佳买卖价		
			Map<String, Object> bestAsk = null;
			Map<String, Object> bestBid = null;

			
			try{
				bestAsk = allcoinMarket.getBestAsk(currency + "_ck.usd", new BigDecimal(3));
				bestBid = allcoinMarket.getBestBid(currency + "_ck.usd", new BigDecimal(3));
				priceMap.put(currency + ":ALLCOIN_CKUSD_BESTASK", ((BigDecimal)bestAsk.get("bestAsk")).multiply((BigDecimal) exchangeRate.get("ckusdbuy")).setScale(2,BigDecimal.ROUND_HALF_UP));
				priceMap.put(currency + ":ALLCOIN_CKUSD_BESTBID", ((BigDecimal)bestBid.get("bestBid")).multiply((BigDecimal) exchangeRate.get("ckusdsell")).setScale(2,BigDecimal.ROUND_HALF_UP));
				priceMap.put(currency + ":ALLCOIN_CKUSD_BESTASKVOL", (BigDecimal)bestAsk.get("askVol"));
				priceMap.put(currency + ":ALLCOIN_CKUSD_BESTBIDVOL", (BigDecimal)bestBid.get("bidVol"));
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
				System.out.println("ALLCOIN市场监控器启动成功！");
				STARTUP = false;
			}
			
			System.out.println("CKUSD_LAST:" + priceMap.get(currency + ":BCEX_CKUSD_LAST"));
			System.out.println("CKUSD_BESTASK:" + priceMap.get(currency + ":BCEX_CKUSD_BESTASK"));
			System.out.println("CKUSD_BESTBID:" + priceMap.get(currency + ":BCEX_CKUSD_BESTBID"));
			System.out.println("CKUSD_BESTASKVOL:" + priceMap.get(currency + ":BCEX_CKUSD_BESTASKVOL"));
			System.out.println("CKUSD_BESTBIDVOL:" + priceMap.get(currency + ":BCEX_CKUSD_BESTBIDVOL"));
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
