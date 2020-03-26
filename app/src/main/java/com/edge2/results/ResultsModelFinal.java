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

import android.content.Context;
import android.os.Handler;

import com.edge2.allevents.models.GroupsModel;
import com.edge2.data.DataRepo;
import com.edge2.event.EventCategoryModel;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static android.os.Environment.DIRECTORY_ALARMS;

public class ResultsModelFinal {
    public Map<String, List<ScreenItem>> screens;
    public Map<String, List<Result>> results;

    public ResultsModelFinal(Context context) {
        screens = new HashMap<>();
        results = new HashMap<>();
        DataRepo repo = DataRepo.getInstance(context);
        List<String> allEvents = new LinkedList<>();
        repo.getGroups(true).observeForever(groups -> {
            List<ScreenItem> screenItems = new LinkedList<>();
            for (GroupsModel group : groups) {
                ScreenItem screenItem = new ScreenItem();
                screenItem.icName = group.imageName;
                screenItem.name = group.getName();
                screenItem.desc = group.getNumEventsIntra();
                screenItem.dest = screenItem.name.toLowerCase().replace("-", "").replace(" ", "-");
                screenItem.isNextScreen = true;
                screenItems.add(screenItem);

                repo.getCategories(true, group.getName()).observeForever(events -> {
                    List<ScreenItem> currentScreen = new LinkedList<>();
                    for (EventCategoryModel event : events) {
                        allEvents.add(event.name);
                        ScreenItem item = new ScreenItem();
                        item.icName = event.iconName;
                        item.name = event.name;
                        item.dest = item.name.toLowerCase().replace("-", "").replace(" ", "-");
                        item.isNextScreen = false;
                        currentScreen.add(item);
                    }
                    screens.put(group.name.toLowerCase().replace("-", "").replace(" ", "-"), currentScreen);
                });
            }
            screens.put("default", screenItems);
        });
        new Handler().postDelayed(() -> {
                    allEvents.forEach(name -> {
                        List<Result> curReses = new LinkedList<>();
                        List<ResultsModel> results = ResultsData.getResult(name);
                        for (ResultsModel result : results) {
                            Result curRes = new Result();
                            curRes.rank = result.getPos();
                            curRes.members = getMembers(result.getMembers());
                            String tn = result.getTeamName();
                            curRes.tName = "".equals(tn) ? null : tn.trim();
                            curReses.add(curRes);
                        }
                        this.results.put(name.toLowerCase().replace("-", "").replace(" ", "-"), curReses);
                    });
                    Moshi moshi = new Moshi.Builder().build();
                    JsonAdapter<ResultsModelFinal> jsonAdapter = moshi.adapter(ResultsModelFinal.class);

                    String json = jsonAdapter.toJson(this);

                    File file = new File(context.getExternalFilesDir(DIRECTORY_ALARMS), "res.json");
                    try {
                        FileOutputStream os = new FileOutputStream(file, true);
                        OutputStreamWriter osw = new OutputStreamWriter(os);
                        osw.write(json);
                        osw.close();
                        os.close();
                    } catch (IOException ign) {
                    }
                },
                2000);
    }

    private List<Member> getMembers(String[] m) {
        List<Member> mems = new LinkedList<>();
        for (String s : m) {
            Member member = new Member();
            member.name = toTitleCase(s);
            mems.add(member);
        }
        return mems;
    }

    private String toTitleCase(String s) {
        String[] words = s.trim().split("\\s+");
        String result = null;
        for (String word : words) {
            String thisWord = Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase();
            result = result == null ? thisWord : result + " " + thisWord;
        }
        return result;
    }
}

class ScreenItem {
    //public String icUrl;
    public String icName;
    public String name;
    public String desc;
    public String dest;
    public boolean isNextScreen;
}

class Result {
    public int rank;
    public List<Member> members;
    public String tName;
    //public List<String> imgs;
}

class Member {
    public String name;
    //public String clg;
    //public String dept;
    //public int year;
}