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
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.edge2.allevents.models.BannerItemsModel;
import com.edge2.allevents.models.GroupsModel;
import com.edge2.allevents.models.QuickItemModel;
import com.edge2.allevents.recycler.EventsAdapter;
import com.edge2.allevents.recycler.ItemDecoration;
import com.edge2.data.DataViewModel;
import com.edge2.event.EventFragment;
import com.edge2.genericevents.GenericEventFragment;
import com.edge2.utils.DimenUtils;
import com.edge2.views.OnClickListener;
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
    private List<GroupsModel> allEventsList;
    private EventsAdapter eventsAdapter;
    @Nullable
    private QuickItemsAdapter quickAdapter;
    /**
     * All available banner items.
     */
    private List<BannerItemsModel> bannerItemsModels;
    /**
     * Items that are actually in the banner. Items with no image are skipped.
     */
    private List<BannerItemsModel> itemsInBanner;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        listener = (OnFragmentScrollListener) context;
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
            new GravitySnapHelper(Gravity.START).attachToRecyclerView(quickReycler);
        }
        mainReycler = rootView.findViewById(R.id.main_recycler);

        float itemSize = getResources().getDimensionPixelSize(R.dimen.allevents_main_events_img_w) +
                2 * getResources().getDimension(R.dimen.allevents_main_events_padding_h);
        int columnCount = getRecyclerColumnCount(rootView, mainReycler, itemSize);
        RecyclerView.LayoutManager mainLayoutManager = new GridLayoutManager(context, columnCount);
        mainReycler.setHasFixedSize(true);
        mainReycler.setLayoutManager(mainLayoutManager);

        // Show the toolbar and bottomnav
        listener.onListScrolled(-1, Integer.MAX_VALUE);

        showData();
        return rootView;
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

        // Needed because the shared element transition doesn't work on return unless
        // postponeEnterTransition() is called. And postponeEnterTransition needs
        // a corresponding startPostponedEnterTransition().
        mainReycler.postDelayed(this::startPostponedEnterTransition, 150);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_APPBAR_OFFSET, appBarOffset);
    }

    private void showData() {
        DataViewModel viewModel = ViewModelProviders.of(this).get(DataViewModel.class);

        if (eventsAdapter == null) {
            viewModel.getGroups(isIntra).observe(this, events -> {
                allEventsList = events;
                eventsAdapter = new EventsAdapter(allEventsList, isIntra, this::onEventClicked);
                mainReycler.setAdapter(eventsAdapter);
            });
        } else {
            mainReycler.setAdapter(eventsAdapter);
        }

        setupObservers(viewModel);
        setQuickItems();
    }

    private void setQuickItems() {
        if (!isIntra) {
            if (quickAdapter == null) {
                ArrayList<QuickItemModel> quickItems = new ArrayList<>();
                QuickItemModel quickItem = new QuickItemModel(getString(R.string.team_title),
                        context.getDrawable(R.drawable.ic_team),
                        getString(R.string.team_desc));
                quickItems.add(quickItem);
                quickItem = new QuickItemModel(getString(R.string.about_title),
                        context.getDrawable(R.drawable.ic_about),
                        getString(R.string.about_desc));
                quickItems.add(quickItem);
                quickItem = new QuickItemModel(getString(R.string.upcoming_title),
                        context.getDrawable(R.drawable.ic_upcoming),
                        getString(R.string.upcoming_desc));
                quickItems.add(quickItem);
                quickItem = new QuickItemModel(getString(R.string.accommodation_title),
                        context.getDrawable(R.drawable.ic_accommodation),
                        getString(R.string.accommodation_desc));
                quickItems.add(quickItem);

                for (int j = 0; j < 2; j++) {
                    QuickItemModel item = new QuickItemModel("Accommodations",
                            context.getDrawable(R.drawable.quick_accomodation),
                            "Registrations are now open");
                    quickItems.add(item);
                }
                quickAdapter = new QuickItemsAdapter(quickItems, new OnQuickClickListener());
            }
            //noinspection ConstantConditions
            quickReycler.setAdapter(quickAdapter);
            quickReycler.addItemDecoration(new QuickItemDecorator());
        }
    }

    private class OnQuickClickListener implements OnClickListener {
        @Override
        public void onClick(int position, View root, View nameView, View descView,
                            View imageView, @Nullable View view5) {
            int actionRes;
            switch (position) {
                case 0:
                    actionRes = R.id.action_events_to_team;
                    break;
                case 1:
                    actionRes = R.id.action_events_to_about;
                    break;
                case 2:
                    actionRes = R.id.action_events_to_upcoming;
                    break;
                case 3:
                    actionRes = R.id.action_events_to_accommodation;
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

    private void setupObservers(DataViewModel viewModel) {
        if (!isIntra) {
            OnBannerItemClickedListener bannerListener = new OnBannerItemClickedListener();
            viewModel.getBanner().observe(this, items -> {
                bannerItemsModels = items;
                itemsInBanner = new ArrayList<>(items.size());
                List<Slide> list = new ArrayList<>(items.size());
                for (int i = 0; i < items.size(); i++) {
                    BannerItemsModel item = items.get(i);
                    Uri uri = item.getImageUri();
                    if (uri != null) {
                        list.add(new Slide(i, uri, 0));
                        itemsInBanner.add(item);
                    }
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
            if (itemsInBanner != null) {
                BannerItemsModel item = itemsInBanner.get(position);
                String transitionImgName = getString(R.string.events_to_sub_img_transition);
                String transitionNameName = getString(R.string.events_to_sub_name_transition);
                String transitionRootName = getString(R.string.events_to_sub_root_transition);

                // To add more shared views here, call "setTransitionName" in the adapter
                /*FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                        .addSharedElement(imageView, transitionImgName)
                        .addSharedElement(nameView, transitionNameName)
                        .addSharedElement(rootView, transitionRootName)
                        .build();*/

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
