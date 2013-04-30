package com.bravebot.youngipos;

public class ListRow {
    public String title0;
    public String title1;
    public String title2;
    public String title3;
    public double discount = 1.0;
    public int productNO;
    public ListRow(){
        super();
    }
    
    public ListRow(String title0, String title1, String title2, String title3, double discount, int pno) {
        super();
        this.title0 = title0;
        this.title1 = title1;
        this.title2 = title2;
        this.title3 = title3;
        this.discount = discount;
        this.productNO = pno;

    }
}
