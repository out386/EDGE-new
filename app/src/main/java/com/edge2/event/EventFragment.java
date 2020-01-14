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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Transition;
import androidx.transition.TransitionInflater;

import com.edge2.OnFragmentScrollListener;
import com.edge2.R;
import com.edge2.event.recycler.EventCategoryAdapter;
import com.edge2.event.recycler.ItemDecoration;
import com.edge2.utils.DimenUtils;

import java.util.ArrayList;

public class EventFragment extends Fragment {
    private OnFragmentScrollListener listener;
    private RecyclerView mainReycler;
    private int topViewHeight;
    private ItemDecoration itemDecoration;
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (OnFragmentScrollListener) context;
        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        postponeEnterTransition();

        View rootView = inflater.inflate(R.layout.fragment_event, container, false);
        mainReycler = rootView.findViewById(R.id.eventcat_content);
        mainReycler.setHasFixedSize(true);
        mainReycler.setLayoutManager(new LinearLayoutManager(context));

        Transition transition = TransitionInflater.from(context)
                .inflateTransition(android.R.transition.move);
        setSharedElementEnterTransition(transition);
        setSharedElementReturnTransition(transition);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupInsets(view);
        prototype();
    }

    private void setupInsets(View v) {
        v.setOnApplyWindowInsetsListener((v1, insets) -> {
            int topInset = insets.getSystemWindowInsetTop();
            int leftInset = insets.getSystemWindowInsetLeft();
            int rightInset = insets.getSystemWindowInsetRight();
            int bottomInset = insets.getSystemWindowInsetBottom();

            mainReycler.setPadding(leftInset, 0, rightInset, 0);
            int itemMargin = context.getResources()
                    .getDimensionPixelSize(R.dimen.margin_huge);
            int itemPadding = context.getResources()
                    .getDimensionPixelSize(R.dimen.margin_large);
            if (itemDecoration != null)
                mainReycler.removeItemDecoration(itemDecoration);
            itemDecoration = new ItemDecoration(bottomInset, itemMargin - itemPadding);
            mainReycler.addItemDecoration(itemDecoration);

            RelativeLayout topView = v.findViewById(R.id.eventcat_top);
            View divider = v.findViewById(R.id.divider);

            topView.post(() -> {
                LinearLayout.LayoutParams topViewParams =
                        (LinearLayout.LayoutParams) topView.getLayoutParams();
                int toolbarHeight = DimenUtils.getActionbarHeight(context);
                int dividerHeight = ((LinearLayout.LayoutParams) divider.getLayoutParams()).topMargin
                        + divider.getHeight();

                topViewParams.setMargins(leftInset, toolbarHeight + topInset, rightInset, 0);
                topView.setLayoutParams(topViewParams);

                // Hide the toolbar
                //listener.onListScrolled(0, -Integer.MAX_VALUE);
                listener.onListScrolled(0, Integer.MAX_VALUE);
                topViewHeight = topView.getHeight() + dividerHeight;
                startPostponedEnterTransition();
            });
            setupScrollListener(v);
            return insets;
        });

        // No idea why onApplyWindowInsets never fires without this.
        if (v.getRootWindowInsets() != null)
            v.dispatchApplyWindowInsets(v.getRootWindowInsets());
    }

    private void prototype() {
        ArrayList<EventCategoryModel> events = new ArrayList<>();
        for (int j = 0; j < 10; j++) {
            EventCategoryModel event;
            if (j % 2 == 0)
                event = new EventCategoryModel("Crypto Quest",
                        context.getDrawable(R.drawable.event_icon),
                        "Put your cryptography and deciphering skills to the test by proving yourself while solving the clues.");
            else
                event = new EventCategoryModel("Bug Hunt",
                        context.getDrawable(R.drawable.event_icon),
                        "Some dummy short description");
            events.add(event);
        }
        EventCategoryAdapter eventsAdapter = new EventCategoryAdapter(events, this::onEventClicked);
        mainReycler.setAdapter(eventsAdapter);
    }

    private void onEventClicked(int position, View rootView, View imageView,
                                View nameView, View countView) {
    }

    private void setupScrollListener(View rootView) {
        NestedScrollView scrollView = rootView.findViewById(R.id.scroll_view);
        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (v, scrollX, scrollY, oldScrollX, oldScrollY) ->
                        listener.onListScrolled(
                                scrollY - oldScrollY, topViewHeight - scrollY));
    }
}