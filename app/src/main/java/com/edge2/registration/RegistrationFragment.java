package com.edge2.registration;

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

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.transition.Transition;

import com.edge2.BaseFragment;
import com.edge2.MainActivity;
import com.edge2.R;
import com.edge2.transitions.MoveTransition;
import com.edge2.views.GeneralHeaderView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class RegistrationFragment extends BaseFragment {
    private OnSharedElementListener sharedElementListener;
    private MoveTransition transition;
    private GeneralHeaderView topView;
    private RegistrationViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //postponeEnterTransition();
        View v = inflater.inflate(R.layout.fragment_registration, container, false);
        topView = v.findViewById(R.id.top_view);
        transition = new MoveTransition(null);
        setSharedElementEnterTransition(transition);
        setSharedElementReturnTransition(transition);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View contentView = view.findViewById(R.id.reg_content);
        NestedScrollView scrollView = view.findViewById(R.id.reg_scroll);

        topView.setNameTransition(getString(R.string.events_to_quick_title_transition));
        topView.setDescTransition(getString(R.string.events_to_quick_desc_transition));
        topView.setIconTransition(getString(R.string.events_to_quick_icon_transition));
        topView.setData(R.string.registration_title, R.string.registration_desc,
                R.drawable.ic_reg, false);

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

        if (savedInstanceState != null)
            topView.showImage(0);

        setupViewModel();
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

    private void setupViewModel() {
        FragmentActivity activity = getActivity();
        if (!(activity instanceof ImagePicker)) {
            Log.e("RegFrag", "setupViewModel: activity null");
            return;
        }
        viewModel = new ViewModelProvider(activity).get(RegistrationViewModel.class);

        ((ImagePicker) activity).pickImage(this);

    }

    public void onImagePicked(Uri fileUri) {
        Log.i("blah", "onImagePicked: " + fileUri);
        byte [] imageBytes = getImage(fileUri);
        Log.i("blah", "file data: " + imageBytes);

        if (getView() != null && viewModel != null && imageBytes != null) {
            Log.i("blah", "onImagePicked: registering");
            viewModel.register("Test 1", "test1@testmail.com", "none", "1",
                    "TMSL", "8876543890", "sometestpasss")
            .observe(getViewLifecycleOwner(), res -> {
                if (res == null) {
                    Toast.makeText(getContext(), "Failed to register", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i("blah", "register: " + res.toString());
                }
            });
        }
    }

    private byte[] getImage(Uri imgUri) {
        Activity activity = getActivity();
        if (activity == null)
            return null;
        try {
            InputStream inputStream = getActivity().getContentResolver().openInputStream(imgUri);
            if (inputStream == null)
                return null;

            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            int l;
            byte[] buffer = new byte[1024];

            while ((l = inputStream.read(buffer)) != -1)
                byteBuffer.write(buffer, 0, l);
            return byteBuffer.toByteArray();
        } catch (IOException e) {
            return null;
        }
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

    public interface ImagePicker {
        void pickImage(RegistrationFragment fragment);
    }
}
