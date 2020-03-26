package com.edge2.results;

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

public class ResultsModel {
    private int pos;
    private String teamName;
    private String[] members;

    ResultsModel(int pos, String teamName, String members) {
        this.pos = pos;
        this.teamName = teamName;
        this.members = members.trim().split("\n");
    }

    public int getPos() {
        return pos;
    }

    public String getTeamName() {
        return teamName;
    }

    public String[] getMembers() {
        return members;
    }
}
