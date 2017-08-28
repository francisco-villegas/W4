package com.example.francisco.w4.view.maps_activity;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.example.francisco.w4.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by FRANCISCO on 28/08/2017.
 */

public class CustomDialogClass extends Dialog implements android.view.View.OnClickListener {


    @BindView(R.id.txt_dia)
    TextView txt_dia;

    public Activity c;
    public Dialog d;
    String message;



    public CustomDialogClass(Activity a, String message) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.message = message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog);

        ButterKnife.bind(this);
        txt_dia.setText(message);
    }

    @OnClick({R.id.btn_no})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_no:
                dismiss();
                break;
        }
    }
}