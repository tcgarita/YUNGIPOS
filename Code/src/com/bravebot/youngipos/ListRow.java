package com.bravebot.youngipos;

public class ListRow {
    public String title0;
    public String title1;
    public String title2;
    public String title3;
    public double discount = 1.0;
    public int productNO; // cat_id
    public int prdouct_id;
    
    public ListRow(){
        super();
    }
  
    public ListRow(String title0, int price, 
    		int count, int final_price, double discount, int pno,int product_id) {
        super();
        this.title0 = title0;
        this.title1 = String.valueOf(price);
        this.title2 = String.valueOf(count);
        this.title3 = String.valueOf(final_price);
        this.discount = discount;
        this.productNO = pno;
        this.prdouct_id = product_id;
    }
}
