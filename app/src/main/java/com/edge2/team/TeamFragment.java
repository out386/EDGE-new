package com.edge2.team;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

import com.edge2.BaseFragment;
import com.edge2.R;

import java.util.List;

public class TeamFragment extends BaseFragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_team, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View topView = view.findViewById(R.id.top_view);
        LinearLayout contentView = view.findViewById(R.id.team_content);
        // Show the toolbar
        onFragmentScrollListener.onListScrolled(0, Integer.MAX_VALUE);
        setupWindowInsets(view, contentView, topView, false,
                false, null);
        topView.post(() ->
                setupScrollListener((NestedScrollView) view, topView.getHeight()));
        setData(contentView);
    }

    private void setData(LinearLayout contentView) {
        List<Data.MemberModel> team = new Data().getMembers();
        Context context = requireContext();
        for (Data.MemberModel member : team) {
            MemberView view = new MemberView(context, member);
            contentView.addView(view);
        }
    }

}