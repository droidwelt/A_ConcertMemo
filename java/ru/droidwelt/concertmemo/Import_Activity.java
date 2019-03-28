package ru.droidwelt.concertmemo;

import java.util.Date;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

@SuppressLint("StaticFieldLeak")
public class Import_Activity extends ListActivity implements LoaderCallbacks<Cursor> {

	private TextView nameTextView;
	private TextView eventTextView;
	private TextView clubTextView;
	private TextView datebegTextView;
	private TextView stateTextView;
	private TextView commentTextView;
	private TextView userTextView;
	private static Det_ImageCursorAdapter detAdapter;

	private boolean inProgress = false;
	private String dbNameImport = "";
	private static SQLiteDatabase databaseImport;

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
				v = inflater.inflate(R.layout.import_activity, null);
			}
			int id = Import_Activity.getDetAdapterd_ID(pos);
			ImageView iv = v.findViewById(R.id.import_item_pict);
			iv.setImageBitmap(getOneDetPreviewValueImport(id));//
			return (v);
		}
	}

	public Bitmap getOneDetPreviewValueImport(long ID) {
		Bitmap theImage;
		byte[] resall = null;

		String sql = " select preview from det where _ID='" + ID + "'";
		Cursor cursor = databaseImport.rawQuery(sql, null);
		cursor.moveToFirst();
		try {
			resall = cursor.getBlob(0);
			cursor.close();
		} catch (Exception ignored) {
		} finally {
			cursor.close();
		}

		try {
			assert resall != null;
			if (resall.length > 0) {
				theImage = BitmapFactory.decodeByteArray(resall, 0, resall.length); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			} else {
				theImage = BitmapFactory.decodeResource(getApplicationContext().getResources(),
						R.drawable.ic_empty_picture);
			}
		} catch (Exception e) {
			theImage = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ic_info);
		}

		return theImage;
	}

	public static int getDetAdapterd_ID(int position) {
		return (int) getDetAdapter().getItemId(position);
	}

	public static Det_ImageCursorAdapter getDetAdapter() {
		return detAdapter;
	}

	public static void setDetAdapter(Det_ImageCursorAdapter _detAdapter) {
		Import_Activity.detAdapter = _detAdapter;
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

	static class PictCursorLoader extends CursorLoader {
		PictCursorLoader(Context context) {
			super(context);
		}

		@Override
		public Cursor loadInBackground() {
			String sql = "SELECT _id,comment from det order by ord";
			return databaseImport.rawQuery(sql, null);
		}
	}

	// ------------------------------------------------------------------------------------------------------------------------
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.import_activity);
		WMA.setHeaderFont (this);

		nameTextView = findViewById(R.id.import_name);
		eventTextView = findViewById(R.id.import_Event);
		clubTextView = findViewById(R.id.import_Club);
		datebegTextView = findViewById(R.id.import_datebeg);
		stateTextView = findViewById(R.id.import_state);
		commentTextView = findViewById(R.id.import_comment);
		userTextView = findViewById(R.id.import_user);

		if (WMA.getFileImportMode()) {
			dbNameImport = WMA.getMyImportFileName();
			WMA.setFileImportMode(false);
		} else {			
			Uri uri = getIntent().getData();
			assert uri != null;
			dbNameImport = uri.getPath();
		}
		Log.i("dbNameImport", dbNameImport);
		databaseImport = SQLiteDatabase.openDatabase(dbNameImport, null, SQLiteDatabase.OPEN_READONLY);

		loadRecord();

		String[] from = new String[] { "comment" };
		int[] to = new int[] { R.id.import_item_comment };

		setDetAdapter(new Det_ImageCursorAdapter(this, R.layout.import_item, null, from, to));
		// detListView = this.getListView();
		setListAdapter(getDetAdapter());
		getLoaderManager().initLoader(0, null, this);
	}

	// ------------------------------------------------------------------------------------------------------------------------
	protected void loadRecord() {
		String sql = "select _id,name,genre,event,album,club,state,datebeg,dateend,mark,comment,massid,masmid,user from mas";
		Cursor cursor = databaseImport.rawQuery(sql, null);
		cursor.moveToFirst();
		int nameIndex = cursor.getColumnIndex("name");
		int genreIndex = cursor.getColumnIndex("genre");
		int eventIndex = cursor.getColumnIndex("event");
		int albumIndex = cursor.getColumnIndex("album");
		int clubIndex = cursor.getColumnIndex("club");
		int stateIndex = cursor.getColumnIndex("state");
		int datebegIndex = cursor.getColumnIndex("datebeg");
		int dateendIndex = cursor.getColumnIndex("dateend");
		int markIndex = cursor.getColumnIndex("mark");
		int commentIndex = cursor.getColumnIndex("comment");
		int userIndex = cursor.getColumnIndex("user");

		String __name = WMA.strnormalize(cursor.getString(nameIndex));
		String __genre = WMA.strnormalize(cursor.getString(genreIndex));
		String __event = WMA.strnormalize(cursor.getString(eventIndex));
		Date dt_datebeg = WMA.ConvertStrToDate(cursor.getString(datebegIndex));
		assert dt_datebeg != null;
		String __datebeg = WMA.ConvertDateToStr_Loc(dt_datebeg);
		WMA.ConvertStrToDate(cursor.getString(dateendIndex));
		String __album = WMA.strnormalize(cursor.getString(albumIndex));
		String __club = WMA.strnormalize(cursor.getString(clubIndex));
		String __state = WMA.strnormalize(cursor.getString(stateIndex));
		String __comment = WMA.strnormalize(cursor.getString(commentIndex));
		String __user = WMA.strnormalize(cursor.getString(userIndex));
		WMA.strnormalize(cursor.getString(datebegIndex));

		nameTextView.setText(__name);
		eventTextView.setText(__event);
		String s = "";
		s = WMA.AddString(s, __album);
		s = WMA.AddString(s, __club);
		s = WMA.AddString(s, __genre);
		clubTextView.setText(s);
		datebegTextView.setText(__datebeg);
		stateTextView.setText(__state);
		commentTextView.setText(__comment);
		userTextView.setText(__user);

		ImageView imgrating = findViewById(R.id.import_mark);
		WMA.DisplayMark_ImageView(cursor.getString(markIndex), imgrating);
		cursor.close();
	}

	// ------------------------------------------------------------------------------------------------------------------------
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.import_menu, menu);
		return true;
	}

	// ------------------------------------------------------------------------------------------------------------------------
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.import_menuItem_save:
			Intent intent = new Intent(Import_Activity.this, ImportInfo_Activity.class);
			startActivity(intent);			
			execImportEvent();
			finish();
			WMA.animateFinish(Import_Activity.this);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// ---------------------------------------------------------------------------------------------------------------------------
	public void execImportEvent() {
		inProgress = true;
		long new_mid, old_mid;
		String sid;

		String sql_mas = "select massid,masmid,lang, user, recmod, tp, name, genre, event, city, "
				+ "club, state, mark, album, addr, phone, email, http, geo,"
				+ "comment, nameup, searchup, datebeg,dateend,pict from mas";
		Cursor c_mas = databaseImport.rawQuery(sql_mas, null);
		c_mas.moveToFirst();
		if (c_mas.getCount() == 1) {
			sid = c_mas.getString(c_mas.getColumnIndex("massid"));
			old_mid = WMA.getMasIdBySid(sid);

			ContentValues recMaster = new ContentValues();
			recMaster.put("name", c_mas.getString(c_mas.getColumnIndex("name")));
			recMaster.put("massid", c_mas.getString(c_mas.getColumnIndex("massid")));
			recMaster.put("masmid", c_mas.getString(c_mas.getColumnIndex("masmid")));
			recMaster.put("lang", c_mas.getString(c_mas.getColumnIndex("lang")));
			recMaster.put("user", c_mas.getString(c_mas.getColumnIndex("user")));
			recMaster.put("recmod", c_mas.getString(c_mas.getColumnIndex("recmod")));
			recMaster.put("tp", c_mas.getString(c_mas.getColumnIndex("tp")));
			recMaster.put("genre", c_mas.getString(c_mas.getColumnIndex("genre")));
			recMaster.put("event", c_mas.getString(c_mas.getColumnIndex("event")));
			recMaster.put("city", c_mas.getString(c_mas.getColumnIndex("city")));
			recMaster.put("club", c_mas.getString(c_mas.getColumnIndex("club")));
			recMaster.put("state", c_mas.getString(c_mas.getColumnIndex("state")));
			recMaster.put("mark", c_mas.getString(c_mas.getColumnIndex("mark")));
			recMaster.put("album", c_mas.getString(c_mas.getColumnIndex("album")));
			recMaster.put("addr", c_mas.getString(c_mas.getColumnIndex("addr")));
			recMaster.put("phone", c_mas.getString(c_mas.getColumnIndex("phone")));
			recMaster.put("email", c_mas.getString(c_mas.getColumnIndex("email")));
			recMaster.put("http", c_mas.getString(c_mas.getColumnIndex("http")));
			recMaster.put("geo", c_mas.getString(c_mas.getColumnIndex("geo")));
			recMaster.put("comment", c_mas.getString(c_mas.getColumnIndex("comment")));
			recMaster.put("nameup", c_mas.getString(c_mas.getColumnIndex("nameup")));
			recMaster.put("searchup", c_mas.getString(c_mas.getColumnIndex("searchup")));
			recMaster.put("datebeg", c_mas.getString(c_mas.getColumnIndex("datebeg")));
			recMaster.put("dateend", c_mas.getString(c_mas.getColumnIndex("dateend")));
			recMaster.put("pict", c_mas.getBlob(c_mas.getColumnIndex("pict")));
			new_mid = WMA.getDatabase().insert("mas", null, recMaster);

			if (new_mid > 0) {
				String sql_det = "select mid,ord,detsid,detmid,massid,comment,preview,img from det order by ord";
				Cursor c_det = databaseImport.rawQuery(sql_det, null);
				c_det.moveToFirst();
				while (!c_det.isAfterLast()) {
					ContentValues recDetail = new ContentValues();
					recDetail.put("mid", new_mid);
					recDetail.put("ord", c_det.getInt(c_det.getColumnIndex("ord")));
					recDetail.put("detsid", c_det.getString(c_det.getColumnIndex("detsid")));
					recDetail.put("detmid", c_det.getString(c_det.getColumnIndex("detmid")));
					recDetail.put("massid", c_det.getString(c_det.getColumnIndex("massid")));
					recDetail.put("comment", c_det.getString(c_det.getColumnIndex("comment")));
					recDetail.put("preview", c_det.getBlob(c_det.getColumnIndex("preview")));
					recDetail.put("img", c_det.getBlob(c_det.getColumnIndex("img")));
					WMA.getDatabase().insert("det", null, recDetail);
					c_det.moveToNext();
				}
				c_det.close();

				WMA.setMAS_ID(new_mid);
				WMA.deleteRecordByMasDetByMid(old_mid);
				setResult(RESULT_OK);
			}
		}
		c_mas.close();
		databaseImport.close();

		if (WMA.getchoice_deleteevent()) {
			WMA.deleteFileByName(dbNameImport);
		}
		WMA.myWidgetAlarm (Import_Activity.this);
		
		inProgress = false;
		finish();
		WMA.animateFinish(Import_Activity.this);
	}

	@Override
	public void onBackPressed() {
		if (!inProgress) {
			Import_Activity.super.onBackPressed();
			WMA.animateFinish(Import_Activity.this);
		}

	}

}