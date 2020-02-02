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

import android.graphics.drawable.Drawable;

public class QuickItemModel {
    private String name;
    private Drawable image;
    private String desc;

    public QuickItemModel(String name, Drawable image, String desc) {
        this.name = name;
        this.image = image;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public Drawable getImage() {
        return image;
    }

    public String getDesc() {
        return desc;
    }
}