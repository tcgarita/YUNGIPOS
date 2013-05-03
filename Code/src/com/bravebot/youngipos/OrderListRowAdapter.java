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
import android.util.Log;
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

public class OrderListRowAdapter extends ArrayAdapter<OrderListRow>{
	Context context; 
    int layoutResourceId;    
    ArrayList<OrderListRow> data = null;
    private int selectedItem;
 
	
    public OrderListRowAdapter(Context context, int layoutResourceId, ArrayList<OrderListRow> data) {
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
            holder.txtTitle2 = (TextView)row.findViewById(R.id.txtTitle2);
            holder.txtTitle3 = (TextView)row.findViewById(R.id.txtTitle3);
            holder.txtTitle4 = (TextView)row.findViewById(R.id.txtTitle4);
            holder.txtTitle5 = (TextView)row.findViewById(R.id.txtTitle5);
            row.setTag(holder);
        }
        else
        {
            holder = (RowHolder)row.getTag();
        }
        
        OrderListRow listRow = data.get(position);
        holder.txtTitle0.setText(listRow.title0);
        holder.txtTitle1.setText(listRow.title1);
        holder.txtTitle2.setText(listRow.title2);
        NumberFormat formatter = new DecimalFormat("###,###,###");
        holder.txtTitle3.setText(formatter.format(Integer.parseInt(listRow.title3)));
        if(listRow.title4.equalsIgnoreCase("0"))
        {
        	holder.txtTitle4.setText("門市現金");
        }
        else if(listRow.title4.equalsIgnoreCase("1"))
        {
        	holder.txtTitle4.setText("預約自取");
        }
        else if(listRow.title4.equalsIgnoreCase("2"))
        {
        	holder.txtTitle4.setText("外送");
        }
        else if(listRow.title4.equalsIgnoreCase("3"))
        {
        	holder.txtTitle4.setText("月結");
        }
        
        if(listRow.title5.equalsIgnoreCase("0"))
        {
        	holder.txtTitle5.setText("未結帳");
        	holder.txtTitle5.setTextColor(Color.RED);
        }
        else if(listRow.title5.equalsIgnoreCase("1"))
        {
        	holder.txtTitle5.setText("已結帳");
        	holder.txtTitle5.setTextColor(Color.BLUE);
        }
        else if(listRow.title5.equalsIgnoreCase("2"))
        {
        	holder.txtTitle5.setText("月結");
        	holder.txtTitle5.setTextColor(Color.BLUE);
        }
        
        //holder.txtTitle6.setText(listRow.title6);
        //holder.txtTitle7.setText(listRow.title3);
        
        LinearLayout ActiveItem = (LinearLayout) row;
        if (position == selectedItem)
        {
            ActiveItem.setBackgroundResource(R.color.lightBlue);

            // for focus on it
            int top = (ActiveItem == null) ? 0 : ActiveItem.getTop();
           // ((ListView) parent).setSelectionFromTop(position, top);
        }
        else
        {
            ActiveItem.setBackgroundResource(R.color.white);
        }
        
        return row;
    }
    
    static class RowHolder
    {
        TextView txtTitle0;
        TextView txtTitle1;
        TextView txtTitle2;
        TextView txtTitle3;
        TextView txtTitle4;
        TextView txtTitle5;
    }

    public void setSelectedItem(int position) {
        selectedItem = position;
        this.notifyDataSetChanged();
    }
   
}
