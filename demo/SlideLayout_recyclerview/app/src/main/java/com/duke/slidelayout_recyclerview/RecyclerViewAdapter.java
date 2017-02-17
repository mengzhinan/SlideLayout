package com.duke.slidelayout_recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.duke.slidelayout.SlideLayout;
import java.util.ArrayList;

/**
 * @Author: duke
 * @DateTime: 2017-01-03 22:24
 * @Description:
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private ArrayList<String> lists = new ArrayList<>();
    private OnMyClickListener onMyClickListener;

    public void setOnMyClickListener(OnMyClickListener l) {
        this.onMyClickListener = l;
    }

    public RecyclerViewAdapter(ArrayList<String> lists) {
        this.lists.clear();
        this.lists.addAll(lists);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /**
         * 加载滑动布局item_root，其中已经包含了content和optinos布局
         */
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_root, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.content.setText(lists.get(position));
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private SlideLayout slideLayout;
        private TextView content;

        public ViewHolder(View itemView) {
            super(itemView);
            slideLayout = (SlideLayout) itemView;
            content = (TextView) itemView.findViewById(R.id.recycler_view_item_content);

            slideLayout.setOnChildClickListener(new SlideLayout.OnChildClickListener() {
                @Override
                public void onChildClick(View view) {
                    if (onMyClickListener != null) {
                        onMyClickListener.onChildClick(view);
                    }
                }
            });
        }
    }

    public interface OnMyClickListener {
        void onChildClick(View view);
    }
}