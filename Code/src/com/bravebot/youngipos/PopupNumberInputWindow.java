package com.bravebot.youngipos;

import com.bravebot.youngipos.FragmentMenu.OnClickOrderButtonListener;
import com.bravebot.youngipos.RowAdapter.ItemButton_Click;

import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

public class PopupNumberInputWindow extends PopupWindow {
	private TextView textViewNumberEdit;
	public View popView;
	private Button[] buttons;
	private Button[] discountButtons;
	private Boolean first;
	private ImageButton cancelButton;
	private Button discountButton;
	private double discount = 1.0;
	private FrameLayout discountFrame;
	private String oldTitle;
	private TextView textViewHeader;
	private int pop;
	protected OnClickNumberButtonListener callback;
    public interface OnClickNumberButtonListener {
        public void onNumberEnterClicked(int count, double discount, int pop);
    }
	public void setCallback(Fragment fragment)
	{
		try {
			callback = (OnClickNumberButtonListener) fragment;
	    } catch (ClassCastException e) {
	    	throw new ClassCastException(fragment.toString()
	    			+ " must implement OnClickNumberButtonListener");
	    }
	}
	public PopupNumberInputWindow(LayoutInflater inflater, Resources resources, String title, int mode, int p)
	{
        pop = p;
		popView = inflater.inflate(R.layout.popup_number_input, null);
		textViewNumberEdit = (TextView)popView.findViewById(R.id.TextViewEdit);
		textViewHeader = (TextView) popView.findViewById(R.id.textView0);
		TextView textViewTitle = (TextView) popView.findViewById(R.id.textView1);
		TextView textViewTitle2 = (TextView) popView.findViewById(R.id.TextView01);
		View viewBanner = (View) popView.findViewById(R.id.view2);
		oldTitle = title;
		textViewHeader.setText(oldTitle);
		if(mode == 0)
		{
			first = true;
			viewBanner.setBackgroundColor(resources.getColor(R.color.blue));
		}
		else
		{
			first = false;
			textViewTitle.setText(R.string.price_input_title_1);
			//viewBanner.setBackgroundColor(resources.getColor(R.color.red));
			textViewNumberEdit.setText("");
			textViewTitle2.setText(R.string.price_input_title_2);
		}
			
		
		buttons = new Button[10];
		for(int i = 0; i < 10; i++)
		{
			String buttonID = "NumberButton" + i;
			int resID = resources.getIdentifier(buttonID, "id", MainActivity.PACKAGE_NAME);
		    buttons[i] = ((Button) popView.findViewById(resID));
		    buttons[i].setOnClickListener(clickButton);
		    buttons[i].setTag(i);
		}
		discountButtons = new Button[10];
		for(int i = 0; i < 3; i++)
		{
			String buttonID = "ButtonDiscount" + i;
			int resID = resources.getIdentifier(buttonID, "id", MainActivity.PACKAGE_NAME);
			discountButtons[i] = ((Button) popView.findViewById(resID));
			discountButtons[i].setOnClickListener(clickDiscountButtons);
			discountButtons[i].setTag(i);
		}
		Button backButton = ((Button) popView.findViewById(R.id.NumberButtonBack));
		backButton.setOnClickListener(clickButton);
		backButton.setTag(10);
		Button enterButton = ((Button) popView.findViewById(R.id.NumberButtonEnter));
		enterButton.setOnClickListener(clickButton);
		enterButton.setTag(11);
		cancelButton = (ImageButton) popView.findViewById(R.id.ButtonCancel);
		cancelButton.setOnClickListener(new ItemButton_Click());
		discountButton = (Button) popView.findViewById(R.id.ButtonDiscount);
		discountButton.setOnClickListener(clickDiscountButton);
		discountFrame = (FrameLayout) popView.findViewById(R.id.frameDiscount);
		discountFrame.setVisibility(View.GONE);
		if(mode != 0)
		{
			discountButton.setVisibility(View.GONE);
		}
		
		
		
	}
	private Button.OnClickListener clickDiscountButton = new Button.OnClickListener()
	{
		public void onClick(View v)
		{
			discountFrame.setVisibility(View.VISIBLE);
		}
	};
	private Button.OnClickListener clickDiscountButtons = new Button.OnClickListener()
	{
		public void onClick(View v)
		{
			int tag = Integer.parseInt(v.getTag().toString());
			if(tag == 0)
			{
				discount = 0.9;
				textViewHeader.setText(oldTitle + "(９折)");
				discountFrame.setVisibility(View.GONE);
			}
			if(tag == 1)
			{
				
				discount = 0.8;
				textViewHeader.setText(oldTitle + "(８折)");
				discountFrame.setVisibility(View.GONE);
			}
			if(tag == 2)
			{
				textViewHeader.setText(oldTitle + "(招待)");
				discount = 0;
				discountFrame.setVisibility(View.GONE);
			}
		}
	};
	private Button.OnClickListener clickButton = new Button.OnClickListener()
	{
		public void onClick(View v)
		{
			String string = textViewNumberEdit.getText().toString();
			int tag = Integer.parseInt(v.getTag().toString());
			if(string.length() < 6)
			{
				if(tag >= 0 && tag <= 9)
				{
					if(first)
					{
						first = false;
						textViewNumberEdit.setText(((Button)v).getTag().toString());
					}
					else
					{
						textViewNumberEdit.setText(string + ((Button)v).getTag().toString()); 
					}
				}
			}
			if(tag == 10)
			{
				
				if(string.length() > 0)
				{
					string = string.substring(0, string.length() - 1);
					textViewNumberEdit.setText(string);
				}
			}
			else if(tag == 11)
			{
				if(string.equalsIgnoreCase(""))
					callback.onNumberEnterClicked(0, discount, pop);
				else
					callback.onNumberEnterClicked(Integer.parseInt(string), discount, pop);
			}
			
		}
	};
	class ItemButton_Click implements OnClickListener {


 	   @Override
 	   public void onClick(View v) {
 		  callback.onNumberEnterClicked(0, discount, pop);
 	   }
 }
	
}
