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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.transition.Transition;
import androidx.transition.TransitionInflater;

import com.edge2.R;
import com.edge2.utils.DimenUtils;
import com.google.android.material.appbar.CollapsingToolbarLayout;

public class EventFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        postponeEnterTransition();

        View rootView = inflater.inflate(R.layout.fragment_event, container, false);

        Transition transition = TransitionInflater.from(requireContext())
                .inflateTransition(android.R.transition.move);
        setSharedElementEnterTransition(transition);
        setSharedElementReturnTransition(transition);
        startPostponedEnterTransition();
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
            Toolbar toolbar = v.findViewById(R.id.toolbar);
            ViewGroup.LayoutParams toolbarParams = toolbar.getLayoutParams();
            View content = v.findViewById(R.id.event_content);
            RelativeLayout header = v.findViewById(R.id.event_header);
            CollapsingToolbarLayout.LayoutParams headerParams =
                    (CollapsingToolbarLayout.LayoutParams) header.getLayoutParams();
            int toolbarHeight = DimenUtils.getActionbarHeight(v.getContext());

            toolbarParams.height = toolbarHeight + topInset;
            toolbar.setLayoutParams(toolbarParams);
            content.setPadding(leftInset, 0, rightInset, 0);
            toolbar.setPadding(leftInset, topInset, rightInset, 0);
            headerParams.setMargins(leftInset, toolbarParams.height, rightInset, 0);
            header.setLayoutParams(headerParams);
            return insets;
        });
    }
}