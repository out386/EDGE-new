package com.edge2.views;

/*
 * Copyright (C) 2020 Ritayan Chakraborty <ritayanout@gmail.com>
 *
 * This file is part of EDGE-new
 *
 * EDGE-new is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EDGE-new is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with EDGE-new.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.edge2.R;

public class GeneralHeaderView extends ConstraintLayout {
    private boolean hideImage;
    private TextView nameTv;
    private TextView descTv;
    private ImageView iconView;
    private ImageView imageView;
    private View divider;
    private HideViewRunnable runnable;
    private boolean flip;

    public GeneralHeaderView(Context context) {
        super(context);
        init(context, null);
    }

    public GeneralHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public GeneralHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.GeneralHeaderView, 0, 0);
        flip = a.getBoolean(R.styleable.GeneralHeaderView_flip, false);
        if (flip)
            inflate(context, R.layout.general_header_inverted, this);
        else
            inflate(context, R.layout.general_header, this);
        hideImage = a.getBoolean(R.styleable.GeneralHeaderView_hideImage, false);
        boolean skipTint = a.getBoolean(R.styleable.GeneralHeaderView_skipTint, false);
        String name = a.getString(R.styleable.GeneralHeaderView_name);
        String desc = a.getString(R.styleable.GeneralHeaderView_desc);
        Drawable icon = a.getDrawable(R.styleable.GeneralHeaderView_icon);
        a.recycle();

        setViews();
        setData(name, desc, icon, skipTint);
    }

    private void setViews() {
        nameTv = findViewById(R.id.general_name);
        descTv = findViewById(R.id.general_desc);
        iconView = findViewById(R.id.general_icon);
        imageView = findViewById(R.id.general_img);
        divider = findViewById(R.id.divider);

        if (hideImage) {
            runnable = new HideViewRunnable();
            imageView.post(runnable);
        }
    }

    public void setData(int nameRes, int descRes, int iconRes, boolean skipTint) {
        Resources resources = getResources();
        String name = resources.getString(nameRes);
        String desc = resources.getString(descRes);
        Drawable icon = resources.getDrawable(iconRes, null);
        setData(name, desc, icon, skipTint);
    }

    public void setData(String name, String desc, Drawable icon, boolean skipTint) {
        nameTv.setText(name);
        if (desc == null || desc.isEmpty()) {
            descTv.setVisibility(INVISIBLE);
        } else {
            descTv.setVisibility(VISIBLE);
            descTv.setText(desc);
        }
        if (skipTint)
            iconView.setImageTintList(null);
        if (icon != null)
            iconView.setImageDrawable(icon);
    }

    public void setData(String name, String desc, Uri iconUri, int iconPlaceholderRes,
                        boolean skipTint) {
        setData(name, desc, iconUri, iconPlaceholderRes, skipTint, false);
    }

    public void setData(String name, String desc, Uri iconUri, int iconPlaceholderRes,
                        boolean skipTint, boolean skipCircle) {
        setData(name, desc, null, skipTint);
        if (iconUri != null) {
            RequestBuilder<Drawable> builder = Glide.with(getContext())
                    .load(iconUri);
            if (!skipCircle)
                builder.apply(RequestOptions.circleCropTransform());
            builder.placeholder(iconPlaceholderRes)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .into(iconView);
        } else {
            iconView.setImageResource(iconPlaceholderRes);
        }
    }

    public void setDesc(String desc) {
        if (desc == null || desc.isEmpty()) {
            descTv.setVisibility(INVISIBLE);
        } else {
            descTv.setVisibility(VISIBLE);
            descTv.setText(desc);
        }
    }

    public void setName(String name) {
        if (name != null && !name.isEmpty())
            nameTv.setText(name);
    }

    public void setNameTransition(String transitionName) {
        ViewCompat.setTransitionName(nameTv, transitionName);
    }

    public void setDescTransition(String transitionName) {
        ViewCompat.setTransitionName(descTv, transitionName);
    }

    public void setIconTransition(String transitionName) {
        ViewCompat.setTransitionName(iconView, transitionName);
    }

    public TextView getNameTv() {
        return nameTv;
    }

    public void showImage(int animDuration) {
        if (hideImage) {
            imageView.removeCallbacks(runnable);
            imageView.setVisibility(VISIBLE);
            imageView.animate()
                    .setDuration(animDuration)
                    .translationX(0);
            divider.setVisibility(VISIBLE);
            divider.animate()
                    .setDuration(animDuration)
                    .translationY(0);
        }
    }

    class HideViewRunnable implements Runnable {
        @Override
        public void run() {
            if (flip)
                imageView.setTranslationX(-imageView.getWidth());
            else
                imageView.setTranslationX(imageView.getWidth());
            imageView.setVisibility(INVISIBLE);
            divider.setTranslationY(-divider.getHeight());
            divider.setVisibility(INVISIBLE);
        }
    }
}
