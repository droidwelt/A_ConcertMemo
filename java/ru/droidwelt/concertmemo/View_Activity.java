package ru.droidwelt.concertmemo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

@SuppressLint("StaticFieldLeak")
public class View_Activity extends ListActivity implements LoaderCallbacks<Cursor> {

	private static long MAS_ID;
	private TextView nameTextView;
	private TextView eventTextView;
	private TextView clubTextView;
	private TextView datebegTextView;
	private TextView stateTextView;
	private TextView commentTextView;
	private TextView userTextView;

	private ImageButton btn_info_club;
	private static final int MAS_EDIT = 301;
	private static final int DET_VIEW = 302;
	private String __name = "", __event = "", __genre = "", __user = "";
	private String __datebeg = "";
	private String __album = "", __state = "", __club = "", __comment = "";
	private Date dt_datebeg, dt_dateend;
	private String SendDBFileName = "";

	private static Det_ImageCursorAdapter detAdapter;

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
				v = inflater.inflate(R.layout.view_activity, null);
			}
			int id = View_Activity.getDetAdapterd_ID(pos);
			ImageView iv = v.findViewById(R.id.view_item_pict);
			iv.setImageBitmap(WMA.getOneDetPreviewValue(id));//
			return (v);
		}
	}

	public static int getDetAdapterd_ID(int position) {
		return (int) getDetAdapter().getItemId(position);
	}

	public static Det_ImageCursorAdapter getDetAdapter() {
		return detAdapter;
	}

	public static void setDetAdapter(Det_ImageCursorAdapter _detAdapter) {
		View_Activity.detAdapter = _detAdapter;
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
			String sql = "SELECT _id,comment from det where  mid=" + MAS_ID + " order by ord";
			return WMA.getDatabase().rawQuery(sql, null);
		}
	}

	// ------------------------------------------------------------------------------------------------------------------------
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_activity);

		ActionBar bar = getActionBar();
		assert bar != null;
		bar.setHomeButtonEnabled(true);
		bar.setDisplayHomeAsUpEnabled(true);
		WMA.setHeaderFont(this);

		nameTextView = findViewById(R.id.view_name);
		eventTextView = findViewById(R.id.view_Event);
		clubTextView = findViewById(R.id.view_Club);
		datebegTextView = findViewById(R.id.view_datebeg);
		stateTextView = findViewById(R.id.view_state);
		commentTextView = findViewById(R.id.view_comment);
		userTextView = findViewById(R.id.view_user);
		btn_info_club = findViewById(R.id.info_club);
		btn_info_club.setOnClickListener(oclBtnOk);

		MAS_ID = WMA.getMAS_ID();
		loadRecord();

		String[] from = new String[] { "comment" };
		int[] to = new int[] { R.id.view_item_comment };

		setDetAdapter(new Det_ImageCursorAdapter(this, R.layout.view_item, null, from, to));
		ListView detListView = this.getListView();
		detListView.setOnItemClickListener(detListener);
		setListAdapter(getDetAdapter());

		/*
		 * LayoutAnimationController controller = AnimationUtils
		 * .loadLayoutAnimation(this, R.anim.list_layout_controller);
		 * getListView().setLayoutAnimation(controller);
		 */

		getLoaderManager().initLoader(0, null, this);
	}

	// ------------------------------------------------------------------------------------------
	// слушатель событий в ListView
	OnItemClickListener detListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			Intent viewPicture = new Intent(View_Activity.this, ViewPict_Activity.class);
			WMA.setDET_ID(id);
			startActivityForResult(viewPicture, DET_VIEW);
			WMA.animateStart(View_Activity.this);
		}
	};

	// ------------------------------------------------------------------------------------------------------------------------
	protected void loadRecord() {
		String sql = String.format(
				"SELECT _id,name,genre,event,album,club,state,datebeg,dateend,mark,comment,massid,masmid,user from mas  "
						+ " where _id=%1$s ", MAS_ID);
		Cursor cursor = WMA.getDatabase().rawQuery(sql, null);
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

		// заполнение компонентов TextViews выбранными данными
		__name = WMA.strnormalize(cursor.getString(nameIndex));
		__genre = WMA.strnormalize(cursor.getString(genreIndex));
		__event = WMA.strnormalize(cursor.getString(eventIndex));
		dt_datebeg = WMA.ConvertStrToDate(cursor.getString(datebegIndex));
		assert dt_datebeg != null;
		__datebeg = WMA.ConvertDateToStr_Loc(dt_datebeg);
		dt_dateend = WMA.ConvertStrToDate(cursor.getString(dateendIndex));
		__album = WMA.strnormalize(cursor.getString(albumIndex));
		__club = WMA.strnormalize(cursor.getString(clubIndex));
		__state = WMA.strnormalize(cursor.getString(stateIndex));
		__comment = WMA.strnormalize(cursor.getString(commentIndex));
		__user = WMA.strnormalize(cursor.getString(userIndex));
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

		ImageView imgrating = findViewById(R.id.view_mark);
		WMA.DisplayMark_ImageView(cursor.getString(markIndex), imgrating);
		cursor.close();

		if (__club.equals("")) {
			btn_info_club.setVisibility(ImageView.INVISIBLE);
		} else {
			btn_info_club.setVisibility(ImageView.VISIBLE);
		}
	}

	// ------------------------------------------------------------------------------------------------------------------------
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.view_menu, menu);
		return true;
	}

	private String prepareTextToSend() {
		String res = "";
		if (!__name.equals(""))
			res = res + __name + ";";
		if (!__event.equals(""))
			res = res + __event + ";";
		if (!__datebeg.equals(""))
			res = res + __datebeg + ";";
		if (!__album.equals(""))
			res = res + __album + ";";
		if (!__club.equals(""))
			res = res + __club + ";";
		if (!__genre.equals(""))
			res = res + __genre + ";";
		if (!__state.equals(""))
			res = res + __state + ";";
		if (!__user.equals(""))
			res = res + __user + ";";
		if (!__comment.equals(""))
			res = res + __comment + ";";
		res = res + getString(R.string.s_prepare_to_share);
		return res;
	}

	// ------------------------------------------------------------------------------------------------------------------------
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.view_menuItem_edit:
			Intent addEditMas = new Intent(this, Edit_Activity.class);
			WMA.setMAS_ID(MAS_ID);
			WMA.setMastChangeUser(!__user.equals(WMA.getMyGoogleAccount()));
			startActivityForResult(addEditMas, MAS_EDIT);
			WMA.animateStart(View_Activity.this);
			return true;

		case R.id.view_menuItem_calend:
			addToCalendar();
			return true;

		case R.id.view_menuItem_delete:
			deleteOneMasRecor();
			return true;

		case R.id.view_menuItem_share:
			shareDetRecord(__name, prepareTextToSend());
			return true;

		case R.id.view_menuItem_export:
			exportDetRecord(__name, prepareTextToSend());
			return true;

		case android.R.id.home:
		/*	if (_createIcon) {
				setResult(RESULT_OK);
			} */
			setResult(RESULT_OK);
			WMA.deleteTempFile(WMA.sendFilename);
			finish();
			WMA.animateFinish(View_Activity.this);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// ------------------------------------------------------------------------------------------------------------------------
	public void shareDetRecord(String __name, String __sendString) {
		WMA.deleteTempFile(WMA.sendFilename);
		long _det_id = WMA.getFirstDetByMas();
		Bitmap imageBig1 = null;
		if (_det_id > 0) {
			imageBig1 = WMA.getOneDetImageValue(_det_id);
		}
		if ((_det_id > 0) & (imageBig1 != null)) {
			try {
				FileOutputStream fos = new FileOutputStream(WMA.DB_PATH + WMA.sendFilename);
				imageBig1.compress(CompressFormat.JPEG, 75, fos);

				fos.flush();
				fos.close();
			} catch (Exception ignored) {
			}
		}

		File file = new File(WMA.DB_PATH, WMA.sendFilename);
		Uri imageUri = Uri.fromFile(file);

		Intent i = new Intent(Intent.ACTION_SEND);
		if ((_det_id > 0) & (imageUri != null)) {
			i.setType("image/jpg");
			i.putExtra(Intent.EXTRA_STREAM, imageUri);
		} else {
			i.setType("plain/text");
		}

		i.putExtra(Intent.EXTRA_TEXT, __sendString);
		i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.s_event_report_subject) + " " + __name);

		i = Intent.createChooser(i, getString(R.string.s_send_report));
		startActivity(i);
		WMA.animateStart(View_Activity.this);
	}

	// удаление
	// контакта-------------------------------------------------------------------------------------
	private void deleteOneMasRecor() {
		final Timer timer = new Timer();
		AlertDialog.Builder builder = new AlertDialog.Builder(View_Activity.this);
		builder.setTitle(R.string.s_confirm_request);
		builder.setMessage(R.string.s_delete_record);

		builder.setNegativeButton(R.string.s_no, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				timer.purge();
				timer.cancel();
			}
		});

		builder.setPositiveButton(R.string.s_yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int button) {
				timer.purge();
				timer.cancel();
				setResult(RESULT_OK);

				AsyncTask<Long, Object, Object> deleteTask = new AsyncTask<Long, Object, Object>() {
					@Override
					protected Object doInBackground(Long... params) {
						WMA.getDatabase().delete("det", "mid=" + params[0], null);
						WMA.getDatabase().delete("mas", "_id=" + params[0], null);
						return null;
					}

					@Override
					protected void onPostExecute(Object result) {
						WMA.deleteTempFile(WMA.sendFilename);
						setResult(RESULT_OK);
						finish();						
						WMA.animateFinish(View_Activity.this);
					}
				};

				deleteTask.execute(MAS_ID);
			}
		});

		final AlertDialog dlg = builder.create();
		dlg.show();

		timer.schedule(new TimerTask() {
			public void run() {
				dlg.dismiss();
				timer.purge();
				timer.cancel();
			}
		}, 5000);
	}

	// ------------------------------------------------------------------------------------------------------------------------
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

		switch (requestCode) {

		case MAS_EDIT:
			setResult(RESULT_OK);
			WMA.deleteTempFile(WMA.sendFilename);
			finish();
			WMA.animateFinish(View_Activity.this);
			break;

		case DET_VIEW:
			if (resultCode == RESULT_OK) {
			}
			break;
		}

	}

	// слушатель нажатия на кнопку-----------------------------------------
	android.view.View.OnClickListener oclBtnOk = new android.view.View.OnClickListener() {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {

			case R.id.info_club:
				Intent viewInfo = new Intent(View_Activity.this, InfoClub_Activity.class);
				WMA.setMAS_ID(MAS_ID);
				startActivity(viewInfo);
				break;

			default:
				break;
			}
		}
	};

	// защита от закрытия по Back------------------------------------
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Обработка нажатия, возврат true, если обработка выполнена
		switch (keyCode) {

		case KeyEvent.KEYCODE_BACK:
			/*if (_createIcon) {
				setResult(RESULT_OK);
			}*/
			setResult(RESULT_OK);
			finish();
			WMA.animateFinish(View_Activity.this);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// -----------------------------------------------------------------------------
	@SuppressWarnings("deprecation")
	public void addToCalendar() {
		Intent calIntent = new Intent(Intent.ACTION_INSERT);
		calIntent.setType("vnd.android.cursor.item/event");
		calIntent.putExtra(Events.TITLE, __name);
		calIntent.putExtra(Events.EVENT_LOCATION, __club);
		calIntent.putExtra(Events.DESCRIPTION, __event);

		// отсчет месяцев ведется с 0!
		int Y = dt_datebeg.getYear() + 1900;
		int M = dt_datebeg.getMonth();
		int D = dt_datebeg.getDate();
		int H = dt_datebeg.getHours();
		int N = dt_datebeg.getMinutes();
		GregorianCalendar calDate1 = new GregorianCalendar(Y, M, D);
		calDate1.set(Y, M, D, H, N);

		if (dt_dateend.before(dt_datebeg)) {
			dt_dateend = dt_datebeg;
		}
		Y = dt_dateend.getYear() + 1900;
		M = dt_dateend.getMonth();
		D = dt_dateend.getDate();
		H = dt_dateend.getHours();
		N = dt_dateend.getMinutes();
		GregorianCalendar calDate2 = new GregorianCalendar(Y, M, D);
		calDate2.set(Y, M, D, H, N);

		long startMillis = calDate1.getTimeInMillis();
		long endMillis = calDate2.getTimeInMillis();

		calIntent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false);
		calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis);
		calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis);

		// calIntent.putExtra(Events.RRULE,
		// "FREQ=WEEKLY;COUNT=10;WKST=SU;BYDAY=TU,TH");

		startActivity(calIntent);
		WMA.animateStart(View_Activity.this);
	}

	public void copyExportDataBase() {
		try {
			InputStream externalDbStream = this.getAssets().open(WMA.DB_NAMEEXPORT);
			String outFileName = WMA.DB_PATH + SendDBFileName;
			OutputStream localDbStream = new FileOutputStream(outFileName);

			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = externalDbStream.read(buffer)) > 0) {
				localDbStream.write(buffer, 0, bytesRead);
			}
			localDbStream.close();
			externalDbStream.close();
		} catch (IOException e) {
			Log.i("XXX", "_________Copying error " + e.toString());
		}
    }

	public void copyRecordToExport() {
		String dbNameExport = WMA.DB_PATH + SendDBFileName;
		SQLiteDatabase databaseExport = SQLiteDatabase.openDatabase(dbNameExport, null, SQLiteDatabase.OPEN_READWRITE);

		String sql_mas = "select massid,masmid,lang, user, recmod, tp, name, genre, event, city, "
				+ "club, state, mark, album, addr, phone, email, http, geo,"
				+ "comment, nameup, searchup, datebeg,dateend,pict from mas where _id=" + MAS_ID;
		Cursor c_mas = WMA.getDatabase().rawQuery(sql_mas, null);
		c_mas.moveToFirst();

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
		databaseExport.insert("mas", null, recMaster);
		c_mas.close();

		String sql_det = "select mid,ord,detsid,detmid,massid,comment,preview,img from det where mid=" + MAS_ID
				+ " order by ord";
		Cursor c_det = WMA.getDatabase().rawQuery(sql_det, null);
		c_det.moveToFirst();

		while (!c_det.isAfterLast()) {
			ContentValues recDetail = new ContentValues();
			recDetail.put("mid", c_det.getInt(c_det.getColumnIndex("mid")));
			recDetail.put("ord", c_det.getInt(c_det.getColumnIndex("ord")));
			recDetail.put("detsid", c_det.getString(c_det.getColumnIndex("detsid")));
			recDetail.put("detmid", c_det.getString(c_det.getColumnIndex("detmid")));
			recDetail.put("massid", c_det.getString(c_det.getColumnIndex("massid")));
			recDetail.put("comment", c_det.getString(c_det.getColumnIndex("comment")));
			recDetail.put("preview", c_det.getBlob(c_det.getColumnIndex("preview")));
			recDetail.put("img", c_det.getBlob(c_det.getColumnIndex("img")));
			databaseExport.insert("det", null, recDetail);
			c_det.moveToNext();
		}
		c_det.close();
		databaseExport.close();
	}

	// -----------------------------------------------------------------------------
	public void exportDetRecord(String __name, String __sendString) {

		WMA.deleteTempFile(SendDBFileName);
		SendDBFileName = WMA.generValidFileName(__name);
		copyExportDataBase();
		copyRecordToExport();

		File file = new File(WMA.DB_PATH, SendDBFileName);
		Uri imageUri = Uri.fromFile(file);

		Intent i = new Intent(Intent.ACTION_SEND);
		if (imageUri != null) {
			i.setType("*/*");
			i.putExtra(Intent.EXTRA_STREAM, imageUri);
		} else {
			i.setType("plain/text");
		}

		i.putExtra(Intent.EXTRA_TEXT, __sendString);
		i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.s_event_report_subject) + " " + __name);

		i = Intent.createChooser(i, getString(R.string.s_send_report));
		startActivity(i);
		WMA.animateStart(View_Activity.this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		WMA.deleteTempFile(SendDBFileName);
		// Log.i("View_Activity", "delete - " + SendDBFileName);

	}

}