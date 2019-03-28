package ru.droidwelt.concertmemo;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
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
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TimePicker;

@SuppressLint("StaticFieldLeak")
public class Edit_Activity extends ListActivity implements LoaderCallbacks<Cursor> {


	private static long MAS_ID = 0;
	private EditText nameEditText;
	private EditText genreEditText;
	private EditText eventEditText;
	private EditText albumEditText;
	private EditText datebegEditText;
	private EditText dateendEditText;
	private EditText timebegEditText;
	private EditText timeendEditText;
	private EditText clubEditText;
	private EditText addrEditText;
	private EditText phoneEditText;
	private EditText emailEditText;
	private EditText httpEditText;
	private EditText stateEditText;
	private EditText commentEditText;
	private EditText userEditText;

	private ImageButton btn_choice_mark;

	private static ListView detListView;
	private static Det_ImageCursorAdapter detAdapter;
	AlertDialog.Builder adb_xxx;

	static final int EDIT_PICT = 23;
	static final int CHOICE_PICT = 10;

	private String __name = "";
	private String __genre = "";
	private String __event = "";
	private String __album = "";
	private String __datebeg = "";
	private String __dateend = "";
	private String __club = "";
	private String __addr = "";
	private String __phone = "";
	private String __email = "";
	private String __http = "";
	private String __mark = "";
	private String markCurrent = "";
	private String __state = "";
	private String __comment = "";
	private String __user = "";
	private static String __massid = "";

	private String __nameup = "";
	private String __searchup = "";

	int myYearBeg = 0, myYearEnd = 0;
	int myMonthBeg = 0, myMonthEnd = 0;
	int myDayBeg = 0, myDayEnd = 0;
	int myHourBeg = 0, myHourEnd = 0;
	int myMinuteBeg = 0, myMinuteEnd = 0;

	int _myYearBeg = 0, _myYearEnd = 0;
	int _myMonthBeg = 0, _myMonthEnd = 0;
	int _myDayBeg = 0, _myDayEnd = 0;
	int _myHourBeg = 0, _myHourEnd = 0;
	int _myMinuteBeg = 0, _myMinuteEnd = 0;

