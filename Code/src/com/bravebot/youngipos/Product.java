package com.bravebot.youngipos;

public class Product {
	String name;
	int sticker_price;
	int id;
	int cat_id;
	
	public Product(){
		super();
	}
	public Product(String p_name, int price, int id, int category){
		this.name = p_name;
		this.sticker_price = price;
		this.id = id;
		this.cat_id = category;
	}
}
