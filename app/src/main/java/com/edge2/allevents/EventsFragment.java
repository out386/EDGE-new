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
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.edge2.MainActivity;
import com.edge2.R;
import com.edge2.allevents.models.EventModel;
import com.edge2.allevents.models.QuickItemModel;
import com.edge2.allevents.recycler.EventsAdapter;
import com.edge2.allevents.recycler.ItemDecoration;
import com.edge2.utils.DimenUtils;
import com.edge2.utils.Logger;
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;
import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.List;

import ir.apend.slider.model.Slide;
import ir.apend.slider.ui.Slider;

public class EventsFragment extends Fragment {

    private static final String KEY_APPBAR_OFFSET = "appBarOffset";

    private AppBarLayout appBarLayout;
    private RecyclerView mainReycler;
    private RecyclerView quickReycler;
    private EventsViewModel viewModel;
    private Slider banner;
    private Context context;
    private OnEventsFragmentListener listener;
    private ItemDecoration itemDecoration;
    private int appBarOffset;
    private OnAppbarOffsetChangedListener appbarListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        listener = (OnEventsFragmentListener) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        postponeEnterTransition();

        if (savedInstanceState != null) {
            appBarOffset = savedInstanceState.getInt(KEY_APPBAR_OFFSET);
        }

        appBarLayout = rootView.findViewById(R.id.app_bar_layout);
        mainReycler = rootView.findViewById(R.id.main_recycler);
        quickReycler = rootView.findViewById(R.id.quick_recycler);
        new GravitySnapHelper(Gravity.START).attachToRecyclerView(quickReycler);
        banner = rootView.findViewById(R.id.top_banner);
        banner.setTargetWidth(getResources().getDimensionPixelSize(R.dimen.allevents_banner_w));

        float itemSize = getResources().getDimensionPixelSize(R.dimen.allevents_main_events_img_w) +
                2 * getResources().getDimension(R.dimen.allevents_main_events_padding_h);
        int columnCount = getRecyclerColumnCount(rootView, mainReycler, itemSize);
        RecyclerView.LayoutManager mainLayoutManager = new GridLayoutManager(context, columnCount);
        mainReycler.setHasFixedSize(true);
        mainReycler.setLayoutManager(mainLayoutManager);

        RecyclerView.LayoutManager quickLayoutManager = new LinearLayoutManager(
                context, RecyclerView.HORIZONTAL, false);
        quickReycler.setHasFixedSize(true);
        quickReycler.setLayoutManager(quickLayoutManager);

