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

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "EventDetails")
public class EventDetailsModel {
    @PrimaryKey
    @NonNull
    public String name;
    public String longDesc;
    public String rules;
    public String schedule;
    public String cN1;
    public long cNo1;
    public String cN2;
    public long cNo2;
    public String cN3;
    public long cNo3;
    public String cN4;
    public long cNo4;
    @Ignore
    private List<Pair<String, Long>> contacts;

    public EventDetailsModel(@NonNull String name, String longDesc, String rules, String schedule,
                             String cN1, long cNo1, String cN2, long cNo2, String cN3, long cNo3,
                             String cN4, long cNo4) {
        this.name = name;
        this.longDesc = longDesc;
        this.rules = rules;
        this.schedule = schedule;
        contacts = new ArrayList<>(4);
        if (cN1 != null && !cN1.isEmpty()) {
            contacts.add(new Pair<>(cN1, cNo1));
        }
        if (cN2 != null && !cN2.isEmpty()) {
            contacts.add(new Pair<>(cN2, cNo2));
        }
        if (cN3 != null && !cN3.isEmpty()) {
            contacts.add(new Pair<>(cN3, cNo3));
        }
        if (cN4 != null && !cN4.isEmpty()) {
            contacts.add(new Pair<>(cN4, cNo4));
        }
    }

    @NonNull
    public String getName() {
        return name;
    }

    public String getLongDesc() {
        return longDesc;
    }

    public String getRules() {
        return rules;
    }

    public String getSchedule() {
        return schedule;
    }

    public List<Pair<String, Long>> getContacts() {
        return contacts;
    }

}