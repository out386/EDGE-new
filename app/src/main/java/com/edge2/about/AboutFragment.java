package com.edge2.about;

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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.transition.Transition;

import com.edge2.BaseFragment;
import com.edge2.R;
import com.edge2.transitions.MoveTransition;
import com.edge2.views.GeneralHeaderView;
import com.google.android.material.button.MaterialButton;
import com.mikepenz.aboutlibraries.LibsBuilder;

public class AboutFragment extends BaseFragment {

    private OnSharedElementListener sharedElementListener;
    private MoveTransition transition;
    private GeneralHeaderView topView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        postponeEnterTransition();
        View v = inflater.inflate(R.layout.fragment_about, container, false);
        topView = v.findViewById(R.id.top_view);
        transition = new MoveTransition(null);
        setSharedElementEnterTransition(transition);
        setSharedElementReturnTransition(transition);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View contentView = view.findViewById(R.id.about_content);

        topView.setNameTransition(getString(R.string.events_to_quick_title_transition));
        topView.setDescTransition(getString(R.string.events_to_quick_desc_transition));
        topView.setIconTransition(getString(R.string.events_to_quick_icon_transition));

        if (sharedElementListener != null) {
            transition.removeListener(sharedElementListener);
        }
        sharedElementListener = new OnSharedElementListener(topView, contentView);
        transition.addListener(sharedElementListener);

        setupWindowInsets(view, contentView, topView, false,
                false, null);
        topView.post(() ->
                setupScrollListener((NestedScrollView) view, topView.getHeight()));
        soManyListenersExclamationMark(view);

        if (savedInstanceState != null)
            topView.showImage(0);
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
    }

    private void soManyListenersExclamationMark(View root) {
        View edge = root.findViewById(R.id.about_img_edge);
        View tmsl = root.findViewById(R.id.about_img_tmsl);
        View gx = root.findViewById(R.id.about_img_gx);
        View location = root.findViewById(R.id.about_img_location);
        View fb = root.findViewById(R.id.about_img_fb);
        View yt = root.findViewById(R.id.about_img_yt);
        View insta = root.findViewById(R.id.about_img_insta);
        View twitter = root.findViewById(R.id.about_img_twitter);
        MaterialButton aboutLicence = root.findViewById(R.id.about_licences);
        MaterialButton aboutPrivacy = root.findViewById(R.id.about_privacy);

        edge.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://edg.co.in"))));

        tmsl.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.ticollege.ac.in/"))));

        gx.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://geekonix.org/"))));

        location.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/maps/@22.5764407,88.4276723,18z"))));

        fb.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.facebook.com/Gx.Edg"))));

        yt.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.youtube.com/channel/UCSwFemGqe1XRmVlg1jRNJYw"))));

        insta.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://instagram.com/geekonix"))));

        twitter.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://twitter.com/geekonixedge"))));

        aboutLicence.setOnClickListener(view -> new LibsBuilder()
                .withFields(R.string.class.getFields())
                .start(requireContext()));

        aboutPrivacy.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://firebasestorage.googleapis.com/v0/b/edge-new-a7306.appspot.com/o/privacy.html?alt=media"))));

        startPostponedEnterTransition();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Show the toolbar
        onFragmentScrollListener.onListScrolled(0, Integer.MAX_VALUE);
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