	Cursor choice_cursor;

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
				v = inflater.inflate(R.layout.edit_activity, null);
			}
			int id = Edit_Activity.getDetAdapterd_ID(pos);
			ImageView iv = v.findViewById(R.id.edit_item_pict);
			iv.setImageBitmap(WMA.getOneDetPreviewValue(id));
			return (v);
		}
	}

	public static int getDetAdapterd_ID(int position) {
		return (int) getDetAdapter().getItemId(position);
	}

	public static Det_ImageCursorAdapter getDetAdapter() {
		return detAdapter;
	}

	// -----------------------------------------------------------------------------------------------
	public static void setDetAdapter(Det_ImageCursorAdapter _detAdapter) {
		Edit_Activity.detAdapter = _detAdapter;
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
			return getAllPict(MAS_ID);
		}
	}

	public static Cursor getAllPict(long mid) {
		String sql = "SELECT _id,ord,comment from det where mid=" + mid + "  order by ord";
		return WMA.getDatabase().rawQuery(sql, null);
	}

	// ------------------------------------------------------------------------------------------------------------------------
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_activity);

		ActionBar bar = getActionBar();
		assert bar != null;
		bar.setHomeButtonEnabled(true);
		bar.setDisplayHomeAsUpEnabled(true);
		WMA.setHeaderFont(this);

		ImageButton btn_choice_name = findViewById(R.id.edit_choice_name);
		btn_choice_name.setOnClickListener(oclBtnOk);

		ImageButton btn_choice_genre = findViewById(R.id.edit_choice_genre);
		btn_choice_genre.setOnClickListener(oclBtnOk);

		ImageButton btn_choice_event = findViewById(R.id.edit_choice_event);
		btn_choice_event.setOnClickListener(oclBtnOk);

		ImageButton btn_choice_album = findViewById(R.id.edit_choice_album);
		btn_choice_album.setOnClickListener(oclBtnOk);

		ImageButton btn_choice_datebeg = findViewById(R.id.edit_choice_datebeg);
		btn_choice_datebeg.setOnClickListener(oclBtnOk);

		ImageButton btn_choice_dateend = findViewById(R.id.edit_choice_dateend);
		btn_choice_dateend.setOnClickListener(oclBtnOk);

		ImageButton btn_choice_timebeg = findViewById(R.id.edit_choice_timebeg);
		btn_choice_timebeg.setOnClickListener(oclBtnOk);

		ImageButton btn_choice_timeend = findViewById(R.id.edit_choice_timeend);
		btn_choice_timeend.setOnClickListener(oclBtnOk);

		ImageButton btn_choice_club = findViewById(R.id.edit_choice_club);
		btn_choice_club.setOnClickListener(oclBtnOk);

		ImageButton btn_choice_state = findViewById(R.id.edit_choice_state);
		btn_choice_state.setOnClickListener(oclBtnOk);

		btn_choice_mark = findViewById(R.id.edit_choice_mark);
		btn_choice_mark.setOnClickListener(oclBtnOk);

		nameEditText = findViewById(R.id.edit_name);
		genreEditText = findViewById(R.id.edit_genre);
		eventEditText = findViewById(R.id.edit_event);
		albumEditText = findViewById(R.id.edit_album);
		datebegEditText = findViewById(R.id.edit_datebeg);
		dateendEditText = findViewById(R.id.edit_dateend);
		timebegEditText = findViewById(R.id.edit_timebeg);
		timeendEditText = findViewById(R.id.edit_timeend);
		clubEditText = findViewById(R.id.edit_club);
		addrEditText = findViewById(R.id.edit_addr);
		phoneEditText = findViewById(R.id.edit_phone);
		emailEditText = findViewById(R.id.edit_email);
		httpEditText = findViewById(R.id.edit_http);
		stateEditText = findViewById(R.id.edit_state);
		commentEditText = findViewById(R.id.edit_comment);
		userEditText = findViewById(R.id.edit_user);

		MAS_ID = WMA.getMAS_ID();
		if (MAS_ID > 0) {
			loadEditRecord();
		}

		adb_xxx = new AlertDialog.Builder(Edit_Activity.this);

		Context context = getApplicationContext();
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		assert imm != null;
		imm.hideSoftInputFromWindow(nameEditText.getWindowToken(), 0);

		String[] from = new String[] { "comment" };
		int[] to = new int[] { R.id.edit_item_comment };

		setDetAdapter(new Det_ImageCursorAdapter(this, R.layout.edit_item, null, from, to));
		detListView = this.getListView();
		detListView.setOnItemClickListener(detListener);
		detListView.setOnItemLongClickListener(detLoggListener);
		setListAdapter(getDetAdapter());
		getLoaderManager().initLoader(0, null, this);
	}

	// длинный клик, удаление записи
	OnItemLongClickListener detLoggListener = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
			if (id > 0) {
				deleteMasRecord(id);
			}
			return true;
		}
	};

	// -------------------------------------------------------------------------------------------------------------------------
	public void detTableRefresh() {
		getDetAdapter().changeCursor(getAllPict(MAS_ID));
	}

	private void deleteMasRecord(final long id_Delete) {
		final Timer timer = new Timer();
		AlertDialog.Builder builder = new AlertDialog.Builder(Edit_Activity.this);
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

				@SuppressLint("StaticFieldLeak") AsyncTask<Long, Object, Object> deleteTask = new AsyncTask<Long, Object, Object>() {
					@Override
					protected Object doInBackground(Long... params) {
						WMA.getDatabase().delete("det", "_id=" + params[0], null);
						return null;
					}

					@Override
					protected void onPostExecute(Object result) {
						detTableRefresh();
					}
				};
				deleteTask.execute(id_Delete);
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

	private void EditPicture(long id, int action) {
		if (MAS_ID == 0) {
			saveRecord();
		}
		Intent editPict = new Intent(Edit_Activity.this, EditPict_Activity.class);
		WMA.setDET_ACTION(action);
		WMA.setDET_COUNT(detListView.getCount());
		WMA.setDET_ID(id);
		WMA.setMAS_ID(MAS_ID);
		WMA.setMAS_SID(__massid);
		startActivityForResult(editPict, EDIT_PICT);
		WMA.animateStart(Edit_Activity.this);
	}

	private void OrderPicture(int action) {
		if (getDetAdapter().getCount() > 1) {
			if (MAS_ID == 0) {
				saveRecord();
			}
			Intent orderPict = new Intent(Edit_Activity.this, OrderPic_Activity.class);
			WMA.setDET_ACTION(action);
			WMA.setDET_COUNT(detListView.getCount());
			WMA.setMAS_ID(MAS_ID);
			WMA.setMAS_SID(__massid);
			startActivityForResult(orderPict, EDIT_PICT);
			WMA.animateStart(Edit_Activity.this);
		}
	}

	// ------------------------------------------------------------------------------------------
	// слушатель событий в ListView
	OnItemClickListener detListener = new OnItemClickListener() {
		// посмотр записи, из него редактирование или удаление записи
		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			EditPicture(id, 0);
		}
	};

	@SuppressWarnings("deprecation")
	@SuppressLint("SimpleDateFormat")
	private void loadEditRecord() {
		String sql = "select _id,name,genre,event,album,datebeg,dateend,club,addr,phone,email,"
				+ "http,state,mark,comment,user,nameup,searchup,massid from mas where _id=" + MAS_ID;
		Cursor EditData = WMA.getDatabase().rawQuery(sql, null);
		EditData.moveToFirst(); // перемещение к первому элементу

		int nameIndex = EditData.getColumnIndex("name");
		int genreIndex = EditData.getColumnIndex("genre");
		int eventIndex = EditData.getColumnIndex("event");
		int albumIndex = EditData.getColumnIndex("album");
		int datebegIndex = EditData.getColumnIndex("datebeg");
		int dateendIndex = EditData.getColumnIndex("dateend");
		int clubIndex = EditData.getColumnIndex("club");
		int addrIndex = EditData.getColumnIndex("addr");
		int phoneIndex = EditData.getColumnIndex("phone");
		int emailIndex = EditData.getColumnIndex("email");
		int httpIndex = EditData.getColumnIndex("http");
		int stateIndex = EditData.getColumnIndex("state");
		int markIndex = EditData.getColumnIndex("mark");
		int commentIndex = EditData.getColumnIndex("comment");
		int userIndex = EditData.getColumnIndex("user");
		int searchupIndex = EditData.getColumnIndex("searchup");
		int nameupIndex = EditData.getColumnIndex("nameup");
		int massidIndex = EditData.getColumnIndex("massid");

		// заполнение компонентов TextViews выбранными данными
		__name = WMA.strnormalize(EditData.getString(nameIndex));
		__genre = WMA.strnormalize(EditData.getString(genreIndex));
		__event = WMA.strnormalize(EditData.getString(eventIndex));
		__album = WMA.strnormalize(EditData.getString(albumIndex));
		__datebeg = WMA.strnormalize(EditData.getString(datebegIndex));
		__dateend = WMA.strnormalize(EditData.getString(dateendIndex));
		__club = WMA.strnormalize(EditData.getString(clubIndex));
		__addr = WMA.strnormalize(EditData.getString(addrIndex));
		__phone = WMA.strnormalize(EditData.getString(phoneIndex));
		__email = WMA.strnormalize(EditData.getString(emailIndex));
		__http = WMA.strnormalize(EditData.getString(httpIndex));
		__state = WMA.strnormalize(EditData.getString(stateIndex));
		__mark = WMA.strnormalize(EditData.getString(markIndex));
		__comment = WMA.strnormalize(EditData.getString(commentIndex));
		__user = WMA.strnormalize(EditData.getString(userIndex));
		__nameup = WMA.strnormalize(EditData.getString(nameupIndex));
		__searchup = WMA.strnormalize(EditData.getString(searchupIndex));
		__massid = WMA.strnormalize(EditData.getString(massidIndex));
		WMA.setMAS_SID(__massid);

		Date dateBeg = WMA.ConvertStrToDate(__datebeg);
		assert dateBeg != null;
		_myYearBeg = dateBeg.getYear() + 1900;
		_myMonthBeg = dateBeg.getMonth();
		_myDayBeg = dateBeg.getDate();

		Date dateEnd = WMA.ConvertStrToDate(__dateend);
		assert dateEnd != null;
		_myYearEnd = dateEnd.getYear() + 1900;
		_myMonthEnd = dateEnd.getMonth();
		_myDayEnd = dateEnd.getDate();

		Date date = WMA.ConvertStrToDate(__datebeg);
		assert date != null;
		myYearBeg = date.getYear() + 1900;
		myMonthBeg = date.getMonth() + 1;
		myDayBeg = date.getDate();
		myHourBeg = date.getHours();
		myMinuteBeg = date.getMinutes();

		date = WMA.ConvertStrToDate(__dateend);
		assert date != null;
		myYearEnd = date.getYear() + 1900;
		myMonthEnd = date.getMonth() + 1;
		myDayEnd = date.getDate();
		myHourEnd = date.getHours();
		myMinuteEnd = date.getMinutes();

		nameEditText.setText(__name);
		genreEditText.setText(__genre);
		eventEditText.setText(__event);
		albumEditText.setText(__album);
		DisplayDT();
		clubEditText.setText(__club);
		addrEditText.setText(__addr);
		phoneEditText.setText(__phone);
		emailEditText.setText(__email);
		httpEditText.setText(__http);
		stateEditText.setText(__state);
		markCurrent = (__mark);
		WMA.DisplayMark(markCurrent, btn_choice_mark);
		commentEditText.setText(__comment);
		userEditText.setText(__user);
		if (WMA.getMastChangeUser()) {
			__user = WMA.getMyGoogleAccount();
			userEditText.setTextColor(getResources().getColor(R.color.c_attention));
		}
		EditData.close();
	}

	@SuppressWarnings("deprecation")
	@SuppressLint({"DefaultLocale", "SetTextI18n"})
	private void saveRecordPrepare() {
		if (nameEditText.getText().length() == 0)
			nameEditText.setText("_" + WMA.ConvertDateToStr(new Date()));
		__name = nameEditText.getText().toString().trim();
		__genre = genreEditText.getText().toString().trim();
		__event = eventEditText.getText().toString().trim();
		__album = albumEditText.getText().toString().trim();
		if (myDayBeg == 0) {
			Date dt = new Date();
			dt.setHours(19);
			dt.setMinutes(0);
			dt.setDate(dt.getDate() + 1);
			dt.setSeconds(0);
			__datebeg = WMA.ConvertDateToStr(dt) + ":00";
		} else {
			__datebeg = WMA.ConvertDatePartsToStr(myYearBeg, myMonthBeg, myDayBeg, myHourBeg, myMinuteBeg) + ":00";
		}
		if (myDayEnd == 0) {
			Date dt = new Date();
			dt.setHours(22);
			dt.setMinutes(0);
			dt.setDate(dt.getDate() + 1);
			dt.setSeconds(0);
			__dateend = WMA.ConvertDateToStr(dt) + ":00";
		} else {
			__dateend = WMA.ConvertDatePartsToStr(myYearEnd, myMonthEnd, myDayEnd, myHourEnd, myMinuteEnd) + ":00";
		}
		__club = clubEditText.getText().toString().trim();
		__addr = addrEditText.getText().toString().trim();
		__phone = phoneEditText.getText().toString().trim();
		__email = emailEditText.getText().toString().trim();
		__http = httpEditText.getText().toString().trim(); // .toLowerCase(java.util.Locale.ROOT)
		__state = stateEditText.getText().toString().trim();
		__mark = markCurrent;
		__comment = commentEditText.getText().toString().trim();

		__nameup = __name.toUpperCase();

		String user;
		if (WMA.getMastChangeUser()) {
			user = WMA.getMyGoogleAccount();
		} else {
			user = __user;
		}
		__searchup = (__name + user + __genre + __event + __club + __state + __album + __addr + __email + __http
				+ __phone + __email + __http).toUpperCase();
	}

	// сохранение записи в базе данных----------------------------
	private void saveRecord() {
		saveRecordPrepare();

		if (MAS_ID == 0) {
			MAS_ID = insertContact(__name, __genre, __event, __album, __datebeg, __dateend, __club, __addr, __phone,
					__email, __http, __state, __mark, __comment, __user, __nameup, __searchup);
		} else {
			updateContact(MAS_ID, __name, __genre, __event, __album, __datebeg, __dateend, __club, __addr, __phone,
					__email, __http, __state, __mark, __comment, __nameup, __searchup);
		}

		setResult(RESULT_OK);
		WMA.myWidgetAlarm(Edit_Activity.this);
		finish();
		WMA.animateFinish(Edit_Activity.this);
	}

	// --------------------------------------------------------------------------------------------------------------
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent returnedIntent) {
		super.onActivityResult(requestCode, resultCode, returnedIntent);

		if (resultCode == RESULT_OK) {

			switch (requestCode) {

			case EDIT_PICT:
				detTableRefresh();
				setResult(RESULT_OK);
				break;

			case CHOICE_PICT:
				int pict_id = returnedIntent.getIntExtra("PICT", 0);
				if (pict_id == 0) {
					markCurrent = "0";
				} else {
					markCurrent = Integer.toString(pict_id);
				}
				WMA.DisplayMark(markCurrent, btn_choice_mark);
				break;
			}
		}
	}

	// подключение меню ----------------------------------------
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.edit_menu, menu);
		return true;
	}

	// меню -----------------------------------------------
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.edit_menuItem_save:
			saveRecord();
			return true;

		case R.id.editstart_menuItem_pic_choice:
			EditPicture((long) 0, 1);
			return true;

		case R.id.editstart_menuItem_pic_photo:
			EditPicture((long) 0, 2);
			return true;

		case R.id.editstart_menuItem_pic_order:
			OrderPicture(1);
			return true;

		case android.R.id.home:
			if (isRecordModified()) {
				openQuitDialogMy();
			} else {
				finish();
				WMA.animateFinish(Edit_Activity.this);
			}
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// ------------------------------------------------------------------------------------------------------------------------
	private boolean isRecordModified() {
		String dbeg = WMA.ConvertDatePartsToStr(myYearBeg, myMonthBeg, myDayBeg, myHourBeg, myMinuteBeg) + ":00";
		String dend = WMA.ConvertDatePartsToStr(myYearEnd, myMonthEnd, myDayEnd, myHourEnd, myMinuteEnd) + ":00";

		return !(__name.compareToIgnoreCase(nameEditText.getText().toString()) == 0)
				| !(__event.compareToIgnoreCase(eventEditText.getText().toString()) == 0)
				| !(__genre.compareToIgnoreCase(genreEditText.getText().toString()) == 0)
				| !(__album.compareToIgnoreCase(albumEditText.getText().toString()) == 0)
				| !(__datebeg.compareToIgnoreCase(dbeg) == 0) | !(__dateend.compareToIgnoreCase(dend) == 0)
				| !(__club.compareToIgnoreCase(clubEditText.getText().toString()) == 0)
				| !(__addr.compareToIgnoreCase(addrEditText.getText().toString()) == 0)
				| !(__phone.compareToIgnoreCase(phoneEditText.getText().toString()) == 0)
				| !(__email.compareToIgnoreCase(emailEditText.getText().toString()) == 0)
				| !(__http.compareToIgnoreCase(httpEditText.getText().toString()) == 0)
				| !(__state.compareToIgnoreCase(stateEditText.getText().toString()) == 0)
				| !(__mark.compareToIgnoreCase(markCurrent) == 0)
				| !(__comment.compareToIgnoreCase(commentEditText.getText().toString()) == 0) | (__nameup.equals(""))
				| (__searchup.equals(""));
	}

	// защита от закрытия по Back------------------------------------
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Обработка нажатия, возврат true, если обработка выполнена
		switch (keyCode) {

		case KeyEvent.KEYCODE_BACK:
			if (isRecordModified()) {
				openQuitDialogMy();
			} else {
				finish();
				WMA.animateFinish(Edit_Activity.this);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// Диалог переспроса о выходе------------------------------------
	private void openQuitDialogMy() {
		final Timer timer = new Timer();
		AlertDialog.Builder builder = new AlertDialog.Builder(Edit_Activity.this);
		builder.setTitle(R.string.s_exit_wo_save);

		builder.setPositiveButton(R.string.s_yes, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				timer.purge();
				timer.cancel();
				finish();
				WMA.animateFinish(Edit_Activity.this);
			}
		});

		builder.setNegativeButton(R.string.s_no, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				timer.purge();
				timer.cancel();
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

	// обработчик нажатия на пункт списка диалога
	OnClickListener name_choice_ClickListener = new OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			choice_cursor.moveToPosition(which);
			nameEditText.setText(choice_cursor.getString(choice_cursor.getColumnIndex("res")));
			dialog.dismiss();
		}
	};

	OnClickListener genre_choice_ClickListener = new OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			choice_cursor.moveToPosition(which);
			genreEditText.setText(choice_cursor.getString(choice_cursor.getColumnIndex("res")));
			dialog.dismiss();
		}
	};

	OnClickListener event_choice_ClickListener = new OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			choice_cursor.moveToPosition(which);
			eventEditText.setText(choice_cursor.getString(choice_cursor.getColumnIndex("res")));
			dialog.dismiss();
		}
	};

	OnClickListener album_choice_ClickListener = new OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			choice_cursor.moveToPosition(which);
			albumEditText.setText(choice_cursor.getString(choice_cursor.getColumnIndex("res")));
			dialog.dismiss();
		}
	};

	OnClickListener club_choice_ClickListener = new OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			choice_cursor.moveToPosition(which);
			String myClub = choice_cursor.getString(choice_cursor.getColumnIndex("res"));
			clubEditText.setText(myClub);
			if (!myClub.isEmpty()) {
				String sql = "select  addr from  mas where not(addr is null) and addr<>'' and club='" + myClub
						+ "' order by _id desc";
				Cursor clubcursor = WMA.getDatabase().rawQuery(sql, null);
				if (clubcursor.getCount() > 0) {
					clubcursor.moveToFirst();
					try {
						addrEditText.setText(clubcursor.getString(clubcursor.getColumnIndex("addr")));
					} catch (Exception ignored) {
					}
				} else {
					addrEditText.setText("");
				}
				clubcursor.close();
			}

			myClub = choice_cursor.getString(choice_cursor.getColumnIndex("res"));
			clubEditText.setText(myClub);
			if (!myClub.isEmpty()) {
				String sql = "select  phone from  mas where not(phone is null) and phone<>'' and club='" + myClub
						+ "' order by _id desc";
				Cursor clubcursor = WMA.getDatabase().rawQuery(sql, null);
				if (clubcursor.getCount() > 0) {
					clubcursor.moveToFirst();
					try {
						phoneEditText.setText(clubcursor.getString(clubcursor.getColumnIndex("phone")));
					} catch (Exception ignored) {
					}
				} else {
					phoneEditText.setText("");
				}
				clubcursor.close();
			}

			myClub = choice_cursor.getString(choice_cursor.getColumnIndex("res"));
			clubEditText.setText(myClub);
			if (!myClub.isEmpty()) {
				String sql = "select  email from  mas where not(email is null) and email<>'' and club='" + myClub
						+ "' order by _id desc";
				Cursor clubcursor = WMA.getDatabase().rawQuery(sql, null);
				if (clubcursor.getCount() > 0) {
					clubcursor.moveToFirst();
					try {
						emailEditText.setText(clubcursor.getString(clubcursor.getColumnIndex("email")));
					} catch (Exception ignored) {
					}
				} else {
					emailEditText.setText("");
				}
				clubcursor.close();
			}

			myClub = choice_cursor.getString(choice_cursor.getColumnIndex("res"));
			clubEditText.setText(myClub);
			if (!myClub.isEmpty()) {
				String sql = "select  http from  mas where not(http is null) and http<>'' and club='" + myClub
						+ "' order by _id desc";
				Cursor clubcursor = WMA.getDatabase().rawQuery(sql, null);
				if (clubcursor.getCount() > 0) {
					clubcursor.moveToFirst();
					try {
						httpEditText.setText(clubcursor.getString(clubcursor.getColumnIndex("http")));
					} catch (Exception ignored) {
					}
				} else {
					httpEditText.setText("");
				}
				clubcursor.close();
			}
			dialog.dismiss();
		}
	};

	OnClickListener state_choice_ClickListener = new OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			choice_cursor.moveToPosition(which);
			stateEditText.setText(choice_cursor.getString(choice_cursor.getColumnIndex("res")));
			dialog.dismiss();
		}
	};

	// слушатель установки даты----------------------------------------------
	OnDateSetListener DateBegCallBack = new OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			myYearBeg = year;
			myMonthBeg = monthOfYear + 1;
			myDayBeg = dayOfMonth;
		/*	if (myYearEnd == 0) {
				myYearEnd = myYearBeg;
				myMonthEnd = myMonthBeg;
				myDayEnd = myDayBeg;
			} */
			DisplayDT();
		}
	};

	// слушатель установки
	// даты--------------------------------------------------
	OnDateSetListener DateEndCallBack = new OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			myYearEnd = year;
			myMonthEnd = monthOfYear + 1;
			myDayEnd = dayOfMonth;
		/*	if (myYearBeg == 0) {
				myYearBeg = myYearEnd;
				myMonthBeg = myMonthEnd;
				myDayBeg = myDayEnd;
			} */
			DisplayDT();
		}
	};

	// слушатель установки
	// времени-------------------------------------------------
	OnTimeSetListener TimeBegCallBack = new OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			myHourBeg = hourOfDay;
			myMinuteBeg = minute;
		/*	if (myHourEnd == 0) {
				myHourEnd = myHourBeg;
				myMinuteEnd = myMinuteBeg;
			} */
			DisplayDT();
		}
	};

	// слушатель установки времени------------------------------------------
	OnTimeSetListener TimeEndCallBack = new OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			myHourEnd = hourOfDay;
			myMinuteEnd = minute;

			DisplayDT();
		}
	};

	// ------------------------------------------------------------------
	@SuppressWarnings("deprecation")
	public void DisplayDT() {
		String sBeg_DT;
		Date da_beg = new Date();
		da_beg.setYear(myYearBeg - 1900);
		da_beg.setMonth(myMonthBeg - 1);
		da_beg.setDate(myDayBeg);
		if (myYearBeg > 2000) {
			sBeg_DT = WMA.ConvertDateToStr_LocDT(da_beg);
		} else {
			sBeg_DT = "???";
		}
		datebegEditText.setText(sBeg_DT);

		String sEnd_DT;
		Date da_end = new Date();
		da_end.setYear(myYearEnd - 1900);
		da_end.setMonth(myMonthEnd - 1);
		da_end.setDate(myDayEnd);
		if (myYearEnd > 2000) {
			sEnd_DT = WMA.ConvertDateToStr_LocDT(da_end);
		} else {
			sEnd_DT = "???";
		}
		dateendEditText.setText(sEnd_DT);

		String sBeg_TM = WMA.ConvertDatePartsToStr_TM(myHourBeg, myMinuteBeg);
		timebegEditText.setText(sBeg_TM);
		String sEnd_TM = WMA.ConvertDatePartsToStr_TM(myHourEnd, myMinuteEnd);
		timeendEditText.setText(sEnd_TM);
	}

	public static Cursor getChoiceList(String tbl) {
		String sql = "select  _id, " + tbl + " as res from  mas " + "where   not " + tbl + " is null and " + tbl
				+ "<>'' and " + "(tp=0 or (tp=1 and (lang='' or lang='" + WMA.getLangprefix() + "'))) "
				+ "group by  res	 order by res";
		return WMA.getDatabase().rawQuery(sql, null);
	}

	// слушатель нажатия на кнопку-----------------------------------------
	android.view.View.OnClickListener oclBtnOk = new android.view.View.OnClickListener() {

		@SuppressLint("SimpleDateFormat")
		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {

			case R.id.edit_choice_name:
				choice_cursor = getChoiceList("name");
				adb_xxx.setTitle(R.string.s_fld_name);
				adb_xxx.setCursor(choice_cursor, name_choice_ClickListener, "res");
				adb_xxx.show();
				break;

			case R.id.edit_choice_genre:
				choice_cursor = getChoiceList("genre");
				adb_xxx.setTitle(R.string.s_fld_genre);
				adb_xxx.setCursor(choice_cursor, genre_choice_ClickListener, "res");
				adb_xxx.show();
				break;

			case R.id.edit_choice_event:
				choice_cursor = getChoiceList("event");
				adb_xxx.setTitle(R.string.s_fld_event);
				adb_xxx.setCursor(choice_cursor, event_choice_ClickListener, "res");
				adb_xxx.show();
				break;

			case R.id.edit_choice_album:
				choice_cursor = getChoiceList("album");
				adb_xxx.setTitle(R.string.s_fld_album);
				adb_xxx.setCursor(choice_cursor, album_choice_ClickListener, "res");
				adb_xxx.show();
				break;

			case R.id.edit_choice_club:
				choice_cursor = getChoiceList("club");
				adb_xxx.setTitle(R.string.s_fld_club);
				adb_xxx.setCursor(choice_cursor, club_choice_ClickListener, "res");
				adb_xxx.show();
				break;

			case R.id.edit_choice_state:
				choice_cursor = getChoiceList("state");
				adb_xxx.setTitle(R.string.s_fld_state);
				adb_xxx.setCursor(choice_cursor, state_choice_ClickListener, "res");
				adb_xxx.show();
				break;

			case R.id.edit_choice_mark:
				Intent choiceactivity = new Intent(Edit_Activity.this, ChoicePict_Activity.class);
				startActivityForResult(choiceactivity, CHOICE_PICT);
				break;

			case R.id.edit_choice_datebeg: {
				if (myYearBeg == 0) {
					long currentTime = System.currentTimeMillis();
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(currentTime);
					_myYearBeg = cal.get(Calendar.YEAR);
					_myMonthBeg = cal.get(Calendar.MONTH);
					_myDayBeg = cal.get(Calendar.DAY_OF_MONTH);
					__datebeg = WMA.ConvertDatePartsToStr_DT(_myYearBeg, _myMonthBeg, _myDayBeg); // ###
				} else {
					Date date = WMA.ConvertStrToDate(__datebeg);
					if (!(date == null)) {
						_myYearBeg = date.getYear() + 1900;
						_myMonthBeg = date.getMonth();
						_myDayBeg = date.getDate();
					}
				}

				DatePickerDialog dtbeg = new DatePickerDialog(Edit_Activity.this, DateBegCallBack, _myYearBeg,
						_myMonthBeg, _myDayBeg);
				dtbeg.show();
				break;
			}

			case R.id.edit_choice_dateend: {
				if (myYearEnd == 0) {
					long currentTime = System.currentTimeMillis();
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(currentTime);
					_myYearEnd = cal.get(Calendar.YEAR);
					_myMonthEnd = cal.get(Calendar.MONTH);
					_myDayEnd = cal.get(Calendar.DAY_OF_MONTH);
					__dateend = WMA.ConvertDatePartsToStr_DT(_myYearEnd, _myMonthEnd, _myDayEnd); // ###
				} else {
					Date date = WMA.ConvertStrToDate(__dateend);
					if (!(date == null)) {
						_myYearEnd = date.getYear() + 1900;
						_myMonthEnd = date.getMonth();
						_myDayEnd = date.getDate();
					}
				}
				DatePickerDialog dtend = new DatePickerDialog(Edit_Activity.this, DateEndCallBack, _myYearEnd,
						_myMonthEnd, _myDayEnd);
				dtend.show();
				break;
			}

			case R.id.edit_choice_timebeg: {
				if ((myHourBeg == 0) | (__datebeg.equals(""))) {
					_myHourBeg = 19;
					_myMinuteBeg = 0;
				} else {
					Date date = WMA.ConvertStrToDate(__datebeg);
					if (!(date == null)) {
						_myHourBeg = date.getHours();
						_myMinuteBeg = date.getMinutes();
					}
				}
				TimePickerDialog tmbeg = new TimePickerDialog(Edit_Activity.this, TimeBegCallBack, _myHourBeg,
						_myMinuteBeg, true);
				tmbeg.show();
				break;
			}

			case R.id.edit_choice_timeend: {
				if ((myHourEnd == 0) | (__dateend.equals(""))) {
					_myHourEnd = 23;
					_myMinuteEnd = 0;
				} else {
					Date date = WMA.ConvertStrToDate(__dateend);
					if (!(date == null)) {
						_myHourEnd = date.getHours();
						_myMinuteEnd = date.getMinutes();
					}
				}
				TimePickerDialog tmend = new TimePickerDialog(Edit_Activity.this, TimeEndCallBack, _myHourEnd,
						_myMinuteEnd, true);
				tmend.show();
				break;
			}

			default:
				break;
			}
		}
	};

	// ------------------------------------------------------------------------
	public static long insertContact(String name, String genre, String event, String album, String datebeg,
			String dateend, String club, String addr, String phone, String email, String http, String state,
			String mark, String comment, String user, String nameup, String searchup) {
		ContentValues newContact = new ContentValues();
		newContact.put("name", name);
		newContact.put("genre", genre);
		newContact.put("event", event);
		newContact.put("album", album);
		newContact.put("datebeg", datebeg);
		newContact.put("dateend", dateend);
		newContact.put("club", club);
		newContact.put("addr", addr);
		newContact.put("phone", phone);
		newContact.put("email", email);
		newContact.put("http", http);
		newContact.put("state", state);
		newContact.put("mark", mark);
		newContact.put("comment", comment);
		newContact.put("user", user);
		newContact.put("nameup", nameup);
		newContact.put("searchup", searchup);
		newContact.put("tp", "0");
		__massid = WMA.generateGUID(20);
		newContact.put("massid", __massid);
		newContact.put("masmid", WMA.generateGUID(20));
		long new_id = WMA.getDatabase().insert("mas", null, newContact);
		WMA.setMAS_ID(new_id);
		WMA.setMAS_SID(__massid);
		return new_id;
	}

	// ------------------------------------------------------------------------
	public static void updateContact(long id, String name, String genre, String event, String album, String datebeg,
			String dateend, String club, String addr, String phone, String email, String http, String state,
			String mark, String comment, String nameup, String searchup) {
		ContentValues editContact = new ContentValues();
		editContact.put("name", name);
		editContact.put("genre", genre);
		editContact.put("event", event);
		editContact.put("album", album);
		editContact.put("datebeg", datebeg);
		editContact.put("dateend", dateend);
		editContact.put("club", club);
		editContact.put("addr", addr);
		editContact.put("phone", phone);
		editContact.put("email", email);
		editContact.put("http", http);
		editContact.put("state", state);
		editContact.put("mark", mark);
		editContact.put("comment", comment);
		if (WMA.getMastChangeUser()) {
			editContact.put("user", WMA.getMyGoogleAccount());
		}
		editContact.put("nameup", nameup);
		editContact.put("searchup", searchup);
		editContact.put("tp", "0");
		if ((WMA.getMastChangeUser()) & (__massid.isEmpty()) & (__massid.equals(""))) {
			__massid = WMA.generateGUID(20);
			editContact.put("massid", __massid);
			WMA.setMAS_SID(__massid);
			WMA.updateSidInDetail(MAS_ID, __massid);
		}
		editContact.put("masmid", WMA.generateGUID(20));
		WMA.getDatabase().update("mas", editContact, "_id=" + id, null);
	}
}
