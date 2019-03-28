package ru.droidwelt.concertmemo;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

@SuppressLint("StaticFieldLeak")
public class DB_RestoreProc extends DialogFragment implements OnClickListener {


	private static Button btn_Ok;
	private static Button btn_Cancel;
	private static String filename;
	private static ProgressBar pb;

	public DB_RestoreProc() {

	}

	public interface DB_RestoreProc_Listener {
		void DB_RestoreProc_onFinishEditDialog(String inputText);
	}

	public void set_filename(String s) {
		filename = s;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.db_restoreproc, container);
		getDialog().setTitle(R.string.s_restoreDB_confirm);
		btn_Ok = view.findViewById(R.id.wait_operation_start);
		btn_Ok.setOnClickListener(this);
		btn_Cancel = view.findViewById(R.id.wait_operation_cancel);
		btn_Cancel.setOnClickListener(this);
		pb = view.findViewById(R.id.wait_operation_pb);
		return view;
	}

	@Override
	public void onClick(View v) {
		DB_RestoreProc_Listener activity = (DB_RestoreProc_Listener) getActivity();
		int id = v.getId();
		switch (id) {

		case R.id.wait_operation_cancel:
			this.dismiss();
			activity.DB_RestoreProc_onFinishEditDialog("CANCEL");
			break;

		case R.id.wait_operation_start:
			if (filename != null) {
				btn_Ok.setEnabled(false);
				btn_Cancel.setEnabled(false);
				pb.setIndeterminate(true);
				Restore_DB(filename);
			}
			break;

		default:
			break;
		}
	}

	public void Restore_DB(final String DB_NAMEFROM) {
		class WorkingThread extends Thread {
			@Override
			public void run() {
				super.run();

				try {				
					String externalFileName = WMA.DB_DOWNLOAD+DB_NAMEFROM; 					
					InputStream externalDbStream = new FileInputStream(externalFileName);
					String outFileName = WMA.DB_PATH + WMA.DB_NAME;					
					OutputStream localDbStream = new FileOutputStream(outFileName);
					byte[] buffer = new byte[1024 * 100];
					int bytesRead;

					while ((bytesRead = externalDbStream.read(buffer)) > 0) {
						// Log.i("XXX", "externalDbStream.read");
						localDbStream.write(buffer, 0, bytesRead);
						// Log.i("XXX", "localDbStream.write");
						db_restore_Handler.sendEmptyMessage(1);
						// yield();
					}
					localDbStream.close();
					externalDbStream.close();
					db_restore_Handler.sendEmptyMessage(3);
					// Log.i("XXX", "Restore_DB - sendEmptyMessage(3)");

				} catch (IOException e) {
					Log.i("XXX", "Restore_DB Error ");
					db_restore_Handler.sendEmptyMessage(3);
					e.printStackTrace();
				}
			}
		}
		new WorkingThread().start();
	}

	@SuppressLint("HandlerLeak")
	Handler db_restore_Handler = new Handler() {
		double f = 0.0d;

		@Override
		@SuppressLint({"DefaultLocale", "Assert", "SetTextI18n"})
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case 1:
				f = (float) (f + 0.1);
				String S = String.format("%.1f", f);
				DB_RestoreProc.btn_Ok.setText(getString(R.string.s_loaded) + S + " Mb");
				break;

			case 3:
				f = 0.0d;
				Main_Activity.getDlg_ResroreProc().dismiss();
				// Log.i("XXX",
				// "DB_RestoreProc - Main_Activity.getDlg_ResroreProc().dismiss();");
				DB_RestoreProc_Listener activity = (DB_RestoreProc_Listener) getActivity();
				activity.DB_RestoreProc_onFinishEditDialog("OK");
				// Log.i("XXX",
				// "DB_RestoreProc - activity.DB_RestoreProc_onFinishEditDialog('OK');");
				break;

			default:
				assert (false);
				break;
			}
		}
	};

}