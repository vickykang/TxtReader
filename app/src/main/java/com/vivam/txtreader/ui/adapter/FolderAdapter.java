package com.vivam.txtreader.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.vivam.txtreader.R;
import com.vivam.txtreader.data.model.BookFile;

import java.util.ArrayList;

/**
 * Created by kangweodai on 20/01/17.
 */

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.Holder> {

    private final ArrayList<BookFile> mFiles = new ArrayList<>();
    private Context mContext;

    public FolderAdapter() {

    }

    public void setFiles(ArrayList<BookFile> files) {
        mFiles.clear();
        if (files != null) {
            mFiles.addAll(files);
        }
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new Holder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_file, parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        BookFile file = mFiles.get(position);
        if (file == null) {
            return;
        }

        if (file.isDirectory()) {
            holder.iconIv.setImageResource(R.drawable.ic_folder_24);
            holder.sizeTv.setVisibility(View.GONE);
        } else {
            holder.iconIv.setImageResource(R.drawable.ic_txt);
            holder.sizeTv.setVisibility(View.VISIBLE);
            holder.sizeTv.setText(Formatter.formatFileSize(mContext, file.getSize()));
        }
        holder.nameTv.setText(file.getName());
        holder.checkBox.setChecked(file.isSelected());
    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {

        ImageView iconIv;
        TextView nameTv;
        TextView sizeTv;
        CheckBox checkBox;

        public Holder(View itemView) {
            super(itemView);
            iconIv = (ImageView) itemView.findViewById(R.id.icon);
            nameTv = (TextView) itemView.findViewById(R.id.tv_name);
            sizeTv = (TextView) itemView.findViewById(R.id.tv_size);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
            checkBox.setButtonDrawable(R.drawable.ic_check_box_folder);
        }
    }
}
