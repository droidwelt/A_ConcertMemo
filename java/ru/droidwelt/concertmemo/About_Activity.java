package ru.droidwelt.concertmemo;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

public class About_Activity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_activity);
		setTitle(getString(R.string.s_about));
		WMA.setHeaderFont (this);
		TextView versionTextView = findViewById(R.id.about_Version);
		TextView dbsizeTextView = findViewById(R.id.about_dbsize);
		TextView accountTextView = findViewById(R.id.about_account);
		String s = getString(R.string.s_about_account)+" "+WMA.getMyGoogleAccount();
		accountTextView.setText(s);

		PackageInfo pinfo;
		try {
			pinfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
			String sx = "Version " + pinfo.versionName + " build " + pinfo.versionCode;
			versionTextView.setText(sx);
		} catch (NameNotFoundException e) {
			versionTextView.setText("");
			e.printStackTrace();
		}

		try {
			DB_OpenHelper dbh = new DB_OpenHelper(WMA.getContext(), WMA.DB_NAME);
			int nrec = 0;
			String sql = "select COUNT(*) as NREC from  mas where tp=0";
			Cursor c = WMA.getDatabase().rawQuery(sql, null);
			if (c.getCount() > 0) {
				c.moveToFirst();
				nrec = c.getInt(c.getColumnIndex("NREC"));
			}
			c.close();

			String ss = getString(R.string.s_about_records) + " " + Integer.toString(nrec) + ";  "
					+ dbh.getDatabaseSize() + " Mb";
			dbsizeTextView.setText(ss);

		} catch (Exception e) {
			dbsizeTextView.setText("");
		}		
	}
}
