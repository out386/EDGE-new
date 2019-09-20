package com.edge2.views.carousel;

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

import java.util.List;

public class EventModel {
    private String name;
    private String icon;
    private String backgroundImg;
    private List<SubeventNameModel> subevents;
    private SubeventAdapter.OnItemClickListener listener;

    public EventModel(String name, String icon, String backgroundImg,
                      List<SubeventNameModel> subevents, SubeventAdapter.OnItemClickListener listener) {
        this.name = name;
        this.icon = icon;
        this.backgroundImg = backgroundImg;
        this.subevents = subevents;
        this.listener = listener;
    }

    public String getName() {
        return name;
    }

    String getIcon() {
        return icon;
    }

    String getBackgroundImg() {
        return backgroundImg;
    }

    List<SubeventNameModel> getSubevents() {
        return subevents;
    }

    SubeventAdapter.OnItemClickListener getListener() {
        return listener;
    }
}