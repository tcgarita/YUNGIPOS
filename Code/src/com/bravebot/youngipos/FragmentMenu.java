package com.bravebot.youngipos;

import android.support.v4.app.Fragment;

public class FragmentMenu extends Fragment{
	protected OnClickOrderButtonListener callback;
    public interface OnClickOrderButtonListener {
        public void onButtonClicked(int cat_id, int product_id);
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
}
