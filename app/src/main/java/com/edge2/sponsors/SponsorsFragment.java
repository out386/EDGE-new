package com.edge2.sponsors;

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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Transition;

import com.edge2.BaseFragment;
import com.edge2.R;
import com.edge2.data.DataViewModel;
import com.edge2.sponsors.recycler.ItemDecoration;
import com.edge2.sponsors.recycler.SponsorsAdapter;
import com.edge2.transitions.MoveTransition;
import com.edge2.views.GeneralHeaderView;

public class SponsorsFragment extends BaseFragment {
    private RecyclerView mainRecycler;
    private TextView fetchFailView;
    private SponsorsAdapter mainAdapter;
    private ItemDecoration itemDecoration;
    private Context context;
    private MoveTransition transition;
    private boolean isTransitionFinished;
    private OnSharedElementListener sharedElementListener;
    private GeneralHeaderView topView;
    private GridLayoutManager layoutManager;

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
        View view = inflater.inflate(R.layout.fragment_sponsors, container, false);
        mainRecycler = view.findViewById(R.id.sponsors_content);
        fetchFailView = view.findViewById(R.id.sponsors_fail);
        topView = view.findViewById(R.id.top_view);
        layoutManager = new GridLayoutManager(context, 2);
        mainRecycler.setLayoutManager(layoutManager);
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
        mainRecycler.setLayoutManager(null);
        itemDecoration = null;
        mainRecycler = null;
        layoutManager = null;
        if (sharedElementListener != null) {
            transition.removeListener(sharedElementListener);
            sharedElementListener = null;
        }
        transition.onDestroy();
        transition = null;
        topView = null;
        fetchFailView = null;
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

        topView.post(() ->
                setupScrollListener((NestedScrollView) view, topView.getHeight()));

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
                    if (itemDecoration != null)
                        mainRecycler.removeItemDecoration(itemDecoration);
                    itemDecoration =
                            new ItemDecoration(layoutManager, b, itemMargin);
                    mainRecycler.addItemDecoration(itemDecoration);
                });
    }

    private void setData() {
        if (mainAdapter == null) {
            DataViewModel viewModel = ViewModelProviders.of(requireActivity())
                    .get(DataViewModel.class);
            viewModel.getSponsors().observe(this, sponsors -> {
                if (sponsors == null) {
                    mainRecycler.setVisibility(View.GONE);
                    fetchFailView.setText(getString(R.string.sponsor_fetch_fail));
                    fetchFailView.setVisibility(View.VISIBLE);
                } else {
                    mainRecycler.setVisibility(View.VISIBLE);
                    fetchFailView.setVisibility(View.GONE);
                    mainAdapter = new SponsorsAdapter(sponsors);
                    mainRecycler.setAdapter(mainAdapter);
                    mainRecycler.scheduleLayoutAnimation();
                }
            });
        } else {
            //Only play the animation when this fragment is first started (not on backstack pop)
            if (isTransitionFinished)
                mainRecycler.setLayoutAnimation(null);
            mainRecycler.setAdapter(mainAdapter);
            mainRecycler.scheduleLayoutAnimation();
        }
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
