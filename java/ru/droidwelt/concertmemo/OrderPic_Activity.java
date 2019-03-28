package ru.droidwelt.concertmemo;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

@SuppressLint("StaticFieldLeak")
public class OrderPic_Activity extends ListActivity implements LoaderCallbacks<Cursor> {

	
	private static long MAS_ID = 0;
	private static long DET_ID = 0;

	private EditText nameEditText;

	private static ListView detListView;
	private static Det_ImageCursorAdapter detAdapter;
	private static ArrayList<Long> ord;

	
	// ------------------------------------------------------------------------------------------
	public class Det_ImageCursorAdapter extends SimpleCursorAdapter {

		private Context context;

		@SuppressWarnings("deprecation")
		Det_ImageCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
			super(context, layout, c, from, to);
			this.context = context;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int pos, View inView, ViewGroup parent) {
			View v = super.getView(pos, inView, parent);
			if (v == null) {
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				assert inflater != null;
				v = inflater.inflate(R.layout.order_activity, null);
			}
			long id = ord.get(pos); // Edit_Activity.getDetAdapterd_ID(pos);
			ImageView iv = v.findViewById(R.id.order_item_pict);
			iv.setImageBitmap(WMA.getOneDetPreviewValue(id));//
			if (id == DET_ID) {
				v.setBackgroundColor(getResources().getColor(R.color.c_selected));
			} else {
				v.setBackgroundColor(getResources().getColor(R.color.c_main));
			}
			return (v);
		}
	}


	public static Det_ImageCursorAdapter getDetAdapter() {
		return detAdapter;
	}

	// -----------------------------------------------------------------------------------------------
	public static void setDetAdapter(Det_ImageCursorAdapter _detAdapter) {
		OrderPic_Activity.detAdapter = _detAdapter;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new PictCursorLoader(this);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
		getDetAdapter().swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
	}

	// -----------------------------------------------------------------------------------------------
	static class PictCursorLoader extends CursorLoader {
		PictCursorLoader(Context context) {
			super(context);
		}

		@Override
		public Cursor loadInBackground() {
			return getAllPict();
		}
	}

	public static Cursor getAllPict() {
		String sql = "SELECT _id,ord,comment from det where mid=" + MAS_ID + " order by ord";
		Cursor c = WMA.getDatabase().rawQuery(sql, null);
		c.moveToFirst();
		ord.clear();
		while (!c.isAfterLast()) {
			ord.add(c.getLong(0));
			c.moveToNext();
		}
		c.moveToFirst();
		//Log.i("XXX", "getAllPict");
		return c;
	}

	// ------------------------------------------------------------------------------------------------------------------------
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_activity);

		ActionBar bar = getActionBar();
		assert bar != null;
		bar.setHomeButtonEnabled(true);
		bar.setDisplayHomeAsUpEnabled(true);
		WMA.setHeaderFont(this);

		ord = new ArrayList<>();
		ord.clear();

		nameEditText = findViewById(R.id.order_name);
		ImageView iv1 = findViewById(R.id.order_iv_1);
		ImageView iv2 = findViewById(R.id.order_iv_2);
		ImageView iv3 = findViewById(R.id.order_iv_3);
		ImageView iv4 = findViewById(R.id.order_iv_4);
		iv1.setOnClickListener(oclBtnOk);
		iv2.setOnClickListener(oclBtnOk);
		iv3.setOnClickListener(oclBtnOk);
		iv4.setOnClickListener(oclBtnOk);

		MAS_ID = WMA.getMAS_ID();
		loadEditRecord();

		Context context = getApplicationContext();
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		assert imm != null;
		imm.hideSoftInputFromWindow(nameEditText.getWindowToken(), 0);

		String[] from = new String[] { "comment" }; // comment
		int[] to = new int[] { R.id.order_item_comment };

		setDetAdapter(new Det_ImageCursorAdapter(this, R.layout.order_item, null, from, to));
		detListView = this.getListView();
		detListView.setOnItemClickListener(detClickListener);
		setListAdapter(getDetAdapter());
		getLoaderManager().initLoader(0, null, this);
	}

	// слушатель нажатия на кнопку-----------------------------------------
	android.view.View.OnClickListener oclBtnOk = new android.view.View.OnClickListener() {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {

			case R.id.order_iv_1: // сделать первым
				Det_First();
				break;

			case R.id.order_iv_2: // в начало, раньше
				Det_MoveUp();
				break;

			case R.id.order_iv_3: // в конец, позже
				Det_MoveDown();
				break;

			case R.id.order_iv_4: // сделать последним
				Det_Last();
				break;

			default:
				break;
			}
		}
	};

	// слушатель клика в ListView
	OnItemClickListener detClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			DET_ID = id;
			detAdapter.notifyDataSetChanged();
			WMA.showToast(Long.toString(DET_ID));
		}
	};

	private void loadEditRecord() {
		String sql = "select _id,name from mas where _id=" + MAS_ID;
		Cursor c_mas = WMA.getDatabase().rawQuery(sql, null);
		c_mas.moveToFirst();
		int nameIndex = c_mas.getColumnIndex("name");
		nameEditText.setText(c_mas.getString(nameIndex));
		c_mas.close();
	}

	private void SaveOrd() {
		for (int i = 0; i < ord.size(); i = i + 1) {
			ContentValues editDetRecord = new ContentValues();
			editDetRecord.put("ord", i + 1);
			WMA.getDatabase().update("det", editDetRecord, "_id=" + ord.get(i), null);
			WMA.updateMidInMaster(MAS_ID, WMA.generateGUID(20));
		}
	}

	private int get_ord_pos() {
		int pos = 0;
		for (int i = 0; i < ord.size(); i = i + 1) {
			if (ord.get(i) == DET_ID)
				pos = i;
		}
		return pos;
	}

	private void Det_First() {
		int pos = get_ord_pos();
		if (pos > 0) {
			ArrayList<Long> ord2 = new ArrayList<>();
			ord2.add(DET_ID);
			for (int i = 0; i < ord.size(); i = i + 1) {
				if (ord.get(i) != DET_ID)
					ord2.add(ord.get(i));
			}
			ord.clear();
			ord.addAll(ord2);
			SaveOrd();
			getDetAdapter().swapCursor(getAllPict());
			detListView.smoothScrollToPosition(0);
			setResult(RESULT_OK);
		}
	}

	private void Det_MoveUp() {
		int pos = get_ord_pos();
		if (pos > 0) {
			long tmp1 = ord.get(pos);
			long tmp2 = ord.get(pos - 1);
			ord.set(pos, tmp2);
			ord.set(pos - 1, tmp1);
			SaveOrd();
			getDetAdapter().swapCursor(getAllPict());
			detListView.smoothScrollToPosition(pos - 1);
			setResult(RESULT_OK);
		}
	}

	private void Det_MoveDown() {
		int pos = get_ord_pos();
		if (pos < (ord.size() - 1)) {
			long tmp1 = ord.get(pos);
			long tmp2 = ord.get(pos + 1);
			ord.set(pos, tmp2);
			ord.set(pos + 1, tmp1);
			SaveOrd();
			getDetAdapter().swapCursor(getAllPict());
			detListView.smoothScrollToPosition(pos + 1);
			setResult(RESULT_OK);
		}
	}

	private void Det_Last() {
		int pos = get_ord_pos();
		if (pos < (ord.size() - 1)) {
			ArrayList<Long> ord2 = new ArrayList<>();
			for (int i = 0; i < ord.size(); i = i + 1) {
				if (ord.get(i) != DET_ID)
					ord2.add(ord.get(i));
			}
			ord2.add(DET_ID);
			ord.clear();
			ord.addAll(ord2);
			SaveOrd();
			getDetAdapter().swapCursor(getAllPict());
			detListView.smoothScrollToPosition(999);
			setResult(RESULT_OK);
		}
	}

	// защита от закрытия по Back------------------------------------
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Обработка нажатия, возврат true, если обработка выполнена
		switch (keyCode) {

		case KeyEvent.KEYCODE_BACK:
			finish();
			WMA.animateFinish(OrderPic_Activity.this);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// подключение меню ----------------------------------------
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.order_menu, menu);
		return true;
	}

	// меню -----------------------------------------------
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
			finish();
			WMA.animateFinish(OrderPic_Activity.this);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
