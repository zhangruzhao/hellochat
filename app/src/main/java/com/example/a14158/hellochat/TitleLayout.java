package com.example.a14158.hellochat;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Created by 14158 on 2017/10/30.
 */

public class TitleLayout extends LinearLayout implements View.OnClickListener {
   //public static LocalBroadcastManager localBroadcastManager;

    public TitleLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.title,this);
        //localBroadcastManager = LocalBroadcastManager.getInstance(getContext());

        Button back = (Button)findViewById(R.id.back_button);
        Button edit = (Button)findViewById(R.id.edit_button);
        back.setOnClickListener(this);
        edit.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_button:
                ((Activity) getContext()).finish();
                break;
            case R.id.edit_button:
                ActivityCollector.finishAll();
                //Intent intent = new Intent("com.example.broadcastbestpractice.FORCE_OFFLINE");
                //getContext().sendBroadcast(intent);
                break;
            default:
                break;
        }
    }
}
