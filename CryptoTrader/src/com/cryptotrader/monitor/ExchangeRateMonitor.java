package com.cryptotrader.monitor;

import java.math.BigDecimal;
import java.util.Map;

import com.cryptotrader.market.Market;
import com.cryptotrader.market.ZBMarket;



/**
 * 汇率监控器
 * @author SUNLIANCHENG687
 *
 */


public class ExchangeRateMonitor implements Runnable{
	private int duration;
	private Market zbMarket;
	private Map exchangeRate;
	public static volatile boolean STOP = false;
	public static volatile boolean STARTUP = true;
	
	public ExchangeRateMonitor(int duration,Map priceMap) {
		this.duration = duration;
		this.exchangeRate = priceMap;
		zbMarket = new ZBMarket();
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
			exchangeRate.put("bitcnybuy", BitCnyBuy);
			exchangeRate.put("bitcnysell", BitCnySell);
			exchangeRate.put("usdtbuy", usdtBuy);
			exchangeRate.put("usdtsell", usdtSell);
			//System.out.println("usdtbuy:" + usdtBuy);
			//System.out.println("usdtsell:" + usdtSell);
			
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
