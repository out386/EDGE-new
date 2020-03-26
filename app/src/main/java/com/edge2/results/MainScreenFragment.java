package com.edge2.results;

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
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Transition;

import com.edge2.BaseFragment;
import com.edge2.R;
import com.edge2.results.recycler.ItemDecoration;
import com.edge2.results.recycler.MainScreenAdapter;
import com.edge2.transitions.MoveTransition;
import com.edge2.utils.DimenUtils;
import com.edge2.views.GeneralHeaderView;
import com.edge2.results.MainScreenModel.MainScreenItem;

import java.util.List;

public class MainScreenFragment extends BaseFragment {
    private OnSharedElementListener sharedElementListener;
    private MoveTransition transition;
    private GeneralHeaderView topView;
    private RecyclerView recyclerView;
    private MainScreenAdapter adapter;
    private ItemDecoration itemDecoration;
    private TextView messageView;
    private View contentView;
    private List<MainScreenItem> allEventsList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //postponeEnterTransition();
        View v = inflater.inflate(R.layout.fragment_results, container, false);

        topView = v.findViewById(R.id.top_view);
        messageView = v.findViewById(R.id.result_msg_view);
        recyclerView = v.findViewById(R.id.result_recycler);
        transition = new MoveTransition(null);
        setSharedElementEnterTransition(transition);
        setSharedElementReturnTransition(transition);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        contentView = view.findViewById(R.id.placeholder_content);
        NestedScrollView scrollView = view.findViewById(R.id.placeholder_scroll);

        topView.setNameTransition(getString(R.string.events_to_quick_title_transition));
        topView.setDescTransition(getString(R.string.events_to_quick_desc_transition));
        topView.setIconTransition(getString(R.string.events_to_quick_icon_transition));
        topView.setData(R.string.results_title, R.string.results_desc,
                R.drawable.ic_result, false);

        if (sharedElementListener != null) {
            transition.removeListener(sharedElementListener);
        }
        sharedElementListener = new OnSharedElementListener(topView, contentView);
        transition.addListener(sharedElementListener);

        // Show the toolbar
        onFragmentScrollListener.onListScrolled(0, Integer.MAX_VALUE);
        setupWindowInsets(view, contentView, topView, false,
                false, null);
        topView.post(() ->
                setupScrollListener(scrollView, topView.getHeight()));

        //if (savedInstanceState != null)
        topView.showImage(0);
        setupResults();
    }

    private void setupResults() {
        FragmentActivity activity = getActivity();
        if (activity == null)
            return;
        ResultsViewModel viewModel = new ViewModelProvider(activity).get(ResultsViewModel.class);
        if (getView() != null) {
            viewModel.getMainScreen()
                    .observe(getViewLifecycleOwner(), res -> {
                        if (res.isError()) {
                            messageView.setText(getString(R.string.results_fetch_fail));
                            messageView.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } /*else if (res == null) {
                            messageView.setText(getString(R.string.results_not_decl));
                            messageView.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }*/ else {
                            messageView.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            setupRecycler(res.getData());
                        }
                    });
        } else {
            messageView.setText(getString(R.string.results_fetch_fail));
            recyclerView.setVisibility(View.GONE);
        }
    }

    private void setupRecycler(MainScreenModel mainScreen) {
        Context context = getContext();
        if (context == null)
            return;

        if (recyclerView.getLayoutManager() == null) {
            float itemSize = getResources().getDimensionPixelSize(R.dimen.allevents_main_events_img_w) +
                    2 * getResources().getDimension(R.dimen.allevents_main_events_padding_h);
            int columnCount = getRecyclerColumnCount(context, recyclerView, itemSize);
            RecyclerView.LayoutManager mainLayoutManager = new GridLayoutManager(context, columnCount);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(mainLayoutManager);
        }

        if (adapter == null) {
            allEventsList = mainScreen.getItems();
            adapter = new MainScreenAdapter(allEventsList, (p, rv, iv, nv, cv, v5) ->
                    onEventClicked(p, rv, iv, nv));
            recyclerView.setAdapter(adapter);
        } else {
            recyclerView.setAdapter(adapter);
        }
    }

    private void onEventClicked(int position, View rootView, View imageView,
                                View nameView) {
        MainScreenItem item = allEventsList.get(position);
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
        args.putString(ResultsSubsFragment.KEY_DISPLAY_NAME, item.getName());
        args.putString(ResultsSubsFragment.KEY_DEST_URL, item.getDest());
        args.putString(ResultsSubsFragment.KEY_DESC, item.getlDesc());
        args.putString(ResultsSubsFragment.KEY_IMAGE, item.getIc());

        NavHostFragment.findNavController(this)
                .navigate(R.id.action_resultsMain_to_resultsSubs, args, null, extras);
    }

    private int getRecyclerColumnCount(Context context, View child, float pxWidth) {
        int totalPadding = contentView.getPaddingRight() + contentView.getPaddingLeft()
                + child.getPaddingRight() + child.getPaddingLeft();
        pxWidth += child.getPaddingRight() + child.getPaddingLeft();
        int screenWidth = DimenUtils.getWindowWidth(context) - totalPadding;
        int columnCount = (int) Math.floor(screenWidth / pxWidth) - 1;
        return columnCount <= 1 ? 2 : columnCount;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (sharedElementListener != null) {
            transition.removeListener(sharedElementListener);
            sharedElementListener = null;
        }
        transition.onDestroy();
        transition = null;
        topView = null;
        if (recyclerView != null) {
            recyclerView.setAdapter(null);
            recyclerView.removeItemDecoration(itemDecoration);
            recyclerView = null;
        }
        itemDecoration = null;
        messageView = null;
        contentView = null;
    }

    private class OnSharedElementListener implements Transition.TransitionListener {
        private int animTime;
        private int animOffset;
        private GeneralHeaderView topView;
        private View contentView;
        private Interpolator interpolator;

        OnSharedElementListener(GeneralHeaderView topView, View contentView) {
            animTime = getResources().getInteger(android.R.integer.config_mediumAnimTime);
            animOffset = getResources().getDimensionPixelOffset(R.dimen.item_animate_h_offset);
            this.topView = topView;
            this.contentView = contentView;
            interpolator = new DecelerateInterpolator();
        }

        @Override
        public void onTransitionStart(@NonNull Transition transition) {
            contentView.setTranslationY(animOffset);
            contentView.setAlpha(0);
        }

        @Override
        public void onTransitionEnd(@NonNull Transition transition) {
            contentView.animate()
                    .setDuration(animTime)
                    .translationY(0)
                    .setInterpolator(interpolator)
                    .alpha(1);
            topView.showImage(animTime);
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
