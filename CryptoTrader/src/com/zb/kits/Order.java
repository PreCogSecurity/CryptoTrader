package com.zb.kits;

import java.math.BigDecimal;

public class Order {
	public static final int WAIT = 0;
	public static final int CANCEL = 1;
	public static final int DONE = 2;
	public static final int PART_WAIT = 3;
	public static final String NO_ORDER = "3001";
	
	
	private String id;
	
	private String currency;
	
	private BigDecimal price;

	private int status;
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	private int type;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	
	
}
