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

import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CustomViewOnClickedListener {
    private static final int ANIM_DELAY = 150;
    private static final int BLOCK_DURATION = 3 * ANIM_DELAY;

    private OnClickListener listener;
    private Handler clickHandler;
    private boolean isNoClickInProgress = true;
    private BlockRunnable blockRunnable;
    private ClickRunnable clickRunnable;

    public CustomViewOnClickedListener(@NonNull OnClickListener listener) {
        this.listener = listener;
        clickHandler = new Handler();
        blockRunnable = new BlockRunnable();
        clickRunnable = new ClickRunnable();
    }

    public void onClick(int position) {
        onClick(position, null, null, null, null, null);
    }

    public void onClick(int position, @Nullable View root, @Nullable View view1,
                        @Nullable View view2, @Nullable View view3, @Nullable View view4) {
        if (isNoClickInProgress) {
            isNoClickInProgress = false;
            // Allow view ripple to play before triggering click
            clickRunnable.setVars(position, root, view1, view2, view3, view4);
            clickHandler.postDelayed(clickRunnable, ANIM_DELAY);
            // Block multiple clicks. BLOCK_DURATION > ANIM_DELAY ensures blocking between
            // the listener being triggered and the new fragment becoming visible
            clickHandler.postDelayed(blockRunnable, BLOCK_DURATION);
        }
    }

    private class BlockRunnable implements Runnable {
        @Override
        public void run() {
            isNoClickInProgress = true;
        }
    }

    private class ClickRunnable implements Runnable {
        private int position;
        private View root;
        private View view1;
        private View view2;
        private View view3;
        private View view4;

        void setVars(int position, View root, View view1, View view2, View view3, View view4) {
            this.position = position;
            this.root = root;
            this.view1 = view1;
            this.view2 = view2;
            this.view3 = view3;
            this.view4 = view4;
        }

        @Override
        public void run() {
            listener.onClick(position, root, view1, view2, view3, view4);
        }
    }
}
