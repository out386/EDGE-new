package com.edge2;

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
import android.content.IntentSender;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;

import com.afollestad.materialdialogs.MaterialDialog;
import com.edge2.allevents.EventsFragment;
import com.edge2.ca.CAFragment;
import com.edge2.registration.RegistrationFragment;
import com.edge2.utils.DimenUtils;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Collections;
import java.util.List;

public class MainActivity extends ThemeActivity implements OnFragmentScrollListener,
        CAFragment.OnAuthStartListener, RegistrationFragment.ImagePicker {

    private static final int CODE_UPDATE_FLOW = 2;
    private static final int CODE_LOG_IN = 5;
    private static final int CODE_PICK_IMAGE = 10;

    private BottomNavigationView bottomNav;
    private ConstraintLayout toolbar;
    private int animTime;
    private int lastEventScrollDy = 0;
    private int lastToolbarScrollDy = 0;
    private int toolbarHeight;
    private ViewPropertyAnimator bottomNavAnimator;
    private ViewPropertyAnimator toolbarAnimator;
    private AppUpdateManager appUpdateManager;
    private RegistrationFragment registrationFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        animTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        bottomNav = findViewById(R.id.navigation);

        toolbar = findViewById(R.id.toolbar);
        toolbarAnimator = toolbar.animate();

        setupBottomNav();
        setupInsets();
        checkUpdate();
    }

    private void checkUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    startUpdate(appUpdateInfo);
                }
            }
        });
    }

    private void startUpdate(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager
                    .startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE,
                            this, CODE_UPDATE_FLOW);
        } catch (IntentSender.SendIntentException ign) {
            // What do you even want me to do, huh? Open Play? Actually...\
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        appUpdateManager.getAppUpdateInfo()
                .addOnSuccessListener(appUpdateInfo -> {
                    if (appUpdateInfo.updateAvailability()
                            == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                        // If an in-app update is already running, resume the update.
                        startUpdate(appUpdateInfo);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_UPDATE_FLOW) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(this, getString(R.string.app_update_cancel), Toast.LENGTH_LONG)
                        .show();
            }
        } else if (requestCode == CODE_LOG_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    showAuthFailed(null);
                }
            } else {
                String message = null;
                if (response != null && response.getError() != null) {
                    message = response.getError().getMessage();
                }
                showAuthFailed(message);
            }
        } else if (resultCode == RESULT_OK && requestCode == CODE_PICK_IMAGE) {
            if (registrationFragment != null && data != null) {
                registrationFragment.onImagePicked(data.getData());
            }
            registrationFragment = null;
        }
    }

    private void showAuthFailed(String message) {
        String msg = String.format(getString(R.string.auth_failed), message == null ? "" : message);
        new MaterialDialog.Builder(this)
                .contentColor(getColor(R.color.textHeader))
                .backgroundColor(getColor(R.color.windowBackground))
                .content(msg)
                .positiveColor(getColor(R.color.textHeader))
                .positiveText("OK")
                .build()
                .show();
    }

    @Override
    public void logIn() {
        List<AuthUI.IdpConfig> providers = Collections.singletonList(
                new AuthUI.IdpConfig.GoogleBuilder().build());

        startActivityForResult(AuthUI.getInstance()
                        .createSignInIntentBuilder().setAvailableProviders(providers).build(),
                CODE_LOG_IN);
    }

    private void setupBottomNav() {
        NavController navController = Navigation.findNavController(this, R.id.content_frame);
        bottomNavAnimator = bottomNav.animate();
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            Bundle args = new Bundle();
            switch (item.getItemId()) {
                case R.id.nav_events:
                    clearBackStack(navController);
                    args.putBoolean(EventsFragment.KEY_IS_INTRA, false);
                    navController.navigate(R.id.events_dest, args);
                    break;
                case R.id.nav_intra:
                    clearBackStack(navController);
                    args.putBoolean(EventsFragment.KEY_IS_INTRA, true);
                    navController.navigate(R.id.events_dest, args);
                    break;
                case R.id.nav_sett:
                    clearBackStack(navController);
                    navController.navigate(R.id.settings_dest);
                    break;
            }
            return true;
        });
        bottomNav.setOnNavigationItemReselectedListener(item -> {
        });
        bottomNav.setItemIconTintList(null);
    }

    // Don't judge - had no other option.
    private void clearBackStack(NavController navController) {
        for (NavDestination navDestination : navController.getGraph()) {
            navController.popBackStack();
        }
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

    @Override
    public int getBottomNavHeight() {
        return bottomNav.getHeight();
    }

    @Override
    public void pickImage(RegistrationFragment fragment) {
        this.registrationFragment = fragment;
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, CODE_PICK_IMAGE);
    }
}
