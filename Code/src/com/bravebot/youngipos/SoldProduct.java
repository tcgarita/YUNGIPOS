package com.bravebot.youngipos;

import android.util.Log;

public class SoldProduct {
	private Product product;  // ���~
	private double discount;  // �馩
	private int sold_price;   // ���
	private int count;        // �ƶq
	private int total_price, final_price;  
	private int product_id;
	private String name_from_db;// from database 
	
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
	public SoldProduct(int product_id, double discount, int count, int final_price, int total_price, String name_from_db){
		this.product = MainActivity.dbhelper.getProductById(product_id);
		if(product == null){
			this.sold_price = 0;
		}
		else 
			this.sold_price = product.sticker_price;
		this.product_id = product_id;
		this.discount = discount;
		this.count = count;
		this.final_price = final_price;
		this.total_price = total_price;
		this.name_from_db = name_from_db;
		Log.v("Msg","final_price:"+final_price+" total_price:"+total_price+" discount:"+discount);
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
		this.final_price = (int) Math.round(this.sold_price * this.discount);
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
		if(product != null && this.sold_price != 0)
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
		String name;
		if(product != null)
			name = product.name;
		else
			name = name_from_db;
		
		if(this.discount != 1.0)
			return name + 
					"(" + discountMapping(this.discount)+")";
		else 
			return name;
	}
	
	private String discountMapping(Double discount)
	{
		String str = "";
		if(discount == 0.5)
			str = "５折";
		if(discount == 0.6)
			str = "６折";
		if(discount == 0.7)
			str = "７折";
		if(discount == 0.8)
			str = "８折";
		if(discount == 0.9)
			str = "９折";
		if(discount == 0)
			str = "招待";
		return str;
	}
}
