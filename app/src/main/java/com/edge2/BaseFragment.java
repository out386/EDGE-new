package com.edge2;

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
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.edge2.data.DataRepo;
import com.edge2.utils.DimenUtils;

public abstract class BaseFragment extends Fragment {

    protected OnFragmentScrollListener onFragmentScrollListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onFragmentScrollListener = (OnFragmentScrollListener) context;
    }

    protected void setupScrollListener(NestedScrollView scrollView, int topViewHeight) {
        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (v, scrollX, scrollY, oldScrollX, oldScrollY) ->
                        onFragmentScrollListener.onListScrolled(scrollY - oldScrollY,
                                topViewHeight - scrollY));
    }

    @Override
    public void onResume() {
        super.onResume();
        DataRepo.getInstance().updateDb(getContext());
    }

    protected void setupWindowInsets(View rootView, View contentView, View topView,
                                     boolean skipTopViewSideMargins, boolean skipContentBottomMargin,
                                     OnApplyWindowInsetsListener listener) {

        rootView.setOnApplyWindowInsetsListener((v1, insets) -> {
            int leftInset = insets.getSystemWindowInsetLeft();
            int topInset = insets.getSystemWindowInsetTop();
            int rightInset = insets.getSystemWindowInsetRight();
            int bottomInset = insets.getSystemWindowInsetBottom();
            int toolbarHeight = DimenUtils.getActionbarHeight(requireContext());

            MarginLayoutParams contentParams = (MarginLayoutParams) contentView.getLayoutParams();
            if (skipContentBottomMargin)
                contentParams.setMargins(leftInset, 0, rightInset, 0);
            else
                contentParams.setMargins(leftInset, 0, rightInset, bottomInset);
            contentView.setLayoutParams(contentParams);

            MarginLayoutParams topViewParams = (MarginLayoutParams) topView.getLayoutParams();
            if (skipTopViewSideMargins) {
                topViewParams.topMargin = toolbarHeight + topInset;
            } else {
                topViewParams.setMargins(
                        leftInset, toolbarHeight + topInset, rightInset, 0);
            }
            topView.setLayoutParams(topViewParams);

            if (listener != null)
                listener.onApplyWindowInsets(leftInset, topInset, rightInset, bottomInset);
            return insets;
        });

        // Required if other fragments are transacted.
        // No idea why that makes onApplyWindowInsets never fire.
        if (rootView.getRootWindowInsets() != null)
            rootView.dispatchApplyWindowInsets(rootView.getRootWindowInsets());
    }

    protected interface OnApplyWindowInsetsListener {
        void onApplyWindowInsets(int leftinset, int topInset, int rightInset, int bottomInset);
    }
}
