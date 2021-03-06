package com.bravebot.youngipos;



import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.util.Log;

public class MyDBHelper extends SQLiteOpenHelper {
	private final static String DATABASE_NAME = "YUNGGIPOSDB";
    private final static int DATABASE_VERSION = 1;
    
    private Context context;
    
	public MyDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context; 
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE category "+
				"(id INTEGER PRIMARY KEY AUTOINCREMENT,"+
				" name TEXT)");
		db.execSQL("CREATE TABLE product "+
				"(id integer primary key autoincrement,"+
				" name TEXT,"+
				" price INTEGER,"+
				" cat_id INTEGER)");
		data_init(db);

		final String BILL_TABLE = "CREATE TABLE " + DBConstants.BILL_TABLE_NAME + " (" +
				 DBConstants.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				 DBConstants.BILL_TODAY_MONEY + " INTEGER, " +
				 DBConstants.BILL_MOTH_MONEY + " INTEGER, " +
				 DBConstants.BILL_PAY_MONEY + " INTEGER, " +
				 DBConstants.BILL_SUBMIT_MONEY + " INTEGER, " +
				 DBConstants.BILL_DATE + " TEXT," +
				 DBConstants.BILL_SN + " INTEGER)"
				 ;
		 db.execSQL(BILL_TABLE);
		 
		 final String ORDER_TABLE = "CREATE TABLE " + DBConstants.ORDER_TABLE_NAME + " (" +
				 DBConstants.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				 DBConstants.ORDER_TITLE + " TEXT, " +
				 DBConstants.ORDER_PRICE + " INTEGER, " +
				 DBConstants.ORDER_TYPE + " INTEGER, " +
				 DBConstants.ORDER_CREATE_TIME + " TEXT, " +
				 DBConstants.ORDER_MODIFY_TIME + " TEXT, " +
				 DBConstants.ORDER_DATE + " TEXT," +
				 DBConstants.ORDER_NUMBER + " TEXT," +
				 DBConstants.ORDER_STATUS + " INTEGER, " +
				 DBConstants.ORDER_SN + " INTEGER, " +
				 DBConstants.ORDER_DELETE + " INTEGER)";
		 db.execSQL(ORDER_TABLE);
		 
		 final String ORDER_DETAIL_TABLE = "CREATE TABLE " + DBConstants.ORDER_DETAIL_TABLE_NAME + " (" +
				 DBConstants.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				 DBConstants.ORDER_DETAIL_ORDER_ID + " INTEGER, " +
				 DBConstants.ORDER_DETAIL_PRODUCT + " INTEGER, " +
				 DBConstants.ORDER_DETAIL_CAT_ID + " INTEGER, " +
				 DBConstants.ORDER_DETAIL_AMOUNT + " INTEGER, " +
				 DBConstants.ORDER_DETAIL_PRICE + " INTEGER, " +
				 DBConstants.ORDER_DETAIL_TOTAL + " INTEGER, " +
				 DBConstants.ORDER_DETAIL_DISCOUNT + " REAL, " +
				 DBConstants.ORDER_DETAIL_SN + " INTEGER, " +
				 DBConstants.ORDER_DETAIL_DELETE + " INTEGER, "+
				 DBConstants.ORDER_DETAIL_PRODUCT_ID +" INTEGER)";
		 db.execSQL(ORDER_DETAIL_TABLE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		 String DROP_TABLE = "DROP TABLE IF EXISTS " + DBConstants.ORDER_TABLE_NAME;
	     db.execSQL(DROP_TABLE);
	     DROP_TABLE = "DROP TABLE IF EXISTS " + DBConstants.ORDER_DETAIL_TABLE_NAME;
	     db.execSQL(DROP_TABLE);
	     onCreate(db);
	     Log.v("Msg","DB onUpgrade");
	}
	
	@Override
	public void onOpen(SQLiteDatabase db){
		super.onOpen(db);
	}
	
	private void data_init(SQLiteDatabase db) {
		insert_db(db,"category",R.raw.category);
		insert_db(db,"product",R.raw.db_init);
	}

	private void insert_db(SQLiteDatabase db,String table_name,int resource){
		InputStream input = this.context.getResources().openRawResource(resource);
		try {
			if ( input != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(input);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                String [] header = null;
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                	String [] fields = receiveString.split(",");
                	if(header == null) {
                		header = new String[fields.length];
                		header = fields;
                	} else {
                		ContentValues values = new ContentValues();
                		for(int i=0;i<fields.length;++i){
                			values.put(header[i],fields[i]);
                		}
                		db.insert(table_name, null, values);
                	}
                }
                input.close();
			}
		} catch (IOException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void table_dumper (String table){
		SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "+table+" where 1", null);
        int count = cursor.getCount();
        Log.v("Msg","TABLE:"+table+" COUNT:"+count);
        if(count>0){
        	int field_num = cursor.getColumnCount();
        	cursor.moveToFirst();
        	do{
        		for(int i=0;i<field_num;++i){
        			Log.v("Msg","idx "+String.valueOf(i)+
        				  " : "+cursor.getString(i));
        		}
        	}while(cursor.moveToNext());
        }
        cursor.close();
	}

	public Product getProductById (int id){
		Product p = null;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM product where id=?", 
				new String[]{String.valueOf(id)});
		if(cursor.getCount()>0){
			Log.v("Msg","has product");
			p = new Product();
			cursor.moveToFirst();
			p.id = cursor.getInt(0);
			p.name = cursor.getString(1);
			p.sticker_price = cursor.getInt(2);
			p.cat_id = cursor.getInt(3);	
		}
		cursor.close();
		return p;
	}
	
	public ArrayList<Product> getProductByCatId (int cat_id){
		ArrayList<Product> res = new ArrayList<Product>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM product where cat_id=?", 
				new String[]{String.valueOf(cat_id)});
		if(cursor.getCount()>0){
			cursor.moveToFirst();
			do{
				Product p = new Product();
				p.id = cursor.getInt(0);
				p.name = cursor.getString(1);
				p.sticker_price = cursor.getInt(2);
				p.cat_id = cursor.getInt(3);
				res.add(p);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return res;
	}
	
	public ArrayList<Category> getAllCategory () {
		ArrayList<Category> res = new ArrayList<Category>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM category where 1",null);
		if(cursor.getCount()>0){
			cursor.moveToFirst();
			do{
				Category p = new Category();
				p.id = cursor.getInt(0);
				p.name = cursor.getString(1);
				res.add(p);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return res;
	}
	
	public long addCategory(String name) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.beginTransaction();
		Cursor cursor = db.rawQuery("select count(*) from category where 1", null);
		cursor.moveToFirst();
		int count = cursor.getInt(0);
		long res = 0;
		if(count < 6){
			ContentValues values = new ContentValues();
			values.put("name", name);
			res = db.insert("category", null, values);
			db.setTransactionSuccessful();
		}
		cursor.close();
		db.endTransaction();
		return res;
	}
	
	public boolean delCategoryById (int cat_id){
		SQLiteDatabase db = this.getWritableDatabase();
		db.beginTransaction();
		boolean res = false;
		try{
			int ret = db.delete("product", "cat_id=?", new String[]{String.valueOf(cat_id)});
			int ret2 = db.delete("category", "id=?", new String[]{String.valueOf(cat_id)});
			if(ret != -1 && ret2 != -1){
				res = true;
				db.setTransactionSuccessful();
			}
		}finally{
			db.endTransaction();
		}
		return res;
	}
	
	public int editCategoryById(int id, String new_name){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("name", new_name);
		return db.update("category", values, "id=?", 
			new String[]{String.valueOf(id)});
	}
	
	public long addProduct(String name, int price, int category_id){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("name",name);
		values.put("price", price);
		values.put("cat_id", category_id);
		return db.insert("product", null, values);
	}
	
	public int delProductById (int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		int res = db.delete("product", "id=?", new String[]{String.valueOf(id)});
		return res;
		
	}
	
	public int editProductById(int id, String new_name, int new_price, int cat_id ) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("name", new_name);
		values.put("price", new_price);
		values.put("cat_id", cat_id);
		
		return db.update("product", values, "id=?", new String[]{String.valueOf(id)});
	}
	@SuppressLint("SimpleDateFormat")
	//public void addOrder(ArrayList<ListRow> Lists, int mode, int total_price, int orderNumber, int SN){
	public void addOrder(ArrayList<SoldProduct> Lists, int mode, int total_price, int orderNumber, int SN){
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss"); 
		String timeString = dateFormat.format(new Date());
		dateFormat = new SimpleDateFormat("yyyyMMdd");
		String dateString = dateFormat.format(new Date());
		
		SQLiteDatabase db = MainActivity.dbhelper.getWritableDatabase();
		String orderTitle = "";
		int count = 0; 
		int status;

		for(SoldProduct row : Lists)
		{
//			orderTitle = orderTitle + row.title0.replace("　", "");
			orderTitle = orderTitle + row.getSoldName();
			count += 1;
			if(count == Lists.size())
				break;
			else if(count >= 3)
			{
				orderTitle += "等";
				break;
			}
			else
				orderTitle += ", "; 
		}
		
        switch (mode){
    		case 0: status = 1; break;
    		case 3: status = 2; break;
    		default: status = 0;
        }

        ContentValues values = new ContentValues();
        values.put(DBConstants.ORDER_TITLE, orderTitle);
        values.put(DBConstants.ORDER_PRICE, total_price);
        values.put(DBConstants.ORDER_TYPE, mode);
        values.put(DBConstants.ORDER_CREATE_TIME, timeString);
        values.put(DBConstants.ORDER_MODIFY_TIME, timeString);
        values.put(DBConstants.ORDER_DATE, dateString);
        values.put(DBConstants.ORDER_NUMBER, orderNumber);
        values.put(DBConstants.ORDER_SN, SN);
        values.put(DBConstants.ORDER_DELETE, 0);
        values.put(DBConstants.ORDER_STATUS, status);
        
        long orderId = db.insert(DBConstants.ORDER_TABLE_NAME, null, values);
	
        for(SoldProduct row : Lists){
	        values = new ContentValues();
	        values.put(DBConstants.ORDER_DETAIL_ORDER_ID, orderId);
	        values.put(DBConstants.ORDER_DETAIL_PRODUCT, row.getSoldName());
	        values.put(DBConstants.ORDER_DETAIL_AMOUNT, row.getCount());
	        values.put(DBConstants.ORDER_DETAIL_PRICE, row.getFinalSoldPriceString());
	        values.put(DBConstants.ORDER_DETAIL_TOTAL, row.getTotalPriceString());
	        values.put(DBConstants.ORDER_DETAIL_DISCOUNT, row.getDiscount());
	        values.put(DBConstants.ORDER_DETAIL_CAT_ID, row.getProductCatId());
	        values.put(DBConstants.ORDER_DETAIL_PRODUCT_ID, row.getProductId());
	        Log.v("Msg","單價:"+row.getFinalSoldPriceString()+" 總計:"+row.getTotalPriceString());
	        values.put(DBConstants.ORDER_DETAIL_SN, SN);
	        values.put(DBConstants.ORDER_DETAIL_DELETE, 0);
	        db.insert(DBConstants.ORDER_DETAIL_TABLE_NAME, null, values);
		}	
	}
	
	public ArrayList<OrderListRow> readDataFromDatabase(int mode, int SN) {
		ArrayList<OrderListRow> row_data = new ArrayList<OrderListRow>();
		SQLiteDatabase db = MainActivity.dbhelper.getReadableDatabase();
		Cursor cursor;
		if(mode == 0)
			cursor = db.rawQuery("select * from " + DBConstants.ORDER_TABLE_NAME + " WHERE " + DBConstants.ORDER_SN 
					+ "=?" + " AND " + DBConstants.ORDER_DETAIL_DELETE + "=0" +
					" ORDER BY -_id ", new String[]{String.valueOf(SN)});
		else
			cursor = db.rawQuery("select * from " + DBConstants.ORDER_TABLE_NAME + " WHERE " + DBConstants.ORDER_STATUS +"=0 AND " +
					DBConstants.ORDER_SN + "=?" + " AND " + DBConstants.ORDER_DETAIL_DELETE + "=0"+
					" ORDER BY -_id", new String[]{String.valueOf(SN)});
		
        if(cursor.getCount() > 0) {
        	cursor.moveToFirst();   //將指標移至第一筆資料
        	do{
        		row_data.add(new OrderListRow(
        				cursor.getString(0), 
        				cursor.getInt(8), 
        				cursor.getString(7), 
        				cursor.getString(4), 
        				cursor.getString(1), 
        				cursor.getString(2), 
        				cursor.getString(3), 
        				cursor.getString(8)));
        	}while(cursor.moveToNext());
        }
	    cursor.close();
	    return row_data;  
	}
	
	public ArrayList<SoldProduct> readDataDetailFromDatabase(String order_id, int SN) {
		ArrayList<SoldProduct> row_data_detail = new ArrayList<SoldProduct>();

		SQLiteDatabase db = MainActivity.dbhelper.getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"select * from " + DBConstants.ORDER_DETAIL_TABLE_NAME + 
				" WHERE "+ DBConstants.ORDER_DETAIL_ORDER_ID + "=? AND " + 
				DBConstants.ORDER_SN + "=?" + " AND " + 
				DBConstants.ORDER_DETAIL_DELETE + "=0" + " ORDER BY -_id", 
				new String[]{order_id,String.valueOf(SN)});
		
		if(cursor.getCount()>0){
			cursor.moveToFirst();
			do{
				Log.v("Msg",cursor.getString(2));
				row_data_detail.add(
					new SoldProduct(
						cursor.getInt(10),
						cursor.getDouble(7),
						cursor.getInt(4),
						cursor.getInt(5),
						cursor.getInt(6),
						cursor.getString(2)
						));
			}while(cursor.moveToNext());
		}
        cursor.close();
        return row_data_detail;
	}
}
