package ru.droidwelt.concertmemo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

@SuppressLint("StaticFieldLeak")
public class DB_OpenHelper extends SQLiteOpenHelper {

    private static Context context;
    private static String DB_NAME;
    public static SQLiteDatabase database;


    DB_OpenHelper(Context context, String dbName) {
        super(context, dbName, null, 1);
        DB_OpenHelper.context = context;
        File PATH_TO = new File(WMA.DB_PATH); // куда
        if (!(PATH_TO.isDirectory() && PATH_TO.canExecute() && PATH_TO.canRead() && PATH_TO.canWrite())) {
            PATH_TO.mkdir();
        }
        DB_NAME = dbName;
        database = openDataBase();

        try {
            database.query("mas", new String[]{"_id", "name"}, "_id=0",
                    null, null, null, null);
        } catch (SQLException e) {
            //Log.i("ERR", "Android database corrupt 2");
            Toast.makeText(context, "Структура базы программы повреждена. Производится восстановление из эталона",
                    Toast.LENGTH_LONG).show();
            database.close();
            copyDataBase();
            database = openDataBase();
        }
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }

    long getDatabaseSize() {
        File file = new File(WMA.DB_PATH + DB_NAME);
        return (file.length() + 524288) / 1048576;
    }

    private static void copyDataBase() {
        try {
            InputStream externalDbStream = context.getAssets().open(WMA.DB_NAMEMODEL);
            String outFileName = WMA.DB_PATH + DB_NAME;
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

    // Создаст базу, если она не создана
    private static void createDataBase() {
        boolean dbExist = checkDataBase();
        if (!dbExist) {
            // this.getReadableDatabase()
            copyDataBase();
        }
    }

    // Проверка существования базы данных
    private static boolean checkDataBase() {
        SQLiteDatabase checkDb = null;
        try {
            checkDb = SQLiteDatabase.openDatabase(WMA.DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLException ignored) {
        }
        if (checkDb != null) {
            checkDb.close();
        }
        return checkDb != null;
    }

    private static SQLiteDatabase openDataBase() throws SQLException {
        if (database == null) {
            createDataBase();
            try {
                database = SQLiteDatabase.openDatabase(WMA.DB_PATH + DB_NAME, null,
                        SQLiteDatabase.OPEN_READWRITE);
            } catch (SQLException e) {
                Log.i("ERR", "Android database corrupt 1");
                Toast.makeText(context, context.getResources().getString(R.string.s_db_recovery),
                        Toast.LENGTH_LONG).show();
                createDataBase();
            }
        }
        return database;
    }

/*	public static void closeDatabase() {
		if (database != null) {
			database.close();
		}
	}*/

    @Override
    public synchronized void close() {
        if (database != null) {
            database.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    void Backup_DB(String DBN) {
        Contact_uploadDB_exec(DBN);
    }

    // ------------- выгрузка, сохрание БД -------------------------------
    private void Contact_uploadDB_exec(String DBN) {
        File PATH_TO = new File(WMA.DB_DOWNLOAD); // куда

        if (!(PATH_TO.isDirectory() && PATH_TO.canExecute() && PATH_TO.canRead() && PATH_TO.canWrite())) {
            PATH_TO.mkdir();
        }

        if (!(PATH_TO.isDirectory() && PATH_TO.canExecute() && PATH_TO.canRead() && PATH_TO.canWrite())) {

            Toast.makeText(context, context.getResources().getString(R.string.s_access_folder_error) + PATH_TO, Toast.LENGTH_LONG).show();

        } else {

            final String fn_from = WMA.DB_PATH + DB_NAME;
            final String fn_to = PATH_TO + "//" + DBN;
            // Log.i("LOG", "fn_from " + fn_from);
            // Log.i("LOG", "fn_to " + fn_to);

            class WorkingThread extends Thread {
                @Override
                public void run() {
                    super.run();
                    try {
                        final InputStream DbStream_from = new FileInputStream(fn_from);
                        final OutputStream DbStream_to = new FileOutputStream(fn_to);
                        byte[] buffer = new byte[1024 * 100];
                        int bytesRead;

                        while ((bytesRead = DbStream_from.read(buffer)) > 0) {
                            DbStream_to.write(buffer, 0, bytesRead);
                            DB_BackupFile.db_backup_Handler.sendEmptyMessage(1);
                        }

                        DbStream_to.close();
                        DbStream_from.close();
                        DB_BackupFile.db_backup_Handler.sendEmptyMessage(3);
                        Log.i("XXX", "База выгружена в " + fn_to);
                        // Toast.makeText(context, "База выгружена в " + fn_to,
                        // Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Log.i("XXX", "IOException ");
                        e.printStackTrace();
                    }
                }
            }
            new WorkingThread().start();
        }
    }

    /*
     * // Копирование базы из эталона через getAssets private void
     * copyDataBase() throws IOException { Log.i("XXX",
     * "Копирование базы из эталона через getAssets в " + WMA.DB_PATH
     * + DB_NAME); InputStream externalDbStream =
     * context.getAssets().open(WMA.DB_NAMEMODEL); String outFileName
     * = WMA.DB_PATH + DB_NAME; OutputStream localDbStream = new
     * FileOutputStream(outFileName);
     *
     * byte[] buffer = new byte[1024]; int bytesRead; while ((bytesRead =
     * externalDbStream.read(buffer)) > 0) { localDbStream.write(buffer, 0,
     * bytesRead); } localDbStream.close(); externalDbStream.close(); }
     */

}