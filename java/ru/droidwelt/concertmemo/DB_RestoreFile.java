package ru.droidwelt.concertmemo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint("StaticFieldLeak")
public class DB_RestoreFile extends DialogFragment implements OnClickListener {

    private static TextView tv;
    private static Button btn_Ok;
    private static List<String> filenames = new ArrayList<>();
    private static String fn;

    public DB_RestoreFile() {
    }

    public interface DB_RestoreFile_Listener {
        void DB_RestoreFile_onFinishEditDialog(String inputText);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.db_restorefile, container);
        getDialog().setTitle(R.string.s_restoreDB_choice);

        tv = view.findViewById(R.id.download_filename);
        if (savedInstanceState != null)
            tv.setText(fn);
        btn_Ok = view.findViewById(R.id.contact_downloaddb_Ok);
        Button btn_Cancel = view.findViewById(R.id.contact_downloaddb_Cancel);
        btn_Ok.setOnClickListener(this);
        btn_Cancel.setOnClickListener(this);
        ListView lv = view.findViewById(R.id.download_listView);

        filenames.clear();
        File dir = new File(WMA.DB_DOWNLOAD);
        if (dir.exists()) {
            for (File file : dir.listFiles()) {
                if ((file.isFile()) & (file.getName().contains(WMA.TYPE_CMDB)))
                    filenames.add(file.getName());
            }
        } else {
            btn_Ok.setEnabled(false);
            filenames.add(getString(R.string.s_no_folder_to_load) + " " + WMA.DB_DOWNLOAD);
        }
        Collections.sort(filenames);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1,
                filenames);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(getfilename_Listener);

        return view;
    }

    OnItemClickListener getfilename_Listener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            fn = filenames.get(position);
            tv.setText(fn);
            btn_Ok.setEnabled(true);
        }
    };

    @Override
    public void onClick(View v) {
        DB_RestoreFile_Listener activity = (DB_RestoreFile_Listener) getActivity();
        int id = v.getId();
        switch (id) {

            case R.id.contact_downloaddb_Cancel:
                this.dismiss();
                activity.DB_RestoreFile_onFinishEditDialog("CANCEL");
                break;

            case R.id.contact_downloaddb_Ok:
                String filename = tv.getText().toString();
                activity.DB_RestoreFile_onFinishEditDialog("OK");
                android.app.FragmentManager fm = getFragmentManager();
                Main_Activity.setDlg_ResroreProc(new DB_RestoreProc());
                Main_Activity.getDlg_ResroreProc().setCancelable(false);
                Main_Activity.getDlg_ResroreProc().show(fm, "");
                Main_Activity.getDlg_ResroreProc().set_filename(filename);
                this.dismiss();
                break;

            default:
                break;
        }
    }

}