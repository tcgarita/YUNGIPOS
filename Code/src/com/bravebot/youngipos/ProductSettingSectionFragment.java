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
import java.util.Set;
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
import android.app.Dialog;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
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

@SuppressLint({ "SimpleDateFormat", "UseSparseArrays" })
public class ProductSettingSectionFragment extends Fragment implements FragmentMenu.OnClickOrderButtonListener, PopupNumberInputWindow.OnClickNumberButtonListener, RowAdapter.OnClickDeleteButtonListener
, PopupBillInputWindow.OnClickBillButtonListener{
	private View fragmentView;
	private TabHost tabHost;
	private Handler m_handler;
	private Product product;
	private ArrayList<Category> categories;
	private Category category;
	private PopupWindow popWindow;
	private LayoutInflater inflater;
	private int mode;
	private Button new_product_button;
	private Button save_product_button;
	private Button del_product_button;
	private CheckBox customize_checkbox;
	private HashMap<String, Fragment> hash_fragment;
	
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
		this.inflater = inflater;
		fragmentView = inflater.inflate(R.layout.product_setting, container, false);

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
				first_cat = c.id;
				category = c;
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
			category = getCategoryById(Integer.parseInt((tabId)));
			Log.v("Msg","Category on change:"+category.name);
			FragmentTransaction ft  = getFragmentManager().beginTransaction();
			ft.replace(android.R.id.tabcontent, hash_fragment.get(tabId));
			ft.commit();
			clearEditText();	
		 }
	};
	

	@SuppressLint("SimpleDateFormat")
	private void initViews()
	{
		tabHost = (TabHost)fragmentView.findViewById(android.R.id.tabhost);
		tabHost.setup();
	
		new_product_button = (Button) fragmentView.findViewById(R.id.button1);
		new_product_button.setOnClickListener(new View.OnClickListener()  {
            public void onClick(View view)  {
        		EditText editText1 = (EditText) fragmentView.findViewById(R.id.editText1);
        		EditText editText2 = (EditText) fragmentView.findViewById(R.id.editText2);
        		EditText editText3 = (EditText) fragmentView.findViewById(R.id.editText3);
        		EditText editText5 = (EditText) fragmentView.findViewById(R.id.editText5);
        		
        		editText1.setText("請填入產品名稱");
        		editText2.setText(categories.get(tabHost.getCurrentTab()).name);
        		editText3.setText(String.valueOf(75));
        		editText5.setText(String.valueOf(0));
        		product = null;
            }
       });
	
		save_product_button = (Button) fragmentView.findViewById(R.id.button2);
		save_product_button.setOnClickListener(new View.OnClickListener()  {
            public void onClick(View view)  {
            	// do something
        		EditText editText1 = (EditText) fragmentView.findViewById(R.id.editText1);
        		EditText editText2 = (EditText) fragmentView.findViewById(R.id.editText2);
        		EditText editText3 = (EditText) fragmentView.findViewById(R.id.editText3);
        		EditText editText5 = (EditText) fragmentView.findViewById(R.id.editText5);
        		CheckBox checkbox1 = (CheckBox) fragmentView.findViewById(R.id.checkBox1);
        		if(editText5.getText().toString().equals(""))
        			return;
        		String name = editText1.getText().toString();
        		String text_id = editText5.getText().toString();
    			int price;
    			if(checkbox1.isChecked())
    				price = 0;
    			else 
    				price = Integer.parseInt(editText3.getText().toString());
    			int cat = getCategoryIdByName(editText2.getText().toString());
    			
        		if( Integer.parseInt(text_id) == 0 ){
        			// 新增
        			Log.v("Msg","name:"+name+",cat:"+cat+",price:"+price);
        			long new_id = MainActivity.dbhelper.addProduct(editText1.getText().toString(), price, cat);
        			if( new_id > 0 ){
        				// TODO it's not a safety way to convert long to integer
        				Product new_product = new Product(name,price, (int) new_id, cat);
        				((FragmentMenu) hash_fragment.get(String.valueOf(category.id))).getButtonAdapter().addProduct(new_product);
        			} else {
        				setAlert("新增修改品項","新增產品失敗，請聯繫程式設計師");
        			}
        		}
        		else {
        			// 修改
        			if( product.id == Integer.parseInt(text_id)){
        				if( name.equals(product.name) && 
        					price == product.sticker_price && 
        					cat == product.cat_id)
        					return;
        				
        				// -1 表示沒更動, 因為 price = 0 有意義，所以統一用 -1  
        				String edit_name = "";
        				int edit_price = -1, edit_cat_id = -1;
        				
        				if( !name.equals(product.name) )
        					edit_name = name;
        				if(price != product.sticker_price)
        					edit_price = price;
        				if(cat != product.cat_id)
        					edit_cat_id = cat;
        				MainActivity.dbhelper.editProductById(product.id, edit_name, edit_price, edit_cat_id);
        				((FragmentMenu) hash_fragment.get(String.valueOf(category.id))).getButtonAdapter().editProduct(product.pos,edit_name, edit_price, edit_cat_id);
        				setAlert("新增修改品項","修改成功");
        			}
        		}
            }
       });
		
		
		del_product_button = (Button) fragmentView.findViewById(R.id.button3);
		del_product_button.setOnClickListener(new View.OnClickListener()  {
            public void onClick(View view)  {
            	if(product != null){
            		Log.v("Msg","product:"+product.name);
            		Log.v("Msg","category:"+category.name);
            		MainActivity.dbhelper.delProductById(product.id);
            		((FragmentMenu) hash_fragment.get(String.valueOf(category.id))).getButtonAdapter().delProduct(product.pos);
            		product = null;
            	}
            	clearEditText();
            }
       });
		
		customize_checkbox = (CheckBox) fragmentView.findViewById(R.id.checkBox1);
		customize_checkbox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				EditText editText3 = (EditText) fragmentView.findViewById(R.id.editText3);
				if(isChecked == true) {					
					editText3.setEnabled(false);
				} else {
					editText3.setEnabled(true);
				}
			}
		});
		
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
	public void onButtonClicked(Product p, int product_id) {
		// TODO Auto-generated method stub
		
		EditText editText1 = (EditText) fragmentView.findViewById(R.id.editText1);
		EditText editText2 = (EditText) fragmentView.findViewById(R.id.editText2);
		EditText editText3 = (EditText) fragmentView.findViewById(R.id.editText3);
		EditText editText5 = (EditText) fragmentView.findViewById(R.id.editText5);
		CheckBox checkbox1 = (CheckBox) fragmentView.findViewById(R.id.checkBox1);
		product = p; // product is a global variable.
		editText1.setText(product.name);
		editText2.setText(categories.get(tabHost.getCurrentTab()).name);
		editText3.setText(String.valueOf(product.sticker_price));
		editText5.setText(String.valueOf(product_id));
		if (product.sticker_price == 0) {
			checkbox1.setChecked(true);
		} else {
			checkbox1.setChecked(false);
		}
	}

	public Category getCategoryByName(String name) {
		for(Category c : categories){
			if( c.name.equals(name) )
				return c;
		}
		return null;
	}
	
	public Category getCategoryById(int id){
		for(Category c : categories){
			if( c.id == id )
				return c;
		}
		return null;
	}
	
	public int getCategoryIdByName(String name) {
		for(Category c : categories){
			if( c.name.equals(name) )
				return c.id;
		}
		return 0;
	}
	
	public void setAlert(String title, String msg){
		Builder dialog = new AlertDialog.Builder(getActivity());
		dialog.setTitle(title).setMessage(msg);
		dialog.setCancelable(true);
		dialog.show();
	}
	
	public void clearEditText() {
		EditText editText1 = (EditText) fragmentView.findViewById(R.id.editText1);
		EditText editText2 = (EditText) fragmentView.findViewById(R.id.editText2);
		EditText editText3 = (EditText) fragmentView.findViewById(R.id.editText3);
		EditText editText5 = (EditText) fragmentView.findViewById(R.id.editText5);
		
		editText1.setText("");
		editText2.setText("");
		editText3.setText("");
		editText5.setText("");
	}
}
