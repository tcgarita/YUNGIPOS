package com.bravebot.youngipos;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import com.bravebot.youngipos.FragmentMenu.OnClickOrderButtonListener;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class RowAdapter3 extends ArrayAdapter<ListRow>{
	Context context; 
    int layoutResourceId;
    public boolean showDeleteButton = true;
    protected OnClickDeleteButtonListener callback;
    public interface OnClickDeleteButtonListener {
        public void onDeleteButtonClicked(int position);
    }
	public void setCallback(Fragment fragment)
	{
		try {
			callback = (OnClickDeleteButtonListener) fragment;
	    } catch (ClassCastException e) {
	    	throw new ClassCastException(fragment.toString()
	    			+ " must implement OnClickDeleteButtonListener");
	    }
	}
    //public RowAdapter(Context context, int layoutResourceId, ArrayList<ListRow> data) {
	public RowAdapter3(Context context, int layoutResourceId) {
        super(context, layoutResourceId);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
//        this.data = new ArrayList<ListRow>();
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
            holder.deleteButton = (ImageButton)row.findViewById(R.id.ButtonDelete);
            row.setTag(holder);
        }
        else
        {
            holder = (RowHolder)row.getTag();
        }
        if(showDeleteButton == false)
        	holder.deleteButton.setVisibility(View.GONE);
        NumberFormat formatter = new DecimalFormat("###,###,###");
//        ListRow listRow = data.get(position);
        ListRow listRow = this.getItem(position);
        holder.txtTitle0.setText(listRow.title0);
        holder.txtTitle1.setText(formatter.format(Integer.parseInt(listRow.title1)));
        holder.txtTitle2.setText(listRow.title2);
        holder.txtTitle3.setText(formatter.format(Integer.parseInt(listRow.title3)));
        holder.deleteButton.setOnClickListener(new ItemButton_Click(position));
        
        return row;
    }
    
    static class RowHolder
    {
        TextView txtTitle0;
        TextView txtTitle1;
        TextView txtTitle2;
        TextView txtTitle3;
        ImageButton deleteButton;
    }
    
    class ItemButton_Click implements OnClickListener {
    	   private int position;

    	   ItemButton_Click(int pos) {
    	       position = pos;
    	   }

    	   @Override
    	   public void onClick(View v) {
    		   Log.v("Msg","tcgarita RowAdapter ItemButton_Click");
    		   callback.onDeleteButtonClicked(position);
    	       //int vid = v.getId();
    	       //if (vid == itemView.ItemButton.getId())
    	        //Log.v("ola_log",String.valueOf(position) );
    	   }
    }
    
    public void AddRow(ListRow row){
    	Log.v("Msg","AddRow size:"+this.getCount());
    	this.add(row);
    	Log.v("Msg","AddRow size:"+this.getCount());
    }
    
    public void delRow(int index){
    	Log.v("Msg","delRow");
    	ListRow del_row = this.getItem(index);
    	this.remove(del_row);
    }
    
    public void clearRow(){
    	this.clear();
    	this.notifyDataSetChanged();
    }
    
    public void setRow(int index, ListRow new_row){
    	ListRow old_row = this.getItem(index);
    	this.remove(old_row);
    	this.add(new_row);
    	this.notifyDataSetChanged();
    }
    
    public int getItemPrice(int index){
    	ListRow data = this.getItem(index);
    	return Integer.parseInt(data.title3);
    }
}
