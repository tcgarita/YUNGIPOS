package com.bravebot.youngipos;

import android.support.v4.app.Fragment;
import android.util.Log;

public class FragmentMenu extends Fragment{
	protected OnClickOrderButtonListener callback;
    public interface OnClickOrderButtonListener {
        public void onButtonClicked(int sectionNo, int btnNo);
        public void onButtonClicked(int product_id);
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
   
	public void onOrderButtonClicked(int secNo, int id){
		Log.v("Msg","on FragmentMenu onOrderButtonClicked");
		callback.onButtonClicked(secNo, id);
	}	
}
