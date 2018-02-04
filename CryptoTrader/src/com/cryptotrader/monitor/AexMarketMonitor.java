package com.cryptotrader.monitor;

import java.math.BigDecimal;
import java.util.Map;

import com.cryptotrader.market.*;
import com.cryptotrader.util.BigDecimalConvertor;


public class AexMarketMonitor implements Runnable{
	private int duration;
	private String currency;
	private Market aexMarket;
	private Map priceMap;
	private Map exchangeRate;
	public static volatile boolean STOP = false;
	private static boolean STARTUP = true;
	private BigDecimalConvertor convertor = new BigDecimalConvertor();
	
	public AexMarketMonitor(String currency, int duration,Map priceMap,Map exchangeRate) {
		this.currency = currency;
		this.duration = duration;
		this.priceMap = priceMap;
		this.exchangeRate = exchangeRate;
		aexMarket = new AexMarket();
	}
		

	public void run() {
		for(; !STOP ;) {
			//获取AEX市场最新价		
			Map<String, Object> aexTickerMap = null;
			try {
				aexTickerMap = aexMarket.ticker(currency + "_bitcny");
				Map<String, Object> aexTicker = (Map<String, Object>)aexTickerMap.get("ticker");
				BigDecimal last = convertor.convert(aexTicker.get("last"));
				priceMap.put(currency + ":AEX_BITCNY_LAST", last.multiply((BigDecimal) exchangeRate.get("bitcnybuy")));
			}
			catch(Exception e) {
				e.printStackTrace();
				continue;
			}			

			try {
				aexTickerMap = aexMarket.ticker(currency + "_usdt");
				Map<String, Object> aexTicker = (Map<String, Object>)aexTickerMap.get("ticker");
				BigDecimal last = convertor.convert(aexTicker.get("last"));
				last = last.multiply((BigDecimal) exchangeRate.get("usdtbuy")).setScale(2,BigDecimal.ROUND_HALF_UP);
				priceMap.put(currency + ":AEX_USDT_LAST" , last);
			}
			catch(Exception e) {
				e.printStackTrace();
				continue;
			}
			
			
			//获取AEX最佳买卖价		
			Map<String, Object> aexBestAsk = null;
			Map<String, Object> aexBestBid = null;
			try{
				aexBestAsk = aexMarket.getBestAsk(currency + "_bitcny", new BigDecimal(3));
				aexBestBid = aexMarket.getBestBid(currency + "_bitcny", new BigDecimal(3));
				//System.out.println(aexBestAsk);
				//System.out.println(aexBestBid);
				priceMap.put(currency + ":AEX_BITCNY_BESTASK", ((BigDecimal)aexBestAsk.get("bestAsk")).multiply((BigDecimal) exchangeRate.get("bitcnybuy")).setScale(2,BigDecimal.ROUND_HALF_UP));
				priceMap.put(currency + ":AEX_BITCNY_BESTBID", ((BigDecimal)aexBestBid.get("bestBid")).multiply((BigDecimal) exchangeRate.get("bitcnysell")).setScale(2,BigDecimal.ROUND_HALF_UP));
				priceMap.put(currency + ":AEX_BITCNY_BESTASKVOL", (BigDecimal)aexBestAsk.get("askVol"));
				priceMap.put(currency + ":AEX_BITCNY_BESTBIDVOL", (BigDecimal)aexBestBid.get("bidVol"));
			}catch(Exception e) {
				System.out.println(e);
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					e.printStackTrace();
				}
				continue;
			}	
			
			try{
				aexBestAsk = aexMarket.getBestAsk(currency + "_usdt", new BigDecimal(3));
				aexBestBid = aexMarket.getBestBid(currency + "_usdt", new BigDecimal(3));
				priceMap.put(currency + ":AEX_USDT_BESTASK", ((BigDecimal)aexBestAsk.get("bestAsk")).multiply((BigDecimal) exchangeRate.get("usdtbuy")).setScale(2,BigDecimal.ROUND_HALF_UP));
				priceMap.put(currency + ":AEX_USDT_BESTBID", ((BigDecimal)aexBestBid.get("bestBid")).multiply((BigDecimal) exchangeRate.get("usdtsell")).setScale(2,BigDecimal.ROUND_HALF_UP));
				priceMap.put(currency + ":AEX_USDT_BESTASKVOL", (BigDecimal)aexBestAsk.get("askVol"));
				priceMap.put(currency + ":AEX_USDT_BESTBIDVOL", (BigDecimal)aexBestBid.get("bidVol"));
			}catch(Exception e) {
				e.printStackTrace();
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					e.printStackTrace();
				}
				continue;
			}	

			
			//如果是初次启动成功，则给出提示
			if(STARTUP){
				System.out.println("AEX市场监控器启动成功！");
				STARTUP = false;
			}
			
			//System.out.println("BITCNY_LAST:" + priceMap.get(currency + ":AEX_BITCNY_LAST"));
			//System.out.println("BITCNY_BESTASK:" + priceMap.get(currency + ":AEX_BITCNY_BESTASK"));
			//System.out.println("BITCNY_BESTBID:" + priceMap.get(currency + ":AEX_BITCNY_BESTBID"));
			//System.out.println("USDT_LAST:" + priceMap.get(currency + ":ZB_USDT_LAST"));
			//System.out.println("USDT_BESTASK:" + priceMap.get(currency + ":ZB_USDT_BESTASK"));
			//System.out.println("USDT_BESTBID:" + priceMap.get(currency + ":ZB_USDT_BESTBID"));
			
			try {
				//每次间隔时间随机，避免所有线程集中请求
				java.util.Random random = new java.util.Random();
				Thread.sleep(duration+ random.nextInt(duration));
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
			
		}
		
	}

}
