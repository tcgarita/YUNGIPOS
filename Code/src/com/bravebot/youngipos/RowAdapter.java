package com.bravebot.youngipos;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import com.bravebot.youngipos.FragmentMenu.OnClickOrderButtonListener;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class RowAdapter extends ArrayAdapter<ListRow>{
	Context context; 
    int layoutResourceId;    
    ArrayList<ListRow> data = null;
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
    public RowAdapter(Context context, int layoutResourceId, ArrayList<ListRow> data) {
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
        ListRow listRow = data.get(position);
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
    		   callback.onDeleteButtonClicked(position);
    	       //int vid = v.getId();
    	       //if (vid == itemView.ItemButton.getId())
    	        //Log.v("ola_log",String.valueOf(position) );
    	   }
    }
}
