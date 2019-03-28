package ru.droidwelt.concertmemo;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

public class InfoClub_Activity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info_club);	
		WMA.setHeaderFont (this);
		setTitle(getString(R.string.s_fld_club));

		TextView tv_addr = findViewById(R.id.infoclub_addr);
		TextView tv_phone = findViewById(R.id.infoclub_phone);
		TextView tv_email = findViewById(R.id.infoclub_email);
		TextView tv_http = findViewById(R.id.infoclub_http);

		String sql = "select  club,addr,phone,email,http from  mas where _id=" + WMA.getMAS_ID();
		Cursor clubcursor = WMA.getDatabase().rawQuery(sql, null);
		if (clubcursor.getCount() > 0) {
			clubcursor.moveToFirst();
			setTitle(clubcursor.getString(clubcursor.getColumnIndex("club")));
			tv_addr.setText(clubcursor.getString(clubcursor.getColumnIndex("addr")));
			tv_phone.setText(clubcursor.getString(clubcursor.getColumnIndex("phone")));
			tv_email.setText(clubcursor.getString(clubcursor.getColumnIndex("email")));
			tv_http.setText(clubcursor.getString(clubcursor.getColumnIndex("http")));
		}
		clubcursor.close();
	}
}
