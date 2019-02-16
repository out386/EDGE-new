package com.edge2.views.carousel;

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

public class HeroModel {
    private String name;
    private String icon;
    private String backgroundImg;

    public HeroModel(String name, String icon, String backgroundImg) {
        this.name = name;
        this.icon = icon;
        this.backgroundImg = backgroundImg;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public String getBackgroundImg() {
        return backgroundImg;
    }
}