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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

import com.edge2.BaseFragment;
import com.edge2.R;
import com.edge2.ThemeActivity;
import com.google.android.material.button.MaterialButton;

import static android.widget.Toast.LENGTH_LONG;

public class SettingsFragment extends BaseFragment {

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
        // Hide the toolbar
        onFragmentScrollListener.onListScrolled(0, Integer.MAX_VALUE);
        setupWindowInsets(view, contentView, topView, false,
                false, null);
        topView.post(() ->
                setupScrollListener((NestedScrollView) view, topView.getHeight()));
        setupListeners(view);
    }

    private void setupListeners(View rootView) {
        TextView theme = rootView.findViewById(R.id.sett_theme);

        theme.setOnClickListener(view ->
                ((ThemeActivity) requireActivity()).showThemeDialog()
        );

        // TODO: Make the notifications switch actually work
        /*
         * Currently, it can't work when the app is in the background, because FCM doesn't allow
         * handling notification messages when the app is in the background, but just fires
         * notifications itself. This restriction does not exist for FCM data messages, but the FCM
         * console can't send data messages. Fixing this switch requires writing a tiny server app
         * to send data messages with.
         */
    }
}
