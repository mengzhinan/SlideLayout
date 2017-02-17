package com.duke.slidelayout_recyclerview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class RecyclerViewActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private ArrayList<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        initData();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        adapter = new RecyclerViewAdapter(list);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new RecycleViewLineDivider(LinearLayoutManager.VERTICAL, 4, Color.WHITE));
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        /**
         * 传递出slidelayout中content和options两个布局所有view的点击事件，根据需要做判断
         */
        adapter.setOnMyClickListener(new RecyclerViewAdapter.OnMyClickListener() {
            @Override
            public void onChildClick(View view) {
                String string = "";
                if (view.getId() == R.id.recycler_view_item_content) {
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
                Toast.makeText(RecyclerViewActivity.this, string, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initData() {
        for (int i = 0; i < 30; i++) {
            list.add("老老老老老" + i);
        }
    }
}