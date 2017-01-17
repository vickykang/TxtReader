package com.vivam.txtreader.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vivam.txtreader.R;
import com.vivam.txtreader.data.model.Book;

import java.util.ArrayList;
import java.util.List;

public class BookshelfAdapter extends RecyclerView.Adapter<BookshelfAdapter.Holder> {

    private Context mContext;
    private List<Book> mBooks;

    public BookshelfAdapter(Context context) {
        mContext = context;
        mBooks = new ArrayList<>();
    }

    public void setData(List<Book> data) {
        mBooks.clear();
        mBooks.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_bookshelf, parent, false));
    }

    @Override
    public void onBindViewHolder(final Holder holder, final int position) {
        final Book book = mBooks.get(position);
        holder.nameTv.setText(book.getName());
    }

    @Override
    public int getItemCount() {
        return mBooks.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        TextView nameTv;
        ProgressBar readPb;
        TextView progressTv;

        public Holder(View itemView) {
            super(itemView);
            nameTv = (TextView) itemView.findViewById(R.id.tv_name);
            readPb = (ProgressBar) itemView.findViewById(R.id.pb_read);
            progressTv = (TextView) itemView.findViewById(R.id.pb_text);
        }
    }
}
