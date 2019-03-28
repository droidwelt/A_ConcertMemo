package ru.droidwelt.concertmemo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class Help_Activity extends Activity {

	class MySimpleAdapter extends SimpleAdapter {

		MySimpleAdapter(Context context,
						List<? extends Map<String, ?>> data, int resource,
						String[] from, int[] to) {
			super(context, data, resource, from, to); 
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help_activity);

		String[] helplist_qwe = getResources().getStringArray(R.array.help_list_qwe);
		String[] helplist_ans = getResources().getStringArray(R.array.help_list_ans);

		ActionBar bar = getActionBar();
		assert bar != null;
		bar.setHomeButtonEnabled(true);
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setTitle(getResources().getString(R.string.s_help));

		ArrayList<Map<String, Object>> data = new ArrayList<>(
				helplist_qwe.length);
		Map<String, Object> m;

		for (int i = 0; i < helplist_qwe.length; i++) {
			m = new HashMap<>();
			m.put("QWE", helplist_qwe[i].trim());
			m.put("ANS", helplist_ans[i].trim().substring(2));
			data.add(m);
		}

		String[] from = { "QWE", "ANS" };
		int[] to = { R.id.help_item1, R.id.help_item2 };

		MySimpleAdapter sAdapter = new MySimpleAdapter(this, data,
				R.layout.help_item, from, to);

		ListView lvMain = findViewById(R.id.listView1);
		lvMain.setAdapter(sAdapter);
	}

	

	// защита от закрытия по Back------------------------------------
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {

		case KeyEvent.KEYCODE_BACK:
			finish();
			WMA.animateFinish(Help_Activity.this);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// подключение меню ----------------------------------------
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.help_menu, menu);
		return true;
	}

	// меню -----------------------------------------------
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
			finish();
			WMA.animateFinish(Help_Activity.this);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
