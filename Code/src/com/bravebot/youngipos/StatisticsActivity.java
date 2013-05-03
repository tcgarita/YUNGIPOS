package com.bravebot.youngipos;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.starmicronics.stario.PortInfo;
import com.starmicronics.stario.StarIOPort;
import com.starmicronics.stario.StarIOPortException;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


public class StatisticsActivity extends FragmentActivity implements PopupPayInputWindow.OnClickPayButtonListener ,
PopupSubmitInputWindow.OnClickSubmitButtonListener{
	
	private ArrayList<StatisticsCategoryRow> row_data_category;
	private ArrayList<StatisticsCategoryRow> row_data_detail;
	private ArrayList<StatisticsCategoryRow> row_data_detail_0;
	private ArrayList<StatisticsCategoryRow> row_data_detail_1;
	private ArrayList<StatisticsCategoryRow> row_data_detail_2;
	private ArrayList<StatisticsCategoryRow> row_data_detail_3;
	private ListView listViewCategory;
	private ListView listViewDetail;
	private StatisticsCategoryRowAdapter adapter;
	private StatisticsCategoryRowAdapter adapter_detail;
	private StatisticsCategoryRow selectedRow;
	private int categoryNumbers[];
	private int categoryNumber1 = 0;
	private int categoryNumber2 = 0;
	private int categoryNumber3 = 0;
	private int categoryNumbersTotal[];
	private int categoryNumber1Total = 0;
	private int categoryNumber2Total = 0;
	private int categoryNumber3Total = 0;
	private String categoryMoneysTotal[];
	private String categoryMoneys[];
	private ProductMap productMap;
	private int mode = 0;
	private Button submitButton;
	private Button submitButtonTotal;
	private PopupWindow popWindow;
	private int todayMoney;
	private int payMoney;
	private int submitMoney;
	private int monthMoney;
	private int SN;
	private String DATAURL = "http://lime.csie.ntu.edu.tw/~bani/yunggi/UploadData.php";
	private String BILLURL = "http://lime.csie.ntu.edu.tw/~bani/yunggi/UploadBill.php";
	private String GROOVEBILLURL = "http://lime.csie.ntu.edu.tw/~bani/yunggi/UploadGrooveCount.php";
	private ProgressDialog loadingDialog;
	private HashMap<Double, Integer> priceMap;
	private HashMap<Double, Integer> priceMapTotal;
	private ArrayList<String> SNs;
	private ArrayList<Integer> totalMoneys;
	private Boolean isShow = false;
	private Boolean isSubmit = false;
	private Boolean isPay = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		SharedPreferences settings = getSharedPreferences ("POS_ORDER", 0);
		
		SN = settings.getInt("SN", 1);
		Bundle bundle = this.getIntent().getExtras();
		mode = bundle.getInt("mode");
		if(mode == 1)
		{
	    	setTitle("日結作業");
	    	setContentView(R.layout.accounting_activity);
		}
		else
			setContentView(R.layout.statistics_data_activity);
		productMap = new ProductMap();
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
		
		this.initViews();
		getStatisticsNumberFromDatabase();
		row_data_category = new ArrayList<StatisticsCategoryRow>();
		row_data_category.add(new StatisticsCategoryRow("總覽", String.valueOf(categoryNumber1 + categoryNumber2 + categoryNumber3)));
		row_data_category.add(new StatisticsCategoryRow("便當", String.valueOf(categoryNumber1)));
		row_data_category.add(new StatisticsCategoryRow("燒臘", String.valueOf(categoryNumber2)));
		row_data_category.add(new StatisticsCategoryRow("粥品/小菜/其他", String.valueOf(categoryNumber3)));
		initDetail0();
		row_data_detail_1 = initDetail(0, "便當", categoryNumber1);
		row_data_detail_2 = initDetail(1, "燒臘", categoryNumber2);
		row_data_detail_3 = initDetail(2, "粥品/小菜/其他", categoryNumber3);
		
		adapter = new StatisticsCategoryRowAdapter(this, R.layout.statistics_category_row, row_data_category);
		listViewCategory.setAdapter(adapter);
		row_data_detail = new ArrayList<StatisticsCategoryRow>();
		adapter_detail = new StatisticsCategoryRowAdapter(this, R.layout.statistics_list_row, row_data_detail);
		listViewDetail.setAdapter(adapter_detail);
	    
	    listViewCategory.setOnItemClickListener(clickListItem);
	    if(row_data_category.size() > 0)
	    	listViewCategory.performItemClick(listViewCategory.getAdapter().getView(0, null, null), 0, 0);
	    categoryMoneys = new String[3];
	    
	    
	}
	private int getWaitBillNumber()
	{
		SQLiteDatabase db = MainActivity.dbhelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from " + DBConstants.ORDER_TABLE_NAME + " WHERE " + DBConstants.ORDER_STATUS + " =0"
				+ " AND " + DBConstants.ORDER_DETAIL_DELETE + "=0", null);
		return cursor.getCount();
	}
	private void initDetail0() {
		SharedPreferences settings = getSharedPreferences ("POS_ORDER", 0);
		int cashBoxMoney = settings.getInt("CashBoxMoney", 0);
		todayMoney = settings.getInt("TodayMoney", 0);
		int waitMoney = settings.getInt("WaitMoney", 0);
		monthMoney = settings.getInt("MonthMoney", 0);
		
		NumberFormat formatter = new DecimalFormat("###,###,###");
		row_data_detail_0 = new ArrayList<StatisticsCategoryRow>();
		row_data_detail_0.add(new StatisticsCategoryRow("櫃台現金", formatter.format(cashBoxMoney)));
		row_data_detail_0.add(new StatisticsCategoryRow("營業額", formatter.format(monthMoney + todayMoney + waitMoney)));
		row_data_detail_0.add(new StatisticsCategoryRow("營業現金", formatter.format(todayMoney)));
		row_data_detail_0.add(new StatisticsCategoryRow("待收帳", formatter.format(waitMoney)));
		row_data_detail_0.add(new StatisticsCategoryRow("月結帳", formatter.format(monthMoney)));
		row_data_detail_0.add(new StatisticsCategoryRow("總銷售量", formatter.format(categoryNumber1 + categoryNumber2 + categoryNumber3)));
	}
	private ArrayList<StatisticsCategoryRow> initDetail(int categoryNo, String title, int categoryNumber) {
		NumberFormat formatter = new DecimalFormat("###,###,###");
		ArrayList<StatisticsCategoryRow> row_data_detail = new ArrayList<StatisticsCategoryRow>();
		row_data_detail.add(new StatisticsCategoryRow(title + "營業額", getCategoryTotalAmount(categoryNo)));
		row_data_detail.add(new StatisticsCategoryRow(title + "銷售量", formatter.format(categoryNumber)));
		for(int i = 0; i < productMap.categoryNos[categoryNo]; i++)
		{
			HashMap<Double, Integer> map = getProductNumber(categoryNo * 1000 + i);
			//row_data_detail.add(new StatisticsCategoryRow(productMap.productNames[categoryNo][i], 
		//			formatter.format(number) + "   =>  ＄" + formatter.format(number * productMap.productPrice[categoryNo][i])));
			for(Double key:map.keySet())
			{
				if(key == 1.0)
					row_data_detail.add(new StatisticsCategoryRow(productMap.productNames[categoryNo][i].replace("　", ""), String.valueOf(map.get(key))));
				else
					row_data_detail.add(new StatisticsCategoryRow(String.format(productMap.productNamesDiscount[categoryNo][i], discountMapping(key)).replace("　", ""), String.valueOf(map.get(key))));
			}
		}
		return row_data_detail;
		
		
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
	
	private ArrayList<Integer> getTotalMoneys()
	{
		
		SQLiteDatabase db = MainActivity.dbhelper.getReadableDatabase();
		
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		String dateString = dateFormat.format(new Date());
		Cursor cursor = db.rawQuery("select SUM("+ DBConstants.BILL_TODAY_MONEY + "), SUM(" + DBConstants.BILL_MOTH_MONEY 
				+ "), SUM(" + DBConstants.BILL_PAY_MONEY + "), SUM(" + DBConstants.BILL_SUBMIT_MONEY
				+ ")  from " + DBConstants.BILL_TABLE_NAME +
				" WHERE " + DBConstants.BILL_DATE + "=" + String.valueOf(dateString) , null);
		cursor.moveToFirst();
		ArrayList<Integer> moneys = new ArrayList<Integer>();
		for(int i=0; i< cursor.getCount(); i++)
		{
			moneys.add(cursor.getInt(0));
			moneys.add(cursor.getInt(1));
			moneys.add(cursor.getInt(2));
			moneys.add(cursor.getInt(3));
		}
		return moneys;
	}
	
	private void getStatisticsNumberFromDatabase() {
		SQLiteDatabase db = MainActivity.dbhelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select SUM(" + DBConstants.ORDER_DETAIL_AMOUNT + ") from " + DBConstants.ORDER_DETAIL_TABLE_NAME +
				" WHERE " + DBConstants.ORDER_DETAIL_PRODUCT_ID + ">=0 AND " + DBConstants.ORDER_DETAIL_PRODUCT_ID + "<1000"
				+ " AND " + DBConstants.ORDER_DETAIL_SN + "=" + String.valueOf(SN) +
				" AND " + DBConstants.ORDER_DETAIL_DELETE + "=0" , null);
		cursor.moveToFirst();
		categoryNumber1 = cursor.getInt(0);//取得資料表列數
		cursor = db.rawQuery("select SUM(" + DBConstants.ORDER_DETAIL_AMOUNT + ") from " + DBConstants.ORDER_DETAIL_TABLE_NAME +
				" WHERE " + DBConstants.ORDER_DETAIL_PRODUCT_ID + ">=1000 AND " + DBConstants.ORDER_DETAIL_PRODUCT_ID + "<2000"
				+ " AND " + DBConstants.ORDER_DETAIL_SN + "=" + String.valueOf(SN) + 
				" AND " + DBConstants.ORDER_DETAIL_DELETE + "=0" , null);
		cursor.moveToFirst();
		categoryNumber2 = cursor.getInt(0);//取得資料表列數
		cursor = db.rawQuery("select SUM(" + DBConstants.ORDER_DETAIL_AMOUNT + ") from " + DBConstants.ORDER_DETAIL_TABLE_NAME +
				" WHERE " + DBConstants.ORDER_DETAIL_PRODUCT_ID + ">=2000 AND " + DBConstants.ORDER_DETAIL_PRODUCT_ID + "<3000" 
				+ " AND " + DBConstants.ORDER_DETAIL_SN + "=" + String.valueOf(SN) + 
				" AND " + DBConstants.ORDER_DETAIL_DELETE + "=0" , null);
		cursor.moveToFirst();
		categoryNumber3 = cursor.getInt(0);//取得資料表列數
		categoryNumbers = new int[3];
		categoryNumbers[0] = categoryNumber1;
		categoryNumbers[1] = categoryNumber2;
		categoryNumbers[2] = categoryNumber3;
		
	}
	
	private void getStatisticsNumberFromDatabaseTotal() {
		
		categoryNumber1Total = 0;
		categoryNumber2Total = 0;
		categoryNumber3Total = 0;
		
		for(String thisSN : SNs)
		{
			SQLiteDatabase db = MainActivity.dbhelper.getReadableDatabase();
			Cursor cursor = db.rawQuery("select SUM(" + DBConstants.ORDER_DETAIL_AMOUNT + ") from " + DBConstants.ORDER_DETAIL_TABLE_NAME +
					" WHERE " + DBConstants.ORDER_DETAIL_PRODUCT_ID + ">=0 AND " + DBConstants.ORDER_DETAIL_PRODUCT_ID + "<1000"
					+ " AND " + DBConstants.ORDER_DETAIL_SN + "=" + String.valueOf(thisSN) +
					" AND " + DBConstants.ORDER_DETAIL_DELETE + "=0" , null);
			cursor.moveToFirst();
			categoryNumber1Total += cursor.getInt(0);//取得資料表列數
			cursor = db.rawQuery("select SUM(" + DBConstants.ORDER_DETAIL_AMOUNT + ") from " + DBConstants.ORDER_DETAIL_TABLE_NAME +
					" WHERE " + DBConstants.ORDER_DETAIL_PRODUCT_ID + ">=1000 AND " + DBConstants.ORDER_DETAIL_PRODUCT_ID + "<2000"
					+ " AND " + DBConstants.ORDER_DETAIL_SN + "=" + String.valueOf(thisSN) + 
					" AND " + DBConstants.ORDER_DETAIL_DELETE + "=0" , null);
			cursor.moveToFirst();
			categoryNumber2Total += cursor.getInt(0);//取得資料表列數
			cursor = db.rawQuery("select SUM(" + DBConstants.ORDER_DETAIL_AMOUNT + ") from " + DBConstants.ORDER_DETAIL_TABLE_NAME +
					" WHERE " + DBConstants.ORDER_DETAIL_PRODUCT_ID + ">=2000 AND " + DBConstants.ORDER_DETAIL_PRODUCT_ID + "<3000" 
					+ " AND " + DBConstants.ORDER_DETAIL_SN + "=" + String.valueOf(thisSN) + 
					" AND " + DBConstants.ORDER_DETAIL_DELETE + "=0" , null);
			cursor.moveToFirst();
			categoryNumber3Total += cursor.getInt(0);//取得資料表列數
			
		}
		categoryNumbersTotal = new int[3];
		categoryNumbersTotal[0] = categoryNumber1Total;
		categoryNumbersTotal[1] = categoryNumber2Total;
		categoryNumbersTotal[2] = categoryNumber3Total;
		
	}

	private String getCategoryTotalAmount(int categoryNo)
	{
		NumberFormat formatter = new DecimalFormat("###,###,###");
		SQLiteDatabase db = MainActivity.dbhelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select SUM(" + DBConstants.ORDER_DETAIL_TOTAL + ") from " + DBConstants.ORDER_DETAIL_TABLE_NAME +
				" WHERE " + DBConstants.ORDER_DETAIL_PRODUCT_ID + ">=" + String.valueOf(1000 * categoryNo) + " AND "
				+ DBConstants.ORDER_DETAIL_PRODUCT_ID + "<" + String.valueOf(1000 * (categoryNo + 1)) 
				+ " AND " + DBConstants.ORDER_DETAIL_SN + "=" + String.valueOf(SN) +
				" AND " + DBConstants.ORDER_DETAIL_DELETE + "=0" , null);
		cursor.moveToFirst();
		if(cursor.getString(0) != null)
			return formatter.format(cursor.getInt(0));//取得資料表列數
		else
			return "0";
		
	}
	
	private int[] getGrooveAmountBarbecue()
	{
		SQLiteDatabase db = MainActivity.dbhelper.getReadableDatabase();
		int[] amounts = new int[6];
		amounts[0] = 0;
		amounts[1] = 0;
		amounts[2] = 0;
		amounts[3] = 0;
		amounts[4] = 0;
		amounts[5] = 0;
		int[] maps = new int[6];
		maps[0] = 0;
		maps[1] = 1;
		maps[2] = 2;
		maps[3] = 3;
		maps[4] = 14;
		maps[5] = 15;
		for(int i=0; i < maps.length; i++)
		{
			Cursor cursor = db.rawQuery("select SUM(" + DBConstants.ORDER_DETAIL_AMOUNT + ") from " + DBConstants.ORDER_DETAIL_TABLE_NAME +
					" WHERE " + DBConstants.ORDER_DETAIL_PRODUCT_ID + "=" + String.valueOf(1000 + maps[i]) + " AND " + DBConstants.ORDER_DETAIL_SN + "=" + String.valueOf(SN) +
					" AND " + DBConstants.ORDER_DETAIL_DELETE + "=0" , null);
			cursor.moveToFirst();
			if(cursor.getString(0) != null)
			{
				amounts[i] += cursor.getInt(0);
			}
				
		}
		return amounts;
	}
	
	private int[] getGrooveTotalAmountLunchBox()
	{
		int[] amounts = new int[4];
		amounts[0] = 0;
		amounts[1] = 0;
		amounts[2] = 0;
		amounts[3] = 0;
		int[] maps = new int[7];
		maps[0] = 0;
		maps[1] = 2;
		maps[2] = 3;
		maps[3] = 4;
		maps[4] = 6;
		maps[5] = 1;
		maps[6] = 12;
		SQLiteDatabase db = MainActivity.dbhelper.getReadableDatabase();
		for(int i=0; i < maps.length; i++)
		{
			Cursor cursor = db.rawQuery("select SUM(" + DBConstants.ORDER_DETAIL_AMOUNT + ") from " + DBConstants.ORDER_DETAIL_TABLE_NAME +
					" WHERE " + DBConstants.ORDER_DETAIL_PRODUCT_ID + "=" + String.valueOf(maps[i]) + " AND " + DBConstants.ORDER_DETAIL_SN + "=" + String.valueOf(SN) +
					" AND " + DBConstants.ORDER_DETAIL_DELETE + "=0" , null);
			cursor.moveToFirst();
			if(cursor.getString(0) != null)
			{
				if(i == 0)
					amounts[0] += cursor.getInt(0);
				else if(i >= 1 && i <= 3)
					amounts[1] += cursor.getInt(0);
				else if(i == 4)
					amounts[2] += cursor.getInt(0);
				else if(i == 5)
					amounts[3] += cursor.getInt(0);
				else if(i == 6)
					amounts[0] += cursor.getInt(0);
			}
				
		}
		return amounts;
	}
	
	private String getCategoryTotalAmountTotal(int categoryNo)
	{
		int totalAmount = 0;
		for(String thisSN : SNs)
		{
			
			SQLiteDatabase db = MainActivity.dbhelper.getReadableDatabase();
			Cursor cursor = db.rawQuery("select SUM(" + DBConstants.ORDER_DETAIL_TOTAL + ") from " + DBConstants.ORDER_DETAIL_TABLE_NAME +
					" WHERE " + DBConstants.ORDER_DETAIL_PRODUCT_ID + ">=" + String.valueOf(1000 * categoryNo) + " AND "
					+ DBConstants.ORDER_DETAIL_PRODUCT_ID + "<" + String.valueOf(1000 * (categoryNo + 1)) 
					+ " AND " + DBConstants.ORDER_DETAIL_SN + "=" + String.valueOf(thisSN) +
					" AND " + DBConstants.ORDER_DETAIL_DELETE + "=0" , null);
			cursor.moveToFirst();
			if(cursor.getString(0) != null)
				totalAmount += cursor.getInt(0);
			
		}
		NumberFormat formatter = new DecimalFormat("###,###,###");
	
		return formatter.format(totalAmount);//取得資料表列數
	
		
	}
	/*
	private int getProductNumber(int productNo)
	{
		SQLiteDatabase db = MainActivity.dbhelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select SUM(" + DBConstants.ORDER_DETAIL_AMOUNT + ") from " + DBConstants.ORDER_DETAIL_TABLE_NAME +
				" WHERE " + DBConstants.ORDER_DETAIL_PRODUCT_ID + "=" + String.valueOf(productNo) , null);
		cursor.moveToFirst();
		if(cursor.getString(0) != null)
			return cursor.getInt(0);//取得資料表列數
		else
			return 0;	 
		
	}
	*/
	private ArrayList<String> getTodaySNs()
	{
		ArrayList<String> SNs = new ArrayList<String>();
		SQLiteDatabase db = MainActivity.dbhelper.getReadableDatabase();
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		String dateString = dateFormat.format(new Date());
		Cursor cursor = db.rawQuery("select "+ DBConstants.ORDER_SN + "  from " + DBConstants.BILL_TABLE_NAME +
				" WHERE " + DBConstants.BILL_DATE + "=" + String.valueOf(dateString) , null);
		cursor.moveToFirst();
		for(int i=0; i< cursor.getCount(); i++)
		{
			SNs.add(cursor.getString(0));
			cursor.moveToNext();
		}
		return SNs;
	}
	private HashMap<Double, Integer> getProductNumber(int productNo)
	{
		priceMap = new HashMap<Double, Integer>();
		HashMap<Double, Integer> map = new HashMap<Double, Integer>();
		SQLiteDatabase db = MainActivity.dbhelper.getReadableDatabase();

		Cursor cursor = db.rawQuery("select "+ DBConstants.ORDER_DETAIL_DISCOUNT + ", SUM(" + DBConstants.ORDER_DETAIL_AMOUNT + "), SUM("
				+ DBConstants.ORDER_DETAIL_TOTAL + ") from " + DBConstants.ORDER_DETAIL_TABLE_NAME +
				" WHERE " + DBConstants.ORDER_DETAIL_PRODUCT_ID + "=" + String.valueOf(productNo) + " AND " + DBConstants.ORDER_DETAIL_SN + "=" + String.valueOf(SN)
				+" AND " + DBConstants.ORDER_DETAIL_DELETE + "=0"
				+ " GROUP BY " + DBConstants.ORDER_DETAIL_DISCOUNT +" Order by -" + DBConstants.ORDER_DETAIL_DISCOUNT, null);
		cursor.moveToFirst();
		for(int i=0; i< cursor.getCount(); i++)
		{
			map.put(cursor.getDouble(0), cursor.getInt(1));
			priceMap.put(cursor.getDouble(0), cursor.getInt(2));
			cursor.moveToNext();
		}
		return map;//取得資料表列數 
		
	}
	
	private HashMap<Double, Integer> getProductNumberTotal(int productNo, ArrayList<String> thisSNs)
	{
		priceMap = new HashMap<Double, Integer>();
		priceMapTotal = new HashMap<Double, Integer>();
		HashMap<Double, Integer> map = new HashMap<Double, Integer>();
		for(String thisSN:thisSNs)
		{
			SQLiteDatabase db = MainActivity.dbhelper.getReadableDatabase();
			Cursor cursor = db.rawQuery("select "+ DBConstants.ORDER_DETAIL_DISCOUNT + ", SUM(" + DBConstants.ORDER_DETAIL_AMOUNT + "), SUM("
					+ DBConstants.ORDER_DETAIL_TOTAL + ") from " + DBConstants.ORDER_DETAIL_TABLE_NAME +
					" WHERE " + DBConstants.ORDER_DETAIL_PRODUCT_ID + "=" + String.valueOf(productNo) + " AND " + DBConstants.ORDER_DETAIL_SN + "=" + String.valueOf(thisSN)
					+" AND " + DBConstants.ORDER_DETAIL_DELETE + "=0"
					+ " GROUP BY " + DBConstants.ORDER_DETAIL_DISCOUNT +" Order by -" + DBConstants.ORDER_DETAIL_DISCOUNT, null);
			cursor.moveToFirst();
			for(int i=0; i< cursor.getCount(); i++)
			{
				if(map.get(cursor.getDouble(0)) != null)
					map.put(cursor.getDouble(0), map.get(cursor.getDouble(0)) + cursor.getInt(1));
				else
					map.put(cursor.getDouble(0), cursor.getInt(1));
				if(priceMapTotal.get(cursor.getDouble(0)) != null)
					priceMapTotal.put(cursor.getDouble(0), priceMapTotal.get(cursor.getDouble(0)) + cursor.getInt(2));
				else
					priceMapTotal.put(cursor.getDouble(0), cursor.getInt(2));
				cursor.moveToNext();
			}
		}
		return map;//取得資料表列數 
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
	}
	
	private void initViews()
	{
        listViewCategory = (ListView)findViewById(R.id.ListViewCategory);
        listViewDetail = (ListView)findViewById(R.id.ListViewDetail);
        popWindow = new PopupWindow(1280, 748);
        if(mode == 1)
        {
        	submitButton = (Button)findViewById(R.id.buttonSubmit); 
        	submitButton.setOnClickListener(clickSubmitButton);
        	submitButtonTotal = (Button)findViewById(R.id.ButtonSubmitAll); 
        	submitButtonTotal.setOnClickListener(clickSubmitButtonTotal);
        }
        
		
	}
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

	    int itemId = item.getItemId();
	    switch (itemId) {	
	    	case android.R.id.home:
	    		this.finish();

	    		break;
	    	case R.id.menu_back:
	    		this.finish();

	    		break;
	    

	    }

	    return true;
	}
	private OnItemClickListener clickListItem = new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long id) {
			adapter.setSelectedItem(position);
			StatisticsCategoryRow row = row_data_category.get(position);
			selectedRow = row;
			row_data_detail.clear();
			if(position == 0)
				row_data_detail.addAll(row_data_detail_0);
			else if(position == 1)
				row_data_detail.addAll(row_data_detail_1);
			else if(position == 2)
				row_data_detail.addAll(row_data_detail_2);
			else if(position == 3)
				row_data_detail.addAll(row_data_detail_3);
			
			adapter_detail.notifyDataSetChanged();
			
			
		}
    	
    };
    private Button.OnClickListener clickSubmitButton = new Button.OnClickListener()
	{

		@Override
		public void onClick(View v) {
			if(popWindow.isShowing() == false)
			{
				if(getWaitBillNumber() > 0)
				{
					Builder dialog = new AlertDialog.Builder(v.getContext());
					dialog.setTitle("提醒");
					dialog.setMessage("還有待結訂單未結帳\n是否刪除所有待結訂單?");
				    
				    dialog.setPositiveButton("刪除", alertDeleteClick);
				    dialog.setNegativeButton("取消", alertCancelClick);
				    AlertDialog dialogView = dialog.show();
				    TextView textView = (TextView) dialogView.findViewById(android.R.id.message);
				    textView.setTextSize(26);
				    Button btn1 = dialogView.getButton(DialogInterface.BUTTON_POSITIVE);
				    btn1.setTextSize(24);
				    btn1.setTextColor(Color.RED);
				    Button btn2 = dialogView.getButton(DialogInterface.BUTTON_NEGATIVE);
				    btn2.setTextSize(24);
				    btn2.setTextColor(Color.GREEN);
				}
				else
				{
					isPay = true;
					PopupPayInputWindow popupNumberInputWindow = new PopupPayInputWindow(LayoutInflater.from(v.getContext()), getResources());
					popupNumberInputWindow.setCallback((Activity) v.getContext());
					popWindow = new PopupWindow(1280, 748);
					popWindow.setContentView(popupNumberInputWindow.popView);
					popWindow.update();
					popWindow.setFocusable(true);
					popWindow.showAtLocation(v, Gravity.LEFT|Gravity.TOP, 0, 0);
				}
			}
			
		}

		
		
	};
	private Button.OnClickListener clickSubmitButtonTotal = new Button.OnClickListener()
	{

		@Override
		public void onClick(View v) {
			Builder dialog2 = new AlertDialog.Builder(v.getContext());
    		dialog2.setTitle("今日總結表");
    		dialog2.setMessage("表單已列印！");
    		dialog2.setNeutralButton("確定", null);
		    dialog2.show();
		    
		   
			printAccountingTotal(1);
			
			
		}

		
		
	};
	private DialogInterface.OnClickListener alertDeleteClick = new DialogInterface.OnClickListener()
	{

		@Override
		public void onClick(DialogInterface dialog, int which) {
			removeWaitBillToDatabase();
			isPay = true;
			PopupPayInputWindow popupNumberInputWindow = new PopupPayInputWindow(LayoutInflater.from(getWindow().getDecorView().findViewById(android.R.id.content).getContext()), getResources());
			popupNumberInputWindow.setCallback((Activity) getWindow().getDecorView().findViewById(android.R.id.content).getContext());
			popWindow = new PopupWindow(1280, 748);
			popWindow.setContentView(popupNumberInputWindow.popView);
			popWindow.update();
			popWindow.setFocusable(true);
			popWindow.showAtLocation(getWindow().getDecorView().findViewById(android.R.id.content), Gravity.LEFT|Gravity.TOP, 0, 0);
			
		}
		
	};
	private void removeWaitBillToDatabase()
	{
		SQLiteDatabase db = MainActivity.dbhelper.getWritableDatabase();
		Cursor cursor = db.rawQuery("select _id from " + DBConstants.ORDER_TABLE_NAME +
				" WHERE " + DBConstants.ORDER_STATUS + "=0  AND " + DBConstants.ORDER_SN + "=" + String.valueOf(SN) , null);
		cursor.moveToFirst();
		for(int i = 0; i < cursor.getCount(); i++)
		{	
			db.execSQL("UPDATE " + DBConstants.ORDER_DETAIL_TABLE_NAME + " SET " + DBConstants.ORDER_DELETE + "=? WHERE "
					+ DBConstants.ORDER_DETAIL_ORDER_ID + "=?", new Object[]{1, cursor.getString(0)});
			
			cursor.moveToNext();
		}
		db.execSQL("UPDATE " + DBConstants.ORDER_TABLE_NAME + " SET " + DBConstants.ORDER_DELETE + "=? " +
				"WHERE " + DBConstants.ORDER_STATUS + "=0");
		db.close();
		
	}
	private DialogInterface.OnClickListener alertCancelClick = new DialogInterface.OnClickListener()
	{

		@Override
		public void onClick(DialogInterface dialog, int which) {
		
		}
		
	};
	
	
	@Override
	public void onPayEnterClicked(int count) {
		popWindow.dismiss();
		if(count != -1 && isPay == true)
		{
			isPay = false;
			isSubmit = true;
			payMoney = count;
			
			PopupSubmitInputWindow popupSubmitInputWindow = new PopupSubmitInputWindow(LayoutInflater.from(this), getResources(), todayMoney, count);
			popupSubmitInputWindow.setCallback(this);
			popWindow = new PopupWindow(1280, 748);
			popWindow.setContentView(popupSubmitInputWindow.popView);
			popWindow.update();
			popWindow.setFocusable(true);
			popWindow.showAtLocation(getWindow().getDecorView().findViewById(android.R.id.content), Gravity.LEFT|Gravity.TOP, 0, 0);
		}
	}
	@Override
	public void onSubmitEnterClicked(int count) {
		popWindow.dismiss();
		if(count != -1 && isSubmit == true)
		{
			isSubmit = false;
			submitMoney = count;
			Builder dialog2 = new AlertDialog.Builder(this);
    		dialog2.setTitle("上傳日結資料確認");
    		dialog2.setMessage("今日營業現金："+ String.valueOf(todayMoney) +"\n"
    				+ "支出金額："+ String.valueOf(payMoney) + "\n"
    				+ "預計上繳金額："+ String.valueOf(todayMoney - payMoney) + "\n"
    				+ "實際上繳金額："+ String.valueOf(submitMoney) + "\n"
    				//+ "誤差金額："+ String.valueOf(todayMoney - payMoney- submitMoney) + "\n"
    				);
    		dialog2.setNeutralButton("確定", confirmClick);
		    AlertDialog dialogView = dialog2.show();
		    TextView textViewMsg = (TextView) dialogView.findViewById(android.R.id.message);
		    textViewMsg.setTextSize(40);
		    textViewMsg.setGravity(Gravity.CENTER);
		    textViewMsg.setTextColor(Color.RED);
		    final int alertTitle = getResources().getIdentifier( "alertTitle", "id", "android" );
		    TextView textViewTitle = (TextView) dialogView.findViewById(alertTitle);
		    
		    textViewTitle.setGravity(Gravity.CENTER);
		    textViewTitle.setTextSize(40);
		
		    Button btn1 = dialogView.getButton(DialogInterface.BUTTON_NEUTRAL);
		    btn1.setTextSize(40);
		    btn1.setTextColor(Color.BLUE);
		    
			
		}
		
		
		
	}
	private DialogInterface.OnClickListener confirmClick = new DialogInterface.OnClickListener()
	{

		@Override
		public void onClick(DialogInterface dialog, int which) {
			
			printAccounting(1);
		}
		
	};
	private void printAccounting(int times)
	{
		loadingDialog = ProgressDialog.show(this, "", "上傳資料中請稍待...", true);
		StarIOPort port = null;
		try 
    	{			
			SharedPreferences pref =  getSharedPreferences("POS_ORDER", 0);
			String portName = pref.getString("portName", "");
			String storeSN = pref.getString("StoreSN", "");
			SNs = getTodaySNs();
			if(portName.equalsIgnoreCase(""))
			{
				clickbuttonRecieve();
				uloadData();
				clearData();
				return;
			}
			port = StarIOPort.getPort(portName, "", 10000, this);
			
				
			//open cash drawer
			//byte[] openCashDrawer = new byte[] {0x07};
			//port.writePort(openCashDrawer, 0, openCashDrawer.length);
			//print recipe
			String storeName = "";
			if(storeSN.equals("YG0001"))
				storeName = "自由店";
			else if(storeSN.equals("YG0002"))
				storeName = "旗艦店";
			else if(storeSN.equals("YG0003"))
				storeName = "鳳山店";
			byte[] outputByteBuffer=null;
			port.writePort(new byte[]{0x1b, 0x40}, 0, 2);                         // Initialization

			port.writePort(new byte[]{0x1b, 0x24, 0x31}, 0, 3);                   // 漢字モード設定
                               
			
				port.writePort(new byte[]{0x1b, 0x44, 0x10, 0x00}, 0, 4);             // 水平タブ位置設定
				port.writePort(new byte[]{0x1b, 0x1d, 0x61, 0x31}, 0, 4);             // 中央揃え設定
					
				port.writePort(new byte[]{0x1b, 0x69, 0x02, 0x00}, 0, 4);             // 文字縦拡大設定
				port.writePort(new byte[]{0x1b, 0x45}, 0, 2); 							// 強調印字設定
				outputByteBuffer = createShiftBIG5("香港湧記燒鵝" + "\n");
				port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
				outputByteBuffer = createShiftBIG5(storeName + "\n");
				port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
				
				port.writePort(new byte[]{0x1b, 0x69, 0x01, 0x00}, 0, 4);             // 文字縦拡大設定
				Date now = new Date();
				String nowDate = new SimpleDateFormat("yyyy/MM/dd").format(now);
				outputByteBuffer = createShiftBIG5(nowDate + "日結單 #" + String.valueOf(SNs.size() + 1) + "\n");
				port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
				
				port.writePort(new byte[]{0x1b, 0x46}, 0, 2);                         // 強調印字解除
				
				//port.writePort(new byte[]{0x1b, 0x69, 0x00, 0x00}, 0, 4);             // 文字縦拡大解除
				outputByteBuffer = createShiftBIG5("——————————————————————\n");
				port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
				String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now);
				
				port.writePort(new byte[]{0x1b, 0x1d, 0x61, 0x30}, 0, 4);            //左揃え設定
				
				outputByteBuffer = createShiftBIG5("時間：" + nowTime + "\n\n");
				port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
				
				//outputByteBuffer = createShiftBIG5("--------------------------------------------\n");
				//port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
				port.writePort(new byte[]{0x1b, 0x44, 0x10, 0x00}, 0, 4);             // 水平タブ位置設定
				port.writePort(new byte[]{0x1b, 0x1d, 0x61, 0x31}, 0, 4);             // 中央揃え設定
				port.writePort(new byte[]{0x1b, 0x45}, 0, 2); 							// 強調印字設定
			    outputByteBuffer = createShiftBIG5("總覽" + "\n");
				port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
				port.writePort(new byte[]{0x1b, 0x46}, 0, 2);                         // 強調印字解除
				port.writePort(new byte[]{0x1b, 0x1d, 0x61, 0x30}, 0, 4);            //左揃え設定
				
				outputByteBuffer = createShiftBIG5("——————————————————————\n");
				port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
				NumberFormat formatter = new DecimalFormat("###,###,###");
				outputByteBuffer = createShiftBIG5(String.format("%-25s%14s\n", "今日營業額", formatter.format(todayMoney + monthMoney)));
				port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
				outputByteBuffer = createShiftBIG5(String.format("%-25s%14s\n", "月結帳　　", formatter.format(monthMoney)));
				port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
				outputByteBuffer = createShiftBIG5(String.format("%-25s%14s\n", "營業現金　", formatter.format(todayMoney)));
				port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
				outputByteBuffer = createShiftBIG5(String.format("%-25s%14s\n", "支出總額　", formatter.format(payMoney)));
				port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
				outputByteBuffer = createShiftBIG5(String.format("%-25s%14s\n", "上繳金額　", formatter.format(submitMoney)));
				port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
				outputByteBuffer = createShiftBIG5(String.format("%-25s%14s\n", "誤差金額　", formatter.format(todayMoney - payMoney - submitMoney)));
				port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
				outputByteBuffer = createShiftBIG5(String.format("%-25s%14s\n", "總銷售量　", formatter.format(categoryNumber1 + categoryNumber2 + categoryNumber3)));
				port.writePort(outputByteBuffer, 0, outputByteBuffer.length); 
				
				 
				ProductMap map = new ProductMap();
				for(int i = 0; i < map.catagoryNo; i++)
				{
					port.writePort(new byte[]{0x1b, 0x44, 0x10, 0x00}, 0, 4);             // 水平タブ位置設定
					port.writePort(new byte[]{0x1b, 0x1d, 0x61, 0x31}, 0, 4);             // 中央揃え設定
					port.writePort(new byte[]{0x1b, 0x45}, 0, 2); 							// 強調印字設定
				    outputByteBuffer = createShiftBIG5(map.categoryNames[i] + "\n");
					port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
					categoryMoneys[i] = getCategoryTotalAmount(i);
					
					outputByteBuffer = createShiftBIG5("銷售額:" + categoryMoneys[i] + "\n");
					port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
					outputByteBuffer = createShiftBIG5("銷售量:" + formatter.format(categoryNumbers[i]) + "\n");
					port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
					port.writePort(new byte[]{0x1b, 0x46}, 0, 2);                         // 強調印字解除
					port.writePort(new byte[]{0x1b, 0x1d, 0x61, 0x30}, 0, 4);            //左揃え設定
					outputByteBuffer = createShiftBIG5("——————————————————————\n");
					port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
					int count = 0;
					// 印出各個產品的詳細資料
					for(int j = 0 ; j < map.categoryNos[i]; j++)
					{
						HashMap<Double, Integer> pMap = getProductNumber(i * 1000 + j);
						for(Double key:pMap.keySet())
						{
							int pno = pMap.get(key);
							if(pno != 0)
							{
								count += 1;
								if(key == 1.0)
									outputByteBuffer = createShiftBIG5(String.format("%-17s%8s%8s\n", productMap.productNames[i][j], formatter.format(pno), formatter.format(priceMap.get(key))));
								else
									outputByteBuffer = createShiftBIG5(String.format("%-17s%8s%8s\n", String.format(productMap.productNamesDiscount[i][j], discountMapping(key)), formatter.format(pno), formatter.format(priceMap.get(key))));
									
								port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
							}
						}
					
						
						
						
					}
					if(count == 0)
					{
						outputByteBuffer = createShiftBIG5("無售出品項\n");
						port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
					}
				}
				port.writePort(new byte[]{0x1b, 0x69, 0x00, 0x00}, 0, 4);
				port.writePort(new byte[]{0x1b, 0x64, 0x33}, 0, 3); //CUT
				
				clickbuttonRecieve();
				uloadData();
				clearData();

    	}
    	catch (StarIOPortException e)
    	{
    		Builder dialog2 = new AlertDialog.Builder(this);
    		dialog2.setTitle("無法連接出單機");
    		dialog2.setMessage("1.請確認出單機已開機且呈綠燈保持發亮非閃爍。\n2.請確認無線基地台已開機並與出單機正常連接。\n3.排除問題後請按重新連結。");
		    
    		dialog2.setPositiveButton("重新連接", connectRestartClick);
    		dialog2.setNegativeButton("不使用出單機", connectCancelClick);
		    AlertDialog dialogView = dialog2.show();
		    TextView textView = (TextView) dialogView.findViewById(android.R.id.message);
		    textView.setTextSize(26);
		    Button btn1 = dialogView.getButton(DialogInterface.BUTTON_POSITIVE);
		    btn1.setTextSize(24);
		    btn1.setTextColor(Color.BLUE);
		    Button btn2 = dialogView.getButton(DialogInterface.BUTTON_NEGATIVE);
		    btn2.setTextSize(24);
		    btn2.setTextColor(Color.RED);
		    SharedPreferences pref = this.getSharedPreferences("POS_ORDER", 0);
    		Editor editor = pref.edit();
    		editor.putString("portName", "");
    		editor.commit();
    	}
		
		finally
		{
			if(port != null)
			{
				try {
				StarIOPort.releasePort(port);
				} 
				catch (StarIOPortException e) {}
			}
		}
	}
	public void portDiscovery(int total)
    {
		ProgressDialog dialog = ProgressDialog.show(this, "", "連結出單機中請稍待...", true);
    	List<PortInfo> TCPPortList;

    	final ArrayList<PortInfo> arrayDiscovery;
		arrayDiscovery = new ArrayList<PortInfo>();

    	try {

			TCPPortList = StarIOPort.searchPrinter("TCP:");

			
	    	for (PortInfo portInfo : TCPPortList) {
	    		arrayDiscovery.add(portInfo);
	    	}
 		
		} catch (StarIOPortException e) {
			e.printStackTrace();
		}
    	if(arrayDiscovery.size() > 0)
    	{
    		SharedPreferences pref = getSharedPreferences("POS_ORDER", 0);
    		Editor editor = pref.edit();
    		editor.putString("portName", arrayDiscovery.get(0).getPortName());
    		editor.commit();
    		dialog.dismiss();
    		if(total == 0)
    			printAccounting(1);
    		else
    			printAccountingTotal(1);
    	}
    	else
    	{
    		dialog.dismiss();
    		Builder dialog2 = new AlertDialog.Builder(this);
    		dialog2.setTitle("無法連接出單機");
    		dialog2.setMessage("1.請確認出單機已開機\n2.請確認無線基地台已開機並與出單機正常連接。\n3.排除問題後請按重新連結。");
    		if(total == 0)
    		{
    			dialog2.setPositiveButton("重新連接", connectRestartClick);
    			dialog2.setNegativeButton("不使用出單機", connectCancelClick);
    		}
    		else
    		{
    			dialog2.setPositiveButton("重新連接", connectRestartClickTotal);
    			dialog2.setNegativeButton("不使用出單機", connectCancelClickTotal);
    		}
		    AlertDialog dialogView = dialog2.show();
		    TextView textView = (TextView) dialogView.findViewById(android.R.id.message);
		    textView.setTextSize(26);
		    Button btn1 = dialogView.getButton(DialogInterface.BUTTON_POSITIVE);
		    btn1.setTextSize(24);
		    btn1.setTextColor(Color.BLUE);
		    Button btn2 = dialogView.getButton(DialogInterface.BUTTON_NEGATIVE);
		    btn2.setTextSize(24);
		    btn2.setTextColor(Color.RED);
		    SharedPreferences pref = getSharedPreferences("POS_ORDER", 0);
    		Editor editor = pref.edit();
    		editor.putString("portName", "");
    		editor.commit();

    	}
    	
    }
	private DialogInterface.OnClickListener connectRestartClick = new DialogInterface.OnClickListener()
	{

		@Override
		public void onClick(DialogInterface dialog, int which) {
			portDiscovery(0);
			
		}
		
	};
	private DialogInterface.OnClickListener connectCancelClick = new DialogInterface.OnClickListener()
	{

		@Override
		public void onClick(DialogInterface dialog, int which) {
			SharedPreferences pref = getSharedPreferences("POS_ORDER", 0);
    		Editor editor = pref.edit();
    		editor.putString("portName", "");
    		editor.commit();
    		dialog.dismiss();
    		clickbuttonRecieve();
			uloadData();
			clearData();
		}
		
	};
	
	private DialogInterface.OnClickListener connectRestartClickTotal = new DialogInterface.OnClickListener()
	{

		@Override
		public void onClick(DialogInterface dialog, int which) {
			portDiscovery(1);
			
		}
		
	};
	
	private DialogInterface.OnClickListener connectCancelClickTotal = new DialogInterface.OnClickListener()
	{

		@Override
		public void onClick(DialogInterface dialog, int which) {
			SharedPreferences pref = getSharedPreferences("POS_ORDER", 0);
    		Editor editor = pref.edit();
    		editor.putString("portName", "");
    		editor.commit();
    		dialog.dismiss();
    		
		}
		
	};
	private void clearData()
	{
		SharedPreferences settings = getSharedPreferences ("POS_ORDER", 0);
		int cashboxMoneyDefault = settings.getInt("CashBoxMoneyDefault", 0);
		SharedPreferences.Editor PE = settings.edit();
		
		
		PE.putInt("CashBoxMoney", cashboxMoneyDefault);
		PE.putInt("TodayMoney", 0);
		PE.putInt("TodayMoney", 0);
		PE.putInt("WaitMoney", 0);
		PE.putInt("OrderNumber", 1);
		PE.putInt("WaitOrderCount", 0);
		PE.putInt("MonthMoney", 0);
		SN += 1;
		PE.putInt("SN", SN);
		PE.commit();
		
		SQLiteDatabase db = MainActivity.dbhelper.getWritableDatabase();
        
		db.execSQL("DELETE FROM " + DBConstants.ORDER_TABLE_NAME + " WHERE " + DBConstants.ORDER_SN  + "<" + String.valueOf(SN - 30));
		db.execSQL("DELETE FROM " + DBConstants.ORDER_DETAIL_TABLE_NAME +  " WHERE " + DBConstants.ORDER_DETAIL_SN  + "<" + String.valueOf(SN - 30));
		db.close();
		
		getStatisticsNumberFromDatabase();
		
		initDetail0();
		row_data_detail_1 = initDetail(0, "便當", categoryNumber1);
		row_data_detail_2 = initDetail(1, "燒臘", categoryNumber2);
		row_data_detail_3 = initDetail(2, "粥品/小菜/其他", categoryNumber3);
		
		row_data_category.clear();
		row_data_category.add(new StatisticsCategoryRow("總覽", "0"));
		row_data_category.add(new StatisticsCategoryRow("便當", "0"));
		row_data_category.add(new StatisticsCategoryRow("燒臘", "0"));
		row_data_category.add(new StatisticsCategoryRow("粥品/小菜/其他", "0"));
		adapter.notifyDataSetChanged();
		if(row_data_category.size() > 0)
	    	listViewCategory.performItemClick(listViewCategory.getAdapter().getView(0, null, null), 0, 0);
		loadingDialog.dismiss();
		
		MainActivity.dbhelper.table_dumper(DBConstants.ORDER_DETAIL_TABLE_NAME);
	}
	private static byte[] createShiftBIG5(String inputText) {
    	byte[] byteBuffer = null;
    	
    	try {
			byteBuffer = inputText.getBytes("BIG5");
		} catch (UnsupportedEncodingException e) {
			byteBuffer = inputText.getBytes();
		}
    	
    	return byteBuffer;
    }
	
	 private void uloadData() {
		 SQLiteDatabase db = MainActivity.dbhelper.getWritableDatabase();
	        ContentValues values = new ContentValues();
	        values.put(DBConstants.BILL_TODAY_MONEY, todayMoney);
	        values.put(DBConstants.BILL_MOTH_MONEY, monthMoney);
	        values.put(DBConstants.BILL_PAY_MONEY, payMoney);
	        values.put(DBConstants.BILL_SUBMIT_MONEY, submitMoney);
	        values.put(DBConstants.BILL_SN, SN);
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			String dateString = dateFormat.format(new Date());
	        values.put(DBConstants.BILL_DATE, dateString);
	        db.insert(DBConstants.BILL_TABLE_NAME, null, values);
         try { 
        	 SharedPreferences settings = getSharedPreferences ("POS_ORDER", 0);
        	 String storeSN = settings.getString("StoreSN", "YG0000");
        	dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss"); 
     		String timeString = dateFormat.format(new Date());
        	 
             HttpClient client = new DefaultHttpClient(); 
             httpPostFileUpload(client, DATAURL, "uploadedfile", storeSN + "_" + timeString + ".sqlite"); 
         } catch (Exception e) { 
         
             e.printStackTrace(); 
          } 
	 }

	 public void httpPostFileUpload( HttpClient client, String uploadUri,  String inputNameAttr, String fname) 
			 throws ClientProtocolException, IOException 
	{
		HttpUriRequest request = new HttpPost(uploadUri + "?fname="+fname); 
		MultipartEntity form = new MultipartEntity(); 
		client.getParams().setBooleanParameter("http.protocol.expect-continue", false);
		File file = getDatabasePath("YUNGGIPOSDB");
		Log.v("Msg",file.getAbsolutePath());
		form.addPart(inputNameAttr, new FileBody(getDatabasePath("YUNGGIPOSDB"))); 
		((HttpEntityEnclosingRequestBase) request).setEntity(form);
         try { 
                 client.execute(request); 
         } catch (ClientProtocolException e) { 
                 throw e; 
         } catch (IOException ee) { 
                 throw ee; 
         } 
	} 
	 
	 public void clickbuttonRecieve() {
		    try {
		    	SharedPreferences settings = getSharedPreferences ("POS_ORDER", 0);
	        	String storeSN = settings.getString("StoreSN", "YG0000");
	        	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	    		String dateString = dateFormat.format(new Date());
	    		if(categoryMoneys[0] == null)
	    		{
	    			
	    			categoryMoneys[0] = getCategoryTotalAmount(0);
	    			categoryMoneys[1] = getCategoryTotalAmount(1);
	    			categoryMoneys[2] = getCategoryTotalAmount(2);
	    			
	    		}
		        JSONObject json = new JSONObject();	    
		        json.put("storeSN", storeSN);
		        json.put("date", dateString);
		        json.put("todayMoney", String.valueOf(todayMoney));
		        json.put("monthMoney", String.valueOf(monthMoney));
		        json.put("payMoney", String.valueOf(payMoney));
		        json.put("submitMoney", String.valueOf(submitMoney));
		        json.put("lunchboxNo", String.valueOf(categoryNumber1));
		        json.put("lunchboxMoney", categoryMoneys[0].replace(",", ""));
		        json.put("barbecueNo", String.valueOf(categoryNumber2));
		        json.put("barbecueMoney",  categoryMoneys[1].replace(",", ""));
		        json.put("otherNo", String.valueOf(categoryNumber3));
		        json.put("otherMoney",  categoryMoneys[2].replace(",", ""));
		        HttpParams httpParams = new BasicHttpParams();
		        HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
		        HttpConnectionParams.setSoTimeout(httpParams, 5000);
		        HttpClient client = new DefaultHttpClient(httpParams);
		        
		       
		      

		        HttpPost request = new HttpPost(BILLURL);
		        request.setEntity(new ByteArrayEntity(json.toString().getBytes(
		                "UTF8")));
		        request.setHeader("json", json.toString());
		        HttpResponse response = client.execute(request);
		        HttpEntity entity = response.getEntity();
		        // If the response does not enclose an entity, there is no need
		        if (entity != null) {
		            InputStream instream = (InputStream) entity.getContent();

		            String result = convertStreamToString(instream);
		            Log.d("Read from server", result);
		            //Toast.makeText(this,  result, Toast.LENGTH_LONG).show();
		            System.out.println(result);
		        }
		    } catch (Throwable t) {
		        Toast.makeText(this, "Request failed: " + t.toString(),
		                Toast.LENGTH_LONG).show();
		    }
		    
		    try {
		    	SharedPreferences settings = getSharedPreferences ("POS_ORDER", 0);
	        	String storeSN = settings.getString("StoreSN", "YG0000");
	        	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	    		String dateString = dateFormat.format(new Date());
	    		int[] countMapsBarbecue = getGrooveAmountBarbecue();
	    		int[] countMapsLunchbox = getGrooveTotalAmountLunchBox();
	    		
		        JSONObject json = new JSONObject();	    
		        json.put("storeSN", storeSN);
		        json.put("date", dateString);
		        json.put("groove_barbecue_count_0", String.valueOf(countMapsBarbecue[0]));
		        json.put("groove_barbecue_count_1", String.valueOf(countMapsBarbecue[1]));
		        json.put("groove_barbecue_count_2", String.valueOf(countMapsBarbecue[2]));
		        json.put("groove_barbecue_count_3", String.valueOf(countMapsBarbecue[3]));
		        json.put("groove_barbecue_count_4", String.valueOf(countMapsBarbecue[4]));
		        json.put("groove_barbecue_count_5", String.valueOf(countMapsBarbecue[5]));
		        json.put("groove_lunchbox_count_0", String.valueOf(countMapsLunchbox[0]));
		        json.put("groove_lunchbox_count_1", String.valueOf(countMapsLunchbox[1]));
		        json.put("groove_lunchbox_count_2", String.valueOf(countMapsLunchbox[2]));
		        json.put("groove_lunchbox_count_3", String.valueOf(countMapsLunchbox[3]));
		        
		        HttpParams httpParams = new BasicHttpParams();
		        HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
		        HttpConnectionParams.setSoTimeout(httpParams, 5000);
		        HttpClient client = new DefaultHttpClient(httpParams);
		        
		        HttpPost request = new HttpPost(GROOVEBILLURL);
		        request.setEntity(new ByteArrayEntity(json.toString().getBytes(
		                "UTF8")));
		        request.setHeader("json", json.toString());
		        HttpResponse response = client.execute(request);
		        HttpEntity entity = response.getEntity();
		        // If the response does not enclose an entity, there is no need
		        if (entity != null) {
		            InputStream instream = (InputStream) entity.getContent();

		            String result = convertStreamToString(instream);
		            Log.d("Read from server", result);
		           
		            System.out.println(result);
		        }
		    } catch (Throwable t) {
		        Toast.makeText(this, "Request failed: " + t.toString(),
		                Toast.LENGTH_LONG).show();
		    }
		    
		    
		}
	 
	 private String convertStreamToString(InputStream is) {
		    /*
		     * To convert the InputStream to String we use the BufferedReader.readLine()
		     * method. We iterate until the BufferedReader return null which means
		     * there's no more data to read. Each line will appended to a StringBuilder
		     * and returned as String.
		     */
		    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		    StringBuilder sb = new StringBuilder();

		    String line = null;
		    try {
		        while ((line = reader.readLine()) != null) {
		            sb.append(line + "\n");
		        }
		    } catch (IOException e) {
		        e.printStackTrace();
		    } finally {
		        try {
		            is.close();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		    }
		    return sb.toString();

	 }	
	
	 private void printAccountingTotal(int times)
		{
			StarIOPort port = null;
			try 
	    	{			
				SharedPreferences pref =  getSharedPreferences("POS_ORDER", 0);
				String portName = pref.getString("portName", "");
				String storeSN = pref.getString("StoreSN", "");
				SNs = getTodaySNs();
				getStatisticsNumberFromDatabaseTotal();
				categoryMoneysTotal = new String[3];
                totalMoneys = getTotalMoneys();
				
				
				if(portName.equalsIgnoreCase(""))
				{
					
					return;
				}
				port = StarIOPort.getPort(portName, "", 10000, this);
				
				String storeName = "";
				if(storeSN.equals("YG0001"))
					storeName = "自由店";
				else if(storeSN.equals("YG0002"))
					storeName = "旗艦店";
				else if(storeSN.equals("YG0003"))
					storeName = "鳳山店";
				byte[] outputByteBuffer=null;
				port.writePort(new byte[]{0x1b, 0x40}, 0, 2);                         // Initialization

				port.writePort(new byte[]{0x1b, 0x24, 0x31}, 0, 3);                   // 漢字モード設定
	                               
				
					port.writePort(new byte[]{0x1b, 0x44, 0x10, 0x00}, 0, 4);             // 水平タブ位置設定
					port.writePort(new byte[]{0x1b, 0x1d, 0x61, 0x31}, 0, 4);             // 中央揃え設定
						
					port.writePort(new byte[]{0x1b, 0x69, 0x02, 0x00}, 0, 4);             // 文字縦拡大設定
					port.writePort(new byte[]{0x1b, 0x45}, 0, 2); 							// 強調印字設定
					outputByteBuffer = createShiftBIG5("香港湧記燒鵝" + "\n");
					port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
					outputByteBuffer = createShiftBIG5(storeName + "\n");
					port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
					
					port.writePort(new byte[]{0x1b, 0x69, 0x01, 0x00}, 0, 4);             // 文字縦拡大設定
					Date now = new Date();
					String nowDate = new SimpleDateFormat("yyyy/MM/dd").format(now);
					outputByteBuffer = createShiftBIG5(nowDate + "日結單 全日總結\n");
					port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
					
					port.writePort(new byte[]{0x1b, 0x46}, 0, 2);                         // 強調印字解除
					
					//port.writePort(new byte[]{0x1b, 0x69, 0x00, 0x00}, 0, 4);             // 文字縦拡大解除
					outputByteBuffer = createShiftBIG5("——————————————————————\n");
					port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
					String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now);
					
					port.writePort(new byte[]{0x1b, 0x1d, 0x61, 0x30}, 0, 4);            //左揃え設定
					
					outputByteBuffer = createShiftBIG5("時間：" + nowTime + "\n\n");
					port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
					
					//outputByteBuffer = createShiftBIG5("--------------------------------------------\n");
					//port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
					port.writePort(new byte[]{0x1b, 0x44, 0x10, 0x00}, 0, 4);             // 水平タブ位置設定
					port.writePort(new byte[]{0x1b, 0x1d, 0x61, 0x31}, 0, 4);             // 中央揃え設定
					port.writePort(new byte[]{0x1b, 0x45}, 0, 2); 							// 強調印字設定
				    outputByteBuffer = createShiftBIG5("總覽" + "\n");
					port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
					port.writePort(new byte[]{0x1b, 0x46}, 0, 2);                         // 強調印字解除
					port.writePort(new byte[]{0x1b, 0x1d, 0x61, 0x30}, 0, 4);            //左揃え設定
					
					outputByteBuffer = createShiftBIG5("——————————————————————\n");
					port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
					NumberFormat formatter = new DecimalFormat("###,###,###");
					outputByteBuffer = createShiftBIG5(String.format("%-25s%14s\n", "今日營業額", formatter.format(totalMoneys.get(0) + totalMoneys.get(1))));
					port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
					outputByteBuffer = createShiftBIG5(String.format("%-25s%14s\n", "月結帳　　", formatter.format(totalMoneys.get(1))));
					port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
					outputByteBuffer = createShiftBIG5(String.format("%-25s%14s\n", "營業現金　", formatter.format(totalMoneys.get(0))));
					port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
					outputByteBuffer = createShiftBIG5(String.format("%-25s%14s\n", "支出總額　", formatter.format(totalMoneys.get(2))));
					port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
					outputByteBuffer = createShiftBIG5(String.format("%-25s%14s\n", "上繳金額　", formatter.format(totalMoneys.get(3))));
					port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
					outputByteBuffer = createShiftBIG5(String.format("%-25s%14s\n", "誤差金額　", formatter.format(totalMoneys.get(0) - totalMoneys.get(2) - totalMoneys.get(3))));
					port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
					outputByteBuffer = createShiftBIG5(String.format("%-25s%14s\n", "總銷售量　", formatter.format(categoryNumber1Total + categoryNumber2Total + categoryNumber3Total)));
					port.writePort(outputByteBuffer, 0, outputByteBuffer.length); 
					
					 
					ProductMap map = new ProductMap();
					for(int i = 0; i < map.catagoryNo; i++)
					{
						port.writePort(new byte[]{0x1b, 0x44, 0x10, 0x00}, 0, 4);             // 水平タブ位置設定
						port.writePort(new byte[]{0x1b, 0x1d, 0x61, 0x31}, 0, 4);             // 中央揃え設定
						port.writePort(new byte[]{0x1b, 0x45}, 0, 2); 							// 強調印字設定
					    outputByteBuffer = createShiftBIG5(map.categoryNames[i] + "\n");
						port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
						categoryMoneysTotal[i] = getCategoryTotalAmountTotal(i);
						
						outputByteBuffer = createShiftBIG5("銷售額:" + categoryMoneysTotal[i] + "\n");
						port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
						outputByteBuffer = createShiftBIG5("銷售量:" + formatter.format(categoryNumbersTotal[i]) + "\n");
						port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
						port.writePort(new byte[]{0x1b, 0x46}, 0, 2);                         // 強調印字解除
						port.writePort(new byte[]{0x1b, 0x1d, 0x61, 0x30}, 0, 4);            //左揃え設定
						outputByteBuffer = createShiftBIG5("——————————————————————\n");
						port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
						int count = 0;
						for(int j = 0 ; j < map.categoryNos[i]; j++)
						{
							HashMap<Double, Integer> pMap = getProductNumberTotal(i * 1000 + j, SNs);
							for(Double key:pMap.keySet())
							{
								int pno = pMap.get(key);
								if(pno != 0)
								{
									count += 1;
									if(key == 1.0)
										outputByteBuffer = createShiftBIG5(String.format("%-17s%8s%8s\n", productMap.productNames[i][j], formatter.format(pno), formatter.format(priceMapTotal.get(key))));
									else
										outputByteBuffer = createShiftBIG5(String.format("%-17s%8s%8s\n", String.format(productMap.productNamesDiscount[i][j], discountMapping(key)), formatter.format(pno), formatter.format(priceMapTotal.get(key))));
										
									port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
								}
							}
						
							
							
							
						}
						if(count == 0)
						{
							outputByteBuffer = createShiftBIG5("無售出品項\n");
							port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
						}
					}
					port.writePort(new byte[]{0x1b, 0x69, 0x00, 0x00}, 0, 4);
					port.writePort(new byte[]{0x1b, 0x64, 0x33}, 0, 3); //CUT
					

	    	}
	    	catch (StarIOPortException e)
	    	{
	    		Builder dialog2 = new AlertDialog.Builder(this);
	    		dialog2.setTitle("無法連接出單機");
	    		dialog2.setMessage("1.請確認出單機已開機且呈綠燈保持發亮非閃爍。\n2.請確認無線基地台已開機並與出單機正常連接。\n3.排除問題後請按重新連結。");
			    
	    		dialog2.setPositiveButton("重新連接", connectRestartClickTotal);
	    		dialog2.setNegativeButton("不使用出單機", connectCancelClickTotal);
			    AlertDialog dialogView = dialog2.show();
			    TextView textView = (TextView) dialogView.findViewById(android.R.id.message);
			    textView.setTextSize(26);
			    Button btn1 = dialogView.getButton(DialogInterface.BUTTON_POSITIVE);
			    btn1.setTextSize(24);
			    btn1.setTextColor(Color.BLUE);
			    Button btn2 = dialogView.getButton(DialogInterface.BUTTON_NEGATIVE);
			    btn2.setTextSize(24);
			    btn2.setTextColor(Color.RED);
			    SharedPreferences pref = this.getSharedPreferences("POS_ORDER", 0);
	    		Editor editor = pref.edit();
	    		editor.putString("portName", "");
	    		editor.commit();
	    	}
			
			finally
			{
				if(port != null)
				{
					try {
					StarIOPort.releasePort(port);
					} 
					catch (StarIOPortException e) {}
				}
			}
		}
}
