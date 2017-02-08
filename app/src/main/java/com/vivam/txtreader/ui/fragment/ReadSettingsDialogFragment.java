package com.vivam.txtreader.ui.fragment;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.vivam.txtreader.R;
import com.vivam.txtreader.ui.activity.ChapterActivity;
import com.vivam.txtreader.ui.activity.ReadActivity;

public class ReadSettingsDialogFragment extends DialogFragment implements View.OnClickListener {

    private static final String ARG_BOOK_ID = "book_id";

    private ImageView mChapterButton;
    private ImageView mTextFormatButton;
    private ImageView mBackgroundButton;

    private long mBookId;

    public static ReadSettingsDialogFragment newInstance(long bookId) {
        ReadSettingsDialogFragment f = new ReadSettingsDialogFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_BOOK_ID, bookId);
        f.setArguments(args);
        return f;
    }

    public ReadSettingsDialogFragment() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(null);
            dialog.getWindow().setWindowAnimations(R.style.Animation_AppCompat_Light_ShowAtBottom);
            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.width = ActionBar.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.BOTTOM;
            dialog.getWindow().setAttributes(lp);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_read_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mChapterButton = (ImageView) view.findViewById(R.id.btn_chapter);
        mTextFormatButton = (ImageView) view.findViewById(R.id.btn_text_format);
        mBackgroundButton = (ImageView) view.findViewById(R.id.btn_background);

        mChapterButton.setOnClickListener(this);
        mTextFormatButton.setOnClickListener(this);
        mBackgroundButton.setOnClickListener(this);

        initData();
    }

    private void initData() {
        Bundle args = getArguments();
        if (args != null) {
            mBookId = args.getLong(ARG_BOOK_ID);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_chapter:
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), ChapterActivity.class);
                    intent.putExtra(ChapterActivity.EXTRA_BOOK_ID, mBookId);
                    getActivity().startActivityForResult(intent, ReadActivity.REQUEST_CHAPTER_LIST);
                }
                dismiss();
                break;
            case R.id.btn_text_format:
                break;
            case R.id.btn_background:
                break;
        }
    }
}
