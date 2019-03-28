package ru.droidwelt.concertmemo;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressLint("StaticFieldLeak")
public class WMA extends Application {


    private static Context context;

    public static String UPDATE_ALL_WIDGETS = "ru.droidwelt.concertmemo.update_all_widgets";
    public static String S_UNLOADED = "";

    public static String DB_PATH = Environment.getExternalStorageDirectory().toString() + "/A_ConcertMemo/";
    public static String DB_DOWNLOAD = Environment.getExternalStorageDirectory().toString() + "/Download/";
    public static String DB_BLUETOOTH = Environment.getExternalStorageDirectory().toString() + "/Bluetooth/";

    public static String imageFilename_photo = "ConcertMemo_photo.jpg";
    public static String imageFilename_crop = "ConcertMemo_crop.jpg";
    public static String sendFilename = "ConcertMemo_send.jpg";
    public static String TYPE_CME = "CME_";
    public static String TYPE_CMDB = ".cmdb";
    public static String TYPE_CMDE = ".cmde";

    public static String DB_NAMEMODEL = "ConcertMemo_etal" + TYPE_CMDB;
    public static String DB_NAMEEXPORT = "ConcertMemo_export" + TYPE_CMDB;
    public static String DB_NAME = "ConcertMemo" + TYPE_CMDB;

    private static SQLiteDatabase database = null;

    private static Typeface type_header = null;

    private static int quality_big = 75;

    private static Boolean fileImportMode = false;
    private static Boolean mastChangeUser = false;
    private static String langprefix;
    private static long MAS_ID = 0;
    private static long DET_ID = 0;
    private static String MAS_SID = "";
    private static int DET_COUNT = 0;
    private static int DET_ACTION = 0;

    private static String myGoogleAccount = "";
    private static String myImportFileName = "";

    @SuppressLint("SimpleDateFormat")
    public static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        try {
            AccountManager am = AccountManager.get(this);
            Account[] accounts = am.getAccountsByType("com.google");
            myGoogleAccount = accounts[0].name;
            int x = myGoogleAccount.indexOf("@");
            if (x > 1) {
                myGoogleAccount = myGoogleAccount.substring(0, x);
            }
        } catch (Exception ignored) {
        }

        String land = getResources().getConfiguration().locale.getLanguage();
        Locale locale = new Locale(land);
        langprefix = locale.getLanguage();

        S_UNLOADED = getAppContext().getString(R.string.s_unloaded);

