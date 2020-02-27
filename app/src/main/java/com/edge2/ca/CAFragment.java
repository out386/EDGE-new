package com.edge2.ca;

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

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.core.widget.NestedScrollView;
import androidx.transition.Transition;

import com.edge2.BaseFragment;
import com.edge2.R;
import com.edge2.transitions.MoveTransition;
import com.edge2.views.GeneralHeaderView;
import com.firebase.ui.auth.AuthUI;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ir.androidexception.datatable.DataTable;
import ir.androidexception.datatable.model.DataTableHeader;
import ir.androidexception.datatable.model.DataTableRow;

public class CAFragment extends BaseFragment {
    private static String URL_BROCHURE = "https://drive.google.com/open?id=10MTheTeHReLNGVpPAl1sFkjaRcT8pAY7";
    private static String URL_REGISTER = "https://forms.gle/vujB6gvrsVL8W8Xn9";
    private static String KEY_IS_POINTS_SHOWING = "isPointsShowing";

    private OnSharedElementListener sharedElementListener;
    private MoveTransition transition;
    private View registerHolderView;
    private View dataHolderView;
    private View errorView;
    private ContentLoadingProgressBar loadingView;
    private MaterialButton loginButton;
    private TextView loginPrompt;
    private GeneralHeaderView topView;
    private TextView dataPoints;
    private DataTable dataTable;
    private DataTable rulesTable;
    private ChipGroup pointsChipGroup;
    private View pointsHolder;
    private View rulesHolder;
    private View perksHolder;
    private int animTime;
    private int animOffset;
    private OnAuthStartListener authStartListener;
    private boolean isTransitionPlayed;
    private int caScreenNumber;

