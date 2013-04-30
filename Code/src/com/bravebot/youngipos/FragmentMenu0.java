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

public class FragmentMenu0 extends FragmentMenu{
		private View fragmentView;
		private int buttonCount = 13;
	    public FragmentMenu0() {
		}
	    public void create_button() {
	    	GridView gridview = (GridView) fragmentView.findViewById(R.id.gridview);
	    	gridview.setNumColumns(6);
			
			gridview.setOnItemClickListener(new GridView.OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					Log.v("Msg","haha test"+String.valueOf(position));
				}
			});
			gridview.setAdapter(new ButtonAdapter(getActivity(),1));
	    }
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			//fragmentView = inflater.inflate(R.layout.fragment_menu_0, container, false);
			fragmentView = inflater.inflate(R.layout.test, container, false);
			//fragmentView = inflater.createView();
			this.initViews();
			
			return fragmentView;
		}

		private void initViews()
		{
			create_button();
			/*
			buttons = new Button[buttonCount];
			for(int i = 0; i < buttonCount; i++)
			{
				String buttonID = "Button" + i;
				int resID = getResources().getIdentifier(buttonID, "id", MainActivity.PACKAGE_NAME);
			    buttons[i] = ((Button) fragmentView.findViewById(resID));
			    buttons[i].setOnClickListener(clickButton);
			    buttons[i].setTag(i);
			}*/
		}
		private Button.OnClickListener clickButton = new Button.OnClickListener()
		{
			public void onClick(View v)
			{				 
				Log.d("Button", v.getTag().toString());
				callback.onButtonClicked(0, Integer.parseInt(v.getTag().toString()));
			}
		};
		private OnItemClickListener clickButton2 = new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent,
					View view, int position, long id)
			{
				Log.v("Msg","Click:"+ String.valueOf(view.getId()));
				//Toast.makeText(getActivity(), "hello" + position, Toast.LENGTH_SHORT).show();
				//callback.onButtonClicked(0, Integer.parseInt(view.getTag().toString()));
			}
		};
	}
