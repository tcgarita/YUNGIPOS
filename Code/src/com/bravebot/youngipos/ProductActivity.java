package com.bravebot.youngipos;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import com.bravebot.youngipos.ProductSettingSectionFragment.OrderSectionFragmentListener;
import android.util.Log;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class ProductActivity extends FragmentActivity implements
ActionBar.TabListener, OrderSectionFragmentListener{
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	private ProductSettingSectionFragment fragment0;
	public ActionBar actionBar;
	private int waitOrderCount = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
		setContentView(R.layout.activity_order);
		// Set up the action bar to show tabs.
		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setHomeButtonEnabled(true);
		// For each of the sections in the app, add a tab to the action bar.
		actionBar.addTab(actionBar.newTab().setText("門市現金")
				 .setTabListener(this));
		
		actionBar.addTab(actionBar.newTab().setText(R.string.order_section_title1)
				 .setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText(R.string.order_section_title2)
				 .setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText("月結")
				 .setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText("待結訂單")
				 .setTabListener(this));
		
		Log.v("Msg","Order ACtivity Constructor");
		SharedPreferences settings = getSharedPreferences ("POS_ORDER", 0);
		//waitOrderCount = settings.getInt("WaitOrderCount", 0);

		
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		actionBar.selectTab(actionBar.getTabAt(0));
		waitOrderCount = getWaitBillNumber();
		if(waitOrderCount != 0)
		{
			actionBar.removeTabAt(4);
			actionBar.addTab(actionBar.newTab().setText("待結訂單(" + String.valueOf(waitOrderCount) + ")" )
					 .setTabListener(this));
		}
		else
		{
			actionBar.removeTabAt(4);
			actionBar.addTab(actionBar.newTab().setText("待結訂單")
					 .setTabListener(this));
		}
	}
	private int getWaitBillNumber()
	{
		SQLiteDatabase db = MainActivity.dbhelper.getReadableDatabase();
		Log.v("Msg","Use db in getWaitBillNumber");
		Cursor cursor = db.rawQuery("select * from " + DBConstants.ORDER_TABLE_NAME + " WHERE " + DBConstants.ORDER_STATUS + " =0"+
				" AND " + DBConstants.ORDER_DETAIL_DELETE + "=0", null);
		return  cursor.getCount();
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current tab position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current tab position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
				.getSelectedNavigationIndex());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
	}

	

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		if(tab.getPosition() == 0)
		{
			fragment0 = new ProductSettingSectionFragment();
			Bundle bundle = new Bundle();
			bundle.putInt("mode", 0);
			fragment0.setArguments(bundle);
			fragment0.callback = this;
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.container, fragment0).commit();
		}
		else if(tab.getPosition() == 1)
		{
			fragment0 = new ProductSettingSectionFragment();
			Bundle bundle = new Bundle();
			bundle.putInt("mode", 1);
			fragment0.setArguments(bundle);
			fragment0.callback = this;
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.container, fragment0).commit();
		}
		else if(tab.getPosition() == 2)
		{
			fragment0 = new ProductSettingSectionFragment();
			Bundle bundle = new Bundle();
			bundle.putInt("mode", 2);
			fragment0.setArguments(bundle);
			fragment0.callback = this;
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.container, fragment0).commit();
		}
		else if(tab.getPosition() == 3)
		{
			fragment0 = new ProductSettingSectionFragment();
			Bundle bundle = new Bundle();
			bundle.putInt("mode", 3);
			fragment0.setArguments(bundle);
			fragment0.callback = this;
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.container, fragment0).commit();
			
		}
		else if(tab.getPosition() == 4)
		{
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putInt("mode", 1);
			intent.setClass(this, OrderListActivity.class);
			intent.putExtras(bundle);
			startActivity(intent);
			
			
		}
		
	}
	public void addWaitOrderCount()
	{
		waitOrderCount += 1;
		actionBar.removeTabAt(4);
		actionBar.addTab(actionBar.newTab().setText("待結訂單(" + String.valueOf(waitOrderCount) + ")")
				 .setTabListener(this));
		SharedPreferences settings = getSharedPreferences("POS_ORDER", 0);
		//SharedPreferences.Editor PE = settings.edit();
		//PE.putInt("WaitOrderCount", waitOrderCount);
		//PE.commit();
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		
		
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

	@Override
	public void onChangeToTab0() {
		actionBar.setSelectedNavigationItem(0);
		
	}

}
