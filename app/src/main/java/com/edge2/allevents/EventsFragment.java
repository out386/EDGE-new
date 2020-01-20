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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.edge2.BaseFragment;
import com.edge2.OnFragmentScrollListener;
import com.edge2.R;
import com.edge2.allevents.models.EventModel;
import com.edge2.allevents.models.QuickItemModel;
import com.edge2.allevents.recycler.EventsAdapter;
import com.edge2.allevents.recycler.ItemDecoration;
import com.edge2.event.EventFragment;
import com.edge2.utils.DimenUtils;
import com.edge2.utils.Logger;
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;

import java.util.ArrayList;
import java.util.List;

import ir.apend.slider.model.Slide;
import ir.apend.slider.ui.Slider;

public class EventsFragment extends BaseFragment {
    public static final String KEY_IS_INTRA = "isIntra";
    private static final String KEY_APPBAR_OFFSET = "appBarOffset";

    private RecyclerView mainReycler;
    @Nullable
    private RecyclerView quickReycler;
    @Nullable
    private Slider banner;
    private Context context;
    private OnFragmentScrollListener listener;
    private ItemDecoration itemDecoration;
    private int appBarOffset;
    private boolean isIntra;
    @Nullable
    private View topViewEdge;
    @Nullable
    private View topViewIntra;
    private ArrayList<EventModel> allEventsList;
    private EventsAdapter eventsAdapter;
    @Nullable
    private QuickItemsAdapter quickAdapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        listener = (OnFragmentScrollListener) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        postponeEnterTransition();
        Bundle args = getArguments();
        if (args != null)
            isIntra = args.getBoolean(KEY_IS_INTRA);
        if (savedInstanceState != null) {
            appBarOffset = savedInstanceState.getInt(KEY_APPBAR_OFFSET);
        }

        if (isIntra) {
            topViewIntra = rootView.findViewById(R.id.top_view_intra);
            topViewIntra.setVisibility(View.VISIBLE);
        } else {
            topViewEdge = rootView.findViewById(R.id.top_view_edge);
            topViewEdge.setVisibility(View.VISIBLE);
            banner = rootView.findViewById(R.id.top_banner);
            banner.setTargetWidth(getResources().getDimensionPixelSize(R.dimen.allevents_banner_w));
            quickReycler = rootView.findViewById(R.id.quick_recycler);
            quickReycler.setHasFixedSize(true);
            RecyclerView.LayoutManager quickLayoutManager = new LinearLayoutManager(
                    context, RecyclerView.HORIZONTAL, false);
            quickReycler.setLayoutManager(quickLayoutManager);

        }
        mainReycler = rootView.findViewById(R.id.main_recycler);
        new GravitySnapHelper(Gravity.START).attachToRecyclerView(quickReycler);

        float itemSize = getResources().getDimensionPixelSize(R.dimen.allevents_main_events_img_w) +
                2 * getResources().getDimension(R.dimen.allevents_main_events_padding_h);
        int columnCount = getRecyclerColumnCount(rootView, mainReycler, itemSize);
        RecyclerView.LayoutManager mainLayoutManager = new GridLayoutManager(context, columnCount);
        mainReycler.setHasFixedSize(true);
        mainReycler.setLayoutManager(mainLayoutManager);

        // Show the toolbar and bottomnav
        listener.onListScrolled(-1, Integer.MAX_VALUE);

