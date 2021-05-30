package com.moptim.easyvat.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.moptim.easyvat.R;

public class ImageLayout extends RelativeLayout {

    private ImageView imageView;

    public ImageLayout(Context context) {
        super(context);

        LayoutInflater.from(context).inflate(R.layout.layout_image, this);
        imageView = findViewById(R.id.sr_imageView);
    }

    public ImageView getImageView() {
        return imageView;
    }

}
