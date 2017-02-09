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
import android.widget.SeekBar;

import com.vivam.txtreader.R;
import com.vivam.txtreader.data.EventBus;
import com.vivam.txtreader.data.event.SeekPageEvent;
import com.vivam.txtreader.ui.activity.ChapterActivity;
import com.vivam.txtreader.ui.activity.ReadActivity;

public class ReadSettingsDialogFragment extends DialogFragment
        implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private static final String ARG_BOOK_ID = "book_id";
    private static final String ARG_TOTAL_PAGES = "total_pages";
    private static final String ARG_CURRENT_PAGE = "current_page";

    private SeekBar mSeekBar;
    private ImageView mChapterButton;
    private ImageView mTextFormatButton;
    private ImageView mBackgroundButton;

    private long mBookId;
    private int mTotalPages;
    private int mCurrentPage;

    public static ReadSettingsDialogFragment newInstance(long bookId, int total, int current) {
        ReadSettingsDialogFragment f = new ReadSettingsDialogFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_BOOK_ID, bookId);
        args.putInt(ARG_TOTAL_PAGES, total);
        args.putInt(ARG_CURRENT_PAGE, current);
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
        mSeekBar = (SeekBar) view.findViewById(R.id.seek_bar);
        mChapterButton = (ImageView) view.findViewById(R.id.btn_chapter);
        mTextFormatButton = (ImageView) view.findViewById(R.id.btn_text_format);
        mBackgroundButton = (ImageView) view.findViewById(R.id.btn_background);

        mSeekBar.setOnSeekBarChangeListener(this);
        mChapterButton.setOnClickListener(this);
        mTextFormatButton.setOnClickListener(this);
        mBackgroundButton.setOnClickListener(this);

        initData();
        syncView();
    }

    private void initData() {
        Bundle args = getArguments();
        if (args != null) {
            mBookId = args.getLong(ARG_BOOK_ID);
            mTotalPages = args.getInt(ARG_TOTAL_PAGES);
            mCurrentPage = args.getInt(ARG_CURRENT_PAGE);
        }
    }

    private void syncView() {
        mSeekBar.setMax(mTotalPages);
        mSeekBar.setProgress(mCurrentPage);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_chapter:
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), ChapterActivity.class);
                    intent.putExtra(ChapterActivity.EXTRA_BOOK_ID, mBookId);
                    getActivity().startActivityForResult(intent, ReadActivity.REQUEST_CHAPTER_LIST);
                    getActivity().overridePendingTransition(R.anim.slide_in_bottom, 0);
                }
                dismiss();
                break;
            case R.id.btn_text_format:
                break;
            case R.id.btn_background:
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!fromUser) {
            return;
        }
        mCurrentPage = progress;
        EventBus.post(new SeekPageEvent(mCurrentPage));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
