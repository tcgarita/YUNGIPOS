package com.bravebot.youngipos;

public class ProductMap {
	int catagoryNo = 3;
	int lunchBoxNo = 13;
	int barbecueNo = 16;
	int ortherNo = 7;
	public String[][] productNames;
	public String[][] productNamesDiscount;
	public String[][] productNamesFree;
	public String[] categoryNames;
	public int[] categoryNos;
	public int[][] productPrice;
	public ProductMap() {
		categoryNos = new int[3];
		categoryNos[0] = 13;
		categoryNos[1] = 16;
		categoryNos[2] = 7;
		int maxNum = catagoryNo > lunchBoxNo? catagoryNo : lunchBoxNo;
		maxNum = maxNum > barbecueNo? maxNum :barbecueNo;
		categoryNames = new String[catagoryNo];
		categoryNames[0] = "便當";
		categoryNames[1] = "燒臘";
		categoryNames[2] = "粥品/小菜/其他";
		productNames = new String[catagoryNo][maxNum];
		productNames[0][0] = "招牌肥鵝飯　　　　　　";
		productNames[0][1] = "燒鵝四寶飯　　　　　　";
		productNames[0][2] = "燒鵝叉燒飯　　　　　　";
		productNames[0][3] = "燒鵝油雞飯　　　　　　";
		productNames[0][4] = "燒鵝香腸飯　　　　　　";
		productNames[0][5] = "油雞腿飯　　　　　　　";
		productNames[0][6] = "燒鵝三寶飯　　　　　　";
		productNames[0][7] = "叉燒油雞飯　　　　　　";
		productNames[0][8] = "叉燒香腸飯　　　　　　";
		productNames[0][9] = "香腸油雞飯　　　　　　";
		productNames[0][10]= "蜜汁叉燒飯　　　　　　";
		productNames[0][11]= "特級香腸飯　　　　　　";
		productNames[0][12]= "促銷燒鵝飯　　　　　　";
		
		productNames[1][0] = "燒鵝全隻　　　　　　　";
		productNames[1][1] = "燒鵝半隻　　　　　　　";
		productNames[1][2] = "燒鵝１／４隻　　　　　";
		productNames[1][3] = "燒鵝１份　　　　　　　";
		productNames[1][4] = "燒鵝　　　　　　　　　";
		productNames[1][5] = "鵝頭　　　　　　　　　";
		productNames[1][6] = "蜜汁叉燒肉１斤　　　　";
		productNames[1][7] = "蜜汁叉燒肉半斤　　　　";
		productNames[1][8] = "蜜汁叉燒肉１份　　　　";
		productNames[1][9] = "蜜汁叉燒肉　　　　　　";
		productNames[1][10]= "油雞腿１隻　　　　　　";
		productNames[1][11]= "特級香腸１條　　　　　";
		productNames[1][12]= "特級香腸半條　　　　　";
		productNames[1][13]= "特級香腸　　　　　　　";
		productNames[1][14]= "燒鵝拼盤大份　　　　　";
		productNames[1][15]= "燒鵝拼盤小份　　　　　";
		
		productNames[2][0] = "上湯紅蟳粥﹣大　　　　";
		productNames[2][1] = "上湯紅蟳粥﹣中　　　　";
		productNames[2][2] = "上湯紅蟳粥﹣小　　　　";
		productNames[2][3] = "紅酒滷花生１包　　　　";
		productNames[2][4] = "紅酒滷花生　　　　　　";
		productNames[2][5] = "白飯　　　　　　　　　";
		productNames[2][6] = "辦桌　　　　　　　　　";
		
		productNamesDiscount = new String[catagoryNo][maxNum];
		productNamesDiscount[0][0] = "招牌肥鵝飯（%s）　　";
		productNamesDiscount[0][1] = "燒鵝四寶飯（%s）　　";
		productNamesDiscount[0][2] = "燒鵝叉燒飯（%s）　　";
		productNamesDiscount[0][3] = "燒鵝油雞飯（%s）　　";
		productNamesDiscount[0][4] = "燒鵝香腸飯（%s）　　";
		productNamesDiscount[0][5] = "油雞腿飯（%s）　　　";
		productNamesDiscount[0][6] = "燒鵝三寶飯（%s）　　";
		productNamesDiscount[0][7] = "叉燒油雞飯（%s）　　";
		productNamesDiscount[0][8] = "叉燒香腸飯（%s）　　";
		productNamesDiscount[0][9] = "香腸油雞飯（%s）　　";
		productNamesDiscount[0][10]= "蜜汁叉燒飯（%s）　　";
		productNamesDiscount[0][11]= "特級香腸飯（%s）　　";
		productNamesDiscount[0][12]= "促銷燒鵝飯（%s）　　";
		
		productNamesDiscount[1][0] = "燒鵝全隻（%s）　　　";
		productNamesDiscount[1][1] = "燒鵝半隻（%s）　　　";
		productNamesDiscount[1][2] = "燒鵝１／４隻（%s）　";
		productNamesDiscount[1][3] = "燒鵝１份（%s）　　　";
		productNamesDiscount[1][4] = "燒鵝（%s）　　　　　";
		productNamesDiscount[1][5] = "鵝頭（%s）　　　　　";
		productNamesDiscount[1][6] = "蜜汁叉燒肉１斤（%s）";
		productNamesDiscount[1][7] = "蜜汁叉燒肉半斤（%s）";
		productNamesDiscount[1][8] = "蜜汁叉燒肉１份（%s）";
		productNamesDiscount[1][9] = "蜜汁叉燒肉（%s）　　";
		productNamesDiscount[1][10] = "油雞腿１隻（%s）　　";
		productNamesDiscount[1][11]= "特級香腸１條（%s）　";
		productNamesDiscount[1][12]= "特級香腸半條（%s）　";
		productNamesDiscount[1][13]= "特級香腸（%s）　　　";
		productNamesDiscount[1][14]= "燒鵝拼盤大份（%s）　";
		productNamesDiscount[1][15]= "燒鵝拼盤小份（%s）　";
		
		productNamesDiscount[2][0] = "上湯紅蟳粥﹣大（%s）";
		productNamesDiscount[2][1] = "上湯紅蟳粥﹣中（%s）";
		productNamesDiscount[2][2] = "上湯紅蟳粥﹣小（%s）";
		productNamesDiscount[2][3] = "紅酒滷花生１包（%s）";
		productNamesDiscount[2][4] = "紅酒滷花生（%s）　　";
		productNamesDiscount[2][5] = "白飯（%s）　　　　　";
		productNamesDiscount[2][6] = "辦桌（%s）　　　　　";
		
		productPrice = new int[catagoryNo][maxNum];
		
		productPrice[0][0] = 90;
		productPrice[0][1] = 85;
		productPrice[0][2] = 80;
		productPrice[0][3] = 80;
		productPrice[0][4] = 80;
		productPrice[0][5] = 75;
		productPrice[0][6] = 70;
		productPrice[0][7] = 65;
		productPrice[0][8] = 65;
		productPrice[0][9] = 65;
		productPrice[0][10]= 60;
		productPrice[0][11]= 60;
		productPrice[0][12]= 60;
		
		
		productPrice[1][0] = 1150;
		productPrice[1][1] = 600;
		productPrice[1][2] = 350;
		productPrice[1][3] = 200;
		productPrice[1][4] = 0;
		productPrice[1][5] = 50;
		productPrice[1][6] = 350;
		productPrice[1][7] = 200;
		productPrice[1][8] = 120;
		productPrice[1][9] = 0;
		productPrice[1][10] = 50;
		productPrice[1][11]= 100;
		productPrice[1][12]= 50;
		productPrice[1][13]= 0;
		productPrice[1][14]= 600;
		productPrice[1][15]= 350;
		
		productPrice[2][0] = 900;
		productPrice[2][1] = 600;
		productPrice[2][2] = 350;
		productPrice[2][3] = 100;
		productPrice[2][4] = 0;
		productPrice[2][5] = 0;
		productPrice[2][6] = 0;
	}
}
