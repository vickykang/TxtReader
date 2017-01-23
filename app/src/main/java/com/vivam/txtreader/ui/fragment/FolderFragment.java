package com.vivam.txtreader.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vivam.txtreader.R;
import com.vivam.txtreader.data.DataManager;
import com.vivam.txtreader.data.EventBus;
import com.vivam.txtreader.data.event.FolderEvent;
import com.vivam.txtreader.data.model.BookFile;
import com.vivam.txtreader.ui.adapter.BookScannerAdapter;
import com.vivam.txtreader.ui.adapter.FolderAdapter;
import com.vivam.txtreader.ui.widget.RecyclerViewHelper;
import com.vivam.txtreader.utils.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FolderFragment extends Fragment
        implements RecyclerViewHelper.OnItemClickListener {

    public static final String ARG_PATH = "path";
    public static final String ARG_FILES = "files";

    private View mEmptyView;
    private RecyclerView mList;

    private FolderAdapter mAdapter;

    private String mPath;

    private final ArrayList<BookFile> mData = new ArrayList<>();

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

        mAdapter = new FolderAdapter();
        mList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mList.setAdapter(mAdapter);
        RecyclerViewHelper.addTo(mList)
                .setOnItemClickListener(this);

        mDataManager = DataManager.getInstance(getActivity());

        initData();
        refreshView();
    }

    private void initData() {
        ArrayList<BookFile> dirs = new ArrayList<>();
        ArrayList<BookFile> files = new ArrayList<>();

        Bundle args = getArguments();
        if (args != null) {
            mPath = args.getString(ARG_PATH);
            ArrayList<BookFile> fileList = (ArrayList<BookFile>) args.getSerializable(ARG_FILES);
            if (fileList != null) {
                for (BookFile file : fileList) {
                    if (file.isDirectory()) {
                        dirs.add(file);
                    } else {
                        files.add(file);
                    }
                }
            }
        }

        File[] fileList = null;
        if (!TextUtils.isEmpty(mPath)) {
            fileList = new File(mPath).listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return !file.isHidden() && file.canRead() && (file.isDirectory() ||
                            file.getPath().endsWith(FileUtils.TXT_SUFFIX));
                }
            });
        }

        if (fileList == null) return;
        for (File file : fileList) {
            BookFile bookFile = new BookFile();
            bookFile.setName(file.getName());
            bookFile.setPath(file.getAbsolutePath());
            bookFile.setSelected(false);
            bookFile.setImported(mDataManager.isImported(file.getAbsolutePath()));
            bookFile.setSize(file.length());
            if (file.isDirectory()) {
                bookFile.setDirectory(true);
                dirs.add(bookFile);
            } else {
                bookFile.setDirectory(false);
                files.add(bookFile);
            }
        }
        sortData(dirs, files);
    }

    private void sortData(ArrayList<BookFile> dirs, ArrayList<BookFile> files) {
        mData.clear();
        if (dirs != null) {
            Collections.sort(dirs, new NameComparator());
            mData.addAll(dirs);
        }
        if (files != null) {
            Collections.sort(files, new NameComparator());
            mData.addAll(files);
        }
    }

    private void refreshView() {
        if (mData.size() > 0) {
            mEmptyView.setVisibility(View.GONE);
            mList.setVisibility(View.VISIBLE);
            mAdapter.setData(mData);
        } else {
            mEmptyView.setVisibility(View.VISIBLE);
            mList.setVisibility(View.GONE);
        }
    }

    private String formatPath(String path) {
        if (!TextUtils.isEmpty(path)) {
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
            path = path.replace("/", " > ");
            return path;
        }
        return "Unknown directory";
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onItemClick(RecyclerView recyclerView, int position, View view) {
        FolderAdapter adapter = (FolderAdapter) recyclerView.getAdapter();
        if (adapter == null) {
            return;
        }
        BookFile file = adapter.getItem(position);
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

    private class NameComparator implements Comparator<BookFile> {

        @Override
        public int compare(BookFile o1, BookFile o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }
}
