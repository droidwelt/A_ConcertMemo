package ru.droidwelt.concertmemo;


import android.os.Bundle;
import android.preference.PreferenceActivity;


public class Pref_Activity extends PreferenceActivity {


	public static void main(String[] args) {
		}
	
	@SuppressWarnings("deprecation")
	@Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    WMA.setHeaderFont (this);
	    addPreferencesFromResource(R.xml.pref);
	  }

		

}
