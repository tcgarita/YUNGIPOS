package com.bravebot.youngipos;


import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class SettingsActivity extends FragmentActivity implements PopupPrepareInputWindow.OnClickPrepareButtonListener  {
	
	private TextView prepareView;
	private Button prepareBotton;
	private Button connectBotton;
	private PopupWindow popWindow;
	private int todayMoney;
	private RadioGroup radioGroup;
	private RadioButton radio0;
	private RadioButton radio1;
	private RadioButton radio2;
 
     
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
		setContentView(R.layout.activity_settings);
		
		initViews();
		SharedPreferences pref = getSharedPreferences("POS_ORDER", MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
		int cashBoxMoney = pref.getInt("CashBoxMoneyDefault", 0);
		todayMoney = pref.getInt("TodayMoney", 0);
		NumberFormat formatter = new DecimalFormat("###,###,###");
		prepareView.setText(formatter.format(cashBoxMoney));
		int times = pref.getInt("PrintTimes", 1);
		if(times == 0)
			radioGroup.check(R.id.radioButton2);
		else if(times == 1)
			radioGroup.check(R.id.radioButton0);
		else if(times == 2)
			radioGroup.check(R.id.radioButton1);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

	    int itemId = item.getItemId();
	    switch (itemId) {	
	    	case android.R.id.home:
	    		this.finish();

	    		// Toast.makeText(this, "home pressed", Toast.LENGTH_LONG).show();
	    		break;
	    	case R.id.menu_back:
	    		this.finish();

	    		// Toast.makeText(this, "home pressed", Toast.LENGTH_LONG).show();
	    		break;
	    

	    }
		

	    return true;
	}
	private void initViews()
	{
		prepareView = (TextView)findViewById(R.id.TextViewPrepare);
		prepareBotton = (Button)findViewById(R.id.ButtonPrepare);
		prepareBotton.setOnClickListener(clickPrepareButton);
		connectBotton = (Button)findViewById(R.id.buttonConnect);
		connectBotton.setOnClickListener(clickConnectButton);
		radioGroup = (RadioGroup) findViewById(R.id.myRadioGroup);
		radio0 = (RadioButton) findViewById(R.id.radioButton0);
	    radio1 = (RadioButton) findViewById(R.id.radioButton1);
	    radio2 = (RadioButton) findViewById(R.id.radioButton2); 
	      
	    radioGroup.setOnCheckedChangeListener(changeRadio); 
		
	}
	private Button.OnClickListener clickConnectButton = new Button.OnClickListener()
	{
		public void onClick(View v)
		{
			
			portDiscovery();
			
		}
	};
	
	private RadioGroup.OnCheckedChangeListener changeRadio = new 
	           RadioGroup.OnCheckedChangeListener()
	  { 
	    @Override 
	    public void onCheckedChanged(RadioGroup group, int checkedId)
	    { 
	      int times = 0;
	      if(checkedId == radio0.getId())
		  { 
	    	  times = 1;
		  } 
	      if(checkedId == radio1.getId())
	      { 
	    	  times = 2;
	      } 
	      else if(checkedId == radio2.getId()) 
	      { 
	    	  times = 0;
	        
	      } 
	      SharedPreferences pref = getSharedPreferences("POS_ORDER", MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
	      SharedPreferences.Editor PE = pref.edit();
	      PE.putInt("PrintTimes", times);
	      PE.commit();
	    } 
	  }; 
	private Button.OnClickListener clickPrepareButton = new Button.OnClickListener()
	{
		public void onClick(View v)
		{
			
			PopupPrepareInputWindow popupNumberInputWindow = new PopupPrepareInputWindow(LayoutInflater.from(v.getContext()), getResources(), prepareView.getText().toString());
			popupNumberInputWindow.setCallback((Activity) v.getContext());
			popWindow = new PopupWindow(1280, 748);
			popWindow.setContentView(popupNumberInputWindow.popView);
			popWindow.update();
			popWindow.setFocusable(true);
			popWindow.showAtLocation(v, Gravity.LEFT|Gravity.TOP, 0, 0);
			
		}
	};

	@Override
	public void onPrepareEnterClicked(int count) {
		popWindow.dismiss();
		if(count == -1)
		{
			
		}
		else
		{
			NumberFormat formatter = new DecimalFormat("###,###,###");
			prepareView.setText(formatter.format(count));
			SharedPreferences pref = getSharedPreferences("POS_ORDER", MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
			SharedPreferences.Editor PE = pref.edit();
			PE.putInt("CashBoxMoney", todayMoney + count);
			PE.putInt("CashBoxMoneyDefault", count);
			PE.commit();
		}
		
	}
	
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
