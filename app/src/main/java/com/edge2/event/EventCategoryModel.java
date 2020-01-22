package com.edge2.event;

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
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.edge2.MainApplication;

@Entity(tableName = "EventCategories")
public class EventCategoryModel {
    @PrimaryKey
    @NonNull
    public String name;
    public String groupName;
    @Ignore
    @DrawableRes
    public int icon;
    public String desc;
    public String iconName;
    public boolean isInEdge;
    public boolean isInIntra;

    public EventCategoryModel(@NonNull String name, String groupName, String iconName, String desc,
                              boolean isInEdge, boolean isInIntra) {
        this.name = name;
        this.groupName = groupName;
        this.icon = MainApplication.getIdForDrawable(iconName);
        this.desc = desc;
        this.isInEdge = isInEdge;
        this.isInIntra = isInIntra;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public int getIcon() {
        return icon;
    }

    public String getDesc() {
        return desc;
    }
}