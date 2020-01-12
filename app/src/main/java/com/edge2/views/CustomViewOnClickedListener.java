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

import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CustomViewOnClickedListener {
    private static final int ANIM_DELAY = 150;

    private OnClickListener listener;
    private Handler clickHandler;
    private boolean isNoClickInProgress = true;

    public CustomViewOnClickedListener(@NonNull OnClickListener listener) {
        this.listener = listener;
        clickHandler = new Handler();
    }

    public void onClick(int position) {
        onClick(position, null, null);
    }

    public void onClick(int position, @Nullable View view1, @Nullable View root) {
        // Block double clicks
        if (isNoClickInProgress) {
            isNoClickInProgress = false;
            // Allow view ripple to play before triggering click
            clickHandler.postDelayed(() -> {
                listener.onClick(position, view1, root);
                isNoClickInProgress = true;
            }, ANIM_DELAY);
        }
    }
}
