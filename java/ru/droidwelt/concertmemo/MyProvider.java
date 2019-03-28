package ru.droidwelt.concertmemo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.widget.RemoteViews;

import java.util.Objects;

public class MyProvider extends AppWidgetProvider {

	final String ACTION_ON_CLICK = "ru.droidwelt.concertmemo.itemonclick";
	final static String ITEM_POSITION = "ru.droidwelt.concertmemo.item_position";
	final static String ITEM_MAS_ID = "ru.droidwelt.concertmemo.item_mas_id";
	final static String UPDATE_ALL_WIDGETS = "ru.droidwelt.concertmemo.update_all_widgets";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		for (int i : appWidgetIds) {
			updateWidget(context, appWidgetManager, i);
		}
	}

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		Intent intent = new Intent(context, MyProvider.class); // MyWidget
		intent.setAction(UPDATE_ALL_WIDGETS);
		PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		assert alarmManager != null;
		alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 1800000, pIntent);
	}

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		Intent intent = new Intent(context, MyProvider.class); // MyWidget
		intent.setAction(UPDATE_ALL_WIDGETS);
		PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		assert alarmManager != null;
		alarmManager.cancel(pIntent);
	}

	private Bitmap convertToImg(String text, Context context) {
		Bitmap btmText = Bitmap.createBitmap(800, 100, Bitmap.Config.ARGB_4444);
		Canvas cnvText = new Canvas(btmText);
		Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/Decker.ttf");
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setSubpixelText(true);
		paint.setTypeface(tf);
		paint.setColor(Color.WHITE);
		paint.setTextSize(90); 

		cnvText.drawText(text, 0, 80, paint);
		return btmText;
	}

	void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
		RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget);

		setUpdateTV(rv, context, appWidgetId);
		setList(rv, context, appWidgetId);
		setListClick(rv, context);

		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
		remoteViews.setImageViewBitmap(R.id.widget_imagetitle, convertToImg("Concert Memo", context));
		appWidgetManager.updateAppWidget(appWidgetId, remoteViews);

		appWidgetManager.updateAppWidget(appWidgetId, rv);
		appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_lv_list);
	}

	void setUpdateTV(RemoteViews rv, Context context, int appWidgetId) {
		// rv.setTextViewText(R.id.tvUpdate, "Concert Memo");

		/* обновление виджета */
		Intent updIntent = new Intent(context, MyProvider.class);
		updIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		updIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[] { appWidgetId });
		PendingIntent updPIntent = PendingIntent.getBroadcast(context, appWidgetId, updIntent, 0);
		rv.setOnClickPendingIntent(R.id.widget_imageview, updPIntent);

		{
			Intent intent = new Intent(context, MyProvider.class); // MyWidget
			intent.setAction(UPDATE_ALL_WIDGETS);
			PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			assert alarmManager != null;
			alarmManager.cancel(pIntent);
		}

		/* вызов главной активности - списка */
		Intent viewEventIntent = new Intent(context, Main_Activity.class);
		viewEventIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		viewEventIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		PendingIntent pIntent = PendingIntent.getActivity(context, appWidgetId, viewEventIntent, 0);
		rv.setOnClickPendingIntent(R.id.widget_imagetitle, pIntent);
		rv.setOnClickPendingIntent(R.id.widget_applicon, pIntent);
	}

	void setList(RemoteViews rv, Context context, int appWidgetId) {
		Intent adapter = new Intent(context, MyService.class);
		adapter.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		Uri data = Uri.parse(adapter.toUri(Intent.URI_INTENT_SCHEME));
		adapter.setData(data);
		rv.setRemoteAdapter(R.id.widget_lv_list, adapter);
	}

	void setListClick(RemoteViews rv, Context context) {
		Intent listClickIntent = new Intent(context, MyProvider.class);
		listClickIntent.setAction(ACTION_ON_CLICK);
		PendingIntent listClickPIntent = PendingIntent.getBroadcast(context, 0, listClickIntent, 0);
		rv.setPendingIntentTemplate(R.id.widget_lv_list, listClickPIntent);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			if (Objects.requireNonNull(intent.getAction()).equalsIgnoreCase(UPDATE_ALL_WIDGETS)) {
                ComponentName thisAppWidget = new ComponentName(context.getPackageName(), getClass().getName());
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
                for (int appWidgetID : ids) {
                    updateWidget(context, appWidgetManager, appWidgetID);
                }
            }
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			if (Objects.requireNonNull(intent.getAction()).equalsIgnoreCase(ACTION_ON_CLICK)) {
                int itemPos = intent.getIntExtra(ITEM_POSITION, -1);
                String itemMas_ID = intent.getStringExtra(ITEM_MAS_ID);

                if ((itemPos != -1) & (!itemMas_ID.equals("0"))) {
                    /* показ события */
                    int MAS_ID = Integer.parseInt(itemMas_ID);
                    Intent viewEventIntent = new Intent(context, View_Activity.class);
                    WMA.setMAS_ID(MAS_ID);
                    viewEventIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                    viewEventIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 1);
                    PendingIntent pIntent = PendingIntent.getActivity(context, 1, viewEventIntent, 0);
                    try {
                        pIntent.send();
                    } catch (CanceledException e) {
                        e.printStackTrace();
                    }
                }
            }
		}
	}

}