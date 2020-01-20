package com.edge2.genericevents.models;

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

import java.util.List;
import java.util.Map;

public class GenericEventModel {
    private String name;
    private String imageUrl;
    private String longDesc;
    private String schedule;
    private Map<String, Long> contacts;

    GenericEventModel(String name, String imageUrl, String desc, String longDesc,
                      String rules, String schedule, Map<String, Long> contacts) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.longDesc = longDesc;
        this.schedule = schedule;
        this.contacts = contacts;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return imageUrl;
    }

    public String getLongDesc() {
        return longDesc;
    }

    public String getSchedule() {
        return schedule;
    }

    public Map<String, Long> getContacts() {
        return contacts;
    }

}