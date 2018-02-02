package com.zb.kits;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WatchJob {
	public static String[] WARCHER_LIST = {"btc_qc", "eth_qc", "etc_qc", "ltc_qc", "bcc_qc", "xrp_qc", "bts_qc", "bcx_qc", "dash_qc"};
	

	public static void main(String[] args) {
		ExecutorService pool = Executors.newFixedThreadPool(WARCHER_LIST.length);
		for(String currency : WARCHER_LIST) {
			pool.submit(new Watcher(currency, 120000));
		}

	}
}
