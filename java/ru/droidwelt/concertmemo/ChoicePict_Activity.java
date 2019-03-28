package ru.droidwelt.concertmemo;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class ChoicePict_Activity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choice_pict);
        setTitle(getString(R.string.s_fld_mark));
        WMA.setHeaderFont(this);

        ImageButton ib_0 = findViewById(R.id.choce_pict_0);
        ImageButton ib_1 = findViewById(R.id.choce_pict_1);
        ImageButton ib_2 = findViewById(R.id.choce_pict_2);
        ImageButton ib_3 = findViewById(R.id.choce_pict_3);
        ImageButton ib_4 = findViewById(R.id.choce_pict_4);
        ImageButton ib_5 = findViewById(R.id.choce_pict_5);
        ImageButton ib_6 = findViewById(R.id.choce_pict_6);
        ImageButton ib_7 = findViewById(R.id.choce_pict_7);
        ImageButton ib_8 = findViewById(R.id.choce_pict_8);
        ImageButton ib_9 = findViewById(R.id.choce_pict_9);
        ImageButton ib_10 = findViewById(R.id.choce_pict_10);
        ImageButton ib_11 = findViewById(R.id.choce_pict_11);
        ImageButton ib_12 = findViewById(R.id.choce_pict_12);
        ImageButton ib_13 = findViewById(R.id.choce_pict_13);
        ImageButton ib_14 = findViewById(R.id.choce_pict_14);
        ImageButton ib_15 = findViewById(R.id.choce_pict_15);
        ImageButton ib_16 = findViewById(R.id.choce_pict_16);
        ImageButton ib_17 = findViewById(R.id.choce_pict_17);
        ImageButton ib_18 = findViewById(R.id.choce_pict_18);
        ImageButton ib_19 = findViewById(R.id.choce_pict_19);
        ImageButton ib_20 = findViewById(R.id.choce_pict_20);
        ImageButton ib_21 = findViewById(R.id.choce_pict_21);
        ImageButton ib_22 = findViewById(R.id.choce_pict_22);
        ImageButton ib_23 = findViewById(R.id.choce_pict_23);

        ib_0.setOnClickListener(oclBtnOk);
        ib_1.setOnClickListener(oclBtnOk);
        ib_2.setOnClickListener(oclBtnOk);
        ib_3.setOnClickListener(oclBtnOk);
        ib_4.setOnClickListener(oclBtnOk);
        ib_5.setOnClickListener(oclBtnOk);
        ib_6.setOnClickListener(oclBtnOk);
        ib_7.setOnClickListener(oclBtnOk);
        ib_8.setOnClickListener(oclBtnOk);
        ib_9.setOnClickListener(oclBtnOk);
        ib_10.setOnClickListener(oclBtnOk);
        ib_11.setOnClickListener(oclBtnOk);
        ib_12.setOnClickListener(oclBtnOk);
        ib_13.setOnClickListener(oclBtnOk);
        ib_14.setOnClickListener(oclBtnOk);
        ib_15.setOnClickListener(oclBtnOk);
        ib_16.setOnClickListener(oclBtnOk);
        ib_17.setOnClickListener(oclBtnOk);
        ib_18.setOnClickListener(oclBtnOk);
        ib_19.setOnClickListener(oclBtnOk);
        ib_20.setOnClickListener(oclBtnOk);
        ib_21.setOnClickListener(oclBtnOk);
        ib_22.setOnClickListener(oclBtnOk);
        ib_23.setOnClickListener(oclBtnOk);
    }


    android.view.View.OnClickListener oclBtnOk = new android.view.View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            Intent intent = new Intent();
            switch (id) {

                case R.id.choce_pict_0:
                    intent.putExtra("PICT", 0);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;

                case R.id.choce_pict_1:
                    intent.putExtra("PICT", 1);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;

                case R.id.choce_pict_2:
                    intent.putExtra("PICT", 2);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;

                case R.id.choce_pict_3:
                    intent.putExtra("PICT", 3);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;

                case R.id.choce_pict_4:
                    intent.putExtra("PICT", 4);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;

                case R.id.choce_pict_5:
                    intent.putExtra("PICT", 5);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;

                case R.id.choce_pict_6:
                    intent.putExtra("PICT", 6);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;

                case R.id.choce_pict_7:
                    intent.putExtra("PICT", 7);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;

                case R.id.choce_pict_8:
                    intent.putExtra("PICT", 8);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;

                case R.id.choce_pict_9:
                    intent.putExtra("PICT", 9);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;

                case R.id.choce_pict_10:
                    intent.putExtra("PICT", 10);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;

                case R.id.choce_pict_11:
                    intent.putExtra("PICT", 11);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;

                case R.id.choce_pict_12:
                    intent.putExtra("PICT", 12);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;

                case R.id.choce_pict_13:
                    intent.putExtra("PICT", 13);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;

                case R.id.choce_pict_14:
                    intent.putExtra("PICT", 14);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;

                case R.id.choce_pict_15:
                    intent.putExtra("PICT", 15);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;

                case R.id.choce_pict_16:
                    intent.putExtra("PICT", 16);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;

                case R.id.choce_pict_17:
                    intent.putExtra("PICT", 17);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;

                case R.id.choce_pict_18:
                    intent.putExtra("PICT", 18);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;

                case R.id.choce_pict_19:
                    intent.putExtra("PICT", 19);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;

                case R.id.choce_pict_20:
                    intent.putExtra("PICT", 20);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;

                case R.id.choce_pict_21:
                    intent.putExtra("PICT", 21);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;

                case R.id.choce_pict_22:
                    intent.putExtra("PICT", 22);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;

                case R.id.choce_pict_23:
                    intent.putExtra("PICT", 23);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;

                default:
                    break;
            }
        }
    };


}
