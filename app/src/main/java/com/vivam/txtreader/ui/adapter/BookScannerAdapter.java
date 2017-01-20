package com.vivam.txtreader.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.vivam.txtreader.R;
import com.vivam.txtreader.data.model.BookFile;
import com.vivam.txtreader.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BookScannerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_DIR = 0;
    public static final int TYPE_FILE = 1;

    private Context mContext;
    final private List<Object> mData = new ArrayList<>();

    public BookScannerAdapter(Context context) {
        mContext = context;
    }

    public void setData(Map<String, List<BookFile>> data) {
        mData.clear();
        for (String dir : data.keySet()) {
            mData.add(dir);
            for (BookFile file : data.get(dir)) {
                mData.add(file);
            }
        }
        notifyDataSetChanged();
    }

    public Object getItem(int position) {
        if (position < mData.size()) {
            return mData.get(position);
        }
        return null;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_FILE) {
            return new FileHolder(inflater.inflate(R.layout.item_file, parent, false));
        }
        if (viewType == TYPE_DIR) {
            return new DirHolder(inflater.inflate(R.layout.item_dir, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Object data = mData.get(position);

        if (data instanceof BookFile) {
            final BookFile file = (BookFile) data;
            final FileHolder fileHolder = (FileHolder) holder;
            fileHolder.nameTv.setText(file.getName());
            fileHolder.sizeTv.setText(Formatter.formatFileSize(mContext, file.getSize()));
            fileHolder.divider.setVisibility(
                    (position < mData.size() - 1 && mData.get(position + 1) instanceof BookFile)
                            ? View.VISIBLE : View.GONE);
            if (file.isImported()) {
                fileHolder.checkbox.setVisibility(View.GONE);
                fileHolder.importedTv.setVisibility(View.VISIBLE);
                fileHolder.itemView.setClickable(false);
            } else {
                fileHolder.checkbox.setVisibility(View.VISIBLE);
                fileHolder.importedTv.setVisibility(View.GONE);
                fileHolder.itemView.setClickable(true);
                fileHolder.checkbox.setChecked(file.isSelected());
            }

        } else if (data instanceof String) {
            DirHolder dirHolder = (DirHolder) holder;
            dirHolder.dirTv.setText(FileUtils.getName((String) data));

            if (position > 0) {
                dirHolder.divider.setVisibility(View.VISIBLE);
            } else {
                dirHolder.divider.setVisibility(View.GONE);
            }
            dirHolder.itemView.setClickable(false);
        }
    }

    private String buildParentPath(String path) {
        if (path.equals(FileUtils.EXTERNAL_PATH) || path.equals(FileUtils.EXTERNAL_PATH + "/")) {
            return mContext.getString(R.string.disk);
        }
        String parent = path.substring(FileUtils.EXTERNAL_PATH.length() + 1)
                .replace('/', '.');
        if (parent.startsWith(".")) {
            parent = path.substring(1);
        }
        return parent;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position) instanceof BookFile ? TYPE_FILE : TYPE_DIR;
    }

    public static class DirHolder extends RecyclerView.ViewHolder {

        TextView dirTv;
        View divider;

        public DirHolder(View itemView) {
            super(itemView);
            dirTv = (TextView) itemView.findViewById(R.id.tv_dir);
            divider = itemView.findViewById(R.id.divider);
        }
    }

    public static class FileHolder extends RecyclerView.ViewHolder {

        TextView nameTv;
        TextView sizeTv;
        CheckBox checkbox;
        TextView importedTv;
        View divider;

        public FileHolder(View itemView) {
            super(itemView);
            nameTv = (TextView) itemView.findViewById(R.id.tv_name);
            sizeTv = (TextView) itemView.findViewById(R.id.tv_size);
            checkbox = (CheckBox) itemView.findViewById(R.id.checkbox);
            importedTv = (TextView) itemView.findViewById(R.id.tv_imported);
            divider = itemView.findViewById(R.id.divider);
        }

        public boolean isChecked() {
            return checkbox.isChecked();
        }

        public void setChecked(boolean checked) {
            checkbox.setChecked(checked);
        }
    }
}
