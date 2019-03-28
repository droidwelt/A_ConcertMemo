package ru.droidwelt.concertmemo;

import java.util.Timer;

import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ImportInfo_Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.importinfo_activity);
        WMA.setHeaderFont(this);
        TextView tv_name = findViewById(R.id.importinfo_name);
        tv_name.setText(WMA.getMyImportFileName());
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {

                finish();
            }
        }, 2000);
    }
}
