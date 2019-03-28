package ru.droidwelt.concertmemo;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class EditPict_Activity extends Activity {

    private static long DET_ID = 0;

    static final int PIC_CROP = 3;
    static final int GALLERY_REQUEST = 1;
    static final int CAMERA_CAPTURE = 2;

    private boolean __imagemodified = false; // false
    private static Bitmap imageBig, imagePreview;
    private static String __comment = "";
    private static String __detsid = "";
    private static Uri outputFileUri_photo; // куда сохраняется наше фото
    private static Uri outputFileUri_crop;

    private TouchImageView tvi;
    private EditText commentEditText;


    // ------------------------------------------------------------------------------------------------------------------------
    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editpict_activity);

        ActionBar bar = getActionBar();
        assert bar != null;
        bar.setHomeButtonEnabled(true);
        bar.setDisplayHomeAsUpEnabled(true);
        WMA.setHeaderFont(this);

        commentEditText = findViewById(R.id.editpict_comment);

        DET_ID = WMA.getDET_ID();
        __comment = "";
        __detsid = "";
        if (DET_ID > 0) {
            String sql = "select comment,detsid,ord from det where _id=" + DET_ID;
            Cursor detcursor = WMA.getDatabase().rawQuery(sql, null);
            int commentIndex = detcursor.getColumnIndex("comment");
            int detsidIndex = detcursor.getColumnIndex("detsid");
            int ordIndex = detcursor.getColumnIndex("ord");
            detcursor.moveToFirst();
            __comment = WMA.strnormalize(detcursor.getString(commentIndex));
            __detsid = WMA.strnormalize(detcursor.getString(detsidIndex));
            detcursor.getInt(ordIndex);
            commentEditText.setText(__comment);
            detcursor.close();
        }

        tvi = findViewById(R.id.editpict_touch);
        imageBig = WMA.getOneDetImageValue((int) DET_ID);
        tvi.setImageBitmap(imageBig);

        int action = WMA.getDET_ACTION();
        if (action == 1) {
            ChoicePict();
        }
        if (action == 2) {
            ChoicePhoto();
        }

    }

    // сохранение записи в базе данных----------------------------
    private void saveRecord() {

        byte[] imageInByteBig;
        byte[] imageInBytePreview;

        if (isRecordModified() & (imageBig != null)) {

            __comment = commentEditText.getText().toString().trim();

            double kmax = 0.0;
            int bm_h = 0, bm_w = 0;
            if (WMA.getPicture_size() > 0) {
                try {
                    bm_h = imageBig.getHeight();
                    bm_w = imageBig.getWidth();

                    if ((bm_h > 0) & (bm_w > 0)) {
                        double kh = (double) bm_h / (double) WMA.getPicture_size();
                        double kw = (double) bm_w / (double) WMA.getPicture_size();
                        kmax = kh;
                        if (kw > kmax)
                            kmax = kw;
                    }

                } catch (Exception ignored) {
                }
            }

            if (kmax > 1) {
                double dx = (double) bm_w / kmax;
                double dy = (double) bm_h / kmax;
                int x = (int) dx;
                int y = (int) dy;
                imageBig = Bitmap.createScaledBitmap(imageBig, x, y, false);
            }

            double kmax2 = 0.0;
            try {
                bm_h = imageBig.getHeight();
                bm_w = imageBig.getWidth();

                if ((bm_h > 0) & (bm_w > 0)) {
                    double kh2 = (double) bm_h / (double) WMA.getPicturepreview_size();
                    double kw2 = (double) bm_w / (double) WMA.getPicturepreview_size();
                    kmax2 = kh2;
                    if (kw2 > kmax2)
                        kmax2 = kw2;
                }

            } catch (Exception ignored) {
            }

            if (kmax2 != 0) {
                double dx2 = (double) bm_w / kmax2;
                double dy2 = (double) bm_h / kmax2;
                int x2 = (int) dx2;
                int y2 = (int) dy2;
                imagePreview = Bitmap.createScaledBitmap(imageBig, x2, y2, false);
            }

            ByteArrayOutputStream streamBig = new ByteArrayOutputStream();
            imageBig.compress(Bitmap.CompressFormat.JPEG, WMA.getQuality_big(), streamBig);
            imageInByteBig = streamBig.toByteArray();

            ByteArrayOutputStream streamPreview = new ByteArrayOutputStream();
            imagePreview.compress(Bitmap.CompressFormat.JPEG, WMA.getQuality_preview(), streamPreview);
            imageInBytePreview = streamPreview.toByteArray();

            if (DET_ID == 0) {
                DET_ID = insertDetailRecord(__comment, imageInByteBig, imageInBytePreview);
                WMA.setDET_ID(DET_ID);
                WMA.makeMainIconAutomaticByMAS();
            } else {
                updateDetailRecord(DET_ID, __comment, imageInByteBig, imageInBytePreview);
                WMA.makeMainIconAutomaticByMAS();
            }
        }

        setResult(RESULT_OK);
    }

    // --------------------------------------------------------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent returnedIntent) {
        super.onActivityResult(requestCode, resultCode, returnedIntent);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case GALLERY_REQUEST:
                    outputFileUri_photo = returnedIntent.getData();
                    if (outputFileUri_photo == null) {
                        Toast toast = Toast.makeText(this, R.string.s_no_request_image_from_gallery_7, Toast.LENGTH_LONG);
                        toast.show();
                        break;
                    }
                    performCrop();
                    break;

                case CAMERA_CAPTURE: // после CAMERA
                    performCrop();
                    break;

                case PIC_CROP: // после CROP
                    try {
                        imageBig = Media.getBitmap(getContentResolver(), outputFileUri_crop);
                        __imagemodified = true;
                        tvi.setImageBitmap(imageBig);
                    } catch (Exception e) {
                        Toast toast = Toast.makeText(this, R.string.s_no_prepate_after_crop_5, Toast.LENGTH_LONG);
                        toast.show();
                        break;
                    }
                    break;
            }
        }
    }

    // ------------------------------------------------------------------------------------------------------------------------
    private void performCrop() {

        File file = new File(WMA.DB_PATH, WMA.imageFilename_crop);
        if (file.exists())
            file.delete();
        outputFileUri_crop = Uri.fromFile(file);

        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setDataAndType(outputFileUri_photo, "image/*");
        cropIntent.putExtra("crop", "true");

        cropIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        cropIntent.putExtra("output", outputFileUri_crop);
        cropIntent.putExtra("noFaceDetection", true);
        startActivityForResult(cropIntent, PIC_CROP);
        // WMA.animateStart(EditPict_Activity.this);
    }

    // подключение меню ----------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.editpict_menu, menu);

        if (DET_ID == 0) {
            MenuItem item_download = menu.findItem(R.id.editpict_menuItem_download);
            item_download.setEnabled(false);
        }
        return true;
    }

    // ------------------------------------------------------------------------------------------------------------------------
    private boolean isRecordModified() {
        return __imagemodified | !(__comment.compareToIgnoreCase(commentEditText.getText().toString()) == 0);
    }

    private void ChoicePict() {
        WMA.deleteTempFile(WMA.DB_PATH + WMA.imageFilename_photo);
        WMA.deleteTempFile(WMA.DB_PATH + WMA.imageFilename_crop);
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
        // WMA.animateStart(EditPict_Activity.this);
    }

    private void ChoicePhoto() {
        WMA.deleteTempFile(WMA.DB_PATH + WMA.imageFilename_photo);
        WMA.deleteTempFile(WMA.DB_PATH + WMA.imageFilename_crop);
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(WMA.DB_PATH, WMA.imageFilename_photo);
        outputFileUri_photo = Uri.fromFile(file);
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri_photo);
        startActivityForResult(captureIntent, CAMERA_CAPTURE);
        // WMA.animateStart(EditPict_Activity.this);
    }

    // меню -----------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.editpict_menuItem_save:
                saveRecord();
                finish();
                WMA.animateFinish(EditPict_Activity.this);
                return true;

            case R.id.editpict_menuItem_pic_choice:
                ChoicePict();
                return true;

            case R.id.editpict_menuItem_pic_photo:
                ChoicePhoto();
                return true;

            case R.id.editpict_menuItem_pic_clear: // CLEAR
                __imagemodified = true;
                imageBig = null;
                imagePreview = null;
                tvi.setImageBitmap(null);
                return true;

            case R.id.editpict_menuItem_makethumbnail:

                final Timer timer = new Timer();
                AlertDialog.Builder builder = new AlertDialog.Builder(EditPict_Activity.this);
                builder.setTitle(getString(R.string.s_makethumbnail) + " ?");

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
                        if (DET_ID == 0)
                            saveRecord();
                        WMA.makeMainThumbnail(DET_ID);
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

                return true;

            case R.id.editpict_menuItem_download:
                WMA.uploadDetPicture(DET_ID);
                return true;

            case android.R.id.home:
                if (isRecordModified()) {
                    openQuitDialogMy();
                } else {
                    finish();
                    WMA.animateFinish(EditPict_Activity.this);
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // защита от закрытия по Back------------------------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Обработка нажатия, возврат true, если обработка выполнена
        switch (keyCode) {

            case KeyEvent.KEYCODE_BACK:
                if (isRecordModified()) {
                    openQuitDialogMy();
                } else {
                    finish();
                    WMA.animateFinish(EditPict_Activity.this);
                }
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // Диалог переспроса о выходе------------------------------------
    private void openQuitDialogMy() {
        final Timer timer = new Timer();
        AlertDialog.Builder builder = new AlertDialog.Builder(EditPict_Activity.this);
        builder.setTitle(R.string.s_exit_wo_save);

        builder.setPositiveButton(R.string.s_yes, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                timer.purge();
                timer.cancel();
                finish();
                WMA.animateFinish(EditPict_Activity.this);
            }
        });

        builder.setNegativeButton(R.string.s_no, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                timer.purge();
                timer.cancel();
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

    // ------------------------------------------------------------------------
    public static long insertDetailRecord(String comment, byte[] imgBig, byte[] imgPreview) {
        ContentValues newDetRecord = new ContentValues();
        newDetRecord.put("comment", comment);
        newDetRecord.put("img", imgBig);
        newDetRecord.put("preview", imgPreview);
        int mid = (int) WMA.getMAS_ID();
        int ord = WMA.getMaxOrdDet();
        newDetRecord.put("ord", ord);
        newDetRecord.put("mid", mid);
        newDetRecord.put("massid", WMA.getMAS_SID());
        newDetRecord.put("detsid", WMA.generateGUID(20));
        newDetRecord.put("detmid", WMA.generateGUID(20));
        long new_id = -1;
        new_id = WMA.getDatabase().insert("det", null, newDetRecord);
        return new_id;
    }

    // ------------------------------------------------------------------------
    public static void updateDetailRecord(long id, String comment, byte[] imgBig, byte[] imgPreview) {
        ContentValues editDetRecord = new ContentValues();
        editDetRecord.put("comment", comment);
        editDetRecord.put("img", imgBig);
        editDetRecord.put("preview", imgPreview);

        if ((__detsid.isEmpty()) & (__detsid.equals(""))) {
            editDetRecord.put("detsid", WMA.generateGUID(20));
        }
        editDetRecord.put("detmid", WMA.generateGUID(20));
        WMA.getDatabase().update("det", editDetRecord, "_id=" + id, null);
    }

}
