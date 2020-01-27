package com.edge2.upcoming;

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
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Transition;

import com.edge2.BaseFragment;
import com.edge2.R;
import com.edge2.allevents.models.BannerItemsModel;
import com.edge2.data.DataViewModel;
import com.edge2.genericevents.GenericEventFragment;
import com.edge2.transitions.MoveTransition;
import com.edge2.upcoming.recycler.ItemDecoration;
import com.edge2.upcoming.recycler.UpcomingAdapter;
import com.edge2.views.GeneralHeaderView;

import java.util.List;

public class UpcomingFragment extends BaseFragment {
    private RecyclerView mainRecycler;
    private UpcomingAdapter mainAdapter;
    private ItemDecoration itemDecoration;
    private Context context;
    private List<BannerItemsModel> eventsList;
    private MoveTransition transition;
    private boolean isTransitionFinished;
    private OnSharedElementListener sharedElementListener;
    private GeneralHeaderView topView;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        context = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        postponeEnterTransition();
        if (savedInstanceState != null)
            isTransitionFinished = true;
        View view = inflater.inflate(R.layout.fragment_upcoming, container, false);
        mainRecycler = view.findViewById(R.id.upcoming_content);
        topView = view.findViewById(R.id.top_view);
        mainRecycler.setLayoutManager(new LinearLayoutManager(context));
        transition = new MoveTransition(null);
        setSharedElementEnterTransition(transition);
        setSharedElementReturnTransition(transition);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mainRecycler.setAdapter(null);
        mainRecycler.removeItemDecoration(itemDecoration);
        itemDecoration = null;
        mainRecycler = null;
        if (sharedElementListener != null) {
            transition.removeListener(sharedElementListener);
            sharedElementListener = null;
        }
        transition.onDestroy();
        transition = null;
        topView = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Show the toolbar
        onFragmentScrollListener.onListScrolled(0, Integer.MAX_VALUE);

        topView.setNameTransition(getString(R.string.events_to_quick_title_transition));
        topView.setDescTransition(getString(R.string.events_to_quick_desc_transition));
        topView.setIconTransition(getString(R.string.events_to_quick_icon_transition));

        if (sharedElementListener != null) {
            transition.removeListener(sharedElementListener);
        }
        sharedElementListener = new OnSharedElementListener(topView);
        transition.addListener(sharedElementListener);

        NestedScrollView scroll = view.findViewById(R.id.upcoming_scroll);

        topView.post(() ->
                setupScrollListener(scroll, topView.getHeight()));

        setupInsets(view, topView);
        if (isTransitionFinished) {
            topView.showImage(0);
            setData();
        }
    }

    private void setupInsets(View v, View topView) {
        mainRecycler.postDelayed(this::startPostponedEnterTransition, 150);

        setupWindowInsets(v, mainRecycler, topView, false,
                true, (l, t, r, b) -> {
                    int itemMargin = context.getResources()
                            .getDimensionPixelSize(R.dimen.margin_huge);
                    int itemPadding = context.getResources()
                            .getDimensionPixelSize(R.dimen.margin_large);
                    if (itemDecoration != null)
                        mainRecycler.removeItemDecoration(itemDecoration);
                    itemDecoration = new ItemDecoration(b, itemMargin - itemPadding);
                    mainRecycler.addItemDecoration(itemDecoration);
                });
    }

    private void setData() {
        if (mainAdapter == null) {
            DataViewModel viewModel = ViewModelProviders.of(this)
                    .get(DataViewModel.class);
            viewModel.getUpcoming().observe(this, events -> {
                eventsList = events;
                mainAdapter = new UpcomingAdapter(events, this::onEventClicked);
                mainRecycler.setAdapter(mainAdapter);
                mainRecycler.scheduleLayoutAnimation();
            });
        } else {
            //Only play the animation when this fragment is first started (not on backstack pop)
            if (isTransitionFinished)
                mainRecycler.setLayoutAnimation(null);
            mainRecycler.setAdapter(mainAdapter);
            mainRecycler.scheduleLayoutAnimation();
        }
    }

    private void onEventClicked(int position, View rootView, View imageView, View nameView,
                                View descView, View button) {

        BannerItemsModel item = eventsList.get(position);
        Bundle args = new Bundle();
        if (item.getImageUri() == null) {
            if (item.getIconUri() != null)
                args.putString(GenericEventFragment.KEY_EVENT_IMG, item.getIconUri().toString());
        } else {
            args.putString(GenericEventFragment.KEY_EVENT_IMG, item.getImageUri().toString());
        }
        args.putString(GenericEventFragment.KEY_EVENT_NAME, item.getName());
        args.putString(GenericEventFragment.KEY_EVENT_DESC, item.getDesc());
        args.putString(GenericEventFragment.KEY_EVENT_SCHED, item.getSched());
        args.putInt(GenericEventFragment.KEY_EVENT_IS_MEGA, 0);

        // To add more shared views here, call "setTransitionName" in the adapter
        /*String transitionImgName = getString(R.string.sub_to_details_img_transition);
        String transitionNameName = getString(R.string.sub_to_details_name_transition);
        String transitionDescName = getString(R.string.sub_to_details_desc_transition);
        String transitionRootName = getString(R.string.sub_to_details_root_transition);
        String transitionButtonName = getString(R.string.sub_to_details_button_transition);
        FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                .addSharedElement(imageView, transitionImgName)
                .addSharedElement(nameView, transitionNameName)
                .addSharedElement(descView, transitionDescName)
                .addSharedElement(rootView, transitionRootName)
                .addSharedElement(button, transitionButtonName)
                .build();*/

        NavHostFragment.findNavController(this)
                .navigate(R.id.action_upcoming_to_genericEvent, args, null, null);
    }


    private class OnSharedElementListener implements Transition.TransitionListener {
        private int animTime;
        private GeneralHeaderView topView;

        OnSharedElementListener(GeneralHeaderView topView) {
            animTime = getResources().getInteger(android.R.integer.config_mediumAnimTime);
            this.topView = topView;
        }

        @Override
        public void onTransitionStart(@NonNull Transition transition) {
        }

        @Override
        public void onTransitionEnd(@NonNull Transition transition) {
            topView.showImage(animTime);
            // The recyclerview is populated here, so that the item animation plays after the transition
            setData();
            isTransitionFinished = true;
        }

        @Override
        public void onTransitionCancel(@NonNull Transition transition) {

        }

        @Override
        public void onTransitionPause(@NonNull Transition transition) {

        }

        @Override
        public void onTransitionResume(@NonNull Transition transition) {

        }
    }
}
