package ru.droidwelt.concertmemo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.format.DateFormat;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

class MyFactory implements RemoteViewsService.RemoteViewsFactory {

    private static int ListNRead = 0;
    private ArrayList<String> data_s1;
    private ArrayList<String> data_s2;
    private ArrayList<String> data_id;
    private ArrayList<String> data_m;
    private ArrayList<byte[]> data_pict;
    Context context;

    public String generateRnd() {
        String template = "0123456789ABCDEFGHIJKLMNOPQRSTUWXYZ#$";
        StringBuilder s = new StringBuilder();
        Random rnd = new Random();
        for (int i = 1; i <= 16; i = i + 1) {
            int k = rnd.nextInt(35);
            s.append(template.substring(k, k + 1));
        }
        return s.toString();
    }

    MyFactory(Context ctx, Intent intent) {
        context = ctx;
        intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        data_s1 = new ArrayList<>();
        data_s2 = new ArrayList<>();
        data_id = new ArrayList<>();
        data_m = new ArrayList<>();
        data_pict = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return data_s1.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    private int GetRIDByMark(String markCurrent) {
        int res = R.drawable.state_0;
        if (markCurrent != null) {
            if (markCurrent.equals("1"))
                res = R.drawable.state_1;
            if (markCurrent.equals("2"))
                res = R.drawable.state_2;
            if (markCurrent.equals("3"))
                res = R.drawable.state_3;
            if (markCurrent.equals("4"))
                res = R.drawable.state_4;
            if (markCurrent.equals("5"))
                res = R.drawable.state_5;
            if (markCurrent.equals("6"))
                res = R.drawable.state_6;
            if (markCurrent.equals("7"))
                res = R.drawable.state_7;
            if (markCurrent.equals("8"))
                res = R.drawable.state_8;
            if (markCurrent.equals("9"))
                res = R.drawable.state_9;
            if (markCurrent.equals("10"))
                res = R.drawable.state_10;
            if (markCurrent.equals("11"))
                res = R.drawable.state_11;
            if (markCurrent.equals("12"))
                res = R.drawable.state_12;
            if (markCurrent.equals("13"))
                res = R.drawable.state_13;
            if (markCurrent.equals("14"))
                res = R.drawable.state_14;
            if (markCurrent.equals("15"))
                res = R.drawable.state_15;
            if (markCurrent.equals("16"))
                res = R.drawable.state_16;
            if (markCurrent.equals("17"))
                res = R.drawable.state_17;
            if (markCurrent.equals("18"))
                res = R.drawable.state_18;
            if (markCurrent.equals("19"))
                res = R.drawable.state_19;
            if (markCurrent.equals("20"))
                res = R.drawable.state_20;
            if (markCurrent.equals("21"))
                res = R.drawable.state_21;
            if (markCurrent.equals("22"))
                res = R.drawable.state_22;
            if (markCurrent.equals("23"))
                res = R.drawable.state_23;
        }
        return res;
    }


    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rView = new RemoteViews(context.getPackageName(), R.layout.widget_item);

        rView.setTextViewText(R.id.widget_item_tv1, data_s1.get(position));
        rView.setTextViewText(R.id.widget_item_tv2, data_s2.get(position));
        rView.setImageViewResource(R.id.widget_item_mark, GetRIDByMark(data_m.get(position)));
        if (data_pict.get(position) != null) {
            Bitmap bm = BitmapFactory.decodeByteArray(data_pict.get(position), 0, data_pict.get(position).length);
            rView.setImageViewBitmap(R.id.widget_item_pict, bm);
        } else {
            rView.setImageViewResource(R.id.widget_item_pict, R.drawable.ic_empty_picture);
        }

        Intent clickIntent = new Intent();
        clickIntent.putExtra(MyProvider.ITEM_POSITION, position);
        clickIntent.putExtra(MyProvider.ITEM_MAS_ID, data_id.get(position));
        rView.setOnClickFillInIntent(R.id.widget_item_tv1, clickIntent);
        rView.setOnClickFillInIntent(R.id.widget_item_tv2, clickIntent);
        rView.setOnClickFillInIntent(R.id.widget_item_mark, clickIntent);
        rView.setOnClickFillInIntent(R.id.widget_item_pict, clickIntent);

        return rView;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private static String strnorma(String s) {
        try {
            if ((s == null) | (s != null && s.isEmpty()))
                return "";
        } catch (Exception e) {
            return "";
        }
        return s;
    }

    private String addstring(String s1, String s2) {
        s1 = strnorma(s1);
        s2 = strnorma(s2);
        String res = s1;

        if (!s2.equals("")) {
            if (!s1.equals("")) {
                res = res + "; ";
            }
            res = res + s2;
        }
        return res;
    }

    @SuppressWarnings("deprecation")
    private static String convertdatetostr_local(Date date) {
        int Y = date.getYear() + 1900;
        // int M = date.getMonth()+1;
        int D = date.getDate();
        int H = date.getHours();
        int N = date.getMinutes();

        String sm, sd, sh, sn;
        sm = String.format("%tB", date);

        if (D < 10) {
            sd = "0" + Integer.toString(D);
        } else {
            sd = Integer.toString(D);
        }
        if (H == 0) {
            sh = "00";
        } else {
            if (H < 10) {
                sh = "0" + Integer.toString(H);
            } else {
                sh = Integer.toString(H);
            }
        }
        if (N == 0) {
            sn = "00";
        } else {
            if (N < 10) {
                sn = "0" + Integer.toString(N);
            } else {
                sn = Integer.toString(N);
            }
        }
        return sd + " " + sm + " " + Integer.toString(Y) + " " + sh + ":" + sn;
    }

    @SuppressLint("SimpleDateFormat")
    private static Date convertstrtodate(String str) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        try {
            return formatter.parse(str);
        } catch (java.text.ParseException e) {
            // Log.i("XXX", "formatter.parse(" + str + ")");
            return null;
        }
    }

