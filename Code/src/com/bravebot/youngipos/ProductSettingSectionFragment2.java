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

import com.bravebot.youngipos.PopupBillInputWindow.ItemButton_Click;

import android.R.drawable;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TabHost;
import android.widget.TextView;

@SuppressLint({ "SimpleDateFormat", "UseSparseArrays" })
public class ProductSettingSectionFragment2 extends Fragment implements FragmentMenu.OnClickOrderButtonListener, PopupNumberInputWindow.OnClickNumberButtonListener, RowAdapter.OnClickDeleteButtonListener
{
	private View fragmentView;
	private ListView listView;
	private TabHost tabHost;
	private Handler m_handler;
	private Product product;
	private ArrayList<Category> categories;
	private Category category;
	private PopupWindow popWindow;
	private LayoutInflater inflater;
	private int mode;
	private View pop_view;
	private Button btn_edit_category;
	private Button btn_del_category;
	private Button btn_add_category;
	private Button btn_edit_product;
	private Button btn_del_product;
	private Button btn_add_product;
	private CheckBox customize_checkbox;
	private Drawable d;
	private HashMap<String, Fragment> hash_fragment;
	private ProductSettingSectionFragment2 self;
	View btn_prev_view = null;
	int save = -1;
	
	
	public enum Alignment {Left, Center, Right};