        ((MainActivity) requireActivity()).setupToolbar(rootView.findViewById(R.id.toolbar));
        setupInsets(rootView);
        setupRecyclerListeners();
        setupObservers();
        prototype();
        return rootView;
    }

    private void setupInsets(View v) {
        CoordinatorLayout.LayoutParams appbarParams =
                (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        LinearLayout.LayoutParams bannerParams =
                (LinearLayout.LayoutParams) banner.getLayoutParams();
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

            mainReycler.setPadding(leftInset, 0, rightInset, 0);
            quickReycler.setPadding(leftInset, 0, rightInset, 0);

            if (itemDecoration != null)
                mainReycler.removeItemDecoration(itemDecoration);
            itemDecoration =
                    new ItemDecoration(mainReycler.getLayoutManager(), bottomInset, topInset);
            mainReycler.addItemDecoration(itemDecoration);

            appBarLayout.post(() -> {
                appbarParams.height = appBarLayout.getHeight() + toolbarParams.height;
                appBarLayout.setLayoutParams(appbarParams);
                bannerParams.setMargins(leftInset, toolbarParams.height, rightInset, 0);
                banner.setLayoutParams(bannerParams);
                int currOffset = appBarLayout.getBottom() - appBarLayout.getHeight();
                AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) appbarParams.getBehavior();
                behavior.onNestedPreScroll(v.findViewById(R.id.main_coordinator),
                        appBarLayout, null, 0, -1 * appBarOffset + currOffset, new int[]{0, 0}, 0);
                setAppbarListener(appBarLayout);
            });
            return insets;
        });
    }

    private void setAppbarListener(AppBarLayout appBarLayout) {
        if (appbarListener == null)
            appBarLayout.addOnOffsetChangedListener(new OnAppbarOffsetChangedListener());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_APPBAR_OFFSET, appBarOffset);
        // show the bottomnav in preparation for a new fragment to be shown
        listener.onEventsScrolled(-1);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        appBarLayout.removeOnOffsetChangedListener(appbarListener);
        // show the bottomnav in preparation for a new fragment to be shown
        listener.onEventsScrolled(-1);
    }

    private void setupRecyclerListeners() {
        mainReycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                listener.onEventsScrolled(dy);
            }
        });

        // Needed because the shared element transition doesn't work on return unless
        // postponeEnterTransition() is called. And postponeEnterTransition needs a corresponding
        // startPostponedEnterTransition()
        ViewTreeObserver viewTreeObserver = mainReycler.getViewTreeObserver();
        viewTreeObserver
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (mainReycler.getMeasuredWidth() > 0 && mainReycler.getMeasuredHeight() > 0) {
                            if (viewTreeObserver.isAlive())
                                viewTreeObserver.removeOnGlobalLayoutListener(this);
                            startPostponedEnterTransition();
                        }
                    }
                });
    }

    private void prototype() {
        ArrayList<EventModel> events = new ArrayList<>();
        String template = context.getString(R.string.num_sub_events);
        for (int j = 0; j < 12; j++) {
            EventModel event = new EventModel("ComputeAid",
                    context.getDrawable(R.drawable.computeaid), 4, template);
            events.add(event);
        }
        EventsAdapter eventsAdapter = new EventsAdapter(events, new EventsClickedListener());
        mainReycler.setAdapter(eventsAdapter);


        ArrayList<QuickItemModel> quickItems = new ArrayList<>();
        for (int j = 0; j < 5; j++) {
            QuickItemModel item = new QuickItemModel("Accommodations",
                    context.getDrawable(R.drawable.quick_accomodation),
                    "Registrations are now open");
            quickItems.add(item);
        }
        QuickItemsAdapter quickAdapter = new QuickItemsAdapter(quickItems,
                position -> Logger.log("EventListener", "onQuickClicked: " + position));
        quickReycler.setAdapter(quickAdapter);
        quickReycler.addItemDecoration(new QuickItemDecorator());
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
        int columnCount = (int) Math.floor(screenWidth / pxWidth) - 1;
        return columnCount <= 1 ? 2 : columnCount;
    }

    class OnBannerItemClickedListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Logger.log("BannerListener", "onItemClick: " + position);
        }
    }

    private class QuickItemDecorator extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                   @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            int margin = getResources().getDimensionPixelSize(R.dimen.margin);
            int marginLarge = getResources().getDimensionPixelSize(R.dimen.margin_large);
            RecyclerView.Adapter adapter = parent.getAdapter();
            if (parent.getChildAdapterPosition(view) == 0)
                outRect.left = marginLarge;
            else
                outRect.left = margin;
            if (adapter != null &&
                    parent.getChildAdapterPosition(view) == adapter.getItemCount() - 1)
                outRect.right = marginLarge;
        }
    }

    public interface OnEventsFragmentListener {
        void onEventsScrolled(int dy);
    }

    class EventsClickedListener implements EventsAdapter.OnEventsClickedListener {
        @Override
        public void onEventClicked(int position, View imageView, View rootView) {
            String transitionName = getString(R.string.events_to_sub_transition);
            String rootTransitionName = getString(R.string.events_to_sub_root_transition);
            FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                    .addSharedElement(imageView, transitionName)
                    .addSharedElement(rootView, rootTransitionName)
                    .build();
            NavHostFragment.findNavController(EventsFragment.this)
                    .navigate(R.id.action_events_to_subEvents, null, null, extras);
        }
    }

    class OnAppbarOffsetChangedListener implements AppBarLayout.OnOffsetChangedListener {
        private int previousOffset = Integer.MIN_VALUE;

        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            appBarOffset = verticalOffset;
            if (previousOffset != Integer.MIN_VALUE && previousOffset != verticalOffset) {
                // Needed because the RecyclerView's OnScrollListener doesn't help when the appbar is scrolling
                listener.onEventsScrolled(previousOffset - verticalOffset);
            }
            previousOffset = verticalOffset;
        }
    }

}
