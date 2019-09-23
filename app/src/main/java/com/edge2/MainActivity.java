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
import android.widget.AdapterView;
import android.widget.FrameLayout;

import androidx.appcompat.app.ActionBar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.edge2.allevents.EventsViewModel;
import com.edge2.utils.DimenUtils;
import com.edge2.utils.Logger;
import com.edge2.views.CenterToolbar;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import ir.apend.slider.model.Slide;
import ir.apend.slider.ui.Slider;

public class MainActivity extends ThemeActivity {

    private BottomNavigationView bottomNav;
    private Slider banner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CenterToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayShowTitleEnabled(false);
        }

        NavController navController = Navigation.findNavController(this, R.id.content_frame);
        banner = findViewById(R.id.top_banner);
        bottomNav = findViewById(R.id.navigation);
        NavigationUI.setupWithNavController(bottomNav, navController);
        bottomNav.setItemIconTintList(null);
        setupInsets();
        setupBanner();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    private void setupBanner() {
        banner.setScreenWidth(DimenUtils.getWindowWidth(this));

        EventsViewModel viewModel = ViewModelProviders.of(this)
                .get(EventsViewModel.class);

        viewModel.getBanner()
                .observe(this, eventNameModels -> {
                    List<Slide> list = new ArrayList<>(eventNameModels.size());
                    for (int i = 0; i < eventNameModels.size(); i++) {
                        list.add(new Slide(i, eventNameModels.get(i).getImg(), 0));
                    }
                    banner.setItemClickListener(new BannerListener());
                    banner.addSlides(list);
                });
    }

    private void setupInsets() {
        View decorView = getWindow().getDecorView();
        CoordinatorLayout.LayoutParams navbarParams =
                (CoordinatorLayout.LayoutParams) bottomNav.getLayoutParams();
        FrameLayout contentView = findViewById(R.id.content_frame);
        CenterToolbar toolbar = findViewById(R.id.toolbar);
        AppBarLayout appBarLayout = findViewById(R.id.app_bar_layout);
        CoordinatorLayout.LayoutParams appbarParams =
                (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        CollapsingToolbarLayout.LayoutParams bannerParams =
                (CollapsingToolbarLayout.LayoutParams) banner.getLayoutParams();

        decorView.setOnApplyWindowInsetsListener((view, insets) -> {
            int topInset = insets.getSystemWindowInsetTop();
            int leftInset = insets.getSystemWindowInsetLeft();
            int rightInset = insets.getSystemWindowInsetRight();
            int bottomInset = insets.getSystemWindowInsetBottom();

            bottomNav.post(() -> {
                navbarParams.height = bottomNav.getHeight() + bottomInset;
                bottomNav.setLayoutParams(navbarParams);
                bottomNav.setPadding(leftInset, 0, rightInset, bottomInset);
            });

            toolbar.post(() ->
                    appBarLayout.post(() -> {
                        appbarParams.height = appBarLayout.getHeight() + toolbar.getHeight();
                        appBarLayout.setLayoutParams(appbarParams);
                        bannerParams.setMargins(0, toolbar.getHeight(), 0, 0);
                        banner.setLayoutParams(bannerParams);
                    })
            );

            contentView.setPadding(leftInset, 0, rightInset, 0);
            decorView.setPadding(0, topInset, 0, 0);

            return insets.consumeSystemWindowInsets();
        });
    }

    class BannerListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Logger.log("BannerListener", "onItemClick: " + position);
        }
    }

}
