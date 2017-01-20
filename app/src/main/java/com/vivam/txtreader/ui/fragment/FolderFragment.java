package com.vivam.txtreader.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vivam.txtreader.R;
import com.vivam.txtreader.utils.FileUtils;

import java.io.File;
import java.io.FileFilter;

public class FolderFragment extends Fragment {

    public static final String ARG_PATH = "path";

    private View mEmptyView;
    private RecyclerView mList;

    private String mPath;

    private File[] mFiles = null;

    public static FolderFragment newInstance(String path) {
        FolderFragment f = new FolderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PATH, path);
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

        Bundle args = getArguments();
        if (args != null) {
            mPath = args.getString(ARG_PATH);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(mPath)) {
            mFiles = new File(mPath).listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return !file.isHidden() && (file.isDirectory() ||
                            file.getPath().endsWith(FileUtils.TXT_SUFFIX));
                }
            });
        }

        if (mFiles != null) {
            mEmptyView.setVisibility(View.GONE);
            mList.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.VISIBLE);
            mList.setVisibility(View.GONE);
        }
    }
}
