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

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "BannerItems")
public class BannerItemsModel implements Serializable {
    @PrimaryKey
    @NonNull
    public String name;
    public String imgName;
    public String imgUrl;
    @Ignore
    private Uri uri;
    public String sched;
    public String desc;
    public boolean isMega;

    public BannerItemsModel(@NonNull String name, String imgName, String imgUrl, String sched,
                            String desc, boolean isMega) {
        this.name = name;
        if (imgName != null && !imgName.isEmpty()) {
            uri = Uri.parse("android.resource://com.edge2/drawable/" + imgName);
        } else if (imgUrl != null && !imgUrl.isEmpty()) {
            uri = Uri.parse(imgUrl);
        }
        this.sched = sched;
        this.desc = desc;
        this.isMega = isMega;
    }

    public Uri getUri() {
        return uri;
    }

    public String getName() {
        return name;
    }

    public String getSched() {
        return sched;
    }

    public String getDesc() {
        return desc;
    }

    public boolean getMega() {
        return isMega;
    }
}
