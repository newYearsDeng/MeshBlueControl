package com.northmeter.meshbluecontrol.base;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.northmeter.meshbluecontrol.R;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 *Created by dyd on 2018/8/29.
 */
public abstract  class BaseFragment extends Fragment {
    private Unbinder unbinder;
    private Context context;
    private LoadingDialog mLoadingDialog;

    public BaseFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startGetArgument(savedInstanceState);

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View parentView=inflater.inflate(getLayoutResId(),container,false);
        return parentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();
        unbinder = ButterKnife.bind(this, view);
        //初始化控件
        finishCreateView(savedInstanceState);

    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }

    protected abstract int getLayoutResId();

    /**
     * 初始化传递的参数
     */
    protected abstract void startGetArgument(Bundle savedInstanceState);

    /**
     * 初始化控件
     * @param savedInstanceState
     */
    protected abstract void finishCreateView(Bundle savedInstanceState);

    protected void showMsg(String msg) {
        ToastUtil.showToastShort(context, msg);
    }

    protected void showMsgLong(String msg) {
        ToastUtil.showToastLong(context, msg);
    }

    protected void startLoadingDialog(){
        mLoadingDialog = new LoadingDialog(context);
        mLoadingDialog.setLoadingText("加载中,请稍后...");
        mLoadingDialog.setInterceptBack(false);
        mLoadingDialog.show();

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handeler.sendEmptyMessage(0);
            }
        };
        new Timer().schedule(timerTask,15000);
    }

    Handler handeler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mLoadingDialog.close();
        }
    };

    protected void stopLoadingDialog(){
        if(mLoadingDialog==null){
            mLoadingDialog = new LoadingDialog(context);
        }
        mLoadingDialog.close();
    }


//    public void dialog_show(Bitmap bitmap, String data){
//        try{
//            final AlertDialog dialogSex = new AlertDialog.Builder(getActivity()).create();
//            dialogSex.show();
//            Window window = dialogSex.getWindow();
//            window.setContentView(R.layout.dialog_layout);
//
//            dialogSex.setCanceledOnTouchOutside(true);
//            dialogSex.setCancelable(true);
//
//            // 在此设置显示动画
//            window.setWindowAnimations(R.style.AnimBottom_Dialog);
//
//            TextView textview_1 = (TextView) window.findViewById(R.id.textview_1);
//            ImageView image_show = (ImageView) window.findViewById(R.id.image_show);
//            image_show.setImageBitmap(bitmap);
//            textview_1.setText(data);
//
//
//            dialogSex.show();
//
//            window.findViewById(R.id.relativeLayout2).setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View arg0) {
//                    // TODO Auto-generated method stub
//                    dialogSex.cancel();
//                }
//            });
//
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//    }

}
