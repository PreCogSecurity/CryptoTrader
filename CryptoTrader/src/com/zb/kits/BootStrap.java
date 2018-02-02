package com.zb.kits;

public class BootStrap {

	public static void main(String[] args) {
		try {
			boolean needProxy = Boolean.parseBoolean(args[0]);
			String market = args[1];
			String gap = args[2];
			int sleep = Integer.parseInt(args[3]);
			double maxPosition = Double.parseDouble(args[4]);
			
			String accessKey = args[5];
			String secretKey = args[6];
			
			if (needProxy) {
				String sProxyHost = "127.0.0.1";
				String sProxyPort = "62466";
				String sProxySocketPort = "62467";
				System.setProperty("proxySet", "true"); 
				
				System.setProperty("http.proxyHost", sProxyHost);  
				System.setProperty("http.proxyPort", sProxyPort);
				
				System.setProperty("https.proxyHost", sProxyHost);  
				System.setProperty("https.proxyPort", sProxyPort);
				
				System.setProperty("socksProxyHost", sProxyHost);  
				System.setProperty("socksProxyPort", sProxySocketPort);
			}
			Trader trader = new Trader();
			trader.ACCESS_KEY = accessKey;
			trader.SECRET_KEY = secretKey;
			
			Strategy2 s2 = new Strategy2(market, gap, sleep, maxPosition, trader);
			s2.combineOperation();
		} catch (Exception ex) {
			System.err.println(ex);
			ex.printStackTrace();
		}
		
	}

}
