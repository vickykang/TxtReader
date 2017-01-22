package com.vivam.txtreader.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vivam.txtreader.R;
import com.vivam.txtreader.data.DataManager;
import com.vivam.txtreader.data.EventBus;
import com.vivam.txtreader.data.event.FolderEvent;
import com.vivam.txtreader.data.model.BookFile;
import com.vivam.txtreader.ui.adapter.BookScannerAdapter;
import com.vivam.txtreader.ui.widget.RecyclerViewHelper;
import com.vivam.txtreader.utils.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

public class FolderFragment extends Fragment
        implements RecyclerViewHelper.OnItemClickListener {

    public static final String ARG_PATH = "path";
    public static final String ARG_FILES = "files";

    private View mEmptyView;
    private RecyclerView mList;

    private BookScannerAdapter mAdapter;

    private String mPath;

    private final ArrayList<BookFile> mFiles = new ArrayList<>();

    private DataManager mDataManager;

    public static FolderFragment newInstance(String path) {
        FolderFragment f = new FolderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PATH, path);
        f.setArguments(args);
        return f;
    }

    public static FolderFragment newInstance(ArrayList<BookFile> files) {
        FolderFragment f = new FolderFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_FILES, files);
        f.setArguments(args);
        return f;
    }

    public FolderFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_folder, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mEmptyView = view.findViewById(R.id.empty_view);
        mList = (RecyclerView) view.findViewById(R.id.list);

        mAdapter = new BookScannerAdapter(getActivity());
        mList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mList.setAdapter(mAdapter);
        RecyclerViewHelper.addTo(mList)
                .setOnItemClickListener(this);

        mDataManager = DataManager.getInstance(getActivity());

        initData();
        refreshView();
    }

    private void initData() {
        mFiles.clear();

        Bundle args = getArguments();
        if (args != null) {
            mPath = args.getString(ARG_PATH);
            ArrayList<BookFile> files = (ArrayList<BookFile>) args.getSerializable(ARG_FILES);
            if (files != null) {
                mFiles.addAll(files);
            }
        }

        File[] files = null;
        if (!TextUtils.isEmpty(mPath)) {
            files = new File(mPath).listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return !file.isHidden() && file.canRead() && (file.isDirectory() ||
                            file.getPath().endsWith(FileUtils.TXT_SUFFIX));
                }
            });
        }

        if (files == null) return;
        for (File file : files) {
            BookFile bookFile = new BookFile();
            bookFile.setName(file.getName());
            bookFile.setPath(file.getAbsolutePath());
            bookFile.setSelected(false);
            bookFile.setImported(mDataManager.isImported(file.getAbsolutePath()));
            bookFile.setSize(file.length());
            bookFile.setDirectory(file.isDirectory());
            mFiles.add(bookFile);
        }
    }

    private void refreshView() {
        if (mFiles.size() > 0) {
            mEmptyView.setVisibility(View.GONE);
            mList.setVisibility(View.VISIBLE);
            mAdapter.setData(mFiles);
        } else {
            mEmptyView.setVisibility(View.VISIBLE);
            mList.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onItemClick(RecyclerView recyclerView, int position, View view) {
        BookScannerAdapter adapter = (BookScannerAdapter) recyclerView.getAdapter();
        if (adapter == null) {
            return;
        }
        BookFile file = (BookFile) adapter.getItem(position);
        if (file == null || file.isImported()) {
            return;
        }

        if (!file.isDirectory()) {
            boolean isChecked = !file.isSelected();
            file.setSelected(isChecked);
            adapter.notifyDataSetChanged();
        }
        EventBus.post(new FolderEvent(file));
    }
}
