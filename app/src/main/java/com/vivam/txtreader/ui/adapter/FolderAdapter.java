package com.vivam.txtreader.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.vivam.txtreader.R;

import java.io.File;

/**
 * Created by kangweodai on 20/01/17.
 */

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.Holder> {

    public File[] mFiles = null;

    public FolderAdapter() {

    }

    public void setFiles(File[] files) {
        this.mFiles = files;
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_file, parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        File file = mFiles[position];
        if (file == null) {
            return;
        }

        if (file.isDirectory()) {
            holder.iconIv.setImageResource(R.drawable.ic_folder);
        }
    }

    @Override
    public int getItemCount() {
        return mFiles != null ? mFiles.length : 0;
    }

    public static class Holder extends RecyclerView.ViewHolder {

        ImageView iconIv;
        TextView nameTv;
        TextView sizeTv;
        CheckBox checkBox;
        ImageView rightIv;

        public Holder(View itemView) {
            super(itemView);
            iconIv = (ImageView) itemView.findViewById(R.id.icon);
            nameTv = (TextView) itemView.findViewById(R.id.tv_name);
            sizeTv = (TextView) itemView.findViewById(R.id.tv_size);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
            rightIv = (ImageView) itemView.findViewById(R.id.iv_selected);

            checkBox.setVisibility(View.GONE);
            rightIv.setVisibility(View.VISIBLE);
        }
    }
}
