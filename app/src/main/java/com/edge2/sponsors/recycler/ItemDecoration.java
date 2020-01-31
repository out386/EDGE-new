package com.edge2.sponsors.recycler;

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

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ItemDecoration extends RecyclerView.ItemDecoration {
    private int bottomOffset;
    private int topOffset;
    private double columns;

    public ItemDecoration(RecyclerView.LayoutManager layoutManager, int bottomOffset, int topOffset) {
        columns = ((GridLayoutManager) layoutManager).getSpanCount();
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
        double itemRow = Math.ceil((itemPosition + 1) / columns);
        if (itemRow == Math.ceil(adapter.getItemCount() / columns)) {
            outRect.bottom = bottomOffset;
        } else if (itemRow == 1) {
            params.topMargin = 0;
            outRect.top = topOffset;
        }
    }
}