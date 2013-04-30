package com.bravebot.youngipos;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class FragmentMenu1 extends FragmentMenu{
		private View fragmentView;
		private int buttonCount = 16;
		private Button[] buttons;
		public FragmentMenu1() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			fragmentView = inflater.inflate(R.layout.fragment_menu_1, container, false);
			this.initViews();
			       
			return fragmentView;
		}
		
		private void initViews()
		{
			buttons = new Button[buttonCount];
			for(int i = 0; i < buttonCount; i++)
			{
				String buttonID = "Button" + i;
				int resID = getResources().getIdentifier(buttonID, "id", MainActivity.PACKAGE_NAME);
			    buttons[i] = ((Button) fragmentView.findViewById(resID));
			    buttons[i].setOnClickListener(clickButton);
			    buttons[i].setTag(i);
			}
		}
		private Button.OnClickListener clickButton = new Button.OnClickListener()
		{
			public void onClick(View v)
			{
				Log.d("Button", v.getTag().toString());
				callback.onButtonClicked(1, Integer.parseInt(v.getTag().toString()));
			}
		};
	}
