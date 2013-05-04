package com.bravebot.youngipos;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


import com.bravebot.youngipos.PopupPrepareInputWindow.OnClickPrepareButtonListener;
import com.bravebot.youngipos.PopupSubmitInputWindow.OnClickSubmitButtonListener;
import com.starmicronics.stario.PortInfo;
import com.starmicronics.stario.StarIOPort;
import com.starmicronics.stario.StarIOPortException;

import android.annotation.SuppressLint;
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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;

@SuppressLint("SimpleDateFormat")
public class ProductSettingSectionFragment extends Fragment implements FragmentMenu.OnClickOrderButtonListener, PopupNumberInputWindow.OnClickNumberButtonListener, RowAdapter.OnClickDeleteButtonListener
, PopupBillInputWindow.OnClickBillButtonListener {
	private View fragmentView;
	private TabHost tabHost;
	private Fragment fragment_menu_0;
	private Fragment fragment_menu_1;
	private Fragment fragment_menu_2;
	private ListView listView;
	private LayoutInflater inflater;
	private String targetName;
	/* tcgarita */
	private Product product;
	private SoldProduct sold_product;
	private ArrayList<SoldProduct> sold_array;
	
	private int targetPrice;
	
	private ArrayList<ListRow> row_data;
	private RowAdapter adapter;
	private PopupWindow popWindow;
	private int total = 0;
	private TextView textViewTotal;
	private ArrayList<String> product_data;
	private int cashBoxMoney;
	private int cashBoxMoneyDefault;
	private int todayMoney;
	private int waitMoney;
	private int monthMoney;
	private int orderNumber;
	private TextView titleView;
	private TextView cashBoxMoneyView;
	private TextView todayMoneyView;
	private TextView waitMoneyView;
	private TextView monthMoneyView;
	private TextView orderNumberView;
	private TextView dateView;
	private int mode;
	private TextView textViewMsg;
	private Handler m_handler;
	private int SN;
	public enum Alignment {Left, Center, Right};

	public OrderSectionFragmentListener callback;
	
	public ProductSettingSectionFragment() {
		
	}
	public interface OrderSectionFragmentListener {
        public void onChangeToTab0();
    }
	public void setCallback(Activity activity)
	{
		try {
			callback = (OrderSectionFragmentListener) activity;
	    } catch (ClassCastException e) {
	    	throw new ClassCastException(activity.toString()
	    			+ " must implement OrderSectionFragmentListener");
	    }
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_handler = new Handler();
		Bundle bundle = getArguments();
		mode = bundle.getInt("mode");
		product_data = new ArrayList<String>();
		this.inflater = inflater;
		fragmentView = inflater.inflate(R.layout.product_setting, container, false);

		this.initViews();
		
		tabHost.addTab(tabHost.newTabSpec("tab1")
		       .setIndicator("便當").setContent(R.id.tab1));
		tabHost.addTab(tabHost.newTabSpec("tab2")
		       .setIndicator("燒臘").setContent(R.id.tab2));
		tabHost.addTab(tabHost.newTabSpec("tab3")
		       .setIndicator("粥品/小菜/其他").setContent(R.id.tab3));
		TabWidget tw = tabHost.getTabWidget(); 
		tw.setBackgroundColor(Color.BLACK);
		tabHost.setOnTabChangedListener(menuTabChange);
		
		fragment_menu_0 = new FragmentMenu();
		((FragmentMenu) fragment_menu_0).setCategory(1);
		((FragmentMenu) fragment_menu_0).setCallback(this);
		fragment_menu_1 = new FragmentMenu();
		((FragmentMenu) fragment_menu_1).setCategory(2);
		((FragmentMenu) fragment_menu_1).setCallback(this);
		fragment_menu_2 = new FragmentMenu();
		((FragmentMenu) fragment_menu_2).setCategory(3);
		((FragmentMenu) fragment_menu_2).setCallback(this);
		
	    FragmentTransaction ft  = getFragmentManager().beginTransaction();
	    ft.replace(android.R.id.tabcontent, fragment_menu_0);
	    ft.commit();
		tabHost.setCurrentTab(0);
	    for(int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) 
	    {
	    	tabHost.getTabWidget().getChildAt(i).getLayoutParams().height = 52;
	    	TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
	        tv.setTextSize(23);
	        tv.setTextColor(Color.WHITE);
	        tv.setTypeface(null,Typeface.NORMAL);
	    }   
	    
	    row_data = new ArrayList<ListRow>();
	    
	    sold_array = new ArrayList<SoldProduct>();
	
//	    adapter = new RowAdapter(getActivity(), 
//                R.layout.listview_item_row, row_data);
	    /*
	    adapter = new RowAdapter(getActivity(),
	    					R.layout.listview_item_row);
	    adapter.setCallback(this);
        
        listView = (ListView)fragmentView.findViewById(R.id.listView1);
        
        listView.setAdapter(adapter);
        */
		return fragmentView;
	}
	@Override
	public void onResume()
	{
		super.onResume();
	}

	
	private OnTabChangeListener menuTabChange = new OnTabChangeListener() 
	{
		@Override
		public void onTabChanged(String tabId) 
		{
			if(tabId.equalsIgnoreCase("tab1"))
			{
			    FragmentTransaction ft  = getFragmentManager().beginTransaction();
			    ft.replace(android.R.id.tabcontent, fragment_menu_0);
			    ft.commit();
			}
			else if(tabId.equalsIgnoreCase("tab2"))
			{
			    FragmentTransaction ft  = getFragmentManager().beginTransaction();
			    ft.replace(android.R.id.tabcontent, fragment_menu_1);
			    ft.commit();
			}
			else if(tabId.equalsIgnoreCase("tab3"))
			{
			    FragmentTransaction ft  = getFragmentManager().beginTransaction();
			    ft.replace(android.R.id.tabcontent, fragment_menu_2);
			    ft.commit();
			}
		
			
		  }
	};
	

	@SuppressLint("SimpleDateFormat")
	private void initViews()
	{
		tabHost = (TabHost)fragmentView.findViewById(android.R.id.tabhost);
		tabHost.setup(); 
	}


	@Override
	public void onSubmitEnterClicked(int count) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onDeleteButtonClicked(int position) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onNumberEnterClicked(int count, double discount, int pop) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onButtonClicked(int cat_id, int product_id) {
		// TODO Auto-generated method stub
		EditText editText1 = (EditText) fragmentView.findViewById(R.id.editText1);
		Product product1 = MainActivity.dbhelper.getProductById(product_id);
		editText1.setText(product1.name);
	}

	



}
