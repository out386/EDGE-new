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
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

@Entity(tableName = "BannerItems")
public class BannerItemsModel implements Serializable {
    @PrimaryKey
    @NonNull
    public String name;
    public String imgName;
    public String imgUrl;
    @Ignore
    @Nullable
    private Uri imageUri;
    public String icName;
    public String icUrl;
    @Ignore
    @Nullable
    private Uri iconUri;
    public String sched;
    public String desc;
    public boolean isMega;

    private BannerItemsModel() {
    }

    public BannerItemsModel(@NonNull String name, String imgName, String imgUrl, String icName,
                            String icUrl, String sched, String desc, boolean isMega) {
        this.name = name;
        this.imgName = imgName;
        this.imgUrl = imgUrl;
        this.icName = icName;
        this.icUrl = icUrl;
        setUri(this);
        this.sched = sched;
        this.desc = desc;
        this.isMega = isMega;
    }

    private static void setUri(BannerItemsModel item) {
        if (item.imgName != null && !item.imgName.isEmpty()) {
            item.imageUri = Uri.parse("android.resource://com.edge2/drawable/" + item.imgName);
        } else if (item.imgUrl != null && !item.imgUrl.isEmpty()) {
            item.imageUri = Uri.parse(item.imgUrl);
        }

        if (item.icName != null && !item.icName.isEmpty()) {
            item.iconUri = Uri.parse("android.resource://com.edge2/drawable/" + item.icName);
        } else if (item.icUrl != null && !item.icUrl.isEmpty()) {
            item.iconUri = Uri.parse(item.icUrl);
        }
    }

    @Nullable
    public Uri getImageUri() {
        return imageUri;
    }

    @Nullable
    public Uri getIconUri() {
        return iconUri;
    }

    @NonNull
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

    public static BannerItemsModel getFromJson(JSONObject ob) throws JSONException {
        BannerItemsModel item = new BannerItemsModel();
        item.name = ob.getString("name");

        // Yes, the generated JSON I'm using can actually have a "null" string
        if (item.name == null || item.name.isEmpty() || item.name.equals("null"))
            return null;

        item.imgName = ob.getString("imgName");
        if (item.imgName == null || item.imgName.isEmpty() || item.imgName.equals("null"))
            item.imgName = null;
        item.imgUrl = ob.getString("imgUrl");
        if (item.imgUrl == null || item.imgUrl.isEmpty() || item.imgUrl.equals("null"))
            item.imgUrl = null;

        item.icName = ob.getString("imgName");
        if (item.icName == null || item.icName.isEmpty() || item.icName.equals("null"))
            item.icName = null;
        item.icUrl = ob.getString("imgUrl");
        if (item.icUrl == null || item.icUrl.isEmpty() || item.icUrl.equals("null"))
            item.icUrl = null;

        item.sched = ob.getString("sched");
        if (item.sched == null || item.sched.isEmpty() || item.sched.equals("null"))
            item.sched = null;
        item.desc = ob.getString("desc");
        if (item.desc == null || item.desc.isEmpty() || item.desc.equals("null"))
            item.desc = null;

        item.isMega = ob.getInt("isMega") == 1;
        setUri(item);
        return item;
    }
}
