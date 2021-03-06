package com.vivam.txtreader.ui.activity;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.otto.Subscribe;
import com.vivam.txtreader.Constants;
import com.vivam.txtreader.R;
import com.vivam.txtreader.data.DataManager;
import com.vivam.txtreader.data.EventBus;
import com.vivam.txtreader.data.event.ScanEvent;
import com.vivam.txtreader.data.model.BookFile;
import com.vivam.txtreader.data.model.StorageInfo;
import com.vivam.txtreader.thread.ScanFileThread;
import com.vivam.txtreader.ui.adapter.BookScannerAdapter;
import com.vivam.txtreader.ui.widget.RecyclerViewHelper;
import com.vivam.txtreader.utils.FileUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScanActivity extends AppCompatActivity
        implements View.OnClickListener,
        RecyclerViewHelper.OnItemClickListener {

    private static final String TAG = "ScanActivity";

    private static final int REQUEST_FOLDER = 1;

    public static final String EXTRA_IMPORTED = "imported";

    public static final int MSG_END_SCANNING = 1;

    private Toolbar mToolbar;
    private View mScanSdcardButton;
    private TextView mEmptyView;
    private View mLoadingView;
    private RecyclerView mBookList;
    private ViewGroup mImportButton;

    private LayoutTransition mTransition;

    private BookScannerAdapter mAdapter;

    final private Map<String, List<BookFile>> mFiles = new HashMap<>();
    final private ArrayList<BookFile> mSelected = new ArrayList<>();

    private DataManager mDataManager;

    private List<StorageInfo> mStorageInfos;

    private Handler mRefreshHandler;
    private final Handler.Callback mCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == MSG_END_SCANNING) {
                EventBus.post(new ScanEvent((HashMap<String, ArrayList<BookFile>>) msg.obj));
                return true;
            }
            return false;
        }
    };

    private ExecutorService mScanExecutor = Executors.newCachedThreadPool();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mScanSdcardButton = findViewById(R.id.btn_scan_sdcard);
        mEmptyView = (TextView) findViewById(R.id.no_book_found);
        mLoadingView = findViewById(R.id.loading);
        mBookList = (RecyclerView) findViewById(R.id.list_book);
        mImportButton = (ViewGroup) findViewById(R.id.btn_import);

        setSupportActionBar(mToolbar);
        mToolbar.setTitle(getTitle());

        mAdapter = new BookScannerAdapter(this);
        mBookList.setLayoutManager(new LinearLayoutManager(this));
        mBookList.setAdapter(mAdapter);
        RecyclerViewHelper.addTo(mBookList)
                .setOnItemClickListener(this);

        mScanSdcardButton.setOnClickListener(this);
        mImportButton.setOnClickListener(this);

        mTransition = new LayoutTransition();
        mImportButton.setLayoutTransition(mTransition);

        mDataManager = DataManager.getInstance(this);
        mRefreshHandler = new Handler(mCallback);
        mStorageInfos = FileUtils.listAvailableStorage(this);
        notifyState(Constants.STATE_LOADING);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.register(this);
        startScan();
    }

    private void startScan() {
        for (StorageInfo info : mStorageInfos) {
            mScanExecutor.execute(new ScanFileThread(mDataManager, info.path,
                    FileUtils.TXT_SUFFIX, mRefreshHandler));
        }
    }

    @Subscribe
    public void onScanFinished(ScanEvent event) {
        mFiles.clear();
        HashMap<String, ArrayList<BookFile>> data = event.getBookFiles();
        if (data == null || data.size() == 0) {
            notifyState(Constants.STATE_EMPTY);
        } else {
            mFiles.putAll(data);
            notifyState(Constants.STATE_SUCCESS);
        }
    }

    private void notifyState(int state) {
        switch (state) {
            case Constants.STATE_LOADING:
                mScanSdcardButton.setVisibility(View.GONE);
                mLoadingView.setVisibility(View.VISIBLE);
                mEmptyView.setVisibility(View.GONE);
                mBookList.setVisibility(View.GONE);
                break;

            case Constants.STATE_SUCCESS:
                mScanSdcardButton.setVisibility(View.VISIBLE);
                mLoadingView.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.GONE);
                mBookList.setVisibility(View.VISIBLE);
                mAdapter.setData(mFiles);
                break;

            case Constants.STATE_EMPTY:
                mScanSdcardButton.setVisibility(View.VISIBLE);
                mLoadingView.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.VISIBLE);
                mBookList.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_FOLDER) {
            if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.unregister(this);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.btn_scan_sdcard) {
            startActivityForResult(new Intent(this, FolderActivity.class), REQUEST_FOLDER);

        } else if (id == R.id.btn_import) {
            if (mSelected.size() == 0) {
                setResult(RESULT_CANCELED);
                finish();
                return;
            }
            for (BookFile file : mSelected) {
                mDataManager.importBook(file);
            }
            Intent intent = new Intent();
            intent.putExtra(EXTRA_IMPORTED, true);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onItemClick(RecyclerView recyclerView, int position, View view) {
        BookScannerAdapter adapter = (BookScannerAdapter) recyclerView.getAdapter();
        if (adapter == null) {
            return;
        }

        Object item = adapter.getItem(position);
        if (!(item instanceof BookFile)) {
            return;
        }

        BookFile file = (BookFile) item;
        if (file.isImported()) {
            return;
        }

        boolean isChecked = !file.isSelected();
        file.setSelected(isChecked);
        mAdapter.notifyDataSetChanged();

        if (isChecked) {
            mSelected.add(file);
            setImportButtonVisibility(View.VISIBLE);
        } else {
            mSelected.remove(file);
            setImportButtonVisibility(mSelected.size() > 0 ? View.VISIBLE : View.GONE);
        }
    }

    private void setImportButtonVisibility(int visibility) {
        mImportButton.setVisibility(visibility);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
