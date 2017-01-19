package com.vivam.txtreader.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vivam.txtreader.R;

public class PageFragment extends Fragment {

    private static final String ARG_CONTENT = "content";

    private TextView mTextView;

    private String mContent;

    public static PageFragment newInstance(String content) {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CONTENT, content);
        fragment.setArguments(args);
        return fragment;
    }

    public PageFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_page, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTextView = (TextView) view.findViewById(R.id.tv_content);

        Bundle args = getArguments();
        if (args != null) {
            mContent = args.getString(ARG_CONTENT);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mTextView.setText(mContent);
    }
}
