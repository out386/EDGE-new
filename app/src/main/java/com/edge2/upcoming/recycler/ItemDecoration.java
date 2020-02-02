package com.edge2.upcoming.recycler;

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

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class ItemDecoration extends RecyclerView.ItemDecoration {
    private int bottomOffset;
    private int topOffset;

    public ItemDecoration(int bottomOffset, int topOffset) {
        this.bottomOffset = bottomOffset;
        this.topOffset = topOffset;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        final RecyclerView.Adapter adapter = parent.getAdapter();
        if (adapter == null)
            return;

        final int itemPosition = parent.getChildAdapterPosition(view);
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        if (itemPosition == adapter.getItemCount() - 1) {
            params.bottomMargin = 0;
            outRect.bottom = bottomOffset;
        } else if (itemPosition == 0) {
            params.topMargin = 0;
            outRect.top = topOffset;
        }
    }
}