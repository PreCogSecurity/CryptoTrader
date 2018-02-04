package com.cryptotrader.strategy;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import com.cryptotrader.util.BigDecimalConvertor;


public class Strategy1 implements Runnable{
	//static Logger logger = Logger.getLogger(Strategy1.class); 
	private int duration;
	private String currency;
	private Map priceMap;
	public static volatile boolean STOP = false;
	public static volatile boolean STARTUP = true;
	//public static String[] MARKET_LIST = {"ZB_QC","ZB_USDT","AEX_BITCNY","AEX_USDT","EXX_QC","HUOBI_USDT","GATE_USDT","BCEX_CKUSD""ALLCOIN_CK.USD"};
	public static String[] MARKET_LIST = {"ZB_QC","ZB_USDT","AEX_BITCNY","GATE_USDT","KRAKEN_USD"};
	private String BUYMARKET;
	private String SELLMARKET;
	private BigDecimal BUYPRICE;
	private BigDecimal SELLPRICE;
	private BigDecimal BUYVOL;
	private BigDecimal SELLVOL;
	private BigDecimal GAP;
	private Date day;    
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private BigDecimalConvertor convertor = new BigDecimalConvertor();
	
	public Strategy1(String currency, int duration,Map priceMap) {
		this.currency = currency;
		this.duration = duration;
		this.priceMap = priceMap;
		
	}
		

	public void run() {
		for(; !STOP ;) {
			try {
				//ÿ�μ��ʱ����������������̼߳�������
				java.util.Random random = new java.util.Random();
				Thread.sleep(duration+ random.nextInt(duration));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			//����ǳ��������ɹ����������ʾ
			if(STARTUP){
				System.out.println(currency + "���Գ��������ɹ���");
				STARTUP = false;
			}
			
			//������ʼ��
			BUYMARKET = null;
			SELLMARKET = null;
			BUYPRICE = new BigDecimal(99999999.00);
			SELLPRICE  = new BigDecimal(0.00);
			if("ETH".equals(currency)){
				GAP = new BigDecimal(100.00);
			}else if("LTC".equals(currency)){
				GAP = new BigDecimal(20.00);
			}else if("BTC".equals(currency)){
				GAP = new BigDecimal(1000.00);
			}else if("BCC".equals(currency)){
				GAP = new BigDecimal(150.00);
			}else if("ETC".equals(currency)){
				GAP = new BigDecimal(3.00);
			}else if("BTS".equals(currency)){
				GAP = new BigDecimal(0.08);
			}else if("XRP".equals(currency)){
				GAP = new BigDecimal(0.15);
			}else if("DASH".equals(currency)){
				GAP = new BigDecimal(0.80);
			}else if("BCX".equals(currency)){
				GAP = new BigDecimal(0.01);
			}
			 	
			
			//System.out.println(priceMap);
			
			//ȡ�г����ŵ������
			for(String marketlist : MARKET_LIST) {
				Object bestask = priceMap.get(currency+":"+marketlist+ "_BESTASK");
				BigDecimal ask = null;
				try{
					ask = convertor.convert(bestask);

					//ȡ�����г�����͵�������
					if (ask.compareTo(BUYPRICE) < 0) {
						BUYPRICE = ask;
						BUYMARKET = marketlist;
						BUYVOL = convertor.convert(priceMap.get(currency+":"+marketlist+ "_BESTASKVOL"));
					} 
				}catch(Exception e){
					continue;
				}
			}		
			System.out.println(currency + "_" + BUYMARKET + "_�����:" + BUYPRICE + "   ��������" + BUYVOL);
			

			//ȡ�г����ŵ�������
			for(String marketlist : MARKET_LIST) {
				Object bestbid = priceMap.get(currency+":"+marketlist+ "_BESTBID");
				BigDecimal bid = null;
				try{
					bid = convertor.convert(bestbid);

					//ȡ�����г�����ߵ������
					if (bid.compareTo(SELLPRICE) > 0) {
						SELLPRICE = bid;
						SELLMARKET = marketlist;
						SELLVOL = convertor.convert(priceMap.get(currency+":"+marketlist+ "_BESTBIDVOL"));
					} 
				}catch(Exception e){
					continue;
				}
			}
			System.out.println(currency + "_" + SELLMARKET + "_������:" + SELLPRICE + "   ��������" + SELLVOL);
			
			
			
			
			if(SELLPRICE.subtract(BUYPRICE).compareTo(GAP) >=0 && BUYPRICE.compareTo(new BigDecimal(0.00)) > 0 && SELLPRICE.compareTo(new BigDecimal(99999999.00))>=0){
				day = new Date();
				System.err.println(df.format(day) + "   " + 
						currency+
						"  �����г�:" + BUYMARKET +
						"  ����۸�:" + BUYPRICE +
						"  �����г�:" + SELLMARKET +
						"  �����۸�:" + SELLPRICE +
						"  ���߼۲�:" + SELLPRICE.subtract(BUYPRICE));
				
				
				//System.err.println("-------------" + currency + "-------------");
				//df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				//System.err.println(df.format(day));   
				//System.err.println("�����г���" + BUYMARKET + "      ����۸�:" + BUYPRICE);
				//System.err.println("�����г���" + SELLMARKET + "      �����۸�:" + SELLPRICE);
				//System.err.println("���߼۲" + SELLPRICE.subtract(BUYPRICE));
				//System.err.println("--------------------------");
				
		
				//logger.error("TEST");
			}
			
			//System.out.println(currency + "������");
			
		}
		
	}

}
