package com.bravebot.youngipos;


import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;

import com.starmicronics.stario.PortInfo;
import com.starmicronics.stario.StarIOPort;
import com.starmicronics.stario.StarIOPortException; 
import com.starmicronics.stario.StarPrinterStatus;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {
	public static String PACKAGE_NAME;
	ImageButton order_button;
	ImageButton settings_button;
	ImageButton accounts_button;
	public static MyDBHelper dbhelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		SharedPreferences settings = getSharedPreferences ("POS_ORDER", 0);
		SharedPreferences.Editor PE = settings.edit();
		PE.putString("StoreSN", "YG0001");
		PE.commit();
		/*SharedPreferences settings = getSharedPreferences ("POS_ORDER", 0);
		SharedPreferences.Editor PE = settings.edit();
		PE.putInt("CashBoxMoney", 11000);
		PE.putInt("TodayMoney", 0);
		PE.putInt("WaitMoney", 0);
		PE.putInt("OrderNumber", 1);
		PE.putInt("WaitOrderCount", 0);
		PE.commit();*/
		if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD)
		{
		    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
	        .detectDiskReads()
	        .detectDiskWrites()
	        .detectNetwork()   // or .detectAll() for all detectable problems
	        .penaltyLog()
	        .build());
		} 
		
		
		portDiscovery();
		
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
		setContentView(R.layout.activity_main);
		this.initViews();
		PACKAGE_NAME = getApplicationContext().getPackageName();
		openDatabase();
	}
	@Override
	public void onResume()
	{
		super.onResume();
		/*
		SharedPreferences pref = getSharedPreferences("POS_ORDER", MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
		String portName = pref.getString("portName", "");
		if(!portName.equalsIgnoreCase(""))
		{
			portDiscovery();
		}
		*/
			
		
	}
	private void openDatabase(){
		Log.v("Msg","Before DB");
        dbhelper = new MyDBHelper(this);
        Log.v("Msg","After DB");
    }
	 private void closeDatabase(){
	    dbhelper.close();
	}
	@Override
	protected void onDestroy() {
		closeDatabase();
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
		//getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	private void initViews()
	{
		order_button = (ImageButton)this.findViewById(R.id.order_button);
		order_button.setOnClickListener(clickOrder);
		accounts_button = (ImageButton)this.findViewById(R.id.accounts_button);
		accounts_button.setOnClickListener(clickAccounts);
		settings_button = (ImageButton)this.findViewById(R.id.settings_button);
		settings_button.setOnClickListener(clickSettings);
	}
	
	private ImageButton.OnClickListener clickOrder = new ImageButton.OnClickListener()
	{
		public void onClick(View v)
		{
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, OrderActivity.class);
			startActivity(intent);
		}
	};
	
	
	private ImageButton.OnClickListener clickAccounts = new ImageButton.OnClickListener()
	{
		public void onClick(View v)
		{
			Bundle bundle = new Bundle();
			bundle.putInt("mode", 1);
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, StatisticsActivity.class);
			intent.putExtras(bundle);
			startActivity(intent);

		}
	};
	private ImageButton.OnClickListener clickSettings = new ImageButton.OnClickListener()
	{
		public void onClick(View v)
		{
			
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, SettingsActivity.class);
			startActivity(intent);

		}
	};
	
	public void portDiscovery()
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
    		SharedPreferences pref = getSharedPreferences("POS_ORDER", MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
    		Editor editor = pref.edit();
    		editor.putString("portName", arrayDiscovery.get(0).getPortName());
    		editor.commit();
    		dialog.dismiss();
    	}
    	else
    	{
    		dialog.dismiss();
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
		    SharedPreferences pref = getSharedPreferences("POS_ORDER", MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
    		Editor editor = pref.edit();
    		editor.putString("portName", "");
    		editor.commit();

    	}
    	
    }
	
	private DialogInterface.OnClickListener connectRestartClick = new DialogInterface.OnClickListener()
	{

		@Override
		public void onClick(DialogInterface dialog, int which) {
			portDiscovery();
			
		}
		
	};
	private DialogInterface.OnClickListener connectCancelClick = new DialogInterface.OnClickListener()
	{

		@Override
		public void onClick(DialogInterface dialog, int which) {
			SharedPreferences pref = getSharedPreferences("POS_ORDER", MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
    		Editor editor = pref.edit();
    		editor.putString("portName", "");
    		editor.commit();
    		dialog.dismiss();
		}
		
	};
}
