package com.edge2.allevents;

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
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.edge2.MainActivity;
import com.edge2.R;
import com.edge2.allevents.models.EventModel;
import com.edge2.utils.DimenUtils;
import com.edge2.utils.Logger;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.ArrayList;
import java.util.List;

import ir.apend.slider.model.Slide;
import ir.apend.slider.ui.Slider;

public class EventsFragment extends Fragment {

    private RecyclerView mainReycler;
    private EventsViewModel viewModel;
    private Slider banner;
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mainReycler = rootView.findViewById(R.id.main_recycler);
        banner = rootView.findViewById(R.id.top_banner);
        banner.setScreenWidth(DimenUtils.getWindowWidth(context));

        float itemSize = getResources().getDimension(R.dimen.main_events_img_w) +
                2 * getResources().getDimension(R.dimen.main_events_padding);
        int columnCount = getRecyclerColumnCount(rootView, mainReycler, itemSize);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, columnCount);
        mainReycler.setHasFixedSize(true);
        mainReycler.setLayoutManager(layoutManager);

        ((MainActivity) requireActivity()).setupToolbar(rootView.findViewById(R.id.toolbar));
        setupInsets(rootView);
        setupObservers();
        prototype();
        return rootView;
    }

    private void setupInsets(View v) {
        AppBarLayout appBarLayout = v.findViewById(R.id.app_bar_layout);
        CoordinatorLayout.LayoutParams appbarParams =
                (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        CollapsingToolbarLayout.LayoutParams bannerParams =
                (CollapsingToolbarLayout.LayoutParams) banner.getLayoutParams();
        Toolbar toolbar = v.findViewById(R.id.toolbar);
        ViewGroup.LayoutParams toolbarParams = toolbar.getLayoutParams();
        int toolbarHeight = DimenUtils.getActionbarHeight(context);

        v.setOnApplyWindowInsetsListener((v1, insets) -> {
            int topInset = insets.getSystemWindowInsetTop();
            int leftInset = insets.getSystemWindowInsetLeft();
            int rightInset = insets.getSystemWindowInsetRight();
            int bottomInset = insets.getSystemWindowInsetBottom();

            toolbarParams.height = toolbarHeight + topInset;
            toolbar.setLayoutParams(toolbarParams);
            toolbar.setPadding(leftInset, topInset, rightInset, 0);

            mainReycler.setPadding(leftInset, 0, rightInset, bottomInset);

            appBarLayout.post(() -> {
                appbarParams.height = appBarLayout.getHeight() + toolbarParams.height;
                appBarLayout.setLayoutParams(appbarParams);
                bannerParams.setMargins(leftInset, toolbarParams.height, rightInset, 0);
                banner.setLayoutParams(bannerParams);
            });
            return insets;
        });
    }

    private void prototype() {
        ArrayList<EventModel> events = new ArrayList<>();
        String template = context.getString(R.string.num_sub_events);
        for (int j = 0; j < 12; j++) {
            EventModel event = new EventModel("ComputeAid",
                    requireActivity().getDrawable(R.drawable.computeaid), 4, template);
            events.add(event);
        }
        EventsAdapter eventsAdapter = new EventsAdapter(events,
                position -> Logger.log("EventListener", "onEventClicked: " + position));
        mainReycler.setAdapter(eventsAdapter);
    }

    private void setupObservers() {
        OnBannerItemClickedListener bannerListener = new OnBannerItemClickedListener();
        viewModel = ViewModelProviders.of(this)
                .get(EventsViewModel.class);

        viewModel.getBanner().observe(this, eventNameModels -> {
            List<Slide> list = new ArrayList<>(eventNameModels.size());
            for (int i = 0; i < eventNameModels.size(); i++) {
                list.add(new Slide(i, eventNameModels.get(i).getImg(), 0));
            }
            banner.setItemClickListener(bannerListener);
            banner.addSlides(list);
        });
    }

    private int getRecyclerColumnCount(View parent, View child, float pxWidth) {
        int totalPadding = parent.getPaddingRight() + parent.getPaddingLeft()
                + child.getPaddingRight() + child.getPaddingLeft();
        pxWidth += child.getPaddingRight() + child.getPaddingLeft();
        int screenWidth = DimenUtils.getWindowWidth(context) - totalPadding;
        return (int) Math.floor(screenWidth / pxWidth);
    }

    class OnBannerItemClickedListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Logger.log("BannerListener", "onItemClick: " + position);
        }
    }
}
