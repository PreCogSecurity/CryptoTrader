package com.cryptotrader.strategy;

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


public class TestStrategy implements Runnable{
	private int duration;
	private String currency;
	private Market zbMarket;
	private Market aexMarket;
	private Map priceMap;
	public static volatile boolean STOP = false;
	
	public TestStrategy(String currency, int duration,Map priceMap) {
		this.currency = currency;
		this.duration = duration;
		this.priceMap = priceMap;
		zbMarket = new ZBMarket();
		aexMarket = new AexMarket();

	}
		

	public void run() {
		
		
		
		
		for(; !STOP ;) {
			StringBuilder buffer = new StringBuilder();
			
			//获取ZB市场行情			
			Map<String, Object> zbTickerMap = null;
			try {
				zbTickerMap = zbMarket.ticker(currency + "_qc");
			}
			catch(IOException e) {
				e.printStackTrace();
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					
					e1.printStackTrace();
				}
				continue;
			}			
			String dateStr = (String)zbTickerMap.get("date");
			DateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String tickerTime = formatter.format(new Date(Long.parseLong(dateStr)));
			Map<String, Object> zbTicker = (Map<String, Object>)zbTickerMap.get("ticker");
			String last = (String) zbTicker.get("last");
			String buy = (String) zbTicker.get("buy");
			String sell = (String) zbTicker.get("sell");
			String vol = (String) zbTicker.get("vol");
			
			//获取ZB市场深度	
			JSONArray zbAskArray = null;
			JSONArray zbBidArray = null;
			try {
				zbAskArray = zbMarket.getAsks(currency + "_qc", 10);
				zbBidArray = zbMarket.getBids(currency + "_qc", 10);
			}
			catch(IOException e) {
				e.printStackTrace();
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					
					e1.printStackTrace();
				}
				continue;
			}		
			
			
			//获取ZB最佳买卖价		
			Map<String, Object> zbBestAsk = null;
			Map<String, Object> zbBestBid = null;
			try{
				zbBestAsk = zbMarket.getBestAsk(currency + "_qc", new BigDecimal(0.001));
				zbBestBid = zbMarket.getBestBid(currency + "_qc", new BigDecimal(0.001));			
				
			}catch(IOException e) {
				e.printStackTrace();
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					
					e1.printStackTrace();
				}
				continue;
			}	
				

			//获取AEX市场行情
			Map<String, Object> aexTickerMap = null;
			try {
				aexTickerMap = aexMarket.ticker(currency + "_bitcny");
			}
			catch(IOException e) {
				System.out.println(e);
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					
					e1.printStackTrace();
				}
				continue;
			}	
			Map<String, Object> aexTicker = (Map<String, Object>)aexTickerMap.get("ticker");
			//String last = String.valueOf(ticker.get("last")) ;
			//String buy = String.valueOf(ticker.get("buy")) ;
			//String sell = String.valueOf(ticker.get("sell")) ;
			//String vol = String.valueOf(ticker.get("vol")) ;
			
			//获取ZB市场深度
			JSONArray aexAskArray = null;
			JSONArray aexBidArray = null;
			try {
				aexAskArray = aexMarket.getAsks(currency + "_bitcny", 10);
				aexAskArray = aexMarket.getBids(currency + "_bitcny" , 10);
			}
			catch(IOException e) {
				e.printStackTrace();
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					
					e1.printStackTrace();
				}
				continue;
			}	
			
			//获取AEX最佳买卖价	
			Map<String, Object> aexBestAsk = null;
			Map<String, Object> aexBestBid = null;
			try{
				aexBestAsk = aexMarket.getBestAsk(currency + "_bitcny", new BigDecimal(1));
				aexBestBid = aexMarket.getBestBid(currency + "_bitcny", new BigDecimal(1));			
				
			}catch(IOException e) {
				e.printStackTrace();
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					
					e1.printStackTrace();
				}
				continue;
			}	
			
			
			BigDecimal zbaex = new BigDecimal(0.00);
			BigDecimal aexzb = new BigDecimal(0.00);
			//System.out.println("-----------------" + currency + "-----------------");
			//System.out.println("zbbid:" + (BigDecimal)zbBestBid.get("bestBid"));
			//System.out.println("zbask:" + (BigDecimal)zbBestAsk.get("bestAsk"));
			//System.out.println("aexbid:" + ((BigDecimal)aexBestBid.get("bestBid")).multiply((BigDecimal)exchangeRate.getBitCnySellRate()));	
			//System.out.println("aexask:" + ((BigDecimal)aexBestAsk.get("bestAsk")).multiply(exchangeRate.getBitCnyBuyRate()));
			//zbaex = ((BigDecimal)zbBestBid.get("bestBid")).subtract(((BigDecimal)aexBestAsk.get("bestAsk")).multiply(exchangeRate.getBitCnyBuyRate()));
			//aexzb = (((BigDecimal)aexBestBid.get("bestBid")).multiply((BigDecimal)exchangeRate.getBitCnySellRate())).subtract((BigDecimal)zbBestAsk.get("bestAsk"));
			//System.out.println("zb-aex:" + zbaex);	
			//System.out.println("aex-zb:" + aexzb);
			//System.out.println("----------------------------------");
			
			try{
				priceMap.put("zbaex", zbaex);
			}catch(Exception e){
				System.out.println(e);
			}
			

			try {
				Thread.sleep(duration);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
			
		}
		
	}

}
