package com.cryptotrader.monitor;

import java.math.BigDecimal;
import java.util.Map;
import com.cryptotrader.market.Market;
import com.cryptotrader.market.ZBMarket;
import com.cryptotrader.market.*;



/**
 * 汇率监控器
 * @author SUNLIANCHENG687
 *
 */


public class ExchangeRateMonitor implements Runnable{
	private int duration;
	private Market zbMarket;
	private Market krakenMarket;
	private Map exchangeRate;
	public static volatile boolean STOP = false;
	public static volatile boolean STARTUP = true;
	
	public ExchangeRateMonitor(int duration,Map priceMap) {
		this.duration = duration;
		this.exchangeRate = priceMap;
		zbMarket = new ZBMarket();
		krakenMarket = new KrakenMarket();
	}
		

	public void run() {
		for(; !STOP ;) {
			//从ZB平台获取bitcny和USDT汇率
			BigDecimal BitCnyBuy = new BigDecimal(0.0000);
			BigDecimal BitCnySell = new BigDecimal(0.0000);
			BigDecimal usdtBuy = new BigDecimal(0.0000);
			BigDecimal usdtSell = new BigDecimal(0.0000);
			try {
				BitCnyBuy = (BigDecimal)zbMarket.getBestAsk("bitcny_qc", new BigDecimal(20000)).get("bestAsk");
				BitCnySell = (BigDecimal)zbMarket.getBestBid("bitcny_qc", new BigDecimal(20000)).get("bestBid");
				usdtBuy = (BigDecimal)zbMarket.getBestAsk("usdt_qc", new BigDecimal(3000)).get("bestAsk");
				usdtSell = (BigDecimal)zbMarket.getBestBid("usdt_qc", new BigDecimal(3000)).get("bestBid");
			} catch (Exception e) {
				System.out.println(e);
			}
			
			//从KRAKEN平台取USDTUSD汇率
			BigDecimal usdtusdBuy = new BigDecimal(0.0000);
			BigDecimal usdtusdSell = new BigDecimal(0.0000);
			
			try {
				//usdtusdBuy = (BigDecimal)krakenMarket.getBestAsk("usdtzusd", new BigDecimal(100)).get("bestAsk");
				//usdtusdSell = (BigDecimal)krakenMarket.getBestBid("usdzusd", new BigDecimal(100)).get("bestBid");
			} catch (Exception e) {
				System.out.println(e);
			}
			
			
			exchangeRate.put("bitcnybuy", BitCnyBuy);
			exchangeRate.put("bitcnysell", BitCnySell);
			exchangeRate.put("zbusdtbuy", usdtBuy);
			exchangeRate.put("zbusdtsell", usdtSell);
			//exchangeRate.put("usdtusd", usdtusdBuy);
			//exchangeRate.put("usdtusd", usdtusdSell);
			System.out.println("usdtusd:" + usdtusdBuy);
			System.out.println("usdtusd:" + usdtusdSell);
			
			if(STARTUP){
				System.out.println("汇率监控器启动成功！");
				STARTUP = false;
			}
			
			try {
				Thread.sleep(duration);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
			
		}
		
	}

}
