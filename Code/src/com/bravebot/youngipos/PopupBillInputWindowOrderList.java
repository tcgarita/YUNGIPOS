package com.bravebot.youngipos;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import com.bravebot.youngipos.FragmentMenu.OnClickOrderButtonListener;
import com.bravebot.youngipos.RowAdapter.ItemButton_Click;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Color;
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

public class PopupBillInputWindowOrderList extends PopupWindow {
	private TextView textViewNumberEdit;
	public View popView;
	private Button[] buttons;
	private Boolean first = true;
	private ImageButton cancelButton;
	protected OnClickBillButtonListener callback;
	private int billMoney;
    public interface OnClickBillButtonListener {
        public void onSubmitEnterClicked(int count);
    }
	public void setCallback(Activity activity)
	{
		try {
			callback = (OnClickBillButtonListener) activity;
	    } catch (ClassCastException e) {
	    	throw new ClassCastException(activity.toString()
	    			+ " must implement OnClickSubmitButtonListener");
	    }
	}
	public PopupBillInputWindowOrderList(LayoutInflater inflater, Resources resources, int money)
	{
		billMoney = money;
		popView = inflater.inflate(R.layout.popup_bill_input, null);
		textViewNumberEdit = (TextView)popView.findViewById(R.id.TextViewEdit);
		TextView moneyView0 = (TextView)popView.findViewById(R.id.TextViewMoney0);
		
		NumberFormat formatter = new DecimalFormat("###,###,###");
		moneyView0.setText("帳單金額:" + formatter.format(billMoney));
		textViewNumberEdit.setText(String.valueOf(billMoney));
		
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
				{
					Builder dialog2 = new AlertDialog.Builder(v.getContext());
		    		dialog2.setTitle("請輸入實收金額");
		    		dialog2.setMessage("請重新輸入實收金額！");
				    
		    		dialog2.setNeutralButton("確定", confirmClick);
				    AlertDialog dialogView = dialog2.show();
				    TextView textView = (TextView) dialogView.findViewById(android.R.id.message);
				    textView.setTextSize(26);
				    Button btn1 = dialogView.getButton(DialogInterface.BUTTON_NEUTRAL);
				    btn1.setTextSize(24);
				    btn1.setTextColor(Color.BLUE);
					return;
				}
				if(billMoney > Integer.parseInt(textViewNumberEdit.getText().toString()))
				{
					Builder dialog2 = new AlertDialog.Builder(v.getContext());
		    		dialog2.setTitle("實收金額小於帳單金額");
		    		dialog2.setMessage("請重新輸入實收金額！");
				    
		    		dialog2.setNeutralButton("確定", confirmClick);
				    AlertDialog dialogView = dialog2.show();
				    TextView textView = (TextView) dialogView.findViewById(android.R.id.message);
				    textView.setTextSize(26);
				    Button btn1 = dialogView.getButton(DialogInterface.BUTTON_NEUTRAL);
				    btn1.setTextSize(24);
				    btn1.setTextColor(Color.BLUE);
				    textViewNumberEdit.setText("");
					return;
				}
				
				callback.onSubmitEnterClicked(Integer.parseInt(string));
			}
			
		}
	};
	private DialogInterface.OnClickListener confirmClick = new DialogInterface.OnClickListener()
	{

		@Override
		public void onClick(DialogInterface dialog, int which) {
			
		}
		
	};
	class ItemButton_Click implements OnClickListener {


 	   @Override
 	   public void onClick(View v) {
 		  callback.onSubmitEnterClicked(-1);
 	   }
 }
	
}
