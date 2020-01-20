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

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.Map;

public class BannerItemsModel implements Serializable {
    private String id;
    private String img;
    private String name;
    private String sched;
    private String desc;

    public BannerItemsModel(QueryDocumentSnapshot documentSnapshot) {
        Map<String, Object> data = documentSnapshot.getData();
        id = (String) data.get("id");
        img = (String) data.get("img");
        name = (String) data.get("name");
        sched = (String) data.get("sched");
        desc = (String) data.get("desc");
    }

    public BannerItemsModel(String id, String img, String name, String sched, String desc) {
        this.id = id;
        this.img = img;
        this.name = name;
        this.sched = sched;
        this.desc = desc;
    }

    public String getId() {
        return id;
    }

    public String getImg() {
        return img;
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
}
