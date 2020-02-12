package com.edge2.genericevents;

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
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;

import androidx.appcompat.widget.AppCompatImageView;

import com.edge2.R;

public class AdditionalImageView extends AppCompatImageView {
    public AdditionalImageView(Context context) {
        this(context, null);
    }

    public AdditionalImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AdditionalImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        MarginLayoutParams params = new MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        int marginLarge = getResources().getDimensionPixelSize(R.dimen.margin_huge);
        params.setMargins(0, 0, 0, marginLarge);

        setLayoutParams(params);
        setAdjustViewBounds(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int specWidth = MeasureSpec.getSize(widthMeasureSpec);
        int maxWidth = getResources().getDimensionPixelSize(R.dimen.full_width_img_max_width);
        if (specWidth > maxWidth) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(maxWidth,
                    MeasureSpec.getMode(widthMeasureSpec));
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
