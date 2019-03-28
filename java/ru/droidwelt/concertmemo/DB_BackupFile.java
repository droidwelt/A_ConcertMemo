package ru.droidwelt.concertmemo;

import java.util.Date;
import java.util.Objects;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

@SuppressLint("StaticFieldLeak")
public class DB_BackupFile extends DialogFragment implements OnClickListener {


    private static EditText mEditText;
    private static Button btn_Ok;
    private static Button btn_Cancel;
    private static ProgressBar pb;
    private static String fn;

    public DB_BackupFile() {
    }

    public interface DB_BackupFile_Listener {
        void DB_BackupFile_onFinishEditDialog(String inputText);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.db_backupfile, container);
        getDialog().setTitle(R.string.s_backupDB);

        mEditText = view.findViewById(R.id.backup_filename);
        if (savedInstanceState != null)
            mEditText.setText(fn);
        // Show soft keyboard automatically
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getDialog().getWindow()).setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        btn_Ok = view.findViewById(R.id.button_Ok);
        btn_Cancel = view.findViewById(R.id.button_Cancel);
        btn_Ok.setOnClickListener(this);
        btn_Cancel.setOnClickListener(this);
        pb = view.findViewById(R.id.wait_operation_pb);
        return view;
    }

    @Override
    public void onClick(View v) {
        DB_BackupFile_Listener activity = (DB_BackupFile_Listener) getActivity();
        int id = v.getId();
        switch (id) {

            case R.id.button_Cancel:
                this.dismiss();
                activity.DB_BackupFile_onFinishEditDialog("CANCEL");
                break;

            case R.id.button_Ok:
                btn_Ok.setEnabled(false);
                btn_Cancel.setEnabled(false);
                pb.setIndeterminate(true);
                fn = mEditText.getText().toString();
                if (fn.equals(""))
                    fn = "ConcertMemo_DB " + DateFormat.format("yyyy-MM-dd hh-mm-ss", new Date());
                if (!fn.endsWith(WMA.TYPE_CMDB)) {
                    fn = fn.concat(WMA.TYPE_CMDB);
                    mEditText.setText(fn);
                }

                DB_OpenHelper dbh = new DB_OpenHelper(WMA.getContext(), WMA.DB_NAME);
                dbh.Backup_DB(fn);
                break;

            default:
                break;
        }
    }

    @SuppressLint("HandlerLeak")
    public static Handler db_backup_Handler = new Handler() {
        double f = 0.0d;

        @Override
        @SuppressLint({"DefaultLocale", "Assert", "SetTextI18n"})
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case 1:
                    f = (float) (f + 0.1);
                    String S = String.format("%.1f", f);
                    DB_BackupFile.btn_Ok.setText(WMA.S_UNLOADED + S + " Mb");
                    break;

                case 3:
                    f = 0.0d;
                    Main_Activity.getDlg_BackupFile().dismiss();
                    //	Main_Activity.Contact_refresh();
                    break;

                default:
                    assert (false);
                    break;
            }
        }
    };

}