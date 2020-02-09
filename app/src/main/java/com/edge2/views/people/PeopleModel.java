package com.edge2.views.people;

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

public class PeopleModel {
    private String name;
    private String role;
    private int imgRes;
    private long phoneNo;

    public PeopleModel(String name, String role, int imgRes, long phoneNo) {
        this.name = name;
        this.role = role;
        this.imgRes = imgRes;
        this.phoneNo = phoneNo;
    }

    String getName() {
        return name;
    }

    String getRole() {
        return role;
    }

    int getImgRes() {
        return imgRes;
    }

    long getPhoneNo() {
        return phoneNo;
    }

}
