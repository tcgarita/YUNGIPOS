package com.bravebot.youngipos;

import android.support.v4.app.Fragment;
import android.util.Log;

public class FragmentMenu extends Fragment implements ButtonAdapter.OnClickButtonListener{
	protected OnClickOrderButtonListener callback;
    public interface OnClickOrderButtonListener {
        public void onButtonClicked(int sectionNo, int btnNo);
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
   
	public void onOrderButtonClicked(int a, int b){
		Log.v("Msg","on FragmentMenu onOrderButtonClicked");
		callback.onButtonClicked(a, b);
	}
	
}