	public OrderSectionFragmentListener callback;

	
	public ProductSettingSectionFragment2() {
		
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
		fragmentView = inflater.inflate(R.layout.product_setting2, container, false);
		this.initButtons();
		categories = MainActivity.dbhelper.getAllCategory();
		listView = (ListView) fragmentView.findViewById(R.id.listView1);
		CategoryAdapter c_adapter = new CategoryAdapter(getActivity(), 
				R.layout.category_list_item, categories);
		listView.setAdapter(c_adapter);
		
		int first_cat = 0;
		hash_fragment =	new HashMap<String, Fragment>();
		for(Category  c: categories) {
			Fragment fragment = new FragmentMenu();
			((FragmentMenu) fragment ).setCategory(c.id);
			((FragmentMenu) fragment ).setCallback(this);		
			hash_fragment.put(String.valueOf(c.id), fragment);	
			if(first_cat == 0){
				first_cat = c.id;
				category = c;
			}
		}
		
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long arg3) {
				
				category = (Category) listView.getItemAtPosition(position);
				
				FragmentTransaction ft  = getFragmentManager().beginTransaction();
				ft.replace(android.R.id.tabcontent, 
						hash_fragment.get(String.valueOf(category.id)));
				ft.commit();
				
				view.setBackgroundResource(R.color.lightBlue);
				if( save != -1 && save != position )
					parent.getChildAt(save).setBackgroundResource(R.color.transparent);
				save = position;
				product = null;
				
			}
		});
		if(listView.getCount() > 0){
			save = 0;
	    	listView.performItemClick(
	    			listView.getAdapter().getView(0, null, null), 
	    			0, listView.getAdapter().getItemId(0));
		}
		self = this;
		return fragmentView;
	}
	@Override
	public void onResume()
	{
		super.onResume();
	}

	@SuppressLint("SimpleDateFormat")
	private void initButtons()
	{
		btn_edit_category = (Button) fragmentView.findViewById(R.id.button1);
		btn_edit_category.setOnClickListener(new View.OnClickListener()  {
            public void onClick(View view)  {
            	if(category!=null){
            		CategoryPopupWindow(R.layout.popup_category_edit,"編輯類別"
            			,category.name,
            			new View.OnClickListener() {
    						@Override
    						public void onClick(View v) {
    							String new_name = ((EditText) pop_view.findViewById(R.id.edit)).getText().toString();
    							MainActivity.dbhelper.editCategoryById(category.id, new_name);    				
    							((CategoryAdapter)listView.getAdapter()).editCategory(category,new_name);
    							popWindow.dismiss();
    						}
    					});
            	}
            }
		});
		btn_del_category = (Button) fragmentView.findViewById(R.id.button2);
		btn_del_category.setOnClickListener(new View.OnClickListener()  {
            public void onClick(View view)  {
            	if(category!=null){
            		del_dialog("即將刪除"+category.name+"\n(類別內所有品項都會一起刪除)",
            			new DialogInterface.OnClickListener() {
                    		@Override
                    		public void onClick(DialogInterface dialog, int which) {
                    			MainActivity.dbhelper.delCategoryById(category.id);
                    			((CategoryAdapter) listView.getAdapter()).remove(category);
                    			FragmentMenu temp = (FragmentMenu) hash_fragment.get(String.valueOf(category.id));
                    			/*((FragmentMenu) hash_fragment
                           				.get(String.valueOf(category.id)))
                           				.getButtonAdapter()
                           				.clear();
                           				*/
                    			temp.getButtonAdapter().clear();
                    			hash_fragment.remove(temp);
                    			product = null;
                    			category = null;
                    			save = -1;
                    		}
                	});
            	}
            }
		});
		btn_add_category = (Button) fragmentView.findViewById(R.id.button5);
		btn_add_category.setOnClickListener(new View.OnClickListener()  {
            public void onClick(View view)  {
            	Log.v("Msg","add cat");
            	CategoryPopupWindow(R.layout.popup_category_edit,"新增類別"
                		,"",
                		new View.OnClickListener() {
        					@Override
        					public void onClick(View v) {
        						String new_name = ((EditText) pop_view.findViewById(R.id.edit)).getText().toString();
        						long id = MainActivity.dbhelper.addCategory(new_name);
        						Category cat = new Category((int)id,new_name);
        						((CategoryAdapter)listView.getAdapter()).addCategory(cat);
        						
        						Fragment fragment = new FragmentMenu();
        						((FragmentMenu) fragment ).setCategory(cat.id);
        						((FragmentMenu) fragment ).setCallback(self);		
        						hash_fragment.put(String.valueOf(cat.id), fragment);
        						
        						product = null;
                    			category = null;
                    			save = -1;
                    			
                    			popWindow.dismiss();
        					}
        		});
            }
		});
		btn_add_product = (Button) fragmentView.findViewById(R.id.button4);
		btn_add_product.setOnClickListener(new View.OnClickListener()  {
            public void onClick(View view)  {

            	Log.v("Msg","add cat");
            }
		});
		btn_edit_product = (Button) fragmentView.findViewById(R.id.button6);
		btn_edit_product.setOnClickListener(new View.OnClickListener()  {
            public void onClick(View view)  {
            	if(product!=null){
            		Log.v("Msg","add cat");
            	}
            }
		});
		btn_del_product = (Button) fragmentView.findViewById(R.id.button3);
		btn_del_product.setOnClickListener(new View.OnClickListener()  {
            public void onClick(View view)  {
            	if(product!=null){
            		del_dialog("即將刪除"+product.name,
            			new DialogInterface.OnClickListener() {
                    		@Override
                    		public void onClick(DialogInterface dialog, int which) {
                    			MainActivity.dbhelper.delProductById(product.id);
                    			((FragmentMenu) hash_fragment
                    				.get(String.valueOf(category.id)))
                    				.getButtonAdapter()
                    				.delProduct(product.pos);
                    			btn_prev_view.setBackground(d);
                    			product = null;
                    		}
                		});
            	}
            }
		});
		
	/*
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
		*/
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
	public void onButtonClicked(Product p, int product_id, View v) {
		// TODO Auto-generated method stub
		Log.v("Msg","Oh: "+ p.name);
		product = p;
		if(btn_prev_view == null)
			d = v.getBackground();
		
		v.setBackgroundResource(R.color.lightBlue);
		
		if(btn_prev_view != null && btn_prev_view != v)
			btn_prev_view.setBackground(d);
		
		btn_prev_view = v;
	}

	public void onCategoryClicked(Category cat){
		
	}
	
	public void setAlert(String title, String msg){
		Builder dialog = new AlertDialog.Builder(getActivity());
		dialog.setTitle(title).setMessage(msg);
		dialog.setCancelable(true);
		dialog.show();
	}

	public AlertDialog del_dialog(String message, DialogInterface.OnClickListener listener){
		AlertDialog dialog = new AlertDialog.Builder(getActivity())
    	.setIcon(android.R.drawable.ic_dialog_alert)
    	.setTitle("刪除")
    	.setMessage(message)
    	.setPositiveButton("確定", listener)
    	.setNegativeButton("取消", null)
    	.show();
		
		TextView text = (TextView) dialog.findViewById(android.R.id.message);
		text.setTextSize(32);
		text.setGravity(Gravity.CENTER);
		return dialog;
	}
	
	public void CategoryPopupWindow (int resource, String title, String message, 
			Button.OnClickListener submit_listener){
    	pop_view = inflater.inflate(resource,null);
    	((ImageButton) pop_view.findViewById(R.id.ButtonCancel))
    		.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				popWindow.dismiss();
			}
		});
		((Button) pop_view.findViewById(R.id.cancel))
			.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				popWindow.dismiss();
			}
		});
		((Button) pop_view.findViewById(R.id.submit))
			.setOnClickListener(submit_listener);
		((TextView) pop_view.findViewById(R.id.title)).setText(title);
		((EditText) pop_view.findViewById(R.id.edit)).setText(message);
    	popWindow = new PopupWindow(1280,748);
    	popWindow.setContentView(pop_view);
		popWindow.update();
		popWindow.setFocusable(true);
		popWindow.showAtLocation(fragmentView, Gravity.LEFT|Gravity.TOP, 0, 0);
	}
}
