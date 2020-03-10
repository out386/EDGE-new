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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Transition;

import com.edge2.BaseFragment;
import com.edge2.R;
import com.edge2.data.DataViewModel;
import com.edge2.event.EventCategoryModel;
import com.edge2.event.EventFragment;
import com.edge2.results.recycler.DetailsAdapter;
import com.edge2.results.recycler.EventsAdapter;
import com.edge2.results.recycler.ItemDecoration;
import com.edge2.utils.DimenUtils;
import com.edge2.views.GeneralHeaderView;

import java.util.List;

public class ResultsDetailsFragment extends BaseFragment {
    static final String KEY_EVENT_NAME = "eventName";
    static final String KEY_EVENT_IMAGE = "eventImage";

    //private OnSharedElementListener sharedElementListener;
    //private MoveTransition transition;
    private GeneralHeaderView topView;
    private RecyclerView recyclerView;
    private DetailsAdapter adapter;
    private ItemDecoration itemDecoration;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_results_events, container, false);

        topView = v.findViewById(R.id.top_view);
        recyclerView = v.findViewById(R.id.result_recycler);
        //transition = new MoveTransition(null);
        //setSharedElementEnterTransition(transition);
        //setSharedElementReturnTransition(transition);

        Bundle args = getArguments();
        if (args == null) {
            Context context = getContext();
            if (context == null)
                return v;
            Toast.makeText(context, "Internal error", Toast.LENGTH_SHORT).show();
            return v;
        }

        topView.setData(getString(R.string.results_title), args.getString(KEY_EVENT_NAME),
                getResources().getDrawable(args.getInt(KEY_EVENT_IMAGE), null), false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NestedScrollView scrollView = view.findViewById(R.id.placeholder_scroll);

        topView.setNameTransition(getString(R.string.events_to_quick_title_transition));
        topView.setDescTransition(getString(R.string.events_to_quick_desc_transition));
        topView.setIconTransition(getString(R.string.events_to_quick_icon_transition));

        /*if (sharedElementListener != null) {
            transition.removeListener(sharedElementListener);
        }
        sharedElementListener = new OnSharedElementListener(topView, recyclerView);
        transition.addListener(sharedElementListener);*/

        // Show the toolbar
        onFragmentScrollListener.onListScrolled(0, Integer.MAX_VALUE);
        setupWindowInsets(view, recyclerView, topView, false,
                false, null);
        topView.post(() ->
                setupScrollListener(scrollView, topView.getHeight()));

        //if (savedInstanceState != null)
        //    topView.showImage(0);
        Bundle args = getArguments();
        if (args != null) {
            setupRecycler(args.getString(KEY_EVENT_NAME));
        }
    }

    private void setupRecycler(String groupName) {
        FragmentActivity context = getActivity();
        if (context == null)
            return;

        if (recyclerView.getLayoutManager() == null) {
            RecyclerView.LayoutManager mainLayoutManager = new LinearLayoutManager(context);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(mainLayoutManager);
        }

        if (adapter == null) {
            adapter = new DetailsAdapter(ResultsData.getResult(groupName));
            recyclerView.setAdapter(adapter);
        } else {
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        /*if (sharedElementListener != null) {
            transition.removeListener(sharedElementListener);
            sharedElementListener = null;
        }
        transition.onDestroy();
        transition = null;*/
        topView = null;
        recyclerView.setAdapter(null);
        recyclerView.removeItemDecoration(itemDecoration);
        itemDecoration = null;
    }

    private class OnSharedElementListener implements Transition.TransitionListener {
        private int animTime;
        private int animOffset;
        private GeneralHeaderView topView;
        private View contentView;
        private Interpolator interpolator;

        // TODO: Use itemanimator instead of translation
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