        setType_header(Typeface.createFromAsset(getAssets(), "fonts/Decker.ttf"));
    }

    public static void startDbHelper() {
        if (database == null) {
            DB_OpenHelper dbh = new DB_OpenHelper(context, DB_NAME);
            database = dbh.getDatabase();
        }
    }


    public static Context getAppContext() {
        return WMA.context;
    }


    public static String AddString(String s1, String s2) {
        s1 = strnormalize(s1);
        s2 = strnormalize(s2);
        String res = s1;

        if (!s2.equals("")) {
            if (!s1.equals("")) {
                res = res + ";";
            }
            res = res + s2;
        }
        return res;
    }

    public static byte[] concatArray(byte[] a, byte[] b) {
        if (a == null)
            return b;
        if (b == null)
            return a;
        byte[] r = new byte[a.length + b.length];
        System.arraycopy(a, 0, r, 0, a.length);
        System.arraycopy(b, 0, r, a.length, b.length);
        return r;
    }

    public static String strnormalize(String s) {
        try {
            assert s != null;
            if (s.isEmpty())
                return "";
        } catch (Exception e) {
            return "";
        }
        return s;
    }

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        WMA.context = context;
    }


    public static SQLiteDatabase getDatabase() {
        return database;
    }

    public static void setDatabase(SQLiteDatabase database) {
        WMA.database = database;
    }


    public static String getLangprefix() {
        return langprefix;
    }


    public static Date ConvertStrToDate(String str) {
        try {
            return formatter.parse(str);

        } catch (java.text.ParseException e) {
            Log.i("XXX", "formatter.parse(" + str + ")");
            return null;
        }
    }

    public static String ConvertDatePartsToStr(int Y, int M, int D, int H, int N) {
        String sm, sd, sh, sn;
        if (M < 10) {
            sm = "0" + Integer.toString(M);
        } else {
            sm = Integer.toString(M);
        }
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
        return Integer.toString(Y) + "-" + sm + "-" + sd + " " + sh + ":" + sn;
    }

    public static String ConvertDatePartsToStr_DT(int Y, int M, int D) {
        String sm, sd;
        if (M < 10) {
            sm = "0" + Integer.toString(M);
        } else {
            sm = Integer.toString(M);
        }
        if (D < 10) {
            sd = "0" + Integer.toString(D);
        } else {
            sd = Integer.toString(D);
        }
        return Integer.toString(Y) + "-" + sm + "-" + sd;
    }

    public static String ConvertDatePartsToStr_TM(int H, int N) {
        String sh, sn;

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
        return sh + ":" + sn;
    }

    @SuppressWarnings("deprecation")
    public static String ConvertDateToStr(Date date) {
        int Y = date.getYear() + 1900;
        int M = date.getMonth() + 1;
        int D = date.getDate();
        int H = date.getHours();
        int N = date.getMinutes();

        String sm, sd, sh, sn;
        if (M < 10) {
            sm = "0" + Integer.toString(M);
        } else {
            sm = Integer.toString(M);
        }

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
        return Integer.toString(Y) + "-" + sm + "-" + sd + " " + sh + ":" + sn;
    }

    @SuppressWarnings("deprecation")
    public static String ConvertDateToStr_Loc(Date date) {
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
        return sd + " " + sm + " " + Integer.toString(Y) + "   " + sh + ":" + sn;
    }

    @SuppressWarnings("deprecation")
    public static String ConvertDateToStr_LocDT(Date date) {
        int Y = date.getYear() + 1900;
        // int M = date.getMonth()+1;
        int D = date.getDate();

        String sm, sd;
        sm = String.format("%tB", date);

        if (D < 10) {
            sd = "0" + Integer.toString(D);
        } else {
            sd = Integer.toString(D);
        }

        return sd + " " + sm + " " + Integer.toString(Y);
    }


    public static String generateGUID(int strlength) {
        String template = "0123456789ABCDEFGHIJKLMNOPQRSTUWXYZ#$";
        StringBuilder s = new StringBuilder();
        Random rnd = new Random();
        for (int i = 1; i <= strlength; i = i + 1) {
            int k = rnd.nextInt(35);
            s.append(template.substring(k, k + 1));
        }
        return s.toString();
    }

    public static void deleteTempFile(String fn) {
        if (!fn.equals("")) {
            File file = new File(DB_PATH, fn);
            if (file.exists())
                file.delete();
        }
    }


    public static void deleteFileByName(String fn) {
        if (!fn.equals("")) {
            File file = new File(fn, "");
            if (file.exists()) {
                file.delete();
            }
        }
    }

    public static Bitmap getOneDetImageValue(long ID) {
        int i = 0;
        int rdbytes = 200000;
        Bitmap theImage = null;
        byte[] resall = null;
        int theImagePos = 0;
        try {
            while (rdbytes == 200000 & i < 100) {
                Cursor cursor = WMA.getDatabase().rawQuery(
                        "select substr(img,1+" + String.valueOf(i) + "*200000 ,200000) from det where _id = "
                                + String.valueOf(ID), null);
                i = i + 1;
                cursor.moveToFirst();
                byte[] res;

                rdbytes = cursor.getBlob(0).length;
                if (rdbytes > 0) {
                    res = cursor.getBlob(0);
                    resall = WMA.concatArray(resall, res);
                    theImagePos = theImagePos + res.length;
                }
                cursor.close();
            }
            assert resall != null;
            if (resall.length > 0) {
                theImage = BitmapFactory.decodeByteArray(resall, 0, resall.length); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            } else {
                theImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_empty_picture);
            }

        } catch (Exception ignored) {
        }
        return theImage;
    }

    public static Bitmap getOneDetPreviewValue(long ID) {
        Bitmap theImage;
        byte[] resall = null;

        String sql = " select preview from det where _ID='" + ID + "'";
        Cursor cursor = WMA.getDatabase().rawQuery(sql, null);
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
                theImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_empty_picture);
            }
        } catch (Exception e) {
            theImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_info);
        }

        return theImage;
    }

    public static void DisplayMark(String markCurrent, ImageButton btn_choice_mark) {
        btn_choice_mark.setImageResource(R.drawable.state_0);
        if (markCurrent != null) {
            if (markCurrent.equals("1"))
                btn_choice_mark.setImageResource(R.drawable.state_1);
            if (markCurrent.equals("2"))
                btn_choice_mark.setImageResource(R.drawable.state_2);
            if (markCurrent.equals("3"))
                btn_choice_mark.setImageResource(R.drawable.state_3);
            if (markCurrent.equals("4"))
                btn_choice_mark.setImageResource(R.drawable.state_4);
            if (markCurrent.equals("5"))
                btn_choice_mark.setImageResource(R.drawable.state_5);
            if (markCurrent.equals("6"))
                btn_choice_mark.setImageResource(R.drawable.state_6);
            if (markCurrent.equals("7"))
                btn_choice_mark.setImageResource(R.drawable.state_7);
            if (markCurrent.equals("8"))
                btn_choice_mark.setImageResource(R.drawable.state_8);
            if (markCurrent.equals("9"))
                btn_choice_mark.setImageResource(R.drawable.state_9);
            if (markCurrent.equals("10"))
                btn_choice_mark.setImageResource(R.drawable.state_10);
            if (markCurrent.equals("11"))
                btn_choice_mark.setImageResource(R.drawable.state_11);
            if (markCurrent.equals("12"))
                btn_choice_mark.setImageResource(R.drawable.state_12);
            if (markCurrent.equals("13"))
                btn_choice_mark.setImageResource(R.drawable.state_13);
            if (markCurrent.equals("14"))
                btn_choice_mark.setImageResource(R.drawable.state_14);
            if (markCurrent.equals("15"))
                btn_choice_mark.setImageResource(R.drawable.state_15);
            if (markCurrent.equals("16"))
                btn_choice_mark.setImageResource(R.drawable.state_16);
            if (markCurrent.equals("17"))
                btn_choice_mark.setImageResource(R.drawable.state_17);
            if (markCurrent.equals("18"))
                btn_choice_mark.setImageResource(R.drawable.state_18);
            if (markCurrent.equals("19"))
                btn_choice_mark.setImageResource(R.drawable.state_19);
            if (markCurrent.equals("20"))
                btn_choice_mark.setImageResource(R.drawable.state_20);
            if (markCurrent.equals("21"))
                btn_choice_mark.setImageResource(R.drawable.state_21);
            if (markCurrent.equals("22"))
                btn_choice_mark.setImageResource(R.drawable.state_22);
            if (markCurrent.equals("23"))
                btn_choice_mark.setImageResource(R.drawable.state_23);
        }
    }

    public static void DisplayMark_ImageView(String markCurrent, ImageView iv_choice_mark) {
        iv_choice_mark.setImageResource(R.drawable.state_0);
        if (markCurrent != null) {
            if (markCurrent.equals("1"))
                iv_choice_mark.setImageResource(R.drawable.state_1);
            if (markCurrent.equals("2"))
                iv_choice_mark.setImageResource(R.drawable.state_2);
            if (markCurrent.equals("3"))
                iv_choice_mark.setImageResource(R.drawable.state_3);
            if (markCurrent.equals("4"))
                iv_choice_mark.setImageResource(R.drawable.state_4);
            if (markCurrent.equals("5"))
                iv_choice_mark.setImageResource(R.drawable.state_5);
            if (markCurrent.equals("6"))
                iv_choice_mark.setImageResource(R.drawable.state_6);
            if (markCurrent.equals("7"))
                iv_choice_mark.setImageResource(R.drawable.state_7);
            if (markCurrent.equals("8"))
                iv_choice_mark.setImageResource(R.drawable.state_8);
            if (markCurrent.equals("9"))
                iv_choice_mark.setImageResource(R.drawable.state_9);
            if (markCurrent.equals("10"))
                iv_choice_mark.setImageResource(R.drawable.state_10);
            if (markCurrent.equals("11"))
                iv_choice_mark.setImageResource(R.drawable.state_11);
            if (markCurrent.equals("12"))
                iv_choice_mark.setImageResource(R.drawable.state_12);
            if (markCurrent.equals("13"))
                iv_choice_mark.setImageResource(R.drawable.state_13);
            if (markCurrent.equals("14"))
                iv_choice_mark.setImageResource(R.drawable.state_14);
            if (markCurrent.equals("15"))
                iv_choice_mark.setImageResource(R.drawable.state_15);
            if (markCurrent.equals("16"))
                iv_choice_mark.setImageResource(R.drawable.state_16);
            if (markCurrent.equals("17"))
                iv_choice_mark.setImageResource(R.drawable.state_17);
            if (markCurrent.equals("18"))
                iv_choice_mark.setImageResource(R.drawable.state_18);
            if (markCurrent.equals("19"))
                iv_choice_mark.setImageResource(R.drawable.state_19);
            if (markCurrent.equals("20"))
                iv_choice_mark.setImageResource(R.drawable.state_20);
            if (markCurrent.equals("21"))
                iv_choice_mark.setImageResource(R.drawable.state_21);
            if (markCurrent.equals("22"))
                iv_choice_mark.setImageResource(R.drawable.state_22);
            if (markCurrent.equals("23"))
                iv_choice_mark.setImageResource(R.drawable.state_23);
        }
    }

    public static long getMAS_ID() {
        return MAS_ID;
    }

    public static void setMAS_ID(long _MAS_ID) {
        MAS_ID = _MAS_ID;
    }

    public static String getMAS_SID() {
        return MAS_SID;
    }

    public static void setMAS_SID(String mAS_SID) {
        MAS_SID = mAS_SID;
    }

    public static long getDET_ID() {
        return DET_ID;
    }

    public static void setDET_ID(long dET_ID) {
        DET_ID = dET_ID;
    }

    public static int getDET_COUNT() {
        return DET_COUNT;
    }

    public static void setDET_COUNT(int dET_COUNT) {
        DET_COUNT = dET_COUNT;
    }

    public static int getQuality_big() {
        return quality_big;
    }


    public static void makeMainIconAutomaticByMAS() {
        if (getDET_COUNT() == 1) {
            makeMainThumbnail(getFirstDetByMas());
        }
    }

    public static void makeMainThumbnail(long _DET_ID) {
        int _MAS_ID = (int) WMA.getMAS_ID();
        if ((_DET_ID > 0) & (_MAS_ID > 0)) {
            double kmax = 0.0;
            int bm_w;
            int bm_h;
            Bitmap bm = WMA.getOneDetImageValue(_DET_ID);
            if (bm != null) {

                ByteArrayOutputStream streamSmall = new ByteArrayOutputStream();
                bm_h = bm.getHeight();
                bm_w = bm.getWidth();
                if (bm_w > 0) {
                    int picturesmall_size = 240;
                    kmax = (double) bm_w / (double) picturesmall_size;
                }
                double dx = (double) bm_w / kmax;
                double dy = (double) bm_h / kmax;
                int x = (int) dx;
                int y = (int) dy;

                Bitmap imageSmall = Bitmap.createScaledBitmap(bm, x, y, false);
                int quality_small = 75;
                imageSmall.compress(Bitmap.CompressFormat.JPEG, quality_small, streamSmall);
                byte[] imageInByteSmall = streamSmall.toByteArray();

                ContentValues editRecord = new ContentValues();
                editRecord.put("pict", imageInByteSmall);
                editRecord.put("masmid", WMA.generateGUID(20));
                WMA.getDatabase().update("mas", editRecord, "_id=" + _MAS_ID, null);
            }
        }
    }

    public static void uploadDetPicture(long _DET_ID) {
        int _MAS_ID = (int) WMA.getMAS_ID();
        if ((_DET_ID > 0) & (_MAS_ID > 0)) {
            Bitmap bm = WMA.getOneDetImageValue(_DET_ID);
            if (bm != null) {
                String fn = "ConcertMemo_" + _MAS_ID + "_" + _DET_ID + ".jpg";
                File file = new File(DB_DOWNLOAD, fn);
                FileOutputStream fOut;
                try {
                    fOut = new FileOutputStream(file);
                    bm.compress(Bitmap.CompressFormat.JPEG, quality_big, fOut);
                    fOut.flush();
                    fOut.close();
                    Toast.makeText(context, DB_DOWNLOAD + fn, Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static long getFirstDetByMas() {
        Cursor c = WMA.getDatabase().rawQuery("select min (_id) as ID  from det where mid = " + getMAS_ID(), null);
        c.moveToFirst();
        long id = c.getLong(0);
        c.close();
        return id;
    }

    public static int getDET_ACTION() {
        return DET_ACTION;
    }

    public static void setDET_ACTION(int dET_ACTION) {
        DET_ACTION = dET_ACTION;
    }

    public static int getPicturepreview_size() {
        return 600;
    }


    public static int getQuality_preview() {
        return 65;
    }


    public static String getMyGoogleAccount() {
        return myGoogleAccount;
    }


    @SuppressLint("DefaultLocale")
    public static String generValidFileName(String s_in) {
        StringBuilder s_out = new StringBuilder(TYPE_CME);
        try {
            assert s_in != null;
            if (!s_in.isEmpty()) {
                String expression = "[ #~+=?0123456789QWERTYUIOPASDFGHJKLZXCVBNM_-ЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮЁ]";
                Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
                int l = s_in.length();
                for (int i = 0; i < l; i = i + 1) {
                    String s = s_in.substring(i, i + 1).toUpperCase();
                    Matcher matcher = pattern.matcher(s);
                    if (matcher.matches()) {
                        s_out.append(s);
                    } else {
                        s_out.append("_");
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return s_out + TYPE_CMDE;
    }

    public static long getMasIdBySid(String SID) {
        String _s = "";
        long id = 0;
        try {
            assert SID != null;
            if (!SID.equals(""))
                _s = SID;
        } catch (Exception ignored) {
        }
        Cursor c = database.rawQuery("select min (_id) as ID  from mas where massid = '" + _s + "'", null);
        c.moveToFirst();
        if (c.getCount() > 0) {
            id = c.getLong(0);
        }
        c.close();
        return id;
    }


    public static void deleteRecordByMasDetByMid(long MID) {
        if (MID != 0) {
            database.delete("det", "mid=" + MID, null);
            database.delete("mas", "_id=" + MID, null);
        }
    }

    public static String getMyImportFileName() {
        return myImportFileName;
    }

    public static void setMyImportFileName(String myImportFileName) {
        WMA.myImportFileName = myImportFileName;
    }

    public static Boolean getFileImportMode() {
        return fileImportMode;
    }

    public static void setFileImportMode(Boolean fileImportMode) {
        WMA.fileImportMode = fileImportMode;
    }

    public static Boolean getMastChangeUser() {
        return mastChangeUser;
    }

    public static void setMastChangeUser(Boolean mastChangeUser) {
        WMA.mastChangeUser = mastChangeUser;
    }

    public static void updateSidInDetail(long id, String sid) {
        ContentValues dtr = new ContentValues();
        dtr.put("massid", sid);
        getDatabase().update("det", dtr, "mid=" + id, null);
    }

    public static void updateMidInMaster(long id, String mid) {
        ContentValues dtr = new ContentValues();
        dtr.put("masmid", mid);
        getDatabase().update("mas", dtr, "_id=" + id, null);
    }

    public static void myWidgetAlarm(Context context) {
        /* оповещение виджетов */
        Intent intent = new Intent(context, MyProvider.class); // MyWidget
        intent.setAction(WMA.UPDATE_ALL_WIDGETS);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis(), pIntent);
    }

    public static void showToast(String phrase) {
        Toast.makeText(context, phrase, Toast.LENGTH_SHORT).show();
    }

    public static int getMaxOrdDet() {
        Cursor c = WMA.getDatabase().rawQuery("select max (ord) as ord  from det where mid = " + getMAS_ID(), null);
        c.moveToFirst();
        int id;
        try {
            id = c.getInt(0);
        } catch (Exception e) {
            id = 0;
        }
        c.close();
        return id;
    }

    public static Typeface getType_header() {
        return type_header;
    }

    public static void setType_header(Typeface type_header) {
        WMA.type_header = type_header;
    }

    public static void setHeaderFont(Activity act) {
        int actionBarTitle = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        TextView actionBarTitleView = act.getWindow().findViewById(actionBarTitle);
        if (actionBarTitleView != null)
            actionBarTitleView.setTypeface(WMA.getType_header());
    }

    public static void animateStart(Activity a) {
        switch (getAnimation_mode()) {

            case 1:
                a.overridePendingTransition(R.anim.activity_down_up_enter, R.anim.activity_down_up_exit);
                break;

            case 2:
                a.overridePendingTransition(R.anim.activity_up_down_enter, R.anim.activity_up_down_exit);
                break;

            case 3:
                a.overridePendingTransition(R.anim.activity_left_rigth_enter, R.anim.activity_left_rigth_exit);
                break;

            case 4:
                a.overridePendingTransition(R.anim.activity_rigth_left_enter, R.anim.activity_rigth_left_exit);
                break;

            default:
                break;
        }
    }

    public static void animateFinish(Activity a) {

        switch (getAnimation_mode()) {

            case 1:
                a.overridePendingTransition(R.anim.activity_down_up_close_enter, R.anim.activity_down_up_close_exit);
                break;

            case 2:
                a.overridePendingTransition(R.anim.activity_up_down_close_enter, R.anim.activity_up_down_close_exit);
                break;

            case 3:
                a.overridePendingTransition(R.anim.activity_left_rigth_close_enter, R.anim.activity_left_rigth_close_exit);
                break;

            case 4:
                a.overridePendingTransition(R.anim.activity_rigth_left_close_enter, R.anim.activity_rigth_left_close_exit);
                break;

            default:
                break;
        }
    }

    public static int getAnimation_mode() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getAppContext());
        String picture_size_val = sp.getString("animation_mode", "0");
        return Integer.parseInt(picture_size_val);
    }

    public static boolean getchoice_allfield() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getAppContext());
        return sp.getBoolean("choice_allfield", false);
    }

    public static Boolean getchoice_quickstart() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getAppContext());
        return sp.getBoolean("choice_quickstart", false);
    }

    public static Boolean getchoice_deleteevent() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getAppContext());
        return sp.getBoolean("choice_deleteevent", false);
    }


    public static int getPicture_size() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getAppContext());
        String picture_size_val = sp.getString("picture_size", "0");
        return Integer.parseInt(picture_size_val);
    }

}
