package com.edge2.utils;

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

public class Misc {
    /**
     * {@code num} should be less than 21
     */
    public static String numToStr(int num) {
        String rankStr = Integer.toString(num);
        switch (num) {
            case 1:
                rankStr += "st";
                break;
            case 2:
                rankStr += "nd";
                break;
            case 3:
                rankStr += "rd";
                break;
            default:
                rankStr += "th";
        }
        return rankStr;
    }
}
