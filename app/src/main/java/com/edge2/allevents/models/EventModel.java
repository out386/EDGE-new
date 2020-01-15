package com.edge2.allevents.models;

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

import androidx.annotation.DrawableRes;

public class EventModel {
    private String name;
    private int image;
    private String numEvents;
    private String desc;

    public EventModel(String name, @DrawableRes int image, int countEvents, String countTemplate, String desc) {
        this.name = name;
        this.image = image;
        numEvents = String.format(countTemplate, countEvents,
                countEvents == 1 ? "" : "s");
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public int getImage() {
        return image;
    }

    public String getNumEvents() {
        return numEvents;
    }

    public String getDesc() {
        return desc;
    }
}