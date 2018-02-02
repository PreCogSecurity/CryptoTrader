package com.cryptotrader.util;

import java.math.BigDecimal;

public class BigDecimalConvertor{
	
	public BigDecimal convert(Object obj){
		BigDecimal value = null;
		if(obj instanceof Integer) {
			value = new BigDecimal((Integer)obj);
		} else if(obj instanceof BigDecimal) {
			value = (BigDecimal)obj;
		}else if(obj instanceof String) {
			value = new BigDecimal((String)obj);
		}
		
		return value;
	}
	
	
}