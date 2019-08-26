package com.northmeter.meshbluecontrol.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by liangminhua on 16/9/29.
 * liangminhua
 * E-mail:elecat@126.com
 * 通用的ViewHolder
 */
public class ViewHolder {

    private final SparseArray<View> mViews;
    private View mConvertView;

    private ViewHolder(Context context, ViewGroup parent, int layoutId,
                       int position) {
        this.mViews = new SparseArray<View>();
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent,
                false);
        //setTag
        mConvertView.setTag(this);


    }

    /**
     * 拿到一个ViewHolder对象
     *
     * @param context
     * @param convertView
     * @param parent
     * @param layoutId
     * @param position
     * @return
     */
    public static ViewHolder get(Context context, View convertView,
                                 ViewGroup parent, int layoutId, int position) {

        if (convertView == null) {
            return new ViewHolder(context, parent, layoutId, position);
        }
        return (ViewHolder) convertView.getTag();
    }

    /**
     * 通过控件的Id获取对于的控件，如果没有则加入views
     *
     * @param viewId
     * @return
     */
    public void getImageViewSet(int viewId,int res) {

        ImageView imageView = (ImageView) mViews.get(viewId);
        if (imageView == null) {
            imageView = mConvertView.findViewById(viewId);
            mViews.put(viewId, imageView);
        }
        imageView.setImageResource(res);
    }

    /**
     * 通过控件的Id获取对于的控件，如果没有则加入views
     *
     * @param viewId
     * @return
     */
    public void getTextViewSet(int viewId,String res) {

        TextView textView = (TextView) mViews.get(viewId);
        if (textView == null) {
            textView = mConvertView.findViewById(viewId);
            mViews.put(viewId, textView);
        }
        textView.setText(res);
    }

    public void getCheckViewSet(int viewId,boolean check) {

        CheckBox checkBox = (CheckBox) mViews.get(viewId);
        if (checkBox == null) {
            checkBox = mConvertView.findViewById(viewId);
            mViews.put(viewId, checkBox);
        }
        checkBox.setChecked(check);
    }

    /**
     * 通过控件的Id获取对于的控件，如果没有则加入views
     *
     * @param viewId
     * @return
     */
    public <T extends View> T getView(int viewId) {

        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public View getConvertView() {
        return mConvertView;
    }


}
