package com.bravebot.youngipos;

import java.util.List;

import com.bravebot.youngipos.RowAdapter3.OnClickDeleteButtonListener;
import com.bravebot.youngipos.RowAdapter3.RowHolder;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

class CategoryAdapter extends ArrayAdapter<Category> {
	private Context mContext;
	private int textViewResourceId;
	private int selected;
	public CategoryAdapter(Context context, int textViewResourceId,
			List<Category> objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.textViewResourceId = textViewResourceId;
	}

    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textview = null;
        
        if(convertView == null)
        {
            LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
            convertView = inflater.inflate(textViewResourceId, parent, false);            
            textview = (TextView)convertView.findViewById(R.id.textView1);
            convertView.setTag(textview);
        }
        else
        {
            textview = (TextView) convertView.getTag();
        }
        
        Category cat = this.getItem(position);
        textview.setText(cat.name);
        
        if( selected == position )
        	convertView.setBackgroundResource(R.color.lightBlue);
        else
        	convertView.setBackgroundResource(R.color.transparent);
        
        return convertView;
    }	
	
    public void setSelected(int pos){
    	selected = pos;
        this.notifyDataSetChanged();
    }
    
    public void addCategory(Category category){
    	this.add(category);
    }
    public void editCategory(Category category, String name){
    	category.name = name;
    	this.notifyDataSetChanged();
    }
}