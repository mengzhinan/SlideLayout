package com.duke.slidelayout_listview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.duke.slidelayout.SlideLayout;
import java.util.ArrayList;

/**
 * @Author: duke
 * @DateTime: 2017-01-03 22:24
 * @Description:
 */
public class ListViewAdapter extends BaseAdapter{
    private ArrayList<String> list = new ArrayList<>();
    private OnMyClickListener onMyClickListener;

    public void setOnMyClickListener(OnMyClickListener l) {
        this.onMyClickListener = l;
    }

    public ListViewAdapter(ArrayList<String> lists) {
        this.list.clear();
        this.list.addAll(lists);
    }

    @Override
    public int getCount() {
        return list ==null?0: list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_root, parent, false);
            holder.slideLayout = (SlideLayout) convertView;
            holder.content = (TextView) convertView.findViewById(R.id.recycler_view_item_content);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.content.setText(list.get(position));
        holder.slideLayout.setOnChildClickListener(new SlideLayout.OnChildClickListener() {
            @Override
            public void onChildClick(View view) {
                if (onMyClickListener != null) {
                    onMyClickListener.onChildClick(view);
                }
            }
        });
        return convertView;
    }

    static class ViewHolder{
        private SlideLayout slideLayout;
        private TextView content;
    }

    public interface OnMyClickListener {
        void onChildClick(View view);
    }
}