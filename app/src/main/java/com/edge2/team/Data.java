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
import com.edge2.views.people.PeopleModel;

import java.util.LinkedList;
import java.util.List;

public class Data {
    private List<PeopleModel> members;

    Data() {
        members = new LinkedList<>();
        members.add(new PeopleModel("Prattay Raha", "Resource Head", R.drawable.Prattay,
                8172025890L));

        members.add(new PeopleModel("Anubhav Talukdar",
                "Co Convener & Co Sponsorship Head", R.drawable.Anubhav, 9531129063L));

        members.add(new PeopleModel("Sayak Das",
                "Co Convener & Co Signing Authority", R.drawable.Sayak, 7685052883L));

        members.add(new PeopleModel("Sayan Kumar Roy", "Sponsorship Head", R.drawable.Sayan,
                7595888827L));

        members.add(new PeopleModel("Arijit Kar", "Treasurer & Signing Authority",
                R.drawable.Arijit, 9432903075L));

        members.add(new PeopleModel("Alok Ranjan", "Administrative Head",
                R.drawable.Alok, 7488094037L));

        members.add(new PeopleModel("Sahasra Banerjee", "Media Strategist",
                R.drawable.Sahasra, 9830086260L));

        members.add(new PeopleModel("Shreya Jaiswal", "Convener",
                R.drawable.Shreya, 8017195583L));

        members.add(new PeopleModel("Sharmistha Pan",
                "Acquisition Head and Co-Editorial Head", R.drawable.Anyesha,
                8910817240L));

        members.add(new PeopleModel("Angana Sen", "Outreach Head & Editorial Head",
                R.drawable.Angana, 8584805352L));


    }

    List<PeopleModel> getMembers() {
        return members;
    }

}
