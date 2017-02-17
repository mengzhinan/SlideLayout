package com.duke.slidelayout_listview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ListViewActivity extends AppCompatActivity {
    private ListView listView;
    private ListViewAdapter adapter;
    private ArrayList<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        initData();
        listView = (ListView) findViewById(R.id.list_view);
        adapter = new ListViewAdapter(list);
        listView.setAdapter(adapter);
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        adapter.setOnMyClickListener(new ListViewAdapter.OnMyClickListener() {
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
                Toast.makeText(ListViewActivity.this, string, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initData() {
        for (int i = 0; i < 30; i++) {
            list.add("老老老老老" + i);
        }
    }
}