package com.edge2.settings;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.edge2.OnFragmentScrollListener;
import com.edge2.R;
import com.edge2.ThemeActivity;
import com.edge2.utils.DimenUtils;
import com.google.android.material.button.MaterialButton;

import static android.widget.Toast.LENGTH_LONG;

public class SettingsFragment extends Fragment {
    private OnFragmentScrollListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (OnFragmentScrollListener) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View topView = view.findViewById(R.id.top_view);
        View contentView = view.findViewById(R.id.sett_content);
        setupInsets(view, topView, contentView);
        setupScrollListener((NestedScrollView) view, topView);
        setupListeners(view);
    }

    private void setupInsets(View rootView, View topView, View contentView) {
        rootView.setOnApplyWindowInsetsListener((v1, insets) -> {
            int topInset = insets.getSystemWindowInsetTop();
            int leftInset = insets.getSystemWindowInsetLeft();
            int rightInset = insets.getSystemWindowInsetRight();
            int bottomInset = insets.getSystemWindowInsetBottom();

            LinearLayout.LayoutParams contentParams =
                    (LinearLayout.LayoutParams) contentView.getLayoutParams();
            contentParams.setMargins(leftInset, 0, rightInset, bottomInset);
            contentView.setLayoutParams(contentParams);
            int toolbarHeight = DimenUtils.getActionbarHeight(requireContext());
            LinearLayout.LayoutParams topViewParams =
                    (LinearLayout.LayoutParams) topView.getLayoutParams();
            topViewParams.topMargin = toolbarHeight + topInset;
            topView.setLayoutParams(topViewParams);

            return insets;
        });

        // Required if other fragments are transacted.
        // No idea why that makes onApplyWindowInsets never fire.
        if (rootView.getRootWindowInsets() != null)
            rootView.dispatchApplyWindowInsets(rootView.getRootWindowInsets());
    }

    private void setupScrollListener(NestedScrollView scrollView, View topView) {
        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> listener
                        .onListScrolled(scrollY - oldScrollY,
                                topView.getHeight() - scrollY));
    }

    private void setupListeners(View rootView) {
        TextView theme = rootView.findViewById(R.id.sett_theme);
        MaterialButton signIn = rootView.findViewById(R.id.sett_sign_in);

        theme.setOnClickListener(view ->
                ((ThemeActivity) requireActivity()).showThemeDialog()
        );
        signIn.setOnClickListener(view -> Toast.makeText(requireContext(),
                getString(R.string.sett_signin_message), LENGTH_LONG).show()
        );
    }
}
