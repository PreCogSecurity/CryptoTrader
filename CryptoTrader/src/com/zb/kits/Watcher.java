package com.zb.kits;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.aex.AexMarket;
import com.alibaba.fastjson.JSON;

import io.vcoins.Market;

public class Watcher implements Runnable{
	private final static String PATH = "/workspace/coins/watch/"; 
	private int duration;
	private String currency;
	private Market zbMarket;
	private Market aexMarket;
	private BufferedWriter bfw;
	public static final String SPLIT = ",";
	public static volatile boolean STOP = false;
	
	public Watcher(String currency, int duration) {
		this.currency = currency;
		this.duration = duration;
		zbMarket = new ZBMarket();
		aexMarket = new AexMarket();
		
		Path fpath = Paths.get(PATH + currency + ".csv");
		 //创建文件
        if(!Files.exists(fpath)) {
            try {
                Files.createFile(fpath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
		try {
			bfw = Files.newBufferedWriter(fpath, StandardOpenOption.CREATE ,StandardOpenOption.APPEND);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
       
	}

	@Override
	public void run() {
		for(; !STOP ;) {
			StringBuilder buffer = new StringBuilder();
			//zb
			{
				String tickerJson = "{}";
				try {
					tickerJson = zbMarket.ticker(currency);
				}
				catch(IOException e) {
					e.printStackTrace();
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					continue;
				}
				
				Map<String, Object> jsonMap = JSON.parseObject(tickerJson);
				String dateStr = (String)jsonMap.get("date");
				DateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String tickerTime = formatter.format(new Date(Long.parseLong(dateStr)));
				System.out.println(tickerTime + " " + currency);
				
				Map<String, Object> tickerMap = (Map<String, Object>)jsonMap.get("ticker");
				String last = (String) tickerMap.get("last");
				String buy = (String) tickerMap.get("buy");
				String sell = (String) tickerMap.get("sell");
				String vol = (String) tickerMap.get("vol");
				
				buffer.append(tickerTime + SPLIT);
				buffer.append(last + SPLIT);
				buffer.append(buy + SPLIT);
				buffer.append(sell + SPLIT);
				buffer.append(vol + SPLIT);
			}
			//aex
			{
				String tickerJson = "{}";
				try {
					tickerJson = aexMarket.ticker(currency);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					continue;
					
				}
				Map<String, Object> jsonMap = JSON.parseObject(tickerJson);
				Map<String, Object> tickerMap = (Map<String, Object>)jsonMap.get("ticker");
				
				Object lastObj = (Object) tickerMap.get("last");
				Object buyObj = (Object) tickerMap.get("buy");
				Object sellObj = (Object) tickerMap.get("sell");
				Object volObj = (Object) tickerMap.get("vol");
				
				buffer.append(lastObj.toString() + SPLIT);
				buffer.append(buyObj.toString() + SPLIT);
				buffer.append(sellObj.toString() + SPLIT);
				buffer.append(volObj.toString());
			}
			
			try {
	            bfw.write(buffer.toString() + "\n");
	            bfw.flush();
//	            bfw.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
			try {
				Thread.sleep(duration);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			bfw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
