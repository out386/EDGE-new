package com.edge2.results;

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

import androidx.annotation.Nullable;

import com.edge2.BuildConfig;

import java.util.List;

public class MainScreenModel {
    private List<MainScreenItem> items;

    public List<MainScreenItem> getItems() {
        return items;
    }

    public static class MainScreenItem {
        private String name;
        private String ic;
        private String desc;
        private String lDesc;
        private String dest;

        /**
         * Name of this item. Example: "Intra '20", "EDGE '20"
         */
        public String getName() {
            return name;
        }

        public String getIc() {
            return BuildConfig.URL_RESULT_ICONS + ic;
        }

        /**
         * Short description to show on the main screen. Example: "9 events"
         */
        @Nullable
        public String getDesc() {
            return desc;
        }

        /**
         * Long description to show on the header of the screen of this event. Example: "Results for
         * Intra College Fest"
         */
        String getlDesc() {
            return lDesc;
        }

        /**
         * The URL to the .json that describes this item. Example: "{@link
         * com.edge2.BuildConfig#URL_RESULT_BASE}" + "intra.json".
         */
        String getDest() {
            return BuildConfig.URL_RESULT_BASE + dest;
        }
    }
}
