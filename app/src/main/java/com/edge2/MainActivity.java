package com.edge2;

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

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.edge2.allevents.EventsFragment;
import com.edge2.utils.DimenUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends ThemeActivity implements OnFragmentScrollListener {

    private BottomNavigationView bottomNav;
    private Toolbar toolbar;
    private int animTime;
    private int lastEventScrollDy = 0;
    private int lastToolbarScrollDy = 0;
    private int toolbarHeight;
    private ViewPropertyAnimator bottomNavAnimator;
    private ViewPropertyAnimator toolbarAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        animTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        bottomNav = findViewById(R.id.navigation);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayShowTitleEnabled(false);
        }
        toolbarAnimator = toolbar.animate();

        setupBottomNav();
        setupInsets();
    }

    private void setupBottomNav() {
        NavController navController = Navigation.findNavController(this, R.id.content_frame);
        bottomNavAnimator = bottomNav.animate();
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            Bundle args = new Bundle();
            switch (item.getItemId()) {
                case R.id.nav_events:
                    navController.popBackStack(R.id.events_dest, true);
                    args.putBoolean(EventsFragment.KEY_IS_INTRA, false);
                    navController.navigate(R.id.events_dest, args);
                    break;
                case R.id.nav_intra:
                    navController.popBackStack(R.id.events_dest, true);
                    args.putBoolean(EventsFragment.KEY_IS_INTRA, true);
                    navController.navigate(R.id.events_dest, args);
                    break;
                case R.id.nav_sett:
                    navController.popBackStack(R.id.events_dest, true);
                    navController.navigate(R.id.settings_dest);
                    break;
            }
            return true;
        });
        bottomNav.setOnNavigationItemReselectedListener(item -> {
        });
        bottomNav.setItemIconTintList(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    private void setupInsets() {
        View rootView = findViewById(R.id.main_root);
        rootView.setOnApplyWindowInsetsListener((view, insets) -> {
            int topInset = insets.getSystemWindowInsetTop();
            int leftInset = insets.getSystemWindowInsetLeft();
            int rightInset = insets.getSystemWindowInsetRight();
            int bottomInset = insets.getSystemWindowInsetBottom();
            ViewGroup.LayoutParams toolbarParams = toolbar.getLayoutParams();
            int toolbarHeight = DimenUtils.getActionbarHeight(rootView.getContext());

            toolbarParams.height = toolbarHeight + topInset;
            toolbar.setLayoutParams(toolbarParams);
            toolbar.setPadding(leftInset, topInset, rightInset, 0);
            this.toolbarHeight = -toolbarParams.height;
            bottomNav.setPadding(leftInset, 0, rightInset, bottomInset);
            rootView.setPadding(0, 0, 0, 0);

            return insets;
        });
    }

    @Override
    public void onListScrolled(int dy, int toolbarDy) {
        if (dy < 1) {
            if (lastEventScrollDy >= 1) {
                bottomNavAnimator.cancel();
                bottomNavAnimator
                        .setDuration(animTime)
                        .translationY(0);
                lastEventScrollDy = dy;
            }
        } else {
            if (lastEventScrollDy < 1) {
                bottomNavAnimator.cancel();
                bottomNavAnimator = bottomNav.animate()
                        .setDuration(animTime)
                        .translationY(bottomNav.getHeight());
                lastEventScrollDy = dy;
            }
        }

        // Only animate if the toolbar is either expanding, or was expanded. No need otherwise as
        // toolbarDy would change by small numbers in such cases.
        if (lastToolbarScrollDy == Integer.MAX_VALUE) { // The toolbar was expanded before
            toolbarAnimator.cancel();
            toolbarAnimator
                    .setDuration(animTime)
                    .translationY(Math.min(0, Math.max(toolbarHeight, toolbarDy)));
        } else if (toolbarDy == Integer.MAX_VALUE) { // The toolbar is being expanded
            toolbarAnimator.cancel();
            toolbarAnimator
                    .setDuration(animTime)
                    .translationY(0);
        } else {
            toolbar.setTranslationY(Math.min(0, Math.max(toolbarHeight, toolbarDy)));
        }
        lastToolbarScrollDy = toolbarDy;
    }
}
