package com.bravebot.youngipos;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

import com.bravebot.youngipos.FragmentMenu.OnClickOrderButtonListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class StatisticsCategoryRowAdapter extends ArrayAdapter<StatisticsCategoryRow>{
	Context context; 
    int layoutResourceId;    
    ArrayList<StatisticsCategoryRow> data = null;
    private int selectedItem;
    
 
	
    public StatisticsCategoryRowAdapter(Context context, int layoutResourceId, ArrayList<StatisticsCategoryRow> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RowHolder holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new RowHolder();
            holder.txtTitle0 = (TextView)row.findViewById(R.id.txtTitle0);
            holder.txtTitle1 = (TextView)row.findViewById(R.id.txtTitle1);
            row.setTag(holder);
        }
        else
        {
            holder = (RowHolder)row.getTag();
        }
        
        StatisticsCategoryRow listRow = data.get(position);
        holder.txtTitle0.setText(listRow.title0);
        holder.txtTitle1.setText(listRow.title1);
        
        if(layoutResourceId == R.layout.statistics_category_row)
        {
	        LinearLayout ActiveItem = (LinearLayout) row;
	        if (position == selectedItem)
	        {
	            ActiveItem.setBackgroundResource(R.color.lightBlue);
	
	            // for focus on it
	            int top = (ActiveItem == null) ? 0 : ActiveItem.getTop();
	        }
	        else
	        {
	            ActiveItem.setBackgroundResource(R.color.white);
	        }
        }
        
        return row;
    }
    
    static class RowHolder
    {
        TextView txtTitle0;
        TextView txtTitle1;
    }

    public void setSelectedItem(int position) {
        selectedItem = position;
        this.notifyDataSetChanged();
    }
   
}
