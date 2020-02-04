package com.edge2.allevents.models;

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

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class HideEventsModel {
    private static final String KEY_IS_EVENTS_HIDDEN = "isEventsHidden";
    private static final String KEY_EVENTS_HIDDEN_URL = "eventsHiddenUrl";

    private boolean hideEvents;
    private String imgUrl;

    private HideEventsModel() {
        hideEvents = true;
    }

    public boolean isHideEvents() {
        return hideEvents;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    /**
     * Format of the string: <1 or 0>\n<imgUrl> 1 = hide events, 2 = show events
     */
    public static HideEventsModel getFromString(String s) {
        HideEventsModel item = new HideEventsModel();
        String[] i = s.split("\n");
        try {
            item.hideEvents = Integer.parseInt(i[0]) == 1;
        } catch (NumberFormatException ignored) {
        }
        if (i.length > 1 && !i[1].isEmpty())
            item.imgUrl = i[1];
        return item;
    }

    public static HideEventsModel getFromPrefs(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        HideEventsModel item = new HideEventsModel();
        item.hideEvents = prefs.getBoolean(KEY_IS_EVENTS_HIDDEN, true);
        item.imgUrl = prefs.getString(KEY_EVENTS_HIDDEN_URL, null);
        return item;
    }
}
