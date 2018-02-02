package com.cryptotrader.monitor;

import java.math.BigDecimal;
import java.util.Map;

import com.cryptotrader.market.*;


public class HuobiMarketMonitor implements Runnable{
	private int duration;
	private String currency;
	private Market huobiMarket;
	private Map priceMap;
	private Map exchangeRate;
	public static volatile boolean STOP = false;
	public static volatile boolean STARTUP = true;
	
	public HuobiMarketMonitor(String currency, int duration,Map priceMap,Map exchangeRate) {
		this.currency = currency;
		this.duration = duration;
		this.priceMap = priceMap;
		this.exchangeRate = exchangeRate;
		huobiMarket = new HuobiMarket();
	}
		

	public void run() {
		for(; !STOP ;) {
			//��ȡHUOBI�г����¼�		
			Map<String, Object> tickerMap = null;
			try {
				tickerMap = huobiMarket.ticker(currency + "usdt");
				Map<String, Object> ticker = (Map<String, Object>)tickerMap.get("ticker");
				//BigDecimal last = new BigDecimal((String)ticker.get("last"));
				BigDecimal last = new BigDecimal(0.00);
				last = last.multiply((BigDecimal) exchangeRate.get("usdtbuy")).setScale(2,BigDecimal.ROUND_HALF_UP);
				priceMap.put(currency + ":HUOBI_USDT_LAST" + "", last);
			}
			catch(Exception e) {
				System.out.println(e);
				continue;
			}
				
			//��ȡHUOBI���������		
			Map<String, Object> bestAsk = null;
			Map<String, Object> bestBid = null;
			
			try{
				bestAsk = huobiMarket.getBestAsk(currency + "usdt", new BigDecimal(0.3));
				bestBid = huobiMarket.getBestBid(currency + "usdt", new BigDecimal(0.3));
				priceMap.put(currency + ":HUOBI_USDT_BESTASK", ((BigDecimal)bestAsk.get("bestAsk")).multiply((BigDecimal) exchangeRate.get("usdtbuy")).setScale(2,BigDecimal.ROUND_HALF_UP));
				priceMap.put(currency + ":HUOBI_USDT_BESTBID", ((BigDecimal)bestBid.get("bestBid")).multiply((BigDecimal) exchangeRate.get("usdtsell")).setScale(2,BigDecimal.ROUND_HALF_UP));
			}catch(Exception e) {
				System.out.println(e);
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					System.out.println(e);
				}
				continue;
			}	

			
			//����ǳ��������ɹ����������ʾ
			if(STARTUP){
				System.out.println("HUOBI�г�����������ɹ���");
				STARTUP = false;
			}
			
			System.out.println("USDT_LAST:" + priceMap.get(currency + ":HUOBI_USDT_LAST"));
			System.out.println("USDT_BESTASK:" + priceMap.get(currency + ":HUOBI_USDT_BESTASK"));
			System.out.println("USDT_BESTBID:" + priceMap.get(currency + ":HUOBI_USDT_BESTBID"));
			
			try {
				//ÿ�μ��ʱ����������������̼߳�������
				java.util.Random random = new java.util.Random();
				Thread.sleep(duration+ random.nextInt(duration));
			} catch (InterruptedException e) {

				System.out.println(e);
			}
			
		}
		
	}

}
