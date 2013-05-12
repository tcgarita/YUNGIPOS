package com.bravebot.youngipos;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

public class FragmentMenu extends Fragment {
	private View fragmentView;	
	private GridView gridview;
	private ButtonAdapter adapter;
	private int category;
	
	protected OnClickOrderButtonListener callback;
    public interface OnClickOrderButtonListener {
        public void onButtonClicked(Product p, int product_id, View v);
    }
	public void setCallback(Fragment fragment)
	{
		try {
			callback = (OnClickOrderButtonListener) fragment;
	    } catch (ClassCastException e) {
	    	throw new ClassCastException(fragment.toString()
	    			+ " must implement OnClickOrderButtonListener");
	    }
	}
    public FragmentMenu(){
    	this.category = 1;
    }
    
    public void setCategory(int category){
    	this.category = category;
    }
    
	public ButtonAdapter getButtonAdapter(){
		return this.adapter;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		fragmentView = inflater.inflate(R.layout.fragment_gridview, container, false);
		this.initViews();
		
		return fragmentView;
	}

	private void initViews()
	{
    	gridview = (GridView) fragmentView.findViewById(R.id.gridview);
    	gridview.setNumColumns(6);

		adapter = new ButtonAdapter(getActivity(),this.category);
		
		gridview.setAdapter(adapter);
		adapter.setCallback(this.callback);
	}
	
	public void addProduct(Product p){
		adapter.addProduct(p);
	}
}
