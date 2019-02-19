package com.edge2.behaviours;

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
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.edge2.R;
import com.edge2.utils.Logger;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;

public class AppBarBehaviour extends CoordinatorLayout.Behavior<RelativeLayout> {
    private static final float SLOP_FACTOR = 20f;
    private static int slop = 20;

    private int dy = 0;
    private Toolbar toolbar;

    public AppBarBehaviour(Context context, AttributeSet attrs) {
        super(context, attrs);
        float density = context.getResources().getDisplayMetrics().density;
        slop = (int) (SLOP_FACTOR * density);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull RelativeLayout child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        if (toolbar == null)
            toolbar = child.findViewById(R.id.toolbar);
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull RelativeLayout child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);

        NestedScrollView scrollView = (NestedScrollView) target;

        int offset = scrollView.getScrollY();
        Log.i("meh2", "onStopNestedScroll: " + offset +","+toolbar.getHeight());
    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull RelativeLayout child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        this.dy += dy;
        NestedScrollView scrollView = (NestedScrollView) target;

        int offset = scrollView.getScrollY();
        Log.i("meh", "onStopNestedScroll: " + offset +","+toolbar.getHeight());
        toolbar.setTranslationY(Math.max(-toolbar.getHeight(), Math.min(0, toolbar.getTranslationY() - dy)));
    }

    @Override
    public void onStopNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull RelativeLayout child, @NonNull View target, int type) {
        super.onStopNestedScroll(coordinatorLayout, child, target, type);
        NestedScrollView scrollView = (NestedScrollView) target;

        int offset = scrollView.getScrollY();
        Log.i("meh", "onStopNestedScroll: " + offset +","+toolbar.getHeight());

        if (dy < -slop || dy > 0 && dy < slop)
            toolbar.animate()
                    .translationY(0);
        else if (dy >= slop || dy < 0 && dy >= -slop)
            toolbar.animate()
                    .translationY(-toolbar.getHeight());
        dy = 0;
    }
}
