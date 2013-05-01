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
	    private List<Pair<String,String>> product_data;
	    private int section_id;
	    protected OnClickButtonListener callback;
	    
	    public interface OnClickButtonListener {
	        public void onOrderButtonClicked(int section_num,int position);
	    }
	    
		public void setCallback(Fragment fragment)
		{
			try {
				Log.v("Msg","set callback in ButtonAdapter");
				callback = (OnClickButtonListener) fragment;
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
	    	product_data = new ArrayList<Pair<String,String>>();
	    	SQLiteDatabase db = MainActivity.dbhelper.getReadableDatabase();
	    	Cursor cursor = db.rawQuery("select * from product where cat_id=?", 
	    						new String [] {String.valueOf(cat_id)});
	        if(cursor.getCount()>0){
	        	cursor.moveToFirst();
	        	do{
	        		product_data.add(new Pair<String,String>(
	        				cursor.getString(0),
	        				cursor.getString(1)+"\n$"+cursor.getString(2)));
	        	}while(cursor.moveToNext());
	        }
	    }
	    public Object getItem(int position) {
	        return null;
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
	        Pair<String,String> p = product_data.get(position);
	        btn.setId(Integer.parseInt(p.first));
	        btn.setText(p.second);
	        btn.setOnClickListener(new ItemButton_Click(Integer.parseInt(p.first)));
	        return btn;
	    }
	    
		@Override
		public int getCount() {
			return product_data.size();
			//return 0;
		}
		
	    class ItemButton_Click implements OnClickListener {
	    	   private int id;

	    	   ItemButton_Click(int product_id) {
	    	       id = product_id;
	    	   }

	    	   @Override
	    	   public void onClick(View v) {
	    		   Log.v("Msg","section:"+String.valueOf(section_id)+",id:"+String.valueOf(id));
	    		   callback.onOrderButtonClicked(section_id-1,id-1); // Map db and ProductMap temporarily 
	    	   }
	    }
}
