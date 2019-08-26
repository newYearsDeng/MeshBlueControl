package com.northmeter.meshbluecontrol.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dyd on 2019/2/27.
 * 项目内建筑列表的Adapter
 */
public class ProgectBuildListRVAapter extends RecyclerView.Adapter<ProgectBuildListRVAapter.BuildListViewHolder> {

    public interface OnMyClickListener {
        void onItemClick(View view, int position);
    }

    public OnMyClickListener onClickListener;

    public void setOnMyClickListener(OnMyClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }


    public List models;

    public ProgectBuildListRVAapter(List models) {
        this.models = models;
    }


    @Override
    public BuildListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(0, parent, false);
        BuildListViewHolder viewHolder = new BuildListViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final BuildListViewHolder holder, final int position) {
        //holder.tvBuildTotal.setText((position + 1) + "/" + models.size());

    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class BuildListViewHolder extends RecyclerView.ViewHolder {
//        @BindView(R.id.linear_buildlist)
//        LinearLayout linearBuildlist;


        public BuildListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
