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

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.edge2.MainApplication;
import com.edge2.R;

@Entity(tableName = "Groups")
public class GroupsModel {
    @PrimaryKey
    @NonNull
    public String name;
    @Ignore
    public int image;
    public String imageName;
    @Ignore
    public String numEventsEdge;
    @Ignore
    public String numEventsIntra;
    public String desc;
    public int countEventsEdge;
    public int countEventsIntra;

    public GroupsModel(String name, String imageName, int countEventsEdge, int countEventsIntra,
                       String desc) {
        this.name = name;
        this.image = MainApplication.getIdForDrawable(imageName);
        numEventsEdge = String.format(MainApplication.getResString(R.string.sub_events_num),
                countEventsEdge, countEventsEdge == 1 ? "" : "s");
        numEventsIntra = String.format(MainApplication.getResString(R.string.sub_events_num),
                countEventsIntra, countEventsIntra == 1 ? "" : "s");
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public int getImage() {
        return image;
    }

    public String getNumEventsEdge() {
        return numEventsEdge;
    }

    public String getNumEventsIntra() {
        return numEventsIntra;
    }

    public String getDesc() {
        return desc;
    }
}