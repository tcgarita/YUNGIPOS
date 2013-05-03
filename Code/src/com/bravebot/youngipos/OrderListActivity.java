package com.bravebot.youngipos;


import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.starmicronics.stario.PortInfo;
import com.starmicronics.stario.StarIOPort;
import com.starmicronics.stario.StarIOPortException;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
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

public class OrderListActivity extends FragmentActivity  implements PopupBillInputWindowOrderList.OnClickBillButtonListener{
	
	private ArrayList<OrderListRow> row_data;
	private ArrayList<ListRow> row_data_detail;
	private ListView listView;
	private ListView listViewDetail;
	private OrderListRowAdapter adapter;
	private RowAdapter adapter_detail;
	private Button deleteButton;
	private Button payButton;
	private OrderListRow selectedRow;
	private TextView totalView;
	private int mode = 0;
	private PopupWindow popWindow;
	private TextView textViewMsg;
	private Handler m_handler;
	private int SN;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences settings = getSharedPreferences ("POS_ORDER", 0);
		
		SN = settings.getInt("SN", 1);
		m_handler = new Handler();
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		Bundle bundle = this.getIntent().getExtras();
		mode = bundle.getInt("mode");
		if(mode == 1)
	    	setTitle("待結訂單");
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
		setContentView(R.layout.order_list_activity);
		this.initViews();
		row_data = new ArrayList<OrderListRow>();
		adapter = new OrderListRowAdapter(this, R.layout.orderlistview_item_row, row_data);
		listView.setAdapter(adapter);
		row_data_detail = new ArrayList<ListRow>();
		adapter_detail = new RowAdapter(this, R.layout.listview_item_row, row_data_detail);
		adapter_detail.showDeleteButton = false;
		listViewDetail.setAdapter(adapter_detail);
	    
