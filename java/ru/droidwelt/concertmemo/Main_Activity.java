package ru.droidwelt.concertmemo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ru.droidwelt.concertmemo.DB_BackupFile.DB_BackupFile_Listener;
import ru.droidwelt.concertmemo.DB_RestoreFile.DB_RestoreFile_Listener;
import ru.droidwelt.concertmemo.DB_RestoreProc.DB_RestoreProc_Listener;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

@SuppressLint("StaticFieldLeak")

public class Main_Activity extends ListActivity implements DB_BackupFile_Listener, DB_RestoreProc_Listener,
        DB_RestoreFile_Listener, LoaderCallbacks<Cursor> {

    static final int MAS_MODIFIED = 201;
    static final int MAS_INSERTED = 202;
    static final int MAS_EDIT = 301;
    static final int MAS_IMPORT = 305;
    static final int PREF_EXIT = 323;

    private static DB_RestoreProc dlg_ResroreProc;
    private static DB_BackupFile dlg_BackupFile;
    private static ListView mas_ListView;
    private static DB_ImageCursorAdapter masAdapter;

    private boolean inProgress = false;

    private AlertDialog.Builder adb_order;
    AlertDialog.Builder adb_import;
    private String[] sort_list_field;
    private String[] sort_list_name;
    private static String sort_field = "";
    private static boolean viewall_mode = false;
    private static String s_viewall = "";
    private static String fn_import = "";
    private List<String> filenames_display = new ArrayList<>();

    // ----------------------------------------------------------------------

    public class DB_ImageCursorAdapter extends SimpleCursorAdapter {

        private Context context;

        @SuppressWarnings("deprecation")
        DB_ImageCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
            super(context, layout, c, from, to);
            this.context = context;
        }

        byte[] getOneMasImageValue(long id) {
            String sql = " select pict from mas where _id='" + id + "'";
            Cursor cursor = WMA.getDatabase().rawQuery(sql, null);
            cursor.moveToFirst();
            byte[] res = null;
            try {
                res = cursor.getBlob(0);
                cursor.close();
            } catch (Exception ignored) {
            } finally {
                cursor.close();
            }
            return res;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int pos, View inView, ViewGroup parent) {
            View v = super.getView(pos, inView, parent);

            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                assert inflater != null;
                v = inflater.inflate(R.layout.main_activity, null);
            }

            int id = Main_Activity.get_MasAdapterd_ID(pos);

            TextView tvn = v.findViewById(R.id.item_name);
            if (id == WMA.getMAS_ID()) {
                tvn.setBackgroundColor(getResources().getColor(R.color.c_selected));
                tvn.setTextColor(getResources().getColor(R.color.c_bgr_text));
            } else {
                tvn.setBackgroundColor(getResources().getColor(R.color.c_main_name));
                tvn.setTextColor(getResources().getColor(R.color.c_black));
            }

            ImageView iv = v.findViewById(R.id.item_graphic);
            byte[] favicon = getOneMasImageValue(id);

            if (favicon != null) {
                Bitmap theImage = BitmapFactory.decodeByteArray(favicon, 0, favicon.length);
                iv.setImageBitmap(theImage);
            } else {
                iv.setImageResource(R.drawable.ic_empty_picture);
            }

            int index;
            String s;

            s = "";
            TextView tv1 = v.findViewById(R.id.item_event);
            index = Main_Activity.getMasAdapter().getCursor().getColumnIndex("event");
            s = WMA.AddString(s, Main_Activity.getMasAdapter().getCursor().getString(index));
            tv1.setText(s);

            s = "";
            TextView tv2 = v.findViewById(R.id.item_club);
            index = Main_Activity.getMasAdapter().getCursor().getColumnIndex("album");
            s = WMA.AddString(s, Main_Activity.getMasAdapter().getCursor().getString(index));
            index = Main_Activity.getMasAdapter().getCursor().getColumnIndex("club");
            s = WMA.AddString(s, Main_Activity.getMasAdapter().getCursor().getString(index));
            index = Main_Activity.getMasAdapter().getCursor().getColumnIndex("genre");
            s = WMA.AddString(s, Main_Activity.getMasAdapter().getCursor().getString(index));
            tv2.setText(s);

            s = "";
            TextView tv3 = v.findViewById(R.id.item_datebeg);
            index = Main_Activity.getMasAdapter().getCursor().getColumnIndex("datebeg");
            Date date = WMA.ConvertStrToDate(Main_Activity.getMasAdapter().getCursor().getString(index));
            assert date != null;
            s = WMA.ConvertDateToStr_Loc(date);
            tv3.setText(s);

            s = "";
            TextView tv4 = v.findViewById(R.id.item_state);
            index = Main_Activity.getMasAdapter().getCursor().getColumnIndex("state");
            s = WMA.AddString(s, Main_Activity.getMasAdapter().getCursor().getString(index));
            tv4.setText(s);

            ImageView irating = v.findViewById(R.id.item_mark);
            index = Main_Activity.getMasAdapter().getCursor().getColumnIndex("mark");
            String rat = Main_Activity.getMasAdapter().getCursor().getString(index);

            irating.setImageResource(R.drawable.state_0);
            if (rat != null) {
                if (rat.equals("1"))
                    irating.setImageResource(R.drawable.state_1);
                if (rat.equals("2"))
                    irating.setImageResource(R.drawable.state_2);
                if (rat.equals("3"))
                    irating.setImageResource(R.drawable.state_3);
                if (rat.equals("4"))
                    irating.setImageResource(R.drawable.state_4);
                if (rat.equals("5"))
                    irating.setImageResource(R.drawable.state_5);
                if (rat.equals("6"))
                    irating.setImageResource(R.drawable.state_6);
                if (rat.equals("7"))
                    irating.setImageResource(R.drawable.state_7);
                if (rat.equals("8"))
                    irating.setImageResource(R.drawable.state_8);
                if (rat.equals("9"))
                    irating.setImageResource(R.drawable.state_9);
                if (rat.equals("10"))
                    irating.setImageResource(R.drawable.state_10);
                if (rat.equals("11"))
                    irating.setImageResource(R.drawable.state_11);
                if (rat.equals("12"))
                    irating.setImageResource(R.drawable.state_12);
                if (rat.equals("13"))
                    irating.setImageResource(R.drawable.state_13);
                if (rat.equals("14"))
                    irating.setImageResource(R.drawable.state_14);
                if (rat.equals("15"))
                    irating.setImageResource(R.drawable.state_15);
                if (rat.equals("16"))
                    irating.setImageResource(R.drawable.state_16);
                if (rat.equals("17"))
                    irating.setImageResource(R.drawable.state_17);
                if (rat.equals("18"))
                    irating.setImageResource(R.drawable.state_18);
                if (rat.equals("19"))
                    irating.setImageResource(R.drawable.state_19);
                if (rat.equals("20"))
                    irating.setImageResource(R.drawable.state_20);
                if (rat.equals("21"))
                    irating.setImageResource(R.drawable.state_21);
                if (rat.equals("22"))
                    irating.setImageResource(R.drawable.state_22);
                if (rat.equals("23"))
                    irating.setImageResource(R.drawable.state_23);
            }
            return (v);
        }
    }

    public static void setViewAllString() {
        if (viewall_mode) {
            s_viewall = "";
        } else {
            s_viewall = " and datebeg>='" + DateFormat.format("yyyy-MM-dd", new Date()) + "' ";
        }
    }

    // -----------------------------------------------------------------------

    public static int get_MasAdapterd_ID(int position) {
        return (int) getMasAdapter().getItemId(position);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.main_activity);
        setViewAllString();
        WMA.setHeaderFont(this);

        String[] from = new String[]{"name"};
        int[] to = new int[]{R.id.item_name};

        setContactAdapter(new DB_ImageCursorAdapter(this, R.layout.main_item, null, from, to));
        mas_ListView = this.getListView();
        mas_ListView.setOnItemClickListener(masListener);
        mas_ListView.setOnItemLongClickListener(masLoggListener);
        setListAdapter(getMasAdapter());
        getLoaderManager().initLoader(0, null, this);

        adb_order = new AlertDialog.Builder(this);
        adb_import = new AlertDialog.Builder(this);
    }

    // -------------------------------------------------------------------------------------------------------------------------
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MyCursorLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
        getMasAdapter().swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
    }

    static class MyCursorLoader extends CursorLoader {
        MyCursorLoader(Context context) {
            super(context);
        }

        @Override
        public Cursor loadInBackground() {
            return getAllMasRecords("");
        }
    }

    @SuppressLint("DefaultLocale")
    public static Cursor getAllMasRecords(String filter) {
        assert sort_field != null;
        if (sort_field.equals(""))
            sort_field = "datebeg";

        if (!sort_field.equals("datebeg"))
            sort_field = sort_field + ",datebeg";

        String sqlitefilter = "'%" + filter.toUpperCase().trim() + "%'";
        String search_field = "nameup";
        if (WMA.getchoice_allfield())
            search_field = "searchup";

        String sql = "SELECT _id,name,genre,event,city,club,state,mark,album,datebeg,massid,masmid from mas  "
                + " where tp='0' " + s_viewall + " and " + search_field + " like " + sqlitefilter + " order by "
                + sort_field;

        // Log.i("SQL getFilterdMasRecords", sql);
        return WMA.getDatabase().rawQuery(sql, null);
    }

    // -------------------------------------------------------------------------------------------------------------------------
    protected void ResreshData(long id_to_find) {
        getMasAdapter().changeCursor(getAllMasRecords(""));
        int n = 0;
        while (n <= getMasAdapter().getCount() - 1) {
            if (getMasAdapter().getItemId(n) == id_to_find) {
                break;
            }
            n = n + 1;
        }
        mas_ListView.invalidate();
        mas_ListView.smoothScrollToPosition(n);
    }

    // -------------------------------------------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        MenuItem mi = menu.findItem(R.id.main_viewall);
        mi.setChecked(viewall_mode);

        final SearchView searchView = (SearchView) menu.findItem(R.id.main_search).getActionView();

        sort_list_field = getResources().getStringArray(R.array.sort_list_field);
        sort_list_name = getResources().getStringArray(R.array.sort_list_name);
        if (sort_field.equals(""))
            sort_field = sort_list_field[0];

        searchView.setOnQueryTextListener(new OnQueryTextListener() {
            @Override
            // при нажатии на поиск на клавиатуре
            public boolean onQueryTextSubmit(String query) {
                onQueryTextChange(searchView.getQuery().toString());
                return true;
            }

            @Override
            // при изменении текста запроса
            public boolean onQueryTextChange(String newText) {
                try {
                    if (newText == null)
                        newText = "";
                } catch (Exception e) {
                    newText = "";
                }

                getMasAdapter().changeCursor(getAllMasRecords(newText));
                return false;
            }
        });
        return true;
    }

    // выбор из меню
    // ---------------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (!inProgress) {

            // Поиск
            if (item.getItemId() == R.id.main_search) {
                return true;
            }

            // Настройки
            if (item.getItemId() == R.id.main_pref) {
                Intent intent = new Intent(Main_Activity.this, Pref_Activity.class);
                startActivityForResult(intent, PREF_EXIT);
                WMA.animateStart(Main_Activity.this);
                return true;
            }

            // Импорт события
            if (item.getItemId() == R.id.main_import) {
                importEvent();
                return true;
            }

            // О программе
            if (item.getItemId() == R.id.main_about) {
                Intent intent = new Intent(this, About_Activity.class);
                startActivity(intent);
                return true;
            }

            // Сортировка
            if (item.getItemId() == R.id.main_order) {

                adb_order.setTitle(R.string.s_order);
                adb_order.setCancelable(true);
                adb_order.setItems(sort_list_name, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        String sf = sort_list_field[item];
                        if (!sf.equals(sort_field)) {
                            sort_field = sf;
                            getMasAdapter().changeCursor(getAllMasRecords(""));
                        }
                    }
                });
                adb_order.create();
                adb_order.show();
                return true;
            }

            // Справка
            if (item.getItemId() == R.id.main_help) {
                Intent intentViewHelp = new Intent(Main_Activity.this, Help_Activity.class);
                startActivity(intentViewHelp);
                WMA.animateStart(Main_Activity.this);
                return true;
            }

            // режим показа
            if (item.getItemId() == R.id.main_viewall) {
                item.setChecked(!item.isChecked());
                viewall_mode = item.isChecked();
                setViewAllString();
                // ResreshData(0);
                getMasAdapter().changeCursor(getAllMasRecords(""));
                return true;
            }

            // добавление новой записи
            if (item.getItemId() == R.id.main_add) {
                WMA.setMAS_ID(0);
                Intent addNewContact = new Intent(Main_Activity.this, Edit_Activity.class);
                startActivityForResult(addNewContact, MAS_INSERTED);
                WMA.animateStart(Main_Activity.this);
                return true;
            }

            // Выгрузить базу
            if (item.getItemId() == R.id.main_backupDB) {
                android.app.FragmentManager fm_BackupFile = getFragmentManager();
                setDlg_BackupFile(new DB_BackupFile());
                getDlg_BackupFile().setCancelable(false);
                getDlg_BackupFile().show(fm_BackupFile, "");
                return true;
            }

            // Загрузить базу
            if (item.getItemId() == R.id.main_restoreDB) {
                android.app.FragmentManager fm_RestoreFile = getFragmentManager();
                DB_RestoreFile dlg_ResroreFile = new DB_RestoreFile();
                dlg_ResroreFile.setCancelable(false);
                dlg_ResroreFile.show(fm_RestoreFile, "");
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // ------------------------------------------------------------------------------------------
    // слушатель событий в ListView
    OnItemClickListener masListener = new OnItemClickListener() {
        // посмотр записи, из него редактирование или удаление записи
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            if (!inProgress) {
                Intent viewRecord = new Intent(Main_Activity.this, View_Activity.class);
                WMA.setMAS_ID(id);
                startActivityForResult(viewRecord, MAS_MODIFIED);
                WMA.animateStart(Main_Activity.this);
            }
        }
    };

    OnItemLongClickListener masLoggListener = new OnItemLongClickListener() {
        // длинный клик, удаление записи
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
            if (!inProgress) {
                deleteMasRecord(id);
            }
            return true;
        }
    };

    private void deleteMasRecord(final long id_Delete) {
        final Timer timer = new Timer();
        AlertDialog.Builder builder = new AlertDialog.Builder(Main_Activity.this);
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
                        WMA.getDatabase().delete("det", "mid=" + params[0], null);
                        WMA.getDatabase().delete("mas", "_id=" + params[0], null);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object result) {
                        masTableRefresh();
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

    // -------------------------------------------------------------------------------------------------------------------------
    public void masTableRefresh() {
        getMasAdapter().changeCursor(getAllMasRecords(""));
    }

    // -------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case MAS_MODIFIED: // из экрана View
                    ResreshData(WMA.getMAS_ID());
                    break;

                case MAS_EDIT: // из экрана Edit
                    ResreshData(WMA.getMAS_ID());
                    break;

                case MAS_INSERTED: // из экрана Edit
                    ResreshData(WMA.getMAS_ID());

                case MAS_IMPORT: // из экрана Import
                    ResreshData(WMA.getMAS_ID());
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        final Timer timer = new Timer();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.s_exit_appl);
        //	builder.setMessage(R.string.s_exit_appl);

        builder.setNegativeButton(R.string.s_no, new OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                timer.purge();
                timer.cancel();
            }
        });

        builder.setPositiveButton(R.string.s_yes, new OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                timer.purge();
                timer.cancel();
                Main_Activity.super.onBackPressed();
                WMA.animateFinish(Main_Activity.this);
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

    public static DB_ImageCursorAdapter getMasAdapter() {
        return masAdapter;
    }

    public static void setContactAdapter(DB_ImageCursorAdapter _masAdapter) {
        Main_Activity.masAdapter = _masAdapter;
    }

    public static DB_RestoreProc getDlg_ResroreProc() {
        return dlg_ResroreProc;
    }

    public static void setDlg_ResroreProc(DB_RestoreProc dlg_ResroreProc) {
        Main_Activity.dlg_ResroreProc = dlg_ResroreProc;
    }

    public static DB_BackupFile getDlg_BackupFile() {
        return dlg_BackupFile;
    }

    public static void setDlg_BackupFile(DB_BackupFile dlg_BackupFile) {
        Main_Activity.dlg_BackupFile = dlg_BackupFile;
    }

    // -------------------------------------------------------------------------------------------------------------------------
    @Override
    public void DB_BackupFile_onFinishEditDialog(String inputText) {
        inputText.length();
    }

    // ----------------- завершение процесса восстановления базы
    @Override
    public void DB_RestoreProc_onFinishEditDialog(String inputText) {
        masTableRefresh();
        WMA.myWidgetAlarm(Main_Activity.this);
        // Log.i("XXX", "Main_activity - masTableRefresh()");
    }

    @Override
    public void DB_RestoreFile_onFinishEditDialog(String inputText) {
        inputText.length();
    }

    // ---------------------------------------------------------------------------------------------------------------------------
    public void importEvent() {
        filenames_display.clear();
        File dir = new File(WMA.DB_DOWNLOAD);
        if (dir.exists()) {
            for (File file : dir.listFiles()) {
                if ((file.isFile()) & (file.getName().endsWith(WMA.TYPE_CMDE))
                        & (file.getName().startsWith(WMA.TYPE_CME))) {
                    filenames_display.add(file.getName());
                }
            }
        }

        dir = new File(WMA.DB_BLUETOOTH);
        if (dir.exists()) {
            for (File file : dir.listFiles()) {
                if ((file.isFile()) & (file.getName().endsWith(WMA.TYPE_CMDE))
                        & (file.getName().startsWith(WMA.TYPE_CME))) {
                    filenames_display.add(file.getName() + ":BT");
                }
            }
        }

        if (filenames_display.isEmpty()) {
            filenames_display.add(getString(R.string.s_no_event_to_import));
        }

        Collections.sort(filenames_display);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                filenames_display);

        adb_import.setTitle(R.string.s_import);
        adb_import.setCancelable(true);
        adb_import.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                fn_import = filenames_display.get(item);
                if (!fn_import.equals(getString(R.string.s_no_event_to_import))) {

                    WMA.setFileImportMode(true);
                    if (fn_import.endsWith(":BT")) {
                        fn_import = fn_import.replace(":BT", "");
                        WMA.setMyImportFileName(WMA.DB_BLUETOOTH + "/" + fn_import);
                    } else {
                        WMA.setMyImportFileName(WMA.DB_DOWNLOAD + "/" + fn_import);
                    }
                    Intent intent = new Intent(Main_Activity.this, Import_Activity.class);
                    startActivityForResult(intent, MAS_IMPORT);
                    WMA.animateStart(Main_Activity.this);
                }
            }
        });
        adb_import.create();
        adb_import.show();
    }

}