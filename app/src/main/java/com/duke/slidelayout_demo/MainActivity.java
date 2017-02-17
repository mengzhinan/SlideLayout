package com.duke.slidelayout_demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.duke.slidelayout.SlideLayout;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        SlideLayout.OnOptionsStateChangeListener, SlideLayout.OnChildClickListener {
    private SlideLayout slideLayout;
    private Button setSlideDirection1, setSlideDirection2, setOptionsLayoutPosition1,
            setOptionsLayoutPosition2, setOpenedfalse, setOpenedtrue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        slideLayout = (SlideLayout) findViewById(R.id.slideLayout);
        slideLayout.setOnOptionsStateChangeListener(this);
        slideLayout.setOnChildClickListener(this);

        setSlideDirection1 = (Button) findViewById(R.id.setSlideDirection1);
        setSlideDirection2 = (Button) findViewById(R.id.setSlideDirection2);
        setOptionsLayoutPosition1 = (Button) findViewById(R.id.setOptionsLayoutPosition1);
        setOptionsLayoutPosition2 = (Button) findViewById(R.id.setOptionsLayoutPosition2);
        setOpenedfalse = (Button) findViewById(R.id.setOpenedfalse);
        setOpenedtrue = (Button) findViewById(R.id.setOpenedtrue);
        setSlideDirection1.setOnClickListener(this);
        setSlideDirection2.setOnClickListener(this);
        setOptionsLayoutPosition1.setOnClickListener(this);
        setOptionsLayoutPosition2.setOnClickListener(this);
        setOpenedfalse.setOnClickListener(this);
        setOpenedtrue.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setSlideDirection1:
                slideLayout.setSlideDirection(SlideLayout.SlideDirection.LEFT);
                break;
            case R.id.setSlideDirection2:
                slideLayout.setSlideDirection(SlideLayout.SlideDirection.RIGHT);
                break;
            case R.id.setOptionsLayoutPosition1:
                slideLayout.setOptionsLayoutPosition(SlideLayout.OptionsLayoutPosition.FIXED);
                break;
            case R.id.setOptionsLayoutPosition2:
                slideLayout.setOptionsLayoutPosition(SlideLayout.OptionsLayoutPosition.FOLLOW);
                break;
            case R.id.setOpenedfalse:
                slideLayout.setOpened(false);
                break;
            case R.id.setOpenedtrue:
                slideLayout.setOpened(true);
                break;
        }
    }

    @Override
    public void onOptionsViewChange(boolean isOpened) {
        Toast.makeText(this, isOpened ? "菜单打开" : "菜单关闭", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onChildClick(View view) {
        String string = "";
        if (view.getId() == R.id.content_root) {
            string = "内容点击";
        } else if (view.getId() == R.id.content_text) {
            TextView textView = (TextView) view;
            string = textView.getText().toString();
        } else if (view.getId() == R.id.options_root) {
            string = "操作菜单点击";
        } else if (view.getId() == R.id.tv_open) {
            TextView textView = (TextView) view;
            string = textView.getText().toString();
        } else if (view.getId() == R.id.tv_delete) {
            TextView textView = (TextView) view;
            string = textView.getText().toString();
        }
        Toast.makeText(MainActivity.this, string, Toast.LENGTH_SHORT).show();
    }
}