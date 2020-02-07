package com.edge2.allevents;

/*
 * Copyright (C) 2020 Ritayan Chakraborty <ritayanout@gmail.com>
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
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.edge2.BaseFragment;
import com.edge2.OnFragmentScrollListener;
import com.edge2.R;
import com.edge2.allevents.models.BannerItemsModel;
import com.edge2.allevents.models.GroupsModel;
import com.edge2.allevents.models.HideEventsModel;
import com.edge2.allevents.models.QuickItemModel;
import com.edge2.allevents.recycler.EventsAdapter;
import com.edge2.allevents.recycler.ItemDecoration;
import com.edge2.data.DataViewModel;
import com.edge2.event.EventFragment;
import com.edge2.genericevents.GenericEventFragment;
import com.edge2.utils.DimenUtils;
import com.edge2.views.OnClickListener;
import com.example.a4.GameActivity;
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
    private View eventsHiddenView;
    private ImageView eventsHiddenImg;
    private Context context;
    private OnFragmentScrollListener listener;
    private ItemDecoration itemDecoration;
    private int appBarOffset;
    private boolean isIntra;
    private HideEventsModel hideEventsModel;
    @Nullable
    private View topViewEdge;
    @Nullable
    private View topViewIntra;
    private List<GroupsModel> allEventsList;
    private EventsAdapter eventsAdapter;
    @Nullable
    private QuickItemsAdapter quickAdapter;
    /**
     * Items that are actually in the banner. Items with no image are skipped.
     */
    private List<BannerItemsModel> itemsInBanner;
    private List<Slide> bannerItemslist;
    private View rootView;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        listener = (OnFragmentScrollListener) context;
        hideEventsModel = HideEventsModel.getFromPrefs(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        context = null;
        listener = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        postponeEnterTransition();
        Bundle args = getArguments();
        mainReycler = rootView.findViewById(R.id.main_recycler);
        eventsHiddenView = rootView.findViewById(R.id.wait_view);
        eventsHiddenImg = rootView.findViewById(R.id.wait_imageview);
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
            GravitySnapHelper snap = new GravitySnapHelper(Gravity.START);
            snap.setMaxFlingSizeFraction(0.2f);
            snap.attachToRecyclerView(quickReycler);
        }

        // Show the toolbar and bottomnav
        listener.onListScrolled(-1, Integer.MAX_VALUE);
        setupRecyclerOrHide();
        return rootView;
    }

    /**
     * Checks if events have been announced yet. If yes, shows the Events RecyclerView, else shows
     * an ImageView
     */
    private void setupRecyclerOrHide() {
        if (hideEventsModel.isHideEvents()) {
            mainReycler.setVisibility(View.GONE);
            eventsHiddenView.setVisibility(View.VISIBLE);
            if (hideEventsModel.getImgUrl() != null) {
                Glide.with(requireContext())
                        .load(hideEventsModel.getImgUrl())
                        .into(eventsHiddenImg);
            }
        } else {
            mainReycler.setVisibility(View.VISIBLE);
            eventsHiddenView.setVisibility(View.GONE);
            float itemSize = getResources().getDimensionPixelSize(R.dimen.allevents_main_events_img_w) +
                    2 * getResources().getDimension(R.dimen.allevents_main_events_padding_h);
            int columnCount = getRecyclerColumnCount(mainReycler, itemSize);
            RecyclerView.LayoutManager mainLayoutManager = new GridLayoutManager(context, columnCount);
            mainReycler.setHasFixedSize(true);
            mainReycler.setLayoutManager(mainLayoutManager);
        }
        showData();
    }

    @Override
    public void onResume() {
        super.onResume();
        observeIsHidden();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        topViewIntra = null;
        topViewEdge = null;
        if (banner != null) {
            banner.setItemClickListener(null);
            banner.onDestroy();
            banner = null;
        }
        mainReycler.setAdapter(null);
        mainReycler.removeItemDecoration(itemDecoration);
        itemDecoration = null;
        if (quickReycler != null)
            quickReycler.setAdapter(null);
        mainReycler = null;
        quickReycler = null;
        eventsHiddenView = null;
        eventsHiddenImg = null;
        rootView = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NestedScrollView scrollView = view.findViewById(R.id.scroll_view);
        setupInsets(view, scrollView);
    }

    private void setupInsets(View v, NestedScrollView scrollView) {
        View topView;
        View contentView;
        if (isIntra)
            topView = topViewIntra;
        else
            topView = topViewEdge;

        if (hideEventsModel.isHideEvents()) {
            contentView = eventsHiddenView;
            startPostponedEnterTransition();
        } else {
            contentView = mainReycler;
            // Needed because the shared element transition doesn't work on return unless
            // postponeEnterTransition() is called. And postponeEnterTransition needs
            // a corresponding startPostponedEnterTransition().
            mainReycler.postDelayed(this::startPostponedEnterTransition, 150);
        }

        setupWindowInsets(v, contentView, topView, true,
                true, (l, t, r, b) -> {

                    if (!isIntra) {
                        //noinspection ConstantConditions
                        quickReycler.setPadding(l, 0, r, 0);
                    }
                    //noinspection ConstantConditions
                    topView.post(() -> {
                        setupScrollListener(scrollView, topView.getHeight());
                    });

                    if (!hideEventsModel.isHideEvents()) {
                        if (itemDecoration != null)
                            mainReycler.removeItemDecoration(itemDecoration);
                        int itemMargins = context.getResources()
                                .getDimensionPixelSize(R.dimen.margin_huge);
                        itemDecoration =
                                new ItemDecoration(mainReycler.getLayoutManager(), b, itemMargins);
                        mainReycler.addItemDecoration(itemDecoration);
                    }
                });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_APPBAR_OFFSET, appBarOffset);
    }

    private void showData() {
        DataViewModel viewModel = ViewModelProviders.of(requireActivity()).get(DataViewModel.class);

        if (!hideEventsModel.isHideEvents()) {
            if (eventsAdapter == null) {
                viewModel.getGroups(isIntra).observe(this, events -> {
                    allEventsList = events;
                    eventsAdapter = new EventsAdapter(allEventsList, isIntra, this::onEventClicked);
                    mainReycler.setAdapter(eventsAdapter);
                });
            } else {
                mainReycler.setAdapter(eventsAdapter);
            }
        }

        observeBanner(viewModel);
        setQuickItems();
    }

    // Make sure it is safe to call getIsEventsHidden() multiple times, as observeIsHidden is
    // called both from onResume and onCreateView
    private void observeIsHidden() {
        DataViewModel viewModel = ViewModelProviders.of(requireActivity()).get(DataViewModel.class);
        viewModel.getIsEventsHidden().observe(this, hideEvents -> {
            hideEventsModel = hideEvents;
            setupRecyclerOrHide();
        });
    }

    private void setQuickItems() {
        if (!isIntra) {
            if (quickAdapter == null) {
                ArrayList<QuickItemModel> quickItems = new ArrayList<>();
                QuickItemModel quickItem = new QuickItemModel(getString(R.string.ca_title),
                        context.getDrawable(R.drawable.ic_ca),
                        getString(R.string.ca_desc));
                quickItems.add(quickItem);

                quickItem = new QuickItemModel(getString(R.string.game_title),
                        context.getDrawable(R.drawable.ic_game),
                        getString(R.string.game_desc));
                quickItems.add(quickItem);

                quickItem = new QuickItemModel(getString(R.string.upcoming_title),
                        context.getDrawable(R.drawable.ic_upcoming),
                        getString(R.string.upcoming_desc));
                quickItems.add(quickItem);
                quickItem = new QuickItemModel(getString(R.string.registration_title),
                        context.getDrawable(R.drawable.ic_reg),
                        getString(R.string.registration_desc));
                quickItems.add(quickItem);
                quickItem = new QuickItemModel(getString(R.string.team_title),
                        context.getDrawable(R.drawable.ic_team),
                        getString(R.string.team_desc));
                quickItems.add(quickItem);
                quickItem = new QuickItemModel(getString(R.string.about_title),
                        context.getDrawable(R.drawable.ic_about),
                        getString(R.string.about_desc));
                quickItems.add(quickItem);
                quickItem = new QuickItemModel(getString(R.string.accommodation_title),
                        context.getDrawable(R.drawable.ic_accommodation),
                        getString(R.string.accommodation_desc));
                quickItems.add(quickItem);
                quickItem = new QuickItemModel(getString(R.string.results_title),
                        context.getDrawable(R.drawable.ic_result),
                        getString(R.string.results_desc));
                quickItems.add(quickItem);
                quickItem = new QuickItemModel(getString(R.string.sponsors_title),
                        context.getDrawable(R.drawable.ic_sponsor),
                        getString(R.string.sponsors_desc));
                quickItems.add(quickItem);
                quickAdapter = new QuickItemsAdapter(quickItems, new OnQuickClickListener());
                //noinspection ConstantConditions
                quickReycler.addItemDecoration(new QuickItemDecorator());
            }
            //noinspection ConstantConditions
            quickReycler.setAdapter(quickAdapter);
        }
    }

    private class OnQuickClickListener implements OnClickListener {
        @Override
        public void onClick(int position, View root, View nameView, View descView,
                            View imageView, @Nullable View view5) {
            int actionRes;
            switch (position) {
                case 0:
                    actionRes = R.id.action_events_to_CA;
                    break;
                case 1:
                    startActivity(new Intent(requireContext(), GameActivity.class));
                    return;
                case 2:
                    actionRes = R.id.action_events_to_upcoming;
                    break;
                case 3:
                    actionRes = R.id.action_events_to_registration;
                    break;
                case 4:
                    actionRes = R.id.action_events_to_team;
                    break;
                case 5:
                    actionRes = R.id.action_events_to_about;
                    break;
                case 6:
                    actionRes = R.id.action_events_to_accommodation;
                    break;
                case 7:
                    actionRes = R.id.action_events_to_results;
                    break;
                case 8:
                    actionRes = R.id.action_events_to_sponsors;
                    break;
                default:
                    return;
            }

            String transitionNameName = getString(R.string.events_to_quick_title_transition);
            String transitionDescName = getString(R.string.events_to_quick_desc_transition);
            String transitionImgName = getString(R.string.events_to_quick_icon_transition);
            String transitionRootName = getString(R.string.events_to_quick_root_transition);

            // To add more shared views here, call "setTransitionName" in the adapter
            FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                    .addSharedElement(nameView, transitionNameName)
                    .addSharedElement(descView, transitionDescName)
                    .addSharedElement(imageView, transitionImgName)
                    .addSharedElement(root, transitionRootName)
                    .build();
            NavHostFragment.findNavController(EventsFragment.this)
                    .navigate(actionRes, null, null, extras);
        }
    }

    private void observeBanner(DataViewModel viewModel) {
        if (!isIntra) {
            if (bannerItemslist != null) {
                setBanner();
                return;
            }

            viewModel.getBanner().observe(this, items -> {
                itemsInBanner = new ArrayList<>(items.size());
                bannerItemslist = new ArrayList<>(items.size());
                for (int i = 0; i < items.size(); i++) {
                    BannerItemsModel item = items.get(i);
                    Uri uri = item.getImageUri();
                    if (uri != null) {
                        bannerItemslist.add(new Slide(i, uri, 0));
                        itemsInBanner.add(item);
                    }
                }
                setBanner();
            });
        }
    }

    private void setBanner() {
        OnBannerItemClickedListener bannerListener = new OnBannerItemClickedListener();
        //noinspection ConstantConditions
        banner.setItemClickListener(bannerListener);
        banner.addSlides(bannerItemslist);
    }

    private int getRecyclerColumnCount(View child, float pxWidth) {
        int totalPadding = rootView.getPaddingRight() + rootView.getPaddingLeft()
                + child.getPaddingRight() + child.getPaddingLeft();
        pxWidth += child.getPaddingRight() + child.getPaddingLeft();
        int screenWidth = DimenUtils.getWindowWidth(context) - totalPadding;
        int columnCount = (int) Math.floor(screenWidth / pxWidth) - 1;
        return columnCount <= 1 ? 2 : columnCount;
    }

    class OnBannerItemClickedListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (itemsInBanner != null) {
                BannerItemsModel item = itemsInBanner.get(position);

                Bundle args = new Bundle();
                if (item.getImageUri() == null) {
                    if (item.getIconUri() != null) {
                        args.putString(GenericEventFragment.KEY_EVENT_IMG, item.getIconUri().toString());
                    }
                } else {
                    args.putString(GenericEventFragment.KEY_EVENT_IMG, item.getImageUri().toString());
                }
                args.putString(GenericEventFragment.KEY_EVENT_NAME, item.getName());
                args.putString(GenericEventFragment.KEY_EVENT_SCHED, item.getSched());
                args.putString(GenericEventFragment.KEY_EVENT_DESC, item.getDesc());
                args.putBoolean(GenericEventFragment.KEY_EVENT_IS_MEGA, item.getMega());

                NavHostFragment.findNavController(EventsFragment.this)
                        .navigate(R.id.action_events_to_genericEvent, args, null, null);

            }
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
        GroupsModel item = allEventsList.get(position);
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
