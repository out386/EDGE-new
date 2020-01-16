package com.edge2.eventdetails.models;

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

public class EventDetailsModel {
    private String name;
    private int icon;
    private String desc;
    private String longDesc;
    private String rules;
    private List<ScheduleModel> schedule;
    private Map<String, Long> contacts;

    EventDetailsModel(String name, @DrawableRes int icon, String desc, String longDesc,
                      String rules, List<ScheduleModel> schedule, Map<String, Long> contacts) {
        this.name = name;
        this.icon = icon;
        this.desc = desc;
        this.longDesc = longDesc;
        this.rules = rules;
        this.schedule = schedule;
        this.contacts = contacts;
    }

    public String getName() {
        return name;
    }

    public int getIcon() {
        return icon;
    }

    public String getDesc() {
        return desc;
    }

    public String getLongDesc() {
        return longDesc;
    }

    public String getRules() {
        return rules;
    }

    public List<ScheduleModel> getSchedule() {
        return schedule;
    }

    public Map<String, Long> getContacts() {
        return contacts;
    }

}