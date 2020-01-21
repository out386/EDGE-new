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

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatDelegate;

public class MainApplication extends Application {
    static final String KEY_THEME_TYPE = "themeSetting";
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        int theme;
        if (Build.VERSION.SDK_INT > 28)
            theme = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        else
            theme = AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        theme = prefs.getInt(KEY_THEME_TYPE, theme);

        AppCompatDelegate.setDefaultNightMode(theme);
    }

    public static String getResString(@StringRes int id) {
        return context.getString(id);
    }

    public static int getIdForDrawable(String name) {
        return context
                .getResources().getIdentifier(name, "drawable", context.getPackageName());
    }
}

