package com.edge2.eventdetails;

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
import androidx.transition.Transition;

import com.bumptech.glide.Glide;
import com.edge2.MoveTransition;
import com.edge2.OnFragmentScrollListener;
import com.edge2.R;
import com.edge2.utils.DimenUtils;

public class EventDetailsFragment extends Fragment {
    public static final String KEY_EVENT_IMAGE = "eventImage";
    public static final String KEY_EVENT_NAME = "eventName";
    public static final String KEY_EVENT_DESC = "eventDesc";

    private OnFragmentScrollListener listener;
    private int topViewHeight;
    private Context context;
    private OnSharedElementListener sharedElementListener;
    private Transition transition;

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
        View rootView = inflater.inflate(R.layout.fragment_event_details, container, false);

        transition = new MoveTransition();
        setSharedElementEnterTransition(transition);
        setSharedElementReturnTransition(transition);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View divider = view.findViewById(R.id.divider);
        View topView = view.findViewById(R.id.eventdetails_top);
        View contentView = view.findViewById(R.id.eventdetails_content);
        NestedScrollView scrollView = view.findViewById(R.id.scroll_view);
        TextView shortDesc = view.findViewById(R.id.eventdetails_short_desc);
        TextView name = view.findViewById(R.id.eventdetails_name);
        ImageView image = view.findViewById(R.id.eventdetails_icon);
        View dummy = view.findViewById(R.id.eventdetails_dummy_bg);
        TextView longDesc = view.findViewById(R.id.eventdetails_long_desc);
        TextView rules = view.findViewById(R.id.eventdetails_rules);
        TextView contacts = view.findViewById(R.id.eventdetails_contacts);

        prototype(longDesc, rules, contacts);

        if (sharedElementListener != null) {
            transition.removeListener(sharedElementListener);
        }
        sharedElementListener = new OnSharedElementListener(dummy, divider, longDesc,
                rules, contacts);
        transition.addListener(sharedElementListener);

        setupInsets(view, divider, topView, scrollView, contentView);
        setData(name, shortDesc, image);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (sharedElementListener != null)
            transition.removeListener(sharedElementListener);
    }

    private void setData(TextView name, TextView desc, ImageView image) {
        Bundle args = getArguments();
        if (args != null) {
            name.setText(args.getString(KEY_EVENT_NAME));
            desc.setText(args.getString(KEY_EVENT_DESC));
            Glide.with(image.getContext()).
                    load(args.getInt(KEY_EVENT_IMAGE))
                    .into(image);
        }
    }

    private void setupInsets(View v, View divider, View topView, NestedScrollView scrollView,
                             View content) {
        v.setOnApplyWindowInsetsListener((v1, insets) -> {
            int topInset = insets.getSystemWindowInsetTop();
            int leftInset = insets.getSystemWindowInsetLeft();
            int rightInset = insets.getSystemWindowInsetRight();
            int bottomInset = insets.getSystemWindowInsetBottom();

            content.setPadding(leftInset, 0, rightInset, bottomInset);

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

    private void prototype(TextView longDesc, TextView rules, TextView contacts) {
        longDesc.setText("Can you solve the cipher above and make a meaningful sentence out of it? Well if you can then you are made for this event. In this event you need to be calm and gather all your wits to make meaning of most of the questions. Get ready for a cryptic experience!");
        String rule = "<h3>Rules</h3><ul><li>Each team can have either 1 or 2 members.</li><li>Various questions involving ciphers and steganography will be provided to you. You will simply have to give us the flags, which will be hidden within the questions.</li><li>Knowledge of computer programming won\\'t be required to solve the problems, although it may help to solve some problems faster.</li><li>The event will be conducted either on Ubuntu or on Windows, as per your choice. Basic knowledge of such an environment, can prove helpful during the event</li><li>Teams won\\'t be allowed to use any external electronic devices.</li><li>Internet access may be provided to the participants if the co-ordinators decide that any question will require it. In case it is not provided, we will provide you with almost all the knowledge, you will require to crack the questions.</li><li>In the event of a tie, the team to solve the most difficult questions first will be considered the winner.</li><li>The coordinators have the right to change the rules and judging criteria of the contest at any time they deem fit.</li></ul>";
        rules.setText(rule);
        contacts.setText("Name A: 0123456789");
    }

    private void setupScrollListener(NestedScrollView scrollView) {
        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (v, scrollX, scrollY, oldScrollX, oldScrollY) ->
                        listener.onListScrolled(
                                scrollY - oldScrollY, topViewHeight - scrollY));
    }

    private class OnSharedElementListener implements Transition.TransitionListener {
        private int animTime;
        private int animOffset;
        private View dummy;
        private View divider;
        private TextView longDesc;
        private TextView rules;
        private TextView contacts;
        private Interpolator interpolator;

        OnSharedElementListener(View dummy, View divider, TextView longDesc,
                                TextView rules, TextView contacts) {
            animTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            animOffset = getResources().getDimensionPixelOffset(R.dimen.item_animate_h_offset);
            this.dummy = dummy;
            this.divider = divider;
            this.longDesc = longDesc;
            this.rules = rules;
            this.contacts = contacts;
            interpolator = new DecelerateInterpolator();
        }

        @Override
        public void onTransitionStart(@NonNull Transition transition) {
            longDesc.setTranslationY(animOffset);
            rules.setTranslationY(animOffset);
            contacts.setTranslationY(animOffset);
            longDesc.setAlpha(0);
            rules.setAlpha(0);
            contacts.setAlpha(0);
            divider.setAlpha(0);
        }

        @Override
        public void onTransitionEnd(@NonNull Transition transition) {
            dummy.setVisibility(View.GONE);

            divider.animate()
                    .setDuration(animTime)
                    .setInterpolator(interpolator)
                    .alpha(1);
            longDesc.animate()
                    .setDuration(animTime)
                    .setInterpolator(interpolator)
                    .translationY(0)
                    .alpha(1);
            rules.animate()
                    .setDuration(animTime)
                    .setInterpolator(interpolator)
                    .setStartDelay(100)
                    .translationY(0)
                    .alpha(1);
            contacts.animate()
                    .setDuration(animTime)
                    .setInterpolator(interpolator)
                    .setStartDelay(200)
                    .translationY(0)
                    .alpha(1);

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