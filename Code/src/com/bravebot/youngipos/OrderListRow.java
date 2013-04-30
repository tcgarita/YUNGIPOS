package com.bravebot.youngipos;

public class OrderListRow {
	public String id;
	public int paid;
	public String title0;
    public String title1;
    public String title2;
    public String title3;
    public String title4;
    public String title5;
    public OrderListRow(){
        super();
    }
    
    public OrderListRow(String id, int paid, String title0, String title1, String title2, String title3, String title4, String title5) {
        super();
        this.id = id;
        this.paid = paid;
        this.title0 = title0;
        this.title1 = title1;
        this.title2 = title2;
        this.title3 = title3;
        this.title4 = title4;
        this.title5 = title5;


    }
}