        setupRecyclerListeners();
        setupObservers();
        prototype();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NestedScrollView scrollView = view.findViewById(R.id.scroll_view);
        setupInsets(view, scrollView);
    }

    private void setupInsets(View v, NestedScrollView scrollView) {
        View topView;
        if (isIntra) {
            topView = topViewIntra;
        } else {
            topView = topViewEdge;
        }
        setupWindowInsets(v, mainReycler, topView, true,
                true, (l, t, r, b) -> {

                    if (!isIntra) {
                        //noinspection ConstantConditions
                        quickReycler.setPadding(l, 0, r, 0);
                    }
                    //noinspection ConstantConditions
                    topView.post(() -> {
                        setupScrollListener(scrollView, topView.getHeight());
                    });

                    if (itemDecoration != null)
                        mainReycler.removeItemDecoration(itemDecoration);
                    int itemMargins = context.getResources()
                            .getDimensionPixelSize(R.dimen.margin_huge);
                    itemDecoration =
                            new ItemDecoration(mainReycler.getLayoutManager(), b, itemMargins);
                    mainReycler.addItemDecoration(itemDecoration);
                });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_APPBAR_OFFSET, appBarOffset);
    }

    private void setupRecyclerListeners() {
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
        if (eventsAdapter == null) {
            allEventsList = new ArrayList<>();
            String template;
            String desc;
            String name;
            int iconRes = R.drawable.ic_rrc;
            int iconRes2 = R.drawable.ic_rrc;
            if (isIntra) {
                template = context.getString(R.string.sub_events_num);
                name = "Robotics";
                desc = "Some (hopefully) long description to fill this space with, but not long enough to overflow on some devices.";
            } else {
                template = context.getString(R.string.sub_events_num);
                name = "ComputeAid";
                desc = "Anyone can write code that a computer can understand, but good programmers write code that humans can understand.";
            }
            for (int j = 0; j < 12; j++) {
                EventModel event;
                if (j % 2 == 0)
                    event = new EventModel(name, iconRes, 4, template, desc);
                else
                    event = new EventModel(name, iconRes2, 4, template, desc);
                allEventsList.add(event);
            }
            eventsAdapter = new EventsAdapter(allEventsList, this::onEventClicked);
        }
        mainReycler.setAdapter(eventsAdapter);

        if (!isIntra) {
            if (quickAdapter == null) {
                ArrayList<QuickItemModel> quickItems = new ArrayList<>();
                for (int j = 0; j < 5; j++) {
                    QuickItemModel item = new QuickItemModel("Accommodations",
                            context.getDrawable(R.drawable.quick_accomodation),
                            "Registrations are now open");
                    quickItems.add(item);
                }
                quickAdapter = new QuickItemsAdapter(quickItems,
                        (position, v1, v2, v3, v4, v5) ->
                                Logger.log("EventListener", "onQuickClicked: " + position));
            }
            //noinspection ConstantConditions
            quickReycler.setAdapter(quickAdapter);
            quickReycler.addItemDecoration(new QuickItemDecorator());
        }
    }

    private void setupObservers() {
        if (!isIntra) {
            OnBannerItemClickedListener bannerListener = new OnBannerItemClickedListener();
            EventsViewModel viewModel = ViewModelProviders.of(this)
                    .get(EventsViewModel.class);
            viewModel.getBanner().observe(this, eventNameModels -> {
                List<Slide> list = new ArrayList<>(eventNameModels.size());
                for (int i = 0; i < eventNameModels.size(); i++) {
                    list.add(new Slide(i, eventNameModels.get(i).getImg(), 0));
                }
                //noinspection ConstantConditions
                banner.setItemClickListener(bannerListener);
                banner.addSlides(list);
            });
        }
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

    private void onEventClicked(int position, View rootView, View imageView,
                                View nameView, View countView, View v5) {
        EventModel item = allEventsList.get(position);
        String transitionImgName = getString(R.string.events_to_sub_img_transition);
        String transitionNameName = getString(R.string.events_to_sub_name_transition);
        String transitionRootName = getString(R.string.events_to_sub_root_transition);

        // To add more shared views here, call "setTransitionName" in the adapter
        FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                .addSharedElement(imageView, transitionImgName)
                .addSharedElement(nameView, transitionNameName)
                .addSharedElement(rootView, transitionRootName)
                .build();

        Bundle args = new Bundle();
        args.putString(EventFragment.KEY_CAT_NAME, item.getName());
        args.putString(EventFragment.KEY_CAT_DESC, item.getDesc());
        args.putInt(EventFragment.KEY_CAT_IMAGE, item.getImage());
        args.putBoolean(KEY_IS_INTRA, isIntra);

        NavHostFragment.findNavController(EventsFragment.this)
                .navigate(R.id.action_events_to_subEvents, args, null, extras);
    }

}
