package com.example.livedome.dialog;

import android.os.Bundle;

import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.livedome.Constants;
import com.example.livedome.LiveAnchorActivity;
import com.example.livedome.R;
import com.example.livedome.adapter.LiveFunctionAdapter;
import com.example.livedome.interfaces.LiveFunctionClickListener;
import com.example.livedome.interfaces.OnItemClickListener;
import com.example.livedome.utils.DpUtil;


/**
 * Created by cxf on 2018/10/9.
 */

public class LiveFunctionDialogFragment extends AbsDialogFragment implements OnItemClickListener<Integer> {

    private LiveFunctionClickListener mFunctionClickListener;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_function;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog2;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        params.y = DpUtil.dp2px(50);
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        boolean hasGame = false;
        Bundle bundle = getArguments();
        if (bundle != null) {
            hasGame = bundle.getBoolean(Constants.HAS_GAME, false);
        }
        RecyclerView recyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 5, GridLayoutManager.VERTICAL, false));
        LiveFunctionAdapter adapter = new LiveFunctionAdapter(mContext, hasGame);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    public void setFunctionClickListener(LiveFunctionClickListener functionClickListener) {
        mFunctionClickListener = functionClickListener;
    }

    @Override
    public void onItemClick(Integer bean, int position) {
        dismiss();
        if (mFunctionClickListener != null) {
            mFunctionClickListener.onClick(bean);
        }
    }

    @Override
    public void onDestroy() {
        mFunctionClickListener = null;
        ((LiveAnchorActivity) mContext).setBtnFunctionDark();
        super.onDestroy();
    }
}
