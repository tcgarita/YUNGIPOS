package com.bravebot.youngipos;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class FragmentMenu0 extends FragmentMenu {
		private View fragmentView;
		//private int buttonCount = 13;
	    public FragmentMenu0() {
		}
	    
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			//fragmentView = inflater.inflate(R.layout.fragment_menu_0, container, false);
			//fragmentView = inflater.createView();
			
			fragmentView = inflater.inflate(R.layout.fragment_gridview, container, false);
			this.initViews();
			
			return fragmentView;
		}

		private void initViews()
		{
	    	GridView gridview = (GridView) fragmentView.findViewById(R.id.gridview);
	    	gridview.setNumColumns(6);

			ButtonAdapter adapter = new ButtonAdapter(getActivity(),1);
			gridview.setAdapter(adapter);
			//this.callback.onButtonClicked(1, 2);
			adapter.setCallback(this.callback);
		}
		
	}
