package com.edge2.events;

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

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.edge2.R;
import com.edge2.utils.Logger;
import com.edge2.views.carousel.EventsAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import ir.apend.slider.model.Slide;
import ir.apend.slider.ui.Slider;

public class EventsFragment extends Fragment {
    public static final String TAG = "main";

    private Slider banner;
    private LinearLayout rootLayout;
    private EventsViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Logger.log("EventsFragment", "onCreateView: ");
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        banner = rootView.findViewById(R.id.top_banner);
        rootLayout = rootView.findViewById(R.id.root_layout);
        banner.setScreenWidth(getScreenWidth());
        setupObservers();

        return rootView;
    }


    private void setupObservers() {
        viewModel = ViewModelProviders.of(this)
                .get(EventsViewModel.class);

        viewModel.getBanner()
                .observe(this, eventNameModels -> {
                    List<Slide> list = new ArrayList<>(eventNameModels.size());
                    for (int i = 0; i < eventNameModels.size(); i++) {
                        list.add(new Slide(i, eventNameModels.get(i).getImg(), 0));
                    }
                    banner.setItemClickListener(new BannerListener());
                    banner.addSlides(list);
                });
    }

    class BannerListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Logger.log("BannerListener", "onItemClick: " + position);
        }
    }

    private int getScreenWidth() {
        Activity activity = getActivity();
        if (activity != null) {
            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            return size.x;
        }
        return -1;
    }

    private class OnItemClickedListener implements EventsAdapter.OnItemClickListener {
        @Override
        public void onItemClicked(EventNameModel item) {
            Logger.log("EventsListItem", "Clicked: " + item.getName());
        }
    }

}
