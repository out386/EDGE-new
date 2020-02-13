package com.edge2.eventdetails.models;

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

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

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

    private EventDetailsModel() {
    }

    public EventDetailsModel(@NonNull String name, String longDesc, String rules, String schedule,
                             String cN1, long cNo1, String cN2, long cNo2, String cN3, long cNo3,
                             String cN4, long cNo4) {
        this.name = name;
        this.longDesc = longDesc;
        this.rules = rules;
        this.schedule = schedule;
        this.cN1 = cN1;
        this.cNo1 = cNo1;
        this.cN2 = cN2;
        this.cNo2 = cNo2;
        this.cN3 = cN3;
        this.cNo3 = cNo3;
        this.cN4 = cN4;
        this.cNo4 = cNo4;
        setContacts(this);
    }

    private static void setContacts(EventDetailsModel item) {
        item.contacts = new ArrayList<>(4);
        if (item.cN1 != null && !item.cN1.isEmpty()) {
            item.contacts.add(new Pair<>(item.cN1, item.cNo1));
        }
        if (item.cN2 != null && !item.cN2.isEmpty()) {
            item.contacts.add(new Pair<>(item.cN2, item.cNo2));
        }
        if (item.cN3 != null && !item.cN3.isEmpty()) {
            item.contacts.add(new Pair<>(item.cN3, item.cNo3));
        }
        if (item.cN4 != null && !item.cN4.isEmpty()) {
            item.contacts.add(new Pair<>(item.cN4, item.cNo4));
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

    public static EventDetailsModel getFromJson(JSONObject ob) throws JSONException {
        EventDetailsModel item = new EventDetailsModel();
        item.name = ob.getString("name");
        if (item.name == null || item.name.isEmpty() || item.name.equals("null"))
            return null;
        item.longDesc = ob.getString("longDesc");
        if (item.longDesc.isEmpty())
            item.longDesc = null;
        item.rules = ob.getString("rules");
        if (item.rules.isEmpty())
            item.rules = null;
        item.schedule = ob.getString("schedule");
        if (item.schedule.isEmpty())
            item.schedule = null;
        item.cN1 = ob.getString("cN1");
        if (item.cN1 == null || item.cN1.isEmpty() || item.cN1.equals("null"))
            item.cN1 = null;
        item.cN2 = ob.getString("cN2");
        if (item.cN2 == null || item.cN2.isEmpty() || item.cN2.equals("null"))
            item.cN2 = null;
        item.cN3 = ob.getString("cN3");
        if (item.cN3 == null || item.cN3.isEmpty() || item.cN3.equals("null"))
            item.cN3 = null;
        item.cN4 = ob.getString("cN4");
        if (item.cN4 == null || item.cN4.isEmpty() || item.cN4.equals("null"))
            item.cN4 = null;

        try {
            item.cNo1 = ob.getLong("cNo1");
            item.cNo2 = ob.getLong("cNo2");
            item.cNo3 = ob.getLong("cNo3");
            item.cNo4 = ob.getLong("cNo4");
        } catch (JSONException e) {
            try {
                // My sqlite to json tool sometimes makes all numbers text
                item.cNo1 = Long.parseLong(ob.getString("cNo1"));
                item.cNo2 = Long.parseLong(ob.getString("cNo2"));
                item.cNo3 = Long.parseLong(ob.getString("cNo3"));
                item.cNo4 = Long.parseLong(ob.getString("cNo4"));
            } catch (NumberFormatException | JSONException ignored) {
            }
        }
        setContacts(item);
        return item;
    }

}