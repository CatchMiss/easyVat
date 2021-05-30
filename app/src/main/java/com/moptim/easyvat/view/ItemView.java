package com.moptim.easyvat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moptim.easyvat.R;

public class ItemView extends LinearLayout {

    private View view;
    private ImageView mImage;
    private TextView mTvTitle;
    private TextView mTvDesc;

    public ItemView(Context context) {
        super(context);
        initView();
    }

    public ItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        setAttrs(attrs);
    }

    private void setAttrs(AttributeSet attrs) {
        //xmlns:item="http://schemas.android.com/apk/res-auto"
        String image = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "image");
        String title = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "title");
        String desc = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "desc");

        Log.i("ItemView", "setAttrs: " + image + ", " + title + ", " + desc);
    }

    private void initView() {
        view = LayoutInflater.from(getContext()).inflate(R.layout.layout_item, this);

        mImage = view.findViewById(R.id.imageView);
        mTvTitle = view.findViewById(R.id.title);
        mTvDesc = view.findViewById(R.id.desc);
    }

}
