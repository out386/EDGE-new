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
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Transition;

import com.bumptech.glide.Glide;
import com.edge2.transitions.MoveTransition;
import com.edge2.OnFragmentScrollListener;
import com.edge2.R;
import com.edge2.event.recycler.EventCategoryAdapter;
import com.edge2.event.recycler.ItemDecoration;
import com.edge2.eventdetails.EventDetailsFragment;
import com.edge2.utils.DimenUtils;

import java.util.ArrayList;

public class EventFragment extends Fragment {
    private static final String KEY_TRANSITION_FINISHED = "transitionFinished";
    public static final String KEY_CAT_IMAGE = "catImage";
    public static final String KEY_CAT_NAME = "catName";
    public static final String KEY_CAT_DESC = "catDesc";

    private OnFragmentScrollListener listener;
    private RecyclerView mainReycler;
    private EventCategoryAdapter mainAdapter;
    private int topViewHeight;
    private ItemDecoration itemDecoration;
    private Context context;
    private OnSharedElementListener sharedElementListener;
    private Transition transition;
    private boolean isTransitionFinished;
    private ArrayList<EventCategoryModel> eventList;
    private TextView nameTv;

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
        if (savedInstanceState != null)
            isTransitionFinished = savedInstanceState.getBoolean(KEY_TRANSITION_FINISHED);

        View rootView = inflater.inflate(R.layout.fragment_event, container, false);
        mainReycler = rootView.findViewById(R.id.eventcat_content);
        mainReycler.setHasFixedSize(true);
        mainReycler.setLayoutManager(new LinearLayoutManager(context));
        nameTv = rootView.findViewById(R.id.eventcat_name);

        if (isTransitionFinished)
            prototype();

        transition = new MoveTransition(nameTv);
        setSharedElementEnterTransition(transition);
        setSharedElementReturnTransition(transition);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View divider = view.findViewById(R.id.divider);
        View topView = view.findViewById(R.id.eventcat_top);
        NestedScrollView scrollView = view.findViewById(R.id.scroll_view);
        TextView desc = view.findViewById(R.id.eventcat_desc);
        ImageView image = view.findViewById(R.id.eventcat_icon);
        View dummy = view.findViewById(R.id.eventcat_dummy_bg);

        if (sharedElementListener != null) {
            transition.removeListener(sharedElementListener);
        }
        sharedElementListener = new OnSharedElementListener(dummy, desc, divider);
        transition.addListener(sharedElementListener);

        setupInsets(view, divider, topView, scrollView);
        setData(desc, image);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_TRANSITION_FINISHED, isTransitionFinished);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (sharedElementListener != null)
            transition.removeListener(sharedElementListener);
    }

    private void setData(TextView desc, ImageView image) {
        Bundle args = getArguments();
        if (args != null) {
            nameTv.setText(args.getString(KEY_CAT_NAME));
            desc.setText(args.getString(KEY_CAT_DESC));
            Glide.with(image.getContext()).
                    load(args.getInt(KEY_CAT_IMAGE))
                    .into(image);
        }
    }

    private void setupInsets(View v, View divider, View topView, NestedScrollView scrollView) {
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

            topView.post(() -> {
                RelativeLayout.LayoutParams topViewParams =
                        (RelativeLayout.LayoutParams) topView.getLayoutParams();
                int toolbarHeight = DimenUtils.getActionbarHeight(context);
                int dividerHeight = ((RelativeLayout.LayoutParams) divider.getLayoutParams()).topMargin
                        + divider.getHeight();

                topViewParams.setMargins(leftInset, toolbarHeight + topInset, rightInset, 0);
                topView.setLayoutParams(topViewParams);

                // Hide the toolbar
                listener.onListScrolled(0, Integer.MAX_VALUE);
                topViewHeight = topView.getHeight() + dividerHeight;
                startPostponedEnterTransition();
            });
            setupScrollListener(scrollView);
            return insets;
        });

        // No idea why onApplyWindowInsets never fires without this.
        if (v.getRootWindowInsets() != null)
            v.dispatchApplyWindowInsets(v.getRootWindowInsets());
    }

    private void prototype() {
        if (mainAdapter == null) {
            eventList = new ArrayList<>();
            for (int j = 0; j < 10; j++) {
                EventCategoryModel event;
                if (j % 2 == 0)
                    event = new EventCategoryModel("Crypto Quest",
                            R.drawable.event_icon,
                            "Put your cryptography and deciphering skills to the test by proving yourself while solving the clues.");
                else
                    event = new EventCategoryModel("Bug Hunt",
                            R.drawable.event_icon,
                            "Some dummy short description");
                eventList.add(event);
            }
            mainAdapter = new EventCategoryAdapter(eventList, this::onEventClicked);
        }

        // Only play the animation when this fragment is first started (not on backstack pop)
        if (isTransitionFinished)
            mainReycler.setLayoutAnimation(null);
        mainReycler.setAdapter(mainAdapter);
        mainReycler.scheduleLayoutAnimation();
    }

    private void onEventClicked(int position, View rootView, View imageView, View nameView,
                                View descView, View button) {

        EventCategoryModel item = eventList.get(position);
        Bundle args = new Bundle();
        args.putString(EventDetailsFragment.KEY_EVENT_NAME, item.getName());
        args.putString(EventDetailsFragment.KEY_EVENT_DESC, item.getDesc());
        args.putInt(EventDetailsFragment.KEY_EVENT_IMAGE, item.getIcon());

        // To add more shared views here, call "setTransitionName" in the adapter
        String transitionImgName = getString(R.string.sub_to_details_img_transition);
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
                .build();

        NavHostFragment.findNavController(EventFragment.this)
                .navigate(R.id.action_subEvents_to_eventDetails, args, null, extras);
    }

    private void setupScrollListener(NestedScrollView scrollView) {
        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    listener.onListScrolled(scrollY - oldScrollY, topViewHeight - scrollY);
                });
    }

    private class OnSharedElementListener implements Transition.TransitionListener {
        private int animTime;
        private int animOffset;
        private View dummy;
        private View desc;
        private View divider;
        private Interpolator interpolator;

        OnSharedElementListener(View dummy, View desc, View divider) {
            animTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            animOffset = getResources().getDimensionPixelOffset(R.dimen.item_animate_h_offset);
            this.dummy = dummy;
            this.desc = desc;
            this.divider = divider;
            interpolator = new DecelerateInterpolator();
        }

        @Override
        public void onTransitionStart(@NonNull Transition transition) {
            desc.setTranslationY(animOffset);
            desc.setAlpha(0);
            divider.setAlpha(0);
        }

        @Override
        public void onTransitionEnd(@NonNull Transition transition) {
            dummy.setVisibility(View.GONE);

            divider.animate()
                    .setDuration(animTime)
                    .setInterpolator(interpolator)
                    .alpha(1);
            desc.animate()
                    .setDuration(animTime)
                    .translationY(0)
                    .alpha(1);

            // The recyclerview is populated here, so that the item animation plays after the transition
            prototype();
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