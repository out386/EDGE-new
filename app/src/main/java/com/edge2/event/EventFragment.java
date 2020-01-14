package com.edge2.event;

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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.transition.Transition;
import androidx.transition.TransitionInflater;

import com.edge2.OnFragmentScrollListener;
import com.edge2.R;

public class EventFragment extends Fragment {
    private OnFragmentScrollListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (OnFragmentScrollListener) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        postponeEnterTransition();

        View rootView = inflater.inflate(R.layout.fragment_event, container, false);

        Transition transition = TransitionInflater.from(requireContext())
                .inflateTransition(android.R.transition.move);
        setSharedElementEnterTransition(transition);
        setSharedElementReturnTransition(transition);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupInsets(view);
    }

    private void setupInsets(View v) {
        v.setOnApplyWindowInsetsListener((v1, insets) -> {
            int topInset = insets.getSystemWindowInsetTop();
            int leftInset = insets.getSystemWindowInsetLeft();
            int rightInset = insets.getSystemWindowInsetRight();
            View content = v.findViewById(R.id.event_content);

            content.setPadding(leftInset, 0, rightInset, 0);
            v.post(() -> {
                FrameLayout.LayoutParams rootParams =
                        (FrameLayout.LayoutParams) v.getLayoutParams();
                rootParams.setMargins(leftInset, topInset, rightInset, 0);
                v.setLayoutParams(rootParams);

                // Hide the toolbar
                listener.onListScrolled(0, -Integer.MAX_VALUE);
                startPostponedEnterTransition();
            });
            setupScrollListener(v);
            return insets;
        });

        // No idea why onApplyWindowInsets never fires without this.
        if (v.getRootWindowInsets() != null)
            v.dispatchApplyWindowInsets(v.getRootWindowInsets());
    }

    private void setupScrollListener(View rootView) {
        NestedScrollView scrollView = rootView.findViewById(R.id.scroll_view);
        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (v, scrollX, scrollY, oldScrollX, oldScrollY) ->
                        listener.onListScrolled(
                                scrollY - oldScrollY, -Integer.MAX_VALUE));
    }
}