    //TODO: Break this Fragment up into different Fragments. I just didn't have the time.

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        authStartListener = (OnAuthStartListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        authStartListener = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        transition = new MoveTransition(null);
        animTime = getResources().getInteger(android.R.integer.config_mediumAnimTime);
        animOffset = getResources().getDimensionPixelOffset(R.dimen.item_animate_h_offset);
        setSharedElementEnterTransition(transition);
        setSharedElementReturnTransition(transition);
        postponeEnterTransition();
        return inflater.inflate(R.layout.fragment_ca, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        topView = view.findViewById(R.id.top_view);
        View contentView = view.findViewById(R.id.ca_content);
        loginButton = view.findViewById(R.id.ca_loginout_button);
        registerHolderView = view.findViewById(R.id.ca_register_holder);
        loadingView = view.findViewById(R.id.ca_loading);
        dataHolderView = view.findViewById(R.id.ca_data_holder);
        dataPoints = view.findViewById(R.id.ca_data_points);
        dataTable = view.findViewById(R.id.ca_data_table);
        rulesTable = view.findViewById(R.id.ca_rules_table);
        pointsChipGroup = view.findViewById(R.id.ca_points_chipgroup);
        pointsHolder = view.findViewById(R.id.ca_points_holder);
        rulesHolder = view.findViewById(R.id.ca_fixedpoints_holder);
        perksHolder = view.findViewById(R.id.ca_rules_holder);
        errorView = view.findViewById(R.id.ca_error_holder);
        loginPrompt = view.findViewById(R.id.ca_account_prompt);

        loadingView.hide();

        topView.setNameTransition(getString(R.string.events_to_quick_title_transition));
        topView.setDescTransition(getString(R.string.events_to_quick_desc_transition));
        topView.setIconTransition(getString(R.string.events_to_quick_icon_transition));

        if (sharedElementListener != null) {
            transition.removeListener(sharedElementListener);
        }
        sharedElementListener = new OnSharedElementListener(topView);
        transition.addListener(sharedElementListener);

        setUserInfo();
        startPostponedEnterTransition();

        // Show the toolbar
        onFragmentScrollListener.onListScrolled(0, Integer.MAX_VALUE);
        setupWindowInsets(view, contentView, topView, false,
                false, null);
        topView.post(() ->
                setupScrollListener((NestedScrollView) view, topView.getHeight()));
        setupListeners(view);

        // If this is null, onTransitionEnd() of the transition listener will call these anyway.
        // This is because the transition only happens when this Fragment is first created.
        if (savedInstanceState != null) {
            topView.showImage(0);
            isTransitionPlayed = true;
            caScreenNumber = savedInstanceState.getInt(KEY_IS_POINTS_SHOWING);
        }
        if (caScreenNumber == 0) {
            hideView(rulesHolder);
            hideView(perksHolder);
            showView(pointsHolder);
        } else if (caScreenNumber == 1) {
            hideView(pointsHolder);
            hideView(perksHolder);
            showView(rulesHolder);
        } else {
            hideView(rulesHolder);
            hideView(pointsHolder);
            showView(perksHolder);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_IS_POINTS_SHOWING, caScreenNumber);
    }

    private void setUserInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            UserInfo userInfo = getUserGoogleData(user);
            if (userInfo == null) { // Dammit
                loginButton.setText(R.string.auth_logout);
                String name = getString(R.string.auth_failed_name);
                String desc = getString(R.string.auth_failed_desc);
                topView.setData(name, desc, null, R.drawable.ic_user, true);
                return;
            }
            String name = userInfo.getDisplayName() != null ?
                    userInfo.getDisplayName() : getNameFromEmail(userInfo.getEmail());
            topView.setData(name, null, getUserPhoto(userInfo), R.drawable.ic_user, true);
            loginButton.setText(R.string.auth_logout);
        } else {
            loginButton.setText(R.string.auth_login);
            String name = getString(R.string.auth_notlog_name);
            String desc = getString(R.string.auth_notlog_desc);
            topView.setData(name, desc, null, R.drawable.ic_user, true);
        }
    }

    private Uri getUserPhoto(UserInfo userInfo) {
        Uri photo = userInfo.getPhotoUrl();
        if (photo == null)
            return null;
        String photoUrl = photo.toString();
        photoUrl = photoUrl.replace("s96-c", "s400-c");
        return Uri.parse(photoUrl);
    }

    private String getNameFromEmail(@Nullable String emailAddress) {
        if (emailAddress == null)
            return getString(R.string.auth_failed_name);
        return emailAddress.split("@gmail.com")[0];
    }

    private UserInfo getUserGoogleData(FirebaseUser user) {
        List<? extends UserInfo> providers = user.getProviderData();
        for (UserInfo userInfo : providers) {
            if ("firebase".equals(userInfo.getProviderId()))
                return userInfo;
        }
        return null;
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
        registerHolderView = null;
        loadingView = null;
        dataHolderView = null;
        errorView = null;
        loginButton = null;
        loginPrompt = null;
        topView = null;
        dataPoints = null;
        dataTable = null;
        pointsHolder = null;
        pointsChipGroup = null;
        rulesHolder = null;
        rulesTable = null;
        perksHolder = null;
    }

    private void setupListeners(View rootView) {
        MaterialButton brochureButton = rootView.findViewById(R.id.ca_brochure_button);
        MaterialButton registerButton = rootView.findViewById(R.id.ca_register_button);

        brochureButton.setOnClickListener(view -> {
            Uri uri = Uri.parse(URL_BROCHURE);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
        registerButton.setOnClickListener(view -> {
            Uri uri = Uri.parse(URL_REGISTER);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
        loginButton.setOnClickListener(view -> {
            if (getString(R.string.auth_login).contentEquals(loginButton.getText())) {
                authStartListener.logIn();
            } else {
                Context context = getContext();
                if (context != null) {
                    AuthUI.getInstance().signOut(context).addOnCompleteListener(task -> {
                        setUserInfo();
                        setData();
                    });
                }
            }
        });
        pointsChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.ca_points_chip) {
                caScreenNumber = 0;
                hideView(perksHolder);
                hideView(rulesHolder);
                showView(pointsHolder);
            } else if (checkedId == R.id.ca_rules_chip) {
                caScreenNumber = 1;
                hideView(perksHolder);
                hideView(pointsHolder);
                showView(rulesHolder);
            } else if (checkedId == R.id.ca_perks_chip) {
                caScreenNumber = 2;
                hideView(rulesHolder);
                hideView(pointsHolder);
                showView(perksHolder);
            }
        });
    }

    private void showView(View view) {
        if (view == null)
            return;
        view.setVisibility(View.VISIBLE);
        view.setTranslationY(animOffset);
        view.setAlpha(0);
        view.animate()
                .setDuration(animTime)
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator())
                .alpha(1)
                .setListener(null);
    }

    private void hideView(View view) {
        if (view == null)
            return;
        if (view.getVisibility() == View.GONE)
            return;
        view.animate()
                .setDuration(animTime)
                .translationY(animOffset)
                .setInterpolator(new DecelerateInterpolator())
                .alpha(0)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
    }

    private void showSingleView(View view) {
        if (view == dataHolderView) {
            if (loadingView != null) {
                loadingView.hide();
            }
            hideView(registerHolderView);
            hideView(errorView);
            showView(dataHolderView);

        } else if (view == registerHolderView) {
            if (loadingView != null) {
                loadingView.hide();
            }
            hideView(dataHolderView);
            hideView(errorView);
            showView(registerHolderView);

        } else if (view == errorView) {
            if (loadingView != null) {
                loadingView.hide();
            }
            hideView(registerHolderView);
            hideView(dataHolderView);
            showView(errorView);
        } else if (view == loadingView) {
            if (loadingView != null) {
                loadingView.show();
            }
            hideView(registerHolderView);
            hideView(errorView);
            hideView(dataHolderView);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isTransitionPlayed) {
            setUserInfo();
            setData();
        }
    }

    private void setData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            loginPrompt.setText(R.string.ca_account_no_log);
            showSingleView(registerHolderView);
        } else {
            showSingleView(loadingView);
            fetchData();
        }
    }

    private void fetchData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            UserInfo userInfo = getUserGoogleData(user);
            if (userInfo == null) {
                showSingleView(errorView);
            } else {
                fetchFirestoreData(userInfo);
            }
        } else {
            loginPrompt.setText(R.string.ca_account_no_log);
            showSingleView(registerHolderView);
        }
    }

    private void fetchFirestoreData(UserInfo userInfo) {
        String email = userInfo.getEmail();
        if (email == null) {
            showSingleView(errorView);
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("ca").document(email).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            showFirestoreData(document);
                        } else {
                            if (loginPrompt != null)
                                loginPrompt.setText(R.string.ca_account_not_ca);
                            showSingleView(registerHolderView);
                        }
                    } else {
                        if (task.getException() != null)
                            showSingleView(errorView);
                    }
                });
    }

    private void showFirestoreData(DocumentSnapshot document) {
        Object college = document.get("college");
        if (college instanceof String)
            topView.setDesc((String) college);
        Object name = document.get("name");
        if (name instanceof String)
            topView.setName((String) name);

        try {
            setupTable(document);
            setupRulesTable();
        } catch (ClassCastException e) {
            e.printStackTrace();
            showNoTable();
        }
        showSingleView(dataHolderView);
    }

    private void showNoTable() {
        //TODO: Show YET ANOTHER damn view!
    }

    private void setupTable(DocumentSnapshot document) throws ClassCastException {
        Object data = document.get("data");
        if (data instanceof List) {
            List<Integer> tableData = convertToIntList((List) data);

            // Not translatable; I don't care. Move it to Strings if you do.
            String[] headers = {"PARTICULARS", "POINTS"};
            String[] rowHeaders = {"Poster", "Merchandise", "Workshop [on 28-29th Mar]", "Strokes",
                    "Coupon(s)", "EDGE event registration", "Workshop [17-19th Apr]"};
            showTable(headers, rowHeaders, tableData);
        } else {
            showNoTable();
        }
    }

    // Using this just in case I forget to change the data type to Number in Firebase while entering
    // data. Will remove this after writing a script to fill the values instead of the FB Console.
    private List<Integer> convertToIntList(List data) throws ClassCastException {
        List<Integer> points = new LinkedList<>();
        for (Object item : data) {
            if (item instanceof Number) {
                points.add(((Number) item).intValue());
            } else if (item instanceof String) {
                points.add(Integer.parseInt((String) item));
            } else {
                throw new ClassCastException();
            }
        }
        return points;
    }

    private void showTable(String[] columnHeaders, String[] rowHeaders, List<Integer> data) {
        if (data == null || data.size() != rowHeaders.length) {
            // We messed up during data entry.
            showSingleView(errorView);
            Context context = getContext();
            if (context != null)
                Toast.makeText(context,
                        "Inconsistent data. Please contact Geekonix.", Toast.LENGTH_LONG)
                        .show();
            return;
        }

        DataTableHeader.Builder tableHeaders = new DataTableHeader.Builder();
        ArrayList<DataTableRow> tableRows = new ArrayList<>();
        for (int i = 0; i < columnHeaders.length; i++) {
            int weight;
            if (i == 0)
                weight = 1;
            else
                weight = 1;
            tableHeaders.item(columnHeaders[i], weight);
        }

        int totalPoints = 0;
        for (int i = 0; i < rowHeaders.length; i++) {
            totalPoints += data.get(i);
            DataTableRow.Builder tableRow = new DataTableRow.Builder();
            tableRow.value(rowHeaders[i]);
            tableRow.value(String.valueOf(data.get(i)));
            tableRows.add(tableRow.build());
        }

        dataPoints.setText(
                String.format(
                        getString(R.string.ca_points_template), totalPoints));

        dataTable.setHeader(tableHeaders.build());
        dataTable.setRows(tableRows);
        Context context = getContext();
        if (context != null) {
            dataTable.setTypeface(ResourcesCompat.getFont(context, R.font.forum));
            dataTable.inflate(context);
        }
    }

    private void setupRulesTable() {
        ArrayList<DataTableRow> tableRows = new ArrayList<>();
        String[] rowHeaders = getResources().getStringArray(R.array.ca_rule_evs);
        String[] data = getResources().getStringArray(R.array.ca_rule_pts);
        DataTableHeader.Builder tableHeaders = new DataTableHeader.Builder()
                .item("EVENTS", 2)
                .item("POINTS YOU GET", 1);

        for (int i = 0; i < rowHeaders.length; i++) {
            DataTableRow.Builder tableRow = new DataTableRow.Builder();
            tableRow.value(rowHeaders[i]);
            tableRow.value(String.valueOf(data[i]));
            tableRows.add(tableRow.build());
        }

        rulesTable.setHeader(tableHeaders.build());
        rulesTable.setRows(tableRows);
        Context context = getContext();
        if (context != null) {
            rulesTable.setTypeface(ResourcesCompat.getFont(context, R.font.forum));
            rulesTable.inflate(context);
        }
    }

    private class OnSharedElementListener implements Transition.TransitionListener {
        private GeneralHeaderView topView;

        OnSharedElementListener(GeneralHeaderView topView) {
            this.topView = topView;
        }

        @Override
        public void onTransitionStart(@NonNull Transition transition) {
        }

        @Override
        public void onTransitionEnd(@NonNull Transition transition) {
            topView.showImage(getResources().getInteger(android.R.integer.config_mediumAnimTime));
            setData();
            isTransitionPlayed = true;
            transition.removeListener(this);
        }

        @Override
        public void onTransitionCancel(@NonNull Transition transition) {
            isTransitionPlayed = true;
            transition.removeListener(this);
        }

        @Override
        public void onTransitionPause(@NonNull Transition transition) {

        }

        @Override
        public void onTransitionResume(@NonNull Transition transition) {

        }
    }

    public interface OnAuthStartListener {
        void logIn();
    }
}
