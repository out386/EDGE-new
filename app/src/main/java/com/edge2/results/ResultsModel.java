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
import java.util.Map;

public class ResultsModel {
    private Map<String, List<ScreenItem>> screens;
    private Map<String, List<Result>> results;

    public Map<String, List<ScreenItem>> getScreens() {
        return screens;
    }

    public Map<String, List<Result>> getResults() {
        return results;
    }

    public static class ScreenItem {
        private String icUrl;
        private String icName;
        private String name;
        private String desc;
        private String dest;
        private boolean isNextScreen;

        public String getName() {
            return name;
        }

        public String getDesc() {
            return desc;
        }

        String getDest() {
            return dest;
        }

        boolean isNextScreen() {
            return isNextScreen;
        }

        public String getIconUriString() {
            if (icName != null && !icName.isEmpty()) {
                return String.format("android.resource://%s/drawable/%s",
                        BuildConfig.APPLICATION_ID, icName);
            } else if (icUrl != null && !icUrl.isEmpty()) {
                return BuildConfig.URL_RESULT_ICONS + icUrl;
            } else {
                return null;
            }
        }
    }

    public static class Result {
        private int rank;
        private List<Member> members;
        private String tName;
        private List<String> imgs;

        public int getRank() {
            return rank;
        }

        public List<Member> getMembers() {
            return members;
        }

        public String getTName() {
            return tName;
        }

        /**
         * List of image URLs, relative to {@link BuildConfig#URL_RESULT_PICS}
         */
        public List<String> getImgs() {
            return imgs;
        }
    }

    public static class Member {
        private String name;
        private String clg;
        private String dept;
        private int year;

        public String getName() {
            return name;
        }

        @Nullable
        public String getClg() {
            return clg;
        }

        @Nullable
        public String getDept() {
            return dept;
        }

        public int getYear() {
            return year;
        }
    }
}