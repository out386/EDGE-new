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

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.afollestad.materialdialogs.MaterialDialog;

import static com.edge2.MainApplication.KEY_THEME_TYPE;

public abstract class ThemeActivity extends AppCompatActivity {
    private SharedPreferences prefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Check if theme preferences were changed
        int mode = prefs.getInt(KEY_THEME_TYPE, Integer.MAX_VALUE);
        if (mode == Integer.MAX_VALUE)
            setThemeType(AppCompatDelegate.MODE_NIGHT_YES);

        setSystemUIFlags();
    }

    private void setThemeType(int theme) {
        AppCompatDelegate.setDefaultNightMode(theme);
        prefs.edit()
                .putInt(KEY_THEME_TYPE, theme)
                .apply();
    }

    private void setSystemUIFlags() {
        int uiMode = getCurrentTheme();
        View decorView = getWindow().getDecorView();
        switch (uiMode) {
            case Configuration.UI_MODE_NIGHT_YES:
                decorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                                View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

                break;
            case Configuration.UI_MODE_NIGHT_NO:
            default:
                if (Build.VERSION.SDK_INT >= 26) {
                    decorView.setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR |
                                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR |
                                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                } else {
                    decorView.setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR |
                                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                }
        }
    }

    private int getCurrentTheme() {
        return getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
    }

    public void showThemeDialog() {
        String[] options = new String[3];
        options[0] = getString(R.string.action_theme_day);
        options[1] = getString(R.string.action_theme_night);
        if (Build.VERSION.SDK_INT > 28)
            options[2] = getString(R.string.action_theme_system);
        else
            options[2] = getString(R.string.action_theme_battery);


        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(getString(R.string.action_theme))
                .itemsColor(getColor(R.color.textHeader))
                .items(options)
                .backgroundColor(getColor(R.color.windowBackground))
                .itemsCallbackSingleChoice(getThemeAsIndex(),
                        (dialog1, itemView, which, text) -> {
                            switch (which) {
                                case 0:
                                    setThemeType(AppCompatDelegate.MODE_NIGHT_NO);
                                    break;
                                case 1:
                                    setThemeType(AppCompatDelegate.MODE_NIGHT_YES);
                                    break;
                                case 2:
                                    if (Build.VERSION.SDK_INT > 28)
                                        setThemeType(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                                    else
                                        setThemeType(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                                    break;
                            }
                            return true;
                        })
                .build();
        dialog.show();
    }

    private int getThemeAsIndex() {
        int currentTheme = prefs.getInt(KEY_THEME_TYPE, AppCompatDelegate.MODE_NIGHT_YES);
        switch (currentTheme) {
            case AppCompatDelegate.MODE_NIGHT_NO:
                return 0;
            case AppCompatDelegate.MODE_NIGHT_YES:
                return 1;
            case AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY:
            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
            default:
                return 2;
        }
    }
}
