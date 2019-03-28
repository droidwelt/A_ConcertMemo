package ru.droidwelt.concertmemo;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class ViewPict_Activity extends Activity {

	private static int DET_ID = 0;
	private boolean _createIcon = false;
	private String __commentDet = "";

	// ------------------------------------------------------------------------------------------------------------------------
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewpict_activity);
		_createIcon = false;

		ActionBar bar = getActionBar();
		assert bar != null;
		bar.setHomeButtonEnabled(true);
		bar.setDisplayHomeAsUpEnabled(true);
		WMA.setHeaderFont(this);

		DET_ID = (int) WMA.getDET_ID();
		TouchImageView tvi = findViewById(R.id.viewpict_touch);
		tvi.setImageBitmap(WMA.getOneDetImageValue(DET_ID));

		String sql = "SELECT comment from det where _id= " + DET_ID;

		Cursor cursor = WMA.getDatabase().rawQuery(sql, null);
		cursor.moveToFirst();
		int commentIndex = cursor.getColumnIndex("comment");
		__commentDet = WMA.strnormalize(cursor.getString(commentIndex));
		TextView tv_comment = findViewById(R.id.viewpict_comment);
		tv_comment.setText(__commentDet);
		cursor.close();

	}

	// ------------------------------------------------------------------------------------------------------------------------
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.viewpict_menu, menu);
		return true;
	}

	// защита от закрытия по Back------------------------------------
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Обработка нажатия, возврат true, если обработка выполнена
		switch (keyCode) {

		case KeyEvent.KEYCODE_BACK:
			if (_createIcon) {
				setResult(RESULT_OK);
			}
			finish();
			WMA.animateFinish(ViewPict_Activity.this);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// ------------------------------------------------------------------------------------------------------------------------
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.viewpict_menuItem_share:

			String sql = "SELECT _id,name,event,album,club,state,datebeg,dateend,comment,genre,user from mas where _id= "
					+ WMA.getMAS_ID();

			Cursor cursor = WMA.getDatabase().rawQuery(sql, null);
			cursor.moveToFirst();
			int nameIndex = cursor.getColumnIndex("name");
			int eventIndex = cursor.getColumnIndex("event");
			int albumIndex = cursor.getColumnIndex("album");
			int clubIndex = cursor.getColumnIndex("club");
			int stateIndex = cursor.getColumnIndex("state");
			int datebegIndex = cursor.getColumnIndex("datebeg");
			int commentIndex = cursor.getColumnIndex("comment");
			int genreIndex = cursor.getColumnIndex("genre");
			int userIndex = cursor.getColumnIndex("user");

			// заполнение компонентов TextViews выбранными данными
			String __name = WMA.strnormalize(cursor.getString(nameIndex));
			String __event = WMA.strnormalize(cursor.getString(eventIndex));
			Date dt_datebeg = WMA.ConvertStrToDate(cursor.getString(datebegIndex));
			assert dt_datebeg != null;
			String __datebeg = WMA.ConvertDateToStr_Loc(dt_datebeg);
			String __album = WMA.strnormalize(cursor.getString(albumIndex));
			String __club = WMA.strnormalize(cursor.getString(clubIndex));
			String __state = WMA.strnormalize(cursor.getString(stateIndex));
			String __comment = WMA.strnormalize(cursor.getString(commentIndex));
			String __genre = WMA.strnormalize(cursor.getString(genreIndex));
			String __user = WMA.strnormalize(cursor.getString(userIndex));
			WMA.strnormalize(cursor.getString(datebegIndex));
			cursor.close();

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
			if (!__commentDet.equals(""))
				res = res + __commentDet + ";";
			res = res + getString(R.string.s_prepare_to_share);

			shareDetRecord(DET_ID, __name, res);
			return true;

		case R.id.viewpict_menuItem_makethumbnail:

			final Timer timer = new Timer();
			AlertDialog.Builder builder = new AlertDialog.Builder(ViewPict_Activity.this);
			builder.setTitle(getString(R.string.s_makethumbnail) + " ?");

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
					WMA.makeMainThumbnail(DET_ID);
					_createIcon = true;
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

			return true;

		case R.id.viewpict_menuItem_download:
			WMA.uploadDetPicture(DET_ID);
			return true;

		case android.R.id.home:
			WMA.deleteTempFile(WMA.sendFilename);
			if (_createIcon) {
				setResult(RESULT_OK);
			}
			finish();
			WMA.animateFinish(ViewPict_Activity.this);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// -------------------------------------------------------------------------------
	public void shareDetRecord(long _DET_ID, String __name, String __sendString) {
		Bitmap imageBig1 = WMA.getOneDetImageValue(_DET_ID);
		if (imageBig1 != null) {

			Intent i = new Intent(Intent.ACTION_SEND);

			try {
				FileOutputStream fos = new FileOutputStream(WMA.DB_PATH + WMA.sendFilename);
				imageBig1.compress(CompressFormat.JPEG, 75, fos);

				fos.flush();
				fos.close();
			} catch (Exception e) {
				Log.i("XXX", "FileOutputStream fos " + e.toString());
			}

			File file = new File(WMA.DB_PATH, WMA.sendFilename);
			Uri imageUri = Uri.fromFile(file);
			if (imageUri != null) {
				i.setType("image/jpg");
				i.putExtra(Intent.EXTRA_STREAM, imageUri);
			} else {
				i.setType("plain/text");
			}

			i.putExtra(Intent.EXTRA_TEXT, __sendString);
			i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.s_event_report_subject) + " " + __name);

			i = Intent.createChooser(i, getString(R.string.s_send_report));
			startActivity(i);
			WMA.animateStart(ViewPict_Activity.this);
		} else {
			Toast.makeText(this, getString(R.string.s_forbilden_wo_pic), Toast.LENGTH_LONG).show();
		}
	}

}