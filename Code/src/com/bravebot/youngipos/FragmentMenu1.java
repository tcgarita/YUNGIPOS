package com.bravebot.youngipos;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;


public class FragmentMenu1 extends FragmentMenu{
		private View fragmentView;
//		private int buttonCount = 16;
//		private Button[] buttons;
		public FragmentMenu1() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			//fragmentView = inflater.inflate(R.layout.fragment_menu_1, container, false);
			fragmentView = inflater.inflate(R.layout.fragment_gridview, container, false);
			this.initViews();
			       
			return fragmentView;
		}
		
		private void initViews()
		{
	    	GridView gridview = (GridView) fragmentView.findViewById(R.id.gridview);
	    	gridview.setNumColumns(6);

			ButtonAdapter adapter = new ButtonAdapter(getActivity(),2);
			gridview.setAdapter(adapter);
			adapter.setCallback(this.callback);
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
		/*private Button.OnClickListener clickButton = new Button.OnClickListener()
		{
			public void onClick(View v)
			{
				Log.d("Button", v.getTag().toString());
				callback.onButtonClicked(1, Integer.parseInt(v.getTag().toString()));
			}
		};*/
	}
