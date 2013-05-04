package com.bravebot.youngipos;

import android.util.Log;

public class SoldProduct {
	private Product product;  // ²£«~
	private double discount;  // §é¦©
	private int sold_price;   // °â»ù
	private int count;        // ¼Æ¶q
	private int total_price, final_price;  
	private int product_id;
	
	public SoldProduct(){
		super();
	}
	
	public SoldProduct(Product p){
		this.product = p;
		this.discount = 1.0;
		this.sold_price = p.sticker_price;
		this.count = 1;
	}
	
	public SoldProduct(Product p, double discount, int count){
		if( discount != 1 && count > 1){
			this.product = p;
			this.discount = discount;
			this.count = count;
			this.sold_price = (int) Math.round(this.product.sticker_price * discount);
		} else {
			this.product = p;
			this.discount = 1;
			this.count = 1;
			this.sold_price = this.product.sticker_price;
			Log.v("Msg","in soldproduct contructor");
		}
	}
	public SoldProduct(int product_id, double discount, int count, int final_price, int total_price){
		this.product = MainActivity.dbhelper.getProductById(product_id);
		this.sold_price = product.sticker_price;
		this.product_id = product_id;
		this.discount = discount;
		this.count = count;
		this.final_price = final_price;
		this.total_price = total_price;
		
	}
	
	public int getProductId(){
		return this.product.id;
	}
	
	public int getProductCatId(){
		return this.product.cat_id;
	}
	
	public void setDiscount(double dis){
		if(dis != 1.0 && dis >= 0){
			this.discount = dis;
		}else{
			this.discount = 1.0;
		}
	}
	public double getDiscount(){
		return this.discount;
	}
	
	public boolean setSoldPrice(int price){
		if(this.product.sticker_price == 0){
			this.sold_price = price;
			return true;
		}
		return false;
	}
	public int getSoldPrice(){
		return this.sold_price;
	}
	public int getFinalSoldPrice(){
		final_price = (int) Math.round(this.sold_price * this.discount);
		return final_price;
	}
	
	public String getFinalSoldPriceString(){
		if( this != null ){
			this.getFinalSoldPrice();
			return String.valueOf(final_price);
		}
		else 
			return "";
	}
	
	public boolean setCount(int count){
		if(count > 0){
			this.count = count;
			return true;
		}
		return false;
	}
	public int getCount(){
		return this.count;
	}

	public String getCountString(){
		if(this != null){
			return String.valueOf(this.count);
		}else
			return "";
	}
	
	public void addCount(int add){
		Log.v("Msg","Add in SoldProduct : "+ add);
		this.count += add;
		Log.v("Msg","Count : "+ this.count);
	}
	
	public int getTotalPrice(){
		total_price = this.getFinalSoldPrice() * this.count;
		return total_price;
	}
	
	public String getTotalPriceString(){
		if(this != null){
			this.getTotalPrice();
			return String.valueOf(total_price);
		} else 
			return "";
	}
	
	public String getSoldName(){
		if(this.discount != 1.0){
			return this.product.name + 
					"(" + discountMapping(this.discount)+")";
		}
		else {
			return this.product.name;
		}
	}
	
	private String discountMapping(Double discount)
	{
		String str = "";
		if(discount == 0.5)
			str = "¢´§é";
		if(discount == 0.6)
			str = "¢µ§é";
		if(discount == 0.7)
			str = "¢¶§é";
		if(discount == 0.8)
			str = "¢·§é";
		if(discount == 0.9)
			str = "¢¸§é";
		if(discount == 0)
			str = "©Û«Ý";
		return str;
	}
}
