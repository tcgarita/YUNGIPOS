package com.bravebot.youngipos;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class RowAdapter extends BaseAdapter{
	private Context mContext;
	ArrayList<SoldProduct> sold_array;
	int layout;
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
	public RowAdapter (Context context, int layoutResource){
		this.mContext = context;
		this.layout = layoutResource;
		this.sold_array = new ArrayList<SoldProduct>();
	}
	@Override
	public int getCount() {
		return sold_array.size();
	}

	@Override
	public SoldProduct getItem(int position) {
		return sold_array.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
	        RowHolder holder = null;
	        if(row == null)
	        {
	            LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
	            row = inflater.inflate(this.layout, parent, false);
	            
	            holder = new RowHolder();
	            holder.name = (TextView)row.findViewById(R.id.txtTitle0);
	            holder.price = (TextView)row.findViewById(R.id.txtTitle1);
	            holder.count = (TextView)row.findViewById(R.id.txtTitle2);
	            holder.total = (TextView)row.findViewById(R.id.txtTitle3);
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
	        
	        SoldProduct sold_product = sold_array.get(position);
	        holder.name.setText(sold_product.getSoldName());
	        holder.price.setText(formatter.format(sold_product.getFinalSoldPrice()));
	        holder.count.setText(sold_product.getCountString());
	        holder.total.setText(formatter.format(sold_product.getTotalPrice()));
	        
	        holder.deleteButton.setOnClickListener(new ItemButton_Click(position));
	        return row;
	}
    
    static class RowHolder
    {
        TextView name;
        TextView price;
        TextView count;
        TextView total;
        ImageButton deleteButton;
    }
    
    class ItemButton_Click implements OnClickListener {
    	   private int position;

    	   ItemButton_Click(int pos) {
    	       position = pos;
    	   }

    	   @Override
    	   public void onClick(View v) {
    		   callback.onDeleteButtonClicked(position);
    	   }
    }
    
    public void AddRow(SoldProduct row){
    	sold_array.add(row);
    	this.notifyDataSetChanged();
    }
    
    public void delRow(int index){
    	if(sold_array.size()>index){
    		sold_array.remove(index);
    		this.notifyDataSetChanged();
    	}
    }
    
    public void clearRow(){
    	sold_array.clear();
    	this.notifyDataSetChanged();
    }
    
    public int getItemPrice(int index){ 	
    	SoldProduct p = sold_array.get(index);
    	return p.getFinalSoldPrice();
    }
    
    public ArrayList<SoldProduct> getData(){
    	return this.sold_array;
    }
    
    public void setData(ArrayList<SoldProduct> lists){
    	this.sold_array.clear();
    	this.sold_array = lists;
    	this.notifyDataSetChanged();
    }
    
    public int getTotalAmount(){
    	int res = 0;
    	for(SoldProduct list: sold_array){
    		res += list.getTotalPrice();
    	}
    	Log.v("Msg","Total in Adapter"+res);
    	return res;
    }

    public void setRow(int index, SoldProduct new_row){
    	sold_array.remove(index);
    	sold_array.add(new_row);
    	this.notifyDataSetChanged();
    }
    
}
