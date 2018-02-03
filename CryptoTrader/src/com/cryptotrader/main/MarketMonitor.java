package com.cryptotrader.main;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.cryptotrader.monitor.AexMarketMonitor;
import com.cryptotrader.monitor.AllCoinMarketMonitor;
import com.cryptotrader.monitor.BcexMarketMonitor;
import com.cryptotrader.monitor.ExchangeRateMonitor;
import com.cryptotrader.monitor.ExxMarketMonitor;
import com.cryptotrader.monitor.GateMarketMonitor;
import com.cryptotrader.monitor.HuobiMarketMonitor;
import com.cryptotrader.monitor.ZBMarketMonitor;
import com.cryptotrader.strategy.Strategy1;

public class MarketMonitor {
	//public static String[] WARCHER_LIST = {"ETH","LTC","BTC","ETC","BTS","BCC"};
	public static String[] WARCHER_LIST = {"ETH","LTC"};
	public static Map<String, BigDecimal> priceMap = new HashMap<String, BigDecimal>();
	public static Map<String, BigDecimal> exchangeRate = new HashMap<String, BigDecimal>();
	public static int marketSize = 3;
	

	public static void main(String[] args) {
		//ExecutorService pool = Executors.newFixedThreadPool(WARCHER_LIST.length*(marketSize+1)+1);
		ExecutorService pool = Executors.newFixedThreadPool(100);
		//初始化汇率参数
		exchangeRate.put("qcbuy", new BigDecimal(1));
		exchangeRate.put("qcsell", new BigDecimal(1));
		exchangeRate.put("zbusdtbuy", new BigDecimal(6.5));
		exchangeRate.put("zbusdtsell", new BigDecimal(6.43));
		exchangeRate.put("usdtbuy", new BigDecimal(6.5));
		exchangeRate.put("usdtsell", new BigDecimal(6.5));
		exchangeRate.put("ckusdbuy", new BigDecimal(6.31));
		exchangeRate.put("ckusdsell", new BigDecimal(6.30));
		//启动汇率监控器
		pool.submit(new ExchangeRateMonitor(60000,exchangeRate));

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		//启动市场监控器
		for(String currency : WARCHER_LIST) {
			pool.submit(new ZBMarketMonitor(currency, 10000,priceMap,exchangeRate));
			pool.submit(new AexMarketMonitor(currency, 10000,priceMap,exchangeRate));
			//pool.submit(new GateMarketMonitor(currency, 10000,priceMap,exchangeRate));
			//pool.submit(new BcexMarketMonitor(currency, 10000,priceMap,exchangeRate));
			//pool.submit(new AllCoinMarketMonitor(currency, 10000,priceMap,exchangeRate));
			//pool.submit(new ExxMarketMonitor(currency, 10000,priceMap,exchangeRate));
			//pool.submit(new HuobiMarketMonitor(currency, 30000,priceMap,exchangeRate));
			pool.submit(new Strategy1(currency, 20000,priceMap));
		}
		
		//驱动监控策略
		
		System.out.println("数字货币监控程序开始运行");
		
	}
}
