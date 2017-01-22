package com.vivam.txtreader.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.squareup.otto.Subscribe;
import com.vivam.txtreader.R;
import com.vivam.txtreader.data.DataManager;
import com.vivam.txtreader.data.EventBus;
import com.vivam.txtreader.data.event.FolderEvent;
import com.vivam.txtreader.data.model.BookFile;
import com.vivam.txtreader.data.model.StorageInfo;
import com.vivam.txtreader.ui.fragment.FolderFragment;
import com.vivam.txtreader.utils.FileUtils;

import java.util.ArrayList;

public class FolderActivity extends AppCompatActivity
        implements View.OnClickListener {

    private static final String EXTRA_PARENT = "parent";

    private Toolbar mToolbar;
    private View mImportButton;
    private TextView mImportTextView;

    ArrayList<StorageInfo> mStorageInfos;

    private String mParent;
    private final ArrayList<BookFile> mSelectedFiles = new ArrayList<>();

    private DataManager mDataManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mImportButton = findViewById(R.id.btn_import);
        mImportTextView = (TextView) findViewById(R.id.tv_import);

        setSupportActionBar(mToolbar);
        mImportButton.setOnClickListener(this);

        mDataManager = DataManager.getInstance(this);

        initData(savedInstanceState);
    }

    private void initData(Bundle savedInstanceState) {

        mStorageInfos = FileUtils.listAvailableStorage(this);

        if (savedInstanceState == null) {
            mParent = getString(R.string.sdcard);
            if (mStorageInfos.size() == 1) {
                startFragment(mParent, mStorageInfos.get(0).path);

            } else if (mStorageInfos.size() > 1){
                ArrayList<BookFile> files = new ArrayList<>();
                for (StorageInfo info : mStorageInfos) {
                    BookFile file = new BookFile();
                    file.setName(FileUtils.getName(info.path));
                    file.setPath(info.path);
                    file.setDirectory(true);
                    file.setSelected(false);
                    files.add(file);
                }
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.container, FolderFragment.newInstance(files));
                ft.addToBackStack(null);
                ft.commit();
            }

        } else {
            mParent = savedInstanceState.getString(EXTRA_PARENT);
        }
        mToolbar.setTitle(mParent);
    }

    private void startFragment(String parent, String path) {
        mParent = parent;
        mToolbar.setTitle(mParent);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, FolderFragment.newInstance(path))
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.register(this);
        refreshImportView();
    }

    @Subscribe
    public void onItemClick(FolderEvent event) {
        BookFile file = event.getFile();
        if (file == null) {
            return;
        }
        if (file.isDirectory()) {
            startFragment(file.getName(), file.getPath());
        } else {
            if (file.isSelected()) {
                mSelectedFiles.add(file);
            } else {
                mSelectedFiles.remove(file);
            }
            refreshImportView();
        }
    }

    private void refreshImportView() {
        boolean enable = mSelectedFiles.size() > 0;
        mImportButton.setClickable(enable);
        mImportTextView.setEnabled(enable);
    }

    @Override
    public void onClick(View v) {
        if (mSelectedFiles.size() == 0) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }
        for (BookFile file : mSelectedFiles) {
            mDataManager.importBook(file);
        }
        Intent intent = new Intent();
        intent.putExtra(ScanActivity.EXTRA_IMPORTED, true);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.unregister(this);
    }
}
