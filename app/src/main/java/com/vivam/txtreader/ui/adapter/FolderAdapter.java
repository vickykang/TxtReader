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
 * Created by kangweodai on 23/01/17.
 */

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.Holder> {

    private Context mContext;
    public final ArrayList<BookFile> mFiles = new ArrayList<>();

    public FolderAdapter() {

    }

    public void setData(ArrayList<BookFile> files) {
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
        holder.nameTv.setText(file.getName());
        if (file.isDirectory()) {
            holder.iconIv.setImageResource(R.drawable.ic_folder);
            holder.sizeTv.setVisibility(View.GONE);
            holder.checkbox.setVisibility(View.GONE);
            holder.importedTv.setVisibility(View.GONE);
        } else {
            holder.iconIv.setImageResource(R.drawable.ic_txt);
            holder.sizeTv.setVisibility(View.VISIBLE);
            holder.sizeTv.setText(Formatter.formatFileSize(mContext, file.getSize()));
            if (file.isImported()) {
                holder.importedTv.setVisibility(View.VISIBLE);
                holder.checkbox.setVisibility(View.GONE);
                holder.itemView.setClickable(false);
            } else {
                holder.importedTv.setVisibility(View.GONE);
                holder.checkbox.setVisibility(View.VISIBLE);
                holder.checkbox.setChecked(file.isSelected());
                holder.itemView.setClickable(true);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    public BookFile getItem(int position) {
        return mFiles.get(position);
    }

    public static class Holder extends RecyclerView.ViewHolder {

        private ImageView iconIv;
        private TextView nameTv;
        private TextView sizeTv;
        private CheckBox checkbox;
        private TextView importedTv;

        public Holder(View itemView) {
            super(itemView);
            iconIv = (ImageView) itemView.findViewById(R.id.icon);
            nameTv = (TextView) itemView.findViewById(R.id.tv_name);
            sizeTv = (TextView) itemView.findViewById(R.id.tv_size);
            importedTv = (TextView) itemView.findViewById(R.id.tv_imported);
            checkbox = (CheckBox) itemView.findViewById(R.id.checkbox);
            checkbox.setButtonDrawable(R.drawable.ic_check_box_sdcard);
        }
    }
}
