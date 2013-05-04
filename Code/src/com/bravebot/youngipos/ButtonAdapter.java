package com.bravebot.youngipos;

import java.util.ArrayList;
import java.util.List;

import com.bravebot.youngipos.FragmentMenu.OnClickOrderButtonListener;
import com.bravebot.youngipos.RowAdapter.ItemButton_Click;
import com.bravebot.youngipos.RowAdapter.OnClickDeleteButtonListener;

import android.os.Bundle;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

public class ButtonAdapter extends BaseAdapter {
	    private Context mContext;
	    private ArrayList<Product> products;
	    private int section_id;
	    
	    protected OnClickOrderButtonListener callback;
	   
	    
		public void setCallback(OnClickOrderButtonListener fragment)
		{
			try {
				callback = fragment;
		    } catch (ClassCastException e) {
		    	throw new ClassCastException(fragment.toString()
		    			+ " must implement OnClickButtonListener");
		    }
		}
	    
	    public ButtonAdapter(Context c,int cat_id) {
	    	this.section_id = cat_id;
	    	init_product_data(cat_id);
	        mContext = c;
	    }
	    
	    void init_product_data(int cat_id){
	    	products = MainActivity.dbhelper.getProductByCatId(cat_id);
	    }
	    public Object getItem(int position) {
	        return products.get(position);
	    }

	    public long getItemId(int position) {
	        return 0;
	    }

	    // create a new ImageView for each item referenced by the Adapter
	    public View getView(int position, View convertView, ViewGroup parent) {
	        Button btn;
	        if (convertView == null) {  // if it's not recycled, initialize some attributes
	            btn = new Button(mContext);
	            btn.setLayoutParams(new GridView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
	            btn.setWidth(130);
	            btn.setHeight(90);
	            btn.setPadding(8, 8, 8, 8);
	        } else {
	            btn = (Button) convertView;
	        }        
	        Product p = products.get(position);
	        btn.setId(p.id);
	        btn.setText(p.getNameandPrice());
	        btn.setOnClickListener(new ItemButton_Click(p));
	        
	        return btn;
	    }
	    
	    public void addProduct(Product p){
	    	products.add(p);
	    	this.notifyDataSetChanged();
	    }
	    
		@Override
		public int getCount() {
			return products.size();
		}
		
	    class ItemButton_Click implements OnClickListener {
	    	   private Product product;
	    	   ItemButton_Click(Product p) {
	    	       this.product = p;
	    	   }

	    	   @Override
	    	   public void onClick(View v) {
	    		   Log.v("Msg","section:"+String.valueOf(section_id)+",id:"+product.id);
	    		   callback.onButtonClicked(section_id,product.id);
	    	   }
	    }
}
