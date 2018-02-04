package com.cryptotrader.monitor;


import java.math.BigDecimal;
import java.util.Map;

import com.cryptotrader.market.*;
import com.cryptotrader.util.BigDecimalConvertor;


public class ExxMarketMonitor implements Runnable{
	private int duration;
	private String currency;
	private Market exxMarket;
	private Map priceMap;
	private Map exchangeRate;
	public static volatile boolean STOP = false;
	public static volatile boolean STARTUP = true;
	private BigDecimalConvertor convertor = new BigDecimalConvertor();
	
	public ExxMarketMonitor(String currency, int duration,Map priceMap,Map exchangeRate) {
		this.currency = currency;
		this.duration = duration;
		this.priceMap = priceMap;
		this.exchangeRate = exchangeRate;
		exxMarket = new ExxMarket();
	}
		

	public void run() {
		for(; !STOP ;) {
			//获取EXX市场最新价		
			Map<String, Object> exxTickerMap = null;
			try {
				exxTickerMap = exxMarket.ticker(currency + "_qc");
				Map<String, Object> exxTicker = (Map<String, Object>)exxTickerMap.get("ticker");
				BigDecimal last = convertor.convert(exxTicker.get("last")).setScale(2,BigDecimal.ROUND_HALF_UP);
				priceMap.put(currency + ":EXX_QC_LAST" + "", last);
			}
			catch(Exception e) {
				e.printStackTrace();
				continue;
			}			

			//try {
				//exxTickerMap = exxMarket.ticker(currency + "_usdt");
				//Map<String, Object> exxTicker = (Map<String, Object>)exxTickerMap.get("ticker");
				//BigDecimal last = new BigDecimal((String)exxTicker.get("last"));
				//last = last.multiply((BigDecimal) exchangeRate.get("usdtbuy")).setScale(2,BigDecimal.ROUND_HALF_UP);
				//priceMap.put(currency + ":EXX_USDT_LAST" + "", last);
			//}
			//catch(Exception e) {
				//System.out.println(e);
				//continue;
			//}
			
			
			//获取EXX最佳买卖价		
			Map<String, Object> exxBestAsk = null;
			Map<String, Object> exxBestBid = null;
			try{
				exxBestAsk = exxMarket.getBestAsk(currency + "_qc", new BigDecimal(3));
				exxBestBid = exxMarket.getBestBid(currency + "_qc", new BigDecimal(3));
				priceMap.put(currency + ":EXX_QC_BESTASK", (BigDecimal)exxBestAsk.get("bestAsk"));
				priceMap.put(currency + ":EXX_QC_BESTBID", (BigDecimal)exxBestBid.get("bestBid"));
				priceMap.put(currency + ":EXX_QC_BESTASKVOL", (BigDecimal)exxBestAsk.get("askVol"));
				priceMap.put(currency + ":EXX_QC_BESTBIDVOL", (BigDecimal)exxBestBid.get("bidVol"));
			}catch(Exception e) {
				e.printStackTrace();
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					e.printStackTrace();
				}
				continue;
			}	
			
			//try{
				//exxBestAsk = exxMarket.getBestAsk(currency + "_usdt", new BigDecimal(0.3));
				//exxBestBid = exxMarket.getBestBid(currency + "_usdt", new BigDecimal(0.3));
				//priceMap.put(currency + ":EXX_USDT_BESTASK", ((BigDecimal)exxBestAsk.get("bestAsk")).multiply((BigDecimal) exchangeRate.get("usdtbuy")).setScale(2,BigDecimal.ROUND_HALF_UP));
				//priceMap.put(currency + ":EXX_USDT_BESTBID", ((BigDecimal)exxBestBid.get("bestBid")).multiply((BigDecimal) exchangeRate.get("usdtsell")).setScale(2,BigDecimal.ROUND_HALF_UP));
			//}catch(Exception e) {
				//System.out.println(e);
				//try {
					//Thread.sleep(2000);
				//} catch (InterruptedException e1) {
					//System.out.println(e);
				//}
				//continue;
			//}	

			
			//如果是初次启动成功，则给出提示
			if(STARTUP){
				System.out.println("EXX市场监控器启动成功！");
				STARTUP = false;
			}
			
			//System.out.println("QC_LAST:" + priceMap.get(currency + ":EXX_QC_LAST"));
			//System.out.println("QC_BESTASK:" + priceMap.get(currency + ":EXX_QC_BESTASK"));
			//System.out.println("QC_BESTBID:" + priceMap.get(currency + ":EXX_QC_BESTBID"));
			//System.out.println("USDT_LAST:" + priceMap.get(currency + ":EXX_USDT_LAST"));
			//System.out.println("USDT_BESTASK:" + priceMap.get(currency + ":EXX_USDT_BESTASK"));
			//System.out.println("USDT_BESTBID:" + priceMap.get(currency + ":EXX_USDT_BESTBID"));
			
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