	    readDataFromDatabase();
	    listView.setOnItemClickListener(clickListItem);
	    if(row_data.size() > 0)
	    	listView.performItemClick(listView.getAdapter().getView(0, null, null), 0, 0);
	    
	}
	
	@SuppressLint("SimpleDateFormat")
	private void readDataFromDatabase() {
		row_data.clear();
		SQLiteDatabase db = MainActivity.dbhelper.getReadableDatabase();
		Cursor cursor;
		if(mode == 0)
			cursor = db.rawQuery("select * from " + DBConstants.ORDER_TABLE_NAME + " WHERE " + DBConstants.ORDER_SN 
					+ "=" + String.valueOf(SN) + " AND " + DBConstants.ORDER_DETAIL_DELETE + "=0" +
					" ORDER BY -_id ", null);
		else
			cursor = db.rawQuery("select * from " + DBConstants.ORDER_TABLE_NAME + " WHERE " + DBConstants.ORDER_STATUS +"=0 AND " +
					DBConstants.ORDER_SN + "=" + String.valueOf(SN) + " AND " + DBConstants.ORDER_DETAIL_DELETE + "=0"+
					" ORDER BY -_id", null);
		 int rows_num = cursor.getCount();//取得資料表列數
		 String last_id = "";
        if(rows_num != 0) {
        	cursor.moveToFirst();   //將指標移至第一筆資料
        	for(int i=0; i < rows_num; i++) {
        		int paid = cursor.getInt(8); 
        		row_data.add(new OrderListRow(cursor.getString(0), paid, cursor.getString(7), cursor.getString(4), cursor.getString(1), 
        				cursor.getString(2), cursor.getString(3), cursor.getString(8)));
        		cursor.moveToNext();//將指標移至下一筆資料
        	}
        }
	    cursor.close();
	    adapter.notifyDataSetChanged();
	        
	}
	private void readDataDetailFromDatabase(String order_id) {
		row_data_detail.clear();
		SQLiteDatabase db = MainActivity.dbhelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from " + DBConstants.ORDER_DETAIL_TABLE_NAME + " WHERE "
		+ DBConstants.ORDER_DETAIL_ORDER_ID + "=" + order_id  +" AND " + DBConstants.ORDER_SN + "=" + String.valueOf(SN) +
		" AND " + DBConstants.ORDER_DETAIL_DELETE + "=0" + " ORDER BY -_id", null);
		int rows_num = cursor.getCount();//取得資料表列數
	    System.out.println(rows_num);
        if(rows_num != 0) {
        	cursor.moveToFirst();   //將指標移至第一筆資料
        	for(int i=0; i < rows_num; i++) {
        		row_data_detail.add(new ListRow(cursor.getString(2), cursor.getInt(5), cursor.getInt(4), 
        				cursor.getInt(6), cursor.getDouble(7), 0, 0));
        	    
           
        		cursor.moveToNext();//將指標移至下一筆資料
        	}
        }
        cursor.close();
        adapter_detail.notifyDataSetChanged();
	}
	
	private void payBillToDatabase(String order_id)
	{
		SQLiteDatabase db = MainActivity.dbhelper.getWritableDatabase();
		db.execSQL("UPDATE " + DBConstants.ORDER_TABLE_NAME + " SET " + DBConstants.ORDER_STATUS + "=? WHERE "
		+ DBConstants.ID + "=?", new Object[]{1, order_id});
		db.close();
		readDataFromDatabase();
		adapter.notifyDataSetChanged();
		if(row_data.size() > 0)
			listView.performItemClick(listView.getAdapter().getView(0, null, null), 0, 0);
		else
		{
			row_data_detail.clear();
			adapter_detail.notifyDataSetChanged();
			payButton.setEnabled(false);
			totalView.setText("0");
		}
	}
	
	private void removeBillToDatabase(String order_id)
	{
		SQLiteDatabase db = MainActivity.dbhelper.getWritableDatabase();
		db.execSQL("UPDATE " + DBConstants.ORDER_TABLE_NAME + " SET " + DBConstants.ORDER_DELETE + "=? ," 
				+ DBConstants.ORDER_MODIFY_TIME + " = time('now', 'localtime') WHERE " + DBConstants.ID + "=?", new Object[]{1, order_id});
		db.execSQL("UPDATE " + DBConstants.ORDER_DETAIL_TABLE_NAME + " SET " + DBConstants.ORDER_DELETE + "=? WHERE "
				+ DBConstants.ORDER_DETAIL_ORDER_ID + "=?", new Object[]{1, order_id});
		
		db.close();
		readDataFromDatabase();
		adapter.notifyDataSetChanged();
		if(row_data.size() > 0)
			listView.performItemClick(listView.getAdapter().getView(0, null, null), 0, 0);
		else
		{
			row_data_detail.clear();
			adapter_detail.notifyDataSetChanged();
			payButton.setEnabled(false);
			totalView.setText("0");
		}
			
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

		payButton = (Button)findViewById(R.id.PayButton);
		payButton.setOnClickListener(clickPayButton);
		payButton.setEnabled(false);
		deleteButton = (Button)findViewById(R.id.DeleteButton);
		deleteButton.setOnClickListener(clickDeleteButton);
		
		totalView = (TextView)findViewById(R.id.TextViewTotal);
        listView = (ListView)findViewById(R.id.ListViewOrder);
        listViewDetail = (ListView)findViewById(R.id.listView1);
        
        
		
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
			OrderListRow row = row_data.get(position);
			selectedRow = row;
			readDataDetailFromDatabase(row.id);
			payButton.setEnabled(row.paid == 0 ? true : false);
			NumberFormat formatter = new DecimalFormat("###,###,###");
			totalView.setText(formatter.format(Integer.valueOf(row.title3)));
			
			
		}
    	
    };
    private void popBill()
	{
		PopupBillInputWindowOrderList popupNumberInputWindow = new PopupBillInputWindowOrderList(LayoutInflater.from(this), getResources(), Integer.parseInt(totalView.getText().toString().replace(",", "")));
		popupNumberInputWindow.setCallback(this);
		popWindow = new PopupWindow(1280, 748);
		popWindow.setContentView(popupNumberInputWindow.popView);
		popWindow.update();
		popWindow.setFocusable(true);
		popWindow.showAtLocation(getWindow().getDecorView().findViewById(android.R.id.content), Gravity.LEFT|Gravity.TOP, 0, 0);
	}
	private Button.OnClickListener clickPayButton = new Button.OnClickListener()
	{
		public void onClick(View v)
		{
			popBill();
			
			
		}
	};
	private Button.OnClickListener clickDeleteButton = new Button.OnClickListener()
	{
		public void onClick(View v)
		{
			if(row_data.size() != 0)
			{
				Builder dialog = new AlertDialog.Builder(v.getContext());
				dialog.setTitle("提醒");
				dialog.setMessage("刪除訂單 <" + selectedRow.title1 +"> ?");
			    
			    dialog.setPositiveButton("刪除", alertDeleteClick);
			    dialog.setNegativeButton("保留", alertCancelClick);
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
			
		}
	};
	private DialogInterface.OnClickListener alertDeleteClick = new DialogInterface.OnClickListener()
	{

		@Override
		public void onClick(DialogInterface dialog, int which) {
			
			SharedPreferences settings = getSharedPreferences ("POS_ORDER", 0);
			int cashBoxMoney = settings.getInt("CashBoxMoney", 0);
			int todayMoney = settings.getInt("TodayMoney", 0);
			int waitMoney = settings.getInt("WaitMoney", 0);
			int mobthMoney = settings.getInt("MonthMoney", 0);
			String totalStr = selectedRow.title3.replace(",", "");
			int billTotal = Integer.parseInt(totalStr);
			if(selectedRow.paid == 0)
				waitMoney -= billTotal;
			else if(selectedRow.paid == 1)
			{
				cashBoxMoney -= billTotal;
				todayMoney -= billTotal;
			}
			else if(selectedRow.paid == 2)
			{
				mobthMoney -= billTotal;
			}
			
			SharedPreferences.Editor PE = settings.edit();
			PE.putInt("CashBoxMoney", cashBoxMoney);
			PE.putInt("TodayMoney", todayMoney);
			PE.putInt("WaitMoney", waitMoney);
			PE.putInt("MonthMoney", mobthMoney);
			PE.commit();
			removeBillToDatabase(selectedRow.id);
		}
		
	};
	private DialogInterface.OnClickListener alertCancelClick = new DialogInterface.OnClickListener()
	{

		@Override
		public void onClick(DialogInterface dialog, int which) {
		
		}
		
	};
	private void openDrawer(Context context)
	{
		StarIOPort port = null;
		try 
    	{			
			SharedPreferences pref = context.getSharedPreferences("POS_ORDER", 0);
			String portName = pref.getString("portName", "");
			
			port = StarIOPort.getPort(portName, "", 10000, context);
			
			
			//open cash drawer
			byte[] openCashDrawer = new byte[] {0x07};
			port.writePort(openCashDrawer, 0, openCashDrawer.length);
			
			

    	}
    	catch (StarIOPortException e)
    	{
    		
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
	
	@Override
	public void onSubmitEnterClicked(int count) {
		popWindow.dismiss();
		if(count == -1)
		{
			
		}
		else
		{
			int total = Integer.parseInt(totalView.getText().toString().replace(",", ""));
			if((count - total) > 0)
			{
				Builder dialog2 = new AlertDialog.Builder(this);
	    		dialog2.setTitle("找零金額");
	    		dialog2.setMessage("$"+ String.valueOf(count - total));
	    		dialog2.setNeutralButton("確定", confirmClick);
			    AlertDialog dialogView = dialog2.show();
			    textViewMsg = (TextView) dialogView.findViewById(android.R.id.message);
			    textViewMsg.setTextSize(40);
			    textViewMsg.setVisibility(View.INVISIBLE);
			    textViewMsg.setGravity(Gravity.CENTER);
			    textViewMsg.setTextColor(Color.RED);
			    final int alertTitle = getResources().getIdentifier( "alertTitle", "id", "android" );
			    TextView textViewTitle = (TextView) dialogView.findViewById(alertTitle);
			    
			    textViewTitle.setGravity(Gravity.CENTER);
			    textViewTitle.setTextSize(40);
			
			    Button btn1 = dialogView.getButton(DialogInterface.BUTTON_NEUTRAL);
			    btn1.setTextSize(40);
			    btn1.setTextColor(Color.BLUE);
			    
			    startRepeatingTask();
			}
			openDrawer(this);
			SharedPreferences settings = getSharedPreferences ("POS_ORDER", 0);
			int cashBoxMoney = settings.getInt("CashBoxMoney", 0);
			int todayMoney = settings.getInt("TodayMoney", 0);
			int waitMoney = settings.getInt("WaitMoney", 0);
			String totalStr = selectedRow.title3.replace(",", "");
			int billTotal = Integer.parseInt(totalStr);
			waitMoney -= billTotal;
			cashBoxMoney += billTotal;
			todayMoney += billTotal;
			
			SharedPreferences.Editor PE = settings.edit();
			PE.putInt("CashBoxMoney", cashBoxMoney);
			PE.putInt("TodayMoney", todayMoney);
			PE.putInt("WaitMoney", waitMoney);
			PE.commit();
			payBillToDatabase(selectedRow.id);
		}
		
	}
	private DialogInterface.OnClickListener confirmClick = new DialogInterface.OnClickListener()
	{

		@Override
		public void onClick(DialogInterface dialog, int which) {
			stopRepeatingTask();
		}
		
	};
	Runnable m_statusChecker = new Runnable()
	{
	     @Override 
	     public void run() {
	    	 if(textViewMsg.getVisibility() == View.VISIBLE){
	           	textViewMsg.setVisibility(View.INVISIBLE);
	           }else{
	           	textViewMsg.setVisibility(View.VISIBLE);
	          }
	          m_handler.postDelayed(m_statusChecker, 600);
	     }
	};
	private void startRepeatingTask()
	{
	    m_statusChecker.run(); 
	}

	private void stopRepeatingTask()
	{
	    m_handler.removeCallbacks(m_statusChecker);
	}
	
	
	
}
