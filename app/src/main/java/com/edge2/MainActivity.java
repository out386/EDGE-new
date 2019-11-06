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
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.RelativeLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.edge2.allevents.EventsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends ThemeActivity implements EventsFragment.OnEventsFragmentListener {

    private BottomNavigationView bottomNav;
    private int animTime;
    private int lastEventScrollDy = 0;
    private ViewPropertyAnimator bottomNavAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        animTime = getResources().getInteger(android.R.integer.config_mediumAnimTime);
        NavController navController = Navigation.findNavController(this, R.id.content_frame);
        bottomNav = findViewById(R.id.navigation);
        bottomNavAnimator = bottomNav.animate();
        NavigationUI.setupWithNavController(bottomNav, navController);
        bottomNav.setItemIconTintList(null);
        setupInsets();
    }

    public void setupToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    private void setupInsets() {
        View rootView = findViewById(R.id.mainRoot);
        rootView.setOnApplyWindowInsetsListener((view, insets) -> {
            int leftInset = insets.getSystemWindowInsetLeft();
            int rightInset = insets.getSystemWindowInsetRight();
            int bottomInset = insets.getSystemWindowInsetBottom();

            View content = findViewById(R.id.content_frame);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) content.getLayoutParams();
            // Needed because the CoordinatorLayout in content causes the view to shift up when scrolled
            params.bottomMargin = -bottomInset;

            RelativeLayout.LayoutParams bottomNavParams =
                    (RelativeLayout.LayoutParams) bottomNav.getLayoutParams();
            bottomNav.setPadding(leftInset, 0, rightInset, bottomInset);
            //bottomNavParams.setMargins(0, 0, 0, bottomInset);
            rootView.setPadding(0, 0, 0, 0);

            return insets;
        });
    }

    @Override
    public void onEventsScrolled(int dy) {
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
                        .translationY(bottomNav.getHeight() << 1);
                lastEventScrollDy = dy;
            }
        }
    }
}
