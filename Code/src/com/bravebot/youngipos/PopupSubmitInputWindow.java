package com.bravebot.youngipos;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import com.bravebot.youngipos.FragmentMenu.OnClickOrderButtonListener;
import com.bravebot.youngipos.RowAdapter.ItemButton_Click;

import android.app.Activity;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

public class PopupSubmitInputWindow extends PopupWindow {
	private TextView textViewNumberEdit;
	public View popView;
	private Button[] buttons;
	private Boolean first = true;
	private ImageButton cancelButton;
	protected OnClickSubmitButtonListener callback;
    public interface OnClickSubmitButtonListener {
        public void onSubmitEnterClicked(int count);
    }
	public void setCallback(Activity activity)
	{
		try {
			callback = (OnClickSubmitButtonListener) activity;
	    } catch (ClassCastException e) {
	    	throw new ClassCastException(activity.toString()
	    			+ " must implement OnClickSubmitButtonListener");
	    }
	}
	public PopupSubmitInputWindow(LayoutInflater inflater, Resources resources, int todayMoney, int payMoney)
	{
		popView = inflater.inflate(R.layout.popup_submit_input, null);
		textViewNumberEdit = (TextView)popView.findViewById(R.id.TextViewEdit);
		TextView moneyView0 = (TextView)popView.findViewById(R.id.TextViewMoney0);
		TextView moneyView1 = (TextView)popView.findViewById(R.id.TextViewMoney1);
		TextView moneyView2 = (TextView)popView.findViewById(R.id.TextViewMoney2);
		NumberFormat formatter = new DecimalFormat("###,###,###");
		moneyView0.setText("營業額：" + formatter.format(todayMoney));
		moneyView1.setText("支出：" + formatter.format(payMoney));
		moneyView2.setText("預估上繳金額：" + formatter.format(todayMoney - payMoney));
		
		buttons = new Button[10];
		for(int i = 0; i < 10; i++)
		{
			String buttonID = "NumberButton" + i;
			int resID = resources.getIdentifier(buttonID, "id", MainActivity.PACKAGE_NAME);
		    buttons[i] = ((Button) popView.findViewById(resID));
		    buttons[i].setOnClickListener(clickButton);
		    buttons[i].setTag(i);
		}
		Button backButton = ((Button) popView.findViewById(R.id.NumberButtonBack));
		backButton.setOnClickListener(clickButton);
		backButton.setTag(10);
		Button enterButton = ((Button) popView.findViewById(R.id.NumberButtonEnter));
		enterButton.setOnClickListener(clickButton);
		enterButton.setTag(11);
		cancelButton = (ImageButton) popView.findViewById(R.id.ButtonCancel);
		cancelButton.setOnClickListener(new ItemButton_Click());
		
		
	}
	private Button.OnClickListener clickButton = new Button.OnClickListener()
	{
		public void onClick(View v)
		{
			String string = textViewNumberEdit.getText().toString();
			int tag = Integer.parseInt(v.getTag().toString());
			if(string.length() < 8)
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
					callback.onSubmitEnterClicked(-1);
				else
					callback.onSubmitEnterClicked(Integer.parseInt(string));
			}
			
		}
	};
	class ItemButton_Click implements OnClickListener {


 	   @Override
 	   public void onClick(View v) {
 		  callback.onSubmitEnterClicked(-1);
 	   }
 }
	
}
