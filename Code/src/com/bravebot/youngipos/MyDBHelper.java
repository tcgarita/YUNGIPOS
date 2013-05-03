package com.bravebot.youngipos;



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
import java.util.ArrayList;

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
				 DBConstants.ORDER_DETAIL_PRODUCT_ID + " INTEGER, " +
				 DBConstants.ORDER_DETAIL_AMOUNT + " INTEGER, " +
				 DBConstants.ORDER_DETAIL_PRICE + " INTEGER, " +
				 DBConstants.ORDER_DETAIL_TOTAL + " INTEGER, " +
				 DBConstants.ORDER_DETAIL_DISCOUNT + " REAL, " +
				 DBConstants.ORDER_DETAIL_SN + " INTEGER, " +
				 DBConstants.ORDER_DETAIL_DELETE + " INTEGER)";
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

	public Cursor getOrderDetail (){
		SQLiteDatabase db = this.getReadableDatabase();
		return db.rawQuery("select * from " + DBConstants.ORDER_DETAIL_TABLE_NAME + " WHERE "
				+ DBConstants.ORDER_DETAIL_DELETE + "=0", null);
	}
	
	public Product getProductById (int id){
		Product p = new Product();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM product where id=?", 
				new String[]{String.valueOf(id)});
		if(cursor.getCount()>0){
			cursor.moveToFirst();
			p.id = cursor.getInt(0);
			p.name = cursor.getString(1);
			p.sticker_price = cursor.getInt(2);
			p.cat_id = cursor.getInt(3);	
		}
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
		return res;
	}
	
	public long addCategory(String name) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("name", name);
		return db.insert("category", null, values);
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
		if(!new_name.equals("")){
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("name", new_name);
			return db.update("category", values, "id=?", 
				new String[]{String.valueOf(id)});
		} else {
			return 0;
		}
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
		return db.delete("product", "id=?", new String[]{String.valueOf("id")});
	}
	
	public int editProductById(int id, String new_name, int new_price, int cat_id ) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		if( ! new_name.equals("") )
			values.put("name", new_name);
		
		if( new_price != -1 )
			values.put("price", new_price);
		
		if( cat_id != -1 )
			values.put("cat_id", cat_id);
		
		return db.update("product", values, "id=?", new String[]{String.valueOf(id)});
	}
}
