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
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;

@SuppressLint("SimpleDateFormat")
public class OrderSectionFragment0 extends Fragment implements FragmentMenu.OnClickOrderButtonListener, PopupNumberInputWindow.OnClickNumberButtonListener, RowAdapter.OnClickDeleteButtonListener
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
	private ArrayList<Category> categories;
	private Category category;
	private HashMap<String, Fragment> hash_fragment;
	
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
	private RadioGroup radioGroup;
	private int inDoor = 0;
	private RadioButton radio0;
	private RadioButton radio1;
	private Boolean isPaying = false;
	public OrderSectionFragmentListener callback;
	
	public OrderSectionFragment0() {
		
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
		fragmentView = inflater.inflate(R.layout.fragment_order_section_0, container, false);

		this.initViews();
		
		categories = MainActivity.dbhelper.getAllCategory();
		hash_fragment =	new HashMap<String, Fragment>();
		int first_cat = 0;
		
		for(Category  c: categories) {
			tabHost.addTab(tabHost.newTabSpec(String.valueOf(c.id))
				.setIndicator(c.name).setContent(R.id.tab1));
			Fragment fragment = new FragmentMenu();
			((FragmentMenu) fragment ).setCategory(c.id);
			((FragmentMenu) fragment ).setCallback(this);				
			hash_fragment.put(String.valueOf(c.id), fragment);
			
			if(first_cat == 0){
				category = c;
				first_cat = c.id;
			}
		}
		TabWidget tw = tabHost.getTabWidget(); 
		tw.setBackgroundColor(Color.BLACK);
		tabHost.setOnTabChangedListener(menuTabChange);
		
	    FragmentTransaction ft  = getFragmentManager().beginTransaction();
	    ft.replace(android.R.id.tabcontent, hash_fragment.get(String.valueOf(first_cat)));
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
	    
	    adapter = new RowAdapter(getActivity(),R.layout.listview_item_row);
	    adapter.setCallback(this);
        
        listView = (ListView)fragmentView.findViewById(R.id.listView1);
        
        listView.setAdapter(adapter);
		return fragmentView;
	}
	@Override
	public void onResume()
	{
		super.onResume();
		SharedPreferences settings = getActivity().getSharedPreferences ("POS_ORDER", 0);
		cashBoxMoneyDefault = settings.getInt("CashBoxMoneyDefault", 0);
		cashBoxMoney = settings.getInt("CashBoxMoney", cashBoxMoneyDefault);
		todayMoney = settings.getInt("TodayMoney", 0);
		waitMoney = settings.getInt("WaitMoney", 0);
		monthMoney = settings.getInt("MonthMoney", 0);
		orderNumber = settings.getInt("OrderNumber", 1);
		SN = settings.getInt("SN", 1);
		orderNumberView = (TextView)fragmentView.findViewById(R.id.TextViewOrderNumber);
		orderNumberView.setText(String.valueOf(orderNumber));
		
		NumberFormat formatter = new DecimalFormat("###,###,###");
		cashBoxMoneyView.setText(formatter.format(cashBoxMoney));
		todayMoneyView.setText(formatter.format(todayMoney));
		waitMoneyView.setText(formatter.format(waitMoney));
		monthMoneyView.setText(formatter.format(monthMoney));
	}
	private void popBill()
	{
		PopupBillInputWindow popupNumberInputWindow = new PopupBillInputWindow(this.inflater, getResources(), total);
		popupNumberInputWindow.setCallback(this);
		popWindow = new PopupWindow(1280, 748);
		popWindow.setContentView(popupNumberInputWindow.popView);
		popWindow.update();
		popWindow.setFocusable(true);
		popWindow.showAtLocation(this.fragmentView, Gravity.LEFT|Gravity.TOP, 0, 0);
	}
	private void pop(int again)
	{  
		// 輸入金額
		if(sold_product.getSoldPrice() == 0)
		{
			PopupNumberInputWindow popupNumberInputWindow = new PopupNumberInputWindow(this.inflater, getResources(), sold_product.getSoldName(), 1, again);
			popupNumberInputWindow.setCallback(this);
			popWindow = new PopupWindow(1280, 748);
			popWindow.setContentView(popupNumberInputWindow.popView);
			popWindow.update();
			popWindow.setFocusable(true);
			popWindow.showAtLocation(this.fragmentView, Gravity.LEFT|Gravity.TOP, 0, 0);
		}
		// 輸入數量
		else
		{
			PopupNumberInputWindow popupNumberInputWindow = new PopupNumberInputWindow(this.inflater, getResources(), sold_product.getSoldName(), 0, again);
			popupNumberInputWindow.setCallback(this);
			popWindow = new PopupWindow(1280, 748);
			popWindow.setContentView(popupNumberInputWindow.popView);
			popWindow.update();
			popWindow.setFocusable(true);
			popWindow.showAtLocation(this.fragmentView, Gravity.LEFT|Gravity.TOP, 0, 0);
		}
		
	}
	
	private OnTabChangeListener menuTabChange = new OnTabChangeListener() 
	{
		@Override
		public void onTabChanged(String tabId) 
		{
			category = getCategoryById(Integer.parseInt((tabId)));
			FragmentTransaction ft  = getFragmentManager().beginTransaction();
			ft.replace(android.R.id.tabcontent, hash_fragment.get(tabId));
			ft.commit();
			
			/*
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
			}*/
		  }
	};
	

	@SuppressLint("SimpleDateFormat")
	private void initViews()
	{
		tabHost = (TabHost)fragmentView.findViewById(android.R.id.tabhost);
		tabHost.setup(); 
		textViewTotal = (TextView)fragmentView.findViewById(R.id.TextViewTotal);
		
		Button payButton = (Button)fragmentView.findViewById(R.id.PayButton);
		payButton.setOnClickListener(clickPayButton);
		Button cancelButton = (Button)fragmentView.findViewById(R.id.CancelButton);
		cancelButton.setOnClickListener(clickCancelButton);
		Button orderListButton = (Button)fragmentView.findViewById(R.id.buttonQueryOrder);
		orderListButton.setOnClickListener(clickOrderListButton);
		Button dataButton = (Button)fragmentView.findViewById(R.id.ButtonQueryData);
		dataButton.setOnClickListener(clickDataButton);
		radioGroup = (RadioGroup) fragmentView.findViewById(R.id.myRadioGroup);
		radioGroup.check(R.id.radioButton0);
		radio0 = (RadioButton) fragmentView.findViewById(R.id.radioButton0);
	    radio1 = (RadioButton) fragmentView.findViewById(R.id.radioButton1);
	    radioGroup.setOnCheckedChangeListener(changeRadio); 
		titleView = (TextView)fragmentView.findViewById(R.id.textViewTitle);
		switch(mode)
		{
			case 0:
				titleView.setText("門市現金");
				titleView.setTextColor(Color.BLUE);
				break;
			case 1:
				titleView.setText("預約自取");
				titleView.setTextColor(0xFF1BA31D);
				payButton.setText("儲 存");
				radioGroup.setVisibility(View.INVISIBLE);
				break;
			case 2:
				titleView.setText("外送");
				titleView.setTextColor(Color.RED);
				payButton.setText("儲 存");
				radioGroup.setVisibility(View.INVISIBLE);
				break;
			case 3:
				titleView.setText("月結");
				titleView.setTextColor(0xFF5200A0);
				payButton.setText("儲 存");
				radioGroup.setVisibility(View.INVISIBLE);
				break;
		}
		
		
		cashBoxMoneyView = (TextView)fragmentView.findViewById(R.id.textViewCashBox);
		todayMoneyView = (TextView)fragmentView.findViewById(R.id.TextViewTodayCash);
		waitMoneyView = (TextView)fragmentView.findViewById(R.id.TextViewWaitCash);
		monthMoneyView = (TextView)fragmentView.findViewById(R.id.TextViewMonthMoney);
		
		
		
		dateView = (TextView)fragmentView.findViewById(R.id.TextViewDate);
		
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		dateView.setText(sdf.format(new Date()));
		
		popWindow = new PopupWindow(1280, 748);
	}
	
	private RadioGroup.OnCheckedChangeListener changeRadio = new 
	           RadioGroup.OnCheckedChangeListener()
	{ 
	    @Override 
	    public void onCheckedChanged(RadioGroup group, int checkedId)
	    { 
	      if(checkedId == radio0.getId())
		  { 
	    	  inDoor = 0;
		  } 
	      if(checkedId == radio1.getId())
	      { 
	    	  inDoor = 1;
	      } 
	     
	    
	    } 
	  }; 

	@Override
	//public void onButtonClicked(int sectionNo, int product_id) {
	public void onButtonClicked(Product p, int product_id) {
		Log.v("Fragment0", p.pos + " " + product_id);
		if(popWindow != null && popWindow.isShowing() == false)
		{
			product = MainActivity.dbhelper.getProductById(product_id);
			sold_product = new SoldProduct(product);
			
			targetName = sold_product.getSoldName();
			
			if(product.name.matches("辦桌"))
				pop(1);
			else 	
				pop(0);
		}		
	}

	@Override
	public void onNumberEnterClicked(int count, double discount, int pop) {
		if(count == 0){ // 數量=0
			popWindow.dismiss();
			return;
		}
		
		if(sold_product.getSoldPrice() == 0){ // 要自訂價錢又要自訂份數的
			popWindow.dismiss();
			boolean res = sold_product.setSoldPrice(count);
			if(res)
				Log.v("Msg","set ok");
			else
				Log.v("Msg","set failed");
			
			if(pop == 1){
				pop(0);return;
			}else{
				count = 1;
			}
		}
		
		if(discount != 1.0 && discount >= 0)
			sold_product.setDiscount(discount);
		
		targetName = sold_product.getSoldName();
		
		NumberFormat formatter = new DecimalFormat("###,###,###");
			
		int index = product_data.indexOf(targetName);
		Log.v("Msg","index "+index);
		Log.v("Msg","count in order"+count);
		if(index == -1 || product.sticker_price == 0)
		{
			product_data.add(targetName);
			sold_product.setCount(count);
			adapter.AddRow(sold_product);
			Log.v("Msg","add new row");
		}
		else
		{	
			product_data.remove(index);
			product_data.add(targetName);
			sold_product = (SoldProduct) adapter.getItem(index);
			sold_product.addCount(count);

			adapter.setRow(index, sold_product);
		}
		total = adapter.getTotalAmount();

		Log.v("Msg","Total Price:"+ sold_product.getTotalPrice());
			
		textViewTotal.setText(formatter.format(total));
		listView.setSelection(adapter.getCount() - 1);
		popWindow.dismiss();
		
			// special case, 買一送一
			/*if(targetSectionNo == 0 && targetBtnNo == 12)
			{
				//targetName = String.format(productMap.productNamesDiscount[targetSectionNo][targetBtnNo], discountMapping(0.0));
				targetName = targetName + " (" + discountMapping(0.0)+")HaHaa";
				targetPrice = (int) Math.round(targetPrice * 0);
				index = product_data.indexOf(targetName);
				//if(index == -1 || productMap.productPrice[targetSectionNo][targetBtnNo] == 0)
				if(index == -1 || product.sticker_price == 0)
				{
					product_data.add(targetName);
					row_data.add(new ListRow(targetName, String.valueOf(targetPrice), String.valueOf(count), String.valueOf(targetPrice * count), 0, targetSectionNo * 1000 + targetBtnNo));
					
				}
				else
				{
					product_data.remove(index);
					product_data.add(targetName);
					ListRow rowData = row_data.get(index);
					int newCount = Integer.parseInt(rowData.title2) + count;
					rowData = new ListRow(targetName, String.valueOf(targetPrice), String.valueOf(newCount), String.valueOf(targetPrice * newCount), 0, targetSectionNo * 1000 + targetBtnNo);
					row_data.remove(index);
					row_data.add(rowData);
				}
			}
			
		adapter.notifyDataSetChanged();
		listView.setSelection(adapter.getCount() - 1);
		popWindow.dismiss();
		*/
	}
	
	@Override
	public void onDeleteButtonClicked(int position) {
		NumberFormat formatter = new DecimalFormat("###,###,###");
		
		adapter.delRow(position);
		total = adapter.getTotalAmount();
		product_data.remove(position);
		listView.setSelection(adapter.getCount() - 1);
		textViewTotal.setText(formatter.format(total));
		Log.v("Msg","tcgarita OrderSectionFragment");
	}
	
	private Button.OnClickListener clickPayButton = new Button.OnClickListener()
	{
		public void onClick(View v)
		{
			if(popWindow.isShowing() == false)
			{
				if(mode == 0 && product_data.size() > 0)
				{
					popBill();
					return;
				}
				if(product_data.size() > 0)
				{
					MainActivity.dbhelper.addOrder(adapter.getData(), mode, total, orderNumber, SN);					NumberFormat formatter = new DecimalFormat("###,###,###");
					if(mode == 0)
					{
						cashBoxMoney += total;
						cashBoxMoneyView.setText(formatter.format(cashBoxMoney));
						todayMoney += total;
						todayMoneyView.setText(formatter.format(todayMoney));
					}
					else if(mode == 1 || mode == 2)
					{
						waitMoney += total;
						waitMoneyView.setText(formatter.format(waitMoney));
						OrderActivity orderActivity = (OrderActivity)getActivity();
						orderActivity.addWaitOrderCount();
						
					}
					else if(mode == 3)
					{
						monthMoney += total;
						monthMoneyView.setText(formatter.format(monthMoney));
					}
					
					
					
					SharedPreferences pref = getActivity().getSharedPreferences("POS_ORDER", 0);
					int times = pref.getInt("PrintTimes", 1);
					
		    		printRecipe(getActivity(), times);
		    		orderNumber += 1;	
					orderNumberView.setText(String.valueOf(orderNumber));
					SharedPreferences settings = getActivity().getSharedPreferences ("POS_ORDER", 0);
					SharedPreferences.Editor PE = settings.edit();
					PE.putInt("CashBoxMoney", cashBoxMoney);
					PE.putInt("TodayMoney", todayMoney);
					PE.putInt("WaitMoney", waitMoney);
					PE.putInt("MonthMoney", monthMoney);
					PE.putInt("OrderNumber", orderNumber);
					PE.commit();
					tabHost.setCurrentTab(0);
					callback.onChangeToTab0();
					
				}
			}
		}
	};
	private Button.OnClickListener clickCancelButton = new Button.OnClickListener()
	{
		public void onClick(View v)
		{
			
			product_data.clear();
			adapter.clearRow();
			sold_product = null;
			total = 0;
//			textViewTotal.setText(String.valueOf(total));
			textViewTotal.setText(String.valueOf(adapter.getTotalAmount()));
			radioGroup.check(R.id.radioButton0);
			
		}
	};
	// 訂單查詢
	private Button.OnClickListener clickOrderListButton = new Button.OnClickListener()
	{
		public void onClick(View v)
		{
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putInt("mode", 0);
			intent.setClass(getActivity(), OrderListActivity.class);
			intent.putExtras(bundle);
			startActivity(intent);
		}
	};
	// 銷售數據
	private Button.OnClickListener clickDataButton = new Button.OnClickListener()
	{
		public void onClick(View v)
		{
			Bundle bundle = new Bundle();
			bundle.putInt("mode", 0);
			Intent intent = new Intent();
			intent.setClass(getActivity(), StatisticsActivity.class);
			intent.putExtras(bundle);
			startActivity(intent);
		}
	};
	

	private void printRecipe(Context context, int times)
	{
		StarIOPort port = null;
		
		try 
    	{		
			SharedPreferences pref = context.getSharedPreferences("POS_ORDER", 0);
			String portName = pref.getString("portName", "");
			
			if(portName.equalsIgnoreCase("") || mode == 3 || times == 0)
			{
				port = StarIOPort.getPort(portName, "", 5000, context);
				byte[] openCashDrawer = new byte[] {0x07};
				port.writePort(openCashDrawer, 0, openCashDrawer.length);
				product_data.clear();
				adapter.clearRow();
				total = 0;
				textViewTotal.setText(String.valueOf(total));
				radioGroup.check(R.id.radioButton0);
				return;
			}
			//旗艦
			/*
			port = StarIOPort.getPort(portName, "", 5000, context);
			if(mode == 0)
			{
				//open cash drawer
				byte[] openCashDrawer = new byte[] {0x07};
				port.writePort(openCashDrawer, 0, openCashDrawer.length);
			}
			
			
			*/
			port = StarIOPort.getPort(portName, "", 2000, context);
			
			//print recipe
			for(int i = 0; i < times; i++)
			{
					
					
					//
					byte[] outputByteBuffer = null;
					//port.writePort(new byte[]{0x1b, 0x3f}, 0, 2);
					port.writePort(new byte[]{0x1b, 0x40}, 0, 2);                         // Initialization
		
					port.writePort(new byte[]{0x1b, 0x24, 0x31}, 0, 3);                   // 漢字モード設定
		            port.writePort(new byte[]{0x1b, 0x44, 0x10, 0x00}, 0, 4);             // 水平タブ位置設定
					port.writePort(new byte[]{0x1b, 0x1d, 0x61, 0x31}, 0, 4);             // 中央揃え設定
					
					port.writePort(new byte[]{0x1b, 0x69, 0x02, 0x00}, 0, 4);             // 文字縦拡大設定
					port.writePort(new byte[]{0x1b, 0x45}, 0, 2);                         // 強調印字設定
			
					outputByteBuffer = createShiftBIG5("香港湧記燒鵝" + "\n");
					port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
					
					port.writePort(new byte[]{0x1b, 0x69, 0x01, 0x00}, 0, 4);             // 文字縦拡大設定
					
					outputByteBuffer = createShiftBIG5(titleView.getText() + "訂單 #" + String.valueOf(orderNumber) + "\n");
					port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
					
					if(mode == 0)
					{
						if(inDoor == 1)
						{
							outputByteBuffer = createShiftBIG5("內用\n");
							port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
						}
						else
						{
							outputByteBuffer = createShiftBIG5("外帶\n");
							port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
						}
					}
					else if(mode == 1)
					{
						outputByteBuffer = createShiftBIG5("預約自取\n");
						port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
					}
					else if(mode == 2)
					{
						outputByteBuffer = createShiftBIG5("外送\n");
						port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
					}
					else if(mode == 3)
					{
						outputByteBuffer = createShiftBIG5("月結\n");
						port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
					}
				
					
					port.writePort(new byte[]{0x1b, 0x46}, 0, 2);                         // 強調印字解除
					
					//port.writePort(new byte[]{0x1b, 0x69, 0x00, 0x00}, 0, 4);             // 文字縦拡大解除
					outputByteBuffer = createShiftBIG5("——————————————————————\n");
					port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
					Date now = new Date();
					String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now);
					
					port.writePort(new byte[]{0x1b, 0x1d, 0x61, 0x30}, 0, 4);            //左揃え設定
					
					outputByteBuffer = createShiftBIG5("訂單時間：" + nowTime + "\n\n");
					port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
					
					//outputByteBuffer = createShiftBIG5("--------------------------------------------\n");
					//port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
					String productTitleStr = String.format("%-14s%8s%7s%7s", "品項", "單價", "數量", "小計");
				    outputByteBuffer = createShiftBIG5(productTitleStr + "\n");
					port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
					outputByteBuffer = createShiftBIG5("——————————————————————\n");
					port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
					NumberFormat formatter = new DecimalFormat("###,###,###");
					int total = 0;

					ArrayList<SoldProduct> row_data = adapter.getData();
					for(SoldProduct row : row_data){
//					ArrayList<ListRow> row_data = adapter.getData();
//					for(ListRow row : row_data) {
//						total += Integer.parseInt(row.title3);
				       // String productStr = String.format("%1$-12s%-7s%7s%7s", row.title0, row.title1, row.title2, row.title3);
//						String productStr = String.format("%-7s%5s%8s%9s", 
//								row.title0, 
//								formatter.format(Integer.parseInt(row.title1)), 
//								row.title2, 
//								formatter.format(Integer.parseInt(row.title3)));
						
						String productStr = String.format("%-7s%5s%8s%9s", 
								row.getSoldName(), 
								formatter.format(row.getFinalSoldPrice()), 
								row.getCountString(), 
								formatter.format(row.getTotalPriceString()));
						
				        outputByteBuffer = createShiftBIG5(productStr + "\n");
				        System.out.println(productStr);
						port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
				    }
					total = adapter.getTotalAmount();
					outputByteBuffer = createShiftBIG5("——————————————————————\n");
					port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
					productTitleStr = String.format("%-33s%7s", "結算金額", formatter.format(total));
				    outputByteBuffer = createShiftBIG5(productTitleStr + "\n");
					port.writePort(outputByteBuffer, 0, outputByteBuffer.length);
					
					port.writePort(new byte[]{0x1b, 0x64, 0x33}, 0, 3); //CUT
					
					//自由
					
					
					
					
					
    		}
			
			Thread.sleep(300);
	
			if(mode == 0)
			{
				//open cash drawer
				byte[] openCashDrawer = new byte[] {0x07};
				port.writePort(openCashDrawer, 0, openCashDrawer.length);
			}
		

			
			product_data.clear();
			adapter.clearRow();
			total = 0;
			//textViewTotal.setText(String.valueOf(total));
			textViewTotal.setText("0");
			radioGroup.check(R.id.radioButton0);

    	}
    	catch (StarIOPortException e)
    	{
    		Builder dialog2 = new AlertDialog.Builder(getActivity());
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
		    SharedPreferences pref = getActivity().getSharedPreferences("POS_ORDER", 0);
    		Editor editor = pref.edit();
    		editor.putString("portName", "");
    		editor.commit();
    		
    	} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	public void portDiscovery()
    {
		ProgressDialog dialog = ProgressDialog.show(getActivity(), "", "連結出單機中請稍待...", true);
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
    		SharedPreferences pref = getActivity().getSharedPreferences("POS_ORDER", 0);
    		Editor editor = pref.edit();
    		editor.putString("portName", arrayDiscovery.get(0).getPortName());
    		editor.commit();
    		dialog.dismiss();
    			
    		int times = pref.getInt("PrintTimes", 1);
    	
 	       
    		printRecipe(getActivity(), times);
    		
    	}
    	else
    	{
    		dialog.dismiss();
    		Builder dialog2 = new AlertDialog.Builder(getActivity());
    		dialog2.setTitle("無法連接出單機");
    		dialog2.setMessage("1.請確認出單機已開機\n2.請確認無線基地台已開機並與出單機正常連接。\n3.排除問題後請按重新連結。");
		    
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
		    SharedPreferences pref = getActivity().getSharedPreferences("POS_ORDER", 0);
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
			SharedPreferences pref = getActivity().getSharedPreferences("POS_ORDER", 0);
    		Editor editor = pref.edit();
    		editor.putString("portName", "");
    		editor.commit();
    		dialog.dismiss();
    		product_data.clear();
			total = 0;
			adapter.clearRow();

			//textViewTotal.setText(String.valueOf(total));
			textViewTotal.setText(String.valueOf(adapter.getTotalAmount()));
			radioGroup.check(R.id.radioButton0);
		}
		
	};
	public static String padRight(String s, int n) {
	     return String.format("%1$-" + n + "s", s);  
	}

	public static String padLeft(String s, int n) {
	    return String.format("%1$" + n + "s", s);  
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
	public static void AddRange(ArrayList<Byte> array, Byte[] newData)
	{
		for(int index=0; index<newData.length; index++)
		{
			array.add(newData[index]);
		}
	}


	@Override
	public void onSubmitEnterClicked(int count) {
		if(isPaying == false)
		{
			isPaying = true;
			popWindow.dismiss();
			if(count == -1)
			{
				
			}
			else
			{
				if((count - total) > 0)
				{
					Builder dialog2 = new AlertDialog.Builder(getActivity());
		    		dialog2.setTitle("找零金額");
		    		dialog2.setMessage("$"+ String.valueOf(count - total));
		    		dialog2.setNeutralButton("確定", confirmClick);
				    AlertDialog dialogView = dialog2.show();
				    textViewMsg = (TextView) dialogView.findViewById(android.R.id.message);
				    textViewMsg.setTextSize(40);
				    textViewMsg.setVisibility(View.INVISIBLE);
				    textViewMsg.setGravity(Gravity.CENTER);
				    textViewMsg.setTextColor(Color.RED);
				    final int alertTitle = getActivity().getResources().getIdentifier( "alertTitle", "id", "android" );
				    TextView textViewTitle = (TextView) dialogView.findViewById(alertTitle);
				    
				    textViewTitle.setGravity(Gravity.CENTER);
				    textViewTitle.setTextSize(40);
				
				    Button btn1 = dialogView.getButton(DialogInterface.BUTTON_NEUTRAL);
				    btn1.setTextSize(40);
				    btn1.setTextColor(Color.BLUE);
				    startRepeatingTask();
				    
				}
								
				MainActivity.dbhelper.addOrder(adapter.getData(), mode, total, orderNumber, SN);
				
				NumberFormat formatter = new DecimalFormat("###,###,###");
				
				cashBoxMoney += total;
				cashBoxMoneyView.setText(formatter.format(cashBoxMoney));
				todayMoney += total;
				todayMoneyView.setText(formatter.format(todayMoney));
				
				
				SharedPreferences pref = getActivity().getSharedPreferences("POS_ORDER", 0);
				int times = pref.getInt("PrintTimes", 1);
				
	    		printRecipe(getActivity(), times);
	    		orderNumber += 1;	
				orderNumberView.setText(String.valueOf(orderNumber));
				SharedPreferences settings = getActivity().getSharedPreferences ("POS_ORDER", 0);
				SharedPreferences.Editor PE = settings.edit();
				PE.putInt("CashBoxMoney", cashBoxMoney);
				PE.putInt("TodayMoney", todayMoney);
				PE.putInt("WaitMoney", waitMoney);
				PE.putInt("MonthMoney", monthMoney);
				PE.putInt("OrderNumber", orderNumber);
				PE.commit();
			}
			isPaying = false;
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

	public Category getCategoryById(int id){
		for(Category c : categories){
			if( c.id == id )
				return c;
		}
		return null;
	}
}