    @Override
    public void onDataSetChanged() {
        ListNRead = ListNRead + 1;
        data_s1.clear();
        data_s2.clear();
        data_id.clear();
        data_m.clear();
        data_pict.clear();

        String DBN = Environment.getExternalStorageDirectory().toString() + "/A_ConcertMemo/ConcertMemo_etal.cmdb";
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DBN, null, SQLiteDatabase.OPEN_READONLY);
        String sfilter = " and datebeg>='" + DateFormat.format("yyyy-MM-dd", new Date()) + "' ";
        @SuppressLint("Recycle") Cursor c = db.rawQuery("select _id,name,event,club,album,mark,state,datebeg,pict from mas where tp='0'"
                + sfilter + " order by datebeg ", null);
        c.moveToFirst();
        int ind_id = c.getColumnIndex("_id");
        int ind_name = c.getColumnIndex("name");
        int ind_event = c.getColumnIndex("event");
        int ind_club = c.getColumnIndex("club");
        int ind_album = c.getColumnIndex("album");
        int ind_state = c.getColumnIndex("state");
        int ind_mark = c.getColumnIndex("mark");
        int ind_datebeg = c.getColumnIndex("datebeg");
        int ind_pict = c.getColumnIndex("pict");

        String _s;

        while (!c.isAfterLast()) {
            int _id = c.getInt(ind_id);
            _s = Integer.toString(_id);
            data_id.add(_s);

            Date sd = convertstrtodate(c.getString(ind_datebeg));
            assert sd != null;
            _s = convertdatetostr_local(sd);
            data_s1.add(_s);

            _s = strnorma(c.getString(ind_name));
            _s = addstring(_s, strnorma(c.getString(ind_event)));
            _s = addstring(_s, strnorma(c.getString(ind_album)));
            _s = addstring(_s, strnorma(c.getString(ind_club)));
            _s = addstring(_s, strnorma(c.getString(ind_state)));
            data_s2.add(_s);
            data_m.add(strnorma(c.getString(ind_mark)));
            data_pict.add(c.getBlob(ind_pict));
            c.moveToNext();
        }
        db.close();
    }

    @Override
    public void onDestroy() {

    }

}
