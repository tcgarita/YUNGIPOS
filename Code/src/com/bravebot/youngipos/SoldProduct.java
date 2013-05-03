package com.bravebot.youngipos;

public class SoldProduct {
	private Product product;  // ²£«~
	private double discount;  // §é¦©
	private int sold_price;   // °â»ù
	private int count;        // ¼Æ¶q
	private String sold_name; // ¦WºÙ
	
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
		}
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
		return (int) Math.round(this.sold_price * this.discount);
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

	public void addCount(int add){
		this.count += add;
	}
	
	public int getTotalPrice(){
		return this.getFinalSoldPrice() * this.count;
	}
	
	public String getSoldName(){
		if(this.discount != 1.0){
			return this.product.name + 
					" (" + discountMapping(this.discount)+")";
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
