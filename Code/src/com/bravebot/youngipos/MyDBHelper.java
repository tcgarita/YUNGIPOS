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
                			//Log.v("Msg",header[i]+":"+ fields[i]);
                			values.put(header[i],fields[i]);
                		}
                		db.insert(table_name, null, values);
                	}
                }
                input.close();
                table_dumper(db,table_name);
			}
		} catch (IOException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void table_dumper (SQLiteDatabase db, String table){
        Cursor cursor = db.rawQuery("select * from "+table+" where 1", null);
        Log.v("Msg","NUM:"+String.valueOf(cursor.getColumnCount()));
        if(cursor.getCount()>0){
        	int field_num = cursor.getColumnCount();
        	cursor.moveToFirst();
        	do{
        		for(int i=0;i<field_num;++i){
        			Log.v("Msg","idx "+String.valueOf(i)+
        				  " : "+cursor.getString(i));
        		}
        	}while(cursor.moveToNext());
        }
	}
	
}
