package com.edge2.views;

/*
 * Copyright (C) 2019 Ritayan Chakraborty <ritayanout@gmail.com>
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
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.edge2.R;
import com.edge2.utils.DimenUtils;

public class CenterToolbar extends Toolbar {
    private ImageView imageText;
    private ImageView imageLogo;
    private int[] arr = new int[2];
    private int paddingLeftLogo;

    public CenterToolbar(Context context) {
        this(context, null);
    }

    public CenterToolbar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.toolbarStyle);
    }

    public CenterToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Context context = getContext();
        Resources res = context.getResources();
        int padding = res.getDimensionPixelSize(R.dimen.toolbar_image_padding_top_bottom_x2);
        paddingLeftLogo = res.getDimensionPixelSize(R.dimen.toolbar_image_padding_left);
        int toolbarHeight = DimenUtils.getActionbarHeight(context);
        int aspectRatioLogo = res.getDimensionPixelSize(R.dimen.toolbar_image_logo_aspect_ratio);
        int height = toolbarHeight - padding;
        int widthLogo = aspectRatioLogo * height;

        LayoutParams logoParams = new LayoutParams(widthLogo, height);
        LayoutParams textParams = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, toolbarHeight - padding);

        imageText = getToolbarTextImageView();
        imageLogo = getToolbarLogoImageView();

        addView(imageText, textParams);
        addView(imageLogo, logoParams);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        int width = DimenUtils.getWindowWidth(getContext());
        imageText.getLocationInWindow(arr);
        int targetPos = (width / 2) - (imageText.getWidth() / 2 + arr[0]);
        imageText.setTranslationX(imageText.getTranslationX() + targetPos);

        imageLogo.getLocationInWindow(arr);
        targetPos = arr[0];
        imageLogo.setTranslationX(imageLogo.getTranslationX() - targetPos + paddingLeftLogo);
    }

    public ImageView getToolbarTextImageView() {
        ImageView imageView = new ImageView(getContext());

        imageView.setImageResource(R.drawable.stat_edge);
        return imageView;
    }

    public ImageView getToolbarLogoImageView() {
        ImageView imageView = new ImageView(getContext());

        imageView.setImageResource(R.drawable.stat_edge_logo);
        return imageView;
    }
}
