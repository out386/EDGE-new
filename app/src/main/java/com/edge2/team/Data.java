package com.edge2.team;

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

import com.edge2.R;

import java.util.LinkedList;
import java.util.List;

public class Data {
    private List<MemberModel> members;

    Data() {
        members = new LinkedList<>();
        members.add(new MemberModel("Soham Biswas", "Convener", R.drawable.soham,
                7602760944L));

        members.add(new MemberModel("Rishav Bhattacharyya",
                "Co Convener & Co Sponsorship Head", R.drawable.rishav, 8335067136L));

        members.add(new MemberModel("Sayantan Bhattacharya",
                "Co Convener & Co Signing Authority", R.drawable.sayantan, 9163480278L));

        members.add(new MemberModel("Sroman Guha", "Sponsorship Head", R.drawable.sroman,
                8981570577L));

        members.add(new MemberModel("Subhajit Saha", "Treasurer & Signing Authority",
                R.drawable.subhajit, 9679343204L));

        members.add(new MemberModel("Abhishek Jha", "Administrative Head",
                R.drawable.abhishek, 7654285631L));

        members.add(new MemberModel("Suman Ghosh", "Resource Management Head",
                R.drawable.suman, 9734900238L));

        members.add(new MemberModel("Shibshankar Saha", "Public Relations Coordinator",
                R.drawable.shibshankar, 9062294021L));

        members.add(new MemberModel("Sharmistha Pan",
                "Member Management Head & Editorial Head", R.drawable.sarmistha,
                8768078258L));

        members.add(new MemberModel("Swarnali Talukdar", "Outreach Head",
                R.drawable.swarnali, 9123334065L));


    }

    public List<MemberModel> getMembers() {
        return members;
    }

    class MemberModel {
        String name;
        String role;
        int imgRes;
        long phoneNo;

        MemberModel(String name, String role, int imgRes, long phoneNo) {
            this.name = name;
            this.role = role;
            this.imgRes = imgRes;
            this.phoneNo = phoneNo;
        }
    }
}
