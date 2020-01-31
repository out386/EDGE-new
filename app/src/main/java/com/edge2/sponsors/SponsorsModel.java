package com.edge2.sponsors;

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

import org.json.JSONException;
import org.json.JSONObject;

public class SponsorsModel {
    private String name;
    private String imgUrl;

    private SponsorsModel() {
    }

    public SponsorsModel(String name, String imgUrl) {
        this.name = name;
        this.imgUrl = imgUrl;
    }

    public String getName() {
        return name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public static SponsorsModel getFromJson(JSONObject ob) throws JSONException{
        SponsorsModel item = new SponsorsModel();
            item.name = ob.getString("n");
            if (item.name == null || item.name.isEmpty() || item.name.equals("null"))
                return null;
            item.imgUrl = ob.getString("u");
            if (item.imgUrl.isEmpty())
                item.imgUrl = null;
            return item;
    }
}
