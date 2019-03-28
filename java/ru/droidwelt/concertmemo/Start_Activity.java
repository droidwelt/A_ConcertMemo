package ru.droidwelt.concertmemo;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Start_Activity extends AppCompatActivity {

	private int timerTime = 5000;
	private Timer timer = new Timer();
	public static final int RequestPermissionCode = 1;

	protected boolean checkMyPremissoins() {
		return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
				!((ContextCompat.checkSelfPermission(this, CAMERA) != PackageManager.PERMISSION_GRANTED) ||
						(ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
						(ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED));
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
		switch (requestCode) {
			case RequestPermissionCode:
				if (grantResults.length > 0) {
					boolean CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
					boolean ReadExternalStoragePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
					boolean WriteExternalStoragePermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;
					if (CameraPermission & ReadExternalStoragePermission & WriteExternalStoragePermission) {
                        myStart();
					} else {
						finish();
					}
				}
				break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_activity);
		WMA.animateStart(Start_Activity.this);

        if (checkMyPremissoins()) {
            myStart();
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                    {CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, RequestPermissionCode);
        }
	}

    private void myStart() {
        WMA.startDbHelper();
        if (WMA.getchoice_quickstart()) timerTime=100;

        timer.schedule(new TimerTask() {
            public void run() {
                timer.purge();
                timer.cancel();
                Intent intent = new Intent(Start_Activity.this, Main_Activity.class);
                startActivity(intent);
                finish();
                WMA.animateStart(Start_Activity.this);
            }
        }, timerTime);

        ImageView iv_start = findViewById(R.id.imageView_start);
        iv_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.purge();
                timer.cancel();
                Intent intent = new Intent(Start_Activity.this, Main_Activity.class);
                startActivity(intent);
                WMA.animateStart(Start_Activity.this);
                finish();
            }
        });


    }
	
	@Override
	public void onBackPressed() {
		timer.purge();
		timer.cancel(); 
		finish();
		WMA.animateFinish(Start_Activity.this);
	}
	

	

}
