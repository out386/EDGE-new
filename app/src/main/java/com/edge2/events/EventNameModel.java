package com.edge2.events;

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

import java.util.Map;

class EventNameModel {
    private String name;
    private String img;

    EventNameModel(QueryDocumentSnapshot documentSnapshot) {
        Map<String, Object> data = documentSnapshot.getData();
        name = (String) data.get("name");
        img = (String) data.get("img");
    }

    String getName() {
        return name;
    }

    String getImg() {
        return img;
    }
}
