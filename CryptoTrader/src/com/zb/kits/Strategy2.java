package com.zb.kits;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class Strategy2 {
	private ZBMarket marketWatcher;
	private Trader trader;
	private String market;
	private BigDecimal maxPosition;
	private BigDecimal currentPosition;
	private String tagetCurrency;
	
	private BigDecimal BASE_VOL = new BigDecimal(1.0);
	//
	private BigDecimal BASE_GAP = new BigDecimal(0.01);
	private BigDecimal PRICE_DELTA = new BigDecimal(0.001);
	//卖1
	private BigDecimal askPrice_1 = new BigDecimal(0.0); 
	//买1
	private BigDecimal bidPrice_1 = new BigDecimal(0.0);
	private BigDecimal askVol = new BigDecimal(0.0);
	private BigDecimal bidVol = new BigDecimal(0.0);
	//已挂ask单
	private Order orderedAsk;
	//已挂bid单
	private Order orderedBid;
	
	private final int MARKET_DEPTH = 10;
	private int sleep = 15000;//15s
	private final Logger logger = LoggerFactory.getLogger(Strategy2.class);
	
	public Strategy2(String market, String gap, int sleep,  double maxPosition, Trader trader) {
		BASE_GAP = new BigDecimal(gap);
		
		this.maxPosition = new BigDecimal(maxPosition);
		this.market = market;
		this.sleep = sleep;
		if (market != null && market.contains("_")) {
			tagetCurrency = market.split("_")[0].toUpperCase();
		}
		marketWatcher = new ZBMarket();
		if (ZBMarket.MARKET_MAP.get(market) != null) {
			PRICE_DELTA = new BigDecimal(ZBMarket.MARKET_MAP.get(market));
		}
		this.trader = trader;
	}
	
	public void combineOperation() {
		for(;;) {
			try {
				Map<String, Object> accountInfo = trader.getAccountInfo();
				JSONObject result = (JSONObject)accountInfo.get("result");
				if (result != null) {
					JSONArray coins = (JSONArray)result.get("coins");
					for (Object coin : coins) {
						JSONObject coinMap = (JSONObject)coin;
						String enName = (String)coinMap.get("enName");
						if(tagetCurrency.equals(enName)) {
							String available = (String)coinMap.get("available");
							currentPosition = new BigDecimal(available);
						}
					}
				}
				else {
					System.out.println("无法获取用户账户信息，请检查网络或者key是否正确");
					Thread.sleep(sleep);
					continue;
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
			//获取现有挂单，理论上单货币应该只有一条单边挂单，所以只取第一条
			String resultAsk = trader.getOrders(market, Trader.ASK);
			if (resultAsk != null && !resultAsk.contains(Order.NO_ORDER)) {
				JSONArray askOrderArray = JSON.parseArray(resultAsk);
				for(int i = 0; i < askOrderArray.size(); i++) {
					JSONObject orderMap = (JSONObject)askOrderArray.get(i);
					int status = (Integer)orderMap.get("status");
					if(orderMap != null && (Order.WAIT == status || Order.PART_WAIT == status)) {
						orderedAsk = new Order();
						orderedAsk.setCurrency((String)orderMap.get("currency"));
						orderedAsk.setId((String)orderMap.get("id"));
						orderedAsk.setStatus(status);
						orderedAsk.setType((int)orderMap.get("type"));
						orderedAsk.setPrice((BigDecimal)orderMap.get("price"));
						//如果有挂单，即刻撤销
						trader.cancelOrder(orderedAsk.getCurrency(), orderedAsk.getId());
					}
				}
			}
			
			String resultBid = trader.getOrders(market, Trader.BID);
			if (resultBid != null && !resultBid.contains(Order.NO_ORDER)) {
				JSONArray bidOrderArray = JSON.parseArray(trader.getOrders(market, Trader.BID));
				for(int i = 0; i < bidOrderArray.size(); i++)
				{
					JSONObject orderMap = (JSONObject)bidOrderArray.get(i);
					int status = (Integer)orderMap.get("status");
					if(orderMap != null && (Order.WAIT == status || Order.PART_WAIT == status)) {
						orderedBid = new Order();
						orderedBid.setCurrency((String)orderMap.get("currency"));
						orderedBid.setId((String)orderMap.get("id"));
						orderedBid.setStatus(status);
						orderedBid.setType((int)orderMap.get("type"));
						orderedBid.setPrice((BigDecimal)orderMap.get("price"));
						
						//如果有挂单，即刻撤销
						trader.cancelOrder(orderedBid.getCurrency(), orderedBid.getId());
					}
				}
			}
			try {
				Map<String, Object> depth = marketWatcher.retriveMarketDepth(market, MARKET_DEPTH);
				JSONArray askArray = (JSONArray) depth.get("asks");
				JSONArray bidArray = (JSONArray) depth.get("bids");
				
				Collections.reverse(askArray);
				System.out.println("askArray:" + askArray);
				System.out.println("bidArray:" + bidArray);
				
				for(Object item : askArray) {
					Object volObj = ((JSONArray)item).get(1);
					BigDecimal vol = null;
					if(volObj instanceof Integer) {
						vol = new BigDecimal((int)volObj);
					} else if(volObj instanceof BigDecimal) {
						vol = (BigDecimal)volObj;
					}
					//卖量大于1
					if (vol.compareTo(BASE_VOL) >= 0) {
						askPrice_1 = (BigDecimal) ((JSONArray)item).get(0);
						askVol = vol;
						break;
					} 
				}
				for(Object item : bidArray) {
					Object volObj = ((JSONArray)item).get(1);
					BigDecimal vol = null;
					if(volObj instanceof Integer) {
						vol = new BigDecimal((int)volObj);
					} else if(volObj instanceof BigDecimal) {
						vol = (BigDecimal)volObj;
					}
					//买量大于1
					if (vol.compareTo(BASE_VOL) >= 0) {
						bidPrice_1 = (BigDecimal) ((JSONArray)item).get(0);
						bidVol = vol;
						break;
					} 
				}
				BigDecimal gap = askPrice_1.subtract(bidPrice_1).abs().divide(askPrice_1, 6, RoundingMode.HALF_UP);
				System.out.println(gap.toPlainString());
				if (gap.compareTo(BASE_GAP) >= 0) {
					beginTxn();
				}
				
				Thread.sleep(sleep);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	private void beginTxn() {
		if(currentPosition.compareTo(maxPosition) < 0) {
//			if (orderedBid != null && bidPrice_1.compareTo(orderedBid.getPrice()) < 0) {
//				trader.cancelOrder(orderedBid.getCurrency(), orderedBid.getId());
//			}
			BigDecimal vol = maxPosition.subtract(currentPosition);	
			logger.info("bid 挂单，挂单价格:" + bidPrice_1.add(PRICE_DELTA).toPlainString() + "数量:" + vol);
			String json = trader.order(market, Trader.BID, bidPrice_1.add(PRICE_DELTA), vol);
			Map<String, Object> jsonMap = JSON.parseObject(json);
			int code;
			String message = "";
			if(jsonMap != null) {
				code = (int)jsonMap.get("code");
				message = (String)jsonMap.get("message");
				if (ZBMarket.SUCCESS == code) {
//					currentPosition = currentPosition.add(vol);
					System.out.println("bid挂单成功");
					logger.info("bid挂单成功");
				}else {
					System.out.println("bid挂单失败，" + message);
				}
			}else {
				System.out.println("bid挂单失败，" + message);
			}
		}
		
		//当前持仓<0
		if(currentPosition.compareTo(new BigDecimal(0)) > 0) {
//			if (orderedAsk != null && askPrice_1.compareTo(orderedAsk.getPrice()) < 0) {
//				trader.cancelOrder(orderedAsk.getCurrency(), orderedAsk.getId());
//			}
			currentPosition = currentPosition.setScale(2, RoundingMode.HALF_UP);
			logger.info("ask 挂单，挂单价格:" + askPrice_1.subtract(PRICE_DELTA).toPlainString() + "数量:" + currentPosition.toPlainString());
			String json = trader.order(market, Trader.ASK, askPrice_1.subtract(PRICE_DELTA), currentPosition);
			Map<String, Object> jsonMap = JSON.parseObject(json);
			int code;
			String message = "";
			if(jsonMap != null) {
				code = (int)jsonMap.get("code");
				message = (String)jsonMap.get("message");
				if (ZBMarket.SUCCESS == code) {
//					currentPosition = currentPosition.subtract(currentPosition);
					logger.info("ask挂单成功");
					System.out.println("ask挂单成功");
				} else {
					logger.info("ask挂单失败");
					System.out.println("ask挂单失败，" + message);
				}
			}else {
				System.out.println("ask挂单失败，" + message);
			}
		}
	}
}
