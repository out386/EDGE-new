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
import android.net.Uri;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Transition;

import com.edge2.BaseFragment;
import com.edge2.R;
import com.edge2.results.recycler.ItemDecoration;
import com.edge2.results.recycler.SubsAdapter;
import com.edge2.utils.DimenUtils;
import com.edge2.views.GeneralHeaderView;

import java.util.List;
import java.util.Map;

public class ResultsSubsFragment extends BaseFragment {
    static final String KEY_DISPLAY_NAME = "displayName";
    static final String KEY_DEST_URL = "destUrl";
    static final String KEY_DESC = "desc";
    static final String KEY_IMAGE = "image";
    static final String KEY_SCREEN_NAME = "screenName";

    //private OnSharedElementListener sharedElementListener;
    //private MoveTransition transition;
    private String screenName;
    private String url;
    private GeneralHeaderView topView;
    private RecyclerView recyclerView;
    private TextView messageView;
    private SubsAdapter adapter;
    private ItemDecoration itemDecoration;
    private List<ResultsModel.ScreenItem> screenItems;

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
        messageView = v.findViewById(R.id.result_msg_view);
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

        url = args.getString(KEY_DEST_URL);
        screenName = args.getString(KEY_SCREEN_NAME) == null ? "default" : args.getString(KEY_SCREEN_NAME);
        Uri iconUri = Uri.parse(args.getString(KEY_IMAGE));
        topView.setData(getString(R.string.results_title), args.getString(KEY_DISPLAY_NAME),
                iconUri, R.drawable.ic_result, false, true);
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
        setupRecycler();
    }

    private void setupRecycler() {
        FragmentActivity activity = getActivity();
        if (activity == null)
            return;
        ResultsViewModel viewModel = new ViewModelProvider(activity).get(ResultsViewModel.class);

        if (recyclerView.getLayoutManager() == null) {
            float itemSize = getResources().getDimensionPixelSize(R.dimen.allevents_main_events_img_w) +
                    2 * getResources().getDimension(R.dimen.allevents_main_events_padding_h);
            int columnCount = getRecyclerColumnCount(activity, recyclerView, itemSize);
            RecyclerView.LayoutManager mainLayoutManager = new GridLayoutManager(activity, columnCount);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(mainLayoutManager);
        }

        viewModel.getSubscreens(url).observe(getViewLifecycleOwner(), downloadModel -> {
            // Implies the json is still being downloaded, and this is just old data
            if (downloadModel == null || !url.equals(downloadModel.getUrl()))
                return;

            if (downloadModel.isError() || downloadModel.getData() == null) {
                messageView.setText(getString(R.string.results_fetch_fail));
                messageView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                return;
            }

            Map<String, List<ResultsModel.ScreenItem>> screensList =
                    downloadModel.getData().getScreens();

            if (screensList == null || screensList.get(screenName) == null) {
                messageView.setText(getString(R.string.results_fetch_fail));
                messageView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                return;
            }

            screenItems = screensList.get(screenName);
            if (adapter == null || adapter.getItems() == null
                    || !adapter.getItems().equals(screenItems)) {

                messageView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter = new SubsAdapter(screenItems, (p, rv, iv, nv, cv, v5) ->
                        onEventClicked(p, rv, iv, nv));
                recyclerView.setAdapter(adapter);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                messageView.setVisibility(View.GONE);
                recyclerView.setAdapter(adapter);
            }
        });
    }

    private void onEventClicked(int position, View rootView, View imageView,
                                View nameView) {
        ResultsModel.ScreenItem item = screenItems.get(position);
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
        args.putString(KEY_DISPLAY_NAME, item.getName());
        args.putString(KEY_DEST_URL, url);
        args.putString(KEY_DESC, item.getDesc());
        args.putString(KEY_IMAGE, item.getIconUriString());
        args.putString(KEY_SCREEN_NAME, item.getDest());

        int destination;
        if (item.isNextScreen())
            destination = R.id.action_resultsSubs_to_subs;
        else
            destination = R.id.action_resultsSubs_to_details;

        NavHostFragment.findNavController(this)
                .navigate(destination, args, null, extras);
    }

    private int getRecyclerColumnCount(Context context, View child, float pxWidth) {
        int totalPadding = child.getPaddingRight() + child.getPaddingLeft();
        pxWidth += child.getPaddingRight() + child.getPaddingLeft();
        int screenWidth = DimenUtils.getWindowWidth(context) - totalPadding;
        int columnCount = (int) Math.floor(screenWidth / pxWidth) - 1;
        return columnCount <= 1 ? 2 : columnCount;
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
        messageView = null;
        if (recyclerView != null) {
            recyclerView.setAdapter(null);
            recyclerView.removeItemDecoration(itemDecoration);
            recyclerView = null;
        }
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
