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
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.edge2.R;

public class GeneralHeaderView extends ConstraintLayout {
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
        boolean flip = a.getBoolean(R.styleable.GeneralHeaderView_flip, false);
        if (flip)
            inflate(context, R.layout.general_header_inverted, this);
        else
            inflate(context, R.layout.general_header, this);
        String name = a.getString(R.styleable.GeneralHeaderView_name);
        String desc = a.getString(R.styleable.GeneralHeaderView_desc);
        Drawable icon = a.getDrawable(R.styleable.GeneralHeaderView_icon);
        a.recycle();
        setData(name, desc, icon);
    }

    private void setData(String name, String desc, Drawable icon) {
        TextView nameTv = findViewById(R.id.general_name);
        TextView descTv = findViewById(R.id.general_desc);
        ImageView iconView = findViewById(R.id.general_icon);

        nameTv.setText(name);
        descTv.setText(desc);
        iconView.setImageDrawable(icon);
    }
}
